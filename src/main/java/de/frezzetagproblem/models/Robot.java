package de.frezzetagproblem.models;

import java.util.List;
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

  public Robot(String id, Location location) {
    this.id = id;
    this.location = location;
    this.status = Status.OFF;
  }

  public Robot() {
  }

  /**
   * Hier wird das Algorithms ausgeführt..
   * Wir unterscheiden 3 Fällen:
   * 1: Robot (this) ist ON, und hat noch kein Robot gefunden, den er aufweckt. -> Es wird nach Target-Robot gesucht (der nächste Nachbar),
   * und ihn markiert, damit er nicht von einem anderen Roboter markiert wird.
   * @param off List of Robots with status "OFF"
   */
  public void run(List<Robot> off) {
    if (this.targetRobot == null) {
      this.targetRobot = this.getNearestNeighbor(off);
      if (this.targetRobot != null && !this.targetRobot.declared) {
        //Robot markiert sein Goal
        this.targetRobot.declared = true;
      }
    }
  }

  private Robot getNearestNeighbor(List<Robot> off) {
    try {
      throw new NotImplementedException("Not implemented"); //
    } catch (NotImplementedException e) {
      throw new RuntimeException(e);
    }
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
}