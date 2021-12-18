package Obj.StaticObj.Projectile;

import Obj.BaseObj.ObjProperty;
import Obj.BaseObj.GameObject;
import Obj.DynamicObj.Person;
import Systems.Texture.TextureMgr;

public class FlameSegment extends GameObject {

    public FlameSegment(ObjProperty props) {
        super(props);
    }

    @Override
    public void draw() {
        TextureMgr.get().draw(_objProps);
    }

    @Override
    public void update(float dt) {
        //
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
