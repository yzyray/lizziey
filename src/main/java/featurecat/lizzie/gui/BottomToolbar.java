package featurecat.lizzie.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import featurecat.lizzie.Lizzie;

public class BottomToolbar extends JPanel{
	JButton firstButton;
	JButton lastButton;
	JButton clearButton;
	JButton countButton;
	public BottomToolbar() {		
	    Color hsbColor =
	            Color.getHSBColor(
	                Color.RGBtoHSB(232, 232, 232, null)[0],
	                Color.RGBtoHSB(232, 232, 232, null)[1],
	                Color.RGBtoHSB(232, 232, 232, null)[2]);
	        this.setBackground(hsbColor);
		setLayout(null);
		clearButton = new JButton("清空棋盘");
		  firstButton = new JButton("|<");
		  lastButton = new JButton(">|");
		  countButton = new JButton("形势判断");
		 add(clearButton);
		 add(lastButton);
		 add(firstButton);
		 add(countButton);
		 clearButton.addActionListener(
			        new ActionListener() {			        	
			          public void actionPerformed(ActionEvent e) {
			        	  Lizzie.board.clear();
			        	  clearButton.setFocusable(false);			        	  
			           lastButton.setFocusable(false);
			           firstButton.setFocusable(false);		
			           countButton.setFocusable(false);	
			          }
			        });
		 lastButton.addActionListener(
			        new ActionListener() {
			          public void actionPerformed(ActionEvent e) {
			        	  while (Lizzie.board.nextMove()) ;
			        	  firstButton.setFocusable(false);
			        	  lastButton.setFocusable(false);
			        	  clearButton.setFocusable(false);
			        	  countButton.setFocusable(false);	
			          }
			        });
		 firstButton.addActionListener(
			        new ActionListener() {
			          public void actionPerformed(ActionEvent e) {
			        	  while (Lizzie.board.previousMove()) ;
			        	  firstButton.setFocusable(false);
			        	  lastButton.setFocusable(false);
			        	  clearButton.setFocusable(false);
			        	  countButton.setFocusable(false);	
			          }
			        });
		 countButton.addActionListener(
			        new ActionListener() {
			          public void actionPerformed(ActionEvent e) {
			        	  if(Lizzie.frame.iscounting)
			        	  {Lizzie.frame.boardRenderer.removecountblock();
			              Lizzie.frame.repaint();
			              Lizzie.frame.iscounting = false;
			              Lizzie.frame.countResults.setVisible(false);}
			        	  else {
			        	  Lizzie.frame.countstones();
			        	  }
			        	  firstButton.setFocusable(false);
			        	  lastButton.setFocusable(false);
			        	  clearButton.setFocusable(false);
			        	  countButton.setFocusable(false);	
			          }
			        });
	}
	
	public void setButtonLocation(int boardmid)
	{
		firstButton.setBounds(boardmid-25, 0, 45, 20);
		lastButton.setBounds(boardmid+25, 0, 45, 20);
		clearButton.setBounds(boardmid-110, 0, 80, 20);
		countButton.setBounds(boardmid+75, 0, 80, 20);
	}
}
