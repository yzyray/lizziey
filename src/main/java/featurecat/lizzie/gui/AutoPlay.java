package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AutoPlay extends JFrame {

  private JPanel contentPane;
  private JTextField txtAutoPlayMain;
  private JTextField txtAutoPlaySub;
  /** Create the frame. */
  public AutoPlay() {
    setBounds(100, 100, 280, 134);
    if (Lizzie.frame != null) setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setTitle("设置自动播放");
    setLocationRelativeTo(Lizzie.frame);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    JCheckBox chkAutoPlayMainbord = new JCheckBox("自动播放(大棋盘)(秒)");
    chkAutoPlayMainbord.setBounds(6, 6, 156, 33);
    contentPane.add(chkAutoPlayMainbord);
    chkAutoPlayMainbord.setFocusable(false);

    JCheckBox chkAutoPlaySubbord = new JCheckBox("自动播放(小棋盘)(毫秒)");
    chkAutoPlaySubbord.setBounds(6, 41, 168, 14);
    contentPane.add(chkAutoPlaySubbord);
    chkAutoPlaySubbord.setFocusable(false);

    txtAutoPlayMain = new JTextField();
    txtAutoPlayMain.setBounds(180, 12, 66, 21);
    contentPane.add(txtAutoPlayMain);
    txtAutoPlayMain.setColumns(10);

    txtAutoPlaySub = new JTextField();
    txtAutoPlaySub.setBounds(180, 38, 66, 21);
    contentPane.add(txtAutoPlaySub);
    txtAutoPlaySub.setColumns(10);

    if (Lizzie.frame.toolbar.chkAutoMain.isSelected()) {
      chkAutoPlayMainbord.setSelected(true);
    }
    txtAutoPlayMain.setText(Lizzie.frame.toolbar.txtAutoMain.getText());
    if (Lizzie.frame.toolbar.chkAutoSub.isSelected()) {
      chkAutoPlaySubbord.setSelected(true);
    }
    txtAutoPlaySub.setText(Lizzie.frame.toolbar.txtAutoSub.getText());

    JButton btnNewButton = new JButton("确定");
    btnNewButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkAutoPlayMainbord.isSelected() || chkAutoPlaySubbord.isSelected()) {
              if (chkAutoPlayMainbord.isSelected()) {
                Lizzie.frame.toolbar.txtAutoMain.setText(txtAutoPlayMain.getText());
                if (!Lizzie.frame.toolbar.chkAutoMain.isSelected()) {
                  Lizzie.frame.toolbar.chkAutoMain.setSelected(true);
                  Lizzie.frame.toolbar.autoPlayMain();
                }
              } else {
                Lizzie.frame.toolbar.chkAutoMain.setSelected(false);
              }
              if (chkAutoPlaySubbord.isSelected()) {
                Lizzie.frame.toolbar.txtAutoSub.setText(txtAutoPlaySub.getText());
                if (!Lizzie.frame.toolbar.chkAutoSub.isSelected()) {
                  Lizzie.frame.toolbar.chkAutoSub.setSelected(true);
                  Lizzie.frame.toolbar.autoPlaySub();
                }
              } else {
                Lizzie.frame.toolbar.chkAutoSub.setSelected(false);
                Lizzie.frame.toolbar.autoPlaySub();
              }

            } else {
              Lizzie.frame.toolbar.chkAutoMain.setSelected(false);
              Lizzie.frame.toolbar.chkAutoSub.setSelected(false);
              Lizzie.frame.toolbar.autoPlaySub();
            }
            setVisible(false);
          }
        });
    btnNewButton.setBounds(83, 62, 93, 23);
    contentPane.add(btnNewButton);
  }
}
