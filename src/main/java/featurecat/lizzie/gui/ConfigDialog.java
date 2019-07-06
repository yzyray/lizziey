package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.InternationalFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigDialog extends JDialog {
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private String osName;
  private JTextField txtEngine;
  private JTextField txtEngine1;
  private JTextField txtEngine2;
  private JTextField txtEngine3;
  private JTextField txtEngine4;
  private JTextField txtEngine5;
  private JTextField txtEngine6;
  private JTextField txtEngine7;
  private JTextField txtEngine8;
  private JTextField txtEngine9;
  private JTextField nameEngine;
  private JTextField nameEngine1;
  private JTextField nameEngine2;
  private JTextField nameEngine3;
  private JTextField nameEngine4;
  private JTextField nameEngine5;
  private JTextField nameEngine6;
  private JTextField nameEngine7;
  private JTextField nameEngine8;
  private JTextField nameEngine9;
  private JTextField[] txts;
  private JTextField[] names;
  private JCheckBox chkPreload;
  private JCheckBox chkPreload1;
  private JCheckBox chkPreload2;
  private JCheckBox chkPreload3;
  private JCheckBox chkPreload4;
  private JCheckBox chkPreload5;
  private JCheckBox chkPreload6;
  private JCheckBox chkPreload7;
  private JCheckBox chkPreload8;
  private JCheckBox chkPreload9;
  private JCheckBox[] chkPreloads;
  private JRadioButton rdoBoardSize19;
  private JRadioButton rdoBoardSize13;
  private JRadioButton rdoBoardSize9;
  private JRadioButton rdoBoardSize7;
  private JRadioButton rdoBoardSize5;
  private JRadioButton rdoBoardSize4;
  private JRadioButton rdoWinrate;
  private JRadioButton rdoLcb;
  private JRadioButton rdoonlyWinrate;
  private JRadioButton rdoLcbfix;
  private JRadioButton rdoponder;
  private JRadioButton rdonoponder;
  private JRadioButton rdorect;
  private JRadioButton rdonorect;
  private JRadioButton rdofast;
  private JRadioButton rdonofast;
  private JRadioButton rdoloadzen;
  private JRadioButton rdonoloadzen;

  public String enginePath = "";
  public String weightPath = "";
  public String commandHelp = "";

  private Path curPath;
  private BufferedInputStream inputStream;
  private JFormattedTextField txtMaxAnalyzeTime;
  private JFormattedTextField txtMaxGameThinkingTime;
  private JFormattedTextField txtMaxsuggestionmoves;
  private JFormattedTextField txtlimitBranchLength;
  private JFormattedTextField txtAnalyzeUpdateInterval;
  private JCheckBox chkPrintEngineLog;
  private JSONObject leelazConfig;
  private JTextField txtBoardSize;
  private JRadioButton rdoBoardSizeOther;

  public ConfigDialog() {
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setTitle(resourceBundle.getString("LizzieConfig.title.config"));
    setModalityType(ModalityType.APPLICATION_MODAL);
    setType(Type.POPUP);
    setBounds(100, 100, 661, 787);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    JButton okButton = new JButton(resourceBundle.getString("LizzieConfig.button.ok"));
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            saveConfig();
            // applyChange();
          }
        });
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    JButton cancelButton = new JButton(resourceBundle.getString("LizzieConfig.button.cancel"));
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    getContentPane().add(tabbedPane, BorderLayout.CENTER);

    JPanel engineTab = new JPanel();
    tabbedPane.addTab(resourceBundle.getString("LizzieConfig.title.engine"), null, engineTab, null);
    engineTab.setLayout(null);

    JLabel lblPreload = new JLabel("预加载");
    lblPreload.setBounds(570, 14, 92, 16);
    lblPreload.setHorizontalAlignment(SwingConstants.LEFT);
    engineTab.add(lblPreload);

    JLabel lblEngine = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "1|别名");
    lblEngine.setBounds(6, 44, 92, 16);
    lblEngine.setHorizontalAlignment(SwingConstants.LEFT);
    engineTab.add(lblEngine);

    txtEngine = new JTextField();
    txtEngine.setBounds(87, 40, 420, 26);
    engineTab.add(txtEngine);
    txtEngine.setColumns(10);
    chkPreload = new JCheckBox();
    chkPreload.setBounds(570, 41, 23, 23);
    engineTab.add(chkPreload);

    nameEngine = new JTextField();
    nameEngine.setBounds(510, 40, 60, 26);
    engineTab.add(nameEngine);

    JLabel lblEngine1 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "2|别名");
    lblEngine1.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine1.setBounds(6, 80, 92, 16);
    engineTab.add(lblEngine1);

    txtEngine1 = new JTextField();
    txtEngine1.setColumns(10);
    txtEngine1.setBounds(87, 75, 420, 26);
    engineTab.add(txtEngine1);
    chkPreload1 = new JCheckBox();
    chkPreload1.setBounds(570, 76, 23, 23);
    engineTab.add(chkPreload1);

    nameEngine1 = new JTextField();
    nameEngine1.setBounds(510, 75, 60, 26);
    engineTab.add(nameEngine1);

    JLabel lblEngine2 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "3|别名");
    lblEngine2.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine2.setBounds(6, 110, 92, 16);
    engineTab.add(lblEngine2);

    txtEngine2 = new JTextField();
    txtEngine2.setColumns(10);
    txtEngine2.setBounds(87, 105, 420, 26);
    engineTab.add(txtEngine2);
    chkPreload2 = new JCheckBox();
    chkPreload2.setBounds(570, 106, 23, 23);
    engineTab.add(chkPreload2);

    nameEngine2 = new JTextField();
    nameEngine2.setBounds(510, 105, 60, 26);
    engineTab.add(nameEngine2);

    JLabel lblEngine3 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "4|别名");
    lblEngine3.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine3.setBounds(6, 140, 92, 16);
    engineTab.add(lblEngine3);

    txtEngine3 = new JTextField();
    txtEngine3.setColumns(10);
    txtEngine3.setBounds(87, 135, 420, 26);
    engineTab.add(txtEngine3);
    chkPreload3 = new JCheckBox();
    chkPreload3.setBounds(570, 136, 23, 23);
    engineTab.add(chkPreload3);

    nameEngine3 = new JTextField();
    nameEngine3.setBounds(510, 135, 60, 26);
    engineTab.add(nameEngine3);

    JLabel lblEngine4 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "5|别名");
    lblEngine4.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine4.setBounds(6, 170, 92, 16);
    engineTab.add(lblEngine4);

    txtEngine4 = new JTextField();
    txtEngine4.setColumns(10);
    txtEngine4.setBounds(87, 165, 420, 26);
    engineTab.add(txtEngine4);
    chkPreload4 = new JCheckBox();
    chkPreload4.setBounds(570, 166, 23, 23);
    engineTab.add(chkPreload4);

    nameEngine4 = new JTextField();
    nameEngine4.setBounds(510, 165, 60, 26);
    engineTab.add(nameEngine4);

    JLabel lblEngine5 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "6|别名");
    lblEngine5.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine5.setBounds(6, 200, 92, 16);
    engineTab.add(lblEngine5);

    txtEngine5 = new JTextField();
    txtEngine5.setColumns(10);
    txtEngine5.setBounds(87, 195, 420, 26);
    engineTab.add(txtEngine5);
    chkPreload5 = new JCheckBox();
    chkPreload5.setBounds(570, 196, 23, 23);
    engineTab.add(chkPreload5);

    nameEngine5 = new JTextField();
    nameEngine5.setBounds(510, 195, 60, 26);
    engineTab.add(nameEngine5);

    JLabel lblEngine6 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "7|别名");
    lblEngine6.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine6.setBounds(6, 230, 92, 16);
    engineTab.add(lblEngine6);

    txtEngine6 = new JTextField();
    txtEngine6.setColumns(10);
    txtEngine6.setBounds(87, 225, 420, 26);
    engineTab.add(txtEngine6);
    chkPreload6 = new JCheckBox();
    chkPreload6.setBounds(570, 226, 23, 23);
    engineTab.add(chkPreload6);

    nameEngine6 = new JTextField();
    nameEngine6.setBounds(510, 225, 60, 26);
    engineTab.add(nameEngine6);

    JLabel lblEngine7 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "8|别名");
    lblEngine7.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine7.setBounds(6, 260, 92, 16);
    engineTab.add(lblEngine7);

    txtEngine7 = new JTextField();
    txtEngine7.setColumns(10);
    txtEngine7.setBounds(87, 255, 420, 26);
    engineTab.add(txtEngine7);
    chkPreload7 = new JCheckBox();
    chkPreload7.setBounds(570, 256, 23, 23);
    engineTab.add(chkPreload7);

    nameEngine7 = new JTextField();
    nameEngine7.setBounds(510, 255, 60, 26);
    engineTab.add(nameEngine7);

    JLabel lblEngine8 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "9|别名");
    lblEngine8.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine8.setBounds(6, 290, 92, 16);
    engineTab.add(lblEngine8);

    txtEngine8 = new JTextField();
    txtEngine8.setColumns(10);
    txtEngine8.setBounds(87, 285, 420, 26);
    engineTab.add(txtEngine8);
    chkPreload8 = new JCheckBox();
    chkPreload8.setBounds(570, 286, 23, 23);
    engineTab.add(chkPreload8);

    nameEngine8 = new JTextField();
    nameEngine8.setBounds(510, 285, 60, 26);
    engineTab.add(nameEngine8);

    JLabel lblEngine9 = new JLabel(resourceBundle.getString("LizzieConfig.title.engine") + "10|别名");
    lblEngine9.setHorizontalAlignment(SwingConstants.LEFT);
    lblEngine9.setBounds(6, 320, 92, 16);
    engineTab.add(lblEngine9);

    txtEngine9 = new JTextField();
    txtEngine9.setColumns(10);
    txtEngine9.setBounds(87, 315, 420, 26);
    engineTab.add(txtEngine9);
    chkPreload9 = new JCheckBox();
    chkPreload9.setBounds(570, 316, 23, 23);
    engineTab.add(chkPreload9);

    nameEngine9 = new JTextField();
    nameEngine9.setBounds(510, 315, 60, 26);
    engineTab.add(nameEngine9);

    JButton button = new JButton("...");
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine.setText(el);
            }
            setVisible(true);
          }
        });
    button.setBounds(595, 40, 40, 26);
    engineTab.add(button);

    JButton button_1 = new JButton("...");
    button_1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine1.setText(el);
            }
            setVisible(true);
          }
        });
    button_1.setBounds(595, 75, 40, 26);
    engineTab.add(button_1);

    JButton button_2 = new JButton("...");
    button_2.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine2.setText(el);
            }
            setVisible(true);
          }
        });
    button_2.setBounds(595, 105, 40, 26);
    engineTab.add(button_2);

    JButton button_3 = new JButton("...");
    button_3.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine3.setText(el);
            }
            setVisible(true);
          }
        });
    button_3.setBounds(595, 135, 40, 26);
    engineTab.add(button_3);

    JButton button_4 = new JButton("...");
    button_4.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine4.setText(el);
            }
            setVisible(true);
          }
        });
    button_4.setBounds(595, 165, 40, 26);
    engineTab.add(button_4);

    JButton button_5 = new JButton("...");
    button_5.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine5.setText(el);
            }
            setVisible(true);
          }
        });
    button_5.setBounds(595, 195, 40, 26);
    engineTab.add(button_5);

    JButton button_6 = new JButton("...");
    button_6.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine6.setText(el);
            }
            setVisible(true);
          }
        });
    button_6.setBounds(595, 225, 40, 26);
    engineTab.add(button_6);

    JButton button_7 = new JButton("...");
    button_7.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine7.setText(el);
            }
            setVisible(true);
          }
        });
    button_7.setBounds(595, 255, 40, 26);
    engineTab.add(button_7);

    JButton button_8 = new JButton("...");
    button_8.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine8.setText(el);
            }
            setVisible(true);
          }
        });
    button_8.setBounds(595, 285, 40, 26);
    engineTab.add(button_8);

    JButton button_9 = new JButton("...");
    button_9.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String el = getEngineLine();
            if (!el.isEmpty()) {
              txtEngine9.setText(el);
            }
            setVisible(true);
          }
        });
    button_9.setBounds(595, 315, 40, 26);
    engineTab.add(button_9);

    JLabel lblMaxAnalyzeTime =
        new JLabel(resourceBundle.getString("LizzieConfig.title.maxAnalyzeTime"));
    lblMaxAnalyzeTime.setBounds(6, 380, 157, 16);
    engineTab.add(lblMaxAnalyzeTime);

    JLabel lblMaxAnalyzeTimeHint = new JLabel("设置分析时的最大分析时间,超过自动停止时可按空格键继续分析");
    lblMaxAnalyzeTimeHint.setBounds(6, 405, 357, 16);
    engineTab.add(lblMaxAnalyzeTimeHint);
    lblMaxAnalyzeTimeHint.setVisible(false);

    JLabel lblMaxAnalyzeTimeMinutes =
        new JLabel(resourceBundle.getString("LizzieConfig.title.minutes"));
    lblMaxAnalyzeTimeMinutes.setBounds(213, 380, 82, 16);

    engineTab.add(lblMaxAnalyzeTimeMinutes);

    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    txtMaxAnalyzeTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtMaxAnalyzeTime.setBounds(171, 375, 40, 26);
    engineTab.add(txtMaxAnalyzeTime);
    txtMaxAnalyzeTime.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblMaxAnalyzeTimeHint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblMaxAnalyzeTimeHint.setVisible(false);
          }
        });
    txtMaxAnalyzeTime.setColumns(10);

    JLabel lblMaxGameThinkingTime =
        new JLabel(resourceBundle.getString("LizzieConfig.title.maxGameThinkingTime"));
    lblMaxGameThinkingTime.setBounds(6, 440, 157, 16);
    engineTab.add(lblMaxGameThinkingTime);

    JLabel lblMaxGameThinkingTimeSeconds =
        new JLabel(resourceBundle.getString("LizzieConfig.title.seconds"));
    lblMaxGameThinkingTimeSeconds.setBounds(213, 440, 82, 16);
    engineTab.add(lblMaxGameThinkingTimeSeconds);

    JLabel lblMaxGameThinkingTimeSecondsHint = new JLabel("设置与AI对弈时,AI一步棋的最大思考时间");
    lblMaxGameThinkingTimeSecondsHint.setBounds(6, 465, 257, 16);
    engineTab.add(lblMaxGameThinkingTimeSecondsHint);
    lblMaxGameThinkingTimeSecondsHint.setVisible(false);

    txtMaxGameThinkingTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtMaxGameThinkingTime.setColumns(10);
    txtMaxGameThinkingTime.setBounds(171, 435, 40, 26);
    txtMaxGameThinkingTime.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblMaxGameThinkingTimeSecondsHint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblMaxGameThinkingTimeSecondsHint.setVisible(false);
          }
        });
    engineTab.add(txtMaxGameThinkingTime);

    JLabel lblMaxsuggestionmoves =
        new JLabel(resourceBundle.getString("LizzieConfig.title.maxSuggestionmoves"));
    lblMaxsuggestionmoves.setBounds(331, 440, 157, 16);
    engineTab.add(lblMaxsuggestionmoves);

    JLabel lblMaxsuggestionmovesnums =
        new JLabel(resourceBundle.getString("LizzieConfig.title.numbers"));
    lblMaxsuggestionmovesnums.setBounds(538, 440, 82, 16);
    engineTab.add(lblMaxsuggestionmovesnums);

    JLabel lblMaxsuggestionmovesnumshint = new JLabel("设置分析点显示数目,0为不限制");
    lblMaxsuggestionmovesnumshint.setBounds(331, 465, 257, 16);
    engineTab.add(lblMaxsuggestionmovesnumshint);
    lblMaxsuggestionmovesnumshint.setVisible(false);
    txtMaxsuggestionmoves =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtMaxsuggestionmoves.setColumns(10);
    txtMaxsuggestionmoves.setBounds(496, 435, 40, 26);
    txtMaxsuggestionmoves.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblMaxsuggestionmovesnumshint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblMaxsuggestionmovesnumshint.setVisible(false);
          }
        });
    engineTab.add(txtMaxsuggestionmoves);

    JLabel lbllimitBranchLength = new JLabel("分析点变化图最大手数");
    lbllimitBranchLength.setBounds(331, 500, 187, 16);
    engineTab.add(lbllimitBranchLength);

    JLabel lbllimitBranchNumber = new JLabel("手");
    lbllimitBranchNumber.setBounds(538, 500, 82, 16);
    engineTab.add(lbllimitBranchNumber);

    JLabel lbllimitBranchLengthHint = new JLabel("设置分析点变化图最大手数,0为不限制");
    lbllimitBranchLengthHint.setBounds(331, 525, 257, 16);
    engineTab.add(lbllimitBranchLengthHint);
    lbllimitBranchLengthHint.setVisible(false);
    txtlimitBranchLength =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtlimitBranchLength.setColumns(10);
    txtlimitBranchLength.setBounds(496, 495, 40, 26);
    txtlimitBranchLength.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lbllimitBranchLengthHint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lbllimitBranchLengthHint.setVisible(false);
          }
        });
    engineTab.add(txtlimitBranchLength);

    JLabel lblShowLcbWinrate =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showlcbwinrate"));
    lblShowLcbWinrate.setBounds(331, 555, 157, 16);
    engineTab.add(lblShowLcbWinrate);

    JLabel lblShowLcbWinratehint = new JLabel("设置胜率显示方式,Lcb更可靠但低计算量下会偏低");
    lblShowLcbWinratehint.setBounds(331, 580, 287, 16);
    engineTab.add(lblShowLcbWinratehint);
    lblShowLcbWinratehint.setVisible(false);

    rdoLcb = new JRadioButton("Lcb");
    rdoLcb.setBounds(496, 553, 50, 23);
    rdoLcb.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbWinratehint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbWinratehint.setVisible(false);
          }
        });
    engineTab.add(rdoLcb);

    rdoWinrate = new JRadioButton("Winrate");
    rdoWinrate.setBounds(545, 553, 80, 23);
    rdoWinrate.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbWinratehint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbWinratehint.setVisible(false);
          }
        });
    engineTab.add(rdoWinrate);

    ButtonGroup wrgroup = new ButtonGroup();
    wrgroup.add(rdoLcb);
    wrgroup.add(rdoWinrate);

    JLabel lblShowLcbColor = new JLabel("分析点红绿程度依据");
    lblShowLcbColor.setBounds(6, 555, 157, 16);
    engineTab.add(lblShowLcbColor);

    JLabel lblShowLcbColorhint =
        new JLabel("设置分析点红绿程度的依据,Lcb方式需0.17引擎支持,比计算量更可靠,无论显示胜率方式是否为Lcb此选项都可选择Lcb");
    lblShowLcbColorhint.setBounds(6, 580, 637, 16);
    engineTab.add(lblShowLcbColorhint);
    lblShowLcbColorhint.setVisible(false);

    rdoLcbfix = new JRadioButton("Lcb");
    rdoLcbfix.setBounds(171, 553, 50, 23);
    rdoLcbfix.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbColorhint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbColorhint.setVisible(false);
          }
        });
    engineTab.add(rdoLcbfix);

    rdoonlyWinrate = new JRadioButton("计算量");
    rdoonlyWinrate.setBounds(220, 553, 80, 23);
    rdoonlyWinrate.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbColorhint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblShowLcbColorhint.setVisible(false);
          }
        });
    engineTab.add(rdoonlyWinrate);

    ButtonGroup rggroup = new ButtonGroup();
    rggroup.add(rdoonlyWinrate);
    rggroup.add(rdoLcbfix);

    JLabel noponder = new JLabel("对弈时AI是否后台计算");
    noponder.setBounds(6, 597, 157, 16);
    engineTab.add(noponder);

    rdoponder = new JRadioButton("是");
    rdoponder.setBounds(171, 597, 50, 23);
    engineTab.add(rdoponder);

    rdonoponder = new JRadioButton("否");
    rdonoponder.setBounds(220, 597, 80, 23);
    engineTab.add(rdonoponder);

    ButtonGroup rdopondergp = new ButtonGroup();
    rdopondergp.add(rdonoponder);
    rdopondergp.add(rdoponder);

    JLabel norect = new JLabel("鼠标移动时是否显示小方块");
    norect.setBounds(331, 597, 157, 16);
    engineTab.add(norect);

    rdorect = new JRadioButton("是");
    rdorect.setBounds(496, 597, 50, 23);
    engineTab.add(rdorect);

    rdonorect = new JRadioButton("否");
    rdonorect.setBounds(545, 597, 80, 23);
    engineTab.add(rdonorect);

    ButtonGroup rdorectgp = new ButtonGroup();
    rdorectgp.add(rdonorect);
    rdorectgp.add(rdorect);

    JLabel nofast = new JLabel("是否启用引擎快速切换");
    nofast.setBounds(6, 625, 157, 16);
    engineTab.add(nofast);

    rdofast = new JRadioButton("是");
    rdofast.setBounds(171, 625, 50, 23);
    engineTab.add(rdofast);

    rdonofast = new JRadioButton("否");
    rdonofast.setBounds(220, 625, 80, 23);
    engineTab.add(rdonofast);

    ButtonGroup rdofastgp = new ButtonGroup();
    rdofastgp.add(rdonofast);
    rdofastgp.add(rdofast);

    JLabel noloadzen = new JLabel("是否预加载Zen(用于点目)");
    noloadzen.setBounds(331, 625, 157, 16);
    engineTab.add(noloadzen);

    rdoloadzen = new JRadioButton("是");
    rdoloadzen.setBounds(496, 625, 50, 23);
    engineTab.add(rdoloadzen);

    rdonoloadzen = new JRadioButton("否");
    rdonoloadzen.setBounds(545, 625, 80, 23);
    engineTab.add(rdonoloadzen);

    ButtonGroup rdoloadzengp = new ButtonGroup();
    rdoloadzengp.add(rdonoloadzen);
    rdoloadzengp.add(rdoloadzen);

    JLabel fastenginehint = new JLabel("如启用快速引擎切换,已经加载过的引擎再次启用时将不必重新加载");
    fastenginehint.setBounds(6, 650, 437, 16);
    engineTab.add(fastenginehint);
    fastenginehint.setVisible(false);

    rdofast.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            fastenginehint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            fastenginehint.setVisible(false);
          }
        });

    rdonofast.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            fastenginehint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            fastenginehint.setVisible(false);
          }
        });

    JLabel lblAnalyzeUpdateInterval =
        new JLabel(resourceBundle.getString("LizzieConfig.title.analyzeUpdateInterval"));
    lblAnalyzeUpdateInterval.setBounds(331, 380, 157, 16);
    engineTab.add(lblAnalyzeUpdateInterval);

    JLabel lblAnalyzeUpdateIntervalhint = new JLabel("设置界面刷新间隔,如感觉卡顿可适当加大");
    lblAnalyzeUpdateIntervalhint.setBounds(331, 405, 257, 16);
    engineTab.add(lblAnalyzeUpdateIntervalhint);
    lblAnalyzeUpdateIntervalhint.setVisible(false);

    JLabel lblAnalyzeUpdateIntervalCentisec =
        new JLabel(resourceBundle.getString("LizzieConfig.title.centisecond"));
    lblAnalyzeUpdateIntervalCentisec.setBounds(538, 380, 82, 16);
    engineTab.add(lblAnalyzeUpdateIntervalCentisec);

    txtAnalyzeUpdateInterval =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtAnalyzeUpdateInterval.setColumns(10);
    txtAnalyzeUpdateInterval.setBounds(496, 375, 40, 26);
    txtAnalyzeUpdateInterval.addFocusListener(
        new FocusListener() {

          @Override
          public void focusGained(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblAnalyzeUpdateIntervalhint.setVisible(true);
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            // TODO Auto-generated method stub
            lblAnalyzeUpdateIntervalhint.setVisible(false);
          }
        });
    engineTab.add(txtAnalyzeUpdateInterval);

    JLabel lblPrintEngineLog =
        new JLabel(resourceBundle.getString("LizzieConfig.title.printEngineLog"));
    lblPrintEngineLog.setBounds(6, 500, 157, 16);
    engineTab.add(lblPrintEngineLog);

    chkPrintEngineLog = new JCheckBox("");
    chkPrintEngineLog.setBounds(167, 498, 80, 23);
    engineTab.add(chkPrintEngineLog);
    JPanel uiTab = new JPanel();
    //  tabbedPane.addTab(resourceBundle.getString("LizzieConfig.title.ui"), null, uiTab, null);
    uiTab.setLayout(null);

    JLabel lblBoardSize = new JLabel(resourceBundle.getString("LizzieConfig.title.boardSize"));
    lblBoardSize.setBounds(6, 6, 67, 16);
    lblBoardSize.setHorizontalAlignment(SwingConstants.LEFT);
    uiTab.add(lblBoardSize);

    rdoBoardSize19 = new JRadioButton("19x19");
    rdoBoardSize19.setBounds(85, 2, 84, 23);
    uiTab.add(rdoBoardSize19);
    // engineTab.add(rdoBoardSize19);

    rdoBoardSize13 = new JRadioButton("13x13");
    rdoBoardSize13.setBounds(170, 2, 84, 23);
    uiTab.add(rdoBoardSize13);

    rdoBoardSize9 = new JRadioButton("9x9");
    rdoBoardSize9.setBounds(255, 2, 57, 23);
    uiTab.add(rdoBoardSize9);

    rdoBoardSize7 = new JRadioButton("7x7");
    rdoBoardSize7.setBounds(325, 2, 67, 23);
    uiTab.add(rdoBoardSize7);

    rdoBoardSize5 = new JRadioButton("5x5");
    rdoBoardSize5.setBounds(395, 2, 67, 23);
    uiTab.add(rdoBoardSize5);

    rdoBoardSize4 = new JRadioButton("4x4");
    rdoBoardSize4.setBounds(460, 2, 67, 23);
    uiTab.add(rdoBoardSize4);

    rdoBoardSizeOther = new JRadioButton("");
    rdoBoardSizeOther.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (rdoBoardSizeOther.isSelected()) {
              txtBoardSize.setEnabled(true);
            } else {
              txtBoardSize.setEnabled(false);
            }
          }
        });
    rdoBoardSizeOther.setBounds(530, 2, 29, 23);
    uiTab.add(rdoBoardSizeOther);

    ButtonGroup group = new ButtonGroup();
    group.add(rdoBoardSize19);
    group.add(rdoBoardSize13);
    group.add(rdoBoardSize9);
    group.add(rdoBoardSize7);
    group.add(rdoBoardSize5);
    group.add(rdoBoardSize4);
    group.add(rdoBoardSizeOther);

    txtBoardSize =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtBoardSize.setBounds(564, 1, 52, 26);
    uiTab.add(txtBoardSize);
    txtBoardSize.setColumns(10);

    JTabbedPane tabTheme = new JTabbedPane(JTabbedPane.TOP);
    // tabbedPane.addTab(resourceBundle.getString("LizzieConfig.title.theme"), null, tabTheme,
    // null);

    names =
        new JTextField[] {
          nameEngine,
          nameEngine1,
          nameEngine2,
          nameEngine3,
          nameEngine4,
          nameEngine5,
          nameEngine6,
          nameEngine7,
          nameEngine8,
          nameEngine9
        };
    leelazConfig = Lizzie.config.leelazConfig;
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(leelazConfig.optJSONArray("engine-name-list"));
    enginesNameOpt.ifPresent(
        a -> {
          IntStream.range(0, 9)
              .forEach(
                  i -> {
                    names[i].setText(a.getString(i));
                  });
        });

    txts =
        new JTextField[] {
          txtEngine1,
          txtEngine2,
          txtEngine3,
          txtEngine4,
          txtEngine5,
          txtEngine6,
          txtEngine7,
          txtEngine8,
          txtEngine9
        };

    txtEngine.setText(leelazConfig.getString("engine-command"));
    Optional<JSONArray> enginesOpt =
        Optional.ofNullable(leelazConfig.optJSONArray("engine-command-list"));
    enginesOpt.ifPresent(
        a -> {
          IntStream.range(0, 8)
              .forEach(
                  i -> {
                    txts[i].setText(a.getString(i));
                  });
        });

    chkPreloads =
        new JCheckBox[] {
          chkPreload,
          chkPreload1,
          chkPreload2,
          chkPreload3,
          chkPreload4,
          chkPreload5,
          chkPreload6,
          chkPreload7,
          chkPreload8,
          chkPreload9
        };
    Optional<JSONArray> enginePreloadOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));
    enginePreloadOpt.ifPresent(
        a -> {
          IntStream.range(0, 9)
              .forEach(
                  i -> {
                    chkPreloads[i].setSelected(a.optBoolean(i));
                  });
        });

    txtMaxAnalyzeTime.setText(String.valueOf(leelazConfig.getInt("max-analyze-time-minutes")));
    txtAnalyzeUpdateInterval.setText(
        String.valueOf(leelazConfig.getInt("analyze-update-interval-centisec")));
    txtMaxGameThinkingTime.setText(
        String.valueOf(leelazConfig.getInt("max-game-thinking-time-seconds")));
    txtMaxsuggestionmoves.setText(String.valueOf(leelazConfig.getInt("limit-max-suggestion")));
    txtlimitBranchLength.setText(String.valueOf(leelazConfig.getInt("limit-branch-length")));
    chkPrintEngineLog.setSelected(leelazConfig.getBoolean("print-comms"));
    curPath = (new File("")).getAbsoluteFile().toPath();
    osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    setBoardSize();
    setShowLcbWinrate();
    setPonder();
    setFastEngine();
    setRect();
    setLoadZen();
    setShowLcbColor();
    setLocationRelativeTo(getOwner());
  }

  private String getEngineLine() {
    String engineLine = "";
    File engineFile = null;
    File weightFile = null;
    JFileChooser chooser = new JFileChooser(".");
    if (isWindows()) {
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(
              resourceBundle.getString("LizzieConfig.title.engine"), "exe", "bat");
      chooser.setFileFilter(filter);
    } else {
      setVisible(false);
    }
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectEngine"));
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      engineFile = chooser.getSelectedFile();
      if (engineFile != null) {
        enginePath = engineFile.getAbsolutePath();
        enginePath = relativizePath(engineFile.toPath());
        getCommandHelp();
        JFileChooser chooserw = new JFileChooser(".");
        chooserw.setMultiSelectionEnabled(false);
        chooserw.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectWeight"));
        result = chooserw.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
          weightFile = chooserw.getSelectedFile();
          if (weightFile != null) {
            weightPath = relativizePath(weightFile.toPath());
            EngineParameter ep = new EngineParameter(enginePath, weightPath, commandHelp);
            ep.setVisible(true);
            if (!ep.commandLine.isEmpty()) {
              engineLine = ep.commandLine;
            }
          }
        }
      }
    }
    return engineLine;
  }

  private String relativizePath(Path path) {
    Path relatPath;
    if (path.startsWith(curPath)) {
      relatPath = curPath.relativize(path);
    } else {
      relatPath = path;
    }
    return relatPath.toString();
  }

  private void getCommandHelp() {

    List<String> commands = new ArrayList<String>();
    commands.add(enginePath);
    commands.add("-h");

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.directory();
    processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
      inputStream = new BufferedInputStream(process.getInputStream());
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
      }
      commandHelp = line.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveConfig() {
    try {

      leelazConfig.putOpt("max-analyze-time-minutes", txtFieldValue(txtMaxAnalyzeTime));
      Lizzie.config.maxAnalyzeTimeMillis = 60 * 1000 * txtFieldValue(txtMaxAnalyzeTime);
      if (Lizzie.config.maxAnalyzeTimeMillis == 0) {
        Lizzie.config.maxAnalyzeTimeMillis = 9999 * 60 * 1000;
      }
      leelazConfig.putOpt(
          "analyze-update-interval-centisec", txtFieldValue(txtAnalyzeUpdateInterval));
      Lizzie.config.analyzeUpdateIntervalCentisec = txtFieldValue(txtAnalyzeUpdateInterval);
      leelazConfig.putOpt("max-game-thinking-time-seconds", txtFieldValue(txtMaxGameThinkingTime));
      leelazConfig.putOpt("print-comms", chkPrintEngineLog.isSelected());
      leelazConfig.putOpt("show-lcb-winrate", getShowLcbWinrate());
      leelazConfig.putOpt("show-rect", getRect());
      leelazConfig.putOpt("load-zen", getLoadZen());
      leelazConfig.putOpt("play-ponder", getPonder());
      leelazConfig.putOpt("fast-engine-change", getFastEngine());
      leelazConfig.putOpt("show-lcb-color", getShowLcbColor());
      leelazConfig.put("engine-command", txtEngine.getText().trim());
      JSONArray preloads = new JSONArray();
      Arrays.asList(chkPreloads).forEach(t -> preloads.put(t.isSelected()));
      leelazConfig.put("engine-preload-list", preloads);
      leelazConfig.putOpt("limit-max-suggestion", txtFieldValue(txtMaxsuggestionmoves));
      leelazConfig.putOpt("limit-branch-length", txtFieldValue(txtlimitBranchLength));
      Lizzie.config.limitMaxSuggestion = txtFieldValue(txtMaxsuggestionmoves);
      Lizzie.config.limitBranchLength = txtFieldValue(txtlimitBranchLength);
      JSONArray engines = new JSONArray();
      Arrays.asList(txts).forEach(t -> engines.put(t.getText().trim()));
      leelazConfig.put("engine-command-list", engines);
      JSONArray enginenames = new JSONArray();
      Arrays.asList(names).forEach(t -> enginenames.put(t.getText().trim()));
      leelazConfig.put("engine-name-list", enginenames);
      //  Lizzie.config.uiConfig.put("board-size", getBoardSize());
      Lizzie.config.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Lizzie.engineManager.updateEngines();
  }

  //  private void applyChange() {
  //    Lizzie.board.reopen(getBoardSize(), getBoardSize());
  //  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  private String txtFieldString(JTextField txt) {
    if (txt.getText().trim().isEmpty()) {
      return "";
    } else {
      return txt.getText().trim().toString();
    }
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

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
      String newStr = text != null ? text.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.replace(offset, length, newStr, attrs);
      }
    }
  }

  public boolean isWindows() {
    return osName != null && !osName.contains("darwin") && osName.contains("win");
  }

  private void setLoadZen() {

    if (Lizzie.config.loadZen) {
      rdoloadzen.setSelected(true);
    } else {
      rdonoloadzen.setSelected(true);
    }
  }

  private boolean getLoadZen() {
    if (rdoloadzen.isSelected()) {
      Lizzie.config.loadZen = true;
    }
    if (rdonoloadzen.isSelected()) {
      Lizzie.config.loadZen = false;
    }
    return Lizzie.config.loadZen;
  }

  private void setRect() {

    if (Lizzie.config.showrect) {
      rdorect.setSelected(true);
    } else {
      rdonorect.setSelected(true);
    }
  }

  private boolean getRect() {
    if (rdorect.isSelected()) {
      Lizzie.config.showrect = true;
      return true;
    }
    if (rdonorect.isSelected()) {
      Lizzie.config.showrect = false;
      Lizzie.frame.boardRenderer.removeblock();
      return false;
    }
    return true;
  }

  private void setFastEngine() {

    if (Lizzie.config.fastChange) {
      rdofast.setSelected(true);
    } else {
      rdonofast.setSelected(true);
      chkPreload.setSelected(false);
      chkPreload.setEnabled(false);
      chkPreload1.setSelected(false);
      chkPreload1.setEnabled(false);
      chkPreload2.setSelected(false);
      chkPreload2.setEnabled(false);
      chkPreload3.setSelected(false);
      chkPreload3.setEnabled(false);
      chkPreload4.setSelected(false);
      chkPreload4.setEnabled(false);
      chkPreload5.setSelected(false);
      chkPreload5.setEnabled(false);
      chkPreload6.setSelected(false);
      chkPreload6.setEnabled(false);
      chkPreload7.setSelected(false);
      chkPreload7.setEnabled(false);
      chkPreload8.setSelected(false);
      chkPreload8.setEnabled(false);
      chkPreload9.setSelected(false);
      chkPreload9.setEnabled(false);
    }
  }

  private boolean getFastEngine() {
    if (rdofast.isSelected()) {
      Lizzie.config.fastChange = true;
      return true;
    }
    if (rdonofast.isSelected()) {
      Lizzie.config.fastChange = false;
      chkPreload.setSelected(false);
      chkPreload1.setSelected(false);
      chkPreload2.setSelected(false);
      chkPreload3.setSelected(false);
      chkPreload4.setSelected(false);
      chkPreload5.setSelected(false);
      chkPreload6.setSelected(false);
      chkPreload7.setSelected(false);
      chkPreload8.setSelected(false);
      chkPreload9.setSelected(false);
      return false;
    }
    return true;
  }

  private void setPonder() {

    if (Lizzie.config.playponder) {
      rdoponder.setSelected(true);
    } else {
      rdonoponder.setSelected(true);
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

  private void setShowLcbWinrate() {
    if (Lizzie.config.leelaversion < 17) {
      rdoLcb.setEnabled(false);
      rdoWinrate.setEnabled(false);
    } else {
      if (Lizzie.config.showlcbwinrate) {
        rdoLcb.setSelected(true);
      } else {
        rdoWinrate.setSelected(true);
      }
    }
  }

  private boolean getShowLcbWinrate() {
    if (rdoLcb.isSelected()) {
      Lizzie.config.showlcbwinrate = true;
      return true;
    }
    if (rdoWinrate.isSelected()) {
      Lizzie.config.showlcbwinrate = false;
      return false;
    }
    return true;
  }

  private boolean getShowLcbColor() {
    if (rdoLcbfix.isSelected()) {
      Lizzie.config.showlcbcolor = true;
      return true;
    }
    if (rdoonlyWinrate.isSelected()) {
      Lizzie.config.showlcbcolor = false;
      return false;
    }
    return true;
  }

  private void setShowLcbColor() {
    if (Lizzie.config.leelaversion < 17) {
      rdoLcbfix.setEnabled(false);
      rdoonlyWinrate.setEnabled(false);
    } else {
      if (Lizzie.config.showlcbcolor) {
        rdoLcbfix.setSelected(true);
      } else {
        rdoonlyWinrate.setSelected(true);
      }
    }
  }

  private void setBoardSize() {
    int size = Lizzie.config.uiConfig.optInt("board-size", 19);
    txtBoardSize.setEnabled(false);
    switch (size) {
      case 19:
        rdoBoardSize19.setSelected(true);
        break;
      case 13:
        rdoBoardSize13.setSelected(true);
        break;
      case 9:
        rdoBoardSize9.setSelected(true);
        break;
      case 7:
        rdoBoardSize7.setSelected(true);
        break;
      case 5:
        rdoBoardSize5.setSelected(true);
        break;
      case 4:
        rdoBoardSize4.setSelected(true);
        break;
      default:
        txtBoardSize.setText(String.valueOf(size));
        rdoBoardSizeOther.setSelected(true);
        txtBoardSize.setEnabled(true);
        break;
    }
  }

  private int getBoardSize() {
    if (rdoBoardSize19.isSelected()) {
      return 19;
    } else if (rdoBoardSize13.isSelected()) {
      return 13;
    } else if (rdoBoardSize9.isSelected()) {
      return 9;
    } else if (rdoBoardSize7.isSelected()) {
      return 7;
    } else if (rdoBoardSize5.isSelected()) {
      return 5;
    } else if (rdoBoardSize4.isSelected()) {
      return 4;
    } else {
      int size = Integer.parseInt(txtBoardSize.getText().trim());
      if (size < 2) {
        size = 19;
      }
      return size;
    }
  }
}
