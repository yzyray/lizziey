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

public class Avoidmoves extends JDialog {
  private JFormattedTextField txtMoveNumber;
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private int changeMoveNumber;
  private static JTextField defaultText = new JTextField();

  public Avoidmoves() {
    setType(Type.POPUP);
    setTitle("输入强制不分析的持续手数");
    setBounds(0, 0, 240, 150);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton("确定");
    okButton.setBounds(80, 68, 74, 29);
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

    JLabel lblChangeTo = new JLabel("设置往后多少手内不分析：");
    lblChangeTo.setBounds(45, 34, 80, 20);
    buttonPane.add(lblChangeTo);
    lblChangeTo.setHorizontalAlignment(SwingConstants.LEFT);

    txtMoveNumber =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtMoveNumber.setBounds(110, 34, 60, 20);
    buttonPane.add(txtMoveNumber);
    txtMoveNumber.setColumns(10);

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
    // Lizzie.board.changeMove(txtFieldValue(txtMoveNumber), getChangeToType());
    featurecat.lizzie.gui.RightClickMenu.startmove = Lizzie.board.getcurrentmovenumber();
    featurecat.lizzie.gui.RightClickMenu.move =
        changeMoveNumber + Lizzie.board.getcurrentmovenumber();
    ;
    featurecat.lizzie.gui.RightClickMenu.voidanalyze();
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
    Color c = defaultText.getBackground();
    if (changeMoveNumber <= 0) {
      txtMoveNumber.setToolTipText(
          resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
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
}
