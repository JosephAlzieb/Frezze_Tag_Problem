package de.frezzetagproblem.models;

import java.util.List;

public class ResultDetails {

  double totalTimeUnit;
  WakeUpTree wakeUpTree;
  List<Robot> permutation;

  public ResultDetails(double totalTimeUnit, WakeUpTree wakeUpTree,
      List<Robot> permutation) {
    this.totalTimeUnit = totalTimeUnit;
    this.wakeUpTree = wakeUpTree;
    this.permutation = permutation;
  }

  public double getTotalTimeUnit() {
    return totalTimeUnit;
  }
}
