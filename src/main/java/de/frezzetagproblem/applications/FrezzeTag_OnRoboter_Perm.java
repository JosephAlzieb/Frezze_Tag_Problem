package de.frezzetagproblem.applications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.models.Pair;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FrezzeTag_OnRoboter_Perm {

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

        double timeunit = 0;
        List<Double> timeUnits = new ArrayList<>();
        List<String> wake_up_tree = new ArrayList<>();
        while (!off.isEmpty()) {

          TreeMap<Pair<String, String>, Double> possibleSolutions  = new TreeMap<>();


          if (on.size() > 3) {
            //Hier werden alle möglichen Permutationen einer Liste erzeugt
           List<List<Robot>> permutations = generatePermutations(on);

           for (List<Robot> permutation : permutations) {
             for (Robot r : permutation) {
               //Hier werden alle möglichen Lösungen gespeichert. z.b. Robot_1 zu Robot_2 benötigt X
               r.computeDistance(off,possibleSolutions);
             }
           }
          }

          for (Robot r : on) {
            r.run(off, timeUnits, possibleSolutions, wake_up_tree);
          }

          for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
            Robot robot = iterator.next();
            if (robot.isAktive()) {
              on.add(robot);
              iterator.remove();
            }
          }

          /**
           * Nach jedem Schritt wird die Zeit (Endergebnis) aktualisiert, und
           * die Liste der TimeUnits für den nächsten Durchlauf geleert.
           */
          timeunit += getMaxValue(timeUnits);
          timeUnits.clear();
        }

        result.add(timeunit, List.copyOf(wake_up_tree), null);
        results.add(result);
        wake_up_tree.clear();
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

  private static void saveResults(int robotCount, Gson gson, List<Result> results)
      throws IOException {
    String resultDirectory= "results/on_roboter_permutation/";
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