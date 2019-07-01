package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.InternationalFormatter;

public class SetBoardSize extends JDialog {
  private JFormattedTextField width;
  private JFormattedTextField height;
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private int widthNumber;
  private int heightNumber;
  private static JTextField defaultText = new JTextField();

  public SetBoardSize() {
    setType(Type.POPUP);
    setTitle("设置棋盘大小");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setBounds(0, 0, 340, 130);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton("确定");
    okButton.setBounds(120, 55, 74, 29);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (checkMove()) {
              setVisible(false);
              applyChange();
            }
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JLabel lblChangeTo = new JLabel("设置棋盘大小    宽：");
    lblChangeTo.setBounds(45, 24, 120, 20);
    buttonPane.add(lblChangeTo);
    lblChangeTo.setHorizontalAlignment(SwingConstants.LEFT);

    width =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    width.setBounds(210, 26, 30, 20);
    buttonPane.add(width);
    width.setColumns(3);

    width.setText(Lizzie.board.boardWidth + "");

    JLabel lblheight = new JLabel("高：");
    lblheight.setBounds(185, 24, 30, 20);
    buttonPane.add(lblheight);
    lblheight.setHorizontalAlignment(SwingConstants.LEFT);

    height =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    height.setBounds(150, 26, 30, 20);
    buttonPane.add(height);
    height.setColumns(3);

    height.setText(Lizzie.board.boardHeight + "");

    setLocationRelativeTo(getOwner());
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

  private void applyChange() {

    Lizzie.board.reopen(widthNumber, heightNumber);
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

    widthNumber = txtFieldValue(width);
    heightNumber = txtFieldValue(height);
    //  changePosition = getChangeToType();
    Color c = defaultText.getBackground();
    if (widthNumber < 2 || heightNumber < 2) {
      width.setToolTipText(resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
      height.setToolTipText(resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
      Action action = width.getActionMap().get("postTip");
      Action action2 = height.getActionMap().get("postTip");
      if (action != null) {
        ActionEvent ae =
            new ActionEvent(
                height,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action.actionPerformed(ae);
        ActionEvent ae2 =
            new ActionEvent(
                width,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action2.actionPerformed(ae2);
      }
      width.setBackground(Color.red);
      height.setBackground(Color.red);
      return false;
    }
    return true;
  }
}
