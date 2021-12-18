package Systems.AISystem;

import java.util.ArrayList;
import java.util.List;

import Systems.Level.LevelMgr;
import javafx.util.Pair;

public class MapDetail {
    public static int ROW, COL;

    private static char[][] charMap;
    private static boolean[][] staticMap;
    private static boolean[][] dynamicMap;
    public static final int[] addX = { -1, 0, 1, 0 };
    public static final int[] addY = { 0, -1, 0, 1 };

    public static void setCharMap(char[][] originalMap) {
        charMap = new char[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                charMap[i][j] = originalMap[i][j];
            }
        }
    }

    public static void setMapDetail(char[][] charMap) {
        ROW = LevelMgr.get().getLvlTileHeight();
        COL = LevelMgr.get().getLvlTileWidth();
        setCharMap(charMap);
        staticMap = new boolean[ROW][COL];
        dynamicMap = new boolean[ROW][COL];

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (charMap[i][j] != '*' && charMap[i][j] != '#' && charMap[i][j] != 'x') {
                    staticMap[i][j] = true;
                    dynamicMap[i][j] = true;
                } else {
                    staticMap[i][j] = false;
                    dynamicMap[i][j] = false;
                }
            }
        }
    }

    public static boolean isValidTile(int tileX, int tileY) {
        if (tileX < 1 || tileY < 1 || tileX >= COL || tileY >= ROW) {
            return false;
        }
        return true;
    }

    public static void blockTile(int tileX, int tileY) {
        // System.out.print(tileX + "-" + tileY + " ");
        if (!isValidTile(tileX, tileY)) {
            return;
        }
        dynamicMap[tileY][tileX] = false;
    }

    public static void unblockTile(int tileX, int tileY) {
        if (!isValidTile(tileX, tileY)) {
            return;
        }
        charMap[tileY][tileX] = ' ';
        staticMap[tileY][tileX] = true;
        dynamicMap[tileY][tileX] = true;
    }

    public static boolean checkSafeHere(int tileX, int tileY) {
        if (!isValidTile(tileX, tileY)) {
            return false;
        }
        return dynamicMap[tileY][tileX];
    }

    public static boolean hasBrickHere(int tileX, int tileY) {
        if (!isValidTile(tileX, tileY)) {
            return false;
        }
        return charMap[tileY][tileX] == '*';
    }

    public static Integer calculatePathLength(List<Pair<Integer, Integer>> path) {
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

    public static void printSafeMap() {
        System.out.println();
        for (boolean[] bool : dynamicMap) {
            for (boolean b : bool) {
                if (b) {
                    System.out.print(" ");
                } else {
                    System.out.print("#");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void resetDynamicMap() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                dynamicMap[i][j] = staticMap[i][j];
            }
        }
    }

    public static boolean[][] copyDynamicMap() {
        boolean[][] copyMap = new boolean[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                copyMap[i][j] = dynamicMap[i][j];
                // if (privateMap[i][j]) {
                // System.out.print(" ");
                // } else {
                // System.out.print("#");
                // }
            }
            // System.out.println();
        }
        return copyMap;
    }

    public static List<Pair<Integer, Integer>> copyOfPath(List<Pair<Integer, Integer>> betterPath) {
        List<Pair<Integer, Integer>> newPath = new ArrayList<>();
        for (Pair<Integer, Integer> eachPair : betterPath) {
            newPath.add(new Pair<Integer, Integer>(eachPair.getKey(), eachPair.getValue()));
        }
        return newPath;
    }
}