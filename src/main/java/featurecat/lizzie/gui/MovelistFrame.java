package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.Movelistwr;
import featurecat.lizzie.rules.Stone;
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
public class MovelistFrame extends JPanel {
  public static Config config;
  TableModel dataModel;
  JPanel tablepanel;
  JPanel selectpanel=new JPanel();
  JScrollPane scrollpane;
  public static JTable table;
  Timer timer;
  int sortnum = 4;
  public static int selectedorder = -1;
  boolean issorted = false;
  JSpinner dropwinratechooser = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
  JSpinner playoutschooser = new JSpinner(new SpinnerNumberModel(50, 0, 999999, 100));
  JCheckBox checkBlack = new JCheckBox();
  JCheckBox checkWhite = new JCheckBox();

  public MovelistFrame() {
	  super(new BorderLayout());
    dataModel = getTableModel();
    table = new JTable(dataModel);
    table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
    table.setFont(new Font("宋体", Font.BOLD, 18));
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.setRowHeight(20);
    tablepanel =new JPanel(new BorderLayout());
    this.add(tablepanel, BorderLayout.CENTER);
    this.add(selectpanel, BorderLayout.SOUTH);
    scrollpane =
        new JScrollPane(table);

    timer =
        new Timer(
            100,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                dataModel.getColumnCount();
                table.validate();
                table.updateUI();
                Optional<int[]> passstep = Optional.empty();
                if (Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()
                    && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
                    && !(Lizzie.board.getHistory().getCurrentHistoryNode().getData().lastMove
                        == passstep)) {

                  double lastwr = 50.0;
                  lastwr =
                      Lizzie.board
                          .getHistory()
                          .getCurrentHistoryNode()
                          .previous()
                          .get()
                          .getData()
                          .getWinrate();
                  double wr = 100 - lastwr;
                  int playouts = 0;
                  int previousplayouts = 0;
                  if (!Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .getData()
                      .bestMoves
                      .isEmpty()) {
                    wr =
                        (100
                            - Lizzie.board
                                .getHistory()
                                .getCurrentHistoryNode()
                                .getData()
                                .bestMoves
                                .get(0)
                                .winrate);
                    playouts =
                        Lizzie.board
                            .getHistory()
                            .getCurrentHistoryNode()
                            .getData()
                            .bestMoves
                            .stream()
                            .mapToInt(move -> move.playouts)
                            .sum();
                    previousplayouts =
                        Lizzie.board
                            .getHistory()
                            .getCurrentHistoryNode()
                            .previous()
                            .get()
                            .getData()
                            .getPlayouts();
                  }
                  int[] coords =
                      Lizzie.board.getHistory().getCurrentHistoryNode().getData().lastMove.get();
                  int movenumer = Lizzie.board.getHistory().getMoveNumber();
                  double diffwinrate = wr - lastwr;
                  boolean isblack =
                      !Lizzie.board.getHistory().getCurrentHistoryNode().getData().blackToPlay;

                  if (!Lizzie.board.movelistwr.isEmpty()
                      && Lizzie.board.movelistwr.size() >= movenumer) {
                    if (playouts >= Lizzie.board.movelistwr.get(movenumer - 1).playouts
                            && previousplayouts
                                >= Lizzie.board.movelistwr.get(movenumer - 1).previousplayouts
                        || Lizzie.board.movelistwr.get(movenumer - 1).isdelete) {
                      Lizzie.board.movelistwr.get(movenumer - 1).diffwinrate = diffwinrate;
                      Lizzie.board.movelistwr.get(movenumer - 1).winrate = wr;
                      Lizzie.board.movelistwr.get(movenumer - 1).coords = coords;
                      Lizzie.board.movelistwr.get(movenumer - 1).isblack = isblack;
                      Lizzie.board.movelistwr.get(movenumer - 1).playouts = playouts;
                      Lizzie.board.movelistwr.get(movenumer - 1).movenum = movenumer;
                      Lizzie.board.movelistwr.get(movenumer - 1).previousplayouts =
                          previousplayouts;
                      Lizzie.board.movelistwr.get(movenumer - 1).isdelete = false;
                    }
                  } else {
                    Movelistwr mv = new Movelistwr();
                    mv.diffwinrate = diffwinrate;
                    mv.winrate = wr;
                    mv.coords = coords;
                    mv.isblack = isblack;
                    mv.playouts = playouts;
                    mv.movenum = movenumer;
                    mv.previousplayouts = previousplayouts;
                    mv.isdelete = false;
                    Lizzie.board.movelistwr.add(mv);
                  }
                }
              }
            });
    timer.start();
       tablepanel.add(scrollpane);
      
       table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(20);
    table.getColumnModel().getColumn(1).setPreferredWidth(20);
    table.getColumnModel().getColumn(2).setPreferredWidth(25);
    table.getColumnModel().getColumn(3).setPreferredWidth(40);
    table.getColumnModel().getColumn(4).setPreferredWidth(68);
    table.getColumnModel().getColumn(5).setPreferredWidth(75);
    table.getColumnModel().getColumn(6).setPreferredWidth(50);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
            && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
            && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length() == 11) {
          JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
          table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
          table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
          table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
          table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
          table.getColumnModel().getColumn(4).setPreferredWidth(pos.getInt(8));
          table.getColumnModel().getColumn(5).setPreferredWidth(pos.getInt(9));
          table.getColumnModel().getColumn(6).setPreferredWidth(pos.getInt(10));
    }
    
    JTableHeader header = table.getTableHeader();

    dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    checkBlack.setSelected(true);
    checkWhite.setSelected(true);

    JLabel checkBlacktxt = new JLabel("显示黑:");
    JLabel checkWhitetxt = new JLabel("白:");
    JLabel dropwinratechoosertxt = new JLabel("只显示胜率波动超过:");
    JLabel playoutschoosertxt = new JLabel("只显示前后计算量都超过:");
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
            if (e.getKeyCode() == KeyEvent.VK_T) {
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
      // if(row%2 == 0){
      if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString())[0]
              == Lizzie.frame.clickbadmove[0]
          && Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString())[1]
              == Lizzie.frame.clickbadmove[1]) {

        //        if (selectedorder != row) {
        //          selectedorder = -1;
        //          setForeground(Color.RED);
        //        }
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
    if (JOptionPane.getFrameForComponent(this).isAlwaysOnTop()) {
      JOptionPane.getFrameForComponent(this).setAlwaysOnTop(false);
      Lizzie.config.uiConfig.put("badmoves-always-ontop", false);
    } else {
      JOptionPane.getFrameForComponent(this).setAlwaysOnTop(true);
      Lizzie.config.uiConfig.put("badmoves-always-ontop", true);
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
      int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString());
      Lizzie.frame.clickbadmove = coords;
      Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
      Lizzie.frame.repaint();
      selectedorder = row;
    } else {
      Lizzie.frame.clickbadmove = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.boardRenderer.removedrawmovestone();
      Lizzie.frame.repaint();
      selectedorder = -1;
    }
  }

  private void handleTableDoubleClick(int row, int col) {
    int movenumber = Integer.parseInt(table.getValueAt(row, 1).toString());
    Lizzie.board.goToMoveNumber(1);
    Lizzie.board.goToMoveNumber(movenumber);
    int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 2).toString());
    Lizzie.frame.clickbadmove = coords;
    Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
    Lizzie.frame.repaint();
    selectedorder = row;
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 7;
      }

      public int getRowCount() {
        int row = 0;
        ArrayList<Movelistwr> data2 = Lizzie.board.movelistwr;
        for (int i = 0; i < Lizzie.board.movelistwr.size(); i++) {
          Movelistwr mwr = Lizzie.board.movelistwr.get(i);
          if (!mwr.isdelete)
            if (mwr.isblack && checkBlack.isSelected() || !mwr.isblack && checkWhite.isSelected())
              if (Math.abs(mwr.diffwinrate) > (int) dropwinratechooser.getValue())
                if (mwr.playouts > (int) playoutschooser.getValue()) row = row + 1;
        }

        return row;
      }

      public String getColumnName(int column) {
        if (column == 0) return "黑白";
        if (column == 1) return "手数";
        if (column == 2) return "坐标";
        if (column == 3) return "胜率(%)";
        if (column == 4) return "胜率波动(%)";
        if (column == 5) return "前一步计算量";
        if (column == 6) return "计算量";
        return "无";
      }

      @SuppressWarnings("unchecked")
      public Object getValueAt(int row, int col) {
        ArrayList<Movelistwr> data2 = new ArrayList<Movelistwr>();

        for (int i = 0; i < Lizzie.board.movelistwr.size(); i++) {
          Movelistwr mwr = Lizzie.board.movelistwr.get(i);

          if (mwr.isblack && checkBlack.isSelected() || !mwr.isblack && checkWhite.isSelected())
            if (!mwr.isdelete)
              if (Math.abs(mwr.diffwinrate) > (int) dropwinratechooser.getValue())
                if (mwr.playouts > (int) playoutschooser.getValue()) data2.add(mwr);
        }
        //		Collections.sort(data2) ;
        Collections.sort(
            data2,
            new Comparator<Movelistwr>() {

              @Override
              public int compare(Movelistwr s1, Movelistwr s2) {
                // 降序
                if (!issorted) {
                  if (sortnum == 0) {
                    if (s2.isblack) return 1;
                    if (!s2.isblack) return -1;
                  }
                  if (sortnum == 1) {
                    if (s1.movenum > s2.movenum) return 1;
                    if (s1.movenum < s2.movenum) return -1;
                  }
                  if (sortnum == 2) {
                    return 1;
                  }
                  if (sortnum == 3) {
                    if (s1.winrate < s2.winrate) return 1;
                    if (s1.winrate > s2.winrate) return -1;
                  }
                  if (sortnum == 4) {
                    if (Math.abs(s1.diffwinrate) < Math.abs(s2.diffwinrate)) return 1;
                    if (Math.abs(s1.diffwinrate) > Math.abs(s2.diffwinrate)) return -1;
                  }
                  if (sortnum == 5) {
                    if (s1.previousplayouts < s2.previousplayouts) return 1;
                    if (s1.previousplayouts > s2.previousplayouts) return -1;
                  }
                  if (sortnum == 6) {
                    if (s1.playouts < s2.playouts) return 1;
                    if (s1.playouts > s2.playouts) return -1;
                  }

                } else {
                  if (sortnum == 0) {
                    if (!s2.isblack) return 1;
                    if (s2.isblack) return -1;
                  }
                  if (sortnum == 1) {
                    if (s1.movenum < s2.movenum) return 1;
                    if (s1.movenum > s2.movenum) return -1;
                  }
                  if (sortnum == 2) {
                    return 1;
                  }
                  if (sortnum == 3) {
                    if (s1.winrate > s2.winrate) return 1;
                    if (s1.winrate < s2.winrate) return -1;
                  }
                  if (sortnum == 4) {
                    if (Math.abs(s1.diffwinrate) > Math.abs(s2.diffwinrate)) return 1;
                    if (Math.abs(s1.diffwinrate) < Math.abs(s2.diffwinrate)) return -1;
                  }
                  if (sortnum == 5) {
                    if (s1.previousplayouts > s2.previousplayouts) return 1;
                    if (s1.previousplayouts < s2.previousplayouts) return -1;
                  }
                  if (sortnum == 6) {
                    if (s1.playouts > s2.playouts) return 1;
                    if (s1.playouts < s2.playouts) return -1;
                  }
                }
                return 0;
              }
            });

        // featurecat.lizzie.analysis.MoveDataSorter MoveDataSorter = new MoveDataSorter(data2);
        // ArrayList sortedMoveData = MoveDataSorter.getSortedMoveDataByPolicy();

        Movelistwr data = data2.get(row);
        switch (col) {
          case 0:
            if (data.isblack) return "黑";
            return "白";
          case 1:
            return data.movenum;
          case 2:
            return Board.convertCoordinatesToName(data.coords[0], data.coords[1]);
          case 3:
            return String.format("%.2f", data.winrate);
          case 4:
            return String.format("%.2f", data.diffwinrate);
          case 5:
            return data.previousplayouts;
          case 6:
            return data.playouts;
          default:
            return "";
        }
      }
    };
  }

  public static JDialog createBadmovesDialog() {
    // Create and set up the window.
	  JDialog jf = new JDialog();
    jf.setTitle("仅主分支有效,B显示/关闭,单击显示紫圈,双击跳转T切换总在最前");

    jf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
          }
        });

    final MovelistFrame newContentPane = new MovelistFrame();
    newContentPane.setOpaque(true); // content panes must be opaque
    jf.setContentPane(newContentPane);
    // Display the window.
  //  jf.setSize(521, 320);
    
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
            && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
            && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length() >= 4) {
          JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
          jf.setBounds(pos.getInt(0), pos.getInt(1),pos.getInt(2),pos.getInt(3));
    }
    else {
    	 jf.setBounds(-9,0,521,320);
    }
    try {
      jf.setIconImage(ImageIO.read(MovelistFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

   // jf.setResizable(false);
    return jf;
  }
}
