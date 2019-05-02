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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class AnalysisFrame extends JPanel {

  TableModel dataModel;
  JScrollPane scrollpane;
  JTable table;
  Timer timer;
  int sortnum = 1;

  public AnalysisFrame() {

    dataModel = getTableModel();
    table = new JTable(dataModel);
    table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
    table.setFont(new Font("宋体", 0, 18));

    scrollpane =
        new JScrollPane(table) {
          @Override
          public Dimension getPreferredSize() {
            return new Dimension(460, 330);
          }
        };
    // table.setAutoCreateRowSorter(true);

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
    this.add(scrollpane);

    table.getColumnModel().getColumn(0).setPreferredWidth(20);
    table.getColumnModel().getColumn(1).setPreferredWidth(75);
    table.getColumnModel().getColumn(2).setPreferredWidth(30);
    table.getColumnModel().getColumn(3).setPreferredWidth(60);
    table.getColumnModel().getColumn(4).setPreferredWidth(50);
    // scrollpane.setBounds(0, 10, 470, 400);
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

  private void handleTableClick(int row, int col) {
    String aa = table.getValueAt(row, 0).toString();
    int[] coords = Lizzie.board.convertNameToCoordinates(aa);
    Lizzie.frame.suggestionclick = coords;
    Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
    Lizzie.frame.repaint();
  }

  private void handleTableDoubleClick(int row, int col) {
    int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 0).toString());
    Lizzie.frame.mouseOverCoordinate = coords;
    Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
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

  public static JDialog createAnalysisDialog(JFrame owner) {
    // Create and set up the window.
    JDialog dialog = new JDialog(owner, "单击显示紫圈(小棋盘显示变化图),双击显示后续变化图,快捷键U显示/关闭");
    dialog.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
          }
        });
    // Create and set up the content pane.
    final AnalysisFrame newContentPane = new AnalysisFrame();
    newContentPane.setOpaque(true); // content panes must be opaque
    dialog.setContentPane(newContentPane);

    // Display the window.
    dialog.setSize(480, 380);

    // Handle close event

    return dialog;
  }
}
