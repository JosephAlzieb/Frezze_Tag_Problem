package de.frezzetagproblem.simulator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.frezzetagproblem.Location;
import java.awt.Color;
import java.awt.Graphics;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Simulator {

  List<Robot_Simulator> robots;
  List<Robot_Simulator> on;
  List<Robot_Simulator> off;

  public static void main(String[] args) throws IOException {
    Simulator simulator = new Simulator();
    simulator.runExperiments(5);
  }

  private void runExperiments(int robotCount) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    Path dir = Paths.get("dummy-data/normal/" + robotCount);
    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
    }

    off = new ArrayList<>();
    on = new ArrayList<>();

    JsonReader reader = new JsonReader(new FileReader(dir + "/0.json"));
    JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
    JsonObject robotsJson = jsonObject.getAsJsonObject("robots");

    for (Map.Entry<String, JsonElement> robotEntry : robotsJson.entrySet()) {
      JsonObject robotObj = robotEntry.getValue().getAsJsonObject();
      Robot_Simulator r = gson.fromJson(robotObj, Robot_Simulator.class);

      if (r.isAktive()) {
        on.add(r);
      } else {
        off.add(r);
      }

      this.robots = new ArrayList<>();
      updateRobots();
    }

    SwingUtilities.invokeLater(() -> {
      FTPGraph graph = new FTPGraph(robots);
      graph.setVisible(true);
    });

  }

  private void updateRobots() {
    robots.clear();
    robots.addAll(on);
    robots.addAll(off);
  }

  public class FTPGraph extends JFrame {

    private List<Robot_Simulator> robots;
    private int padding = 50; // Puffer, damit die Punkte nicht direkt am Rand sind


    public FTPGraph(List<Robot_Simulator> robots) {
      this.robots = robots;
      setTitle("FTP - Simulator");
      setSize(800, 600);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      GraphPanel graphPanel = new GraphPanel();
      add(graphPanel);

      double timeunit = 0;
      List<Double> timeUnits = new ArrayList<>();
      Timer timer = new Timer(100, e -> {
//        while (!off.isEmpty()) {
//
//
//          /**
//           * ON- und OFF-Listen werden aktualisiert.
//           */
//          for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
//            Robot robot = iterator.next();
//            if (robot.isAktive()) {
//              on.add(robot);
//              iterator.remove();
//            }
//          }
//          updateRobots();
//          graphPanel.repaint();
//        }
        //movePoints();
        //graphPanel.repaint();
        for (Robot_Simulator ro : on) {
          ro.run(off, timeUnits);
        }
        for (Iterator<Robot_Simulator> iterator = off.iterator(); iterator.hasNext(); ) {
          Robot_Simulator robot = iterator.next();
          if (robot.isAktive()) {
            on.add(robot);
            iterator.remove();
          }
        }
          updateRobots();
          graphPanel.repaint();
      });
      //timer.setRepeats(false);
      timer.start();
    }

    private void movePoints() {
      for (Robot_Simulator point : robots) {
        var x = point.getLocation_x();
        var y = point.getLocation_y();
        point.move(new Location(x+1, y+1));
      }
    }

    private class GraphPanel extends JPanel {

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
      }

      private void drawGraph(Graphics g) {
        /*g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        // Zeichne die Punkte
        for (Robot point : robots) {
          int x = getWidth() / 2 + point.getLocation_x();
          int y = getHeight() / 2 - point.getLocation_y(); // invertiere y, da die y-Koordinate in Swing von oben nach unten geht

          if (point.isDeclared()) {
            g.setColor(Color.GREEN);
          } else {
            g.setColor(Color.BLACK);
          }

          g.fillOval(x - 3, y - 3, 6, 6);
        }*/
        // Zeichne die Achsen
        //g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        //g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        int width = getWidth();
        int height = getHeight();

        // Finde min und max x und y Werte
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Robot_Simulator robot : robots) {
          if (robot.getLocation_x() < minX) minX = robot.getLocation_x();
          if (robot.getLocation_x() > maxX) maxX = robot.getLocation_x();
          if (robot.getLocation_y() < minY) minY = robot.getLocation_y();
          if (robot.getLocation_y() > maxY) maxY = robot.getLocation_y();
        }

        // Skalierungsfaktoren berechnen
        double xScale = (double) (width - 2 * padding) / (maxX - minX);
        double yScale = (double) (height - 2 * padding) / (maxY - minY);

        // Zeichne die Achsen
        g.drawLine(padding, height / 2, width - padding, height / 2); // x-Achse
        g.drawLine(width / 2, padding, width / 2, height - padding); // y-Achse


        // Zeichne die Punkte
        for (Robot_Simulator robot : robots) {
          int x = (int) ((robot.getLocation_x() - minX) * xScale) + padding;
          int y = height - padding - (int) ((robot.getLocation_y() - minY) * yScale); // invertiere y

          if (robot.isAktive()) {
            g.setColor(Color.GREEN);
          } else {
            g.setColor(Color.BLACK);
          }

          g.fillOval(x - 3, y - 3, 10, 10);
          //g.drawString("R_"+robot.getId(), x - 3, y - 3);
        }
      }
    }
  }

}
