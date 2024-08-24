package de.frezzetagproblem.applications;

import static de.frezzetagproblem.Properties.R;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.models.Helper;
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
 *
 * @Author Joseph Alzieb
 */
public class DummyDataGenerator {

  public static void main(String[] args) throws IOException {
    generateDummyRoboters(
        Properties.ROBOTS_COUNT,
        Properties.TOTAL_ROBOTS_COUNT,
        Properties.FILE_COUNT);
  }

  /**
   * Generiert Dummy-Roboter in JSON-Dateien.
   *
   * @param robotsCount      Anzahl der Roboter in den ersten (filesCount) JSON-Dateien (im ersten
   *                         Experiment).
   * @param totalRobotsCount Anzahl der Roboter in den letzten (filesCount) JSON-Dateien (im letzten
   *                         Experiment).
   * @param filesCount       Anzahl der JSON-Dateien.
   * @throws IOException Wenn ein E/A-Fehler auftritt.
   */
  private static void generateDummyRoboters(int robotsCount, int totalRobotsCount, int filesCount) throws IOException {
    while (robotsCount <= totalRobotsCount) {
      for (int j = 0; j < filesCount; j++) {
        List<Robot> robots = new ArrayList<>();

        // der erste aktive Roboter
        robots.add(new Robot("0", new Location(0, 0), false));
        robots.get(0).aktive();
        int index = 1;

        if (Properties.ALLOW_MULTIPLE_ROBOTS && robotsCount >= 50) {
          robots.add(new Robot("1", new Location(R / 2, R / 2), false));
          robots.get(1).aktive();

          robots.add(new Robot("2", new Location(R / 2, -R / 2), false));
          robots.get(2).aktive();

          robots.add(new Robot("3", new Location(-R / 2, R / 2), false));
          robots.get(3).aktive();

          robots.add(new Robot("4", new Location(-R / 2, -R / 2), false));
          robots.get(4).aktive();

          index = 5;
        }

        // Hier werden die anderen schlafenden Roboter generiert.
        for (int i = index; i < robotsCount; i++) {
          Location randomLocation = null;
          if (Properties.ALLOW_GENERATE_WORSTCASE_DATA) {
            randomLocation = getRandomLocationOnEdge();
          } else {
            randomLocation = getRandomLocation();

          }
          robots.add(new Robot(String.valueOf(i), randomLocation, false));
        }

        String directory = Helper.getPathName();
        String pathname = directory + robotsCount + "/";
        File dir = new File(pathname);
        if (!dir.exists()) {
          dir.mkdirs();
        }

        save(pathname + j + ".json", robots);
      }
      robotsCount = Helper.increaseRobotsCount(robotsCount);
    }
  }

  /**
   * Alle Punkte müssen im Kreis liegen für L2. oder im Quadrat für L1. Für L2 muss die Distanz
   * zwischen der Random-Location und der Start-Location < Radius sein.
   *
   * @return random Location
   */
  private static Location getRandomLocation() {
    Random random = new Random();
    while (true) {
      int x = random.nextInt((Properties.MAX - Properties.MIN) + 1) + Properties.MIN;
      int y = random.nextInt((Properties.MAX - Properties.MIN) + 1) + Properties.MIN;
      Location location = new Location(x, y);
      if (location.distance(Properties.START_Location) < R) {
        return location;
      }
    }
  }


  private static Location getRandomLocationOnEdge() {
    Random random = new Random();
    int edge = random.nextInt(4); // 0: top, 1: right, 2: bottom, 3: left
    int x, y;

    while (true) {
      switch (edge) {
        case 0: // top edge
          x = random.nextInt((Properties.MAX - Properties.MIN) + 1) + Properties.MIN;
          y = random.nextInt(21) + 80;
          break;
        case 1: // right edge
          x = random.nextInt(21) + 80;
          y = random.nextInt((Properties.MAX - Properties.MIN) + 1) + Properties.MIN;
          break;
        case 2: // bottom edge
          x = random.nextInt((Properties.MAX - Properties.MIN) + 1) + Properties.MIN;
          y = random.nextInt(21) - 100;
          break;
        case 3: // left edge
        default:
          x = random.nextInt(21) - 100;
          y = random.nextInt((Properties.MAX - Properties.MIN) + 1) + Properties.MIN;
          break;
      }
      Location location = new Location(x, y);

      if (location.distance(Properties.START_Location) < R) {
        return location;
      }
    }
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
