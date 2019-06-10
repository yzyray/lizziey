package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class EnginePkConfig extends JDialog {
  JTextField txtresignSetting;
  JTextField txtresignSetting2;
  JTextField txtnameSetting;
  JCheckBox chkGenmove;
  JCheckBox chkAutosave;

  public EnginePkConfig() {
    setType(Type.POPUP);
    setTitle("引擎对战设置");
    setBounds(0, 0, 340, 200);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setLayout(null);
    setLocationRelativeTo(getOwner());
    JLabel lblresignSetting = new JLabel("认输阈值:连续");
    JLabel lblresignSetting2 = new JLabel("手胜率低于");
    JLabel lblresignSetting3 = new JLabel("%");
    txtresignSetting = new JTextField();
    txtresignSetting2 = new JTextField();
    add(lblresignSetting);
    add(lblresignSetting2);
    add(txtresignSetting);
    add(txtresignSetting2);
    add(lblresignSetting3);
    lblresignSetting.setBounds(5, 2, 85, 25);
    txtresignSetting.setBounds(80, 7, 20, 18);
    lblresignSetting2.setBounds(100, 2, 60, 25);
    txtresignSetting2.setBounds(160, 7, 35, 18);
    lblresignSetting3.setBounds(195, 2, 15, 25);
    JLabel lblnameSetting = new JLabel("多盘对战棋谱保存文件夹名(一次有效):");
    txtnameSetting = new JTextField();
    add(lblnameSetting);
    add(txtnameSetting);
    lblnameSetting.setBounds(5, 22, 210, 25);
    txtnameSetting.setBounds(210, 27, 100, 18);
    chkGenmove = new JCheckBox();
    JLabel lblGenmove = new JLabel("使用genmove命令落子");
    add(chkGenmove);
    add(lblGenmove);
    chkGenmove.setBounds(2, 45, 20, 20);
    lblGenmove.setBounds(22, 45, 130, 18);
    chkAutosave = new JCheckBox();
    JLabel lblAutosave = new JLabel("自动保存棋谱");
    add(chkAutosave);
    add(lblAutosave);
    chkAutosave.setBounds(152, 45, 20, 20);
    lblAutosave.setBounds(172, 45, 100, 18);
    JLabel lblHints = new JLabel("注:genmove命令下只能按时间落子,计算量和认输阈值只受");
    JLabel lblHints2 = new JLabel("引擎参数限制(-r,-p,-v),界面上的设置无效");
    add(lblHints);
    add(lblHints2);
    lblHints.setBounds(5, 70, 350, 20);
    lblHints2.setBounds(5, 90, 300, 20);

    JButton okButton = new JButton("确认");
    JButton cancelButton = new JButton("取消");
    add(okButton);
    add(cancelButton);
    okButton.setMargin(new Insets(0, 0, 0, 0));
    cancelButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.setBounds(100, 120, 50, 30);
    cancelButton.setBounds(170, 120, 50, 30);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyChange();
          }
        });

    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    txtresignSetting.setText(Lizzie.frame.toolbar.pkResignMoveCounts + "");
    txtresignSetting2.setText(String.valueOf(Lizzie.frame.toolbar.pkResginWinrate));
    txtnameSetting.setText(Lizzie.frame.toolbar.batchPkName);
    if (Lizzie.frame.toolbar.AutosavePk) {
      chkAutosave.setSelected(true);
    }
    if (Lizzie.frame.toolbar.isGenmove) {
      chkGenmove.setSelected(true);
    }
  }

  private void applyChange() {
    try {
      Lizzie.frame.toolbar.pkResignMoveCounts = Integer.parseInt(txtresignSetting.getText());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.frame.toolbar.pkResginWinrate = Double.parseDouble(txtresignSetting2.getText());
    } catch (NumberFormatException err) {
    }
   double a= Double.parseDouble(txtresignSetting2.getText());
    Lizzie.frame.toolbar.AutosavePk = chkAutosave.isSelected();
    Lizzie.frame.toolbar.isGenmove = chkGenmove.isSelected();
    Lizzie.frame.toolbar.batchPkName = txtnameSetting.getText();
    setVisible(false);
  }
}
