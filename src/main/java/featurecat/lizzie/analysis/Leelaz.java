package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

//import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.Movelist;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An interface with leelaz go engine. Can be adapted for GTP, but is
 * specifically designed for GCP's Leela Zero. leelaz is modified to output
 * information as it ponders see www.github.com/gcp/leela-zero
 */
public class Leelaz {
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
	private static final long MINUTE = 60 * 1000; // number of milliseconds in a minute

	// private long maxAnalyzeTimeMillis; // , maxThinkingTimeMillis;
	private int cmdNumber;
	private int currentCmdNum;
	//private boolean isResponse=false;
	private ArrayDeque<String> cmdQueue;

	public Process process;

	private BufferedInputStream inputStream;
	private BufferedOutputStream outputStream;

	private boolean printCommunication;
	public boolean gtpConsole;
	public boolean genmovenoponder = false;
	// public Board board;
	private List<MoveData> bestMoves;
	private List<MoveData> bestMovesPrevious;
	private List<MoveData> bestMovesTemp;
	private boolean canGetGenmoveInfo = false;

	private List<LeelazListener> listeners;

	private boolean isPondering;
	private long startPonderTime;
	// private long commandTime;
//	private boolean firstNoRespond=true;
//	private boolean firstNoRespond2=true;

	// fixed_handicap
	public boolean isSettingHandicap = false;

	// genmove
	public boolean isThinking = false;
	public boolean isInputCommand = false;

	public boolean preload = false;
	public boolean started = false;
	public boolean isLoaded = false;
	private boolean isCheckingVersion;
	private boolean isCheckingName;
public boolean startAutoAna=false;
	// for Multiple Engine
	public String engineCommand;
	private List<String> commands;
	private JSONObject config;
	private String currentWeightFile = "";
	private String currentWeight = "";
	public boolean switching = false;
	private int currentEngineN = -1;
	private ScheduledExecutorService executor;
	ArrayList<Double> tempcount = new ArrayList<Double>();

	// dynamic komi and opponent komi as reported by dynamic-komi version of leelaz
//	private float dynamicKomi = Float.NaN;
//	private float dynamicOppKomi = Float.NaN;
	
	public int version = -1;
//	public ArrayList<Integer> heatcount = new ArrayList<Integer>();
	public String currentEnginename = "";
	boolean analysed = false;
	private boolean isSaving = false;
	public boolean isResigning = false;
	public boolean isClosing = false;
	public boolean isColorEngine = false;
	public int stage = -1;
	public float komi = (float) 7.5;
	public int blackResignMoveCounts = 0;
	public int whiteResignMoveCounts = 0;
	public boolean resigned = false;
	public boolean isManualB=false;
	public boolean isManualW=false;
	public boolean doublePass = false;
	public boolean outOfMoveNum = false;
	public boolean played = false;
	public boolean isKatago = false;
	public double scoreMean = 0;
	public double scoreStdev = 0;
	private boolean isCommandLine = false;
	public int width = 19;
	public int height = 19;
	public boolean firstLoad = false;
	 Message msg;
	public boolean playNow=false;
	private boolean isZen=false;
	private boolean isInfoLine = false;
	//private boolean isNotifying = false;
	public boolean isSSH = false;
	public boolean isheatmap = false;
	public ArrayList<Integer> heatcount = new ArrayList<Integer>();
	Thread threadThis;
	Thread threadLast;
//private int refreshNumber=0;
	// private boolean isEstimating=true;
	/**
	 * Initializes the leelaz process and starts reading output
	 *
	 * @throws IOException
	 */
	public Leelaz(String engineCommand) throws IOException, JSONException {
		// board = new Board();
		bestMoves = new ArrayList<>();
		bestMovesPrevious = new ArrayList<>();
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
		if (engineCommand.toLowerCase().contains("override-version")) {
			this.isKatago = true;
		}
		if (engineCommand.toLowerCase().contains("zen")) {
			this.isZen = true;
		}
		if (engineCommand.toLowerCase().contains("ssh")) {
			this.isSSH = true;
		}
		// maxAnalyzeTimeMillis = MINUTE * config.getInt("max-analyze-time-minutes");

		// command string for starting the engine
		// if (engineCommand == null || engineCommand.isEmpty()) {
		// engineCommand = config.getString("engine-command");
		// // substitute in the weights file
		// engineCommand = engineCommand.replaceAll("%network-file",
		// config.getString("network-file"));
		// }
		this.engineCommand = engineCommand;

		// Initialize current engine number and start engine
		currentEngineN = 0;
	}

	public void updateCommand(String engineCommand) {
		this.engineCommand = engineCommand;
		if (engineCommand.toLowerCase().contains("override-version")) {
			this.isKatago = true;
		}
		if (engineCommand.toLowerCase().contains("zen")) {
			this.isZen = true;
		}
		if (engineCommand.toLowerCase().contains("ssh")) {
			this.isSSH = true;
		}
	}

	public void getEngineName(int index) {
		Optional<JSONArray> enginesNameOpt = Optional
				.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
		enginesNameOpt.ifPresent(a -> {
			IntStream.range(0, a.length()).forEach(i -> {
				if (i == index)
					currentEnginename = a.getString(i);
			});
		});
		if (currentEnginename.equals(""))
			currentEnginename = currentWeight;
	}

	public void startEngine(int index) throws IOException {
		if (engineCommand.trim().isEmpty()) {
			return;
		}

		commands = splitCommand(engineCommand);

		// Get weight name
		Pattern wPattern = Pattern.compile("(?s).*?(--weights |-w |-model )([^'\" ]+)(?s).*");
		Matcher wMatcher = wPattern.matcher(engineCommand);
		Optional<JSONArray> enginesNameOpt = Optional
				.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
		enginesNameOpt.ifPresent(a -> {
			IntStream.range(0, a.length()).forEach(i -> {
				if (i == index)
					currentEnginename = a.getString(i);
			});
		});
		currentEngineN = index;
		if (wMatcher.matches() && wMatcher.groupCount() == 2) {
			currentWeightFile = wMatcher.group(2);
			String[] names = currentWeightFile.split("[\\\\|/]");
			currentWeight = names.length > 1 ? names[names.length - 1] : currentWeightFile;
			if (currentEnginename.equals(""))
				currentEnginename = currentWeight;
		}

		// Check if engine is present
		// Commented for remote ssh. TODO keep or remove this code?
		// File startfolder = new File(config.optString("engine-start-location", "."));
		// File lef = startfolder.toPath().resolve(new
		// File(commands.get(0)).toPath()).toFile();
		// System.out.println(lef.getPath());
		// if (!lef.exists()) {
		// JOptionPane.showMessageDialog(
		// null,
		// resourceBundle.getString("LizzieFrame.display.leelaz-missing"),
		// "Lizzie - Error!",
		// JOptionPane.ERROR_MESSAGE);
		// throw new IOException("engine not present");
		// }

		// Check if network file is present
		// File wf = startfolder.toPath().resolve(new
		// File(currentWeightFile).toPath()).toFile();
		// if (!wf.exists()) {
		// JOptionPane.showMessageDialog(
		// null, resourceBundle.getString("LizzieFrame.display.network-missing"));
		// throw new IOException("network-file not present");
		// }

		// run leelaz
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		// Commented for remote ssh
		// processBuilder.directory(startfolder);

		processBuilder.redirectErrorStream(true);
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			if (firstLoad) {
				try {
					if(msg==null||!msg.isVisible())
	            	{	
					  msg=new Message();
		             msg.setMessage("加载引擎失败,目前为不加载引擎直接运行 ");
		             msg.setVisible(true);
	            	}
					Lizzie.engineManager = new EngineManager(Lizzie.config, -1);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return;
		}
		initializeStreams();

		// Send a version request to check that we have a supported version
		// Response handled in parseLine
		isCheckingVersion = true;
		isCheckingName = true;
		// sendCommand("turnon");
		sendCommand("name");
		sendCommand("version");
		sendCommand("komi " + komi);
		if(width!=19||height!=19)
		boardSize(width, height);

		// start a thread to continuously read Leelaz output
		// new Thread(this::read).start();
		// can stop engine for switching weights
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.execute(this::read);
		started = true;
		if(index>20)
			Lizzie.frame.menu.changeEngineIcon(20,1);
		else
			Lizzie.frame.menu.changeEngineIcon(index,1);

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
		// currentEngineN = index;
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
		ArrayList<Movelist> mv = Lizzie.board.getmovelist();
		startEngine(index);
		// currentEngineN = index;
		// Lizzie.board.restoreMoveNumber();
		Lizzie.board.setmovelist(mv);
		ponder();
	}

	public void normalQuit() {
		if(currentEngineN>20)
			Lizzie.frame.menu.changeEngineIcon(20,0);
		else
			Lizzie.frame.menu.changeEngineIcon(currentEngineN,0);

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

	public List<MoveData> parseInfo(String line) {
		List<MoveData> bestMoves = new ArrayList<>();
		String[] variations = line.split(" info ");
		int k = (Lizzie.config.limitMaxSuggestion > 0&&!Lizzie.config.showNoSuggCircle ? Lizzie.config.limitMaxSuggestion : 361);
		for (String var : variations) {
			if (!var.trim().isEmpty()) {
				bestMoves.add(MoveData.fromInfo(var));
				k = k - 1;
				if (k < 1)
					break;
			}
		}
		Lizzie.board.getData().tryToSetBestMoves(bestMoves);
		return bestMoves;
	}

	public List<MoveData> parseInfoKatago(String line) {
		List<MoveData> bestMoves = new ArrayList<>();
		String[] variations = line.split(" info ");
		int k = (Lizzie.config.limitMaxSuggestion > 0&&!Lizzie.config.showNoSuggCircle ? Lizzie.config.limitMaxSuggestion : 361);
		for (String var : variations) {
			if (!var.trim().isEmpty()) {
				bestMoves.add(MoveData.fromInfoKatago(var));
				k = k - 1;
				if (k < 1)
					break;
			}
		}
 		Lizzie.board.getData().tryToSetBestMoves(bestMoves);
		return bestMoves;
	}

	public static List<MoveData> parseInfofromfile(String line) {
		List<MoveData> bestMoves = new ArrayList<>();
		String[] variations = line.split(" info ");
		// int k =
		// Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
		for (String var : variations) {
			if (!var.trim().isEmpty()) {
				bestMoves.add(MoveData.fromInfofromfile(var));
				// k = k - 1;
				// if (k < 1) break;
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

	private void parseLineForGenmovePk(String line) {
		//Lizzie.gtpConsole.addLineforce(line);			
		if (line.startsWith("=")) {			
			String[] params = line.trim().split(" ");
			//currentCmdNum = Integer.parseInt(params[0].substring(1).trim());
			if ( Lizzie.frame.toolbar.isEnginePk && params.length == 2) {

				if (Lizzie.board.getHistory().isBlacksTurn()
						&& (this.currentEngineN == Lizzie.frame.toolbar.engineWhite)) {
					return;
				}
				if (!Lizzie.board.getHistory().isBlacksTurn()
						&& (this.currentEngineN == Lizzie.frame.toolbar.engineBlack)) {
					return;
				}
				if (params[1].startsWith("resign")) {
					nameCmdfornoponder();
					genmoveResign();
					return;
				}
				if (Lizzie.frame.toolbar.checkGameMaxMove&&Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.maxGanmeMove) {
					   outOfMoveNum=true;	
						nameCmdfornoponder();
						genmoveResign();
					  return;
				   }
				if (params[1].startsWith("pass")) {
					Optional<int[]> passStep = Optional.empty();
					Optional<int[]> lastMove = Lizzie.board.getLastMove();
					if (lastMove == passStep) {
						Lizzie.board.pass();
						doublePass = true;
						nameCmdfornoponder();
						genmoveResign();

						return;
					}
					Lizzie.board.pass();
					if (this.currentEngineN == Lizzie.frame.toolbar.engineBlack) {

						if(!Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveGenmove("B",
								"pass"))
						{return;}
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).genmoveForPk("W");
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).nameCmdfornoponder();
						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
					}

					else {
						if(!Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveGenmove("W",
								"pass"))
							{return;}
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).genmoveForPk("B");
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).nameCmdfornoponder();
						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
					}

					return;
				} else {
					Optional<int[]> coords = Lizzie.board.asCoordinates(params[1]);
					// if(!coords.isPresent())
					// {return;}
					Lizzie.board.place(coords.get()[0], coords.get()[1]);
					if (this.currentEngineN == Lizzie.frame.toolbar.engineBlack) {

						if(!Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveGenmove("B",
								params[1]))
							{return;}
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).genmoveForPk("W");
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).nameCmdfornoponder();
						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);

					}

					else {
						if(!Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveGenmove("W",
								params[1]))
							{return;}
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).genmoveForPk("B");
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).nameCmdfornoponder();
						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
					}

