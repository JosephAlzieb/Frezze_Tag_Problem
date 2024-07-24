package de.frezzetagproblem.tests;

import java.util.*;

public class MinKeyFinder {

    public static void main(String[] args) {
        // Beispiel-TreeMap
        TreeMap<Double, List<String>> treeMap = new TreeMap<>();
        treeMap.put(3.5, Arrays.asList("A", "B", "C"));
        treeMap.put(5.2, Arrays.asList("D", "E", "F"));
        treeMap.put(2.8, Arrays.asList("G", "H", "I"));

        List<String> minList = getListWithMinKey(treeMap);
        System.out.println("Liste mit dem kleinsten Key: " + minList);
    }

    /**
     * Findet die Liste mit dem kleinsten Key-Wert in einem TreeMap.
     * @param map das TreeMap mit Double als Key und Liste als Wert
     * @return die Liste, die dem kleinsten Key entspricht
     */
    public static <T> List<T> getListWithMinKey(NavigableMap<Double, List<T>> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyList(); // Rückgabe einer leeren Liste, wenn das Map null oder leer ist
        }

        return map.firstEntry().getValue(); // Holen des ersten Eintrags
    }
}
