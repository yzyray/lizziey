package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
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

public class SetKomi extends JDialog {
  // private JFormattedTextField time;
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private double komi;
  private static JTextField defaultText = new JTextField();
  public static final DecimalFormat FORMAT_KOMI = new DecimalFormat("#0.0");

  public JTextField textFieldKomi;

  public SetKomi() {
    setType(Type.POPUP);
    setTitle("设置贴目");
    setAlwaysOnTop(true);
    setBounds(0, 0, 240, 95);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton("确定");
    okButton.setBounds(50, 30, 60, 25);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            applyChange();
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);

    JButton closeButton = new JButton("关闭");
    closeButton.setBounds(125, 30, 60, 25);

    closeButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    buttonPane.add(closeButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JLabel lblChangeTo = new JLabel("设置贴目(负数为到题目)：");
    lblChangeTo.setBounds(15, 4, 150, 20);
    buttonPane.add(lblChangeTo);
    lblChangeTo.setHorizontalAlignment(SwingConstants.LEFT);

    textFieldKomi = new JFormattedTextField(FORMAT_KOMI);

    textFieldKomi.setBounds(160, 6, 50, 19);
    buttonPane.add(textFieldKomi);
    textFieldKomi.setText(FORMAT_KOMI.format(Lizzie.board.getHistory().getGameInfo().getKomi()));
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
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

  private void applyChange() {
    try {
      komi = FORMAT_KOMI.parse(textFieldKomi.getText()).doubleValue();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      return;
    }
    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
    Lizzie.board.getHistory().getGameInfo().setKomi(komi);
    Lizzie.frame.komi = komi + "";
    Lizzie.leelaz.sendCommand("komi " + komi);
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }
}