					return;
				}
			}
		}
		if (canGetGenmoveInfo) {
//		  if(this.currentEngineN==Lizzie.frame.toolbar.engineBlack)
//		  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
//		  if(this.currentEngineN==Lizzie.frame.toolbar.engineWhite)
//			  Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);  
			if (line.contains(" ->   ")) {
				MoveData mv = MoveData.fromSummary(line);
				if (mv != null)
					bestMoves.add(mv);

				Lizzie.board.getData().tryToSetBestMoves(bestMoves);
				if (Lizzie.gtpConsole.isVisible())
					Lizzie.gtpConsole.addLine(line);

				return;
			}
		}
	}

	private void parseLine(String line) {
		synchronized (this) {
			if (!played) {
			if (Lizzie.frame.toolbar.isEnginePk && this.isPondering) {
				Lizzie.engineManager.startInfoTime = System.currentTimeMillis();
			}
			// if (printCommunication || gtpConsole) {
			//Lizzie.gtpConsole.addLineforce(line);
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
			// } else
			// if (line.equals("\n")) {
			// End of response
			// } else
			
			if ((Lizzie.frame.isPlayingAgainstLeelaz && !genmovenoponder)) {
				if (isThinking && !canGetGenmoveInfo && Lizzie.config.playponder) {
					if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()) {
						if (line.contains(" ->   ")) {
							MoveData mv = MoveData.fromSummary(line);
							if (mv != null)
								bestMovesPrevious.add(mv);
							Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().getData()
									.tryToSetBestMoves(bestMovesPrevious);
							if (Lizzie.gtpConsole.isVisible())
								Lizzie.gtpConsole.addLine(line);
							return;

						}
					}
				}
				if (canGetGenmoveInfo || !Lizzie.config.playponder) {
					if (Lizzie.board.getHistory().isBlacksTurn() == Lizzie.frame.playerIsBlack) {
						return;
					}
					if (Lizzie.frame.toolbar.isGenmove)
						Lizzie.leelaz = this;
					if (line.contains(" ->   ")) {
						MoveData mv = MoveData.fromSummary(line);
						if (mv != null)
							bestMoves.add(mv);

						Lizzie.board.getData().tryToSetBestMoves(bestMoves);
						if (Lizzie.gtpConsole.isVisible())
							Lizzie.gtpConsole.addLine(line);

						return;
					}
				}

			}
			if (line.startsWith("info")) {

				if (Lizzie.frame.toolbar.isEnginePk) {
					Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
					Lizzie.frame.boardRenderer.reverseBestmoves = false;
					Lizzie.leelaz = this;
				}
				// Clear switching prompt
				switching = false;

				// Display engine command in the title
				Lizzie.frame.updateTitle();
				if (isResponseUpToDate()) {
					// This should not be stale data when the command number match

					if (isKatago) {
						this.bestMoves = parseInfoKatago(line.substring(5));
						if (Lizzie.config.showKataGoEstimate) {
							if (line.contains("ownership")) {
								tempcount = new ArrayList<Double>();
								String[] params = line.trim().split("ownership");
								String[] params2 = params[1].trim().split(" ");
								for (int i = 0; i < params2.length; i++)
									tempcount.add(Double.parseDouble(params2[i]));
								if (Lizzie.config.showKataGoEstimateBySize) {
									if (Lizzie.config.showSubBoard && Lizzie.config.showKataGoEstimateOnSubbord)
										Lizzie.frame.subBoardRenderer.drawcountblockkata2(tempcount);
									if (Lizzie.config.showKataGoEstimateOnMainbord)
										Lizzie.frame.boardRenderer.drawcountblockkata2(tempcount);
								} else {
									if (Lizzie.config.showSubBoard && Lizzie.config.showKataGoEstimateOnSubbord)
										Lizzie.frame.subBoardRenderer.drawcountblockkata(tempcount);
									if (Lizzie.config.showKataGoEstimateOnMainbord)
										Lizzie.frame.boardRenderer.drawcountblockkata(tempcount);
								}
							}
						}
					} else {
						this.bestMoves = parseInfo(line.substring(5));
					}				
//					if(bestMoves.size()>35)
//					{
//						if(refreshNumber<(bestMoves.size()/35+1))
//						{
//							refreshNumber=refreshNumber+1;
//						}
//						else
//						{
//							refreshNumber=0;
//							Lizzie.frame.refresh();
//						}
//					}
//					else
					Lizzie.frame.refresh();
					// don't follow the maxAnalyzeTime rule if we are in analysis mode
					if ((!Lizzie.frame.toolbar.isEnginePk || !Lizzie.frame.toolbar.isAutoAna)
							&& (System.currentTimeMillis() - startPonderTime) > Lizzie.config.maxAnalyzeTimeMillis
							&& !Lizzie.frame.toolbar.isAutoAna) {
						togglePonder();
					}
				}
				if (!this.bestMoves.isEmpty()) {	
					isInfoLine=true;
				}
				// 临时添加为了解决SSH时的卡顿
//				else { if(firstNoRespond)
//				{
//					commandTime= System.currentTimeMillis();
//					firstNoRespond=false;
//				}
//				if(System.currentTimeMillis()-commandTime>200)
//				{
//					setResponseUpToDate();
//					commandTime=System.currentTimeMillis();
//					firstNoRespond=true;
//				}
				// 临时添加为了解决SSH时的卡顿
				// }
			}
			else if (Lizzie.gtpConsole.isVisible())
			Lizzie.gtpConsole.addLine(line);
			// System.out.println(line);
			if (line.startsWith("| ST")) {
				String[] params = line.trim().split(" ");
				if (params.length == 13) {
					isColorEngine = true;
					if (Lizzie.gtpConsole.isVisible())
						Lizzie.gtpConsole.addLineforce(currentEnginename + ": " + line);
					stage = Integer.parseInt(params[3].substring(0, params[3].length() - 1));
					komi = Float.parseFloat(params[6].substring(0, params[6].length() - 1));	
			        
				}
			} else if (line.startsWith("play")) {
				// In lz-genmove_analyze
				if (Lizzie.frame.isPlayingAgainstLeelaz) {
					Lizzie.board.place(line.substring(5).trim());
				}
				isThinking = false;

			} else if (line.startsWith("=")) {			
//				if (!isLoaded)
					isLoaded = true;
//					try {
//						trySendCommandFromQueue();
//				} catch (Exception ex) {
//				}	
				if (isThinking) {
					canGetGenmoveInfo = true;
				}
				String[] params = line.trim().split(" ");

				//if (line.startsWith("?") || params.length == 1)
				//	return;

				if (isSettingHandicap) {
					bestMoves = new ArrayList<>();
					for (int i = 1; i < params.length; i++) {
						Lizzie.board.asCoordinates(params[i])
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
							else
								Lizzie.frame.zen.sendCommand("play " + "b " + params[1]);

							Lizzie.frame.zen.countStones();
						}
					}
					if (Lizzie.frame.isPlayingAgainstLeelaz) {
						if (params[1].startsWith("resign")) {
							if (Lizzie.frame.playerIsBlack) {

								if(msg==null||!msg.isVisible())
				            	{	
								  msg=new Message();
					             msg.setMessage( "黑胜,LeelaZero 认输!");
					             msg.setVisible(true);
				            	}
								GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
								gameInfo.setResult("黑胜");
								Lizzie.frame.setResult("黑胜");

							} else {
								if(msg==null||!msg.isVisible())
				            	{	
								  msg=new Message();
					             msg.setMessage( "白胜,LeelaZero 认输!");
					             msg.setVisible(true);
				            	}
								GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
								gameInfo.setResult("白胜");
								Lizzie.frame.setResult("白胜");

							}
							togglePonder();
						}

						if (params[1].startsWith("pass")) {
							Lizzie.board.pass();
							Lizzie.frame.menu.toggleEngineMenuStatus(false,false);
						} else {
							Lizzie.board.place(params[1]);
							Lizzie.frame.menu.toggleEngineMenuStatus(false,false);
						}
						if (Lizzie.frame.isAutocounting) {
							if (Lizzie.board.getHistory().isBlacksTurn())
								Lizzie.frame.zen.sendCommand("play " + "w " + params[1]);
							else
								Lizzie.frame.zen.sendCommand("play " + "b " + params[1]);

							Lizzie.frame.zen.countStones();
						}
						if (!Lizzie.config.playponder)
							Lizzie.leelaz.nameCmdfornoponder();
					}
					if (!isInputCommand) {
						isPondering = false;
					}
					isThinking = false;
					if (isInputCommand) {
						isInputCommand = false;
					}
				} else {
					if (isCheckingName) {
						isCheckingName = false;
						if (params[1].toLowerCase().startsWith("zen"))
							this.isZen=true;
						if (params[1].startsWith("KataGo") || isKatago) {
							this.isKatago = true;
							this.version = 17;							
							isCheckingVersion = false;

							if (this.currentEngineN == EngineManager.currentEngineNo) {
								Lizzie.config.leelaversion = version;
							}
							isLoaded = true;
							
							Lizzie.initializeAfterVersionCheck();
							if(currentEngineN>20)
								Lizzie.frame.menu.changeEngineIcon(20,2);
							else
								Lizzie.frame.menu.changeEngineIcon(currentEngineN,2);
							if(Lizzie.engineManager.currentEngineNo>20)
								Lizzie.frame.menu.changeEngineIcon(20,3);
							else
								Lizzie.frame.menu.changeEngineIcon(Lizzie.engineManager.currentEngineNo,3);
						}
					} else if (isCheckingVersion && !isKatago) {
						String[] ver = params[1].split("\\.");
						try {
						int minor = Integer.parseInt(ver[1]);
						// Gtp support added in version 15
						version = minor;}
						catch(Exception ex)
						{
							version=16;
						}
						if (this.currentEngineN == EngineManager.currentEngineNo) {
							Lizzie.config.leelaversion = version;
						}
//						if (minor < 15) {
//							if(msg==null||!msg.isVisible())
//			            	{	
//							  msg=new Message();
//				             msg.setMessage("Lizzie需要使用0.15或更新版本的leela zero引擎,当前引擎版本是: " + params[1]);
//				             msg.setVisible(true);
//			            	}
//				
//						}
						isCheckingVersion = false;						
						isLoaded = true;
						
						Lizzie.initializeAfterVersionCheck();
						if(currentEngineN>20)
							Lizzie.frame.menu.changeEngineIcon(20,2);
						else
							Lizzie.frame.menu.changeEngineIcon(currentEngineN,2);
						if(Lizzie.engineManager.currentEngineNo>20)
							Lizzie.frame.menu.changeEngineIcon(20,3);
						else
							Lizzie.frame.menu.changeEngineIcon(Lizzie.engineManager.currentEngineNo,3);

					}
				}

			}
			if (isheatmap) {
				if (line.startsWith(" ") || Character.isDigit(line.charAt(0))) {
					try {
						String[] params = line.trim().split("\\s+");
						if (params.length == Lizzie.board.boardWidth) {
							for (int i = 0; i < params.length; i++)
								heatcount.add(Integer.parseInt(params[i]));
						}
					} catch (Exception ex) {
					}
				}
				if (line.contains("winrate:")) {
					isheatmap = false;
				//	String[] params = line.trim().split(" ");
					 //heatwinrate = Double.valueOf(params[1]);
					Lizzie.frame.refresh();
				}
			}
		}else  if (line.startsWith("=") || line.startsWith("?")) {
			currentCmdNum = currentCmdNum+ 1;			
	if(currentCmdNum>cmdNumber-1)
		currentCmdNum=cmdNumber-1;}
		}
		
			
	}

	private void notifyAutoPlay() {
		if (Lizzie.frame.toolbar.isAutoPlay) {
			if ((Lizzie.board.getHistory().isBlacksTurn() && Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())
					|| (!Lizzie.board.getHistory().isBlacksTurn()
							&& Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected())) {
				int time = 0;
				int playouts = 0;
				int firstPlayouts = 0;
				if (Lizzie.frame.toolbar.chkAutoPlayTime.isSelected()) {
					try {
						time = 1000 * Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayTime.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
				if (Lizzie.frame.toolbar.chkAutoPlayPlayouts.isSelected()) {
					try {
						playouts = Integer
								.parseInt(Lizzie.frame.toolbar.txtAutoPlayPlayouts.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
				if (Lizzie.frame.toolbar.chkAutoPlayFirstPlayouts.isSelected()) {
					try {
						firstPlayouts = Integer
								.parseInt(Lizzie.frame.toolbar.txtAutoPlayFirstPlayouts.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}

				if (firstPlayouts > 0) {
					if (bestMoves.get(0).playouts >= firstPlayouts) {
						int coords[] = Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
						if ((Lizzie.board.getData().blackToPlay && Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())
								|| (!Lizzie.board.getData().blackToPlay
										&& Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected())) {
							Lizzie.board.place(coords[0], coords[1]);

						}
						if (!Lizzie.config.playponder) {
							if (!(Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()
									&& Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())) {
								nameCmd();
							}
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
						int coords[] = Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);

						if ((Lizzie.board.getData().blackToPlay && Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())
								|| (!Lizzie.board.getData().blackToPlay
										&& Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected())) {
							Lizzie.board.place(coords[0], coords[1]);

						}
						if (!Lizzie.config.playponder) {
							if (!(Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()
									&& Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())) {
								nameCmd();
							}
						}

					}
				}

				if (time > 0) {
					if (System.currentTimeMillis() - startPonderTime > time) {
						int coords[] = Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
						Lizzie.board.place(coords[0], coords[1]);
						if ((Lizzie.board.getData().blackToPlay && Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())
								|| (!Lizzie.board.getData().blackToPlay
										&& Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected())) {
							Lizzie.board.place(coords[0], coords[1]);

						}
						if (!Lizzie.config.playponder) {
							if (!(Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()
									&& Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected())) {
								nameCmd();
							}
						}
					}
				}

			}
		}
	}

	private void saveAndLoad() {
		if (!Lizzie.frame.isBatchAna) {
			if(!analysed) {
				return;
			}
			File file = new File("");
			String courseFile = "";
			try {
				courseFile = file.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String df = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			File autoSaveFile = new File(courseFile + "\\" + "AutoSave" + "\\" + df + ".sgf");

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
			isSaving = false;

			
				if(msg==null||!msg.isVisible())
            	{	
				  msg=new Message();
            msg.setMessage("自动分析已完毕,棋谱保存在"+courseFile+ "\\" + "AutoSave");
            msg.setVisible(true);
            	}
			return;
		} else {
			String name = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getName();
			String path = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getParent();
			String df = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());			
			String prefix=name.substring(name.lastIndexOf("."));
					      int num=prefix.length();		      
		      String fileOtherName=name.substring(0, name.length()-num);		      
			String filename = path + "\\" + fileOtherName + "_已分析_"+df+".sgf";
			File autoSaveFile = new File(filename);
			try {
				SGFParser.save(Lizzie.board, autoSaveFile.getPath());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Lizzie.frame.Batchfiles.size() > (Lizzie.frame.BatchAnaNum + 1)) {
				Runnable runnable =
				        new Runnable() {
				          public void run() {
								loadAutoBatchFile();		
								if(Lizzie.board.getHistory().getEnd().getData().moveNumber==0)
									{if (Lizzie.frame.Batchfiles.size() > (Lizzie.frame.BatchAnaNum + 1))
											loadAutoBatchFile();		}	
								Lizzie.leelaz.isClosing=false;
									Lizzie.leelaz.ponder();
				          }
				        };				        
				
				    Thread thread = new Thread(runnable);
				    thread.start();
//				Timer timer = new Timer();
//				timer.schedule(new TimerTask() {
//					public void run() {
//						loadAutoBatchFile();		
//						if(Lizzie.board.getHistory().getEnd().getData().moveNumber==0)
//							{if (Lizzie.frame.Batchfiles.size() > (Lizzie.frame.BatchAnaNum + 1))
//									loadAutoBatchFile();		}	
//						Lizzie.leelaz.isClosing=false;
//							Lizzie.leelaz.ponder();
//						this.cancel();
//					}
//				}, 300);
			} else {
				Lizzie.frame.isBatchAna = false;
				Lizzie.frame.toolbar.chkAnaAutoSave.setEnabled(true);
				isSaving = false;
				if(msg==null||!msg.isVisible())
            	{	
				  msg=new Message();
	             msg.setMessage( "批量棋谱已全部分析完毕,棋谱保存在"+path);
	             msg.setVisible(true);
            	}
				Lizzie.frame.Batchfiles = new ArrayList<File>();
				Lizzie.frame.BatchAnaNum=0;
				Lizzie.frame.addInput();
				if(Lizzie.frame.analysisTable!=null&&Lizzie.frame.analysisTable.frame.isVisible())
				{Lizzie.frame.analysisTable.refreshTable();
				}			
				return;
			}
		}

	}

	private void savePkFile() {
		File file = new File("");
		String courseFile = "";
		try {
			courseFile = file.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

		String df = "";
		if (Lizzie.frame.toolbar.isEnginePkBatch) {
			df = Lizzie.frame.toolbar.EnginePkBatchNumberNow + "_";
		}
		df = df + "黑" + Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename + "_白"
				+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename;
		if (Lizzie.frame.toolbar.isGenmove) {
			if (this.currentEngineN == Lizzie.frame.toolbar.engineBlack) {
				df = df + "_白("
						+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename
						+ ")胜";
			} else {
				df = df + "_黑("
						+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename
						+ ")胜";
			}
		} else {
			if (blackResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts) {
				df = df + "_白("
						+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename
						+ ")胜";
			} else {
				df = df + "_黑("
						+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename
						+ ")胜";
			}
		}
		df = df + "_" + sf;
		// 增加如果已命名,则保存在命名的文件夹下
		
		File autoSaveFile;
		File autoSaveFile2 = null;
		if (Lizzie.frame.toolbar.isEnginePkBatch) {
			autoSaveFile = new File(
					courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.batchPkName + "\\" + df + ".sgf");
			autoSaveFile2 = new File(
					courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.SF + "\\" + df + ".sgf");
		} else {
			autoSaveFile = new File(courseFile + "\\" + "PkAutoSave" + "\\" + df + ".sgf");
			autoSaveFile2 = new File(courseFile + "\\" + "PkAutoSave" + "\\" + df + ".sgf");
		}

		File fileParent = autoSaveFile.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		try {
			SGFParser.save(Lizzie.board, autoSaveFile.getPath());
			if(Lizzie.frame.toolbar.enginePkSaveWinrate)
			{
				String autoSavePng;
				if (Lizzie.frame.toolbar.isEnginePkBatch) {
					autoSavePng = 
							courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.batchPkName + "\\" + df + ".png";
					
				} else {
					autoSavePng = courseFile + "\\" + "PkAutoSave" + "\\" + df + ".png";
				}				
				Lizzie.frame.saveImage(Lizzie.frame.statx,Lizzie.frame.staty,(int) (Lizzie.frame.grw * 1.03),Lizzie.frame.grh +Lizzie.frame.stath, autoSavePng);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (Lizzie.frame.toolbar.isEnginePkBatch) {
				try {
					File fileParent2 = autoSaveFile2.getParentFile();
					if (!fileParent2.exists()) {
						fileParent2.mkdirs();
					}
					SGFParser.save(Lizzie.board, autoSaveFile2.getPath());
					
					if(Lizzie.frame.toolbar.enginePkSaveWinrate)
					{
						
						String autoSavePng2 = null;
						if (Lizzie.frame.toolbar.isEnginePkBatch) {
							autoSavePng2 = 
									courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.SF + "\\" + df + ".png";
						} else {
							autoSavePng2 = courseFile + "\\" + "PkAutoSave" + "\\" + df + ".png";
						}
						Lizzie.frame.saveImage(Lizzie.frame.statx,Lizzie.frame.staty,(int) (Lizzie.frame.grw * 1.03),Lizzie.frame.grh +Lizzie.frame.stath, autoSavePng2);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	private void savePkTxt(String settingB,String settingW,String settingAll,String resultB,String resultW,String resultOther) {
		File file = new File("");
		String courseFile = "";
		try {
			courseFile = file.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 增加如果已命名,则保存在命名的文件夹下		
		File autoSaveFile;
		File autoSaveFile2 = null;	
			autoSaveFile = new File(
					courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.batchPkName + "\\" + "结果"+ Lizzie.frame.toolbar.SF + ".txt");
			autoSaveFile2 = new File(
					courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.SF + "\\"  + "结果"+ Lizzie.frame.toolbar.SF + ".txt");		

		File fileParent = autoSaveFile.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		try {	
			 PrintWriter pfp= new PrintWriter(autoSaveFile);
			 pfp.print(settingAll);
			 pfp.println();
			 pfp.print(settingB);
			 pfp.println();
			 pfp.print(settingW);
			 pfp.println();
			 pfp.print(resultB);
			 pfp.println();
			 pfp.print(resultW);
			 pfp.println();
			 pfp.print(resultOther);
			 pfp.println();
			 pfp.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
				try {
					File fileParent2 = autoSaveFile2.getParentFile();
					if (!fileParent2.exists()) {
						fileParent2.mkdirs();
					}
					 PrintWriter pfp= new PrintWriter(autoSaveFile);
					 pfp.print(settingAll);
					 pfp.println();
					 pfp.print(settingB);
					 pfp.println();
					 pfp.print(settingW);
					 pfp.println();
					 pfp.print(resultB);
					 pfp.println();
					 pfp.print(resultW);
					 pfp.println();
					 pfp.print(resultOther);
					 pfp.println();
					 pfp.close();		
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					}			
			e.printStackTrace();
		}
	}

	private void loadAutoBatchFile() {
		// sendCommand("clear_board");
		Lizzie.frame.BatchAnaNum = Lizzie.frame.BatchAnaNum + 1;
		try {
		if(Lizzie.frame.analysisTable!=null&&Lizzie.frame.analysisTable.frame.isVisible())
			{Lizzie.frame.analysisTable.refreshTable();
			}
		}
		catch (Exception ex)
		{}
		LizzieFrame.loadFile(Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum));
		try {
			Lizzie.frame.toolbar.firstMove = Integer.parseInt(Lizzie.frame.toolbar.txtFirstAnaMove.getText());
		} catch (Exception ex) {
		}
		try {
			Lizzie.frame.toolbar.lastMove = Integer.parseInt(Lizzie.frame.toolbar.txtLastAnaMove.getText());
		} catch (Exception ex) {
		}
	    //Lizzie.board.clearBoardStat();
	    Lizzie.frame.toolbar.startAutoAna();
		//Lizzie.frame.toolbar.isAutoAna = true;
		startAutoAna=true;
		//Lizzie.frame.toolbar.startAutoAna = true;
		//Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(true);		
		isSaving = false;
	}

	public void notifyAutoAna()   {
		if (!bestMoves.isEmpty()&&!isClosing) {
			if (Lizzie.frame.toolbar.startAutoAna) {
				if ((Lizzie.frame.toolbar.firstMove == -1||Lizzie.frame.toolbar.firstMove>=Lizzie.board.getHistory().getMainEnd().getData().moveNumber)&&!Lizzie.board.getHistory().getNext().isPresent()) {
					Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(false);
					//togglePonder();
					Lizzie.frame.toolbar.isAutoAna = false;
					Lizzie.frame.addInput();
					if (Lizzie.frame.isBatchAna) {	
						closeAutoAna();				
					}
					return;
				} else {
					analysed = false;				
				}				
				if(Lizzie.frame.toolbar.firstMove != -1) {
				if (startAutoAna) {	
					Runnable runnable =
					        new Runnable() {
					          public void run() {
					        	  Lizzie.board.goToMoveNumberBeyondBranch(Lizzie.frame.toolbar.firstMove - 1);	
					            	setResponseUpToDate();	
					          }
					        };
					    Thread thread = new Thread(runnable);
					    thread.start();		
					startAutoAna=false;
				}
				if(Lizzie.board.getHistory().getMoveNumber()!=Lizzie.frame.toolbar.firstMove-1)
				{
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}				
				}
				else {setResponseUpToDate();}
				Lizzie.frame.toolbar.startAutoAna = false;		
			}		
		
			if (Lizzie.board.getHistory().getNext().isPresent()) {
				if (Lizzie.frame.toolbar.lastMove != -1) {
					if (Lizzie.frame.toolbar.lastMove < Lizzie.board.getHistory().getData().moveNumber) {
						closeAutoAna();
						return;
					}
				}
				int time = 0;
				int playouts = 0;
				int firstPlayouts = 0;
				if (Lizzie.frame.toolbar.chkAnaTime.isSelected()) {
					try {
						time = 1000 * Integer.parseInt(Lizzie.frame.toolbar.txtAnaTime.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
				if (Lizzie.frame.toolbar.chkAnaPlayouts.isSelected()) {
					try {
						playouts = Integer.parseInt(Lizzie.frame.toolbar.txtAnaPlayouts.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
				if (Lizzie.frame.toolbar.chkAnaFirstPlayouts.isSelected()) {
					try {
						firstPlayouts = Integer
								.parseInt(Lizzie.frame.toolbar.txtAnaFirstPlayouts.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
				if(Lizzie.board.getHistory().isBlacksTurn()&&!Lizzie.frame.toolbar.chkAnaBlack.isSelected())
				{
					bestMoves = new ArrayList<>();
					Lizzie.board.nextMove();
					analysed = true;
					return;
				}
				if(!Lizzie.board.getHistory().isBlacksTurn()&&!Lizzie.frame.toolbar.chkAnaWhite.isSelected())
				{
					bestMoves = new ArrayList<>();
					Lizzie.board.nextMove();
					analysed = true;
					return;
				}
				
				if (firstPlayouts > 0) {
					if (bestMoves.get(0).playouts >= firstPlayouts) {
						bestMoves = new ArrayList<>();
						Lizzie.board.nextMove();
						analysed = true;
						return;
					}
				}
				if (playouts > 0) {
					int sum = 0;
					for (MoveData move : bestMoves) {
						sum += move.playouts;
					}
					if (sum >= playouts) {
						bestMoves = new ArrayList<>();
						Lizzie.board.nextMove();
						analysed = true;
						return;
					}
				}

				if (time > 0) {
					if (System.currentTimeMillis() - startPonderTime > time) {
						bestMoves = new ArrayList<>();
						Lizzie.board.nextMove();
						analysed = true;
						return;
					}
				}

			} else {
				closeAutoAna();
			}
		}
	}
	
	

	public void genmoveResign() {		
		Lizzie.gtpConsole.addLine(currentEnginename + " 认输");
		Lizzie.board.updateComment();
		
		String settingB="黑方设置:";
		String settingW="白方设置:";
		String settingAll="其他设置:";
		if(Lizzie.frame.toolbar.isEnginePkBatch)
	    {				
			settingB=settingB + "引擎命令:"+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).engineCommand;
			settingW=settingW + "引擎命令:"+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).engineCommand;
			if(Lizzie.frame.toolbar.chkenginePkTime.isSelected())
		{
			settingB=settingB + " 时间:"+Lizzie.frame.toolbar.txtenginePkTime.getText()+"S";
			settingW=settingW + " 时间:"+Lizzie.frame.toolbar.txtenginePkTimeWhite.getText()+"S";
			}		
		
			
			
		if(Lizzie.frame.toolbar.chkenginePkBatch.isSelected())
		{
			settingAll=settingAll + " 多盘:"+Lizzie.frame.toolbar.txtenginePkBatch.getText();
			}
		if(Lizzie.frame.toolbar.chkenginePkContinue.isSelected())
		{
			settingAll=settingAll + " 续弈: 是";
			}
		else {
			settingAll=settingAll + " 续弈: 否";
		}
		if (Lizzie.frame.toolbar.exChange) {
			settingAll=settingAll + " 交换黑白: 是";
		    }
		else {
			settingAll=settingAll + " 交换黑白: 否";
		}
		
		 if (Lizzie.frame.toolbar.checkGameMaxMove) {
			 settingAll=settingAll + " 最大手数: "+Lizzie.frame.toolbar.maxGanmeMove;
		    }		    
	    }    
		
		if(outOfMoveNum)
		{
			saveTimeoutFile();
		}
		else {
			if(!doublePass&&!outOfMoveNum) {
		if (this.currentEngineN == Lizzie.frame.toolbar.engineBlack) {
			// 白胜
			
			if(Lizzie.frame.toolbar.exChange) {
				if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
				{	Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
				if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkBlackWinAsWhite = Lizzie.frame.toolbar.pkBlackWinAsWhite+1;
				if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkWhiteWinAsWhite = Lizzie.frame.toolbar.pkWhiteWinAsWhite+1;
				}
				else
				{	Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
				if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsWhite = Lizzie.frame.toolbar.pkBlackWinAsWhite+1;
				if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsWhite = Lizzie.frame.toolbar.pkWhiteWinAsWhite+1;
				}
					}
					else {
						Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
						if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsWhite = Lizzie.frame.toolbar.pkBlackWinAsWhite+1;
						if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsWhite = Lizzie.frame.toolbar.pkWhiteWinAsWhite+1;
					}
			
			
			
			GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
			gameInfo.setResult("白("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename + ")胜");
			Lizzie.frame.setResult("白("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename + ")胜");
		} else {
			// 黑胜
			if(Lizzie.frame.toolbar.exChange) {
				if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
					{Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
					if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkBlackWinAsBlack = Lizzie.frame.toolbar.pkBlackWinAsBlack+1;
					if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkWhiteWinAsBlack = Lizzie.frame.toolbar.pkWhiteWinAsBlack+1;
					}
				else
					{Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
					if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsBlack = Lizzie.frame.toolbar.pkBlackWinAsBlack+1;
					if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsBlack = Lizzie.frame.toolbar.pkWhiteWinAsBlack+1;
					}
				}
				else {
					Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
					if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsBlack = Lizzie.frame.toolbar.pkBlackWinAsBlack+1;
					if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsBlack = Lizzie.frame.toolbar.pkWhiteWinAsBlack+1;
				}
			
			GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
			gameInfo.setResult("黑("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename + ")胜");
			Lizzie.frame.setResult("黑("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename + ")胜");

//			if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
//				Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
//			else
//				Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
		}}
		if (Lizzie.frame.toolbar.AutosavePk || Lizzie.frame.toolbar.isEnginePkBatch) {
			if (doublePass) {
				savePassFile();				
			} else {
				savePkFile();
			}
		}
		}
		
		if(Lizzie.frame.toolbar.isEnginePkBatch)
		{
			String resultB="";
			String resultW="";
			String resultOther="";
			if(Lizzie.frame.toolbar.exChange) {
				if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
				{ 
				resultB=resultB+"黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkBlackWins + "局";
				resultB=resultB+" 执黑胜"+Lizzie.frame.toolbar.pkWhiteWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkWhiteWinAsWhite+"局";
				resultW="白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkWhiteWins + "局";
				resultW=resultW+" 执黑胜"+Lizzie.frame.toolbar.pkBlackWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkBlackWinAsWhite+"局";
				}
				else
				{	 resultB="黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkBlackWins + "局";
				resultB=resultB+" 执黑胜"+Lizzie.frame.toolbar.pkWhiteWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkWhiteWinAsWhite+"局";
				resultW=resultW+"白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkWhiteWins + "局";			
				resultW=resultW+" 执黑胜"+Lizzie.frame.toolbar.pkBlackWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkBlackWinAsWhite+"局";
				}
					}
					else {
						resultB="黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkBlackWins + "局";
						resultB=resultB+" 执黑胜"+Lizzie.frame.toolbar.pkWhiteWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkWhiteWinAsWhite+"局";		
						resultW=resultW+"白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkWhiteWins + "局";
						resultW=resultW+" 执黑胜"+Lizzie.frame.toolbar.pkBlackWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkBlackWinAsWhite+"局";
					}
			
			
			resultOther=resultOther+"双pass"+Lizzie.frame.toolbar.doublePassGame+"局";
			resultOther=resultOther+" 超手数"+Lizzie.frame.toolbar.maxMoveGame+"局";
			savePkTxt(settingB,settingW,settingAll,resultB,resultW,resultOther);
			}
		
		if (Lizzie.frame.toolbar.isEnginePkBatch) {
			int EnginePkBatchNumber = 1;
			try {
				EnginePkBatchNumber = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkBatch.getText());
			} catch (NumberFormatException err) {
			}
			if (Lizzie.frame.toolbar.EnginePkBatchNumberNow < EnginePkBatchNumber) {
				Lizzie.frame.toolbar.EnginePkBatchNumberNow = Lizzie.frame.toolbar.EnginePkBatchNumberNow + 1;
				// 下一盘PK
//				if (Lizzie.frame.toolbar.checkGameTime) {
//					Lizzie.engineManager.gameTime = System.currentTimeMillis();
//				}
				
				Lizzie.frame.setResult("");
				if (Lizzie.frame.toolbar.exChange)
				// if(false)
				{
					int temp = Lizzie.frame.toolbar.engineBlack;
					Lizzie.frame.toolbar.engineBlack = Lizzie.frame.toolbar.engineWhite;
					Lizzie.frame.toolbar.engineWhite = temp;
					if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0) {
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
						int tempindex = Lizzie.frame.toolbar.enginePkBlack.getSelectedIndex();
						Lizzie.frame.toolbar.enginePkBlack
								.setSelectedIndex(Lizzie.frame.toolbar.enginePkWhite.getSelectedIndex());
						Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(tempindex);
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
						Lizzie.frame.toolbar.lblenginePkResult
								.setText(Lizzie.frame.toolbar.pkWhiteWins + ":" + Lizzie.frame.toolbar.pkBlackWins);
						String temp1 = Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkFirstPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
						temp1 = Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);						
						temp1 = Lizzie.frame.toolbar.txtenginePkTime.getText();
						Lizzie.frame.toolbar.txtenginePkTime
								.setText(Lizzie.frame.toolbar.txtenginePkTimeWhite.getText());
						Lizzie.frame.toolbar.txtenginePkTimeWhite.setText(temp1);
					} else {
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
						int tempindex = Lizzie.frame.toolbar.enginePkBlack.getSelectedIndex();
						Lizzie.frame.toolbar.enginePkBlack
								.setSelectedIndex(Lizzie.frame.toolbar.enginePkWhite.getSelectedIndex());
						Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(tempindex);
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
						Lizzie.frame.toolbar.lblenginePkResult
								.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);
						String temp1 = Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkFirstPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
						temp1 = Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);
					}
				} else {
					Lizzie.frame.toolbar.lblenginePkResult
							.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);
				}

				Lizzie.board.clearforpk();
				if (Lizzie.frame.toolbar.chkenginePkContinue.isSelected()) {
					Lizzie.board.setlist(Lizzie.frame.toolbar.startGame);
				}
				if (Lizzie.board.getHistory().isBlacksTurn()) {
					Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineWhite);
					Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineBlack);					
					Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).genmoveForPk("B");

				} else {
					Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineBlack);
					Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineWhite);					
					Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).genmoveForPk("W");
				}
				Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).notPondering();
				Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).notPondering();

				Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());

				Lizzie.frame.setPlayers(
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename,
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename);
				GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();

				gameInfo.setPlayerWhite(
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename);
				gameInfo.setPlayerBlack(
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename);

			} else {
				clear();
				// 结束PK
				if (Lizzie.frame.toolbar.exChange && Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0) {
					Lizzie.frame.toolbar.lblenginePkResult
							.setText(Lizzie.frame.toolbar.pkWhiteWins + ":" + Lizzie.frame.toolbar.pkBlackWins);
				} else {
					Lizzie.frame.toolbar.lblenginePkResult
							.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);
				}
				Lizzie.frame.addInput();
				Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
				Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
				Lizzie.frame.toolbar.txtenginePkBatch.setEnabled(true);
				Lizzie.frame.toolbar.btnEnginePkConfig.setEnabled(true);
				Lizzie.frame.toolbar.chkenginePkTime.setEnabled(true);
				Lizzie.frame.toolbar.txtenginePkTime.setEnabled(true);
				Lizzie.frame.toolbar.txtenginePkTimeWhite.setEnabled(true);
				Lizzie.frame.toolbar.isEnginePk = false;
				Lizzie.frame.toolbar.btnStartPk.setText("开始");
				Lizzie.frame.toolbar.chkenginePkBatch.setEnabled(true);
				// chkenginePkgenmove.setEnabled(true);
				Lizzie.frame.toolbar.chkenginePk.setEnabled(true);
				Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).played = false;
				Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).played = false;
				// Lizzie.engineManager.switchEngineForEndPk(Lizzie.engineManager.currentEngineNo);
				Lizzie.frame.toolbar.batchPkName = "";
				Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
	              Lizzie.frame.boardRenderer.reverseBestmoves = false;
				Lizzie.frame.toolbar.analyse.setEnabled(true);
				Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
				Lizzie.engineManager.changeEngIcoForEndPk();
				
				if(msg==null||!msg.isVisible())
            	{	
					File file = new File("");
					String courseFile = "";
					try {
						courseFile = file.getCanonicalPath();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					  msg=new Message();
					  String passandMove="";
					  if(Lizzie.frame.toolbar.doublePassGame>0)
						  passandMove=passandMove+"双方Pass局数 "+Lizzie.frame.toolbar.doublePassGame;
					  if(Lizzie.frame.toolbar.maxMoveGame>0)
						  passandMove=passandMove+"超最大手数局数  "+Lizzie.frame.toolbar.maxMoveGame;
		              msg.setMessage( "批量对战已结束,比分为" + Lizzie.frame.toolbar.pkBlackWins + ":"
								+ Lizzie.frame.toolbar.pkWhiteWins +" "+passandMove+ "棋谱保存在"+courseFile+"\\PkAutoSave");
		              msg.setVisible(true);
            	}

			}
		} else {
			clear();
			// 结束PKLizzie.engineManager.engineList.get(engineBlack).notPondering();
			Lizzie.frame.toolbar.lblenginePkResult
					.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);

			Lizzie.frame.addInput();
			Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
			Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
			Lizzie.frame.toolbar.txtenginePkBatch.setEnabled(true);
			Lizzie.frame.toolbar.btnEnginePkConfig.setEnabled(true);
			Lizzie.frame.toolbar.chkenginePkTime.setEnabled(true);
			Lizzie.frame.toolbar.txtenginePkTime.setEnabled(true);
			Lizzie.frame.toolbar.txtenginePkTimeWhite.setEnabled(true);
			Lizzie.frame.toolbar.isEnginePk = false;
			Lizzie.frame.toolbar.btnStartPk.setText("开始");
			Lizzie.frame.toolbar.analyse.setEnabled(true);
			Lizzie.frame.toolbar.chkenginePkBatch.setEnabled(true);
			// chkenginePkgenmove.setEnabled(true);
			Lizzie.frame.toolbar.chkenginePk.setEnabled(true);
			Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).played = false;
			Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).played = false;
			// Lizzie.engineManager.switchEngineForEndPk(Lizzie.engineManager.currentEngineNo);
			Lizzie.frame.toolbar.batchPkName = "";

			String jg = "对战已结束，";
			if(Lizzie.frame.toolbar.checkGameMaxMove&&Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.maxGanmeMove)
				jg=jg+"超过手数限制";
			else 
			if(doublePass)
			{
				jg=jg+"双方Pass无法判断胜负";
			}else {
			if (currentEngineN == Lizzie.frame.toolbar.engineBlack) {
				// df=df+"_白胜";				
				jg = jg + "白胜";
			} else {
				jg = jg + "黑胜";
			}
			}			
			if (Lizzie.frame.toolbar.AutosavePk) {
				File file = new File("");
				String courseFile = "";
				try {
					courseFile = file.getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jg = jg + "，棋谱保存在"+courseFile+"\\PkAutoSave";
			}
			Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
            Lizzie.frame.boardRenderer.reverseBestmoves = false;
			Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
			Lizzie.engineManager.changeEngIcoForEndPk();
			if(msg==null||!msg.isVisible())
        	{	
			  msg=new Message();
              msg.setMessage(jg);
              msg.setVisible(true);
        	}
			
		}

	}

	public void pkResign() {
		if (!resigned || isResigning)
			return;
		if(isManualB) {
			isManualB=false;
			blackResignMoveCounts=Lizzie.frame.toolbar.pkResignMoveCounts+1;
		}
		if(isManualW) {
			isManualW=false;
			whiteResignMoveCounts=Lizzie.frame.toolbar.pkResignMoveCounts+1;
		}
		isResigning = true;
		resigned = false;
		Lizzie.gtpConsole.addLine(currentEnginename + " 认输");
		String settingB="黑方设置:";
		String settingW="白方设置:";
		String settingAll="其他设置:";
		if(Lizzie.frame.toolbar.isEnginePkBatch)
	    {	if(Lizzie.frame.toolbar.chkenginePkTime.isSelected())
		{
			settingB=settingB + " 时间:"+Lizzie.frame.toolbar.txtenginePkTime.getText()+"S";
			settingW=settingW + " 时间:"+Lizzie.frame.toolbar.txtenginePkTimeWhite.getText()+"S";
			}
		if(Lizzie.frame.toolbar.chkenginePkPlayouts.isSelected())
		{
			settingB=settingB + " 总计算量:"+Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
			settingW=settingW + " 总计算量:"+Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText();
			}
		if(Lizzie.frame.toolbar.chkenginePkFirstPlayputs.isSelected())
		{
			settingB=settingB + " 首位计算量:"+Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
			settingW=settingW + " 首位计算量:"+Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText();
			}
		
		settingAll=settingAll+" 认输阈值:连续"+Lizzie.frame.toolbar.pkResignMoveCounts +"手,胜率低于"+Lizzie.frame.toolbar.pkResginWinrate+"%";
		
		if(Lizzie.frame.toolbar.chkenginePkBatch.isSelected())
		{
			settingAll=settingAll + " 多盘:"+Lizzie.frame.toolbar.txtenginePkBatch.getText();
			}
		if(Lizzie.frame.toolbar.chkenginePkContinue.isSelected())
		{
			settingAll=settingAll + " 续弈: 是";
			}
		else {
			settingAll=settingAll + " 续弈: 否";
		}
		if (Lizzie.frame.toolbar.exChange) {
			settingAll=settingAll + " 交换黑白: 是";
		    }
		else {
			settingAll=settingAll + " 交换黑白: 否";
		}
		
		 if (Lizzie.frame.toolbar.checkGameMaxMove) {
			 settingAll=settingAll + " 最大手数: "+Lizzie.frame.toolbar.maxGanmeMove;
		    }
		    if (Lizzie.frame.toolbar.checkGameMinMove) {
		    	settingAll=settingAll + " 最小手数: "+Lizzie.frame.toolbar.minGanmeMove;
		    }		 

		    if (Lizzie.frame.toolbar.isRandomMove) {
		    	settingAll=settingAll + " 随机落子: 前"+Lizzie.frame.toolbar.randomMove +"手,胜率不低于首位"+Lizzie.frame.toolbar.randomDiffWinrate + "%";
			  }
		    
	    }    
		Lizzie.board.updateComment();
		if(outOfMoveNum)
		{
			saveTimeoutFile();
		}
		else {	
			if(!outOfMoveNum&&!doublePass) {
			if (blackResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts) {
			// df=df+"_白胜";
				if(Lizzie.frame.toolbar.exChange) {
			if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
			{	Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
			if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkBlackWinAsWhite = Lizzie.frame.toolbar.pkBlackWinAsWhite+1;
			if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkWhiteWinAsWhite = Lizzie.frame.toolbar.pkWhiteWinAsWhite+1;
			}
			else
			{	Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
			if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsWhite = Lizzie.frame.toolbar.pkBlackWinAsWhite+1;
			if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsWhite = Lizzie.frame.toolbar.pkWhiteWinAsWhite+1;
			}
				}
				else {
					Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
					if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsWhite = Lizzie.frame.toolbar.pkBlackWinAsWhite+1;
					if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsWhite = Lizzie.frame.toolbar.pkWhiteWinAsWhite+1;
				}
			GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
			gameInfo.setResult("白("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename + ")胜");
			Lizzie.frame.setResult("白("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename + ")胜");
		} else {
			// df=df+"_黑胜";
			if(Lizzie.frame.toolbar.exChange) {
			if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
				{Lizzie.frame.toolbar.pkWhiteWins = Lizzie.frame.toolbar.pkWhiteWins + 1;
				if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkBlackWinAsBlack = Lizzie.frame.toolbar.pkBlackWinAsBlack+1;
				if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkWhiteWinAsBlack = Lizzie.frame.toolbar.pkWhiteWinAsBlack+1;
				}
			else
				{Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
				if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsBlack = Lizzie.frame.toolbar.pkBlackWinAsBlack+1;
				if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsBlack = Lizzie.frame.toolbar.pkWhiteWinAsBlack+1;
				}
			}
			else {
				Lizzie.frame.toolbar.pkBlackWins = Lizzie.frame.toolbar.pkBlackWins + 1;
				if(currentEngineN==Lizzie.frame.toolbar.engineBlack)Lizzie.frame.toolbar.pkBlackWinAsBlack = Lizzie.frame.toolbar.pkBlackWinAsBlack+1;
				if(currentEngineN==Lizzie.frame.toolbar.engineWhite)Lizzie.frame.toolbar.pkWhiteWinAsBlack = Lizzie.frame.toolbar.pkWhiteWinAsBlack+1;
			}
			GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
			gameInfo.setResult("黑("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename + ")胜");
			Lizzie.frame.setResult("黑("
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename + ")胜");

		}
			}

		 if (Lizzie.frame.toolbar.AutosavePk || Lizzie.frame.toolbar.isEnginePkBatch) {
			if (doublePass) {
				savePassFile();				
			} else {
				savePkFile();
			}
		}
		}
		if(Lizzie.frame.toolbar.isEnginePkBatch)
		{
			String resultB="";
			String resultW="";
			String resultOther="";
			if(Lizzie.frame.toolbar.exChange) {
				if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0)
				{ 
				resultB=resultB+"黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkBlackWins + "局";
				resultB=resultB+" 执黑胜"+Lizzie.frame.toolbar.pkWhiteWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkWhiteWinAsWhite+"局";
				resultW="白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkWhiteWins + "局";
				resultW=resultW+" 执黑胜"+Lizzie.frame.toolbar.pkBlackWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkBlackWinAsWhite+"局";
				}
				else
				{	 resultB="黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkBlackWins + "局";
				resultB=resultB+" 执黑胜"+Lizzie.frame.toolbar.pkWhiteWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkWhiteWinAsWhite+"局";
				resultW=resultW+"白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkWhiteWins + "局";			
				resultW=resultW+" 执黑胜"+Lizzie.frame.toolbar.pkBlackWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkBlackWinAsWhite+"局";
				}
					}
					else {
						resultB="黑("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkBlackWins + "局";
						resultB=resultB+" 执黑胜"+Lizzie.frame.toolbar.pkWhiteWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkWhiteWinAsWhite+"局";		
						resultW=resultW+"白("+Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename+")胜:"+Lizzie.frame.toolbar.pkWhiteWins + "局";
						resultW=resultW+" 执黑胜"+Lizzie.frame.toolbar.pkBlackWinAsBlack+"局 执白胜"+Lizzie.frame.toolbar.pkBlackWinAsWhite+"局";
					}
			
			
			resultOther=resultOther+"双pass"+Lizzie.frame.toolbar.doublePassGame+"局";
			resultOther=resultOther+" 超手数"+Lizzie.frame.toolbar.maxMoveGame+"局";
			savePkTxt(settingB,settingW,settingAll,resultB,resultW,resultOther);
			}
		if (Lizzie.frame.toolbar.isEnginePkBatch) {
			int EnginePkBatchNumber = 1;
			try {
				EnginePkBatchNumber = Integer.parseInt(Lizzie.frame.toolbar.txtenginePkBatch.getText());
			} catch (NumberFormatException err) {
			}
			if (Lizzie.frame.toolbar.EnginePkBatchNumberNow < EnginePkBatchNumber) {
				Lizzie.frame.toolbar.EnginePkBatchNumberNow = Lizzie.frame.toolbar.EnginePkBatchNumberNow + 1;
				// 下一盘PK
//				if (Lizzie.frame.toolbar.checkGameTime) {
//					Lizzie.engineManager.gameTime = System.currentTimeMillis();
//				}
				Lizzie.frame.setResult("");
				if (Lizzie.frame.toolbar.exChange)
				// if(false)
				{
					int temp = Lizzie.frame.toolbar.engineBlack;
					Lizzie.frame.toolbar.engineBlack = Lizzie.frame.toolbar.engineWhite;
					Lizzie.frame.toolbar.engineWhite = temp;
					if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0) {
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
						int tempindex = Lizzie.frame.toolbar.enginePkBlack.getSelectedIndex();
						Lizzie.frame.toolbar.enginePkBlack
								.setSelectedIndex(Lizzie.frame.toolbar.enginePkWhite.getSelectedIndex());
						Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(tempindex);
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
						Lizzie.frame.toolbar.lblenginePkResult
								.setText(Lizzie.frame.toolbar.pkWhiteWins + ":" + Lizzie.frame.toolbar.pkBlackWins);
						String temp1 = Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkFirstPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
						temp1 = Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);
						temp1 = Lizzie.frame.toolbar.txtenginePkTime.getText();
						Lizzie.frame.toolbar.txtenginePkTime
								.setText(Lizzie.frame.toolbar.txtenginePkTimeWhite.getText());
						Lizzie.frame.toolbar.txtenginePkTimeWhite.setText(temp1);
					} else {
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
						int tempindex = Lizzie.frame.toolbar.enginePkBlack.getSelectedIndex();
						Lizzie.frame.toolbar.enginePkBlack
								.setSelectedIndex(Lizzie.frame.toolbar.enginePkWhite.getSelectedIndex());
						Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(tempindex);
						Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
						Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
						Lizzie.frame.toolbar.lblenginePkResult
								.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);
						String temp1 = Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkFirstPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
						temp1 = Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
						Lizzie.frame.toolbar.txtenginePkPlayputs
								.setText(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
						Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);
					}
				} else {
					Lizzie.frame.toolbar.lblenginePkResult
							.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);
				}

				Lizzie.board.clearforpk();
				if (Lizzie.frame.toolbar.chkenginePkContinue.isSelected()) {
					Lizzie.board.setlist(Lizzie.frame.toolbar.startGame);
				}
				if (Lizzie.board.getHistory().isBlacksTurn()) {
					 Lizzie.frame.toolbar.isEnginePk = false;
					 Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineBlack);
				        Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineWhite);
				        Runnable runnable =
				            new Runnable() {
				              public void run() {
				            	  try {
					                    Thread.sleep(500);
					                  } catch (InterruptedException e) {
					                    // TODO Auto-generated catch block
					                    e.printStackTrace();
					                  }
				                while (!Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).isLoaded()
				                    || !Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).isLoaded()) {
				                	 try {
						                    Thread.sleep(500);
						                  } catch (InterruptedException e) {
						                    // TODO Auto-generated catch block
						                    e.printStackTrace();
						                  }
				                }
				                Lizzie.frame.toolbar.isEnginePk = true;
				           	 Runnable runnable =
							            new Runnable() {
							              public void run() {
							                while (Lizzie.frame.toolbar.isEnginePk)
							                {
							                	try {
													Thread.sleep(Lizzie.config.analyzeUpdateIntervalCentisec*10);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
							                  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).pkResign();
							                Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).pkResign();
							                }
							              }
							            };
							        Thread thread = new Thread(runnable);
							        thread.start();
				                Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
				                Lizzie.leelaz.ponder();
				              }
			            };
			            Thread thread = new Thread(runnable);
			            thread.start();

				} else {
					 Lizzie.frame.toolbar.isEnginePk = false;
					 Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineWhite);
				        Lizzie.engineManager.startEngineForPk(Lizzie.frame.toolbar.engineBlack);
				        Runnable runnable =
				            new Runnable() {
				              public void run() {
				            	  try {
					                    Thread.sleep(500);
					                  } catch (InterruptedException e) {
					                    // TODO Auto-generated catch block
					                    e.printStackTrace();
					                  }
				                while (!Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).isLoaded()
				                    || !Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).isLoaded()) {
				                  try {
				                    Thread.sleep(500);
				                  } catch (InterruptedException e) {
				                    // TODO Auto-generated catch block
				                    e.printStackTrace();
				                  }
				                }
				                Lizzie.frame.toolbar.isEnginePk = true;
				           	 Runnable runnable =
							            new Runnable() {
							              public void run() {
							                while (Lizzie.frame.toolbar.isEnginePk)
							                {
							                	try {
													Thread.sleep(Lizzie.config.analyzeUpdateIntervalCentisec*10);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
							                  Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).pkResign();
							                Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).pkResign();
							                }
							              }
							            };
							        Thread thread = new Thread(runnable);
							        thread.start();
				                Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
				                Lizzie.leelaz.ponder();
				              }
			            };
			            Thread thread = new Thread(runnable);
			            thread.start();

				}

				Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());

				Lizzie.frame.setPlayers(
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename,
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename);
				GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();

				gameInfo.setPlayerWhite(
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename);
				gameInfo.setPlayerBlack(
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename);	
			} else {

				// 结束PK
				Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
				Lizzie.frame.boardRenderer.reverseBestmoves = false;
				if (Lizzie.frame.toolbar.exChange && Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0) {
					Lizzie.frame.toolbar.lblenginePkResult
							.setText(Lizzie.frame.toolbar.pkWhiteWins + ":" + Lizzie.frame.toolbar.pkBlackWins);
				} else {
					Lizzie.frame.toolbar.lblenginePkResult
							.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);
				}
				Lizzie.frame.addInput();
				Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
				Lizzie.frame.toolbar.analyse.setEnabled(true);
				Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
				Lizzie.frame.toolbar.txtenginePkBatch.setEnabled(true);
				Lizzie.frame.toolbar.btnEnginePkConfig.setEnabled(true);
				Lizzie.frame.toolbar.isEnginePk = false;
				Lizzie.frame.toolbar.btnStartPk.setText("开始");
				Lizzie.frame.toolbar.chkenginePkBatch.setEnabled(true);
				// chkenginePkgenmove.setEnabled(true);
				Lizzie.frame.toolbar.chkenginePk.setEnabled(true);
				Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
				Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).played = false;
				Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).played = false;
				// Lizzie.engineManager.switchEngineForEndPk(Lizzie.engineManager.currentEngineNo);
				Lizzie.frame.toolbar.batchPkName = "";
				
				Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
				Lizzie.engineManager.changeEngIcoForEndPk();
				if(msg==null||!msg.isVisible())
            	{	
					File file = new File("");
					String courseFile = "";
					try {
						courseFile = file.getCanonicalPath();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  msg=new Message();
				  String passandMove="";
				  if(Lizzie.frame.toolbar.doublePassGame>0)
					  passandMove=passandMove+"双方Pass局数 "+Lizzie.frame.toolbar.doublePassGame;
				  if(Lizzie.frame.toolbar.maxMoveGame>0)
					  passandMove=passandMove+"超最大手数局数  "+Lizzie.frame.toolbar.maxMoveGame;
	              msg.setMessage( "批量对战已结束,比分为" + Lizzie.frame.toolbar.pkBlackWins + ":"
							+ Lizzie.frame.toolbar.pkWhiteWins +" "+passandMove+ "棋谱保存在"+courseFile+"\\PkAutoSave");
	              msg.setVisible(true);
            	}
				
			}
		} else {
			// 结束PKLizzie.engineManager.engineList.get(engineBlack).notPondering();
			Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
			Lizzie.frame.boardRenderer.reverseBestmoves = false;
			Lizzie.frame.toolbar.lblenginePkResult
					.setText(Lizzie.frame.toolbar.pkBlackWins + ":" + Lizzie.frame.toolbar.pkWhiteWins);

			Lizzie.frame.addInput();
			Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
			Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
			Lizzie.frame.toolbar.txtenginePkBatch.setEnabled(true);
			Lizzie.frame.toolbar.btnEnginePkConfig.setEnabled(true);
			Lizzie.frame.toolbar.analyse.setEnabled(true);
			Lizzie.frame.toolbar.isEnginePk = false;
			Lizzie.frame.toolbar.btnStartPk.setText("开始");
			Lizzie.frame.toolbar.chkenginePkBatch.setEnabled(true);
			// chkenginePkgenmove.setEnabled(true);
			Lizzie.frame.toolbar.chkenginePk.setEnabled(true);
			Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
			Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).played = false;
			Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).played = false;
			// Lizzie.engineManager.switchEngineForEndPk(Lizzie.engineManager.currentEngineNo);
			Lizzie.frame.toolbar.batchPkName = "";
	

			String jg = "对战已结束，";
			if(Lizzie.frame.toolbar.checkGameMaxMove&&Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.maxGanmeMove)
				jg=jg+"超过手数限制";
			else {
			if (currentEngineN == Lizzie.frame.toolbar.engineBlack) {
				// df=df+"_白胜";
				jg = jg + "白胜";
			} else {
				jg = jg + "黑胜";
			}
			}
			if (Lizzie.frame.toolbar.AutosavePk) {
				File file = new File("");
				String courseFile = "";
				try {
					courseFile = file.getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jg = jg + "，棋谱保存在"+courseFile+"\\PkAutoSave";
			}
			Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
            Lizzie.frame.boardRenderer.reverseBestmoves = false;
			Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
			Lizzie.engineManager.changeEngIcoForEndPk();
			if(msg==null||!msg.isVisible())
        	{	
				
			  msg=new Message();
             msg.setMessage(jg);
             msg.setVisible(true);
        	}
		}
		
	}

	private void savePassFile() {
		Lizzie.frame.toolbar.doublePassGame=Lizzie.frame.toolbar.doublePassGame+1;
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
		if (Lizzie.frame.toolbar.isEnginePkBatch) {
			
			autoSaveFile = new File(courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.batchPkName + "\\"
					+ "双Pass对局" + sf + ".sgf");
			autoSaveFile2 = new File(
					courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.SF + "\\" + "双Pass对局" + sf + ".sgf");

		} else {
			autoSaveFile2 = new File(courseFile + "\\" + "PkAutoSave" + "\\" + "双Pass对局" + sf + ".sgf");
			autoSaveFile = new File(courseFile + "\\" + "PkAutoSave" + "\\" + "双Pass对局" + sf + ".sgf");
			
		}
	
		File fileParent = autoSaveFile.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		try {
			SGFParser.save(Lizzie.board, autoSaveFile.getPath());
			if(Lizzie.frame.toolbar.enginePkSaveWinrate)
			{
				String autoSavePng;
				if (Lizzie.frame.toolbar.isEnginePkBatch) {
					autoSavePng = courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.batchPkName + "\\"
							+ "双Pass对局" + sf + ".png";
				} else {
					autoSavePng =courseFile + "\\" + "PkAutoSave" + "\\" + "双Pass对局" + sf + ".png";
				}

				Lizzie.frame.saveImage(Lizzie.frame.statx,Lizzie.frame.staty,(int) (Lizzie.frame.grw * 1.03),Lizzie.frame.grh +Lizzie.frame.stath, autoSavePng);
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (Lizzie.frame.toolbar.isEnginePkBatch) {
				try {
					File fileParent2 = autoSaveFile2.getParentFile();
					if (!fileParent2.exists()) {
						fileParent2.mkdirs();
					}
					SGFParser.save(Lizzie.board, autoSaveFile2.getPath());
					if(Lizzie.frame.toolbar.enginePkSaveWinrate)
					{
						
						String autoSavePng2 = null;
						if (Lizzie.frame.toolbar.isEnginePkBatch) {
							autoSavePng2 = courseFile + "\\" + "PkAutoSave" + "\\" +  Lizzie.frame.toolbar.SF + "\\"
									+ "双Pass对局" + sf + ".png";
						} else {
							autoSavePng2 = courseFile + "\\" + "PkAutoSave" +  "\\"
									+ "双Pass对局" + sf + ".png";
						}

						Lizzie.frame.saveImage(Lizzie.frame.statx,Lizzie.frame.staty,(int) (Lizzie.frame.grw * 1.03),Lizzie.frame.grh +Lizzie.frame.stath, autoSavePng2);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

	 private void saveTimeoutFile() {		 
		 Lizzie.frame.toolbar.maxMoveGame=Lizzie.frame.toolbar.maxMoveGame+1;
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

			String df = "";
			if (Lizzie.frame.toolbar.isEnginePkBatch) {
				df = Lizzie.frame.toolbar.EnginePkBatchNumberNow + "_";
			}
			df = df + "黑" + Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename + "_白"
					+ Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename;
			if (Lizzie.frame.toolbar.isEnginePkBatch) {
				
				autoSaveFile =
				        new File(
				            courseFile
				                + "\\"
				                + "PkAutoSave"
				                + "\\"
				                + Lizzie.frame.toolbar.batchPkName
				                + "\\"
				                +df+ "_超手数对局_"
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
				                +df  + "_超手数对局_"
				                + sf
				                + ".sgf");
			} else {
				autoSaveFile2 = new File(courseFile + "\\" + "PkAutoSave" + "\\"  +df+ "_超手数对局_" + sf + ".sgf");
				autoSaveFile = new File(courseFile + "\\" + "PkAutoSave" + "\\" +df+ "_超手数对局_"+ sf + ".sgf");
				
			}
		
		

		    File fileParent = autoSaveFile.getParentFile();
		    if (!fileParent.exists()) {
		      fileParent.mkdirs();
		    }
		    try {
		      SGFParser.save(Lizzie.board, autoSaveFile.getPath());
		      if(Lizzie.frame.toolbar.enginePkSaveWinrate)
				{
					String autoSavePng;
					if (Lizzie.frame.toolbar.isEnginePkBatch) {
						autoSavePng = courseFile + "\\" + "PkAutoSave" + "\\" + Lizzie.frame.toolbar.batchPkName + "\\"
								+df	+ "_超手数对局_"  + sf + ".png";
					} else {
						autoSavePng =courseFile + "\\" + "PkAutoSave" + "\\" +df	+ "_超手数对局_" + sf + ".png";
					}

					Lizzie.frame.saveImage(Lizzie.frame.statx,Lizzie.frame.staty,(int) (Lizzie.frame.grw * 1.03),Lizzie.frame.grh +Lizzie.frame.stath, autoSavePng);
				}
		    } catch (IOException e) {
		      // TODO Auto-generated catch block
		      if (Lizzie.frame.toolbar.isEnginePkBatch) {
		        try {
		          File fileParent2 = autoSaveFile2.getParentFile();
		          if (!fileParent2.exists()) {
		            fileParent2.mkdirs();
		          }
		          SGFParser.save(Lizzie.board, autoSaveFile2.getPath());
		          if(Lizzie.frame.toolbar.enginePkSaveWinrate)
					{
						
						String autoSavePng2 = null;
						if (Lizzie.frame.toolbar.isEnginePkBatch) {
							autoSavePng2 = courseFile + "\\" + "PkAutoSave" + "\\" +  Lizzie.frame.toolbar.SF + "\\"
								+df	+ "_超手数对局_" + sf + ".png";
						} else {
							autoSavePng2 = courseFile + "\\" + "PkAutoSave" +  "\\"
									+df+ "_超手数对局_" + sf + ".png";
						}

						Lizzie.frame.saveImage(Lizzie.frame.statx,Lizzie.frame.staty,(int) (Lizzie.frame.grw * 1.03),Lizzie.frame.grh +Lizzie.frame.stath, autoSavePng2);
					}		         
		        } catch (IOException e1) {
		          // TODO Auto-generated catch block
		          e1.printStackTrace();
		        }
		      }
		      e.printStackTrace();
		    }
		  }

	 
	private void notifyAutoPK() {
		if (resigned) {
			return;
		}
		if (Lizzie.frame.toolbar.isPkStop) {
			return;
		}

		if (Lizzie.frame.toolbar.isEnginePk) {
			double curWR = this.bestMoves.get(0).oriwinrate;

			int time = 0;
			int playouts = 0;
			int firstPlayouts = 0;
			if (Lizzie.frame.toolbar.chkenginePkTime.isSelected()) {
				if (!Lizzie.frame.toolbar.isSameEngine && this.currentEngineN == Lizzie.frame.toolbar.engineBlack
						|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
					try {
						time = 1000 * Integer.parseInt(Lizzie.frame.toolbar.txtenginePkTime.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				} else {
					try {
						time = 1000 * Integer
								.parseInt(Lizzie.frame.toolbar.txtenginePkTimeWhite.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
			}
			if (Lizzie.frame.toolbar.chkenginePkPlayouts.isSelected()) {
				if (!Lizzie.frame.toolbar.isSameEngine && this.currentEngineN == Lizzie.frame.toolbar.engineBlack
						|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
					try {
						playouts = Integer
								.parseInt(Lizzie.frame.toolbar.txtenginePkPlayputs.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				} else {
					try {
						playouts = Integer
								.parseInt(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
			}
			if (Lizzie.frame.toolbar.chkenginePkFirstPlayputs.isSelected()) {
				if (!Lizzie.frame.toolbar.isSameEngine && this.currentEngineN == Lizzie.frame.toolbar.engineBlack
						|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
					try {
						firstPlayouts = Integer
								.parseInt(Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				} else {
					try {
						firstPlayouts = Integer.parseInt(
								Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText().replace(" ", ""));
					} catch (NumberFormatException err) {
					}
				}
			}
			
			   if (Lizzie.frame.toolbar.checkGameMaxMove&&Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.maxGanmeMove) {
				   outOfMoveNum=true;
					resigned = true;

				//	pkResign();
				
					nameCmd();
				  return;
			   }

			if (firstPlayouts > 0||playNow) {
				if (bestMoves.get(0).playouts >= firstPlayouts||playNow) {
					played = true;
					playNow=false;
					if ((curWR < Lizzie.frame.toolbar.pkResginWinrate)
							&& Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.minMove) {
						if (Lizzie.board.getHistory().isBlacksTurn())
							blackResignMoveCounts = blackResignMoveCounts + 1;
						else
							whiteResignMoveCounts = whiteResignMoveCounts + 1;
					} else {

						if (blackResignMoveCounts > 0 || whiteResignMoveCounts > 0) {
							if (Lizzie.board.getHistory().isBlacksTurn())
								blackResignMoveCounts = 0;
							else
								whiteResignMoveCounts = 0;
						}
					}
					if (blackResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts
							|| whiteResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts) {

						if (bestMoves.get(0).coordinate.equals("pass")) {
							Lizzie.board.pass();
						} else {
							int coords[] = Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
							Lizzie.board.place(coords[0], coords[1]);
						}
						resigned = true;

				//		pkResign();
					
						nameCmd();
						return;
					}
					
					MoveData playMove=null;
					if(Lizzie.frame.toolbar.isRandomMove&&Lizzie.board.getHistory().getMoveNumber()<=Lizzie.frame.toolbar.randomMove)
						playMove=this.randomBestmove(bestMoves, Lizzie.frame.toolbar.randomDiffWinrate);
					else
						playMove=bestMoves.get(0);
					if (playMove.coordinate.equals("pass")) {
						Optional<int[]> passStep = Optional.empty();
						Optional<int[]> lastMove = Lizzie.board.getLastMove();
						if (lastMove == passStep) {
							Lizzie.board.pass();
							doublePass = true;
							resigned = true;
							nameCmd();
							return;
						}

						if (!Lizzie.frame.toolbar.isSameEngine
								&& this.currentEngineN == Lizzie.frame.toolbar.engineBlack
								|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
							if (!Lizzie.frame.toolbar.isSameEngine)
								Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack)
										.playMoveNoPonder("B", "pass");
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B",
									"pass");
							Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
							// Lizzie.leelaz.isPondering=true;
							Lizzie.board.pass();
							// Lizzie.leelaz.played=false;
						}

						else {
							if (!Lizzie.frame.toolbar.isSameEngine)
								Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite)
										.playMoveNoPonder("W", "pass");
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W",
									"pass");
							Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
							// Lizzie.leelaz.isPondering=true;
							Lizzie.board.pass();
							// Lizzie.leelaz.played=false;
						}
						return;
					}

					int coords[] = Lizzie.board.convertNameToCoordinates(playMove.coordinate);

					// nameCmd();
					if (!Lizzie.frame.toolbar.isSameEngine && this.currentEngineN == Lizzie.frame.toolbar.engineBlack
							|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
						if (!Lizzie.frame.toolbar.isSameEngine)
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder("B",
									Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B",
								Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));

						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
						// Lizzie.leelaz.isPondering=true;
						Lizzie.board.place(coords[0], coords[1]);
						// Lizzie.leelaz.played=false;
					}

					else { //
							// Lizzie.leelaz.isPondering=true;
						if (!Lizzie.frame.toolbar.isSameEngine)
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W",
									Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W",
								Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
						Lizzie.board.place(coords[0], coords[1]);
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
					played = true;
					// if(playanyway)
					// playanyway=false;
					if (curWR < Lizzie.frame.toolbar.pkResginWinrate
							&& Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.minMove) {
						if (Lizzie.board.getHistory().isBlacksTurn())
							blackResignMoveCounts = blackResignMoveCounts + 1;
						else
							whiteResignMoveCounts = whiteResignMoveCounts + 1;
					} else {

						if (blackResignMoveCounts > 0 || whiteResignMoveCounts > 0) {
							if (Lizzie.board.getHistory().isBlacksTurn())
								blackResignMoveCounts = 0;
							else
								whiteResignMoveCounts = 0;
						}
					}
					if (blackResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts
							|| whiteResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts) {
						if (bestMoves.get(0).coordinate.equals("pass")) {
							Lizzie.board.pass();
						} else {
							int coords[] = Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
							Lizzie.board.place(coords[0], coords[1]);
						}
						resigned = true;
						// pkResign();
						// System.out.println("认输2"+this.currentEngineN);
						nameCmd();

						return;
					}

					MoveData playMove=null;
					if(Lizzie.frame.toolbar.isRandomMove&&Lizzie.board.getHistory().getMoveNumber()<=Lizzie.frame.toolbar.randomMove)
						playMove=this.randomBestmove(bestMoves, Lizzie.frame.toolbar.randomDiffWinrate);
					else
						playMove=bestMoves.get(0);
					if (playMove.coordinate.equals("pass")) {
						Optional<int[]> passStep = Optional.empty();
						Optional<int[]> lastMove = Lizzie.board.getLastMove();
						if (lastMove == passStep) {
							Lizzie.board.pass();
							doublePass = true;
							resigned = true;
							nameCmd();
							return;
						}
						if (!Lizzie.frame.toolbar.isSameEngine
								&& this.currentEngineN == Lizzie.frame.toolbar.engineBlack
								|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
							if (!Lizzie.frame.toolbar.isSameEngine)
								Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack)
										.playMoveNoPonder("B", "pass");
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B",
									"pass");
							// Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
							// Lizzie.leelaz.isPondering=true;
							Lizzie.board.pass();
							// Lizzie.leelaz.played=false;
						}

						else {
							if (!Lizzie.frame.toolbar.isSameEngine)
								Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite)
										.playMoveNoPonder("W", "pass");
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W",
									"pass");
							// Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
							// Lizzie.leelaz.isPondering=true;
							Lizzie.board.pass();
							// Lizzie.leelaz.played=false;
						}
						return;
					}

					int coords[] = Lizzie.board.convertNameToCoordinates(playMove.coordinate);

					// nameCmd();
					if (!Lizzie.frame.toolbar.isSameEngine && this.currentEngineN == Lizzie.frame.toolbar.engineBlack
							|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
						if (!Lizzie.frame.toolbar.isSameEngine)
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder("B",
									Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B",
								Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						// Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).bestMoves
						// = new ArrayList<>();
						// Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
						// Lizzie.leelaz.isPondering=true;
						Lizzie.board.place(coords[0], coords[1]);

						// Lizzie.leelaz.played=false;
					}

					else {
						// Lizzie.leelaz.isPondering=true;
						if (!Lizzie.frame.toolbar.isSameEngine)
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W",
									Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W",
								Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						// Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).bestMoves
						// = new ArrayList<>();
						// Lizzie.leelaz=Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
						Lizzie.board.place(coords[0], coords[1]);

						// Lizzie.leelaz.played=false;
					}
					return;

				}
			}

			if (time > 0) {
				if (System.currentTimeMillis() - startPonderTime > time) {
					played = true;
					// if(playanyway)
					// playanyway=false;
					if (curWR < Lizzie.frame.toolbar.pkResginWinrate
							&& Lizzie.board.getHistory().getMoveNumber() > Lizzie.frame.toolbar.minMove) {
						if (Lizzie.board.getHistory().isBlacksTurn())
							blackResignMoveCounts = blackResignMoveCounts + 1;
						else
							whiteResignMoveCounts = whiteResignMoveCounts + 1;
					} else {

						if (blackResignMoveCounts > 0 || whiteResignMoveCounts > 0) {
							if (Lizzie.board.getHistory().isBlacksTurn())
								blackResignMoveCounts = 0;
							else
								whiteResignMoveCounts = 0;
						}
					}
					if (blackResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts
							|| whiteResignMoveCounts >= Lizzie.frame.toolbar.pkResignMoveCounts) {
						if (bestMoves.get(0).coordinate.equals("pass")) {
							Lizzie.board.pass();
						} else {
							int coords[] = Lizzie.board.convertNameToCoordinates(bestMoves.get(0).coordinate);
							Lizzie.board.place(coords[0], coords[1]);
						}
						resigned = true;
						// pkResign();
						// System.out.println("认输3"+this.currentEngineN);
						nameCmd();
						return;
					}

					MoveData playMove=null;
					if(Lizzie.frame.toolbar.isRandomMove&&Lizzie.board.getHistory().getMoveNumber()<=Lizzie.frame.toolbar.randomMove)
						playMove=this.randomBestmove(bestMoves, Lizzie.frame.toolbar.randomDiffWinrate);
					else
						playMove=bestMoves.get(0);
					if (playMove.coordinate.equals("pass")) {

						Optional<int[]> passStep = Optional.empty();
						Optional<int[]> lastMove = Lizzie.board.getLastMove();
						if (lastMove == passStep) {
							Lizzie.board.pass();
							doublePass = true;
							resigned = true;
							nameCmd();
							return;
						}
						if (!Lizzie.frame.toolbar.isSameEngine
								&& this.currentEngineN == Lizzie.frame.toolbar.engineBlack
								|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
							if (!Lizzie.frame.toolbar.isSameEngine)
								Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack)
										.playMoveNoPonder("B", "pass");
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B",
									"pass");
							Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
							// Lizzie.leelaz.isPondering=true;
							Lizzie.board.pass();
							// Lizzie.leelaz.played=false;
						}

						else {
							if (!Lizzie.frame.toolbar.isSameEngine)
								Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite)
										.playMoveNoPonder("W", "pass");
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W",
									"pass");
							Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
							// Lizzie.leelaz.isPondering=true;
							Lizzie.board.pass();
							// Lizzie.leelaz.played=false;
						}
						return;
					}

					int coords[] = Lizzie.board.convertNameToCoordinates(playMove.coordinate);

					// nameCmd();
					if (!Lizzie.frame.toolbar.isSameEngine && this.currentEngineN == Lizzie.frame.toolbar.engineBlack
							|| Lizzie.frame.toolbar.isSameEngine && Lizzie.board.getData().blackToPlay) {
						if (!Lizzie.frame.toolbar.isSameEngine)
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMoveNoPonder("B",
									Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMovePonder("B",
								Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));

						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite);
						// Lizzie.leelaz.isPondering=true;
						Lizzie.board.place(coords[0], coords[1]);
						// Lizzie.leelaz.played=false;
					}

					else {
						// Lizzie.leelaz.isPondering=true;
						if (!Lizzie.frame.toolbar.isSameEngine)
							Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playMoveNoPonder("W",
									Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playMovePonder("W",
								Lizzie.board.convertCoordinatesToName(coords[0], coords[1]));
						Lizzie.leelaz = Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack);
						Lizzie.board.place(coords[0], coords[1]);
						// Lizzie.leelaz.played=false;
					}
					return;
				}
			}

		}
	}

	public void nameCmd() {
		try {
			sendCommand("name");
		} catch (Exception es) {

		}
		Lizzie.frame.menu.toggleEngineMenuStatus(false,false);
	}

	public void boardSize(int width, int height) {
		sendCommand("boardsize " + width + (width != height ? " " + height : ""));
		if (firstLoad) {
			Lizzie.board.open(width, height);
			Lizzie.board.getHistory().getGameInfo().setKomi(komi);
			Lizzie.board.getHistory().getGameInfo().DEFAULT_KOMI = (double) komi;
			Lizzie.frame.komi = komi + "";
			firstLoad = false;

		}
	}
	
	  public void komi(double komi) {
		    synchronized (this) {
		      sendCommand("komi " + (komi == 0.0 ? "0" : komi));		     
		      Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
		      if (isPondering) ponder();
		    }
		  }

	public void nameCmdfornoponder() {
		canGetGenmoveInfo = false;
		try {
			sendCommand("name");
		} catch (Exception es) {

		}
		Lizzie.frame.menu.toggleEngineMenuStatus(false,false);

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
				// while (true) {
				// c = process.getInputStream().read();
				line.append((char) c);
				if ((c == '\n')) {				
					if (Lizzie.frame.toolbar.isEnginePk && Lizzie.frame.toolbar.isGenmove && isLoaded) {
						try {
							parseLineForGenmovePk(line.toString());
						} catch (Exception e) {
						}

					} else {
						try {
							parseLine(line.toString());
						} catch (Exception e) {
						}						
					}				
					line = new StringBuilder();
					if(isInfoLine)
					{
						if (!this.bestMoves.isEmpty()) {							
							  notifyAutoPK();	
				        	  notifyAutoPlay();						        
						}
					}
					if(isCommandLine)
					{
						currentCmdNum = currentCmdNum+1;
						if(currentCmdNum>cmdNumber-1)
							currentCmdNum=cmdNumber-1;
						try {
						trySendCommandFromQueue();
						}
						catch (Exception e) {
							
						}						
					}
					isCommandLine = false;
					isInfoLine=false;
				} 
				else if (c == '='||c=='?') {
					isCommandLine = true;
				}
			}
			// this line will be reached when Leelaz shuts down
			System.out.println("Leelaz process ended.");
			//process.destroy();
			shutdown();
			// Do no exit for switching weights
			// System.exit(-1);
		} catch (IOException e) {
			// e.printStackTrace();
		//	System.out.println("读出错");
			// System.exit(-1);
			// read();
		}
	}

	private void closeAutoAna() {		
		Runnable runnable =
		        new Runnable() {
		          public void run() {
		        	  if (!isClosing) {
		      			isClosing=true;			
		      			Lizzie.frame.toolbar.stopAutoAna();
		      			Lizzie.frame.addInput();
		      			if (!isSaving&&Lizzie.frame.toolbar.chkAnaAutoSave.isSelected()) {
		      				isSaving = true;
		      				saveAndLoad();				
		      			} else {		      				
		      				if(msg==null||!msg.isVisible())
		                  	{	
		      				  msg=new Message();
		      		             msg.setMessage("自动分析已完毕");
		      		             msg.setVisible(true);
		                  	}
		      			}
		      			}
		          }
		        };
		    Thread thread = new Thread(runnable);
		    thread.start();
		//	isClosing=false;
	}

	/**
	 * Sends a command to command queue for leelaz to execute
	 *
	 * @param command a GTP command containing no newline characters
	 */
	public void sendCommand(String command) {
		synchronized (cmdQueue) {
			// For efficiency, delete unnecessary "lz-analyze" that will be stopped
			// immediately
			if (!cmdQueue.isEmpty() && (cmdQueue.peekLast().startsWith("lz-analyze")
					|| cmdQueue.peekLast().startsWith("kata-analyze"))) {
				cmdQueue.removeLast();
			}
			cmdQueue.addLast(command);
			trySendCommandFromQueue();
			if (Lizzie.frame.isAutocounting) {
				if (command.startsWith("play") || command.startsWith("undo")) {
					Lizzie.frame.zen.sendCommand(command);
					Lizzie.frame.zen.countStones();
				}
			}
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
			if (cmdQueue.isEmpty() || (cmdQueue.peekFirst().startsWith("lz-analyze")
					|| cmdQueue.peekFirst().startsWith("kata-analyze")) && !isResponseUpToDate()) {
				// 临时添加为了解决SSH时的卡顿
//				if(!isResponseUpToDate())
//				{
//					if(firstNoRespond2)
//					{
//						this.commandTime= System.currentTimeMillis();
//						firstNoRespond2=false;
//					}
//					if(System.currentTimeMillis()-commandTime>200)
//					{
//					if(cmdQueue.peekFirst().startsWith("lz-analyze")
//							|| cmdQueue.peekFirst().startsWith("kata-analyze"))
//							{	String command = cmdQueue.removeFirst();
//					sendCommandToLeelaz(command);}
//						commandTime=System.currentTimeMillis();
//						firstNoRespond2=true;
//					}
				// 临时添加为了解决SSH时的卡顿
				// }
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
		if (Lizzie.engineManager.isEmpty)
			return;
		if (command.startsWith("fixed_handicap") || (isKatago && command.startsWith("place_free_handicap")))
			isSettingHandicap = true;
//    if (printCommunication) {
//      System.out.println(currentEnginename+" "+ cmdNumber+" "+ command);
//    }
		//
		cmdNumber++;
		if (Lizzie.gtpConsole.isVisible())
			Lizzie.gtpConsole.addCommand(command, cmdNumber, currentEnginename);			
		//command = cmdNumber + " " + command;
		//cmdNumber++;
		try {
			outputStream.write((command + "\n").getBytes());
			outputStream.flush();
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
//	private boolean isResponseUpToDate2() {
//		// Use >= instead of == for avoiding hang-up, though it cannot happen
//		return currentCmdNum >= cmdNumber - 2;
//	}
	public void setResponseUpToDate() {
		// Use >= instead of == for avoiding hang-up, though it cannot happen
		currentCmdNum = cmdNumber-1 ;
	}

	/**
	 * @param color color of stone to play
	 * @param move  coordinate of the coordinate
	 */
	public void playMove(Stone color, String move) {
		if (Lizzie.engineManager.isEmpty) {
			return;
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
				throw new IllegalArgumentException("The stone color must be B or W, but was " + color.toString());
			}

			sendCommand("play " + colorString + " " + move);
			bestMoves = new ArrayList<>();
			if (Lizzie.frame.isPlayingAgainstLeelaz)
				bestMovesPrevious = new ArrayList<>();
			if (isPondering && !Lizzie.frame.isPlayingAgainstLeelaz)
				ponder2();
		}
	}
	
	public void playMoveNoPonder(Stone color, String move) {
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
				throw new IllegalArgumentException("The stone color must be B or W, but was " + color.toString());
			}
			sendCommand("play " + colorString + " " + move);
			Lizzie.frame.subBoardRenderer.reverseBestmoves = true;
			Lizzie.frame.boardRenderer.reverseBestmoves = true;
			// bestMoves = new ArrayList<>();
		}
		}	
	

	public void playMoveNoPonder(String colorString, String move) {
		synchronized (this) {
			sendCommand("play " + colorString + " " + move);
			sendCommand("name");
			Lizzie.frame.subBoardRenderer.reverseBestmoves = true;
			Lizzie.frame.boardRenderer.reverseBestmoves = true;
			// bestMoves = new ArrayList<>();
		}
		}	
	
	

	public void playMovePonder(String colorString, String move) {
		
		synchronized (this) {
			played = false;
			bestMoves = new ArrayList<>();
			sendCommand("play " + colorString + " " + move);
			pkponder();
		}
	}

	public boolean playMoveGenmove(String colorString, String move) {
		if(this.resigned)
		{
			this.genmoveResign();
			return false;
		}
		synchronized (this) {
			played = false;

			sendCommand("play " + colorString + " " + move);
		}
		return true;
	}

	public void playMovewithavoid(Stone color, String move) {
		if (Lizzie.frame.toolbar.isEnginePk) {
			return;
		}
		
			String colorString;
			switch (color) {
			case BLACK:
				colorString = "B";
				break;
			case WHITE:
				colorString = "W";
				break;
			default:
				throw new IllegalArgumentException("The stone color must be B or W, but was " + color.toString());
			}

			sendCommand("play " + colorString + " " + move);
			bestMoves = new ArrayList<>();

			if (isPondering && !Lizzie.frame.isPlayingAgainstLeelaz)
				ponder();
	
	}

	public void genmove(String color) {
		String command = "genmove " + color;
		/*
		 * We don't support displaying this while playing, so no reason to request it
		 * (for now) if (isPondering) { command = "lz-genmove_analyze " + color + " 10";
		 * }
		 */
		sendCommand(command);
		isThinking = true;
		Lizzie.frame.menu.toggleEngineMenuStatus(false,true);
		canGetGenmoveInfo = false;
		isPondering = false;
		genmovenoponder = false;
	}

	public void genmoveForPk(String color) {
		if(Lizzie.frame.toolbar.isPkStop)
		{
			Lizzie.frame.toolbar.isPkGenmoveStop=true;
			if(color.equals("B"))
		{
			Lizzie.frame.toolbar.isPkStopGenmoveB=true;
		}
		else {
			Lizzie.frame.toolbar.isPkStopGenmoveB=false;
		}
			return;}
		String command = "genmove " + color;
		/*
		 * We don't support displaying this while playing, so no reason to request it
		 * (for now) if (isPondering) { command = "lz-genmove_analyze " + color + " 10";
		 * }
		 */
		bestMoves = new ArrayList<>();
		canGetGenmoveInfo = true;
		sendCommand(command);
		// isThinking = true;

		// isPondering = false;
		// genmovenoponder =false;
	}

	public void genmove_analyze(String color) {
		String command = "lz-genmove_analyze " + color + " " + Lizzie.config.analyzeUpdateIntervalCentisec;
		sendCommand(command);
		isThinking = true;
		isPondering = false;
	}

	public void time_settings() {
		Lizzie.leelaz.sendCommand("time_settings 0 "
				+ Lizzie.config.config.getJSONObject("leelaz").getInt("max-game-thinking-time-seconds") + " 1");
	}

	public void clear() {
		synchronized (this) {
			sendCommand("clear_board");
			// sendCommand("clear_cache");
			if (isKatago) {
				scoreMean = 0;
				scoreStdev = 0;
			}
			bestMoves = new ArrayList<>();
			if (isPondering)
				ponder();
		}
	}

	public void clearWithoutPonder() {
		synchronized (this) {
			this.notPondering();
			sendCommand("name");			
			sendCommand("clear_board");
			// sendCommand("clear_cache");
			bestMoves = new ArrayList<>();
			// if (isPondering) ponder();
		}
	}

	public void undo() {
		if (Lizzie.engineManager.isEmpty) {
			return;
		}
		synchronized (this) {
			sendCommand("undo");
			bestMoves = new ArrayList<>();
			if (isPondering)
				ponder();
		}
	}

	public void analyzeAvoid(String type, String color, String coordList, int untilMove) {

		// added for change bestmoves immediatly not wait until totalplayouts is bigger
		// than previous
		// analyze result
		analyzeAvoid(String.format("%s %s %s %d", type, color, coordList, untilMove <= 0 ? 1 : untilMove));
	//	Lizzie.board.getHistory().getData().tryToClearBestMoves();
		Lizzie.board.clearbestmoves();
	}

	public void analyzeAvoid(String parameters) {
		if (this.isKatago) {
			return;
		}
		// added for change bestmoves immediatly not wait until totalplayouts is bigger
		// than previous
		// analyze result
		bestMoves = new ArrayList<>();
		if (!isPondering) {
			isPondering = true;
			startPonderTime = System.currentTimeMillis();
		}
			sendCommand(String.format("lz-analyze %d %s", getInterval(), parameters));		
		//Lizzie.board.getHistory().getData().tryToClearBestMoves();
		Lizzie.board.clearbestmoves();
	}

	/** This initializes leelaz's pondering mode at its current position */
	public void ponder() {
		if(isZen)
			return;
		isPondering = true;
		startPonderTime = System.currentTimeMillis();
//		if (Lizzie.frame.isheatmap) {
//			Lizzie.leelaz.heatcount.clear();
//			// Lizzie.frame.isheatmap = false;
//		}
		if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
			return;
		}
		int currentmove = Lizzie.board.getcurrentmovenumber();
		if (featurecat.lizzie.gui.RightClickMenu.isKeepForcing) {
			featurecat.lizzie.gui.RightClickMenu.voidanalyze();
		} else {
			featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
			featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
			featurecat.lizzie.gui.RightClickMenu.move = 0;
			featurecat.lizzie.gui.RightClickMenu.isforcing = false;
			if (this.isKatago) {
				if (Lizzie.config.showKataGoEstimate)
					sendCommand("kata-analyze " + getInterval() + " ownership true");
				else
					sendCommand("kata-analyze " + getInterval());
			} else {				
				sendCommand("lz-analyze " + getInterval());
			} // until it responds to this, incoming
				// ponder results are obsolete
		}
		Lizzie.frame.menu.toggleEngineMenuStatus(true,false);
	}
	
	private int getInterval() {
		if(isSSH&&(Lizzie.config.analyzeUpdateIntervalCentisec<Lizzie.config.analyzeUpdateIntervalCentisecSSH))
			return Lizzie.config.analyzeUpdateIntervalCentisecSSH;
		else
			return Lizzie.config.analyzeUpdateIntervalCentisec;
	}
	
	public void ponder2() {
		if(isZen)
			return;
		isPondering = true;
		startPonderTime = System.currentTimeMillis();
//		if (Lizzie.frame.isheatmap) {
//			Lizzie.leelaz.heatcount.clear();
//			// Lizzie.frame.isheatmap = false;
//		}
		if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
			return;
		}
		int currentmove = Lizzie.board.getcurrentmovenumber();
		if (featurecat.lizzie.gui.RightClickMenu.isKeepForcing) {
			featurecat.lizzie.gui.RightClickMenu.voidanalyzeponder();
		} else {
			featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
			featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
			featurecat.lizzie.gui.RightClickMenu.move = 0;
			featurecat.lizzie.gui.RightClickMenu.isforcing = false;
			if (this.isKatago) {
				if (Lizzie.config.showKataGoEstimate)
					sendCommand("kata-analyze " + getInterval() + " ownership true");
				else
					sendCommand("kata-analyze " + getInterval());
			} else {
				sendCommand("lz-analyze " + getInterval());
			} // until it responds to this, incoming
				// ponder results are obsolete
		}
		Lizzie.frame.menu.toggleEngineMenuStatus(true,false);
	}
	
	public void pkponder() {
		isPondering = true;
		startPonderTime = System.currentTimeMillis();
//		if (Lizzie.frame.isheatmap) {
//			Lizzie.leelaz.heatcount.clear();
//			// Lizzie.frame.isheatmap = false;
//		}
		
			if (this.isKatago) {
				if (Lizzie.config.showKataGoEstimate)
					sendCommand("kata-analyze " + getInterval() + " ownership true");
				else
					sendCommand("kata-analyze " + getInterval());
			} else {				
				sendCommand("lz-analyze " + getInterval());
			} // until it responds to this, incoming
				// ponder results are obsolete
		
	}

	public void ponderwithavoid() {
		isPondering = true;
//		if (Lizzie.frame.isheatmap) {
//			Lizzie.leelaz.heatcount.clear();
//			// Lizzie.frame.isheatmap = false;
//		}
		if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
			return;
		}
		startPonderTime = System.currentTimeMillis();
		int currentmove = Lizzie.board.getcurrentmovenumber();
		if (featurecat.lizzie.gui.RightClickMenu.move > 0 && featurecat.lizzie.gui.RightClickMenu.move > currentmove
				&& currentmove >= featurecat.lizzie.gui.RightClickMenu.startmove) {
			featurecat.lizzie.gui.RightClickMenu.voidanalyzeponder();
		} else {
			featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
			featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
			featurecat.lizzie.gui.RightClickMenu.move = 0;
			featurecat.lizzie.gui.RightClickMenu.isforcing = false;
			if (this.isKatago) {
				sendCommand("kata-analyze " + getInterval());
			} else {
				sendCommand("lz-analyze " + getInterval());
			} // until it responds to this, incoming
				// ponder results are obsolete
		}
	}

	public void togglePonder() {
		isPondering = !isPondering;
		if(Lizzie.frame.isShowingHeatmap)
		{	Lizzie.frame.isShowingHeatmap=false;
		ponder();	
		}
		if (isPondering) {
			ponder();			
		} else {
			sendCommand("name"); // ends pondering
			Lizzie.frame.menu.toggleEngineMenuStatus(false,false);
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
	public void clearBestMoves() {
		bestMoves = new ArrayList<>();
	}
	
	// public Optional<String> getDynamicKomi() {
	// if (Float.isNaN(dynamicKomi) || Float.isNaN(dynamicOppKomi)) {
	// return Optional.empty();
	// } else {
	// return Optional.of(String.format("%.1f / %.1f", dynamicKomi,
	// dynamicOppKomi));
	// }
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
	 * Return the best win rate and total number of playouts. If no analysis
	 * available, win rate is negative and playouts is 0.
	 */
	public WinrateStats getWinrateStats() {
		WinrateStats stats = new WinrateStats(-100, 0);

		if (!bestMoves.isEmpty()) {
			// we should match the Leelaz UCTNode get_eval, which is a weighted average
			// copy the list to avoid concurrent modification exception... TODO there must
			// be a better way
			// (note the concurrent modification exception is very very rare)
			// We should use Lizzie Board's best moves as they will generally be the most
			// accurate
			final List<MoveData> moves = new ArrayList<MoveData>(Lizzie.board.getData().bestMoves);

			// get the total number of playouts in moves
			int totalPlayouts = moves.stream().mapToInt(move -> move.playouts).sum();
			stats.totalPlayouts = totalPlayouts;

			// stats.maxWinrate = bestMoves.get(0).winrate;
			stats.maxWinrate = BoardData.getWinrateFromBestMoves(moves);
			// BoardData.getWinrateFromBestMoves(moves);
		}

		return stats;
	}

	/*
	 * initializes the normalizing factor for winrate_to_handicap_stones conversion.
	 */
	public void estimatePassWinrate() {
		// we use A1 instead of pass, because valuenetwork is more accurate for A1 on
		// empty board than a
		// pass.
		// probably the reason for higher accuracy is that networks have randomness
		// which produces
		// occasionally A1 as first move, but never pass.
		// for all practical purposes, A1 should equal pass for the value it provides,
		// hence good
		// replacement.
		// this way we avoid having to run lots of playouts for accurate winrate for
		// pass.
		playMove(Stone.BLACK, "A1");
		togglePonder();
		WinrateStats stats = getWinrateStats();

		// we could use a timelimit or higher minimum playouts to get a more accurate
		// measurement.
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
	 * Convert winrate to handicap stones, by normalizing winrate by first move pass
	 * winrate (one stone handicap).
	 */
	public static double winrateToHandicap(double pWinrate) {
		// we assume each additional handicap lowers winrate by fixed percentage.
		// this is pretty accurate for human handicap games at least.
		// also this kind of property is a requirement for handicaps to determined based
		// on rank
		// difference.

		// lets convert the 0%-50% range and 100%-50% from both the move and and pass
		// into range of 0-1
		double moveWinrateSymmetric = 1 - Math.abs(1 - (pWinrate / 100) * 2);
		double passWinrateSymmetric = 1 - Math.abs(1 - (mHandicapWinrate / 100) * 2);

		// convert the symmetric move winrate into correctly scaled log scale, so that
		// winrate of
		// passWinrate equals 1 handicap.
		double handicapSymmetric = Math.log(moveWinrateSymmetric) / Math.log(passWinrateSymmetric);

		// make it negative if we had low winrate below 50.
		return Math.signum(pWinrate - 50) * handicapSymmetric;
	}

	// public synchronized void addListener(LeelazListener listener) {
	// listeners.add(listener);
	// }

	// Beware, due to race conditions, bestMoveNotification can be called once even
	// after item is
	// removed
	// with removeListener
	public synchronized void removeListener(LeelazListener listener) {
		listeners.remove(listener);
	}

	// private synchronized void notifyBestMoveListeners() {
	// for (LeelazListener listener : listeners) {
	// listener.bestMoveNotification(bestMoves);
	// }
	// }

	private static enum ParamState {
		NORMAL, QUOTE, DOUBLE_QUOTE
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
	
	//随机落子
	public MoveData randomBestmove(List<MoveData> bestMoves,double diffWinrate)
	{
		double minWinrate=bestMoves.get(0).winrate-diffWinrate;
		List<MoveData> bestMovesTemp = new ArrayList<>();
		bestMovesTemp.add(bestMoves.get(0));
		for(int i=1;i<bestMoves.size();i++)
		{
              if(bestMoves.get(i).winrate>=minWinrate)
            	  bestMovesTemp.add(bestMoves.get(i));
		}
		    Random random = new Random();
		         int n = random.nextInt(bestMovesTemp.size());		         
		return bestMovesTemp.get(n);
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

	public void toggleHeatmap() {
		// TODO Auto-generated method stub		
        Lizzie.frame.isShowingHeatmap=!Lizzie.frame.isShowingHeatmap;
        isheatmap = Lizzie.frame.isShowingHeatmap;
        heatcount = new ArrayList<Integer>();
		if(isheatmap)
		{ 
		sendCommand("heatmap");}
		else if(isPondering){
			ponder();
		}
		//isPondering=false;
	}
}
