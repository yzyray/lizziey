package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.rules.Movelist;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.json.JSONArray;

public class BottomToolbar extends JPanel {
  JButton firstButton;
  JButton lastButton;
  JButton clearButton;
  JButton countButton;
  JButton forward10;
  JButton backward10;
  JButton forward1;
  JButton gotomove;
  JButton backward1;
  JButton openfile;
  JButton savefile;
  public JButton analyse;
  public JButton detail;
  JButton kataEstimate;
  JButton heatMap;
  JButton backMain;
  JButton setMain;
  JButton batchOpen;
  JButton refresh;
  JButton tryPlay;
  JButton komi;
  public SetKomi setkomi;

  int savedbroadmid;
  JTextField txtMoveNumber;
  private int changeMoveNumber = 0;
  public boolean isAutoAna = false;
  public boolean isAutoPlay = false;
  public int firstMove = -1;
  public int lastMove = -1;
  public boolean startAutoAna = false;
  public int pkBlackWins = 0;
  public int pkWhiteWins = 0;
  public int timeb = -1;
  public int timew = -1;

  public int maxGanmeMove = 360;
  public boolean checkGameMaxMove = false;

  public int minGanmeMove = 100;
  public int minMove = -1;
  public boolean checkGameMinMove = false;

  public boolean isEnginePk = false;
  public int displayedSubBoardBranchLength = 1;
  public int engineBlack = -1;
  public int engineWhite = -1;
  public boolean isSameEngine = false;
  public int pkResignMoveCounts = 2;
  public double pkResginWinrate = 10;
  public boolean isEnginePkBatch = false;

  public boolean isRandomMove = false;
  public int randomMove = 30;
  public double randomDiffWinrate = 0.1;
  // public int EnginePkBatchNumber = 1;
  public int EnginePkBatchNumberNow = 1;
  public String batchPkName = "";
  public String SF = "";
  public boolean isGenmove = false;
  public boolean AutosavePk = false;
  public boolean exChange = true;
  public JCheckBox chkAutoAnalyse;
  public JCheckBox chkAnaTime;
  public JCheckBox chkAnaPlayouts;
  public JCheckBox chkAnaFirstPlayouts;
  public JCheckBox chkAnaAutoSave;
  public JCheckBox chkAnaBlack;
  public JCheckBox chkAnaWhite;

  public JCheckBox chkShowBlack;
  public JCheckBox chkShowWhite;

  public JCheckBox chkAutoMain;
  public JCheckBox chkAutoSub;

  public JTextField txtAutoMain;
  public JTextField txtAutoSub;

  public JCheckBox chkAutoPlay;
  public JCheckBox chkAutoPlayBlack;
  public JCheckBox chkAutoPlayWhite;
  public JCheckBox chkAutoPlayTime;
  public JCheckBox chkAutoPlayPlayouts;
  public JCheckBox chkAutoPlayFirstPlayouts;

  public JTextField txtAnaTime;
  public JTextField txtAnaPlayouts;
  public JTextField txtAnaFirstPlayouts;
  public JTextField txtFirstAnaMove;
  public JTextField txtLastAnaMove;

  public JTextField txtAutoPlayTime;
  public JTextField txtAutoPlayPlayouts;
  public JTextField txtAutoPlayFirstPlayouts;
  public int anaPanelOrder = 0;
  public int enginePkOrder = 1;
  public int autoPlayOrder = 2;
  public boolean isPkStop = false;
  // JButton cancelAutoAna;

  JLabel lblchkShowBlack;
  JLabel lblchkShowWhite;

  JLabel lblchkAutoAnalyse;
  JLabel lbltxtAnaTime;
  JLabel lbltxtAnaPlayouts;
  JLabel lblAnaFirstPlayouts;
  JLabel lblAnaMove;
  JLabel lblAnaAutoSave;
  JLabel lblAnaMoveAnd;

  JLabel lblAutoPlay;
  JLabel lblAutoPlayBlack;
  JLabel lblAutoPlayWhite;
  JLabel lblAutoPlayTime;
  JLabel lblAutoPlayPlayouts;
  JLabel lblAutoPlayFirstPlayouts;

  JPanel anaPanel;
  JPanel autoPlayPanel;
  JPanel enginePkPanel;

  public JCheckBox chkenginePk;
  // public JCheckBox chkenginePkgenmove;
  public JCheckBox chkenginePkTime;
  public JCheckBox chkenginePkPlayouts;
  public JCheckBox chkenginePkFirstPlayputs;

  public JCheckBox chkenginePkBatch;
  public JCheckBox chkenginePkContinue;
  public ArrayList<Movelist> startGame;
  // public JCheckBox chkenginePkAutosave;

  public JButton btnStartPk;
  public JButton btnEnginePkConfig;
  public JButton btnEnginePkStop;
  public JButton btnEngineMannul;

  JLabel lblenginePk;
  // JLabel lblgenmove;
  JLabel lblenginePkTime;
  JLabel lblenginePkTimeWhite;
  public JLabel lblengineBlack;
  public JLabel lblengineWhite;
  public JLabel lblenginePkPlayputs;
  public JLabel lblenginePkFirstPlayputs;
  public JLabel lblenginePkPlayputsWhite;
  public JLabel lblenginePkFirstPlayputsWhite;

  JLabel lblenginePkBatch;
  JLabel lblenginePkExchange;
  JLabel lblenginePkAutosave;

  public JLabel lblenginePkResult;

  public JTextField txtenginePkTime;
  public JTextField txtenginePkTimeWhite;
  public JTextField txtenginePkPlayputs;
  public JTextField txtenginePkFirstPlayputs;
  public JTextField txtenginePkPlayputsWhite;
  public JTextField txtenginePkFirstPlayputsWhite;
  public JTextField txtenginePkBatch;
  Message msg;
  public JComboBox enginePkBlack;
  ItemListener enginePkBlackLis;
  public JComboBox enginePkWhite;
  ItemListener enginePkWhiteLis;

  ImageIcon iconUp;
  ImageIcon iconDown;

