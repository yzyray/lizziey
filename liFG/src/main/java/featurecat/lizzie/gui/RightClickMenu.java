package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.Input;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class RightClickMenu extends JPopupMenu {
  public static int mousex;
  public static int mousey;
  private JMenuItem insertmode;
  private JMenuItem addblack;
  private JMenuItem addwhite;
  private JMenuItem addone;
  private JMenuItem deleteone;
  private JMenuItem quitinsert;
  private JMenuItem allow;
  private JMenuItem avoid;
  private JMenuItem avoid2;
  private static JMenuItem cancelavoid;
  private BoardRenderer boardRenderer;
  public  String allowcoords="";
  public static String avoidcoords="";
  public static int move=0;
 
  public RightClickMenu() {
    insertmode = new JMenuItem("进入插入棋子模式");
    quitinsert = new JMenuItem("退出插入棋子模式");
    addblack = new JMenuItem("插入黑子");
    addwhite = new JMenuItem("插入白子");
    addone = new JMenuItem("轮流插入棋子");
    deleteone = new JMenuItem("更改棋子位置");
    allow = new JMenuItem("强制分析此点");
    avoid = new JMenuItem("强制不分析此点");
    avoid2 = new JMenuItem("设置强制不分析持续手数");
    cancelavoid = new JMenuItem("清除强制分析/不分析");
    
    // this.add(addblack);
    // this.add(addwhite);
    this.add(allow);
    this.add(avoid);    
    this.add(cancelavoid);
    this.add(avoid2);
    this.add(insertmode);
    this.add(deleteone);

  //Lizzie.frame.RightClickMenu.show(invoker, x, y);
    insertmode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("进入插入棋子模式");
            insertmode();
          }
        });
    quitinsert.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("退出插入棋子模式");
            quitinsertmode();
          }
        });
    addblack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("增加黑子");
            addblack();
          }
        });
    addwhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("增加白子");
            addwhite();
          }
        });
    addone.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                System.out.println("轮流插入棋子");
                addone();
              }
            });
    allow.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("强制分析");
            allow();
          }
        });
    avoid.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("强制不分析");
            avoid();
            
          }
        });
    avoid2.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                System.out.println("设置强制不分析持续手数");
                avoid2();
              }
            });
    deleteone.addActionListener(
    		new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println("更改棋子位置");
          delete();
        }
      });
    cancelavoid.addActionListener(
    		new ActionListener() {
    	        @Override
    	        public void actionPerformed(ActionEvent e) {
    	          System.out.println("清除所有强制分析设置");
    	          cancelavoid();
    	        }
    	      });
    
  }
  

  private void insertmode() {

    boolean isinsertmode;
    isinsertmode = Lizzie.board.insertmode();
    if (isinsertmode) {
      this.remove(insertmode);
      this.remove(allow);
      this.remove(avoid);
      this.remove(avoid2);      
      this.add(quitinsert);
      this.add(addblack);
      this.add(addwhite);
      this.add(addone);   
      this.add(deleteone);
      this.add(allow);
      this.add(avoid);  
      this.add(cancelavoid);
      this.add(avoid2);
      
    }
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.ponder();
    }
  }

  private void quitinsertmode() {
    this.remove(quitinsert);
    this.remove(addblack);
    this.remove(addwhite);
    this.remove(addone);
    this.remove(deleteone);
    this.add(insertmode);
    this.add(allow);
    this.add(avoid);
    this.add(cancelavoid);
    this.add(avoid2);
    this.add(deleteone);    
    Lizzie.board.quitinsertmode();
    if (Lizzie.leelaz.isPondering()) {
        Lizzie.leelaz.ponder();
      }
    
  }
  
  private void addblack() {

	    Lizzie.frame.insertMove(mousex, mousey, true);
	  }

  private void cancelavoid() {
	  allowcoords="";
	  avoidcoords="";
	  move=0;	  
		  Lizzie.leelaz.ponder();
  }

  private void addwhite() {
    Lizzie.frame.insertMove(mousex, mousey, false);
  }
  
  private void addone() {
	    Lizzie.frame.insertMove(mousex, mousey);
	  }


  private void allow() {	 
	  String color=Lizzie.frame.getstonecolor(mousex,mousey);
	  if (color!="N")
	  { if (allowcoords=="")
	 {
			 allowcoords= Lizzie.frame.convertmousexy(mousex, mousey);
	 }
		 else
		 {
			 allowcoords=allowcoords+","+Lizzie.frame.convertmousexy(mousex, mousey);
		 }
	  Lizzie.leelaz.analyzeAvoid( "allow", color, allowcoords, 1);
	 Lizzie.frame.isshowrightmenu=false;
	 
	  }
  }

  public static void avoid() {	  
	  String color=Lizzie.frame.getstonecolor(mousex,mousey);
	  if (color!="N")
	  {  if (avoidcoords=="")
			 {
		  			avoidcoords= Lizzie.frame.convertmousexy(mousex, mousey);
			 }
				 else
				 {
					 avoidcoords=avoidcoords+","+Lizzie.frame.convertmousexy(mousex, mousey);
				 }
	  Lizzie.leelaz.analyzeAvoid( "avoid", color, avoidcoords, 99);
	 Lizzie.frame.isshowrightmenu=false;	 
	  }
  }
  

  private void avoid2() {	  
	  Lizzie.frame.openAvoidmoves();
  }
  
  private void delete() {
	int movenumber=Lizzie.frame.getmovenumber(mousex, mousey);
	int movenumberinbranch=Lizzie.frame.getmovenumberinbranch(mousex, mousey);
	if(movenumber<1) {
		return;
	}
	if(featurecat.lizzie.gui.Input.isinsertmode&&movenumberinbranch<=0)
	{
		JOptionPane.showMessageDialog(null, "插入模式下只能修改当前分支的棋子");
		return;
	}
	if(featurecat.lizzie.gui.Input.isinsertmode&&movenumberinbranch>0)
	{
		 Lizzie.frame.openChangeMoveDialog2(movenumber,true);
		 return;
	}
	if (movenumberinbranch<=1)
	{
		
int n = JOptionPane.showConfirmDialog(null, "修改当前分支以外的棋子(或者当前分支第一步)不能继承分支变化", "确认修改", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			Lizzie.frame.openChangeMoveDialog2(movenumber,false);
		} else if (n == JOptionPane.NO_OPTION) {
			return;
		}

		//Lizzie.frame.openChangeMoveDialog2(movenumber);
	}
	//System.out.println(movenumber);

	else
	 {
	 Lizzie.frame.openChangeMoveDialog2(movenumber,true);
	 return;
	 }
  }
  
  public void Store(int x, int y) {
    mousex = x;
    mousey = y;
  }
  
}
