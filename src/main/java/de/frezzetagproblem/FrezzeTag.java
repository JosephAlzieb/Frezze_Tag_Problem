package de.frezzetagproblem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.models.Robot;
import de.frezzetagproblem.models.Status;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FrezzeTag {

  public static void main(String[] args) throws IOException {
    runExperiments(5,40, 2);
  }

  private static void runExperiments(int robotCount, int totalRobotsCount, int offset) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    while (robotCount <= totalRobotsCount) {
      Map<String, Integer> results = new HashMap<>();
      Path dir = Paths.get("dummy-data/" + robotCount);
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

          if (Status.ON == r.status) {
            on.add(r);
          } else if (Status.OFF == r.status) {
            off.add(r);
          }
        }

        /**
         * timeunit (Zeiteinheit) wird nach jedem Schritt hochgezählt.
         */
        int timeunit = 0;
        while (!off.isEmpty()) {
          for (Robot r : on) {
            r.run(off);
          }

          /**
           * ON- und OFF-Listen werden aktualisiert.
           */
          for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
            Robot robot = iterator.next();
            if (robot.status == Status.ON) {
              on.add(robot);
              iterator.remove();
            }
          }
          timeunit++;
        }

        results.put(entry.getFileName().toString(), timeunit);
      }

      saveResults(robotCount, gson, results);

      robotCount *= offset;
    }
  }

  private static void saveResults(int robotCount, Gson gson, Map<String, Integer> results)
      throws IOException {
    String resultDirectory= "results/";
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