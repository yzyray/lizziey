package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.json.JSONArray;

public class Menu extends MenuBar {

  final ButtonGroup buttonGroup = new ButtonGroup();

  Font headFont;
  public static ImageIcon icon;
  public static ImageIcon stop;
  public static ImageIcon ready;
  public static JMenuItem[] engine = new JMenuItem[21];
  public static JMenu engineMenu;
  public static MenuBar menuBar;
  JMenuItem closeall;
  JMenuItem forcecloseall;
  JMenuItem closeother;
  JMenuItem restartZen;
  JMenuItem config;
  JMenuItem moreconfig;
  // private boolean onlyboard = false;

  public Menu() {
    // super(owner);
    // setLayout(null);
    // setUndecorated(true);
    // setResizable(true);
    // setBounds(
    // Lizzie.frame.getX() + Lizzie.frame.getInsets().left,
    // Lizzie.frame.getY() + Lizzie.frame.getInsets().top,
    // Lizzie.frame.getContentPane().getWidth(),
    // 25);
    Color hsbColor =
        Color.getHSBColor(
            Color.RGBtoHSB(232, 232, 232, null)[0],
            Color.RGBtoHSB(232, 232, 232, null)[1],
            Color.RGBtoHSB(232, 232, 232, null)[2]);
    this.setBackground(hsbColor);
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBorder(new EmptyBorder(0, 0, -1, -1));
    menuBar = new MenuBar();
    // bar.setBounds(0, 0, 450, 12);

    menuBar.setColor(hsbColor);
    this.add(bar);
    bar.add(menuBar);

    headFont = new Font("幼圆", Font.BOLD, 15);
    // onlyboard = Lizzie.config.uiConfig.optBoolean("only-board", false);

    final JMenu fileMenu = new JMenu(" 文件  "); // 创建“文件”菜单
    // fileMenu.setMnemonic('F'); // 设置快捷键
    fileMenu.setForeground(Color.BLACK);
    fileMenu.setFont(headFont);

    menuBar.add(fileMenu);
    final JMenuItem openItem = new JMenuItem("打开棋谱（O）");
    openItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(openItem);

    menuBar.add(fileMenu);
    final JMenuItem batchfile = new JMenuItem("批量分析棋谱（ALT+O）");
    batchfile.addActionListener(new ItemListeneryzy());
    fileMenu.add(batchfile);

    final JMenuItem openUrlItem = new JMenuItem("打开在线棋谱（Q）");
    openUrlItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(openUrlItem);

    final JMenuItem saveItem = new JMenuItem();
    saveItem.setText("保存棋谱（S）");
    saveItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(saveItem);

    fileMenu.addSeparator();
    final JMenuItem copyItem = new JMenuItem();
    copyItem.setText("复制到剪贴板（CTRL+C）");
    copyItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(copyItem);

    final JMenuItem pasteItem = new JMenuItem();
    pasteItem.setText("从剪贴板粘贴（CTRL+V）");
    pasteItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(pasteItem);
    fileMenu.addSeparator();

    final JMenuItem resume = new JMenuItem();
    resume.setText("打开关闭前的棋谱或自动保存的棋谱");
    resume.addActionListener(new ItemListeneryzy());
    fileMenu.add(resume);

    final JMenuItem resumeItem = new JMenuItem();
    resumeItem.setText("打开自动保存棋谱(10秒一次)");
    resumeItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(resumeItem);

    final JMenuItem resumeItem2 = new JMenuItem();
    resumeItem2.setText("关闭自动保存棋谱");
    resumeItem2.addActionListener(new ItemListeneryzy());
    fileMenu.add(resumeItem2);

    fileMenu.addSeparator();

    final JMenuItem exitItem = new JMenuItem();
    exitItem.setText("强制退出");
    exitItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(exitItem);

    final JMenuItem exit = new JMenuItem();
    exit.setText("退出");
    exit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.shutdown();
          }
        });
    fileMenu.add(exit);

    final JMenu viewMenu = new JMenu();
    viewMenu.setText(" 显示  ");
    // editMenu.setMnemonic('E');
    menuBar.add(viewMenu);
    viewMenu.setForeground(Color.BLACK);
    viewMenu.setFont(headFont);

    final JMenuItem leftItem = new JMenuItem();
    leftItem.setText("棋盘左移（[）");
    leftItem.addActionListener(new ItemListeneryzy());
    viewMenu.add(leftItem);

    final JMenuItem rightItem = new JMenuItem();
    rightItem.setText("棋盘右移（]）");
    rightItem.addActionListener(new ItemListeneryzy());
    viewMenu.add(rightItem);

    final JMenu winrate = new JMenu("胜率图和推荐点");
    viewMenu.add(winrate);

    final JCheckBoxMenuItem alwaysBlack = new JCheckBoxMenuItem();
    alwaysBlack.setText("总是显示黑胜率");
    alwaysBlack.addActionListener(new ItemListeneryzy());
    winrate.add(alwaysBlack);

    final JCheckBoxMenuItem isOnmouse = new JCheckBoxMenuItem();
    isOnmouse.setText("鼠标所指推荐点显示变化图");
    isOnmouse.addActionListener(new ItemListeneryzy());
    winrate.add(isOnmouse);

    final JCheckBoxMenuItem winratemode1 = new JCheckBoxMenuItem();
    winratemode1.setText("显示双方胜率图");
    winratemode1.addActionListener(new ItemListeneryzy());
    winrate.add(winratemode1);

    final JCheckBoxMenuItem winratemode0 = new JCheckBoxMenuItem();
    winratemode0.setText("显示黑方胜率图");
    winratemode0.addActionListener(new ItemListeneryzy());
    winrate.add(winratemode0);

    final JCheckBoxMenuItem blunder = new JCheckBoxMenuItem();
    blunder.setText("显示柱状失误条");
    blunder.addActionListener(new ItemListeneryzy());
    winrate.add(blunder);

    final JCheckBoxMenuItem showsuggorder = new JCheckBoxMenuItem();
    showsuggorder.setText("显示推荐点右上方角标");
    showsuggorder.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSuggestionOrder = !Lizzie.config.showSuggestionOrder;
            Lizzie.config.uiConfig.put("show-suggestion-order", Lizzie.config.showSuggestionOrder);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    winrate.add(showsuggorder);

    final JCheckBoxMenuItem showsuggred = new JCheckBoxMenuItem();
    showsuggred.setText("最高胜率-计算量-目差 显示为红色");
    showsuggred.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSuggestionMaxRed = !Lizzie.config.showSuggestionMaxRed;
            Lizzie.config.uiConfig.put(
                "show-suggestion-maxred", Lizzie.config.showSuggestionMaxRed);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    winrate.add(showsuggred);

    final JMenuItem setReplayTime = new JMenuItem();
    setReplayTime.setText("设置推荐点分支回放间隔");
    setReplayTime.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            SetReplayTime setReplayTime = new SetReplayTime();
            setReplayTime.setVisible(true);
          }
        });
    winrate.add(setReplayTime);

    final JCheckBoxMenuItem coordsItem = new JCheckBoxMenuItem();
    coordsItem.setText("坐标（C）");
    coordsItem.addActionListener(new ItemListeneryzy());
    viewMenu.add(coordsItem);

    final JMenu moveMenu = new JMenu("手数(M)");
    viewMenu.add(moveMenu);

    final JCheckBoxMenuItem noItem = new JCheckBoxMenuItem();
    noItem.setText("不显示");
    noItem.addActionListener(new ItemListeneryzy());
    moveMenu.add(noItem);

    final JCheckBoxMenuItem oneItem = new JCheckBoxMenuItem();
    oneItem.setText("最近1手");
    oneItem.addActionListener(new ItemListeneryzy());
    moveMenu.add(oneItem);

    final JCheckBoxMenuItem fiveItem = new JCheckBoxMenuItem();
    fiveItem.setText("最近5手");
    fiveItem.addActionListener(new ItemListeneryzy());
    moveMenu.add(fiveItem);

    final JCheckBoxMenuItem tenItem = new JCheckBoxMenuItem();
    tenItem.setText("最近10手");
    tenItem.addActionListener(new ItemListeneryzy());
    moveMenu.add(tenItem);

    final JCheckBoxMenuItem allItem = new JCheckBoxMenuItem();
    allItem.setText("全部");
    allItem.addActionListener(new ItemListeneryzy());
    moveMenu.add(allItem);

    final JCheckBoxMenuItem anymove = new JCheckBoxMenuItem();
    anymove.setText("自定义");
    anymove.addActionListener(new ItemListeneryzy());
    moveMenu.add(anymove);

    final JCheckBoxMenuItem alwaysone = new JCheckBoxMenuItem();
    alwaysone.setText("总是从1开始显示");
    alwaysone.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showMoveNumberFromOne = !Lizzie.config.showMoveNumberFromOne;
            Lizzie.config.uiConfig.put("movenumber-from-one", Lizzie.config.showMoveNumberFromOne);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    moveMenu.add(alwaysone);

    final JCheckBoxMenuItem subItem = new JCheckBoxMenuItem();
    subItem.setText("放大小棋盘（ALT+V）");
    subItem.addActionListener(new ItemListeneryzy());
    viewMenu.add(subItem);

    final JCheckBoxMenuItem largewin = new JCheckBoxMenuItem();
    largewin.setText("放大胜率图(Ctrl+W)");
    largewin.addActionListener(new ItemListeneryzy());
    viewMenu.add(largewin);

    final JCheckBoxMenuItem appentComment = new JCheckBoxMenuItem();
    appentComment.setText("记录胜率到评论中");
    appentComment.addActionListener(new ItemListeneryzy());
    viewMenu.add(appentComment);

    final JCheckBoxMenuItem alwaysontop = new JCheckBoxMenuItem();
    alwaysontop.setText("总在最前");
    alwaysontop.addActionListener(new ItemListeneryzy());
    viewMenu.add(alwaysontop);
    viewMenu.addSeparator();
    final JCheckBoxMenuItem toolMenu = new JCheckBoxMenuItem("简略工具栏"); // 创建“字体”子菜单
    viewMenu.add(toolMenu); // 添加到“编辑”菜单
    toolMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem bigtoolMenu = new JCheckBoxMenuItem("详细工具栏");
    viewMenu.add(bigtoolMenu);
    bigtoolMenu.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem closeTool = new JCheckBoxMenuItem("关闭工具栏"); // 创建“字体”子菜单
    viewMenu.add(closeTool); // 添加到“编辑”菜单
    closeTool.addActionListener(new ItemListeneryzy()); // 添加动作监听器 viewMenu.addSeparator();

    final JMenuItem bigtoolConf = new JMenuItem("设置详细工具栏顺序");
    viewMenu.add(bigtoolConf);
    bigtoolConf.addActionListener(new ItemListeneryzy());
    viewMenu.addSeparator();

    final JCheckBoxMenuItem subboard = new JCheckBoxMenuItem("小棋盘"); // 创建“字体”子菜单
    viewMenu.add(subboard); // 添加到“编辑”菜单
    subboard.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem winratetMenu = new JCheckBoxMenuItem("胜率面板(W)"); // 创建“字体”子菜单
    viewMenu.add(winratetMenu); // 添加到“编辑”菜单
    winratetMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem commitMenu = new JCheckBoxMenuItem("评论面板(T)"); // 创建“字体”子菜单
    viewMenu.add(commitMenu); // 添加到“编辑”菜单
    commitMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem branch = new JCheckBoxMenuItem("分支面板(G)"); // 创建“字体”子菜单
    viewMenu.add(branch); // 添加到“编辑”菜单
    branch.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem topleft = new JCheckBoxMenuItem("左上角面板"); // 创建“字体”子菜单
    viewMenu.add(topleft); // 添加到“编辑”菜单
    topleft.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem bottomleft = new JCheckBoxMenuItem("左下角状态"); // 创建“字体”子菜单
    viewMenu.add(bottomleft); // 添加到“编辑”菜单
    bottomleft.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem gtpMenu = new JCheckBoxMenuItem("命令窗口(E)"); // 创建“字体”子菜单
    viewMenu.add(gtpMenu); // 添加到“编辑”菜单
    gtpMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器
    viewMenu.addSeparator();

    final JMenuItem defview = new JMenuItem("默认模式"); // 创建“字体”子菜单
    viewMenu.add(defview); // 添加到“编辑”菜单
    defview.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JMenuItem claview = new JMenuItem("经典模式"); // 创建“字体”子菜单
    viewMenu.add(claview); // 添加到“编辑”菜单
    claview.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JMenuItem allview = new JMenuItem("精简模式");
    viewMenu.add(allview);
    allview.addActionListener(new ItemListeneryzy());
    viewMenu.addSeparator();

    final JMenu katasugg = new JMenu("KataGo推荐点显示");
    viewMenu.add(katasugg);

    final JCheckBoxMenuItem katasugg1 = new JCheckBoxMenuItem("胜率+计算量", false);

    // ItemListener itn= new ItemListeneryzy() {
    // public void itemStateChanged(ItemEvent e) {
    //
    // if(katasugg1.getState()){
    // katasugg.add(katasugg1);
    // } else {
    // katasugg.remove(katasugg1);
    // }
    // }
    // };
    //
    // katasugg1.addItemListener(itn);

    // final JMenuItem katasugg1 = new JMenuItem("胜率+计算量");
    katasugg.add(katasugg1);
    katasugg1.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem katasugg2 = new JCheckBoxMenuItem("目差+计算量");
    katasugg.add(katasugg2);
    // katasugg2.addActionListener(new ItemListeneryzy());

    katasugg2.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showKataGoScoreMean = true;
            Lizzie.config.kataGoNotShowWinrate = true;
            Lizzie.config.uiConfig.put("show-katago-scoremean", Lizzie.config.showKataGoScoreMean);
            Lizzie.config.uiConfig.put(
                "katago-notshow-winrate", Lizzie.config.kataGoNotShowWinrate);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JCheckBoxMenuItem katasugg3 = new JCheckBoxMenuItem("胜率+计算量+目差");
    katasugg.add(katasugg3);
    katasugg3.addActionListener(new ItemListeneryzy());

    final JMenu kataboard = new JMenu("KataGo目差显示");
    viewMenu.add(kataboard);

    final JCheckBoxMenuItem kataboardmean = new JCheckBoxMenuItem("目差");
    kataboard.add(kataboardmean);
    kataboardmean.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataboardboard = new JCheckBoxMenuItem("盘面差");
    kataboard.add(kataboardboard);
    kataboardboard.addActionListener(new ItemListeneryzy());

    final JMenu katameanalways = new JMenu("KataGo目差视角");
    viewMenu.add(katameanalways);

    final JCheckBoxMenuItem katameanblack = new JCheckBoxMenuItem("永远为黑视角");
    katameanalways.add(katameanblack);
    katameanblack.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem katameanblackwhite = new JCheckBoxMenuItem("黑白交替视角");
    katameanalways.add(katameanblackwhite);
    katameanblackwhite.addActionListener(new ItemListeneryzy());

    final JMenu katawingraphboard = new JMenu("KataGo目差在胜率图上显示");
    viewMenu.add(katawingraphboard);

    final JCheckBoxMenuItem katawinboardmean = new JCheckBoxMenuItem("目差");
    katawingraphboard.add(katawinboardmean);
    katawinboardmean.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.scoreMeanWinrateGraphBoard = false;
            Lizzie.config.uiConfig.put(
                "scoremean-winrategraph-board", Lizzie.config.scoreMeanWinrateGraphBoard);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JCheckBoxMenuItem katawinboardboard = new JCheckBoxMenuItem("盘面差");
    katawingraphboard.add(katawinboardboard);
    katawinboardboard.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.scoreMeanWinrateGraphBoard = true;
            Lizzie.config.uiConfig.put(
                "scoremean-winrategraph-board", Lizzie.config.scoreMeanWinrateGraphBoard);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JMenu kataEstimate = new JMenu("KataGo评估显示");
    viewMenu.add(kataEstimate);

    final JCheckBoxMenuItem kataEstimate1 = new JCheckBoxMenuItem("关闭评估");
    kataEstimate.add(kataEstimate1);
    kataEstimate1.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataEstimate2 = new JCheckBoxMenuItem("显示在大棋盘上");
    kataEstimate.add(kataEstimate2);
    kataEstimate2.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataEstimate3 = new JCheckBoxMenuItem("显示在小棋盘上");
    kataEstimate.add(kataEstimate3);
    kataEstimate3.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataEstimate4 = new JCheckBoxMenuItem("显示在大小棋盘上");
    kataEstimate.add(kataEstimate4);
    kataEstimate4.addActionListener(new ItemListeneryzy());

    kataEstimate.addSeparator();

    final JCheckBoxMenuItem kataEstimate5 = new JCheckBoxMenuItem("以方块大小表示占有率");
    kataEstimate.add(kataEstimate5);
    kataEstimate5.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataEstimate6 = new JCheckBoxMenuItem("以方块透明度表示占有率");
    kataEstimate.add(kataEstimate6);
    kataEstimate6.addActionListener(new ItemListeneryzy());

    viewMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (!Lizzie.config.showKataGoScoreMean) katasugg1.setState(true);
            else katasugg1.setState(false);
            if (Lizzie.config.showKataGoScoreMean && Lizzie.config.kataGoNotShowWinrate)
              katasugg2.setState(true);
            else katasugg2.setState(false);
            if (Lizzie.config.showKataGoScoreMean && !Lizzie.config.kataGoNotShowWinrate)
              katasugg3.setState(true);
            else katasugg3.setState(false);
            if (Lizzie.config.showKataGoBoardScoreMean) {
              kataboardmean.setState(false);
              kataboardboard.setState(true);
            } else {
              kataboardmean.setState(true);
              kataboardboard.setState(false);
            }

            if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
              katameanblack.setState(true);
              katameanblackwhite.setState(false);
            } else {
              katameanblack.setState(false);
              katameanblackwhite.setState(true);
            }
            if (Lizzie.config.showKataGoEstimate) {
              kataEstimate1.setState(false);
              if (Lizzie.config.showKataGoEstimateOnMainbord
                  && Lizzie.config.showKataGoEstimateOnSubbord) {
                kataEstimate4.setState(true);
                kataEstimate2.setState(false);
                kataEstimate3.setState(false);
              } else if (Lizzie.config.showKataGoEstimateOnMainbord) {
                kataEstimate2.setState(true);
                kataEstimate4.setState(false);
                kataEstimate3.setState(false);
              } else if (Lizzie.config.showKataGoEstimateOnSubbord) {
                kataEstimate3.setState(true);
                kataEstimate2.setState(false);
                kataEstimate4.setState(false);
              }

            } else {
              kataEstimate1.setState(true);
              kataEstimate2.setState(false);
              kataEstimate3.setState(false);
              kataEstimate4.setState(false);
            }

            if (Lizzie.config.showKataGoEstimateBySize) {
              kataEstimate5.setState(true);
              kataEstimate6.setState(false);
            } else {
              kataEstimate6.setState(true);
              kataEstimate5.setState(false);
            }
            if (Lizzie.config.uiConfig.getBoolean("win-rate-always-black"))
              alwaysBlack.setState(true);
            else alwaysBlack.setState(false);
            if (Lizzie.config.showSuggestionVaritions) isOnmouse.setState(true);
            else isOnmouse.setState(false);
            if (Lizzie.frame.winrateGraph.mode == 1) {
              winratemode1.setState(true);
              winratemode0.setState(false);
            } else {
              winratemode1.setState(false);
              winratemode0.setState(true);
            }
            if (Lizzie.config.showBlunderBar) blunder.setState(true);
            else blunder.setState(false);

            if (Lizzie.config.showWinrate && Lizzie.config.showLargeWinrate())
              largewin.setState(true);
            else largewin.setState(false);
            if (Lizzie.config.showSubBoard && Lizzie.config.showLargeSubBoard())
              subItem.setState(true);
            else subItem.setState(false);
            if (Lizzie.config.appendWinrateToComment) appentComment.setState(true);
            else appentComment.setState(false);

            if (Lizzie.config.uiConfig.optBoolean("mains-always-ontop", false))
              alwaysontop.setState(true);
            else alwaysontop.setState(false);
            if (Lizzie.frame.toolbarHeight == 26) toolMenu.setState(true);
            else toolMenu.setState(false);
            if (Lizzie.frame.toolbarHeight == 70) bigtoolMenu.setState(true);
            else bigtoolMenu.setState(false);
            if (Lizzie.frame.toolbarHeight == 0) closeTool.setState(true);
            else closeTool.setState(false);

            if (Lizzie.config.showSubBoard) subboard.setState(true);
            else subboard.setState(false);
            if (Lizzie.config.showWinrate) winratetMenu.setState(true);
            else winratetMenu.setState(false);
            if (Lizzie.config.showComment) commitMenu.setState(true);
            else commitMenu.setState(false);
            if (Lizzie.config.showVariationGraph) branch.setState(true);
            else branch.setState(false);
            if (Lizzie.config.showCaptured) topleft.setState(true);
            else topleft.setState(false);
            if (Lizzie.config.showStatus) bottomleft.setState(true);
            else bottomleft.setState(false);
            if (Lizzie.gtpConsole.isVisible()) gtpMenu.setState(true);
            else gtpMenu.setState(false);
            if (Lizzie.config.showCoordinates) coordsItem.setState(true);
            else coordsItem.setState(false);
            switch (Lizzie.config.allowMoveNumber) {
              case 0:
                noItem.setState(true);
                oneItem.setState(false);
                fiveItem.setState(false);
                tenItem.setState(false);
                allItem.setState(false);
                anymove.setState(false);
                break;
              case 1:
                noItem.setState(false);
                oneItem.setState(true);
                fiveItem.setState(false);
                tenItem.setState(false);
                allItem.setState(false);
                anymove.setState(false);
                break;
              case 5:
                noItem.setState(false);
                oneItem.setState(false);
                fiveItem.setState(true);
                tenItem.setState(false);
                allItem.setState(false);
                anymove.setState(false);
                break;
              case 10:
                noItem.setState(false);
                oneItem.setState(false);
                fiveItem.setState(false);
                tenItem.setState(true);
                allItem.setState(false);
                anymove.setState(false);
                break;
              case -1:
                noItem.setState(false);
                oneItem.setState(false);
                fiveItem.setState(false);
                tenItem.setState(false);
                allItem.setState(true);
                anymove.setState(false);
                break;
              default:
                noItem.setState(false);
                oneItem.setState(false);
                fiveItem.setState(false);
                tenItem.setState(false);
                allItem.setState(false);
                anymove.setState(true);
            }
            if (Lizzie.config.showMoveNumberFromOne) alwaysone.setState(true);
            else alwaysone.setState(false);

            if (Lizzie.config.showSuggestionOrder) showsuggorder.setState(true);
            else showsuggorder.setState(false);

            if (Lizzie.config.showSuggestionMaxRed) showsuggred.setState(true);
            else showsuggred.setState(false);
            if (Lizzie.config.scoreMeanWinrateGraphBoard) {
              katawinboardmean.setState(false);
              katawinboardboard.setState(true);
            } else {
              katawinboardmean.setState(true);
              katawinboardboard.setState(false);
            }
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JMenu gameMenu = new JMenu("棋局 ", false);
    gameMenu.setText(" 棋局  ");
    // helpMenu.setMnemonic('H');
    gameMenu.setForeground(Color.BLACK);
    gameMenu.setFont(headFont);
    menuBar.add(gameMenu);

    final JMenuItem newGameItem = new JMenuItem();
    newGameItem.setText("新的一局（N）");
    // aboutItem.setMnemonic('A');
    newGameItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(newGameItem);

    final JMenuItem continueGameBlackItem = new JMenuItem();
    continueGameBlackItem.setText("续弈(我执黑)(回车)");
    // aboutItem.setMnemonic('A');
    continueGameBlackItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(continueGameBlackItem);

    final JMenuItem continueGameWhiteItem = new JMenuItem();
    continueGameWhiteItem.setText("续弈(我执白)(回车)");
    // aboutItem.setMnemonic('A');
    continueGameWhiteItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(continueGameWhiteItem);

    final JMenuItem breakplay = new JMenuItem();
    breakplay.setText("中断对局");
    breakplay.addActionListener(new ItemListeneryzy());
    gameMenu.add(breakplay);
    gameMenu.addSeparator();

    final JMenuItem setBoard = new JMenuItem();
    setBoard.setText("设置棋盘大小");
    setBoard.addActionListener(new ItemListeneryzy());
    gameMenu.add(setBoard);

    final JMenuItem settime = new JMenuItem();
    settime.setText("设置AI用时");
    settime.addActionListener(new ItemListeneryzy());
    gameMenu.add(settime);

    final JMenuItem setinfo = new JMenuItem();
    setinfo.setText("设置棋局信息(修改贴目)");
    setinfo.addActionListener(new ItemListeneryzy());
    gameMenu.add(setinfo);
    gameMenu.addSeparator();

    final JMenuItem bestone = new JMenuItem();
    bestone.setText("落最佳一手(逗号)");
    bestone.addActionListener(new ItemListeneryzy());
    gameMenu.add(bestone);

    final JMenuItem pass = new JMenuItem();
    pass.setText("停一手(P)");
    pass.addActionListener(new ItemListeneryzy());
    gameMenu.add(pass);
    gameMenu.addSeparator();

    final JMenuItem empty = new JMenuItem();
    empty.setText("清空棋盘（Ctrl+Home）");
    // aboutItem.setMnemonic('A');
    empty.addActionListener(new ItemListeneryzy());
    gameMenu.add(empty);

    final JMenuItem firstItem = new JMenuItem();
    firstItem.setText("跳转到最前（Home）");
    // aboutItem.setMnemonic('A');
    firstItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(firstItem);

    final JMenuItem lastItem = new JMenuItem();
    lastItem.setText("跳转到最后（End）");
    // aboutItem.setMnemonic('A');
    lastItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(lastItem);

    final JMenuItem commetup = new JMenuItem();
    commetup.setText("跳转到左分支(左)");
    // aboutItem.setMnemonic('A');
    commetup.addActionListener(new ItemListeneryzy());
    gameMenu.add(commetup);

    final JMenuItem commetdown = new JMenuItem();
    commetdown.setText("跳转到右分支(右)");
    // aboutItem.setMnemonic('A');
    commetdown.addActionListener(new ItemListeneryzy());
    gameMenu.add(commetdown);

    final JMenuItem setMain = new JMenuItem();
    setMain.setText("设为主分支");
    setMain.addActionListener(new ItemListeneryzy());
    gameMenu.add(setMain);

    setMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.setAsMainBranch()) ;
          }
        });

    final JMenuItem branchStart = new JMenuItem();
    branchStart.setText("返回主分支(CTRL+左)");
    // aboutItem.setMnemonic('A');
    branchStart.addActionListener(new ItemListeneryzy());
    gameMenu.add(branchStart);

    final JMenu analyMenu = new JMenu("分析 ", false);
    analyMenu.setText(" 分析  ");
    analyMenu.setForeground(Color.BLACK);
    analyMenu.setFont(headFont);
    menuBar.add(analyMenu);

    final JMenuItem anaItem = new JMenuItem();
    anaItem.setText("分析/停止（空格）");
    // aboutItem.setMnemonic('A');
    anaItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(anaItem);

    final JMenuItem autoanItem = new JMenuItem();
    autoanItem.setText("自动分析（A）");
    // aboutItem.setMnemonic('A');
    autoanItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(autoanItem);

    final JMenuItem heatItem = new JMenuItem();
    heatItem.setText("策略网络（H）");
    heatItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(heatItem);

    final JMenuItem countsItem = new JMenuItem();
    countsItem.setText("形势判断（点）");
    // aboutItem.setMnemonic('A');
    countsItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(countsItem);
    analyMenu.addSeparator();

    final JMenuItem badmovesItem = new JMenuItem("恶手列表(B)");
    badmovesItem.addActionListener(new ItemListeneryzy()); // 添加动作监听器
    analyMenu.add(badmovesItem); // 添加到“属性”子菜单

    final JMenuItem leelasu = new JMenuItem("AI选点列表(U)");
    leelasu.addActionListener(new ItemListeneryzy()); // 添加动作监听器
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
    blackItem.addActionListener(new ItemListeneryzy());
    editMenu.add(blackItem);
    blackItem.setIcon(iconblack);

    final JMenuItem whiteItem = new JMenuItem();
    whiteItem.setText("落白子");
    // aboutItem.setMnemonic('A');
    whiteItem.addActionListener(new ItemListeneryzy());
    editMenu.add(whiteItem);
    whiteItem.setIcon(iconwhite);

    final JMenuItem bhItem = new JMenuItem();
    bhItem.setText("交替落子");
    // aboutItem.setMnemonic('A');
    bhItem.addActionListener(new ItemListeneryzy());
    editMenu.add(bhItem);
    bhItem.setIcon(iconbh);
    editMenu.addSeparator();

    final JMenuItem insertbItem = new JMenuItem();
    insertbItem.setText("插入黑子");
    // aboutItem.setMnemonic('A');
    insertbItem.addActionListener(new ItemListeneryzy());
    editMenu.add(insertbItem);

    final JMenuItem insertwItem = new JMenuItem();
    insertwItem.setText("插入白子");
    // aboutItem.setMnemonic('A');
    insertwItem.addActionListener(new ItemListeneryzy());
    editMenu.add(insertwItem);

    final JMenuItem bhisItem = new JMenuItem();
    bhisItem.setText("交替插入棋子");
    // aboutItem.setMnemonic('A');
    bhisItem.addActionListener(new ItemListeneryzy());
    editMenu.add(bhisItem);

    editMenu.addSeparator();
    final JMenuItem clearsave = new JMenuItem();
    clearsave.setText("清除Lizzie所有选点缓存");
    // aboutItem.setMnemonic('A');
    clearsave.addActionListener(new ItemListeneryzy());
    editMenu.add(clearsave);

    final JMenuItem clearthis = new JMenuItem();
    clearthis.setText("清除当前选点缓存");
    // aboutItem.setMnemonic('A');
    clearthis.addActionListener(new ItemListeneryzy());
    editMenu.add(clearthis);

    engineMenu = new JMenu("引擎 ", false);
    engineMenu.setText(" 引擎  ");
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

    updateEngineMenuone();
    ArrayList<EngineData> engineData = getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      Lizzie.frame.toolbar.enginePkBlack.addItem("[" + (i + 1) + "]" + engineDt.name);
      Lizzie.frame.toolbar.enginePkWhite.addItem("[" + (i + 1) + "]" + engineDt.name);
    }

    engineMenu.addSeparator();
    closeall = new JMenuItem();
    closeall.setText("关闭所有引擎");
    // aboutItem.setMnemonic('A');
    closeall.addActionListener(new ItemListeneryzy());
    engineMenu.add(closeall);

    forcecloseall = new JMenuItem();
    forcecloseall.setText("强制关闭所有引擎");
    // aboutItem.setMnemonic('A');
    forcecloseall.addActionListener(new ItemListeneryzy());
    engineMenu.add(forcecloseall);

    closeother = new JMenuItem();
    closeother.setText("关闭当前以外引擎");
    // aboutItem.setMnemonic('A');
    closeother.addActionListener(new ItemListeneryzy());
    engineMenu.add(closeother);

    restartZen = new JMenuItem();
    restartZen.setText("重启Zen(形势判断用)");
    // aboutItem.setMnemonic('A');
    restartZen.addActionListener(new ItemListeneryzy());
    engineMenu.add(restartZen);
    engineMenu.addSeparator();

    config = new JMenuItem();
    config.setText("设置");
    config.addActionListener(new ItemListeneryzy());
    engineMenu.add(config);

    moreconfig = new JMenuItem();
    moreconfig.setText("更多引擎设置");
    engineMenu.add(moreconfig);
    moreconfig.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.openMoreEngineDialog();
          }
        });
  }

  public void updateEngineMenuone() {

    for (int i = 0; i < engine.length; i++) {
      engine[i] = new JMenuItem();
      engineMenu.add(engine[i]);
      engine[i].setText("引擎" + (i + 1) + ":");
      engine[i].setVisible(false);
    }
    ArrayList<EngineData> engineData = getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      if (i > (engine.length - 2)) {
        engine[i].setText("更多引擎...");
        engine[i].setVisible(true);
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                JDialog chooseMoreEngine;
                chooseMoreEngine = ChooseMoreEngine.createBadmovesDialog();
                chooseMoreEngine.setVisible(true);
              }
            });
        return;
      } else {
        engine[i].setText("引擎" + (i + 1) + ":" + engineDt.name);
        engine[i].setVisible(true);
        int a = i;
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.engineManager.switchEngine(a);
              }
            });
      }
    }
  }

  public void updateEngineMenu() {

    menuBar.remove(engineMenu);
    engineMenu = new JMenu("引擎 ", false);
    engineMenu.setText(" 引擎  ");
    engineMenu.setForeground(Color.BLACK);
    engineMenu.setFont(headFont);
    menuBar.add(engineMenu);
    for (int i = 0; i < engine.length; i++) {
      try {
        engineMenu.remove(engine[i]);
      } catch (Exception e) {
      }
      engine[i] = new JMenuItem();
      engineMenu.add(engine[i]);
      engine[i].setText("引擎" + (i + 1) + ":");
      engine[i].setVisible(false);
    }
    for (int i = 0; i < Lizzie.engineManager.engineList.size(); i++) {
      if (i <= 20 && Lizzie.engineManager.engineList.get(i).isLoaded()) {
        engine[i].setIcon(ready);
      }
      if (Lizzie.engineManager.engineList.get(i).currentEngineN()
          == Lizzie.engineManager.currentEngineNo) {
        if (i <= 20) engine[i].setIcon(icon);
        engineMenu.setText(
            "引擎" + (i + 1) + ": " + Lizzie.engineManager.engineList.get(i).currentEnginename);
      }
    }
    ArrayList<EngineData> engineData = getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      if (i > (engine.length - 2)) {
        engine[i].setText("更多引擎...");
        engine[i].setVisible(true);
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                JDialog chooseMoreEngine;
                chooseMoreEngine = ChooseMoreEngine.createBadmovesDialog();
                chooseMoreEngine.setVisible(true);
              }
            });
        engineMenu.addSeparator();
        engineMenu.add(closeall);
        engineMenu.add(forcecloseall);
        engineMenu.add(closeother);
        engineMenu.add(restartZen);
        engineMenu.addSeparator();
        engineMenu.add(config);
        engineMenu.add(moreconfig);
        return;
      } else {
        engine[i].setText("引擎" + (i + 1) + ":" + engineDt.name);
        engine[i].setVisible(true);
        int a = i;
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.engineManager.switchEngine(a);
              }
            });
      }
    }
    engineMenu.addSeparator();
    engineMenu.add(closeall);
    engineMenu.add(forcecloseall);
    engineMenu.add(closeother);
    engineMenu.add(restartZen);
    engineMenu.addSeparator();
    engineMenu.add(config);
    engineMenu.add(moreconfig);
  }

  // public void updateEngineName() {
  // Optional<JSONArray> enginesNameOpt =
  // Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
  // enginesNameOpt.ifPresent(
  // a -> {
  // IntStream.range(0, a.length())
  // .forEach(
  // i -> {
  // String name = a.getString(i);
  //
  // if (i == 9) engine[i].setText(engine[i].getText().substring(0, 5) + name);
  // else engine[i].setText(engine[i].getText().substring(0, 4) + name);
  // if (!name.equals("")) engine[i].setVisible(true);
  // else {
  // engine[i].setVisible(false);
  // }
  // });
  // });
  // }

  public ArrayList<EngineData> getEngineData() {
    ArrayList<EngineData> engineData = new ArrayList<EngineData>();
    Optional<JSONArray> enginesCommandOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
    Optional<JSONArray> enginesPreloadOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));

    Optional<JSONArray> enginesWidthOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-width-list"));

    Optional<JSONArray> enginesHeightOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-height-list"));
    Optional<JSONArray> enginesKomiOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-komi-list"));

    int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);

    for (int i = 0;
        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() + 1 : 0);
        i++) {
      if (i == 0) {
        String engineCommand = Lizzie.config.leelazConfig.getString("engine-command");
        int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
        int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
        float komi =
            enginesKomiOpt.isPresent()
                ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
                : (float) 7.5;
        boolean preload =
            enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) : false;
        EngineData enginedt = new EngineData();
        enginedt.commands = engineCommand;
        enginedt.name = name;
        enginedt.preload = preload;
        enginedt.index = i;
        enginedt.width = width;
        enginedt.height = height;
        enginedt.komi = komi;
        if (defaultEngine == i) enginedt.isDefault = true;
        else enginedt.isDefault = false;
        engineData.add(enginedt);
      } else {
        String commands =
            enginesCommandOpt.isPresent() ? enginesCommandOpt.get().optString(i - 1, "") : "";
        if (!commands.equals("")) {
          int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
          int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
          String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
          float komi =
              enginesKomiOpt.isPresent()
                  ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
                  : (float) 7.5;
          boolean preload =
              enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) : false;
          EngineData enginedt = new EngineData();
          enginedt.commands = commands;
          enginedt.name = name;
          enginedt.preload = preload;
          enginedt.index = i;
          enginedt.width = width;
          enginedt.height = height;
          enginedt.komi = komi;
          if (defaultEngine == i) enginedt.isDefault = true;
          else enginedt.isDefault = false;
          engineData.add(enginedt);
        }
      }
    }
    return engineData;
  }

  class ItemListeneryzy implements ActionListener {
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
      if (menuItem.getText().startsWith("强制退出")) {
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
        // if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
        // if (!Lizzie.config.showLargeSubBoard()) Lizzie.config.showLargeSubBoard();
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
      if (menuItem.getText().startsWith("简略")) {
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
      if (menuItem.getText().startsWith("总是显示黑")) {
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
      if (menuItem.getText().startsWith("鼠标所指")) {
        Lizzie.config.showSuggestionVaritions = !Lizzie.config.showSuggestionVaritions;
        Lizzie.config.uiConfig.put(
            "show-suggestion-varitions", Lizzie.config.showSuggestionVaritions);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
          // TODO Auto-generated catch block
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
      if (menuItem.getText().startsWith("显示柱状失")) {
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
      if (menuItem.getText().startsWith("关闭评估")) {
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
