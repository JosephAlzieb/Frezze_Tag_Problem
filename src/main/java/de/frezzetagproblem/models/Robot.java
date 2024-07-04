package de.frezzetagproblem.models;

/**
 * @Author Joseph Alzieb
 */
public class Robot {

  public String id;
  public Location location;
  public Status status;

  public Robot(String id, Location location) {
    this.id = id;
    this.location = location;
    this.status = Status.OFF;
  }
}