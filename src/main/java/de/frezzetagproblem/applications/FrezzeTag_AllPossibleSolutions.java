package de.frezzetagproblem.applications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.models.Result;
import de.frezzetagproblem.models.Robot;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
public class FrezzeTag_AllPossibleSolutions {

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
        List<Robot> off = new ArrayList<>();
        List<Robot> on = new ArrayList<>();

        String f = entry.getFileName().toString();
        Result  result = new Result(f);



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

        List<List<Robot>> permutations = generatePermutations(off);

        List<Double> timeUnits = new ArrayList<>();
        List<String> wake_up = new ArrayList<>();

        List<Robot> puffer_aktive_robots = new ArrayList<>();
        for (List<Robot> permutation : permutations) {
          List<Robot> p_off = copyRobots(permutation);
          List<Robot> p_on = copyRobots(on);
          double timeunit = 0;


          while (!p_off.isEmpty()) {

            for (Robot r : p_on) {
              if (!p_off.isEmpty()){
                Robot targetRobot = r.getNextRobot(p_off);
                if (targetRobot != null){
                  targetRobot.aktive();
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
    String resultDirectory= "results/all_possible_solutions/";
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