package featurecat.lizzie.gui;


import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class CountResults extends JFrame {
	int allblackcounts=0;
	int allwhitecounts=0;
	int blackEat=0;
	int whiteEat=0;
	public CountResults() 
	{
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int)screensize.getWidth()/2-180, (int)screensize.getHeight()/2-125, 340, 240);
	}
public void Counts(int blackEatCount,int whiteEatCount,int blackPrisonerCount,int whitePrisonerCount,ArrayList<Integer> tempcount) {
	int blackcounts=0;
	int whitecounts=0;
	
	for(int i=0;i<tempcount.size();i++)
	{
		if(tempcount.get(i)>0)
			blackcounts++;
		if(tempcount.get(i)<0)
			whitecounts++;		
	}
	allblackcounts=blackcounts+blackEatCount+whitePrisonerCount;
	allwhitecounts=whitecounts+whiteEatCount+blackPrisonerCount;
	blackEat=blackEatCount;
	whiteEat=whiteEatCount;
	this.setBackground(Color.LIGHT_GRAY);
	this.setResizable(false);
	repaint();
	
  }

public void paint(Graphics g)//画图对象 
{
	 Graphics2D g2 = (Graphics2D)g;
	 
	
	 Image  image=null;
	try {
		image = ImageIO.read(getClass().getResourceAsStream("/assets/background.jpg"));
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	 g2.drawImage(image, 0, 0, null);
	 g2.drawImage(image, image.getWidth(getOwner()), 0, null);
	 g2.drawImage(image, 2*image.getWidth(getOwner()), 0, null);
	 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	
	 g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2f));
	g2.fillOval(50,100, 32, 32);
	//g2.drawOval(260,50, 32, 32);
	g2.setColor(Color.WHITE);
	g2.fillOval(260,100, 32, 32);	
	g2.setColor(Color.BLACK);
	 Font allFont;

	    try {
	    	allFont =
	          Font.createFont(
	              Font.TRUETYPE_FONT,
	              Thread.currentThread()
	                  .getContextClassLoader()
	                  .getResourceAsStream("fonts/OpenSans-Semibold.ttf"));

	    } catch (IOException | FontFormatException e) {
	      e.printStackTrace();
	    }
	    allFont = new Font("allFont", Font.BOLD, 40);
	    g2.setFont(allFont);
	    if(allblackcounts>=allwhitecounts)
	    {
	    	g2.setColor(Color.BLACK);
	    g2.drawString("黑", 45,75);
	    }
	    else
	    {
	    	g2.setColor(Color.WHITE);
	    	g2.drawString("白", 45,75);
	    }
	    allFont = new Font("allFont", Font.BOLD, 25);
	    g2.setFont(allFont);
	    g2.drawString("盘面领先:    "+Math.abs(allblackcounts-allwhitecounts)+"目", 115,70);
	    allFont = new Font("allFont", Font.BOLD, 20);
	    g2.setColor(Color.BLACK);
	    g2.setFont(allFont);
	g2.drawString("目数", 145,170);
	g2.drawString("提子", 145,212);	
	g2.drawString(allblackcounts+"", 53,170);//黑目数
	g2.drawString(blackEat+"", 53,212);//黑提子
	g2.setColor(Color.WHITE);
	g2.drawString(allwhitecounts+"", 265,170);//白目数
	g2.drawString(whiteEat+"", 265,212);//白提子
}

}
