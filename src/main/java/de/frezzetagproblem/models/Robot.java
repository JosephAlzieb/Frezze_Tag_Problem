package de.frezzetagproblem.models;

import de.frezzetagproblem.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Robot
 *
 * @Author Joseph Alzieb
 */
public class Robot implements Cloneable {

  private final String id;
  private Location location;
  private Status status;
  private boolean declared;
  private double[] velocity;
  private Robot targetRobot;

  public Robot(String id, Location location, boolean declared) {
    this.id = id;
    this.location = location;
    this.declared = declared;
    this.status = Status.OFF;
  }

  /**
   * Hier wird das Algorithms ausgeführt, das in der Klasse {@link Properties} definiert ist.
   *
   * @param off inakive Robots
   */
  public void run(
      List<Robot> off,
      WakeUpTree wake_up_tree) {
    if (Properties.ALGORITHM.equals(Properties.Greedy_WITH_TIMEUNITS_1)) {
      runGreedyAlgo_1(off, wake_up_tree);
    } else if (Properties.ALGORITHM.equals(Properties.Greedy_WITH_DISTANCE)) {
      runGreedyAlgo_Dis(off, wake_up_tree);
    }
  }

  /**
   * Bessere Lösung - Algorithms Hier wird immer gecheckt, ob es eine bessere Lösung gibt..
   *
   * @param off               inaktive robots
   * @param possibleSolutions alle möglichen Lösungen
   */
  public void run(
      List<Robot> off,
      TreeMap<Pair<String, String>, Double> possibleSolutions,
      WakeUpTree wake_up_tree) {
    this.targetRobot = this.getNearestRobot(off);
    if (this.targetRobot != null) {
      double distance = this.distance(this.targetRobot);

      //Wenn eine besser Lösung gibt, um das Target zu aktivieren, wird nach ihr gesucht.
      if (existsBetterSolution(possibleSolutions, distance).get()) {
        this.removeTargetRobot();
        return;
      }
      this.targetRobot.declare();
      targetRobot.aktive();
      wake_up_tree.addChild(id, targetRobot.id, distance);

      //Der Roboter bewegt sich zu dem zu aktivierenden Robot.
      this.moveTo(this.targetRobot.location);
      this.removeTargetRobot();
    }
  }

  private String buildChild(double distance) {
    return String.format("%s wakes %s in (%s)", this.id, targetRobot.id, (int) distance);
  }

  private AtomicBoolean existsBetterSolution(
      TreeMap<Pair<String, String>, Double> possibleSolutions,
      Double distance) {
    AtomicBoolean b = new AtomicBoolean(false);
    possibleSolutions.forEach((x, y) -> {
      if (this.targetRobot.id.equals(x.getSecond()) && y < distance) {
        b.set(true);
      }
    });

    return b;
  }

  public void computeDistance(
      List<Robot> off,
      TreeMap<Pair<String, String>, Double> possibleSolutions) {
    var targetRobot = this.getNearestRobot(off);
    if (targetRobot != null) {
      possibleSolutions.put(getKey(targetRobot), this.distance(targetRobot));
    }
  }

  private Pair<String, String> getKey(Robot targetRobot) {
    return new Pair<>(this.id, targetRobot.id);
  }

  /**
   * Hier sucht Robot (this) das näherte Nachbar, und aktiviert ihn. Die Zeit, die zu aktivierung
   * benötigt wird, wäre den Abstand zwischen den Robotern.
   *
   * @param off          List of Robots with status "OFF".
   * @param wake_up_tree Wake-up-tree
   */
  private void runGreedyAlgo_Dis(
      List<Robot> off,
      WakeUpTree wake_up_tree) {
    this.targetRobot = this.getNearestRobot(off);
    if (this.targetRobot != null) {
      this.targetRobot.declare();
      this.targetRobot.aktive();
      double distance = this.distance(this.targetRobot);
      wake_up_tree.addChild(id, targetRobot.id, distance);

      //Der Roboter bewegt sich zu dem zu aktivierenden Robot.
      this.moveTo(this.targetRobot.location);
      this.removeTargetRobot();
    }
  }

  public void run(
      List<Robot> off,
      List<Robot> on,
      WakeUpTree wake_up_tree) {
    /*
    Hier werden die aktiven Roboter bei der Auswahl den nächsten inaktiven Robot berücksichtigt.
     */
    this.targetRobot = this.getNearestRobot(on, off);

    if (this.targetRobot != null) {
      this.targetRobot.declare();
      this.targetRobot.aktive();
      double distance = this.distance(this.targetRobot);
      wake_up_tree.addChild(id, targetRobot.id, distance);

      //Der Roboter bewegt sich zu dem zu aktivierenden Robot.
      this.moveTo(this.targetRobot.location);
      this.removeTargetRobot();
    }
  }

