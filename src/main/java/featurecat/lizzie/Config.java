package featurecat.lizzie;

import featurecat.lizzie.theme.Theme;
import java.awt.Color;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;
import org.json.*;

public class Config {
  public String language = "en";

  public boolean showBorder = false;
  public boolean showMoveNumber = false;
  public int onlyLastMoveNumber = 10;
  // 0: Do not show; -1: Show all move number; other: Show last move number
  public int allowMoveNumber = -1;
  public boolean showMoveNumberFromOne = false;
  public boolean newMoveNumberInBranch = true;
  public boolean showWinrate = true;
  public boolean largeWinrate = false;
  public boolean showBlunderBar = true;
  public boolean weightedBlunderBarHeight = false;
  // public boolean dynamicWinrateGraphWidth = true;
  public boolean showVariationGraph = true;
  public boolean showComment = true;
  public boolean showRawBoard = false;
  public boolean showBestMovesTemporarily = false;
  public boolean showCaptured = true;
  public boolean handicapInsteadOfWinrate = false;
  public boolean showDynamicKomi = false;
  public double replayBranchIntervalSeconds = 0.3;
  public boolean showCoordinates = true;
  public boolean colorByWinrateInsteadOfVisits = false;
  public boolean showlcbwinrate = false;
  public boolean playponder = true;
  public boolean showrect = true;
  public boolean showlcbcolor = true;
  public boolean fastChange = true;
  public boolean showKataGoBoardScoreMean = false;
  public boolean kataGoScoreMeanAlwaysBlack = false;
  public boolean scoreMeanWinrateGraphBoard = false;
  public boolean showKataGoEstimate = false;
  public boolean allowDrageDoubleClick = true;
  public boolean showKataGoEstimateBySize = false;
  public boolean showKataGoEstimateOnSubbord = true;
  public boolean showKataGoEstimateOnMainbord = true;
  public boolean showSuggestionOrder = true;
  public boolean showSuggestionMaxRed = true;
  public boolean showStatus = true;
  public boolean showBranch = true;
  public boolean showBestMoves = true;
  public boolean showNextMoves = true;
  public boolean showSubBoard = true;
  public boolean largeSubBoard = false;
  public boolean startMaximized = true;
  public boolean loadZen = false;
  public boolean showSuggestionVaritions = true;
  public boolean showWinrateInSuggestion = true;
  public boolean showPlayoutsInSuggestion = true;
  public boolean showScoremeanInSuggestion = true;
  public boolean showEditbar = true;
  public boolean showNameInBoard = true;
  public boolean openHtmlOnLive = false;
  public boolean readKomi = false;
  public boolean alwaysGotoLastOnLive = false;
  public String readBoardArg1 = "0";
  public int readBoardArg2 = 300;
  public boolean readBoardArg3 = true;
  public boolean alwaysSyncBoardStat = false;
  public boolean playSound = true;
  public boolean noRefreshOnMouseMove = false;

  public JSONObject config;
  public JSONObject leelazConfig;
  public JSONObject uiConfig;
  public JSONObject persisted;
  public JSONObject persistedUi;

  private String configFilename = "config.txt";
  private String persistFilename = "persist";

  public Theme theme;
  public int winrateStrokeWidth = 2;
  public int leelaversion = 17;
  public int minimumBlunderBarWidth = 3;
  public int shadowSize = 55;
  public String fontName = null;
  public String uiFontName = null;
  public String winrateFontName = null;
  public int commentFontSize = 0;
  public Color commentFontColor = null;
  public Color commentBackgroundColor = null;
  public Color winrateLineColor = null;
  public Color winrateMissLineColor = null;
  public Color scoreMeanLineColor = null;
  public Color blunderBarColor = null;
  public float bestMoveColor;
  public boolean solidStoneIndicator = false;
  public int stoneIndicatorType = 1; // 0: non, 1: circle, 2: solid
  public boolean showCommentNodeColor = true;
  public boolean badmovesalwaysontop = false;
  public boolean mainsalwaysontop = false;
  public boolean suggestionsalwaysontop = false;
  public Color commentNodeColor = null;
  public Optional<List<Double>> blunderWinrateThresholds;
  public Optional<Map<Double, Color>> blunderNodeColors;
  public int nodeColorMode = 0;
  public boolean appendWinrateToComment = false;
  public int boardPositionProportion = 4;
  public int limitBranchLength = 0;
  public int limitMaxSuggestion = 0;
  public boolean showNoSuggCircle = true;
  public int limitbadmoves = 0;
  public int limitbadMeanmoves = 0;
  public int limitbadplayouts = 0;
  public long maxAnalyzeTimeMillis;
  public int analyzeUpdateIntervalCentisec;
  public int analyzeUpdateIntervalCentisecSSH;
  public boolean showHeat = false;
  public boolean showHeatAfterCalc = false;
  public String gtpConsoleStyle = "";
  private final String defaultGtpConsoleStyle =
      "body {background:#000000; color:#d0d0d0; font-family:Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace; margin:4px;} .command {color:#ffffff;font-weight:bold;} .winrate {color:#ffffff;font-weight:bold;} .coord {color:#ffffff;font-weight:bold;}";

