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
  JTextField txtGameMAX;
  JTextField txtGameMIN;

  // JCheckBox chkGenmove;
  JRadioButton rdoGenmove;
  JRadioButton rdoAna;
  JCheckBox chkAutosave;
  JCheckBox chkExchange;
  JCheckBox chkGameMAX;
  JCheckBox chkGameMIN;
  JCheckBox chkRandomMove;
  JCheckBox chkSaveWinrate;

  JTextField txtRandomMove;
  JTextField txtRandomDiffWinrate;

  public EnginePkConfig() {
    setType(Type.POPUP);
    setModal(true);
    setTitle("引擎对战设置");
    setBounds(0, 0, 550, 300);
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

    chkExchange = new JCheckBox("交换黑白");
    add(chkExchange);
    chkExchange.setBounds(210, 6, 75, 18);

    chkRandomMove = new JCheckBox("随机落子:前");
    add(chkRandomMove);
    chkRandomMove.setBounds(283, 6, 88, 18);

    txtRandomMove = new JTextField();
    add(txtRandomMove);
    txtRandomMove.setBounds(372, 7, 30, 18);

    JLabel lblRandomWinrate = new JLabel("手,胜率不低于首位");
    add(lblRandomWinrate);
    lblRandomWinrate.setBounds(402, 6, 105, 18);

    txtRandomDiffWinrate = new JTextField();
    add(txtRandomDiffWinrate);
    txtRandomDiffWinrate.setBounds(500, 7, 25, 18);

    JLabel lblRandomWinrate2 = new JLabel("%");
    add(lblRandomWinrate2);
    lblRandomWinrate2.setBounds(525, 6, 15, 18);

    JLabel lblnameSetting = new JLabel("多盘对战棋谱保存文件夹名(一次有效):");
    txtnameSetting = new JTextField();
    add(lblnameSetting);
    add(txtnameSetting);
    lblnameSetting.setBounds(5, 22, 210, 25);
    txtnameSetting.setBounds(210, 27, 100, 18);

    rdoGenmove = new JRadioButton("genmove模式对战");
    rdoAna = new JRadioButton("分析模式对战");

    rdoAna.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            txtresignSetting.setEnabled(true);
            txtresignSetting2.setEnabled(true);
            txtGameMIN.setEnabled(true);
            chkGameMIN.setEnabled(true);
            chkRandomMove.setEnabled(true);
            txtRandomMove.setEnabled(true);
            txtRandomDiffWinrate.setEnabled(true);
            chkRandomMove.setEnabled(true);
            txtRandomDiffWinrate.setEnabled(true);
            txtRandomMove.setEnabled(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
          }
        });

    rdoGenmove.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            txtresignSetting.setEnabled(false);
            txtresignSetting2.setEnabled(false);
            txtGameMIN.setEnabled(false);
            chkGameMIN.setEnabled(false);
            chkRandomMove.setEnabled(false);
            txtRandomMove.setEnabled(false);
            txtRandomDiffWinrate.setEnabled(false);
            chkRandomMove.setEnabled(false);
            txtRandomDiffWinrate.setEnabled(false);
            txtRandomMove.setEnabled(false);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
          }
        });
    ButtonGroup wrgroup = new ButtonGroup();
    wrgroup.add(rdoGenmove);
    wrgroup.add(rdoAna);

    add(rdoGenmove);
    add(rdoAna);

    rdoGenmove.setBounds(2, 45, 140, 20);
    rdoAna.setBounds(142, 45, 140, 20);

    chkGameMAX = new JCheckBox();
    JLabel lblGameMAX = new JLabel("最大手数");
    txtGameMAX = new JTextField();
    add(chkGameMAX);
    add(lblGameMAX);
    add(txtGameMAX);

    chkGameMAX.setBounds(2, 65, 20, 20);
    lblGameMAX.setBounds(22, 65, 50, 18);
    txtGameMAX.setBounds(72, 66, 40, 18);

    chkGameMIN = new JCheckBox();
    JLabel lblGameMIN = new JLabel("最小手数");
    txtGameMIN = new JTextField();
    add(chkGameMIN);
    add(lblGameMIN);
    add(txtGameMIN);

    chkGameMIN.setBounds(112, 65, 20, 20);
    lblGameMIN.setBounds(132, 65, 50, 18);
    txtGameMIN.setBounds(182, 66, 40, 18);

    chkAutosave = new JCheckBox("自动保存棋谱");
    // JLabel lblAutosave = new JLabel("自动保存棋谱");
    add(chkAutosave);
    // add(lblAutosave);
    chkAutosave.setBounds(222, 64, 100, 20);
    // lblAutosave.setBounds(242, 65, 100, 18);

    chkSaveWinrate = new JCheckBox("保存胜率截图");
    add(chkSaveWinrate);
    chkSaveWinrate.setBounds(320, 64, 100, 20);

    JLabel lblHints = new JLabel("注:  设置最大手数后,超过手数的对局将被中止并保存,不记入比分");
    JLabel lblHints2 = new JLabel("       如出现双方pass则不计入比分,但会记录棋谱");
    JLabel lblHints3 = new JLabel("       genmove模式下,引擎强烈建议添加 --noponder参数,否则可能争用资源并且显示混乱");
    JLabel lblHints4 = new JLabel("       genmove模式下,使用 genmove命令,只能按时间落子,认输阈值、计算量、随机落子");
    JLabel lblHints5 = new JLabel("       只受引擎参数限制(-r,-p,-v,-m),界面上的设置无效,且固定92手前不会认输");
    JLabel lblHints6 = new JLabel("       建议使用分析模式对战,熟悉引擎参数的用户可以考虑使用genmove模式");
    lblHints3.setForeground(Color.RED);
    add(lblHints);
    add(lblHints2);
    add(lblHints3);
    add(lblHints4);
    add(lblHints5);
    add(lblHints6);
    lblHints.setBounds(5, 90, 470, 20);
    lblHints2.setBounds(5, 110, 470, 20);
    lblHints3.setBounds(5, 130, 470, 20);
    lblHints4.setBounds(5, 150, 470, 20);
    lblHints5.setBounds(5, 170, 470, 20);
    lblHints6.setBounds(5, 190, 470, 20);

    JButton okButton = new JButton("确认");
    JButton cancelButton = new JButton("取消");
    add(okButton);
    add(cancelButton);
    okButton.setMargin(new Insets(0, 0, 0, 0));
    cancelButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.setBounds(200, 225, 50, 30);
    cancelButton.setBounds(270, 225, 50, 30);

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
      txtresignSetting.setEnabled(false);
      txtresignSetting2.setEnabled(false);
      txtGameMIN.setEnabled(false);
      chkGameMIN.setEnabled(false);
      chkRandomMove.setEnabled(false);
      txtRandomMove.setEnabled(false);
      txtRandomDiffWinrate.setEnabled(false);
      chkRandomMove.setEnabled(false);
      txtRandomDiffWinrate.setEnabled(false);
      txtRandomMove.setEnabled(false);
    } else {
      rdoAna.setSelected(true);
      txtresignSetting.setEnabled(true);
      txtresignSetting2.setEnabled(true);
      txtGameMIN.setEnabled(true);
      chkGameMIN.setEnabled(true);
      chkRandomMove.setEnabled(true);
      txtRandomMove.setEnabled(true);
      txtRandomDiffWinrate.setEnabled(true);
      chkRandomMove.setEnabled(true);
      txtRandomDiffWinrate.setEnabled(true);
      txtRandomMove.setEnabled(true);
    }
    if (Lizzie.frame.toolbar.exChange) {
      chkExchange.setSelected(true);
    }
    if (Lizzie.frame.toolbar.checkGameMaxMove) {
      chkGameMAX.setSelected(true);
    }
    txtGameMAX.setText(Lizzie.frame.toolbar.maxGanmeMove + "");

    if (Lizzie.frame.toolbar.checkGameMinMove) {
      chkGameMIN.setSelected(true);
    }
    txtGameMIN.setText(Lizzie.frame.toolbar.minGanmeMove + "");

    if (Lizzie.frame.toolbar.isRandomMove) {
      chkRandomMove.setSelected(true);
    }
    if (Lizzie.frame.toolbar.randomMove > 0)
      txtRandomMove.setText(Lizzie.frame.toolbar.randomMove + "");
    txtRandomDiffWinrate.setText(Lizzie.frame.toolbar.randomDiffWinrate + "");
    if (Lizzie.frame.toolbar.enginePkSaveWinrate) chkSaveWinrate.setSelected(true);
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
    // double a = Double.parseDouble(txtresignSetting2.getText());
    Lizzie.frame.toolbar.AutosavePk = chkAutosave.isSelected();
    Lizzie.frame.toolbar.isGenmove = rdoGenmove.isSelected();
    Lizzie.frame.toolbar.batchPkName = txtnameSetting.getText();
    Lizzie.frame.toolbar.exChange = chkExchange.isSelected();
    Lizzie.frame.toolbar.isRandomMove = chkRandomMove.isSelected();
    Lizzie.frame.toolbar.enginePkSaveWinrate = chkSaveWinrate.isSelected();
    try {
      Lizzie.frame.toolbar.randomMove = Integer.parseInt(txtRandomMove.getText());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.frame.toolbar.randomDiffWinrate = Double.parseDouble(txtRandomDiffWinrate.getText());
    } catch (NumberFormatException err) {
    }

    Lizzie.frame.toolbar.setGenmove();

    Lizzie.frame.toolbar.checkGameMaxMove = chkGameMAX.isSelected();
    try {
      Lizzie.frame.toolbar.maxGanmeMove = Integer.parseInt(txtGameMAX.getText());
    } catch (NumberFormatException err) {
    }
    Lizzie.frame.toolbar.checkGameMinMove = chkGameMIN.isSelected();
    try {
      Lizzie.frame.toolbar.minGanmeMove = Integer.parseInt(txtGameMIN.getText());
    } catch (NumberFormatException err) {
    }
    setVisible(false);
  }
}
