package Obj.DynamicObj.Player;

import java.util.ArrayList;
import java.util.List;

import Commons.Config;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Person;
import Obj.DynamicObj.Enemy.Enemy;
import Obj.StaticObj.LayeredTile;
import Obj.StaticObj.Tile;
import Obj.StaticObj.Projectile.Bomb;
import Obj.StaticObj.SpecificTile.Grass;
import Systems.AISystem.MapDetail;
import Systems.AISystem.AStar.AStar;
import Systems.AISystem.SMMAX.Ant;
import Systems.Level.LevelMgr;
import javafx.util.Pair;

public class AIBomber extends Bomber {
    public static final int TILE_SIZE = LevelMgr.get().getLvlTileSize();
    public static final int BUFFER_WIDTH = LevelMgr.get().getLvlTileSize() / 10;

    public static final long ANT_REPEAT_TIME = 100;
    public static final int PLACE_BOMB_DISTANCE = TILE_SIZE * 44 / 64;
    public static final int ALERT_DISTANCE = 10;
    public static final int THREAT_STOP_DISTANCE = 1;
    public static final int SPOT_STOP_DISTANCE = 0;

    private static final int[] addX = { 0, -1, 0, 1 };
    private static final int[] addY = { 1, 0, -1, 0 };

    private static List<Pair<Integer, Integer>> portalPos = new ArrayList<>();

    private List<Pair<Integer, Integer>> autoPath = new ArrayList<>();
    private List<Pair<Integer, Integer>> savedPath = new ArrayList<>();
    private int safeLengthDistance = Integer.MAX_VALUE;

    private boolean goingUp;
    private boolean goingDown;
    private boolean goingLeft;
    private boolean goingRight;
    private boolean plantBomb;

    private int nextTileX, nextTileY;
    private int currentTileX, currentTileY;

    public AIBomber(ObjProperty props, String dir) {
        super(props, dir);

        goingUp = false;
        goingDown = false;
        goingLeft = false;
        goingRight = false;
        plantBomb = false;
    }

    @Override
    protected void events(float dt) {

        if (!_alive) {
            _moving = false;
            _rigidBody.unsetForce();
            return;
        }

        // Moving is default
        _moving = true;
        _rigidBody.unsetForce();

        currentTileX = (int) _collider.getX() / TILE_SIZE;
        currentTileY = (int) _collider.getY() / TILE_SIZE;
        System.out.print("Bomber pos: (" + currentTileX + "-" + currentTileY + ")\n");
        autoControl();

        if (goingUp) {
            _dir = Config.DIRECTION_UP;
            _rigidBody.applyForceYAxis(_runForce * (-1));

        } else if (goingDown) {
            _dir = Config.DIRECTION_DOWN;
            _rigidBody.applyForceYAxis(_runForce * 1);

        } else if (goingLeft) {
            _dir = Config.DIRECTION_LEFT;
            _rigidBody.applyForceXAxis(_runForce * (-1));

        } else if (goingRight) {
            _dir = Config.DIRECTION_RIGHT;
            _rigidBody.applyForceXAxis(_runForce * 1);

        } else {
            _moving = false;
        }

        // Đặt bom
        if (plantBomb && _currBombRate > 0 && _coolDownTime > 0) {
            // Luôn luôn đặt bom dưới chân mình, tức vị trị bottom-left

            int xPlantBomb = currentTileX;
            int yPlantBomb = currentTileY;
            Tile t = LevelMgr.get().getTile(currentTileX, currentTileY);

            if (t instanceof Grass ||
                    (t instanceof LayeredTile && ((LayeredTile) t).getTopTile() instanceof Grass)) {

                ObjProperty bombProps = new ObjProperty(
                        "BOMB",
                        LevelMgr.get().tileToPixel(xPlantBomb),
                        LevelMgr.get().tileToPixel(yPlantBomb));
                Bomb b = new Bomb(bombProps, this, _maxBombRadius);
                LevelMgr.get().getBombList().add(b);

                this._currBombRate--;
                // Audio.playBombDrop();
            }
        }

        if (!LevelMgr.get().getBombList().isEmpty()) {
            if (_coolDownTime > 0) {
                _coolDownTime--;
            } else {
                this.resetBombRate();
                this.resetBombCooldown();
            }
        } else {
            this.resetBombRate();
            this.resetBombCooldown();
        }

    }

    public void autoControl() {
        goingUp = false;
        goingDown = false;
        goingLeft = false;
        goingRight = false;
        plantBomb = false;

        decidePath();
        setMoveDirection();
    }

