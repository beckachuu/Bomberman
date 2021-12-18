package Systems.AISystem.SMMAX;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import Commons.Config;
import Systems.AISystem.MapDetail;
import Systems.Level.LevelMgr;
import javafx.util.Pair;

public class Ant {

    private static Ant _instance = null;

    public static final int MAX_FAIL_TIME = 20;
    public static final int MAX_BLOCKED = 4;

    public static final double MAX_PHER = 1.0;
    public final double MIN_PHER;
    public static final double RHO = 0.4;
    public static final int HEURISTIC_ALPHA = 2;
    public static final int PHERMONE_ALPHA = 1;
    private final double[][] ORIGINAL_PHERMONE;

    private int ROW, COL;
    private int failCount;
    private final double[][] phermone;
    private List<Pair<Integer, Integer>> bestPath;
    private int bestCompleteLength;
    private int bestUncompleteDistance;

    private List<Pair<Integer, Integer>> pathResult;
    private boolean[][] notBlocked;

    private Ant() {
        ROW = LevelMgr.get().getLvlTileHeight();
        COL = LevelMgr.get().getLvlTileWidth();
        MIN_PHER = (ROW > COL) ? MAX_PHER / ROW : MAX_PHER / COL;

        ORIGINAL_PHERMONE = new double[ROW][COL];
        phermone = new double[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                ORIGINAL_PHERMONE[i][j] = MAX_PHER;
                phermone[i][j] = MAX_PHER;
            }
        }
        failCount = 0;
        bestPath = new ArrayList<>();
        bestCompleteLength = Integer.MAX_VALUE;
        bestUncompleteDistance = Integer.MAX_VALUE;
        pathResult = new ArrayList<>();
        notBlocked = MapDetail.copyDynamicMap();
    }

    public static Ant get() {
        return _instance = (_instance != null) ? _instance : new Ant();
    }

    public boolean notBlocked(int tileX, int tileY) {
        if (tileX < 1 || tileY < 1 || tileX >= COL || tileY >= ROW) {
            return false;
        }
        return notBlocked[tileY][tileX];
    }

    public void block(int tileX, int tileY) {
        if (tileX < 1 || tileY < 1 || tileX >= COL || tileY >= ROW) {
            return;
        }
        notBlocked[tileY][tileX] = false;
    }

    public boolean pickRandomPath(int fromTileX, int fromTileY, int toTileX, int toTileY, int acceptDistance) {
        int tileX, tileY;
        double heuristic;
        double sumRate = 0;
        PriorityQueue<TileDetail> wheelRating = new PriorityQueue<>();

        while (true) {
            if (Math.abs(fromTileX - toTileX) + Math.abs(fromTileY - toTileY) <= acceptDistance) {
                // pathResult.add(new Pair<Integer,Integer>(toTileX, toTileY));
                return true;
            }

            for (int i = 0; i < 4; i++) {
                tileX = fromTileX + Config.addX[i];
                tileY = fromTileY + Config.addY[i];
                if (notBlocked(tileX, tileY)) {
                    int antToDesti = Math.abs(toTileX - tileX) + Math.abs(toTileY - tileY);

                    heuristic = 1 / Math.pow(antToDesti, HEURISTIC_ALPHA);
                    sumRate += Math.pow(phermone[tileY][tileX], PHERMONE_ALPHA) * heuristic;
                    wheelRating.add(new TileDetail(tileX, tileY, sumRate));

                    // System.out.print("\nChecking this available spot: " + tileX + ", " + tileY);
                    // System.out.print("\n+ Phermone: " + phermone[tileY][tileX]);
                    // System.out.print("\n+ Distance: " + antToDesti);
                    // System.out.print("\n+ Rate: " + sumRate + "\n");
                }
            }
            if (sumRate == 0) {
                return false;
            }

            double randomPher = Math.random() * sumRate;
            // System.out.println("Random pher: " + randomPher);

            for (TileDetail each : wheelRating) {
                if (each.getHeuristic() >= randomPher) {
                    // System.out.println("Recieved: " + each.getHeuristic());
                    block(fromTileX, fromTileY);
                    fromTileX = each.getTileX();
                    fromTileY = each.getTileY();
                    // System.out.print("Picked (" + fromTileX + "-" + fromTileY + ") ");
                    break;
                }
            }

            pathResult.add(new Pair<Integer, Integer>(fromTileX, fromTileY));

            sumRate = 0;
            wheelRating.clear();
        }
    }

    public boolean solvePath(int fromTileX, int fromTileY, int toTileX, int toTileY,
            int acceptDistance, int lastTileX, int lastTileY) {
        // System.out.println("\nFrom: (" + fromTileX + "-" + fromTileY + "), to: (" +
        // toTileX + "-" + toTileY + ")");

        Ant singleAnt = new Ant();
        if (singleAnt.pickRandomPath(fromTileX, fromTileY, toTileX, toTileY, acceptDistance)) {
            int pathResultLength = MapDetail.calculatePathLength(singleAnt.pathResult);
            bestUncompleteDistance = pathResultLength;
            if (pathResultLength <= bestCompleteLength) {
                bestCompleteLength = pathResultLength;
                reducePhermone(bestPath);
                bestPath = new ArrayList<>(singleAnt.pathResult);
                increasePhermone(bestPath);
                return true;
            }
            return false;
            // singleAnt.reducePhermone();
        } else {
            int prevDistance = Math.abs(toTileX - lastTileX) + Math.abs(toTileY - lastTileY);
            if (prevDistance < bestUncompleteDistance) {
                bestUncompleteDistance = prevDistance;
                reducePhermone(bestPath);
                bestPath = new ArrayList<>(singleAnt.pathResult);
                increasePhermone(bestPath);
                return true;
            } else {
                reducePhermone(singleAnt.pathResult);
                failCount++;
                if (failCount >= MAX_FAIL_TIME) {
                    failCount = 0;
                    bestCompleteLength = Integer.MAX_VALUE;
                    bestUncompleteDistance = Integer.MAX_VALUE;
                }
                return false;
            }
        }
    }

    public void reducePhermone(final List<Pair<Integer, Integer>> path) {
        int x, y;
        // System.out.println("REDUCING PHERMONE");
        for (Pair<Integer, Integer> eachStep : path) {
            x = eachStep.getKey();
            y = eachStep.getValue();
            phermone[y][x] = (1 - RHO) * phermone[y][x] + RHO * MIN_PHER;
        }
    }

    public void increasePhermone(final List<Pair<Integer, Integer>> path) {
        int x, y;
        // System.out.println("INCREASING PHERMONE");
        for (Pair<Integer, Integer> eachStep : path) {
            x = eachStep.getKey();
            y = eachStep.getValue();
            phermone[y][x] = (1 - RHO) * phermone[y][x] + RHO * MAX_PHER;
        }
    }

    public void resetBest() {
        bestPath = new ArrayList<>();
        bestCompleteLength = Integer.MAX_VALUE;
        bestUncompleteDistance = Integer.MAX_VALUE;
    }

    public void resetPhermone() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                phermone[i][j] = ORIGINAL_PHERMONE[i][j];
            }
        }
    }

    public void resetAll() {
        resetPhermone();
        resetBest();
    }

    public List<Pair<Integer, Integer>> getBestPath() {
        return bestPath;
    }

    public void printPhermone() {
        System.out.println();
        for (double[] each : phermone) {
            for (double d : each) {
                System.out.printf("%.2f ", d);
            }
            System.out.println();
        }
        System.out.println();
    }
}
