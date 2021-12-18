package Obj.StaticObj.Item;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Systems.Level.LevelMgr;

public class SpeedItem extends Item {

    private float _newRunForce = Config.ITEM_RUN_FORCE;

    public SpeedItem(ObjProperty props) {
        super(props);
    }

    @Override
    protected void takeEffect() {
        _bbm.setRunForce(_newRunForce);
        LevelMgr.get().getItemList().add(this);
    }

    @Override
    protected void resetToNormal() {
        _bbm.setRunForce(Config.BOMBER_DEFAULT_RUN_FORCE);
    }

    public float getNewRunForce() { return _newRunForce; }
}
