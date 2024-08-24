package de.frezzetagproblem.applications;

import de.frezzetagproblem.Properties;
import de.frezzetagproblem.models.Distance;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FrezzeTag_AllPossibleSolutions {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT, Properties.TOTAL_ROBOTS_COUNT);
  }

  public static void runExperiments(int robotsCount, int totalRobotsCount) throws IOException {
    ExecutorService executor = Executors.newCachedThreadPool();

    while (robotsCount <= totalRobotsCount) {
      List<Future<?>> futures = new ArrayList<>();
      int finalRobotsCount = robotsCount;
      Future<?> future = executor.submit(() -> {
        try {
          runExperimentsForRobotsCount(finalRobotsCount);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

      });
      futures.add(future);

      robotsCount = Helper.increaseRobotsCount(robotsCount);
    }

    executor.shutdown();
    try {
      if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }

  private static void runExperimentsForRobotsCount(int robotsCount) throws IOException {
    List<Result> results = new ArrayList<>();
    String pathName = Helper.getPathName();

    String fileName = pathName + robotsCount;
    Path dir = Paths.get(fileName);
    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
    }

    DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json");

    int experimentNumber = 1;

    for (Path entry : stream) {
      List<Robot> off = new ArrayList<>();
      List<Robot> on = new ArrayList<>();

      String f = entry.getFileName().toString();
      Result result = new Result(f, robotsCount, experimentNumber++);

      Helper.readJsonFile(entry, on, off);

      execute(on, off, result);

      results.add(result);
    }

      /*
      Von allen möglichen Lösungen werden nur die optimalen gespeichert, denn für große n kann die Datei sehr groß werden.
       */
    Helper.saveResults(robotsCount, "all_possible_solutions", results);
  }

  public static void execute(
      List<Robot> on,
      List<Robot> off,
      Result result) {
    List<List<Robot>> permutations = Helper.generatePermutations(off);
    List<Distance> distances = new ArrayList<>();
    for (List<Robot> permutation : permutations) {
      List<Robot> p_off = copyRobots(permutation);
      List<Robot> p_on = copyRobots(on);
      WakeUpTree wake_up_tree = new WakeUpTree();

      while (!p_off.isEmpty()) {
        distances.clear();
        distances = Distance.calculateDistances(p_on, p_off);
        for (Robot r : p_on) {
          Robot target = r.getNextRobot(p_off);
          Robot start = Distance.findClosestAktiveRobot(target, distances);
          if (target != null && r.equals(start)) {
            Distance.clear(start, distances);
            target.aktive();
            double distance = r.distance(target);
            wake_up_tree.addChild(r.getId(), target.getId(), distance);
            r.moveTo(target.getLocation());
          }
        }

        for (Iterator<Robot> iterator = p_off.iterator(); iterator.hasNext(); ) {
          Robot robot = iterator.next();
          if (robot.isAktive()) {
            p_on.add(robot);
            iterator.remove();
          }
        }

      }

      double makespan = wake_up_tree.getMakespan();
      result.add(makespan, wake_up_tree, permutation);
    }
  }

  private static List<Robot> copyRobots(List<Robot> robots) {
    List<Robot> l = new ArrayList<>();
    for (Robot robot : robots) {
      try {
        Robot copy = (Robot) robot.clone();
        l.add(copy);
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
    return l;
  }
}