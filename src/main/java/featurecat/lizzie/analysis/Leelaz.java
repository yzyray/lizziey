package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
//import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.Movelist;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An interface with leelaz go engine. Can be adapted for GTP, but is specifically designed for
 * GCP's Leela Zero. leelaz is modified to output information as it ponders see
 * www.github.com/gcp/leela-zero
 */
public class Leelaz {
  private static final ResourceBundle resourceBundle =
      ResourceBundle.getBundle("l10n.DisplayStrings");

  private static final long MINUTE = 60 * 1000; // number of milliseconds in a minute

  // private long maxAnalyzeTimeMillis; // , maxThinkingTimeMillis;
  private int cmdNumber;
  private int currentCmdNum;
  private ArrayDeque<String> cmdQueue;

  public Process process;

  private BufferedInputStream inputStream;
  private BufferedOutputStream outputStream;

  private boolean printCommunication;
  public boolean gtpConsole;

 // public Board board;
  private List<MoveData> bestMoves;
  private List<MoveData> bestMovesTemp;

  private List<LeelazListener> listeners;

  private boolean isPondering;
  private long startPonderTime;

  // fixed_handicap
  public boolean isSettingHandicap = false;

  // genmove
  public boolean isThinking = false;
  public boolean isInputCommand = false;

  public boolean preload = false;
  public boolean started = false;
  private boolean isLoaded = false;
  private boolean isCheckingVersion;

  // for Multiple Engine
  public String engineCommand;
  private List<String> commands;
  private JSONObject config;
  private String currentWeightFile = "";
  private String currentWeight = "";
  public boolean switching = false;
  private int currentEngineN = -1;
  private ScheduledExecutorService executor;

  // dynamic komi and opponent komi as reported by dynamic-komi version of leelaz
  private float dynamicKomi = Float.NaN;
  private float dynamicOppKomi = Float.NaN;
  public boolean isheatmap = false;
  public int version = -1;
  public ArrayList<Integer> heatcount = new ArrayList<Integer>();
  public String currentEnginename = "";
  boolean analysed=false;
  private boolean isSaving=false;
  private boolean isResigning=false;
  private boolean isClosing=false;
  public boolean isColorEngine=false;
  public int stage=-1;
  public float komi =(float) 7.5;
  public int blackResignMoveCounts=0;
  public int  whiteResignMoveCounts=0;
  public boolean resigned=false;
  public boolean doublePass=false;
  public boolean played=false;
  //private boolean pkBlackWins=true;
  //public boolean playanyway=false;
  // public double heatwinrate;
  /**
   * Initializes the leelaz process and starts reading output
   *
   * @throws IOException
   */
  public Leelaz(String engineCommand) throws IOException, JSONException {
   // board = new Board();
    bestMoves = new ArrayList<>();
    bestMovesTemp = new ArrayList<>();
    listeners = new CopyOnWriteArrayList<>();

    isPondering = false;
    startPonderTime = System.currentTimeMillis();
    cmdNumber = 1;
    currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();

    // Move config to member for other method call
    config = Lizzie.config.config.getJSONObject("leelaz");

    printCommunication = config.getBoolean("print-comms");
    gtpConsole = printCommunication;
    // maxAnalyzeTimeMillis = MINUTE * config.getInt("max-analyze-time-minutes");

    // command string for starting the engine
    //    if (engineCommand == null || engineCommand.isEmpty()) {
    //      engineCommand = config.getString("engine-command");
    //      // substitute in the weights file
    //      engineCommand = engineCommand.replaceAll("%network-file",
    // config.getString("network-file"));
    //    }
    this.engineCommand = engineCommand;

    // Initialize current engine number and start engine
    currentEngineN = 0;
  }

  public void updateCommand(String engineCommand) {
    this.engineCommand = engineCommand;
  }

  public void getEngineName(int index) {
    Optional<JSONArray> enginesNameOpt =
            Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
        enginesNameOpt.ifPresent(
            a -> {
              IntStream.range(0, a.length())
                  .forEach(
                      i -> {
                    	  if(i==index)
                    		  currentEnginename=  a.getString(i);
                      });
            });
    if (currentEnginename.equals("")) currentEnginename = currentWeight;
  }

 

  public void startEngine(int index) throws IOException {
    if (engineCommand.trim().isEmpty()) {
      return;
    }

    commands = splitCommand(engineCommand);

    // Get weight name
    Pattern wPattern = Pattern.compile("(?s).*?(--weights |-w )([^'\" ]+)(?s).*");
    Matcher wMatcher = wPattern.matcher(engineCommand);
    if (wMatcher.matches() && wMatcher.groupCount() == 2) {
      currentWeightFile = wMatcher.group(2);
      String[] names = currentWeightFile.split("[\\\\|/]");
      currentWeight = names.length > 1 ? names[names.length - 1] : currentWeightFile;
      currentEngineN = index;
      Optional<JSONArray> enginesNameOpt =
              Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
          enginesNameOpt.ifPresent(
              a -> {
                IntStream.range(0, a.length())
                    .forEach(
                        i -> {
                      	  if(i==index)
                      		  currentEnginename=  a.getString(i);
                        });
              });
      if (currentEnginename.equals("")) currentEnginename = currentWeight;
    }

    // Check if engine is present
    // Commented for remote ssh. TODO keep or remove this code?
    //    File startfolder = new File(config.optString("engine-start-location", "."));
    //    File lef = startfolder.toPath().resolve(new File(commands.get(0)).toPath()).toFile();
    //    System.out.println(lef.getPath());
    //    if (!lef.exists()) {
    //      JOptionPane.showMessageDialog(
    //          null,
    //          resourceBundle.getString("LizzieFrame.display.leelaz-missing"),
    //          "Lizzie - Error!",
    //          JOptionPane.ERROR_MESSAGE);
    //      throw new IOException("engine not present");
    //    }

    // Check if network file is present
    //    File wf = startfolder.toPath().resolve(new File(currentWeightFile).toPath()).toFile();
    //    if (!wf.exists()) {
    //      JOptionPane.showMessageDialog(
    //          null, resourceBundle.getString("LizzieFrame.display.network-missing"));
    //      throw new IOException("network-file not present");
    //    }

    // run leelaz
    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    // Commented for remote ssh
    //    processBuilder.directory(startfolder);

    processBuilder.redirectErrorStream(true);
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      if (index == 0) {
        Lizzie.frame.openConfigDialog();
        System.exit(1);
      }
      return;
    }
    initializeStreams();

    // Send a version request to check that we have a supported version
    // Response handled in parseLine
    isCheckingVersion = true;
    sendCommand("version");
    sendCommand("boardsize " + Lizzie.config.uiConfig.optInt("board-size", 19));

