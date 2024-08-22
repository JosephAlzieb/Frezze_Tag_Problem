package de.frezzetagproblem.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Result {
  private String fileName;
  private int experimentNumber;
  private int robotsCount;
  private ResultDetails details;


  public Result(String fileName, int robotsCount, int experimentNumber) {
    this.fileName = fileName;
    this.robotsCount = robotsCount;
    this.experimentNumber = experimentNumber;
  }

  public Result(String fileName, ResultDetails details, int robotsCount, int experimentNumber) {
    this.fileName = fileName;
    this.details = details;
    this.robotsCount = robotsCount;
    this.experimentNumber = experimentNumber;
  }

  public void add (double totalTimeUnit, WakeUpTree wakeUpTree, List<Robot> permutation){
    ResultDetails d = new ResultDetails(totalTimeUnit, wakeUpTree, permutation);

    if (details == null) details = d;
    else if (details.getTotalTimeUnit() > totalTimeUnit){
      details = d;
    }
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

  public ResultDetails getDetails() {
    return details;
  }

  @Override
  public String toString() {
    return "Result{" +
        "fileName='" + fileName + '\'' +
        ", experimentNumber=" + experimentNumber +
        ", robotsCount=" + robotsCount +
        '}';
  }

  public double getTotalTimeUnit() {
    return details.getTotalTimeUnit();
  }
}
