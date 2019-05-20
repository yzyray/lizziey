package featurecat.lizzie;

import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.gui.AnalysisFrame;
import featurecat.lizzie.gui.GtpConsolePane;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.MovelistFrame;
import featurecat.lizzie.rules.Board;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.swing.*;
import org.json.JSONArray;

/** Main class. */
public class Lizzie {
  public static Config config;
  public static GtpConsolePane gtpConsole;
  public static LizzieFrame frame;
  public static JDialog analysisframe;
  public static AnalysisFrame analysisFrame;
  public static JDialog movelistframe;
  public static MovelistFrame movelistFrame;
  public static Board board;
  public static Leelaz leelaz;
  public static String lizzieVersion = "0.7";
  private static String[] mainArgs;
  //  public static Menu menu;

  /** Launches the game window, and runs the game. */
  public static void main(String[] args) throws IOException {
    setLookAndFeel();
    mainArgs = args;
    config = new Config();
    board = new Board();

    analysisframe = AnalysisFrame.createAnalysisDialog();
    //  analysisframe.setLocation(-7, 0);
    analysisframe.setVisible(config.uiConfig.optBoolean("show-suggestions-frame", true));
    analysisframe.setAlwaysOnTop(Lizzie.config.suggestionsalwaysontop);

    movelistframe = MovelistFrame.createBadmovesDialog();
    //   movelistframe.setLocation(-7, 302);
    movelistframe.setVisible(config.uiConfig.optBoolean("show-badmoves-frame", true));
    movelistframe.setAlwaysOnTop(Lizzie.config.badmovesalwaysontop);

    frame = new LizzieFrame();
    gtpConsole = new GtpConsolePane(frame);
    gtpConsole.setVisible(config.leelazConfig.optBoolean("print-comms", false));
    //  menu = new Menu(frame);
    //  menu.setVisible(true);
    try {
      leelaz = new Leelaz();

      if (config.handicapInsteadOfWinrate) {
        leelaz.estimatePassWinrate();
      }
      if (mainArgs.length == 1) {
        frame.loadFile(new File(mainArgs[0]));
      } else if (config.config.getJSONObject("ui").getBoolean("resume-previous-game")) {
        board.resumePreviousGame();
      }
      leelaz.togglePonder();
    } catch (IOException e) {
      frame.openConfigDialog();
      System.exit(1);
    }
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  public static void shutdown() {
    if (config.config.getJSONObject("ui").getBoolean("confirm-exit")) {
      int ret =
          JOptionPane.showConfirmDialog(
              null, "是否保存SGF棋谱?", "保存SGF棋谱?", JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.OK_OPTION) {
        LizzieFrame.saveFile();
      }
    }
    board.autosaveToMemory();

    try {
      config.persist();
    } catch (IOException e) {
      e.printStackTrace(); // Failed to save config
    }

    if (leelaz != null) leelaz.killAllEngines();
    System.exit(0);
  }

  /**
   * Switch the Engine by index number
   *
   * @param index engine index
   */
  public static void switchEngine(int index) {
    // BoardData.isChanged = true;
    // added for change bestmoves immediatly not wait until totalplayouts>last engine's
    // totalplayouts
    String commandLine;
    if (index == 0) {
      String networkFile = Lizzie.config.leelazConfig.getString("network-file");
      commandLine = Lizzie.config.leelazConfig.getString("engine-command");
      commandLine = commandLine.replaceAll("%network-file", networkFile);
    } else {
      Optional<JSONArray> enginesOpt =
          Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
      if (enginesOpt.map(e -> e.length() < index).orElse(true)) {
        return;
      }
      commandLine = enginesOpt.get().getString(index - 1);
    }
    if (commandLine.trim().isEmpty()) {
      return;
    }
    if (leelaz.switching) {
      JOptionPane.showMessageDialog(null, "正在加载引擎,请等待");
      return;
    }
    //    if (leelaz.isThinking) {
    //        JOptionPane.showMessageDialog(null, "AI正在思考,无法切换");
    //        return;
    //      }
    if (index == Lizzie.leelaz.currentEngineN() && Lizzie.leelaz.isEngineAlive(index)) {
      return;
    }
    //    if(leelaz.isEngineBusy(index))
    //	 {
    //		 JOptionPane.showMessageDialog(null, "请不要频繁切换");
    //		 System.out.println("频繁切换");
    //		 return;
    //	 }
    // Workaround for leelaz no exiting when restarting
    if (leelaz.isThinking || Lizzie.frame.isPlayingAgainstLeelaz) {

      Lizzie.frame.isPlayingAgainstLeelaz = false;
      Lizzie.leelaz.isThinking = false;
    }

    int movenumber = board.getcurrentmovenumber();
    // board.saveMoveNumber();
    leelaz.switching = true;

    board.clearbestmovesafter(board.getHistory().getStart(), movenumber);
    board.savelistforswitch();
    // leelaz.execuser = !leelaz.execuser;
    try {

      leelaz.restartEngine(commandLine, index);
      // board.restoreMoveNumber();

      board.setlistforswitch();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
