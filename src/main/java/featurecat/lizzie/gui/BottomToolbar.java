package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BottomToolbar extends JPanel {
  JButton firstButton;
  JButton lastButton;
  JButton clearButton;
  JButton countButton;
  JButton forward10;
  JButton backward10;
  JButton openfile;
  JButton analyse;

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
    forward10 = new JButton(">>");
    backward10 = new JButton("<<");
    openfile = new JButton("打开文件");
    analyse = new JButton("分析|暂停");
    add(clearButton);
    add(lastButton);
    add(firstButton);
    add(countButton);
    add(forward10);
    add(backward10);
    add(openfile);
    add(analyse);
    analyse.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.togglePonder();
            setAllUnfocuse();
          }
        });
    forward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(10);
            setAllUnfocuse();
          }
        });
    backward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(10);
            setAllUnfocuse();
          }
        });
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFile();
            setAllUnfocuse();
          }
        });
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clear();
            setAllUnfocuse();
          }
        });
    lastButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.nextMove()) ;
            setAllUnfocuse();
          }
        });
    firstButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.previousMove()) ;
            setAllUnfocuse();
          }
        });
    countButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.iscounting) {
              Lizzie.frame.boardRenderer.removecountblock();
              Lizzie.frame.repaint();
              Lizzie.frame.iscounting = false;
              Lizzie.frame.countResults.setVisible(false);
            } else {
              Lizzie.frame.countstones();
            }
            setAllUnfocuse();
          }
        });
  }

  public void setAllUnfocuse() {
    firstButton.setFocusable(false);
    lastButton.setFocusable(false);
    clearButton.setFocusable(false);
    countButton.setFocusable(false);
    forward10.setFocusable(false);
    backward10.setFocusable(false);
    openfile.setFocusable(false);
    analyse.setFocusable(false);
  }

  public void setButtonLocation(int boardmid) {
    backward10.setBounds(boardmid - 25, 0, 45, 20);
    forward10.setBounds(boardmid + 25, 0, 45, 20);
    firstButton.setBounds(boardmid - 75, 0, 45, 20);
    lastButton.setBounds(boardmid + 75, 0, 45, 20);
    openfile.setBounds(boardmid - 160, 0, 80, 20);
    analyse.setBounds(boardmid - 250, 0, 90, 20);
    clearButton.setBounds(boardmid + 125, 0, 80, 20);
    countButton.setBounds(boardmid + 210, 0, 80, 20);
  }
}
