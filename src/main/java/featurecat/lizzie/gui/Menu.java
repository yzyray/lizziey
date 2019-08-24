package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.*;
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
  public static JMenu closeEngine;
  public static MenuBar menuBar;
  JMenuItem closeall;
  JMenuItem forcecloseall;
  JMenuItem closeThis;
  JMenuItem closeother;
  JMenuItem restartZen;
  JMenuItem config;
  JMenuItem moreconfig;
  JButton black;
  JButton white;
  JButton blackwhite;
  Message msg;
  ImageIcon iconblack;
  ImageIcon iconblack2;
  ImageIcon iconwhite;
  ImageIcon iconwhite2;
  ImageIcon iconbh;
  ImageIcon iconbh2;
  // private boolean onlyboard = false;

  public Menu() {

    headFont = new Font("", Font.PLAIN, 12);
    // onlyboard = Lizzie.config.uiConfig.optBoolean("only-board", false);

    final JMenu fileMenu = new JMenu(" 文件  ");
    fileMenu.setForeground(Color.BLACK);
    fileMenu.setFont(headFont);
    setPreferredSize(new Dimension(100, 18));
    this.add(fileMenu);
    final JMenuItem openItem = new JMenuItem("打开棋谱(O)");
    openItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(openItem);

    final JCheckBoxMenuItem readKomi = new JCheckBoxMenuItem();
    readKomi.setText("自动保存棋谱(每10秒)");
    readKomi.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.readKomi = !Lizzie.config.readKomi;
            Lizzie.config.uiConfig.put("read-komi", Lizzie.config.readKomi);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    fileMenu.add(readKomi);

    final JMenuItem openUrlItem = new JMenuItem("打开在线链接(Q)");
    openUrlItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(openUrlItem);

    final JMenuItem saveItem = new JMenuItem();
    saveItem.setText("保存棋谱(S)");
    saveItem.addActionListener(new ItemListeneryzy());
    fileMenu.add(saveItem);

    final JMenuItem saveImage = new JMenuItem();
    saveImage.setText("保存截图(Alt+S)");
    saveImage.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveImage();
          }
        });
    fileMenu.add(saveImage);

    final JMenuItem saveWinrate = new JMenuItem();
    saveWinrate.setText("保存胜率图(Shift+S)");
    saveWinrate.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveImage(
                Lizzie.frame.statx,
                Lizzie.frame.staty,
                (int) (Lizzie.frame.grw * 1.03),
                Lizzie.frame.grh + Lizzie.frame.stath);
          }
        });
    fileMenu.add(saveWinrate);

    fileMenu.addSeparator();
    final JMenuItem copyItem = new JMenuItem();
    copyItem.setText("复制棋谱到剪贴板(Ctrl+C)");
    fileMenu.add(copyItem);
    copyItem.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.copySgf();
          }
        });

    final JMenuItem copyBoard = new JMenuItem("复制主棋盘到剪贴板(Shift+C)");
    fileMenu.add(copyBoard);
    copyBoard.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.savePicToClipboard(
                Lizzie.frame.boardX,
                Lizzie.frame.boardY,
                Lizzie.frame.maxSize,
                Lizzie.frame.maxSize);
          }
        });

    final JMenuItem pasteItem = new JMenuItem();
    pasteItem.setText("从剪贴板粘贴棋谱(Ctrl+V)");
    fileMenu.add(pasteItem);
    fileMenu.addSeparator();
    pasteItem.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.pasteSgf();
          }
        });

    //    if (menuItem.getText().startsWith("打开关闭前")) {
    //        Lizzie.board.resumePreviousGame();
    //        return;
    //      }
    //      if (menuItem.getText().startsWith("关闭自动保存")) {

    //        return;
    //      }
    //
    //      if (menuItem.getText().startsWith("打开自动保存")) {
    //

    //      }

    final JCheckBoxMenuItem autoSave = new JCheckBoxMenuItem();
    autoSave.setText("自动保存棋谱(每10秒)");
    autoSave.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (Lizzie.config.uiConfig.optInt("autosave-interval-seconds", -1) > 0) {
              Lizzie.config.uiConfig.put("autosave-interval-seconds", -1);
              Lizzie.config.uiConfig.put("resume-previous-game", false);
              try {
                Lizzie.config.save();
              } catch (IOException es) {
                // TODO Auto-generated catch block
              }
            } else {
              Lizzie.config.uiConfig.put("autosave-interval-seconds", 10);
              Lizzie.config.uiConfig.put("resume-previous-game", true);
              try {
                Lizzie.config.save();
              } catch (IOException es) {
                // TODO Auto-generated catch block
              }
            }
          }
        });
    fileMenu.add(autoSave);

    final JMenuItem resume = new JMenuItem();
    resume.setText("恢复棋谱");
    fileMenu.add(resume);

    resume.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.resumePreviousGame();
          }
        });

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

    fileMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.uiConfig.optInt("autosave-interval-seconds", -1) > 0)
              autoSave.setState(true);
            else autoSave.setState(false);
            if (Lizzie.config.readKomi) readKomi.setState(true);
            else readKomi.setState(false);
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

    final JMenu viewMenu = new JMenu();
    viewMenu.setText(" 显示  ");
    // editMenu.setMnemonic('E');
    this.add(viewMenu);
    viewMenu.setForeground(Color.BLACK);
    viewMenu.setFont(headFont);

    final JMenu mainboard = new JMenu("主棋盘位置");
    viewMenu.add(mainboard);

    final JMenuItem leftItem = new JMenuItem();
    leftItem.setText("左移 ([)");
    leftItem.addActionListener(new ItemListeneryzy());
    mainboard.add(leftItem);

    final JMenuItem rightItem = new JMenuItem();
    rightItem.setText("右移 (])");
    rightItem.addActionListener(new ItemListeneryzy());
    mainboard.add(rightItem);

    final JCheckBoxMenuItem coordsItem = new JCheckBoxMenuItem();
    coordsItem.setText("坐标(C)");
    coordsItem.addActionListener(new ItemListeneryzy());
    viewMenu.add(coordsItem);

    final JMenu moveMenu = new JMenu("手数(M)");
    viewMenu.add(moveMenu);

    final JMenu Suggestions = new JMenu("推荐点");
    viewMenu.add(Suggestions);

    final JMenu panel = new JMenu("面板");
    viewMenu.add(panel);

    viewMenu.addSeparator();

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
    subItem.setText("放大小棋盘(Alt+F)");
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

    final JCheckBoxMenuItem showname = new JCheckBoxMenuItem("棋盘下方显示黑白名字");
    showname.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showNameInBoard = !Lizzie.config.showNameInBoard;
            Lizzie.config.uiConfig.put("show-name-in-board", Lizzie.config.showNameInBoard);
            Lizzie.board.setForceRefresh(true);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    viewMenu.add(showname);

    final JCheckBoxMenuItem alwaysontop = new JCheckBoxMenuItem();
    alwaysontop.setText("总在最前");
    alwaysontop.addActionListener(new ItemListeneryzy());
    viewMenu.add(alwaysontop);
    viewMenu.addSeparator();

    final JCheckBoxMenuItem suggestion1 = new JCheckBoxMenuItem("胜率");
    suggestion1.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showWinrateInSuggestion = !Lizzie.config.showWinrateInSuggestion;
            Lizzie.config.uiConfig.put(
                "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    Suggestions.add(suggestion1);

    final JCheckBoxMenuItem suggestion2 = new JCheckBoxMenuItem("计算量");
    suggestion2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showPlayoutsInSuggestion = !Lizzie.config.showPlayoutsInSuggestion;
            Lizzie.config.uiConfig.put(
                "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    Suggestions.add(suggestion2);

    final JCheckBoxMenuItem suggestion3 = new JCheckBoxMenuItem("目差");
    suggestion3.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showScoremeanInSuggestion = !Lizzie.config.showScoremeanInSuggestion;
            Lizzie.config.uiConfig.put(
                "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    Suggestions.add(suggestion3);
    Suggestions.addSeparator();

    final JMenu winrate = new JMenu("胜率图设置");
    viewMenu.add(winrate);

    final JCheckBoxMenuItem subboard = new JCheckBoxMenuItem("小棋盘(Z)"); // 创建“字体”子菜单
    panel.add(subboard); // 添加到“编辑”菜单
    subboard.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem winratetMenu = new JCheckBoxMenuItem("胜率图(W)"); // 创建“字体”子菜单
    panel.add(winratetMenu); // 添加到“编辑”菜单
    winratetMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem commitMenu = new JCheckBoxMenuItem("评论面板(T)"); // 创建“字体”子菜单
    panel.add(commitMenu); // 添加到“编辑”菜单
    commitMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem branch = new JCheckBoxMenuItem("分支面板(G)"); // 创建“字体”子菜单
    panel.add(branch); // 添加到“编辑”菜单
    branch.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem topleft = new JCheckBoxMenuItem("左上角面板"); // 创建“字体”子菜单
    panel.add(topleft); // 添加到“编辑”菜单
    topleft.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem bottomleft = new JCheckBoxMenuItem("左下角状态"); // 创建“字体”子菜单
    panel.add(bottomleft); // 添加到“编辑”菜单
    bottomleft.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem gtpMenu = new JCheckBoxMenuItem("Gtp窗口(E)"); // 创建“字体”子菜单
    panel.add(gtpMenu); // 添加到“编辑”菜单
    gtpMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem winratemode1 = new JCheckBoxMenuItem();
    winratemode1.setText("显示双方胜率图");
    winratemode1.addActionListener(new ItemListeneryzy());
    winrate.add(winratemode1);

    final JCheckBoxMenuItem winratemode0 = new JCheckBoxMenuItem();
    winratemode0.setText("显示黑方胜率图");
    winratemode0.addActionListener(new ItemListeneryzy());
    winrate.add(winratemode0);
    winrate.addSeparator();
    // 增加设置胜率曲线宽度

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
    Suggestions.add(showsuggorder);

    final JCheckBoxMenuItem showsuggred = new JCheckBoxMenuItem();
    showsuggred.setText("最高胜率-计算量-目差 反色显示");
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
    Suggestions.add(showsuggred);
    Suggestions.addSeparator();

    final JCheckBoxMenuItem alwaysBlack = new JCheckBoxMenuItem();
    alwaysBlack.setText("总是显示黑胜率");
    alwaysBlack.addActionListener(new ItemListeneryzy());
    Suggestions.add(alwaysBlack);

    final JCheckBoxMenuItem isOnmouse = new JCheckBoxMenuItem();
    isOnmouse.setText("鼠标悬停显示变化图");
    isOnmouse.addActionListener(new ItemListeneryzy());
    Suggestions.add(isOnmouse);

    final JCheckBoxMenuItem noRefreshOnMouse = new JCheckBoxMenuItem();
    noRefreshOnMouse.setText("鼠标悬停显示变化图时,变化图不刷新");
    Suggestions.add(noRefreshOnMouse);

    noRefreshOnMouse.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.noRefreshOnMouseMove = !Lizzie.config.noRefreshOnMouseMove;
            Lizzie.config.uiConfig.put(
                "norefresh-onmouse-move", Lizzie.config.noRefreshOnMouseMove);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JCheckBoxMenuItem blunder = new JCheckBoxMenuItem();
    blunder.setText("显示柱状失误条");
    blunder.addActionListener(new ItemListeneryzy());
    winrate.add(blunder);

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
    Suggestions.add(setReplayTime);

    final JMenu kata = new JMenu("KataGo相关设置");
    viewMenu.add(kata);

    final JMenu heat = new JMenu("热点图设置");
    viewMenu.add(heat);
    viewMenu.addSeparator();

    final JCheckBoxMenuItem showHeat = new JCheckBoxMenuItem("第一感热点图");
    heat.add(showHeat);

    showHeat.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showHeat = true;

            Lizzie.config.showHeatAfterCalc = false;
            Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
            Lizzie.frame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
            Lizzie.frame.subBoardRenderer.clearBranch();
            Lizzie.frame.subBoardRenderer.removeHeat();
            Lizzie.config.uiConfig.put("show-heat", true);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", false);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JCheckBoxMenuItem showHeatAfterCalc = new JCheckBoxMenuItem("计算后热点图");
    heat.add(showHeatAfterCalc);

    showHeatAfterCalc.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showHeat = true;
            Lizzie.config.showHeatAfterCalc = true;
            Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
            Lizzie.frame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
            Lizzie.frame.subBoardRenderer.clearBranch();
            Lizzie.frame.subBoardRenderer.removeHeat();
            Lizzie.config.uiConfig.put("show-heat", true);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", true);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JCheckBoxMenuItem notShowHeat = new JCheckBoxMenuItem("不显示热点图");
    heat.add(notShowHeat);

    notShowHeat.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showHeat = false;
            Lizzie.config.showHeatAfterCalc = false;
            Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
            Lizzie.frame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
            Lizzie.frame.subBoardRenderer.removeHeat();
            Lizzie.config.uiConfig.put("show-heat", false);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", false);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    final JMenuItem defview = new JMenuItem("默认模式"); // 创建“字体”子菜单
    viewMenu.add(defview); // 添加到“编辑”菜单
    defview.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JMenuItem claview = new JMenuItem("经典模式"); // 创建“字体”子菜单
    viewMenu.add(claview); // 添加到“编辑”菜单
    claview.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JMenuItem allview = new JMenuItem("精简模式");
    viewMenu.add(allview);
    allview.addActionListener(new ItemListeneryzy());

    //    final JMenu katasugg = new JMenu("推荐点显示");
    //    kata.add(katasugg);
    //
    //    final JCheckBoxMenuItem katasugg1 = new JCheckBoxMenuItem("胜率+计算量");
    //
    //
    //    katasugg.add(katasugg1);
    //    katasugg1.addActionListener(new ItemListeneryzy());
    //
    //    final JCheckBoxMenuItem katasugg2 = new JCheckBoxMenuItem("目差+计算量");
    //    katasugg.add(katasugg2);
    //    // katasugg2.addActionListener(new ItemListeneryzy());
    //
    //    katasugg2.addActionListener(
    //        new ActionListener() {
    //
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            // TODO Auto-generated method stub
    //            Lizzie.config.showKataGoScoreMean = true;
    //            Lizzie.config.kataGoNotShowWinrate = true;
    //            Lizzie.config.uiConfig.put("show-katago-scoremean",
    // Lizzie.config.showKataGoScoreMean);
    //            Lizzie.config.uiConfig.put(
    //                "katago-notshow-winrate", Lizzie.config.kataGoNotShowWinrate);
    //            try {
    //              Lizzie.config.save();
    //            } catch (IOException es) {
    //              // TODO Auto-generated catch block
    //            }
    //          }
    //        });

    //    final JCheckBoxMenuItem katasugg3 = new JCheckBoxMenuItem("胜率+计算量+目差");
    //    katasugg.add(katasugg3);
    //    katasugg3.addActionListener(new ItemListeneryzy());

    final JMenu kataboard = new JMenu("目差显示");
    kata.add(kataboard);

    final JCheckBoxMenuItem kataboardmean = new JCheckBoxMenuItem("目差");
    kataboard.add(kataboardmean);
    kataboardmean.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataboardboard = new JCheckBoxMenuItem("盘面差");
    kataboard.add(kataboardboard);
    kataboardboard.addActionListener(new ItemListeneryzy());

    final JMenu katameanalways = new JMenu("目差视角");
    kata.add(katameanalways);

    final JCheckBoxMenuItem katameanblack = new JCheckBoxMenuItem("永远为黑视角");
    katameanalways.add(katameanblack);
    katameanblack.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem katameanblackwhite = new JCheckBoxMenuItem("黑白交替视角");
    katameanalways.add(katameanblackwhite);
    katameanblackwhite.addActionListener(new ItemListeneryzy());

    final JMenu katawingraphboard = new JMenu("目差在胜率图上显示");
    kata.add(katawingraphboard);

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

    final JMenu kataEstimate = new JMenu("Kata评估显示");
    kata.add(kataEstimate);

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
    kataEstimate5.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem kataEstimate6 = new JCheckBoxMenuItem("以方块透明度表示占有率");
    kataEstimate.add(kataEstimate6);
    kataEstimate.add(kataEstimate5);
    kataEstimate6.addActionListener(new ItemListeneryzy());

    viewMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.showWinrateInSuggestion) suggestion1.setState(true);
            else suggestion1.setState(false);
            if (Lizzie.config.showPlayoutsInSuggestion) suggestion2.setState(true);
            else suggestion2.setState(false);
            if (Lizzie.config.showScoremeanInSuggestion) suggestion3.setState(true);
            else suggestion3.setState(false);
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
            if (Lizzie.config.noRefreshOnMouseMove) noRefreshOnMouse.setState(true);
            else noRefreshOnMouse.setState(false);
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

            if (Lizzie.config.showHeat) {
              if (Lizzie.config.showHeatAfterCalc) {
                showHeatAfterCalc.setState(true);
                showHeat.setState(false);
                notShowHeat.setState(false);
              } else {
                showHeat.setState(true);
                showHeatAfterCalc.setState(false);
                notShowHeat.setState(false);
              }
            } else {
              notShowHeat.setState(true);
              showHeat.setState(false);
              showHeatAfterCalc.setState(false);
            }
            if (Lizzie.config.showNameInBoard) showname.setState(true);
            else showname.setState(false);
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
    this.add(gameMenu);

    final JMenu newgames = new JMenu("新对局");
    gameMenu.add(newgames);

    final JMenu contgames = new JMenu("人机续弈");
    gameMenu.add(contgames);

    final JMenuItem enginePk = new JMenuItem();
    enginePk.setText("引擎对局(Alt+E)");

    enginePk.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.toolbar.isEnginePk) {
              msg = new Message();
              msg.setMessage("请等待当前引擎对战结束,或使用详细工具栏引擎对战面板中的[终止]按钮中断对战");
              msg.setVisible(true);
              return;
            }
            Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
            Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
            NewEngineGameDialog engineGame = new NewEngineGameDialog();
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            engineGame.setGameInfo(gameInfo);
            engineGame.setVisible(true);
            Lizzie.frame.toolbar.resetEnginePk();
            if (engineGame.isCancelled()) {
              Lizzie.frame.toolbar.chkenginePk.setSelected(false);
              Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
              Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
              return;
            }

            Lizzie.board.getHistory().setGameInfo(gameInfo);
            Lizzie.leelaz.sendCommand("komi " + gameInfo.getKomi());
            Lizzie.frame.komi = gameInfo.getKomi() + "";

            Lizzie.frame.toolbar.chkenginePk.setSelected(true);
            Lizzie.frame.toolbar.isEnginePk = true;
            Lizzie.frame.toolbar.startEnginePk();
          }
        });
    newgames.add(enginePk);

    final JMenuItem newanaGame = new JMenuItem();
    newanaGame.setText("人机对局(N)");
    newanaGame.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            newGame();
          }
        });
    newgames.add(newanaGame);

    final JMenuItem newGameItem = new JMenuItem();
    newGameItem.setText("人机对局(Genmove模式 Alt+N)");
    // aboutItem.setMnemonic('A');
    newGameItem.addActionListener(new ItemListeneryzy());
    newgames.add(newGameItem);

    final JMenuItem continueanaGameBlack = new JMenuItem();
    continueanaGameBlack.setText("续弈[我执黑](回车)");

    continueanaGameBlack.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toolbar.txtAutoPlayTime.setText(
                Lizzie.config
                        .config
                        .getJSONObject("leelaz")
                        .getInt("max-game-thinking-time-seconds")
                    + "");
            Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
            Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlayTime.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlay.setSelected(true);
            Lizzie.frame.toolbar.chkShowBlack.setSelected(false);
            Lizzie.frame.toolbar.chkShowWhite.setSelected(false);
            Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
            Lizzie.frame.toolbar.isAutoPlay = true;
            Lizzie.leelaz.ponder();
          }
        });

    contgames.add(continueanaGameBlack);

    final JMenuItem continueanaGameWhite = new JMenuItem();
    continueanaGameWhite.setText("续弈[我执白](回车)");

    continueanaGameWhite.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toolbar.txtAutoPlayTime.setText(
                Lizzie.config
                        .config
                        .getJSONObject("leelaz")
                        .getInt("max-game-thinking-time-seconds")
                    + "");
            Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
            Lizzie.frame.toolbar.chkAutoPlayTime.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlay.setSelected(true);
            Lizzie.frame.toolbar.chkShowBlack.setSelected(false);
            Lizzie.frame.toolbar.chkShowWhite.setSelected(false);
            Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
            Lizzie.frame.toolbar.isAutoPlay = true;
            Lizzie.leelaz.ponder();
          }
        });

    contgames.add(continueanaGameWhite);

    final JMenuItem continueGameBlackItem = new JMenuItem();
    continueGameBlackItem.setText("续弈[我执黑](Genmove模式 Alt+回车)");
    // aboutItem.setMnemonic('A');
    continueGameBlackItem.addActionListener(new ItemListeneryzy());
    contgames.add(continueGameBlackItem);

    final JMenuItem continueGameWhiteItem = new JMenuItem();
    continueGameWhiteItem.setText("续弈[我执白](Genmove模式 Alt+回车)");
    // aboutItem.setMnemonic('A');
    continueGameWhiteItem.addActionListener(new ItemListeneryzy());
    contgames.add(continueGameWhiteItem);
    gameMenu.addSeparator();
    final JMenuItem breakplay = new JMenuItem();
    breakplay.setText("中断人机对局(空格)");
    breakplay.addActionListener(new ItemListeneryzy());
    gameMenu.add(breakplay);

    final JMenuItem settime = new JMenuItem();
    settime.setText("设置AI用时");
    settime.addActionListener(new ItemListeneryzy());
    gameMenu.add(settime);
    gameMenu.addSeparator();

    final JMenuItem setinfo = new JMenuItem();
    setinfo.setText("设置棋局信息(I)");
    setinfo.addActionListener(new ItemListeneryzy());
    gameMenu.add(setinfo);

    final JMenuItem setBoard = new JMenuItem();
    setBoard.setText("设置棋盘大小(Ctrl+I)");
    setBoard.addActionListener(new ItemListeneryzy());
    gameMenu.add(setBoard);

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
    empty.setText("清空棋盘(Ctrl+Home)");
    // aboutItem.setMnemonic('A');
    empty.addActionListener(new ItemListeneryzy());
    gameMenu.add(empty);

    final JMenuItem setMain = new JMenuItem();
    setMain.setText("设为主分支");
    gameMenu.add(setMain);
    gameMenu.addSeparator();

    setMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            while (Lizzie.board.setAsMainBranch()) ;
          }
        });

    final JMenuItem branchStart = new JMenuItem();
    branchStart.setText("返回上一分支(Ctrl+左)");
    // aboutItem.setMnemonic('A');
    branchStart.addActionListener(new ItemListeneryzy());
    gameMenu.add(branchStart);

    final JMenuItem firstItem = new JMenuItem();
    firstItem.setText("跳转到最前(Home)");
    // aboutItem.setMnemonic('A');
    firstItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(firstItem);

    final JMenuItem lastItem = new JMenuItem();
    lastItem.setText("跳转到最后(End)");
    // aboutItem.setMnemonic('A');
    lastItem.addActionListener(new ItemListeneryzy());
    gameMenu.add(lastItem);

    final JMenuItem commetup = new JMenuItem();
    commetup.setText("跳转到左分支(←)");
    // aboutItem.setMnemonic('A');
    commetup.addActionListener(new ItemListeneryzy());
    gameMenu.add(commetup);

    final JMenuItem commetdown = new JMenuItem();
    commetdown.setText("跳转到右分(→)");
    // aboutItem.setMnemonic('A');
    commetdown.addActionListener(new ItemListeneryzy());
    gameMenu.add(commetdown);

    final JMenu analyMenu = new JMenu("分析 ");
    analyMenu.setText(" 分析  ");
    analyMenu.setForeground(Color.BLACK);
    analyMenu.setFont(headFont);
    this.add(analyMenu);

    final JMenuItem anaItem = new JMenuItem();
    anaItem.setText("分析/停止(空格)");
    // aboutItem.setMnemonic('A');
    anaItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(anaItem);

    final JMenuItem autoPlayItem = new JMenuItem();
    autoPlayItem.setText("自动播放(ALT+A)");
    autoPlayItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            AutoPlay autoPlay = new AutoPlay();
            autoPlay.setVisible(true);
          }
        });
    analyMenu.add(autoPlayItem);

    final JMenuItem autoanItem = new JMenuItem();
    autoanItem.setText("自动分析(A)");
    // aboutItem.setMnemonic('A');
    autoanItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(autoanItem);

    final JMenuItem batchana = new JMenuItem("批量分析(Alt+O)");
    batchana.addActionListener(new ItemListeneryzy());
    analyMenu.add(batchana);

    final JMenuItem batchTable = new JMenuItem("批量分析进度表");
    analyMenu.add(batchTable);
    analyMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openAnalysisTable();
          }
        });
    analyMenu.addSeparator();

    final JMenuItem heatItem = new JMenuItem();
    heatItem.setText("策略网络(H)");
    heatItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(heatItem);

    final JMenuItem countsItem = new JMenuItem();
    countsItem.setText("形势判断(点)");
    // aboutItem.setMnemonic('A');
    countsItem.addActionListener(new ItemListeneryzy());
    analyMenu.add(countsItem);
    analyMenu.addSeparator();

    final JCheckBoxMenuItem badmovesItem = new JCheckBoxMenuItem("恶手列表(B)");
    badmovesItem.addActionListener(new ItemListeneryzy()); // 添加动作监听器
    analyMenu.add(badmovesItem); // 添加到“属性”子菜单

    final JCheckBoxMenuItem leelasu = new JCheckBoxMenuItem("AI选点列表(U)");
    leelasu.addActionListener(new ItemListeneryzy()); // 添加动作监听器
    analyMenu.add(leelasu); // 添加到“属性”子菜单

    analyMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.uiConfig.optBoolean("show-suggestions-frame", false))
              leelasu.setState(true);
            else leelasu.setState(false);
            if (Lizzie.config.uiConfig.optBoolean("show-badmoves-frame", false))
              badmovesItem.setState(true);
            else badmovesItem.setState(false);
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

    final JMenu editMenu = new JMenu("编辑 ", false);
    editMenu.setText(" 编辑  ");
    editMenu.setForeground(Color.BLACK);
    editMenu.setFont(headFont);
    this.add(editMenu);
    iconblack2 = new ImageIcon();
    try {
      iconblack2.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallblack2.png")));
      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconblack = new ImageIcon();
    try {
      iconblack.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallblack.png")));
      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconwhite2 = new ImageIcon();
    try {
      iconwhite2.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite4.png")));
      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconwhite = new ImageIcon();
    try {
      iconwhite.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png")));
      // ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconbh2 = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/menu.png")));
      iconbh2.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/hb2.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconbh = new ImageIcon();
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

    final JCheckBoxMenuItem openEditToolbar = new JCheckBoxMenuItem();
    openEditToolbar.setText("显示落子工具");
    editMenu.add(openEditToolbar);

    openEditToolbar.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showEditbar = !Lizzie.config.showEditbar;
            Lizzie.config.uiConfig.put("show-edit-bar", Lizzie.config.showEditbar);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            toggleShowEditbar(Lizzie.config.showEditbar);
          }
        });

    editMenu.addSeparator();

    final JMenuItem delete = new JMenuItem("删除(Delete)");
    editMenu.add(delete);
    delete.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.deleteMove();
          }
        });

    final JMenuItem deleteBranch = new JMenuItem("删除分支(Shift+Delete)");
    editMenu.add(deleteBranch);
    deleteBranch.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.deleteBranch();
          }
        });

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

    final JCheckBoxMenuItem allowdrag = new JCheckBoxMenuItem();
    allowdrag.setText("允许拖动和双击棋子");
    editMenu.add(allowdrag);

    allowdrag.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.allowDrageDoubleClick = !Lizzie.config.allowDrageDoubleClick;
            Lizzie.config.uiConfig.put(
                "allow-drag-doubleclick", Lizzie.config.allowDrageDoubleClick);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

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

    editMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.allowDrageDoubleClick) allowdrag.setState(true);
            else allowdrag.setState(false);
            if (Lizzie.config.showEditbar) openEditToolbar.setState(true);
            else openEditToolbar.setState(false);
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

    final JMenu live = new JMenu(" 直播  ");
    live.setForeground(Color.BLACK);
    live.setFont(headFont);
    this.add(live);

    final JMenuItem yikeLive = new JMenuItem("弈客直播(Shift+O)");
    live.add(yikeLive);

    yikeLive.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser("https://home.yikeweiqi.com/#/live", "弈客直播");
          }
        });

    final JMenuItem yikeGame = new JMenuItem("弈客大厅");
    live.add(yikeGame);

    yikeGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser("https://home.yikeweiqi.com/#/game", "弈客大厅");
          }
        });

    final JMenuItem readBoard = new JMenuItem("棋盘识别工具(Alt+O)");
    live.add(readBoard);

    readBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openBoardSync();
          }
        });

    live.addSeparator();

    final JCheckBoxMenuItem openHtmlOnLive = new JCheckBoxMenuItem("跳转网页");
    openHtmlOnLive.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.openHtmlOnLive = !Lizzie.config.openHtmlOnLive;
            Lizzie.config.uiConfig.put("open-html-onlive", Lizzie.config.openHtmlOnLive);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    live.add(openHtmlOnLive);

    final JCheckBoxMenuItem alwaysGo = new JCheckBoxMenuItem("总是跳转到最新一步");
    alwaysGo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.alwaysGotoLastOnLive = !Lizzie.config.alwaysGotoLastOnLive;
            Lizzie.config.uiConfig.put(
                "always-gotolast-onlive", Lizzie.config.alwaysGotoLastOnLive);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    live.add(alwaysGo);

    final JMenu readBoardArg = new JMenu("识别工具选项");
    live.add(readBoardArg);

    final JMenu defaultconf = new JMenu("默认平台");
    readBoardArg.add(defaultconf);

    final JCheckBoxMenuItem yehu = new JCheckBoxMenuItem("野狐");
    yehu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.readBoardArg1 = "0";
            Lizzie.config.uiConfig.put("read-board-arg1", Lizzie.config.readBoardArg1);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    defaultconf.add(yehu);

    final JCheckBoxMenuItem yicheng = new JCheckBoxMenuItem("弈城");
    yicheng.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.readBoardArg1 = "1";
            Lizzie.config.uiConfig.put("read-board-arg1", Lizzie.config.readBoardArg1);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    defaultconf.add(yicheng);

    final JCheckBoxMenuItem xinlang = new JCheckBoxMenuItem("新浪");
    xinlang.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.readBoardArg1 = "2";
            Lizzie.config.uiConfig.put("read-board-arg1", Lizzie.config.readBoardArg1);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    defaultconf.add(xinlang);

    final JCheckBoxMenuItem other = new JCheckBoxMenuItem("其他");
    other.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.readBoardArg1 = "3";
            Lizzie.config.uiConfig.put("read-board-arg1", Lizzie.config.readBoardArg1);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    defaultconf.add(other);

    final JCheckBoxMenuItem noticeLast = new JCheckBoxMenuItem("识别最后一手(关闭可加速,但首次同步后轮谁下可能错误)");
    noticeLast.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.readBoardArg3 = !Lizzie.config.readBoardArg3;
            Lizzie.config.uiConfig.put("read-board-arg3", Lizzie.config.readBoardArg3);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    readBoardArg.add(noticeLast);

    final JMenuItem setTime = new JMenuItem("设置持续同步间隔");
    setTime.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetBoardSyncTime setBoardSyncTime = new SetBoardSyncTime();
            setBoardSyncTime.setVisible(true);
          }
        });
    readBoardArg.add(setTime);

    final JCheckBoxMenuItem alwaysSyncBoardStat = new JCheckBoxMenuItem("回退时保持棋盘一致(可能破坏历史手顺)");
    alwaysSyncBoardStat.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.alwaysSyncBoardStat = !Lizzie.config.alwaysSyncBoardStat;
            Lizzie.config.uiConfig.put("always-sync-boardstat", Lizzie.config.alwaysSyncBoardStat);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });
    readBoardArg.add(alwaysSyncBoardStat);

    // readBoardArg1= uiConfig.optString("read-board-arg1"

    live.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.openHtmlOnLive) openHtmlOnLive.setState(true);
            else openHtmlOnLive.setState(false);
            if (Lizzie.config.alwaysGotoLastOnLive) alwaysGo.setState(true);
            else alwaysGo.setState(false);
            if (Lizzie.config.readBoardArg1.equals("0")) {
              yehu.setState(true);
              yicheng.setState(false);
              other.setState(false);
            }
            if (Lizzie.config.readBoardArg1.equals("1")) {
              yicheng.setState(true);
              yehu.setState(false);
              other.setState(false);
            }
            if (Lizzie.config.readBoardArg1.equals("2")) {
              other.setState(true);
              yicheng.setState(false);
              yehu.setState(false);
            }
            if (Lizzie.config.alwaysSyncBoardStat) alwaysSyncBoardStat.setState(true);
            else alwaysSyncBoardStat.setState(false);
            if (Lizzie.config.readBoardArg3) noticeLast.setState(true);
            else noticeLast.setState(false);
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
    final JMenu toolbar = new JMenu(" 工具栏  ");
    toolbar.setForeground(Color.BLACK);
    toolbar.setFont(headFont);
    this.add(toolbar);

    final JCheckBoxMenuItem toolMenu = new JCheckBoxMenuItem("基本工具栏"); // 创建“字体”子菜单
    toolbar.add(toolMenu); // 添加到“编辑”菜单
    toolMenu.addActionListener(new ItemListeneryzy()); // 添加动作监听器

    final JCheckBoxMenuItem bigtoolMenu = new JCheckBoxMenuItem("详细工具栏");
    toolbar.add(bigtoolMenu);
    bigtoolMenu.addActionListener(new ItemListeneryzy());

    final JCheckBoxMenuItem closeTool = new JCheckBoxMenuItem("关闭工具栏"); // 创建“字体”子菜单
    toolbar.add(closeTool); // 添加到“编辑”菜单
    closeTool.addActionListener(new ItemListeneryzy()); // 添加动作监听器 viewMenu.addSeparator();

    final JMenuItem bigtoolConf = new JMenuItem("设置详细工具栏顺序");
    toolbar.add(bigtoolConf);
    bigtoolConf.addActionListener(new ItemListeneryzy());

    toolbar.addSeparator();

    final JMenu chooseButton = new JMenu("自定义显示按钮");
    toolbar.add(chooseButton);

    final JCheckBoxMenuItem liveButton = new JCheckBoxMenuItem("直播");
    chooseButton.add(liveButton);
    liveButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.liveButton = !Lizzie.config.liveButton;
            Lizzie.config.uiConfig.put("liveButton", Lizzie.config.liveButton);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem kataEstimateButton = new JCheckBoxMenuItem("Kata评估");
    chooseButton.add(kataEstimateButton);
    kataEstimateButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.kataEstimate = !Lizzie.config.kataEstimate;
            Lizzie.config.uiConfig.put("kataEstimate", Lizzie.config.kataEstimate);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem batchOpen = new JCheckBoxMenuItem("批量分析");
    chooseButton.add(batchOpen);
    batchOpen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.batchOpen = !Lizzie.config.batchOpen;
            Lizzie.config.uiConfig.put("batchOpen", Lizzie.config.batchOpen);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem openfile = new JCheckBoxMenuItem("打开");
    chooseButton.add(openfile);
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.openfile = !Lizzie.config.openfile;
            Lizzie.config.uiConfig.put("openfile", Lizzie.config.openfile);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem savefile = new JCheckBoxMenuItem("保存");
    chooseButton.add(savefile);
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.savefile = !Lizzie.config.savefile;
            Lizzie.config.uiConfig.put("savefile", Lizzie.config.savefile);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem komi = new JCheckBoxMenuItem("贴目");
    chooseButton.add(komi);
    komi.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.komi = !Lizzie.config.komi;
            Lizzie.config.uiConfig.put("komi", Lizzie.config.komi);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem refresh = new JCheckBoxMenuItem("刷新");
    chooseButton.add(refresh);
    refresh.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.refresh = !Lizzie.config.refresh;
            Lizzie.config.uiConfig.put("refresh", Lizzie.config.refresh);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem analyse = new JCheckBoxMenuItem("分析");
    chooseButton.add(analyse);
    analyse.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.analyse = !Lizzie.config.analyse;
            Lizzie.config.uiConfig.put("analyse", Lizzie.config.analyse);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem tryPlay = new JCheckBoxMenuItem("试下");
    chooseButton.add(tryPlay);
    tryPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.tryPlay = !Lizzie.config.tryPlay;
            Lizzie.config.uiConfig.put("tryPlay", Lizzie.config.tryPlay);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem setMainButton = new JCheckBoxMenuItem("设为主分支");
    chooseButton.add(setMainButton);
    setMainButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.setMain = !Lizzie.config.setMain;
            Lizzie.config.uiConfig.put("setMain", Lizzie.config.setMain);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem backMain = new JCheckBoxMenuItem("返回主分支");
    chooseButton.add(backMain);
    backMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.backMain = !Lizzie.config.backMain;
            Lizzie.config.uiConfig.put("backMain", Lizzie.config.backMain);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem clearButton = new JCheckBoxMenuItem("清空棋盘");
    chooseButton.add(clearButton);
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.clearButton = !Lizzie.config.clearButton;
            Lizzie.config.uiConfig.put("clearButton", Lizzie.config.clearButton);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem countButton = new JCheckBoxMenuItem("形势判断");
    chooseButton.add(countButton);
    countButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.countButton = !Lizzie.config.countButton;
            Lizzie.config.uiConfig.put("countButton", Lizzie.config.countButton);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem heatMap = new JCheckBoxMenuItem("策略网络");
    chooseButton.add(heatMap);
    heatMap.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.heatMap = !Lizzie.config.heatMap;
            Lizzie.config.uiConfig.put("heatMap", Lizzie.config.heatMap);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem badMoves = new JCheckBoxMenuItem("恶手列表");
    chooseButton.add(badMoves);
    badMoves.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.badMoves = !Lizzie.config.badMoves;
            Lizzie.config.uiConfig.put("badMoves", Lizzie.config.badMoves);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem move = new JCheckBoxMenuItem("手数");
    chooseButton.add(move);
    move.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.move = !Lizzie.config.move;
            Lizzie.config.uiConfig.put("move", Lizzie.config.move);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem coords = new JCheckBoxMenuItem("坐标");
    chooseButton.add(coords);
    coords.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.coords = !Lizzie.config.coords;
            Lizzie.config.uiConfig.put("coords", Lizzie.config.coords);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    final JCheckBoxMenuItem autoPlay = new JCheckBoxMenuItem("自动播放");
    chooseButton.add(autoPlay);
    autoPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.autoPlay = !Lizzie.config.autoPlay;
            Lizzie.config.uiConfig.put("autoPlay", Lizzie.config.autoPlay);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            Lizzie.frame.toolbar.reSetButtonLocation();
          }
        });

    chooseButton.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {

            if (Lizzie.config.liveButton) liveButton.setState(true);
            else liveButton.setState(false);
            if (Lizzie.config.kataEstimate) kataEstimateButton.setState(true);
            else kataEstimateButton.setState(false);
            if (Lizzie.config.batchOpen) batchOpen.setState(true);
            else batchOpen.setState(false);
            if (Lizzie.config.openfile) openfile.setState(true);
            else openfile.setState(false);
            if (Lizzie.config.savefile) savefile.setState(true);
            else savefile.setState(false);
            if (Lizzie.config.komi) komi.setState(true);
            else komi.setState(false);

            if (Lizzie.config.refresh) refresh.setState(true);
            else refresh.setState(false);
            if (Lizzie.config.analyse) analyse.setState(true);
            else analyse.setState(false);
            if (Lizzie.config.tryPlay) tryPlay.setState(true);
            else tryPlay.setState(false);
            if (Lizzie.config.setMain) setMainButton.setState(true);
            else setMainButton.setState(false);
            if (Lizzie.config.backMain) backMain.setState(true);
            else backMain.setState(false);

            if (Lizzie.config.clearButton) clearButton.setState(true);
            else clearButton.setState(false);
            if (Lizzie.config.countButton) countButton.setState(true);
            else countButton.setState(false);
            if (Lizzie.config.heatMap) heatMap.setState(true);
            else heatMap.setState(false);
            if (Lizzie.config.badMoves) badMoves.setState(true);
            else badMoves.setState(false);
            if (Lizzie.config.move) move.setState(true);
            else move.setState(false);
            if (Lizzie.config.coords) coords.setState(true);
            else coords.setState(false);
            if (Lizzie.config.autoPlay) autoPlay.setState(true);
            else autoPlay.setState(false);
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

    live.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.frame.toolbarHeight == 26) toolMenu.setState(true);
            else toolMenu.setState(false);
            if (Lizzie.frame.toolbarHeight == 70) bigtoolMenu.setState(true);
            else bigtoolMenu.setState(false);
            if (Lizzie.frame.toolbarHeight == 0) closeTool.setState(true);
            else closeTool.setState(false);
            if (Lizzie.config.liveButton) liveButton.setState(true);
            else liveButton.setState(false);
            if (Lizzie.config.kataEstimate) kataEstimateButton.setState(true);
            else kataEstimateButton.setState(false);
            if (Lizzie.config.batchOpen) batchOpen.setState(true);
            else batchOpen.setState(false);
            if (Lizzie.config.openfile) openfile.setState(true);
            else openfile.setState(false);
            if (Lizzie.config.savefile) savefile.setState(true);
            else savefile.setState(false);
            if (Lizzie.config.komi) komi.setState(true);
            else komi.setState(false);
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

    final JMenu settings = new JMenu(" 设置  ");
    settings.setForeground(Color.BLACK);
    settings.setFont(headFont);
    this.add(settings);

    final JMenuItem engine = new JMenuItem("引擎(Ctrl+X)");
    settings.add(engine);

    engine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog();
          }
        });

    final JMenuItem moreengine = new JMenuItem("更多引擎");
    settings.add(moreengine);

    moreengine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openMoreEngineDialog();
          }
        });

    final JMenuItem allConfig = new JMenuItem("界面(Shift+X)");
    settings.add(allConfig);

    allConfig.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog2(0);
          }
        });

    final JMenuItem theme = new JMenuItem("主题");
    settings.add(theme);

    theme.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog2(1);
          }
        });

    final JMenuItem about = new JMenuItem("关于");
    settings.add(about);

    about.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog2(1);
          }
        });

    settings.addSeparator();
    final JCheckBoxMenuItem sound = new JCheckBoxMenuItem("播放落子声");
    settings.add(sound);

    sound.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.playSound = !Lizzie.config.playSound;
            Lizzie.config.uiConfig.put("play-sound", Lizzie.config.playSound);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
          }
        });

    settings.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.playSound) sound.setState(true);
            else sound.setState(false);
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

    engineMenu = new JMenu("引擎 ", false);
    engineMenu.setText(" 引擎  ");
    engineMenu.setForeground(Color.BLACK);
    headFont = new Font("", Font.BOLD, 15);
    engineMenu.setFont(headFont);
    headFont = new Font("", Font.PLAIN, 12);
    this.add(engineMenu);

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

    closeEngine = new JMenu("关闭引擎 ", false);
    closeEngine.setText("关闭引擎");
    engineMenu.add(closeEngine);

    closeThis = new JMenuItem();
    closeThis.setText("关闭当前引擎");

    closeThis.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.killThisEngines();
            engineMenu.setText("未加载引擎");
          }
        });
    closeEngine.add(closeThis);

    closeother = new JMenuItem();
    closeother.setText("关闭当前以外引擎");
    // aboutItem.setMnemonic('A');
    closeother.addActionListener(new ItemListeneryzy());
    closeEngine.add(closeother);

    closeall = new JMenuItem();
    closeall.setText("关闭所有引擎");
    // aboutItem.setMnemonic('A');
    closeall.addActionListener(new ItemListeneryzy());
    closeEngine.add(closeall);

    forcecloseall = new JMenuItem();
    forcecloseall.setText("强制关闭所有引擎");
    // aboutItem.setMnemonic('A');
    forcecloseall.addActionListener(new ItemListeneryzy());
    closeEngine.add(forcecloseall);

    restartZen = new JMenuItem();
    restartZen.setText("重启Zen(形势判断用)");
    // aboutItem.setMnemonic('A');
    restartZen.addActionListener(new ItemListeneryzy());
    engineMenu.add(restartZen);
    //    engineMenu.addSeparator();

    //    config = new JMenuItem();
    //    config.setText("设置");
    //    config.addActionListener(new ItemListeneryzy());
    //    engineMenu.add(config);

    //    moreconfig = new JMenuItem();
    //    moreconfig.setText("更多引擎设置");
    //    engineMenu.add(moreconfig);
    //    moreconfig.addActionListener(
    //        new ActionListener() {
    //
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            // TODO Auto-generated method stub
    //            Lizzie.frame.openMoreEngineDialog();
    //          }
    //        });
    black = new JButton(iconblack);
    black.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (Lizzie.frame.blackorwhite != 1) {
              Lizzie.frame.blackorwhite = 1;
              black.setIcon(iconblack2);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            } else {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    black.setFocusable(false);
    black.setMargin(new Insets(0, 0, 0, 0));
    this.add(black);
    black.setToolTipText("落黑子");

    white = new JButton(iconwhite);
    white.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (Lizzie.frame.blackorwhite != 2) {
              Lizzie.frame.blackorwhite = 2;
              black.setIcon(iconblack);
              white.setIcon(iconwhite2);
              blackwhite.setIcon(iconbh);
            } else {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    white.setFocusable(false);
    white.setMargin(new Insets(0, 0, 0, 0));
    this.add(white);
    white.setToolTipText("落白子");

    blackwhite = new JButton(iconbh);
    blackwhite.addActionListener(
        new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (blackwhite.getIcon() == iconbh) {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh2);
            } else {
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    blackwhite.setFocusable(false);
    blackwhite.setMargin(new Insets(0, -2, 0, -2));
    this.add(blackwhite);
    blackwhite.setToolTipText("交替落子");
    toggleShowEditbar(Lizzie.config.showEditbar);
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

    this.remove(engineMenu);
    engineMenu = new JMenu("引擎 ", false);
    engineMenu.setText(" 引擎  ");
    engineMenu.setForeground(Color.BLACK);
    headFont = new Font("", Font.BOLD, 15);
    engineMenu.setFont(headFont);
    this.add(engineMenu);
    this.remove(black);
    this.remove(white);
    this.remove(blackwhite);

    black = new JButton(iconblack);
    black.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (Lizzie.frame.blackorwhite != 1) {
              Lizzie.frame.blackorwhite = 1;
              black.setIcon(iconblack2);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            } else {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    black.setFocusable(false);
    black.setMargin(new Insets(0, 0, 0, 0));
    this.add(black);
    black.setToolTipText("落黑子");

    white = new JButton(iconwhite);
    white.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (Lizzie.frame.blackorwhite != 2) {
              Lizzie.frame.blackorwhite = 2;
              black.setIcon(iconblack);
              white.setIcon(iconwhite2);
              blackwhite.setIcon(iconbh);
            } else {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    white.setFocusable(false);
    white.setMargin(new Insets(0, 0, 0, 0));
    this.add(white);
    white.setToolTipText("落白子");

    blackwhite = new JButton(iconbh);
    blackwhite.addActionListener(
        new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (blackwhite.getIcon() == iconbh) {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh2);
            } else {
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    blackwhite.setFocusable(false);
    blackwhite.setMargin(new Insets(0, -2, 0, -2));
    this.add(blackwhite);
    blackwhite.setToolTipText("交替落子");
    toggleShowEditbar(Lizzie.config.showEditbar);

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
      if (i <= 20
          && Lizzie.engineManager.engineList.get(i).isLoaded()
          && Lizzie.engineManager.engineList.get(i).process.isAlive()) {
        engine[i].setIcon(ready);
      }
      if (i == Lizzie.engineManager.currentEngineNo && i <= 20) {
        engine[i].setIcon(icon);
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
        engineMenu.add(closeEngine);
        closeEngine.add(closeThis);
        closeEngine.add(closeother);
        closeEngine.add(closeall);
        closeEngine.add(forcecloseall);

        engineMenu.add(restartZen);
        //  engineMenu.addSeparator();
        //  engineMenu.add(config);
        //  engineMenu.add(moreconfig);
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
    engineMenu.add(closeEngine);
    closeEngine.add(closeThis);
    closeEngine.add(closeother);
    closeEngine.add(closeall);
    closeEngine.add(forcecloseall);
    engineMenu.add(restartZen);
    // engineMenu.addSeparator();
    // engineMenu.add(config);
    //   engineMenu.add(moreconfig);
  }

  public void changeEngineIcon(int index, int mode) {
    if (index > 20) index = 20;

    if (mode == 0) engine[index].setIcon(null);
    if (mode == 1) engine[index].setIcon(stop);
    if (mode == 2) engine[index].setIcon(ready);
    if (mode == 3) engine[index].setIcon(icon);
  }

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

  public void changeicon() {

    for (int i = 0; i < 21; i++) {
      if (i < Lizzie.engineManager.engineList.size()
          && !Lizzie.engineManager.engineList.get(i).isStarted())
        featurecat.lizzie.gui.Menu.engine[i].setIcon(null);
      else if (featurecat.lizzie.gui.Menu.engine[i].getIcon() != null
          && featurecat.lizzie.gui.Menu.engine[i].getIcon() != featurecat.lizzie.gui.Menu.stop) {
        featurecat.lizzie.gui.Menu.engine[i].setIcon(featurecat.lizzie.gui.Menu.ready);
      }
    }
    if (Lizzie.engineManager.currentEngineNo <= 20) {
      if (featurecat.lizzie.gui.Menu.engine[Lizzie.engineManager.currentEngineNo].getIcon()
          == null) {
      } else {
        featurecat.lizzie.gui.Menu.engine[Lizzie.engineManager.currentEngineNo].setIcon(
            featurecat.lizzie.gui.Menu.icon);
      }
    }
  }

  public void newGame() {
    Lizzie.frame.isPlayingAgainstLeelaz = false;
    // GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    NewAnaGameDialog newgame = new NewAnaGameDialog();
    // newgame.setGameInfo(gameInfo);
    newgame.setVisible(true);
    newgame.dispose();
    if (newgame.isCancelled()) return;
    GameInfo gameInfo = newgame.gameInfo;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
    Lizzie.leelaz.sendCommand("komi " + gameInfo.getKomi());
    Lizzie.frame.komi = gameInfo.getKomi() + "";
    Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
    Lizzie.frame.toolbar.isAutoPlay = true;
    Lizzie.leelaz.ponder();
  }

  public void toggleShowEditbar(boolean show) {
    this.black.setVisible(show);
    this.white.setVisible(show);
    this.blackwhite.setVisible(show);
  }

  class ItemListeneryzy implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JMenuItem menuItem = (JMenuItem) e.getSource();
      // System.out.println("您单击的是菜单项：" + menuItem.getText());
      Lizzie.frame.setVisible(true);
      if (menuItem.getText().startsWith("打开棋谱")) {
        Lizzie.frame.openFileAll();
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
        Lizzie.frame.refresh();
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
      if (menuItem.getText().startsWith("胜率图")) {
        Lizzie.config.toggleShowWinrate();
        return;
      }
      if (menuItem.getText().startsWith("Gtp")) {
        Lizzie.frame.toggleGtpConsole();
        return;
      }
      if (menuItem.getText().startsWith("左移")) {
        if (Lizzie.frame.BoardPositionProportion > 0) Lizzie.frame.BoardPositionProportion--;
        Lizzie.frame.refresh();
        return;
      }
      if (menuItem.getText().startsWith("右移")) {
        if (Lizzie.frame.BoardPositionProportion < 8) Lizzie.frame.BoardPositionProportion++;
        Lizzie.frame.refresh();
        return;
      }
      if (menuItem.getText().startsWith("人机对局")) {
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
          Lizzie.leelaz.togglePonder();
        }
        if (Lizzie.frame.toolbar.isAutoAna) {
          Lizzie.frame.toolbar.isAutoAna = false;
          Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(false);
        }
        if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
          Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
          Lizzie.frame.toolbar.chkAutoPlay.setSelected(false);
          Lizzie.frame.toolbar.isAutoPlay = false;
          Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
          Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
          Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
          Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
          Lizzie.leelaz.togglePonder();
        }
        Lizzie.leelaz.togglePonder();
        return;
      }

      if (menuItem.getText().startsWith("交替落")) {
        featurecat.lizzie.gui.Input.insert = 0;
        Lizzie.frame.blackorwhite = 0;
        black.setIcon(iconblack);
        white.setIcon(iconwhite);
        blackwhite.setIcon(iconbh2);
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
        black.setIcon(iconblack2);
        white.setIcon(iconwhite);
        blackwhite.setIcon(iconbh);
        return;
      }
      if (menuItem.getText().startsWith("落白")) {
        Lizzie.frame.blackorwhite = 2;
        black.setIcon(iconblack);
        white.setIcon(iconwhite2);
        blackwhite.setIcon(iconbh);
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
        Lizzie.config.onlyLastMoveNumber = 1;
        Lizzie.config.uiConfig.put("only-last-move-number", 1);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }
        return;
      }
      if (menuItem.getText().startsWith("最近5手")) {
        Lizzie.config.allowMoveNumber = 5;
        Lizzie.config.uiConfig.put("allow-move-number", 5);
        Lizzie.config.onlyLastMoveNumber = 5;
        Lizzie.config.uiConfig.put("only-last-move-number", 5);
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }
        return;
      }
      if (menuItem.getText().startsWith("最近10手")) {
        Lizzie.config.allowMoveNumber = 10;
        Lizzie.config.uiConfig.put("allow-move-number", 10);
        Lizzie.config.onlyLastMoveNumber = 10;
        Lizzie.config.uiConfig.put("only-last-move-number", 10);
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
        try {
          Lizzie.config.save();
        } catch (IOException es) {
        }
        return;
      }
      if (menuItem.getText().startsWith("自动分")) {
        StartAnaDialog newgame = new StartAnaDialog();
        newgame.setVisible(true);
        if (newgame.isCancelled()) {
          Lizzie.frame.toolbar.resetAutoAna();
          return;
        }
        return;
      }

      if (menuItem.getText().startsWith("返回上一分支")) {
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
      if (menuItem.getText().startsWith("中断人机")) {
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
          Lizzie.leelaz.ponder();
        }
        if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
          Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
          Lizzie.frame.toolbar.chkAutoPlay.setSelected(false);
          Lizzie.frame.toolbar.isAutoPlay = false;
          Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
          Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
          Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
          Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
          Lizzie.leelaz.ponder();
        }

        return;
      }
      if (menuItem.getText().startsWith("基本")) {
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
        Lizzie.frame.openFileWithAna();
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
      if (menuItem.getText().startsWith("鼠标悬停")) {
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
      //      if (menuItem.getText().startsWith("胜率+计算量+")) {
      //        Lizzie.config.showKataGoScoreMean = true;
      //        Lizzie.config.kataGoNotShowWinrate = false;
      //        Lizzie.config.uiConfig.put("show-katago-scoremean",
      // Lizzie.config.showKataGoScoreMean);
      //        Lizzie.config.uiConfig.put("katago-notshow-winrate",
      // Lizzie.config.kataGoNotShowWinrate);
      //        try {
      //          Lizzie.config.save();
      //        } catch (IOException es) {
      //          // TODO Auto-generated catch block
      //        }
      //        return;
      //      }
      //      if (menuItem.getText().startsWith("胜率+计")) {
      //        Lizzie.config.showKataGoScoreMean = false;
      //        Lizzie.config.uiConfig.put("show-katago-scoremean",
      // Lizzie.config.showKataGoScoreMean);
      //        try {
      //          Lizzie.config.save();
      //        } catch (IOException es) {
      //          // TODO Auto-generated catch block
      //        }
      //        return;
      //      }
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
