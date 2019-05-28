package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.InternationalFormatter;

public class BottomToolbar extends JPanel {
  JButton firstButton;
  JButton lastButton;
  JButton clearButton;
  JButton countButton;
  JButton forward10;
  JButton backward10;
  JButton forward1;
  // JButton backward1;
  JButton openfile;
  JButton savefile;
  JButton analyse;
  JFormattedTextField txtMoveNumber;
  private int changeMoveNumber;

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
    forward1 = new JButton("跳转");
    savefile = new JButton("保存");
    // backward1 = new JButton("<");
    openfile = new JButton("打开");
    analyse = new JButton("分析|暂停");
    add(clearButton);
    add(lastButton);
    add(firstButton);
    add(countButton);
    add(forward10);
    add(savefile);
    add(backward10);
    add(forward1);
    // add(backward1);
    add(openfile);
    add(analyse);

    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    txtMoveNumber =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    add(txtMoveNumber);
    txtMoveNumber.setColumns(3);

    txtMoveNumber.addKeyListener(
        new KeyListener() {
          @Override
          public void keyPressed(KeyEvent arg0) {
            int key = arg0.getKeyCode();
            if (key == '\n') {
              checkMove();
              txtMoveNumber.setFocusable(false);
              txtMoveNumber.setFocusable(true);
              txtMoveNumber.setBackground(Color.WHITE);
              txtMoveNumber.setText("");
              Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);
              setAllUnfocuse();
            }
          }

          @Override
          public void keyReleased(KeyEvent e) {}

          @Override
          public void keyTyped(KeyEvent e) {}
        });
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
    forward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            checkMove();
            txtMoveNumber.setFocusable(false);
            txtMoveNumber.setFocusable(true);
            txtMoveNumber.setBackground(Color.WHITE);
            txtMoveNumber.setText("");
            // Lizzie.board.savelist(changeMoveNumber);
            // Lizzie.board.setlist();
            Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);
            setAllUnfocuse();
          }
        });
    //        backward1.addActionListener(
    //            new ActionListener() {
    //              public void actionPerformed(ActionEvent e) {
    //                Input.undo(1);
    //                setAllUnfocuse();
    //              }
    //            });
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFile();
            setAllUnfocuse();
          }
        });
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveFile();
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
              Lizzie.countResults.setVisible(false);
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
    forward1.setFocusable(false);
    openfile.setFocusable(false);
    analyse.setFocusable(false);
    savefile.setFocusable(false);
  }

  private class DigitOnlyFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
      String newStr = string != null ? string.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.insertString(offset, newStr, attr);
      }
    }
  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  private void checkMove() {

    changeMoveNumber = txtFieldValue(txtMoveNumber);
  }

  public void setButtonLocation(int boardmid) {
    openfile.setBounds(boardmid - 286, 0, 56, 26);
    txtMoveNumber.setBounds(boardmid + 2, 1, 28, 24);
    forward1.setBounds(boardmid + 30, 0, 56, 26);
    backward10.setBounds(boardmid - 43, 0, 45, 26);
    forward10.setBounds(boardmid + 85, 0, 45, 26);
    firstButton.setBounds(boardmid - 87, 0, 45, 26);
    lastButton.setBounds(boardmid + 129, 0, 45, 26);
    analyse.setBounds(boardmid - 176, 0, 90, 26);
    savefile.setBounds(boardmid - 231, 0, 56, 26);
    clearButton.setBounds(boardmid + 173, 0, 80, 26);
    countButton.setBounds(boardmid + 252, 0, 80, 26);
  }
}
