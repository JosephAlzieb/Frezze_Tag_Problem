package de.frezzetagproblem;

import static org.junit.Assert.assertEquals;

import de.frezzetagproblem.applications.DummyDataGenerator;
import de.frezzetagproblem.applications.FreezeTag_AllPossibleSolutions;
import de.frezzetagproblem.models.Helper;
import de.frezzetagproblem.models.Location;
import de.frezzetagproblem.models.Result;
import de.frezzetagproblem.models.Robot;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Tests sind für L2 geschrieben.
 */
public class FrezzeTag_AllPossibleSolutionsTest {

  @Test
  public void test_2_inaktive_robots() {
    Result result = new Result("Test", 2, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(2, 3), false);
    Robot r2 = new Robot("2", new Location(-4, 4), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    // Optimale Lösung finden
    assertEquals(result.getTotalTimeUnit(), 9, 0);
  }

  @Test
  public void test_3_inaktive_robots() {
    Result result = new Result("Test", 3, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(2, 3), false);
    Robot r2 = new Robot("2", new Location(-4, 4), false);
    Robot r3 = new Robot("3", new Location(-3, -2), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);
    off.add(r3);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    assertEquals(result.getTotalTimeUnit(), 10, 0);
  }

  @Test
  public void test_4_inaktive_robots() {
    Result result = new Result("Test", 4, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(2, 3), false);
    Robot r2 = new Robot("2", new Location(-4, 4), false);
    Robot r3 = new Robot("3", new Location(-3, -2), false);
    Robot r4 = new Robot("4", new Location(0, 7), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);
    off.add(r3);
    off.add(r4);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    assertEquals(result.getTotalTimeUnit(), 13, 0);
  }

  @Test
  public void test_4_inaktive_robots_on_edge() {
    Result result = new Result("Test", 4, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(86, -39), false);
    Robot r2 = new Robot("2", new Location(-83, -24), false);
    Robot r3 = new Robot("3", new Location(-86, 33), false);
    Robot r4 = new Robot("4", new Location(81, 14), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);
    off.add(r3);
    off.add(r4);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    assertEquals(result.getTotalTimeUnit(), 307, 0);
  }

  @Test
  public void test_5_inaktive_robots() {
    Result result = new Result("Test", 5, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(2, 3), false);
    Robot r2 = new Robot("2", new Location(-4, 4), false);
    Robot r3 = new Robot("3", new Location(-3, -2), false);
    Robot r4 = new Robot("4", new Location(0, 7), false);
    Robot r5 = new Robot("5", new Location(4, 5), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);
    off.add(r3);
    off.add(r4);
    off.add(r5);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    assertEquals(result.getTotalTimeUnit(), 13, 0);
  }

  @Test
  public void test_6_inaktive_robots() {
    Result result = new Result("Test", 6, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(2, 3), false);
    Robot r2 = new Robot("2", new Location(-4, 4), false);
    Robot r3 = new Robot("3", new Location(-3, -2), false);
    Robot r4 = new Robot("4", new Location(0, 7), false);
    Robot r5 = new Robot("5", new Location(4, 5), false);
    Robot r6 = new Robot("6", new Location(3, -1), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);
    off.add(r3);
    off.add(r4);
    off.add(r5);
    off.add(r6);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    // Optimale Lösung finden
    assertEquals(result.getTotalTimeUnit(), 13, 0);
  }

  @Test
  public void test_5_inaktive_robots_a() {
    Result result = new Result("Test", 6, 0);
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    Robot r1 = new Robot("1", new Location(91, 24), false);
    Robot r2 = new Robot("2", new Location(-15,87), false);
    Robot r3 = new Robot("3", new Location(11, -93), false);
    Robot r4 = new Robot("4", new Location(31,-95), false);
    Robot r5 = new Robot("5", new Location(-83,-18), false);
    ArrayList<Robot> off = new ArrayList<>();
    off.add(r1);
    off.add(r2);
    off.add(r3);
    off.add(r4);
    off.add(r5);

    FreezeTag_AllPossibleSolutions.execute(on, off, result);

    // Optimale Lösung finden
    assertEquals(result.getTotalTimeUnit(), 13, 0);
  }

  /**
   * Versuche, den Beweis von L2 zu widersprechen.
   */
  @Test
  public void test_11_inaktive_robots() {
    Robot r0 = new Robot("0", new Location(0, 0), true);
    ArrayList<Robot> on = new ArrayList<>();
    on.add(r0);

    double expected = 383.8;
    double makespan = 0;
    List<Robot> robots = null;
    Result result = null;
    while (makespan < expected){
      result = new Result("Test", 11, 0);
      List<Robot> off = new ArrayList<>();
      for (int i = 1; i < 11; i++) {
        Location randomLocation = null;
        randomLocation = DummyDataGenerator.getRandomLocationOnEdge();
        off.add(new Robot(String.valueOf(i), randomLocation, false));
      }
      robots = Helper.copyRobots(off);
      FreezeTag_AllPossibleSolutions.execute(on, off, result);
      makespan = result.getTotalTimeUnit();

      System.out.println("-------------------------------------");
      System.out.println(robots +" -- "+ makespan);
    }
  }

}
