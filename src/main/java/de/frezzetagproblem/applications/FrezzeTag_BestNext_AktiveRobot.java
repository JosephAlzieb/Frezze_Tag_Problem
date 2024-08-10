package de.frezzetagproblem.applications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.models.Result;
import de.frezzetagproblem.models.Robot;
import de.frezzetagproblem.models.WakeUpTree;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FrezzeTag_BestNext_AktiveRobot {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT,Properties.TOTAL_ROBOTS_COUNT);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    while (robotsCount <= totalRobotsCount) {
      List<Result> results = new ArrayList<>();
      String pathName = Properties.ALLOW_GENERATE_WORSTCASE_DATA ?
          Properties.WORST_CASE_FILE_NAME :
          Properties.NORMAL_CASE_FILE_NAME;

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

        JsonReader reader = new JsonReader(new FileReader(entry.toFile()));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonObject robotsJson = jsonObject.getAsJsonObject("robots");

        for (Map.Entry<String, JsonElement> robotEntry : robotsJson.entrySet()) {
          JsonObject robotObj = robotEntry.getValue().getAsJsonObject();
          Robot r = gson.fromJson(robotObj, Robot.class);

          if (r.isAktive()) {
            on.add(r);
          } else {
            off.add(r);
          }
        }

        WakeUpTree wake_up_tree = new WakeUpTree();
        while (!off.isEmpty()) {
          for (Robot r : on) {
            r.run(off, on, wake_up_tree);
          }

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

      saveResults(robotsCount, gson, results);

      if (robotsCount < 15){
        robotsCount++;
      } else if (robotsCount < 100){
        robotsCount += 5;
      } else {
        robotsCount += 50;
      }
    }
  }
  private static double updateTimeUnit(List<Double> timeUnits) {
    if (timeUnits.isEmpty()) return 0;
    return Collections.max(timeUnits);
  }

  private static void saveResults(int robotCount, Gson gson, List<Result> results)
      throws IOException {
    String path = Properties.ALLOW_GENERATE_WORSTCASE_DATA ?
        Properties.WORST_CASE_RESULT_FILE_NAME:
        Properties.NORMAL_CASE_RESULT_FILE_NAME;
    String resultDirectory= path + "best_next_aktive_robot/";
    File resDir = new File(resultDirectory);
    if (!resDir.exists()) {
      resDir.mkdirs();
    }
    String resultFileName =  resultDirectory + robotCount + "-results.json";
    try (FileWriter writer = new FileWriter(resultFileName)) {
      gson.toJson(results, writer);
    }
  }

}