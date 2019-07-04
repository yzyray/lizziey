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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.json.JSONArray;

@SuppressWarnings("serial")
public class MoreEngines extends JPanel {
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
  JSpinner dropwinratechooser = new JSpinner(new SpinnerNumberModel(1, 0, 99, 1));
  JSpinner playoutschooser = new JSpinner(new SpinnerNumberModel(100, 0, 99999, 100));
  JCheckBox checkBlack = new JCheckBox();
  JCheckBox checkWhite = new JCheckBox();
  JTextArea command;
  JTextField txtName;
  JLabel engineName;
  JCheckBox preload;
  JTextField txtWidth;
  JTextField txtHeight;
  JButton save;
  JButton cancel;
  JButton exit;
  JCheckBox chkdefault;
  JRadioButton rdoDefault;
  JRadioButton rdoLast;
  JRadioButton rdoMannul;
  int curIndex = -1;

  public MoreEngines() {
    // super(new BorderLayout());
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
    tablepanel.setBounds(0, 330, 685, 432);
    this.add(tablepanel, BorderLayout.SOUTH);
    selectpanel.setBounds(0, 0, 700, 330);
    this.add(selectpanel, BorderLayout.NORTH);
    scrollpane = new JScrollPane(table);

    tablepanel.add(scrollpane);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(30);
    table.getColumnModel().getColumn(1).setPreferredWidth(100);
    table.getColumnModel().getColumn(2).setPreferredWidth(370);
    table.getColumnModel().getColumn(3).setPreferredWidth(40);
    table.getColumnModel().getColumn(4).setPreferredWidth(20);
    table.getColumnModel().getColumn(5).setPreferredWidth(20);
    table.getColumnModel().getColumn(6).setPreferredWidth(30);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length() == 12) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
      //      table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
      //      table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
      //      table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
      //      table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
    }

    JTableHeader header = table.getTableHeader();

    //    dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    //    playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    //    checkBlack.setSelected(true);
    //    checkWhite.setSelected(true);

    engineName = new JLabel("单机选中列表中的引擎进行设置");
    engineName.setFont(new Font("微软雅黑", Font.PLAIN, 14));
    JLabel lblname = new JLabel("名称：");
    txtName = new JTextField();
    command = new JTextArea(5, 80);
    command.setLineWrap(true);
    command.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    JLabel lblcommand = new JLabel("命令行：");
    preload = new JCheckBox();
    JLabel lblpreload = new JLabel("预加载");
    JLabel lblWidth = new JLabel("默认棋盘 宽：");
    JLabel lblHeight = new JLabel("高：");

    txtWidth = new JTextField();
    txtHeight = new JTextField();
    save = new JButton("保存");
    cancel = new JButton("取消");
    exit = new JButton("退出");
    save.setFocusable(false);
    save.setMargin(new Insets(0, 0, 0, 0));
    cancel.setFocusable(false);
    cancel.setMargin(new Insets(0, 0, 0, 0));
    exit.setFocusable(false);
    exit.setMargin(new Insets(0, 0, 0, 0));

    chkdefault = new JCheckBox();
    JLabel lbldefault = new JLabel("默认引擎");
    JLabel lblchooseStart = new JLabel("启动时：");
    rdoDefault = new JRadioButton();
    JLabel lblrdoDefault = new JLabel("自动加载默认引擎");
    rdoLast = new JRadioButton();
    JLabel lblrdoLast = new JLabel("自动加载上次退出的引擎");
    rdoMannul = new JRadioButton();
    JLabel lblrdoMannul = new JLabel("手动选择");

    engineName.setBounds(5, 5, 500, 20);
    txtName.setBounds(50, 35, 600, 20);
    lblname.setBounds(5, 35, 45, 20);
    lblcommand.setBounds(5, 65, 50, 20);
    command.setBounds(50, 65, 600, 200);
    preload.setBounds(47, 271, 20, 20);
    lblpreload.setBounds(70, 271, 50, 20);
    lblWidth.setBounds(120, 271, 80, 20);
    txtWidth.setBounds(190, 272, 30, 20);
    lblHeight.setBounds(225, 271, 30, 20);
    txtHeight.setBounds(245, 272, 30, 20);
    save.setBounds(510, 270, 40, 22);
    cancel.setBounds(560, 270, 40, 22);
    exit.setBounds(610, 270, 40, 22);

    chkdefault.setBounds(280, 271, 20, 20);
    lbldefault.setBounds(300, 271, 60, 20);

    lblchooseStart.setBounds(5, 300, 60, 20);
    rdoDefault.setBounds(60, 300, 20, 20);
    lblrdoDefault.setBounds(80, 300, 150, 20);
    rdoLast.setBounds(180, 300, 20, 20);
    lblrdoLast.setBounds(200, 300, 200, 20);
    rdoMannul.setBounds(335, 300, 20, 20);
    lblrdoMannul.setBounds(355, 300, 60, 20);
    ButtonGroup startGroup = new ButtonGroup();
    startGroup.add(rdoDefault);
    startGroup.add(rdoLast);
    startGroup.add(rdoMannul);
    if (Lizzie.config.uiConfig.optBoolean("autoload-default", false)) {
      if (Lizzie.config.uiConfig.optBoolean("autoload-last", false)) rdoLast.setSelected(true);
      else rdoDefault.setSelected(true);
    } else {
      rdoMannul.setSelected(true);
    }
    //    checkBlacktxt = new JLabel("黑:");
    //    checkWhitetxt = new JLabel("白:");
    //    JLabel dropwinratechoosertxt = new JLabel("胜率波动筛选:");
    //    JLabel playoutschoosertxt = new JLabel("前后计算量筛选:");
    selectpanel.add(engineName);
    selectpanel.add(lblname);
    selectpanel.add(txtName);
    selectpanel.add(command);
    selectpanel.add(lblcommand);
    selectpanel.add(lblpreload);
    selectpanel.add(preload);
    selectpanel.add(lblWidth);
    selectpanel.add(txtWidth);
    selectpanel.add(lblHeight);
    selectpanel.add(txtHeight);
    selectpanel.add(save);
    selectpanel.add(cancel);
    selectpanel.add(exit);
    selectpanel.add(chkdefault);
    selectpanel.add(lbldefault);

    selectpanel.add(lblchooseStart);
    selectpanel.add(rdoDefault);
    selectpanel.add(lblrdoDefault);
    selectpanel.add(rdoLast);
    selectpanel.add(lblrdoLast);
    selectpanel.add(rdoMannul);
    selectpanel.add(lblrdoMannul);

    //
    //    selectpanel.add(checkBlacktxt);
    //    selectpanel.add(checkBlack);
    //    selectpanel.add(checkWhitetxt);
    //    selectpanel.add(checkWhite);
    //    selectpanel.add(dropwinratechoosertxt);
    //    selectpanel.add(dropwinratechooser);
    //    selectpanel.add(playoutschoosertxt);
    //    selectpanel.add(playoutschooser);

    //    playoutschooser.addChangeListener(
    //        new ChangeListener() {
    //
    //          public void stateChanged(ChangeEvent e) {
    //
    //            Lizzie.config.leelazConfig.putOpt(
    //                "badmoves-playouts-limits", playoutschooser.getValue());
    //            try {
    //              Lizzie.config.save();
    //            } catch (IOException e1) {
    //              // TODO Auto-generated catch block
    //              e1.printStackTrace();
    //            }
    //          }
    //        });
    //
    //    dropwinratechooser.addChangeListener(
    //        new ChangeListener() {
    //
    //          public void stateChanged(ChangeEvent e) {
    //
    //            Lizzie.config.leelazConfig.putOpt(
    //                "badmoves-winrate-limits", dropwinratechooser.getValue());
    //            try {
    //              Lizzie.config.save();
    //            } catch (IOException e1) {
    //              // TODO Auto-generated catch block
    //              e1.printStackTrace();
    //            }
    //          }
    //        });

    exit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            engjf.setVisible(false);
          }
        });
    cancel.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            command.setText("");
            engineName.setText("单机选中列表中的引擎进行设置");
            txtName.setText("");
            preload.setSelected(false);
            txtWidth.setText("");
            txtHeight.setText("");
            chkdefault.setSelected(false);
            curIndex = -1;
          }
        });
    save.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkdefault.isSelected()) Lizzie.config.uiConfig.put("default-engine", curIndex);
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
            table.validate();
            table.updateUI();
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
              if (row >= 0 && col >= 0) {
                if (e.getButton() == MouseEvent.BUTTON3)
                  try {
                    handleTableDoubleClick(row, col);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                else
                  try {
                    handleTableClick(row, col);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
              }
            }
          }
        });
    table.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            //            if (e.getKeyCode() == KeyEvent.VK_B) {
            //              Lizzie.frame.toggleBadMoves();
            //            }
            //            if (e.getKeyCode() == KeyEvent.VK_U) {
            //              Lizzie.frame.toggleBestMoves();
            //            }
            //            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //              if (Lizzie.frame.isPlayingAgainstLeelaz) {
            //                Lizzie.frame.isPlayingAgainstLeelaz = false;
            //                Lizzie.leelaz.isThinking = false;
            //              }
            //              Lizzie.leelaz.togglePonder();
            //            }
            //            if (e.getKeyCode() == KeyEvent.VK_Q) {
            //              togglealwaysontop();
            //            }
          }
        });

    header.addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            //            int pick = header.columnAtPoint(e.getPoint());
            //            sortnum = pick;
            //            issorted = !issorted;
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      //      if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString())[0]
      //              == Lizzie.frame.clickbadmove[0]
      //          && Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString())[1]
      //              == Lizzie.frame.clickbadmove[1]) {

      //        Color hsbColor =
      //            Color.getHSBColor(
      //                Color.RGBtoHSB(238, 221, 130, null)[0],
      //                Color.RGBtoHSB(238, 221, 130, null)[1],
      //                Color.RGBtoHSB(238, 221, 130, null)[2]);
      //        setBackground(hsbColor);
      //        if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      //            && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      //          Color hsbColor2 =
      //              Color.getHSBColor(
      //                  Color.RGBtoHSB(255, 153, 18, null)[0],
      //                  Color.RGBtoHSB(255, 153, 18, null)[1],
      //                  Color.RGBtoHSB(255, 153, 18, null)[2]);
      //          setForeground(hsbColor2);
      //        } else if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) > 10) {
      //          setForeground(Color.RED);
      //        } else {
      //          setForeground(Color.BLACK);
      //        }
      //        return super.getTableCellRendererComponent(table, value, isSelected, false, row,
      // column);
      //      }
      //      if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      //          && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      //        Color hsbColor =
      //            Color.getHSBColor(
      //                Color.RGBtoHSB(255, 153, 18, null)[0],
      //                Color.RGBtoHSB(255, 153, 18, null)[1],
      //                Color.RGBtoHSB(255, 153, 18, null)[2]);
      //        setBackground(Color.WHITE);
      //        setForeground(hsbColor);
      //        return super.getTableCellRendererComponent(table, value, isSelected, false, row,
      // column);
      //      }
      //      if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) > 10) {
      //        setBackground(Color.WHITE);
      //        setForeground(Color.RED);
      //        return super.getTableCellRendererComponent(table, value, isSelected, false, row,
      // column);
      //      } else
      {
        return renderer.getTableCellRendererComponent(table, value, isSelected, false, row, column);
      }
    }
  }

  private void togglealwaysontop() {
    if (engjf.isAlwaysOnTop()) {
      engjf.setAlwaysOnTop(false);
      Lizzie.config.uiConfig.put("badmoves-always-ontop", false);
    } else {
      engjf.setAlwaysOnTop(true);
      Lizzie.config.uiConfig.put("badmoves-always-ontop", true);
      if (Lizzie.frame.isAlwaysOnTop()) Lizzie.frame.toggleAlwaysOntop();
    }
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void handleTableClick(int row, int col) {
    //    if (selectedorder != row) {
    //      int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
    // 2).toString());
    //      Lizzie.frame.clickbadmove = coords;
    //      Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
    //      Lizzie.frame.repaint();
    //      selectedorder = row;
    //    } else {
    //      Lizzie.frame.clickbadmove = Lizzie.frame.outOfBoundCoordinate;
    //      Lizzie.frame.boardRenderer.removedrawmovestone();
    //      Lizzie.frame.repaint();
    //      selectedorder = -1;
    //      table.clearSelection();
    //    }
    command.setText(table.getModel().getValueAt(row, 2).toString());
    engineName.setText("设置引擎" + table.getModel().getValueAt(row, 0).toString());
    txtName.setText(table.getModel().getValueAt(row, 1).toString());
    if (table.getModel().getValueAt(row, 3).toString().equals("是")) preload.setSelected(true);
    else preload.setSelected(false);
    txtWidth.setText(table.getModel().getValueAt(row, 4).toString());
    txtHeight.setText(table.getModel().getValueAt(row, 5).toString());
    if (table.getModel().getValueAt(row, 6).toString().equals("是")) chkdefault.setSelected(true);
    else chkdefault.setSelected(false);
    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
  }

  private void handleTableDoubleClick(int row, int col) {
    //    int movenumber = Integer.parseInt(table.getValueAt(row, 1).toString());
    //    Lizzie.board.goToMoveNumber(1);
    //    Lizzie.board.goToMoveNumber(movenumber - 1);
    //    int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString());
    //    Lizzie.frame.clickbadmove = coords;
    //    Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
    //    Lizzie.frame.repaint();
    //    selectedorder = row;
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

    int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);

    for (int i = 0; i < enginesCommandOpt.get().length(); i++) {
      if (i == 0) {
        String engineCommand = Lizzie.config.leelazConfig.getString("engine-command");
        int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i) : 19;
        int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i) : 19;
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i) : "";
        boolean preload =
            enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i) : false;
        EngineData enginedt = new EngineData();
        enginedt.commands = engineCommand;
        enginedt.name = name;
        enginedt.preload = preload;
        enginedt.index = i;
        enginedt.width = width;
        enginedt.height = height;
        if (defaultEngine == i) enginedt.isDefault = true;
        else enginedt.isDefault = false;
        engineData.add(enginedt);
      } else {
        String commands =
            enginesCommandOpt.isPresent() ? enginesCommandOpt.get().optString(i - 1) : "";
        int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i) : 19;
        int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i) : 19;
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i) : "";
        boolean preload =
            enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i) : false;
        EngineData enginedt = new EngineData();
        enginedt.commands = commands;
        enginedt.name = name;
        enginedt.preload = preload;
        enginedt.index = i;
        enginedt.width = width;
        enginedt.height = height;
        if (defaultEngine == i) enginedt.isDefault = true;
        else enginedt.isDefault = false;
        engineData.add(enginedt);
      }
    }
    return engineData;
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 7;
      }

      public int getRowCount() {

        return 50;
      }

      public String getColumnName(int column) {

        if (column == 0) return "序号";
        if (column == 1) return "名称";
        if (column == 2) return "命令行";
        if (column == 3) return "预加载";
        if (column == 4) return "宽";
        if (column == 5) return "高";
        if (column == 6) return "默认";

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
    engjf.setTitle("更多引擎设置");

    engjf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            engjf.setVisible(false);
          }
        });

    final MoreEngines newContentPane = new MoreEngines();
    newContentPane.setOpaque(true); // content panes must be opaque
    engjf.setContentPane(newContentPane);
    // Display the window.
    //  jf.setSize(521, 320);

    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length() >= 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
      // jf.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
      engjf.setBounds(50, 50, 700, 800);
    } else {
      engjf.setBounds(50, 50, 700, 800);
    }
    try {
      engjf.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    engjf.setLocationRelativeTo(engjf.getOwner());
    // jf.setResizable(false);
    return engjf;
  }
}