    public void decidePath() {
        MapDetail.resetDynamicMap();
        List<Bomb> bombs = LevelMgr.get().getBombList();
        List<Pair<Integer, Integer>> safeSpots = new ArrayList<>();
        List<Enemy> enemies = LevelMgr.get().getEnemyList();

        // Block enemies
        for (Person enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            int enemyTileX = (int) enemy.getCollider().getX() / TILE_SIZE;
            int enemyTileY = (int) enemy.getCollider().getY() / TILE_SIZE;
            MapDetail.blockTile(enemyTileX, enemyTileY);

            // if (Math.abs(_collider.getX() - enemy.getCollider().getX()) <
            // _collider.getW()
            // && Math.abs(_collider.getX() - enemy.getCollider().getX()) < BUFFER_WIDTH) {
            // autoPlaceBomb();
            // } else if (Math.abs(_collider.getX() - enemy.getCollider().getX()) <
            // BUFFER_WIDTH
            // && Math.abs(_collider.getX() - enemy.getCollider().getX()) <
            // _collider.getH()) {
            // autoPlaceBomb();
            // }

            // if (Math.sqrt(Math.pow(_collider.getX() - enemy.getCollider().getX(), 2)
            // + Math.pow(_collider.getY() - enemy.getCollider().getY(), 2)) <=
            // PLACE_BOMB_DISTANCE) {
            // autoPlaceBomb();
            // }

            if (currentTileX == enemyTileX && Math.abs(currentTileY - enemyTileY) <= _maxBombRadius ||
                    (currentTileY == enemyTileY && Math.abs(currentTileX - enemyTileX) <= _maxBombRadius)) {
                autoPlaceBomb();
            }

            // if (Math.abs(getTileX() - enemyTileX) + Math.abs(getTileY() - enemyTileY) <
            // ALERT_DISTANCE) {
            // System.out.printf("ALERT! bomber(%d, %d) - enemy(%d, %d)\n", getTileX(),
            // getTileY(),
            // enemyTileX, enemyTileY);

            // // for (int i = 0; i < 4; i++) {
            // // LevelMgr.get().blockTile(Person.getTileX() + LevelMgr.get().addX[i],
            // // Person.getTileY()
            // // +
            // // LevelMgr.get().addY[i]);
            // // }
            // }
        }

        if (!bombs.isEmpty()) {
            for (Bomb bomb : bombs) {
                safeSpots.addAll(findSafeSpots(bomb.getTileX(), bomb.getTileY()));
            }
        }

        if (!safeSpots.isEmpty()) {
            savedPath = autoPath;
            for (Pair<Integer, Integer> spot : safeSpots) {
                int safeSpotX = spot.getKey();
                int safeSpotY = spot.getValue();

                if (currentTileX == safeSpotX && currentTileY == safeSpotY) {
                    return;
                }

                AStar.get().aStarSearch(currentTileX, currentTileY, safeSpotX, safeSpotY, SPOT_STOP_DISTANCE);
                List<Pair<Integer, Integer>> checkingPath = AStar.get().getPathResult();

                if (!checkingPath.isEmpty()) {
                    int pathLength = MapDetail.calculatePathLength(checkingPath);
                    if (pathLength < safeLengthDistance) {
                        safeLengthDistance = pathLength;
                        updatePath(checkingPath);
                    }
                }
            }
            return;
        }

        if (!enemies.isEmpty() && autoPath.isEmpty()) {
            if (_currBombRate > 0) {
                // Find path to Person
                boolean foundEnemy = false;
                int shortestPathLength = Integer.MAX_VALUE;
                for (Enemy enemy : enemies) {
                    if (!enemy.isAlive()) {
                        continue;
                    }
                    // System.out.println("ENEMY AT: " + enemy.getTileX() + ", " +
                    // enemy.getTileY());
                    AStar.get().aStarSearch(currentTileX, currentTileY, enemy.getTileX(), enemy.getTileY(),
                            THREAT_STOP_DISTANCE);
                    List<Pair<Integer, Integer>> checkingPath = AStar.get().getPathResult();
                    if (!checkingPath.isEmpty()) {
                        int pathLength = MapDetail.calculatePathLength(checkingPath);
                        if (pathLength < shortestPathLength) {
                            updatePath(checkingPath);
                            foundEnemy = true;
                        }
                    }
                }

                if (!foundEnemy) {
                    placeBombIfNearBrick();

                    if (autoPath.isEmpty()) {
                        if (!savedPath.isEmpty()) {
                            autoPath = MapDetail.copyOfPath(savedPath);
                            savedPath.clear();
                            return;
                        }
                        boolean gotPath = false;
                        for (Person enemy : enemies) {
                            for (int i = 0; i < ANT_REPEAT_TIME; i++) {
                                if (Ant.get().solvePath(currentTileX, currentTileY, enemy.getTileX(), enemy.getTileY(),
                                        THREAT_STOP_DISTANCE, nextTileX, nextTileY)) {
                                    gotPath = true;
                                }
                            }
                        }
                        if (gotPath) {
                            updatePath(Ant.get().getBestPath());
                        }
                    }
                }
            } else {
                // Run awayyyyyyyyyyyy
                // System.out.println("RUNNING AWAYYYYYYYYYYYYYYYYYY");
                // int safeX = 1, safeY = 1;
                // int safeDistance = 0;
                // for (int y = 1; y < LevelMgr.get().getLvlTileHeight(); y++) {
                // for (int x = 1; x < LevelMgr.get().getLvlTileWidth(); x++) {
                // if (MapDetail.checkSafeHere(x, y)) {
                // int sumDistance = 0;
                // for (Person enemy : enemies) {
                // sumDistance += (Math.abs(x - enemy.getTileX()) + Math.abs(y -
                // enemy.getTileY()));
                // }
                // if (sumDistance > safeDistance) {
                // safeDistance = sumDistance;
                // safeX = x;
                // safeY = y;
                // }
                // }
                // }
                // }
                // if (!gotAStarPath(safeX, safeY, SPOT_STOP_DISTANCE)) {
                // while (!Ant.get().solvePath(getTileX(), getTileY(), safeX, safeY,
                // SPOT_STOP_DISTANCE, nextTileX, nextTileY)) {
                // }
                // updatePath(Ant.get().getBestPath());
                // }
            }
        } else if (enemies.isEmpty()) {
            placeBombIfNearBrick();
            if (autoPath.isEmpty()) {
                for (Pair<Integer, Integer> portal : portalPos) {
                    if (!gotAStarPath(portal.getKey(), portal.getValue(), SPOT_STOP_DISTANCE)) {
                        while (!Ant.get().solvePath(currentTileX, currentTileY, portal.getKey(), portal.getValue(),
                                SPOT_STOP_DISTANCE, nextTileX, nextTileY)) {
                        }
                        updatePath(Ant.get().getBestPath());
                    }
                }
            }
        }
    }

