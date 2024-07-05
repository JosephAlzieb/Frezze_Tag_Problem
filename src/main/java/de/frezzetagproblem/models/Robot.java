package de.frezzetagproblem.models;

import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * @Author Joseph Alzieb
 */
public class Robot {

  public String id;
  public Location location;
  public Status status;
  public boolean declared;
  public int velocity;
  public Robot targetRobot;

  public Robot(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  public Robot() {
  }

  /**
   * Hier wird das Algorithms ausgeführt..
   * Wir unterscheiden 3 Fällen:
   * 1: Robot (this) ist ON, und hat noch kein Robot gefunden, den er aufweckt.
   * -> Es wird nach Target-Robot gesucht (der nächste Nachbar), und ihn markiert,
   * -> damit er nicht von einem anderen Roboter markiert wird.
   *
   * 2: Robot (this) hat seinen Target-Robot erreicht, und weckt ihn auf.
   * 3: Robot (this) muss sich zu seinem Target-Robot bewegen.
   * @param off List of Robots with status "OFF"
   */
  public void run(List<Robot> off) {
    //TODO immer beginnen wir hier mit dem ersten Roboter. hier müssen wir auch alle ON-Roboter übergeben. Bsp. Es gibt noch einen Robot zu aktivieren, und der erste Robot "0" wäre sehr weit weg von dem. aber der "3" sehr nah.
    if (this.targetRobot == null) {
      this.targetRobot = this.getNearestNeighbor(off);
      if (this.targetRobot != null && !this.targetRobot.declared) {
        //Robot markiert sein Goal
        System.out.println(this.id + " markiert " + this.targetRobot.id);
        this.targetRobot.declared = true;

        //Hier könnte sich der Robot direkt zu seinem Target bewegen, und ihn aktivieren. Dann schaffen wir das in 1-Zeiteinheit
      }
    } else if (Arrays.equals(this.location.toArray(), this.targetRobot.location.toArray())){
        //Robot hat sein Goal erreicht, und er weckt ihn auf. Status vom Target ist ON
        this.targetRobot.status = Status.ON;
        //Target vom Roboter ist wieder null, um neue Roboter aufwecken zu können
      System.out.println(this.id + " aktiviert " + this.targetRobot.id);
      this.targetRobot = null;
    } else {
      //Robot ist bei seinem Target gelandet.
      this.location.x = this.targetRobot.location.x;
      this.location.y = this.targetRobot.location.y;
      System.out.println(this.id + " geht zu " + this.targetRobot.id);

    }
  }

  /**
   * Hier wird einfach über alle OFF-Roboter iteriert, und den nährten Roboter für (this) gefunden.
   * @param off List of Roboter with Status "OFF"
   * @return
   */
  public Robot getNearestNeighbor(List<Robot> off) {
    Robot nearest = null;
    for (Robot r : off) {
      if (!r.declared && nearest == null) {
        nearest = r;
      } else if (!r.declared && nearest != null) {
        if (this.distance(nearest) > this.distance(r)) {
          nearest = r;
        }
      }
    }
    return nearest;
  }

  /**
   * Die Methode berechnet den Abstand zwischen zwei Punkten. (Euklidische Distanz)
   * @param that other Robot
   * @return
   */
  public double distance(Robot that) {
    return Math.sqrt(Math.pow(this.location.x - that.location.x, 2) + Math.pow(this.location.y - that.location.y, 2));
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