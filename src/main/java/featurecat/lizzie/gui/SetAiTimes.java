package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.InternationalFormatter;

public class SetAiTimes extends JDialog {
  private JFormattedTextField txtMoveNumber;
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private int changeMoveNumber;
  private static JTextField defaultText = new JTextField();
  JRadioButton rdonoponder;
  JRadioButton rdoponder;

  public SetAiTimes() {
    setType(Type.POPUP);
    setTitle("设置AI用时");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setBounds(0, 0, 340, 180);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton("确定");
    okButton.setBounds(120, 100, 74, 29);

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

    JLabel lblChangeTo = new JLabel("设置AI每手棋用时(秒)：");
    lblChangeTo.setBounds(45, 24, 180, 20);
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
    txtMoveNumber.setBounds(210, 24, 60, 20);
    buttonPane.add(txtMoveNumber);
    txtMoveNumber.setColumns(10);

    txtMoveNumber.setText(
        String.valueOf(Lizzie.config.leelazConfig.getInt("max-game-thinking-time-seconds")));

    JLabel noponder = new JLabel("对弈时AI是否后台计算");
    noponder.setBounds(45, 60, 180, 20);
    buttonPane.add(noponder);

    rdoponder = new JRadioButton("是");
    rdoponder.setBounds(200, 60, 40, 23);
    buttonPane.add(rdoponder);

    rdonoponder = new JRadioButton("否");
    rdonoponder.setBounds(240, 60, 40, 23);
    buttonPane.add(rdonoponder);

    ButtonGroup rdopondergp = new ButtonGroup();
    rdopondergp.add(rdonoponder);
    rdopondergp.add(rdoponder);
    if (Lizzie.config.playponder) {
      rdoponder.setSelected(true);
    } else {
      rdonoponder.setSelected(true);
    }

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

  private boolean getPonder() {
    if (rdoponder.isSelected()) {
      Lizzie.config.playponder = true;
      return true;
    }
    if (rdonoponder.isSelected()) {
      Lizzie.config.playponder = false;
      return false;
    }
    return true;
  }

  private void applyChange() {
    try {
      Lizzie.config.leelazConfig.putOpt(
          "max-game-thinking-time-seconds", txtFieldValue(txtMoveNumber));
      Lizzie.config.leelazConfig.putOpt("play-ponder", getPonder());
      Lizzie.config.save();
    } catch (IOException e) {
      e.printStackTrace();
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