    public boolean gotAStarPath(int toTileX, int toTileY, int stopDistance) {
        AStar.get().aStarSearch(currentTileX, currentTileY, toTileX, toTileY, stopDistance);

        List<Pair<Integer, Integer>> checkingPath = AStar.get().getPathResult();
        if (!checkingPath.isEmpty()) {
            updatePath(checkingPath);
            return true;
        }
        return false;
    }

    public void updatePath(List<Pair<Integer, Integer>> newPath) {
        autoPath = new ArrayList<>();
        for (Pair<Integer, Integer> each : newPath) {
            autoPath.add(new Pair<Integer, Integer>(each.getKey(), each.getValue()));
        }

        System.out.print("\n================ PATH UPDATED ================\n");
        for (Pair<Integer, Integer> pair : newPath) {
            System.out.print(pair.getKey() + "-" + pair.getValue() + " ");
        }
    }

    public void setMoveDirection() {
        if (!autoPath.isEmpty()) {
            nextTileX = autoPath.get(0).getKey();
            nextTileY = autoPath.get(0).getValue();

            // if (!autoMoveHorizon()) {
            // // System.out.printf("CANT MOVE HORIZONTAL (x = %d). ", (int)
            // // _collider.getX());
            // // System.out.printf("Tile size: %d\n", LevelMgr.get().getLvlTileSize());
            // if (!autoMoveVertical()) {
            // System.out.print(" Removing path...");
            // autoPath.remove(0);
            // setMoveDirection();
            // }
            // }

            if (!autoMoveHorizon() && !autoMoveVertical()) {
                autoPath.remove(0);
                setMoveDirection();
            }
        }
    }

    public boolean autoMoveHorizon() {
        int minX = nextTileX * TILE_SIZE + BUFFER_WIDTH;
        int maxX = nextTileX * TILE_SIZE + TILE_SIZE - BUFFER_WIDTH;
        int thisLeftSide = (int) _collider.getX();
        int thisRightSide = (int) _collider.getX() + (int) _collider.getW();

        if (thisLeftSide >= minX && thisRightSide <= maxX) {
            System.out.printf("CANT MOVE HORIZONTAL (x: %d, y: %d) ", (int) _collider.getX(),
                    (int) _collider.getY());
            return false;
        }

        // if (!LevelMgr.get().checkSafeHere(getTileX(), currentTileY)) {
        // return false;
        // }

        if (thisLeftSide < minX) {
            goingRight = true;
            goingLeft = false;
            System.out.printf("right to: %d (x: %d, y: %d) ", nextTileX, (int) _collider.getX(),
                    (int) _collider.getY());
            return true;
        }

        goingRight = false;
        goingLeft = true;
        System.out.printf("left to: %d (x: %d, y: %d) ", nextTileX, (int) _collider.getX(),
                (int) _collider.getY());
        return true;

    }

