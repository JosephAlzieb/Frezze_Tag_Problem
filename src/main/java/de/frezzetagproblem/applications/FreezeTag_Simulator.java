package de.frezzetagproblem.applications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.frezzetagproblem.models.Helper;
import de.frezzetagproblem.models.Robot;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class FreezeTag_Simulator {

  List<Robot> robots;
  List<Robot> on;
  List<Robot> off;

  public static void main(String[] args) throws IOException {
    FreezeTag_Simulator simulator = new FreezeTag_Simulator();
    simulator.runExperiments(5);
  }

  private void runExperiments(int robotCount) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String path = Helper.getPathName();
    Path dir = Paths.get(path + robotCount);
    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
    }

    off = new ArrayList<>();
    on = new ArrayList<>();

    Helper.readJsonFile(Path.of(dir + "/0.json"), on, off);

    this.robots = new ArrayList<>();
    updateRobots();

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

    private final List<Robot> robots;
    private final int padding = 50; // Puffer, damit die Punkte nicht direkt am Rand sind


    public FTPGraph(List<Robot> robots) {
      this.robots = robots;
      setTitle("FTP - Simulator");
      setSize(800, 600);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      GraphPanel graphPanel = new GraphPanel();
      add(graphPanel);

      Timer timer = new Timer(100, e -> {
        for (Robot ro : on) {
          ro.run(off);
        }
        for (Iterator<Robot> iterator = off.iterator(); iterator.hasNext(); ) {
          Robot robot = iterator.next();
          if (robot.isAktive()) {
            on.add(robot);
            iterator.remove();
          }
        }
        updateRobots();
        graphPanel.repaint();
      });
      timer.start();
    }

    private class GraphPanel extends JPanel {

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
      }

      private void drawGraph(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        // Finde min und max x und y Werte
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Robot robot : robots) {
          if (robot.getLocation_x() < minX) {
            minX = robot.getLocation_x();
          }
          if (robot.getLocation_x() > maxX) {
            maxX = robot.getLocation_x();
          }
          if (robot.getLocation_y() < minY) {
            minY = robot.getLocation_y();
          }
          if (robot.getLocation_y() > maxY) {
            maxY = robot.getLocation_y();
          }
        }

        // Skalierungsfaktoren berechnen
        double xScale = (double) (width - 2 * padding) / (maxX - minX);
        double yScale = (double) (height - 2 * padding) / (maxY - minY);

        // Zeichne die Achsen
        g.drawLine(padding, height / 2, width - padding, height / 2); // x-Achse
        g.drawLine(width / 2, padding, width / 2, height - padding); // y-Achse

        // Zeichne die Punkte
        for (Robot robot : robots) {
          int x = (int) ((robot.getLocation_x() - minX) * xScale) + padding;
          int y =
              height - padding - (int) ((robot.getLocation_y() - minY) * yScale); // invertiere y

          if (robot.isAktive()) {
            g.setColor(Color.GREEN);
          } else {
            g.setColor(Color.BLACK);
          }

          g.fillOval(x - 3, y - 3, 10, 10);
        }
      }
    }
  }

}
