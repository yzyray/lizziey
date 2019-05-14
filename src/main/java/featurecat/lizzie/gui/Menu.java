package featurecat.lizzie.gui;

import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Menu extends JDialog {

  final ButtonGroup buttonGroup = new ButtonGroup();

  Font headFont;
  public static ImageIcon icon;
  public static ImageIcon ready;
  public static JMenuItem engine1 = null;
  public static JMenuItem engine2 = null;
  public static JMenuItem engine3 = null;
  public static JMenuItem engine4 = null;
  public static JMenuItem engine5 = null;
  public static JMenuItem engine6 = null;
  public static JMenuItem engine7 = null;
  public static JMenuItem engine8 = null;
  public static JMenuItem engine9 = null;
  public static JMenuItem engine10 = null;

  public Menu(Window owner) {
    super(owner);
    setLayout(null);
    setUndecorated(true);
    setResizable(true);
    setBounds(
        Lizzie.frame.getX() + Lizzie.frame.getInsets().left,
        Lizzie.frame.getY() + Lizzie.frame.getInsets().top,
        Lizzie.frame.getContentPane().getWidth(),
        25);
    Color hsbColor =
        Color.getHSBColor(
            Color.RGBtoHSB(232, 232, 232, null)[0],
            Color.RGBtoHSB(232, 232, 232, null)[1],
            Color.RGBtoHSB(232, 232, 232, null)[2]);
    this.getContentPane().setBackground(hsbColor);
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBorder(new EmptyBorder(0, 0, -5, -5));
    final MenuBar menuBar = new MenuBar();
    bar.setBounds(0, 0, 450, 25);

    menuBar.setColor(hsbColor);
    this.add(bar);
    bar.add(menuBar);

    headFont = new Font("幼圆", Font.BOLD, 17);

    final JMenu fileMenu = new JMenu(" 文件  "); // 创建“文件”菜单
    // fileMenu.setMnemonic('F'); // 设置快捷键
    fileMenu.setForeground(Color.BLACK);
    fileMenu.setFont(headFont);
    menuBar.add(fileMenu); // 添加到菜单栏
    final JMenuItem openItem = new JMenuItem("打开（CTRL+O）"); // 创建菜单项
    // openItem.setMnemonic('O'); // 设置快捷键
    // 设置加速器为“Ctrl+N”
    // openItem.setAccelerator(KeyStroke.getKeyStroke(VK_O, CTRL_MASK));
    openItem.addActionListener(new ItemListener()); // 添加动作监听器
    fileMenu.add(openItem); // 添加到“文件”菜单
    //    final JMenu openMenu = new JMenu("保存（S）"); // 创建“打开”子菜单
    //    openMenu.setMnemonic('S'); // 设置快捷键
    //    fileMenu.add(openMenu); // 添加到“文件”菜单
    //    // 创建子菜单项
    //    final JMenuItem openNewItem = new JMenuItem("未打开过的（N）");
    //    openNewItem.setMnemonic('N'); // 设置快捷键
    //    // 设置加速器为“Ctrl+Alt+N”
    //    openNewItem.setAccelerator(KeyStroke.getKeyStroke(VK_N, CTRL_MASK | ALT_MASK));
    //    openNewItem.addActionListener(new ItemListener()); // 添加动作监听器
    //    openMenu.add(openNewItem); // 添加到“打开”子菜单
    //    // 创建子菜单项
    //    final JMenuItem openClosedItem = new JMenuItem("刚打开过的（C）");
    //    openClosedItem.setMnemonic('C'); // 设置快捷键
    // 设置加速器
    //    openClosedItem.setAccelerator(KeyStroke.getKeyStroke(VK_C, CTRL_MASK | ALT_MASK));
    //    openClosedItem.setEnabled(false); // 禁用菜单项
    //    // 添加动作监听器
    //    openClosedItem.addActionListener(new ItemListener());
    //    openMenu.add(openClosedItem); // 添加到“打开”子菜单
    // fileMenu.addSeparator(); // 添加分隔线

    final JMenuItem saveItem = new JMenuItem();
    saveItem.setText("保存（CTRL+S）");
    // saveItem.setMnemonic('S');
    // saveItem.setAccelerator(KeyStroke.getKeyStroke(VK_S, CTRL_MASK));
    saveItem.addActionListener(new ItemListener());

    fileMenu.add(saveItem);

    fileMenu.addSeparator();

    final JMenuItem exitItem = new JMenuItem();
    exitItem.setText("退出");
    // exitItem.setMnemonic('E');
    // exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
    exitItem.addActionListener(new ItemListener());
    fileMenu.add(exitItem);

    final JMenu viewMenu = new JMenu();
    viewMenu.setText(" 显示  ");
    // editMenu.setMnemonic('E');
    menuBar.add(viewMenu);
    viewMenu.setForeground(Color.BLACK);
    viewMenu.setFont(headFont);

    final JMenuItem leftItem = new JMenuItem();
    leftItem.setText("棋盘左移（[）");
    leftItem.addActionListener(new ItemListener());
    viewMenu.add(leftItem);

    final JMenuItem rightItem = new JMenuItem();
    rightItem.setText("棋盘右移（]）");
    rightItem.addActionListener(new ItemListener());
    viewMenu.add(rightItem);

    final JMenuItem coordsItem = new JMenuItem();
    coordsItem.setText("坐标（C）");
    coordsItem.addActionListener(new ItemListener());
    viewMenu.add(coordsItem);

    final JMenuItem subItem = new JMenuItem();
    // copyItem.setIcon(icon);
    subItem.setText("放大小棋盘（ALT+V）");
    //   copyItem.setMnemonic('C');
    //   copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
    subItem.addActionListener(new ItemListener());
    viewMenu.add(subItem);

    final JMenuItem heatItem = new JMenuItem();
    //   pastItem.setIcon(icon);
    heatItem.setText("策略网络（H）");
    //   pastItem.setMnemonic('P');
    //  pastItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
    heatItem.addActionListener(new ItemListener());
    viewMenu.add(heatItem);
    viewMenu.addSeparator();
    //  editMenu.addSeparator();
    //  editMenu.insertSeparator(2);
    final JMenuItem winratetMenu = new JMenuItem("胜率面板(W)"); // 创建“字体”子菜单
    viewMenu.add(winratetMenu); // 添加到“编辑”菜单
    winratetMenu.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem commitMenu = new JMenuItem("评论面板"); // 创建“字体”子菜单
    viewMenu.add(commitMenu); // 添加到“编辑”菜单
    commitMenu.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem gtpMenu = new JMenuItem("命令窗口"); // 创建“字体”子菜单
    viewMenu.add(gtpMenu); // 添加到“编辑”菜单
    gtpMenu.addActionListener(new ItemListener()); // 添加动作监听器

    //   fontMenu.setIcon(icon); // 设置菜单图标
    //   fontMenu.setMnemonic('F'); // 设置快捷键
    //  final JCheckBoxMenuItem bCheckBoxItem = new JCheckBoxMenuItem("加粗（B）"); // 创建复选框菜单项
    //   bCheckBoxItem.setMnemonic('B'); // 设置快捷键
    //   bCheckBoxItem.setAccelerator(
    //       KeyStroke.getKeyStroke(VK_B, CTRL_MASK | ALT_MASK)); // 设置加速器为“Ctrl+Alt+B”
    // fontMenu.add(fontMenu);
    //  fontMenu.add(bCheckBoxItem); // 添加到“字体”子菜单
    //  final JCheckBoxMenuItem iCheckBoxItem = new JCheckBoxMenuItem("斜体（I）"); // 创建复选框菜单项
    //   iCheckBoxItem.setMnemonic('I'); // 设置快捷键
    //   iCheckBoxItem.setAccelerator(
    //      KeyStroke.getKeyStroke(VK_I, CTRL_MASK | ALT_MASK)); // 设置加速器为“Ctrl+Alt+I”
    // iCheckBoxItem.addActionListener(new ItemListener()); // 添加动作监听器
    //  fontMenu.add(iCheckBoxItem); // 添加到“字体”子菜单

    final JMenu windowMenu = new JMenu("窗口"); // 创建“属性”子菜单
    //   attributeMenu.setIcon(icon); // 设置菜单图标
    // attributeMenu.setMnemonic('A'); // 设置快捷键
    viewMenu.add(windowMenu); // 添加到“编辑”菜单
    final JMenuItem badmovesItem = new JMenuItem("恶手列表(B)"); // 创建单选按钮菜单项
    //  rRadioButtonItem.setMnemonic('R'); // 设置快捷键
    //    rRadioButtonItem.setAccelerator(
    //        KeyStroke.getKeyStroke(VK_R, CTRL_MASK | ALT_MASK)); // 设置加速器为“Ctrl+Alt+R”
    buttonGroup.add(badmovesItem); // 添加到按钮组
    badmovesItem.setSelected(true); // 设置为被选中
    badmovesItem.addActionListener(new ItemListener()); // 添加动作监听器
    windowMenu.add(badmovesItem); // 添加到“属性”子菜单
    final JMenuItem leelasu = new JMenuItem("AI选点列表(U)"); // 创建单选按钮菜单项
    //  eRadioButtonItem.setMnemonic('E'); // 设置快捷键
    //  eRadioButtonItem.setAccelerator(
    //      KeyStroke.getKeyStroke(VK_E, CTRL_MASK | ALT_MASK)); // 设置加速器为“Ctrl+Alt+E”
    buttonGroup.add(leelasu); // 添加到按钮组
    leelasu.addActionListener(new ItemListener()); // 添加动作监听器
    windowMenu.add(leelasu); // 添加到“属性”子菜单

    final JMenu gameMenu = new JMenu("棋局 ", false);
    gameMenu.setText(" 棋局  ");
    // helpMenu.setMnemonic('H');
    gameMenu.setForeground(Color.BLACK);
    gameMenu.setFont(headFont);
    menuBar.add(gameMenu);

    final JMenuItem newGameItem = new JMenuItem();
    newGameItem.setText("新的一局（N）");
    // aboutItem.setMnemonic('A');
    newGameItem.addActionListener(new ItemListener());
    gameMenu.add(newGameItem);

    final JMenuItem continueGameItem = new JMenuItem();
    continueGameItem.setText("续弈（Enter）");
    // aboutItem.setMnemonic('A');
    continueGameItem.addActionListener(new ItemListener());
    gameMenu.add(continueGameItem);

    final JMenuItem countsItem = new JMenuItem();
    countsItem.setText("形势判断（.）");
    // aboutItem.setMnemonic('A');
    countsItem.addActionListener(new ItemListener());
    gameMenu.add(countsItem);

    final JMenuItem anaItem = new JMenuItem();
    anaItem.setText("分析/停止对局（空格）");
    // aboutItem.setMnemonic('A');
    anaItem.addActionListener(new ItemListener());
    gameMenu.add(anaItem);

    final JMenu editMenu = new JMenu("编辑 ", false);
    editMenu.setText(" 编辑  ");
    // helpMenu.setMnemonic('H');
    editMenu.setForeground(Color.BLACK);
    editMenu.setFont(headFont);
    menuBar.add(editMenu);

    ImageIcon iconblack = new ImageIcon();
    try {
      iconblack.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallblack.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon iconwhite = new ImageIcon();
    try {
      iconwhite.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon iconbh = new ImageIcon();
    try {
      iconbh.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/hb.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    final JMenuItem blackItem = new JMenuItem();
    blackItem.setText("落黑子");
    // aboutItem.setMnemonic('A');
    blackItem.addActionListener(new ItemListener());
    editMenu.add(blackItem);
    blackItem.setIcon(iconblack);

    final JMenuItem whiteItem = new JMenuItem();
    whiteItem.setText("落白子");
    // aboutItem.setMnemonic('A');
    whiteItem.addActionListener(new ItemListener());
    editMenu.add(whiteItem);
    whiteItem.setIcon(iconwhite);

    final JMenuItem bhItem = new JMenuItem();
    bhItem.setText("交替落子");
    // aboutItem.setMnemonic('A');
    bhItem.addActionListener(new ItemListener());
    editMenu.add(bhItem);
    bhItem.setIcon(iconbh);
    editMenu.addSeparator();

    final JMenuItem insertbItem = new JMenuItem();
    insertbItem.setText("插入黑子");
    // aboutItem.setMnemonic('A');
    insertbItem.addActionListener(new ItemListener());
    editMenu.add(insertbItem);

    final JMenuItem insertwItem = new JMenuItem();
    insertwItem.setText("插入白子");
    // aboutItem.setMnemonic('A');
    insertwItem.addActionListener(new ItemListener());
    editMenu.add(insertwItem);

    final JMenuItem bhisItem = new JMenuItem();
    bhisItem.setText("交替插入棋子");
    // aboutItem.setMnemonic('A');
    bhisItem.addActionListener(new ItemListener());
    editMenu.add(bhisItem);

    final JMenu engineMenu = new JMenu("引擎 ", false);
    engineMenu.setText(" 引擎  ");
    // helpMenu.setMnemonic('H');
    engineMenu.setForeground(Color.BLACK);
    engineMenu.setFont(headFont);
    menuBar.add(engineMenu);

    icon = new ImageIcon();
    try {
      icon.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/run.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ready = new ImageIcon();
    try {
      ready.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    engine1 = new JMenuItem();

    engine1.setText("引擎1:  " + Lizzie.config.leelazConfig.optString("enginename1", ""));

    engine1.addActionListener(new ItemListener());
    engineMenu.add(engine1);

    engine2 = new JMenuItem();

    engine2.setText("引擎2:  " + Lizzie.config.leelazConfig.optString("enginename2", ""));

    engine2.addActionListener(new ItemListener());
    engineMenu.add(engine2);

    engine3 = new JMenuItem();

    engine3.setText("引擎3:  " + Lizzie.config.leelazConfig.optString("enginename3", ""));

    engine3.addActionListener(new ItemListener());
    engineMenu.add(engine3);

    engine4 = new JMenuItem();

    engine4.setText("引擎4:  " + Lizzie.config.leelazConfig.optString("enginename4", ""));

    engine4.addActionListener(new ItemListener());
    engineMenu.add(engine4);

    engine5 = new JMenuItem();

    engine5.setText("引擎5:  " + Lizzie.config.leelazConfig.optString("enginename5", ""));

    engine5.addActionListener(new ItemListener());
    engineMenu.add(engine5);

    engine6 = new JMenuItem();

    engine6.setText("引擎6:  " + Lizzie.config.leelazConfig.optString("enginename6", ""));

    engine6.addActionListener(new ItemListener());
    engineMenu.add(engine6);

    engine7 = new JMenuItem();

    engine7.setText("引擎7:  " + Lizzie.config.leelazConfig.optString("enginename7", ""));

    engine7.addActionListener(new ItemListener());
    engineMenu.add(engine7);

    engine8 = new JMenuItem();

    engine8.setText("引擎8:  " + Lizzie.config.leelazConfig.optString("enginename8", ""));

    engine8.addActionListener(new ItemListener());
    engineMenu.add(engine8);

    engine9 = new JMenuItem();

    engine9.setText("引擎9:  " + Lizzie.config.leelazConfig.optString("enginename9", ""));

    engine9.addActionListener(new ItemListener());
    engineMenu.add(engine9);

    engine10 = new JMenuItem();

    engine10.setText("引擎10:  " + Lizzie.config.leelazConfig.optString("enginename10", ""));

    engine10.addActionListener(new ItemListener());
    engineMenu.add(engine10);

    engineMenu.addSeparator();
    final JMenuItem closeall = new JMenuItem();
    closeall.setText("关闭所有引擎");
    // aboutItem.setMnemonic('A');
    closeall.addActionListener(new ItemListener());
    engineMenu.add(closeall);

    final JMenuItem closeother = new JMenuItem();
    closeother.setText("关闭当前以外引擎");
    // aboutItem.setMnemonic('A');
    closeother.addActionListener(new ItemListener());
    engineMenu.add(closeother);

    engineMenu.addSeparator();
    final JMenuItem config = new JMenuItem();
    config.setText("设置");
    // aboutItem.setMnemonic('A');
    config.addActionListener(new ItemListener());
    engineMenu.add(config);
  }

  class ItemListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JMenuItem menuItem = (JMenuItem) e.getSource();
      System.out.println("您单击的是菜单项：" + menuItem.getText());
      if (menuItem.getText().startsWith("打开")) {
        Lizzie.frame.openFile();
      }
      if (menuItem.getText().startsWith("保存")) {
        Lizzie.frame.saveFile();
      }
      if (menuItem.getText().startsWith("退出")) {
        System.exit(0);
      }
      if (menuItem.getText().startsWith("坐标")) {
        Lizzie.config.toggleCoordinates();
      }
      if (menuItem.getText().startsWith("放大")) {
        Lizzie.config.toggleLargeSubBoard();
      }
      if (menuItem.getText().startsWith("评论")) {
        Lizzie.config.toggleShowComment();
      }
      if (menuItem.getText().startsWith("恶手")) {
        Lizzie.frame.toggleBadMoves();
      }
      if (menuItem.getText().startsWith("AI选点")) {
        Lizzie.frame.toggleBestMoves();
      }
      if (menuItem.getText().startsWith("策略")) {
        Lizzie.frame.toggleheatmap();
      }
      if (menuItem.getText().startsWith("胜率")) {
        Lizzie.config.toggleShowWinrate();
      }
      if (menuItem.getText().startsWith("命令")) {
        Lizzie.frame.toggleGtpConsole();
      }
      if (menuItem.getText().startsWith("棋盘左")) {
        if (Lizzie.frame.BoardPositionProportion > 0) Lizzie.frame.BoardPositionProportion--;
      }
      if (menuItem.getText().startsWith("棋盘右")) {
        if (Lizzie.frame.BoardPositionProportion < 8) Lizzie.frame.BoardPositionProportion++;
      }
      if (menuItem.getText().startsWith("新的")) {
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        LizzieFrame.startNewGame();
      }
      if (menuItem.getText().startsWith("续弈")) {
        if (!Lizzie.leelaz.isThinking) {
          Lizzie.leelaz.sendCommand(
              "time_settings 0 "
                  + Lizzie.config
                      .config
                      .getJSONObject("leelaz")
                      .getInt("max-game-thinking-time-seconds")
                  + " 1");
          Lizzie.frame.playerIsBlack = !Lizzie.board.getData().blackToPlay;
          Lizzie.frame.isPlayingAgainstLeelaz = true;
          Lizzie.leelaz.genmove((Lizzie.board.getData().blackToPlay ? "B" : "W"));
        }
      }
      if (menuItem.getText().startsWith("形势")) {
        Lizzie.frame.countstones();
      }
      if (menuItem.getText().startsWith("分析")) {
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
        }
        Lizzie.leelaz.togglePonder();
      }

      if (menuItem.getText().startsWith("引擎1")) {
        Lizzie.switchEngine(0);
      }
      if (menuItem.getText().startsWith("引擎2")) {
        Lizzie.switchEngine(1);
      }
      if (menuItem.getText().startsWith("引擎3")) {
        Lizzie.switchEngine(2);
      }
      if (menuItem.getText().startsWith("引擎4")) {
        Lizzie.switchEngine(3);
      }
      if (menuItem.getText().startsWith("引擎5")) {
        Lizzie.switchEngine(4);
      }
      if (menuItem.getText().startsWith("引擎6")) {
        Lizzie.switchEngine(5);
      }
      if (menuItem.getText().startsWith("引擎7")) {
        Lizzie.switchEngine(6);
      }
      if (menuItem.getText().startsWith("引擎8")) {
        Lizzie.switchEngine(7);
      }
      if (menuItem.getText().startsWith("引擎9")) {
        Lizzie.switchEngine(8);
      }
      if (menuItem.getText().startsWith("引擎10")) {
        Lizzie.switchEngine(9);
      }
      if (menuItem.getText().startsWith("交替落")) {
        featurecat.lizzie.gui.Input.insert = 0;
        Lizzie.frame.blackorwhite = 0;
      }
      if (menuItem.getText().startsWith("插入黑")) {
        featurecat.lizzie.gui.Input.insert = 1;
      }
      if (menuItem.getText().startsWith("插入白")) {
        featurecat.lizzie.gui.Input.insert = 2;
      }
      if (menuItem.getText().startsWith("落黑")) {
        Lizzie.frame.blackorwhite = 1;
      }
      if (menuItem.getText().startsWith("落白")) {
        Lizzie.frame.blackorwhite = 2;
      }

      if (menuItem.getText().startsWith("设置")) {
        Lizzie.frame.openConfigDialog();
      }
      if (menuItem.getText().startsWith("关闭所有")) {
        try {
          Lizzie.leelaz.killAllEngines();
        } catch (Exception ex) {

        }
        engine1.setIcon(null);
        engine2.setIcon(null);
        engine3.setIcon(null);
        engine4.setIcon(null);
        engine5.setIcon(null);
        engine6.setIcon(null);
        engine7.setIcon(null);
        engine8.setIcon(null);
        engine9.setIcon(null);
        engine10.setIcon(null);
      }
      if (menuItem.getText().startsWith("关闭当前")) {
        try {
          Lizzie.leelaz.killOtherEngines();
        } catch (Exception ex) {

        }

        engine1.setIcon(null);
        engine2.setIcon(null);
        engine3.setIcon(null);
        engine4.setIcon(null);
        engine5.setIcon(null);
        engine6.setIcon(null);
        engine7.setIcon(null);
        engine8.setIcon(null);
        engine9.setIcon(null);
        engine10.setIcon(null);
        switch (Lizzie.leelaz.currentEngineN()) {
          case 0:
            engine1.setIcon(icon);
            break;
          case 1:
            engine2.setIcon(icon);
            break;
          case 2:
            engine3.setIcon(icon);
            break;
          case 3:
            engine4.setIcon(icon);
            break;
          case 4:
            engine5.setIcon(icon);
            break;
          case 5:
            engine6.setIcon(icon);
            break;
          case 6:
            engine7.setIcon(icon);
            break;
          case 7:
            engine8.setIcon(icon);
            break;
          case 8:
            engine9.setIcon(icon);
            break;
          case 9:
            engine10.setIcon(icon);
            break;
        }
      }
    }
  }
}
