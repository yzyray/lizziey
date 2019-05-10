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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.json.JSONArray;

public class CountResults extends JFrame {
  public int allblackcounts = 0;
  public int allwhitecounts = 0;
  int blackEat = 0;
  int whiteEat = 0;
  JPanel buttonpanel = new JPanel();
  public boolean iscounted = false;
  public boolean isAutocounting = false;
  public JButton button = new JButton("形式判断");
  public JButton button2 = new JButton("自动判断");

  public CountResults() {
    this.setAlwaysOnTop(true);
    this.add(buttonpanel, BorderLayout.SOUTH);
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            invisiable();
            Lizzie.frame.boardRenderer.removecountblock();
            Lizzie.frame.repaint();
            Lizzie.frame.iscounting = false;
            iscounted = false;
            Lizzie.frame.subBoardRenderer.removecountblock();
            Lizzie.frame.repaint();
            button2.setText("自动判断");
            Lizzie.frame.isAutocounting = false;
          }
        });

    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("movecount-position") != null
        && Lizzie.config.persistedUi.optJSONArray("movecount-position").length() == 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("movecount-position");
      setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    } else {
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds(0, (int) screensize.getHeight() / 2 - 125, 340, 260); // 240
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
              Lizzie.frame.subBoardRenderer.removecountblock();
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
    button.setBounds(0, 240, 100, 20);
    button2.setBounds(100, 240, 100, 20);
    buttonpanel.setBounds(0, 240, 100, 20);
    buttonpanel.add(button);
    buttonpanel.add(button2);
  }

  public void Counts(
      int blackEatCount,
      int whiteEatCount,
      int blackPrisonerCount,
      int whitePrisonerCount,
      ArrayList<Integer> tempcount) {
    allblackcounts = 0;
    allwhitecounts = 0;
    blackEat = 0;
    whiteEat = 0;
    int blackcounts = 0;
    int whitecounts = 0;

    for (int i = 0; i < tempcount.size(); i++) {
      if (tempcount.get(i) > 0) blackcounts++;
      if (tempcount.get(i) < 0) whitecounts++;
    }
    allblackcounts = blackcounts + blackEatCount + whitePrisonerCount;
    allwhitecounts = whitecounts + whiteEatCount + blackPrisonerCount;
    blackEat = blackEatCount;
    whiteEat = whiteEatCount;
    if (!Lizzie.frame.isAutocounting) {
      button.setText("关闭判断");
      iscounted = true;
    }
    this.setResizable(false);
    repaint();
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
    int nums = (340 / image.getWidth(getOwner())) + 1;
    for (int i = 0; i < nums; i++) {
      g2.drawImage(image, image.getWidth(getOwner()) * i, 0, null);
    }

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2f));
    g2.fillOval(50, 100, 32, 32);
    // g2.drawOval(260,50, 32, 32);
    g2.setColor(Color.WHITE);
    g2.fillOval(260, 100, 32, 32);
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
    if (allblackcounts >= allwhitecounts) {
      g2.setColor(Color.BLACK);
      g2.drawString("黑", 45, 75);
    } else {
      g2.setColor(Color.WHITE);
      g2.drawString("白", 45, 75);
    }
    allFont = new Font("allFont", Font.BOLD, 25);
    g2.setFont(allFont);
    g2.drawString("盘面领先:    " + Math.abs(allblackcounts - allwhitecounts) + "目", 115, 70);
    allFont = new Font("allFont", Font.BOLD, 20);
    g2.setColor(Color.BLACK);
    g2.setFont(allFont);
    g2.drawString("目数", 145, 170);
    g2.drawString("提子", 145, 212);
    g2.drawString(allblackcounts + "", 53, 170); // 黑目数
    g2.drawString(blackEat + "", 53, 212); // 黑提子
    g2.setColor(Color.WHITE);
    g2.drawString(allwhitecounts + "", 265, 170); // 白目数
    g2.drawString(whiteEat + "", 265, 212); // 白提子
    button.repaint();
    button2.repaint();
  }

  private void invisiable() {
    this.setVisible(false);
  }
}