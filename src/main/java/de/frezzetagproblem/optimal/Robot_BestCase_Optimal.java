package de.frezzetagproblem.optimal;

import de.frezzetagproblem.Location;
import de.frezzetagproblem.Pair;
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
public class Robot_BestCase_Optimal {

  private String id;
  private Location location;
  private Status status;
  private boolean declared;
  private int velocity;
  private Robot_BestCase_Optimal targetRobot;

  public Robot_BestCase_Optimal(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  public Robot_BestCase_Optimal() {
  }

  public Robot_BestCase_Optimal getNextRobot(List<Robot_BestCase_Optimal> off) {
    Robot_BestCase_Optimal nearest = null;
    for (Robot_BestCase_Optimal r : off) {
      if (!r.isAktive() && nearest == null) {
        nearest = r;
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
  public double distance(Robot_BestCase_Optimal that) {
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

  public void aktive(Robot_BestCase_Optimal targetRobot) {
    targetRobot.status = Status.ON;
    System.out.println(this.id + " aktiviert " + targetRobot.id);
  }

  public void deaktive() {
    this.status = Status.OFF;
  }

  public Location getLocation() {
    return location;
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
    if (!(o instanceof Robot_BestCase_Optimal robot)) {
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