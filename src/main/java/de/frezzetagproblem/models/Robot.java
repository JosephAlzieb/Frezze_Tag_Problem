package de.frezzetagproblem.models;

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