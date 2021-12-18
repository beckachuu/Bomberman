package Obj.StaticObj.Item;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Player.Bomber;
import Obj.StaticObj.Projectile.FlameSegment;
import Systems.Level.LevelMgr;

public class BombItem extends Item{

    private int _newBombRate = Config.ITEM_BOMB_RATE;
    private int _newBombCoolDown = Config.ITEM_BOMB_COOL_DOWN;

    public BombItem(ObjProperty props) {
        super(props);
    }

    @Override
    protected void takeEffect() {
        _bbm.setCoolDownTime(_newBombCoolDown);
        _bbm.setCurrBombRate(_newBombRate);
        LevelMgr.get().getItemList().add(this);
    }

    @Override
    protected void resetToNormal() {
        _bbm.setCoolDownTime(Config.BOMBER_DEFAULT_COOL_DOWN_TIME);
        _bbm.setCurrBombRate(Config.BOMBER_DEFAULT_CURRENT_BOMB_RATE);
    }

    public int getNewBombRate() { return _newBombRate; }
    public int getNewBombCoolDown() { return _newBombCoolDown; }
}
