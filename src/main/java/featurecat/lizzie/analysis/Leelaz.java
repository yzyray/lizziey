package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Stone;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
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

  private long maxAnalyzeTimeMillis; // , maxThinkingTimeMillis;
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
    maxAnalyzeTimeMillis = MINUTE * config.getInt("max-analyze-time-minutes");

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
    currentEnginename =
        Lizzie.config.leelazConfig.optString(
            "enginename" + String.valueOf(index + 1), currentWeight);
    if (currentEnginename.equals("")) currentEnginename = currentWeight;
  }

  public void startEngine() throws IOException {
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
    process = processBuilder.start();

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
    Lizzie.frame.refreshBackground();
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
      currentEnginename =
          Lizzie.config.leelazConfig.optString(
              "enginename" + String.valueOf(index + 1), currentWeight);
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
    switch (index) {
      case 0:
        featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 1:
        featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 2:
        featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 3:
        featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 4:
        featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 5:
        featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 6:
        featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 7:
        featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 8:
        featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
      case 9:
        featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.stop);
        break;
    }
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

  public void normalQuit() {
    switch (currentEngineN) {
      case 0:
        featurecat.lizzie.gui.Menu.engine1.setIcon(null);
        break;
      case 1:
        featurecat.lizzie.gui.Menu.engine2.setIcon(null);
        break;
      case 2:
        featurecat.lizzie.gui.Menu.engine3.setIcon(null);
        break;
      case 3:
        featurecat.lizzie.gui.Menu.engine4.setIcon(null);
        break;
      case 4:
        featurecat.lizzie.gui.Menu.engine5.setIcon(null);
        break;
      case 5:
        featurecat.lizzie.gui.Menu.engine6.setIcon(null);
        break;
      case 6:
        featurecat.lizzie.gui.Menu.engine7.setIcon(null);
        break;
      case 7:
        featurecat.lizzie.gui.Menu.engine8.setIcon(null);
        break;
      case 8:
        featurecat.lizzie.gui.Menu.engine9.setIcon(null);
        break;
      case 9:
        featurecat.lizzie.gui.Menu.engine10.setIcon(null);
        break;
    }
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
  private void initializeStreams() {
    inputStream = new BufferedInputStream(process.getInputStream());
    outputStream = new BufferedOutputStream(process.getOutputStream());
  }

  public static List<MoveData> parseInfo(String line) {
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
      Lizzie.gtpConsole.addLine(line);
      // if (printCommunication || gtpConsole) {
      // Lizzie.gtpConsole.addLine(line);
      // }
      if (line.startsWith("komi=")) {
        try {
          dynamicKomi = Float.parseFloat(line.substring("komi=".length()).trim());
        } catch (NumberFormatException nfe) {
          dynamicKomi = Float.NaN;
        }
      } else if (line.startsWith("opp_komi=")) {
        try {
          dynamicOppKomi = Float.parseFloat(line.substring("opp_komi=".length()).trim());
        } catch (NumberFormatException nfe) {
          dynamicOppKomi = Float.NaN;
        }
      } else if (line.equals("\n")) {
        // End of response
      } else if (line.startsWith("info")) {
        isLoaded = true;
        // Clear switching prompt
        switching = false;

        // Display engine command in the title
        Lizzie.frame.updateTitle();
        if (isResponseUpToDate()) {
          // This should not be stale data when the command number match
          this.bestMoves = parseInfo(line.substring(5));
          notifyBestMoveListeners();
          Lizzie.frame.refresh();
          // don't follow the maxAnalyzeTime rule if we are in analysis mode
          if (System.currentTimeMillis() - startPonderTime > maxAnalyzeTimeMillis
              && !Lizzie.board.inAnalysisMode()) {
            togglePonder();
          }
        }
      } else if (line.contains("STAGE")) {
        Lizzie.gtpConsole.addLineforce(line);
      } else if (line.contains("> KoMI")) {
        Lizzie.gtpConsole.addLineforce(line);
      } else if (line.contains(" ->   ")) {
        isLoaded = true;
        if (isResponseUpToDate()
            || isThinking
                && (!isPondering && Lizzie.frame.isPlayingAgainstLeelaz || isInputCommand)) {
          if (line.contains("pass")) {
          } else if (!switching) {

            bestMoves.add(MoveData.fromSummary(line));
            notifyBestMoveListeners();
            Lizzie.frame.repaint();
          }
        }
      } else if (line.startsWith("play")) {
        // In lz-genmove_analyze
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.board.place(line.substring(5).trim());
        }
        isThinking = false;

      } else if (line.startsWith("=") || line.startsWith("?")) {
        if (printCommunication || gtpConsole) {
          System.out.print(line);
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
            if (!Lizzie.config.playponder) Lizzie.leelaz.sendCommand("name");
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
            JOptionPane.showMessageDialog(
                Lizzie.frame, "Lizzie需要使用0.15或更新版本的leela zero引擎,当前引擎版本是: " + params[1] + ")");
          }
          isCheckingVersion = false;

          switch (currentEngineN) {
            case 0:
              featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 1:
              featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 2:
              featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 3:
              featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 4:
              featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 5:
              featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 6:
              featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 7:
              featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 8:
              featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
              break;
            case 9:
              featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
          }
          switch (Lizzie.engineManager.currentEngineNo) {
            case 0:
              featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 1:
              featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 2:
              featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 3:
              featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 4:
              featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 5:
              featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 6:
              featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 7:
              featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 8:
              featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
            case 9:
              featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.icon);
              break;
          }
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
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
        if ((c == '\n')) {
          parseLine(line.toString());
          line = new StringBuilder();
        }
      }
      // this line will be reached when Leelaz shuts down
      System.out.println("Leelaz process ended.");

      shutdown();
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
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
          || cmdQueue.peekFirst().startsWith("lz-analyze") && !isResponseUpToDate()) {
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
      System.out.printf("> %d %s\n", cmdNumber, command);
    }
    Lizzie.gtpConsole.addCommand(command, cmdNumber);
    command = cmdNumber + " " + command;
    cmdNumber++;
    try {
      outputStream.write((command + "\n").getBytes());
      outputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  public void playMovewithavoid(Stone color, String move) {
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
        "lz-genmove_analyze "
            + color
            + " "
            + Lizzie.config
                .config
                .getJSONObject("leelaz")
                .getInt("analyze-update-interval-centisec");
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
        String.format(
            "lz-analyze %d %s",
            Lizzie.config.config.getJSONObject("leelaz").getInt("analyze-update-interval-centisec"),
            parameters));
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
              + Lizzie.config
                  .config
                  .getJSONObject("leelaz")
                  .getInt(
                      "analyze-update-interval-centisec")); // until it responds to this, incoming
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
              + Lizzie.config
                  .config
                  .getJSONObject("leelaz")
                  .getInt(
                      "analyze-update-interval-centisec")); // until it responds to this, incoming
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

  public Optional<String> getDynamicKomi() {
    if (Float.isNaN(dynamicKomi) || Float.isNaN(dynamicOppKomi)) {
      return Optional.empty();
    } else {
      return Optional.of(String.format("%.1f / %.1f", dynamicKomi, dynamicOppKomi));
    }
  }

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

      stats.maxWinrate = bestMoves.get(0).winrate;
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

  public synchronized void addListener(LeelazListener listener) {
    listeners.add(listener);
  }

  // Beware, due to race conditions, bestMoveNotification can be called once even after item is
  // removed
  // with removeListener
  public synchronized void removeListener(LeelazListener listener) {
    listeners.remove(listener);
  }

  private synchronized void notifyBestMoveListeners() {
    for (LeelazListener listener : listeners) {
      listener.bestMoveNotification(bestMoves);
    }
  }

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
