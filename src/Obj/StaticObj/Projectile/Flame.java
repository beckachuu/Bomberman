package Obj.StaticObj.Projectile;

import Obj.BaseObj.ObjProperty;
import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.DynamicObj.Enemy.Enemy;
import Obj.DynamicObj.Person;
import Obj.DynamicObj.Player.Bomber;
import Obj.StaticObj.Tile;
import Systems.Collision.CollisionMgr;
import Systems.Level.LevelMgr;

public class Flame extends GameObject {

    private String _direction;
    private int _maxBombRadius;
    private FlameSegment[] flameSegments;

    public Flame(ObjProperty props, String dir, int bmbRad) {
        super(props);
        _direction = dir;
        _maxBombRadius = bmbRad;
        this.createFlameSegments();
    }

    private void createFlameSegments() {
        flameSegments = new FlameSegment[calculatePermittedRadius()];

        int xEachTile = this.getTileX();
        int yEachTile = this.getTileY();

        for (int i = 0; i < flameSegments.length; ++i) {
            boolean last = (i == flameSegments.length - 1);

            if (_direction.equals(Config.DIRECTION_UP)) {
                yEachTile--;
            }
            if (_direction.equals(Config.DIRECTION_DOWN)) {
                yEachTile++;
            }
            if (_direction.equals(Config.DIRECTION_LEFT)) {
                xEachTile--;
            }
            if (_direction.equals(Config.DIRECTION_RIGHT)) {
                xEachTile++;
            }

            String tmpDir = "";
            if (!last && ((_direction.equals(Config.DIRECTION_UP))
                    || (_direction.equals(Config.DIRECTION_DOWN)))) {
                tmpDir = Config.DIRECTION_VERTICAL;
            }

            if (!last && ((_direction.equals(Config.DIRECTION_LEFT))
                    || (_direction.equals(Config.DIRECTION_RIGHT)))) {
                tmpDir = Config.DIRECTION_HORIZONTAL;
            }

            ObjProperty props = new ObjProperty(
                    "FLAME" + ((last)? _direction : tmpDir),
                    LevelMgr.get().tileToPixel(xEachTile),
                    LevelMgr.get().tileToPixel(yEachTile));

            flameSegments[i] = new FlameSegment(props);
        }
    }

    private int calculatePermittedRadius() {
        int r = 0;
        int xEachTile = this.getTileX();
        int yEachTile = this.getTileY();

        while (r < _maxBombRadius) {

            if (_direction.equals(Config.DIRECTION_UP)) {
                yEachTile--;
            }
            if (_direction.equals(Config.DIRECTION_DOWN)) {
                yEachTile++;
            }
            if (_direction.equals(Config.DIRECTION_LEFT)) {
                xEachTile--;
            }
            if (_direction.equals(Config.DIRECTION_RIGHT)) {
                xEachTile++;
            }

            FlameSegment tempFlS = new FlameSegment(
                    new ObjProperty("UNKNOWN",
                            LevelMgr.get().tileToPixel(xEachTile),
                            LevelMgr.get().tileToPixel(yEachTile)));

            Tile t = LevelMgr.get().getTile(xEachTile, yEachTile);
            if (t.collide(this)) {
                break;
            }

            Bomb b = LevelMgr.get().getBomb(tempFlS);
            if (b != null) {
                r++;
                if (b.collide(this)) {
                    break;
                }
            }

            Enemy e = LevelMgr.get().getEnemy(tempFlS);
            if (e != null) {
                r++;
                if (e.collide(this)) {
                    break;
                }
            }

            Bomber bbm = LevelMgr.get().getBomber();
            if (CollisionMgr.get().checkObjectToPersonCollision(tempFlS, bbm)) {
                r++;
                if (bbm.collide(this)) {
                    break;
                }
            }

            r++;
        }

        return r;
    }

    @Override
    public void draw() {
        for (FlameSegment fls : flameSegments) {
            fls.draw();
        }
    }

    @Override
    public void update(float dt) {

        // Collider handle
        for (FlameSegment fls : flameSegments) {
            Bomb b = LevelMgr.get().getBomb(fls);
            fls.collide(b);

            Enemy e = LevelMgr.get().getEnemy(fls);
            fls.collide(e);

            Bomber bbm = LevelMgr.get().getBomber();
            if (CollisionMgr.get().checkObjectToPersonCollision(fls, bbm)) {
                fls.collide(bbm);
            }
        }

    }

    @Override
    public boolean collide(GameObject go) {
        if (go instanceof Person p && p.isAlive()) {
            p.killed();
            return true;
        }
        return false;
    }
}
