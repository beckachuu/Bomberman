package Obj.DynamicObj.Enemy;

import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Person;
import Obj.DynamicObj.Player.Bomber;

public abstract class Enemy extends Person {

    public Enemy(ObjProperty props, String dir, String animeFilePath) {
        super(props, dir, animeFilePath);
    }

    @Override
    public boolean collide(GameObject go) {

        if (go instanceof Bomber b && b.isAlive() && this.isAlive()) {
            b.killed();
            return true;
        }

        if (go instanceof Enemy e && e != this) {
            return true;
        }

        return super.collide(go);
    }
}
