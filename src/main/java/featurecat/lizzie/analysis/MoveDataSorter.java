package featurecat.lizzie.analysis;

import java.util.ArrayList;
import java.util.Collections;

public class MoveDataSorter {
  ArrayList MoveData = new ArrayList<>();

  public MoveDataSorter(ArrayList MoveData) {
    this.MoveData = MoveData;
  }

  public ArrayList getSortedMoveDataByPolicy() {
    Collections.sort(MoveData);
    return MoveData;
  }
}
