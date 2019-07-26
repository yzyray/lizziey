package featurecat.lizzie.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.Window.Type;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import featurecat.lizzie.Lizzie;

public class EditToolbar extends JDialog {

  public EditToolbar(Window owner) {
	  super(owner);
	  ImageIcon iconblack = new ImageIcon();
	    try {
	      iconblack.setImage(
	          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallblack.png")));
	      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }

	    ImageIcon iconwhite = new ImageIcon();
	    try {
	      iconwhite.setImage(
	          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png")));
	      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png"));
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }

	    ImageIcon iconbh = new ImageIcon();
	    try {
	      // iconbh.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
	      iconbh.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/hb.png")));
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	  JButton black = new JButton(iconblack);
      JButton white = new JButton(iconwhite);
      JButton blackwhite = new JButton(iconbh);
      black.setFocusable(false);
      white.setFocusable(false);
      blackwhite.setFocusable(false);
      black.setBounds(0,0,20,20);
      white.setBounds(20,0,20,20);
      blackwhite.setBounds(40,0,20,20);
      setType(Type.POPUP);
      setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
      setBounds(0, 0, 60, 20);
      setLayout(null);
      this.setUndecorated(true);
      this.add(black);
      this.add(white);
      this.add(blackwhite);
     
  }
}
