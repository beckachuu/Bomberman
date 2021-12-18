package Obj.StaticObj.Item;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Player.Bomber;
import Obj.StaticObj.Projectile.FlameSegment;
import Obj.StaticObj.Tile;

import java.util.Random;

public abstract class Item extends Tile {

    protected Bomber _bbm;
    protected int _usageDuration = Config.ITEM_DURATION_USAGE;
    protected int _onMapDuration = Config.ITEM_DURATION_ON_MAP;
    protected boolean _consumed = false;

    public Item(ObjProperty props) {
        super(props);
    }

    public void draw() {
        if (!_consumed) {
            super.draw();
        }
    }

    public void update(float dt) {

        if (_consumed) {
            if (_usageDuration > 0) {
                _usageDuration -= dt;
            } else {
                this.resetToNormal();
                _removed = true;
            }
        } else {
            if (_onMapDuration > 0) {
                _onMapDuration -= dt;
            } else {
                _removed = true;
            }
        }
    }

    @Override
    public boolean collide(GameObject go) {

        if (go instanceof FlameSegment) {
            _removed = true;
            return true;
        }

        if (go instanceof Bomber) {
            _consumed = true;
            _bbm = (Bomber)go;
            this.takeEffect();
            return false;
        }

        // Ngoài Bomber và Flame Segment thì k ai được đi qua item
        return true;
    }

    protected abstract void takeEffect();
    protected abstract void resetToNormal();

    public static Item generateRandomItem(int xWidth, int yWidth) {
        Random rand = new Random();
        int randItem = rand.nextInt(4);
        switch (randItem) {
            case 1 :
                return new BombItem(new ObjProperty("ADD_BOMB", xWidth, yWidth));
            case 2 :
                return new FlameItem(new ObjProperty("ADD_FLAME", xWidth, yWidth));
            case 3 :
                return new SpeedItem(new ObjProperty("ADD_SPEED", xWidth, yWidth));
            default:
                return null;
        }
    }
}
