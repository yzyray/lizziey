package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
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
  JButton gotomove;
  JButton backward1;
  JButton openfile;
  JButton savefile;
  JButton analyse;
  JFormattedTextField txtMoveNumber;
  private int changeMoveNumber;
  public boolean isAutoAna = false;

  JCheckBox chkAutoAnalyse;
  public JCheckBox chkAnaTime;
  public JCheckBox chkAnaPlayouts;
  public JCheckBox chkAnaFirstPlayouts;
  public JFormattedTextField txtAnaTime;
  public JFormattedTextField txtAnaPlayouts;
  public JFormattedTextField txtAnaFirstPlayouts;
  // JButton cancelAutoAna;
  JLabel lblchkAutoAnalyse;
  JLabel lbltxtAnaTime;
  JLabel lbltxtAnaPlayouts;
  JLabel lblAnaFirstPlayouts;

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
    gotomove = new JButton("跳转");
    savefile = new JButton("保存");
    backward1 = new JButton("<");
    forward1 = new JButton(">");
    openfile = new JButton("打开");
    analyse = new JButton("分析|暂停");
    add(clearButton);
    add(lastButton);
    add(firstButton);
    add(countButton);
    add(forward10);
    add(savefile);
    add(backward10);
    add(gotomove);
    add(backward1);
    add(forward1);
    add(openfile);
    add(analyse);

    firstButton.setFocusable(false);
    lastButton.setFocusable(false);
    clearButton.setFocusable(false);
    countButton.setFocusable(false);
    forward10.setFocusable(false);
    backward10.setFocusable(false);
    gotomove.setFocusable(false);
    openfile.setFocusable(false);
    analyse.setFocusable(false);
    forward1.setFocusable(false);
    backward1.setFocusable(false);
    savefile.setFocusable(false);

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
              //  setAllUnfocuse();
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
            // setAllUnfocuse();
          }
        });
    forward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(10);
            //  setAllUnfocuse();
          }
        });
    backward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(10);
            //  setAllUnfocuse();
          }
        });
    forward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(1);
            // setAllUnfocuse();
          }
        });
    backward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(1);
            //  setAllUnfocuse();
          }
        });
    gotomove.addActionListener(
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
            //  setAllUnfocuse();
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
            // setAllUnfocuse();
          }
        });
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveFile();
            //  setAllUnfocuse();
          }
        });
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clear();
            //  setAllUnfocuse();
          }
        });
    lastButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.nextMove()) ;
            //   setAllUnfocuse();
          }
        });
    firstButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.previousMove()) ;
            //   setAllUnfocuse();
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
            //    setAllUnfocuse();
          }
        });

    this.addMouseListener(
        new MouseListener() {
          public void mouseClicked(MouseEvent e) {
            if (txtMoveNumber.isFocusOwner()) {

              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
            }
            if (txtAnaTime.isFocusOwner()) {
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaTime.setFocusable(true);
            }
            if (txtAnaPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
            }
            if (txtAnaFirstPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
            }
          }

          @Override
          public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

          }
        });

    chkAutoAnalyse = new JCheckBox();
    add(chkAutoAnalyse);
    lblchkAutoAnalyse = new JLabel("自动分析");
    chkAutoAnalyse.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            isAutoAna = chkAutoAnalyse.isSelected();
            if (txtMoveNumber.isFocusOwner()) {

              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
            }
            if (txtAnaTime.isFocusOwner()) {
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaTime.setFocusable(true);
            }
            if (txtAnaPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
            }
            if (txtAnaFirstPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
            }
          }
        });
    add(lblchkAutoAnalyse);
    chkAutoAnalyse.setBounds(5, 26, 20, 20);
    lblchkAutoAnalyse.setBounds(25, 26, 60, 20);

    chkAnaTime = new JCheckBox();
    lbltxtAnaTime = new JLabel("按时间(秒):");

    chkAnaTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (txtMoveNumber.isFocusOwner()) {

              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
            }
            if (txtAnaTime.isFocusOwner()) {
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaTime.setFocusable(true);
            }
            if (txtAnaPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
            }
            if (txtAnaFirstPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
            }
          }
        });

    add(chkAnaTime);
    add(lbltxtAnaTime);
    chkAnaTime.setBounds(135, 26, 20, 20);
    lbltxtAnaTime.setBounds(155, 26, 80, 20);
    txtAnaTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    add(txtAnaTime);
    txtAnaTime.setBounds(220, 26, 50, 18);

    chkAnaPlayouts = new JCheckBox();
    chkAnaPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (txtMoveNumber.isFocusOwner()) {

              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
            }
            if (txtAnaTime.isFocusOwner()) {
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaTime.setFocusable(true);
            }
            if (txtAnaPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
            }
            if (txtAnaFirstPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
            }
          }
        });
    lbltxtAnaPlayouts = new JLabel("总计算量:");
    add(chkAnaPlayouts);
    add(lbltxtAnaPlayouts);
    chkAnaPlayouts.setBounds(5, 48, 20, 20);
    lbltxtAnaPlayouts.setBounds(25, 48, 80, 20);
    txtAnaPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    add(txtAnaPlayouts);
    txtAnaPlayouts.setBounds(85, 48, 50, 18);

    chkAnaFirstPlayouts = new JCheckBox();
    chkAnaFirstPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (txtMoveNumber.isFocusOwner()) {

              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
            }
            if (txtAnaTime.isFocusOwner()) {
              txtAnaPlayouts.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaTime.setFocusable(true);
            }
            if (txtAnaPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
            }
            if (txtAnaFirstPlayouts.isFocusOwner()) {
              txtAnaTime.setFocusable(false);
              txtAnaPlayouts.setFocusable(false);
              txtMoveNumber.setFocusable(false);
              txtAnaFirstPlayouts.setFocusable(false);
              txtAnaTime.setFocusable(true);
              txtAnaPlayouts.setFocusable(true);
              txtMoveNumber.setFocusable(true);
              txtAnaFirstPlayouts.setFocusable(true);
            }
          }
        });
    lblAnaFirstPlayouts = new JLabel("首位计算量:");
    add(chkAnaFirstPlayouts);
    add(lblAnaFirstPlayouts);
    chkAnaFirstPlayouts.setBounds(135, 48, 20, 20);
    lblAnaFirstPlayouts.setBounds(155, 48, 80, 20);
    txtAnaFirstPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    add(txtAnaFirstPlayouts);
    txtAnaFirstPlayouts.setBounds(220, 48, 50, 18);

    chkAutoAnalyse.setFocusable(false);
    chkAnaTime.setFocusable(false);
    chkAnaPlayouts.setFocusable(false);
    chkAnaFirstPlayouts.setFocusable(false);
  }

  //  public void setAllUnfocuse() {
  //    firstButton.setFocusable(false);
  //    lastButton.setFocusable(false);
  //    clearButton.setFocusable(false);
  //    countButton.setFocusable(false);
  //    forward10.setFocusable(false);
  //    backward10.setFocusable(false);
  //    gotomove.setFocusable(false);
  //    openfile.setFocusable(false);
  //    analyse.setFocusable(false);
  //    forward1.setFocusable(false);
  //    backward1.setFocusable(false);
  //    savefile.setFocusable(false);
  // }

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
    int w = Lizzie.frame.getWidth();
    if (w - boardmid - 30 < 370) boardmid = w / 2 - 50;
    forward1.setBounds(boardmid + 21, 0, 38, 26);
    backward1.setBounds(boardmid - 16, 0, 38, 26);
    openfile.setBounds(boardmid - 303, 0, 56, 26);
    txtMoveNumber.setBounds(boardmid + 305, 1, 28, 24);
    gotomove.setBounds(boardmid + 333, 0, 56, 26);
    backward10.setBounds(boardmid - 60, 0, 45, 26);
    forward10.setBounds(boardmid + 58, 0, 45, 26);
    firstButton.setBounds(boardmid - 104, 0, 45, 26);
    lastButton.setBounds(boardmid + 102, 0, 45, 26);
    analyse.setBounds(boardmid - 193, 0, 90, 26);
    savefile.setBounds(boardmid - 248, 0, 56, 26);
    clearButton.setBounds(boardmid + 146, 0, 80, 26);
    countButton.setBounds(boardmid + 225, 0, 80, 26);
  }
}