    public boolean autoMoveVertical() {
        int minY = nextTileY * TILE_SIZE + BUFFER_WIDTH;
        int maxY = nextTileY * TILE_SIZE + TILE_SIZE - BUFFER_WIDTH;
        int thisUpSide = (int) _collider.getY();
        int thisDownSide = (int) _collider.getY() + (int) _collider.getH();

        if (thisUpSide >= minY && thisDownSide <= maxY) {
            System.out.printf("CANT MOVE VERTICAL (x: %d, y: %d) ", (int) _collider.getX(),
                    (int) _collider.getY());
            return false;
        }
        if (thisUpSide < minY) {
            goingUp = false;
            goingDown = true;
            System.out.printf("down to: %d (x: %d, y: %d) ", nextTileY, (int) _collider.getX(),
                    (int) _collider.getY());
            return true;
        }
        goingUp = true;
        goingDown = false;
        System.out.printf("up to: %d (x: %d, y: %d) ", nextTileY, (int) _collider.getX(),
                (int) _collider.getY());
        return true;

    }

    private List<Pair<Integer, Integer>> findSafeSpots(int bombX, int bombY) {
        List<Pair<Integer, Integer>> safeSpots = new ArrayList<>();
        // If in range of that bomb: find all safe spots around
        if (currentTileX == bombX || currentTileY == bombY) {
            MapDetail.blockTile(bombX, bombY);

            // Checking 4 tile outside bomb radius
            if (MapDetail.checkSafeHere(bombX, bombY + _maxBombRadius + 1)) {
                safeSpots.add(new Pair<Integer, Integer>(bombX, bombY + _maxBombRadius + 1));
            }
            if (MapDetail.checkSafeHere(bombX, bombY - _maxBombRadius - 1)) {
                safeSpots.add(new Pair<Integer, Integer>(bombX, bombY - _maxBombRadius - 1));
            }
            if (MapDetail.checkSafeHere(bombX + _maxBombRadius + 1, bombY)) {
                safeSpots.add(new Pair<Integer, Integer>(bombX + _maxBombRadius + 1, bombY));
            }
            if (MapDetail.checkSafeHere(bombX - _maxBombRadius - 1, bombY)) {
                safeSpots.add(new Pair<Integer, Integer>(bombX - _maxBombRadius - 1, bombY));
            }

            for (int i = -1; i <= 1; i += 2) {
                for (int j = -_maxBombRadius; j <= _currBombRate && j != 0; j++) {
                    // System.out.printf("(%d, %d) ", bombX + i, bombY + j);
                    if (MapDetail.checkSafeHere(bombX + i, bombY + j)) {
                        safeSpots.add(new Pair<Integer, Integer>(bombX + i, bombY + j));
                    }
                    // System.out.printf("(%d, %d) ", bombX + j, bombY + i);
                    if (MapDetail.checkSafeHere(bombX + j, bombY + i)) {
                        safeSpots.add(new Pair<Integer, Integer>(bombX + j, bombY + i));
                    }
                }
            }
        } else {
            // If not in the bomb range, block all bomb path (avoid going into flame)
            for (int i = -_maxBombRadius; i <= _maxBombRadius; i++) {
                MapDetail.blockTile(bombX + i, bombY);
                MapDetail.blockTile(bombX, bombY + i);
            }
        }
        return safeSpots;
    }

    public void placeBombIfNearBrick() {
        for (int c = 0; c < 4; c++) {
            int checkTileX = currentTileX + addX[c];
            int checkTileY = currentTileY + addY[c];
            // System.out.println("Checking for brick at: " + checkTileX + ", " +
            // checkTileY);
            if (MapDetail.hasBrickHere(checkTileX, checkTileY)) {
                // System.out.println("Has brick at: " + checkTileX + ", " + checkTileY);
                autoPlaceBomb();
                return;
            }
        }
    }

    private void autoPlaceBomb() {
        if (_currBombRate > 0) {
            plantBomb = true;

            System.out.printf("\nBOMBBBBBBBBBBBBBBB at: %d, %d\n", currentTileX, currentTileY);

            autoPath.clear();
            safeLengthDistance = Integer.MAX_VALUE;
        }
    }

    public static void addPortalPos(int tileX, int tileY) {
        portalPos.add(new Pair<Integer, Integer>(tileX, tileY));
    }
}
