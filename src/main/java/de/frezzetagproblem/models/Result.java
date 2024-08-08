package de.frezzetagproblem.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Result {
  private String fileName;
  private int experimentNumber;
  private int robotsCount;
  private List<ResultDetails> details;


  public Result(String fileName, int robotsCount, int experimentNumber) {
    this.fileName = fileName;
    this.robotsCount = robotsCount;
    this.experimentNumber = experimentNumber;
    this.details = new ArrayList<>();
  }

  public Result(String fileName, List<ResultDetails> details, int robotsCount, int experimentNumber) {
    this.fileName = fileName;
    this.details = details;
    this.robotsCount = robotsCount;
    this.experimentNumber = experimentNumber;
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
        Result optimalResult = new Result(
            result.fileName,
            optimalDetailList,
            result.robotsCount,
            result.experimentNumber);
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
        Result worstResult = new Result(
            result.fileName,
            worstDetailList,
            result.robotsCount,
            result.experimentNumber);
        worstResults.add(worstResult);
      }
    }

    return worstResults;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public int getExperimentNumber() {
    return experimentNumber;
  }

  public void setExperimentNumber(int experimentNumber) {
    this.experimentNumber = experimentNumber;
  }

  public int getRobotsCount() {
    return robotsCount;
  }

  public void setRobotsCount(int robotsCount) {
    this.robotsCount = robotsCount;
  }

  public List<ResultDetails> getDetails() {
    return details;
  }

  public void setDetails(List<ResultDetails> details) {
    this.details = details;
  }

  public int getTotalTimeUnit() {
    return (int) details.get(0).getTotalTimeUnit();
  }
  @Override
  public String toString() {
    return "Result{" +
        "fileName='" + fileName + '\'' +
        ", experimentNumber=" + experimentNumber +
        ", robotsCount=" + robotsCount +
        '}';
  }
}
