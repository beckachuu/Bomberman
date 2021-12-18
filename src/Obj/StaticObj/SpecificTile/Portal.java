package Obj.StaticObj.SpecificTile;

import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Player.AIBomber;
import Obj.DynamicObj.Player.Bomber;
import Obj.StaticObj.Tile;
import Systems.Level.LevelMgr;
import Systems.State.StateMgr;
import Systems.State.Play.Inter.GameWin;

public class Portal extends Tile {

    private boolean _noEnemyLeft;

    public Portal(ObjProperty props) {
        super(props);
        _noEnemyLeft = false;
        AIBomber.addPortalPos(this.getTileX(), this.getTileY());
    }

    @Override
    public void update(float dt) {
        _noEnemyLeft = LevelMgr.get().isNoEnemyLeft();

        if (_noEnemyLeft) {
            _objProps.textureID = "PORTAL_OPEN";
        } else {
            _objProps.textureID = "PORTAL_CLOSE";
        }
    }

    @Override
    public boolean collide(GameObject go) {

        if (go instanceof Bomber) {
            if (_noEnemyLeft) {
                StateMgr.get().pushState(new GameWin());
            }
            return false; // Bomber can walk through the portal
        }

        return true; // true, if not bomber
    }
}
