package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.CountResults;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Stone;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ReadBoard {
  public Process process;

  private BufferedInputStream inputStream;
  private BufferedOutputStream outputStream;

  // public boolean gtpConsole;

  // private boolean isLoaded = false;

  private String engineCommand;
  // private List<String> commands;
  //  private int cmdNumber;
  // private int currentCmdNum;
  private ArrayDeque<String> cmdQueue;
  private ScheduledExecutorService executor;
  public String currentEnginename = "";
  ArrayList<Integer> tempcount = new ArrayList<Integer>();
  CountResults results;
  boolean firstcount = true;
  public int numberofcount = 0;
  public boolean firstSync = true;
  public boolean syncBoth = Lizzie.config.syncBoth;

  public ReadBoard() throws IOException {

    // cmdNumber = 1;
    // currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();
    //  gtpConsole = true;
    engineCommand = "readboard\\readboard.exe";
    startEngine(engineCommand, 0);
  }

  public void startEngine(String engineCommand, int index) {

    List<String> commands = new ArrayList<String>();
    commands.add(engineCommand);
    commands.add("yzy");
    commands.add(Lizzie.config.readBoardArg1);
    commands.add(Lizzie.config.readBoardArg2 + "");
    if (Lizzie.config.readBoardArg3) {
      commands.add("0");
    } else {
      commands.add("1");
    }
    if (syncBoth) {
      commands.add("0");
    } else {
      commands.add("1");
    }
    commands.add(
        Lizzie.frame.toolbar.txtAutoPlayTime.getText().equals("")
            ? " "
            : Lizzie.frame.toolbar.txtAutoPlayTime.getText());
    commands.add(
        Lizzie.frame.toolbar.txtAutoPlayPlayouts.getText().equals("")
            ? " "
            : Lizzie.frame.toolbar.txtAutoPlayPlayouts.getText());
    commands.add(
        Lizzie.frame.toolbar.txtAutoPlayFirstPlayouts.getText().equals("")
            ? " "
            : Lizzie.frame.toolbar.txtAutoPlayFirstPlayouts.getText());
    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.redirectErrorStream(true);
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      Message msg = new Message();
      msg.setMessage("加载棋盘识别工具失败,请检查目录下readboard文件夹文件夹内dm.dll,dmc.dll,readboard.exe等文件是否存在");
      msg.setVisible(true);

      return;
    }
    initializeStreams();

    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
  }

  private void initializeStreams() {
    inputStream = new BufferedInputStream(process.getInputStream());
    outputStream = new BufferedOutputStream(process.getOutputStream());
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      // while ((c = inputStream.read()) != -1) {
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);

        if ((c == '\n')) {
          try {
            parseLine(line.toString());
          } catch (Exception ex) {
          }
          line = new StringBuilder();
        }
      }
      // this line will be reached when BoardSync shuts down
      System.out.println("BoardSync process ended.");

      shutdown();
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      e.printStackTrace();
      Lizzie.frame.bothSync = false;
      Lizzie.frame.syncBoard = false;
      // System.exit(-1);
    }
  }

  private void parseLine(String line) {
    synchronized (this) {
      // if (Lizzie.gtpConsole.isVisible()) Lizzie.gtpConsole.addLineforce(line);
      if (line.startsWith("re=")) {

        String[] params = line.substring(3, line.length() - 2).split(",");
        if (params.length == Lizzie.board.boardWidth) {
          for (int i = 0; i < params.length; i++) tempcount.add(Integer.parseInt(params[i]));
        }
      }
      if (line.startsWith("end")) {
        syncBoardStones();
        tempcount = new ArrayList<Integer>();
      }
      if (line.startsWith("clear")) {
        Lizzie.board.clear();
        Lizzie.frame.refresh();
      }
      if (line.startsWith("start")) {
        String[] params = line.trim().split(" ");
        if (params.length == 3) {
          int boardWidth = Integer.parseInt(params[1]);
          if (boardWidth != Lizzie.board.boardWidth || boardWidth != Lizzie.board.boardHeight) {
            Lizzie.board.reopen(boardWidth, boardWidth);
          } else {
            Lizzie.board.clear();
          }
        } else {
          Lizzie.board.clear();
        }
      }
      if (line.startsWith("sync")) {
        Lizzie.frame.syncBoard = true;
        if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
      }
      if (line.startsWith("both")) {
        Lizzie.frame.bothSync = true;
      }
      if (line.startsWith("noboth")) {
        Lizzie.frame.bothSync = false;
      }
      if (line.startsWith("stopsync")) {
        Lizzie.frame.syncBoard = false;
        if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
          Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
          Lizzie.frame.toolbar.chkAutoPlay.setSelected(false);
          Lizzie.frame.toolbar.isAutoPlay = false;
          Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
          Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
          Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
          Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
        }
        Lizzie.leelaz.nameCmd();
        Lizzie.leelaz.notPondering();
      }
      if (line.startsWith("play")) {
        String[] params = line.trim().split(">");
        if (params.length == 3) {
          String[] playParams = params[2].trim().split(" ");
          int playouts = Integer.parseInt(playParams[1]);
          int firstPlayouts = Integer.parseInt(playParams[2]);
          Lizzie.frame.toolbar.txtAutoPlayTime.setText(playParams[0]);
          Lizzie.frame.toolbar.chkAutoPlayTime.setSelected(true);
          if (playouts > 0) {
            Lizzie.frame.toolbar.txtAutoPlayPlayouts.setText(playouts + "");
            Lizzie.frame.toolbar.chkAutoPlayPlayouts.setSelected(true);
          } else Lizzie.frame.toolbar.chkAutoPlayPlayouts.setSelected(false);
          if (firstPlayouts > 0) {
            Lizzie.frame.toolbar.txtAutoPlayFirstPlayouts.setText(firstPlayouts + "");
            Lizzie.frame.toolbar.chkAutoPlayFirstPlayouts.setSelected(true);
          } else Lizzie.frame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);
          if (params[1].equals("black")) {
            Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);

            Lizzie.frame.toolbar.chkAutoPlay.setSelected(true);
            Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
            Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
            Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
            Lizzie.frame.toolbar.isAutoPlay = true;
          } else if (params[1].equals("white")) {
            Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
            Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlay.setSelected(true);
            Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
            Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
            Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
            Lizzie.frame.toolbar.isAutoPlay = true;
          }
          Lizzie.leelaz.ponder();
        }
      }
      if (line.startsWith("noponder")) {
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
        }
        if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
          Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
          Lizzie.frame.toolbar.chkAutoPlay.setSelected(false);
          Lizzie.frame.toolbar.isAutoPlay = false;
          Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
          Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
          Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
          Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
        }
        Lizzie.leelaz.nameCmd();
        Lizzie.leelaz.notPondering();
      }
    }
  }

  private void syncBoardStones() {
    if (tempcount.size() > Lizzie.board.boardWidth * Lizzie.board.boardWidth) {
      tempcount = new ArrayList<Integer>();
      return;
    }
    boolean played = false;
    boolean holdLastMove = false;
    int lastX = 0;
    int lastY = 0;
    int playedMove = 0;
    boolean isLastBlack = false;
    BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
    BoardHistoryNode node2 = Lizzie.board.getHistory().getMainEnd();
    Stone[] stones = Lizzie.board.getHistory().getMainEnd().getData().stones;
    for (int i = 0; i < tempcount.size(); i++) {
      int m = tempcount.get(i);
      int y = i / Lizzie.board.boardWidth;
      int x = i % Lizzie.board.boardWidth;
      if (m == 1 && !stones[Lizzie.board.getIndex(x, y)].isBlack()) {
        if (stones[Lizzie.board.getIndex(x, y)].isWhite()) {
          Lizzie.board.clear();
          // syncBoardStones();
          return;
        }
        if (!played) {
          Lizzie.board.moveToAnyPosition(node2);
        }
        Lizzie.board.placeForSync(x, y, Stone.BLACK, true);
        if (node2.variations.get(0).isEndDummay()) {
          node2.variations.add(0, node2.variations.get(node2.variations.size() - 1));
          node2.variations.remove(1);
          node2.variations.remove(node2.variations.size() - 1);
        }
        played = true;
        playedMove = playedMove + 1;
      }
      if (m == 2 && !stones[Lizzie.board.getIndex(x, y)].isWhite()) {
        if (stones[Lizzie.board.getIndex(x, y)].isBlack()) {
          Lizzie.board.clear();
          // syncBoardStones();
          return;
        }

        if (!played) {
          Lizzie.board.moveToAnyPosition(node2);
        }
        Lizzie.board.placeForSync(x, y, Stone.WHITE, true);
        if (node2.variations.get(0).isEndDummay()) {
          node2.variations.add(0, node2.variations.get(node2.variations.size() - 1));
          node2.variations.remove(1);
          node2.variations.remove(node2.variations.size() - 1);
        }
        played = true;
        playedMove = playedMove + 1;
      }
      if (Lizzie.config.alwaysSyncBoardStat && !Lizzie.frame.bothSync) {
        if (m == 0 && stones[Lizzie.board.getIndex(x, y)] != Stone.EMPTY) {
          Lizzie.board.clear();
          // syncBoardStones();
          return;
        }
      }

      if (m == 3 && !stones[Lizzie.board.getIndex(x, y)].isBlack()) {
        if (stones[Lizzie.board.getIndex(x, y)].isWhite()) {
          Lizzie.board.clear();
          // syncBoardStones();
          return;
        }
        holdLastMove = true;
        lastX = x;
        lastY = y;
        isLastBlack = true;
      }
      if (m == 4 && !stones[Lizzie.board.getIndex(x, y)].isWhite()) {
        if (stones[Lizzie.board.getIndex(x, y)].isBlack()) {
          Lizzie.board.clear();
          // syncBoardStones();
          return;
        }
        holdLastMove = true;
        lastX = x;
        lastY = y;
        isLastBlack = false;
      }
    }
    if (firstSync && played) {
      Lizzie.board.hasStartStone = true;
      Lizzie.board.addStartListAll();
      Lizzie.board.flatten();
    }
    // 落最后一步
    if (holdLastMove) {

      if (!played) {
        Lizzie.board.moveToAnyPosition(node2);
      }
      Lizzie.board.placeForSync(lastX, lastY, isLastBlack ? Stone.BLACK : Stone.WHITE, true);
      if (node2.variations.get(0).isEndDummay()) {
        node2.variations.add(0, node2.variations.get(node2.variations.size() - 1));
        node2.variations.remove(1);
        node2.variations.remove(node2.variations.size() - 1);
      }
      played = true;
    }
    if (!Lizzie.frame.bothSync) {
      if (played
          && !Lizzie.config.alwaysGotoLastOnLive
          && !Lizzie.config.alwaysSyncBoardStat
          && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
          && node != node2) {
        Lizzie.board.moveToAnyPosition(node);
      }
    }
    if (firstSync) {
      firstSync = false;
      Lizzie.board.previousMove();
      Timer timer = new Timer();
      timer.schedule(
          new TimerTask() {
            public void run() {
              while (Lizzie.board.nextMove()) ;
              this.cancel();
            }
          },
          100);
    }

    //	    if (played && Lizzie.config.alwaysGotoLastOnLive) {
    //	      int moveNumber = Lizzie.board.getHistory().getMainEnd().getData().moveNumber;
    //	      Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
    //	      Lizzie.frame.refresh();
    //	    }
  }

  //  private void syncBoardStones() {
  //    boolean played = false;
  //    BoardHistoryNode node =Lizzie.board.getHistory().getCurrentHistoryNode();
  //    BoardHistoryList history = Lizzie.board.getHistory();
  //    while (history.previous().isPresent()) history.previous();
  //    while (history.next().isPresent()) history.getNext();
  //    Stone[] stones = Lizzie.board.getHistory().getMainEnd().getData().stones;
  //    for (int i = 0; i < tempcount.size(); i++) {
  //      int m = tempcount.get(i);
  //      int y = i / 19;
  //      int x = i % 19;
  //      if (m == 1 && !stones[Lizzie.board.getIndex(x, y)].isBlack()) {
  //        history.place(x, y, Stone.BLACK, true);
  //        //Lizzie.board.setHistory(history);
  //       if(node==history.getCurrentHistoryNode().previous().get())
  //        	Lizzie.leelaz.playMove(Stone.BLACK, Lizzie.board.convertCoordinatesToName(x, y));
  //        played = true;
  //         Lizzie.board.setHistory(history);
  //      }
  //      if (m == 2 && !stones[Lizzie.board.getIndex(x, y)].isWhite()) {
  //
  //        history.place(x, y, Stone.WHITE, true);
  //       // Lizzie.board.setHistory(history);
  //        if(node==history.getCurrentHistoryNode().previous().get())
  //        	Lizzie.leelaz.playMove(Stone.WHITE, Lizzie.board.convertCoordinatesToName(x, y));
  //        played = true;
  //        Lizzie.board.setHistory(history);
  //      }
  //    }
  //
  ////    if (played && Lizzie.config.alwaysGotoLastOnLive) {
  ////      int moveNumber = Lizzie.board.getHistory().getMainEnd().getData().moveNumber;
  ////      Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
  ////      Lizzie.frame.refresh();
  ////    }
  //  }

  public void shutdown() {
    Lizzie.frame.syncBoard = false;
    Lizzie.frame.bothSync = false;
    process.destroy();
  }

  public void sendCommand(String command) {
    //  synchronized (cmdQueue) {
    if (!cmdQueue.isEmpty()) {
      cmdQueue.removeLast();
    }
    cmdQueue.addLast(command);
    trySendCommandFromQueue();
    //   }
  }

  private void trySendCommandFromQueue() {
    // synchronized (cmdQueue) {
    if (cmdQueue.isEmpty()) {
      return;
    }
    String command = cmdQueue.removeFirst();
    sendCommandTo(command);
    // }
  }

  public void sendCommandTo(String command) {
    // System.out.printf("> %d %s\n", cmdNumber, command);
    // try {
    if (Lizzie.gtpConsole.isVisible()) Lizzie.gtpConsole.addReadBoardCommand(command);
    // } catch (Exception ex) {
    // }
    // cmdNumber++;
    try {
      outputStream.write((command + "\n").getBytes());
      outputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
