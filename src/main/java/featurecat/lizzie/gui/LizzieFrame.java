package featurecat.lizzie.gui;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import com.jhlabs.image.GaussianFilter;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.analysis.YaZenGtp;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.GIBParser;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.json.JSONArray;
import org.json.JSONObject;

/** The window used to display the game. */
public class LizzieFrame extends JFrame {
  private static final ResourceBundle resourceBundle =
      ResourceBundle.getBundle("l10n.DisplayStrings");

  private static final String[] commands = {
    resourceBundle.getString("LizzieFrame.commands.keyN"),
    resourceBundle.getString("LizzieFrame.commands.keyEnter"),
    resourceBundle.getString("LizzieFrame.commands.keySpace"),
    resourceBundle.getString("LizzieFrame.commands.rightClick"),
    resourceBundle.getString("LizzieFrame.commands.mouseWheelScroll"),
    "滚轮单击|落鼠标指向变化图的第一步,后续步可滚动滚轮继续查看",
    "滚轮长按或R|快速回放鼠标指向的变化图",
    "鼠标指向小棋盘|左键/右键点击可切换小棋盘变化图,滚轮可控制变化图前进后退",
    "逗号|落最佳一手,如果鼠标指向变化图则落子到变化图结束",
    "B|显示恶手列表",
    "U|显示AI选点列表",
    "I|编辑棋局信息(修改贴目并告知引擎)",
    "V|试下",
    "F|关闭/显示AI推荐点",
    // resourceBundle.getString("LizzieFrame.commands.keyI"),
    resourceBundle.getString("LizzieFrame.commands.keyUpArrow"),
    resourceBundle.getString("LizzieFrame.commands.keyDownArrow"),
    resourceBundle.getString("LizzieFrame.commands.keyC"),
    resourceBundle.getString("LizzieFrame.commands.keyP"),
    resourceBundle.getString("LizzieFrame.commands.keyPeriod"),
    resourceBundle.getString("LizzieFrame.commands.keyA"),
    resourceBundle.getString("LizzieFrame.commands.keyM"),
    resourceBundle.getString("LizzieFrame.commands.keyAltC"),
    resourceBundle.getString("LizzieFrame.commands.keyAltV"),
    resourceBundle.getString("LizzieFrame.commands.keyF"),
    // resourceBundle.getString("LizzieFrame.commands.keyV"),
    resourceBundle.getString("LizzieFrame.commands.keyW"),
    resourceBundle.getString("LizzieFrame.commands.keyCtrlW"),
    resourceBundle.getString("LizzieFrame.commands.keyG"),
    resourceBundle.getString("LizzieFrame.commands.keyBracket"),
    resourceBundle.getString("LizzieFrame.commands.keyT"),
    resourceBundle.getString("LizzieFrame.commands.keyCtrlT"),
    resourceBundle.getString("LizzieFrame.commands.keyY"),
    // resourceBundle.getString("LizzieFrame.commands.keyZ"),
    // resourceBundle.getString("LizzieFrame.commands.keyShiftZ"),
    resourceBundle.getString("LizzieFrame.commands.keyHome"),
    resourceBundle.getString("LizzieFrame.commands.keyEnd"),
    resourceBundle.getString("LizzieFrame.commands.keyControl"),
    resourceBundle.getString("LizzieFrame.commands.keyDelete"),
    resourceBundle.getString("LizzieFrame.commands.keyBackspace"),
    resourceBundle.getString("LizzieFrame.commands.keyE"),
  };
  private static final String DEFAULT_TITLE = resourceBundle.getString("LizzieFrame.title");
  public static BoardRenderer boardRenderer;
  public static SubBoardRenderer subBoardRenderer;
  public int subBoardXmouse;
  public int subBoardYmouse;
  public int subBoardLengthmouse;
  private static VariationTree variationTree;
  public static WinrateGraph winrateGraph;
  public static Menu menu;
  public static BottomToolbar toolbar;
  // public static EditToolbar editToolbar;
  public Optional<List<String>> variationOpt;

  public static boolean urlSgf = false;
  public static Font uiFont;
  public static Font winrateFont;
  public boolean isshowrightmenu;
  public ArrayList<Movelist> movelist;
  public AnalysisFrame analysisFrame;

  public int blackorwhite = 0;
  // private final BufferStrategy bs;
  public boolean iscounting = false;
  public boolean isAutocounting = false;

  public static final int[] outOfBoundCoordinate = new int[] {-1, -1};

  public boolean isBatchAna = false;
  public int BatchAnaNum = -1;
  public File[] Batchfiles;
  public int[] suggestionclick = outOfBoundCoordinate;
  public int[] clickbadmove = outOfBoundCoordinate;
  public int[] mouseOverCoordinate = outOfBoundCoordinate;
  public boolean showControls = false;
  public boolean isPlayingAgainstLeelaz = false;
  public boolean isAnaPlayingAgainstLeelaz = false;
  public boolean playerIsBlack = true;
  public int winRateGridLines = 3;
  public int BoardPositionProportion = Lizzie.config.boardPositionProportion;
  private long lastAutocomTime = System.currentTimeMillis();
  private int autoIntervalCom;
  private int autoInterval;
  private long lastAutosaveTime = System.currentTimeMillis();
  public boolean isReplayVariation = false;
  private RightClickMenu RightClickMenu = new RightClickMenu();
  private RightClickMenu2 RightClickMenu2 = new RightClickMenu2();
  private int boardPos = 0;
  public String komi = "7.5";
  // private ChangeMoveDialog2 ChangeMoveDialog2 = new ChangeMoveDialog2();

  // Save the player title
  private String playerTitle = "";
  private String resultTitle = "";

  // Display Comment
  private JScrollPane scrollPane;
  private JTextPane commentPane;
  private BufferedImage cachedCommentImage = new BufferedImage(1, 1, TYPE_INT_ARGB);
  private String cachedComment;
  private Rectangle commentRect;
  public YaZenGtp zen;
  public boolean isheatmap = false;
  public boolean isMouseOver = false;
  // Show the playouts in the title
  private ScheduledExecutorService showPlayouts = Executors.newScheduledThreadPool(1);
  private long lastPlayouts = 0;
  private String visitsString = "";
  public boolean isDrawVisitsInTitle = true;
  private Stone draggedstone;
  private int[] startcoords = new int[2];
  private int draggedmovenumer;
  public JPanel mainPanel;
  public int mainPanleX;
  public int mainPanleY;
  public int toolbarHeight = 26;
  boolean isSmallCap = false;
  boolean firstTime = true;
  private HTMLDocument htmlDoc;
  private HtmlKit htmlKit;
  private StyleSheet htmlStyle;
  public Input input = new Input();
  public InputSubboard input2 = new InputSubboard();
  public boolean noInput = true;

  public int grx;
  public int gry;
  public int grw;
  public int grh;

  public int statx;
  public int staty;
  public int statw;
  public int stath;

  public boolean isTrying = false;
  ArrayList<Movelist> tryMoveList;
  String tryString;
  String titleBeforeTrying;

  // boolean lastponder = true;

  static {
    // load fonts

    try {
      uiFont = new Font("微软雅黑", Font.PLAIN, 12);
      // uiFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
      // uiFont = // new Font("圆体", Font.TRUETYPE_FONT, 15);
      // Font.createFont(
      // Font.TRUETYPE_FONT,
      //
      // Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/MSYH.TTC"));
      winrateFont = new Font("微软雅黑", Font.PLAIN, 12);
      Font.createFont(
          Font.TRUETYPE_FONT,
          Thread.currentThread()
              .getContextClassLoader()
              .getResourceAsStream("fonts/OpenSans-Semibold.ttf"));
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
    }
  }

