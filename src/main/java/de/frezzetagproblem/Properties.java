package de.frezzetagproblem;

import de.frezzetagproblem.models.Location;

/**
 * Die Kasse ist dafür gedacht, Variablen und Konstanten an einer Stelle zu definieren.
 * @Author Joseph Alzieb
 */
public class Properties {

  /**
   * Algorithmen & Metriken
   */
  public static String Greedy_WITH_TIMEUNITS_1 = "Greedy_TimeUnits";
  public static String Greedy_WITH_DISTANCE = "Greedy_Distance";
  public static String Greedy_SIMULATION = "Greedy_Simulation";
  public static String L_1 = "L1";
  public static String L_2 = "L2";

  /**
   * Variablen
   */
  public static int ROBOTS_COUNT = 5;
  public static int FILE_COUNT = 5;
  public static int TOTAL_ROBOTS_COUNT = 1000;
  public static int OFFSET = 2;

  /**
   * Properties für Random-Locations
   */
  public static Location START_Location = new Location(0,0);
  public static int R = 500;

  public static int MIN = -500;

  public static int MAX = 500;

  public static String WORST_CASE_FILE_NAME = "dummy-data/worstcase/";

  public static String NORMAL_CASE_FILE_NAME = "dummy-data/normal/";

  public static boolean ALLOW_GENERATE_WORSTCASE_DATA = false;

  public static boolean ALLOW_MULTIPLE_ROBOTS = false;

  /**
   * Mit welchem Algorithms soll die App laufen
   */
  public static String ALGORITHM = Greedy_WITH_DISTANCE;

  /**
   * Mit welcher Metrik soll die Distanz zwischen den Robotern berechnet werden.
   */
  public static String METRIK = L_2;
}
