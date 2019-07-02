package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.*;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
  static JDialog jf;
  Timer timer;
  int sortnum = 3;
  public static int selectedorder = -1;
  boolean issorted = false;
  JSpinner dropwinratechooser = new JSpinner(new SpinnerNumberModel(1, 0, 99, 1));
  JSpinner playoutschooser = new JSpinner(new SpinnerNumberModel(100, 0, 99999, 100));
  JCheckBox checkBlack = new JCheckBox();
  JCheckBox checkWhite = new JCheckBox();

  public MoreEngines() {
    // super(new BorderLayout());
    this.setLayout(null);
    dataModel = getTableModel();
    table = new JTable(dataModel);

    winrateFont = new Font("微软雅黑", Font.PLAIN, 14);
    headFont = new Font("微软雅黑", Font.PLAIN, 13);

    table.getTableHeader().setFont(headFont);
    table.setFont(winrateFont);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.setRowHeight(20);

    tablepanel = new JPanel(new BorderLayout());
    tablepanel.setBounds(0, 0, 500, 100);
    this.add(tablepanel, BorderLayout.SOUTH);
    selectpanel.setBounds(0, 100, 500, 300);
    // 测试到这
    this.add(selectpanel, BorderLayout.NORTH);
    scrollpane = new JScrollPane(table);

    //    timer =
    //        new Timer(
    //
    // Lizzie.config.config.getJSONObject("leelaz").getInt("analyze-update-interval-centisec")
    //                * 30,
    //            new ActionListener() {
    //              public void actionPerformed(ActionEvent evt) {
    //                dataModel.getColumnCount();
    //                table.validate();
    //                table.updateUI();
    //                try {
    //                  Lizzie.board.updateMovelist();
    //                } catch (Exception e) {
    //                }
    //              }
    //            });
    //    timer.start();
    tablepanel.add(scrollpane);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(52);
    table.getColumnModel().getColumn(1).setPreferredWidth(50);
    table.getColumnModel().getColumn(2).setPreferredWidth(57);
    table.getColumnModel().getColumn(3).setPreferredWidth(72);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length() == 12) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
      table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
      table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
      table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
      table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
    }

    JTableHeader header = table.getTableHeader();

    dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    checkBlack.setSelected(true);
    checkWhite.setSelected(true);

    checkBlacktxt = new JLabel("黑:");
    checkWhitetxt = new JLabel("白:");
    JLabel dropwinratechoosertxt = new JLabel("胜率波动筛选:");
    JLabel playoutschoosertxt = new JLabel("前后计算量筛选:");
    selectpanel.add(checkBlacktxt);
    selectpanel.add(checkBlack);
    selectpanel.add(checkWhitetxt);
    selectpanel.add(checkWhite);
    selectpanel.add(dropwinratechoosertxt);
    selectpanel.add(dropwinratechooser);
    selectpanel.add(playoutschoosertxt);
    selectpanel.add(playoutschooser);

    playoutschooser.addChangeListener(
        new ChangeListener() {

          public void stateChanged(ChangeEvent e) {

            Lizzie.config.leelazConfig.putOpt(
                "badmoves-playouts-limits", playoutschooser.getValue());
            try {
              Lizzie.config.save();
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });

    dropwinratechooser.addChangeListener(
        new ChangeListener() {

          public void stateChanged(ChangeEvent e) {

            Lizzie.config.leelazConfig.putOpt(
                "badmoves-winrate-limits", dropwinratechooser.getValue());
            try {
              Lizzie.config.save();
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
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
            if (e.getKeyCode() == KeyEvent.VK_B) {
              Lizzie.frame.toggleBadMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_U) {
              Lizzie.frame.toggleBestMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
              if (Lizzie.frame.isPlayingAgainstLeelaz) {
                Lizzie.frame.isPlayingAgainstLeelaz = false;
                Lizzie.leelaz.isThinking = false;
              }
              Lizzie.leelaz.togglePonder();
            }
            if (e.getKeyCode() == KeyEvent.VK_Q) {
              togglealwaysontop();
            }
          }
        });

    header.addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            int pick = header.columnAtPoint(e.getPoint());
            sortnum = pick;
            issorted = !issorted;
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
    if (jf.isAlwaysOnTop()) {
      jf.setAlwaysOnTop(false);
      Lizzie.config.uiConfig.put("badmoves-always-ontop", false);
    } else {
      jf.setAlwaysOnTop(true);
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

    for (int i = 0; i < enginesCommandOpt.get().length(); i++) {
      String commands = enginesCommandOpt.get().optString(i);

      String name = enginesNameOpt.get().optString(i);
      boolean preload = enginesPreloadOpt.get().optBoolean(i);
      EngineData enginedt = new EngineData();
      enginedt.commands = commands;
      enginedt.name = name;
      enginedt.preload = preload;
      enginedt.index = i;
      engineData.add(enginedt);
    }
    return engineData;
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 4;
      }

      public int getRowCount() {

        return 50;
      }

      public String getColumnName(int column) {

        if (column == 0) return "序号";
        if (column == 1) return "名称";
        if (column == 2) return "命令";
        if (column == 3) return "预加载";

        return "无";
      }

      @SuppressWarnings("unchecked")
      public Object getValueAt(int row, int col) {
        ArrayList<EngineData> EngineDatas = getEngineData();
        if (row > (EngineDatas.size() - 1)) {
          return "";
        }
        EngineData data = EngineDatas.get(row);

        if (data.commands.equals("")) {
          return "";
        }

        switch (col) {
          case 0:
            return data.index;
          case 1:
            return data.name;
          case 2:
            return data.commands;
          case 3:
            return data.preload;

          default:
            return "";
        }
      }
    };
  }

  public static JDialog createBadmovesDialog() {
    // Create and set up the window.
    jf = new JDialog();
    jf.setTitle("仅记录主分支,B显示/关闭,右键/双击跳转,单击显示紫圈,Q切换总在最前");

    jf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.toggleBadMoves();
          }
        });

    final MoreEngines newContentPane = new MoreEngines();
    newContentPane.setOpaque(true); // content panes must be opaque
    jf.setContentPane(newContentPane);
    // Display the window.
    //  jf.setSize(521, 320);

    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
        && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length() >= 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
      jf.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    } else {
      jf.setBounds(-9, 0, 576, 287);
    }
    try {
      jf.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // jf.setResizable(false);
    return jf;
  }
}
