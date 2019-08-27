package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.json.JSONArray;

@SuppressWarnings("serial")
public class LoadEngine extends JPanel {
  public static Config config;
  public TableModel dataModel;
  JPanel tablepanel;
  JPanel selectpanel = new JPanel();

  JScrollPane scrollpane;
  public static JTable table;
  public static JLabel checkBlacktxt;
  public static JLabel checkWhitetxt;
  Font headFont;
  Font winrateFont;
  static JDialog engjf;
  Timer timer;
  int sortnum = 3;
  public static int selectedorder = -1;
  boolean issorted = false;
  boolean firstConf = true;
  // JSpinner dropwinratechooser = new JSpinner(new SpinnerNumberModel(1, 0, 99,
  // 1));
  // JSpinner playoutschooser = new JSpinner(new SpinnerNumberModel(100, 0, 99999,
  // 100));
  // JCheckBox checkBlack = new JCheckBox();
  // JCheckBox checkWhite = new JCheckBox();
  // JTextArea command;
  // JTextField txtName;
  // JLabel engineName;
  // JCheckBox preload;
  // JTextField txtWidth;
  // JTextField txtHeight;
  // JTextField txtKomi;
  //
  // JButton scan;
  // JButton delete;
  JButton ok;
  JButton noEngine;
  JButton exit;
  // JCheckBox chkdefault;
  JRadioButton rdoDefault;
  JRadioButton rdoLast;
  JRadioButton rdoMannul;
  int curIndex = -1;

  public String enginePath = "";
  public String weightPath = "";
  public String commandHelp = "";
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private String osName;
  private BufferedInputStream inputStream;
  private Path curPath;

