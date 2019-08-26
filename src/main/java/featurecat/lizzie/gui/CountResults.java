package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.json.JSONArray;

public class CountResults extends JDialog {
  public int allblackcounts = 0;
  public int allwhitecounts = 0;
  int blackEat = 0;
  int whiteEat = 0;
  JPanel buttonpanel = new JPanel();
  public boolean iscounted = false;
  public boolean isAutocounting = false;
  public JButton button = new JButton("形式判断");
  public JButton button2 = new JButton("自动判断");

  public CountResults(Window owner) {
    super(owner);
    this.add(buttonpanel, BorderLayout.SOUTH);
    this.setResizable(false);
    this.setTitle("形式判断");
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            invisiable();
            Lizzie.frame.boardRenderer.removecountblock();
            Lizzie.frame.repaint();
            Lizzie.frame.iscounting = false;
            iscounted = false;
            try {
              Lizzie.frame.subBoardRenderer.removecountblock();
            } catch (Exception es) {
            }

            Lizzie.frame.repaint();
            button2.setText("自动判断");
            Lizzie.frame.isAutocounting = false;
          }
        });

    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("movecount-position") != null
        && Lizzie.config.persistedUi.optJSONArray("movecount-position").length() == 2) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("movecount-position");
      setBounds(pos.getInt(0), pos.getInt(1), 240, 180);
    } else {
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds(0, (int) screensize.getHeight() / 2 - 125, 240, 180); // 240
    }

    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    button2.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.zen.noread = false;
            if (!isAutocounting) {
              Lizzie.frame.isAutocounting = true;
              Lizzie.frame.zen.syncboradstat();
              Lizzie.frame.zen.countStones();
              button2.setText("停止判断");

            } else {
              try {
                Lizzie.frame.subBoardRenderer.removecountblock();
              } catch (Exception es) {
              }
              Lizzie.frame.boardRenderer.removecountblock();
              Lizzie.frame.repaint();
              // Lizzie.frame.iscounting=false;
              button2.setText("自动判断");
              Lizzie.frame.isAutocounting = false;
              // Lizzie.frame.setVisible(true);
            }
            isAutocounting = !isAutocounting;
          }
        });
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.zen.noread = false;
            if (!iscounted) {
              Lizzie.frame.countstones();
              Lizzie.frame.iscounting = true;
              button.setText("关闭判断");
            } else {
              Lizzie.frame.boardRenderer.removecountblock();
              Lizzie.frame.repaint();
              Lizzie.frame.iscounting = false;
              button.setText("判断形势");
              // Lizzie.frame.setVisible(true);
            }
            iscounted = !iscounted;
          }
        });
    button.setBounds(0, 240, 60, 20);
    button2.setBounds(100, 240, 60, 20);
    buttonpanel.setBounds(0, 240, 60, 20);
    buttonpanel.add(button);
    buttonpanel.add(button2);
  }

  public void Counts(
      int blackEatCount,
      int whiteEatCount,
      int blackPrisonerCount,
      int whitePrisonerCount,
      int blackpont,
      int whitepoint) {
    // synchronized (this) {
    allblackcounts = 0;
    allwhitecounts = 0;
    blackEat = 0;
    whiteEat = 0;

    allblackcounts = blackpont + blackEatCount + whitePrisonerCount;
    allwhitecounts = whitepoint + whiteEatCount + blackPrisonerCount;
    blackEat = blackEatCount;
    whiteEat = whiteEatCount;
    if (!Lizzie.frame.isAutocounting) {
      button.setText("关闭判断");
      iscounted = true;
    }

    repaint();
    //  }
  }

  public void paint(Graphics g) // 画图对象
      {

    Graphics2D g2 = (Graphics2D) g;

    Image image = null;
    try {
      image = ImageIO.read(getClass().getResourceAsStream("/assets/background.jpg"));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    int withtimes = (340 / image.getWidth(getOwner())) + 1;
    int highttimes = (260 / image.getHeight(getOwner())) + 1;

    for (int i = 0; i < highttimes; i++) {
      for (int j = 0; j < withtimes; j++) {
        g2.drawImage(image, image.getWidth(getOwner()) * j, image.getHeight(getOwner()) * i, null);
      }
    }

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2f));
    g2.fillOval(30, 55, 20, 20);
    // g2.drawOval(260,50, 32, 32);
    g2.setColor(Color.WHITE);
    g2.fillOval(170, 55, 20, 20);
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
    allFont = new Font("allFont", Font.BOLD, 25);
    g2.setFont(allFont);
    if (allblackcounts >= allwhitecounts) {
      g2.setColor(Color.BLACK);
      g2.drawString("黑", 25, 50);
    } else {
      g2.setColor(Color.WHITE);
      g2.drawString("白", 25, 50);
    }
    allFont = new Font("allFont", Font.BOLD, 20);
    g2.setFont(allFont);
    g2.drawString("  盘面领先:  " + Math.abs(allblackcounts - allwhitecounts) + "目", 53, 50);
    allFont = new Font("allFont", Font.BOLD, 15);
    g2.setColor(Color.BLACK);
    g2.setFont(allFont);
    g2.drawString("目数", 95, 100);
    g2.drawString("提子", 95, 130);
    g2.drawString(allblackcounts + "", 32, 100); // 黑目数
    g2.drawString(blackEat + "", 32, 130); // 黑提子
    g2.setColor(Color.WHITE);
    g2.drawString(allwhitecounts + "", 172, 100); // 白目数
    g2.drawString(whiteEat + "", 172, 130); // 白提子
    button.repaint();
    button2.repaint();
  }

  private void invisiable() {
    this.setVisible(false);
  }
}
