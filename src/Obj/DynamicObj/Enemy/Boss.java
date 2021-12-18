package Obj.DynamicObj.Enemy;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;

public class Boss extends Enemy {

    public Boss(ObjProperty props, String dir) {
        super(props, dir, Config.BOSS_ANIME_DIRECTORY);
    }

    @Override
    protected void events(float dt) {

    }

    @Override
    protected void reverseMove(GameObject go) {

    }

    @Override
    protected void animate() {

    }

    @Override
    public boolean collide(GameObject go) {
        return false;
    }

    @Override
    public void killed() {
        // Một entity không thể bị giết 2 lần
        if (_alive) {
            _alive = false;
            _animation.refreshFrame();
        }
    }

    @Override
    public void afterKilled() {

    }
}
