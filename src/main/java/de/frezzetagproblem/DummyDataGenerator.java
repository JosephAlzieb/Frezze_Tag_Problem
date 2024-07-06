package de.frezzetagproblem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.frezzetagproblem.models.Location;
import de.frezzetagproblem.models.Robot;
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
   * Die Methode generiert Dummy-Roboters in Json-Files.
   * @param robotsCount Anzahl der Roboter in den ersten (filesCount) Json-Files. (im ersten Experiment)
   * @param totalRobotsCount Anzahl der Roboter in den letzten (filesCount) Json-Files. (im letzten Experiment)
   * @param filesCount  Anzahl der Json-Files.
   * @param offset      Unterschied zu dem nächsten Experiment.
   * @throws IOException
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
   * erzeugt ein Json-File mit dummy-data.
   * @param file um Ergebnisse zu speichern
   * @param robots die random-erzeugte Roboters
   * @throws IOException
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
