package de.frezzetagproblem.optimal;

import de.frezzetagproblem.Location;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.Status;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Robot
 * @Author Joseph Alzieb
 */
public class Robot_BestCase {

  private String id;
  private Location location;
  private Status status;
  private boolean declared;
  private int velocity;
  private Robot_BestCase targetRobot;

  public Robot_BestCase(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  public Robot_BestCase() {
  }

  public void run(List<Robot_BestCase> off, List<Double> time,
      TreeMap<Pair<String, String>, Double> possibleSolutions) {
    this.targetRobot = this.getNearestRobot(off);
    if (this.targetRobot != null) {
      double distance = this.distance(this.targetRobot);

      //Wenn eine besser Lösung gibt, um das Target zu aktivieren, wird nach ihr gesucht.
      if (existsBetterSolution(possibleSolutions, distance).get()){
        this.removeTargetRobot();
        return;
      }
      this.targetRobot.declare();
      this.aktive(targetRobot);
      time.add(distance);

      //Der Roboter bewegt sich zu dem zu aktivierenden Robot.
      this.moveTo(this.targetRobot.location);
      this.removeTargetRobot();
    }
  }

  private AtomicBoolean existsBetterSolution(TreeMap<Pair<String, String>, Double> possibleSolutions, Double distance){
    AtomicBoolean b = new AtomicBoolean(false);
    possibleSolutions.forEach((x,y)->{
      if (this.targetRobot.id.equals(x.getSecond()) && y < distance){
        b.set(true);
        return;
      }
    });

    return b;
  }

  public void computeDistance(List<Robot_BestCase> off,
      TreeMap<Pair<String, String>, Double> possibleSolutions) {
    var targetRobot = this.getNearestRobot(off);
    if (targetRobot != null) {
      possibleSolutions.put(getKey(targetRobot), this.distance(targetRobot));
    }
  }

  private Pair<String, String> getKey(Robot_BestCase targetRobot) {
    return new Pair (this.id, targetRobot.id);
  }

  public Robot_BestCase getNearestRobot(List<Robot_BestCase> off) {
    Robot_BestCase nearest = null;
    for (Robot_BestCase r : off) {
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
  public double distance(Robot_BestCase that) {
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

  public void aktive(Robot_BestCase targetRobot) {
    targetRobot.status = Status.ON;
    System.out.println(this.id + " aktiviert " + targetRobot.id);
  }

  //von The Visibility Freeze-Tag Problem (FTP in Euclidean Space)
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

  public int getLocation_x(){
    return this.location.x;
  }

  public int getLocation_y(){
    return this.location.y;
  }

  public void moveTo(Location location){
    this.location = location;
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
    if (!(o instanceof Robot_BestCase robot)) {
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