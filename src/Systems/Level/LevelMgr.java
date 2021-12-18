package Systems.Level;

import java.util.ArrayList;
import java.util.List;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Person;
import Obj.DynamicObj.Enemy.Boss;
import Obj.DynamicObj.Enemy.Dummy;
import Obj.DynamicObj.Enemy.Enemy;
import Obj.DynamicObj.Enemy.Shaman;
import Obj.DynamicObj.Enemy.Skeleton;
import Obj.DynamicObj.Player.AIBomber;
import Obj.DynamicObj.Player.Bomber;
import Obj.StaticObj.LayeredTile;
import Obj.StaticObj.Tile;
import Obj.StaticObj.Item.Item;
import Obj.StaticObj.Projectile.Bomb;
import Obj.StaticObj.SpecificTile.Brick;
import Obj.StaticObj.SpecificTile.Grass;
import Obj.StaticObj.SpecificTile.Portal;
import Obj.StaticObj.SpecificTile.Wall;
import Systems.Audio.AudioMgr;
import Systems.Camera.Camera;
import Systems.Collision.CollisionMgr;
import Systems.Timer.Timer;
import Systems.XMLParser.ParseMgr;

public class LevelMgr {

    private static LevelMgr _instance;

    private boolean _autoPlayer;

    // Get infos from ParseMgr
    private int _currLvlID;
    private int _lvlTileSize;
    private int _lvlTileWidth;
    private int _lvlTileHeight;
    private char[][] _charMap;

    private Tile[][] _tile2D;
    private Bomber _bomber;
    private final List<Enemy> _enemyList;
    private final List<Bomb> _bombList;
    private final List<Item> _consumeItemList;

    private LevelMgr() {
        _autoPlayer = false;
        _currLvlID = Config.START_LEVEL_ID; // 1
        _lvlTileSize = -1;
        _lvlTileWidth = -1;
        _lvlTileHeight = -1;

        _enemyList = new ArrayList<>();
        _bombList = new ArrayList<>();
        _consumeItemList = new ArrayList<>();
    }

    public static LevelMgr get() {
        return _instance = (_instance != null) ? _instance : new LevelMgr();
    }

    public GameObject getGameObject(int xTile, int yTile, GameObject ignore) {

        // Get person : priority 1st
        if (!(ignore instanceof Bomber)) {
            for (int yt = _bomber.getCollider().getCollTopTile(); yt <= _bomber.getCollider()
                    .getCollBottomTile(); ++yt) {
                for (int xt = _bomber.getCollider().getCollLeftTile(); xt <= _bomber.getCollider()
                        .getCollRightTile(); ++xt) {
                    if (xt == xTile && yt == yTile) {
                        return _bomber;
                    }
                }
            }
        }

        for (Enemy e : _enemyList) {

            if (e instanceof Dummy && ignore instanceof Dummy
                    || e instanceof Skeleton && ignore instanceof Dummy) {
                continue;
            }

            for (int yt = e.getCollider().getCollTopTile(); yt <= e.getCollider().getCollBottomTile(); ++yt) {
                for (int xt = e.getCollider().getCollLeftTile(); xt <= e.getCollider().getCollRightTile(); ++xt) {
                    if (xt == xTile && yt == yTile) {
                        return e;
                    }
                }
            }

        }

        // Get bomb, priority 2nd
        if (!(ignore instanceof Bomb)) {
            for (Bomb b : _bombList) {
                if (b.getTileX() == xTile && b.getTileY() == yTile) {
                    return b;
                }
            }
        }

        // Get tile, priority 3rd
        return _tile2D[yTile][xTile];
    }

