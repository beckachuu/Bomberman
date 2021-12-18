package Obj.DynamicObj.Enemy;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Player.Bomber;
import Systems.Audio.AudioMgr;

public class Dummy extends Enemy {

    public Dummy(ObjProperty props, String dir) {
        super(props, dir, Config.DUMMY_ANIME_DIRECTORY);
    }

    @Override
    protected void events(float dt) {
        _moving = false;
    }

    @Override
    protected void move(float dt) {
        AudioMgr.get().play("DUMMY_IDLE");
    }

    @Override
    protected void reverseMove(GameObject go) {
        // Blank
    }

    @Override
    protected void animate() {

        _animation.setCurrentSeq("DUMMY_MOVE");
        if (!_alive) {
            _animation.setCurrentSeq("DUMMY_DIE");
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
            AudioMgr.get().play("DUMMY_SCREAM");
            _animation.refreshFrame();
        }
    }

    @Override
    public void afterKilled() {
        // + 100 points or smt
    }
}
