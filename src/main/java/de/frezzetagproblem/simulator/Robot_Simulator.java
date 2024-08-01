package de.frezzetagproblem.simulator;

import de.frezzetagproblem.Location;
import de.frezzetagproblem.Properties;
import de.frezzetagproblem.Status;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Robot
 * @Author Joseph Alzieb
 */
public class Robot_Simulator {

  private String id;
  private Location location;
  private Status status;
  private boolean declared;
  private double[] velocity;
  private Robot_Simulator targetRobotSimulator;

  public Robot_Simulator(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  public Robot_Simulator() {
  }

  /**
   * Hier unterscheiden wir 3 Fälle:
   * 1: Robot (this) ist ON, und hat noch kein Robot gefunden, den er aufweckt.
   * -> Es wird nach Target-Robot gesucht (der nächste Nachbar), und ihn markiert,
   * -> damit er nicht von einem anderen Roboter markiert wird.
   *
   * 2: Robot (this) hat seinen Target-Robot erreicht, und weckt ihn auf.
   * 3: Robot (this) muss sich zu seinem Target-Robot bewegen.
   * @param off List of Robots with status "OFF".
   * @param time List of Timeunits.
   */
  public void run(List<Robot_Simulator> off, List<Double> time) {
    if (this.targetRobotSimulator == null) {
      this.targetRobotSimulator = this.getNearestNeighbor(off);
      if (this.targetRobotSimulator != null && !this.targetRobotSimulator.declared) {
        System.out.println("Robot " + this.id + " targets " + this.targetRobotSimulator.id);
        this.targetRobotSimulator.declared = true;
      }
    } else {
      if (Arrays.equals(this.location.toArray(), this.targetRobotSimulator.location.toArray())) {
        System.out.println("Robot " + this.id + " wakes robot " + this.targetRobotSimulator.id);
        this.targetRobotSimulator.aktive();
        this.targetRobotSimulator = null;
      } else {
        this.setVelocity();
        System.out.println("Robot " + this.id + " moves from " + Arrays.toString(this.location.toArray()) + " to " +
            Arrays.toString(new Location((int) Math.round(this.location.x + this.velocity[0]), (int) Math.round(this.location.y + this.velocity[1])).toArray()) +
            " toward robot " + this.targetRobotSimulator.id + " at " + Arrays.toString(this.targetRobotSimulator.location.toArray()));
        this.location.x += Math.round(this.velocity[0]);
        this.location.y += Math.round(this.velocity[1]);
      }
    }
    time.add(1.0);  }

  /**
   * Hier wird einfach über alle OFF-Roboter iteriert, und den nährten Roboter für (this) gefunden.
   * @param off List of Roboter with Status "OFF"
   * @return
   */
  public Robot_Simulator getNearestNeighbor(List<Robot_Simulator> off) {
    Robot_Simulator nearest = null;
    for (Robot_Simulator r : off) {
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
  public double distance(Robot_Simulator that) {
    return Math.sqrt(Math.pow(this.location.x - that.location.x, 2) + Math.pow(this.location.y - that.location.y, 2));
  }

  public void setVelocity() {
    double rad = this.getAngleToTarget();
    this.velocity = new double[]{Math.cos(rad), Math.sin(rad)};
    this.velocity[0] *= 1;
    this.velocity[1] *= 1;
    System.out.println("Setting robot " + this.id + " velocity to " + Arrays.toString(this.velocity));
  }

  public double getAngleToTarget() {
    double xdiff = this.targetRobotSimulator.getLocation_x() - this.location.x;
    double ydiff = this.targetRobotSimulator.getLocation_y() - this.location.y;
    return Math.atan2(ydiff, xdiff);
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
    return this.targetRobotSimulator != null;
  }

  public void removeTargetRobot(){
    this.targetRobotSimulator = null;
  }

  public int getLocation_x(){
    return this.location.x;
  }

  public int getLocation_y(){
    return this.location.y;
  }

  public void move(Location location){
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
        ", targetRobot=" + targetRobotSimulator +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Robot_Simulator robotSimulator)) {
      return false;
    }
    return Objects.equals(id, robotSimulator.id) && Objects.equals(location,
        robotSimulator.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, location);
  }
}
