package utils;

import static utils.Methods.mod;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Random;
import utils.enums.MapElement;

/**
 * Encapsulates map with utilities methods
 *
 * @author Alex Banks
 */
public class Map {

  private final int MAX_X;
  private final int MAX_Y;

  private final int[][] MAP;
  private final ArrayList<Double> SPAWN_POINTS;

  /**
   * basic constructor that takes raw ints and performs preprocessing
   *
   * @param map_ 2d array of ints
   * @see this#getRandomSpawnPoint()
   * @see ResourceLoader#loadMap(String)
   */
  public Map(int[][] map_) {
    MAP = map_;
    MAX_X = MAP.length;
    MAX_Y = MAP[0].length;
    SPAWN_POINTS = loadSpawnPoints();
  }

  /**
   * called on construction to find and load spawn point TODO: convert back to SPAWNPOINT.toInt()
   *
   * @return Array of Point2D.Double spawnPoint.
   * @see this#Map(int[][])
   */
  private ArrayList<Double> loadSpawnPoints() {
    ArrayList<Double> spawnPoints = new ArrayList<>();
    for (int i = 0; i < MAX_X; i++) {
      for (int j = 0; j < MAX_Y; j++) {
        if (MAP[i][j] == MapElement.WALL.toInt()) { // SPAWNPOINT.toInt()
          spawnPoints.add(new Double(i + 0.5, j + 0.5));
        }
      }
    }
    return spawnPoints;
  }

  public int getMaxX() {
    return MAX_X;
  }

  public int getMaxY() {
    return MAX_Y;
  }

  public boolean withinBounds(Double point) {
    boolean x = point.getX() >= 0 && point.getX() < MAX_X;
    boolean y = point.getY() >= 0 && point.getY() < MAX_Y;
    return x && y;
  }

  /**
   * calculates if point is out of bounds modular arithmetic for map looping
   *
   * @param point location to be checked
   * @return true if wall, false otherwise
   */
  public boolean isWall(Double point) {
    return MAP[(int) mod(point.getX(), MAX_X)][(int) mod(point.getY(), MAX_Y)]
        == MapElement.WALL.toInt();
  }

  /**
   * package method for raw processing
   *
   * @return 2D Array of ints
   */
  int[][] raw() {
    return MAP;
  }

  /**
   * @return random location to position ghouls at if caught
   */
  public Double getRandomSpawnPoint() {
    return SPAWN_POINTS.get((new Random()).nextInt(SPAWN_POINTS.size()));
  }
}
