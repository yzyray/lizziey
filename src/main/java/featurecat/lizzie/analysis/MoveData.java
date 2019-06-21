package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Holds the data from Leelaz's pondering mode */
public class MoveData {
  public String coordinate;
  public int playouts;
  public double winrate;
  public List<String> variation;
  public double lcb;
  public double oriwinrate;
  public double policy;
  public int equalplayouts;
  public double scoreMean;
  public double scoreStdev;

  private MoveData() {}

  /**
   * Parses a leelaz ponder output line. For example:
   *
   * <p>0.16 0.15
   *
   * <p>info move R5 visits 38 winrate 5404 order 0 pv R5 Q5 R6 S4 Q10 C3 D3 C4 C6 C5 D5
   *
   * <p>0.17
   *
   * <p>info move Q16 visits 80 winrate 4405 prior 1828 lcb 4379 order 0 pv Q16 D4
   *
   * @param line line of ponder output
   */
  public static MoveData fromInfo(String line) throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");
    // int k = Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
    boolean islcb = (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbwinrate);
    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      if (key.equals("pv")) {
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        result.variation =
            result.variation.subList(
                i + 1,
                (Lizzie.config.limitBranchLength > 0
                        && data.length - i - 1 > Lizzie.config.limitBranchLength)
                    ? i + 1 + Lizzie.config.limitBranchLength
                    : data.length);
        // result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("lcb")) {
          // LCB support
          result.lcb = Integer.parseInt(value) / 100.0;
          if (islcb) {
            result.winrate = Integer.parseInt(value) / 100.0;
          }
        }
        if (key.equals("prior")) {
          result.policy = Integer.parseInt(value) / 100.0;
          ;
        }

        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.oriwinrate = Integer.parseInt(value) / 100.0;
          if (!islcb) {
            result.winrate = Integer.parseInt(value) / 100.0;
          }
        }
      }
    }
    return result;
  }

  public static MoveData fromInfoKatago(String line) throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");
    // int k = Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
    boolean islcb = (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbwinrate);
    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      if (key.equals("pv")) {
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        result.variation =
            result.variation.subList(
                i + 1,
                (Lizzie.config.limitBranchLength > 0
                        && data.length - i - 1 > Lizzie.config.limitBranchLength)
                    ? i + 1 + Lizzie.config.limitBranchLength
                    : data.length);
        // result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("lcb")) {
          // LCB support
          result.lcb = Double.parseDouble(value) * 100;
          if (islcb) {
            result.winrate = Double.parseDouble(value) * 100;
          }
        }
        if (key.equals("prior")) {
          result.policy = Double.parseDouble(value) * 100;
          ;
        }
        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.oriwinrate = Double.parseDouble(value) * 100;
          if (!islcb) {
            result.winrate = Double.parseDouble(value) * 100;
          }
        }
        if (key.equals("scoreMean")) {
          result.scoreMean = Double.parseDouble(value);
          ;
        }
        if (key.equals("scoreStdev")) {
          result.scoreStdev = Double.parseDouble(value);
          ;
        }
      }
    }
    return result;
  }

  public static MoveData fromInfofromfile(String line) throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");

    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      if (key.equals("pv")) {
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.winrate = Integer.parseInt(value) / 100.0;
        }
      }
    }
    return result;
  }
  /**
   * Parses a leelaz summary output line. For example:
   *
   * <p>0.15 0.16
   *
   * <p>P16 -> 4 (V: 50.94%) (N: 5.79%) PV: P16 N18 R5 Q5 D4 -> 1393 (V: 51.16%) (N: 58.90%) PV: D4
   * D17 Q4 C6 F3 C12 K17 O17 G17 F16 E18 G16 E17 E16 H17 D18 D16 E19 F17 D15 C16 B17 B16 C17
   *
   * <p>0.17
   *
   * <p>Q4 -> 4348 (V: 43.88%) (LCB: 43.81%) (N: 18.67%) PV: Q4 D16 D4 Q16 R14 R6 C1
   *
   * @param summary line of summary output
   */
  public static MoveData fromSummary(String summary) {
    Matcher match = summaryPattern.matcher(summary.trim());
    if (!match.matches()) {
      // support 0.16 0.15
      //        if(summary.contains("->   "))
      //        { Matcher matchold = summaryPatternhandicap.matcher(summary.trim());
      //        if (!matchold.matches()) {
      //            throw new IllegalArgumentException("Unexpected summary format: " + summary);
      //          } else {
      //            MoveData result = new MoveData();
      //            result.coordinate = matchold.group(1);
      //            result.playouts = Integer.parseInt(matchold.group(2));
      //            result.winrate = Double.parseDouble(matchold.group(3));
      //            String aa=match.group(4);
      //            result.variation =
      //                Arrays.asList(match.group(4).split(" ", Lizzie.config.limitBranchLength));
      //            //  result.variation = Arrays.asList(matchold.group(4).split(" "));
      //            return result;
      //          }
      //        }
      Matcher matchold = summaryPatternold.matcher(summary.trim());
      if (!matchold.matches()) {
        Lizzie.gtpConsole.addLine("读取总结信息出错");
        return null;
        // throw new IllegalArgumentException("Unexpected summary format: " + summary);
      } else {
        MoveData result = new MoveData();
        result.coordinate = matchold.group(1);
        result.playouts = Integer.parseInt(matchold.group(2));
        result.winrate = Double.parseDouble(matchold.group(3));
        result.variation =
            Arrays.asList(matchold.group(4).split(" ", Lizzie.config.limitBranchLength));
        // result.variation = Arrays.asList(matchold.group(4).split(" "));
        return result;
      }
    } else {
      MoveData result = new MoveData();
      result.coordinate = match.group(1);
      result.playouts = Integer.parseInt(match.group(2));
      result.winrate = Double.parseDouble(match.group(Lizzie.config.showlcbwinrate ? 4 : 3));
      result.variation = Arrays.asList(match.group(5).split(" ", Lizzie.config.limitBranchLength));
      // result.variation = Arrays.asList(match.group(5).split(" "));
      return result;
    }
  }

  private static Pattern summaryPattern =
      Pattern.compile(
          "^ *(\\w\\d*) -> *(\\d+) \\(V: ([^%)]+)%\\) \\(LCB: ([^%)]+)%\\) \\([^\\)]+\\) PV: (.+).*$");
  private static Pattern summaryPatternold =
      Pattern.compile("^ *(\\w\\d*) -> *(\\d+) \\(V: ([^%)]+)%\\) \\([^\\)]+\\) PV: (.+).*$");

  // support 0.16 0.15
  private static Pattern summaryPatternhandicap =
      Pattern.compile("^ *(\\w\\d*) ->    *(\\d+) \\(V: ([^%)]+)%\\) \\([^\\)]+\\) PV: (.+).* $");

  public static int getPlayouts(List<MoveData> moves) {
    int playouts = 0;
    for (MoveData move : moves) {
      playouts += move.playouts;
    }
    return playouts;
  }

  public static Comparator policyComparator =
      new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
          MoveData e1 = (MoveData) o1;
          MoveData e2 = (MoveData) o2;
          if (e1.policy > e2.policy) return 1;
          if (e1.policy < e2.policy) return -1;
          else return 0;
        }
      };
}
