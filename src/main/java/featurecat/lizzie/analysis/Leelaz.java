package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.BoardData;
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
import java.util.Timer;
import java.util.TimerTask;
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
  private JSONObject leelazConfig;
  public ArrayList<Integer> heatcount = new ArrayList<Integer>();
  public double heatwinrate;

  public Process[] process = new Process[10];

  private BufferedInputStream[] inputStream = new BufferedInputStream[10];
  private BufferedOutputStream[] outputStream = new BufferedOutputStream[10];

  private boolean printCommunication;
  public boolean gtpConsole;

  private List<MoveData> bestMoves;
  private List<MoveData> bestMovesTemp;

  private List<LeelazListener> listeners;

  private boolean isPondering;
  private long startPonderTime;

  // fixed_handicap
  public boolean isSettingHandicap = false;
  boolean stopread = false;
  // genmove
  public boolean isheatmap = false;
  public boolean isThinking = false;
  public boolean isInputCommand = false;
  // public boolean isChanged = false;
  private boolean isLoaded = false;
  private boolean isCheckingVersion;
  // for Multiple Engine
  private String engineCommand;
  private List<String> commands;
  private JSONObject config;
  private String currentWeightFile = "";
  private String currentWeight = "";
  public String currentEnginename = "";
  public boolean switching = true;
  private int currentEngineN = -1;
  private ScheduledExecutorService[] executor = new ScheduledExecutorService[10];
  //  private ScheduledExecutorService executor1;
  //  private ScheduledExecutorService executor2;
  //  private ScheduledExecutorService executor3;
  //  private ScheduledExecutorService executor4;
  //  private ScheduledExecutorService executor5;
  //  private ScheduledExecutorService executor6;
  //  private ScheduledExecutorService executor7;
  //  private ScheduledExecutorService executor8;
  //  private ScheduledExecutorService executor9;

  // dynamic komi and opponent komi as reported by dynamic-komi version of leelaz
  private float dynamicKomi = Float.NaN;
  private float dynamicOppKomi = Float.NaN;

  /**
   * Initializes the leelaz process and starts reading output
   *
   * @throws IOException
   */
  public Leelaz() throws IOException, JSONException {
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
    engineCommand = config.getString("engine-command");
    // substitute in the weights file
    engineCommand = engineCommand.replaceAll("%network-file", config.getString("network-file"));

    // Initialize current engine number and start engine
    currentEngineN = 0;
    startEngine(engineCommand, 0);
    featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.stop);
    Lizzie.frame.refreshBackground();
  }

  public void startEngine(String engineCommand, int index) throws IOException {
    if (engineCommand.trim().isEmpty()) {
      return;
    }

    commands = splitCommand(engineCommand);

    Pattern wPattern = Pattern.compile("(?s).*?(--weights |-w )([^'\" ]+)(?s).*");
    Matcher wMatcher = wPattern.matcher(engineCommand);
    if (wMatcher.matches() && wMatcher.groupCount() == 2) {
      currentWeightFile = wMatcher.group(2);
      String[] names = currentWeightFile.split("[\\\\|/]");
      currentWeight = names.length > 1 ? names[names.length - 1] : currentWeightFile;
      currentEnginename =
          Lizzie.config.leelazConfig.optString(
              "enginename" + String.valueOf(index + 1), currentWeight);
    }

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.redirectErrorStream(true);
    process[index] = processBuilder.start();

    initializeStreams(index);
    // isCheckingVersion = true;
    executor[index] = Executors.newSingleThreadScheduledExecutor();
    executor[index].execute(this::read);
    isCheckingVersion = true;
    sendCommand("version");
    sendCommand("boardsize " + Lizzie.config.uiConfig.optInt("board-size", 19));
    // ponder();
  }

  public boolean isEngineAlive(int index) {
    return process[index] != null && process[index].isAlive();
  }

  public void killAllEngines() {
    switching = false;
    for (int i = 0; i < process.length; i++) {
      try {
        process[i].destroy();
      } catch (Exception e) {
      }
    }
  }

  public void killOtherEngines() {
    switching = false;
    for (int i = 0; i < process.length; i++)
      if (i != currentEngineN) {
        {
          try {
            process[i].destroy();
          } catch (Exception e) {
          }
        }
      }
  }

  public void restartEngine(String engineCommand, int index) throws IOException {

    if (engineCommand.trim().isEmpty()) {
      return;
    }

    //  isCheckingVersion = true;
    this.engineCommand = engineCommand;
    // stop the ponder

    isPondering = false;
    // isThinking = false;
    // Lizzie.frame.isPlayingAgainstLeelaz = false;
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
    if (isEngineAlive(index)) // 需要添加判断,对应index的进程知否初始化并且alive
    {

      if (isEngineAlive(currentEngineN)) {
        normalQuit(currentEngineN);
        outputStream[this.currentEngineN].write(("stop" + "\n").getBytes());
        outputStream[this.currentEngineN].flush();
        stopread = true;
      }
      reinitializeStreams(engineCommand, index);
    } else {

      if (isEngineAlive(currentEngineN)) {
        normalQuit(currentEngineN);
        outputStream[this.currentEngineN].write(("stop" + "\n").getBytes());
        outputStream[this.currentEngineN].flush();
        stopread = true;
      }
      startEngine(engineCommand, index);
    }
    currentEngineN = index;
  }

  public void normalQuit(int index) throws IOException {
    if (!Lizzie.config.fastChange) {
      sendCommandToLeelaz("quit", index);

      executor[index].shutdown();
      try {
        while (!executor[index].awaitTermination(1, TimeUnit.SECONDS)) {
          executor[index].shutdownNow();
        }
        if (executor[index].awaitTermination(1, TimeUnit.SECONDS)) {
          shutdown(index);
        }
      } catch (InterruptedException e) {
        executor[index].shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
    //    if (execuser) {
    //      executor.shutdownNow();
    //      //      try {
    //      //        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
    //      //          executor.shutdownNow();
    //      //        }
    //      //        if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
    //      //          shutdown();
    //      //        }
    //      //      } catch (InterruptedException e) {
    //      //        executor.shutdownNow();
    //      //        Thread.currentThread().interrupt();
    //      // }
    //    } else {
    //      executor2.shutdownNow();
    //      //      try {
    //      //        while (!executor2.awaitTermination(1, TimeUnit.SECONDS)) {
    //      //          executor2.shutdownNow();
    //      //        }
    //      //        if (executor2.awaitTermination(1, TimeUnit.SECONDS)) {
    //      //          shutdown();
    //      //        }
    //      //      } catch (InterruptedException e) {
    //      //        executor2.shutdownNow();
    //      //        Thread.currentThread().interrupt();
    //      //      }
    //    }

    if (!Lizzie.config.fastChange) {
      switch (index) {
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
    }
  }

  /** Initializes the input and output streams */
  private void initializeStreams(int index) {
    currentEngineN = index;

    inputStream[index] = new BufferedInputStream(process[index].getInputStream());
    outputStream[index] = new BufferedOutputStream(process[index].getOutputStream());
  }

  private void reinitializeStreams(String engineCommand, int index) {
    commands = splitCommand(engineCommand);
    currentEngineN = index;
    Pattern wPattern = Pattern.compile("(?s).*?(--weights |-w )([^'\" ]+)(?s).*");
    Matcher wMatcher = wPattern.matcher(engineCommand);
    if (wMatcher.matches() && wMatcher.groupCount() == 2) {
      currentWeightFile = wMatcher.group(2);
      String[] names = currentWeightFile.split("[\\\\|/]");
      currentWeight = names.length > 1 ? names[names.length - 1] : currentWeightFile;
      currentEnginename =
          Lizzie.config.leelazConfig.optString(
              "enginename" + String.valueOf(index + 1), currentWeight);
    }
    isCheckingVersion = true;
    inputStream[index] = new BufferedInputStream(process[index].getInputStream());
    outputStream[index] = new BufferedOutputStream(process[index].getOutputStream());
    executor[index] = Executors.newSingleThreadScheduledExecutor();
    stopread = false;
    executor[index].execute(this::read);
    //   sendCommand("version");
    //  ponder();
    Timer timer = new Timer();
    timer.schedule(
        new TimerTask() {
          public void run() {
            sendCommand("version");
            // ponder();
            // sendCommand("version");
            this.cancel();
          }
        },
        100);
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

  private void changeEngIco() {
    switch (currentEngineN) {
      case 0:
        featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 1:
        featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 2:
        featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 3:
        featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 4:
        featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 5:
        featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 6:
        featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 7:
        featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 8:
        featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
      case 9:
        featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.icon);
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null) {
          featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
        }
        break;
    }
  }
  /**
   * Parse a line of Leelaz output
   *
   * @param line output line
   */
  private void parseLine(String line) {

    synchronized (this) {
      // Lizzie.gtpConsole.addLineforce(line);

      if (printCommunication || gtpConsole) {
        if (line.startsWith("info")) {
        } else {
          Lizzie.gtpConsole.addLine(line);
        }
      }

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

      }
      //      else   if (switching) {
      //          if (line.contains("tree")) {
      //            //switching = false;
      //            sendCommand("version");
      //          //  ponder();
      //           //changeEngIco();
      //          }
      //        }
      else if (line.startsWith("info")) {
        isLoaded = true;
        // Clear switching prompt
        //   if (switching) {
        //     if (!line.contains("->")) {
        //      switching = false;
        //      sendCommand("version");
        // ponder();
        //   //changeEngIco();
        //   }
        //  }
        // Display engine command in the title
        Lizzie.frame.updateTitle();
        if (isResponseUpToDate()) {
          // This should not be stale data when the command number match
          this.bestMoves = parseInfo(line.substring(5));
          notifyBestMoveListeners();
          Lizzie.frame.repaint();
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
          if (line.contains("pass")) {}

          //          else {
          //            if (!switching) {
          //              bestMoves.add(MoveData.fromSummary(line));
          //              notifyBestMoveListeners();
          //              Lizzie.frame.repaint();
          //            }
          //          }
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
          Lizzie.config.leelaversion = minor;
          // Gtp support added in version 15
          if (minor < 15) {
            JOptionPane.showMessageDialog(
                Lizzie.frame, "Lizzie需要使用0.15或更新版本的leela zero引擎,当前引擎版本是: " + params[1] + ")");
          }
          isCheckingVersion = false;
          switching = false;
          ponder();
          changeEngIco();
        }
      }
      if (isheatmap) {
        if (line.startsWith(" ")) {
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
          heatwinrate = Double.valueOf(params[1]);
          Lizzie.frame.repaint();
        }
      }
    }
  }

  //  private void parseLine2(String line) {
  //
  //    synchronized (this) {
  //      // Lizzie.gtpConsole.addLineforce(line);
  //      if (printCommunication || gtpConsole) {
  //        if (line.startsWith("info")) {
  //        } else {
  //          Lizzie.gtpConsole.addLine(line);
  //        }
  //      }
  //
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
  //      } else if (line.equals("\n")) {
  //        // End of response
  //      } else if (line.startsWith("info")) {
  //        isLoaded = true;
  //        // Clear switching prompt
  //        //        if (switching) {
  //        //          if (!line.contains("->")) {
  //        //            switching = false;
  //        //            ponder();
  //        //            changeEngIco();
  //        //          }
  //        //        }
  //        // Display engine command in the title
  //        Lizzie.frame.updateTitle();
  //        if (isResponseUpToDate()) {
  //          // This should not be stale data when the command number match
  //          this.bestMoves = parseInfo(line.substring(5));
  //          notifyBestMoveListeners();
  //          Lizzie.frame.repaint();
  //          // don't follow the maxAnalyzeTime rule if we are in analysis mode
  //          if (System.currentTimeMillis() - startPonderTime > maxAnalyzeTimeMillis
  //              && !Lizzie.board.inAnalysisMode()) {
  //            togglePonder();
  //          }
  //        }
  //      } else if (line.contains("STAGE")) {
  //        Lizzie.gtpConsole.addLineforce(line);
  //      } else if (line.contains("> KoMI")) {
  //        Lizzie.gtpConsole.addLineforce(line);
  //      } else if (line.contains(" ->   ")) {
  //        isLoaded = true;
  //        if (isResponseUpToDate()
  //            || isThinking
  //                && (!isPondering && Lizzie.frame.isPlayingAgainstLeelaz || isInputCommand)) {
  //          if (line.contains("pass")) {}
  //          //          } else {
  //          ////            if (!switching) {
  //          ////              bestMoves.add(MoveData.fromSummary(line));
  //          ////              notifyBestMoveListeners();
  //          ////              Lizzie.frame.repaint();
  //          ////            }
  //          //          }
  //        }
  //      } else if (line.startsWith("play")) {
  //        // In lz-genmove_analyze
  //        if (Lizzie.frame.isPlayingAgainstLeelaz) {
  //          Lizzie.board.place(line.substring(5).trim());
  //        }
  //        isThinking = false;
  //
  //      } else if (line.startsWith("=") || line.startsWith("?")) {
  //        if (printCommunication || gtpConsole) {
  //          System.out.print(line);
  //          Lizzie.gtpConsole.addLine(line);
  //        }
  //        String[] params = line.trim().split(" ");
  //        currentCmdNum = Integer.parseInt(params[0].substring(1).trim());
  //
  //        trySendCommandFromQueue();
  //
  //        if (line.startsWith("?") || params.length == 1) return;
  //
  //        if (isSettingHandicap) {
  //          bestMoves = new ArrayList<>();
  //          for (int i = 1; i < params.length; i++) {
  //            Lizzie.board
  //                .asCoordinates(params[i])
  //                .ifPresent(coords -> Lizzie.board.getHistory().setStone(coords, Stone.BLACK));
  //          }
  //          isSettingHandicap = false;
  //        } else if (isThinking && !isPondering) {
  //          if (isInputCommand) {
  //            Lizzie.board.place(params[1]);
  //            togglePonder();
  //            if (Lizzie.frame.isAutocounting) {
  //              if (Lizzie.board.getHistory().isBlacksTurn())
  //                Lizzie.frame.zen.sendCommand("play " + "w " + params[1]);
  //              else Lizzie.frame.zen.sendCommand("play " + "b " + params[1]);
  //
  //              Lizzie.frame.zen.countStones();
  //            }
  //          }
  //          if (Lizzie.frame.isPlayingAgainstLeelaz) {
  //            Lizzie.board.place(params[1]);
  //            if (Lizzie.frame.isAutocounting) {
  //              if (Lizzie.board.getHistory().isBlacksTurn())
  //                Lizzie.frame.zen.sendCommand("play " + "w " + params[1]);
  //              else Lizzie.frame.zen.sendCommand("play " + "b " + params[1]);
  //
  //              Lizzie.frame.zen.countStones();
  //            }
  //            if (!Lizzie.config.playponder) Lizzie.leelaz.sendCommand("name");
  //          }
  //          if (!isInputCommand) {
  //            isPondering = false;
  //          }
  //          isThinking = false;
  //          if (isInputCommand) {
  //            isInputCommand = false;
  //          }
  //
  //        } else if (isCheckingVersion2) {
  //          String[] ver = params[1].split("\\.");
  //          int minor = Integer.parseInt(ver[1]);
  //          Lizzie.config.leelaversion = minor;
  //          // Gtp support added in version 15
  //          if (minor < 15) {
  //            JOptionPane.showMessageDialog(
  //                Lizzie.frame,
  //                "Lizzie requires version 0.15 or later of Leela Zero for analysis (found "
  //                    + params[1]
  //                    + ")");
  //          }
  //          isCheckingVersion2 = false;
  //          switching = false;
  //          ponder();
  //          changeEngIco();
  //        }
  //      }
  //      if (isheatmap) {
  //        if (line.startsWith(" ")) {
  //          try {
  //            String[] params = line.trim().split("\\s+");
  //            if (params.length == 19) {
  //              for (int i = 0; i < params.length; i++)
  // heatcount.add(Integer.parseInt(params[i]));
  //            }
  //          } catch (Exception ex) {
  //          }
  //        }
  //        if (line.startsWith("winrate:")) {
  //          isheatmap = false;
  //          String[] params = line.trim().split(" ");
  //          heatwinrate = Double.valueOf(params[1]);
  //          Lizzie.frame.repaint();
  //        }
  //      }
  //    }
  //  }
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

  /**
   * Continually reads and processes output from leelaz
   *
   * @return
   */
  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      // while ((c = inputStream.read()) != -1) {

      while ((c = inputStream[currentEngineN].read()) != -1) {
        line.append((char) c);
        if (stopread) {
          stopread = false;
          return;
        }
        if ((c == '\n')) {
          parseLine(line.toString());
          line = new StringBuilder();
        }
      }
      // this line will be reached when Leelaz shuts down
      System.out.println("Leelaz process ended.");

      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  //  private void read2() {
  //    try {
  //      int c;
  //      StringBuilder line = new StringBuilder();
  //      // while ((c = inputStream.read()) != -1) {
  //      while ((c = inputStream2.read()) != -1) {
  //        line.append((char) c);
  //
  //        if ((c == '\n')) {
  //          if (execuser) parseLine2(line.toString());
  //          line = new StringBuilder();
  //        }
  //      }
  //      // this line will be reached when Leelaz shuts down
  //      System.out.println("Leelaz process ended.");
  //
  //      // Do no exit for switching weights
  //      // System.exit(-1);
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //      System.exit(-1);
  //    }
  //  }

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
    if (Lizzie.frame.isAutocounting) {
      if (command.startsWith("play") || command.startsWith("undo")) {
        Lizzie.frame.zen.sendCommand(command);
        Lizzie.frame.zen.countStones();
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
      outputStream[this.currentEngineN].write((command + "\n").getBytes());
      outputStream[this.currentEngineN].flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendCommandToLeelaz(String command, int index) {
    if (command.startsWith("fixed_handicap")) isSettingHandicap = true;
    if (printCommunication) {
      System.out.printf("> %d %s\n", cmdNumber, command);
    }
    Lizzie.gtpConsole.addCommand(command, cmdNumber);
    command = cmdNumber + " " + command;
    cmdNumber++;

    try {
      {
        outputStream[index].write((command + "\n").getBytes());
        outputStream[index].flush();
      }
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

      if (isPondering && !Lizzie.frame.isPlayingAgainstLeelaz) ponderwithavoid();
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
    if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
      return;
    }
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
  // this is copyed from https://github.com/zsalch/lizzie/tree/n_avoiddialog

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
  public void shutdown(int index) {
    process[index].destroy();
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

      stats.maxWinrate = BoardData.getWinrateFromBestMoves(moves);
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
