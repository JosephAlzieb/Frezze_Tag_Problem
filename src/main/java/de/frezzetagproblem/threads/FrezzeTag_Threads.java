package de.frezzetagproblem.threads;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FrezzeTag_Threads {

  public static void main(String[] args) throws IOException {
    runExperiments(Properties.ROBOTS_COUNT,Properties.TOTAL_ROBOTS_COUNT, Properties.OFFSET);
  }

  private static void runExperiments(int robotsCount, int totalRobotsCount, int offset) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();


    while (robotsCount <= totalRobotsCount) {
      Map<String, Double> results = new HashMap<>();
      Path dir = Paths.get(
          Properties.ALLOW_GENERATE_WORSTCASE_DATA ?
              Properties.WORST_CASE_FILE_NAME:
              Properties.NORMAL_CASE_FILE_NAME
                  + robotsCount);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }

      DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json");

      /**
       * Wir lesen alle Ordner in /dummy-dta/ eins nach dem anderen.
       */
      for (Path entry : stream) {
        List<Robot_Thread> off = new ArrayList<>();
        List<Robot_Thread> on = new ArrayList<>();

        JsonReader reader = new JsonReader(new FileReader(entry.toFile()));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonObject robotsJson = jsonObject.getAsJsonObject("robots");

        for (Map.Entry<String, JsonElement> robotEntry : robotsJson.entrySet()) {
          JsonObject robotObj = robotEntry.getValue().getAsJsonObject();
          Robot_Thread r = gson.fromJson(robotObj, Robot_Thread.class);

          if (r.isAktive()) {
            on.add(r);
          } else {
            off.add(r);
          }
        }

        long startTime = System.nanoTime();
        ExecutorService executor = Executors.newCachedThreadPool();


        while (!off.isEmpty()) {
          List<Future<?>> futures = new ArrayList<>();
          for (Robot_Thread r : on) {
            Future<?> future = executor.submit(() -> r.run(off));
            futures.add(future);
          }

          // Warte auf das Ende aller gestarteten Tasks
          for (Future<?> future : futures) {
            try {
              future.get();
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
          }

          for (Iterator<Robot_Thread> iterator = off.iterator(); iterator.hasNext(); ) {
            Robot_Thread robot = iterator.next();
            if (robot.isAktive()) {
              on.add(robot);
              iterator.remove();
            }
          }
        }


        long endTime = System.nanoTime();
        long durationInNanoseconds = endTime - startTime;
        double durationInMilliseconds = durationInNanoseconds / 1_000_000.0;

        System.out.println(durationInMilliseconds);
        results.put(entry.getFileName().toString(), durationInMilliseconds);

        executor.shutdown();
        try {
          if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
          }
        } catch (InterruptedException e) {
          executor.shutdownNow();
        }
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

  private static void saveResults(int robotCount, Gson gson, Map<String, Double> results)
      throws IOException {
    String resultDirectory= "results/threads/";
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