/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.rules.Stone;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class NewEngineGameDialog extends JDialog {
  // create formatters
  public static final DecimalFormat FORMAT_KOMI = new DecimalFormat("#0.0");
  public static final DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");
  // public static final JLabel PLACEHOLDER = new JLabel("");

  static {
    FORMAT_HANDICAP.setMaximumIntegerDigits(1);
  }

  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JButton okButton = new JButton();

  // private JCheckBox checkBoxPlayerIsBlack;
  private JTextField textFieldKomi;
  private JTextField textFieldHandicap;

  public JComboBox enginePkBlack;
  public JComboBox enginePkWhite;

  private boolean cancelled = true;
  private GameInfo gameInfo;

  public NewEngineGameDialog() {
    initComponents();
  }

  private static final ResourceBundle resourceBundle =
      ResourceBundle.getBundle("l10n.DisplayStrings");

  private void initComponents() {
    setMinimumSize(new Dimension(310, 280));
    setResizable(false);
    setTitle("引擎对战");
    setModal(true);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    pack();
    setLocationRelativeTo(getOwner());
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    contentPanel.setLayout(null);

    //    checkBoxPlayerIsBlack =
    //        new JCheckBox(resourceBundle.getString("NewGameDialog.PlayBlack"), true);
    //    checkBoxPlayerIsBlack.addChangeListener(evt -> togglePlayerIsBlack());

    textFieldKomi = new JFormattedTextField(FORMAT_KOMI);
    textFieldHandicap = new JFormattedTextField(FORMAT_HANDICAP);
    textFieldHandicap.addPropertyChangeListener(evt -> modifyHandicap());

    enginePkBlack = Lizzie.frame.toolbar.enginePkBlack;
    enginePkWhite = Lizzie.frame.toolbar.enginePkWhite;
    JButton btnConfig = new JButton("更多设置");
    btnConfig.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            EnginePkConfig engineconfig = new EnginePkConfig();
            engineconfig.setVisible(true);
            engineconfig.setAlwaysOnTop(true);
          }
        });

    JLabel lblB = new JLabel("黑方设置");
    JLabel lblW = new JLabel("白方设置");
    lblB.setBounds(125, 2, 50, 20);
    lblW.setBounds(225, 2, 50, 20);

    JLabel lblengine = new JLabel("选择引擎");
    lblengine.setBounds(5, 30, 50, 20);
    enginePkBlack.setBounds(105, 30, 90, 20);
    enginePkWhite.setBounds(205, 30, 90, 20);
    contentPanel.add(lblengine);
    contentPanel.add(lblB);
    contentPanel.add(lblW);
    contentPanel.add(enginePkBlack);
    contentPanel.add(enginePkWhite);

    JLabel lblTime = new JLabel("每手时间(秒)");
    lblTime.setBounds(5, 60, 80, 20);
    Lizzie.frame.toolbar.chkenginePkTime.setBounds(75, 60, 20, 20);
    Lizzie.frame.toolbar.txtenginePkTime.setBounds(105, 60, 90, 20);
    Lizzie.frame.toolbar.txtenginePkTimeWhite.setBounds(205, 60, 90, 20);
    contentPanel.add(lblTime);
    contentPanel.add(Lizzie.frame.toolbar.chkenginePkTime);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkTime);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkTimeWhite);

    JLabel lblPlayout = new JLabel("总计算量");

    lblPlayout.setBounds(5, 90, 80, 20);
    Lizzie.frame.toolbar.chkenginePkPlayouts.setBounds(75, 90, 20, 20);
    Lizzie.frame.toolbar.txtenginePkPlayputs.setBounds(105, 90, 90, 20);
    Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setBounds(205, 90, 90, 20);
    contentPanel.add(lblPlayout);
    contentPanel.add(Lizzie.frame.toolbar.chkenginePkPlayouts);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkPlayputs);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkPlayputsWhite);

    JLabel lblFirstPlayout = new JLabel("首位计算量");
    lblFirstPlayout.setBounds(5, 120, 80, 20);
    Lizzie.frame.toolbar.chkenginePkFirstPlayputs.setBounds(75, 120, 20, 20);
    Lizzie.frame.toolbar.txtenginePkFirstPlayputs.setBounds(105, 120, 90, 20);
    Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setBounds(205, 120, 90, 20);

    contentPanel.add(lblFirstPlayout);
    contentPanel.add(Lizzie.frame.toolbar.chkenginePkFirstPlayputs);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkFirstPlayputs);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite);

    JLabel komi = new JLabel(resourceBundle.getString("NewGameDialog.Komi"));
    JLabel handicap = new JLabel("让子(仅支持19路棋盘)");
    komi.setBounds(5, 150, 30, 20);
    textFieldKomi.setBounds(35, 150, 30, 20);
    handicap.setBounds(80, 150, 120, 20);
    textFieldHandicap.setBounds(210, 150, 30, 20);
    contentPanel.add(komi);
    contentPanel.add(textFieldKomi);
    contentPanel.add(handicap);
    contentPanel.add(textFieldHandicap);

    JLabel conti = new JLabel("当前局面续弈");

    conti.setBounds(5, 180, 100, 20);
    Lizzie.frame.toolbar.chkenginePkContinue.setBounds(75, 180, 20, 20);
    contentPanel.add(conti);
    contentPanel.add(Lizzie.frame.toolbar.chkenginePkContinue);

    JLabel batch = new JLabel("多盘");
    batch.setBounds(105, 180, 30, 20);
    Lizzie.frame.toolbar.chkenginePkBatch.setBounds(130, 180, 20, 20);
    Lizzie.frame.toolbar.txtenginePkBatch.setBounds(150, 180, 40, 20);
    btnConfig.setBounds(205, 180, 80, 20);
    contentPanel.add(batch);
    contentPanel.add(Lizzie.frame.toolbar.chkenginePkBatch);
    contentPanel.add(Lizzie.frame.toolbar.txtenginePkBatch);
    contentPanel.add(btnConfig);

    textFieldKomi.setEnabled(true);

    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  //  private void togglePlayerIsBlack() {
  //    JTextField humanTextField = playerIsBlack() ? textFieldBlack : textFieldWhite;
  //    JTextField computerTextField = playerIsBlack() ? textFieldWhite : textFieldBlack;
  //
  //    humanTextField.setEnabled(true);
  //    humanTextField.setText(GameInfo.DEFAULT_NAME_HUMAN_PLAYER);
  //    computerTextField.setEnabled(false);
  //    computerTextField.setText(GameInfo.DEFAULT_NAME_CPU_PLAYER);
  //  }

  private void modifyHandicap() {
    try {
      int handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getText()).intValue();
      if (handicap < 0) throw new IllegalArgumentException();

      textFieldKomi.setText(FORMAT_KOMI.format(GameInfo.DEFAULT_KOMI));
    } catch (ParseException | RuntimeException e) {
      // do not correct user mistakes
    }
  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

    // ---- okButton ----
    okButton.setText("确定");
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
      // validate data
      String playerBlack =
          Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).currentEnginename;
      String playerWhite =
          Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).currentEnginename;
      ;
      double komi = FORMAT_KOMI.parse(textFieldKomi.getText()).doubleValue();
      int handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getText()).intValue();

      // apply new values
      gameInfo.setPlayerBlack(playerBlack);
      gameInfo.setPlayerWhite(playerWhite);
      gameInfo.setKomi(komi);
      gameInfo.setHandicap(handicap);

      // close window
      cancelled = false;
      setVisible(false);
      if (handicap >= 2 && Lizzie.board.boardWidth == 19 && Lizzie.board.boardHeight == 19) {
        Lizzie.board.clear();
        placeHandicap(handicap);
      }

    } catch (ParseException e) {
      // hide input mistakes.
    }
  }

  private void placeHandicap(int handicap) {
    // TODO Auto-generated method stub
    Lizzie.frame.toolbar.chkenginePkContinue.setSelected(true);
    switch (handicap) {
      case 2:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        break;
      case 3:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        break;
      case 4:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        break;
      case 5:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 9, Stone.BLACK);
        break;
      case 6:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 3, Stone.BLACK);
        Lizzie.board.place(9, 15, Stone.BLACK);
        break;
      case 7:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 3, Stone.BLACK);
        Lizzie.board.place(9, 15, Stone.BLACK);
        Lizzie.board.place(9, 9, Stone.BLACK);
        break;
      case 8:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 3, Stone.BLACK);
        Lizzie.board.place(9, 15, Stone.BLACK);
        Lizzie.board.place(3, 9, Stone.BLACK);
        Lizzie.board.place(15, 9, Stone.BLACK);
        break;
      case 9:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 3, Stone.BLACK);
        Lizzie.board.place(9, 15, Stone.BLACK);
        Lizzie.board.place(3, 9, Stone.BLACK);
        Lizzie.board.place(15, 9, Stone.BLACK);
        Lizzie.board.place(9, 9, Stone.BLACK);
        break;
    }
  }

  public void setGameInfo(GameInfo gameInfo) {
    this.gameInfo = gameInfo;

    textFieldHandicap.setText(FORMAT_HANDICAP.format(gameInfo.getHandicap()));
    textFieldKomi.setText(FORMAT_KOMI.format(gameInfo.getKomi()));

    // update player names
    // togglePlayerIsBlack();
  }

  //  public boolean playerIsBlack() {
  //    return checkBoxPlayerIsBlack.isSelected();
  //  }

  public boolean isCancelled() {
    return cancelled;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          try {
            NewEngineGameDialog window = new NewEngineGameDialog();
            window.setVisible(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }
}
