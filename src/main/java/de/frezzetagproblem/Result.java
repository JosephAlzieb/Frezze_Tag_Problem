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

  public Result(String fileName, List<ResultDetails> details) {
    this.fileName = fileName;
    this.details = details;
  }

  public void add (double totalTimeUnit, List<String> wakeUpTree, List<Robot_BestCase_Optimal> permutation){
    details.add(new ResultDetails(totalTimeUnit, wakeUpTree, permutation));
  }
  public static List<Result> getOptimalResults(List<Result> results) {
    List<Result> optimalResults = new ArrayList<>();

    for (Result result : results) {
      ResultDetails optimalDetail = result.details.stream()
          .min((d1, d2) -> Double.compare(d1.getTotalTimeUnit(), d2.getTotalTimeUnit()))
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

}
