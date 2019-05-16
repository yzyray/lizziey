package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Movelist;
//import featurecat.lizzie.rules.Movelistwr;
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
  
  private JMenuItem addblack;
  private JMenuItem addwhite;

  private JMenuItem allow;
  private JMenuItem allow2;
  private JMenuItem avoid;
  private JMenuItem avoid2;
  private static JMenuItem cancelavoid;
  private static JMenuItem reedit;
  private static JMenuItem cleanupedit;
  private static JMenuItem cleanedittemp;
  public static String allowcoords = "";
  public static String avoidcoords = "";
  public static int move = 0;
  public static int startmove = 0;
  public static boolean isforcing = false;
  public static boolean isallow = false;

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
              if(avoidcoords !="")
              {
            	  cancelavoid.setVisible(true);
              }
            } else {
              allow2.setVisible(false);
            }
           if(Lizzie.board.boardstatbeforeedit=="")
           {
        	   cleanupedit.setVisible(false);     
        	   if(Lizzie.board.boardstatafteredit=="")
        	   { cleanedittemp.setVisible(false);}
           }
           else {
        	   cleanupedit.setVisible(true);
        	   cleanedittemp.setVisible(true);
           }
           if(Lizzie.board.boardstatafteredit=="")
           {
        	   reedit.setVisible(false);
           }
           else {
        	   reedit.setVisible(true);
        	   cleanedittemp.setVisible(true);
           }        
          }
        };

    this.addPopupMenuListener(listener);

    // insertmode = new JMenuItem("进入插入棋子模式");
    // quitinsert = new JMenuItem("退出插入棋子模式");
    addblack = new JMenuItem("插入黑子");
    addwhite = new JMenuItem("插入白子");
    //   deleteone = new JMenuItem("更改棋子位置");
    allow = new JMenuItem("只分析此点(强制)");
    allow2 = new JMenuItem("增加分析此点(强制)");
    avoid = new JMenuItem("不分析此点(强制)");
    avoid2 = new JMenuItem("设置不分析持续手数");
    cancelavoid = new JMenuItem("清除分析设置");
    cleanedittemp  = new JMenuItem("清除编辑缓存");
    //  test=new JMenuItem("测试删除棋子");
    //  test2=new JMenuItem("测试恢复棋盘状态");
    reedit = new JMenuItem("恢复到编辑后");
    cleanupedit = new JMenuItem("恢复到编辑前");
    // this.add(addblack);
    // this.add(addwhite);
    this.add(allow);
    this.add(allow2);
    this.add(avoid);
    this.add(avoid2);
    this.add(cancelavoid);
    this.add(cleanedittemp);
    this.add(addblack);
    this.add(addwhite);    
    this.add(reedit);
    this.add(cleanupedit);


    cleanedittemp.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                //  System.out.println("撤销上次编辑");
            	  cleanedittemp();
              }
            });
    
    reedit.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                //  System.out.println("撤销上次编辑");
            	  reedit();
              }
            });
    
    cleanupedit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            cleanupedit();
          }
        });

    allow2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            allow2();
          }
        });
   

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
  Lizzie.board.cleanedit();
  }

  
  
  private void cleanedittemp() {
	Lizzie.board.cleanedittemp();
  }
  
  private void reedit() {
  Lizzie.board.reedit();
  }
  private void addblack() {
	  if (Lizzie.frame.iscoordsempty(mousex, mousey)) {

	     int[]  coords = Lizzie.frame.convertmousexytocoords(mousex, mousey);
	      int currentmovenumber=Lizzie.board.getcurrentmovenumber();
		     Lizzie.board.savelistforeditmode();
		     
		     Lizzie.board.editmovelistadd(Lizzie.board.tempallmovelist,currentmovenumber,coords[0],coords[1],true);
		     Lizzie.board.clearforedit();
		     Lizzie.board.setlist(Lizzie.board.tempallmovelist);
		     Lizzie.board.goToMoveNumber(currentmovenumber+1);
	    }
	  
  }

  private void cancelavoid() {
    allowcoords = "";
    avoidcoords = "";
    move = 0;
    Lizzie.leelaz.ponder();
  }

  private void addwhite() {
	  if (Lizzie.frame.iscoordsempty(mousex, mousey)) {

		     int[]  coords = Lizzie.frame.convertmousexytocoords(mousex, mousey);
		      int currentmovenumber=Lizzie.board.getcurrentmovenumber();
			     Lizzie.board.savelistforeditmode();
			     
			     Lizzie.board.editmovelistadd(Lizzie.board.tempallmovelist,currentmovenumber,coords[0],coords[1],false);
			     Lizzie.board.clearforedit();
			     Lizzie.board.setlist(Lizzie.board.tempallmovelist);
			     Lizzie.board.goToMoveNumber(currentmovenumber+1);
		    }
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
    isforcing = true;
    isallow = false;
    allowcoords = "";
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturnponder(), avoidcoords, 30);

  }

  public static void voidanalyze() {

    if (avoidcoords == "") {
      avoidcoords = "A0";
    }
    allowcoords = "";
    isforcing = true;
    isallow = false;
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturn(), avoidcoords, 30);

  }

  private void avoid2() {
    Lizzie.leelaz.notPondering();
    Lizzie.frame.openAvoidmoves();
  }

  public void Store(int x, int y) {
    mousex = x;
    mousey = y;
  }
}
