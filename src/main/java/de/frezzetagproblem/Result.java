package de.frezzetagproblem;

import de.frezzetagproblem.optimal.Robot_BestCase_Optimal;
import java.util.ArrayList;
import java.util.List;

public class Result {
  String fileName;
  List<ResultDetails> details;


  public Result(String fileName) {
    this.fileName = fileName;
    this.details = new ArrayList<>();
  }

  public void add (double totalTimeUnit, List<String> wakeUpTree, List<Robot_BestCase_Optimal> permutation){
    details.add(new ResultDetails(totalTimeUnit, wakeUpTree, permutation));
  }
//  public static List<Result> findMinTotalTimeUnits(List<Result> results) {
//    if (results == null || results.isEmpty()) {
//      return null; // Rückgabe null, wenn die Liste leer ist oder null
//    }
//    List<Result> results_copy = new ArrayList<>(results);
//    List<Result> optimal = new ArrayList<>();
//    for (Result r : results_copy){
//      ResultDetails min = r.details.get(0);
//      for (ResultDetails d : r.details) {
//        if (d.getTotalTimeUnit() < min.getTotalTimeUnit()){
//          min = d;
//        }
//      }
//
//      results.
//    }
//
//    return optimal;
//  }

}
