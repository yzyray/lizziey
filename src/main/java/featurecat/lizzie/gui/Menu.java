package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.json.JSONArray;

public class Menu extends MenuBar {

  final ButtonGroup buttonGroup = new ButtonGroup();

  Font headFont;
  public static ImageIcon icon;
  public static ImageIcon stop;
  public static ImageIcon ready;
  public static JMenuItem[] engine = new JMenuItem[10];
  public static JMenu engineMenu;
  // private boolean onlyboard = false;

  public Menu() {
    //  super(owner);
    // setLayout(null);
    // setUndecorated(true);
    // setResizable(true);
    //    setBounds(
    //        Lizzie.frame.getX() + Lizzie.frame.getInsets().left,
    //        Lizzie.frame.getY() + Lizzie.frame.getInsets().top,
    //        Lizzie.frame.getContentPane().getWidth(),
    //        25);
    Color hsbColor =
        Color.getHSBColor(
            Color.RGBtoHSB(232, 232, 232, null)[0],
            Color.RGBtoHSB(232, 232, 232, null)[1],
            Color.RGBtoHSB(232, 232, 232, null)[2]);
    this.setBackground(hsbColor);
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBorder(new EmptyBorder(0, 0, -1, -1));
    final MenuBar menuBar = new MenuBar();
    bar.setBounds(0, 0, 450, 15);

    menuBar.setColor(hsbColor);
    this.add(bar);
    bar.add(menuBar);

    headFont = new Font("幼圆", Font.BOLD, 15);
    //  onlyboard = Lizzie.config.uiConfig.optBoolean("only-board", false);

    final JMenu fileMenu = new JMenu(" 文件  "); // 创建“文件”菜单
    // fileMenu.setMnemonic('F'); // 设置快捷键
    fileMenu.setForeground(Color.BLACK);
    fileMenu.setFont(headFont);

    menuBar.add(fileMenu);
    final JMenuItem openItem = new JMenuItem("打开棋谱（O）");
    openItem.addActionListener(new ItemListener());
    fileMenu.add(openItem);

    menuBar.add(fileMenu);
    final JMenuItem batchfile = new JMenuItem("批量分析棋谱（ALT+O）");
    batchfile.addActionListener(new ItemListener());
    fileMenu.add(batchfile);

    final JMenuItem openUrlItem = new JMenuItem("打开在线棋谱（Q）");
    openUrlItem.addActionListener(new ItemListener());
    fileMenu.add(openUrlItem);

    final JMenuItem saveItem = new JMenuItem();
    saveItem.setText("保存棋谱（S）");
    saveItem.addActionListener(new ItemListener());
    fileMenu.add(saveItem);

    fileMenu.addSeparator();
    final JMenuItem copyItem = new JMenuItem();
    copyItem.setText("复制到剪贴板（CTRL+C）");
    copyItem.addActionListener(new ItemListener());
    fileMenu.add(copyItem);

    final JMenuItem pasteItem = new JMenuItem();
    pasteItem.setText("从剪贴板粘贴（CTRL+V）");
    pasteItem.addActionListener(new ItemListener());
    fileMenu.add(pasteItem);
    fileMenu.addSeparator();

    final JMenuItem resume = new JMenuItem();
    resume.setText("打开关闭前的棋谱或自动保存的棋谱");
    resume.addActionListener(new ItemListener());
    fileMenu.add(resume);

    final JMenuItem resumeItem = new JMenuItem();
    resumeItem.setText("打开自动保存棋谱(10秒一次)");
    resumeItem.addActionListener(new ItemListener());
    fileMenu.add(resumeItem);

    final JMenuItem resumeItem2 = new JMenuItem();
    resumeItem2.setText("关闭自动保存棋谱");
    resumeItem2.addActionListener(new ItemListener());
    fileMenu.add(resumeItem2);

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

    final JMenu winrate = new JMenu("胜率");
    viewMenu.add(winrate);

    final JMenuItem alwaysBlack = new JMenuItem();
    alwaysBlack.setText("切换总是显示黑胜率");
    alwaysBlack.addActionListener(new ItemListener());
    winrate.add(alwaysBlack);

    final JMenuItem winratemode1 = new JMenuItem();
    winratemode1.setText("显示双方胜率图");
    winratemode1.addActionListener(new ItemListener());
    winrate.add(winratemode1);

    final JMenuItem winratemode0 = new JMenuItem();
    winratemode0.setText("显示黑方胜率图");
    winratemode0.addActionListener(new ItemListener());
    winrate.add(winratemode0);

    final JMenuItem blunder = new JMenuItem();
    blunder.setText("显示/关闭柱状失误条");
    blunder.addActionListener(new ItemListener());
    winrate.add(blunder);

    final JMenuItem coordsItem = new JMenuItem();
    coordsItem.setText("坐标（C）");
    coordsItem.addActionListener(new ItemListener());
    viewMenu.add(coordsItem);

    final JMenu moveMenu = new JMenu("手数(M)");
    viewMenu.add(moveMenu);

    final JMenuItem noItem = new JMenuItem();
    noItem.setText("不显示");
    noItem.addActionListener(new ItemListener());
    moveMenu.add(noItem);

    final JMenuItem oneItem = new JMenuItem();
    oneItem.setText("最近1手");
    oneItem.addActionListener(new ItemListener());
    moveMenu.add(oneItem);

    final JMenuItem fiveItem = new JMenuItem();
    fiveItem.setText("最近5手");
    fiveItem.addActionListener(new ItemListener());
    moveMenu.add(fiveItem);

    final JMenuItem tenItem = new JMenuItem();
    tenItem.setText("最近10手");
    tenItem.addActionListener(new ItemListener());
    moveMenu.add(tenItem);

    final JMenuItem allItem = new JMenuItem();
    allItem.setText("全部");
    allItem.addActionListener(new ItemListener());
    moveMenu.add(allItem);

    final JMenuItem anymove = new JMenuItem();
    anymove.setText("自定义");
    anymove.addActionListener(new ItemListener());
    moveMenu.add(anymove);

    final JMenuItem subItem = new JMenuItem();
    subItem.setText("放大小棋盘（ALT+V）");
    subItem.addActionListener(new ItemListener());
    viewMenu.add(subItem);

    final JMenuItem largewin = new JMenuItem();
    largewin.setText("放大胜率图(Ctrl+W)");
    largewin.addActionListener(new ItemListener());
    viewMenu.add(largewin);

    final JMenuItem appentComment = new JMenuItem();
    appentComment.setText("记录胜率到评论中");
    appentComment.addActionListener(new ItemListener());
    viewMenu.add(appentComment);

    final JMenuItem alwaysontop = new JMenuItem();
    alwaysontop.setText("总在最前(Q)");
    alwaysontop.addActionListener(new ItemListener());
    viewMenu.add(alwaysontop);
    viewMenu.addSeparator();
    final JMenuItem toolMenu = new JMenuItem("底部工具栏"); // 创建“字体”子菜单
    viewMenu.add(toolMenu); // 添加到“编辑”菜单
    toolMenu.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem bigtoolMenu = new JMenuItem("详细工具栏");
    viewMenu.add(bigtoolMenu);
    bigtoolMenu.addActionListener(new ItemListener());

    final JMenuItem bigtoolConf = new JMenuItem("设置详细工具栏顺序");
    viewMenu.add(bigtoolConf);
    bigtoolConf.addActionListener(new ItemListener());

    final JMenuItem closeTool = new JMenuItem("关闭工具栏"); // 创建“字体”子菜单
    viewMenu.add(closeTool); // 添加到“编辑”菜单
    closeTool.addActionListener(new ItemListener()); // 添加动作监听器     viewMenu.addSeparator();
    viewMenu.addSeparator();

    final JMenuItem subboard = new JMenuItem("小棋盘"); // 创建“字体”子菜单
    viewMenu.add(subboard); // 添加到“编辑”菜单
    subboard.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem winratetMenu = new JMenuItem("胜率面板(W)"); // 创建“字体”子菜单
    viewMenu.add(winratetMenu); // 添加到“编辑”菜单
    winratetMenu.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem commitMenu = new JMenuItem("评论面板(T)"); // 创建“字体”子菜单
    viewMenu.add(commitMenu); // 添加到“编辑”菜单
    commitMenu.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem branch = new JMenuItem("分支面板(G)"); // 创建“字体”子菜单
    viewMenu.add(branch); // 添加到“编辑”菜单
    branch.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem topleft = new JMenuItem("左上角面板"); // 创建“字体”子菜单
    viewMenu.add(topleft); // 添加到“编辑”菜单
    topleft.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem bottomleft = new JMenuItem("左下角状态"); // 创建“字体”子菜单
    viewMenu.add(bottomleft); // 添加到“编辑”菜单
    bottomleft.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem gtpMenu = new JMenuItem("命令窗口(E)"); // 创建“字体”子菜单
    viewMenu.add(gtpMenu); // 添加到“编辑”菜单
    gtpMenu.addActionListener(new ItemListener()); // 添加动作监听器
    viewMenu.addSeparator();

    final JMenuItem defview = new JMenuItem("默认模式"); // 创建“字体”子菜单
    viewMenu.add(defview); // 添加到“编辑”菜单
    defview.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem claview = new JMenuItem("经典模式"); // 创建“字体”子菜单
    viewMenu.add(claview); // 添加到“编辑”菜单
    claview.addActionListener(new ItemListener()); // 添加动作监听器

    final JMenuItem allview = new JMenuItem("精简模式");
    viewMenu.add(allview);
    allview.addActionListener(new ItemListener());
    viewMenu.addSeparator();

    final JMenu katasugg = new JMenu("KataGo推荐点显示");
    viewMenu.add(katasugg);

    final JMenuItem katasugg1 = new JMenuItem("胜率+计算量");
    katasugg.add(katasugg1);
    katasugg1.addActionListener(new ItemListener());

    final JMenuItem katasugg2 = new JMenuItem("目差+计算量");
    katasugg.add(katasugg2);
    katasugg2.addActionListener(new ItemListener());

    final JMenuItem katasugg3 = new JMenuItem("胜率+计算量+目差");
    katasugg.add(katasugg3);
    katasugg3.addActionListener(new ItemListener());

    final JMenu kataboard = new JMenu("KataGo目差显示");
    viewMenu.add(kataboard);

    final JMenuItem kataboardmean = new JMenuItem("目差");
    kataboard.add(kataboardmean);
    kataboardmean.addActionListener(new ItemListener());

    final JMenuItem kataboardboard = new JMenuItem("盘面差");
    kataboard.add(kataboardboard);
    kataboardboard.addActionListener(new ItemListener());

    final JMenu katameanalways = new JMenu("KataGo目差视角");
    viewMenu.add(katameanalways);

    final JMenuItem katameanblack = new JMenuItem("永远为黑视角");
    katameanalways.add(katameanblack);
    katameanblack.addActionListener(new ItemListener());

    final JMenuItem katameanblackwhite = new JMenuItem("黑白交替视角");
    katameanalways.add(katameanblackwhite);
    katameanblackwhite.addActionListener(new ItemListener());

    final JMenu kataEstimate = new JMenu("KataGo评估显示");
    viewMenu.add(kataEstimate);

    final JMenuItem kataEstimate1 = new JMenuItem("不显示评估");
    kataEstimate.add(kataEstimate1);
    kataEstimate1.addActionListener(new ItemListener());

    final JMenuItem kataEstimate2 = new JMenuItem("显示在大棋盘上");
    kataEstimate.add(kataEstimate2);
    kataEstimate2.addActionListener(new ItemListener());

    final JMenuItem kataEstimate3 = new JMenuItem("显示在小棋盘上");
    kataEstimate.add(kataEstimate3);
    kataEstimate3.addActionListener(new ItemListener());

    final JMenuItem kataEstimate4 = new JMenuItem("显示在大小棋盘上");
    kataEstimate.add(kataEstimate4);
    kataEstimate4.addActionListener(new ItemListener());

    final JMenuItem kataEstimate5 = new JMenuItem("以方块大小表示占有率");
    kataEstimate.add(kataEstimate5);
    kataEstimate5.addActionListener(new ItemListener());

    final JMenuItem kataEstimate6 = new JMenuItem("以方块透明度表示占有率");
    kataEstimate.add(kataEstimate6);
    kataEstimate6.addActionListener(new ItemListener());

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

    final JMenuItem continueGameBlackItem = new JMenuItem();
    continueGameBlackItem.setText("续弈(我执黑)(ALT+回车)");
    // aboutItem.setMnemonic('A');
    continueGameBlackItem.addActionListener(new ItemListener());
    gameMenu.add(continueGameBlackItem);

    final JMenuItem continueGameWhiteItem = new JMenuItem();
    continueGameWhiteItem.setText("续弈(我执白)(ALT+回车)");
    // aboutItem.setMnemonic('A');
    continueGameWhiteItem.addActionListener(new ItemListener());
    gameMenu.add(continueGameWhiteItem);

    final JMenuItem breakplay = new JMenuItem();
    breakplay.setText("中断对局");
    breakplay.addActionListener(new ItemListener());
    gameMenu.add(breakplay);
    gameMenu.addSeparator();

    final JMenuItem setBoard = new JMenuItem();
    setBoard.setText("设置棋盘大小");
    setBoard.addActionListener(new ItemListener());
    gameMenu.add(setBoard);

    final JMenuItem settime = new JMenuItem();
    settime.setText("设置AI用时");
    settime.addActionListener(new ItemListener());
    gameMenu.add(settime);

    final JMenuItem setinfo = new JMenuItem();
    setinfo.setText("设置棋局信息(修改贴目)");
    setinfo.addActionListener(new ItemListener());
    gameMenu.add(setinfo);
    gameMenu.addSeparator();

    final JMenuItem bestone = new JMenuItem();
    bestone.setText("落最佳一手(逗号)");
    bestone.addActionListener(new ItemListener());
    gameMenu.add(bestone);

    final JMenuItem pass = new JMenuItem();
    pass.setText("停一手(P)");
    pass.addActionListener(new ItemListener());
    gameMenu.add(pass);
    gameMenu.addSeparator();

    final JMenuItem empty = new JMenuItem();
    empty.setText("清空棋盘（Ctrl+Home）");
    // aboutItem.setMnemonic('A');
    empty.addActionListener(new ItemListener());
    gameMenu.add(empty);

    final JMenuItem firstItem = new JMenuItem();
    firstItem.setText("跳转到最前（Home）");
    // aboutItem.setMnemonic('A');
    firstItem.addActionListener(new ItemListener());
    gameMenu.add(firstItem);

    final JMenuItem lastItem = new JMenuItem();
    lastItem.setText("跳转到最后（End）");
    // aboutItem.setMnemonic('A');
    lastItem.addActionListener(new ItemListener());
    gameMenu.add(lastItem);

    final JMenuItem commetup = new JMenuItem();
    commetup.setText("跳转到左分支(左)");
    // aboutItem.setMnemonic('A');
    commetup.addActionListener(new ItemListener());
    gameMenu.add(commetup);

    final JMenuItem commetdown = new JMenuItem();
    commetdown.setText("跳转到右分支(右)");
    // aboutItem.setMnemonic('A');
    commetdown.addActionListener(new ItemListener());
    gameMenu.add(commetdown);

    final JMenuItem branchStart = new JMenuItem();
    branchStart.setText("返回主分支(CTRL+左)");
    // aboutItem.setMnemonic('A');
    branchStart.addActionListener(new ItemListener());
    gameMenu.add(branchStart);

    final JMenu analyMenu = new JMenu("分析 ", false);
    analyMenu.setText(" 分析  ");
    analyMenu.setForeground(Color.BLACK);
    analyMenu.setFont(headFont);
    menuBar.add(analyMenu);

    final JMenuItem anaItem = new JMenuItem();
    anaItem.setText("分析/停止（空格）");
    // aboutItem.setMnemonic('A');
    anaItem.addActionListener(new ItemListener());
    analyMenu.add(anaItem);

    final JMenuItem autoanItem = new JMenuItem();
    autoanItem.setText("自动分析（A）");
    // aboutItem.setMnemonic('A');
    autoanItem.addActionListener(new ItemListener());
    analyMenu.add(autoanItem);

    final JMenuItem heatItem = new JMenuItem();
    heatItem.setText("策略网络（H）");
    heatItem.addActionListener(new ItemListener());
    analyMenu.add(heatItem);

    final JMenuItem countsItem = new JMenuItem();
    countsItem.setText("形势判断（点）");
    // aboutItem.setMnemonic('A');
    countsItem.addActionListener(new ItemListener());
    analyMenu.add(countsItem);
    analyMenu.addSeparator();

    final JMenuItem badmovesItem = new JMenuItem("恶手列表(B)");
    badmovesItem.addActionListener(new ItemListener()); // 添加动作监听器
    analyMenu.add(badmovesItem); // 添加到“属性”子菜单

    final JMenuItem leelasu = new JMenuItem("AI选点列表(U)");
    leelasu.addActionListener(new ItemListener()); // 添加动作监听器
    analyMenu.add(leelasu); // 添加到“属性”子菜单

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
      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon iconwhite = new ImageIcon();
    try {
      iconwhite.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png")));
      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon iconbh = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
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

    editMenu.addSeparator();
    final JMenuItem clearsave = new JMenuItem();
    clearsave.setText("清除Lizzie所有选点缓存");
    // aboutItem.setMnemonic('A');
    clearsave.addActionListener(new ItemListener());
    editMenu.add(clearsave);

    final JMenuItem clearthis = new JMenuItem();
    clearthis.setText("清除当前选点缓存");
    // aboutItem.setMnemonic('A');
    clearthis.addActionListener(new ItemListener());
    editMenu.add(clearthis);

    engineMenu = new JMenu("引擎 ", false);
    engineMenu.setText(" 引擎  ");
    // helpMenu.setMnemonic('H');
    engineMenu.setForeground(Color.BLACK);
    engineMenu.setFont(headFont);
    menuBar.add(engineMenu);

    icon = new ImageIcon();
    try {
      icon.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/playing.png")));
      // icon.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/run.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ready = new ImageIcon();
    try {
      ready.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/ready.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    stop = new ImageIcon();
    try {
      stop.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/stop.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    for (int i = 0; i < engine.length; i++) {
      engine[i] = new JMenuItem();
      engine[i].addActionListener(new ItemListener());
      engineMenu.add(engine[i]);
      engine[i].setText("引擎" + (i + 1) + ":");
      engine[i].setVisible(false);
    }

    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
    enginesNameOpt.ifPresent(
        a -> {
          IntStream.range(0, a.length())
              .forEach(
                  i -> {
                    String name = a.getString(i);
                    engine[i].setText(engine[i].getText() + name);
                    if (!name.equals("")) {
                      Lizzie.frame.toolbar.enginePkBlack.addItem("[" + (i + 1) + "]" + name);
                      Lizzie.frame.toolbar.enginePkWhite.addItem("[" + (i + 1) + "]" + name);
                    }
                    if (!name.equals("")) {
                      engine[i].setVisible(true);
                    }
                  });
        });

    engineMenu.addSeparator();
    final JMenuItem closeall = new JMenuItem();
    closeall.setText("关闭所有引擎");
    // aboutItem.setMnemonic('A');
    closeall.addActionListener(new ItemListener());
    engineMenu.add(closeall);

    final JMenuItem forcecloseall = new JMenuItem();
    forcecloseall.setText("强制关闭所有引擎");
    // aboutItem.setMnemonic('A');
    forcecloseall.addActionListener(new ItemListener());
    engineMenu.add(forcecloseall);

    final JMenuItem closeother = new JMenuItem();
    closeother.setText("关闭当前以外引擎");
    // aboutItem.setMnemonic('A');
    closeother.addActionListener(new ItemListener());
    engineMenu.add(closeother);

    final JMenuItem restartZen = new JMenuItem();
    restartZen.setText("重启Zen(形势判断用)");
    // aboutItem.setMnemonic('A');
    restartZen.addActionListener(new ItemListener());
    engineMenu.add(restartZen);
    engineMenu.addSeparator();

    final JMenuItem config = new JMenuItem();
    config.setText("设置");
    // aboutItem.setMnemonic('A');
    config.addActionListener(new ItemListener());
    engineMenu.add(config);
  }

  public void updateEngineName() {
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
    enginesNameOpt.ifPresent(
        a -> {
          IntStream.range(0, a.length())
              .forEach(
                  i -> {
                    String name = a.getString(i);

                    if (i == 9) engine[i].setText(engine[i].getText().substring(0, 5) + name);
                    else engine[i].setText(engine[i].getText().substring(0, 4) + name);
                    if (!name.equals("")) engine[i].setVisible(true);
                    else {
                      engine[i].setVisible(false);
                    }
                  });
        });
  }

  class ItemListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JMenuItem menuItem = (JMenuItem) e.getSource();
      // System.out.println("您单击的是菜单项：" + menuItem.getText());
      Lizzie.frame.setVisible(true);
      if (menuItem.getText().startsWith("打开棋谱")) {
        Lizzie.frame.openFile();
        return;
      }

      if (menuItem.getText().startsWith("保存")) {
        Lizzie.frame.saveFile();
        return;
      }
      if (menuItem.getText().startsWith("退出")) {
        System.exit(0);
        return;
      }
      if (menuItem.getText().startsWith("坐标")) {
        Lizzie.config.toggleCoordinates();
        return;
      }
      if (menuItem.getText().startsWith("放大小")) {
        Lizzie.config.toggleLargeSubBoard();
        return;
      }
      if (menuItem.getText().startsWith("放大胜")) {
        Lizzie.config.toggleLargeWinrate();
        return;
      }
      if (menuItem.getText().startsWith("小棋")) {
        Lizzie.config.toggleShowSubBoard();
        return;
      }
      if (menuItem.getText().startsWith("评论")) {
        Lizzie.config.toggleShowComment();
        return;
      }
      if (menuItem.getText().startsWith("左上")) {
        Lizzie.config.toggleShowCaptured();
        return;
      }
      if (menuItem.getText().startsWith("左下")) {
        Lizzie.config.toggleShowStatus();
        return;
      }
      if (menuItem.getText().startsWith("分支")) {
        Lizzie.config.toggleShowVariationGraph();
        return;
      }
      if (menuItem.getText().startsWith("恶手")) {
        Lizzie.frame.toggleBadMoves();
        return;
      }
      if (menuItem.getText().startsWith("AI选点")) {
        Lizzie.frame.toggleBestMoves();
        return;
      }
      if (menuItem.getText().startsWith("策略")) {
        Lizzie.frame.toggleheatmap();
        return;
      }
      if (menuItem.getText().startsWith("胜率面板")) {
        Lizzie.config.toggleShowWinrate();
        return;
      }
      if (menuItem.getText().startsWith("命令")) {
        Lizzie.frame.toggleGtpConsole();
        return;
      }
      if (menuItem.getText().startsWith("棋盘左")) {
        if (Lizzie.frame.BoardPositionProportion > 0) Lizzie.frame.BoardPositionProportion--;
        return;
      }
      if (menuItem.getText().startsWith("棋盘右")) {
        if (Lizzie.frame.BoardPositionProportion < 8) Lizzie.frame.BoardPositionProportion++;
        return;
      }
      if (menuItem.getText().startsWith("新的")) {
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        LizzieFrame.startNewGame();
        return;
      }
      if (menuItem.getText().startsWith("续弈(我执黑")) {

        boolean playerIsBlack = true;
        Lizzie.leelaz.sendCommand(
            "time_settings 0 "
                + Lizzie.config
                    .config
                    .getJSONObject("leelaz")
                    .getInt("max-game-thinking-time-seconds")
                + " 1");
        Lizzie.frame.playerIsBlack = playerIsBlack;
        Lizzie.frame.isPlayingAgainstLeelaz = true;
        if (Lizzie.board.getData().blackToPlay != playerIsBlack) {
          Lizzie.leelaz.genmove("W");
        }
        return;
      }
      if (menuItem.getText().startsWith("续弈(我执白")) {

        boolean playerIsBlack = false;
        Lizzie.leelaz.sendCommand(
            "time_settings 0 "
                + Lizzie.config
                    .config
                    .getJSONObject("leelaz")
                    .getInt("max-game-thinking-time-seconds")
                + " 1");
        Lizzie.frame.playerIsBlack = playerIsBlack;
        Lizzie.frame.isPlayingAgainstLeelaz = true;
        if (Lizzie.board.getData().blackToPlay != playerIsBlack) {
          Lizzie.leelaz.genmove("B");
        }
        return;
      }
      if (menuItem.getText().startsWith("形势")) {
        Lizzie.frame.countstones();
        return;
      }
      if (menuItem.getText().startsWith("分析")) {
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
        }
        Lizzie.leelaz.togglePonder();
        return;
      }

      if (menuItem.getText().startsWith("引擎1") && !menuItem.getText().startsWith("引擎10")) {
        Lizzie.engineManager.switchEngine(0);
        return;
      }
      if (menuItem.getText().startsWith("引擎2")) {
        Lizzie.engineManager.switchEngine(1);
        return;
      }
      if (menuItem.getText().startsWith("引擎3")) {
        Lizzie.engineManager.switchEngine(2);
        return;
      }
      if (menuItem.getText().startsWith("引擎4")) {
        Lizzie.engineManager.switchEngine(3);
        return;
      }
      if (menuItem.getText().startsWith("引擎5")) {
        Lizzie.engineManager.switchEngine(4);
        return;
      }
      if (menuItem.getText().startsWith("引擎6")) {
        Lizzie.engineManager.switchEngine(5);
        return;
      }
      if (menuItem.getText().startsWith("引擎7")) {
        Lizzie.engineManager.switchEngine(6);
        return;
      }
      if (menuItem.getText().startsWith("引擎8")) {
        Lizzie.engineManager.switchEngine(7);
        return;
      }
      if (menuItem.getText().startsWith("引擎9")) {
        Lizzie.engineManager.switchEngine(8);
        return;
      }
      if (menuItem.getText().startsWith("引擎10")) {
        Lizzie.engineManager.switchEngine(9);
        return;
      }
      if (menuItem.getText().startsWith("交替落")) {
        featurecat.lizzie.gui.Input.insert = 0;
        Lizzie.frame.blackorwhite = 0;
        return;
      }
      if (menuItem.getText().startsWith("插入黑")) {
        featurecat.lizzie.gui.Input.insert = 1;
        return;
      }
      if (menuItem.getText().startsWith("插入白")) {
        featurecat.lizzie.gui.Input.insert = 2;
        return;
      }
      if (menuItem.getText().startsWith("落黑")) {
        Lizzie.frame.blackorwhite = 1;
        return;
      }
      if (menuItem.getText().startsWith("落白")) {
        Lizzie.frame.blackorwhite = 2;
        return;
      }

      if (menuItem.getText() == ("设置")) {
        Lizzie.frame.openConfigDialog();
        return;
      }
      if (menuItem.getText().startsWith("关闭所有")) {
        try {
          Lizzie.engineManager.killAllEngines();
        } catch (Exception ex) {

        }
        for (int i = 0; i < engine.length; i++) {
          engine[i].setIcon(null);
        }
        return;
      }

      if (menuItem.getText().startsWith("强制关闭所有")) {
        try {
          Lizzie.engineManager.forcekillAllEngines();
        } catch (Exception ex) {
        }
        for (int i = 0; i < engine.length; i++) {
          engine[i].setIcon(null);
        }
        return;
      }

      if (menuItem.getText().startsWith("关闭当前")) {
        try {
          Lizzie.engineManager.killOtherEngines();
        } catch (Exception ex) {

        }

        for (int i = 0; i < engine.length; i++) {
          engine[i].setIcon(null);
        }
        engine[Lizzie.leelaz.currentEngineN()].setIcon(icon);

        return;
      }
      if (menuItem.getText().startsWith("清空棋盘")) {
        Lizzie.board.clear();
        Lizzie.frame.refresh();
        return;
      }

      if (menuItem.getText().startsWith("跳转到最前")) {
        while (Lizzie.board.previousMove()) ;
        return;
      }
      if (menuItem.getText().startsWith("跳转到最后")) {
        while (Lizzie.board.nextMove()) ;
        return;
      }

      if (menuItem.getText().startsWith("设置棋盘")) {
        SetBoardSize st = new SetBoardSize();
        st.setVisible(true);
        return;
      }
      if (menuItem.getText().startsWith("设置AI用时")) {
        SetAiTimes st = new SetAiTimes();
        st.setVisible(true);
        return;
      }

      if (menuItem.getText().startsWith("落最佳")) {
        if (!Lizzie.frame.playCurrentVariation()) Lizzie.frame.playBestMove();
        return;
      }

      if (menuItem.getText().startsWith("不显示")) {
        Lizzie.config.allowMoveNumber = 0;
        Lizzie.config.uiConfig.put("allow-move-number", 0);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }

        return;
      }
      if (menuItem.getText().startsWith("最近1手")) {
        Lizzie.config.allowMoveNumber = 1;
        Lizzie.config.uiConfig.put("allow-move-number", 1);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }
        return;
      }
      if (menuItem.getText().startsWith("最近5手")) {
        Lizzie.config.allowMoveNumber = 5;
        Lizzie.config.uiConfig.put("allow-move-number", 5);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }
        return;
      }
      if (menuItem.getText().startsWith("最近10手")) {
        Lizzie.config.allowMoveNumber = 10;
        Lizzie.config.uiConfig.put("allow-move-number", 10);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }
        return;
      }
      if (menuItem.getText().startsWith("自定义")) {
        MovenumberDialog mvdialog = new MovenumberDialog();
        mvdialog.setVisible(true);
        return;
      }
      if (menuItem.getText() == ("全部")) {
        Lizzie.config.allowMoveNumber = -1;
        return;
      }
      if (menuItem.getText().startsWith("自动分")) {
        Input.shouldDisableAnalysis = false;
        Lizzie.board.toggleAnalysis();
        return;
      }

      if (menuItem.getText().startsWith("返回主分支")) {
        if (Lizzie.board.undoToChildOfPreviousWithVariation()) {
          Lizzie.board.previousMove();
        }
        return;
      }

      if (menuItem.getText().startsWith("跳转到左分")) {
        Input.previousBranch();
        return;
      }

      if (menuItem.getText().startsWith("跳转到右分")) {
        Input.nextBranch();
        return;
      }
      if (menuItem.getText().startsWith("复制到")) {
        Lizzie.frame.copySgf();
        return;
      }
      if (menuItem.getText().startsWith("从剪贴")) {
        Lizzie.frame.pasteSgf();
        return;
      }
      if (menuItem.getText().startsWith("精简")) {

        if (Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
        if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
        //   if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
        //  if (!Lizzie.config.showLargeSubBoard()) Lizzie.config.showLargeSubBoard();
        if (Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
        if (Lizzie.config.showStatus) Lizzie.config.toggleShowStatus();
        if (Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
        if (Lizzie.config.showWinrate) Lizzie.config.toggleShowWinrate();
        int minlength =
            Math.min(
                Lizzie.frame.getWidth(), Lizzie.frame.getHeight() - Lizzie.frame.toolbarHeight);
        Lizzie.frame.setBounds(
            Lizzie.frame.getX(),
            Lizzie.frame.getY(),
            (int) (minlength * 0.94),
            minlength + Lizzie.frame.toolbarHeight);
        Lizzie.frame.repaint();
        return;
      }
      if (menuItem.getText().startsWith("经典")) {

        if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
        if (!Lizzie.config.showWinrate) Lizzie.config.toggleShowWinrate();
        if (Lizzie.config.showLargeWinrateOnly()) Lizzie.config.toggleLargeWinrate();
        if (!Lizzie.config.showLargeSubBoard()) Lizzie.config.toggleLargeSubBoard();
        if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
        if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
        if (Lizzie.config.showStatus) Lizzie.config.toggleShowStatus();
        if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
        if (Lizzie.frame.getWidth() - Lizzie.frame.getHeight() < 485)
          Lizzie.frame.setBounds(
              Lizzie.frame.getX(),
              Lizzie.frame.getY(),
              Lizzie.frame.getHeight() + 485,
              Lizzie.frame.getHeight());
        // Lizzie.frame.redrawBackgroundAnyway=true;
        Lizzie.frame.repaint();
        return;
      }
      if (menuItem.getText().startsWith("默认")) {

        if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
        if (!Lizzie.config.showWinrate) Lizzie.config.toggleShowWinrate();
        if (Lizzie.config.showLargeSubBoard()) Lizzie.config.toggleLargeSubBoard();
        if (Lizzie.config.showLargeWinrate()) Lizzie.config.toggleLargeWinrate();
        if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
        if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
        if (!Lizzie.config.showStatus) Lizzie.config.toggleShowStatus();
        if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
        if (Lizzie.frame.getWidth() - Lizzie.frame.getHeight() < 600)
          Lizzie.frame.setBounds(
              Lizzie.frame.getX(),
              Lizzie.frame.getY(),
              Lizzie.frame.getHeight() + 600,
              Lizzie.frame.getHeight());
        Lizzie.frame.repaint();
        return;
      }

      if (menuItem.getText().startsWith("总在最")) {
        Lizzie.frame.toggleAlwaysOntop();
        return;
      }
      if (menuItem.getText().startsWith("中断对局")) {
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
        }
        return;
      }
      if (menuItem.getText().startsWith("底部")) {
        int nowHeight = Lizzie.frame.toolbarHeight;
        Lizzie.frame.toolbarHeight = 26;
        Lizzie.frame.toolbar.setVisible(true);
        Lizzie.frame.toolbar.detail.setIcon(Lizzie.frame.toolbar.iconUp);
        Lizzie.frame.mainPanel.setBounds(
            Lizzie.frame.mainPanel.getX(),
            Lizzie.frame.mainPanel.getY(),
            Lizzie.frame.mainPanel.getWidth(),
            Lizzie.frame.mainPanel.getHeight() - 26 + nowHeight);
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

        return;
      }
      if (menuItem.getText().startsWith("详细")) {
        int nowHeight = Lizzie.frame.toolbarHeight;
        Lizzie.frame.toolbarHeight = 70;
        Lizzie.frame.toolbar.setVisible(true);
        Lizzie.frame.toolbar.detail.setIcon(Lizzie.frame.toolbar.iconDown);
        Lizzie.frame.mainPanel.setBounds(
            Lizzie.frame.mainPanel.getX(),
            Lizzie.frame.mainPanel.getY(),
            Lizzie.frame.mainPanel.getWidth(),
            Lizzie.frame.mainPanel.getHeight() - 70 + nowHeight);
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
        return;
      }
      if (menuItem.getText().startsWith("关闭工")) {
        int nowHeight = Lizzie.frame.toolbarHeight;
        Lizzie.frame.toolbarHeight = 0;
        Lizzie.frame.toolbar.setVisible(false);
        Lizzie.frame.mainPanel.setBounds(
            Lizzie.frame.mainPanel.getX(),
            Lizzie.frame.mainPanel.getY(),
            Lizzie.frame.mainPanel.getWidth(),
            Lizzie.frame.mainPanel.getHeight() + nowHeight);
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
        return;
      }
      if (menuItem.getText().startsWith("打开关闭前")) {
        Lizzie.board.resumePreviousGame();
        return;
      }
      if (menuItem.getText().startsWith("关闭自动保存")) {

        Lizzie.config.uiConfig.put("autosave-interval-seconds", -1);
        Lizzie.config.uiConfig.put("resume-previous-game", false);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }

      if (menuItem.getText().startsWith("打开自动保存")) {

        Lizzie.config.uiConfig.put("autosave-interval-seconds", 10);
        Lizzie.config.uiConfig.put("resume-previous-game", true);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("打开在线")) {
        Lizzie.frame.openOnlineDialog();
        return;
      }
      if (menuItem.getText().startsWith("记录胜")) {
        Lizzie.config.toggleappendWinrateToComment();
        return;
      }
      if (menuItem.getText().startsWith("清除Lizzie所有")) {
        Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
        boolean onTop = false;
        if (Lizzie.frame.isAlwaysOnTop()) {
          Lizzie.frame.setAlwaysOnTop(false);
          onTop = true;
        }
        JOptionPane.showMessageDialog(null, "已清空所有Lizzie缓存的引擎推荐点");
        if (onTop) Lizzie.frame.setAlwaysOnTop(true);
        return;
      }
      if (menuItem.getText().startsWith("清除当前")) {
        Lizzie.board.clearbestmoves();
        boolean onTop = false;
        if (Lizzie.frame.isAlwaysOnTop()) {
          Lizzie.frame.setAlwaysOnTop(false);
          onTop = true;
        }
        JOptionPane.showMessageDialog(null, "已清空当前一步Lizzie缓存的引擎推荐点");
        if (onTop) Lizzie.frame.setAlwaysOnTop(true);
        return;
      }
      if (menuItem.getText().startsWith("重启Z")) {
        Lizzie.frame.restartZen();
        return;
      }
      if (menuItem.getText().startsWith("批量")) {
        Lizzie.frame.openFileAll();
        return;
      }
      if (menuItem.getText().startsWith("停一")) {
        Lizzie.board.pass();
        return;
      }
      if (menuItem.getText().startsWith("设置棋局信")) {
        Lizzie.frame.editGameInfo();
        return;
      }
      if (menuItem.getText().startsWith("设置详细")) {
        ToolbarPositionConfig tbc = new ToolbarPositionConfig();
        tbc.setVisible(true);
        return;
      }
      if (menuItem.getText().startsWith("切换总是显示黑")) {
        if (Lizzie.config.uiConfig.getBoolean("win-rate-always-black")) {
          Lizzie.config.uiConfig.put("win-rate-always-black", false);
          try {
            Lizzie.config.save();
          } catch (IOException es) {
            // TODO Auto-generated catch block
          }
        } else {
          Lizzie.config.uiConfig.put("win-rate-always-black", true);
          try {
            Lizzie.config.save();
          } catch (IOException es) {
            // TODO Auto-generated catch block
          }
        }
        return;
      }
      if (menuItem.getText().startsWith("显示双方")) {
        Lizzie.frame.winrateGraph.mode = 1;
        return;
      }
      if (menuItem.getText().startsWith("显示黑方")) {
        Lizzie.frame.winrateGraph.mode = 0;
        return;
      }
      if (menuItem.getText().startsWith("显示/关闭柱状")) {
        Lizzie.config.showBlunderBar = !Lizzie.config.showBlunderBar;
        Lizzie.config.uiConfig.put("show-blunder-bar", Lizzie.config.showBlunderBar);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("胜率+计算量+")) {
        Lizzie.config.showKataGoScoreMean = true;
        Lizzie.config.kataGoNotShowWinrate = false;
        Lizzie.config.uiConfig.put("show-katago-scoremean", Lizzie.config.showKataGoScoreMean);
        Lizzie.config.uiConfig.put("katago-notshow-winrate", Lizzie.config.kataGoNotShowWinrate);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("胜率+计")) {
        Lizzie.config.showKataGoScoreMean = false;
        Lizzie.config.uiConfig.put("show-katago-scoremean", Lizzie.config.showKataGoScoreMean);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("目差+计")) {
        Lizzie.config.showKataGoScoreMean = true;
        Lizzie.config.kataGoNotShowWinrate = true;
        Lizzie.config.uiConfig.put("show-katago-scoremean", Lizzie.config.showKataGoScoreMean);
        Lizzie.config.uiConfig.put("katago-notshow-winrate", Lizzie.config.kataGoNotShowWinrate);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("目差")) {
        Lizzie.config.showKataGoBoardScoreMean = false;
        Lizzie.config.uiConfig.put(
            "show-katago-boardscoremean", Lizzie.config.showKataGoBoardScoreMean);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("盘面差")) {
        Lizzie.config.showKataGoBoardScoreMean = true;
        Lizzie.config.uiConfig.put(
            "show-katago-boardscoremean", Lizzie.config.showKataGoBoardScoreMean);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("永远为黑")) {
        Lizzie.config.kataGoScoreMeanAlwaysBlack = true;
        Lizzie.config.uiConfig.put(
            "katago-scoremean-alwaysblack", Lizzie.config.kataGoScoreMeanAlwaysBlack);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("黑白交替")) {
        Lizzie.config.kataGoScoreMeanAlwaysBlack = false;
        Lizzie.config.uiConfig.put(
            "katago-scoremean-alwaysblack", Lizzie.config.kataGoScoreMeanAlwaysBlack);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("不显示评估")) {
        Lizzie.config.showKataGoEstimate = false;
        Lizzie.frame.boardRenderer.removecountblock();
        if (Lizzie.config.showSubBoard) Lizzie.frame.subBoardRenderer.removecountblock();
        Lizzie.leelaz.ponder();
        Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("显示在大棋")) {
        Lizzie.config.showKataGoEstimate = true;
        Lizzie.config.showKataGoEstimateOnMainbord = true;
        Lizzie.config.showKataGoEstimateOnSubbord = false;
        if (Lizzie.config.showSubBoard) Lizzie.frame.subBoardRenderer.removecountblock();
        Lizzie.leelaz.ponder();
        Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("显示在小")) {
        Lizzie.config.showKataGoEstimate = true;
        Lizzie.config.showKataGoEstimateOnMainbord = false;
        Lizzie.config.showKataGoEstimateOnSubbord = true;
        Lizzie.frame.boardRenderer.removecountblock();
        Lizzie.leelaz.ponder();
        Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("显示在大小")) {
        Lizzie.config.showKataGoEstimate = true;
        Lizzie.config.showKataGoEstimateOnMainbord = true;
        Lizzie.config.showKataGoEstimateOnSubbord = true;
        Lizzie.leelaz.ponder();
        Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("以方块大")) {
        Lizzie.config.showKataGoEstimateBySize = true;
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-bysize", Lizzie.config.showKataGoEstimateBySize);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
      if (menuItem.getText().startsWith("以方块透")) {
        Lizzie.config.showKataGoEstimateBySize = false;
        Lizzie.config.uiConfig.put(
            "show-katago-estimate-bysize", Lizzie.config.showKataGoEstimateBySize);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
        }
        return;
      }
    }
  }
}
