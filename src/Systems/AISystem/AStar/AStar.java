package Systems.AISystem.AStar;

import Systems.AISystem.MapDetail;
import Systems.Level.LevelMgr;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {

    private static AStar _instance = null;
    private Pair<Integer, Integer> enemyPos;
    private List<Pair<Integer, Integer>> pathResult = new ArrayList<>();

    private AStar() {
    }

    public static AStar get() {
        return _instance = (_instance != null) ? _instance : new AStar();
    }

    public boolean isValid(int tileX, int tileY) {
        return (tileX >= 0) && (tileX < LevelMgr.get().getLvlTileWidth())
                && (tileY >= 0) && (tileY < LevelMgr.get().getLvlTileHeight());
    }

    public boolean isUnBlocked(int tileX, int tileY) {
        return MapDetail.checkSafeHere(tileX, tileY);
    }

    public double distanceToDesti(int tileX, int tileY) {
        return Math.abs(tileX - enemyPos.getKey()) + Math.abs(tileY - enemyPos.getValue());
    }

    public void tracePath(Cell[][] cellDetails, int x, int y) {
        pathResult.clear();
        Pair<Integer, Integer> next_node = new Pair<Integer, Integer>(x, y);
        do {
            // System.out.printf("(%d, %d) ", x, y);
            pathResult.add(0, next_node);

            next_node = cellDetails[y][x].parent;
            x = next_node.getKey();
            y = next_node.getValue();
        } while (!cellDetails[y][x].parent.equals(next_node));
    }

    public void aStarSearch(int fromTileX, int fromTileY, int toTileX, int toTileY, int acceptDistance) {
        pathResult.clear();
        if (Math.abs(fromTileX - toTileX) + Math.abs(fromTileY - toTileY) <= acceptDistance) {
            pathResult.add(new Pair<Integer, Integer>(toTileX, toTileY));
            return;
        }

        // System.out.printf("From: (%d, %d) - to: (%d, %d)\n", fromTileX, fromTileY,
        // toTileX, toTileY);
        enemyPos = new Pair<Integer, Integer>(toTileX, toTileY);

        boolean[][] closedList = new boolean[LevelMgr.get().getLvlTileHeight()][LevelMgr.get().getLvlTileWidth()];
        Cell[][] CellDetails = new Cell[LevelMgr.get().getLvlTileHeight()][LevelMgr.get().getLvlTileWidth()];

        CellDetails[fromTileY][fromTileX] = new Cell(-1, -1, -1, fromTileX, fromTileY);

        PriorityQueue<CellDetail> openList = new PriorityQueue<>();

        // Put the starting Cell on the open list and set its 'f' as 0
        openList.add(new CellDetail(0.0, fromTileX, fromTileY));

        int[] addX = { -1, 0, 1, 0 };
        int[] addY = { 0, -1, 0, 1 };

        // int loopCount = 0;
        while (!openList.isEmpty()) {
            // System.out.println("Loop count: " + loopCount);
            final CellDetail p = openList.poll();
            fromTileX = p.getTileX();
            fromTileY = p.getTileY();

            closedList[fromTileY][fromTileX] = true;

            int neighbourX, neighbourY;
            for (int count = 0; count < 4; count++) {
                // loopCount++;
                neighbourX = fromTileX + addX[count];
                neighbourY = fromTileY + addY[count];
                if (isValid(neighbourX, neighbourY) && isUnBlocked(neighbourX, neighbourY)) {
                    // If the destination Cell is the same as the current successor
                    // Set the Parent of the destination Cell
                    if (Math.abs(neighbourX - toTileX) + Math.abs(neighbourY - toTileY) <= acceptDistance) {
                        CellDetails[neighbourY][neighbourX] = new Cell(
                                new Pair<Integer, Integer>(fromTileX, fromTileY));
                        tracePath(CellDetails, neighbourX, neighbourY);
                        return;
                    }
                    // If the successor is already on the closed list or if it is blocked, then
                    // ignore it
                    if (!closedList[neighbourY][neighbourX]) {
                        double gNew, hNew, fNew;
                        gNew = CellDetails[fromTileY][fromTileX].g + 1.0;
                        hNew = distanceToDesti(neighbourX, neighbourY);
                        fNew = gNew + hNew;

                        // If it isn’t on the open list, add it to the open list. Make the current
                        // square the parent of this square. Record the f, g, and h costs of the
                        // square Cell
                        // OR
                        // If it is on the open list already, check to see if this path to that square
                        // is better, using 'f' cost as the measure.
                        if (CellDetails[neighbourY][neighbourX] == null) {
                            CellDetails[neighbourY][neighbourX] = new Cell(-1, -1, -1);
                        }
                        if (CellDetails[neighbourY][neighbourX].f == -1
                                || CellDetails[neighbourY][neighbourX].f > fNew) {

                            openList.add(new CellDetail(fNew, neighbourX, neighbourY));

                            // Update the details of this Cell
                            CellDetails[neighbourY][neighbourX] = new Cell(fNew, gNew, hNew, fromTileX, fromTileY);
                        }
                    }
                }
            }
        }
        // System.out.println("Failed to find the Destination Cell");
    }

    public Integer calculatePathLength(List<Pair<Integer, Integer>> path) {
        if (path == null || path.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        int firstY = path.get(0).getValue();
        int firstX = path.get(0).getKey();
        int secondX, secondY;
        Integer result = 1;
        for (Pair<Integer, Integer> each : path) {
            secondX = each.getKey();
            secondY = each.getValue();
            result += Math.abs(firstX - secondX) + Math.abs(firstY - secondY);
            firstX = secondX;
            firstY = secondY;
        }
        return result;
    }

    public List<Pair<Integer, Integer>> getPathResult() {
        return pathResult;
    }
}