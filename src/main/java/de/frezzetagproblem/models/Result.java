package de.frezzetagproblem.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Result {
  String fileName;
  List<ResultDetails> details;


  public Result(String fileName) {
    this.fileName = fileName;
    this.details = new ArrayList<>();
  }

  public Result(String fileName, List<ResultDetails> details) {
    this.fileName = fileName;
    this.details = details;
  }

  public void add (double totalTimeUnit, List<String> wakeUpTree, List<Robot> permutation){
    details.add(new ResultDetails(totalTimeUnit, wakeUpTree, permutation));
  }
  public static List<Result> getOptimalResults(List<Result> results) {
    List<Result> optimalResults = new ArrayList<>();

    for (Result result : results) {
      ResultDetails optimalDetail = result.details.stream()
          .min(Comparator.comparingDouble(ResultDetails::getTotalTimeUnit))
          .orElse(null);

      if (optimalDetail != null) {
        List<ResultDetails> optimalDetailList = new ArrayList<>();
        optimalDetailList.add(optimalDetail);
        Result optimalResult = new Result(result.fileName, optimalDetailList);
        optimalResults.add(optimalResult);
      }
    }

    return optimalResults;
  }

  public static List<Result> getWorstCaseResults(List<Result> results) {
    List<Result> worstResults = new ArrayList<>();

    for (Result result : results) {
      ResultDetails worstDetail = result.details.stream()
          .max(Comparator.comparingDouble(ResultDetails::getTotalTimeUnit))
          .orElse(null);

      if (worstDetail != null) {
        List<ResultDetails> worstDetailList = new ArrayList<>();
        worstDetailList.add(worstDetail);
        Result worstResult = new Result(result.fileName, worstDetailList);
        worstResults.add(worstResult);
      }
    }

    return worstResults;
  }

}
