package de.frezzetagproblem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
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
import java.util.List;
import java.util.Map;

public class FrezzeTag {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT,Properties.TOTAL_ROBOTS_COUNT, Properties.OFFSET);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount, int offset) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    while (robotsCount <= totalRobotsCount) {
      Map<String, Double> results = new HashMap<>();
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

        /**
         * Hier wird das Algorithms ausgeführt
         * Timeunit (Zeiteinheit) wird nach jedem Schritt hochgezählt.
         */
        double timeunit = 0;
        List<Double> timeUnits = new ArrayList<Double>();
        while (!off.isEmpty()) {
          for (Robot r : on) {
            r.run(off, timeUnits);
          }

          /**
           * ON- und OFF-Listen werden aktualisiert.
           */
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
          timeunit += updateTimeUnit(timeUnits);
          timeUnits.clear();
        }

        results.put(entry.getFileName().toString(), timeunit);
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

  /**
   * Aktualisiert die Zeit, nachdem sich alle aktiven Roboter (ON-Roboter) um einen Schritt bewegt haben.
   * Da die Roboter parallel arbeiten, wird die Zeit um die maximale Dauer eines Schrittes erhöht.
   *
   * @param timeUnits Eine Liste von Zeiteinheiten, die die Dauer jedes Schrittes der Roboter darstellen.
   * @return Der maximale Wert in der Liste der Zeiteinheiten.
   */
  private static double updateTimeUnit(List<Double> timeUnits) {
    return Collections.max(timeUnits);
  }

  private static void saveResults(int robotCount, Gson gson, Map<String, Double> results)
      throws IOException {
    String resultDirectory= "results/greedy/";
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