/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class NewGameDialog extends JDialog {
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
  private JTextField textFieldBlack;
  private JTextField textFieldWhite;
  private JTextField textFieldKomi;
  private JTextField textFieldHandicap;
  private JTextField textTime;
  private JCheckBox chkPonder;

  private boolean cancelled = true;
  private GameInfo gameInfo;

  public NewGameDialog() {
    initComponents();
  }

  private static final ResourceBundle resourceBundle =
      ResourceBundle.getBundle("l10n.DisplayStrings");

  private void initComponents() {
    setMinimumSize(new Dimension(100, 150));
    setResizable(false);
    setTitle("新的一局(Genmove模式)");
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
    GridLayout gridLayout = new GridLayout(7, 2, 4, 4);
    contentPanel.setLayout(gridLayout);

    checkBoxPlayerIsBlack =
        new JCheckBox(resourceBundle.getString("NewGameDialog.PlayBlack"), true);
    checkBoxPlayerIsBlack.addChangeListener(evt -> togglePlayerIsBlack());
    textFieldWhite = new JTextField();
    textFieldBlack = new JTextField();
    textFieldKomi = new JFormattedTextField(FORMAT_KOMI);
    textFieldKomi.setText("7.5");
    textFieldHandicap = new JFormattedTextField(FORMAT_HANDICAP);
    textFieldKomi.setText("0");
    textFieldHandicap.addPropertyChangeListener(evt -> modifyHandicap());
    textTime = new JTextField();
    textTime.setText(
        Lizzie.config.config.getJSONObject("leelaz").getInt("max-game-thinking-time-seconds") + "");
    chkPonder = new JCheckBox();
    chkPonder.setSelected(Lizzie.config.playponder);
    contentPanel.add(checkBoxPlayerIsBlack);
    contentPanel.add(PLACEHOLDER);
    contentPanel.add(new JLabel(resourceBundle.getString("NewGameDialog.Black")));
    contentPanel.add(textFieldBlack);
    contentPanel.add(new JLabel(resourceBundle.getString("NewGameDialog.White")));
    contentPanel.add(textFieldWhite);
    contentPanel.add(new JLabel(resourceBundle.getString("NewGameDialog.Komi")));
    contentPanel.add(textFieldKomi);
    contentPanel.add(new JLabel(resourceBundle.getString("NewGameDialog.Handicap")));
    contentPanel.add(textFieldHandicap);
    contentPanel.add(new JLabel("AI每手用时(秒)"));
    contentPanel.add(textTime);
    contentPanel.add(new JLabel("AI是否后台思考"));
    contentPanel.add(chkPonder);
    togglePlayerIsBlack();

    textFieldKomi.setEnabled(true);

    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void togglePlayerIsBlack() {
    JTextField humanTextField = playerIsBlack() ? textFieldBlack : textFieldWhite;
    JTextField computerTextField = playerIsBlack() ? textFieldWhite : textFieldBlack;

    humanTextField.setEnabled(true);
    humanTextField.setText(GameInfo.DEFAULT_NAME_HUMAN_PLAYER);
    computerTextField.setEnabled(false);
    computerTextField.setText(Lizzie.leelaz.currentEnginename);
  }

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
      String playerBlack = textFieldBlack.getText();
      String playerWhite = textFieldWhite.getText();
      double komi = FORMAT_KOMI.parse(textFieldKomi.getText()).doubleValue();
      int handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getText()).intValue();

      // apply new values
      gameInfo.setPlayerBlack(playerBlack);
      gameInfo.setPlayerWhite(playerWhite);
      gameInfo.setKomi(komi);
      gameInfo.setHandicap(handicap);
      Lizzie.config.playponder = chkPonder.isSelected();
      try {
        Lizzie.config.leelazConfig.putOpt(
            "max-game-thinking-time-seconds", FORMAT_HANDICAP.parse(textTime.getText()).intValue());
        Lizzie.config.save();
      } catch (IOException e) {
        e.printStackTrace();
      }
      // close window
      cancelled = false;
      setVisible(false);
    } catch (ParseException e) {
      // hide input mistakes.
    }
  }

  public void setGameInfo(GameInfo gameInfo) {
    this.gameInfo = gameInfo;

    textFieldBlack.setText(gameInfo.getPlayerBlack());
    textFieldWhite.setText(gameInfo.getPlayerWhite());
    textFieldHandicap.setText(FORMAT_HANDICAP.format(gameInfo.getHandicap()));
    textFieldKomi.setText(FORMAT_KOMI.format(gameInfo.getKomi()));

    // update player names

  }

  public boolean playerIsBlack() {
    return checkBoxPlayerIsBlack.isSelected();
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          try {
            NewGameDialog window = new NewGameDialog();
            window.setVisible(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }
}