  public BottomToolbar() {
    Color hsbColor =
        Color.getHSBColor(
            Color.RGBtoHSB(232, 232, 232, null)[0],
            Color.RGBtoHSB(232, 232, 232, null)[1],
            Color.RGBtoHSB(232, 232, 232, null)[2]);
    this.setBackground(hsbColor);

    setLayout(null);
    clearButton = new JButton("清空棋盘");
    firstButton = new JButton("|<");
    lastButton = new JButton(">|");
    countButton = new JButton("形势判断");
    forward10 = new JButton(">>");
    backward10 = new JButton("<<");
    gotomove = new JButton("跳转");
    savefile = new JButton("保存");
    backward1 = new JButton("<");
    forward1 = new JButton(">");
    openfile = new JButton("打开");
    kataEstimate = new JButton("Kata评估");
    analyse = new JButton("分析|暂停");
    heatMap = new JButton("策略网络");
    backMain = new JButton("返回主分支");
    setMain = new JButton("设为主分支");
    batchOpen = new JButton("批量分析");
    refresh = new JButton("刷新");
    tryPlay = new JButton("试下");
    komi = new JButton("贴目");
    iconUp = new ImageIcon();
    try {
      iconUp.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/up.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    iconDown = new ImageIcon();
    try {
      iconDown.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/down.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    detail = new JButton("");

    add(clearButton);
    add(lastButton);
    add(firstButton);
    add(countButton);
    add(forward10);
    add(savefile);
    add(backward10);
    add(gotomove);
    add(backward1);
    add(forward1);
    add(openfile);
    add(kataEstimate);
    add(analyse);
    add(detail);
    add(heatMap);
    add(backMain);
    add(setMain);
    add(batchOpen);
    add(refresh);
    add(tryPlay);
    add(komi);
    firstButton.setFocusable(false);
    lastButton.setFocusable(false);
    clearButton.setFocusable(false);
    countButton.setFocusable(false);
    forward10.setFocusable(false);
    backward10.setFocusable(false);
    gotomove.setFocusable(false);
    openfile.setFocusable(false);
    kataEstimate.setFocusable(false);
    analyse.setFocusable(false);
    forward1.setFocusable(false);
    backward1.setFocusable(false);
    savefile.setFocusable(false);
    detail.setFocusable(false);
    heatMap.setFocusable(false);
    backMain.setFocusable(false);
    setMain.setFocusable(false);
    batchOpen.setFocusable(false);
    refresh.setFocusable(false);
    tryPlay.setFocusable(false);
    komi.setFocusable(false);

    firstButton.setMargin(new Insets(0, 0, 0, 0));
    lastButton.setMargin(new Insets(0, 0, 0, 0));
    clearButton.setMargin(new Insets(0, 0, 0, 0));
    countButton.setMargin(new Insets(0, 0, 0, 0));
    forward10.setMargin(new Insets(0, 0, 0, 0));
    backward10.setMargin(new Insets(0, 0, 0, 0));
    gotomove.setMargin(new Insets(0, 0, 0, 0));
    openfile.setMargin(new Insets(0, 0, 0, 0));
    kataEstimate.setMargin(new Insets(0, 0, 0, 0));
    analyse.setMargin(new Insets(0, 0, 0, 0));
    forward1.setMargin(new Insets(0, 0, 0, 0));
    backward1.setMargin(new Insets(0, 0, 0, 0));
    savefile.setMargin(new Insets(0, 0, 0, 0));
    detail.setMargin(new Insets(0, 0, 0, 0));
    heatMap.setMargin(new Insets(0, 0, 0, 0));
    backMain.setMargin(new Insets(0, 0, 0, 0));
    setMain.setMargin(new Insets(0, 0, 0, 0));
    batchOpen.setMargin(new Insets(0, 0, 0, 0));
    refresh.setMargin(new Insets(0, 0, 0, 0));
    tryPlay.setMargin(new Insets(0, 0, 0, 0));
    komi.setMargin(new Insets(0, 0, 0, 0));
    // NumberFormat nf = NumberFormat.getNumberInstance();

    // nf.setGroupingUsed(false);
    // nf.setParseIntegerOnly(true);
    txtMoveNumber = new JTextField();
    add(txtMoveNumber);
    txtMoveNumber.setColumns(3);

    txtMoveNumber.addKeyListener(
        new KeyListener() {
          @Override
          public void keyPressed(KeyEvent arg0) {
            int key = arg0.getKeyCode();
            if (key == '\n') {
              checkMove();
              txtMoveNumber.setFocusable(false);
              txtMoveNumber.setFocusable(true);
              txtMoveNumber.setBackground(Color.WHITE);
              txtMoveNumber.setText("");
              if (changeMoveNumber != 0) Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);
            }
          }

          @Override
          public void keyReleased(KeyEvent e) {}

          @Override
          public void keyTyped(KeyEvent e) {}
        });

    refresh.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearbestmoves();
            Lizzie.leelaz.setResponseUpToDate();
            Lizzie.leelaz.sendCommand("clear_cache");
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.frame.refresh();
          }
        });
    tryPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.tryPlay();
          }
        });
    komi.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setkomi = new SetKomi();
            setkomi.setVisible(true);
          }
        });
    batchOpen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileWithAna();
          }
        });

    backMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (!Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
              Lizzie.board.previousMove();
            }
          }
        });
    setMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.setAsMainBranch()) ;
          }
        });
    heatMap.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleheatmap();
          }
        });
    detail.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.toolbarHeight == 26) {
              Lizzie.frame.toolbarHeight = 70;
              detail.setIcon(iconDown);
              Lizzie.frame.mainPanel.setBounds(
                  Lizzie.frame.mainPanel.getX(),
                  Lizzie.frame.mainPanel.getY(),
                  Lizzie.frame.mainPanel.getWidth(),
                  Lizzie.frame.mainPanel.getHeight() - 44);
              Lizzie.frame.toolbar.setBounds(
                  0,
                  Lizzie.frame.getHeight()
                      - Lizzie.frame.getJMenuBar().getHeight()
                      - Lizzie.frame.getInsets().top
                      - Lizzie.frame.getInsets().bottom
                      - Lizzie.frame.toolbarHeight,
                  Lizzie.frame.getWidth()
                      - Lizzie.frame.getInsets().left
                      - Lizzie.frame.getInsets().right,
                  Lizzie.frame.toolbarHeight);

            } else {
              Lizzie.frame.toolbarHeight = 26;
              detail.setIcon(iconUp);
              Lizzie.frame.mainPanel.setBounds(
                  Lizzie.frame.mainPanel.getX(),
                  Lizzie.frame.mainPanel.getY(),
                  Lizzie.frame.mainPanel.getWidth(),
                  Lizzie.frame.mainPanel.getHeight() + 44);
              Lizzie.frame.toolbar.setBounds(
                  0,
                  Lizzie.frame.getHeight()
                      - Lizzie.frame.getJMenuBar().getHeight()
                      - Lizzie.frame.getInsets().top
                      - Lizzie.frame.getInsets().bottom
                      - Lizzie.frame.toolbarHeight,
                  Lizzie.frame.getWidth()
                      - Lizzie.frame.getInsets().left
                      - Lizzie.frame.getInsets().right,
                  Lizzie.frame.toolbarHeight);
            }
          }
        });
    analyse.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.togglePonder();
            Lizzie.frame.refresh();
            setTxtUnfocuse();
          }
        });
    forward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(10);
            setTxtUnfocuse();
          }
        });
    backward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(10);
            setTxtUnfocuse();
          }
        });
    forward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(1);
            setTxtUnfocuse();
          }
        });
    backward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(1);
            setTxtUnfocuse();
          }
        });
    gotomove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            checkMove();

            txtMoveNumber.setBackground(Color.WHITE);
            txtMoveNumber.setText("");
            // Lizzie.board.savelist(changeMoveNumber);
            // Lizzie.board.setlist();
            if (changeMoveNumber != 0) Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);

            setTxtUnfocuse();
          }
        });
    // backward1.addActionListener(
    // new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // Input.undo(1);
    // setAllUnfocuse();
    // }
    // });
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileAll();
            setTxtUnfocuse();
          }
        });
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveFile();
            setTxtUnfocuse();
          }
        });
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clear();
            Lizzie.frame.refresh();
            setTxtUnfocuse();
          }
        });
    lastButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.nextMove()) ;
            setTxtUnfocuse();
          }
        });
    firstButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.previousMove()) ;
            setTxtUnfocuse();
          }
        });
    countButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.iscounting) {
              Lizzie.frame.boardRenderer.removecountblock();
              Lizzie.frame.repaint();
              Lizzie.frame.iscounting = false;
              Lizzie.countResults.setVisible(false);
            } else {
              Lizzie.frame.countstones();
            }
            setTxtUnfocuse();
          }
        });
    kataEstimate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            Lizzie.config.showKataGoEstimate = !Lizzie.config.showKataGoEstimate;
            if (!Lizzie.config.showKataGoEstimate) {
              Lizzie.frame.boardRenderer.removecountblock();
              if (Lizzie.config.showSubBoard) Lizzie.frame.subBoardRenderer.removecountblock();
            }

            Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            setTxtUnfocuse();
          }
        });

    this.addMouseListener(
        new MouseListener() {
          public void mouseClicked(MouseEvent e) {
            setTxtUnfocuse();
          }

          @Override
          public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

          }
        });
    anaPanel = new JPanel();
    anaPanel.setLayout(null);
    add(anaPanel);
    anaPanel.setBounds(0, 26, 350, 44);
    anaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    autoPlayPanel = new JPanel();
    autoPlayPanel.setLayout(null);
    add(autoPlayPanel);
    autoPlayPanel.setBounds(950, 26, 495, 44);
    autoPlayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    enginePkPanel = new JPanel();
    enginePkPanel.setLayout(null);
    add(enginePkPanel);
    enginePkPanel.setBounds(350, 26, 600, 44);
    enginePkPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    chkAutoAnalyse = new JCheckBox();
    anaPanel.add(chkAutoAnalyse);
    lblchkAutoAnalyse = new JLabel("自动分析");
    chkAutoAnalyse.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              firstMove = Integer.parseInt(txtFirstAnaMove.getText().replace(" ", ""));
            } catch (Exception ex) {
              firstMove = -1;
            }
            try {
              lastMove = Integer.parseInt(txtLastAnaMove.getText().replace(" ", ""));
            } catch (Exception ex) {
              lastMove = -1;
            }
            if (chkAutoAnalyse.isSelected()) {
              Lizzie.leelaz.nameCmd();
              Timer timer = new Timer();
              timer.schedule(
                  new TimerTask() {
                    public void run() {
                      startAutoAna();
                      this.cancel();
                    }
                  },
                  300);

            } else {
              isAutoAna = false;
              startAutoAna = false;
              if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
            }
            setTxtUnfocuse();
            if (chkAutoAnalyse.isSelected()) {
              Lizzie.frame.removeInput();
            } else {
              Lizzie.frame.addInput();
            }
          }
        });
    anaPanel.add(lblchkAutoAnalyse);
    chkAutoAnalyse.setBounds(1, 1, 20, 18);
    lblchkAutoAnalyse.setBounds(21, 0, 60, 20);
    chkAnaBlack = new JCheckBox("黑");
    chkAnaWhite = new JCheckBox("白");
    chkAnaBlack.setFocusable(false);
    chkAnaWhite.setFocusable(false);
    chkAnaBlack.setSelected(true);
    chkAnaWhite.setSelected(true);
    chkAnaBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    chkAnaWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    anaPanel.add(chkAnaBlack);
    anaPanel.add(chkAnaWhite);
    chkAnaBlack.setBounds(68, 1, 40, 20);

    lblAnaMove = new JLabel("手数:");
    lblAnaMove.setBounds(108, 0, 40, 20);
    anaPanel.add(lblAnaMove);

    txtFirstAnaMove = new JTextField();
    anaPanel.add(txtFirstAnaMove);
    txtFirstAnaMove.setBounds(138, 2, 30, 18);

    txtFirstAnaMove.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // 失去焦点执行的代码
            try {
              firstMove = Integer.parseInt(txtFirstAnaMove.getText().replace(" ", ""));
            } catch (Exception ex) {
            }
          }

          @Override
          public void focusGained(FocusEvent e) {
            // 获得焦点执行的代码
          }
        });

    lblAnaMoveAnd = new JLabel("到");
    lblAnaMoveAnd.setBounds(170, 0, 15, 20);
    anaPanel.add(lblAnaMoveAnd);

    txtLastAnaMove = new JTextField();
    anaPanel.add(txtLastAnaMove);
    txtLastAnaMove.setBounds(185, 2, 30, 18);

    txtLastAnaMove.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // 失去焦点执行的代码
            try {
              lastMove = Integer.parseInt(txtLastAnaMove.getText().replace(" ", ""));
            } catch (Exception ex) {
            }
          }

          @Override
          public void focusGained(FocusEvent e) {
            // 获得焦点执行的代码
          }
        });
    lblAnaFirstPlayouts = new JLabel("首位计算量:");
    chkAnaFirstPlayouts = new JCheckBox();
    chkAnaFirstPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

    anaPanel.add(chkAnaFirstPlayouts);
    anaPanel.add(lblAnaFirstPlayouts);
    chkAnaFirstPlayouts.setBounds(215, 1, 20, 18);
    lblAnaFirstPlayouts.setBounds(234, 0, 80, 20);
    txtAnaFirstPlayouts = new JTextField();
    anaPanel.add(txtAnaFirstPlayouts);
    txtAnaFirstPlayouts.setBounds(298, 2, 45, 18);

    chkAnaAutoSave = new JCheckBox();
    anaPanel.add(chkAnaAutoSave);
    chkAnaAutoSave.setBounds(1, 22, 20, 20);
    lblAnaAutoSave = new JLabel("自动保存");
    anaPanel.add(lblAnaAutoSave);
    lblAnaAutoSave.setBounds(21, 22, 50, 20);
    chkAnaAutoSave.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

    chkAnaWhite.setBounds(68, 22, 40, 20);
    chkAnaPlayouts = new JCheckBox();
    chkAnaPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    lbltxtAnaPlayouts = new JLabel("总计算量:");
    anaPanel.add(chkAnaPlayouts);
    anaPanel.add(lbltxtAnaPlayouts);
    chkAnaPlayouts.setBounds(104, 22, 20, 20);
    lbltxtAnaPlayouts.setBounds(124, 22, 80, 20);
    txtAnaPlayouts = new JTextField();
    anaPanel.add(txtAnaPlayouts);
    txtAnaPlayouts.setBounds(175, 23, 50, 18);

    chkAnaTime = new JCheckBox();
    lbltxtAnaTime = new JLabel("按时间(秒):");

    chkAnaTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

    anaPanel.add(chkAnaTime);
    anaPanel.add(lbltxtAnaTime);
    chkAnaTime.setBounds(230, 22, 20, 20);
    lbltxtAnaTime.setBounds(250, 22, 80, 20);
    txtAnaTime = new JTextField();
    anaPanel.add(txtAnaTime);
    txtAnaTime.setBounds(310, 23, 33, 18);

    chkAutoPlay = new JCheckBox();
    chkAutoPlay.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
            isAutoPlay = chkAutoPlay.isSelected();
          }
        });

    chkShowBlack = new JCheckBox();
    chkShowWhite = new JCheckBox();
    chkShowBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    chkShowWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    lblchkShowBlack = new JLabel("显示黑");
    lblchkShowWhite = new JLabel("显示白");

    JLabel autoMain = new JLabel("自动播放(大)(秒)");
    JLabel autoSub = new JLabel("自动播放(小)(毫秒)");
    chkAutoMain = new JCheckBox();
    chkAutoSub = new JCheckBox();
    chkAutoMain.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (chkAutoMain.isSelected()) {
              Runnable runnable =
                  new Runnable() {
                    public void run() {
                      int time = -1;
                      try {
                        time = 1000 * Integer.parseInt(txtAutoMain.getText().replace(" ", ""));
                      } catch (NumberFormatException err) {
                      }
                      if (time <= 0) {
                        chkAutoMain.setSelected(false);
                        return;
                      }
                      while (Lizzie.board.nextMove() && chkAutoMain.isSelected())
                        try {
                          try {
                            time = 1000 * Integer.parseInt(txtAutoMain.getText().replace(" ", ""));
                          } catch (NumberFormatException err) {
                          }
                          Thread.sleep(time);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                      chkAutoMain.setSelected(false);
                    }
                  };
              Thread thread = new Thread(runnable);
              thread.start();
            }

            setTxtUnfocuse();
          }
        });
    chkAutoSub.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (chkAutoSub.isSelected()) {
              Runnable runnable =
                  new Runnable() {
                    public void run() {
                      int time = -1;
                      try {
                        time = Integer.parseInt(txtAutoSub.getText().replace(" ", ""));
                      } catch (NumberFormatException err) {
                      }
                      if (time <= 0) {
                        chkAutoSub.setSelected(false);
                        return;
                      }
                      while (chkAutoSub.isSelected()) {
                        if (!Lizzie.frame.subBoardRenderer.wheeled)
                          Lizzie.frame.subBoardRenderer.setDisplayedBranchLength(
                              displayedSubBoardBranchLength);
                        try {
                          try {
                            time = Integer.parseInt(txtAutoSub.getText().replace(" ", ""));
                          } catch (NumberFormatException err) {
                          }
                          Thread.sleep(time);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                        displayedSubBoardBranchLength = displayedSubBoardBranchLength + 1;
                      }
                    }
                  };
              Thread thread = new Thread(runnable);
              thread.start();
            } else {
              Lizzie.frame.subBoardRenderer.setDisplayedBranchLength(-2);
            }

            setTxtUnfocuse();
          }
        });
    txtAutoMain = new JTextField();
    txtAutoSub = new JTextField();

    chkAutoMain.setBounds(325, 1, 20, 18);
    chkAutoSub.setBounds(325, 22, 20, 18);
    autoMain.setBounds(345, 0, 100, 18);
    autoSub.setBounds(345, 22, 100, 18);
    txtAutoMain.setBounds(445, 2, 40, 18);
    txtAutoSub.setBounds(445, 23, 40, 18);
    autoPlayPanel.add(lblchkShowBlack);
    autoPlayPanel.add(lblchkShowWhite);
    autoPlayPanel.add(chkShowWhite);
    autoPlayPanel.add(chkShowBlack);

    autoPlayPanel.add(autoMain);
    autoPlayPanel.add(autoSub);
    autoPlayPanel.add(chkAutoMain);
    autoPlayPanel.add(chkAutoSub);
    autoPlayPanel.add(txtAutoMain);
    autoPlayPanel.add(txtAutoSub);

    chkShowBlack.setBounds(5, 1, 20, 18);
    lblchkShowBlack.setBounds(25, 0, 40, 18);
    chkShowWhite.setBounds(5, 22, 20, 18);
    lblchkShowWhite.setBounds(25, 22, 40, 18);
    lblAutoPlay = new JLabel("自动落子");
    autoPlayPanel.add(chkAutoPlay);
    autoPlayPanel.add(lblAutoPlay);
    chkAutoPlay.setBounds(60, 1, 20, 18);
    lblAutoPlay.setBounds(80, 0, 60, 20);
    chkAutoPlayBlack = new JCheckBox();
    chkAutoPlayWhite = new JCheckBox();
    chkAutoPlayBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    chkAutoPlayWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    lblAutoPlayBlack = new JLabel("黑");
    lblAutoPlayWhite = new JLabel("白");
    autoPlayPanel.add(lblAutoPlayBlack);
    autoPlayPanel.add(lblAutoPlayWhite);
    autoPlayPanel.add(chkAutoPlayBlack);
    autoPlayPanel.add(chkAutoPlayWhite);
    lblAutoPlayBlack.setBounds(150, 0, 20, 20);
    chkAutoPlayBlack.setBounds(130, 1, 20, 18);

    lblAutoPlayWhite.setBounds(185, 0, 20, 20);
    chkAutoPlayWhite.setBounds(165, 1, 20, 18);

    chkAutoPlayTime = new JCheckBox();
    lblAutoPlay = new JLabel("按时间(秒):");
    txtAutoPlayTime = new JTextField();
    chkAutoPlayTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    autoPlayPanel.add(chkAutoPlayTime);
    autoPlayPanel.add(lblAutoPlay);
    autoPlayPanel.add(txtAutoPlayTime);
    chkAutoPlayTime.setBounds(205, 1, 20, 18);
    lblAutoPlay.setBounds(225, 0, 70, 20);
    txtAutoPlayTime.setBounds(290, 2, 33, 18);

    chkAutoPlayPlayouts = new JCheckBox();
    lblAutoPlayPlayouts = new JLabel("总计算量:");
    txtAutoPlayPlayouts = new JTextField();
    chkAutoPlayPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    autoPlayPanel.add(chkAutoPlayPlayouts);
    autoPlayPanel.add(lblAutoPlayPlayouts);
    autoPlayPanel.add(txtAutoPlayPlayouts);
    chkAutoPlayPlayouts.setBounds(60, 23, 20, 18);
    lblAutoPlayPlayouts.setBounds(80, 22, 60, 20);
    txtAutoPlayPlayouts.setBounds(135, 23, 50, 18);

    chkAutoPlayFirstPlayouts = new JCheckBox();
    lblAutoPlayFirstPlayouts = new JLabel("首位计算量:");
    txtAutoPlayFirstPlayouts = new JTextField();
    chkAutoPlayFirstPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    autoPlayPanel.add(chkAutoPlayFirstPlayouts);
    autoPlayPanel.add(lblAutoPlayFirstPlayouts);
    autoPlayPanel.add(txtAutoPlayFirstPlayouts);
    chkAutoPlayFirstPlayouts.setBounds(185, 23, 20, 18);
    lblAutoPlayFirstPlayouts.setBounds(205, 22, 70, 20);
    txtAutoPlayFirstPlayouts.setBounds(272, 23, 50, 18);

    chkenginePk = new JCheckBox();
    lblenginePk = new JLabel("引擎对战");
    enginePkPanel.add(chkenginePk);
    enginePkPanel.add(lblenginePk);

    chkenginePk.setBounds(2, 1, 20, 18);
    lblenginePk.setBounds(22, 0, 60, 18);
    chkenginePk.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();
            if (!chkenginePk.isSelected()) {
              enginePkBlack.setEnabled(false);
              enginePkWhite.setEnabled(false);
            } else {
              enginePkBlack.setEnabled(true);
              enginePkWhite.setEnabled(true);
            }
          }
        });

    // chkenginePkgenmove = new JCheckBox();
    // lblgenmove = new JLabel("genmove");
    // enginePkPanel.add(chkenginePkgenmove);
    // enginePkPanel.add(lblgenmove);
    // chkenginePkgenmove.setBounds(70, 23, 20, 18);
    // lblgenmove.setBounds(90, 22, 60, 18);

    // chkenginePkgenmove.addActionListener(
    // new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // // TBD未完成
    // setTxtUnfocuse();
    // if (chkenginePkgenmove.isSelected()) {
    // chkenginePkPlayouts.setSelected(false);
    // chkenginePkPlayouts.setEnabled(false);
    // chkenginePkFirstPlayputs.setSelected(false);
    // chkenginePkFirstPlayputs.setEnabled(false);
    // } else {
    // chkenginePkFirstPlayputs.setEnabled(true);
    // chkenginePkPlayouts.setEnabled(true);
    // }
    // }
    // });

    btnEnginePkConfig = new JButton("设置");
    enginePkPanel.add(btnEnginePkConfig);
    btnEnginePkConfig.setBounds(42, 22, 35, 20);
    btnEnginePkConfig.setMargin(new Insets(0, 0, 0, 0));
    btnEnginePkConfig.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();
            EnginePkConfig engineconfig = new EnginePkConfig();
            engineconfig.setVisible(true);
          }
        });
    btnEnginePkStop = new JButton("暂停");
    enginePkPanel.add(btnEnginePkStop);
    btnEnginePkStop.setBounds(76, 22, 35, 20);
    btnEnginePkStop.setMargin(new Insets(0, 0, 0, 0));
    btnEnginePkStop.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();

            if (isPkStop) {
              btnEnginePkStop.setText("暂停");
              isPkStop = false;
              if (Lizzie.board.getData().blackToPlay) {

                Lizzie.engineManager.engineList.get(engineBlack).ponder();
              } else {

                Lizzie.engineManager.engineList.get(engineWhite).ponder();
              }
            } else {
              btnEnginePkStop.setText("继续");

              if (Lizzie.board.getData().blackToPlay) {

                Lizzie.engineManager.engineList.get(engineBlack).nameCmd();
              } else {

                Lizzie.engineManager.engineList.get(engineWhite).nameCmd();
              }
              isPkStop = true;
            }
            Lizzie.engineManager.startInfoTime = System.currentTimeMillis();
            //  Lizzie.engineManager.gameTime = System.currentTimeMillis();
          }
        });

    chkenginePkTime = new JCheckBox();
    lblenginePkTime = new JLabel("时间(秒) 黑");
    lblenginePkTimeWhite = new JLabel("白");
    txtenginePkTime = new JTextField();
    txtenginePkTimeWhite = new JTextField();
    enginePkPanel.add(chkenginePkTime);
    enginePkPanel.add(lblenginePkTime);
    enginePkPanel.add(txtenginePkTime);
    enginePkPanel.add(lblenginePkTimeWhite);
    enginePkPanel.add(txtenginePkTimeWhite);
    chkenginePkTime.setBounds(110, 23, 20, 18);
    lblenginePkTime.setBounds(130, 22, 70, 18);
    txtenginePkTime.setBounds(190, 24, 23, 18);
    lblenginePkTimeWhite.setBounds(215, 22, 15, 18);
    txtenginePkTimeWhite.setBounds(227, 24, 23, 18);
    chkenginePkTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    btnStartPk = new JButton("开始");
    enginePkPanel.add(btnStartPk);
    btnStartPk.setBounds(8, 22, 35, 20);
    btnStartPk.setMargin(new Insets(0, 0, 0, 0));

    btnStartPk.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            if (!chkenginePk.isSelected()) {
              if (msg == null || !msg.isVisible()) {
                msg = new Message();
                msg.setMessage("请先勾选[引擎对战],并选择黑白引擎后再开始");
                msg.setVisible(true);
              }
              return;
            }

            if (!isEnginePk) {
              startEnginePk();
            } else {
              isEnginePk = false;
              btnStartPk.setText("开始");
              Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
              Lizzie.frame.boardRenderer.reverseBestmoves = false;
              Lizzie.engineManager.engineList.get(engineBlack).played = false;
              Lizzie.engineManager.engineList.get(engineWhite).played = false;
              Lizzie.frame.addInput();
              analyse.setEnabled(true);
              enginePkBlack.setEnabled(true);
              enginePkWhite.setEnabled(true);
              // txtenginePkBatch.setEnabled(true);
              // chkenginePkAutosave.setEnabled(true);
              // AutosavePk=true;
              btnEnginePkConfig.setEnabled(true);
              chkenginePkBatch.setEnabled(true);
              chkenginePkTime.setEnabled(true);
              txtenginePkTime.setEnabled(true);
              txtenginePkTimeWhite.setEnabled(true);
              batchPkName = "";
              // chkenginePkgenmove.setEnabled(true);
              chkenginePk.setEnabled(true);
              Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
              Lizzie.engineManager.changeEngIcoForEndPk();
            }
            setTxtUnfocuse();
          }
        });

    enginePkBlack = new JComboBox();
    enginePkPanel.add(enginePkBlack);
    enginePkBlack.setBounds(90, 2, 95, 18);
    lblengineBlack = new JLabel("黑:");
    enginePkPanel.add(lblengineBlack);
    lblengineBlack.setBounds(75, 0, 15, 20);
    UI ui = new UI();
    enginePkBlack.setUI(ui);
    ((Popup) ui.getPopup()).setDisplaySize(200, 100);

    lblenginePkResult = new JLabel("0:0");
    enginePkPanel.add(lblenginePkResult);
    lblenginePkResult.setBounds(190, 0, 45, 20);

    enginePkWhite = new JComboBox();
    addEngineLis();
    enginePkPanel.add(enginePkWhite);
    enginePkWhite.setBounds(255, 2, 95, 18);

    UI ui2 = new UI();
    enginePkWhite.setUI(ui2);
    ((Popup) ui2.getPopup()).setDisplaySize(200, 100);

    lblengineWhite = new JLabel("白:");
    enginePkPanel.add(lblengineWhite);
    lblengineWhite.setBounds(230, 0, 15, 20);

    enginePkBlack.setEnabled(false);
    enginePkWhite.setEnabled(false);

    lblenginePkFirstPlayputs = new JLabel("首位计算量  黑:");
    chkenginePkFirstPlayputs = new JCheckBox();
    txtenginePkFirstPlayputs = new JTextField();
    enginePkPanel.add(lblenginePkFirstPlayputs);
    enginePkPanel.add(chkenginePkFirstPlayputs);
    enginePkPanel.add(txtenginePkFirstPlayputs);
    chkenginePkFirstPlayputs.setBounds(248, 23, 20, 18);
    lblenginePkFirstPlayputs.setBounds(268, 22, 90, 18);
    txtenginePkFirstPlayputs.setBounds(350, 24, 50, 18);
    lblenginePkFirstPlayputsWhite = new JLabel("白:");
    txtenginePkFirstPlayputsWhite = new JTextField();
    enginePkPanel.add(lblenginePkFirstPlayputsWhite);
    enginePkPanel.add(txtenginePkFirstPlayputsWhite);
    lblenginePkFirstPlayputsWhite.setBounds(400, 22, 20, 18);
    txtenginePkFirstPlayputsWhite.setBounds(415, 24, 50, 18);

    chkenginePkFirstPlayputs.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    chkenginePkBatch = new JCheckBox();
    lblenginePkBatch = new JLabel("多盘:");
    txtenginePkBatch = new JTextField();
    enginePkPanel.add(chkenginePkBatch);
    enginePkPanel.add(lblenginePkBatch);
    enginePkPanel.add(txtenginePkBatch);
    chkenginePkBatch.setBounds(465, 23, 20, 18);
    lblenginePkBatch.setBounds(485, 22, 30, 20);
    txtenginePkBatch.setBounds(515, 24, 30, 18);

    chkenginePkBatch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    chkenginePkContinue = new JCheckBox();
    lblenginePkExchange = new JLabel("续弈");
    enginePkPanel.add(chkenginePkContinue);
    enginePkPanel.add(lblenginePkExchange);
    chkenginePkContinue.setBounds(545, 23, 20, 18);
    lblenginePkExchange.setBounds(565, 22, 50, 20);
    chkenginePkContinue.setToolTipText("先摆好一个局面,然后勾选就可以每盘都从这个局面开始对战");
    chkenginePkContinue.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    lblenginePkPlayputs = new JLabel("总计算量  黑:");
    chkenginePkPlayouts = new JCheckBox();
    txtenginePkPlayputs = new JTextField();
    enginePkPanel.add(lblenginePkPlayputs);
    enginePkPanel.add(chkenginePkPlayouts);
    enginePkPanel.add(txtenginePkPlayputs);
    chkenginePkPlayouts.setBounds(355, 1, 20, 18);
    lblenginePkPlayputs.setBounds(375, 0, 70, 18);
    txtenginePkPlayputs.setBounds(445, 2, 50, 18);
    chkenginePkPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    lblenginePkPlayputsWhite = new JLabel("白:");
    txtenginePkPlayputsWhite = new JTextField();
    enginePkPanel.add(lblenginePkPlayputsWhite);
    enginePkPanel.add(txtenginePkPlayputsWhite);
    lblenginePkPlayputsWhite.setBounds(498, 0, 15, 18);
    txtenginePkPlayputsWhite.setBounds(513, 2, 50, 18);

    btnEngineMannul = new JButton("干预");

    btnEngineMannul.setBounds(563, 1, 35, 20);
    btnEngineMannul.setMargin(new Insets(0, 0, 0, 0));
    btnEngineMannul.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // 打开干预面板
            Manual manul = new Manual();
            manul.setVisible(true);
            setTxtUnfocuse();
          }
        });

    enginePkPanel.add(btnEngineMannul);
    // chkenginePkAutosave = new JCheckBox();
    // lblenginePkAutosave = new JLabel("自动保存");
    // enginePkPanel.add(chkenginePkAutosave);
    // enginePkPanel.add(lblenginePkAutosave);
    // chkenginePkAutosave.setBounds(515, 1, 20, 18);
    // lblenginePkAutosave.setBounds(535, 0, 50, 20);
    //
    // chkenginePkAutosave.addActionListener(
    // new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // setTxtUnfocuse();
    // }
    // });
    chkAutoMain.setFocusable(false);
    chkAutoSub.setFocusable(false);
    chkAutoPlay.setFocusable(false);
    chkAutoPlayBlack.setFocusable(false);
    chkAutoPlayWhite.setFocusable(false);
    chkAutoPlayTime.setFocusable(false);
    chkAutoPlayPlayouts.setFocusable(false);
    chkAutoPlayFirstPlayouts.setFocusable(false);
    chkAutoAnalyse.setFocusable(false);
    chkAnaTime.setFocusable(false);
    chkAnaPlayouts.setFocusable(false);
    chkAnaFirstPlayouts.setFocusable(false);
    chkAnaAutoSave.setFocusable(false);
    chkShowBlack.setFocusable(false);
    chkShowWhite.setFocusable(false);
    chkAutoPlayBlack.setFocusable(false);
    chkAutoPlayWhite.setFocusable(false);
    chkAutoPlay.setFocusable(false);
    chkAutoPlayTime.setFocusable(false);
    chkAutoPlayPlayouts.setFocusable(false);
    chkAutoPlayFirstPlayouts.setFocusable(false);
    btnStartPk.setFocusable(false);
    btnEngineMannul.setFocusable(false);
    chkenginePk.setFocusable(false);
    chkenginePkTime.setFocusable(false);
    chkenginePkPlayouts.setFocusable(false);
    chkenginePkFirstPlayputs.setFocusable(false);
    // chkenginePkgenmove.setFocusable(false);
    enginePkBlack.setFocusable(false);
    enginePkWhite.setFocusable(false);
    btnEnginePkConfig.setFocusable(false);
    // chkenginePkAutosave.setFocusable(false);
    chkenginePkContinue.setFocusable(false);
    chkenginePkBatch.setFocusable(false);
    btnEnginePkStop.setFocusable(false);

    chkShowBlack.setSelected(true);
    chkShowWhite.setSelected(true);
    chkAutoPlayBlack.setSelected(true);
    chkAutoPlayWhite.setSelected(true);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter") != null
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter").length() == 49) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("toolbar-parameter");
      if (pos.getInt(0) > 0) {
        this.txtFirstAnaMove.setText(pos.getInt(0) + "");
      }
      if (pos.getInt(1) > 0) {
        this.txtLastAnaMove.setText(pos.getInt(1) + "");
      }
      if (pos.getInt(2) > 0) {
        this.chkAnaTime.setSelected(true);
      }
      if (pos.getInt(3) > 0) {
        this.txtAnaTime.setText(pos.getInt(3) + "");
      }
      if (pos.getInt(4) > 0) {
        this.chkAnaAutoSave.setSelected(true);
      }
      if (pos.getInt(5) > 0) {
        this.chkAnaPlayouts.setSelected(true);
      }
      if (pos.getInt(6) > 0) {
        this.txtAnaPlayouts.setText(pos.getInt(6) + "");
      }
      if (pos.getInt(7) > 0) {
        this.chkAnaFirstPlayouts.setSelected(true);
      }
      if (pos.getInt(8) > 0) {
        this.txtAnaFirstPlayouts.setText(pos.getInt(8) + "");
      }
      if (pos.getInt(9) > 0) {
        this.chkAutoPlayBlack.setSelected(true);
      }
      if (pos.getInt(10) > 0) {
        this.chkAutoPlayWhite.setSelected(true);
      }
      if (pos.getInt(11) > 0) {
        this.chkAutoPlayTime.setSelected(true);
      }
      if (pos.getInt(12) > 0) {
        this.txtAutoPlayTime.setText(pos.getInt(12) + "");
      }
      if (pos.getInt(13) > 0) {
        this.chkAutoPlayPlayouts.setSelected(true);
      }
      if (pos.getInt(14) > 0) {
        this.txtAutoPlayPlayouts.setText(pos.getInt(14) + "");
      }
      if (pos.getInt(15) > 0) {
        this.chkAutoPlayFirstPlayouts.setSelected(true);
      }
      if (pos.getInt(16) > 0) {
        this.txtAutoPlayFirstPlayouts.setText(pos.getInt(16) + "");
      }

      if (pos.getInt(17) > 0) {
        this.txtenginePkFirstPlayputs.setText(pos.getInt(17) + "");
      }

      if (pos.getInt(18) > 0) {
        this.txtenginePkFirstPlayputsWhite.setText(pos.getInt(18) + "");
      }

      if (pos.getInt(19) > 0) {
        this.txtenginePkTime.setText(pos.getInt(19) + "");
      }

      if (pos.getInt(20) > 0) {
        this.txtenginePkPlayputs.setText(pos.getInt(20) + "");
      }

      if (pos.getInt(21) > 0) {
        this.txtenginePkPlayputsWhite.setText(pos.getInt(21) + "");
      }

      if (pos.getInt(22) > 0) {
        this.txtenginePkBatch.setText(pos.getInt(22) + "");
      }
      if (pos.getInt(23) > 0) {
        this.chkenginePkBatch.setSelected(true);
      }
      if (pos.getInt(24) > 0) {
        this.chkenginePkContinue.setSelected(true);
      }
      if (pos.getInt(25) > 0) {
        this.chkenginePkFirstPlayputs.setSelected(true);
      }
      if (pos.getInt(26) > 0) {
        this.chkenginePkPlayouts.setSelected(true);
      }
      if (pos.getInt(27) > 0) {
        this.chkenginePkTime.setSelected(true);
      }
      pkResginWinrate = pos.getDouble(28);
      pkResignMoveCounts = pos.getInt(29);
      AutosavePk = pos.getBoolean(30);
      isGenmove = pos.getBoolean(31);
      anaPanelOrder = pos.getInt(32);
      enginePkOrder = pos.getInt(33);
      autoPlayOrder = pos.getInt(34);
      exChange = pos.getBoolean(35);
      maxGanmeMove = pos.getInt(36);
      checkGameMaxMove = pos.getBoolean(37);
      if (pos.getInt(38) > 0) {
        txtenginePkTimeWhite.setText(pos.getInt(38) + "");
      }
      if (pos.getInt(39) > 0) {
        this.chkAutoSub.setSelected(true);
      }
      if (pos.getInt(40) > 0) {
        this.txtAutoMain.setText(pos.getInt(40) + "");
      }

      if (pos.getInt(41) > 0) {
        this.txtAutoSub.setText(pos.getInt(41) + "");
      }
      minGanmeMove = pos.getInt(42);
      checkGameMinMove = pos.getBoolean(43);
      isRandomMove = pos.getBoolean(44);
      randomMove = pos.getInt(45);
      randomDiffWinrate = pos.getDouble(46);
      chkAnaBlack.setSelected(pos.getBoolean(47));
      chkAnaWhite.setSelected(pos.getBoolean(48));
      setOrder();
    }
    if (chkAutoSub.isSelected()) {
      Runnable runnable =
          new Runnable() {
            public void run() {
              int time = -1;
              try {
                time = Integer.parseInt(txtAutoSub.getText().replace(" ", ""));
              } catch (NumberFormatException err) {
              }
              if (time <= 0) {
                chkAutoSub.setSelected(false);
                return;
              }
              while (chkAutoSub.isSelected()) {
                if (!Lizzie.frame.subBoardRenderer.wheeled)
                  Lizzie.frame.subBoardRenderer.setDisplayedBranchLength(
                      displayedSubBoardBranchLength);
                try {
                  try {
                    time = Integer.parseInt(txtAutoSub.getText().replace(" ", ""));
                  } catch (NumberFormatException err) {
                  }
                  Thread.sleep(time);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                displayedSubBoardBranchLength = displayedSubBoardBranchLength + 1;
              }
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    }
    setGenmove();
  }

  public void setGenmove() {
    if (isGenmove) {
      this.chkenginePkFirstPlayputs.setEnabled(false);
      this.chkenginePkPlayouts.setEnabled(false);
      this.txtenginePkFirstPlayputs.setEnabled(false);
      this.txtenginePkFirstPlayputsWhite.setEnabled(false);
      this.txtenginePkPlayputs.setEnabled(false);
      this.txtenginePkPlayputsWhite.setEnabled(false);
      this.btnEnginePkStop.setEnabled(false);
      this.btnEngineMannul.setEnabled(false);
    } else {
      this.chkenginePkFirstPlayputs.setEnabled(true);
      this.chkenginePkPlayouts.setEnabled(true);
      this.txtenginePkFirstPlayputs.setEnabled(true);
      this.txtenginePkFirstPlayputsWhite.setEnabled(true);
      this.txtenginePkPlayputs.setEnabled(true);
      this.txtenginePkPlayputsWhite.setEnabled(true);
      this.btnEnginePkStop.setEnabled(true);
      this.btnEngineMannul.setEnabled(true);
    }
  }

  public void setOrder() {
    if ((anaPanelOrder != enginePkOrder)
        && (anaPanelOrder != autoPlayOrder)
        && (enginePkOrder != autoPlayOrder)) {
      if ((anaPanelOrder < enginePkOrder) && (anaPanelOrder < autoPlayOrder)) {
        if (enginePkOrder < autoPlayOrder) {
          anaPanel.setBounds(0, 26, 350, 44);
          autoPlayPanel.setBounds(950, 26, 495, 44);
          enginePkPanel.setBounds(350, 26, 600, 44);
        } else {
          anaPanel.setBounds(0, 26, 350, 44);
          autoPlayPanel.setBounds(350, 26, 495, 44);
          enginePkPanel.setBounds(845, 26, 600, 44);
        }
      }

      if ((enginePkOrder < anaPanelOrder) && (enginePkOrder < autoPlayOrder)) {
        if (anaPanelOrder < autoPlayOrder) {
          anaPanel.setBounds(600, 26, 350, 44);
          autoPlayPanel.setBounds(950, 26, 495, 44);
          enginePkPanel.setBounds(0, 26, 600, 44);
        } else {
          anaPanel.setBounds(1095, 26, 350, 44);
          autoPlayPanel.setBounds(600, 26, 495, 44);
          enginePkPanel.setBounds(0, 26, 600, 44);
        }
      }

      if ((autoPlayOrder < anaPanelOrder) && (autoPlayOrder < enginePkOrder)) {
        if (anaPanelOrder < enginePkOrder) {
          anaPanel.setBounds(495, 26, 350, 44);
          autoPlayPanel.setBounds(0, 26, 495, 44);
          enginePkPanel.setBounds(845, 26, 600, 44);
        } else {
          anaPanel.setBounds(1095, 26, 350, 44);
          autoPlayPanel.setBounds(0, 26, 495, 44);
          enginePkPanel.setBounds(495, 26, 600, 44);
        }
      }
    }
  }

  public void setTxtUnfocuse() {
    if (txtMoveNumber.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
    }
    if (txtAnaTime.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaTime.setFocusable(true);
    }
    if (txtAnaPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
    }
    if (txtAnaFirstPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
    }
    if (txtLastAnaMove.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
    }
    if (txtFirstAnaMove.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
    }

    if (txtAutoPlayTime.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
    }

    if (txtAutoPlayPlayouts.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
    }

    if (txtAutoPlayFirstPlayouts.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkTime.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkPlayputs.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkFirstPlayputs.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkPlayputsWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkFirstPlayputsWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkBatch.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
    if (txtenginePkTimeWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
    if (txtAutoMain.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);

      txtAutoSub.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoMain.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);

      txtAutoSub.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
    if (txtAutoSub.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);

      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoSub.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);

      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
  }

  class UI extends javax.swing.plaf.basic.BasicComboBoxUI {
    protected javax.swing.plaf.basic.ComboPopup createPopup() {
      Popup popup = new Popup(comboBox);
      popup.getAccessibleContext().setAccessibleParent(comboBox);
      return popup;
    }

    public javax.swing.plaf.basic.ComboPopup getPopup() {
      return popup;
    }
  }

  class Popup extends javax.swing.plaf.basic.BasicComboPopup {
    public Popup(JComboBox combo) {
      super(combo);
    }

    public void setDisplaySize(int width, int height) {
      scroller.setSize(width, height);
      scroller.setPreferredSize(new Dimension(width, height));
    }

    public void show() {
      setListSelection(comboBox.getSelectedIndex());
      //  java.awt.Point location = getPopupLocation();
      show(comboBox, 0, 0);
    }

    private void setListSelection(int selectedIndex) {
      if (selectedIndex == -1) {
        list.clearSelection();
      } else {
        list.setSelectedIndex(selectedIndex);
        list.ensureIndexIsVisible(selectedIndex);
      }
    }

    //    private java.awt.Point getPopupLocation() {
    //      Dimension popupSize = comboBox.getSize();
    //      Insets insets = getInsets();
    //
    //      // reduce the width of the scrollpane by the insets so that the popup
    //      // is the same width as the combo box.
    //      popupSize.setSize(
    //          popupSize.width - (insets.right + insets.left),
    //          getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
    //      Rectangle popupBounds =
    //          computePopupBounds(0, comboBox.getBounds().height, popupSize.width,
    // popupSize.height);
    //      Dimension scrollSize = popupBounds.getSize();
    //      java.awt.Point popupLocation = popupBounds.getLocation();
    //
    //      //          scroller.setMaximumSize( scrollSize );
    //      //          scroller.setPreferredSize( scrollSize );
    //      //          scroller.setMinimumSize( scrollSize );
    //
    //      list.revalidate();
    //
    //      return popupLocation;
    //    }
  }

  public void resetAutoAna() {
    chkAnaBlack.setText("黑");
    chkAnaWhite.setText("白");
    txtFirstAnaMove.setBounds(138, 2, 30, 18);
    txtLastAnaMove.setBounds(185, 2, 30, 18);
    txtAnaFirstPlayouts.setBounds(298, 2, 45, 18);
    chkAnaWhite.setBounds(68, 22, 40, 20);
    chkAnaBlack.setBounds(68, 1, 40, 20);
    txtAnaTime.setBounds(310, 23, 33, 18);
    chkAnaAutoSave.setBounds(1, 22, 20, 20);
    txtAnaPlayouts.setBounds(175, 23, 50, 18);
    anaPanel.add(txtFirstAnaMove);
    anaPanel.add(txtLastAnaMove);
    anaPanel.add(txtAnaFirstPlayouts);
    anaPanel.add(chkAnaWhite);
    anaPanel.add(chkAnaBlack);
    anaPanel.add(txtAnaTime);
    anaPanel.add(chkAnaAutoSave);
    anaPanel.add(txtAnaPlayouts);
  }

  public void startAutoAna() {
    isAutoAna = true;
    startAutoAna = true;
    Lizzie.board.clearBoardStat();
    Lizzie.leelaz.ponder();
  }

  private void checkMove() {
    try {
      changeMoveNumber = Integer.parseInt(Lizzie.frame.toolbar.txtMoveNumber.getText());
    } catch (NumberFormatException err) {
      changeMoveNumber = 0;
    }
  }

  public void addEngineLis() {
    enginePkBlackLis =
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            engineBlack = enginePkBlack.getSelectedIndex();
            setTxtUnfocuse();
          }
        };
    enginePkWhiteLis =
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            engineWhite = enginePkWhite.getSelectedIndex();
            setTxtUnfocuse();
          }
        };
    enginePkBlack.addItemListener(enginePkBlackLis);
    enginePkWhite.addItemListener(enginePkWhiteLis);
  }

  public void removeEngineLis() {
    enginePkBlack.removeItemListener(enginePkBlackLis);
    enginePkWhite.removeItemListener(enginePkWhiteLis);
  }

  public void startEnginePk() {
    timeb = -1;
    timew = -1;
    if (isGenmove) {
      try {
        timeb = Integer.parseInt(txtenginePkTime.getText().replace(" ", ""));
      } catch (NumberFormatException err) {
      }
      try {
        timew = Integer.parseInt(txtenginePkTimeWhite.getText().replace(" ", ""));
      } catch (NumberFormatException err) {
      }
      // if (timeb <= 0 || timew <= 0) {
      // boolean onTop = false;
      // if (Lizzie.frame.isAlwaysOnTop()) {
      // Lizzie.frame.setAlwaysOnTop(false);
      // onTop = true;
      // }
      // JOptionPane.showMessageDialog(Lizzie.frame,
      // "genmove模式下必须设置黑白双方用时");
      // if (onTop) Lizzie.frame.setAlwaysOnTop(true);
      // return;
      // }
    }
    if (engineWhite == engineBlack) {
      if (isGenmove) {
        boolean onTop = false;
        if (Lizzie.frame.isAlwaysOnTop()) {
          Lizzie.frame.setAlwaysOnTop(false);
          onTop = true;
        }
        JOptionPane.showMessageDialog(null, "genmove模式下,黑白必须为不同引擎");
        if (onTop) Lizzie.frame.setAlwaysOnTop(true);
        return;
      }
      isSameEngine = true;
    } else {
      isSameEngine = false;
    }
    isAutoAna = false;
    isAutoPlay = false;
    Lizzie.board.isPkBoard = true;
    //    if (checkGameTime) {
    //      Lizzie.engineManager.gameTime = System.currentTimeMillis();
    //    }

    Lizzie.frame.isPlayingAgainstLeelaz = false;
    btnStartPk.setText("终止");
    Lizzie.frame.removeInput();
    EnginePkBatchNumberNow = 1;
    isEnginePkBatch = chkenginePkBatch.isSelected();
    if (batchPkName.equals("")) {
      batchPkName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
    SF = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    // txtenginePkBatch.setEnabled(false);

    chkenginePkBatch.setEnabled(false);
    enginePkBlack.setEnabled(false);
    enginePkWhite.setEnabled(false);
    // chkenginePkgenmove.setEnabled(false);
    chkenginePk.setEnabled(false);
    btnEnginePkConfig.setEnabled(false);
    // if (isEnginePkBatch) {
    // chkenginePkAutosave.setSelected(true);
    // chkenginePkAutosave.setEnabled(false);
    // AutosavePk=true;
    // }
    if (chkenginePkContinue.isSelected()) {
      startGame = Lizzie.board.getmovelist();
    }
    lblenginePkResult.setText("0:0");
    pkBlackWins = 0;
    pkWhiteWins = 0;
    featurecat.lizzie.gui.Menu.engineMenu.setText("对战中");
    featurecat.lizzie.gui.Menu.engineMenu.setEnabled(false);
    analyse.setEnabled(false);
    Lizzie.frame.setResult("");
    if (Lizzie.engineManager.currentEngineNo == engineWhite
        || Lizzie.engineManager.currentEngineNo == engineBlack) {
      Lizzie.leelaz.nameCmd();

    } else {
      if (!Lizzie.engineManager.isEmpty) {
        try {
          Lizzie.leelaz.normalQuit();
        } catch (Exception ex) {
        }
      } else {
        Lizzie.engineManager.switchEngine(engineBlack);
      }
    }
    if (!isGenmove) {
      // 分析模式对战
      if (checkGameMinMove) {
        minMove = minGanmeMove;
      } else minMove = -1;
      Lizzie.engineManager.engineList.get(engineBlack).blackResignMoveCounts = 0;
      Lizzie.engineManager.engineList.get(engineBlack).whiteResignMoveCounts = 0;
      Lizzie.engineManager.engineList.get(engineWhite).blackResignMoveCounts = 0;
      Lizzie.engineManager.engineList.get(engineBlack).whiteResignMoveCounts = 0;

      Lizzie.board.clearforpk();

      if (chkenginePkContinue.isSelected()) {
        Lizzie.engineManager.isEmpty = true;
        Lizzie.board.setlist(startGame);
        Lizzie.engineManager.isEmpty = false;
      }
      if (Lizzie.board.getHistory().isBlacksTurn()) {
        isEnginePk = true;
        Lizzie.engineManager.startEngineForPk(engineBlack);
        Lizzie.engineManager.startEngineForPk(engineWhite);
        Runnable runnable =
            new Runnable() {
              public void run() {
                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
                  try {
                    Thread.sleep(500);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
                Lizzie.leelaz = Lizzie.engineManager.engineList.get(engineBlack);
                Lizzie.leelaz.ponder();
              }
            };
        Thread thread = new Thread(runnable);
        thread.start();
      } else {
        isEnginePk = true;
        Lizzie.engineManager.startEngineForPk(engineWhite);
        Lizzie.engineManager.startEngineForPk(engineBlack);
        Runnable runnable =
            new Runnable() {
              public void run() {
                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
                  try {
                    Thread.sleep(500);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }

                Lizzie.leelaz = Lizzie.engineManager.engineList.get(engineWhite);
                Lizzie.leelaz.ponder();
              }
            };
        Thread thread = new Thread(runnable);
        thread.start();
      }
      Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());

      Lizzie.frame.setPlayers(
          Lizzie.engineManager.engineList.get(engineWhite).currentEnginename,
          Lizzie.engineManager.engineList.get(engineBlack).currentEnginename);
      GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
      gameInfo.setPlayerWhite(Lizzie.engineManager.engineList.get(engineWhite).currentEnginename);
      gameInfo.setPlayerBlack(Lizzie.engineManager.engineList.get(engineBlack).currentEnginename);
    } else {
      // genmove对战
      isEnginePk = true;
      chkenginePkTime.setEnabled(false);
      txtenginePkTime.setEnabled(false);
      txtenginePkTimeWhite.setEnabled(false);
      Lizzie.board.clearforpk();
      if (chkenginePkContinue.isSelected()) {
        Lizzie.engineManager.isEmpty = true;
        Lizzie.board.setlist(startGame);
        Lizzie.engineManager.isEmpty = false;
      }
      if (Lizzie.board.getHistory().isBlacksTurn()) {
        Lizzie.engineManager.startEngineForPk(engineWhite);
        Lizzie.engineManager.startEngineForPk(engineBlack);
        Runnable runnable =
            new Runnable() {
              public void run() {
                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
                  try {
                    Thread.sleep(500);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
                Lizzie.engineManager.engineList.get(engineBlack).nameCmd();
                Lizzie.engineManager.engineList.get(engineBlack).notPondering();
                Lizzie.engineManager.engineList.get(engineWhite).nameCmd();
                Lizzie.engineManager.engineList.get(engineWhite).notPondering();
                if (timew > 0)
                  Lizzie.engineManager
                      .engineList
                      .get(engineWhite)
                      .sendCommand("time_settings 0 " + timew + " 1");
                if (timeb > 0)
                  Lizzie.engineManager
                      .engineList
                      .get(engineBlack)
                      .sendCommand("time_settings 0 " + timeb + " 1");
                Lizzie.engineManager.engineList.get(engineBlack).genmoveForPk("B");
              }
            };
        Thread thread = new Thread(runnable);
        thread.start();

      } else {
        Lizzie.engineManager.startEngineForPk(engineBlack);
        Lizzie.engineManager.startEngineForPk(engineWhite);
        Runnable runnable =
            new Runnable() {
              public void run() {
                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
                  try {
                    Thread.sleep(500);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
                Lizzie.engineManager.engineList.get(engineBlack).nameCmd();
                Lizzie.engineManager.engineList.get(engineBlack).notPondering();
                Lizzie.engineManager.engineList.get(engineWhite).nameCmd();
                Lizzie.engineManager.engineList.get(engineWhite).notPondering();
                if (timew > 0)
                  Lizzie.engineManager
                      .engineList
                      .get(engineWhite)
                      .sendCommand("time_settings 0 " + timew + " 1");
                if (timeb > 0)
                  Lizzie.engineManager
                      .engineList
                      .get(engineBlack)
                      .sendCommand("time_settings 0 " + timeb + " 1");
                Lizzie.engineManager.engineList.get(engineWhite).genmoveForPk("W");
              }
            };
        Thread thread = new Thread(runnable);
        thread.start();
      }

      Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());
      Lizzie.frame.setPlayers(
          Lizzie.engineManager.engineList.get(engineWhite).currentEnginename,
          Lizzie.engineManager.engineList.get(engineBlack).currentEnginename);
      GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
      gameInfo.setPlayerWhite(Lizzie.engineManager.engineList.get(engineWhite).currentEnginename);
      gameInfo.setPlayerBlack(Lizzie.engineManager.engineList.get(engineBlack).currentEnginename);
    }
  }

  public void setButtonLocation(int boardmid) {
    savedbroadmid = boardmid;
    int w = Lizzie.frame.getWidth();

    if (Lizzie.leelaz != null && Lizzie.leelaz.isKatago) {
      if (boardmid + 364 > w) boardmid = w - 364;
      if (boardmid - 601 < 0) boardmid = 601;
      detail.setBounds(0, 0, 20, 26);
      kataEstimate.setVisible(true);
      kataEstimate.setBounds(boardmid - 582, 0, 60, 26);
      batchOpen.setBounds(boardmid - 523, 0, 60, 26);
      openfile.setBounds(boardmid - 464, 0, 40, 26);
      savefile.setBounds(boardmid - 425, 0, 40, 26);
      komi.setBounds(boardmid - 386, 0, 40, 26);
      refresh.setBounds(boardmid - 347, 0, 40, 26);
      analyse.setBounds(boardmid - 308, 0, 65, 26);
      tryPlay.setBounds(boardmid - 244, 0, 40, 26);
      setMain.setBounds(boardmid - 205, 0, 70, 26);
      backMain.setBounds(boardmid - 136, 0, 70, 26);
      firstButton.setBounds(boardmid - 67, 0, 30, 26);
      backward10.setBounds(boardmid - 38, 0, 30, 26);
      backward1.setBounds(boardmid - 9, 0, 30, 26);
      forward1.setBounds(boardmid + 20, 0, 30, 26);
      forward10.setBounds(boardmid + 49, 0, 30, 26);
      lastButton.setBounds(boardmid + 78, 0, 30, 26);
      clearButton.setBounds(boardmid + 107, 0, 60, 26);
      countButton.setBounds(boardmid + 166, 0, 60, 26);
      heatMap.setBounds(boardmid + 225, 0, 60, 26);
      txtMoveNumber.setBounds(boardmid + 285, 1, 28, 24);
      gotomove.setBounds(boardmid + 313, 0, 35, 26);
    } else {
      if (boardmid + 364 > w) boardmid = w - 364;
      if (boardmid - 542 < 0) boardmid = 542;
      detail.setBounds(0, 0, 20, 26);
      kataEstimate.setVisible(false);
      batchOpen.setBounds(boardmid - 523, 0, 60, 26);
      openfile.setBounds(boardmid - 464, 0, 40, 26);
      savefile.setBounds(boardmid - 425, 0, 40, 26);
      komi.setBounds(boardmid - 386, 0, 40, 26);
      refresh.setBounds(boardmid - 347, 0, 40, 26);
      analyse.setBounds(boardmid - 308, 0, 65, 26);
      tryPlay.setBounds(boardmid - 244, 0, 40, 26);
      setMain.setBounds(boardmid - 205, 0, 70, 26);
      backMain.setBounds(boardmid - 136, 0, 70, 26);
      firstButton.setBounds(boardmid - 67, 0, 30, 26);
      backward10.setBounds(boardmid - 38, 0, 30, 26);
      backward1.setBounds(boardmid - 9, 0, 30, 26);
      forward1.setBounds(boardmid + 20, 0, 30, 26);
      forward10.setBounds(boardmid + 49, 0, 30, 26);
      lastButton.setBounds(boardmid + 78, 0, 30, 26);
      clearButton.setBounds(boardmid + 107, 0, 60, 26);
      countButton.setBounds(boardmid + 166, 0, 60, 26);
      heatMap.setBounds(boardmid + 225, 0, 60, 26);
      txtMoveNumber.setBounds(boardmid + 285, 1, 28, 24);
      gotomove.setBounds(boardmid + 313, 0, 35, 26);
    }
  }

  public void resetEnginePk() {
    enginePkPanel.add(chkenginePkPlayouts);
    enginePkPanel.add(txtenginePkPlayputs);
    txtenginePkPlayputs.setBounds(445, 2, 50, 18);
    chkenginePkPlayouts.setBounds(355, 1, 20, 18);
    enginePkPanel.add(txtenginePkPlayputsWhite);
    txtenginePkPlayputsWhite.setBounds(513, 2, 50, 18);
    enginePkPanel.add(enginePkBlack);
    enginePkBlack.setBounds(90, 2, 95, 18);
    enginePkPanel.add(enginePkWhite);
    enginePkWhite.setBounds(255, 2, 95, 18);
    enginePkPanel.add(chkenginePkFirstPlayputs);
    enginePkPanel.add(txtenginePkFirstPlayputs);
    chkenginePkFirstPlayputs.setBounds(248, 23, 20, 18);
    enginePkPanel.add(txtenginePkFirstPlayputsWhite);
    txtenginePkFirstPlayputs.setBounds(350, 24, 50, 18);
    txtenginePkFirstPlayputsWhite.setBounds(415, 24, 50, 18);

    enginePkPanel.add(chkenginePkTime);
    enginePkPanel.add(txtenginePkTime);
    enginePkPanel.add(txtenginePkTimeWhite);
    chkenginePkTime.setBounds(110, 23, 20, 18);
    txtenginePkTime.setBounds(190, 24, 23, 18);
    txtenginePkTimeWhite.setBounds(227, 24, 23, 18);

    enginePkPanel.add(chkenginePkFirstPlayputs);
    chkenginePkFirstPlayputs.setBounds(248, 23, 20, 18);
    enginePkPanel.add(txtenginePkFirstPlayputsWhite);
    enginePkPanel.add(txtenginePkFirstPlayputs);
    txtenginePkFirstPlayputs.setBounds(350, 24, 50, 18);

    txtenginePkFirstPlayputsWhite.setBounds(415, 24, 50, 18);

    enginePkPanel.add(chkenginePkBatch);
    chkenginePkBatch.setBounds(465, 23, 20, 18);
    txtenginePkBatch.setBounds(515, 24, 30, 18);
    enginePkPanel.add(txtenginePkBatch);

    chkenginePkContinue.setBounds(545, 23, 20, 18);
    enginePkPanel.add(chkenginePkContinue);
  }

  public void reSetButtonLocation() {
    int boardmid = savedbroadmid;
    int w = Lizzie.frame.getWidth();
    if (Lizzie.leelaz != null && Lizzie.leelaz.isKatago) {
      if (boardmid + 364 > w) boardmid = w - 364;
      if (boardmid - 601 < 0) boardmid = 601;
      detail.setBounds(0, 0, 20, 26);
      kataEstimate.setVisible(true);
      kataEstimate.setBounds(boardmid - 582, 0, 60, 26);
      batchOpen.setBounds(boardmid - 523, 0, 60, 26);
      openfile.setBounds(boardmid - 464, 0, 40, 26);
      savefile.setBounds(boardmid - 425, 0, 40, 26);
      komi.setBounds(boardmid - 386, 0, 40, 26);
      refresh.setBounds(boardmid - 347, 0, 40, 26);
      analyse.setBounds(boardmid - 308, 0, 65, 26);
      tryPlay.setBounds(boardmid - 244, 0, 40, 26);
      setMain.setBounds(boardmid - 205, 0, 70, 26);
      backMain.setBounds(boardmid - 136, 0, 70, 26);
      firstButton.setBounds(boardmid - 67, 0, 30, 26);
      backward10.setBounds(boardmid - 38, 0, 30, 26);
      backward1.setBounds(boardmid - 9, 0, 30, 26);
      forward1.setBounds(boardmid + 20, 0, 30, 26);
      forward10.setBounds(boardmid + 49, 0, 30, 26);
      lastButton.setBounds(boardmid + 78, 0, 30, 26);
      clearButton.setBounds(boardmid + 107, 0, 60, 26);
      countButton.setBounds(boardmid + 166, 0, 60, 26);
      heatMap.setBounds(boardmid + 225, 0, 60, 26);
      txtMoveNumber.setBounds(boardmid + 285, 1, 28, 24);
      gotomove.setBounds(boardmid + 313, 0, 35, 26);
    } else {
      if (boardmid + 364 > w) boardmid = w - 364;
      if (boardmid - 542 < 0) boardmid = 542;
      detail.setBounds(0, 0, 20, 26);
      kataEstimate.setVisible(false);
      batchOpen.setBounds(boardmid - 523, 0, 60, 26);
      openfile.setBounds(boardmid - 464, 0, 40, 26);
      savefile.setBounds(boardmid - 425, 0, 40, 26);
      komi.setBounds(boardmid - 386, 0, 40, 26);
      refresh.setBounds(boardmid - 347, 0, 40, 26);
      analyse.setBounds(boardmid - 308, 0, 65, 26);
      tryPlay.setBounds(boardmid - 244, 0, 40, 26);
      setMain.setBounds(boardmid - 205, 0, 70, 26);
      backMain.setBounds(boardmid - 136, 0, 70, 26);
      firstButton.setBounds(boardmid - 67, 0, 30, 26);
      backward10.setBounds(boardmid - 38, 0, 30, 26);
      backward1.setBounds(boardmid - 9, 0, 30, 26);
      forward1.setBounds(boardmid + 20, 0, 30, 26);
      forward10.setBounds(boardmid + 49, 0, 30, 26);
      lastButton.setBounds(boardmid + 78, 0, 30, 26);
      clearButton.setBounds(boardmid + 107, 0, 60, 26);
      countButton.setBounds(boardmid + 166, 0, 60, 26);
      heatMap.setBounds(boardmid + 225, 0, 60, 26);
      txtMoveNumber.setBounds(boardmid + 285, 1, 28, 24);
      gotomove.setBounds(boardmid + 313, 0, 35, 26);
    }
  }
}
