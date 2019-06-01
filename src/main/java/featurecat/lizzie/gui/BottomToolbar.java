package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.InternationalFormatter;
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
  JFormattedTextField txtMoveNumber;
  private int changeMoveNumber;
  public boolean isAutoAna = false;
  public boolean isAutoPlay = false;
  public int firstMove = -1;
  public int lastMove = -1;
  public boolean startAutoAna = false;
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

  public JFormattedTextField txtAnaTime;
  public JFormattedTextField txtAnaPlayouts;
  public JFormattedTextField txtAnaFirstPlayouts;
  public JFormattedTextField txtFirstAnaMove;
  public JFormattedTextField txtLastAnaMove;

  public JFormattedTextField txtAutoPlayTime;
  public JFormattedTextField txtAutoPlayPlayouts;
  public JFormattedTextField txtAutoPlayFirstPlayouts;
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

    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    txtMoveNumber =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
              Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);
              //  setAllUnfocuse();
            }
          }

          @Override
          public void keyReleased(KeyEvent e) {}

          @Override
          public void keyTyped(KeyEvent e) {}
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
            txtMoveNumber.setFocusable(false);
            txtMoveNumber.setFocusable(true);
            txtMoveNumber.setBackground(Color.WHITE);
            txtMoveNumber.setText("");
            // Lizzie.board.savelist(changeMoveNumber);
            // Lizzie.board.setlist();
            Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);
            //  setAllUnfocuse();
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
    autoPlayPanel.setBounds(350, 26, 335, 44);
    autoPlayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

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
            }
            try {
              lastMove = Integer.parseInt(txtLastAnaMove.getText());
            } catch (Exception ex) {
            }
            isAutoAna = chkAutoAnalyse.isSelected();
            startAutoAna = chkAutoAnalyse.isSelected();
            if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
            setTxtUnfocuse();
          }
        });
    anaPanel.add(lblchkAutoAnalyse);
    chkAutoAnalyse.setBounds(5, 1, 20, 18);
    lblchkAutoAnalyse.setBounds(25, 0, 60, 20);

    lblAnaMove = new JLabel("手数:");
    lblAnaMove.setBounds(78, 0, 40, 20);
    anaPanel.add(lblAnaMove);

    txtFirstAnaMove =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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

    txtLastAnaMove =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
    txtAnaTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
    txtAnaPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
    txtAnaFirstPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
    txtAutoPlayTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
    txtAutoPlayPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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
    txtAutoPlayFirstPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
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

    chkShowBlack.setSelected(true);
    chkShowWhite.setSelected(true);
    chkAutoPlayBlack.setSelected(true);
    chkAutoPlayWhite.setSelected(true);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter") != null
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter").length() == 17) {
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
    }
  }

  public void setTxtUnfocuse() {
    if (txtMoveNumber.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtMoveNumber.setFocusable(true);
    }
    if (txtAnaTime.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
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
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaTime.setFocusable(true);
    }
    if (txtAnaPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
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
      txtAnaFirstPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
    }
    if (txtAnaFirstPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
    }
    if (txtLastAnaMove.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);

      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);

      txtFirstAnaMove.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
    }
    if (txtFirstAnaMove.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
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
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
    }

    if (txtAutoPlayTime.isFocusOwner()) {
      txtAnaTime.setFocusable(false);

      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);

      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
    }

    if (txtAutoPlayPlayouts.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);

      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);

      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
    }

    if (txtAutoPlayFirstPlayouts.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);

      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);

      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
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
  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  private void checkMove() {

    changeMoveNumber = txtFieldValue(txtMoveNumber);
  }

  public void setButtonLocation(int boardmid) {
    int w = Lizzie.frame.getWidth();
    if (boardmid - 303 < 19) boardmid = 322;
    if (boardmid + 484 > w && (w - 789 > 0)) boardmid = w - 484;
    detail.setBounds(0, 0, 20, 26);
    forward1.setBounds(boardmid + 21, 0, 38, 26);
    backward1.setBounds(boardmid - 16, 0, 38, 26);
    openfile.setBounds(boardmid - 303, 0, 56, 26);
    txtMoveNumber.setBounds(boardmid + 305, 1, 28, 24);
    gotomove.setBounds(boardmid + 333, 0, 56, 26);
    heatMap.setBounds(boardmid + 388, 0, 80, 26);
    backward10.setBounds(boardmid - 60, 0, 45, 26);
    forward10.setBounds(boardmid + 58, 0, 45, 26);
    firstButton.setBounds(boardmid - 104, 0, 45, 26);
    lastButton.setBounds(boardmid + 102, 0, 45, 26);
    analyse.setBounds(boardmid - 193, 0, 90, 26);
    savefile.setBounds(boardmid - 248, 0, 56, 26);
    clearButton.setBounds(boardmid + 146, 0, 80, 26);
    countButton.setBounds(boardmid + 225, 0, 80, 26);
  }
}