  /** Creates a window */
  public LizzieFrame() {
    super(DEFAULT_TITLE);

    boardRenderer = new BoardRenderer(true);
    subBoardRenderer = new SubBoardRenderer(false);
    variationTree = new VariationTree();
    winrateGraph = new WinrateGraph();
    toolbar = new BottomToolbar();
    menu = new Menu();

    // MenuTest menu = new MenuTest();
    // add(menu);
    // this.setJMenuBar(menu);
    // this.setVisible(true);

    this.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
    setMinimumSize(new Dimension(640, 400));
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("winrate-graph") != null
        && Lizzie.config.persistedUi.optJSONArray("winrate-graph").length() == 1) {
      JSONArray winrateG = Lizzie.config.persistedUi.getJSONArray("winrate-graph");
      winrateGraph.mode = winrateG.getInt(0);
    }
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("main-window-position") != null
        && Lizzie.config.persistedUi.optJSONArray("main-window-position").length() >= 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("main-window-position");
      this.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
      this.BoardPositionProportion =
          Lizzie.config.persistedUi.optInt("board-postion-propotion", this.BoardPositionProportion);
      if (Lizzie.config.persistedUi.optJSONArray("main-window-position").length() == 5) {
        this.toolbarHeight = pos.getInt(4);
      }
    } else {
      setSize(1360, 850);
      setLocationRelativeTo(null); // Start centered, needs to be called *after* setSize...
    }
    if (Lizzie.config.startMaximized && !persisted) {
      setExtendedState(Frame.MAXIMIZED_BOTH);

    } else if (persisted && Lizzie.config.persistedUi.getBoolean("window-maximized")) {
      setExtendedState(Frame.MAXIMIZED_BOTH);
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("main-window-position");
    }
    mainPanel =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            paintMianPanel(g);
          }
        };

    getContentPane().add(mainPanel);
    getContentPane().add(toolbar);
    getContentPane().setLayout(null);
    setJMenuBar(menu);

    mainPanel.setTransferHandler(
        new TransferHandler() {
          @Override
          public boolean importData(JComponent comp, Transferable t) {
            try {
              Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
              String filepath = o.toString();
              if (filepath.startsWith("[")) {
                filepath = filepath.substring(1);
              }
              if (filepath.endsWith("]")) {
                filepath = filepath.substring(0, filepath.length() - 1);
              }
              String[] filePaths = filepath.split(", ");
              if (filePaths.length == 1) {
                if (!(filepath.endsWith(".sgf") || filepath.endsWith(".gib"))) {
                  return false;
                }
                File file = new File(filepath);
                File files[] = new File[1];
                files[0] = file;
                loadFile(file);
                isBatchAna = true;
                BatchAnaNum = 0;
                Batchfiles = files;
                return true;
              } else if (filePaths.length > 1) {
                File files[] = new File[filePaths.length];
                for (int i = 0; i < filePaths.length; i++) {
                  files[i] = new File(filePaths[i]);
                }
                isBatchAna = true;
                BatchAnaNum = 0;
                Batchfiles = files;
                loadFile(files[0]);
                // toolbar.chkAnaAutoSave.setSelected(true);
                // toolbar.chkAnaAutoSave.setEnabled(false);

                Lizzie.frame.toolbarHeight = 70;
                // 打开分析界面
                StartAnaDialog newgame = new StartAnaDialog();
                newgame.setVisible(true);
                if (newgame.isCancelled()) {
                  toolbar.resetAutoAna();
                  return true;
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }

            return false;
          }

          @Override
          public boolean canImport(JComponent comp, DataFlavor[] flavors) {
            for (int i = 0; i < flavors.length; i++) {
              if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                return true;
              }
            }
            return false;
          }
        });

    // menu.add(Box.createRigidArea(new Dimension(600, 10)));
    // menu.setVisible(true);
    mainPanel.setFocusable(true);
    this.getJMenuBar().setBorder(new EmptyBorder(0, 0, 0, 0));
    if (this.toolbarHeight == 0) toolbar.setVisible(false);

    this.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            try {
              mainPanel.setBounds(
                  0,
                  0,
                  Lizzie.frame.getWidth()
                      - Lizzie.frame.getInsets().left
                      - Lizzie.frame.getInsets().right,
                  Lizzie.frame.getHeight()
                      - Lizzie.frame.getJMenuBar().getHeight()
                      - Lizzie.frame.getInsets().top
                      - Lizzie.frame.getInsets().bottom
                      - toolbarHeight);
              toolbar.setBounds(
                  0,
                  Lizzie.frame.getHeight()
                      - Lizzie.frame.getJMenuBar().getHeight()
                      - Lizzie.frame.getInsets().top
                      - Lizzie.frame.getInsets().bottom
                      - toolbarHeight,
                  Lizzie.frame.getWidth()
                      - Lizzie.frame.getInsets().left
                      - Lizzie.frame.getInsets().right,
                  toolbarHeight);
              if (toolbarHeight == 26) {
                toolbar.detail.setIcon(toolbar.iconUp);
              }
              if (toolbarHeight == 70) {
                toolbar.detail.setIcon(toolbar.iconDown);
              }
            } catch (Exception es) {
            }
          }
        });
    // Allow change font in the config
    if (Lizzie.config.uiFontName != null) {
      uiFont = new Font(Lizzie.config.uiFontName, Font.PLAIN, 12);
    }
    if (Lizzie.config.winrateFontName != null) {
      winrateFont = new Font(Lizzie.config.winrateFontName, Font.BOLD, 12);
    }

    htmlKit = new HtmlKit();
    htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
    htmlStyle = htmlKit.getStyleSheet();
    String style =
        "body {background:"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentBackgroundColor.getRed(),
                Lizzie.config.commentBackgroundColor.getGreen(),
                Lizzie.config.commentBackgroundColor.getBlue())
            + "; color:#"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentFontColor.getRed(),
                Lizzie.config.commentFontColor.getGreen(),
                Lizzie.config.commentFontColor.getBlue())
            + "; font-family:"
            + Lizzie.config.fontName
            + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
            + (Lizzie.config.commentFontSize > 0
                ? "font-size:" + Lizzie.config.commentFontSize
                : "")
            + "}";
    htmlStyle.addRule(style);
    commentPane = new JTextPane();
    commentPane.setBorder(BorderFactory.createEmptyBorder());
    commentPane.setEditorKit(htmlKit);
    commentPane.setDocument(htmlDoc);
    commentPane.setEditable(true);

    // commentPane.setMargin(new Insets(5, 5, 5, 5));
    commentPane.setBackground(Lizzie.config.commentBackgroundColor);
    commentPane.setForeground(Lizzie.config.commentFontColor);
    scrollPane = new JScrollPane();
    scrollPane.setViewportView(commentPane);
    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    commentRect = new Rectangle(0, 0, 0, 0);

    try {
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    setVisible(true);

    // createBufferStrategy(2);
    // bs = getBufferStrategy();

    // necessary for Windows users - otherwise Lizzie shows a blank white screen on
    // startup until
    // updates occur.
    repaint();
    autoIntervalCom =
        Lizzie.config.config.getJSONObject("leelaz").getInt("analyze-update-interval-centisec")
            * 50;
    autoInterval =
        Lizzie.config.config.getJSONObject("ui").getInt("autosave-interval-seconds") * 1000;
    // When the window is closed: save the SGF file, then run shutdown()
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.shutdown();
          }
        });

    // Show the playouts in the title
    showPlayouts.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            if (!isDrawVisitsInTitle) {
              visitsString = "";
              return;
            }
            if (Lizzie.leelaz == null) return;
            try {
              int totalPlayouts = MoveData.getPlayouts(Lizzie.leelaz.getBestMoves());
              if (totalPlayouts <= 0) return;
              visitsString =
                  String.format(
                      " %d visits/second",
                      (totalPlayouts > lastPlayouts) ? totalPlayouts - lastPlayouts : 0);
              updateTitle();
              lastPlayouts = totalPlayouts;
            } catch (Exception e) {
            }
          }
        },
        1,
        1,
        TimeUnit.SECONDS);
    mainPanel.addMouseMotionListener(input);
    toolbar.addMouseWheelListener(input);
    addInput();
  }

  public void addInput() {
    if (noInput) {
      mainPanel.addKeyListener(input);
      mainPanel.addMouseListener(input);
      mainPanel.addMouseWheelListener(input);
      mainPanel.removeMouseListener(input2);
      mainPanel.removeMouseWheelListener(input2);
      noInput = false;
    }
  }

  public void removeInput() {
    if (!noInput) {
      mainPanel.removeKeyListener(input);
      mainPanel.removeMouseListener(input);
      mainPanel.removeMouseWheelListener(input);
      mainPanel.addMouseListener(input2);
      mainPanel.addMouseWheelListener(input2);
      noInput = true;
    }
  }

  /** Clears related status from empty board. */
  public void clear() {
    if (winrateGraph != null) {
      winrateGraph.clear();
    }
  }

  public void openOnlineDialog() {
    OnlineDialog onlineDialog = new OnlineDialog();
    onlineDialog.setVisible(true);
  }

  //  public void openEditToolbar() {
  //	  editToolbar = new EditToolbar(this);
  //	  editToolbar.setVisible(true);
  //	  if((mainPanel.getWidth()/2-30)<400)
  //		  editToolbar.setLocation(this.getX()+400,this.getY()+ this.getInsets().top);
  //	  else
  //	  editToolbar.setLocation(this.getX()+mainPanel.getWidth()/2-30,this.getY()+
  // this.getInsets().top);
  //	  }
  //  public void resetEditToolbarLocation(){
  //	  if((mainPanel.getWidth()/2-30)<400)
  //		  editToolbar.setLocation(this.getX()+400,this.getY()+ this.getInsets().top);
  //	  else
  //	  editToolbar.setLocation(this.getX()+mainPanel.getWidth()/2-30,this.getY()+
  // this.getInsets().top);
  //	  }

  public static void openConfigDialog() {
    ConfigDialog configDialog = new ConfigDialog();
    configDialog.setVisible(true);
  }

  public static void openConfigDialog2(int index) {
    ConfigDialog2 configDialog2 = new ConfigDialog2();
    configDialog2.switchTab(index);
    configDialog2.setVisible(true);
  }

  public static void openMoreEngineDialog() {
    JDialog moreEngines;
    moreEngines = MoreEngines.createBadmovesDialog();
    moreEngines.setVisible(true);
  }

  public static void openChangeMoveDialog() {
    ChangeMoveDialog changeMoveDialog = new ChangeMoveDialog();
    changeMoveDialog.setVisible(true);
  }

  public static void openAvoidmoves() {
    Avoidmoves Avoidmoves = new Avoidmoves();
    Avoidmoves.setVisible(true);
  }

  public void openRightClickMenu(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);

    if (!boardCoordinates.isPresent()) {

      return;
    }
    if (isPlayingAgainstLeelaz) {

      return;
    }
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.sendCommand("name");
    }

    isshowrightmenu = true;

    int[] coords = boardCoordinates.get();

    if (Lizzie.board.getstonestat(coords) == Stone.BLACK
        || Lizzie.board.getstonestat(coords) == Stone.WHITE) {
      RightClickMenu2.Store(x, y);
      Timer timer = new Timer();
      timer.schedule(
          new TimerTask() {
            public void run() {
              Lizzie.frame.showmenu2(x, y);
              this.cancel();
            }
          },
          50);
      return;
    } else {
      RightClickMenu.Store(x, y);
      Timer timer = new Timer();
      timer.schedule(
          new TimerTask() {
            public void run() {
              Lizzie.frame.showmenu(x, y);
              this.cancel();
            }
          },
          50);
    }
  }

  public void showmenu(int x, int y) {
    RightClickMenu.show(mainPanel, x, y);
  }

  public void showmenu2(int x, int y) {
    RightClickMenu2.show(mainPanel, x, y);
  }

  public static void openAvoidMoveDialog() {
    AvoidMoveDialog avoidMoveDialog = new AvoidMoveDialog();
    avoidMoveDialog.setVisible(true);
  }
  // this is copyed from https://github.com/zsalch/lizzie/tree/n_avoiddialog

  public void toggleGtpConsole() {
    Lizzie.leelaz.toggleGtpConsole();
    if (Lizzie.gtpConsole != null) {
      Lizzie.gtpConsole.setVisible(!Lizzie.gtpConsole.isVisible());
      Lizzie.config.leelazConfig.putOpt("print-comms", Lizzie.gtpConsole.isVisible());
    } else {
      Lizzie.gtpConsole = new GtpConsolePane(this);
      Lizzie.gtpConsole.setVisible(true);
      Lizzie.config.leelazConfig.putOpt("print-comms", true);
    }
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void tryPlay() {
    if (!isTrying) {
      isTrying = true;

      try {
        tryString = SGFParser.saveToString();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      titleBeforeTrying = this.getTitle();
      this.setTitle("试下中...");
      toolbar.tryPlay.setText("恢复");
      tryMoveList = Lizzie.board.getmovelist();
      Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber = 0;
    } else {
      isTrying = false;
      toolbar.tryPlay.setText("试下");
      SGFParser.loadFromString(tryString);
      Lizzie.board.setmovelist(tryMoveList);
      this.setTitle(titleBeforeTrying);
    }
  }

  public void toggleBestMoves() {
    if (analysisFrame == null || !analysisFrame.isVisible()) {
      analysisFrame = new AnalysisFrame();
      analysisFrame.setVisible(true);
    } else {
      analysisFrame.setVisible(false);
      try {
        Lizzie.config.persist();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Lizzie.config.uiConfig.put("show-suggestions-frame", analysisFrame.isVisible());
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void countstones() {
    // if (isfirstcount) {
    // try {
    // zen = new YaZenGtp();
    // } catch (IOException e1) {
    // e1.printStackTrace();
    // }
    // isfirstcount = false;
    // } else
    if (zen == null || !zen.process.isAlive()) {
      try {
        zen = new YaZenGtp();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    zen.noread = false;
    zen.syncboradstat();
    zen.countStones();
    iscounting = true;
  }

  public void restartZen() {
    if (zen != null && zen.process != null && zen.process.isAlive()) {

      try {
        Lizzie.frame.zen.process.destroy();
      } catch (Exception e) {
      }
    }
    try {
      zen = new YaZenGtp();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleAlwaysOntop() {
    if (this.isAlwaysOnTop()) {
      this.setAlwaysOnTop(false);
      Lizzie.config.uiConfig.put("mains-always-ontop", false);
    } else {
      this.setAlwaysOnTop(true);
      Lizzie.config.uiConfig.put("mains-always-ontop", true);
    }
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleBadMoves() {
    if (Lizzie.movelistframe == null) {
      Lizzie.movelistframe = MovelistFrame.createBadmovesDialog();
      Lizzie.movelistframe.setAlwaysOnTop(Lizzie.config.badmovesalwaysontop);
      Lizzie.movelistframe.setVisible(true);
      Lizzie.config.uiConfig.put("show-badmoves-frame", true);
      try {
        Lizzie.config.save();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    }

    if (Lizzie.movelistframe.isVisible()) {
      Lizzie.movelistframe.setVisible(false);
      Lizzie.config.uiConfig.put("show-badmoves-frame", false);
      featurecat.lizzie.gui.MovelistFrame.selectedorder = -1;
      clickbadmove = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.boardRenderer.removedrawmovestone();
      Lizzie.frame.repaint();
      try {
        Lizzie.config.persist();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } else {
      Lizzie.movelistframe.setVisible(false);
      Lizzie.movelistframe = MovelistFrame.createBadmovesDialog();
      Lizzie.movelistframe.setAlwaysOnTop(Lizzie.config.badmovesalwaysontop);
      Lizzie.movelistframe.setVisible(true);
      Lizzie.config.uiConfig.put("show-badmoves-frame", true);
    }
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void startNewGame() {
    // GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();

    NewGameDialog newGameDialog = new NewGameDialog();
    // newGameDialog.setGameInfo(gameInfo);
    newGameDialog.setVisible(true);
    boolean playerIsBlack = newGameDialog.playerIsBlack();
    newGameDialog.dispose();
    if (newGameDialog.isCancelled()) return;
    Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
    Lizzie.board.clear();
    GameInfo gameInfo = newGameDialog.gameInfo;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
    Lizzie.leelaz.sendCommand("komi " + gameInfo.getKomi());
    Lizzie.frame.komi = gameInfo.getKomi() + "";
    Lizzie.leelaz.sendCommand(
        "time_settings 0 "
            + Lizzie.config.config.getJSONObject("leelaz").getInt("max-game-thinking-time-seconds")
            + " 1");
    Lizzie.frame.playerIsBlack = playerIsBlack;
    Lizzie.frame.isPlayingAgainstLeelaz = true;

    boolean isHandicapGame = gameInfo.getHandicap() != 0;
    if (isHandicapGame) {
      Lizzie.board.getHistory().getData().blackToPlay = false;
      if (Lizzie.leelaz.isKatago)
        Lizzie.leelaz.sendCommand("place_free_handicap " + gameInfo.getHandicap());
      else Lizzie.leelaz.sendCommand("fixed_handicap " + gameInfo.getHandicap());
      if (playerIsBlack) Lizzie.leelaz.genmove("W");
    } else if (!playerIsBlack) {
      Lizzie.leelaz.genmove("B");
    }
  }

  public static void editGameInfo() {
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();

    GameInfoDialog gameInfoDialog = new GameInfoDialog();
    gameInfoDialog.setGameInfo(gameInfo);
    gameInfoDialog.setVisible(true);

    gameInfoDialog.dispose();
  }

  public static void saveFile() {

    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sgf", "SGF");
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    chooser.setFileFilter(filter);
    JFrame frame = new JFrame();
    frame.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    chooser.setMultiSelectionEnabled(false);
    int result = chooser.showSaveDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (file.exists()) {
        int ret =
            JOptionPane.showConfirmDialog(
                null,
                resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                "Warning",
                JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION) {
          return;
        }
      }
      if (!file.getPath().endsWith(".sgf")) {
        file = new File(file.getPath() + ".sgf");
      }
      try {
        SGFParser.save(Lizzie.board, file.getPath());
        filesystem.put("last-folder", file.getParent());
      } catch (IOException err) {
        JOptionPane.showConfirmDialog(
            null,
            resourceBundle.getString("LizzieFrame.prompt.failedTosaveFile"),
            "Error",
            JOptionPane.ERROR);
      }
    }
  }

  public void openFile() {
    boolean ponder = false;
    if (Lizzie.leelaz.isPondering()) {
      ponder = true;
      Lizzie.leelaz.togglePonder();
    }

    boolean onTop = false;
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFrame frame = new JFrame();
    FileDialog fileDialog = new FileDialog(frame, "选择棋谱");
    if (this.isAlwaysOnTop()) {
      this.setAlwaysOnTop(false);
      fileDialog.setAlwaysOnTop(true);
      onTop = true;
    }

    fileDialog.setLocationRelativeTo(null);
    fileDialog.setDirectory(filesystem.getString("last-folder"));
    fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB;");

    fileDialog.setMultipleMode(false);
    fileDialog.setMode(0);
    fileDialog.setVisible(true);

    if (onTop) this.setAlwaysOnTop(true);
    //
    //    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sgf or *.gib", "SGF",
    // "GIB");
    //    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    //    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    //    chooser.setFileFilter(filter);
    //    chooser.setMultiSelectionEnabled(false);
    //    JFrame frame = new JFrame();
    //    frame.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    //    Action details = chooser.getActionMap().get("viewTypeDetails");
    //    details.actionPerformed(null);
    //    // Find the JTable on the file chooser panel and manually do the sort
    //    JTable table = SwingUtils.getDescendantsOfType(JTable.class, chooser).get(0);
    //    table.getRowSorter().toggleSortOrder(3);
    //    table.getRowSorter().toggleSortOrder(3);
    //   int result = chooser.showOpenDialog(frame);
    File[] file = fileDialog.getFiles();
    // chooser.showOpenDialog(null);
    if (file.length > 0) loadFile(file[0]);

    if (ponder) {
      Lizzie.leelaz.ponder();
    }
  }

  public void openFileAll() {
    boolean ponder = false;
    if (Lizzie.leelaz.isPondering()) {
      ponder = true;
      Lizzie.leelaz.togglePonder();
    }
    boolean onTop = false;
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFrame frame = new JFrame();
    FileDialog fileDialog = new FileDialog(frame, "选择棋谱");
    if (this.isAlwaysOnTop()) {
      this.setAlwaysOnTop(false);
      fileDialog.setAlwaysOnTop(true);
      onTop = true;
    }

    fileDialog.setLocationRelativeTo(null);
    fileDialog.setDirectory(filesystem.getString("last-folder"));
    fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB;");

    fileDialog.setMultipleMode(true);
    fileDialog.setMode(0);
    fileDialog.setVisible(true);

    File[] files = fileDialog.getFiles();
    if (onTop) this.setAlwaysOnTop(true);
    if (files.length > 0) {
      isBatchAna = true;
      BatchAnaNum = 0;
      Batchfiles = files;
      loadFile(files[0]);
      //  toolbar.chkAnaAutoSave.setSelected(true);
      //  toolbar.chkAnaAutoSave.setEnabled(false);

      Lizzie.frame.toolbarHeight = 70;
      toolbar.detail.setIcon(toolbar.iconDown);
    }
    if (ponder) {
      Lizzie.leelaz.ponder();
    }
  }

  public void openFileWithAna() {
    boolean ponder = false;
    if (Lizzie.leelaz.isPondering()) {
      ponder = true;
      Lizzie.leelaz.togglePonder();
    }
    boolean onTop = false;
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFrame frame = new JFrame();
    FileDialog fileDialog = new FileDialog(frame, "选择棋谱");
    if (this.isAlwaysOnTop()) {
      this.setAlwaysOnTop(false);
      fileDialog.setAlwaysOnTop(true);
      onTop = true;
    }

    fileDialog.setLocationRelativeTo(null);
    fileDialog.setDirectory(filesystem.getString("last-folder"));
    fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB;");

    fileDialog.setMultipleMode(true);
    fileDialog.setMode(0);
    fileDialog.setVisible(true);

    File[] files = fileDialog.getFiles();
    if (onTop) this.setAlwaysOnTop(true);
    if (files.length > 0) {
      isBatchAna = true;
      BatchAnaNum = 0;
      Batchfiles = files;
      loadFile(files[0]);
      // toolbar.chkAnaAutoSave.setSelected(true);
      // toolbar.chkAnaAutoSave.setEnabled(false);

      Lizzie.frame.toolbarHeight = 70;
      // 打开分析界面
      StartAnaDialog newgame = new StartAnaDialog();
      newgame.setVisible(true);
      if (newgame.isCancelled()) {
        toolbar.resetAutoAna();
        return;
      }
    }
  }

  public static void loadFile(File file) {
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    if (!(file.getPath().endsWith(".sgf") || file.getPath().endsWith(".gib"))) {
      file = new File(file.getPath() + ".sgf");
    }
    try {
      System.out.println(file.getPath());
      if (file.getPath().endsWith(".sgf")) {
        SGFParser.load(file.getPath());
      } else {
        GIBParser.load(file.getPath());
      }
      filesystem.put("last-folder", file.getParent());
    } catch (IOException err) {
      JOptionPane.showConfirmDialog(
          null,
          resourceBundle.getString("LizzieFrame.prompt.failedToOpenFile"),
          "Error",
          JOptionPane.ERROR);
    }
    Lizzie.board.setMovelistAll();
  }

  private BufferedImage cachedImage;

  private BufferedImage cachedBackground;
  private int cachedBackgroundWidth = 0, cachedBackgroundHeight = 0;
  private boolean cachedBackgroundShowControls = false;
  private boolean cachedShowWinrate = true;
  private boolean cachedShowVariationGraph = true;
  private boolean cachedShowLargeSubBoard = true;
  private boolean cachedLargeWinrate = true;
  private boolean cachedShowComment = true;
  public boolean redrawBackgroundAnyway = false;
  private int cachedBoardPositionProportion = BoardPositionProportion;

  /**
   * Draws the game board and interface
   *
   * @param g0 not used
   */
  public void paintMianPanel(Graphics g0) {
    isSmallCap = false;
    autosaveMaybe();

    int width = mainPanel.getWidth();
    int height = mainPanel.getHeight();

    Optional<Graphics2D> backgroundG;
    if (cachedBackgroundWidth != width
        || cachedBackgroundHeight != height
        || redrawBackgroundAnyway) {
      backgroundG = Optional.of(createBackground(width, height));
    } else {
      backgroundG = Optional.empty();
    }

    if (!showControls) {
      // layout parameters

      int topInset = mainPanel.getInsets().top;
      int leftInset = mainPanel.getInsets().left;
      int rightInset = mainPanel.getInsets().right;
      int bottomInset = mainPanel.getInsets().bottom; // + this.getJMenuBar().getHeight();
      int maxBound = Math.max(width, height);

      boolean noWinrate = !Lizzie.config.showWinrate;
      boolean noVariation = !Lizzie.config.showVariationGraph;
      boolean noBasic = !Lizzie.config.showCaptured;
      boolean noSubBoard = !Lizzie.config.showSubBoard;
      boolean noComment = !Lizzie.config.showComment;
      // board
      int maxSize = (int) (min(width - leftInset - rightInset, height - topInset - bottomInset));
      maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
      int boardX = (width - maxSize) / 8 * BoardPositionProportion;
      int boardY = topInset + (height - topInset - bottomInset - maxSize) / 2;

      int panelMargin = (int) (maxSize * 0.02);

      // captured stones
      int capx = leftInset;
      int capy = topInset;
      int capw = boardX - panelMargin - leftInset;
      int caph = boardY + maxSize / 8 - topInset;

      // move statistics (winrate bar)
      // boardX equals width of space on each side
      statx = capx;
      staty = capy + caph;
      statw = capw;
      stath = maxSize / 10;

      // winrate graph
      grx = statx;
      gry = staty + stath;
      grw = statw;
      grh = maxSize / 3;

      // variation tree container
      int vx = boardX + maxSize + panelMargin;
      int vy = capy;
      int vw = width - vx - rightInset;
      int vh = height - vy - bottomInset;

      // pondering message
      double ponderingSize = .040;
      int ponderingX = leftInset;

      int ponderingY =
          height - bottomInset; // - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize);
      int ponderingY2 =
          height - bottomInset; // - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize *
      // 0.4);
      if (Lizzie.config.showStatus) {
        ponderingY = ponderingY - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize);
        ponderingY2 =
            ponderingY2 - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize * 0.4);
      }
      // dynamic komi
      // double dynamicKomiSize = .02;
      // int dynamicKomiX = leftInset;
      // int dynamicKomiY = ponderingY - (int) (maxBound * dynamicKomiSize);
      // int dynamicKomiLabelX = leftInset;
      // int dynamicKomiLabelY = dynamicKomiY - (int) (maxBound * dynamicKomiSize);

      // loading message;
      double loadingSize = 0.03;
      int loadingX = ponderingX;
      int loadingY = ponderingY - (int) (maxBound * (loadingSize - ponderingSize * 3 / 4));

      // subboard
      int subBoardY = gry + grh;
      int subBoardWidth = grw;
      int subBoardHeight = ponderingY - subBoardY;
      int subBoardLength = min(subBoardWidth, subBoardHeight);
      int subBoardX = statx + (statw - subBoardLength) / 2;

      if (width >= height) {
        // Landscape mode
        if (Lizzie.config.showLargeSubBoard() && !noSubBoard) {
          boardX = width - maxSize - panelMargin;
          int spaceW = boardX - panelMargin - leftInset;
          int spaceH = height - topInset - bottomInset;
          int panelW = spaceW / 2;
          int panelH = spaceH * 2 / 7;

          // captured stones
          capw = (noVariation && noComment) ? spaceW : panelW;
          caph = (int) (panelH * 0.2);
          // move statistics (winrate bar)
          staty = capy + caph;
          statw = capw;
          stath = (int) (panelH * 0.33);
          // winrate graph
          gry = staty + stath;
          grw = spaceW;
          grh = panelH - caph - stath;
          // variation tree container
          vx = statx + statw;
          vw = panelW;
          vh = stath + caph;
          // subboard
          subBoardY = gry + grh;
          subBoardWidth = spaceW;
          subBoardHeight = ponderingY - subBoardY;
          subBoardLength = Math.min(subBoardWidth, subBoardHeight);
          if (subBoardHeight > subBoardWidth) {
            subBoardY = subBoardY + subBoardHeight - subBoardWidth;
            panelH = spaceH * 2 / 7 + (subBoardHeight - subBoardWidth);
            caph = (int) (panelH * 0.2);
            staty = capy + caph;
            stath = (int) (panelH * 0.33);
            gry = staty + stath;
            // staty=staty+(subBoardHeight-subBoardWidth);
            grh = panelH - caph - stath;
            vh = stath + caph;
          }
          subBoardX = statx + (spaceW - subBoardLength) / 2;
          isSmallCap = true;

        } else if (Lizzie.config.showLargeWinrate()) {
          boardX = width - maxSize - panelMargin;
          int spaceW = boardX - panelMargin - leftInset;
          int spaceH = height - topInset - bottomInset;
          int panelW = spaceW / 2;
          int panelH = spaceH / 4;

          // captured stones
          capy = topInset + panelH + 1;
          capw = spaceW;
          caph = (int) ((ponderingY - topInset - panelH) * 0.15);
          // move statistics (winrate bar)
          staty = capy + caph;
          statw = capw;
          stath = caph;
          // winrate graph
          gry = staty + stath;
          grw = statw;
          grh = ponderingY - gry;
          // variation tree container
          vx = leftInset + panelW;
          vw = panelW;
          vh = panelH;
          // subboard
          subBoardY = topInset;
          subBoardWidth = panelW - leftInset;
          subBoardHeight = panelH;
          subBoardLength = Math.min(subBoardWidth, subBoardHeight);
          subBoardX = statx + (vw - subBoardLength) / 2;
        }
      } else {
        // Portrait mode
        if (Lizzie.config.showLargeSubBoard() && !noSubBoard) {
          // board
          maxSize = (int) (maxSize * 0.8);
          boardY = height - maxSize - bottomInset;
          int spaceW = width - leftInset - rightInset;
          int spaceH = boardY - panelMargin - topInset;
          int panelW = spaceW / 2;
          int panelH = spaceH / 2;
          boardX = (spaceW - maxSize) / 2 + leftInset;

          // captured stones
          capw = panelW / 2;
          caph = panelH / 2;
          // move statistics (winrate bar)
          staty = capy + caph / 3;
          statw = capw;
          stath = caph / 3;
          // winrate graph
          gry = staty + stath;
          grw = statw;
          grh = spaceH - caph - stath;
          // variation tree container
          vx = capx + capw;
          vw = panelW / 2;
          vh = spaceH;
          // subboard
          subBoardX = vx + vw;
          subBoardWidth = panelW;
          subBoardHeight = boardY - topInset;
          subBoardLength = Math.min(subBoardWidth, subBoardHeight);
          if (subBoardHeight > subBoardWidth) {
            subBoardY = subBoardY + subBoardHeight - subBoardWidth;
            panelH = spaceH / 2 + subBoardHeight - subBoardWidth;
            caph = panelH / 2;
            stath = caph / 3;
            staty = capy + caph / 3;
            gry = staty + stath;
            grh = spaceH - caph - stath;
            vh = spaceH;
          }

          subBoardX = statx + (spaceW - subBoardLength) / 2;
          // pondering message
          ponderingY = height;
        } else if (Lizzie.config.showLargeWinrate() && !noWinrate) {
          // board
          maxSize = (int) (maxSize * 0.8);
          boardY = height - maxSize - bottomInset;
          int spaceW = width - leftInset - rightInset;
          int spaceH = boardY - panelMargin - topInset;
          int panelW = spaceW / 2;
          int panelH = spaceH / 2;
          boardX = (spaceW - maxSize) / 2 + leftInset;

          // captured stones
          capw = panelW / 2;
          caph = panelH / 4;
          // move statistics (winrate bar)
          statx = capx + capw;
          staty = capy;
          statw = capw;
          stath = caph;
          // winrate graph
          gry = staty + stath;
          grw = spaceW;
          grh = boardY - gry - 1;
          // variation tree container
          vx = statx + statw;
          vy = capy;
          vw = panelW / 2;
          vh = caph;
          // subboard
          subBoardY = topInset;
          subBoardWidth = panelW / 2;
          subBoardHeight = gry - topInset;
          subBoardLength = Math.min(subBoardWidth, subBoardHeight);
          subBoardX = vx + vw;
          // pondering message
          ponderingY = height;
        } else {
          // Normal
          // board
          boardY = (height - maxSize + topInset - bottomInset) / 2;
          int spaceW = width - leftInset - rightInset;
          int spaceH = boardY - panelMargin - topInset;
          int panelW = spaceW / 2;
          int panelH = spaceH / 2;

          // captured stones
          capw = panelW * 3 / 4;
          caph = panelH / 2;
          // move statistics (winrate bar)
          statx = capx + capw;
          staty = capy;
          statw = capw;
          stath = caph;
          // winrate graph
          grx = capx;
          gry = staty + stath;
          grw = capw + statw;
          grh = boardY - gry;
          // subboard
          subBoardX = grx + grw;
          subBoardWidth = panelW / 2;
          subBoardHeight = boardY - topInset;
          subBoardLength = Math.min(subBoardWidth, subBoardHeight);
          subBoardY = capy + (boardY - topInset - subBoardLength) / 2;
          // variation tree container
          vx = leftInset + panelW;
          vy = boardY + maxSize;
          vw = panelW;
          vh = height - vy - bottomInset;
        }
      }

      // graph container
      int contx = statx;
      int conty = staty;
      int contw = statw;
      int conth = stath + grh;
      if (width < height) {
        contw = grw;
        if (Lizzie.config.showLargeWinrate()) {
          contx = grx;
          conty = gry;
          conth = grh;
        } else {
          contx = capx;
          conty = capy;
          conth = stath + grh;
        }
      }

      // variation tree
      if (!Lizzie.config.showWinrate && (Lizzie.config.showLargeSubBoard())) {

        vh = vh + grh;
      }
      int treex = vx;
      int treey = vy;
      int treew = vw;
      int treeh = vh;

      // comment panel
      int cx = vx, cy = vy, cw = vw, ch = vh;
      if (Lizzie.config.showComment) {
        if (width >= height) {
          if (Lizzie.config.showVariationGraph) {
            treeh = vh / 2;
            cy = vy + treeh;
            ch = treeh;
          }
        } else {
          if (Lizzie.config.showVariationGraph) {
            if (Lizzie.config.showLargeSubBoard()) {
              treeh = vh / 2;
              cy = vy + treeh;
              ch = treeh;
            } else {
              treew = vw / 2;
              cx = vx + treew;
              cw = treew;
            }
          }
        }
        if (!Lizzie.config.showLargeSubBoard() && width > height) {
          int tempx = cx;
          int tempy = cy;
          int tempw = cw;
          int temph = ch;
          if (subBoardWidth > subBoardHeight) {
            cx = subBoardX - (subBoardWidth - subBoardHeight) / 2;
          } else {
            cx = subBoardX;
          }
          cy = subBoardY;
          cw = subBoardWidth;
          ch = subBoardHeight;
          subBoardX = tempx;
          subBoardY = tempy;
          subBoardLength = Math.min(tempw, temph);
        }
        // super.paintComponents(g0);
      }

      // initialize

      cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) cachedImage.getGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      if (Lizzie.config.showStatus) drawCommandString(g);

      if (boardPos != boardX + maxSize / 2) {
        boardPos = boardX + maxSize / 2;
        toolbar.setButtonLocation((int) (boardPos - 22));
      }
      if (Lizzie.config.showWinrate && Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
        drawMoveStatistics(g, statx, staty, statw, stath);
      }
      boardRenderer.setLocation(boardX, boardY);
      boardRenderer.setBoardLength(maxSize, maxSize);
      boardRenderer.setupSizeParameters();
      boardRenderer.draw(g);
      if (backgroundG.isPresent()) {
        if (Lizzie.config.showWinrate) {
          if (isSmallCap) {
            drawContainer(backgroundG.get(), contx, conty, contw * 2, conth);
          } else drawContainer(backgroundG.get(), contx, conty, contw, conth);
        }

        if (Lizzie.config.showVariationGraph || Lizzie.config.showComment) {
          drawContainer(backgroundG.get(), vx, vy, vw, vh);
        }
      }

      if (Lizzie.config.showCaptured) drawCaptured(g, capx, capy, capw, caph, isSmallCap);
      if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
        if (Lizzie.config.showStatus) {
          String statusKey = "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
          String statusText = resourceBundle.getString(statusKey);
          String ponderingText = resourceBundle.getString("LizzieFrame.display.pondering");
          String switching = resourceBundle.getString("LizzieFrame.prompt.switching");
          String switchingText = Lizzie.leelaz.switching() ? switching : "";
          String weightText = Lizzie.leelaz.currentEnginename;
          String text1 = weightText;
          String text2 = ponderingText + " " + statusText + " " + switchingText;
          drawPonderingState(g, text1, text2, ponderingX, ponderingY, ponderingY2, ponderingSize);
        }
        if (firstTime) {
          // toolbar.setAllUnfocuse();
          firstTime = false;
        }
        // Optional<String> dynamicKomi = Lizzie.leelaz.getDynamicKomi();
        // if (Lizzie.config.showDynamicKomi && dynamicKomi.isPresent()) {
        // String text = resourceBundle.getString("LizzieFrame.display.dynamic-komi");
        // drawPonderingState(g, text, dynamicKomiLabelX, dynamicKomiLabelY,
        // dynamicKomiSize);
        // drawPonderingState(g, dynamicKomi.get(), dynamicKomiX, dynamicKomiY,
        // dynamicKomiSize);
        // }

        // Todo: Make board move over when there is no space beside the board

        if (Lizzie.config.showVariationGraph || Lizzie.config.showComment) {
          // if (backgroundG.isPresent()) {
          // drawContainer(backgroundG.get(), vx, vy, vw, vh);
          // }
          if (Lizzie.config.showVariationGraph) {
            if (isSmallCap) {
              variationTree.drawsmall(g, treex, treey, treew, treeh);
            } else variationTree.draw(g, treex, treey, treew, treeh);
          }
          if (Lizzie.config.showComment) {
            drawComment(g, cx, cy, cw, ch);
          }
        }
        // 更改布局为大棋盘,一整条分支列表,小棋盘,评论放在左下,做到这里
        if (Lizzie.config.showSubBoard) {
          try {
            subBoardRenderer.setLocation(subBoardX, subBoardY);
            // subBoardRenderer.setLocation( cx,cy);
            subBoardRenderer.setBoardLength(subBoardLength, subBoardLength);

            subBoardXmouse = subBoardX;
            subBoardYmouse = subBoardY;
            subBoardLengthmouse = subBoardLength;
            subBoardRenderer.setupSizeParameters();
            subBoardRenderer.draw(g);
          } catch (Exception e) {
            // This can happen when no space is left for subboard.
          }
        }
        if (Lizzie.config.showWinrate) {
          // drawMoveStatistics(g, statx, staty, statw, stath);
          // if (backgroundG.isPresent()) {
          // if (isSmallCap) {
          // contw = contw + contw;
          // }
          // drawContainer(backgroundG.get(), contx, conty, contw, conth);
          // }
          winrateGraph.draw(g, grx, gry, grw, grh);
        }
      } else {
        if (Lizzie.config.showStatus) {
          String loadingText = resourceBundle.getString("LizzieFrame.display.loading");
          drawPonderingState(g, loadingText, loadingX, loadingY, loadingSize);
        }
      }

      // cleanup
      g.dispose();
    }

    // draw the image
    // Graphics2D bsGraphics = (Graphics2D) bs.getDrawGraphics();
    // bsGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    // bsGraphics.drawImage(cachedBackground, 0, 0, null);
    // bsGraphics.drawImage(cachedImage, 0, 0, null);

    // cleanup
    // bsGraphics.dispose();
    // bs.show();
    g0.drawImage(cachedBackground, 0, 0, null);
    g0.drawImage(cachedImage, 0, 0, null);
    if (showControls) {
      drawControls();
    }
  }

  /**
   * temporary measure to refresh background. ideally we shouldn't need this (but we want to release
   * Lizzie 0.5 today, not tomorrow!). Refactor me out please! (you need to get blurring to work
   * properly on startup).
   */
  public void refreshBackground() {
    redrawBackgroundAnyway = true;
  }

  public void refresh() {
    repaint();
  }

  private Graphics2D createBackground(int width, int hight) {
    cachedBackground = new BufferedImage(width, hight, TYPE_INT_RGB);
    cachedBackgroundWidth = cachedBackground.getWidth();
    cachedBackgroundHeight = cachedBackground.getHeight();
    cachedBackgroundShowControls = showControls;
    cachedShowWinrate = Lizzie.config.showWinrate;
    cachedShowVariationGraph = Lizzie.config.showVariationGraph;
    cachedShowLargeSubBoard = Lizzie.config.showLargeSubBoard();
    cachedLargeWinrate = Lizzie.config.showLargeWinrate();
    cachedShowComment = Lizzie.config.showComment;
    cachedBoardPositionProportion = BoardPositionProportion;

    redrawBackgroundAnyway = false;

    Graphics2D g = cachedBackground.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    BufferedImage wallpaper = boardRenderer.getWallpaper();
    int drawWidth = max(wallpaper.getWidth(), mainPanel.getWidth());
    int drawHeight = max(wallpaper.getHeight(), mainPanel.getHeight());
    // Support seamless texture
    boardRenderer.drawTextureImage(g, wallpaper, 0, 0, drawWidth, drawHeight);

    return g;
  }

  private void drawContainer(Graphics g, int vx, int vy, int vw, int vh) {
    if (vw <= 0
        || vh <= 0
        || vx < cachedBackground.getMinX()
        || vx + vw > cachedBackground.getMinX() + cachedBackground.getWidth()
        || vy < cachedBackground.getMinY()
        || vy + vh > cachedBackground.getMinY() + cachedBackground.getHeight()) {
      return;
    }

    BufferedImage result = new BufferedImage(vw, vh, TYPE_INT_ARGB);
    filter20.filter(cachedBackground.getSubimage(vx, vy, vw, vh), result);
    g.drawImage(result, vx, vy, null);
    // g.setColor(Color.LIGHT_GRAY);
    // g.fillRect(vx, vy, vw, vh);
  }

  private void drawPonderingState(
      Graphics2D g, String text1, String text2, int x, int y, int y2, double size) {
    drawPonderingState(g, text1, x, y, size * 0.6);
    drawPonderingState(g, text2, x, y2, size * 0.4);
  }

  private void drawPonderingState(Graphics2D g, String text, int x, int y, double size) {
    int fontSize = (int) (max(mainPanel.getWidth(), mainPanel.getHeight()) * size);
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, fontSize);
    FontMetrics fm = g.getFontMetrics(font);
    int stringWidth = fm.stringWidth(text);
    // Truncate too long text when display switching prompt
    if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
      int mainBoardX = boardRenderer.getLocation().x;
      if (mainPanel.getWidth() > mainPanel.getHeight()
          && (mainBoardX > x)
          && stringWidth > (mainBoardX - x)) {
        text = truncateStringByWidth(text, fm, mainBoardX - x);
        stringWidth = fm.stringWidth(text);
      }
    }
    // Do nothing when no text
    if (stringWidth <= 0) {
      return;
    }
    int stringHeight = fm.getAscent() - fm.getDescent();
    int width = max(stringWidth, 1);
    int height = max((int) (stringHeight * 1.2), 1);

    BufferedImage result = new BufferedImage(width, height, TYPE_INT_ARGB);
    // commenting this out for now... always causing an exception on startup. will
    // fix in the
    // upcoming refactoring
    // filter20.filter(cachedBackground.getSubimage(x, y, result.getWidth(),
    // result.getHeight()), result);
    g.drawImage(result, x, y, null);

    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(x, y, width, height);
    g.drawRect(x, y, width, height);

    g.setColor(Color.white);
    g.setFont(font);
    g.drawString(
        text, x + (width - stringWidth) / 2, y + stringHeight + (height - stringHeight) / 2);
  }

  /**
   * @return a shorter, rounded string version of playouts. e.g. 345 -> 345, 1265 -> 1.3k, 44556 ->
   *     45k, 133523 -> 134k, 1234567 -> 1.2m
   */
  public String getPlayoutsString(int playouts) {
    if (playouts >= 1_000_000) {
      double playoutsDouble = (double) playouts / 100_000; // 1234567 -> 12.34567
      return round(playoutsDouble) / 10.0 + "m";
    } else if (playouts >= 10_000) {
      double playoutsDouble = (double) playouts / 1_000; // 13265 -> 13.265
      return round(playoutsDouble) + "k";
    } else if (playouts >= 1_000) {
      double playoutsDouble = (double) playouts / 100; // 1265 -> 12.65
      return round(playoutsDouble) / 10.0 + "k";
    } else {
      return String.valueOf(playouts);
    }
  }

  /**
   * Truncate text that is too long for the given width
   *
   * @param line
   * @param fm
   * @param fitWidth
   * @return fitted
   */
  private static String truncateStringByWidth(String line, FontMetrics fm, int fitWidth) {
    if (line.isEmpty()) {
      return "";
    }
    int width = fm.stringWidth(line);
    if (width > fitWidth) {
      int guess = line.length() * fitWidth / width;
      String before = line.substring(0, guess).trim();
      width = fm.stringWidth(before);
      if (width > fitWidth) {
        int diff = width - fitWidth;
        int i = 0;
        for (; (diff > 0 && i < 5); i++) {
          diff = diff - fm.stringWidth(line.substring(guess - i - 1, guess - i));
        }
        return line.substring(0, guess - i).trim();
      } else {
        return before;
      }
    } else {
      return line;
    }
  }

  private GaussianFilter filter20 = new GaussianFilter(20);
  private GaussianFilter filter10 = new GaussianFilter(10);

  /** Display the controls */
  void drawControls() {
    // userAlreadyKnowsAboutCommandString = true;

    cachedImage = new BufferedImage(mainPanel.getWidth(), mainPanel.getHeight(), TYPE_INT_ARGB);

    // redraw background
    createBackground(mainPanel.getWidth(), mainPanel.getHeight());

    List<String> commandsToShow = new ArrayList<>(Arrays.asList(commands));
    // if (Lizzie.leelaz.getDynamicKomi().isPresent()) {
    // commandsToShow.add(resourceBundle.getString("LizzieFrame.commands.keyD"));
    // }

    Graphics2D g = cachedImage.createGraphics();

    int maxSize = min(mainPanel.getWidth(), mainPanel.getHeight());
    int fontSize = (int) (maxSize * min(0.034, 0.80 / commandsToShow.size()));
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, fontSize);
    g.setFont(font);

    FontMetrics metrics = g.getFontMetrics(font);
    int maxCmdWidth = commandsToShow.stream().mapToInt(c -> metrics.stringWidth(c)).max().orElse(0);
    int lineHeight = (int) (font.getSize() * 1.28);

    int boxWidth = min((int) (maxCmdWidth * 1.4), mainPanel.getWidth());
    int boxHeight = min(commandsToShow.size() * lineHeight, mainPanel.getHeight());

    int commandsX = min(mainPanel.getWidth() / 2 - boxWidth / 2, mainPanel.getWidth());
    int top = mainPanel.getInsets().top;
    int commandsY =
        top + min((mainPanel.getHeight() - top) / 2 - boxHeight / 2, mainPanel.getHeight() - top);

    BufferedImage result = new BufferedImage(boxWidth, boxHeight, TYPE_INT_ARGB);
    filter10.filter(
        cachedBackground.getSubimage(commandsX, commandsY, boxWidth, boxHeight), result);
    g.drawImage(result, commandsX, commandsY, null);

    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(commandsX, commandsY, boxWidth, boxHeight);
    int strokeRadius = Lizzie.config.showBorder ? 2 : 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    if (Lizzie.config.showBorder) {
      g.setColor(new Color(0, 0, 0, 60));
      g.drawRect(
          commandsX + strokeRadius,
          commandsY + strokeRadius,
          boxWidth - 2 * strokeRadius,
          boxHeight - 2 * strokeRadius);
    }
    int verticalLineX = (int) (commandsX + boxWidth * 0.3);
    g.setColor(new Color(0, 0, 0, 60));
    g.drawLine(
        verticalLineX,
        commandsY + 2 * strokeRadius,
        verticalLineX,
        commandsY + boxHeight - 2 * strokeRadius);

    g.setStroke(new BasicStroke(1));

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(Color.WHITE);
    int lineOffset = commandsY;
    for (String command : commandsToShow) {
      String[] split = command.split("\\|");
      g.drawString(
          split[0],
          verticalLineX - metrics.stringWidth(split[0]) - strokeRadius * 4,
          font.getSize() + lineOffset);
      g.drawString(split[1], verticalLineX + strokeRadius * 4, font.getSize() + lineOffset);
      lineOffset += lineHeight;
    }

    refreshBackground();
  }

  // private boolean userAlreadyKnowsAboutCommandString = false;

  private void drawCommandString(Graphics2D g) {
    // if (userAlreadyKnowsAboutCommandString) return;

    int maxSize = (int) (min(mainPanel.getWidth(), mainPanel.getHeight()) * 0.98);

    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, (int) (maxSize * 0.023));
    String commandString = resourceBundle.getString("LizzieFrame.prompt.showControlsHint");
    int strokeRadius = Lizzie.config.showBorder ? 2 : 0;

    int showCommandsHeight = (int) (font.getSize() * 1.1);
    int showCommandsWidth = g.getFontMetrics(font).stringWidth(commandString) + 4 * strokeRadius;
    int showCommandsX = mainPanel.getInsets().left;
    int showCommandsY = mainPanel.getHeight() - showCommandsHeight - mainPanel.getInsets().bottom;
    // - this.getJMenuBar().getHeight();
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(showCommandsX, showCommandsY, showCommandsWidth, showCommandsHeight);
    if (Lizzie.config.showBorder) {
      g.setStroke(new BasicStroke(2 * strokeRadius));
      g.setColor(new Color(0, 0, 0, 60));
      g.drawRect(
          showCommandsX + strokeRadius,
          showCommandsY + strokeRadius,
          showCommandsWidth - 2 * strokeRadius,
          showCommandsHeight - 2 * strokeRadius);
    }
    g.setStroke(new BasicStroke(1));

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    g.setFont(font);
    g.drawString(commandString, showCommandsX + 2 * strokeRadius, showCommandsY + font.getSize());
  }

  private void drawMoveStatistics(Graphics2D g, int posX, int posY, int width, int height) {
    if (width < 0 || height < 0) return; // we don't have enough space

    double lastWR = 50; // winrate the previous move
    boolean validLastWinrate = false; // whether it was actually calculated
    Optional<BoardHistoryNode> previous =
        Lizzie.board.getHistory().getCurrentHistoryNode().previous();
    if (toolbar.isEnginePk && Lizzie.board.getHistory().getMoveNumber() > 3) {
      previous = Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().previous();
    }

    if (previous.isPresent() && previous.get().getData().getPlayouts() > 0) {
      lastWR = previous.get().getData().winrate;
      validLastWinrate = true;
    }
    if (toolbar.isEnginePk && Lizzie.board.getHistory().getMoveNumber() > 3) {
      lastWR = 100 - lastWR;
    }
    Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
    double curWR = stats.maxWinrate; // winrate on this move
    boolean validWinrate = (stats.totalPlayouts > 0); // and whether it was actually calculated
    if (!validWinrate) {
      curWR = Lizzie.board.getHistory().getData().winrate;
      validWinrate = Lizzie.board.getHistory().getData().getPlayouts() > 0;
    }
    if (isPlayingAgainstLeelaz
        && playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
      validWinrate = false;
    }

    if (!validWinrate) {
      curWR = 100 - lastWR; // display last move's winrate for now (with color difference)
    }
    double whiteWR, blackWR;
    if (Lizzie.board.getData().blackToPlay) {
      blackWR = curWR;
    } else {
      blackWR = 100 - curWR;
    }

    whiteWR = 100 - blackWR;

    // Background rectangle
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(posX, posY, width, height);

    // border. does not include bottom edge
    int strokeRadius = Lizzie.config.showBorder ? 3 : 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    g.drawLine(
        posX + strokeRadius, posY + strokeRadius, posX - strokeRadius + width, posY + strokeRadius);
    if (Lizzie.config.showBorder) {
      g.drawLine(
          posX + strokeRadius,
          posY + 3 * strokeRadius,
          posX + strokeRadius,
          posY - strokeRadius + height);
      g.drawLine(
          posX - strokeRadius + width,
          posY + 3 * strokeRadius,
          posX - strokeRadius + width,
          posY - strokeRadius + height);
    }

    // resize the box now so it's inside the border
    posX += 2 * strokeRadius;
    posY += 2 * strokeRadius;
    width -= 4 * strokeRadius;
    height -= 4 * strokeRadius;

    // Title
    strokeRadius = 2;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);

    if (width / 3 < height)
      setPanelFont(g, (int) min((width * 0.1), (min(width, height * 0.4) * 0.2)));
    else setPanelFont(g, (int) (min(width, height) * 0.2));

    // Last move
    // validLastWinrate && validWinrate
    if (true) {
      String text = "";
      // if (Lizzie.config.handicapInsteadOfWinrate) {
      // double currHandicapedWR = Lizzie.leelaz.winrateToHandicap(100 - curWR);
      // double lastHandicapedWR = Lizzie.leelaz.winrateToHandicap(lastWR);
      // text = String.format(": %.2f", currHandicapedWR - lastHandicapedWR);
      // } else {

      // }
      if (toolbar.isEnginePk && Lizzie.board.getHistory().getMoveNumber() <= 3) {
        text = "";
      }
      if (Lizzie.leelaz.isKatago) {
        if (!Lizzie.board.getHistory().getData().bestMoves.isEmpty()) {
          double score = Lizzie.leelaz.scoreMean;
          if (Lizzie.board.getHistory().isBlacksTurn()) {
            if (Lizzie.config.showKataGoBoardScoreMean) {
              score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
            }
          } else {
            if (Lizzie.config.showKataGoBoardScoreMean) {
              score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
            }
            if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
              score = -score;
            }
          }
          text = text + "目差:" + String.format("%.1f", score);
          text = text + " 复杂度:" + String.format("%.1f", Lizzie.leelaz.scoreStdev) + " ";
        }
      }
      if (Lizzie.leelaz.isColorEngine) {
        text = text + "阶段:" + Lizzie.leelaz.stage + " 贴目:" + Lizzie.leelaz.komi;
      } else {
        if (!komi.equals("7.5")) text = text + "贴目:" + komi;
      }
      if (!Lizzie.config.showName) {
        if (toolbar.isEnginePk) {
          text =
              text
                  + " 黑:"
                  + Lizzie.engineManager.engineList.get(toolbar.engineBlack).currentEnginename
                  + " 白:"
                  + Lizzie.engineManager.engineList.get(toolbar.engineWhite).currentEnginename;
        } else {
          text = text + playerTitle;
        }
      }
      text =
          text
              + " "
              + resourceBundle.getString("LizzieFrame.display.lastMove")
              + String.format(": %.1f%%", 100 - lastWR - curWR);
      g.drawString(
          text, posX + 2 * strokeRadius, posY + height - 2 * strokeRadius); // - font.getSize());
    } else {
      // I think it's more elegant to just not display anything when we don't have
      // valid data --dfannius
      // g.drawString(resourceBundle.getString("LizzieFrame.display.lastMove") + ":
      // ?%",
      // posX + 2 * strokeRadius, posY + height - 2 * strokeRadius);
    }

    if (validWinrate || validLastWinrate) {
      int maxBarwidth = (int) (width);
      int barWidthB = (int) (blackWR * maxBarwidth / 100);
      int barWidthW = (int) (whiteWR * maxBarwidth / 100);
      int barPosY = posY + height / 3;
      int barPosxB = (int) (posX);
      int barPosxW = barPosxB + barWidthB;
      int barHeight = height / 3;

      // Draw winrate bars
      g.fillRect(barPosxW, barPosY, barWidthW, barHeight);
      g.setColor(Color.BLACK);
      g.fillRect(barPosxB, barPosY, barWidthB, barHeight);

      // Show percentage above bars
      g.setColor(Color.WHITE);
      g.drawString(
          String.format("%.1f%%", blackWR),
          barPosxB + 2 * strokeRadius,
          posY + barHeight - 2 * strokeRadius);
      String winString = String.format("%.1f%%", whiteWR);
      int sw = g.getFontMetrics().stringWidth(winString);
      g.drawString(
          winString,
          barPosxB + maxBarwidth - sw - 2 * strokeRadius,
          posY + barHeight - 2 * strokeRadius);

      g.setColor(Color.GRAY);
      Stroke oldstroke = g.getStroke();
      Stroke dashed =
          new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {4}, 0);
      g.setStroke(dashed);

      for (int i = 1; i <= winRateGridLines; i++) {
        int x = barPosxB + (int) (i * (maxBarwidth / (winRateGridLines + 1)));
        g.drawLine(x, barPosY, x, barPosY + barHeight);
      }
      g.setStroke(oldstroke);
    }
  }

  private void drawCaptured(
      Graphics2D g, int posX, int posY, int width, int height, boolean isSmallCap) {
    // Draw border
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(posX, posY, width, height);

    // border. does not include bottom edge
    int strokeRadius = Lizzie.config.showBorder ? 3 : 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    if (Lizzie.config.showBorder) {
      g.drawLine(
          posX + strokeRadius,
          posY + strokeRadius,
          posX - strokeRadius + width,
          posY + strokeRadius);
      g.drawLine(
          posX + strokeRadius,
          posY + 3 * strokeRadius,
          posX + strokeRadius,
          posY - strokeRadius + height);
      g.drawLine(
          posX - strokeRadius + width,
          posY + 3 * strokeRadius,
          posX - strokeRadius + width,
          posY - strokeRadius + height);
    }

    // Draw middle line
    g.drawLine(
        posX - strokeRadius + width / 2,
        posY + 3 * strokeRadius,
        posX - strokeRadius + width / 2,
        posY - strokeRadius + height);
    g.setColor(Color.white);

    // Draw black and white "stone"
    int diam = height / 3;
    int smallDiam = diam / 2;
    int bdiam = diam, wdiam = diam;
    if (Lizzie.board.inScoreMode() || iscounting) {
      // do nothing
    } else if (Lizzie.board.getHistory().isBlacksTurn()) {
      wdiam = smallDiam;
    } else {
      bdiam = smallDiam;
    }
    g.setColor(Color.black);
    if (isSmallCap) {
      diam = diam * 3 / 2;
      bdiam = bdiam * 3 / 2;
      wdiam = wdiam * 3 / 2;
      g.fillOval(
          posX + width / 4 - bdiam / 2, posY + height * 2 / 8 + (diam - bdiam) / 2, bdiam, bdiam);

      g.setColor(Color.WHITE);
      g.fillOval(
          posX + width * 3 / 4 - wdiam / 2,
          posY + height * 2 / 8 + (diam - wdiam) / 2,
          wdiam,
          wdiam);
      // Status Indicator
      int statusDiam = 10;
      if ((height / 4) < 10) statusDiam = height / 4;

      g.setColor((Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) ? Color.GREEN : Color.RED);
      g.fillOval(
          posX - strokeRadius + width / 2 - statusDiam / 2,
          posY + height * 3 / 10 + (diam - statusDiam) / 2,
          statusDiam,
          statusDiam);
    } else {
      g.fillOval(
          posX + width / 4 - bdiam / 2, posY + height * 3 / 8 + (diam - bdiam) / 2, bdiam, bdiam);

      g.setColor(Color.WHITE);
      g.fillOval(
          posX + width * 3 / 4 - wdiam / 2,
          posY + height * 3 / 8 + (diam - wdiam) / 2,
          wdiam,
          wdiam);
      // Status Indicator
      int statusDiam = height / 8;
      g.setColor((Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) ? Color.GREEN : Color.RED);
      g.fillOval(
          posX - strokeRadius + width / 2 - statusDiam / 2,
          posY + height * 3 / 8 + (diam - statusDiam) / 2,
          statusDiam,
          statusDiam);
    }
    // Draw captures
    String bval = "", wval = "";
    if (isSmallCap) setPanelFont(g, (float) (min(width * 0.3, height) * 0.40));
    else setPanelFont(g, (float) (height * 0.18));
    if (Lizzie.board.inScoreMode()) {
      double score[] = Lizzie.board.getScore(Lizzie.board.scoreStones());
      bval = String.format("%.0f", score[0]);
      wval = String.format("%.1f", score[1]);
    } else if (iscounting || isAutocounting) {
      bval = String.format("%d", Lizzie.countResults.allblackcounts);
      wval = String.format("%d", Lizzie.countResults.allwhitecounts);
    } else {
      bval = String.format("%d", Lizzie.board.getData().blackCaptures);
      wval = String.format("%d", Lizzie.board.getData().whiteCaptures);
    }

    g.setColor(Color.WHITE);
    int bw = g.getFontMetrics().stringWidth(bval);
    int ww = g.getFontMetrics().stringWidth(wval);
    boolean largeSubBoard = Lizzie.config.showLargeSubBoard();
    int bx = (largeSubBoard ? diam : -bw / 2);
    int wx = (largeSubBoard ? bx : -ww / 2);

    g.drawString(bval, posX + width / 4 + bx, posY + height * 7 / 8);
    g.drawString(wval, posX + width * 3 / 4 + wx, posY + height * 7 / 8);
  }

  private void setPanelFont(Graphics2D g, float size) {
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, (int) size);
    g.setFont(font);
  }

  /**
   * Checks whether or not something was clicked and performs the appropriate action
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void onClickedForManul(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (blackorwhite == 0) Lizzie.board.placeForManul(coords[0], coords[1]);
      if (blackorwhite == 1) Lizzie.board.placeForManul(coords[0], coords[1], Stone.BLACK);
      if (blackorwhite == 2) Lizzie.board.placeForManul(coords[0], coords[1], Stone.WHITE);
    }
  }

  public void onClicked(int x, int y) {
    // Check for board click
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    int moveNumber = winrateGraph.moveNumber(x, y);

    if (boardCoordinates.isPresent()) {
      // 增加判断是否为插入模式

      int[] coords = boardCoordinates.get();
      startcoords[0] = coords[0];
      startcoords[1] = coords[1];
      draggedstone = Lizzie.board.getstonestat(coords);
      if (draggedstone == Stone.BLACK || draggedstone == Stone.WHITE) {
        draggedmovenumer = Lizzie.board.getmovenumber(coords);
        featurecat.lizzie.gui.Input.Draggedmode = true;
      }
      if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
      if (!isPlayingAgainstLeelaz || (playerIsBlack == Lizzie.board.getData().blackToPlay)) {
        if (!isAnaPlayingAgainstLeelaz
            || !Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected()
                == Lizzie.board.getData().blackToPlay) {
          if (blackorwhite == 0) Lizzie.board.place(coords[0], coords[1]);
          if (blackorwhite == 1) Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
          if (blackorwhite == 2) Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
        }
      }
    }
    if (Lizzie.config.showWinrate && moveNumber >= 0) {
      isPlayingAgainstLeelaz = false;
      noautocounting();
      Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
    }
    // if (Lizzie.config.showSubBoard && subBoardRenderer.isInside(x, y)) {
    // Lizzie.config.toggleLargeSubBoard();
    // }
    if (Lizzie.config.showVariationGraph && !Lizzie.frame.toolbar.isEnginePk) {
      variationTree.onClicked(x, y);
    }
    repaint();
  }

  public void noautocounting() {
    this.isAutocounting = false;
    try {
      Lizzie.frame.subBoardRenderer.removecountblock();
    } catch (Exception ex) {
    }
    Lizzie.frame.repaint();
    // Lizzie.frame.iscounting=false;
    Lizzie.countResults.isAutocounting = false;
    Lizzie.countResults.button2.setText("自动判断");
  }

  public void insertMove(int x, int y, boolean isblack) {
    // Check for board click
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      Lizzie.board.insertMove(coords, isblack);
    }
    repaint();
  }

  public int getmovenumber(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return Lizzie.board.getmovenumber(coords);
    }
    return -1;
  }

  public int getmovenumberinbranch(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return Lizzie.board.getmovenumberinbranch(Lizzie.board.getIndex(coords[0], coords[1]));
    }
    return -1;
  }

  public void allow() {

    // Lizzie.leelaz.analyzeAvoid();
  }

  public boolean iscoordsempty(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      return Lizzie.board.iscoordsempty(boardCoordinates.get()[0], boardCoordinates.get()[1]);
    }
    return false;
  }

  public String convertmousexy(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return Lizzie.board.convertCoordinatesToName(coords[0], coords[1]);
    }
    return "N";
  }

  public int[] convertmousexytocoords(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return coords;
    }
    return this.outOfBoundCoordinate;
  }

  public void onDoubleClicked(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (!isPlayingAgainstLeelaz) {
        int moveNumber = Lizzie.board.moveNumberByCoord(coords);
        if (moveNumber > 0) {
          Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
        }
      }
    }
  }

  public void insertMove(int x, int y) {
    // Check for board click
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      Lizzie.board.insertMove(coords);
    }
    repaint();
  }

  private final Consumer<String> placeVariation =
      v -> Board.asCoordinates(v).ifPresent(c -> Lizzie.board.place(c[0], c[1]));

  public boolean playCurrentVariation() {
    if (Lizzie.config.showSuggestionVaritions) {
      boardRenderer.variationOpt.ifPresent(vs -> vs.forEach(placeVariation));
      return boardRenderer.variationOpt.isPresent();
    } else {
      variationOpt.ifPresent(vs -> vs.forEach(placeVariation));
      return variationOpt.isPresent();
    }
  }

  public boolean isMouseOverSuggestions() {
    List<MoveData> bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    for (int i = 0; i < bestMoves.size(); i++) {
      Optional<int[]> c = Lizzie.board.asCoordinates(bestMoves.get(i).coordinate);
      if (c.isPresent()) {
        if (Lizzie.frame.isMouseOver2(c.get()[0], c.get()[1])) {
          List<String> variation = bestMoves.get(i).variation;
          variationOpt = Optional.of(variation);
          return true;
        }
      }
    }
    return false;
  }

  public void playBestMove() {
    boardRenderer.bestMoveCoordinateName().ifPresent(placeVariation);
  }

  public void onMouseMoved(int x, int y) {

    if (RightClickMenu.isVisible() || RightClickMenu2.isVisible()) {
      return;
    }

    if (isshowrightmenu) {
      isshowrightmenu = false;
    }

    mouseOverCoordinate = outOfBoundCoordinate;
    Optional<int[]> coords = boardRenderer.convertScreenToCoordinates(x, y);
    coords.filter(c -> !isMouseOver(c[0], c[1])).ifPresent(c -> repaint());
    coords.ifPresent(
        c -> {
          mouseOverCoordinate = c;
          if (Lizzie.frame != null) {
            Lizzie.frame.isMouseOver = boardRenderer.isShowingBranch();
          }
          isReplayVariation = false;
        });
    if (!isMouseOver) {
      clearMoved();
      repaint();
    }
    if (coords.isPresent()) {
      if (Lizzie.config.showrect) {
        boardRenderer.drawmoveblock(
            coords.get()[0], coords.get()[1], Lizzie.board.getHistory().isBlacksTurn());
      }
    } else {
      if (Lizzie.config.showrect) {
        boardRenderer.removeblock();
      }
    }
  }

  public void clearMoved() {
    isReplayVariation = false;
    if (Lizzie.frame != null) {
      Lizzie.frame.isMouseOver = false;
      boardRenderer.startNormalBoard();
    }
  }

  public boolean isMouseOver(int x, int y) {
    if (!Lizzie.frame.toolbar.chkShowBlack.isSelected()
        && !Lizzie.frame.toolbar.chkShowBlack.isSelected()) {
      return false;
    }
    if (Lizzie.config.showSuggestionVaritions)
      return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
    else return false;
  }

  public boolean isMouseOver2(int x, int y) {

    return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
  }

  public boolean isMouseOversub(int x, int y) {
    return suggestionclick[0] == x && suggestionclick[1] == y;
  }

  public void onMouseDragged(int x, int y) {
    int moveNumber = winrateGraph.moveNumber(x, y);
    if (Lizzie.config.showWinrate && moveNumber >= 0) {
      if (Lizzie.board.goToMoveNumberWithinBranch(moveNumber)) {
        repaint();
      }
    }
  }

  /**
   * Process Comment Mouse Wheel Moved
   *
   * @return true when the scroll event was processed by this method
   */
  public boolean processCommentMouseWheelMoved(MouseWheelEvent e) {
    if (Lizzie.config.showComment && commentRect.contains(e.getX(), e.getY())) {
      scrollPane.dispatchEvent(e);
      createCommentImage(true, commentRect.width, commentRect.height);
      mainPanel
          .getGraphics()
          .drawImage(
              cachedCommentImage,
              commentRect.x,
              commentRect.y,
              commentRect.width,
              commentRect.height,
              null);
      return true;
    } else {
      return false;
    }
  }

  public boolean processPressOnSub(MouseEvent e) {
    if (Lizzie.config.showSubBoard) {
      int x = e.getX();
      int y = e.getY();
      if (x >= subBoardXmouse
          && x <= subBoardXmouse + subBoardLengthmouse
          && y <= subBoardYmouse + subBoardLengthmouse
          && y >= subBoardYmouse) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          subBoardRenderer.bestmovesNum++;
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (subBoardRenderer.bestmovesNum >= 1) {
            subBoardRenderer.bestmovesNum--;
            repaint();
          }
        }

        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  public boolean processSubboardMouseWheelMoved(MouseWheelEvent e) {
    if (Lizzie.config.showSubBoard) {
      int x = e.getX();
      int y = e.getY();
      if (x >= subBoardXmouse
          && x <= subBoardXmouse + subBoardLengthmouse
          && y <= subBoardYmouse + subBoardLengthmouse
          && y >= subBoardYmouse) {

        if (e.getWheelRotation() > 0) {
          doBranchSub(1);
          refresh();
        } else if (e.getWheelRotation() < 0) {
          doBranchSub(-1);
          refresh();
        }

        return true;
      } else {
        return false;
      }
    }
    return false;
  }
  /**
   * Create comment cached image
   *
   * @param forceRefresh
   * @param w
   * @param h
   */
  public void createCommentImage(boolean forceRefresh, int w, int h) {
    if (forceRefresh || scrollPane.getWidth() != w || scrollPane.getHeight() != h) {
      if (w > 0 && h > 0) {
        scrollPane.setSize(w, h);
        cachedCommentImage =
            new BufferedImage(scrollPane.getWidth(), scrollPane.getHeight(), TYPE_INT_ARGB);
        Graphics2D g2 = cachedCommentImage.createGraphics();
        scrollPane.doLayout();
        scrollPane.addNotify();
        scrollPane.validate();
        scrollPane.printAll(g2);
        g2.dispose();
      }
    }
  }

  private void autosaveMaybe() {

    if (autoInterval > 0) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - lastAutosaveTime >= autoInterval) {
        Lizzie.board.autosave();
        lastAutosaveTime = currentTime;
        // Lizzie.board.updateComment();
      }
    }
    if (Lizzie.config.appendWinrateToComment && !Lizzie.frame.urlSgf) {
      long currentTime = System.currentTimeMillis();

      if (autoIntervalCom > 0 && currentTime - lastAutocomTime >= autoIntervalCom) {
        lastAutocomTime = currentTime;
        // Append the winrate to the comment
        if (Lizzie.leelaz != null && Lizzie.leelaz.isPondering() && !Lizzie.board.isLoadingFile) {
          // if (MoveData.getPlayouts(Lizzie.board.getHistory().getData().bestMoves) >
          // Lizzie.board.getHistory().getData().getPlayouts())
          // 重要!做保存文件时不更新的判断

          if (toolbar.isEnginePk) {
            if (Lizzie.board.getHistory().getData().comment.isEmpty())
              SGFParser.appendCommentForPk();
          } else {
            if (!Lizzie.board.isPkBoard) SGFParser.appendComment();
          }
        }
      }
    }
  }

  public void setPlayers(String whitePlayer, String blackPlayer) {
    playerTitle = String.format("([黑]%s vs[白]%s)", blackPlayer, whitePlayer);
    updateTitle();
  }

  public void setResult(String result) {
    resultTitle = String.format("(结果:%s)", result);
    updateTitle();
  }

  public void updateTitle() {
    if (isTrying) {
      return;
    }
    StringBuilder sb = new StringBuilder(DEFAULT_TITLE);
    sb.append(playerTitle);
    if (Lizzie.leelaz.isKatago) {

      double score = Lizzie.leelaz.scoreMean;
      if (Lizzie.board.getHistory().isBlacksTurn()) {
        if (Lizzie.config.showKataGoBoardScoreMean) {
          score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
        }
      } else {
        if (Lizzie.config.showKataGoBoardScoreMean) {
          score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
        }
        if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
          score = -score;
        }
      }
      sb.append(" 目差: " + String.format("%.1f", score));
    }
    sb.append(resultTitle);
    if (Lizzie.leelaz.engineCommand().length() < 100)
      sb.append(" [" + Lizzie.leelaz.engineCommand() + "]");
    else sb.append(" [" + Lizzie.leelaz.engineCommand().substring(0, 100) + "...]");
    sb.append(visitsString);
    setTitle(sb.toString());
  }

  private void setDisplayedBranchLength(int n) {
    boardRenderer.setDisplayedBranchLength(n);
  }

  private void setDisplayedBranchLengthSub(int n) {
    subBoardRenderer.setDisplayedBranchLength(n);
  }

  public void startRawBoard() {
    boolean onBranch = boardRenderer.isShowingBranch();
    int n = (onBranch ? 1 : BoardRenderer.SHOW_RAW_BOARD);
    boardRenderer.setDisplayedBranchLength(n);
  }

  public void stopRawBoard() {
    boardRenderer.setDisplayedBranchLength(BoardRenderer.SHOW_NORMAL_BOARD);
  }

  public boolean incrementDisplayedBranchLength(int n) {
    return boardRenderer.incrementDisplayedBranchLength(n);
  }

  public void resetTitle() {
    playerTitle = "";
    updateTitle();
  }

  public void copySgf() {
    try {
      // Get sgf content from game
      String sgfContent = SGFParser.saveToString();

      // Save to clipboard
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable transferableString = new StringSelection(sgfContent);
      clipboard.setContents(transferableString, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void pasteSgf() {
    // Get string from clipboard
    String sgfContent =
        Optional.ofNullable(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null))
            .filter(cc -> cc.isDataFlavorSupported(DataFlavor.stringFlavor))
            .flatMap(
                cc -> {
                  try {
                    return Optional.of((String) cc.getTransferData(DataFlavor.stringFlavor));
                  } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                  return Optional.empty();
                })
            .orElse("");

    // Load game contents from sgf string
    if (!sgfContent.isEmpty()) {
      SGFParser.loadFromString(sgfContent);
    }
  }

  public void increaseMaxAlpha(int k) {
    boardRenderer.increaseMaxAlpha(k);
  }

  /**
   * Draw the Comment of the Sgf file
   *
   * @param g
   * @param x
   * @param y
   * @param w
   * @param h
   */
  private void addText(String text) {
    try {
      htmlDoc.remove(0, htmlDoc.getLength());
      htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), text, 0, 0, null);
      commentPane.setCaretPosition(htmlDoc.getLength());
    } catch (BadLocationException | IOException e) {
      e.printStackTrace();
    }
  }

  private void drawComment(Graphics2D g, int x, int y, int w, int h) {

    String comment = Lizzie.board.getHistory().getData().comment;
    int fontSize = (int) (min(getWidth() * 0.6, getHeight()) * 0.0225);
    if (Lizzie.config.showLargeSubBoard() || Lizzie.config.showLargeWinrate()) {
      fontSize =
          (int)
              (min(
                      (getWidth() > 1.75 * getHeight() ? 1.75 * getHeight() : getWidth()) * 0.43,
                      getHeight())
                  * 0.0225);
    }
    // if (Lizzie.config.commentFontSize > 0) {
    // fontSize = Lizzie.config.commentFontSize;
    // } else if (fontSize < 12) {
    // fontSize = 12;
    // }

    if (Lizzie.config.commentFontSize != fontSize) {
      Lizzie.config.commentFontSize = fontSize;
      String style =
          "body {background:"
              + String.format(
                  "%02x%02x%02x",
                  Lizzie.config.commentBackgroundColor.getRed(),
                  Lizzie.config.commentBackgroundColor.getGreen(),
                  Lizzie.config.commentBackgroundColor.getBlue())
              + "; color:#"
              + String.format(
                  "%02x%02x%02x",
                  Lizzie.config.commentFontColor.getRed(),
                  Lizzie.config.commentFontColor.getGreen(),
                  Lizzie.config.commentFontColor.getBlue())
              + "; font-family:"
              + Lizzie.config.fontName
              + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
              + (Lizzie.config.commentFontSize > 0
                  ? "font-size:" + Lizzie.config.commentFontSize
                  : "")
              + "}";
      htmlStyle.addRule(style);
    }
    // commentPane.setFont(font);
    comment = comment.replaceAll("(\r\n)|(\n)", "<br />").replaceAll(" ", "&nbsp;");
    try {
      commentPane.setText(comment);
    } catch (Exception ex) {
    }
    commentPane.setSize(w, h);
    createCommentImage(!comment.equals(this.cachedComment), w, h);
    commentRect = new Rectangle(x, y, scrollPane.getWidth(), scrollPane.getHeight());

    g.drawImage(
        cachedCommentImage,
        commentRect.x,
        commentRect.y,
        commentRect.width,
        commentRect.height,
        null);
    cachedComment = comment;
  }

  public double lastWinrateDiff(BoardHistoryNode node) {

    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;

    // Current winrate
    BoardData data = node.getData();
    boolean validWinrate = false;
    double curWR = 50;
    if (data == Lizzie.board.getHistory().getData()) {
      Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
      curWR = stats.maxWinrate;
      validWinrate = (stats.totalPlayouts > 0);
      if (isPlayingAgainstLeelaz
          && playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
        validWinrate = false;
      }
    } else {
      validWinrate = (data.getPlayouts() > 0);
      curWR = validWinrate ? data.winrate : 100 - lastWR;
    }

    // Last move difference winrate
    if (validLastWinrate && validWinrate) {
      return 100 - lastWR - curWR;
    } else {
      return 0;
    }
  }

  public Color getBlunderNodeColor(BoardHistoryNode node) {
    if (Lizzie.config.nodeColorMode == 1 && node.getData().blackToPlay
        || Lizzie.config.nodeColorMode == 2 && !node.getData().blackToPlay) {
      return Color.WHITE;
    }
    double diffWinrate = lastWinrateDiff(node);
    Optional<Double> st =
        diffWinrate >= 0
            ? Lizzie.config.blunderWinrateThresholds.flatMap(
                l -> l.stream().filter(t -> (t > 0 && t <= diffWinrate)).reduce((f, s) -> s))
            : Lizzie.config.blunderWinrateThresholds.flatMap(
                l -> l.stream().filter(t -> (t < 0 && t >= diffWinrate)).reduce((f, s) -> f));
    if (st.isPresent()) {
      return Lizzie.config.blunderNodeColors.map(m -> m.get(st.get())).get();
    } else {
      return Color.WHITE;
    }
  }

  public void replayBranch() {
    if (isReplayVariation) return;
    int replaySteps = boardRenderer.getReplayBranch();
    if (replaySteps <= 0) return; // Bad steps or no branch
    int oriBranchLength = boardRenderer.getDisplayedBranchLength();
    isReplayVariation = true;
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    Runnable runnable =
        new Runnable() {
          public void run() {
            int secs = (int) (Lizzie.config.replayBranchIntervalSeconds * 1000);
            for (int i = 1; i < replaySteps + 1; i++) {
              if (!isReplayVariation) break;
              setDisplayedBranchLength(i);
              repaint();
              try {
                Thread.sleep(secs);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            boardRenderer.setDisplayedBranchLength(oriBranchLength);
            isReplayVariation = false;
            if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void replayBranchByWheel() {
    if (isReplayVariation) return;
    int replaySteps = boardRenderer.getReplayBranch();
    if (replaySteps <= 0) return; // Bad steps or no branch
    int oriBranchLength = boardRenderer.getDisplayedBranchLength();
    isReplayVariation = true;
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    Runnable runnable =
        new Runnable() {
          public void run() {
            int secs = (int) (Lizzie.config.replayBranchIntervalSeconds * 1000);
            try {
              Thread.sleep(500);
            } catch (InterruptedException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
            for (int i = 1; i < replaySteps + 1; i++) {
              if (!isReplayVariation) {
                Lizzie.frame.input.nowheelPress = false;
                break;
              }
              setDisplayedBranchLength(i);
              if (i > 1) Lizzie.frame.input.nowheelPress = true;
              repaint();
              try {
                Thread.sleep(secs);

              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            boardRenderer.setDisplayedBranchLength(oriBranchLength);
            isReplayVariation = false;
            if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void DraggedMoved(int x, int y) {
    if (RightClickMenu.isVisible() || RightClickMenu2.isVisible()) {
      return;
    }

    if (isshowrightmenu) {
      isshowrightmenu = false;
    }

    repaint();
  }

  public void DraggedDragged(int x, int y) {
    if (draggedstone != Stone.EMPTY) {
      Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();

        boardRenderer.drawmovestone(coords[0], coords[1], draggedstone);
        repaint();
      }
    }
  }

  public void DraggedReleased(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      if (draggedstone != Stone.BLACK && draggedstone != Stone.WHITE) {
        draggedstone = Stone.EMPTY;
        boardRenderer.removedrawmovestone();
        return;
      }

      int[] coords = boardCoordinates.get();
      if (coords[0] == startcoords[0] && coords[1] == startcoords[1]) {
        // System.out.println("拖动前后一致");
        draggedstone = Stone.EMPTY;
        boardRenderer.removedrawmovestone();
        repaint();
      } else {
        // System.out.println("拖动前后不一致");
        // System.out.println("拖动的棋子序号:"+draggedmovenumer);

        Stone stone = Lizzie.board.getstonestat(coords);
        if (stone != Stone.EMPTY) {
          boardRenderer.removedrawmovestone();
          repaint();
          draggedstone = Stone.EMPTY;
          return;
        }
        int currentmovenumber = Lizzie.board.getcurrentmovenumber();
        Lizzie.board.savelistforeditmode();
        Lizzie.board.editmovelist(
            Lizzie.board.tempallmovelist, draggedmovenumer, coords[0], coords[1]);
        Lizzie.board.clearforedit();
        Lizzie.board.setlist(Lizzie.board.tempallmovelist);
        Lizzie.board.goToMoveNumber(currentmovenumber);
        repaint();
      }
    }

    boardRenderer.removedrawmovestone();
    draggedstone = Stone.EMPTY;
    featurecat.lizzie.gui.Input.Draggedmode = false;
  }

  public void toggleheatmap() {
    if (!isheatmap) {
      Lizzie.leelaz.isheatmap = true;
      isheatmap = true;
      // if (!Lizzie.leelaz.isPondering()) lastponder = false;
      // else {
      // lastponder = true;
      // }
      //
      Lizzie.leelaz.sendCommand("heatmap");
      if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
    } else {
      isheatmap = false;
      Lizzie.leelaz.heatcount.clear();
      // if (lastponder) Lizzie.leelaz.ponder();
    }
  }

  public static class HtmlKit extends HTMLEditorKit {
    private StyleSheet style = new StyleSheet();

    @Override
    public void setStyleSheet(StyleSheet styleSheet) {
      style = styleSheet;
    }

    @Override
    public StyleSheet getStyleSheet() {
      if (style == null) {
        style = super.getStyleSheet();
      }
      return style;
    }
  }

  public void addSuggestionAsBranch() {
    boardRenderer.addSuggestionAsBranch();
  }

  public void doBranchSub(int moveTo) {
    if (subBoardRenderer.isShowingNormalBoard()) {
      setDisplayedBranchLengthSub(2);
      subBoardRenderer.wheeled = true;
    } else if (moveTo > 0) {
      {
        if (subBoardRenderer.getReplayBranch() > subBoardRenderer.getDisplayedBranchLength()) {
          subBoardRenderer.incrementDisplayedBranchLength(1);
          subBoardRenderer.wheeled = true;
        }
      }

    } else {
      if (subBoardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLengthSub(subBoardRenderer.getReplayBranch());
      } else {
        if (subBoardRenderer.getDisplayedBranchLength() > 1) {
          subBoardRenderer.incrementDisplayedBranchLength(-1);
          subBoardRenderer.wheeled = true;
        }
      }
    }
  }

  public void doBranch(int moveTo) {
    if (moveTo > 0) {
      if (boardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLength(2);
      } else {
        if (boardRenderer.getReplayBranch() > boardRenderer.getDisplayedBranchLength()) {
          boardRenderer.incrementDisplayedBranchLength(1);
        }
      }
    } else {
      if (boardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLength(boardRenderer.getReplayBranch());
      } else {
        if (boardRenderer.getDisplayedBranchLength() > 1) {
          boardRenderer.incrementDisplayedBranchLength(-1);
        }
      }
    }
  }

  public void saveImage() {
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    chooser.setAcceptAllFileFilterUsed(false);
    //    String writerNames[] = ImageIO.getWriterFormatNames();
    FileNameExtensionFilter filter1 = new FileNameExtensionFilter("*.png", "PNG");
    FileNameExtensionFilter filter2 = new FileNameExtensionFilter("*.jpg", "JPG", "JPEG");
    FileNameExtensionFilter filter3 = new FileNameExtensionFilter("*.gif", "GIF");
    FileNameExtensionFilter filter4 = new FileNameExtensionFilter("*.bmp", "BMP");
    chooser.addChoosableFileFilter(filter1);
    chooser.addChoosableFileFilter(filter2);
    chooser.addChoosableFileFilter(filter3);
    chooser.addChoosableFileFilter(filter4);
    chooser.setMultiSelectionEnabled(false);
    int result = chooser.showSaveDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (file.exists()) {
        int ret =
            JOptionPane.showConfirmDialog(null, "文件已存在,是否覆盖?", "提示", JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION) {
          return;
        }
      }
      String ext =
          chooser.getFileFilter() instanceof FileNameExtensionFilter
              ? ((FileNameExtensionFilter) chooser.getFileFilter()).getExtensions()[0].toLowerCase()
              : "";
      if (!Utils.isBlank(ext)) {
        if (!file.getPath().toLowerCase().endsWith("." + ext)) {
          file = new File(file.getPath() + "." + ext);
        }
      }
      BufferedImage bImg =
          new BufferedImage(
              this.mainPanel.getWidth(), this.mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();

      this.mainPanel.paintAll(cg);
      try {
        ImageIO.write(bImg, ext, file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void saveImage(int x, int y, int width, int height) {
    boolean oriShowName = Lizzie.config.showName;
    Lizzie.config.showName = false;
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    chooser.setAcceptAllFileFilterUsed(false);
    //    String writerNames[] = ImageIO.getWriterFormatNames();
    FileNameExtensionFilter filter1 = new FileNameExtensionFilter("*.png", "PNG");
    FileNameExtensionFilter filter2 = new FileNameExtensionFilter("*.jpg", "JPG", "JPEG");
    FileNameExtensionFilter filter3 = new FileNameExtensionFilter("*.gif", "GIF");
    FileNameExtensionFilter filter4 = new FileNameExtensionFilter("*.bmp", "BMP");
    chooser.addChoosableFileFilter(filter1);
    chooser.addChoosableFileFilter(filter2);
    chooser.addChoosableFileFilter(filter3);
    chooser.addChoosableFileFilter(filter4);
    chooser.setMultiSelectionEnabled(false);
    int result = chooser.showSaveDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (file.exists()) {
        int ret =
            JOptionPane.showConfirmDialog(null, "文件已存在,是否覆盖?", "提示", JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION) {
          return;
        }
      }
      String ext =
          chooser.getFileFilter() instanceof FileNameExtensionFilter
              ? ((FileNameExtensionFilter) chooser.getFileFilter()).getExtensions()[0].toLowerCase()
              : "";
      if (!Utils.isBlank(ext)) {
        if (!file.getPath().toLowerCase().endsWith("." + ext)) {
          file = new File(file.getPath() + "." + ext);
        }
      }
      BufferedImage bImg =
          new BufferedImage(
              this.mainPanel.getWidth(), this.mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();

      this.mainPanel.paintAll(cg);

      // 截取图片
      Rectangle rect = new Rectangle(x, y, width, height);
      BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
      // 新建一个40*40的Image
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

      try {
        ImageIO.write(buffImg, ext, file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    Lizzie.config.showName = oriShowName;
  }

  public void saveImage(int x, int y, int width, int height, String path) {
    boolean oriShowName = Lizzie.config.showName;
    Lizzie.config.showName = false;
    File file = new File(path);

    BufferedImage bImg =
        new BufferedImage(
            this.mainPanel.getWidth(), this.mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D cg = bImg.createGraphics();

    this.mainPanel.paintAll(cg);

    // 截取图片
    Rectangle rect = new Rectangle(x, y, width, height);
    BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
    // 新建一个40*40的Image
    BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    buffImg
        .getGraphics()
        .drawImage(
            areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

    try {
      ImageIO.write(buffImg, "png", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Lizzie.config.showName = oriShowName;
  }
}
