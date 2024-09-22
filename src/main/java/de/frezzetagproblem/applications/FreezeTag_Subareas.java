package de.frezzetagproblem.applications;

import de.frezzetagproblem.Properties;
import de.frezzetagproblem.models.Helper;
import de.frezzetagproblem.models.Result;
import de.frezzetagproblem.models.Robot;
import de.frezzetagproblem.models.WakeUpTree;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class FreezeTag_Subareas {

  public static void main(String[] args) throws IOException {
    FreezeTag_Subareas ftp = new FreezeTag_Subareas();
    //Ab 5 Roboter macht das Sinn
    ftp.runExperiments(5, Properties.TOTAL_ROBOTS_COUNT);
  }

  //TODO Arraylist - muss nicht 4 sein . kann auch 2 oder 3 sein.
  private Part[] parts = new Part[4]; // Four parts of the room
  PriorityQueue<Part> pq = new PriorityQueue<>((a, b) -> b.inactiveRobots.size() - a.inactiveRobots.size());

  private void runExperiments(int robotsCount, int totalRobotsCount) throws IOException {
    while (robotsCount <= totalRobotsCount) {
      List<Result> results = new ArrayList<>();
      String pathName = Helper.getPathName();

      String fileName = pathName + robotsCount;
      Path dir = Paths.get(fileName);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }

      DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json");

      /*
        Wir lesen alle Ordner in /dummy-dta/ eins nach dem anderen.
       */

      int experimentNumber = 1;

      for (Path entry : stream) {
        List<Robot> off = new ArrayList<>();
        List<Robot> on = new ArrayList<>();

        String f = entry.getFileName().toString();
        Result result = new Result(f, robotsCount, experimentNumber++);

        Helper.readJsonFile(entry, on, off);

        WakeUpTree wake_up_tree = new WakeUpTree();

        // Teile des Raums - Raum wird in 4 Teile geteilt
        for (int i = 0; i < 4; i++) {
          parts[i] = new Part();
        }
        assignRobotsToParts(off);

        for (Part part : parts) {
          if (part.hasInaktiveRobots()) pq.add(part);
        }

        switch (pq.size()){
          case 1:{
            //Phase-1: Anfang aktiver Robot geht zu dem dichtesten Quadrat
            Part targetPart1 = getPartWithMostInactiveRobots();
            activate(on.get(0), targetPart1, wake_up_tree);
          }
          break;
          case 2:{
            //Phase-1: Anfang aktiver Robot geht zu dem dichtesten Quadrat
            Part targetPart1 = getPartWithMostInactiveRobots();
            Part targetPart2 = getPartWithMostInactiveRobots();
            Robot first = activate(on.get(0), targetPart1, wake_up_tree);

            //Phase-2
            Robot second = activate(on.get(0), targetPart1, wake_up_tree);
            Robot third = activate(first, targetPart2, wake_up_tree);
          }
          break;
          case 3:{
            //Phase-1: Anfang aktiver Robot geht zu dem dichtesten Quadrat
            Part targetPart1 = getPartWithMostInactiveRobots();
            Part targetPart2 = getPartWithMostInactiveRobots();
            Part targetPart3 = getPartWithMostInactiveRobots();
            Robot first = activate(on.get(0), targetPart1, wake_up_tree);

            //Phase-2
            Robot second = activate(on.get(0), targetPart1, wake_up_tree);
            Robot third = activate(first, targetPart2, wake_up_tree);

            //Phase-3
            activate(second, targetPart3, wake_up_tree);
          }
          break;
          case 4:{
            //Phase-1: Anfang aktiver Robot geht zu dem dichtesten Quadrat
            Part targetPart1 = getPartWithMostInactiveRobots();
            Part targetPart2 = getPartWithMostInactiveRobots();
            Part targetPart3 = getPartWithMostInactiveRobots();
            Part targetPart4 = getPartWithMostInactiveRobots();
            Robot first = activate(on.get(0), targetPart1, wake_up_tree);

            //Phase-2
            Robot second;
            Robot third;
            if (targetPart1.hasInaktiveRobots()) {
              second = activate(on.get(0), targetPart1, wake_up_tree);
              third = activate(first, targetPart2, wake_up_tree);
            } else {
              second = activate(on.get(0), targetPart2, wake_up_tree);
              third = activate(first, targetPart3, wake_up_tree);
            }

            //Phase-3
            if (targetPart3.hasInaktiveRobots()){
              activate(second, targetPart3, wake_up_tree);
            }
            if (targetPart4.hasInaktiveRobots()){
              activate(third, targetPart4, wake_up_tree);
            }
          }
          break;
        }
        updateLists(off, on);

        while (!off.isEmpty()) {
          for (Robot r : on) {
            r.run(off, wake_up_tree);
          }
          updateLists(off, on);
        }

        double makespan = wake_up_tree.getMakespan();
        result.add(makespan, wake_up_tree, null);
        results.add(result);
      }

      Helper.saveResults(robotsCount, "subareas", results);

      robotsCount = Helper.increaseRobotsCount(robotsCount);
    }
  }

  private void executeFirst4Phases(List<Robot> on, WakeUpTree wake_up_tree) {
  }

  private static void updateLists(List<Robot> off, List<Robot> on) {
    for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
      Robot robot = iterator.next();
      if (robot.isAktive()) {
        on.add(robot);
        iterator.remove();
      }
    }
  }

  private static Robot getClosestInactiveRobot(Part targetPart, Robot robot, Part defaultPart) {
    if (targetPart == null)  return defaultPart.getClosestInactiveRobot(robot);

    return targetPart.getClosestInactiveRobot(robot);
  }

  private static Robot activate(Robot robot, Part part, WakeUpTree wake_up_tree){
    Robot target = part.getClosestInactiveRobot(robot);
    activate(robot, target, wake_up_tree);
    return target;
  }

  private static void activate(Robot robot, Robot target, WakeUpTree wake_up_tree) {
    target.declare();
    target.aktive();
    wake_up_tree.addChild(
        robot.getId(), target.getId(), robot.distance(target));
    robot.moveTo(target.getLocation());
  }

  class Part {
    List<Robot> inactiveRobots = new ArrayList<>();
    boolean hasActiveRobot = false;
    public void addRobot(Robot r) {
      inactiveRobots.add(r);
    }
    public boolean hasInaktiveRobots() {
      return !inactiveRobots.isEmpty()
          && !inactiveRobots.stream().filter(r -> !r.isAktive()).toList().isEmpty();
    }
    public Robot getClosestInactiveRobot(Robot from) {
      return from.getNearestRobot(inactiveRobots);
    }
  }

  private void assignRobotsToParts(List<Robot> robots) {
    for (Robot r : robots) {
      int part = getPartForRobot(r);
      parts[part].addRobot(r);
    }
  }

  private int getPartForRobot(Robot r) {
    if (r.getLocation_x() >= 0 && r.getLocation_y() >= 0) return 0; // Upper-right part
    if (r.getLocation_x() < 0 && r.getLocation_y() >= 0) return 1;  // Upper-left part
    if (r.getLocation_x() >= 0 && r.getLocation_y() < 0) return 2;  // Lower-right part
    return 3; // Upper-right part
//    if (r.getLocation_x() >= 0) return 0; // Right
//    return 1; // left
  }

  private Part getPartWithMostInactiveRobots() {
    if (pq.isEmpty()) return null;

    Part poll = pq.poll();
    //Wenn es keine Roboter in dem Teil gibt, dann gehen wir nicht hin.
    if (poll.inactiveRobots.isEmpty()) return null;

    return poll; // Get the part with the most inactive robots
  }
}
