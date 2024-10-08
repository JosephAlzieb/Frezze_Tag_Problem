package de.frezzetagproblem.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.formula.functions.T;

public class Helper {

  public static void readJsonFile(Path entry, List<Robot> on, List<Robot> off)
      throws FileNotFoundException {
    System.out.println("reading file (" + entry.toString() + ")");

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
  }

  public static void saveResults(int robotCount, String algoName, List<Result> results)
      throws IOException {
    System.out.println("saving results for robots-count - " + robotCount + " : " + algoName);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String path = getResultsPathName();

    String resultDirectory = path + algoName + "/";
    File resDir = new File(resultDirectory);
    if (!resDir.exists()) {
      resDir.mkdirs();
    }
    String resultFileName;
    resultFileName = resultDirectory + robotCount + "-results.json";

    try (FileWriter writer = new FileWriter(resultFileName)) {
      gson.toJson(results, writer);
    }
  }

  public static String getResultsPathName() {
    StringBuilder result = new StringBuilder();
    result.append("results/");
    result.append(Properties.METRIK.toLowerCase() + "/");

    if (Properties.ALLOW_GENERATE_WORSTCASE_DATA) {
      result.append("on_edge/");
    } else {
      result.append("normal/");
    }

    return result.toString();
  }

  public static int increaseRobotsCount(int robotsCount) {
    if (robotsCount < 15) {
      robotsCount++;
    } else if (robotsCount < 100) {
      robotsCount += 5;
    } else {
      robotsCount += 50;
    }

    System.out.println("increasing robots-count to " + robotsCount);
    return robotsCount;
  }

  public static String getPathName() {
    StringBuilder str = new StringBuilder();
    str.append("dummy-data/");
    //str.append(Properties.METRIK.toLowerCase() + "/");
    if (Properties.ALLOW_GENERATE_WORSTCASE_DATA){
      str.append("edge/");
    } else {
      str.append("normal/");
    }

    return str.toString();
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

  public static String buildExcelFileName() {
    StringBuilder str = new StringBuilder();
    str.append(Properties.METRIK);
    str.append("-Ergebnisse");
    if (Properties.ALLOW_GENERATE_WORSTCASE_DATA) {
      str.append("-Edge");
    } else {
      str.append("-Normal");
    }
    str.append(".xlsx");

    System.out.println("generating excel-file " + str);
    return str.toString();
  }

  public static List<Robot> copyRobots(List<Robot> robots) {
    List<Robot> l = new ArrayList<>();
    for (Robot robot : robots) {
      try {
        Robot copy = (Robot) robot.clone();
        l.add(copy);
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
    return l;
  }
}
