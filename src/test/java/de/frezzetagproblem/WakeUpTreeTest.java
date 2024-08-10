package de.frezzetagproblem;

import static org.junit.Assert.assertEquals;

import de.frezzetagproblem.models.WakeUpTree;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class WakeUpTreeTest {

  @Test
  public void test_01() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);

    assertEquals(4, wakeUpTree.getMakespan(), 0.1);
  }

  @Test
  public void test_02() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);
    wakeUpTree.addChild("0", "4", 3);


    assertEquals(6, wakeUpTree.getMakespan(), 0.1);
  }

  @Test
  public void test_03() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);
    wakeUpTree.addChild("2", "4", 3);

    assertEquals(6, wakeUpTree.getms(), 0.1);
  }

  @Test
  public void test_04() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);
    wakeUpTree.addChild("2", "4", 3);
    wakeUpTree.addChild("0", "5", 4);
    wakeUpTree.getms();
    assertEquals(7, wakeUpTree.getms(), 0.1);
  }

  @Test
  public void test_05() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);
    wakeUpTree.addChild("2", "4", 3);
    wakeUpTree.addChild("0", "5", 4);
    wakeUpTree.addChild("1", "6", 6);
    wakeUpTree.addChild("3", "7", 8);

    assertEquals(12, wakeUpTree.getms(), 0.1);
  }

  @Test
  public void test_06() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);
    wakeUpTree.addChild("2", "4", 3);
    wakeUpTree.addChild("0", "5", 4);
    wakeUpTree.addChild("1", "6", 6);
    wakeUpTree.addChild("3", "7", 8);
    wakeUpTree.addChild("7", "8", 9);
    wakeUpTree.addChild("3", "9", 10);

    assertEquals(22, wakeUpTree.getms(), 0.1);
  }

  @Test
  public void test_07() {
    WakeUpTree wakeUpTree = new WakeUpTree();
    wakeUpTree.addChild("0", "1", 1);
    wakeUpTree.addChild("0", "2", 2);
    wakeUpTree.addChild("1", "3", 3);
    wakeUpTree.addChild("2", "4", 3);
    wakeUpTree.addChild("0", "5", 4);
    wakeUpTree.addChild("1", "6", 6);
    wakeUpTree.addChild("3", "7", 8);
    wakeUpTree.addChild("7", "8", 9);
    wakeUpTree.addChild("3", "9", 10);
    wakeUpTree.addChild("5", "10", 16);
    wakeUpTree.addChild("7", "12", 4);
    wakeUpTree.addChild("8", "11", 2);

    assertEquals(25, wakeUpTree.getMakespan(), 0.1);
  }

}
