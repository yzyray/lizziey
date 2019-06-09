package featurecat.lizzie.analysis;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Movelist;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.swing.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EngineManager {

  public List<Leelaz> engineList;
  public static int currentEngineNo;
  //  public boolean firstTime =true;
  Timer timer;
  Timer timer2;

  public EngineManager(Config config) throws JSONException, IOException {

    JSONObject eCfg = config.config.getJSONObject("leelaz");
    String engineCommand = eCfg.getString("engine-command");
    // substitute in the weights file
    engineCommand = engineCommand.replaceAll("%network-file", eCfg.getString("network-file"));

    // Start default engine
    Leelaz lz = new Leelaz(engineCommand);
    Lizzie.leelaz = lz;
    //   Lizzie.board = lz.board;
    if (engineCommand.equals("")) {
      Lizzie.frame.openConfigDialog();
      System.exit(1);
    }
    lz.startEngine(0);
    lz.preload = true;
    engineList = new ArrayList<Leelaz>();
    engineList.add(lz);
    currentEngineNo = 0;
    featurecat.lizzie.gui.Menu.engineMenu.setText("引擎1: " + engineList.get(0).currentEnginename);

    new Thread(
            () -> {
              // Process other engine
              Optional<JSONArray> enginesOpt =
                  Optional.ofNullable(
                      Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
              Optional<JSONArray> enginePreloadOpt =
                  Optional.ofNullable(
                      Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));
              enginesOpt.ifPresent(
                  m -> {
                    IntStream.range(0, m.length())
                        .forEach(
                            i -> {
                              String cmd = m.optString(i);
                              if (cmd != null && !cmd.isEmpty()) {
                                Leelaz e;
                                try {
                                  e = new Leelaz(cmd);
                                  // TODO: how sync the board
                                  //       e.board = Lizzie.board;
                                  e.preload =
                                      enginePreloadOpt.map(p -> p.optBoolean(i)).orElse(false);
                                  if (e.preload) {
                                    e.startEngine(i + 1);
                                  }
                                  // TODO: Need keep analyze?
                                  // e.togglePonder();
                                  engineList.add(e);
                                } catch (JSONException | IOException e1) {
                                  e1.printStackTrace();
                                }

                              } else {
                                // empty
                                engineList.add(null);
                              }
                            });
                  });
            })
        .start();

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

  private void checkEngineAlive() {
    if (Lizzie.frame.toolbar.isEnginePk) {

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
    if (engineList.get(Lizzie.frame.toolbar.engineBlack).process != null
        && engineList.get(Lizzie.frame.toolbar.engineBlack).process.isAlive()) {
    } else {
      try {
        engineList
            .get(Lizzie.frame.toolbar.engineBlack)
            .restartClosedEngine(Lizzie.frame.toolbar.engineBlack);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (engineList.get(Lizzie.frame.toolbar.engineWhite).process != null
        && engineList.get(Lizzie.frame.toolbar.engineWhite).process.isAlive()) {
    } else {
      try {
        engineList
            .get(Lizzie.frame.toolbar.engineWhite)
            .restartClosedEngine(Lizzie.frame.toolbar.engineWhite);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    timer2.stop();
    timer2 = null;
  }

  public void updateEngines() {
    JSONObject config;
    config = Lizzie.config.config.getJSONObject("leelaz");
    String engineCommand;
    engineCommand = config.getString("engine-command");
    engineCommand = engineCommand.replaceAll("%network-file", config.getString("network-file"));
    engineList.get(0).engineCommand = engineCommand;
    Optional<JSONArray> enginesOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
    for (int i = 0; i < enginesOpt.get().length(); i++) {
      if (engineList.get(i + 1) == null) {
        engineList.remove(i + 1);

        try {
          Leelaz e = new Leelaz(enginesOpt.get().optString(i));
          //		e.board = Lizzie.board;
          engineList.add(i + 1, e);
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        // TODO: how sync the board

      } else engineList.get(i + 1).engineCommand = enginesOpt.get().optString(i);
    }
    int j = Lizzie.frame.toolbar.enginePkBlack.getItemCount();
    Lizzie.frame.toolbar.removeEngineLis();
    for (int i = 0; i < j; i++) {
      Lizzie.frame.toolbar.enginePkBlack.removeItemAt(0);
      Lizzie.frame.toolbar.enginePkWhite.removeItemAt(0);
    }
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
    enginesNameOpt.ifPresent(
        a -> {
          IntStream.range(0, a.length())
              .forEach(
                  i -> {
                    String name = a.getString(i);
                    if (!name.equals("")) {
                      Lizzie.frame.toolbar.enginePkBlack.addItem("[" + (i + 1) + "]" + name);
                      Lizzie.frame.toolbar.enginePkWhite.addItem("[" + (i + 1) + "]" + name);
                    }
                  });
        });
    Lizzie.frame.toolbar.addEngineLis();
  }

  public void killAllEngines() {
    currentEngineNo = -1;
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        engineList.get(i).normalQuit();
      }
    }
  }

  public void forcekillAllEngines() {
    currentEngineNo = -1;
    for (int i = 0; i < engineList.size(); i++) {
      try {
        engineList.get(i).started = false;
        engineList.get(i).process.destroy();
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

  /**
   * Switch the Engine by index number
   *
   * @param index engine index
   */
  public void startEngineForPk(int index) {
    if (index > this.engineList.size()) return;
    // Lizzie.board.saveMoveNumber();
    Leelaz newEng = engineList.get(index);
    ArrayList<Movelist> mv = Lizzie.board.getmovelist();
    if (!newEng.isStarted()) {
      try {
        newEng.startEngine(index);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Lizzie.leelaz = newEng;
    Lizzie.leelaz.clear();
    this.currentEngineNo = index;
    Lizzie.board.restoreMoveNumber(index, mv);
  }

  public void switchEngine(int index) {
    if (index == this.currentEngineNo || index > this.engineList.size()) return;
    Leelaz newEng = engineList.get(index);
    if (newEng == null) return;
    Lizzie.board.saveMoveNumber();

    try {
      if (currentEngineNo != -1) {
        Leelaz curEng = engineList.get(this.currentEngineNo);
        curEng.switching = true;
        try {
          if (!Lizzie.config.fastChange) {
            curEng.normalQuit();
          } else {
            curEng.sendCommand("name");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        curEng.notPondering();
      }
      Lizzie.leelaz = newEng;
      // TODO: how sync the board
      //    newEng.board = curEng.board;
      //    Lizzie.board = newEng.board;
      if (!newEng.isStarted()) {
        newEng.startEngine(index);
      } else {
        // newEng.getEngineName(index);
        Lizzie.config.leelaversion = newEng.version;
      }
      // if (!newEng.isPondering()) {
      try {
        newEng.ponder();
      } catch (Exception e) {
        e.printStackTrace();
      }

      Lizzie.board.restoreMoveNumber();
      Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
      this.currentEngineNo = index;
      featurecat.lizzie.gui.Menu.engineMenu.setText(
          "引擎" + (currentEngineNo + 1) + ": " + engineList.get(currentEngineNo).currentEnginename);

    } catch (IOException e) {
      e.printStackTrace();
    }

    changeEngIco();
  }

  public void changeEngIcoForEndPk() {
    for (int i = 0; i < Lizzie.frame.menu.engine.length; i++) {
      if (featurecat.lizzie.gui.Menu.engine[i].getIcon() != null
          && featurecat.lizzie.gui.Menu.engine[i].getIcon() != featurecat.lizzie.gui.Menu.stop) {
        featurecat.lizzie.gui.Menu.engine[i].setIcon(featurecat.lizzie.gui.Menu.ready);
      }
    }
    if (featurecat.lizzie.gui.Menu.engine[Lizzie.leelaz.currentEngineN()].getIcon() == null) {
    } else {
      featurecat.lizzie.gui.Menu.engine[Lizzie.leelaz.currentEngineN()].setIcon(
          featurecat.lizzie.gui.Menu.icon);
    }
    featurecat.lizzie.gui.Menu.engineMenu.setText(
        "引擎"
            + (currentEngineNo + 1)
            + ": "
            + engineList.get(Lizzie.leelaz.currentEngineN()).currentEnginename);
    this.currentEngineNo = Lizzie.leelaz.currentEngineN();
  }

  private void changeEngIco() {
    for (int i = 0; i < Lizzie.frame.menu.engine.length; i++) {
      if (featurecat.lizzie.gui.Menu.engine[i].getIcon() != null
          && featurecat.lizzie.gui.Menu.engine[i].getIcon() != featurecat.lizzie.gui.Menu.stop) {
        featurecat.lizzie.gui.Menu.engine[i].setIcon(featurecat.lizzie.gui.Menu.ready);
      }
    }
    if (featurecat.lizzie.gui.Menu.engine[currentEngineNo].getIcon() == null) {
    } else {
      featurecat.lizzie.gui.Menu.engine[currentEngineNo].setIcon(featurecat.lizzie.gui.Menu.icon);
    }
  }
}
