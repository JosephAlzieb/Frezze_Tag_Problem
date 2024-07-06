package de.frezzetagproblem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generator, um dummy-data für Experimente zu generieren.
 * @Author Joseph Alzieb
 */
public class DummyDataGenerator {

  public static void main(String[] args) throws IOException {
    generateDummyRoboters(Properties.ROBOTS_COUNT, Properties.TOTAL_ROBOTS_COUNT, Properties.FILE_COUNT, Properties.OFFSET);
  }

  /**
   * Generiert Dummy-Roboter in JSON-Dateien.
   *
   * @param robotsCount         Anzahl der Roboter in den ersten (filesCount) JSON-Dateien (im ersten Experiment).
   * @param totalRobotsCount    Anzahl der Roboter in den letzten (filesCount) JSON-Dateien (im letzten Experiment).
   * @param filesCount          Anzahl der JSON-Dateien.
   * @param offset              Differenz zur Anzahl der Roboter im nächsten Experiment.
   * @throws IOException        Wenn ein E/A-Fehler auftritt.
   */
  private static void generateDummyRoboters(int robotsCount, int totalRobotsCount, int filesCount, int offset) throws IOException {
    while (robotsCount <= totalRobotsCount) {
      for (int j = 0; j < filesCount; j++) {
        List<Robot> robots = new ArrayList<>();

        // der erste aktive Roboter
        Location randomLocation = getRandomLocation();
        robots.add(new Robot("0", randomLocation, false));
        robots.get(0).aktive();

        // Hier werden die anderen schlafenden Roboter generiert.
        for (int i = 1; i < robotsCount; i++) {
          randomLocation = getRandomLocation();
          robots.add(new Robot(String.valueOf(i), randomLocation, false));
        }

        String directory = "dummy-data/" + robotsCount + "/";
        File dir = new File(directory);
        if (!dir.exists()) {
          dir.mkdirs();
        }

        save(directory + j + ".json", robots);
      }
      robotsCount *= offset;
    }
  }

  /**
   * @return random Location
   */
  private static Location getRandomLocation() {
    Random random = new Random();
    return new Location(random.nextInt(1000), random.nextInt(1000));
  }

  /**
   * Erzeugt eine JSON-Datei mit Dummy-Daten.
   *
   * @param file   Die Datei, in der die Ergebnisse gespeichert werden sollen.
   * @param robots Die zufällig erzeugten Roboter.
   * @throws IOException Wenn ein E/A-Fehler auftritt.
   */
  private static void save(String file, List<Robot> robots) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonObject robotsJson = new JsonObject();
    for (Robot robot : robots) {
      JsonObject robotJson = new JsonObject();
      JsonObject locationJson = new JsonObject();
      robotJson.addProperty("id", robot.getId());
      locationJson.addProperty("x", robot.getLocation_x());
      locationJson.addProperty("y", robot.getLocation_y());
      robotJson.add("location", locationJson);
      robotJson.addProperty("status", robot.getStatus().name());
      robotsJson.add(robot.getId(), robotJson);
    }

    JsonObject rootJson = new JsonObject();
    rootJson.addProperty("size", robots.size());
    rootJson.add("robots", robotsJson);

    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(rootJson, writer);
    }
  }
}