    // start a thread to continuously read Leelaz output
    // new Thread(this::read).start();
    // can stop engine for switching weights
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
    started = true;
    featurecat.lizzie.gui.Menu.engine[index].setIcon(featurecat.lizzie.gui.Menu.stop);
   
  }

  public void restartEngine(int index) throws IOException {
    if (engineCommand.trim().isEmpty()) {
      return;
    }
    switching = true;
    this.engineCommand = engineCommand;
    // stop the ponder
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
    }
    normalQuit();
    startEngine(index);
    //    currentEngineN = index;
    togglePonder();
  }
  
  public void restartClosedEngine(int index) throws IOException {
	    if (engineCommand.trim().isEmpty()) {
	      return;
	    }
	    switching = true;
	    
	    this.engineCommand = engineCommand;
	    // stop the ponder
//	    if (Lizzie.leelaz.isPondering()) {
//	      Lizzie.leelaz.togglePonder();
//	    }
// normalQuit();
	    ArrayList<Movelist>mv= Lizzie.board.getmovelist();
	    startEngine(index);
	    //    currentEngineN = index;
	    //Lizzie.board.restoreMoveNumber();
	    Lizzie.board.setmovelist(mv);
	    ponder();
	  }

  public void normalQuit() {
	  featurecat.lizzie.gui.Menu.engine[currentEngineN].setIcon(null);
    
    sendCommand("quit");
    executor.shutdown();
    try {
      while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
      if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
        shutdown();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
    started = false;
  }

  /** Initializes the input and output streams */
  public void initializeStreams() {
    inputStream = new BufferedInputStream(process.getInputStream());
    outputStream = new BufferedOutputStream(process.getOutputStream());
  }

  public  List<MoveData> parseInfo(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    int k = (Lizzie.config.limitMaxSuggestion > 0 ? Lizzie.config.limitMaxSuggestion : 361);
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfo(var));
        k = k - 1;
        if (k < 1) break;
      }
    }
    Lizzie.board.getData().tryToSetBestMoves(bestMoves);
    return bestMoves;
  }

  public static List<MoveData> parseInfofromfile(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    // int k = Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfofromfile(var));
        //   k = k - 1;
        //   if (k < 1) break;
      }
    }
    Lizzie.board.getData().tryToSetBestMoves(bestMoves);
    return bestMoves;
  }

  /**
   * Parse a line of Leelaz output
   *
   * @param line output line
   */
  private void parseLine(String line) {
    synchronized (this) {
    	if(Lizzie.frame.toolbar.isEnginePk&&this.isPondering)
    	{
    		Lizzie.engineManager.startInfoTime = System.currentTimeMillis();
    	}
      // if (printCommunication || gtpConsole) {
      // Lizzie.gtpConsole.addLineforce(line);
      // }
//      if (line.startsWith("komi=")) {
//        try {
//          dynamicKomi = Float.parseFloat(line.substring("komi=".length()).trim());
//        } catch (NumberFormatException nfe) {
//          dynamicKomi = Float.NaN;
//        }
//      } else if (line.startsWith("opp_komi=")) {
//        try {
//          dynamicOppKomi = Float.parseFloat(line.substring("opp_komi=".length()).trim());
//        } catch (NumberFormatException nfe) {
//          dynamicOppKomi = Float.NaN;
//        }
  //   } else
    	// if (line.equals("\n")) {
        // End of response
    //  } else 
    	if( Lizzie.frame.isPlayingAgainstLeelaz||Lizzie.frame.toolbar.isGenmove)
    	{if (line.contains(" ->   ")) {
    		 bestMoves.add(MoveData.fromSummary(line));      
    		 Lizzie.board.getData().tryToSetBestMoves(bestMoves);
    	}
    	}
    	  if (line.startsWith("info")) {
        
        // Clear switching prompt
        switching = false;

        // Display engine command in the title
        Lizzie.frame.updateTitle();
        if (isResponseUpToDate()) {
          // This should not be stale data when the command number match
          this.bestMoves = parseInfo(line.substring(5));
          if(Lizzie.frame.toolbar.isEnginePk)
          Lizzie.leelaz=this;
          //notifyBestMoveListeners();
            if(!bestMoves.isEmpty()) {
          notifyAutoPK();  
            }
          Lizzie.frame.refresh();
          // don't follow the maxAnalyzeTime rule if we are in analysis mode
          if ((!Lizzie.frame.toolbar.isEnginePk||!Lizzie.frame.toolbar.isAutoAna)&&(System.currentTimeMillis() - startPonderTime) > Lizzie.config.maxAnalyzeTimeMillis
              && !Lizzie.frame.toolbar.isAutoAna) {
            togglePonder();
          }
        }
      } else if(Lizzie.gtpConsole.isVisible())
          Lizzie.gtpConsole.addLine(line);
    	  //System.out.println(line);
    	  if(line.startsWith("| ST")) {       
        String[] params = line.trim().split(" ");
        if(params.length==13)        	
        {         	
        	isColorEngine=true;
        	if(Lizzie.gtpConsole.isVisible())
        	 Lizzie.gtpConsole.addLineforce(line);
        	stage= Integer.parseInt(params[3].substring(0,params[3].length()-1));
        	komi= Float.parseFloat(params[6].substring(0,params[6].length()-1));
        }
      } 
      //else if (line.contains(" ->   ")) {
     //   isLoaded = true;
       // if (isResponseUpToDate()
       //     || isThinking
        //        && (!isPondering && Lizzie.frame.isPlayingAgainstLeelaz || isInputCommand)) {
        //  if (line.contains("pass")) {
        //  } 
         // else if (!switching) {
           // bestMoves.add(MoveData.fromSummary(line));          
          //  Lizzie.frame.repaint();
         // }
       // }
     // } 
    else if (line.startsWith("play")) {
        // In lz-genmove_analyze
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.board.place(line.substring(5).trim());
        }
        isThinking = false;

      } else if (line.startsWith("=") || line.startsWith("?")) {
        if (Lizzie.gtpConsole.isVisible()) {
         
          Lizzie.gtpConsole.addLine(line);
        }
        String[] params = line.trim().split(" ");
        currentCmdNum = Integer.parseInt(params[0].substring(1).trim());

        trySendCommandFromQueue();

        if (line.startsWith("?") || params.length == 1) return;

        if (isSettingHandicap) {
          bestMoves = new ArrayList<>();
          for (int i = 1; i < params.length; i++) {
            Lizzie.board
                .asCoordinates(params[i])
                .ifPresent(coords -> Lizzie.board.getHistory().setStone(coords, Stone.BLACK));
          }
          isSettingHandicap = false;
        } else if (isThinking && !isPondering) {
          if (isInputCommand) {
            Lizzie.board.place(params[1]);
            togglePonder();
            if (Lizzie.frame.isAutocounting) {
              if (Lizzie.board.getHistory().isBlacksTurn())
                Lizzie.frame.zen.sendCommand("play " + "w " + params[1]);
              else Lizzie.frame.zen.sendCommand("play " + "b " + params[1]);

              Lizzie.frame.zen.countStones();
            }
          }
          if (Lizzie.frame.isPlayingAgainstLeelaz) {
            Lizzie.board.place(params[1]);
            if (Lizzie.frame.isAutocounting) {
              if (Lizzie.board.getHistory().isBlacksTurn())
                Lizzie.frame.zen.sendCommand("play " + "w " + params[1]);
              else Lizzie.frame.zen.sendCommand("play " + "b " + params[1]);

              Lizzie.frame.zen.countStones();
            }
            if (!Lizzie.config.playponder) Lizzie.leelaz.nameCmd();
          }
          if (!isInputCommand) {
            isPondering = false;
          }
          isThinking = false;
          if (isInputCommand) {
            isInputCommand = false;
          }
        } else if (isCheckingVersion) {
          String[] ver = params[1].split("\\.");
          int minor = Integer.parseInt(ver[1]);
          // Gtp support added in version 15
          version = minor;
          if (this.currentEngineN == EngineManager.currentEngineNo) {
            Lizzie.config.leelaversion = minor;
          }
          if (minor < 15) {
        	  boolean onTop=false;
              if (Lizzie.frame.isAlwaysOnTop()) {
            	  Lizzie.frame.setAlwaysOnTop(false);          
                  onTop = true;
                }              
            JOptionPane.showMessageDialog(
                Lizzie.frame, "Lizzie需要使用0.15或更新版本的leela zero引擎,当前引擎版本是: " + params[1] + ")");
            if(onTop)Lizzie.frame.setAlwaysOnTop(true);
          }
          isCheckingVersion = false;
          isLoaded = true;
          featurecat.lizzie.gui.Menu.engine[currentEngineN].setIcon(featurecat.lizzie.gui.Menu.ready);
          featurecat.lizzie.gui.Menu.engine[Lizzie.engineManager.currentEngineNo].setIcon(featurecat.lizzie.gui.Menu.icon);
        
        }
      }
      if (isheatmap) {
        if (line.startsWith(" ") || Character.isDigit(line.charAt(0))) {
          try {
            String[] params = line.trim().split("\\s+");
            if (params.length == 19) {
              for (int i = 0; i < params.length; i++) heatcount.add(Integer.parseInt(params[i]));
            }
          } catch (Exception ex) {
          }
        }
        if (line.startsWith("winrate:")) {
          isheatmap = false;
          String[] params = line.trim().split(" ");
          // heatwinrate = Double.valueOf(params[1]);
          Lizzie.frame.refresh();
        }
      }
    }
  }
  private void notifyAutoPlay() {

	  if (Lizzie.frame.toolbar.isAutoPlay) {
		  if((Lizzie.board.getHistory().isBlacksTurn()&&Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())||(!Lizzie.board.getHistory().isBlacksTurn()&&Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()))
		  {
			  int time = 0;
		        int playouts = 0;
		        int firstPlayouts = 0;
		        if (Lizzie.frame.toolbar.chkAutoPlayTime.isSelected()) {
		          try {
		            time = 1000 * Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayTime.getText());
		          } catch (NumberFormatException err) {
		          }
		        }
		        if (Lizzie.frame.toolbar.chkAutoPlayPlayouts.isSelected()) {
		          try {
		            playouts = Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayPlayouts.getText());
		          } catch (NumberFormatException err) {
		          }
		        }
		        if (Lizzie.frame.toolbar.chkAutoPlayFirstPlayouts.isSelected()) {
		          try {
		            firstPlayouts = Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayFirstPlayouts.getText());
		          } catch (NumberFormatException err) {
		          }
		        }
		       
		        if (firstPlayouts > 0) {
		          if (bestMoves.get(0).playouts >= firstPlayouts) {
		        	  int coords[]=Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
		            Lizzie.board.place(coords[0],coords[1]);
		            if((Lizzie.board.getData().blackToPlay&&Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())||(!Lizzie.board.getData().blackToPlay&&Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()))
		            {
		            	 return;
		            }
		          nameCmd();
		          }
		        }
		        if (playouts > 0) {
		          int sum = 0;
		          for (MoveData move : bestMoves) {
		            sum += move.playouts;
		          }
		          if (sum >= playouts) {
		        	  int coords[]=Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
			            Lizzie.board.place(coords[0],coords[1]);
			            if((Lizzie.board.getData().blackToPlay&&Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())||(!Lizzie.board.getData().blackToPlay&&Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()))
			            {
			            	 return;
			            }
			            nameCmd();
		            
		          }
		        }
		        
		        if (time > 0) {
		          if (System.currentTimeMillis() - startPonderTime > time) {
		        	  int coords[]=Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
			            Lizzie.board.place(coords[0],coords[1]);
			            if((Lizzie.board.getData().blackToPlay&&Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())||(!Lizzie.board.getData().blackToPlay&&Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()))
			            {
			            	 return;
			            }
			            nameCmd();
		          
		          }
		        }
		     
		  }
	  }
  }
  
  private void saveAndLoad() {
	  if(!Lizzie.frame.isBatchAna)
	  {  File file = new File("");
   	 String courseFile ="";
        try {
			 courseFile = file.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String df= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File autoSaveFile=new File(courseFile+ "\\" + "AutoSave"+"\\"+df+".sgf");

        File fileParent = autoSaveFile.getParentFile();
        if (!fileParent.exists()) {
        fileParent.mkdirs();
        }
        try {
       	  SGFParser.save(Lizzie.board, autoSaveFile.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        isSaving=false;
        boolean onTop=false;
        if (Lizzie.frame.isAlwaysOnTop()) {
      	  Lizzie.frame.setAlwaysOnTop(false);          
            onTop = true;
          }

        if(analysed)JOptionPane.showMessageDialog(null, "自动分析已完毕,棋谱在目录AutoSave中");
        if(onTop)Lizzie.frame.setAlwaysOnTop(true);
        return;
	  }
	  else {
    	  String name =Lizzie.frame.Batchfiles[Lizzie.frame.BatchAnaNum].getName();
    	  String path=Lizzie.frame.Batchfiles[Lizzie.frame.BatchAnaNum].getParent();
		  String filename=path+"\\"+name.replaceAll("\\..*$", "")+"_已分析.sgf";
		  File autoSaveFile=new File(filename);
		  try {
	       	  SGFParser.save(Lizzie.board, autoSaveFile.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
		  if(Lizzie.frame.Batchfiles.length>(Lizzie.frame.BatchAnaNum+1)) {
			  Timer timer = new Timer();
		      timer.schedule(
		          new TimerTask() {
		            public void run() {
		            	loadAutoBatchFile();
		              this.cancel();
		            }
		          },
		          300);
		  }
		  else {
			  Lizzie.frame.isBatchAna=false;
			  Lizzie.frame.toolbar.chkAnaAutoSave.setEnabled(true);
			  boolean onTop=false;
              if (Lizzie.frame.isAlwaysOnTop()) {
            	  Lizzie.frame.setAlwaysOnTop(false);          
                  onTop = true;
                }
			  JOptionPane.showMessageDialog(null, "批量棋谱已全部分析完毕");
			  if(onTop)Lizzie.frame.setAlwaysOnTop(true);
			  Lizzie.frame.addInput();
			  return;
		  }
	  }
	  
       
  }
  private void savePkFile() {
	  File file = new File("");
	   	 String courseFile ="";
	        try {
				 courseFile = file.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	      
	        String sf= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	        
	        String df="";
	        if(Lizzie.frame.toolbar.isEnginePkBatch)
	        {
	        	df=Lizzie.frame.toolbar.EnginePkBatchNumberNow+"_";
	        }
	        df=df+"黑"+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+"_白"+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename;
	       
	        if(blackResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts)
	        {df=df+"_白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜";}	        
	        else
	        {df=df+"_黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜";}
	        
	        df=df+"_"+sf;
	        //增加如果已命名,则保存在命名的文件夹下
	        File autoSaveFile;
	        File autoSaveFile2 = null;
	        if(Lizzie.frame.toolbar.isEnginePkBatch) {
	        	autoSaveFile=new File(courseFile+ "\\" + "PkAutoSave"+"\\"+Lizzie.frame.toolbar.batchPkName+"\\"+df+".sgf");
	        	autoSaveFile2=new File(courseFile+ "\\" + "PkAutoSave"+"\\"+Lizzie.frame.toolbar.SF+"\\"+df+".sgf");
	        }else {
	        	autoSaveFile=new File(courseFile+ "\\" + "PkAutoSave"+"\\"+df+".sgf");
	        }
	        

	        File fileParent = autoSaveFile.getParentFile();
	        if (!fileParent.exists()) {
	        fileParent.mkdirs();
	        }
	        try {
	       	  SGFParser.save(Lizzie.board, autoSaveFile.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(Lizzie.frame.toolbar.isEnginePkBatch){
					try {
						  File fileParent2 = autoSaveFile2.getParentFile();
					        if (!fileParent2.exists()) {
					        fileParent2.mkdirs();
					        }
					SGFParser.save(Lizzie.board, autoSaveFile2.getPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}}
				e.printStackTrace();
			}   
  }
  private void loadAutoBatchFile() {
	  //sendCommand("clear_board");
	  Lizzie.frame.BatchAnaNum=Lizzie.frame.BatchAnaNum+1;		
	  LizzieFrame.loadFile(Lizzie.frame.Batchfiles[Lizzie.frame.BatchAnaNum]);
	  try {
          Lizzie.frame.toolbar.firstMove = Integer.parseInt(Lizzie.frame.toolbar.txtFirstAnaMove.getText());
        } catch (Exception ex) {
        }
        try {
        	Lizzie.frame.toolbar.lastMove = Integer.parseInt(Lizzie.frame.toolbar.txtLastAnaMove.getText());
        } catch (Exception ex) {
        }
        Lizzie.frame.toolbar.isAutoAna = true;
        Lizzie.frame.toolbar.startAutoAna = true;
        Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(true);
        if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        isSaving=false;
  }
  
  private void notifyAutoAna() {
	
    if (Lizzie.frame.toolbar.isAutoAna) {
    	
    	if(Lizzie.frame.toolbar.startAutoAna)
    	{
    		if(Lizzie.frame.toolbar.firstMove!=-1)
        	{
    			 while (Lizzie.board.previousMove()) ;
        		Lizzie.board.goToMoveNumberBeyondBranch(Lizzie.frame.toolbar.firstMove-1);        		
        	}
    		if(!Lizzie.board.getHistory().getNext().isPresent())
    		{
    			 Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(false);
    			  togglePonder();
    		        Lizzie.frame.toolbar.isAutoAna = false;
    		        Lizzie.frame.addInput();
    			if(Lizzie.frame.isBatchAna)
    			{
    				loadAutoBatchFile();
    			}
    			return;
    		}
    		else {
    		analysed=false;
    		  isClosing=false;
    		Lizzie.frame.toolbar.startAutoAna=false;
    		}
    	}
    	if(isClosing)return;
      if (Lizzie.board.getHistory().getNext().isPresent()) {
    	  if(Lizzie.frame.toolbar.lastMove!=-1)
    	  {
    		  if(Lizzie.frame.toolbar.lastMove<Lizzie.board.getHistory().getData().moveNumber)
    	  {
    		 closeAutoAna();
    		 return;
    	  }
    	  }
    	int time = 0;
        int playouts = 0;
        int firstPlayouts = 0;
        if (Lizzie.frame.toolbar.chkAnaTime.isSelected()) {
          try {
            time = 1000 * Integer.parseInt(Lizzie.frame.toolbar.txtAnaTime.getText());
          } catch (NumberFormatException err) {
          }
        }
        if (Lizzie.frame.toolbar.chkAnaPlayouts.isSelected()) {
          try {
            playouts = Integer.parseInt(Lizzie.frame.toolbar.txtAnaPlayouts.getText());
          } catch (NumberFormatException err) {
          }
        }
        if (Lizzie.frame.toolbar.chkAnaFirstPlayouts.isSelected()) {
          try {
            firstPlayouts = Integer.parseInt(Lizzie.frame.toolbar.txtAnaFirstPlayouts.getText());
          } catch (NumberFormatException err) {
          }
        }
        
        if (firstPlayouts > 0) {
          if (bestMoves.get(0).playouts >= firstPlayouts) {
            Lizzie.board.nextMove();
            analysed=true;
            return;
          }
        }
        if (playouts > 0) {
          int sum = 0;
          for (MoveData move : bestMoves) {
            sum += move.playouts;
          }
          if (sum >= playouts) {
            Lizzie.board.nextMove();
            analysed=true;
            return;
          }
        }
       
        if (time > 0) {
          if (System.currentTimeMillis() - startPonderTime > time) {
            Lizzie.board.nextMove();
            analysed=true;
            return;
          }
        }
     
      }else {
    	  closeAutoAna();
      }
    }
  }
  
  public void pkResign() {
	if(!resigned||isResigning)
		return;
	  isResigning=true;
	
	 // System.out.println(this.currentEnginename+"认输");
	  Lizzie.gtpConsole.addLine(currentEnginename+"认输");
	 
	  //Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).notPondering();
	 // Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).notPondering();
	  Lizzie.board.updateComment();
	  if(blackResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts)
      {
		  //df=df+"_白胜";
		  
		if(Lizzie.frame.toolbar.EnginePkBatchNumberNow%2==0)
			Lizzie.frame.toolbar.pkBlackWins=Lizzie.frame.toolbar.pkBlackWins+1;
		  
		else
			 Lizzie.frame.toolbar.pkWhiteWins=Lizzie.frame.toolbar.pkWhiteWins+1;GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
		  gameInfo.setResult("白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜");
		  Lizzie.frame.setResult("白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜");
		  }
      else
      {
    	  //df=df+"_黑胜";
    	  
    	  GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
		  gameInfo.setResult("黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜");
		  Lizzie.frame.setResult("黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜");
    	
    	
			if(Lizzie.frame.toolbar.EnginePkBatchNumberNow%2==0)
				  Lizzie.frame.toolbar.pkWhiteWins=Lizzie.frame.toolbar.pkWhiteWins+1;
				else
					Lizzie.frame.toolbar.pkBlackWins=Lizzie.frame.toolbar.pkBlackWins+1;
    		
    	  }
	 
	  if(Lizzie.frame.toolbar.AutosavePk||Lizzie.frame.toolbar.isEnginePkBatch)
	  {
		  if(doublePass)
		  {
			  savePassFile();
			  doublePass=false;
		  }
		  else {
		  savePkFile();
		  }
	  }
	  
	  if(Lizzie.frame.toolbar.isEnginePkBatch)
	  {		  
		  int EnginePkBatchNumber=1;
		  try {
	          EnginePkBatchNumber = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkBatch.getText());
	        } catch (NumberFormatException err) {	     
	        }
		  if(Lizzie.frame.toolbar.EnginePkBatchNumberNow<EnginePkBatchNumber) {
		  Lizzie.frame.toolbar.EnginePkBatchNumberNow=Lizzie.frame.toolbar.EnginePkBatchNumberNow+1;
		  //下一盘PK
		  if(Lizzie.frame.toolbar.checkGameTime)
    	  {
        	  Lizzie.engineManager.gameTime=System.currentTimeMillis();
    	  }
		  Lizzie.frame.setResult("");
		  if(Lizzie.frame.toolbar.exChange)
		 // if(false)
		  {
			  int temp=Lizzie.frame.toolbar.engineBlack;
			  Lizzie.frame.toolbar.engineBlack=Lizzie.frame.toolbar.engineWhite;
			  Lizzie.frame.toolbar.engineWhite=temp;
			  if(Lizzie.frame.toolbar.EnginePkBatchNumberNow%2==0)
			  {
				  Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
				  Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
				  int tempindex=Lizzie.frame.toolbar.enginePkBlack.getSelectedIndex();
				 Lizzie.frame.toolbar.enginePkBlack.setSelectedIndex(Lizzie.frame.toolbar.enginePkWhite.getSelectedIndex());
				  Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(tempindex);
				  Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
				  Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
				  Lizzie.frame.toolbar.lblenginePkResult.setText(Lizzie.frame.toolbar.pkWhiteWins+":"+Lizzie.frame.toolbar.pkBlackWins);
				  String temp1=Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
				  Lizzie.frame.toolbar.txtenginePkFirstPlayputs.setText(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
				  Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
				  temp1=Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
				  Lizzie.frame.toolbar.txtenginePkPlayputs.setText(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
				  Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);
			  }
			  else {
				  Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
				  Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
				  int tempindex=Lizzie.frame.toolbar.enginePkBlack.getSelectedIndex();
					 Lizzie.frame.toolbar.enginePkBlack.setSelectedIndex(Lizzie.frame.toolbar.enginePkWhite.getSelectedIndex());
					  Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(tempindex);
				  Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
				  Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
				  Lizzie.frame.toolbar.lblenginePkResult.setText(Lizzie.frame.toolbar.pkBlackWins+":"+Lizzie.frame.toolbar.pkWhiteWins);
				  String temp1=Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
				  Lizzie.frame.toolbar.txtenginePkFirstPlayputs.setText(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
				  Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
				  temp1=Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
				  Lizzie.frame.toolbar.txtenginePkPlayputs.setText(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
				  Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);
			  }
		  }
		  else {Lizzie.frame.toolbar.lblenginePkResult.setText(Lizzie.frame.toolbar.pkBlackWins+":"+Lizzie.frame.toolbar.pkWhiteWins);}
		  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).blackResignMoveCounts=0;
		  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).whiteResignMoveCounts=0;
    	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).whiteResignMoveCounts=0; 
    	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).blackResignMoveCounts=0; 
    	  Lizzie.board.clearforpk();
    	  if (Lizzie.frame.toolbar.chkenginePkContinue.isSelected()) {
              Lizzie.board.setlist(Lizzie.frame.toolbar.startGame);
            }
            if (Lizzie.board.getHistory().isBlacksTurn()) {
            	//Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).notPondering();
              Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineWhite);
              //Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).Pondering();
              Lizzie.engineManager.startEngineForPkPonder(Lizzie.frame.toolbar.engineBlack);
              
              
            } else {
            	//Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).notPondering();
              Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineBlack);
              //Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).Pondering();
              Lizzie.engineManager.startEngineForPkPonder(Lizzie.frame.toolbar.engineWhite);
             
            }
            
            Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());
          //  Lizzie.leelaz.ponder();
//            Timer timer = new Timer();
//            timer.schedule(
//                new TimerTask() {
//                  public void run() {
//                	  Lizzie.leelaz.ponder();
//                	 // Lizzie.leelaz.playanyway=true;
//                    this.cancel();
//                  }
//                },
//               1000);
            
        Lizzie.frame.setPlayers(
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename,
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename);
        GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
		 
        gameInfo.setPlayerWhite(
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename);
        gameInfo.setPlayerBlack(
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename);
       
	  }
		  else {

			  //结束PK
			  if(Lizzie.frame.toolbar.exChange&&Lizzie.frame.toolbar.EnginePkBatchNumberNow%2==0) {		
			  Lizzie.frame.toolbar.lblenginePkResult.setText(Lizzie.frame.toolbar.pkWhiteWins+":"+Lizzie.frame.toolbar.pkBlackWins);		
			  }
			  else {
				  Lizzie.frame.toolbar.lblenginePkResult.setText(Lizzie.frame.toolbar.pkBlackWins+":"+Lizzie.frame.toolbar.pkWhiteWins);
			  }
	          Lizzie.frame.addInput();
	          Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
	          Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
	          Lizzie.frame.toolbar.txtenginePkBatch.setEnabled(true);
	          Lizzie.frame.toolbar.btnEnginePkConfig.setEnabled(true);
	          Lizzie.frame.toolbar.isEnginePk=false;
	          Lizzie.frame.toolbar.btnStartPk.setText("开始对战");
	          Lizzie.frame.toolbar.chkenginePkBatch.setEnabled(true);
	         // chkenginePkgenmove.setEnabled(true);
	          Lizzie.frame.toolbar.chkenginePk.setEnabled(true);
	          Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
	          Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).played=false;
              Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).played=false;
	         // Lizzie.engineManager.switchEngineForEndPk(Lizzie.engineManager.currentEngineNo);          
	          Lizzie.frame.toolbar.batchPkName="";
	        boolean onTop = false;
	        if (Lizzie.frame.isAlwaysOnTop()) {
	          Lizzie.frame.setAlwaysOnTop(false);
	          onTop = true;
	        }
	        
	       
	        JOptionPane.showMessageDialog(Lizzie.frame,"批量对战已结束,比分为"+Lizzie.frame.toolbar.pkBlackWins+":"+Lizzie.frame.toolbar.pkWhiteWins+"棋谱保存在PkAutoSave文件夹下" );
	       
	        if (onTop) Lizzie.frame.setAlwaysOnTop(true);
	        Lizzie.engineManager.changeEngIcoForEndPk();
		  }
  }
	  else {
		  //结束PKLizzie.engineManager.engineList.get(engineBlack).notPondering();
		  
			  Lizzie.frame.toolbar.lblenginePkResult.setText(Lizzie.frame.toolbar.pkBlackWins+":"+Lizzie.frame.toolbar.pkWhiteWins);
		
          Lizzie.frame.addInput();
          Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
          Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
          Lizzie.frame.toolbar.txtenginePkBatch.setEnabled(true);
          Lizzie.frame.toolbar.btnEnginePkConfig.setEnabled(true);
          Lizzie.frame.toolbar.isEnginePk=false;
          Lizzie.frame.toolbar.btnStartPk.setText("开始对战");
          Lizzie.frame.toolbar.chkenginePkBatch.setEnabled(true);
         // chkenginePkgenmove.setEnabled(true);
          Lizzie.frame.toolbar.chkenginePk.setEnabled(true);
          Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
          Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).played=false;
          Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).played=false;
         // Lizzie.engineManager.switchEngineForEndPk(Lizzie.engineManager.currentEngineNo);          
          Lizzie.frame.toolbar.batchPkName="";
        boolean onTop = false;
        if (Lizzie.frame.isAlwaysOnTop()) {
          Lizzie.frame.setAlwaysOnTop(false);
          onTop = true;
        }
        
      
        	String jg="对战已结束，";
        	if(currentEngineN==Lizzie.frame.toolbar.engineBlack)
            {
      		  //df=df+"_白胜";
        		jg=jg+"白胜";
            }
        	else {
        		jg=jg+"黑胜";
        	}
        	if(Lizzie.frame.toolbar.AutosavePk)
        	{jg=jg+"，棋谱保存在PkAutoSave文件夹下";}
        	 JOptionPane.showMessageDialog(Lizzie.frame,jg );
       
        if (onTop) Lizzie.frame.setAlwaysOnTop(true);
        Lizzie.engineManager.changeEngIcoForEndPk();
	  }
	
  }
  
  private void savePassFile() {
	    File file = new File("");
	    String courseFile = "";
	    try {
	      courseFile = file.getCanonicalPath();
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    File autoSaveFile;
	    File autoSaveFile2 = null;
	    String sf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    autoSaveFile =
	        new File(
	            courseFile
	                + "\\"
	                + "PkAutoSave"
	                + "\\"
	                + Lizzie.frame.toolbar.batchPkName
	                + "\\"
	                + "双Pass对局"
	                + sf
	                + ".sgf");
	    autoSaveFile2 =
	        new File(
	            courseFile
	                + "\\"
	                + "PkAutoSave"
	                + "\\"
	                + Lizzie.frame.toolbar.SF
	                + "\\"
	                + "双Pass对局"
	                + sf
	                + ".sgf");

	    File fileParent = autoSaveFile.getParentFile();
	    if (!fileParent.exists()) {
	      fileParent.mkdirs();
	    }
	    try {
	      SGFParser.save(Lizzie.board, autoSaveFile.getPath());
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      if (Lizzie.frame.toolbar.isEnginePkBatch) {
	        try {
	          File fileParent2 = autoSaveFile2.getParentFile();
	          if (!fileParent2.exists()) {
	            fileParent2.mkdirs();
	          }
	          SGFParser.save(Lizzie.board, autoSaveFile2.getPath());
	        } catch (IOException e1) {
	          // TODO Auto-generated catch block
	          e1.printStackTrace();
	        }
	      }
	      e.printStackTrace();
	    }
	  }
  
  private void notifyAutoPK() {	
	  
		  if(Lizzie.frame.toolbar.isPkStop)
		  {
			 nameCmd();
			 return;
		  }
	 
	  if (Lizzie.frame.toolbar.isEnginePk&&!played) {
		  double curWR=this.bestMoves.get(0).winrate;
		  
			  int time = 0;
		        int playouts = 0;
		        int firstPlayouts = 0;
		        if (Lizzie.frame.toolbar.chkenginePkTime.isSelected()) {
		          try {
		            time = 1000 * Integer.parseInt(Lizzie.frame.toolbar.txtenginePkTime.getText());
		          } catch (NumberFormatException err) {
		          }
		        }
		        if (Lizzie.frame.toolbar.chkenginePkPlayouts.isSelected()) {
		        	if(Lizzie.board.getData().blackToPlay) {
		          try {
		            playouts = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkPlayputs.getText());
		          } catch (NumberFormatException err) {
		          }
		        	}
		        	else {
		        		 try {
		 		            playouts = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
		 		          } catch (NumberFormatException err) {
		 		          }
		        	}
		        }
		        if (Lizzie.frame.toolbar.chkenginePkFirstPlayputs.isSelected()) {
		        	if(Lizzie.board.getData().blackToPlay) {
		          try {
		            firstPlayouts = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText());
		          } catch (NumberFormatException err) {
		          }
		        	}
		        	else {
		        		 try {
		 		            firstPlayouts = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
		 		          } catch (NumberFormatException err) {
		 		          }
		        	}
		        }
		       
		        if (firstPlayouts > 0) {
		          if (bestMoves.get(0).playouts >= firstPlayouts) {
		        	  played=true;
		        	 // if(playanyway)
		        	//	  playanyway=false;
		        	  if(resigned)
		        	  resigned=false; 
		        	  if(curWR<Lizzie.frame.toolbar.pkResginWinrate)
		    		  { if(Lizzie.board.getHistory().isBlacksTurn())
		    			  blackResignMoveCounts=blackResignMoveCounts+1;
		    		  else
		    			  whiteResignMoveCounts=whiteResignMoveCounts+1;
		    		  }
		        	  else {
		        		  
		        		  if(blackResignMoveCounts>0||whiteResignMoveCounts>0)
		        		  {  if(Lizzie.board.getHistory().isBlacksTurn())
		        				  blackResignMoveCounts=0;
		        		  else
		        			  whiteResignMoveCounts=0;}
		        	  }
		        	  if(blackResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts||whiteResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts)
		    		  {
		        		  
		        		  resigned=true;
		        		  
		        		  //pkResign();
		        		  //System.out.println("认输1"+this.currentEngineN);
		        		  nameCmd();		        		  
		    			  return;
		    		  }
		        	  if(isResigning)
		        	  isResigning=false;  
		        	  if(bestMoves.get(0).coordinate.equals("pass"))
		        	  {
		        		  Optional<int[]> passStep = Optional.empty();
		        		  Optional<int[]> lastMove=Lizzie.board.getLastMove();
				          if(lastMove==passStep)
				          {
				        	  Lizzie.board.pass();
				        	  doublePass=true;
				        	  resigned=true;
				        	  nameCmd();		        		  
			    			  return;
				          }
				          
		        		  if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
				          {
		        			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder( "B", "pass");
		        			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B", "pass");
		        			  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
				          //Lizzie.leelaz.isPondering=true;		         
				          Lizzie.board.pass();				          
				         // Lizzie.leelaz.played=false;
				          }
				          
				          else
				          {  
				   			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W", "pass");
				        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W", "pass");				        	  
				        	  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
				          //Lizzie.leelaz.isPondering=true;		          
				          Lizzie.board.pass();
				          //Lizzie.leelaz.played=false;
				          }
		        		  return;
		        	  }
		        	  
		        	  
		        	  int coords[]=Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);		
		        	  
		          //nameCmd();
		          if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
		          {
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder("B", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
				      
		        	  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
		          //Lizzie.leelaz.isPondering=true;		         
		          Lizzie.board.place(coords[0],coords[1]);	
		          //Lizzie.leelaz.played=false;
		          }
		          
		          else
		          { // 
		          //Lizzie.leelaz.isPondering=true;		       
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
		        	  Lizzie.board.place(coords[0],coords[1]);	
		         // Lizzie.leelaz.played=false;
		          }
		          return;
		          }
		        }
		        if (playouts > 0) {
		          int sum = 0;
		          for (MoveData move : bestMoves) {
		            sum += move.playouts;
		          }
		          if (sum >= playouts) {
		        	  played=true;
		        	 // if(playanyway)
		        	//	  playanyway=false;
		        	  if(resigned)
		        	  resigned=false; 
		        	  if(curWR<Lizzie.frame.toolbar.pkResginWinrate)
		    		  { if(Lizzie.board.getHistory().isBlacksTurn())
		    			  blackResignMoveCounts=blackResignMoveCounts+1;
		    		  else
		    			  whiteResignMoveCounts=whiteResignMoveCounts+1;
		    		  }
		        	  else {
		        		  
		        		  if(blackResignMoveCounts>0||whiteResignMoveCounts>0)
		        		  {  if(Lizzie.board.getHistory().isBlacksTurn())
		        				  blackResignMoveCounts=0;
		        		  else
		        			  whiteResignMoveCounts=0;}
		        	  }
		        	  if(blackResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts||whiteResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts)
		    		  {
		        		  resigned=true;
		        		  //pkResign();
		        		  //System.out.println("认输2"+this.currentEngineN);
		        		  nameCmd();
		        		  
		        		 
				           
		    			  return;
		    		  }
		        	  if(isResigning)
		        	  isResigning=false;
		        	  if(bestMoves.get(0).coordinate.equals("pass"))
		        	  {
		        		  Optional<int[]> passStep = Optional.empty();
		        		  Optional<int[]> lastMove=Lizzie.board.getLastMove();
				          if(lastMove==passStep)
				          {
				        	  Lizzie.board.pass();	
				        	  doublePass=true;
				        	  resigned=true;
				        	  nameCmd();		        		  
			    			  return;
				          }
		        		  if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
				          {
		        			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder( "B", "pass");
		        			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B", "pass");
		        			//  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
				          //Lizzie.leelaz.isPondering=true;		         
				        Lizzie.board.pass();				          
				         // Lizzie.leelaz.played=false;
				          }
				          
				          else
				          {  
				   			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W", "pass");
				        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W", "pass");				        	  
				        	// Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
				          //Lizzie.leelaz.isPondering=true;		          
				         Lizzie.board.pass();
				          //Lizzie.leelaz.played=false;
				          }
		        		  return;
		        	  }
		        	  
		        	  
		        	  int coords[]=Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);		
		        	  
		          //nameCmd();
		          if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
		          {
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder("B", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	 // Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).bestMoves = new ArrayList<>();
		        	 // Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
		          //Lizzie.leelaz.isPondering=true;		         
		          Lizzie.board.place(coords[0],coords[1]);	
		          
		          //Lizzie.leelaz.played=false;
		          }
		          
		          else
		          { 
		          //Lizzie.leelaz.isPondering=true;		       
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  //Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).bestMoves = new ArrayList<>();
		        	 // Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
		        	  Lizzie.board.place(coords[0],coords[1]);	
		        	  
		         // Lizzie.leelaz.played=false;
		          }
			          return;
		          			        
		          }
		        }
		        
		        if (time > 0) {
		          if (System.currentTimeMillis() - startPonderTime > time) {
		        	  played=true;	
		        	//  if(playanyway)
		        	//	  playanyway=false;
		        	  if(resigned)
		        	  resigned=false; 
		        	  if(curWR<Lizzie.frame.toolbar.pkResginWinrate)
		    		  { if(Lizzie.board.getHistory().isBlacksTurn())
		    			  blackResignMoveCounts=blackResignMoveCounts+1;
		    		  else
		    			  whiteResignMoveCounts=whiteResignMoveCounts+1;
		    		  }
		        	  else {
		        		  
		        		  if(blackResignMoveCounts>0||whiteResignMoveCounts>0)
		        		  {  if(Lizzie.board.getHistory().isBlacksTurn())
		        				  blackResignMoveCounts=0;
		        		  else
		        			  whiteResignMoveCounts=0;}
		        	  }
		        	  if(blackResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts||whiteResignMoveCounts>=Lizzie.frame.toolbar.pkResignMoveCounts)
		    		  {
		        		  resigned=true;
		        		 // pkResign();
		        		  //System.out.println("认输3"+this.currentEngineN);
		        		  nameCmd();	
		    			  return;
		    		  }
		        	  if(isResigning)
		        	 isResigning=false;
		        	  if(bestMoves.get(0).coordinate.equals("pass"))
		        	  {
		        		  
		        		  Optional<int[]> passStep = Optional.empty();
		        		  Optional<int[]> lastMove=Lizzie.board.getLastMove();
				          if(lastMove==passStep)
				          {
				        	  Lizzie.board.pass();	
				        	  doublePass=true;
				        	  resigned=true;
				        	  nameCmd();		        		  
			    			  return;
				          }
		        		  if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
				          {
		        			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder( "B", "pass");
		        			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B", "pass");
		        			  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
				          //Lizzie.leelaz.isPondering=true;		         
				          Lizzie.board.pass();				          
				         // Lizzie.leelaz.played=false;
				          }
				          
				          else
				          {  
				   			  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W", "pass");
				        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W", "pass");				        	  
				        	  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
				          //Lizzie.leelaz.isPondering=true;		          
				          Lizzie.board.pass();
				          //Lizzie.leelaz.played=false;
				          }
		        		  return;
		        	  }
		        	  
		        	  
		        	  int coords[]=Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);		
		        	  
		          //nameCmd();
		          if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
		          {
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder("B", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
				      
		        	  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
		          //Lizzie.leelaz.isPondering=true;		         
		          Lizzie.board.place(coords[0],coords[1]);	
		          //Lizzie.leelaz.played=false;
		          }
		          
		          else
		          { 
		          //Lizzie.leelaz.isPondering=true;		       
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W", Lizzie.board.convertCoordinatesToName(coords[0],coords[1]));
		        	  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
		        	  Lizzie.board.place(coords[0],coords[1]);	
		         // Lizzie.leelaz.played=false;
		          }
			          return;
		          }
		        }
		 
	  }
  }

  public void nameCmd() {
	  try{sendCommand("name");}
	  catch (Exception es)
	  {
		  
	  }
  }
  /**
   * Parse a move-data line of Leelaz output
   *
   * @param line output line
   */
  private void parseMoveDataLine(String line) {
    line = line.trim();
    // ignore passes, and only accept lines that start with a coordinate letter
    if (line.length() > 0 && Character.isLetter(line.charAt(0)) && !line.startsWith("pass")) {
      if (!(Lizzie.frame.isPlayingAgainstLeelaz
          && Lizzie.frame.playerIsBlack != Lizzie.board.getData().blackToPlay)) {
        try {
          bestMovesTemp.add(MoveData.fromInfo(line));
        } catch (ArrayIndexOutOfBoundsException e) {
          // this is very rare but is possible. ignore
        }
      }
    }
  }

  /** Continually reads and processes output from leelaz */
  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = process.getInputStream().read()) != -1) {
   //   while (true) {
    	 // c = process.getInputStream().read();
        line.append((char) c);
        if ((c == '\n')) {
        	if(!this.bestMoves.isEmpty())
      	  { notifyAutoAna();
            notifyAutoPlay();
          //  notifyAutoPK();    
            pkResign();
            }
        	if(!played)
          parseLine(line.toString());
         
          line = new StringBuilder();
        }
      }
      // this line will be reached when Leelaz shuts down
      System.out.println("Leelaz process ended.");
      process.destroy();
      shutdown();
    //   Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      //e.printStackTrace();
    	System.out.println("读出错");
      //System.exit(-1);
    //	read();
    }
  }
  
  private void closeAutoAna() {
	  if(!isClosing)
	  {
		  Lizzie.frame.toolbar.isAutoAna = false;
		  isClosing=true;
		  Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(false);
		  togglePonder();
	        
	        Lizzie.frame.addInput();
	        if(!isSaving&&Lizzie.frame.toolbar.chkAnaAutoSave.isSelected()&&analysed)
	        {
	        	isSaving=true;
	        	saveAndLoad();
	        	
	        }else {
	        	 boolean onTop=false;
	             if (Lizzie.frame.isAlwaysOnTop()) {
	           	  Lizzie.frame.setAlwaysOnTop(false);          
	                 onTop = true;
	               }
	             if(analysed)JOptionPane.showMessageDialog(null, "自动分析已完毕");
	        if(onTop)Lizzie.frame.setAlwaysOnTop(true);
	       
	  }
	      
	  }
  }

  /**
   * Sends a command to command queue for leelaz to execute
   *
   * @param command a GTP command containing no newline characters
   */
  public void sendCommand(String command) {
    synchronized (cmdQueue) {
      // For efficiency, delete unnecessary "lz-analyze" that will be stopped immediately
      if (!cmdQueue.isEmpty() && cmdQueue.peekLast().startsWith("lz-analyze")) {
        cmdQueue.removeLast();
      }
      cmdQueue.addLast(command);
      trySendCommandFromQueue();
    }
  }

  /** Sends a command from command queue for leelaz to execute if it is ready */
  private void trySendCommandFromQueue() {
    // Defer sending "lz-analyze" if leelaz is not ready yet.
    // Though all commands should be deferred theoretically,
    // only "lz-analyze" is differed here for fear of
    // possible hang-up by missing response for some reason.
    // cmdQueue can be replaced with a mere String variable in this case,
    // but it is kept for future change of our mind.
    synchronized (cmdQueue) {
      if (cmdQueue.isEmpty()
          || cmdQueue.peekFirst().startsWith("lz-analyze")&& !isResponseUpToDate()) {
        return;
      }
      String command = cmdQueue.removeFirst();
      sendCommandToLeelaz(command);
    }
  }

  /**
   * Sends a command for leelaz to execute
   *
   * @param command a GTP command containing no newline characters
   */
  private void sendCommandToLeelaz(String command) {
    if (command.startsWith("fixed_handicap")) isSettingHandicap = true;
    if (printCommunication) {
      System.out.println(currentEnginename+" "+ cmdNumber+" "+ command);
    }
    if(Lizzie.gtpConsole.isVisible())
    Lizzie.gtpConsole.addCommand(command, cmdNumber,currentEnginename);
    command = cmdNumber + " " + command;
    cmdNumber++;
    try {
     outputStream.write((command + "\n").getBytes());      
     outputStream.flush();
    	 //  process.getOutputStream().write((command + "\n").getBytes());
    	//   process.getOutputStream().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
//    if(executor!=null&&executor.isShutdown())
//    {	executor = Executors.newSingleThreadScheduledExecutor();
//    executor.execute(this::read);}
  }

 
  /** Check whether leelaz is responding to the last command */
  private boolean isResponseUpToDate() {
    // Use >= instead of == for avoiding hang-up, though it cannot happen
    return currentCmdNum >= cmdNumber - 1;
  }

  
  /**
   * @param color color of stone to play
   * @param move coordinate of the coordinate
   */
  public void playMove(Stone color, String move) {
	  if(Lizzie.frame.toolbar.isEnginePk)
		  return;
    synchronized (this) {
      String colorString;
      switch (color) {
        case BLACK:
          colorString = "B";
          break;
        case WHITE:
          colorString = "W";
          break;
        default:
          throw new IllegalArgumentException(
              "The stone color must be B or W, but was " + color.toString());
      }

      sendCommand("play " + colorString + " " + move);
      bestMoves = new ArrayList<>();

      if (isPondering && !Lizzie.frame.isPlayingAgainstLeelaz) ponder();
    }
  }
  
  public void playMoveNoPonder( String colorString,String move) {
	    synchronized (this) {
	      //String colorString;
//	      switch (color) {
//	        case BLACK:
//	          colorString = "B";
//	          break;
//	        case WHITE:
//	          colorString = "W";
//	          break;
//	        default:
//	          throw new IllegalArgumentException(
//	              "The stone color must be B or W, but was " + color.toString());
//	      }

	      sendCommand("play " + colorString + " " + move);
	    //  bestMoves = new ArrayList<>();
	     
	    }
	  }
  
//  public void playPassNoPonder(Stone color) {
//	  synchronized (this) {
//	  String colorString;
//	  switch (color) {
//      case BLACK:
//        colorString = "B";
//        break;
//      case WHITE:
//        colorString = "W";
//        break;
//      default:
//        throw new IllegalArgumentException(
//            "The stone color must be B or W, but was " + color.toString());
//    }
//	      sendCommand("play " + colorString +" pass");
//	      played=false;
//	      //bestMoves = new ArrayList<>();
//	     
//	  }
//	  }
//  
//  public void playPassPonder(Stone color) {
//	  synchronized (this) {
//	  String colorString;
//	  switch (color) {
//      case BLACK:
//        colorString = "B";
//        break;
//      case WHITE:
//        colorString = "W";
//        break;
//      default:
//        throw new IllegalArgumentException(
//            "The stone color must be B or W, but was " + color.toString());
//    }
//	      sendCommand("play " + colorString +" pass");
//	      played=false;
//	   //   bestMoves = new ArrayList<>();
//	      ponder();	     
//	  }
//	  }
  
  public void playMovePonder(String colorString, String move) {
	    synchronized (this) {
//	      String colorString;
//	      switch (color) {
//	        case BLACK:
//	          colorString = "B";
//	          break;
//	        case WHITE:
//	          colorString = "W";
//	          break;
//	        default:
//	          throw new IllegalArgumentException(
//	              "The stone color must be B or W, but was " + color.toString());
//	      }
	      played=false;
	      bestMoves = new ArrayList<>();
	      sendCommand("play " + colorString + " " + move);	    
	      ponder();
	    }
	  }

  public void playMovewithavoid(Stone color, String move) {
	  if(Lizzie.frame.toolbar.isEnginePk)
	  {return;
	  }
    synchronized (this) {
      String colorString;
      switch (color) {
        case BLACK:
          colorString = "B";
          break;
        case WHITE:
          colorString = "W";
          break;
        default:
          throw new IllegalArgumentException(
              "The stone color must be B or W, but was " + color.toString());
      }

      sendCommand("play " + colorString + " " + move);
      bestMoves = new ArrayList<>();

      if (isPondering && !Lizzie.frame.isPlayingAgainstLeelaz) ponder();
    }
  }

  public void genmove(String color) {
    String command = "genmove " + color;
    /*
     * We don't support displaying this while playing, so no reason to request it (for now)
    if (isPondering) {
        command = "lz-genmove_analyze " + color + " 10";
    }*/
    sendCommand(command);
    isThinking = true;
    isPondering = false;
  }

  public void genmove_analyze(String color) {
    String command =
        "lz-genmove_analyze " + color + " " + Lizzie.config.analyzeUpdateIntervalCentisec;
    sendCommand(command);
    isThinking = true;
    isPondering = false;
  }

  public void time_settings() {
    Lizzie.leelaz.sendCommand(
        "time_settings 0 "
            + Lizzie.config.config.getJSONObject("leelaz").getInt("max-game-thinking-time-seconds")
            + " 1");
  }

  public void clear() {
    synchronized (this) {
      sendCommand("clear_board");
      bestMoves = new ArrayList<>();
      if (isPondering) ponder();
    }
  }
  
  public void clearWithoutPonder() {
	    synchronized (this) {
	      sendCommand("clear_board");
	      bestMoves = new ArrayList<>();
	   //   if (isPondering) ponder();
	    }
	  }

  public void undo() {
    synchronized (this) {
      sendCommand("undo");
      bestMoves = new ArrayList<>();
      if (isPondering) ponder();
    }
  }

  public void analyzeAvoid(String type, String color, String coordList, int untilMove) {

    // added for change bestmoves immediatly not wait until totalplayouts is bigger than previous
    // analyze result
    analyzeAvoid(
        String.format("%s %s %s %d", type, color, coordList, untilMove <= 0 ? 1 : untilMove));
    Lizzie.board.clearbestmoves();
  }

  public void analyzeAvoid(String parameters) {

    // added for change bestmoves immediatly not wait until totalplayouts is bigger than previous
    // analyze result
    bestMoves = new ArrayList<>();
    if (!isPondering) {
      isPondering = true;
      startPonderTime = System.currentTimeMillis();
    }
    sendCommand(
        String.format("lz-analyze %d %s", Lizzie.config.analyzeUpdateIntervalCentisec, parameters));
    Lizzie.board.clearbestmoves();
  }

  /** This initializes leelaz's pondering mode at its current position */
  public void ponder() {
    isPondering = true;
    if (Lizzie.frame.isheatmap) {
      Lizzie.leelaz.heatcount.clear();
      // Lizzie.frame.isheatmap = false;
    }
    if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
      return;
    }
    startPonderTime = System.currentTimeMillis();
    int currentmove = Lizzie.board.getcurrentmovenumber();
    if (featurecat.lizzie.gui.RightClickMenu.move > 0
        && featurecat.lizzie.gui.RightClickMenu.move >= currentmove
        && currentmove >= featurecat.lizzie.gui.RightClickMenu.startmove) {
      featurecat.lizzie.gui.RightClickMenu.voidanalyze();
    } else {
      featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
      featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
      featurecat.lizzie.gui.RightClickMenu.move = 0;
      featurecat.lizzie.gui.RightClickMenu.isforcing = false;
      sendCommand(
          "lz-analyze "
              + Lizzie.config.analyzeUpdateIntervalCentisec); // until it responds to this, incoming
      // ponder results are obsolete
    }
  }

  public void ponderwithavoid() {
    isPondering = true;
    if (Lizzie.frame.isheatmap) {
      Lizzie.leelaz.heatcount.clear();
      //   Lizzie.frame.isheatmap = false;
    }
    if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
      return;
    }
    startPonderTime = System.currentTimeMillis();
    int currentmove = Lizzie.board.getcurrentmovenumber();
    if (featurecat.lizzie.gui.RightClickMenu.move > 0
        && featurecat.lizzie.gui.RightClickMenu.move > currentmove
        && currentmove >= featurecat.lizzie.gui.RightClickMenu.startmove) {
      featurecat.lizzie.gui.RightClickMenu.voidanalyzeponder();
    } else {
      featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
      featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
      featurecat.lizzie.gui.RightClickMenu.move = 0;
      featurecat.lizzie.gui.RightClickMenu.isforcing = false;
      sendCommand(
          "lz-analyze "
              + Lizzie.config.analyzeUpdateIntervalCentisec); // until it responds to this, incoming
      // ponder results are obsolete
    }
  }

  public void togglePonder() {
    isPondering = !isPondering;
    if (isPondering) {
      ponder();
    } else {
      sendCommand("name"); // ends pondering
    }
  }

  /** End the process */
  public void shutdown() {
    process.destroy();
  }

  public List<MoveData> getBestMoves() {
    synchronized (this) {
      return bestMoves;
    }
  }

  //public Optional<String> getDynamicKomi() {
 //   if (Float.isNaN(dynamicKomi) || Float.isNaN(dynamicOppKomi)) {
 //     return Optional.empty();
 //   } else {
 //     return Optional.of(String.format("%.1f / %.1f", dynamicKomi, dynamicOppKomi));
 //   }
 // }

  public boolean isPondering() {
    return isPondering;
  }

  public void Pondering() {
    isPondering = true;
  }

  public void notPondering() {
    isPondering = false;
  }

  public class WinrateStats {
    public double maxWinrate;
    public int totalPlayouts;

    public WinrateStats(double maxWinrate, int totalPlayouts) {
      this.maxWinrate = maxWinrate;
      this.totalPlayouts = totalPlayouts;
    }
  }

  /*
   * Return the best win rate and total number of playouts.
   * If no analysis available, win rate is negative and playouts is 0.
   */
  public WinrateStats getWinrateStats() {
    WinrateStats stats = new WinrateStats(-100, 0);

    if (!bestMoves.isEmpty()) {
      // we should match the Leelaz UCTNode get_eval, which is a weighted average
      // copy the list to avoid concurrent modification exception... TODO there must be a better way
      // (note the concurrent modification exception is very very rare)
      // We should use Lizzie Board's best moves as they will generally be the most accurate
      final List<MoveData> moves = new ArrayList<MoveData>(Lizzie.board.getData().bestMoves);

      // get the total number of playouts in moves
      int totalPlayouts = moves.stream().mapToInt(move -> move.playouts).sum();
      stats.totalPlayouts = totalPlayouts;

      //stats.maxWinrate = bestMoves.get(0).winrate;
      stats.maxWinrate = BoardData.getWinrateFromBestMoves(moves);
      // BoardData.getWinrateFromBestMoves(moves);
    }

    return stats;
  }
  /*
   * initializes the normalizing factor for winrate_to_handicap_stones conversion.
   */
  public void estimatePassWinrate() {
    // we use A1 instead of pass, because valuenetwork is more accurate for A1 on empty board than a
    // pass.
    // probably the reason for higher accuracy is that networks have randomness which produces
    // occasionally A1 as first move, but never pass.
    // for all practical purposes, A1 should equal pass for the value it provides, hence good
    // replacement.
    // this way we avoid having to run lots of playouts for accurate winrate for pass.
    playMove(Stone.BLACK, "A1");
    togglePonder();
    WinrateStats stats = getWinrateStats();

    // we could use a timelimit or higher minimum playouts to get a more accurate measurement.
    while (stats.totalPlayouts < 1) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new Error(e);
      }
      stats = getWinrateStats();
    }
    mHandicapWinrate = stats.maxWinrate;
    togglePonder();
    undo();
    Lizzie.board.clear();
  }

  public static double mHandicapWinrate = 25;

  /**
   * Convert winrate to handicap stones, by normalizing winrate by first move pass winrate (one
   * stone handicap).
   */
  public static double winrateToHandicap(double pWinrate) {
    // we assume each additional handicap lowers winrate by fixed percentage.
    // this is pretty accurate for human handicap games at least.
    // also this kind of property is a requirement for handicaps to determined based on rank
    // difference.

    // lets convert the 0%-50% range and 100%-50% from both the move and and pass into range of 0-1
    double moveWinrateSymmetric = 1 - Math.abs(1 - (pWinrate / 100) * 2);
    double passWinrateSymmetric = 1 - Math.abs(1 - (mHandicapWinrate / 100) * 2);

    // convert the symmetric move winrate into correctly scaled log scale, so that winrate of
    // passWinrate equals 1 handicap.
    double handicapSymmetric = Math.log(moveWinrateSymmetric) / Math.log(passWinrateSymmetric);

    // make it negative if we had low winrate below 50.
    return Math.signum(pWinrate - 50) * handicapSymmetric;
  }

 // public synchronized void addListener(LeelazListener listener) {
  //  listeners.add(listener);
  //}

  // Beware, due to race conditions, bestMoveNotification can be called once even after item is
  // removed
  // with removeListener
  public synchronized void removeListener(LeelazListener listener) {
    listeners.remove(listener);
  }

 // private synchronized void notifyBestMoveListeners() {
 //   for (LeelazListener listener : listeners) {
 //     listener.bestMoveNotification(bestMoves);
  //  }
 // }

  private static enum ParamState {
    NORMAL,
    QUOTE,
    DOUBLE_QUOTE
  }

  public List<String> splitCommand(String commandLine) {
    if (commandLine == null || commandLine.length() == 0) {
      return new ArrayList<String>();
    }

    final ArrayList<String> commandList = new ArrayList<String>();
    final StringBuilder param = new StringBuilder();
    final StringTokenizer tokens = new StringTokenizer(commandLine, " '\"", true);
    boolean lastTokenQuoted = false;
    ParamState state = ParamState.NORMAL;

    while (tokens.hasMoreTokens()) {
      String nextToken = tokens.nextToken();
      switch (state) {
        case QUOTE:
          if ("'".equals(nextToken)) {
            state = ParamState.NORMAL;
            lastTokenQuoted = true;
          } else {
            param.append(nextToken);
          }
          break;
        case DOUBLE_QUOTE:
          if ("\"".equals(nextToken)) {
            state = ParamState.NORMAL;
            lastTokenQuoted = true;
          } else {
            param.append(nextToken);
          }
          break;
        default:
          if ("'".equals(nextToken)) {
            state = ParamState.QUOTE;
          } else if ("\"".equals(nextToken)) {
            state = ParamState.DOUBLE_QUOTE;
          } else if (" ".equals(nextToken)) {
            if (lastTokenQuoted || param.length() != 0) {
              commandList.add(param.toString());
              param.delete(0, param.length());
            }
          } else {
            param.append(nextToken);
          }
          lastTokenQuoted = false;
          break;
      }
    }
    if (lastTokenQuoted || param.length() != 0) {
      commandList.add(param.toString());
    }
    return commandList;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isLoaded() {
    return isLoaded;
  }

  public String currentWeight() {
    return currentWeight;
  }

  public String currentShortWeight() {
    if (currentWeight != null && currentWeight.length() > 18) {
      return currentWeight.substring(0, 16) + "..";
    }
    return currentWeight;
  }

  public boolean switching() {
    return switching;
  }

  public int currentEngineN() {
    return currentEngineN;
  }

  public String engineCommand() {
    return this.engineCommand;
  }

  public void toggleGtpConsole() {
    gtpConsole = !gtpConsole;
  }
}
