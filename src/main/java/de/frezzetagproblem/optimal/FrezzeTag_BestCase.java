package de.frezzetagproblem.optimal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Properties;
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

public class FrezzeTag_BestCase {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT,8, Properties.OFFSET);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount, int offset) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    while (robotsCount <= totalRobotsCount) {
      Map<String, Double> results = new HashMap<>();
      Path dir = Paths.get("dummy-data/" + robotsCount);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }

      DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json");

      /**
       * Wir lesen alle Ordner in /dummy-dta/ eins nach dem anderen.
       */
      for (Path entry : stream) {
        List<Robot_BestCase> off = new ArrayList<>();
        List<Robot_BestCase> on = new ArrayList<>();

        JsonReader reader = new JsonReader(new FileReader(entry.toFile()));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonObject robotsJson = jsonObject.getAsJsonObject("robots");

        for (Map.Entry<String, JsonElement> robotEntry : robotsJson.entrySet()) {
          JsonObject robotObj = robotEntry.getValue().getAsJsonObject();
          Robot_BestCase r = gson.fromJson(robotObj, Robot_BestCase.class);

          if (r.isAktive()) {
            on.add(r);
          } else {
            off.add(r);
          }
        }

        double timeunit = 0;
        List<Double> timeUnits = new ArrayList<>();
        while (!off.isEmpty()) {

          TreeMap<Pair<String, String>, Double> possibleSolutions  = new TreeMap<>();


          if (on.size() > 3) {
            //Hier werden alle möglichen Permutationen einer Liste erzeugt
           List<List<Robot_BestCase>> permutations = generatePermutations(on);

           for (List<Robot_BestCase> permutation : permutations) {
             for (Robot_BestCase r : permutation) {
               //Hier werden alle möglichen Lösungen gespeichert. z.b. Robot_1 zu Robot_2 benötigt X
               r.computeDistance(off,possibleSolutions);
             }
           }
          }

          //Hier wird das Algorithmus zu Aktivierung der Roboter ausgeführt.
          for (Robot_BestCase r : on) {
            r.run(off, timeUnits, possibleSolutions);
          }

          /**
           * ON- und OFF-Listen werden aktualisiert.
           */
          for (Iterator<Robot_BestCase> iterator = off.iterator(); iterator.hasNext(); ) {
            Robot_BestCase robot = iterator.next();
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

        results.put(entry.getFileName().toString(), timeunit);
        System.out.println("-------------------------------------------------");
      }

      saveResults(robotsCount, gson, results);

      if (robotsCount < 10){
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

  private static void saveResults(int robotCount, Gson gson, Map<String, Double> results)
      throws IOException {
    String resultDirectory= "results/bestCase/";
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