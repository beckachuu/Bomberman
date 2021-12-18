package Obj.DynamicObj.Player;

import Commons.Config;
import Obj.BaseObj.GameObject;
import Obj.DynamicObj.Enemy.Enemy;
import Obj.StaticObj.Item.BombItem;
import Obj.StaticObj.Item.Item;
import Obj.StaticObj.LayeredTile;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Person;
import Obj.StaticObj.SpecificTile.Grass;
import Obj.StaticObj.Tile;
import Systems.Audio.AudioMgr;
import Systems.Camera.Camera;
import Systems.Input.InputMgr;
import Obj.StaticObj.Projectile.Bomb;
import Systems.Level.LevelMgr;
import Systems.State.Play.Inter.GameOver;
import Systems.State.StateMgr;

public class Bomber extends Person {

    protected int timeAfterDeath = 120;
    protected int _coolDownTime;              // Thời gian đặt giữa 2 quả bom
    protected int _currBombRate;              // Số lượng Bomb tối đa Bomber có thể đặt
    protected int _maxBombRadius;             // Bán kính phát nố của Bomb

    public Bomber(ObjProperty props, String dir) {
        super(props, dir, Config.BOMBER_ANIME_DIRECTORY);

        _runForce = Config.BOMBER_DEFAULT_RUN_FORCE;

        _coolDownTime = Config.BOMBER_DEFAULT_COOL_DOWN_TIME;
        _currBombRate = Config.BOMBER_DEFAULT_CURRENT_BOMB_RATE;
        _maxBombRadius = Config.BOMBER_DEFAULT_BOMB_RADIUS;
    }

    @Override
    protected void events(float dt) {

        if (!_alive) {
            _moving = false;
            _rigidBody.unsetForce();
            return;
        }

        // Moving is default
        _moving = true;
        _rigidBody.unsetForce();

        if (InputMgr.get().getKL().up) {
            _dir = Config.DIRECTION_UP;
            _rigidBody.applyForceYAxis(_runForce * (-1));
            AudioMgr.get().play("BOMBER_WALK");

        } else if (InputMgr.get().getKL().down) {
            _dir = Config.DIRECTION_DOWN;
            _rigidBody.applyForceYAxis(_runForce * 1);
            AudioMgr.get().play("BOMBER_WALK");

        } else if (InputMgr.get().getKL().left) {
            _dir = Config.DIRECTION_LEFT;
            _rigidBody.applyForceXAxis(_runForce * (-1));
            AudioMgr.get().play("BOMBER_WALK");

        } else if (InputMgr.get().getKL().right) {
            _dir = Config.DIRECTION_RIGHT;
            _rigidBody.applyForceXAxis(_runForce * 1);
            AudioMgr.get().play("BOMBER_WALK");

        } else {
            _moving = false;
        }

        // Đặt bom
        if (InputMgr.get().getKL().space && _currBombRate > 0 && _coolDownTime > 0) {
            // Luôn luôn đặt bom dưới chân mình, tức vị trị bottom-left

            int xPlantBomb = _collider.getCollLeftTile();
            int yPlantBomb = _collider.getCollBottomTile();
            Tile t = LevelMgr.get().getTile(_collider.getCollLeftTile(), _collider.getCollTopTile());

            if (t instanceof Grass ||
                    (t instanceof LayeredTile && ((LayeredTile)t).getTopTile() instanceof Grass)) {

                ObjProperty bombProps = new ObjProperty(
                        "BOMB",
                        LevelMgr.get().tileToPixel(xPlantBomb),
                        LevelMgr.get().tileToPixel(yPlantBomb));
                Bomb b = new Bomb(bombProps,this, _maxBombRadius);
                LevelMgr.get().getBombList().add(b);

                this._currBombRate--;
            }
        }

        if (!LevelMgr.get().getBombList().isEmpty()) {
            if (_coolDownTime > 0) {
                _coolDownTime--;
            } else {
                this.resetBombRate();
                this.resetBombCooldown();
            }
        } else {
            this.resetBombRate();
            this.resetBombCooldown();
        }

    }

    @Override
    public void move(float dt) {
        super.move(dt);
        Camera.get().trackPlayer();     // Camera's update
    }

    @Override
    protected void animate() {

        _animation.setCurrentSeq("BOMBER_IDLE" + _dir);

        if (_moving) {
            _animation.setCurrentSeq("BOMBER_MOVE" + _dir);
        }

        if (!_alive) {
            _animation.setCurrentSeq("BOMBER_DIE");
            _animation.setRepeated(false);
            if (_animation.isEnded() && timeAfterDeath < 0) {
                _removed = true;
            }
        }

        _animation.updateAnime();
    }

    @Override
    protected void reverseMove(GameObject go) {
        int pastCollTop = _collider.getCollTopTile();
        int pastCollBot = _collider.getCollBottomTile();
        int pastCollLeft = _collider.getCollLeftTile();
        int pastCollRight = _collider.getCollRightTile();

        _objProps.x = _lastSafePos.x;
        _objProps.y = _lastSafePos.y;

        if ((_dir.equals(Config.DIRECTION_RIGHT) && go.getTileX() == pastCollRight)
                || (_dir.equals(Config.DIRECTION_LEFT) && go.getTileX() == pastCollLeft)) {
            if (go.getTileY() == pastCollTop) {
                _objProps.y += Config.AUTO_REPOSITION_Y;    // Move down
            }

            if (go.getTileY() == pastCollBot) {
                _objProps.y -= Config.AUTO_REPOSITION_Y;    // Move up
            }
        }

        if ((_dir.equals(Config.DIRECTION_UP) && go.getTileY() == pastCollTop)
                || (_dir.equals(Config.DIRECTION_DOWN) && go.getTileY() == pastCollBot)) {
            if (go.getTileX() == pastCollLeft) {
                _objProps.x += Config.AUTO_REPOSITION_X;    // Move right
            }

            if (go.getTileX() == pastCollRight) {
                _objProps.x -= Config.AUTO_REPOSITION_X;    // Move left
            }
        }
    }

    @Override
    public void afterKilled() {
        if (timeAfterDeath > 0) {
            timeAfterDeath--;
        } else {
            StateMgr.get().pushState(new GameOver());
        }
    }

    @Override
    public boolean collide(GameObject go) {

        if (go instanceof Enemy e && e.isAlive() && this.isAlive()) {
            this.killed();
            return true;
        }

        return super.collide(go);
    }

    public void resetBombRate() {

        for (Item i : LevelMgr.get().getItemList()) {
            if (i instanceof BombItem) {
                _currBombRate = ((BombItem)i).getNewBombRate();
                return;
            }
        }

        _currBombRate = Config.BOMBER_DEFAULT_CURRENT_BOMB_RATE;
    }

    public void resetBombCooldown() {

        for (Item i : LevelMgr.get().getItemList()) {
            if (i instanceof BombItem) {
                _coolDownTime = ((BombItem)i).getNewBombCoolDown();
                return;
            }
        }

        _coolDownTime = Config.BOMBER_DEFAULT_COOL_DOWN_TIME;
    }

    @Override
    public void killed() {
        if (_alive) {
            _alive = false;
            _animation.refreshFrame();
            AudioMgr.get().play("BOMBER_DIE");
        }
    }

    public void setRunForce(float newForce) { _runForce = newForce; }
    public void setCoolDownTime(int newTime) { _coolDownTime = newTime; }
    public void setCurrBombRate(int newRate) { _currBombRate = newRate; }
    public void setMaxBombRadius(int newRad) { _maxBombRadius = newRad; }
}
