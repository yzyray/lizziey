package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.Action;
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
    // backward1 = new JButton("<");
    openfile = new JButton("打开文件");
    analyse = new JButton("分析|暂停");
    add(clearButton);
    add(lastButton);
    add(firstButton);
    add(countButton);
    add(forward10);
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
    forward1.setFocusable(false);
    openfile.setFocusable(false);
    analyse.setFocusable(false);
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

  private boolean checkMove() {

    changeMoveNumber = txtFieldValue(txtMoveNumber);
    //  changePosition = getChangeToType();
    Color c = Color.RED;
    if (changeMoveNumber < 0 || changeMoveNumber > Lizzie.board.getMaxMoveNumber()) {
      Action action = txtMoveNumber.getActionMap().get("postTip");
      if (action != null) {
        ActionEvent ae =
            new ActionEvent(
                txtMoveNumber,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action.actionPerformed(ae);
      }
      txtMoveNumber.setBackground(Color.red);
      return false;
    }
    return true;
  }

  public void setButtonLocation(int boardmid) {
    txtMoveNumber.setBounds(boardmid - 11, 1, 28, 24);
    forward1.setBounds(boardmid + 17, 0, 56, 26);
    backward10.setBounds(boardmid - 56, 0, 45, 26);
    forward10.setBounds(boardmid + 72, 0, 45, 26);
    firstButton.setBounds(boardmid - 100, 0, 45, 26);
    lastButton.setBounds(boardmid + 116, 0, 45, 26);
    analyse.setBounds(boardmid - 189, 0, 90, 26);
    openfile.setBounds(boardmid - 268, 0, 80, 26);
    clearButton.setBounds(boardmid + 160, 0, 80, 26);
    countButton.setBounds(boardmid + 239, 0, 80, 26);
  }
}
