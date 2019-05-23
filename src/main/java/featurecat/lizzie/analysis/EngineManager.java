package featurecat.lizzie.analysis;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EngineManager {

  private List<Leelaz> engineList;
  public static int currentEngineNo;

  public EngineManager(Config config) throws JSONException, IOException {

    JSONObject eCfg = config.config.getJSONObject("leelaz");
    String engineCommand = eCfg.getString("engine-command");
    // substitute in the weights file
    engineCommand = engineCommand.replaceAll("%network-file", eCfg.getString("network-file"));

    // Start default engine
    Leelaz lz = new Leelaz(engineCommand);
    Lizzie.leelaz = lz;
    Lizzie.board = lz.board;
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
                              //  if (cmd != null && !cmd.isEmpty()) {
                              Leelaz e;
                              try {
                                e = new Leelaz(cmd);
                                // TODO: how sync the board
                                e.board = Lizzie.board;
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
                              // }
                            });
                  });
            })
        .start();
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
      engineList.get(i + 1).engineCommand = enginesOpt.get().optString(i);
    }
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
  public void switchEngine(int index) {
    if (index == this.currentEngineNo || index > this.engineList.size()) return;

    //    if (curEng.isThinking) {
    //      if (Lizzie.frame.isPlayingAgainstLeelaz) {
    //        Lizzie.frame.isPlayingAgainstLeelaz = false;
    //        Lizzie.leelaz.isThinking = false;
    //      }
    //      curEng.togglePonder();
    //    }
    // TODO: Need keep analyze?
    //    if (curEng.isPondering()) {
    //      curEng.togglePonder();
    //    }
    Lizzie.board.saveMoveNumber();
    int movenumber = Lizzie.board.getcurrentmovenumber();
    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart(), movenumber);
    try {
      Leelaz newEng = engineList.get(index);
      if (newEng.engineCommand.equals("")) return;
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
      //      newEng.board = curEng.board;
      //      Lizzie.board = newEng.board;
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
      //  }
      Lizzie.board.restoreMoveNumber();
      this.currentEngineNo = index;
      featurecat.lizzie.gui.Menu.engineMenu.setText(
          "引擎" + (currentEngineNo + 1) + ": " + engineList.get(currentEngineNo).currentEnginename);

    } catch (IOException e) {
      e.printStackTrace();
    }

    changeEngIco();
  }

  private void changeEngIco() {
    if (featurecat.lizzie.gui.Menu.engine1.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine1.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine2.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine2.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine3.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine3.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine4.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine4.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine5.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine5.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine6.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine6.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine7.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine7.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine8.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine8.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine9.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine9.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    if (featurecat.lizzie.gui.Menu.engine10.getIcon() != null
        && featurecat.lizzie.gui.Menu.engine10.getIcon() != featurecat.lizzie.gui.Menu.stop) {
      featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.ready);
    }
    switch (currentEngineNo) {
      case 0:
        if (featurecat.lizzie.gui.Menu.engine1.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine1.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 1:
        if (featurecat.lizzie.gui.Menu.engine2.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine2.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 2:
        if (featurecat.lizzie.gui.Menu.engine3.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine3.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 3:
        if (featurecat.lizzie.gui.Menu.engine4.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine4.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 4:
        if (featurecat.lizzie.gui.Menu.engine5.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine5.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 5:
        if (featurecat.lizzie.gui.Menu.engine6.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine6.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 6:
        if (featurecat.lizzie.gui.Menu.engine7.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine7.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 7:
        if (featurecat.lizzie.gui.Menu.engine8.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine8.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 8:
        if (featurecat.lizzie.gui.Menu.engine9.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine9.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
      case 9:
        if (featurecat.lizzie.gui.Menu.engine10.getIcon() == null) break;
        featurecat.lizzie.gui.Menu.engine10.setIcon(featurecat.lizzie.gui.Menu.icon);
        break;
    }
  }
}