  /**
   * Hier passiert folgendes: 1: Robot (this) ist ON, und hat noch kein Robot gefunden, den er
   * aufweckt. -> Es wird nach Target-Robot gesucht (der nächste Nachbar)
   * <p>
   * 2: Robot (this) fährt zu seinem Target-Robot, und weckt ihn auf.
   *
   * @param off          List of Robots with status "OFF".
   * @param wake_up_tree Wake-up-tree
   */
  private void runGreedyAlgo_1(
      List<Robot> off,
      WakeUpTree wake_up_tree) {
    // wenn wir hier alles in einem Schritt machen, dann ist das ähnlich wie der Theorem 1 von (Freeze-Tag in L1 has Wake-up Time Five)
    double unit = 1;
    if (!hasTargetRobot()) {
      this.targetRobot = this.getNearestRobot(off);
      if (this.targetRobot != null && !this.targetRobot.isDeclared()) {
        this.targetRobot.declare();
        this.targetRobot.aktive();
        wake_up_tree.addChild(id, targetRobot.id, unit);
        this.moveTo(this.targetRobot.location);
        this.removeTargetRobot();
      }
    }
  }

  /**
   * Gedacht für Simulator. Hier unterscheiden wir 3 Fälle: 1: Robot (this) ist ON, und hat noch
   * kein Robot gefunden, den er aufweckt. -> Es wird nach Target-Robot gesucht (der nächste
   * Nachbar), und ihn markiert, -> damit er nicht von einem anderen Roboter markiert wird.
   * <p>
   * 2: Robot (this) hat seinen Target-Robot erreicht, und weckt ihn auf. 3: Robot (this) muss sich
   * zu seinem Target-Robot bewegen.
   *
   * @param off List of Robots with status "OFF".
   */
  public void run(List<Robot> off) {
    if (this.targetRobot == null) {
      this.targetRobot = this.getNearestRobot(off);
      if (this.targetRobot != null && !this.targetRobot.declared) {
        System.out.println("Robot " + this.id + " declare " + this.targetRobot.id);
        this.targetRobot.declared = true;
      }
    } else {
      if (Arrays.equals(this.location.toArray(), this.targetRobot.location.toArray())) {
        System.out.println("Robot " + this.id + " wakes robot " + this.targetRobot.id);
        this.targetRobot.aktive();
        removeTargetRobot();
      } else {
        this.setVelocity();
        System.out.println(
            "Robot " + this.id + " moves from " + Arrays.toString(this.location.toArray()) + " to "
                +
                Arrays.toString(new Location((int) Math.round(this.location.x + this.velocity[0]),
                    (int) Math.round(this.location.y + this.velocity[1])).toArray()) +
                " toward robot " + this.targetRobot.id + " at " + Arrays.toString(
                this.targetRobot.location.toArray()));
        this.location.x += (int) Math.round(this.velocity[0]);
        this.location.y += (int) Math.round(this.velocity[1]);
      }
    }
  }

  public void setVelocity() {
    double rad = this.getAngleToTarget();
    this.velocity = new double[]{Math.cos(rad), Math.sin(rad)};
    this.velocity[0] *= 1;
    this.velocity[1] *= 1;
    System.out.println(
        "Setting robot " + this.id + " velocity to " + Arrays.toString(this.velocity));
  }

  public double getAngleToTarget() {
    double xdiff = this.targetRobot.getLocation_x() - this.location.x;
    double ydiff = this.targetRobot.getLocation_y() - this.location.y;
    return Math.atan2(ydiff, xdiff);
  }

  /**
   * Hier wird einfach über alle OFF-Roboter iteriert, und den nährten Roboter für (this) gefunden.
   *
   * @param off List of Roboter with Status "OFF"
   * @return nearste-robot
   */
  public Robot getNearestRobot(List<Robot> off) {
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
   * @param off List of Roboter with Status "OFF"
   * @return Gibt den nächsten inaktiven Robot in der Liste "off" zurück.
   */
  public Robot getNextRobot(List<Robot> off) {
    Robot nearest = null;
    for (Robot r : off) {
      if (!r.isAktive() && nearest == null) {
        nearest = r;
      }
    }
    return nearest;
  }

  /**
   * @param on  Liste der aktiven Roboter
   * @param off Liste der inaktiven Roboter
   * @return den näherten Robot mit Berücksichtigung der aktiven Roboter.
   */
  public Robot getNearestRobot(List<Robot> on, List<Robot> off) {
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

    // Besonders gut für kleine n. bis n = 10
    if (nearest != null) {
      for (Robot r : on) {
        if (this != r && this.distance(nearest) > r.distance(nearest)) {
          return null;
        }
      }
    }
    return nearest;
  }

  /**
   * Die Methode berechnet den Abstand zwischen zwei Punkten. Je nachdem welche Metrik in
   * {@link Properties} (L1, L2, oder LP) gesetzt ist, wird diese Metrik zur Berechnung der Distanz
   * verwendet.
   *
   * @param that other Robot
   * @return Distance from Robot (this) to Robot (that)
   */
  public double distance(Robot that) {
    if (Properties.METRIK.equals(Properties.L_1)) {
      return Math.abs(this.location.x - that.location.x) +
          Math.abs(this.location.y - that.location.y);
    } else if (Properties.METRIK.equals(Properties.L_2)){
      return Math.sqrt(
          Math.pow(this.location.x - that.location.x, 2) +
          Math.pow(this.location.y - that.location.y, 2));
    } else {
      return Math.max(
          Math.abs(this.location.x - that.location.x),
          Math.abs(this.location.y - that.location.y));
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

  public boolean hasTargetRobot() {
    return this.targetRobot != null;
  }

  public void removeTargetRobot() {
    this.targetRobot = null;
  }

  public int getLocation_x() {
    return this.location.x;
  }

  public int getLocation_y() {
    return this.location.y;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public void moveTo(Location location) {
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

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}