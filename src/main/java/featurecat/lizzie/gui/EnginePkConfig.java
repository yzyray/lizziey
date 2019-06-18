package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class EnginePkConfig extends JDialog {
  JTextField txtresignSetting;
  JTextField txtresignSetting2;
  JTextField txtnameSetting;
  JTextField txtGameTime;

  // JCheckBox chkGenmove;
  JRadioButton rdoGenmove;
  JRadioButton rdoAna;
  JCheckBox chkAutosave;
  JCheckBox chkExchange;
  JCheckBox chkGameTime;

  public EnginePkConfig() {
    setType(Type.POPUP);
    setTitle("引擎对战设置");
    setBounds(0, 0, 490, 300);
    setResizable(false);
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

    JLabel lblExchange = new JLabel("交换黑白");
    chkExchange = new JCheckBox();
    add(lblExchange);
    add(chkExchange);
    chkExchange.setBounds(220, 6, 20, 18);
    lblExchange.setBounds(240, 2, 50, 25);

    JLabel lblnameSetting = new JLabel("多盘对战棋谱保存文件夹名(一次有效):");
    txtnameSetting = new JTextField();
    add(lblnameSetting);
    add(txtnameSetting);
    lblnameSetting.setBounds(5, 22, 210, 25);
    txtnameSetting.setBounds(210, 27, 100, 18);

    rdoGenmove = new JRadioButton("genmove模式对战");
    rdoAna = new JRadioButton("分析模式对战");

    rdoGenmove.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            txtresignSetting.setEnabled(false);
            txtresignSetting2.setEnabled(false);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            txtresignSetting.setEnabled(true);
            txtresignSetting2.setEnabled(true);
          }
        });
    ButtonGroup wrgroup = new ButtonGroup();
    wrgroup.add(rdoGenmove);
    wrgroup.add(rdoAna);

    add(rdoGenmove);
    add(rdoAna);

    rdoGenmove.setBounds(2, 45, 140, 20);
    rdoAna.setBounds(142, 45, 140, 20);

    chkAutosave = new JCheckBox();
    JLabel lblAutosave = new JLabel("自动保存棋谱");
    add(chkAutosave);
    add(lblAutosave);

    chkGameTime = new JCheckBox();
    JLabel lblGameTime = new JLabel("单局超时(分)");
    txtGameTime = new JTextField();
    add(chkGameTime);
    add(lblGameTime);
    add(txtGameTime);
    chkGameTime.setBounds(2, 65, 20, 20);
    lblGameTime.setBounds(22, 65, 70, 18);
    txtGameTime.setBounds(92, 66, 40, 18);

    chkAutosave.setBounds(152, 65, 20, 20);
    lblAutosave.setBounds(172, 65, 100, 18);
    JLabel lblHints = new JLabel("注:  设置单局超时后,超时的对局将被中止并保存,不记入比分");
    JLabel lblHints2 = new JLabel("       如出现双方pass则不计入比分,但会记录棋谱");
    JLabel lblHints3 = new JLabel("       genmove模式下,引擎强烈建议添加 --noponder参数,否则可能争用资源并且显示混乱");
    JLabel lblHints4 = new JLabel("       在genmove模式下,使用 genmove命令落子,只能按时间落子,认输阈值和计算量");
    JLabel lblHints5 = new JLabel("       只受引擎参数限制(-r,-p,-v),界面上的设置无效,且92手前不会认输");
    JLabel lblHints6 = new JLabel("       建议使用分析模式对战,熟悉引擎参数的用户并希望低V测试可以考虑genmove模式");
    lblHints3.setForeground(Color.RED);
    add(lblHints);
    add(lblHints2);
    add(lblHints3);
    add(lblHints4);
    add(lblHints5);
    add(lblHints6);
    lblHints.setBounds(5, 90, 450, 20);
    lblHints2.setBounds(5, 110, 450, 20);
    lblHints3.setBounds(5, 130, 470, 20);
    lblHints4.setBounds(5, 150, 450, 20);
    lblHints5.setBounds(5, 170, 450, 20);
    lblHints6.setBounds(5, 190, 470, 20);

    JButton okButton = new JButton("确认");
    JButton cancelButton = new JButton("取消");
    add(okButton);
    add(cancelButton);
    okButton.setMargin(new Insets(0, 0, 0, 0));
    cancelButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.setBounds(100, 220, 50, 30);
    cancelButton.setBounds(170, 220, 50, 30);

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
      rdoGenmove.setSelected(true);
    } else {
      rdoAna.setSelected(true);
    }
    if (Lizzie.frame.toolbar.exChange) {
      chkExchange.setSelected(true);
    }
    if (Lizzie.frame.toolbar.checkGameTime) {
      chkGameTime.setSelected(true);
    }
    txtGameTime.setText(Lizzie.frame.toolbar.maxGanmeTime + "");
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
    double a = Double.parseDouble(txtresignSetting2.getText());
    Lizzie.frame.toolbar.AutosavePk = chkAutosave.isSelected();
    Lizzie.frame.toolbar.isGenmove = rdoGenmove.isSelected();
    Lizzie.frame.toolbar.batchPkName = txtnameSetting.getText();
    Lizzie.frame.toolbar.exChange = chkExchange.isSelected();

    Lizzie.frame.toolbar.setGenmove();

    Lizzie.frame.toolbar.checkGameTime = chkGameTime.isSelected();
    try {
      Lizzie.frame.toolbar.maxGanmeTime = Integer.parseInt(txtGameTime.getText());
    } catch (NumberFormatException err) {
    }
    setVisible(false);
  }
}
