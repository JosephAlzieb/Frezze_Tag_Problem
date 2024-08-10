package de.frezzetagproblem.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In dieser Klasse wird der Aktivierungsbaum gebaut, um am Ende den MAkespan bestimmen zu können.
 * @Author Joseph Alzieb
 */
public class WakeUpTree {

  //TODO alle parent speichern .. wo startId oder targetId vorkommt. -> makespan max summe
  List<Child> children = new ArrayList<>();
  double firstMove = 0.0;
  Child root;
  String root_2;
  Child nextToRoot;
  List<Child> rootPath = new ArrayList<>();

  public void addChild(String startRobotId, String targetRobotId, double distance) {
    Child parent = null;
    if (root != null && startRobotId.equals(root.startRobotId)){
      List<Child> list = children.
          stream().
          filter(c -> c.startRobotId.equals(root.startRobotId)).
          sorted((o1, o2) -> o1.targetRobotId.compareTo(o2.targetRobotId)).
          toList();
      parent = list.get(list.size() - 1);
    } else if (startRobotId.equals(root_2)){
      List<Child> list = children.
          stream().
          filter(c -> c.startRobotId.equals(root_2)).
          sorted((o1, o2) -> o1.targetRobotId.compareTo(o2.targetRobotId)).
          toList();
      parent = list.isEmpty() ? findParentFor(startRobotId):list.get(list.size() - 1);
    } else {
      parent = findParentFor(startRobotId);
    }

    Child e = new Child(startRobotId, targetRobotId, distance, parent);
    e.setTotalDistance(totalDistance(e));

    if (children.isEmpty()){
      firstMove = distance;
      root = e;
      root_2 = targetRobotId;
    }

    if (startRobotId.equals(root.startRobotId)) rootPath.add(e);
    children.add(e);
  }

  private double totalDistance(Child e) {
    if (root == null) return 0.0;
    if (e.startRobotId.equals(root.startRobotId)){
      Double reduce = children.stream()
          .filter(c -> !c.equals(e) && c.startRobotId.equals(e.startRobotId))
          .map(c -> c.distance)
          .reduce(0.0, Double::sum);
      return reduce + e.distance;

    } else if (e.startRobotId.equals(root_2)){
      Double reduce = children.stream()
          .filter(c ->
              !c.equals(e) &&
                  (
                      c.targetRobotId.equals(e.startRobotId) ||
                      c.startRobotId.equals(e.startRobotId)
                  )
          )
          .map(c -> c.distance)
          .reduce(0.0, Double::sum);
      return reduce + e.distance;

    } else {
      return e.parent().totalDistance + e.distance;
    }
  }

  private Child findParentFor(String startRobotId) {
    Optional<Child> first = children.stream().filter(c -> c.targetRobotId.equals(startRobotId)).findFirst();
    Optional<Child> second = children.stream().filter(c -> c.startRobotId.equals(startRobotId)).findFirst();
    if (second.isPresent() && second.get().parent.equals(first.get())){
      return second.get();
    }
    return first.orElse(null);
  }

  public double getMakespan() {
    return children.stream().map(c -> c.totalDistance).max(Double::compare).get();
  }

  /**
   * Makespan: ist die Länge des längsten Pfades im Aufwachbaum. d.h. Es ist die Maximal
   * zeit, die benötigt wird, um alle Roboter im System zu aktivieren.
   * @return den makespan
   */
//  public double getMakespan() {
//    // Map to store the paths based on the starting robot
//    Map<String, List<Child>> paths = new HashMap<>();
//    // Map to store the cumulative distances for each path
//    Map<String, Double> pathDistances = new HashMap<>();
//
//    System.out.println(children);
//    // Organize the wake-up tree into paths
//    for (Child child : children) {
//      String start = child.startRobotId;
//      paths.computeIfAbsent(start, k -> new ArrayList<>()).add(child);
//
////      double value = computeDistance(child, pathDistances, start);
//      if (isLeaf(child, children)){
//        double value = calculateTotalDistance(child, children);
//        pathDistances.put(start, value);
//      }
//    }
//
//    // Calculate the makespan for each path and determine the maximal makespan
//    double maximalMakespan = Integer.MIN_VALUE;
//    for (Map.Entry<String, Double> entry : pathDistances.entrySet()) {
//      double makespan = entry.getValue();
////          !wakesfromRoot.contains(entry.getKey())?
////          entry.getValue() : entry.getValue() + firstMove;
//
//      maximalMakespan = Math.max(maximalMakespan, makespan);
//    }
//
//    return maximalMakespan;
//  }

  public static double calculateTotalDistance(Child currentChild, List<Child> children) {
    double totalDistance = currentChild.distance;

    // 2- Summe der Distanzen für alle Kinder mit startRobot == currentChild.startRobotId
    for (Child child : children) {
      if (child.startRobotId.equals(currentChild.startRobotId)) {
        totalDistance += child.distance;
      }
    }

    // 3- Summe der Distanzen für alle Kinder mit targetRobot == currentChild.targetRobotId
    for (Child child : children) {
      if (child.targetRobotId.equals(currentChild.targetRobotId)) {
        totalDistance += child.distance;
      }
    }

    return totalDistance;
  }


  public double getms(){
    List<Child> leafs = children.stream().filter(c -> isLeaf(c, children)).toList();

    List<Double> doubles = new ArrayList<>();
    for (Child leaf : leafs) {
      if (leaf.startRobotId.equals(root.startRobotId)){
        Double reduce = rootPath.stream().map(c -> c.distance).reduce(0.0, Double::sum);
        doubles.add(reduce);
      }else {
        doubles.add(getM(leaf));
      }
    }

    return Collections.max(doubles);
  }

