package de.frezzetagproblem.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * In dieser Klasse wird der Aktivierungsbaum gebaut, um am Ende den MAkespan bestimmen zu können.
 *
 * @Author Joseph Alzieb
 */
public class WakeUpTree {

  private final List<Child> children = new ArrayList<>();
  private transient Child root;
  private transient String firstRootTarget;

  /**
   * Diese Methode fügt der Wake-Up-Tree ein Pfad hinzu. Für jedes neues Child wird der Parent
   * gefunden, und due Distanz vom Root bis zu diesem Child berechnet.
   *
   * @param startRobotId  Start Robot
   * @param targetRobotId Target Robot
   * @param distance      Distanz
   */
  public synchronized void addChild(String startRobotId, String targetRobotId, double distance) {
    Child parent;
    if (root != null && startRobotId.equals(root.startRobotId)) {
      List<Child> list = children.
          stream().
          filter(c -> c.startRobotId.equals(root.startRobotId)).
          sorted(Comparator.comparing(o -> o.targetRobotId)).
          toList();
      parent = list.get(list.size() - 1);
    } else if (startRobotId.equals(firstRootTarget)) {
      List<Child> list = children.
          stream().
          filter(c -> c.startRobotId.equals(firstRootTarget)).
          sorted(Comparator.comparing(o -> o.targetRobotId)).
          toList();
      parent = list.isEmpty() ? findParentForChildWithId(startRobotId) : list.get(list.size() - 1);
    } else {
      parent = findParentForChildWithId(startRobotId);
    }

    Child e = new Child(startRobotId, targetRobotId, distance, parent);
    e.setTotalDistance(totalDistance(e));

    if (children.isEmpty()) {
      root = e;
      firstRootTarget = targetRobotId;
    }

    children.add(e);
  }

  /**
   * Die Methode berechnet die totale Distanz vom Root bis zum aktuellen Child.
   *
   * @param current current Child
   * @return Totale Distanz für ein Child vom Root
   */
  private double totalDistance(Child current) {
    if (root == null) {
      return 0.0;
    }
    if (current.startRobotId.equals(root.startRobotId)) {
      Double reduce = children.stream()
          .filter(c -> !c.equals(current) && c.startRobotId.equals(current.startRobotId))
          .map(c -> c.distance)
          .reduce(0.0, Double::sum);
      return reduce + current.distance;

    } else if (current.startRobotId.equals(firstRootTarget)) {
      Double reduce = children.stream()
          .filter(c ->
              !c.equals(current) &&
                  (
                      c.targetRobotId.equals(current.startRobotId) ||
                          c.startRobotId.equals(current.startRobotId)
                  )
          )
          .map(c -> c.distance)
          .reduce(0.0, Double::sum);
      return reduce + current.distance;

    } else {
      return current.parent.totalDistanceFromRoot + current.distance;
    }
  }

  /**
   * @param id vom Robot (StartRobotId eines Childs)
   * @return den Parent Child.
   */
  private Child findParentForChildWithId(String id) {
    Optional<Child> first = children.stream().filter(c -> c.targetRobotId.equals(id)).findFirst();
    Optional<Child> second = children.stream().filter(c -> c.startRobotId.equals(id)).findFirst();
    if (second.isPresent() && second.get().parent.equals(first.get())) {
      // 0->1 , 1->3 , 3->7, 3->9
      // Parent of 3->9 should be 3->7, and not 1->3
      return second.get();
    }
    return first.orElse(null);
  }

  /**
   * Makespan: ist die Länge des längsten Pfades im Aufwachbaum. d.h. Es ist die maximale Zeit, die
   * benötigt wird, um alle Roboter im System zu aktivieren. Die Kosten für jeden Pfad werden in DER
   * Variable "totalDistanceFromRoot" gespeichert.
   *
   * @return den makespan
   */
  public double getMakespan() {
    return children.stream().map(c -> c.totalDistanceFromRoot).max(Double::compare).get();
  }

  static final class Child {

    private final String startRobotId;
    private final String targetRobotId;
    private final double distance;
    private double totalDistanceFromRoot;
    private final transient Child parent;

    Child(String startRobotId, String targetRobotId, double distance, Child parent) {
      this.startRobotId = startRobotId;
      this.targetRobotId = targetRobotId;
      this.distance = distance;
      this.parent = parent;
    }

    public void setTotalDistance(double totalDistanceFromRoot) {
      this.totalDistanceFromRoot = totalDistanceFromRoot;
    }

    @Override
    public String toString() {
      return String.format("%s wakes %s in (%s)", startRobotId, targetRobotId, distance);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Child child)) {
        return false;
      }
      return Objects.equals(startRobotId, child.startRobotId) && Objects.equals(
          targetRobotId, child.targetRobotId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(startRobotId, targetRobotId);
    }
  }
}
