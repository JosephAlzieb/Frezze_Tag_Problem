package de.frezzetagproblem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.frezzetagproblem.models.Location;
import de.frezzetagproblem.models.Robot;
import de.frezzetagproblem.models.Status;
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

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    int x = 5;
    while (x < 41) {
      for (int j = 0; j < 5; j++) {
        List<Robot> robots = new ArrayList<>();

        // der erste aktive Roboter
        Location randomLocation = getRandomLocation();
        robots.add(new Robot("0", randomLocation));
        robots.get(0).status = Status.ON;

        // Hier werden die anderen schlafenden Roboter generiert.
        for (int i = 1; i < x; i++) {
          randomLocation = getRandomLocation();
          robots.add(new Robot(String.valueOf(i), randomLocation));
        }

        String directory = "dummy-data/" + x + "/";
        File dir = new File(directory);
        if (!dir.exists()) {
          dir.mkdirs();
        }

        save(directory + j + ".json", robots);
      }
      x = x * 2;
    }
  }

  /**
   * erzeugt rondom-location.
   * @return
   */
  private static Location getRandomLocation() {
    Random random = new Random();
    return new Location(random.nextInt(1000), random.nextInt(1000));
  }

  /**
   * erzeugt ein Json-File mit dummy-data.
   * @param file
   * @param robots
   * @throws IOException
   */
  private static void save(String file, List<Robot> robots) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonObject robotsJson = new JsonObject();
    for (Robot robot : robots) {
      JsonObject robotJson = new JsonObject();
      JsonObject locationJson = new JsonObject();
      locationJson.addProperty("x", robot.location.x);
      locationJson.addProperty("y", robot.location.y);
      robotJson.add("location", locationJson);
      robotJson.addProperty("status", robot.status.name());
      robotsJson.add(robot.id, robotJson);
    }

    JsonObject rootJson = new JsonObject();
    rootJson.addProperty("size", robots.size());
    rootJson.add("robots", robotsJson);

    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(rootJson, writer);
    }
  }
}
