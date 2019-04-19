package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Board;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ChangeMoveDialog2 extends JDialog {
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private JRadioButton rdoChangeCoord;
  private JRadioButton rdoPass;
  private JRadioButton rdoSwap;
  private JFormattedTextField txtMoveNumber;
  private JTextField txtChangeCoord;
  private int changeMoveNumber;
  private String changePosition;
  private static JTextField defaultText = new JTextField();
  public int movenumber = -1;
  public boolean isthisbranch = true;

  public ChangeMoveDialog2() {
    setTitle(resourceBundle.getString("LizzieChangeMove.title.config"));
    setModalityType(ModalityType.APPLICATION_MODAL);
    setType(Type.POPUP);
    setBounds(100, 100, 330, 207);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton(resourceBundle.getString("LizzieChangeMove.button.ok"));
    okButton.setBounds(75, 128, 74, 29);
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

    JButton cancelButton = new JButton(resourceBundle.getString("LizzieChangeMove.button.cancel"));
    cancelButton.setBounds(180, 128, 74, 29);
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);

    JLabel lblChangeTo = new JLabel(resourceBundle.getString("LizzieChangeMove.title.changeTo2"));
    lblChangeTo.setBounds(30, 95, 74, 14);
    buttonPane.add(lblChangeTo);
    lblChangeTo.setHorizontalAlignment(SwingConstants.LEFT);

    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    txtChangeCoord = new JFormattedTextField();
    txtChangeCoord.setBounds(117, 92, 60, 20);
    buttonPane.add(txtChangeCoord);
    txtChangeCoord.setColumns(10);

    ButtonGroup group = new ButtonGroup();
    rdoChangeCoord = new JRadioButton("");
    rdoChangeCoord.setBounds(90, 91, 28, 21);
    rdoChangeCoord.setSelected(true);
    buttonPane.add(rdoChangeCoord);
    rdoChangeCoord.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (rdoChangeCoord.isSelected()) {
              txtChangeCoord.setEnabled(true);
            } else {
              txtChangeCoord.setEnabled(false);
            }
          }
        });
    group.add(rdoChangeCoord);

    //    rdoSwap = new JRadioButton(resourceBundle.getString("LizzieChangeMove.rdoSwap.text"));
    //    rdoSwap.setBounds(290, 91, 82, 23);
    //    buttonPane.add(rdoSwap);
    //    group.add(rdoSwap);

    rdoPass = new JRadioButton(resourceBundle.getString("LizzieChangeMove.rdoPass.text"));
    rdoPass.setBounds(189, 91, 83, 23);
    buttonPane.add(rdoPass);
    group.add(rdoPass);

    //    JLabel lblMoveNumber =
    //        new JLabel(resourceBundle.getString("LizzieChangeMove.title.moveNumber"));
    //    lblMoveNumber.setBounds(10, 67, 95, 14);
    //    buttonPane.add(lblMoveNumber);

    //    txtMoveNumber =
    //        new JFormattedTextField(
    //            new InternationalFormatter(nf) {
    //              protected DocumentFilter getDocumentFilter() {
    //                return filter;
    //              }
    //
    //              private DocumentFilter filter = new DigitOnlyFilter();
    //            });
    //    txtMoveNumber.setBounds(117, 64, 60, 20);
    //    buttonPane.add(txtMoveNumber);
    //    txtMoveNumber.setColumns(10);

    // JLabel lblPrompt1 = new JLabel(resourceBundle.getString("LizzieChangeMove.lblPrompt1.text"));
    // lblPrompt1.setBounds(10, 11, 398, 14);
    // buttonPane.add(lblPrompt1);

    JLabel lblPrompt2 = new JLabel(resourceBundle.getString("LizzieChangeMove.lblPrompt4.text"));
    lblPrompt2.setBounds(30, 28, 398, 14);
    buttonPane.add(lblPrompt2);

    JLabel lblPrompt3 = new JLabel(resourceBundle.getString("LizzieChangeMove.lblPrompt3.text"));
    lblPrompt3.setBounds(10, 45, 349, 14);
    buttonPane.add(lblPrompt3);

    setLocationRelativeTo(getOwner());
  }

  private void applyChange() {
    // int mv=movenumber;
    if (isthisbranch) {
      Lizzie.board.changeMove(movenumber, getChangeToType());
    } else {
      Lizzie.board.changeMove2(movenumber, getChangeToType());
    }
  }

  //  private Integer txtFieldValue(JTextField txt) {
  //    if (txt.getText().trim().isEmpty()
  //        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
  //      return 0;
  //    } else {
  //      return Integer.parseInt(txt.getText().trim());
  //    }
  //  }

  private class DigitOnlyFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
      String newStr = string != null ? string.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.insertString(offset, newStr, attr);
      }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
      String newStr = text != null ? text.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.replace(offset, length, newStr, attrs);
      }
    }
  }

  private String getChangeToType() {
    if (rdoPass.isSelected()) {
      return "pass";
    } else {
      return txtChangeCoord.getText().trim().toUpperCase();
    }
  }

  public void Store(int x, boolean thisbranch) {
    movenumber = x;
    isthisbranch = thisbranch;
  }

  private boolean checkMove() {
    boolean ret = true;
    changePosition = getChangeToType();
    Color c = defaultText.getBackground();
    Optional<int[]> changeCoord = Board.asCoordinates(changePosition);
    if ("pass".equals(changePosition)
        || (changeCoord.isPresent() && Board.isValid(changeCoord.get()))) {
      txtChangeCoord.setToolTipText("");
      txtChangeCoord.setBackground(c);
    } else {
      txtChangeCoord.setToolTipText(
          resourceBundle.getString("LizzieChangeMove.txtChangeCoord.error"));
      txtChangeCoord.setBackground(Color.red);
      Action action = txtChangeCoord.getActionMap().get("postTip");
      if (action != null) {
        ActionEvent ae =
            new ActionEvent(
                txtChangeCoord,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action.actionPerformed(ae);
      }
      ret = false;
    }
    return ret;
  }
}
