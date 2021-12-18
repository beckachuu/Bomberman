package Obj.BaseObj;

import Components.Animation.FrameAnimation;
import Components.Physics.Collider;

public abstract class AnimatedObj extends GameObject {

    protected FrameAnimation _animation;
    protected Collider _collider;

    public AnimatedObj(ObjProperty props) {
        super(props);
    }

    public Collider getCollider() { return _collider; }
}
