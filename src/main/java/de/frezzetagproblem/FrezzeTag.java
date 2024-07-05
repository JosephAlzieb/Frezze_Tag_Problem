package de.frezzetagproblem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.models.Robot;
import de.frezzetagproblem.models.Status;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrezzeTag {

  public static void main(String[] args) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    int x = 5;

    while (x < 41) {
      Map<String, Integer> results = new HashMap<>();
      Path dir = Paths.get("dummy-data/" + x);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }

      DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json");

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
        System.out.println(off);
      }

      x = x * 2;
    }
  }

}