package de.frezzetagproblem.applications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.models.Distance;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
public class FrezzeTag_AllPossibleSolutions {

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

      int experimentNumber = 1;

      for (Path entry : stream) {
        List<Robot> off = new ArrayList<>();
        List<Robot> on = new ArrayList<>();

        String f = entry.getFileName().toString();
        Result  result = new Result(f, robotsCount, experimentNumber++);



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

        execute(on, off, result);

        results.add(result);
      }

      /*
      Alle möglichen Lösungen werden nur für n = 5 gespeichert, denn für große n kann die Datei sehr groß werden.
       */
      if (robotsCount == 5){
        saveResults(robotsCount, gson, results, null);
      }

      List<Result> optimalResults = Result.getOptimalResults(results);
      saveResults(robotsCount, gson, optimalResults, "-bestCase");

      List<Result> worstCaseResults = Result.getWorstCaseResults(results);
      saveResults(robotsCount, gson, worstCaseResults, "-worstCase");

      if (robotsCount < 15){
        robotsCount++;
      } else if (robotsCount < 100){
        robotsCount += 5;
      } else {
        robotsCount += 50;
      }
    }
  }

  public static void execute(
      List<Robot> on,
      List<Robot> off,
      Result result) {
    List<List<Robot>> permutations = generatePermutations(off);
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
          if (target != null && r.equals(start) ){
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

  private static double getMaxValue(List<Double> list) {
    return Collections.max(list);
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

  private static void saveResults(int robotCount, Gson gson, List<Result> results, String str)
      throws IOException {
    String path = Properties.ALLOW_GENERATE_WORSTCASE_DATA ?
        Properties.WORST_CASE_RESULT_FILE_NAME:
        Properties.NORMAL_CASE_RESULT_FILE_NAME;
    String resultDirectory= path + "all_possible_solutions/";
    File resDir = new File(resultDirectory);
    if (!resDir.exists()) {
      resDir.mkdirs();
    }
    String resultFileName;
    if (str != null){
      resultFileName = resultDirectory + robotCount + str + "-results.json";
    } else {
      resultFileName = resultDirectory + robotCount + "-results.json";
    }
    try (FileWriter writer = new FileWriter(resultFileName)) {
      gson.toJson(results, writer);
    }
  }

}