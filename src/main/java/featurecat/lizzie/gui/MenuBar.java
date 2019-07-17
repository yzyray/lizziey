package featurecat.lizzie.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JMenuBar;

class MenuBar extends JMenuBar {
  Color hsbColor =
      Color.getHSBColor(
          Color.RGBtoHSB(232, 232, 232, null)[0],
          Color.RGBtoHSB(232, 232, 232, null)[1],
          Color.RGBtoHSB(232, 232, 232, null)[2]);
  // Color bgColor = hsbColor;

  //  public void setColor(Color color) {
  //    bgColor = color;
  //  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(hsbColor);
    g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
  }
}
