package featurecat.lizzie;

import com.teamdev.jxbrowser.chromium.ba;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.YaZenGtp;
import featurecat.lizzie.gui.AnalysisFrame;
import featurecat.lizzie.gui.CountResults;
import featurecat.lizzie.gui.GtpConsolePane;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.LoadEngine;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.gui.MovelistFrame;
import featurecat.lizzie.rules.Board;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import javax.swing.*;
import org.json.JSONException;

/** Main class. */
public class Lizzie {
  public static Config config;
  public static GtpConsolePane gtpConsole;
  public static LizzieFrame frame;
  // public  JDialog analysisframe;
  public AnalysisFrame analysisFrame;
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
    if (Lizzie.config.uiConfig.optBoolean("autoload-default", false)) {
      int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);
      start(defaultEngine);
    } else {
      loadEngine = LoadEngine.createBadmovesDialog();
      loadEngine.setVisible(true);
    }
  }

  public static void start(int index) {

    board = new Board();
    frame = new LizzieFrame();
    gtpConsole = new GtpConsolePane(frame);

    try {
      Lizzie.engineManager = new EngineManager(Lizzie.config, index);
    } catch (Exception e) {
      try {
        Message msg = new Message();
        msg.setMessage("加载引擎失败,目前为不加载引擎运行");
        msg.setVisible(true);
        Lizzie.engineManager = new EngineManager(Lizzie.config, -1);
        frame.refresh();
      } catch (JSONException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }

    gtpConsole.setVisible(config.leelazConfig.optBoolean("print-comms", false));

    countResults = new CountResults(frame);

    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if (mainArgs.length == 1) {
              frame.loadFile(new File(mainArgs[0]));
            } else if (config.config.getJSONObject("ui").getBoolean("resume-previous-game")) {
              board.resumePreviousGame();
            }
            //            try {
            //				frame.readBoard=new ReadBoard();
            //			} catch (IOException e) {
            //				// TODO Auto-generated catch block
            //				e.printStackTrace();
            //			}

            if (Lizzie.config.loadZen) {
              try {
                frame.zen = new YaZenGtp();
              } catch (IOException e1) {
                e1.printStackTrace();
              }
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
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
    //    if (config.handicapInsteadOfWinrate) {
    //      leelaz.estimatePassWinrate();
    //    }
    if (!frame.toolbar.isEnginePk) leelaz.ponder();
    leelaz.setResponseUpToDate();
    Runnable runnable =
        new Runnable() {
          public void run() {
            Lizzie.frame.toolbar.reSetButtonLocation();
            if (config.uiConfig.optBoolean("show-suggestions-frame", false)) {
              if (frame.analysisFrame == null) frame.toggleBestMoves();
              else {
                frame.toggleBestMoves();
                frame.toggleBestMoves();
              }
            }
            if (config.uiConfig.optBoolean("show-badmoves-frame", false)) {
              if (Lizzie.movelistframe != null) Lizzie.movelistframe.setVisible(false);
              Lizzie.movelistframe = MovelistFrame.createBadmovesDialog();
              Lizzie.movelistframe.setAlwaysOnTop(Lizzie.config.badmovesalwaysontop);
              Lizzie.movelistframe.setVisible(true);
            }
            frame.setVisible(true);
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
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
    } catch (Exception e) {
      // Failed to save config
    }

    //   if (leelaz != null) engineManager.forcekillAllEngines();
    //    if (Lizzie.frame.zen != null
    //        && Lizzie.frame.zen.process != null
    //        && Lizzie.frame.zen.process.isAlive()) {
    //
    //      try {
    //        Lizzie.frame.zen.process.destroy();
    //      } catch (Exception e) {
    //      }
    //   }
    System.exit(0);
  }

  static {
    try {
      Field e = ba.class.getDeclaredField("e");
      e.setAccessible(true);
      Field f = ba.class.getDeclaredField("f");
      f.setAccessible(true);
      Field modifersField = Field.class.getDeclaredField("modifiers");
      modifersField.setAccessible(true);
      modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
      modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
      e.set(null, new BigInteger("1"));
      f.set(null, new BigInteger("1"));
      modifersField.setAccessible(false);
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }
}
