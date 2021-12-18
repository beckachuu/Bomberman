package Obj.StaticObj.Item;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Player.Bomber;
import Systems.Level.LevelMgr;

public class FlameItem extends Item {

    private int _newBombRadius = Config.ITEM_FLAME_RADIUS;

    public FlameItem(ObjProperty props) {
        super(props);
    }

    @Override
    protected void takeEffect() {
        _bbm.setMaxBombRadius(_newBombRadius);
        LevelMgr.get().getItemList().add(this);
    }

    @Override
    protected void resetToNormal() {
        _bbm.setMaxBombRadius(Config.BOMBER_DEFAULT_BOMB_RADIUS);
    }
}
