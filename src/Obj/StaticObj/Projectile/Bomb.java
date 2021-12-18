package Obj.StaticObj.Projectile;

import Commons.Config;
import Components.Animation.FrameAnimation;
import Components.Physics.Collider;
import Obj.BaseObj.AnimatedObj;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Person;
import Obj.DynamicObj.Player.Bomber;
import Systems.Audio.AudioMgr;
import Systems.Level.LevelMgr;

public class Bomb extends AnimatedObj {

    private Bomber _bomber;
    private boolean _exploded;              // Bom đã nổ hay chưa?
    private boolean _walkThroughAble;       // Bom có thể bị đi qua không?
    private int _countDownTime;             // Frame time đếm ngược từ lúc đặt cho tới lúc bom nổ
    private int _afterExpTime;              // Frame time đếm ngược từ lúc bom nổ cho tới lúc lửa biến mất
    private int _maxBombRadius;                  // Bom nổ tối đa bao nhiêu tile
    private Flame[] _flames;                // List 4 ngọn lửa chưa rõ độ dài được tạo ra khi bom nổ

    public Bomb(ObjProperty props, Bomber bbm, int radius) {
        super(props);

        _animation = new FrameAnimation(Config.BOMB_ANIME_DIRECTORY, props);
        _collider = new Collider(props, _animation.getCurrSeqCollBuffer());

        _bomber = bbm;
        _exploded = false;
        _walkThroughAble = true;
        _countDownTime = Config.BOMB_COUNT_DOWN_TIME;
        _afterExpTime = Config.BOMB_AFTER_EXP_TIME;
        _maxBombRadius = radius;
    }

    @Override
    public void draw() {

        _animation.drawAnime();

        if (_exploded && _afterExpTime > 0) {
            renderFlames();
        }
    }

    @Override
    public void update(float dt) {

        // Move collide
        float bomberX = _bomber.getCollider().getX();
        float bomberY = _bomber.getCollider().getY();
        float bomberW = _bomber.getCollider().getW();
        float bomberH = _bomber.getCollider().getH();

        if (bomberX - _objProps.x >= _objProps.w
        || _objProps.x - bomberX >= bomberW
        || bomberY - _objProps.y >= _objProps.h
        || _objProps.y - bomberY >= bomberH) {
            _walkThroughAble = false;
        }

        // Time's ticking till explosion
        if (_countDownTime > 0) {
            _countDownTime--;

        } else {

            // Bomb exploded!
            if (!_exploded) {
                this.explode();
            } else {
                this.updateFlames(dt);
            }

            if (_afterExpTime > 0) {
                _afterExpTime--;
            } else {
                _removed = true;
            }

        }

        // Collide person with bomb
        Person p = LevelMgr.get().getEnemy(this);
        this.collide(p);

        // Animation
        this.animate();
    }

    private void animate() {

        if (!_exploded && _countDownTime > 0) {
            _animation.setCurrentSeq("BOMB");
        }

        if (_exploded && _afterExpTime > 0) {
            _animation.setCurrentSeq("BOMB_EXP");
        }

        _animation.updateAnime();
    }

    private void explode() {
        _exploded = true;
        AudioMgr.get().play("BOMB_EXPLODE");
        this.createFlames();
    }

    private void createFlames() {
        _flames = new Flame[4];

        _flames[0] = new Flame(_objProps, Config.DIRECTION_UP, _maxBombRadius);
        _flames[1] = new Flame(_objProps, Config.DIRECTION_DOWN, _maxBombRadius);
        _flames[2] = new Flame(_objProps, Config.DIRECTION_LEFT, _maxBombRadius);
        _flames[3] = new Flame(_objProps, Config.DIRECTION_RIGHT, _maxBombRadius);
    }

    private void updateFlames(float dt) {
        for (Flame fl : _flames) {
            fl.update(dt);
        }
    }

    private void renderFlames() {
        for (Flame fl : _flames) {
            fl.draw();
        }
    }

    @Override
    public boolean collide(GameObject go) {

        if (go instanceof Person p) {

            if (!_exploded) {
                return !_walkThroughAble;

            } else {
                p.killed();
                return true;
            }
        }

        if (go instanceof Flame && !_exploded) {
            this.explode();
            return true;
        }

        return false;
    }

    public boolean isExploded() { return _exploded;}
    public boolean isWalkThroughAble() { return _walkThroughAble; }
    public int getAfterExpTime() { return _afterExpTime; }
    public Flame[] getFlames() { return _flames; }
}
