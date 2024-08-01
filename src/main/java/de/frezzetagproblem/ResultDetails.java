package de.frezzetagproblem;

import de.frezzetagproblem.optimal.Robot_BestCase_Optimal;
import java.util.List;

public class ResultDetails {

  public double getTotalTimeUnit() {
    return totalTimeUnit;
  }

  double totalTimeUnit;
  List<String> wakeUpTree;
  List<Robot_BestCase_Optimal> permutation;

  public ResultDetails(double totalTimeUnit, List<String> wakeUpTree,
      List<Robot_BestCase_Optimal> permutation) {
    this.totalTimeUnit = totalTimeUnit;
    this.wakeUpTree = wakeUpTree;
    this.permutation = permutation;
  }
}
