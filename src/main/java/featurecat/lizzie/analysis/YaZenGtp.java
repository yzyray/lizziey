package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.CountResults;
import featurecat.lizzie.rules.Movelist;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JOptionPane;

public class YaZenGtp {
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

  public YaZenGtp() throws IOException {

    cmdNumber = 1;
    currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();
    gtpConsole = true;
    engineCommand = "YAZenGtp.exe";
    startEngine(engineCommand, 0);
  }

  public void startEngine(String engineCommand, int index) {

    currentEnginename = engineCommand;

    ProcessBuilder processBuilder = new ProcessBuilder(currentEnginename);
    processBuilder.redirectErrorStream(true);
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      JOptionPane.showMessageDialog(null, "点目失败,请确认Lizzie目录下是否有YAZenGtop.exe和Zen.dll");
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
      // this line will be reached when YaZenGtp shuts down
      System.out.println("YaZenGtp process ended.");

      shutdown();
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void parseLine(String line) {
    synchronized (this) {
      Lizzie.gtpConsole.addLineforce(line);
      if (line.startsWith("=  ")) {
        String[] params = line.trim().split(" ");
        // Lizzie.gtpConsole.addLineforce("这是详细点目第一行,21分参数");
        for (int i = 2; i < params.length; i++) tempcount.add(Integer.parseInt(params[i]));
      }

      if (line.startsWith(" ")) {

        String[] params = line.trim().split(" ");
        if (params.length == 19) {
          for (int i = 0; i < params.length; i++) tempcount.add(Integer.parseInt(params[i]));
          // Lizzie.gtpConsole.addLineforce("这是详细点目");
        }
      }
    }
    if (line.startsWith("= ")) {
      String[] params = line.trim().split(" ");
      if (params.length == 14) {
        // Lizzie.gtpConsole.addLineforce("这是点目结果");
        if (noread) {
          numberofcount = 0;
        } else {

          blackEatCount = Integer.parseInt(params[3]);
          whiteEatCount = Integer.parseInt(params[4]);
          blackPrisonerCount = Integer.parseInt(params[5]);
          whitePrisonerCount = Integer.parseInt(params[6]);
          int blackpoint = Integer.parseInt(params[7]);
          int whitepoint = Integer.parseInt(params[8]);
          if (!Lizzie.frame.isAutocounting) Lizzie.frame.boardRenderer.drawcountblock(tempcount);
          else {
            if (Lizzie.config.showSubBoard) {
              Lizzie.frame.boardRenderer.removecountblock();
              Lizzie.frame.subBoardRenderer.drawcountblock(tempcount);
            } else Lizzie.frame.boardRenderer.drawcountblock(tempcount);
          }

          Lizzie.frame.repaint();
          if (firstcount) {
            results = Lizzie.countResults;
            results.Counts(
                blackEatCount,
                whiteEatCount,
                blackPrisonerCount,
                whitePrisonerCount,
                blackpoint,
                whitepoint);
            results.setVisible(true);
            firstcount = false;
            numberofcount = 0;
          } else {
            results.Counts(
                blackEatCount,
                whiteEatCount,
                blackPrisonerCount,
                whitePrisonerCount,
                blackpoint,
                whitepoint);
            results.setVisible(true);
            Lizzie.frame.setVisible(true);
            numberofcount = 0;
          }
        }
      }
    }
  }

  public void shutdown() {
    process.destroy();
  }

  public void sendCommand(String command) {
    synchronized (cmdQueue) {
      if (!cmdQueue.isEmpty()) {
        cmdQueue.removeLast();
      }
      cmdQueue.addLast(command);
      trySendCommandFromQueue();
    }
  }

  private void trySendCommandFromQueue() {
    synchronized (cmdQueue) {
      if (cmdQueue.isEmpty()) {
        return;
      }
      String command = cmdQueue.removeFirst();
      sendCommandToZen(command);
    }
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
