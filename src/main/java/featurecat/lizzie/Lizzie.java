package featurecat.lizzie;

import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.YaZenGtp;
import featurecat.lizzie.gui.AnalysisFrame;
import featurecat.lizzie.gui.CountResults;
import featurecat.lizzie.gui.GtpConsolePane;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.LoadEngine;
import featurecat.lizzie.gui.MovelistFrame;
import featurecat.lizzie.rules.Board;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import org.json.JSONException;

/** Main class. */
public class Lizzie {
  public static Config config;
  public static GtpConsolePane gtpConsole;
  public static LizzieFrame frame;
  public static JDialog analysisframe;
  public static AnalysisFrame analysisFrame;
  public static JDialog movelistframe;
  public static JDialog loadEngine;
  public static MovelistFrame movelistFrame;
  public static CountResults countResults;
  public static Board board;
  public static Leelaz leelaz;
  public static String lizzieVersion = "0.7";
  private static String[] mainArgs;
  public static EngineManager engineManager;
  // public static Menu menu;

  /** Launches the game window, and runs the game. */
  public static void main(String[] args) throws IOException {
    setLookAndFeel();
    setUIFont(new javax.swing.plaf.FontUIResource("", Font.PLAIN, 12));
    mainArgs = args;
    config = new Config();
    board = new Board();

    movelistframe = MovelistFrame.createBadmovesDialog();

    // movelistframe.setLocation(-7, 302);
    movelistframe.setVisible(config.uiConfig.optBoolean("show-badmoves-frame", false));
    movelistframe.setAlwaysOnTop(Lizzie.config.badmovesalwaysontop);

    frame = new LizzieFrame();
    gtpConsole = new GtpConsolePane(frame);
    countResults = new CountResults(frame);
    gtpConsole.setVisible(config.leelazConfig.optBoolean("print-comms", false));
    // menu = new Menu(frame);
    // menu.setVisible(true);

    if (Lizzie.config.uiConfig.optBoolean("autoload-default", false)) {
      int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);
      try {
        engineManager = new EngineManager(config, defaultEngine);

      } catch (IOException e) {
        try {
          Lizzie.engineManager = new EngineManager(Lizzie.config, -1);
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    } else {
      loadEngine = LoadEngine.createBadmovesDialog();
      loadEngine.setVisible(true);
    }
    analysisframe = AnalysisFrame.createAnalysisDialog();
    analysisframe.setVisible(config.uiConfig.optBoolean("show-suggestions-frame", false));
    analysisframe.setAlwaysOnTop(Lizzie.config.suggestionsalwaysontop);
    frame.refresh();
  }

  public static void setLookAndFeel() {
    try {
      if (System.getProperty("os.name").contains("Mac")) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
      }
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

  public static void setUIFont(javax.swing.plaf.FontUIResource f) {
    java.util.Enumeration keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource) UIManager.put(key, f);
    }
  }

  public static void initializeAfterVersionCheck() {
    if (config.handicapInsteadOfWinrate) {
      leelaz.estimatePassWinrate();
    }
    if (mainArgs.length == 1) {
      frame.loadFile(new File(mainArgs[0]));
    } else if (config.config.getJSONObject("ui").getBoolean("resume-previous-game")) {
      board.resumePreviousGame();
    }
    leelaz.ponder();
    Lizzie.frame.toolbar.reSetButtonLocation();
    if (Lizzie.config.loadZen) {
      try {
        frame.zen = new YaZenGtp();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }

  public static void shutdown() {
    if (config.config.getJSONObject("ui").getBoolean("confirm-exit")) {
      int ret =
          JOptionPane.showConfirmDialog(
              null, "是否保存SGF棋谱?", "保存SGF棋谱?", JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.OK_OPTION) {
        frame.saveFile();
      }
    }
    board.autosaveToMemory();
    if (Lizzie.config.uiConfig.optBoolean("autoload-last", false)) {
      Lizzie.config.uiConfig.put("default-engine", engineManager.currentEngineNo);
      try {
        Lizzie.config.save();
      } catch (IOException es) {
      }
    }
    try {
      config.persist();
    } catch (IOException e) {
      e.printStackTrace(); // Failed to save config
    }

    if (leelaz != null) engineManager.forcekillAllEngines();
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
