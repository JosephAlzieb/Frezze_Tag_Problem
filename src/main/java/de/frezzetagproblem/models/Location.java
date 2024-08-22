package de.frezzetagproblem.models;

import de.frezzetagproblem.Properties;

/**
 * @Author Joseph Alzieb
 */
public class Location {

  public int x;
  public int y;

  public Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int[] toArray() {
    return new int[]{this.x, this.y};
  }

  public double distance(Location that) {
    if (Properties.METRIK.equals(Properties.L_1)) {
      return Math.abs(this.x - that.x) + Math.abs(this.y - that.y);
    } else {
      return Math.sqrt(Math.pow(this.x - that.x, 2) + Math.pow(this.y - that.y, 2));
    }
  }

  @Override
  public String toString() {
    return "Location{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
}