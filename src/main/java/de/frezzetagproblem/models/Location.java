package de.frezzetagproblem.models;

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
}