  public LoadEngine() {
    // super(new BorderLayout());

    curPath = (new File("")).getAbsoluteFile().toPath();
    osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    this.setLayout(null);
    dataModel = getTableModel();
    table = new JTable(dataModel);
    selectpanel.setLayout(null);
    winrateFont = new Font("微软雅黑", Font.PLAIN, 14);
    headFont = new Font("微软雅黑", Font.PLAIN, 13);

    table.getTableHeader().setFont(headFont);
    table.setFont(winrateFont);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setResizingAllowed(false);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.setRowHeight(20);

    tablepanel = new JPanel(new BorderLayout());
    tablepanel.setBounds(0, 0, 885, 232);
    this.add(tablepanel);
    selectpanel.setBounds(0, 232, 900, 330);
    this.add(selectpanel);
    scrollpane = new JScrollPane(table);

    tablepanel.add(scrollpane);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(30);
    table.getColumnModel().getColumn(1).setPreferredWidth(200);
    table.getColumnModel().getColumn(2).setPreferredWidth(350);
    table.getColumnModel().getColumn(3).setPreferredWidth(40);
    table.getColumnModel().getColumn(4).setPreferredWidth(20);
    table.getColumnModel().getColumn(5).setPreferredWidth(20);
    table.getColumnModel().getColumn(6).setPreferredWidth(30);
    table.getColumnModel().getColumn(7).setPreferredWidth(30);
    // boolean persisted = Lizzie.config.persistedUi != null;
    // if (persisted
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length()
    // == 12) {
    // JSONArray pos =
    // Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
    // // table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
    // // table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
    // // table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
    // // table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
    // }

    JTableHeader header = table.getTableHeader();

    // dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    // playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    // checkBlack.setSelected(true);
    // checkWhite.setSelected(true);

    ok = new JButton("加载选中引擎");
    noEngine = new JButton("不加载引擎");
    exit = new JButton("退出");

    noEngine.setFocusable(false);
    noEngine.setMargin(new Insets(0, 0, 0, 0));
    exit.setFocusable(false);
    exit.setMargin(new Insets(0, 0, 0, 0));
    ok.setFocusable(false);
    ok.setMargin(new Insets(0, 0, 0, 0));

    JLabel lblchooseStart = new JLabel("每次启动：");
    rdoDefault = new JRadioButton();
    JLabel lblrdoDefault = new JLabel("自动加载选择的引擎");
    rdoLast = new JRadioButton();
    JLabel lblrdoLast = new JLabel("自动加载上次退出的引擎");
    rdoMannul = new JRadioButton();
    JLabel lblrdoMannul = new JLabel("手动选择");

    ok.setBounds(600, 20, 80, 22);
    noEngine.setBounds(700, 20, 80, 22);
    exit.setBounds(800, 20, 80, 22);

    lblchooseStart.setBounds(5, 0, 60, 20);
    rdoDefault.setBounds(60, 0, 20, 20);
    lblrdoDefault.setBounds(80, 0, 150, 20);
    rdoLast.setBounds(190, 0, 20, 20);
    lblrdoLast.setBounds(210, 0, 200, 20);
    rdoMannul.setBounds(345, 0, 20, 20);
    lblrdoMannul.setBounds(365, 0, 60, 20);
    ButtonGroup startGroup = new ButtonGroup();
    startGroup.add(rdoDefault);
    startGroup.add(rdoLast);
    startGroup.add(rdoMannul);

    selectpanel.add(ok);
    selectpanel.add(noEngine);
    selectpanel.add(exit);

    selectpanel.add(lblchooseStart);
    selectpanel.add(rdoDefault);
    selectpanel.add(lblrdoDefault);
    selectpanel.add(rdoLast);
    selectpanel.add(lblrdoLast);
    selectpanel.add(rdoMannul);
    selectpanel.add(lblrdoMannul);

    ok.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            if (curIndex < 0) {
              JOptionPane.showMessageDialog(engjf, "请先选择一个引擎 ");
              return;
            }
            engjf.setVisible(false);
            Lizzie.config.uiConfig.put("default-engine", curIndex);
            if (rdoDefault.isSelected()) {
              Lizzie.config.uiConfig.put("autoload-default", true);
            } else {
              Lizzie.config.uiConfig.put("autoload-last", false);
            }

            if (rdoLast.isSelected()) {
              Lizzie.config.uiConfig.put("autoload-last", true);
              Lizzie.config.uiConfig.put("autoload-default", true);
            }
            if (rdoMannul.isSelected()) {
              Lizzie.config.uiConfig.put("autoload-last", false);
              Lizzie.config.uiConfig.put("autoload-default", false);
            }

            try {
              Lizzie.config.save();
            } catch (IOException es) {
            }
            Lizzie.start(curIndex);
          }
        });
    exit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            System.exit(0);
          }
        });
    noEngine.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            engjf.setVisible(false);
            if (rdoDefault.isSelected()) {
              Lizzie.config.uiConfig.put("autoload-default", true);
            } else {
              Lizzie.config.uiConfig.put("autoload-last", false);
            }

            if (rdoLast.isSelected()) {
              Lizzie.config.uiConfig.put("autoload-last", true);
              Lizzie.config.uiConfig.put("autoload-default", true);
            }
            if (rdoMannul.isSelected()) {
              Lizzie.config.uiConfig.put("autoload-last", false);
              Lizzie.config.uiConfig.put("autoload-default", false);
            }

            try {
              Lizzie.config.save();
            } catch (IOException es) {
            }
            Lizzie.start(-1);
          }
        });

    table.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (e.getClickCount() == 2) {
              if (row >= 0 && col >= 0) {
                try {
                  handleTableDoubleClick(row, col);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
            } else {
              try {
                handleTableClick(row, col);
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }
        });
    table.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            // if (e.getKeyCode() == KeyEvent.VK_B) {
            // Lizzie.frame.toggleBadMoves();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_U) {
            // Lizzie.frame.toggleBestMoves();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // if (Lizzie.frame.isPlayingAgainstLeelaz) {
            // Lizzie.frame.isPlayingAgainstLeelaz = false;
            // Lizzie.leelaz.isThinking = false;
            // }
            // Lizzie.leelaz.togglePonder();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_Q) {
            // togglealwaysontop();
            // }
          }
        });

    header.addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            // int pick = header.columnAtPoint(e.getPoint());
            // sortnum = pick;
            // issorted = !issorted;
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      // if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
      // 2).toString())[0]
      // == Lizzie.frame.clickbadmove[0]
      // && Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
      // 2).toString())[1]
      // == Lizzie.frame.clickbadmove[1]) {

      // Color hsbColor =
      // Color.getHSBColor(
      // Color.RGBtoHSB(238, 221, 130, null)[0],
      // Color.RGBtoHSB(238, 221, 130, null)[1],
      // Color.RGBtoHSB(238, 221, 130, null)[2]);
      // setBackground(hsbColor);
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      // && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      // Color hsbColor2 =
      // Color.getHSBColor(
      // Color.RGBtoHSB(255, 153, 18, null)[0],
      // Color.RGBtoHSB(255, 153, 18, null)[1],
      // Color.RGBtoHSB(255, 153, 18, null)[2]);
      // setForeground(hsbColor2);
      // } else if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >
      // 10) {
      // setForeground(Color.RED);
      // } else {
      // setForeground(Color.BLACK);
      // }
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // }
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      // && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      // Color hsbColor =
      // Color.getHSBColor(
      // Color.RGBtoHSB(255, 153, 18, null)[0],
      // Color.RGBtoHSB(255, 153, 18, null)[1],
      // Color.RGBtoHSB(255, 153, 18, null)[2]);
      // setBackground(Color.WHITE);
      // setForeground(hsbColor);
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // }
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) > 10) {
      // setBackground(Color.WHITE);
      // setForeground(Color.RED);
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // } else
      {
        return renderer.getTableCellRendererComponent(table, value, isSelected, false, row, column);
      }
    }
  }

  // private void togglealwaysontop() {
  // if (engjf.isAlwaysOnTop()) {
  // engjf.setAlwaysOnTop(false);
  // Lizzie.config.uiConfig.put("badmoves-always-ontop", false);
  // } else {
  // engjf.setAlwaysOnTop(true);
  // Lizzie.config.uiConfig.put("badmoves-always-ontop", true);
  // if (Lizzie.frame.isAlwaysOnTop()) Lizzie.frame.toggleAlwaysOntop();
  // }
  // try {
  // Lizzie.config.save();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  public boolean isWindows() {
    return osName != null && !osName.contains("darwin") && osName.contains("win");
  }

  private String getEngineLine() {
    String engineLine = "";
    File engineFile = null;
    File weightFile = null;
    JFileChooser chooser = new JFileChooser(".");
    if (isWindows()) {
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(
              resourceBundle.getString("LizzieConfig.title.engine"), "exe", "bat");
      chooser.setFileFilter(filter);
    } else {
      setVisible(false);
    }
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectEngine"));
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      engineFile = chooser.getSelectedFile();
      if (engineFile != null) {
        enginePath = engineFile.getAbsolutePath();
        enginePath = relativizePath(engineFile.toPath());
        getCommandHelp();
        JFileChooser chooserw = new JFileChooser(".");
        chooserw.setMultiSelectionEnabled(false);
        chooserw.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectWeight"));
        result = chooserw.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
          weightFile = chooserw.getSelectedFile();
          if (weightFile != null) {
            weightPath = relativizePath(weightFile.toPath());
            EngineParameter ep = new EngineParameter(enginePath, weightPath, commandHelp);
            ep.setVisible(true);
            if (!ep.commandLine.isEmpty()) {
              engineLine = ep.commandLine;
            }
          }
        }
      }
    }
    return engineLine;
  }

  private String relativizePath(Path path) {
    Path relatPath;
    if (path.startsWith(curPath)) {
      relatPath = curPath.relativize(path);
    } else {
      relatPath = path;
    }
    return relatPath.toString();
  }

  private void getCommandHelp() {

    List<String> commands = new ArrayList<String>();
    commands.add(enginePath);
    commands.add("-h");

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.directory();
    processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
      inputStream = new BufferedInputStream(process.getInputStream());
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
      }
      commandHelp = line.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleTableClick(int row, int col) {

    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
  }

  private void handleTableDoubleClick(int row, int col) {
    engjf.setVisible(false);
    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
    Lizzie.config.uiConfig.put("default-engine", curIndex);
    if (rdoDefault.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-default", true);
    } else {
      Lizzie.config.uiConfig.put("autoload-last", false);
    }

    if (rdoLast.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-last", true);
      Lizzie.config.uiConfig.put("autoload-default", true);
    }
    if (rdoMannul.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-last", false);
      Lizzie.config.uiConfig.put("autoload-default", false);
    }

    try {
      Lizzie.config.save();
    } catch (IOException es) {
    }

    Lizzie.start(curIndex);
  }

  private ArrayList<EngineData> getEngineData() {
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
    if (engineData.size() == 0) {
      if (firstConf) {
        firstConf = false;
        Lizzie.frame.openConfigDialog();
        engjf.setVisible(false);
      } else {

      }
    }
    return engineData;
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 8;
      }

      public int getRowCount() {
        ArrayList<EngineData> EngineDatas = getEngineData();
        return EngineDatas.size();
      }

      public String getColumnName(int column) {

        if (column == 0) return "序号";
        if (column == 1) return "名称";
        if (column == 2) return "命令行";
        if (column == 3) return "预加载";
        if (column == 4) return "宽";
        if (column == 5) return "高";
        if (column == 6) return "贴目";
        if (column == 7) return "默认";

        return "无";
      }

      @SuppressWarnings("unchecked")
      public Object getValueAt(int row, int col) {
        ArrayList<EngineData> EngineDatas = getEngineData();
        if (row > (EngineDatas.size() - 1)) {
          if (col == 0) return row + 1;
          return "";
        }
        EngineData data = EngineDatas.get(row);

        if (col != 0 && data.commands.equals("")) {
          return "";
        }

        switch (col) {
          case 0:
            return data.index + 1;
          case 1:
            return data.name;
          case 2:
            return data.commands;
          case 3:
            if (data.preload) return "是";
            return "否";
          case 4:
            return data.width;
          case 5:
            return data.height;
          case 6:
            return data.komi;
          case 7:
            if (data.isDefault) return "是";
            else return "否";
          default:
            return "";
        }
      }
    };
  }

  public static JDialog createBadmovesDialog() {
    // Create and set up the window.
    engjf = new JDialog();
    engjf.setTitle("选择要加载的引擎(双击加载)");

    engjf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            engjf.setVisible(false);
          }
        });

    final LoadEngine newContentPane = new LoadEngine();
    newContentPane.setOpaque(true); // content panes must be opaque
    engjf.setContentPane(newContentPane);
    engjf.setModal(true);
    // Display the window.
    // jf.setSize(521, 320);

    // boolean persisted = Lizzie.config.persistedUi != null;
    engjf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        });
    engjf.setBounds(50, 50, 900, 320);
    engjf.setResizable(false);
    try {
      engjf.setIconImage(ImageIO.read(LoadEngine.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    engjf.setAlwaysOnTop(true);
    engjf.setLocationRelativeTo(engjf.getOwner());
    // jf.setResizable(false);
    return engjf;
  }
}
