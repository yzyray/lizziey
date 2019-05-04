package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.Movelistwr;
import featurecat.lizzie.rules.SGFParser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class RightClickMenu extends JPopupMenu {
  public static int mousex;
  public static int mousey;
  //  private JMenuItem insertmode;
  private JMenuItem addblack;
  private JMenuItem addwhite;
  private JMenuItem addone;
  //  private JMenuItem deleteone;
  //  private JMenuItem quitinsert;
  private JMenuItem allow;
  private JMenuItem allow2;
  private JMenuItem avoid;
  private JMenuItem avoid2;
  private static JMenuItem cancelavoid;
  //  private static JMenuItem test;
  // private static JMenuItem test2;
  private static JMenuItem editmode;
  private static JMenuItem quiteditmode;
  private static JMenuItem restore;
  private static JMenuItem reedit;
  private static JMenuItem cleanupedit;
  private String saveString;
  private String saveString2;
  // private BoardRenderer boardRenderer;
  public static String allowcoords = "";
  public static String avoidcoords = "";
  public static int move = 0;
  public static int startmove = 0;
  public static boolean isforcing = false;
  public static boolean isallow = false;
  ArrayList<Movelist> currentmovestat;
  ArrayList<Movelist> currentmovestat2;
  ArrayList<Movelist> orimovestat;
  private String oriString = "";
  ArrayList<Movelistwr> copy= new ArrayList<Movelistwr>();

  public RightClickMenu() {

    PopupMenuListener listener =
        new PopupMenuListener() {
          public void popupMenuCanceled(PopupMenuEvent e) {}

          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            if (Lizzie.leelaz.isPondering() && isforcing) {
              if (isallow) {
                Lizzie.leelaz.analyzeAvoid("allow", Lizzie.board.getcurrentturn(), allowcoords, 1);
              } else {
                Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturn(), avoidcoords, 30);
              }
            }
            if (Lizzie.leelaz.isPondering() && !isforcing) {
              Lizzie.leelaz.ponder();
            }
            Lizzie.frame.isshowrightmenu = false;
          }

          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            if (allowcoords != "") {
              allow2.setVisible(true);
            } else {
              allow2.setVisible(false);
            }
            if (oriString != "") {
              cleanupedit.setVisible(true);
            } else {
              cleanupedit.setVisible(false);
            }
          }
        };

    this.addPopupMenuListener(listener);

    // insertmode = new JMenuItem("进入插入棋子模式");
    // quitinsert = new JMenuItem("退出插入棋子模式");
    addblack = new JMenuItem("插入黑子");
    addwhite = new JMenuItem("插入白子");
    addone = new JMenuItem("轮流插入棋子");
    //   deleteone = new JMenuItem("更改棋子位置");
    allow = new JMenuItem("只分析此点(强制)");
    allow2 = new JMenuItem("增加分析此点(强制)");
    avoid = new JMenuItem("不分析此点(强制)");
    avoid2 = new JMenuItem("设置不分析持续手数");
    cancelavoid = new JMenuItem("清除分析设置");
    //  test=new JMenuItem("测试删除棋子");
    //  test2=new JMenuItem("测试恢复棋盘状态");
    editmode = new JMenuItem("进入编辑模式");
    quiteditmode = new JMenuItem("退出编辑模式");
    restore = new JMenuItem("撤销最近一次编辑");
    reedit = new JMenuItem("恢复最近一次编辑");
    cleanupedit = new JMenuItem("清除所有编辑结果");
    // this.add(addblack);
    // this.add(addwhite);
    this.add(allow);
    this.add(allow2);
    this.add(avoid);
    this.add(avoid2);
    this.add(cancelavoid);

    //  this.add(deleteone);
    // this.add(insertmode);
    // this.add(test);
    // this.add(test2);
    this.add(editmode);

    // this.add(quiteditmode);

    cleanupedit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            cleanupedit();
            //  Lizzie.board.clear();
          }
        });

    allow2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            allow2();
            //  Lizzie.board.clear();
          }
        });
    restore.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            restore();
            //  Lizzie.board.clear();
          }
        });

    reedit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            reedit();
            //  Lizzie.board.clear();
          }
        });

    //    test.addActionListener( new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            System.out.println("删除棋子");
    //            saveboard();
    //          //  Lizzie.board.clear();
    //          }
    //        });
    //
    //    test2.addActionListener( new ActionListener() {
    //        @Override
    //        public void actionPerformed(ActionEvent e) {
    //          System.out.println("恢复棋盘状态");
    //          setboard();
    //        }
    //      });
    editmode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // System.out.println("进入拖动模式");
            editmode();
          }
        });
    quiteditmode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("退出拖动模式");
            quiteditmode();
          }
        });
    // Lizzie.frame.RightClickMenu.show(invoker, x, y);
    //    insertmode.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            System.out.println("进入插入棋子模式");
    //            insertmode();
    //          }
    //        });
    //    quitinsert.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            System.out.println("退出插入棋子模式");
    //            quitinsertmode();
    //          }
    //        });
    addblack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("增加黑子");
            addblack();
          }
        });
    addwhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //   System.out.println("增加白子");
            addwhite();
          }
        });
    addone.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //   System.out.println("轮流增加棋子");
            addone();
          }
        });
    allow.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("分析此点(强制)");
            allow();
          }
        });
    avoid.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // System.out.println("不分析此点(强制)");
            avoid();
          }
        });
    avoid2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // System.out.println("设置不分析持续手数");
            avoid2();
          }
        });
    //    deleteone.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            System.out.println("更改棋子位置");
    //            delete();
    //          }
    //        });
    cancelavoid.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // System.out.println("清除所有强制分析设置");
            cancelavoid();
          }
        });
  }

  private void cleanupedit() {
    if (!oriString.isEmpty()) {
      SGFParser.loadFromString(oriString);
      Lizzie.board.setmovelist(orimovestat);
    }
    oriString = "";
    saveString = "";
    saveString2 = "";
    this.remove(restore);
    this.remove(reedit);
    for (int i = 0; i < copy.size(); i++) {
    	Lizzie.board.movelistwr.add(copy.get(i)); 
      }
  }

  private void restore() {
    // Lizzie.frame.pasteSgf();
    try {
      saveString2 = SGFParser.saveToString();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    currentmovestat2 = Lizzie.board.savelistforeditmode();
    if (!saveString.isEmpty()) {
      SGFParser.loadFromString(saveString);

      Lizzie.board.setmovelist(currentmovestat);
    }
    this.remove(restore);
    this.add(reedit);
    for (int i = 0; i < copy.size(); i++) {
    	Lizzie.board.movelistwr.add(copy.get(i)); 
      }
  }

  private void reedit() {
	 
    if (!saveString2.isEmpty()) {
      SGFParser.loadFromString(saveString2);

      Lizzie.board.setmovelist(currentmovestat2);
    }
    this.remove(reedit);
    this.add(restore);
    for (int i = 0; i < copy.size(); i++) {
    	Lizzie.board.movelistwr.add(copy.get(i)); 
      }
  }

  private void editmode() {
	  copy.clear();
    featurecat.lizzie.gui.Input.Draggedmode = true;
    // Lizzie.frame.copySgf();
    try {
      saveString = SGFParser.saveToString();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    for (int i = 0; i < Lizzie.board.movelistwr.size(); i++) {
    	copy.add(Lizzie.board.movelistwr.get(i)); 
      }
    this.remove(reedit);
    this.remove(editmode);
    this.add(addblack);
    this.add(addwhite);
    this.add(addone);
    this.add(quiteditmode);
    Lizzie.board.insertmode();
    currentmovestat = Lizzie.board.savelistforeditmode();
    if (oriString == "") {
      oriString = saveString;
      orimovestat = currentmovestat;
    }
  }

  private void quiteditmode() {
    featurecat.lizzie.gui.Input.Draggedmode = false;
    this.add(editmode);
    this.add(restore);
    this.add(cleanupedit);
    this.remove(quiteditmode);
    this.remove(addblack);
    this.remove(addwhite);
    this.remove(addone);
    Lizzie.board.setlistforeditmode1();
    if (!saveString.isEmpty()) {
      SGFParser.loadFromString(saveString);
    }
    Lizzie.board.setlistforeditmode2();
    Lizzie.board.quitinsertmode();
    this.remove(quiteditmode);
    
    Lizzie.board.movelistwr.clear();
    for (int i = 0; i < copy.size(); i++) {
    	Lizzie.board.movelistwr.add(copy.get(i)); 
      }
    
    
  }

  //  private void insertmode() {
  //
  //    boolean isinsertmode;
  //    isinsertmode = Lizzie.board.insertmode();
  //    if (isinsertmode) {
  //      this.remove(insertmode);
  //      this.add(addblack);
  //      this.add(addwhite);
  //      this.add(addone);
  //      this.add(quitinsert);
  //    }
  //    if (Lizzie.leelaz.isPondering()) {
  //      Lizzie.leelaz.ponder();
  //    }
  //  }

  //  private void quitinsertmode() {
  //    this.remove(quitinsert);
  //    this.remove(addblack);
  //    this.remove(addwhite);
  //    this.remove(addone);
  //    this.add(insertmode);
  //    Lizzie.board.quitinsertmode();
  //    if (Lizzie.leelaz.isPondering()) {
  //      Lizzie.leelaz.ponder();
  //    }
  //  }

  private void addblack() {

    Lizzie.frame.insertMove(mousex, mousey, true);
  }

  private void cancelavoid() {
    allowcoords = "";
    avoidcoords = "";
    move = 0;
    Lizzie.leelaz.ponder();
  }

  private void addwhite() {
    Lizzie.frame.insertMove(mousex, mousey, false);
  }

  private void addone() {
    Lizzie.frame.insertMove(mousex, mousey);
  }

  private void allow() {
    if (Lizzie.frame.iscoordsempty(mousex, mousey)) {

      allowcoords = Lizzie.frame.convertmousexy(mousex, mousey);
    }
    isforcing = true;
    isallow = true;
    avoidcoords = "";
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("allow", Lizzie.board.getcurrentturn(), allowcoords, 1);
    Lizzie.frame.isshowrightmenu = false;
  }

  private void allow2() {
    if (Lizzie.frame.iscoordsempty(mousex, mousey)) {
      if (allowcoords != "") {
        allowcoords = allowcoords + "," + Lizzie.frame.convertmousexy(mousex, mousey);
      } else {
        allowcoords = Lizzie.frame.convertmousexy(mousex, mousey);
      }
    }
    isforcing = true;
    isallow = true;
    avoidcoords = "";
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("allow", Lizzie.board.getcurrentturn(), allowcoords, 1);
    Lizzie.frame.isshowrightmenu = false;
  }

  public static void avoid() {

    if (Lizzie.frame.iscoordsempty(mousex, mousey)) {
      if (avoidcoords != "") {
        avoidcoords = avoidcoords + "," + Lizzie.frame.convertmousexy(mousex, mousey);
      } else {
        avoidcoords = Lizzie.frame.convertmousexy(mousex, mousey);
      }
    }
    voidanalyze();
    Lizzie.frame.isshowrightmenu = false;
  }

  public static void voidanalyzeponder() {
    if (avoidcoords == "") {
      avoidcoords = "A0";
    }
    // String color=Lizzie.frame.getstonecolor(mousex,mousey);
    isforcing = true;
    isallow = false;
    allowcoords = "";
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturnponder(), avoidcoords, 30);

    // System.out.println("ana ponder");
  }

  public static void voidanalyze() {

    // String color=Lizzie.frame.getstonecolor(mousex,mousey);
    if (avoidcoords == "") {
      avoidcoords = "A0";
    }
    allowcoords = "";
    isforcing = true;
    isallow = false;
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturn(), avoidcoords, 30);
    //  System.out.println("ana ");
  }

  private void avoid2() {
    Lizzie.leelaz.notPondering();
    Lizzie.frame.openAvoidmoves();
  }

  //  private void delete() {
  //    int movenumber = Lizzie.frame.getmovenumber(mousex, mousey);
  //    int movenumberinbranch = Lizzie.frame.getmovenumberinbranch(mousex, mousey);
  //    if (movenumber < 1) {
  //      return;
  //    }
  //    if (featurecat.lizzie.gui.Input.isinsertmode && movenumberinbranch <= 0) {
  //      JOptionPane.showMessageDialog(null, "插入模式下只能修改当前分支的棋子");
  //      return;
  //    }
  //    if (featurecat.lizzie.gui.Input.isinsertmode && movenumberinbranch > 0) {
  //      Lizzie.frame.openChangeMoveDialog2(movenumber, true);
  //      return;
  //    }
  //    if (movenumberinbranch <= 1) {
  //
  //      int n =
  //          JOptionPane.showConfirmDialog(
  //              null, "修改当前分支以外的棋子(或者当前分支第一步)不能继承分支变化", "确认修改", JOptionPane.YES_NO_OPTION);
  //      if (n == JOptionPane.YES_OPTION) {
  //        Lizzie.frame.openChangeMoveDialog2(movenumber, false);
  //      } else if (n == JOptionPane.NO_OPTION) {
  //        return;
  //      }
  //
  //      // Lizzie.frame.openChangeMoveDialog2(movenumber);
  //    }
  // System.out.println(movenumber);

  //    else {
  //      Lizzie.frame.openChangeMoveDialog2(movenumber, true);
  //      return;
  //    }
  //  }

  public void Store(int x, int y) {
    mousex = x;
    mousey = y;
  }
}
