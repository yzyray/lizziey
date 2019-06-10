package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.rules.Movelist;
import java.awt.Color;
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
  JButton analyse;
  JButton detail;
  JButton heatMap;
  JButton backMain;
  JButton batchOpen;
  JButton refresh;
  JTextField txtMoveNumber;
  private int changeMoveNumber = 0;
  public boolean isAutoAna = false;
  public boolean isAutoPlay = false;
  public int firstMove = -1;
  public int lastMove = -1;
  public boolean startAutoAna = false;
  public int pkBlackWins = 0;
  public int pkWhiteWins = 0;

  public boolean isEnginePk = false;
  public int engineBlack = -1;
  public int engineWhite = -1;
  public int pkResignMoveCounts = 2;
  public double pkResginWinrate = 10;
  public boolean isEnginePkBatch = false;
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

  public JCheckBox chkShowBlack;
  public JCheckBox chkShowWhite;

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
  //  public JCheckBox chkenginePkAutosave;

  public JButton btnStartPk;
  public JButton btnEnginePkConfig;
  public JButton btnEnginePkStop;

  JLabel lblenginePk;
  // JLabel lblgenmove;
  JLabel lblenginePkTime;
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
  public JTextField txtenginePkPlayputs;
  public JTextField txtenginePkFirstPlayputs;
  public JTextField txtenginePkPlayputsWhite;
  public JTextField txtenginePkFirstPlayputsWhite;
  public JTextField txtenginePkBatch;

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
    analyse = new JButton("分析|暂停");
    heatMap = new JButton("策略网络");
    backMain = new JButton("返回主分支");
    batchOpen = new JButton("批量打开");
    refresh = new JButton("刷新");
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
    add(analyse);
    add(detail);
    add(heatMap);
    add(backMain);
    add(batchOpen);
    add(refresh);
    firstButton.setFocusable(false);
    lastButton.setFocusable(false);
    clearButton.setFocusable(false);
    countButton.setFocusable(false);
    forward10.setFocusable(false);
    backward10.setFocusable(false);
    gotomove.setFocusable(false);
    openfile.setFocusable(false);
    analyse.setFocusable(false);
    forward1.setFocusable(false);
    backward1.setFocusable(false);
    savefile.setFocusable(false);
    detail.setFocusable(false);
    heatMap.setFocusable(false);
    backMain.setFocusable(false);
    batchOpen.setFocusable(false);
    refresh.setFocusable(false);

    firstButton.setMargin(new Insets(0, 0, 0, 0));
    lastButton.setMargin(new Insets(0, 0, 0, 0));
    clearButton.setMargin(new Insets(0, 0, 0, 0));
    countButton.setMargin(new Insets(0, 0, 0, 0));
    forward10.setMargin(new Insets(0, 0, 0, 0));
    backward10.setMargin(new Insets(0, 0, 0, 0));
    gotomove.setMargin(new Insets(0, 0, 0, 0));
    openfile.setMargin(new Insets(0, 0, 0, 0));
    analyse.setMargin(new Insets(0, 0, 0, 0));
    forward1.setMargin(new Insets(0, 0, 0, 0));
    backward1.setMargin(new Insets(0, 0, 0, 0));
    savefile.setMargin(new Insets(0, 0, 0, 0));
    detail.setMargin(new Insets(0, 0, 0, 0));
    heatMap.setMargin(new Insets(0, 0, 0, 0));
    backMain.setMargin(new Insets(0, 0, 0, 0));
    batchOpen.setMargin(new Insets(0, 0, 0, 0));
    refresh.setMargin(new Insets(0, 0, 0, 0));

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
          }
        });

    batchOpen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileAll();
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
              detail.setIcon(iconUp);
              Lizzie.frame.setBounds(
                  Lizzie.frame.getX(),
                  Lizzie.frame.getY(),
                  Lizzie.frame.getWidth(),
                  Lizzie.frame.getHeight() + 44);
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
              detail.setIcon(iconDown);
              Lizzie.frame.setBounds(
                  Lizzie.frame.getX(),
                  Lizzie.frame.getY(),
                  Lizzie.frame.getWidth(),
                  Lizzie.frame.getHeight() - 44);
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
            // setAllUnfocuse();
          }
        });
    forward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(10);
            //  setAllUnfocuse();
          }
        });
    backward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(10);
            //  setAllUnfocuse();
          }
        });
    forward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.redo(1);
            // setAllUnfocuse();
          }
        });
    backward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.undo(1);
            //  setAllUnfocuse();
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
    //        backward1.addActionListener(
    //            new ActionListener() {
    //              public void actionPerformed(ActionEvent e) {
    //                Input.undo(1);
    //                setAllUnfocuse();
    //              }
    //            });
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFile();
            // setAllUnfocuse();
          }
        });
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveFile();
            //  setAllUnfocuse();
          }
        });
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clear();
            //  setAllUnfocuse();
          }
        });
    lastButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.nextMove()) ;
            //   setAllUnfocuse();
          }
        });
    firstButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.previousMove()) ;
            //   setAllUnfocuse();
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
            //    setAllUnfocuse();
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
    autoPlayPanel.setBounds(950, 26, 335, 44);
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
              firstMove = Integer.parseInt(txtFirstAnaMove.getText());
            } catch (Exception ex) {
              firstMove = -1;
            }
            try {
              lastMove = Integer.parseInt(txtLastAnaMove.getText());
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
    chkAutoAnalyse.setBounds(5, 1, 20, 18);
    lblchkAutoAnalyse.setBounds(25, 0, 60, 20);

    lblAnaMove = new JLabel("手数:");
    lblAnaMove.setBounds(78, 0, 40, 20);
    anaPanel.add(lblAnaMove);

    txtFirstAnaMove = new JTextField();
    anaPanel.add(txtFirstAnaMove);
    txtFirstAnaMove.setBounds(108, 2, 37, 18);

    txtFirstAnaMove.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // 失去焦点执行的代码
            try {
              firstMove = Integer.parseInt(txtFirstAnaMove.getText());
            } catch (Exception ex) {
            }
          }

          @Override
          public void focusGained(FocusEvent e) {
            // 获得焦点执行的代码
          }
        });

    lblAnaMoveAnd = new JLabel("到");
    lblAnaMoveAnd.setBounds(147, 0, 15, 20);
    anaPanel.add(lblAnaMoveAnd);

    txtLastAnaMove = new JTextField();
    anaPanel.add(txtLastAnaMove);
    txtLastAnaMove.setBounds(162, 2, 38, 18);

    txtLastAnaMove.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // 失去焦点执行的代码
            try {
              lastMove = Integer.parseInt(txtLastAnaMove.getText());
            } catch (Exception ex) {
            }
          }

          @Override
          public void focusGained(FocusEvent e) {
            // 获得焦点执行的代码
          }
        });

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
    chkAnaTime.setBounds(205, 1, 20, 18);
    lbltxtAnaTime.setBounds(225, 0, 80, 20);
    txtAnaTime = new JTextField();
    anaPanel.add(txtAnaTime);
    txtAnaTime.setBounds(290, 2, 50, 18);

    chkAnaAutoSave = new JCheckBox();
    anaPanel.add(chkAnaAutoSave);
    chkAnaAutoSave.setBounds(5, 22, 20, 20);
    lblAnaAutoSave = new JLabel("自动保存");
    anaPanel.add(lblAnaAutoSave);
    lblAnaAutoSave.setBounds(25, 22, 50, 20);
    chkAnaAutoSave.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

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
    chkAnaPlayouts.setBounds(75, 22, 20, 20);
    lbltxtAnaPlayouts.setBounds(95, 22, 80, 20);
    txtAnaPlayouts = new JTextField();
    anaPanel.add(txtAnaPlayouts);
    txtAnaPlayouts.setBounds(150, 23, 50, 18);

    chkAnaFirstPlayouts = new JCheckBox();
    chkAnaFirstPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    lblAnaFirstPlayouts = new JLabel("首位计算量:");
    anaPanel.add(chkAnaFirstPlayouts);
    anaPanel.add(lblAnaFirstPlayouts);
    chkAnaFirstPlayouts.setBounds(205, 22, 20, 20);
    lblAnaFirstPlayouts.setBounds(225, 22, 80, 20);
    txtAnaFirstPlayouts = new JTextField();
    anaPanel.add(txtAnaFirstPlayouts);
    txtAnaFirstPlayouts.setBounds(290, 23, 50, 18);

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
    autoPlayPanel.add(lblchkShowBlack);
    autoPlayPanel.add(lblchkShowWhite);
    autoPlayPanel.add(chkShowWhite);
    autoPlayPanel.add(chkShowBlack);
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

    chkenginePk.setBounds(5, 1, 20, 18);
    lblenginePk.setBounds(25, 0, 60, 18);
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
    //  lblgenmove = new JLabel("genmove");
    //  enginePkPanel.add(chkenginePkgenmove);
    //  enginePkPanel.add(lblgenmove);
    //  chkenginePkgenmove.setBounds(70, 23, 20, 18);
    //  lblgenmove.setBounds(90, 22, 60, 18);

    //    chkenginePkgenmove.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            // TBD未完成
    //            setTxtUnfocuse();
    //            if (chkenginePkgenmove.isSelected()) {
    //              chkenginePkPlayouts.setSelected(false);
    //              chkenginePkPlayouts.setEnabled(false);
    //              chkenginePkFirstPlayputs.setSelected(false);
    //              chkenginePkFirstPlayputs.setEnabled(false);
    //            } else {
    //              chkenginePkFirstPlayputs.setEnabled(true);
    //              chkenginePkPlayouts.setEnabled(true);
    //            }
    //          }
    //        });

    btnEnginePkConfig = new JButton("设置");
    enginePkPanel.add(btnEnginePkConfig);
    btnEnginePkConfig.setBounds(67, 22, 35, 20);
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
    btnEnginePkStop.setBounds(101, 22, 35, 20);
    btnEnginePkStop.setMargin(new Insets(0, 0, 0, 0));
    btnEnginePkStop.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();
            Lizzie.leelaz.togglePonder();
            if (Lizzie.leelaz.isPondering()) btnEnginePkStop.setText("暂停");
            else btnEnginePkStop.setText("继续");
          }
        });

    chkenginePkTime = new JCheckBox();
    lblenginePkTime = new JLabel("按时间(秒):");
    txtenginePkTime = new JTextField();
    enginePkPanel.add(chkenginePkTime);
    enginePkPanel.add(lblenginePkTime);
    enginePkPanel.add(txtenginePkTime);
    chkenginePkTime.setBounds(135, 23, 20, 18);
    lblenginePkTime.setBounds(155, 22, 60, 18);
    txtenginePkTime.setBounds(218, 24, 30, 18);
    chkenginePkTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    btnStartPk = new JButton("开始对战");
    enginePkPanel.add(btnStartPk);
    btnStartPk.setBounds(8, 22, 60, 20);
    btnStartPk.setMargin(new Insets(0, 0, 0, 0));

    btnStartPk.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            if (!chkenginePk.isSelected()) {
              return;
            }
            //            if (engineWhite == engineBlack) {
            //              boolean onTop = false;
            //              if (Lizzie.frame.isAlwaysOnTop()) {
            //                Lizzie.frame.setAlwaysOnTop(false);
            //                onTop = true;
            //              }
            //              JOptionPane.showMessageDialog(Lizzie.frame, "黑白棋不可选择相同引擎");
            //              if (onTop) Lizzie.frame.setAlwaysOnTop(true);
            //              return;
            //            }
            if (!isEnginePk) {
              isAutoAna = false;
              isAutoPlay = false;
              isEnginePk = true;
              btnStartPk.setText("停止对战");
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
              //         chkenginePkgenmove.setEnabled(false);
              chkenginePk.setEnabled(false);
              btnEnginePkConfig.setEnabled(false);
              //   if (isEnginePkBatch) {
              // chkenginePkAutosave.setSelected(true);
              // chkenginePkAutosave.setEnabled(false);
              // AutosavePk=true;
              //   }
              if (chkenginePkContinue.isSelected()) {
                startGame = Lizzie.board.getmovelist();
              }
              lblenginePkResult.setText("0:0");
              pkBlackWins = 0;
              pkWhiteWins = 0;
              featurecat.lizzie.gui.Menu.engineMenu.setText("对战中");
              featurecat.lizzie.gui.Menu.engineMenu.setEnabled(false);
              if (!isGenmove) {
                // 分析模式对战
                Lizzie.engineManager.engineList.get(engineBlack).blackResignMoveCounts = 0;
                Lizzie.engineManager.engineList.get(engineBlack).whiteResignMoveCounts = 0;
                Lizzie.engineManager.engineList.get(engineWhite).blackResignMoveCounts = 0;
                Lizzie.engineManager.engineList.get(engineBlack).whiteResignMoveCounts = 0;
                Lizzie.frame.setResult("");

                Lizzie.leelaz.nameCmd();
                Lizzie.leelaz.notPondering();
                Lizzie.board.clear();
                if (chkenginePkContinue.isSelected()) {
                  Lizzie.board.setlist(startGame);
                }
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                  Lizzie.engineManager.engineList.get(engineWhite).notPondering();
                  Lizzie.engineManager.startEngineForPk(engineWhite);
                  Lizzie.engineManager.engineList.get(engineWhite).Pondering();
                  Lizzie.engineManager.startEngineForPk(engineBlack);
                } else {
                  Lizzie.engineManager.engineList.get(engineBlack).notPondering();
                  Lizzie.engineManager.startEngineForPk(engineBlack);
                  Lizzie.engineManager.engineList.get(engineBlack).Pondering();
                  Lizzie.engineManager.startEngineForPk(engineWhite);
                }
                Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());
                Lizzie.leelaz.ponder();
                Lizzie.frame.setPlayers(
                    Lizzie.engineManager.engineList.get(engineWhite).currentEnginename,
                    Lizzie.engineManager.engineList.get(engineBlack).currentEnginename);
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setPlayerWhite(
                    Lizzie.engineManager.engineList.get(engineWhite).currentEnginename);
                gameInfo.setPlayerBlack(
                    Lizzie.engineManager.engineList.get(engineBlack).currentEnginename);
                // Lizzie.board.getHistory().setGameInfo(gameinfo);
              } else {
                // genmove对战
              }
            } else {
              isEnginePk = false;
              btnStartPk.setText("开始对战");
              Lizzie.engineManager.engineList.get(engineBlack).notPondering();
              Lizzie.engineManager.engineList.get(engineBlack).nameCmd();
              Lizzie.engineManager.engineList.get(engineWhite).notPondering();
              Lizzie.engineManager.engineList.get(engineWhite).nameCmd();
              Lizzie.frame.addInput();
              enginePkBlack.setEnabled(true);
              enginePkWhite.setEnabled(true);
              // txtenginePkBatch.setEnabled(true);
              // chkenginePkAutosave.setEnabled(true);
              // AutosavePk=true;
              btnEnginePkConfig.setEnabled(true);
              chkenginePkBatch.setEnabled(true);
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
    enginePkBlack.setBounds(95, 2, 105, 18);
    lblengineBlack = new JLabel("黑:");
    enginePkPanel.add(lblengineBlack);
    lblengineBlack.setBounds(80, 0, 15, 20);

    lblenginePkResult = new JLabel("0:0");
    enginePkPanel.add(lblenginePkResult);
    lblenginePkResult.setBounds(205, 0, 45, 20);

    enginePkWhite = new JComboBox();
    addEngineLis();
    enginePkPanel.add(enginePkWhite);
    enginePkWhite.setBounds(270, 2, 105, 18);

    lblengineWhite = new JLabel("白:");
    enginePkPanel.add(lblengineWhite);
    lblengineWhite.setBounds(255, 0, 15, 20);

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
    chkenginePkPlayouts.setBounds(380, 1, 20, 18);
    lblenginePkPlayputs.setBounds(400, 0, 70, 18);
    txtenginePkPlayputs.setBounds(470, 2, 50, 18);
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
    lblenginePkPlayputsWhite.setBounds(525, 0, 15, 18);
    txtenginePkPlayputsWhite.setBounds(540, 2, 50, 18);

    //    chkenginePkAutosave = new JCheckBox();
    //    lblenginePkAutosave = new JLabel("自动保存");
    //    enginePkPanel.add(chkenginePkAutosave);
    //    enginePkPanel.add(lblenginePkAutosave);
    //    chkenginePkAutosave.setBounds(515, 1, 20, 18);
    //    lblenginePkAutosave.setBounds(535, 0, 50, 20);
    //
    //    chkenginePkAutosave.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            setTxtUnfocuse();
    //          }
    //        });

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
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter").length() == 35) {
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
    }

    //    JPanel anaPanel;
    //    JPanel autoPlayPanel;
    //    JPanel enginePkPanel;
    //

  }

  public void setOrder() {
    if ((anaPanelOrder != enginePkOrder)
        && (anaPanelOrder != autoPlayOrder)
        && (enginePkOrder != autoPlayOrder)) {
      if ((anaPanelOrder < enginePkOrder) && (anaPanelOrder < autoPlayOrder)) {
        if (enginePkOrder < autoPlayOrder) {
          anaPanel.setBounds(0, 26, 350, 44);
          autoPlayPanel.setBounds(950, 26, 335, 44);
          enginePkPanel.setBounds(350, 26, 600, 44);
        } else {
          anaPanel.setBounds(0, 26, 350, 44);
          autoPlayPanel.setBounds(350, 26, 335, 44);
          enginePkPanel.setBounds(685, 26, 600, 44);
        }
      }

      if ((enginePkOrder < anaPanelOrder) && (enginePkOrder < autoPlayOrder)) {
        if (anaPanelOrder < autoPlayOrder) {
          anaPanel.setBounds(600, 26, 350, 44);
          autoPlayPanel.setBounds(950, 26, 335, 44);
          enginePkPanel.setBounds(0, 26, 600, 44);
        } else {
          anaPanel.setBounds(935, 26, 350, 44);
          autoPlayPanel.setBounds(600, 26, 335, 44);
          enginePkPanel.setBounds(0, 26, 600, 44);
        }
      }

      if ((autoPlayOrder < anaPanelOrder) && (autoPlayOrder < enginePkOrder)) {
        if (anaPanelOrder < enginePkOrder) {
          anaPanel.setBounds(335, 26, 350, 44);
          autoPlayPanel.setBounds(0, 26, 335, 44);
          enginePkPanel.setBounds(685, 26, 600, 44);
        } else {
          anaPanel.setBounds(935, 26, 350, 44);
          autoPlayPanel.setBounds(0, 26, 335, 44);
          enginePkPanel.setBounds(335, 26, 600, 44);
        }
      }
    }
  }

  public void setTxtUnfocuse() {
    if (txtMoveNumber.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
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
      txtenginePkTime.setFocusable(true);
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
      txtAutoPlayPlayouts.setFocusable(false);
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
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaTime.setFocusable(true);
    }
    if (txtAnaPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
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
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
    }
    if (txtAnaFirstPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
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
      txtFirstAnaMove.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
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
      txtAnaPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
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
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
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
      txtenginePkBatch.setFocusable(true);
      txtenginePkTime.setFocusable(true);
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
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
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
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
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
      txtLastAnaMove.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
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
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkTime.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
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
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
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
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
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
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkPlayputsWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);

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
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);

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
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);

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
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);

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

      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
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
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);

      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
  }

  private void startAutoAna() {
    isAutoAna = true;
    startAutoAna = true;
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
            String content = enginePkBlack.getSelectedItem().toString();
            engineBlack = Integer.parseInt(content.substring(1, 2)) - 1;
            setTxtUnfocuse();
          }
        };
    enginePkWhiteLis =
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            String content = enginePkWhite.getSelectedItem().toString();
            engineWhite = Integer.parseInt(content.substring(1, 2)) - 1;
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

  public void setButtonLocation(int boardmid) {
    int w = Lizzie.frame.getWidth();

    if (boardmid + 364 > w) boardmid = w - 364;
    if (boardmid - 395 < 0) boardmid = 395;
    detail.setBounds(0, 0, 20, 26);
    batchOpen.setBounds(boardmid - 376, 0, 60, 26);
    openfile.setBounds(boardmid - 317, 0, 40, 26);
    savefile.setBounds(boardmid - 278, 0, 40, 26);
    refresh.setBounds(boardmid - 239, 0, 40, 26);
    analyse.setBounds(boardmid - 200, 0, 65, 26);
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