    public void initNewLevel() {

        // Parse level map infos
        ParseMgr.get().parseLevelMap(Config.LEVEL_MAP_DIRECTORY +
                "level" + _currLvlID + ".xml");

        // Start background music
        AudioMgr.get().play(String.valueOf(_currLvlID));

        // Set the camera
        Camera.get().setLevelSize(_lvlTileWidth * _lvlTileSize, _lvlTileHeight * _lvlTileSize);

        // Set the remaining infos
        _tile2D = new Tile[_lvlTileHeight][_lvlTileWidth];
        for (int yTile = 0; yTile < _lvlTileHeight; ++yTile) {
            for (int xTile = 0; xTile < _lvlTileWidth; ++xTile) {

                char c = _charMap[yTile][xTile];
                int xPos = tileToPixel(xTile);
                int yPos = tileToPixel(yTile);

                if (c == ' ') { // Grass
                    _tile2D[yTile][xTile] = new Grass(new ObjProperty("GRASS", xPos, yPos));
                }

                if (c == 'p') { // Bomber
                    _tile2D[yTile][xTile] = new Grass(new ObjProperty(
                            "GRASS", xPos, yPos)); // GRASS

                    ObjProperty charProps = new ObjProperty(
                            "BOMBER",
                            xPos + Config.CHARACTER_START_OFFSET_X,
                            yPos + Config.CHARACTER_START_OFFSET_Y);

                    if (_autoPlayer) {
                        _bomber = new AIBomber(charProps, Config.DIRECTION_RIGHT);
                    } else {
                        _bomber = new Bomber(charProps, Config.DIRECTION_RIGHT);
                    }

                    Camera.get().setTarget(_bomber);
                }

                if (c == '1') { // Dummy
                    _tile2D[yTile][xTile] = new Grass(new ObjProperty(
                            "GRASS", xPos, yPos));

                    ObjProperty charProps = new ObjProperty(
                            "DUMMY",
                            xPos, yPos);
                    _enemyList.add(
                            new Dummy(charProps, Config.DIRECTION_NONE));
                }

                if (c == '2') { // Skeleton
                    _tile2D[yTile][xTile] = new Grass(new ObjProperty(
                            "GRASS", xPos, yPos));

                    ObjProperty charProps = new ObjProperty(
                            "SKELETON",
                            xPos + Config.CHARACTER_START_OFFSET_X,
                            yPos + Config.CHARACTER_START_OFFSET_Y);
                    _enemyList.add(
                            new Skeleton(charProps, Config.DIRECTION_LEFT));
                }

                if (c == '3') { // Shaman
                    _tile2D[yTile][xTile] = new Grass(new ObjProperty(
                            "GRASS", xPos, yPos));

                    ObjProperty charProps = new ObjProperty(
                            "SHAMAN",
                            xPos + Config.CHARACTER_START_OFFSET_X,
                            yPos + Config.CHARACTER_START_OFFSET_Y);
                    _enemyList.add(
                            new Shaman(charProps, Config.DIRECTION_LEFT));
                }

                if (c == '4') { // Boss
                    _tile2D[yTile][xTile] = new Grass(new ObjProperty(
                            "GRASS", xPos, yPos));

                    ObjProperty charProps = new ObjProperty(
                            "BOSS",
                            xPos + Config.CHARACTER_START_OFFSET_X,
                            yPos + Config.CHARACTER_START_OFFSET_Y);
                    _enemyList.add(
                            new Boss(charProps, Config.DIRECTION_LEFT));
                }

                if (c == '#') { // Wall
                    _tile2D[yTile][xTile] = new Wall(new ObjProperty("WALL", xPos, yPos));
                }

                if (c == '*') { // Item inside, Brick at top

                    Tile grass = new Grass(new ObjProperty("GRASS", xPos, yPos));
                    Item item = Item.generateRandomItem(xPos, yPos);
                    Tile brick = new Brick(new ObjProperty("BRICK", xPos, yPos));

                    Tile[] tiles = new Tile[3];
                    tiles[0] = brick;
                    tiles[1] = item;
                    tiles[2] = grass;

                    _tile2D[yTile][xTile] = new LayeredTile(
                            new ObjProperty("CHANGE_LATER", xPos, yPos), tiles);
                }

                if (c == 'x') { // Portal inside, Brick at top
                    Tile portal = new Portal(new ObjProperty("PORTAL_CLOSE", xPos, yPos));
                    Tile brick = new Brick(new ObjProperty("BRICK", xPos, yPos));

                    Tile[] tiles = new Tile[2];
                    tiles[0] = brick;
                    tiles[1] = portal;

                    _tile2D[yTile][xTile] = new LayeredTile(
                            new ObjProperty("CHANGE_LATER", xPos, yPos), tiles);
                }
            }
        }

        // Confirmation
        // System.out.println("Level initialize!");
    }

    public void render() {

        for (Tile[] tile1D : _tile2D) {
            for (Tile tile : tile1D) {
                if (tile == null)
                    continue;
                tile.draw();
            }
        }

        _bomber.draw();

        for (Bomb b : _bombList) {
            if (b == null)
                continue;
            b.draw();
        }

        for (Enemy e : _enemyList) {
            if (e == null)
                continue;
            e.draw();
        }

        // Không draw _consumedItemList vì Item đã bị ăn rồi

        // Confirmation
        // System.out.println("Level render!");
    }

