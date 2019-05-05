package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.MoveData;
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
import java.util.Collections;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.json.JSONArray;

@SuppressWarnings("serial")
public class AnalysisFrame extends JPanel {

  TableModel dataModel;
  JScrollPane scrollpane;
  public static JTable table;
  Timer timer;
  int sortnum = 1;
  static int selectedorder = -1;
  static JDialog jfs;

  public AnalysisFrame() {
    super(new BorderLayout());
    dataModel = getTableModel();
    table = new JTable(dataModel);
    table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
    table.setFont(new Font("宋体", Font.BOLD, 18));
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);

    scrollpane = new JScrollPane(table);
    add(scrollpane);
    //    {
    //
    //          @Override
    //          public Dimension getPreferredSize() {
    //            return new Dimension(510, 245);
    //          }
    //        };

    timer =
        new Timer(
            100,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                dataModel.getColumnCount();
                table.validate();
                table.updateUI();
              }
            });
    timer.start();

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(15);
    table.getColumnModel().getColumn(1).setPreferredWidth(75);
    table.getColumnModel().getColumn(2).setPreferredWidth(30);
    table.getColumnModel().getColumn(3).setPreferredWidth(60);
    table.getColumnModel().getColumn(4).setPreferredWidth(50);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position") != null
        && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position").length() == 8) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position");
      table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
      table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
      table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
      table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
    }

    JTableHeader header = table.getTableHeader();

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
                if (e.getButton() == MouseEvent.BUTTON3) {
                  try {
                    handleTableRightClick(row, col);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                } else
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
            if (e.getKeyCode() == KeyEvent.VK_U) {
              Lizzie.frame.toggleBestMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_B) {
              Lizzie.frame.toggleBadMoves();
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
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      // if(row%2 == 0){
      if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 0).toString())[0]
              == Lizzie.frame.suggestionclick[0]
          && Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 0).toString())[1]
              == Lizzie.frame.suggestionclick[1]) {
        if (selectedorder != row) {
          selectedorder = -1;
          setForeground(Color.RED);
        }
        Color hsbColor =
            Color.getHSBColor(
                Color.RGBtoHSB(238, 221, 130, null)[0],
                Color.RGBtoHSB(238, 221, 130, null)[1],
                Color.RGBtoHSB(238, 221, 130, null)[2]);
        setBackground(hsbColor);

        return super.getTableCellRendererComponent(table, value, false, false, row, column);
      } else {
        return renderer.getTableCellRendererComponent(table, value, false, false, row, column);
      }
    }
    // 该类继承与JLabel，Graphics用于绘制单元格,绘制红线
    //      public void paintComponent(Graphics g){
    //          super.paintComponent(g);
    //          Graphics2D g2=(Graphics2D)g;
    //          final BasicStroke stroke=new BasicStroke(2.0f);
    //          g2.setColor(Color.RED);
    //          g2.setStroke(stroke);
    //          g2.drawLine(0,getHeight()/2,getWidth(),getHeight()/2);
    //      }
  }

  private void togglealwaysontop() {
    if (jfs.isAlwaysOnTop()) {
      jfs.setAlwaysOnTop(false);
      Lizzie.config.uiConfig.put("suggestions-always-ontop", false);
    } else {
      jfs.setAlwaysOnTop(true);
      Lizzie.config.uiConfig.put("suggestions-always-ontop", true);
      if(Lizzie.frame.isAlwaysOnTop())
      Lizzie.frame.toggleAlwaysOntop();
    }
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void handleTableClick(int row, int col) {
    if (selectedorder != row) {
      int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 0).toString());
      Lizzie.frame.suggestionclick = coords;
      Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.repaint();
      selectedorder = row;
    } else {
      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.repaint();
      selectedorder = -1;
    }
  }

  private void handleTableRightClick(int row, int col) {
    String aa = table.getValueAt(row, 0).toString();
    int[] coords = Lizzie.board.convertNameToCoordinates(aa);
    Lizzie.board.place(coords[0], coords[1]);
  }

  private void handleTableDoubleClick(int row, int col) {
    int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 0).toString());
    Lizzie.frame.mouseOverCoordinate = coords;
    Lizzie.frame.suggestionclick = coords;
    Lizzie.frame.repaint();
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 5;
      }

      public int getRowCount() {
        return Lizzie.board.getData().bestMoves.size();
      }

      public String getColumnName(int column) {
        if (column == 0) return "坐标";
        if (column == 1) return "Lcb(%)-与首位差";
        if (column == 2) return "胜率(%)";
        if (column == 3) return "计算量";
        if (column == 4) return "策略网络(%)";
        return "无";
      }

      @SuppressWarnings("unchecked")
      public Object getValueAt(int row, int col) {
        ArrayList<MoveData> data2 = new ArrayList<MoveData>();
        for (int i = 0; i < Lizzie.board.getData().bestMoves.size(); i++) {

          data2.add(Lizzie.board.getData().bestMoves.get(i));
        }

        //		Collections.sort(data2) ;
        Collections.sort(
            data2,
            new Comparator<MoveData>() {

              @Override
              public int compare(MoveData s1, MoveData s2) {
                // 降序
                if (sortnum == 1) {
                  if (s1.lcb < s2.lcb) return 1;
                  if (s1.lcb > s2.lcb) return -1;
                }
                if (sortnum == 2) {
                  if (s1.oriwinrate < s2.oriwinrate) return 1;
                  if (s1.oriwinrate > s2.oriwinrate) return -1;
                }
                if (sortnum == 3) {
                  if (s1.playouts < s2.playouts) return 1;
                  if (s1.playouts > s2.playouts) return -1;
                }
                if (sortnum == 4) {
                  if (s1.policy < s2.policy) return 1;
                  if (s1.policy > s2.policy) return -1;
                }
                return 0;
              }
            });

        // featurecat.lizzie.analysis.MoveDataSorter MoveDataSorter = new MoveDataSorter(data2);
        // ArrayList sortedMoveData = MoveDataSorter.getSortedMoveDataByPolicy();
        double maxlcb = 0;
        for (MoveData move : data2) {
          if (move.lcb > maxlcb) maxlcb = move.lcb;
        }
        MoveData data = data2.get(row);
        switch (col) {
          case 0:
            //
            // if(Lizzie.board.convertNameToCoordinates(data.coordinate)[0]==Lizzie.frame.suggestionclick[0]&&Lizzie.board.convertNameToCoordinates(data.coordinate)[1]==Lizzie.frame.suggestionclick[1])
            //        	  {return "*"+data.coordinate;}
            //        	  else
            return data.coordinate;
          case 1:
            return String.format("%.2f", data.lcb) + "-" + String.format("%.2f", maxlcb - data.lcb);
          case 2:
            return String.format("%.2f", data.oriwinrate);
          case 3:
            return data.playouts;
          case 4:
            return String.format("%.2f", data.policy);
          default:
            return "";
        }
      }
    };
  }

  public static JDialog createAnalysisDialog() {
    // Create and set up the window.
    jfs = new JDialog();

    jfs.setTitle("U显示/关闭,单击显示紫圈(小棋盘显示变化),右键落子,双击显示变化,Q切换总在最前");

    //  JDialog dialog = new JDialog(owner, "单击显示紫圈(小棋盘显示变化),右键落子,双击显示后续变化图,快捷键U显示/关闭");
    jfs.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
          }
        });

    // Create and set up the content pane.
    final AnalysisFrame newContentPane = new AnalysisFrame();
    newContentPane.setOpaque(true); // content panes must be opaque
    jfs.setContentPane(newContentPane);
    // Display the window.
    // jfs.setSize(521, 285);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position") != null
        && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position").length() >= 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position");
      jfs.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    } else {
      jfs.setBounds(-9, 312, 521, 259);
    }
    try {
      jfs.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // jfs.setResizable(false);
    // Handle close event

    return jfs;
  }
}
