package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Movelist;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

  public YaZenGtp() throws IOException {

    cmdNumber = 1;
    currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();
    gtpConsole = true;
    engineCommand = "YAZenGtp.exe";
    startEngine(engineCommand, 0);
  }

  public void startEngine(String engineCommand, int index) throws IOException {

    currentEnginename = engineCommand;

    ProcessBuilder processBuilder = new ProcessBuilder(currentEnginename);
    processBuilder.redirectErrorStream(true);
    process = processBuilder.start();
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
    Lizzie.gtpConsole.addZenCommand(command, cmdNumber);
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

    sendCommandToZen("play" + " " + color + " " + coordsname);
  }

  public void syncboradstat() {
    sendCommandToZen("clear_board");
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
}
