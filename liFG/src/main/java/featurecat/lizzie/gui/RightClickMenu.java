package featurecat.lizzie.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class RightClickMenu extends JPopupMenu {	
	 private JMenuItem insertmode;
	 private JMenuItem addblack;
	 private JMenuItem addwhite;
	 private JMenuItem quitinsert;
	 private JMenuItem allow;
	 private JMenuItem avoid;
	
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
			  this.remove(insertmode);
			  this.remove(allow);
			  this.remove(avoid);
			  this.add(quitinsert);
			  this.add(addblack);
			  this.add(addwhite);	
			  this.add(allow);
			  this.add(avoid);
		  }
	  
		  private void quitinsertmode() {			  
			  this.remove(quitinsert);
			  this.remove(addblack);
			  this.remove(addwhite);
			  this.add(insertmode);			 
			  this.add(allow);
			  this.add(avoid);
		  }
	  
		  private void addblack()
		  {}
		  
		  private void addwhite()
		  {}
		  
		  private void allow()
		  {}
		  
		  private void avoid()
		  {}
	 

	  
	
}