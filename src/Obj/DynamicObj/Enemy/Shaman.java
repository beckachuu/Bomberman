package Obj.DynamicObj.Enemy;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;

public class Shaman extends Enemy {

    public Shaman(ObjProperty props, String dir) {
        super(props, dir, Config.SHAMAN_ANIME_DIRECTORY);
        _runForce = Config.SHAMAN_RUN_FORCE;
    }

    @Override
    protected void events(float dt) {

    }

    @Override
    protected void reverseMove(GameObject go) {

    }

    @Override
    protected void animate() {

        _animation.setCurrentSeq("SHAMAN_IDLE" + _dir);

        if (_moving) {
            _animation.setCurrentSeq("SHAMAN_MOVE" + _dir);
        }

        if (!_alive) {
            _animation.setCurrentSeq("SHAMAN_DIE");
            _animation.setRepeated(false);

            if (_animation.isEnded()) {
                _removed = true;
            }
        }

        _animation.updateAnime();
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
        // + 300 points
    }
}
