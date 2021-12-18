package Obj.StaticObj.SpecificTile;

import Commons.Config;
import Components.Animation.FrameAnimation;
import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.StaticObj.Projectile.Flame;
import Obj.StaticObj.Projectile.FlameSegment;
import Systems.AISystem.MapDetail;
import Obj.StaticObj.Tile;

public class Brick extends Tile {

    private boolean _destroyed = false;
    private FrameAnimation _animation;

    public Brick(ObjProperty props) {
        super(props);
        _animation = new FrameAnimation(Config.TILE_ANIME_DIRECTORY, props);
    }

    @Override
    public void draw() {
        _animation.drawAnime();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (_destroyed) {
            _animation.setCurrentSeq("BRICK_EXP");
            _animation.setRepeated(false);
            if (_animation.isEnded()) {
                _removed = true;
                MapDetail.unblockTile(this.getTileX(), this.getTileY());
            }

        } else {
            _animation.setCurrentSeq("BRICK_NORMAL");
        }

        _animation.updateAnime();
    }

    @Override
    public boolean collide(GameObject go) {

        if (go instanceof Flame || go instanceof FlameSegment) {
            this.destroyed();
            return true;
        }

        return super.collide(go);
    }

    private void destroyed() {
        _destroyed = true;
        _animation.refreshFrame();
    }
}
