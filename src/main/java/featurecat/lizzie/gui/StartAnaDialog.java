/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class StartAnaDialog extends JDialog {
  // create formatters
  public static final DecimalFormat FORMAT_KOMI = new DecimalFormat("#0.0");
  public static final DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");
  public static final JLabel PLACEHOLDER = new JLabel("");

  static {
    FORMAT_HANDICAP.setMaximumIntegerDigits(1);
  }

  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JButton okButton = new JButton();

  private JCheckBox checkBoxPlayerIsBlack;
  //  private JTextField textFieldStartMove;
  //  private JTextField textFieldEndMove;
  //  private JTextField textFieldKomi;
  //  private JTextField textFieldHandicap;
  //  private JTextField textTime;
  //  private JTextField textPlayouts;
  //  private JTextField textFirstPlayouts;
  //  private JCheckBox chkPonder;
  //
  private boolean cancelled = true;
  //  private GameInfo gameInfo;

  public StartAnaDialog() {
    initComponents();
  }

  private static final ResourceBundle resourceBundle =
      ResourceBundle.getBundle("l10n.DisplayStrings");

  private void initComponents() {
    setMinimumSize(new Dimension(100, 150));
    setResizable(false);
    setTitle("自动分析设置");
    setModal(true);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    pack();
    setLocationRelativeTo(getOwner());
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    GridLayout gridLayout = new GridLayout(8, 2, 4, 4);
    contentPanel.setLayout(gridLayout);

    //  checkBoxPlayerIsBlack =
    //      new JCheckBox(resourceBundle.getString("NewGameDialog.PlayBlack"), true);
    //   checkBoxPlayerIsBlack.addChangeListener(evt -> togglePlayerIsBlack());

    // contentPanel.add(checkBoxPlayerIsBlack);
    // contentPanel.add(PLACEHOLDER);
    Lizzie.frame.toolbar.chkAnaBlack.setText("");
    Lizzie.frame.toolbar.chkAnaWhite.setText("");
    contentPanel.add(new JLabel("开始手数(选填)"));
    contentPanel.add(Lizzie.frame.toolbar.txtFirstAnaMove);
    contentPanel.add(new JLabel("结束手数(选填)"));
    contentPanel.add(Lizzie.frame.toolbar.txtLastAnaMove);
    contentPanel.add(new JLabel("每手时间(秒)"));
    contentPanel.add(Lizzie.frame.toolbar.txtAnaTime);
    contentPanel.add(new JLabel("每手总计算量(选填)"));
    contentPanel.add(Lizzie.frame.toolbar.txtAnaPlayouts);
    contentPanel.add(new JLabel("每手首位计算量(选填)"));
    contentPanel.add(Lizzie.frame.toolbar.txtAnaFirstPlayouts);
    contentPanel.add(new JLabel("分析黑棋"));
    contentPanel.add(Lizzie.frame.toolbar.chkAnaBlack);
    contentPanel.add(new JLabel("分析白棋"));
    contentPanel.add(Lizzie.frame.toolbar.chkAnaWhite);
    contentPanel.add(new JLabel("自动保存棋谱"));
    contentPanel.add(Lizzie.frame.toolbar.chkAnaAutoSave);

    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

    // ---- okButton ----
    okButton.setText("开始分析");
    okButton.addActionListener(e -> apply());

    int center = GridBagConstraints.CENTER;
    int both = GridBagConstraints.BOTH;
    buttonBar.add(
        okButton,
        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, center, both, new Insets(0, 0, 0, 0), 0, 0));

    dialogPane.add(buttonBar, BorderLayout.SOUTH);
  }

  public void apply() {
    try {
      Lizzie.frame.toolbar.firstMove =
          Integer.parseInt(Lizzie.frame.toolbar.txtFirstAnaMove.getText().replace(" ", ""));
    } catch (Exception ex) {
      Lizzie.frame.toolbar.firstMove = -1;
    }
    try {
      Lizzie.frame.toolbar.lastMove =
          Integer.parseInt(Lizzie.frame.toolbar.txtLastAnaMove.getText().replace(" ", ""));
    } catch (Exception ex) {
      Lizzie.frame.toolbar.lastMove = -1;
    }
    try {
      if (Integer.parseInt(Lizzie.frame.toolbar.txtAnaTime.getText().replace(" ", "")) > 0)
        Lizzie.frame.toolbar.chkAnaTime.setSelected(true);
      else Lizzie.frame.toolbar.chkAnaTime.setSelected(false);
    } catch (Exception ex) {
      Lizzie.frame.toolbar.chkAnaTime.setSelected(false);
    }
    try {
      if (Integer.parseInt(Lizzie.frame.toolbar.txtAnaPlayouts.getText().replace(" ", "")) > 0)
        Lizzie.frame.toolbar.chkAnaPlayouts.setSelected(true);
      else Lizzie.frame.toolbar.chkAnaPlayouts.setSelected(false);
    } catch (Exception ex) {
      Lizzie.frame.toolbar.chkAnaPlayouts.setSelected(false);
    }
    try {
      if (Integer.parseInt(Lizzie.frame.toolbar.txtAnaFirstPlayouts.getText().replace(" ", "")) > 0)
        Lizzie.frame.toolbar.chkAnaFirstPlayouts.setSelected(true);
      else Lizzie.frame.toolbar.chkAnaFirstPlayouts.setSelected(false);
    } catch (Exception ex) {
      Lizzie.frame.toolbar.chkAnaFirstPlayouts.setSelected(false);
    }
    Lizzie.leelaz.nameCmd();
    Timer timer = new Timer();
    timer.schedule(
        new TimerTask() {
          public void run() {
            Lizzie.frame.toolbar.startAutoAna();
            this.cancel();
          }
        },
        300);

    Lizzie.frame.removeInput();

    cancelled = false;
    Lizzie.frame.toolbar.resetAutoAna();
    this.setVisible(false);
  }

  public boolean isCancelled() {

    return cancelled;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          try {
            StartAnaDialog window = new StartAnaDialog();
            window.setVisible(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }
}
