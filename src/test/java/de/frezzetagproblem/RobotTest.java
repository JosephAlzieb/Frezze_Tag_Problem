package de.frezzetagproblem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.frezzetagproblem.models.Location;
import de.frezzetagproblem.models.Robot;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class RobotTest {

  @Test
  public void testGetNearestNeighbor_AllDeclared() {
    Robot robot = new Robot("0", new Location(0, 0), false);
    List<Robot> robots = List.of(
        new Robot("1", new Location(1, 1), true),
        new Robot("2", new Location(2, 2), true),
        new Robot("3", new Location(3, 3), true)
    );

    Robot result = robot.getNearestRobot(robots);
    assertNull(result); //Expected null when all robots are declared
  }

  @Test
  public void testGetNearestNeighbor_NoDeclared() {
    Robot robot = new Robot("0", new Location(0, 0), false);
    List<Robot> robots = List.of(
        new Robot("1", new Location(1, 1), false),
        new Robot("2", new Location(2, 2), false),
        new Robot("3", new Location(3, 3), false)
    );

    Robot result = robot.getNearestRobot(robots);
    assertEquals(robots.get(0), result); //Expected the nearest robot to be the first one when none are declared
  }

  @Test
  public void testGetNearestNeighbor_MixedDeclared() {
    Robot robot = new Robot("0", new Location(0, 0), false);
    List<Robot> robots = List.of(
        new Robot("1", new Location(1, 1), true),
        new Robot("2", new Location(2, 2), false),
        new Robot("3", new Location(3, 3), true),
        new Robot("4", new Location(1, 1), false)
        );

    Robot result = robot.getNearestRobot(robots);
    assertEquals(robots.get(3), result); //Expected the nearest undeclared robot with id = 4
  }

  @Test
  public void testGetNearestNeighbor_EmptyList() {
    Robot robot = new Robot("0", new Location(0, 0), false);
    List<Robot> robots = new ArrayList<>();

    Robot result = robot.getNearestRobot(robots);
    assertNull(result); //Expected null when the list is empty
  }

  @Test
  public void testDistance_DifferentLocations() {
    Robot robot1 = new Robot("0", new Location(0, 0), false);
    Robot robot2 = new Robot("0", new Location(3, 4), false);
    double distance = robot1.distance(robot2);
    assertEquals(5.0, distance, 0.01); //Distance between (0,0) and (3,4) should be 5
  }

}
