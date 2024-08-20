package de.frezzetagproblem.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Distance {
    private Robot start;
    private Robot target;
    private double distance;

    public Distance(Robot start, Robot target, double distance) {
        this.start = start;
        this.target = target;
        this.distance = distance;
    }

    public static List<Distance> calculateDistances(List<Robot> on, List<Robot> off) {
        List<Distance> distances = new ArrayList<>();

        for (Robot marked : on) {
            for (Robot unmarked : off) {
                double distance = marked.distance(unmarked);
                distances.add(new Distance(marked, unmarked, distance));
            }
        }

        return distances;
    }

    public static Robot findClosestAktiveRobot(Robot target, List<Distance> distances) {
        Robot closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Distance d : distances) {
            if (d.target.equals(target) && d.distance < minDistance) {
                minDistance = d.distance;
                closest = d.start;
            }
        }

        return closest;
    }

    public static void clear(Robot start, List<Distance> distances) {
        for (Iterator<Distance> iterator = distances.iterator(); iterator.hasNext(); ) {
            Distance d = iterator.next();
            if (start.equals(d.start)) {
                iterator.remove();
            }
        }
    }

    @Override
    public String toString() {
        return "Distance{" +
                "start=" + start +
                ", target=" + target +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance distance1)) {
            return false;
        }
        return Double.compare(distance, distance1.distance) == 0 && Objects.equals(
            start, distance1.start) && Objects.equals(target, distance1.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, target, distance);
    }
}