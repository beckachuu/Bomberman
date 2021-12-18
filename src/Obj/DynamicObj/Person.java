package Obj.DynamicObj;

import Commons.Config;
import Components.Animation.FrameAnimation;
import Components.Physics.Collider;
import Components.Physics.RigidBody;
import Components.Physics.Vector2D;
import Obj.BaseObj.AnimatedObj;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.DynamicObj.Enemy.Enemy;
import Obj.StaticObj.Projectile.Bomb;
import Obj.StaticObj.Projectile.Flame;
import Obj.StaticObj.Projectile.FlameSegment;
import Obj.StaticObj.Tile;
import Systems.Level.LevelMgr;

// Including Bomber and Enemy
public abstract class Person extends AnimatedObj {

    protected boolean _alive;
    protected boolean _moving;
    protected boolean _invul;
    protected String _dir;

    protected float _runForce;
    protected RigidBody _rigidBody;     // Yếu tố vật lý tác động lên Bomber
    protected Vector2D _lastSafePos;

    public Person(ObjProperty props, String dir, String animeFilePath) {
        super(props);

        // From Animated Object
        _animation = new FrameAnimation(animeFilePath, props);
        _collider = new Collider(props, _animation.getCurrSeqCollBuffer());

        _alive = true;
        _moving = false;
        _invul = false;
        _dir = dir;
        _runForce = 0.0f;

        _rigidBody = new RigidBody();
        _rigidBody.setGravityAccel(Config.RIGID_BODY_GRAVITY_ACCEL);
        _lastSafePos = new Vector2D(_objProps.x, _objProps.y);
    }

    @Override
    public void draw() {
        _animation.drawAnime();
    }

    @Override
    public void update(float dt) {
        events(dt);
        move(dt);
        animate();

        if (!_alive) { afterKilled();}
    }

    protected abstract void events(float dt);
    protected void move(float dt) {
        _lastSafePos.x = _objProps.x;
        _lastSafePos.y = _objProps.y;

        _rigidBody.move(dt);

        _objProps.x += _rigidBody.getDisplacement().x;
        _objProps.y += _rigidBody.getDisplacement().y;

        for (int yt = _collider.getCollTopTile(); yt <= _collider.getCollBottomTile(); ++yt) {
            for (int xt = _collider.getCollLeftTile(); xt <= _collider.getCollRightTile(); ++xt) {

                // Priority, Person => Bomb => Tile

                Enemy e = LevelMgr.get().getEnemy(this);
                if (e != null) {
                    if (this.collide(e)) {
                        this.reverseMove(e);
                        break;
                    }
                }

                Bomb b = LevelMgr.get().getBomb(this);
                if (b != null) {
                    if (this.collide(b)) {
                        this.reverseMove(b);
                        break;
                    }
                }

                Tile t = LevelMgr.get().getTile(xt, yt);
                if (this.collide(t)) {
                    this.reverseMove(t);
                    break;
                }
            }
        }
    }
    protected abstract void reverseMove(GameObject go);
    protected abstract void animate();

    @Override
    public boolean collide(GameObject go) {

        if (!_alive) {return false;}
        if (go == this) { return false; }
        if (go instanceof Tile) { return go.collide(this); }
        if (go instanceof Bomb) { return go.collide(this); }
        if (go instanceof Flame || go instanceof FlameSegment) { this.killed(); return true; }
        return false;
    }

    public abstract void killed();
    public abstract void afterKilled();
    public boolean isAlive() { return _alive; }
}