    public void update() {

        // Delay tại đây
        float dt = Timer.get().getDeltaTime();

        for (Tile[] tile1D : _tile2D) {
            for (Tile tile : tile1D) {
                if (tile == null)
                    continue;
                tile.update(0);
            }
        }

        _bomber.update(dt);

        for (Bomb b : _bombList) {
            if (b == null)
                continue;
            b.update(dt);
        }

        for (Enemy e : _enemyList) {
            if (e == null)
                continue;
            e.update(dt);
        }

        for (Item i : _consumeItemList) {
            if (i == null)
                continue;
            i.update(dt);
        }

        // Confirmation
        // System.out.println("Play update!");

        // Handle removal
        /*
         * for (Tile[] tile1D : _tile2D) {
         * for (Tile tile : tile1D) {
         * //
         * }
         * }
         */
        _bombList.removeIf(GameObject::isRemoved);
        _enemyList.removeIf(GameObject::isRemoved);
        _consumeItemList.removeIf(GameObject::isRemoved);
    }

    public void clean() {
        AudioMgr.get().stop(String.valueOf(_currLvlID));

        _enemyList.clear();
        _bombList.clear();
        _consumeItemList.clear();
    }

    /*
     * public void setTile2DList(List<List<Tile>> newTile2DList) { _tile2DList =
     * newTile2DList; }
     * public void setPersonList(List<Person> newCharList) { _charList =
     * newCharList; }
     * public void setBombList(List<Bomb> newBombList) { _bombList = newBombList; }
     * public void setPowerupList(List<Powerup> newPowerUpList) { _pwupList =
     * newPowerUpList; }
     * public void setButtonList(List<Button> newButtonList) { _btnList =
     * newButtonList; }
     */
    public void setAutoPlayer(boolean flag) {
        _autoPlayer = flag;
    }

    public void setCurrLvlID(int lvlID) {
        _currLvlID = lvlID;
    }

    public void setLvlTileSize(int newTileSize) {
        _lvlTileSize = newTileSize;
    }

    public void setLvlTileWidth(int newTileWidth) {
        _lvlTileWidth = newTileWidth;
    }

    public void setLvlTileHeight(int newTileHeight) {
        _lvlTileHeight = newTileHeight;
    }

    public void setOriginalMap(char[][] cMap) {
        _charMap = cMap;
    }

    public char[][] get_charMap() {
        return _charMap;
    }

    public void increaseLvlID() {
        _currLvlID++;
    }

    public int getCurrLvlID() {
        return _currLvlID;
    }

    public List<Bomb> getBombList() {
        return _bombList;
    }

    public List<Item> getItemList() {
        return _consumeItemList;
    }

    public Bomber getBomber() {
        return _bomber;
    }

    public List<Enemy> getEnemyList() {
        return _enemyList;
    }

    public int getLvlTileSize() {
        return _lvlTileSize;
    }

    public int getLvlTileWidth() {
        return _lvlTileWidth;
    }

    public int getLvlTileHeight() {
        return _lvlTileHeight;
    }

    public int pixelToTile(float pixel) {
        return (int) (pixel / _lvlTileSize);
    }

    public int tileToPixel(int tile) {
        return tile * _lvlTileSize;
    }

    public Tile getTile(int xTile, int yTile) {
        return _tile2D[yTile][xTile];
    }

    // ignore có thể là Person hoặc là Bomb
    public Enemy getEnemy(GameObject go) {

        for (Enemy e : _enemyList) {
            if (e == go) {
                continue;
            }

            if (go instanceof Person p) {
                if (CollisionMgr.get().checkPersonToPersonCollision(e, p)) { // P2P
                    return e;
                }
            } else {
                if (CollisionMgr.get().checkObjectToPersonCollision(go, e)) { // O2P
                    return e;
                }
            }
        }

        return null;
    }

    public Bomb getBomb(GameObject go) {
        for (Bomb b : _bombList) {
            if (b == go) {
                continue;
            }

            if (go instanceof Person) {
                if (CollisionMgr.get().checkObjectToPersonCollision(b, (Person) go)) { // O2P
                    return b;
                }
            } else {
                if (CollisionMgr.get().checkObjectToObjectCollision(b, go)) { // O2O
                    return b;
                }
            }
        }

        return null;
    }

    public boolean isNoEnemyLeft() {
        for (Person p : _enemyList) {
            if (p instanceof Dummy || p instanceof Skeleton || p instanceof Shaman || p instanceof Boss) {
                return false;
            }
        }
        return true;
    }
}
