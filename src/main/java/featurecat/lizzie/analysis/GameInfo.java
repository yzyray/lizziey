package featurecat.lizzie.analysis;

import java.util.Date;

public class GameInfo {
  public static final String DEFAULT_NAME_HUMAN_PLAYER = "我";
  public static final String DEFAULT_NAME_CPU_PLAYER = "Leela Zero";
  public static double DEFAULT_KOMI = 7.5;

  private String playerBlack = "";
  private String playerWhite = "";
  private Date date = new Date();
  private double komi = DEFAULT_KOMI;
  private int handicap = 0;
  private String result = "";

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getPlayerBlack() {
    return playerBlack;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setPlayerBlack(String playerBlack) {
    this.playerBlack = playerBlack;
  }

  public String getPlayerWhite() {
    return playerWhite;
  }

  public String getSaveFileName() {
    if (playerBlack.equals("") && playerWhite.equals("")) return "";
    else return playerBlack + "_Vs_" + playerWhite;
  }

  public void setPlayerWhite(String playerWhite) {
    this.playerWhite = playerWhite;
  }

  public double getKomi() {
    return komi;
  }

  public void setKomi(double komi) {
    this.komi = komi;
  }

  public int getHandicap() {
    return handicap;
  }

  public void setHandicap(int handicap) {
    this.handicap = handicap;
  }

  public void resetAll() {
    this.komi = DEFAULT_KOMI;
    this.handicap = 0;
    this.playerBlack = "";
    this.playerWhite = "";
    this.date = new Date();
    this.result = "";
  }
}
