package featurecat.lizzie.analysis;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.AnalysisFrame;
import featurecat.lizzie.gui.EngineData;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.SGFParser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.swing.Timer;
import org.json.JSONArray;
import org.json.JSONException;

public class EngineManager {

  public List<Leelaz> engineList;
  public static int currentEngineNo;
  public long startInfoTime = System.currentTimeMillis();
  public long gameTime = System.currentTimeMillis();
  public static boolean isEmpty = false;

  Timer timer;
  Timer timer2;
  Timer timer3;
  // Timer timer4;

  public EngineManager(Config config, int index) throws JSONException, IOException {
    ArrayList<EngineData> engineData = getEngineData();
    if (index > engineData.size() - 1) {
      index = 0;
    }
    // 先做到这,后续处理根据index加载引擎,传递棋盘大小,贴目,以及不加载引擎的办法
    // JSONObject eCfg = config.config.getJSONObject("leelaz");
    // String engineCommand = eCfg.getString("engine-command");
    // // substitute in the weights file
    // engineCommand = engineCommand.replaceAll("%network-file",
    // eCfg.getString("network-file"));
    //
    // // Start default engine
    // Leelaz lz = new Leelaz(engineCommand);
    // Lizzie.leelaz = lz;
    // Lizzie.board = lz.board;
    // if (engineCommand.equals("")) {
    // Lizzie.frame.openConfigDialog();
    // System.exit(1);
    // }
    // lz.startEngine(0);
    // lz.preload = true;
    engineList = new ArrayList<Leelaz>();
    // engineList.add(lz);

    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      Leelaz e;
      e = new Leelaz(engineDt.commands);
      e.preload = engineDt.preload;
      e.width = engineDt.width;
      e.height = engineDt.height;
      e.komi = engineDt.komi;
      if (i == index) {
        Lizzie.leelaz = e;
        e.preload = true;
        e.firstLoad = true;
        e.startEngine(engineDt.index);
      } else {
        if (e.preload) {
          new Thread() {
            public void run() {
              try {
                e.startEngine(engineDt.index);
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }.start();
        }
      }
      engineList.add(e);
    }
    currentEngineNo = index;
    if (index == -1) {
      Lizzie.leelaz = new Leelaz("");
      Lizzie.leelaz.isLoaded = true;
      featurecat.lizzie.gui.Menu.engineMenu.setText("未加载引擎");
      isEmpty = true;
      Lizzie.frame.addInput();
    } else if (!isEmpty) {
      try {
        featurecat.lizzie.gui.Menu.engineMenu.setText(
            "引擎" + (index + 1) + ": " + engineList.get(index).currentEnginename);
      } catch (Exception ex) {
      }
    }
    Lizzie.gtpConsole.console.setText("");
    // new Thread(
    // () -> {
    // // Process other engine
    // Optional<JSONArray> enginesOpt =
    // Optional.ofNullable(
    // Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
    // Optional<JSONArray> enginePreloadOpt =
    // Optional.ofNullable(
    // Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));
    // enginesOpt.ifPresent(
    // m -> {
    // IntStream.range(0, m.length())
    // .forEach(
    // i -> {
    // String cmd = m.optString(i);
    // if (cmd != null && !cmd.isEmpty()) {
    // Leelaz e;
    // try {
    // e = new Leelaz(cmd);
    // // TODO: how sync the board
    // // e.board = Lizzie.board;
    // e.preload =
    // enginePreloadOpt.map(p ->
    // p.optBoolean(i)).orElse(false);
    // if (e.preload) {
    // e.startEngine(i + 1);
    // }
    // // TODO: Need keep analyze?
    // // e.togglePonder();
    // engineList.add(e);
    // } catch (JSONException | IOException e1) {
    // e1.printStackTrace();
    // }
    //
    // } else {
    // // empty
    // engineList.add(null);
    // }
    // });
    // });
    // })
    // .start();

    timer =
        new Timer(
            5000,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                checkEngineAlive();
                try {
                } catch (Exception e) {
                }
              }
            });
    timer.start();
  }

  public ArrayList<EngineData> getEngineData() {
    ArrayList<EngineData> engineData = new ArrayList<EngineData>();
    Optional<JSONArray> enginesCommandOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
    Optional<JSONArray> enginesPreloadOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));

    Optional<JSONArray> enginesWidthOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-width-list"));

    Optional<JSONArray> enginesHeightOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-height-list"));
    Optional<JSONArray> enginesKomiOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-komi-list"));

    int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);

    for (int i = 0;
        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() + 1 : 0);
        i++) {
      if (i == 0) {
        String engineCommand = Lizzie.config.leelazConfig.getString("engine-command");
        int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
        int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
        float komi =
            enginesKomiOpt.isPresent()
                ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
                : (float) 7.5;
        boolean preload =
            enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) : false;
        EngineData enginedt = new EngineData();
        enginedt.commands = engineCommand;
        enginedt.name = name;
        enginedt.preload = preload;
        enginedt.index = i;
        enginedt.width = width;
        enginedt.height = height;
        enginedt.komi = komi;
        if (defaultEngine == i) enginedt.isDefault = true;
        else enginedt.isDefault = false;
        engineData.add(enginedt);
      } else {
        String commands =
            enginesCommandOpt.isPresent() ? enginesCommandOpt.get().optString(i - 1, "") : "";
        if (!commands.equals("")) {
          int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
          int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
          String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
          float komi =
              enginesKomiOpt.isPresent()
                  ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
                  : (float) 7.5;
          boolean preload =
              enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) : false;
          EngineData enginedt = new EngineData();
          enginedt.commands = commands;
          enginedt.name = name;
          enginedt.preload = preload;
          enginedt.index = i;
          enginedt.width = width;
          enginedt.height = height;
          enginedt.komi = komi;
          if (defaultEngine == i) enginedt.isDefault = true;
          else enginedt.isDefault = false;
          engineData.add(enginedt);
        }
      }
    }
    return engineData;
  }

  private void checkEngineNotHang() {
    if (Lizzie.frame.toolbar.isEnginePk
        && !Lizzie.frame.toolbar.isPkStop
        && System.currentTimeMillis() - startInfoTime > 1000 * 240) {
      Lizzie.leelaz.process.destroy();
      startInfoTime = System.currentTimeMillis();
    }
    try {
      timer3.stop();
      timer3 = null;
    } catch (Exception ex) {

    }
  }

  private void checkEngineAlive() {
    if (isEmpty) return;
    if (Lizzie.frame.toolbar.checkGameTime
        && Lizzie.frame.toolbar.isEnginePk
        && !Lizzie.frame.toolbar.isPkStop) {
      if (System.currentTimeMillis() - gameTime > Lizzie.frame.toolbar.maxGanmeTime * 60 * 1000) {
        saveTimeoutFile();
        Lizzie.board.clear();
        this.engineList.get(Lizzie.frame.toolbar.engineBlack).clearWithoutPonder();
        this.engineList.get(Lizzie.frame.toolbar.engineWhite).clearWithoutPonder();
        this.engineList.get(Lizzie.frame.toolbar.engineBlack).ponder();
        // forcekillAllEngines();
        gameTime = System.currentTimeMillis();
      }
    }
    if (Lizzie.frame.toolbar.isEnginePk) {
      // {
      // // if (Lizzie.leelaz.resigned) Lizzie.leelaz.pkResign();
      // if (Lizzie.leelaz.isPondering()) {
      // timer3 =
      // new Timer(
      // 5000,
      // new ActionListener() {
      // public void actionPerformed(ActionEvent evt) {
      // checkEngineNotHang();
      //
      // try {
      // } catch (Exception e) {
      // }
      // }
      // });
      // timer3.start();
      // }
      // }
      timer2 =
          new Timer(
              5000,
              new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                  checkEnginePK();

                  try {
                  } catch (Exception e) {
                  }
                }
              });
      timer2.start();

    } else {
      if (engineList.get(currentEngineNo).process != null
          && engineList.get(currentEngineNo).process.isAlive()) {
      } else {
        try {
          engineList.get(currentEngineNo).restartClosedEngine(currentEngineNo);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  private void checkEnginePK() {
    if (!Lizzie.frame.toolbar.isEnginePk) {
      return;
    }
    if (engineList.get(Lizzie.frame.toolbar.engineBlack).process != null
        && engineList.get(Lizzie.frame.toolbar.engineBlack).process.isAlive()) {
    } else {
      try {
        restartEngineForPk(Lizzie.frame.toolbar.engineBlack);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (engineList.get(Lizzie.frame.toolbar.engineWhite).process != null
        && engineList.get(Lizzie.frame.toolbar.engineWhite).process.isAlive()) {
    } else {
      try {
        restartEngineForPk(Lizzie.frame.toolbar.engineWhite);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    try {
      timer2.stop();
      timer2 = null;
    } catch (Exception ex) {

    }
  }

  public void updateEngines() {
    ArrayList<EngineData> engineData = getEngineData();
    // JSONObject config;
    // config = Lizzie.config.config.getJSONObject("leelaz");
    // String engineCommand;
    // engineCommand = config.getString("engine-command");
    // engineCommand = engineCommand.replaceAll("%network-file",
    // config.getString("network-file"));
    // engineList.get(0).engineCommand = engineCommand;
    // Optional<JSONArray> enginesOpt =
    // Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);

      if (i < engineList.size()) {
        engineList.get(i).engineCommand = engineDt.commands;
        engineList.get(i).width = engineDt.width;
        engineList.get(i).height = engineDt.height;
        engineList.get(i).komi = engineDt.komi;

      } else {
        Leelaz e;
        try {
          e = new Leelaz(engineDt.commands);
          e.width = engineDt.width;
          e.height = engineDt.height;
          e.komi = engineDt.komi;
          engineList.add(e);
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
    int j = Lizzie.frame.toolbar.enginePkBlack.getItemCount();
    Lizzie.frame.toolbar.removeEngineLis();
    for (int i = 0; i < j; i++) {
      Lizzie.frame.toolbar.enginePkBlack.removeItemAt(0);
      Lizzie.frame.toolbar.enginePkWhite.removeItemAt(0);
    }
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      Lizzie.frame.toolbar.enginePkBlack.addItem("[" + (i + 1) + "]" + engineDt.name);
      Lizzie.frame.toolbar.enginePkWhite.addItem("[" + (i + 1) + "]" + engineDt.name);
    }
    Lizzie.frame.toolbar.addEngineLis();
    Lizzie.frame.menu.updateEngineMenu();
  }

  public void killAllEngines() {
    // currentEngineNo = -1;
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        engineList.get(i).normalQuit();
      }
    }
  }

  public void forcekillAllEngines() {
    // currentEngineNo = -1;
    for (int i = 0; i < engineList.size(); i++) {
      try {
        engineList.get(i).started = false;
        engineList.get(i).normalQuit();
        engineList.get(i).process.destroyForcibly();
      } catch (Exception e) {
      }
    }
  }

  public void killOtherEngines() {
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        if (i != currentEngineNo) engineList.get(i).normalQuit();
      }
    }
  }

  private void saveTimeoutFile() {
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
                + "超时对局"
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
                + "超时对局"
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

  /**
   * Switch the Engine by index number
   *
   * @param index engine index
   */
  public void startEngineForPkPonder(int index) {
    if (index > this.engineList.size()) return;
    // Lizzie.board.saveMoveNumber();
    Leelaz newEng = engineList.get(index);
    newEng.played = false;
    newEng.blackResignMoveCounts = 0;
    newEng.whiteResignMoveCounts = 0;
    newEng.resigned = false;
    newEng.isResigning = false;
    newEng.width = Lizzie.board.boardWidth;
    newEng.height = Lizzie.board.boardHeight;
    newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
    ArrayList<Movelist> mv = Lizzie.board.getmovelist();
    if (!newEng.isStarted()) {
      try {
        newEng.startEngine(index);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      newEng.boardSize(newEng.width, newEng.height);
      newEng.sendCommand("komi " + newEng.komi);
    }
    // else {newEng.initializeStreams();}
    // Lizzie.leelaz = newEng;
    Lizzie.engineManager.engineList.get(index).clearWithoutPonder();
    // this.currentEngineNo = index;
    // Lizzie.leelaz.notPondering();
    Lizzie.board.restoreMoveNumberPonder(index, mv);
    // Lizzie.leelaz.Pondering();
  }

  public void startEngineForPk(int index) {
    if (index > this.engineList.size()) return;
    // Lizzie.board.saveMoveNumber();
    Leelaz newEng = engineList.get(index);
    newEng.played = false;
    newEng.blackResignMoveCounts = 0;
    newEng.whiteResignMoveCounts = 0;
    newEng.resigned = false;
    newEng.isResigning = false;
    newEng.width = Lizzie.board.boardWidth;
    newEng.height = Lizzie.board.boardHeight;
    newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
    ArrayList<Movelist> mv = Lizzie.board.getmovelist();
    if (!newEng.isStarted()) {
      try {
        newEng.startEngine(index);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      newEng.boardSize(newEng.width, newEng.height);
      newEng.sendCommand("komi " + newEng.komi);
    }
    // else {newEng.initializeStreams();}
    // Lizzie.leelaz = newEng;
    newEng.isResigning = false;
    Lizzie.engineManager.engineList.get(index).clearWithoutPonder();
    // this.currentEngineNo = index;
    // Lizzie.leelaz.notPondering();
    Lizzie.board.restoreMoveNumber(index, mv);
    Lizzie.frame.boardRenderer.removecountblock();
    if (Lizzie.config.showSubBoard) Lizzie.frame.subBoardRenderer.removecountblock();
    // Lizzie.leelaz.Pondering();
  }

  public void restartEngineForPk(int index) {
    if (index > this.engineList.size()) return;
    // Lizzie.board.saveMoveNumber();
    Leelaz newEng = engineList.get(index);
    newEng.played = false;
    newEng.width = Lizzie.board.boardWidth;
    newEng.height = Lizzie.board.boardHeight;
    newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
    ArrayList<Movelist> mv = Lizzie.board.getmovelist();
    // if (!newEng.isStarted()) {
    try {
      newEng.startEngine(index);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // }
    // else {newEng.initializeStreams();}
    // Lizzie.leelaz = newEng;
    // Lizzie.leelaz.clear();
    this.currentEngineNo = index;
    // Lizzie.leelaz.notPondering();
    Lizzie.board.restoreMoveNumber(index, mv);
    // Lizzie.leelaz.Pondering();
  }

  public void switchEngine(int index) {
    if (isEmpty) isEmpty = false;
    if (index > this.engineList.size()) return;
    Leelaz newEng = engineList.get(index);
    if (newEng == null) return;
    boolean changeBoard = true;

    ArrayList<Movelist> mv = Lizzie.board.getmovelist();

    try {
      if (currentEngineNo != -1) {
        Leelaz curEng = engineList.get(this.currentEngineNo);
        curEng.switching = true;
        if (newEng.width == Lizzie.board.boardWidth && newEng.height == Lizzie.board.boardHeight)
          changeBoard = false;
        else {
          newEng.width = Lizzie.board.boardWidth;
          newEng.height = Lizzie.board.boardHeight;
        }
        newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
        try {
          if (!Lizzie.config.fastChange) {
            curEng.normalQuit();
          } else {
            curEng.sendCommand("version");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        curEng.notPondering();
      }
      Lizzie.leelaz = newEng;
      // TODO: how sync the board
      // newEng.board = curEng.board;
      // Lizzie.board = newEng.board;
      if (!newEng.isStarted()) {
        newEng.startEngine(index);
      } else {
        // newEng.getEngineName(index);
        if (changeBoard) newEng.boardSize(newEng.width, newEng.height);
        newEng.sendCommand("komi " + newEng.komi);
        Lizzie.config.leelaversion = newEng.version;
      }
      newEng.sendCommand("clear_board");
      Lizzie.board.restoreMoveNumber(index, mv);
      newEng.ponder();
      Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
      this.currentEngineNo = index;
      featurecat.lizzie.gui.Menu.engineMenu.setText(
          "引擎" + (currentEngineNo + 1) + ": " + engineList.get(currentEngineNo).currentEnginename);

    } catch (IOException e) {
      e.printStackTrace();
    }

    changeEngIco();
    Lizzie.frame.toolbar.reSetButtonLocation();
    if (Lizzie.analysisframe.isVisible()) {
      Lizzie.analysisframe.setVisible(false);
      Lizzie.analysisframe = AnalysisFrame.createAnalysisDialog();
      Lizzie.analysisframe.setVisible(
          Lizzie.config.uiConfig.optBoolean("show-suggestions-frame", true));
      Lizzie.analysisframe.setAlwaysOnTop(Lizzie.config.suggestionsalwaysontop);
      Lizzie.analysisframe.setVisible(true);
    }

    Lizzie.frame.boardRenderer.removecountblock();
    if (Lizzie.config.showSubBoard) Lizzie.frame.subBoardRenderer.removecountblock();
  }

  public void changeEngIcoForEndPk() {
    Lizzie.frame.toolbar.isEnginePk = false;
    featurecat.lizzie.gui.Menu.engineMenu.setEnabled(true);
    if (Lizzie.board.getData().blackToPlay) {
      // switchEngine(Lizzie.frame.toolbar.engineWhite);
      Lizzie.leelaz = engineList.get(Lizzie.frame.toolbar.engineBlack);
      engineList.get(Lizzie.frame.toolbar.engineWhite).nameCmd();

      // switchEngine(Lizzie.frame.toolbar.engineBlack);
    } else {
      // switchEngine(Lizzie.frame.toolbar.engineBlack);
      Lizzie.leelaz = engineList.get(Lizzie.frame.toolbar.engineWhite);
      engineList.get(Lizzie.frame.toolbar.engineBlack).nameCmd();
      // engineList.get(Lizzie.frame.toolbar.engineWhite).clear();
      // switchEngine(Lizzie.frame.toolbar.engineWhite);
    }
    this.currentEngineNo = Lizzie.leelaz.currentEngineN();
    featurecat.lizzie.gui.Menu.engineMenu.setText(
        "引擎" + (currentEngineNo + 1) + ": " + engineList.get(currentEngineNo).currentEnginename);
    changeEngIco();
  }
  //
  // private void ponderForEndpk() {
  // Lizzie.leelaz.togglePonder();
  // Lizzie.leelaz.togglePonder();
  // try {
  // timer4.stop();
  // timer4 = null;
  // } catch (Exception ex) {
  //
  // }
  // }

  private void changeEngIco() {
    Lizzie.frame.menu.changeicon();
  }
}
