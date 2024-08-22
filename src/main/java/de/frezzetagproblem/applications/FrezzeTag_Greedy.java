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

public class FrezzeTag_Greedy {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT, Properties.TOTAL_ROBOTS_COUNT);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount) throws IOException {
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

        /*
          Hier wird das Algorithms ausgeführt. wir erstellen zunächst den Aktivierungsbaum.
         */
        WakeUpTree wake_up_tree = new WakeUpTree();
        while (!off.isEmpty()) {
          for (Robot r : on) {
            r.run(off, wake_up_tree);
          }

          /*
            ON- und OFF-Listen werden aktualisiert.
           */
          for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
            Robot robot = iterator.next();
            if (robot.isAktive()) {
              on.add(robot);
              iterator.remove();
            }
          }
        }

        double makespan = wake_up_tree.getMakespan();
        result.add(makespan, wake_up_tree, null);
        results.add(result);
      }

      Helper.saveResults(robotsCount, "greedy", results);

      robotsCount = Helper.increaseRobotsCount(robotsCount);
    }
  }

}