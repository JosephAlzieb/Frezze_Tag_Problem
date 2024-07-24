package de.frezzetagproblem.tests;

import java.util.*;

public class PermutationGenerator {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("A", "B", "C", "D"); // Beispiel-Liste
        Map<Integer, List<String>> permutations = generatePermutations(list);

        // Ausgabe der Permutationen
        permutations.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    /**
     * Generiert alle Permutationen einer Liste und speichert sie in einem Map.
     * @param list die Liste der Elemente
     * @return Map mit den Permutationen, wobei der Schlüssel die Reihenfolge und der Wert die Liste ist
     */
    public static <T> Map<Integer, List<T>> generatePermutations(List<T> list) {
        Map<Integer, List<T>> map = new HashMap<>();
        permute(list, 0, map);
        return map;
    }

    /**
     * Rekursive Methode zur Generierung von Permutationen.
     * @param list die Liste der Elemente
     * @param start der aktuelle Startindex
     * @param map das Map zur Speicherung der Permutationen
     */
    private static <T> void permute(List<T> list, int start, Map<Integer, List<T>> map) {
        if (start >= list.size() - 1) {
            map.put(map.size(), new ArrayList<>(list));
            return;
        }

        for (int i = start; i < list.size(); i++) {
            Collections.swap(list, start, i);
            permute(list, start + 1, map);
            Collections.swap(list, start, i); // Rückgängig machen des Tauschs
        }
    }
}
