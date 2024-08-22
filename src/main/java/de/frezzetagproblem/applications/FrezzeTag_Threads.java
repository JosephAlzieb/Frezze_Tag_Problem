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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FrezzeTag_Threads {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT, Properties.TOTAL_ROBOTS_COUNT);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount) throws IOException {
    ExecutorService executor = Executors.newCachedThreadPool();

    while (robotsCount <= totalRobotsCount) {
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

        WakeUpTree wake_up_tree = new WakeUpTree();
        while (!off.isEmpty()) {
          List<Future<?>> futures = new ArrayList<>();
          for (Robot r : on) {
            Future<?> future = executor.submit(() -> r.run(off, wake_up_tree));
            futures.add(future);
          }

          // Warte auf das Ende aller gestarteten Tasks
          for (Future<?> future : futures) {
            try {
              future.get();
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
          }

          for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
            Robot robot = iterator.next();
            if (robot.isAktive()) {
              on.add(robot);
              iterator.remove();
            }
          }
        }

        //System.out.println(durationInMilliseconds);
        double makespan = wake_up_tree.getMakespan();
        result.add(makespan, wake_up_tree, null);
        results.add(result);
      }
      Helper.saveResults(robotsCount, "threads", results);

      robotsCount = Helper.increaseRobotsCount(robotsCount);
    }

    /*
      Am Ende schließen wir den ThreadPool
     */
    executor.shutdown();
    try {
      if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }
}