package de.frezzetagproblem.optimal;

import de.frezzetagproblem.Location;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.Status;
import java.util.List;
import java.util.Objects;

/**
 * Robot
 * @Author Joseph Alzieb
 */
public class Robot_Thread implements Runnable {

  private String id;
  private Location location;
  private Status status;
  private boolean declared;
  private int velocity;
  private Robot_Thread targetRobot;

  public Robot_Thread(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  public Robot_Thread() {
  }

  /**
   * Hier wird das Algorithms ausgeführt, das in der Klasse {@link Properties} definiert ist.
   *
   * @param off
   * @return
   */
  public void run(List<Robot_Thread> off) {
    this.targetRobot = this.getNearestRobot(off);
    if (this.targetRobot != null) {
      this.targetRobot.declare();
      this.targetRobot.aktive();

      //Der Roboter bewegt sich zu dem zu aktivierenden Robot.
      this.moveTo(this.targetRobot.location);
      this.removeTargetRobot();
    }
  }

  /**
   * Hier wird einfach über alle OFF-Roboter iteriert, und den nährten Roboter für (this) gefunden.
   * @param off List of Roboter with Status "OFF"
   * @return
   */
  public Robot_Thread getNearestRobot(List<Robot_Thread> off) {
    Robot_Thread nearest = null;
    for (Robot_Thread r : off) {
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
  public double distance(Robot_Thread that) {
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
    if (!(o instanceof Robot_Thread robot)) {
      return false;
    }
    return Objects.equals(id, robot.id) && Objects.equals(location,
        robot.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, location);
  }

  @Override
  public void run() {

  }
}