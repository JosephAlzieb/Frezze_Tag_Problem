package de.frezzetagproblem;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Robot
 * @Author Joseph Alzieb
 */
public class Robot {

  private String id;
  private Location location;
  private Status status;
  private boolean declared;
  private int velocity;
  private Robot targetRobot;

  public Robot(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  public Robot() {
  }

  /**
   * Hier wird das Algorithms ausgeführt, das in der Klasse {@link Properties} definiert ist.
   * @param off
   * @param time
   * @return
   */
  public void run(List<Robot> off, List<Double> time) {
    //TODO Immer beginnen wir hier mit dem ersten Roboter. hier müssen wir auch alle ON-Roboter übergeben. Bsp. Es gibt noch einen Robot zu aktivieren, und der erste Robot "0" wäre sehr weit weg von dem. aber der "3" sehr nah.
    //TODO Was ist mit der Rheinfolge der ON-Roboter. Es kann sein, dass ON-Robot "0" sehr weit weg von OFF-Robot "2", wobei ON-Robot "1" sehr nah an "2" ist.
    if (Properties.ALGORITHM.equals(Properties.Greedy_WITH_TIMEUNITS_1)){
      runGreedyAlgo_1(off, time);
    } else if (Properties.ALGORITHM.equals(Properties.Greedy_WITH_DISTANCE)){
      runGreedyAlgo_Dis(off, time);
    }
  }

  /**
   * Hier sucht Robot (this) das näherte Nachbar, und aktiviert ihn.
   * Die Zeit, die zu aktivierung benötigt wird, wäre den Abstand zwischen den Robotern.
   * @param off List of Robots with status "OFF".
   * @param time List of Timeunits.
   */
  private void runGreedyAlgo_Dis(List<Robot> off, List<Double> time) {
    this.targetRobot = this.getNearestNeighbor(off);
    if (this.targetRobot != null) {
      this.targetRobot.declare();
      this.targetRobot.aktive();
      time.add(this.distance(this.targetRobot));

      this.targetRobot.removeTargetRobot();
    }
  }

  /**
   * Wir unterscheiden 3 Fällen:
   * 1: Robot (this) ist ON, und hat noch kein Robot gefunden, den er aufweckt.
   * -> Es wird nach Target-Robot gesucht (der nächste Nachbar), und ihn markiert,
   * -> damit er nicht von einem anderen Roboter markiert wird.
   *
   * 2: Robot (this) hat seinen Target-Robot erreicht, und weckt ihn auf.
   * 3: Robot (this) muss sich zu seinem Target-Robot bewegen.
   * @param off List of Robots with status "OFF".
   * @param time List of Timeunits.
   */
  private void runGreedyAlgo_1(List<Robot> off, List<Double> time) {
    double unit = 1;
    if (!hasTargetRobot()) {
      this.targetRobot = this.getNearestNeighbor(off);
      if (this.targetRobot != null && !this.targetRobot.isDeclared()) {
        //Robot markiert sein Goal
        System.out.println(this.id + " markiert " + this.targetRobot.id);
        this.targetRobot.declare();

        //Hier könnte sich der Robot direkt zu seinem Target bewegen, und ihn aktivieren. Dann schaffen wir das in 1-Zeiteinheit
      }
    } else if (Arrays.equals(this.location.toArray(), this.targetRobot.location.toArray())){
        //Robot hat sein Goal erreicht, und er weckt ihn auf. Status vom Target ist ON
        this.targetRobot.aktive();
        //Target vom Roboter ist wieder null, um neue Roboter aufwecken zu können
        System.out.println(this.id + " aktiviert " + this.targetRobot.id);
        this.removeTargetRobot();
    } else {
      //Robot ist bei seinem Target gelandet.
      this.location.x = this.targetRobot.location.x;
      this.location.y = this.targetRobot.location.y;
      System.out.println(this.id + " geht zu " + this.targetRobot.id);
    }
    time.add(unit);
  }

  /**
   * Hier wird einfach über alle OFF-Roboter iteriert, und den nährten Roboter für (this) gefunden.
   * @param off List of Roboter with Status "OFF"
   * @return
   */
  public Robot getNearestNeighbor(List<Robot> off) {
    Robot nearest = null;
    for (Robot r : off) {
      if (!r.isDeclared() && nearest == null) {
        nearest = r;
      } else if (!r.isDeclared() && nearest != null) {
        if (this.distance(nearest) > this.distance(r)) {
          nearest = r;
        }
      }
    }
    return nearest;
  }

  /**
   * Die Methode berechnet den Abstand zwischen zwei Punkten.
   * Je nachdem welche Metrik in {@link Properties} (L1, oder L2) gesetzt ist, wird diese Metrik zur Berechnung
   * der Distanz verwendet.
   * @param that other Robot
   * @return Distance from Robot (this) to Robot (that)
   */
  public double distance(Robot that) {
    if (Properties.METRIK.equals(Properties.L_1)) {
      return Math.abs(this.location.x - that.location.x) + Math.abs(this.location.y - that.location.y);
    } else {
      return Math.sqrt(Math.pow(this.location.x - that.location.x, 2) + Math.pow(this.location.y - that.location.y, 2));
    }

  }

  public String getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

  public boolean isAktive() {
    return this.status == Status.ON;
  }

  public void aktive() {
    this.status = Status.ON;
  }

  public boolean isDeclared() {
    return this.declared;
  }

  public void declare() {
    this.declared = true;
  }

  public boolean hasTargetRobot(){
    return this.targetRobot != null;
  }

  public void removeTargetRobot(){
    this.targetRobot = null;
  }

  public double getLocation_x(){
    return this.location.x;
  }

  public double getLocation_y(){
    return this.location.y;
  }
  @Override
  public String toString() {
    return "Robot{" +
        "id='" + id + '\'' +
        ", location=" + location +
        ", status=" + status +
        ", declared=" + declared +
        ", velocity=" + velocity +
        ", targetRobot=" + targetRobot +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Robot robot)) {
      return false;
    }
    return Objects.equals(id, robot.id) && Objects.equals(location,
        robot.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, location);
  }
}