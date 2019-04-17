package featurecat.lizzie.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Stone;

public class RightClickMenu extends JPopupMenu {	
	public int mousex;
	public int mousey;
	 private JMenuItem insertmode;
	 private JMenuItem addblack;
	 private JMenuItem addwhite;
	 private JMenuItem quitinsert;
	 private JMenuItem allow;
	 private JMenuItem avoid;
	 private  BoardRenderer boardRenderer;
	  public RightClickMenu() {
		  insertmode = new JMenuItem("进入插入棋子模式");		
		  quitinsert = new JMenuItem("退出插入棋子模式");		
		  addblack = new JMenuItem("插入黑子");		 
		  addwhite = new JMenuItem("插入白子");	
		  allow = new JMenuItem("强制分析此点");		
		  avoid = new JMenuItem("强制不分析此点");
		  this.add(insertmode);		
		  //this.add(addblack);
		  //this.add(addwhite);		 
		  this.add(allow);
		  this.add(avoid);
		  //setVisible(true);
		 // this.repaint();
		  
		  insertmode.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.out.println("进入插入棋子模式");	   
	                insertmode();
	            }
	        });
		  quitinsert.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.out.println("退出插入棋子模式");	   
	                quitinsertmode();
	            }
	        });
		  addblack.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.out.println("增加黑子");	   
	                addblack();
	            }
	        });
		  addwhite.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.out.println("增加白子");	   
	                addwhite();
	            }
	        });
		  allow.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.out.println("强制分析");	   
	                allow();
	            }
	        });
		  avoid.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.out.println("强制不分析");	   
	                avoid();
	            }
	        });
	  }
	  
		  private void insertmode() {
			  
			boolean isinsertmode;
			isinsertmode=Lizzie.board.insertmode();
			if(isinsertmode) {
			this.remove(insertmode);
			  this.remove(allow);
			  this.remove(avoid);
			  this.add(quitinsert);
			  this.add(addblack);
			  this.add(addwhite);	
			  this.add(allow);
			  this.add(avoid);
			}
			  if(!Lizzie.leelaz.isPondering())
			  {
				  Lizzie.leelaz.togglePonder();
			  }
		  }
	  
		  private void quitinsertmode() {			  
			  this.remove(quitinsert);
			  this.remove(addblack);
			  this.remove(addwhite);
			  this.add(insertmode);			 
			  this.add(allow);
			  this.add(avoid);
			  Lizzie.board.quitinsertmode();
			  if(!Lizzie.leelaz.isPondering())
			  {
				  Lizzie.leelaz.togglePonder();
			  }
	     }
	  
		  private void addblack()
		  {
			
			 Lizzie.frame.insertMove(mousex,mousey,true);
			 
		  }
		  
		  private void addwhite()
		  {
			  Lizzie.frame.insertMove(mousex,mousey,false);
		  }
		  
		  private void allow()
		  {}
		  
		  private void avoid()
		  {}

		public void Store(int x, int y) {
			mousex=x;
			mousey=y;			
		}
	 

	  
	
}