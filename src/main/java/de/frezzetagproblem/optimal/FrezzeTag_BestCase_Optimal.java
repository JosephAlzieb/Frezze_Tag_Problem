package de.frezzetagproblem.optimal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Pair;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.Result;
import de.frezzetagproblem.Robot;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FrezzeTag_BestCase_Optimal {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT,8, Properties.OFFSET);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount, int offset) throws IOException {
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

      /**
       * Wir lesen alle Ordner in /dummy-dta/ eins nach dem anderen.
       */
      for (Path entry : stream) {
        List<Robot_BestCase_Optimal> off = new ArrayList<>();
        List<Robot_BestCase_Optimal> on = new ArrayList<>();

        String f = entry.getFileName().toString();
        Result  result = new Result(f);



        JsonReader reader = new JsonReader(new FileReader(entry.toFile()));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonObject robotsJson = jsonObject.getAsJsonObject("robots");

        for (Map.Entry<String, JsonElement> robotEntry : robotsJson.entrySet()) {
          JsonObject robotObj = robotEntry.getValue().getAsJsonObject();
          Robot_BestCase_Optimal r = gson.fromJson(robotObj, Robot_BestCase_Optimal.class);

          if (r.isAktive()) {
            on.add(r);
          } else {
            off.add(r);
          }
        }

        List<List<Robot_BestCase_Optimal>> permutations = generatePermutations(off);

        List<Double> timeUnits = new ArrayList<>();
        List<String> wake_up = new ArrayList<>();

        List<Robot_BestCase_Optimal> puffer_aktive_robots = new ArrayList<>();
        for (List<Robot_BestCase_Optimal> permutation : permutations) {
          List<Robot_BestCase_Optimal> p_off = copyRobots(permutation);
          List<Robot_BestCase_Optimal> p_on = copyRobots(on);
          double timeunit = 0;


          while (!p_off.isEmpty()) {

            for (Robot_BestCase_Optimal r : p_on) {
              if (!p_off.isEmpty()){
                Robot_BestCase_Optimal targetRobot = r.getNextRobot(p_off);
                if (targetRobot != null){
                  r.aktive(targetRobot);
                  double distance = r.distance(targetRobot);
                  wake_up.add(String.format("%s wake %s up in (%s)", r.getId(), targetRobot.getId(), (int) distance));
                  timeUnits.add(distance);
                  r.moveTo(targetRobot.getLocation());
                  p_off.remove(targetRobot);
                  puffer_aktive_robots.add(targetRobot);
                }
              }
            }

            p_on.addAll(puffer_aktive_robots);
            puffer_aktive_robots.clear();

            timeunit += getMaxValue(timeUnits);
            timeUnits.clear();
          }
          result.add(timeunit, List.copyOf(wake_up), permutation);

          wake_up.clear();

          System.out.println("-------------------------------------------------");
        }
        results.add(result);
      }
      saveResults(robotsCount, gson, results, false);

      List<Result> optimalResults = Result.getOptimalResults(results);
      saveResults(robotsCount, gson, optimalResults, true);

      if (robotsCount < 10){
        robotsCount++;
      } else if (robotsCount < 100){
        robotsCount += 5;
      } else {
        robotsCount += 50;
      }
    }
  }

  private static List<Robot_BestCase_Optimal> copyRobots(List<Robot_BestCase_Optimal> robots) {
    List<Robot_BestCase_Optimal> l = new ArrayList<>();
    for (Robot_BestCase_Optimal robot : robots) {
      try {
        Robot_BestCase_Optimal copy = (Robot_BestCase_Optimal) robot.clone();
        l.add(copy);
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
    return l;
  }

  private static double getMaxValue(List<Double> list) {
    return Collections.max(list);
  }

  private static double getMinValue(List<Double> list) {
    return Collections.min(list);
  }

  public static <T> List<List<T>> generatePermutations(List<T> list) {
    List<List<T>> lists = new LinkedList<>();
    permute(list, 0, lists);
    return lists;
  }

  private static <T> void permute(List<T> list, int start, List<List<T>> lists) {
    if (start >= list.size() - 1) {
      lists.add(new ArrayList<>(list));
      return;
    }

    for (int i = start; i < list.size(); i++) {
      Collections.swap(list, start, i);
      permute(list, start + 1, lists);
      Collections.swap(list, start, i); // Rückgängig machen des Tauschs
    }
  }

  private static void saveResults(int robotCount, Gson gson, List<Result> results, boolean optimal)
      throws IOException {
    String resultDirectory= "results/bestCase_optimal/";
    File resDir = new File(resultDirectory);
    if (!resDir.exists()) {
      resDir.mkdirs();
    }
    String resultFileName =  null;
    if (optimal){
      resultFileName = resultDirectory + robotCount + "-optimal-results.json";
    } else {
      resultFileName = resultDirectory + robotCount + "-results.json";
    }
    try (FileWriter writer = new FileWriter(resultFileName)) {
      gson.toJson(results, writer);
    }
  }

}