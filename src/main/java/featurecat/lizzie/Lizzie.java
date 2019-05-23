package featurecat.lizzie;

import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.gui.AnalysisFrame;
import featurecat.lizzie.gui.GtpConsolePane;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.MovelistFrame;
import featurecat.lizzie.rules.Board;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

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
  public static EngineManager engineManager;
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
      engineManager = new EngineManager(config);

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
              null, "Do you want to save this SGF?", "Save SGF?", JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.OK_OPTION) {
        frame.saveFile();
      }
    }
    board.autosaveToMemory();

    try {
      config.persist();
    } catch (IOException e) {
      e.printStackTrace(); // Failed to save config
    }

    // if (leelaz != null)
    engineManager.forcekillAllEngines();
    if (Lizzie.frame.zen != null
        && Lizzie.frame.zen.process != null
        && Lizzie.frame.zen.process.isAlive()) {

      try {
        Lizzie.frame.zen.process.destroy();
      } catch (Exception e) {
      }
    }
    System.exit(0);
  }
}