  public boolean firstButton = true;
  public boolean lastButton = true;
  public boolean clearButton = true;
  public boolean countButton = true;
  public boolean forward10 = true;
  public boolean backward10 = true;
  public boolean forward1 = true;
  public boolean gotomove = true;
  public boolean backward1 = true;
  public boolean openfile = true;
  public boolean savefile = true;
  public boolean analyse = true;
  public boolean kataEstimate = true;
  public boolean heatMap = true;
  public boolean backMain = true;
  public boolean setMain = true;
  public boolean batchOpen = true;
  public boolean refresh = true;
  public boolean tryPlay = true;
  public boolean komi = true;
  public boolean move = true;
  public boolean coords = true;
  public boolean liveButton = true;
  public boolean badMoves = true;
  public boolean autoPlay = true;

  private JSONObject loadAndMergeConfig(
      JSONObject defaultCfg, String fileName, boolean needValidation) throws IOException {
    File file = new File(fileName);
    if (!file.canRead()) {
      System.err.printf("Creating config file %s\n", fileName);

      try {
        writeConfig(defaultCfg, file);
      } catch (JSONException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    FileInputStream fp = new FileInputStream(file);

    JSONObject mergedcfg = new JSONObject(new JSONTokener(fp));
    boolean modified = mergeDefaults(mergedcfg, defaultCfg);

    fp.close();

    // Validate and correct settings
    if (needValidation && validateAndCorrectSettings(mergedcfg)) {
      modified = true;
    }

    if (modified) {
      writeConfig(mergedcfg, file);
    }
    return mergedcfg;
  }

  private JSONObject loadAndMergeConfigdef(
      JSONObject defaultCfg, String fileName, boolean needValidation) throws IOException {
    File file = new File(fileName);
    System.err.printf("Creating config file %s\n", fileName);
    try {
      writeConfig(defaultCfg, file);
    } catch (JSONException e) {
      e.printStackTrace();
      System.exit(1);
    }

    FileInputStream fp = new FileInputStream(file);

    JSONObject mergedcfg = new JSONObject(new JSONTokener(fp));
    boolean modified = mergeDefaults(mergedcfg, defaultCfg);

    fp.close();

    // Validate and correct settings
    if (needValidation && validateAndCorrectSettings(mergedcfg)) {
      modified = true;
    }

    if (modified) {
      writeConfig(mergedcfg, file);
    }
    return mergedcfg;
  }

  /**
   * Check settings to ensure its consistency, especially for those whose types are not <code>
   * boolean</code>. If any inconsistency is found, try to correct it or to report it. <br>
   * For example, we only support square boards of size >= 2x2. If the configured board size is not
   * in the list above, we should correct it.
   *
   * @param config The config json object to check
   * @return if any correction has been made.
   */
  private boolean validateAndCorrectSettings(JSONObject config) {
    boolean madeCorrections = false;

    // Check ui configs
    JSONObject ui = config.getJSONObject("ui");

    // Check board-size
    int boardSize = ui.optInt("board-size", 19);
    if (boardSize < 2) {
      // Correct it to default 19x19
      ui.put("board-size", 19);
      madeCorrections = true;
    }

    // Check engine configs
    JSONObject leelaz = config.getJSONObject("leelaz");
    // Checks for startup directory. It should exist and should be a directory.
    String engineStartLocation = getBestDefaultLeelazPath();
    if (!(Files.exists(Paths.get(engineStartLocation))
        && Files.isDirectory(Paths.get(engineStartLocation)))) {
      leelaz.put("engine-start-location", ".");
      madeCorrections = true;
    }

    return madeCorrections;
  }

  public static void copyFile(File sourceFile, File targetFile) throws IOException {
    // 新建文件输入流并对它进行缓冲
    FileInputStream input = new FileInputStream(sourceFile);
    BufferedInputStream inBuff = new BufferedInputStream(input);

    // 新建文件输出流并对它进行缓冲
    FileOutputStream output = new FileOutputStream(targetFile);
    BufferedOutputStream outBuff = new BufferedOutputStream(output);

    // 缓冲数组
    byte[] b = new byte[1024 * 5];
    int len;
    while ((len = inBuff.read(b)) != -1) {
      outBuff.write(b, 0, len);
    }
    // 刷新此缓冲的输出流
    outBuff.flush();

    // 关闭流
    inBuff.close();
    outBuff.close();
    output.close();
    input.close();
  }

  public Config() throws IOException {
    JSONObject defaultConfig = createDefaultConfig();
    JSONObject persistConfig = createPersistConfig();

    // Main properties
    try {
      this.config = loadAndMergeConfig(defaultConfig, configFilename, true);
    } catch (Exception e) {
      try {
        File file = new File("");
        String courseFile = file.getCanonicalPath();
        File fileconfig = new File(courseFile + "\\" + configFilename);
        File wrongfileconfig = new File(courseFile + "\\" + "config_wrong.txt");
        copyFile(fileconfig, wrongfileconfig);
      } catch (Exception ex) {
      }
      this.config = loadAndMergeConfigdef(defaultConfig, configFilename, true);
    }
    // Persisted properties
    this.persisted = loadAndMergeConfig(persistConfig, persistFilename, false);

    leelazConfig = config.getJSONObject("leelaz");
    uiConfig = config.getJSONObject("ui");
    persistedUi = persisted.getJSONObject("ui-persist");

    theme = new Theme(uiConfig);

    // showBorder = uiConfig.optBoolean("show-border", false);
    showMoveNumber = uiConfig.getBoolean("show-move-number");
    onlyLastMoveNumber = uiConfig.optInt("only-last-move-number");

    allowMoveNumber = showMoveNumber ? (onlyLastMoveNumber > 0 ? onlyLastMoveNumber : -1) : 0;
    allowMoveNumber = uiConfig.optInt("allow-move-number", allowMoveNumber);
    newMoveNumberInBranch = uiConfig.optBoolean("new-move-number-in-branch", true);
    showStatus = uiConfig.getBoolean("show-status");
    showBranch = uiConfig.getBoolean("show-leelaz-variation");
    showWinrate = uiConfig.getBoolean("show-winrate");
    largeWinrate = uiConfig.optBoolean("large-winrate", false);
    showBlunderBar = uiConfig.optBoolean("show-blunder-bar", true);
    weightedBlunderBarHeight = uiConfig.optBoolean("weighted-blunder-bar-height", false);
    // dynamicWinrateGraphWidth = uiConfig.optBoolean("dynamic-winrate-graph-width", true);
    showVariationGraph = uiConfig.getBoolean("show-variation-graph");
    showComment = uiConfig.optBoolean("show-comment", true);
    showCaptured = uiConfig.getBoolean("show-captured");
    // showKataGoScoreMean = uiConfig.optBoolean("show-katago-scoremean", true);
    showKataGoBoardScoreMean = uiConfig.optBoolean("show-katago-boardscoremean", false);
    kataGoScoreMeanAlwaysBlack = uiConfig.optBoolean("katago-scoremean-alwaysblack", false);
    showHeat = uiConfig.optBoolean("show-heat", false);
    showHeatAfterCalc = uiConfig.optBoolean("show-heat-aftercalc", false);
    showMoveNumberFromOne = uiConfig.optBoolean("movenumber-from-one", false);
    // kataGoNotShowWinrate = uiConfig.optBoolean("katago-notshow-winrate", false);
    showKataGoEstimate = uiConfig.optBoolean("show-katago-estimate", false);
    scoreMeanWinrateGraphBoard = uiConfig.optBoolean("scoremean-winrategraph-board", false);
    showSuggestionOrder = uiConfig.optBoolean("show-suggestion-order", true);
    showSuggestionMaxRed = uiConfig.optBoolean("show-suggestion-maxred", true);
    showSuggestionVaritions = uiConfig.optBoolean("show-suggestion-varitions", true);
    showKataGoEstimateBySize = uiConfig.optBoolean("show-katago-estimate-bysize", false);
    allowDrageDoubleClick = uiConfig.optBoolean("allow-drag-doubleclick", true);
    showKataGoEstimateOnSubbord = uiConfig.optBoolean("show-katago-estimate-onsubbord", true);
    showKataGoEstimateOnMainbord = uiConfig.optBoolean("show-katago-estimate-onmainboard", true);
    // showBestMoves = uiConfig.getBoolean("show-best-moves");
    showNextMoves = uiConfig.getBoolean("show-next-moves");
    showSubBoard = uiConfig.getBoolean("show-subboard");
    largeSubBoard = uiConfig.getBoolean("large-subboard");
    // handicapInsteadOfWinrate =
    // uiConfig.getBoolean("handicap-instead-of-winrate");
    // showDynamicKomi = uiConfig.getBoolean("show-dynamic-komi");
    showWinrateInSuggestion = uiConfig.optBoolean("show-winrate-in-suggestion", true);
    showPlayoutsInSuggestion = uiConfig.optBoolean("show-playouts-in-suggestion", true);
    showScoremeanInSuggestion = uiConfig.optBoolean("show-scoremean-in-suggestion", true);
    showNameInBoard = uiConfig.optBoolean("show-name-in-board", true);
    openHtmlOnLive = uiConfig.optBoolean("open-html-onlive", false);
    readKomi = uiConfig.optBoolean("read-komi", false);
    alwaysGotoLastOnLive = uiConfig.optBoolean("always-gotolast-onlive", false);
    alwaysSyncBoardStat = uiConfig.optBoolean("always-sync-boardstat", false);
    readBoardArg1 = uiConfig.optString("read-board-arg1", "0");
    readBoardArg2 = uiConfig.optInt("read-board-arg2", 300);
    readBoardArg3 = uiConfig.optBoolean("read-board-arg3", true);
    playSound = uiConfig.optBoolean("play-sound", true);
    noRefreshOnMouseMove = uiConfig.optBoolean("norefresh-onmouse-move", false);
    showEditbar = uiConfig.optBoolean("show-edit-bar", true);
    badmovesalwaysontop = uiConfig.optBoolean("badmoves-always-ontop", false);
    mainsalwaysontop = uiConfig.optBoolean("mains-always-ontop", false);
    suggestionsalwaysontop = uiConfig.optBoolean("suggestions-always-ontop", false);
    appendWinrateToComment = uiConfig.optBoolean("append-winrate-to-comment");
    showCoordinates = uiConfig.optBoolean("show-coordinates");
    replayBranchIntervalSeconds = uiConfig.optDouble("replay-branch-interval-seconds", 0.3);
    colorByWinrateInsteadOfVisits = uiConfig.optBoolean("color-by-winrate-instead-of-visits");
    boardPositionProportion = uiConfig.optInt("board-postion-proportion", 4);
    limitBranchLength = leelazConfig.optInt("limit-branch-length", 0);
    limitMaxSuggestion = leelazConfig.optInt("limit-max-suggestion", 0);
    limitbadmoves = leelazConfig.optInt("badmoves-winrate-limits", 0);
    showNoSuggCircle = leelazConfig.optBoolean("show-nosugg-circle", true);
    limitbadMeanmoves = leelazConfig.optInt("badmoves-scoremean-limits", 0);
    limitbadplayouts = leelazConfig.optInt("badmoves-playouts-limits", 0);

    firstButton = uiConfig.optBoolean("firstButton", true);
    lastButton = uiConfig.optBoolean("lastButton", true);
    clearButton = uiConfig.optBoolean("clearButton", true);
    countButton = uiConfig.optBoolean("countButton", true);
    forward10 = uiConfig.optBoolean("forward10", true);
    backward10 = uiConfig.optBoolean("backward10", true);
    forward1 = uiConfig.optBoolean("forward1", true);
    gotomove = uiConfig.optBoolean("gotomove", true);
    backward1 = uiConfig.optBoolean("backward1", true);
    openfile = uiConfig.optBoolean("openfile", true);
    savefile = uiConfig.optBoolean("savefile", true);
    analyse = uiConfig.optBoolean("analyse", true);
    kataEstimate = uiConfig.optBoolean("kataEstimate", true);
    heatMap = uiConfig.optBoolean("heatMap", true);
    backMain = uiConfig.optBoolean("backMain", true);
    setMain = uiConfig.optBoolean("setMain", true);
    batchOpen = uiConfig.optBoolean("batchOpen", true);
    refresh = uiConfig.optBoolean("refresh", true);
    tryPlay = uiConfig.optBoolean("tryPlay", true);
    komi = uiConfig.optBoolean("komi", true);
    move = uiConfig.optBoolean("move", true);
    coords = uiConfig.optBoolean("coords", true);
    autoPlay = uiConfig.optBoolean("autoPlay", true);
    liveButton = uiConfig.optBoolean("liveButton", true);
    badMoves = uiConfig.optBoolean("badMoves", true);

    winrateStrokeWidth = theme.winrateStrokeWidth();
    minimumBlunderBarWidth = theme.minimumBlunderBarWidth();
    shadowSize = theme.shadowSize();
    showlcbwinrate = config.getJSONObject("leelaz").optBoolean("show-lcb-winrate", false);
    playponder = config.getJSONObject("leelaz").optBoolean("play-ponder", true);
    showrect = config.getJSONObject("leelaz").optBoolean("show-rect", true);
    loadZen = config.getJSONObject("leelaz").optBoolean("load-zen", false);
    showlcbcolor = config.getJSONObject("leelaz").optBoolean("show-lcb-color", true);
    fastChange = config.getJSONObject("leelaz").optBoolean("fast-engine-change", true);
    maxAnalyzeTimeMillis =
        60 * 1000 * config.getJSONObject("leelaz").getInt("max-analyze-time-minutes");
    if (maxAnalyzeTimeMillis == 0) {
      maxAnalyzeTimeMillis = 9999 * 60 * 1000;
    }
    analyzeUpdateIntervalCentisec =
        config.getJSONObject("leelaz").optInt("analyze-update-interval-centisec", 10);
    analyzeUpdateIntervalCentisecSSH =
        config.getJSONObject("leelaz").optInt("analyze-update-interval-centisecssh", 50);
    if (theme.fontName() != null) fontName = theme.fontName();
    else fontName = "微软雅黑";

    if (theme.uiFontName() != null) uiFontName = theme.uiFontName();

    if (theme.winrateFontName() != null) winrateFontName = theme.winrateFontName();

    commentFontSize = theme.commentFontSize();
    commentFontColor = theme.commentFontColor();
    commentBackgroundColor = theme.commentBackgroundColor();
    winrateLineColor = theme.winrateLineColor();
    scoreMeanLineColor = theme.scoreMeanLineColor();
    Color bestColor = theme.bestMoveColor();
    stoneIndicatorType = theme.stoneIndicatorType();
    bestMoveColor =
        Color.RGBtoHSB(bestColor.getRed(), bestColor.getGreen(), bestColor.getBlue(), null)[0];
    winrateMissLineColor = theme.winrateMissLineColor();
    blunderBarColor = theme.blunderBarColor();
    solidStoneIndicator = theme.solidStoneIndicator();
    showCommentNodeColor = theme.showCommentNodeColor();
    commentNodeColor = theme.commentNodeColor();
    blunderWinrateThresholds = theme.blunderWinrateThresholds();
    blunderNodeColors = theme.blunderNodeColors();
    nodeColorMode = theme.nodeColorMode();

    gtpConsoleStyle = uiConfig.optString("gtp-console-style", defaultGtpConsoleStyle);

    System.out.println(Locale.getDefault().getLanguage()); // todo add config option for language...
    setLanguage(Locale.getDefault().getLanguage());
  }

  private File File(String fileold) {
    // TODO Auto-generated method stub
    return null;
  }

  // Modifies config by adding in values from default_config that are missing.
  // Returns whether it added anything.
  public boolean mergeDefaults(JSONObject config, JSONObject defaultsConfig) {
    boolean modified = false;
    Iterator<String> keys = defaultsConfig.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      Object newVal = defaultsConfig.get(key);
      if (newVal instanceof JSONObject) {
        if (!config.has(key)) {
          config.put(key, new JSONObject());
          modified = true;
        }
        Object oldVal = config.get(key);
        modified |= mergeDefaults((JSONObject) oldVal, (JSONObject) newVal);
      } else {
        if (!config.has(key)) {
          config.put(key, newVal);
          modified = true;
        }
      }
    }
    return modified;
  }

  public void toggleShowLcbWinrate() {
    this.showlcbwinrate = !this.showlcbwinrate;
  }

  public void toggleappendWinrateToComment() {
    this.appendWinrateToComment = !appendWinrateToComment;
    Lizzie.config.uiConfig.put("append-winrate-to-comment", appendWinrateToComment);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowMoveNumber() {
    onlyLastMoveNumber = 1;
    if (this.onlyLastMoveNumber > 0) {
      allowMoveNumber =
          (allowMoveNumber == -1 ? onlyLastMoveNumber : (allowMoveNumber == 0 ? -1 : 0));
    } else {
      allowMoveNumber = (allowMoveNumber == 0 ? -1 : 0);
    }
    Lizzie.config.uiConfig.put("allow-move-number", allowMoveNumber);
    try {
      Lizzie.config.save();
    } catch (IOException es) {
    }
  }

  public void toggleNodeColorMode() {
    this.nodeColorMode = this.nodeColorMode > 1 ? 0 : this.nodeColorMode + 1;
  }

  public void toggleShowBranch() {
    this.showBranch = !this.showBranch;
  }

  public void toggleShowWinrate() {
    this.showWinrate = !this.showWinrate;
    Lizzie.config.uiConfig.put("show-winrate", showWinrate);
    Lizzie.frame.redrawBackgroundAnyway = true;
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleLargeWinrateOnly() {
    this.largeWinrate = !this.largeWinrate;
    Lizzie.config.uiConfig.put("large-winrate", largeWinrate);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleLargeWinrate() {
    if (this.largeSubBoard) {
      toggleLargeSubBoard();
      this.largeWinrate = true;
    } else {
      this.largeWinrate = !this.largeWinrate;
    }
    Lizzie.config.uiConfig.put("large-winrate", largeWinrate);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowVariationGraph() {
    this.showVariationGraph = !this.showVariationGraph;
    Lizzie.frame.refreshBackground();
    Lizzie.config.uiConfig.put("show-variation-graph", showVariationGraph);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowCaptured() {
    this.showCaptured = !this.showCaptured;
    Lizzie.config.uiConfig.put("show-captured", showCaptured);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowComment() {
    this.showComment = !this.showComment;
    Lizzie.config.uiConfig.put("show-comment", showComment);
    Lizzie.frame.refreshBackground();
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowStatus() {
    this.showStatus = !this.showStatus;
    Lizzie.config.uiConfig.put("show-status", showStatus);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowCommentNodeColor() {
    this.showCommentNodeColor = !this.showCommentNodeColor;
  }

  // public void toggleShowBestMoves() {
  // this.showBestMoves = !this.showBestMoves;
  // }

  public void toggleShowNextMoves() {
    this.showNextMoves = !this.showNextMoves;
  }

  // public void toggleHandicapInsteadOfWinrate() {
  // this.handicapInsteadOfWinrate = !this.handicapInsteadOfWinrate;
  // }

  public void toggleLargeSubBoard() {
    this.largeSubBoard = !this.largeSubBoard;
    Lizzie.frame.redrawBackgroundAnyway = true;
    try {
      Lizzie.config.uiConfig.put("large-subboard", largeSubBoard);
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleCoordinates() {
    showCoordinates = !showCoordinates;
    try {
      Lizzie.config.uiConfig.put("show-coordinates", showCoordinates);
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleShowSubBoard() {
    showSubBoard = !showSubBoard;
    Lizzie.frame.refreshBackground();
    try {
      Lizzie.config.uiConfig.put("show-subboard", showSubBoard);
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleEvaluationColoring() {
    if (leelaversion < 17) {
      return;
    }
    showlcbcolor = !showlcbcolor;
  }

  public boolean showLargeSubBoard() {
    return showSubBoard && largeSubBoard;
  }

  public boolean showLargeWinrate() {
    return showWinrate && largeWinrate;
  }

  public boolean showLargeWinrateOnly() {
    return largeWinrate;
  }

  public boolean showBestMovesNow() {
    return showBestMoves || showBestMovesTemporarily;
  }

  public boolean showBranchNow() {
    return showBranch || showBestMovesTemporarily;
  }

  /**
   * Scans the current directory as well as the current PATH to find a reasonable default leelaz
   * binary.
   *
   * @return A working path to a leelaz binary. If there are none on the PATH, "./leelaz" is
   *     returned for backwards compatibility.
   */
  public static String getBestDefaultLeelazPath() {
    List<String> potentialPaths = new ArrayList<>();
    potentialPaths.add(".");
    potentialPaths.addAll(Arrays.asList(System.getenv("PATH").split(":")));

    for (String potentialPath : potentialPaths) {
      for (String potentialExtension : Arrays.asList(new String[] {"", ".exe"})) {
        File potentialLeelaz = new File(potentialPath, "leelaz" + potentialExtension);
        if (potentialLeelaz.exists() && potentialLeelaz.canExecute()) {
          return potentialLeelaz.getPath();
        }
      }
    }

    return "./leelaz";
  }

  private JSONObject createDefaultConfig() {
    JSONObject config = new JSONObject();

    // About engine parameter
    JSONObject leelaz = new JSONObject();
    leelaz.put("network-file", "network.gz");
    leelaz.put(
        "engine-command",
        String.format(
            "%s --gtp --lagbuffer 0 --weights %%network-file", getBestDefaultLeelazPath()));
    leelaz.put("engine-start-location", ".");
    leelaz.put("max-analyze-time-minutes", 10);
    leelaz.put("limit-max-suggestion", 0);
    leelaz.put("limit-branch-length", 0);
    leelaz.put("badmoves-winrate-limits", 0);
    leelaz.put("badmoves-playouts-limits", 0);

    leelaz.put("max-game-thinking-time-seconds", 2);
    leelaz.put("print-comms", true);
    leelaz.put("analyze-update-interval-centisec", 10);
    leelaz.put("show-lcb-winrate", false);
    leelaz.put("show-lcb-color", true);
    leelaz.put("leela-version", 17);
    config.put("leelaz", leelaz);

    // About User Interface display
    JSONObject ui = new JSONObject();

    ui.put("board-color", new JSONArray("[217, 152, 77]"));
    ui.put("shadows-enabled", true);

    ui.put("fancy-stones", true);
    ui.put("fancy-board", true);
    ui.put("shadow-size", 100);
    ui.put("show-move-number", false);
    ui.put("show-status", true);
    ui.put("show-leelaz-variation", true);
    ui.put("show-winrate", true);
    ui.put("large-winrate", false);
    ui.put("winrate-stroke-width", 2);
    ui.put("show-blunder-bar", true);
    ui.put("minimum-blunder-bar-width", 3);
    ui.put("weighted-blunder-bar-height", false);
    // ui.put("dynamic-winrate-graph-width", true);
    ui.put("show-comment", true);
    ui.put("comment-font-size", 0);
    ui.put("show-variation-graph", true);
    ui.put("show-captured", true);
    ui.put("show-best-moves", true);
    ui.put("show-coordinates", true);
    ui.put("show-next-moves", true);
    ui.put("show-subboard", true);
    ui.put("large-subboard", false);
    ui.put("win-rate-always-black", false);
    ui.put("confirm-exit", false);
    ui.put("resume-previous-game", false);
    ui.put("autosave-interval-seconds", -1);
    ui.put("handicap-instead-of-winrate", false);
    ui.put("board-size", 19);
    ui.put("show-dynamic-komi", false);
    // ui.put("min-playout-ratio-for-stats", 0.0);
    ui.put("theme", "default");
    ui.put("only-last-move-number", 1);
    ui.put("new-move-number-in-branch", true);
    ui.put("append-winrate-to-comment", true);
    ui.put("replay-branch-interval-seconds", 0.3);
    ui.put("gtp-console-style", defaultGtpConsoleStyle);
    config.put("ui", ui);
    return config;
  }

  private JSONObject createPersistConfig() {
    JSONObject config = new JSONObject();

    // About engine parameter
    JSONObject filesys = new JSONObject();
    filesys.put("last-folder", "");

    config.put("filesystem", filesys);

    // About autosave
    config.put("autosave", "");

    // About User Interface display
    JSONObject ui = new JSONObject();

    // ui.put("window-height", 657);
    // ui.put("window-width", 687);
    // ui.put("max-alpha", 240);

    // Main Window Position & Size
    ui.put("main-window-position", new JSONArray("[]"));
    ui.put("gtp-console-position", new JSONArray("[]"));
    ui.put("window-maximized", false);

    config.put("filesystem", filesys);

    // Avoid the key "ui" because it was used to distinguish "config" and "persist"
    // in old version of validateAndCorrectSettings().
    // If we use "ui" here, we will have trouble to run old lizzie.
    config.put("ui-persist", ui);
    return config;
  }

  private void writeConfig(JSONObject config, File file) throws IOException, JSONException {
    file.createNewFile();

    FileOutputStream fp = new FileOutputStream(file);
    OutputStreamWriter writer = new OutputStreamWriter(fp);

    writer.write(config.toString(2));

    writer.close();
    fp.close();
  }

  public void persist() throws IOException {
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;

    JSONArray mainPos = new JSONArray();
    if (!windowIsMaximized) {
      mainPos.put(Lizzie.frame.getX());
      mainPos.put(Lizzie.frame.getY());
      mainPos.put(Lizzie.frame.getWidth());
      mainPos.put(Lizzie.frame.getHeight());
      mainPos.put(Lizzie.frame.toolbarHeight);
      mainPos.put(Lizzie.frame.bowserX);
      mainPos.put(Lizzie.frame.bowserY);
      mainPos.put(Lizzie.frame.bowserWidth);
      mainPos.put(Lizzie.frame.bowserHeight);
    } else {
      mainPos.put(Lizzie.frame.toolbarHeight);
      mainPos.put(Lizzie.frame.bowserX);
      mainPos.put(Lizzie.frame.bowserY);
      mainPos.put(Lizzie.frame.bowserWidth);
      mainPos.put(Lizzie.frame.bowserHeight);
    }
    persistedUi.put("main-window-position", mainPos);
    JSONArray gtpPos = new JSONArray();
    gtpPos.put(Lizzie.gtpConsole.getX());
    gtpPos.put(Lizzie.gtpConsole.getY());
    gtpPos.put(Lizzie.gtpConsole.getWidth());
    gtpPos.put(Lizzie.gtpConsole.getHeight());
    persistedUi.put("gtp-console-position", gtpPos);
    persistedUi.put("board-postion-propotion", Lizzie.frame.BoardPositionProportion);
    persistedUi.put("window-maximized", windowIsMaximized);

    if (Lizzie.frame.analysisFrame != null) {
      JSONArray suggestionlistPos = new JSONArray();
      suggestionlistPos.put(Lizzie.frame.analysisFrame.getX());
      suggestionlistPos.put(Lizzie.frame.analysisFrame.getY());
      suggestionlistPos.put(Lizzie.frame.analysisFrame.getWidth());
      suggestionlistPos.put(Lizzie.frame.analysisFrame.getHeight());
      suggestionlistPos.put(
          featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(0).getWidth());
      suggestionlistPos.put(
          featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(1).getWidth());
      suggestionlistPos.put(
          featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(2).getWidth());
      suggestionlistPos.put(
          featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(3).getWidth());
      suggestionlistPos.put(
          featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(4).getWidth());
      if (featurecat.lizzie.gui.AnalysisFrame.table.getColumnCount() == 7) {
        suggestionlistPos.put(
            featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(5).getWidth());
        suggestionlistPos.put(
            featurecat.lizzie.gui.AnalysisFrame.table.getColumnModel().getColumn(6).getWidth());
      }
      persistedUi.put("suggestions-list-position", suggestionlistPos);
    } else {
      boolean persisted = Lizzie.config.persistedUi != null;
      if (persisted
          && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position") != null) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position");
        persistedUi.put("suggestions-list-position", pos);
      }
    }
    if (Lizzie.movelistframe != null) {
      JSONArray badmoveslistPos = new JSONArray();

      badmoveslistPos.put(Lizzie.movelistFrame.sortnum);
      badmoveslistPos.put(Lizzie.movelistframe.getX());
      badmoveslistPos.put(Lizzie.movelistframe.getY());
      badmoveslistPos.put(Lizzie.movelistframe.getWidth());
      badmoveslistPos.put(Lizzie.movelistframe.getHeight());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(0).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(1).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(2).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(3).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(4).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(5).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(6).getWidth());
      badmoveslistPos.put(
          featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(7).getWidth());
      if (featurecat.lizzie.gui.MovelistFrame.table.getColumnCount() == 9) {
        badmoveslistPos.put(
            featurecat.lizzie.gui.MovelistFrame.table.getColumnModel().getColumn(8).getWidth());
      }
      persistedUi.put("badmoves-list-position", badmoveslistPos);
    } else {
      boolean persisted = Lizzie.config.persistedUi != null;
      if (persisted && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
        persistedUi.put("badmoves-list-position", pos);
      }
    }

    JSONArray toolbarParameter = new JSONArray();
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtFirstAnaMove.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtLastAnaMove.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAnaTime.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAnaTime.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAnaAutoSave.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkAnaPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAnaPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAnaFirstPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAnaFirstPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkAutoPlayWhite.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkAutoPlayTime.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayTime.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAutoPlayPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAutoPlayFirstPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(
          Integer.parseInt(Lizzie.frame.toolbar.txtAutoPlayFirstPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(
          Integer.parseInt(Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(
          Integer.parseInt(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtenginePkTime.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtenginePkPlayputs.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(
          Integer.parseInt(Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtenginePkBatch.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkenginePkBatch.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkenginePkContinue.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkenginePkFirstPlayputs.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkenginePkPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (Lizzie.frame.toolbar.chkenginePkTime.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    toolbarParameter.put(Lizzie.frame.toolbar.pkResginWinrate);
    toolbarParameter.put(Lizzie.frame.toolbar.pkResignMoveCounts);
    toolbarParameter.put(Lizzie.frame.toolbar.AutosavePk);
    toolbarParameter.put(Lizzie.frame.toolbar.isGenmove);
    toolbarParameter.put(Lizzie.frame.toolbar.anaPanelOrder);
    toolbarParameter.put(Lizzie.frame.toolbar.enginePkOrder);
    toolbarParameter.put(Lizzie.frame.toolbar.autoPlayOrder);
    toolbarParameter.put(Lizzie.frame.toolbar.exChange);
    toolbarParameter.put(Lizzie.frame.toolbar.maxGanmeMove);
    toolbarParameter.put(Lizzie.frame.toolbar.checkGameMaxMove);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtenginePkTimeWhite.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (Lizzie.frame.toolbar.chkAutoSub.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAutoMain.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(Integer.parseInt(Lizzie.frame.toolbar.txtAutoSub.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    toolbarParameter.put(Lizzie.frame.toolbar.minGanmeMove);
    toolbarParameter.put(Lizzie.frame.toolbar.checkGameMinMove);
    toolbarParameter.put(Lizzie.frame.toolbar.isRandomMove);
    toolbarParameter.put(Lizzie.frame.toolbar.randomMove);
    toolbarParameter.put(Lizzie.frame.toolbar.randomDiffWinrate);
    toolbarParameter.put(Lizzie.frame.toolbar.chkAnaBlack.isSelected());
    toolbarParameter.put(Lizzie.frame.toolbar.chkAnaWhite.isSelected());
    toolbarParameter.put(Lizzie.frame.toolbar.enginePkSaveWinrate);
    toolbarParameter.put(Lizzie.frame.toolbar.rightMode);
    persistedUi.put("toolbar-parameter", toolbarParameter);

    JSONArray movecountPos = new JSONArray();
    movecountPos.put(Lizzie.countResults.getX());
    movecountPos.put(Lizzie.countResults.getY());
    persistedUi.put("movecount-position", movecountPos);

    JSONArray winrateGraph = new JSONArray();
    winrateGraph.put(Lizzie.frame.winrateGraph.mode);
    persistedUi.put("winrate-graph", winrateGraph);

    writeConfig(this.persisted, new File(persistFilename));
  }

  public void save() throws IOException {
    writeConfig(this.config, new File(configFilename));
  }

  public void setLanguage(String code) {
    // currently will not set the resource bundle. TODO.
    if (code.equals("ko")) {
      // korean
      if (fontName == null) {
        fontName = "Malgun Gothic";
      }
      if (uiFontName == null) {
        uiFontName = "Malgun Gothic";
      }
      winrateFontName = null;
    }
  }
}
