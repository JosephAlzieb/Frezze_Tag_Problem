package de.frezzetagproblem.applications;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.frezzetagproblem.models.Result;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ResultsToExcelGenerator {

    public static void main(String[] args) throws IOException {
        // Grundverzeichnis, in dem die Ordner liegen
        String rootDir = "results";
        Map<String, List<Result>> resultMap = new HashMap<>();

        // Verzeichnisse durchlaufen
        Files.walk(Paths.get(rootDir))
            .filter(Files::isDirectory)
            .forEach(directory -> {
                String key = directory.getFileName().toString();

                try {
                    // Liste aller JSON-Dateien in diesem Verzeichnis
                    List<Result> results = new ArrayList<>();
                    if ("all_possible_solutions".equals(key)){
                        Files.list(directory)
                            .filter(path -> path.toString().endsWith("bestCase-results.json"))
                            .forEach(jsonFile -> results.addAll(readJsonFile(jsonFile)));

                    } else {
                        Files.list(directory)
                            .filter(path -> path.toString().endsWith(".json"))
                            .forEach(jsonFile -> results.addAll(readJsonFile(jsonFile)));
                    }
                    // Ordnername als Key verwenden
                    if (!results.isEmpty()) {
                        resultMap.put(key, results);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        // Ergebnisse sortieren.
        for (Map.Entry<String, List<Result>> entry : resultMap.entrySet()) {
            entry.setValue(sortResults(entry.getValue()));
        }

        // Zeige das sortierte Ergebnis
        /*resultMap.forEach((key, value) -> {
            System.out.println("Ordner: " + key);
            value.forEach(result -> System.out.println("robotsCount = " + result.getRobotsCount() + ", experimentNumber = " + result.getExperimentNumber()));
        });*/


        generateExcel(resultMap, "Ergebnisse.xlsx");
    }

    // Sortiere die Liste nach robotsCount und experimentNumber
    private static List<Result> sortResults(List<Result> results) {
        results.sort(Comparator.comparingInt(Result::getRobotsCount)
            .thenComparingInt(Result::getExperimentNumber));
        return results;
    }
    private static List<Result> readJsonFile(Path jsonFile) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(jsonFile.toFile())) {
            Type resultListType = new TypeToken<List<Result>>() {}.getType();
            return gson.fromJson(reader, resultListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void generateExcel(Map<String, List<Result>> resultMap, String fileName) {
        // Erstelle eine neue Arbeitsmappe
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ergebnisse");

        //Header der Tabelle
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Experiment");
        headerRow.createCell(1).setCellValue("RobotsCount");

        // Setze die Ordnernamen als Header
        int columnIndex = 2;
        for (String folderName : resultMap.keySet()) {
            headerRow.createCell(columnIndex++).setCellValue(folderName);
        }

        // Erstelle eine Map, um die Result-Daten zu ordnen
        Map<String, Map<Integer, Map<Integer, Row>>> dataMap = new HashMap<>();

        // Verarbeite die Result-Daten
        for (Map.Entry<String, List<Result>> entry : resultMap.entrySet()) {
            String folderName = entry.getKey();
            List<Result> results = entry.getValue();

            for (Result result : results) {
                int experimentNumber = result.getExperimentNumber();
                int robotsCount = result.getRobotsCount();

                String experimentKey = experimentNumber + "_" + robotsCount;

                // Falls noch keine Zeile für dieses Experiment existiert, erstelle sie
                dataMap.putIfAbsent(experimentKey, new HashMap<>());
                Map<Integer, Map<Integer, Row>> robotMap = dataMap.get(experimentKey);

                robotMap.putIfAbsent(robotsCount, new HashMap<>());
                Map<Integer, Row> folderMap = robotMap.get(robotsCount);

                // Falls die Zeile noch nicht existiert, erstelle sie
                if (!folderMap.containsKey(columnIndex)) {
                    Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(0).setCellValue(experimentNumber);
                    row.createCell(1).setCellValue(robotsCount);
                    folderMap.put(columnIndex, row);
                }

                // Füge das Ergebnis in die richtige Spalte ein
                Row row = folderMap.get(columnIndex);
                row.createCell(getColumnIndexForFolder(headerRow, folderName)).setCellValue(result.getTotalTimeUnit());
            }
        }

        // Schreibe die Arbeitsmappe in eine Datei
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getColumnIndexForFolder(Row headerRow, String folderName) {
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equals(folderName)) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

}
