package Obj.DynamicObj.Enemy;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Systems.Audio.AudioMgr;

import java.util.Random;

public class Skeleton extends Enemy {

    private int _resetDirTime;
    private Random _rand;
    private int _randDir;

    public Skeleton(ObjProperty props, String dir) {
        super(props, dir, Config.SKELETON_ANIME_DIRECTORY);

        _runForce = Config.SKELETON_RUN_FORCE;

        _resetDirTime = Config.SKELETON_RESET_DIRECTION_TIME;
        _rand = new Random();
        _randDir = _rand.nextInt(5);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    protected void events(float dt) {

        _rigidBody.unsetForce();

        if (!_alive) { return;}

        if (_resetDirTime == Config.SKELETON_RESET_DIRECTION_TIME) {
            _randDir = _rand.nextInt(4);
            _resetDirTime--;
        } else {
            _resetDirTime--;
            if (_resetDirTime == 0) {
                _resetDirTime = Config.SKELETON_RESET_DIRECTION_TIME;
            }
        }

        switch (_randDir) {
            case 0:
                _moving = true;
                _dir = Config.DIRECTION_UP;
                _rigidBody.applyForceYAxis(_runForce * (-1));
                AudioMgr.get().play("SKELETON_WALK");
                break;
            case 1:
                _moving = true;
                _dir = Config.DIRECTION_DOWN;
                _rigidBody.applyForceYAxis(_runForce * 1);
                AudioMgr.get().play("SKELETON_WALK");
                break;
            case 2:
                _moving = true;
                _dir = Config.DIRECTION_LEFT;
                _rigidBody.applyForceXAxis(_runForce * (-1));
                AudioMgr.get().play("SKELETON_WALK");
                break;
            case 3:
                _moving = true;
                _dir = Config.DIRECTION_RIGHT;
                _rigidBody.applyForceXAxis(_runForce * 1);
                AudioMgr.get().play("SKELETON_WALK");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + _randDir);
        }

    }

    @Override
    protected void animate() {

        _animation.setCurrentSeq("SKELETON_IDLE" + _dir);

        if (_moving) {
            _animation.setCurrentSeq("SKELETON_MOVE" + _dir);
        }

        if (!_alive) {
            _animation.setCurrentSeq("SKELETON_DIE");
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
            AudioMgr.get().play("SKELETON_DIE");
            _animation.refreshFrame();
        }
    }

    @Override
    protected void reverseMove(GameObject go) {
        _objProps.x = _lastSafePos.x;
        _objProps.y = _lastSafePos.y;

        _randDir = _rand.nextInt(4);    // Reset direction after collide
    }

    @Override
    public void afterKilled() {
        // + 200 points
    }
}