  private double getM(Child child) {
    if (child.parent() == null) {
      if (child != root){
        return child.distance + root.distance;
      } else {
        return child.distance;
      }
      // Wenn der Parent null ist, sind wir am Wurzelknoten angekommen
    } else {
      // Rekursiv den Pfad des Elternknotens aufbauen und diesen Knoten anhängen
      return getM(child.parent()) + child.distance;
    }
  }

  public void getmsP(){
    List<Child> leafs = children.stream().filter(c -> isLeaf(c, children)).toList();

    for (Child leaf : leafs) {
      if (leaf.startRobotId.equals(root.startRobotId)){
        System.out.println(rootPath);
      }else {
        System.out.println(buildPath(leaf));
      }
    }
  }

  private String buildPath(Child child) {
    if (child.parent() == null) {
      if (child != root){
        return child.startRobotId() + " -> " + root.targetRobotId +" -> " + child.targetRobotId();
      } else {
        return child.startRobotId() + " -> " + child.targetRobotId();
      }
      // Wenn der Parent null ist, sind wir am Wurzelknoten angekommen
    } else {
      // Rekursiv den Pfad des Elternknotens aufbauen und diesen Knoten anhängen
      return buildPath(child.parent()) + " -> " + child.targetRobotId();
    }
  }
  private static boolean isLeaf(Child child, List<Child> children) {
    // Ein Knoten ist ein Blattknoten, wenn er keine Kinder hat (kein anderes Kind startet von diesem Zielknoten)
    return children.stream().noneMatch(c -> c.startRobotId().equals(child.targetRobotId()));
  }

  private double computeDistance(Child child, Map<String, Double> pathDistances, String start) {
    double parentDistances = 0.0;

    // If this is the first time we encounter this path, compute the parent distances
    if (!pathDistances.containsKey(start)) {
      parentDistances = getParentsDistances(child.parent);
    }

    return pathDistances.getOrDefault(start, 0.0) + child.distance + parentDistances;
  }

  // Recursively calculate the distances from the parents
  private double getParentsDistances(Child parent) {
    if (parent == null || parent.equals(root)) return 0.0;

//    double distance = parent.distance;
//    if (parent.startRobotId.equals(root)){
//      distance += firstMove;
//    }
    return parent.distance + getParentsDistances(parent.parent);
  }

  private void addPath(Child child, Map<String, Double> pathDistances, String start) {
    String key = child.startRobotId +" -> "+ child.targetRobotId;
    for (String k : pathDistances.keySet()) {
      if (k.startsWith(child.startRobotId)){
        double distances = pathDistances.get(k);
        pathDistances.remove(k, distances);
        pathDistances.put(key, distances);
        return;
      }
    }
    pathDistances.put(key, pathDistances.getOrDefault(start, 0.0) + child.distance);
  }


//  private Double computeDistance(
//      Child child,
//      List<Child> children,
//      Map<String, Double> pathDistances,
//      Map<String, List<Child>> paths) {
//    double firstSteps = 0.0;
//    if (pathDistances.get(child.startRobotId) == null){
//      firstSteps = getFirstStepsDistances(child, children, paths);
//    }
//    return pathDistances.getOrDefault(child.startRobotId, 0.0) + child.distance + firstSteps;
//
//  }
//
//  private double getFirstStepsDistances(
//      Child child,
//      List<Child> children,
//      Map<String, List<Child>> paths) {
//
//    Child root = null;
//    for (List<Child> chs : paths.values()){
//      Optional<Child> first = chs.stream().filter(c -> c.targetRobotId.equals(child.startRobotId)).findFirst();
//      if (first.isPresent()){
//        root = first.get();
//      }
//    }
//    Child finalRoot = root;
//    if (finalRoot == null) return 0.0;
//    List<Child> list = children.stream().filter(c -> c.startRobotId.equals(finalRoot.startRobotId))
//        .toList();
//
//    Double firstSteps = list.stream().map(c -> c.distance).reduce(0.0, Double::sum);
//    return firstSteps;
//  }
//
//  private static double computeDistance(
//      Child child,
//      Map<String, Double> pathDistances,
//      String start,
//      Map<String, List<Child>> paths) {
//    double firstSteps = 0.0;
//    if (pathDistances.get(start) == null){
//      firstSteps = getFirstStepsDistances(paths, pathDistances, start);
//    }
//    return pathDistances.getOrDefault(start, 0.0) + child.distance + firstSteps;
//  }
//
//  private static double getFirstStepsDistances(Map<String, List<Child>> paths,
//      Map<String, Double> pathDistances, String start) {
//    double firstStepsDistances = 0.0;
//    if (paths.isEmpty()) return firstStepsDistances;
//
//    for (List<Child> children : paths.values()){
//      Optional<Child> first = children.stream().filter(c -> c.targetRobotId.equals(start)).findFirst();
//      if (first.isPresent()){
//        return pathDistances.get(first.get().startRobotId);
//      }
//    }
//
//    return firstStepsDistances;
//  }

  static final class Child {

    private String startRobotId;
    private String targetRobotId;
    private double distance;
    private double totalDistance;
    private Child parent;

    Child(String startRobotId, String targetRobotId, double distance, Child parent) {
      this.startRobotId = startRobotId;
      this.targetRobotId = targetRobotId;
      this.distance = distance;
      this.parent = parent;
    }

    public void setTotalDistance(double totalDistance) {
      this.totalDistance = totalDistance;
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

    public String startRobotId() {
      return startRobotId;
    }

    public String targetRobotId() {
      return targetRobotId;
    }

    public double distance() {
      return distance;
    }

    public Child parent() {
      return parent;
    }

    }
}
