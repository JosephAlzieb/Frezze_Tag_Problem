//package de.frezzetagproblem.optimal;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.stream.JsonReader;
//import de.frezzetagproblem.Properties;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//public class FrezzeTag_BestSequence {
//
//  static double minTime = Double.MAX_VALUE;
//  static List<Robot_Optimal> bestSequence = new ArrayList<>();
//
//  static List<Robot_Optimal> robots = new ArrayList<>();
//
//  public static void main(String[] args) throws IOException {
//    runExperiments(Properties.ROBOTS_COUNT,5, Properties.OFFSET);
//  }
//
//  private static void runExperiments(int robotsCount, int totalRobotsCount, int offset) throws IOException {
//    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//    while (robotsCount <= totalRobotsCount) {
//      Map<String, Double> results = new HashMap<>();
//      Path dir = Paths.get("dummy-data/" + robotsCount);
//      if (!Files.exists(dir)) {
//        Files.createDirectories(dir);
//      }
//
//      DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json");
//
//      /**
//       * Wir lesen alle Ordner in /dummy-dta/ eins nach dem anderen.
//       */
//      for (Path entry : stream) {
//        List<Robot_Optimal> off = new ArrayList<>();
//        List<Robot_Optimal> on = new ArrayList<>();
//
//        JsonReader reader = new JsonReader(new FileReader(entry.toFile()));
//        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
//        JsonObject robotsJson = jsonObject.getAsJsonObject("robots");
//
//        for (Map.Entry<String, JsonElement> robotEntry : robotsJson.entrySet()) {
//          JsonObject robotObj = robotEntry.getValue().getAsJsonObject();
//          Robot_Optimal r = gson.fromJson(robotObj, Robot_Optimal.class);
//
//          if (r.isAktive()) {
//            on.add(r);
//          } else {
//            off.add(r);
//          }
//        }
//
//        /**
//         * Hier wird das Algorithms ausgeführt
//         * Timeunit (Zeiteinheit) wird nach jedem Schritt hochgezählt.
//         */
//        double timeunit = 0;
//        List<Double> timeUnits = new ArrayList<Double>();
//
//        List<Robot_Optimal> sequence = new ArrayList<>();
//        sequence.add(on.get(0));
//
//        robots.addAll(off);
//
//        getBestSequence(sequence, 0);
//
//        System.out.println(bestSequence);
//
//        while (!off.isEmpty()) {
//          for (Robot_Optimal r : on) {
//            r.run(bestSequence, timeUnits);
//          }
//
//          /**
//           * ON- und OFF-Listen werden aktualisiert.
//           */
//          for (Iterator<Robot_Optimal> iterator = bestSequence.iterator(); iterator.hasNext(); ) {
//            Robot_Optimal robot = iterator.next();
//            if (robot.isAktive()) {
//              on.add(robot);
//              iterator.remove();
//            }
//          }
//
//          /**
//           * Nach jedem Schritt wird die Zeit (Endergebnis) aktualisiert, und
//           * die Liste der TimeUnits für den nächsten Durchlauf geleert.
//           */
//        }
//
//        results.put(entry.getFileName().toString(), minTime);
//      }
//
//      saveResults(robotsCount, gson, results);
//
//      if (robotsCount < 10){
//        robotsCount++;
//      } else if (robotsCount < 100){
//        robotsCount += 5;
//      } else {
//        robotsCount += 50;
//      }
//    }
//  }
//
//  private static void saveResults(int robotCount, Gson gson, Map<String, Double> results)
//      throws IOException {
//    String resultDirectory= "results/bestseq/";
//    File resDir = new File(resultDirectory);
//    if (!resDir.exists()) {
//      resDir.mkdirs();
//    }
//    String resultFileName =  resultDirectory + robotCount + "-results.json";
//    try (FileWriter writer = new FileWriter(resultFileName)) {
//      gson.toJson(results, writer);
//    }
//  }
//
//  static void getBestSequence(List<Robot_Optimal> sequence, double currentTime) {
//    if (sequence.size() == robots.size()) {
//      if (currentTime < minTime) {
//        minTime = currentTime;
//        bestSequence = new ArrayList<>(sequence);
//      }
//      return;
//    }
//
//    for (int i = 0; i < robots.size(); i++) {
//      Robot_Optimal o = robots.get(i);
//      if (!sequence.contains(o)) {
//        double additionalTime = robots.get(robots.size()-1).distance(robots.get(i));
//        sequence.add(o);
//        getBestSequence(sequence, currentTime + additionalTime);
//        sequence.remove(sequence.size() - 1);
//      }
//    }
//  }
//
//}