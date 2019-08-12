package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.CountResults;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.Stone;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JOptionPane;

public class ReadBoard {
  public Process process;

  private BufferedInputStream inputStream;
  private BufferedOutputStream outputStream;

  public boolean gtpConsole;

  private boolean isLoaded = false;

  private String engineCommand;
  // private List<String> commands;
  private int cmdNumber;
  private int currentCmdNum;
  private ArrayDeque<String> cmdQueue;
  private ScheduledExecutorService executor;
  public String currentEnginename = "";
  ArrayList<Integer> tempcount = new ArrayList<Integer>();
  public int blackEatCount = 0;
  public int whiteEatCount = 0;
  public int blackPrisonerCount = 0;
  public int whitePrisonerCount = 0;
  CountResults results;
  boolean firstcount = true;
  public int numberofcount = 0;
  public boolean noread = false;
  public boolean firstSync = true;

  public ReadBoard() throws IOException {

    cmdNumber = 1;
    currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();
    gtpConsole = true;
    engineCommand = "ceshidm.exe";
    startEngine(engineCommand, 0);
  }

  public void startEngine(String engineCommand, int index) {

    List<String> commands = new ArrayList<String>();
    commands.add(engineCommand);
    commands.add("yzy");
    commands.add(Lizzie.config.readBoardArg1);

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.redirectErrorStream(true);
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      boolean onTop = false;
      if (Lizzie.frame.isAlwaysOnTop()) {
        Lizzie.frame.setAlwaysOnTop(false);
        onTop = true;
      }
      JOptionPane.showMessageDialog(null, "加载棋盘识别工具失败");
      if (onTop) Lizzie.frame.setAlwaysOnTop(true);
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

          parseLine(line.toString());
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
      System.exit(-1);
    }
  }

  private void parseLine(String line) {
    //  synchronized (this) {
    if (Lizzie.gtpConsole.isVisible()) Lizzie.gtpConsole.addLineforce(line);

    if (line.startsWith("re=")) {

      String[] params = line.substring(3, line.length() - 2).split(",");
      if (params.length == 19) {
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
      this.firstSync = true;
    }
  }

  private void syncBoardStones() {
    boolean played = false;
    BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
    Stone[] stones = Lizzie.board.getHistory().getMainEnd().getData().stones;
    for (int i = 0; i < tempcount.size(); i++) {
      int m = tempcount.get(i);
      int y = i / 19;
      int x = i % 19;
      if (m == 1 && !stones[Lizzie.board.getIndex(x, y)].isBlack()) {
        if (stones[Lizzie.board.getIndex(x, y)].isWhite()) {
          Lizzie.board.clear();
          syncBoardStones();
          return;
        }
        if (!played) {
          while (!Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
            Lizzie.board.previousMove();
          }
          while (Lizzie.board.nextMove()) ;
        }
        Lizzie.board.place(x, y, Stone.BLACK, true);
        played = true;
      }
      if (m == 2 && !stones[Lizzie.board.getIndex(x, y)].isWhite()) {
        if (stones[Lizzie.board.getIndex(x, y)].isBlack()) {
          Lizzie.board.clear();
          syncBoardStones();
          return;
        }
        if (!played) {
          while (!Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
            Lizzie.board.previousMove();
          }
          while (Lizzie.board.nextMove()) ;
        }
        Lizzie.board.place(x, y, Stone.WHITE, true);
        played = true;
      }
    }
    if (firstSync) {
      Lizzie.board.flatten();
      firstSync = false;
    }
    // 落最后一步
    if (played
        //  && !firstSync
        && !Lizzie.config.alwaysGotoLastOnLive
        && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
        && node != Lizzie.board.getHistory().getCurrentHistoryNode().previous().get()) {
      Lizzie.board.moveToAnyPosition(node);
      // firstSync = false;
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
    sendCommandToZen(command);
    // }
  }

  private void sendCommandToZen(String command) {
    // System.out.printf("> %d %s\n", cmdNumber, command);
    try {
      Lizzie.gtpConsole.addZenCommand(command, cmdNumber);
    } catch (Exception ex) {
    }
    cmdNumber++;
    try {
      outputStream.write((command + "\n").getBytes());
      outputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void playmove(int x, int y, boolean isblack) {
    String coordsname = Lizzie.board.convertCoordinatesToName(x, y);
    String color = isblack ? "b" : "w";

    sendCommand("play" + " " + color + " " + coordsname);
  }

  public void syncboradstat() {
    sendCommand("clear_board");
    sendCommand("boardsize " + Lizzie.board.boardWidth);
    cmdNumber = 1;
    ArrayList<Movelist> movelist = Lizzie.board.getmovelist();
    int lenth = movelist.size();
    for (int i = 0; i < lenth; i++) {
      Movelist move = movelist.get(lenth - 1 - i);
      if (!move.ispass) {
        playmove(move.x, move.y, move.isblack);
      }
    }
  }

  public void countStones() {
    if (numberofcount > 5) {
      noread = true;
      cmdQueue.clear();
      Lizzie.frame.noautocounting();
      return;
    }
    numberofcount++;
    tempcount.clear();
    blackEatCount = 0;
    whiteEatCount = 0;
    blackPrisonerCount = 0;
    whitePrisonerCount = 0;
    sendCommand("territory_statistics territory");
    //
    sendCommand("score_statistics");
  }

  private void reautocounting() {
    Lizzie.frame.isAutocounting = true;
    syncboradstat();
    countStones();
    Lizzie.countResults.isAutocounting = true;
  }
}
