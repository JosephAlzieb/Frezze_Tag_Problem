package de.frezzetagproblem;

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
  public static String L_1 = "L1";
  public static String L_2 = "L2";

  /**
   * Variablen
   */
  public static int ROBOTS_COUNT = 5;
  public static int FILE_COUNT = 1;
  public static int TOTAL_ROBOTS_COUNT = 40;
  public static int OFFSET = 2;

  /**
   * Mit welchem Algorithms soll die App laufen
   */
  public static String ALGORITHM = Greedy_WITH_DISTANCE;

  /**
   * Mit welcher Metrik soll die Distanz zwischen den Robotern berechnet werden.
   */
  public static String METRIK = L_2;
}
