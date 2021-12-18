package Obj.BaseObj;

import Systems.Level.LevelMgr;

/**
 * Tất cả các Obj trong game
 */
public abstract class GameObject implements IObject, Collidable {

    protected ObjProperty _objProps;
    protected boolean _removed = false;

    public GameObject(ObjProperty props) {
        _objProps = props;
    }

    public float getX() {
        return _objProps.x;
    }

    public float getY() {
        return _objProps.y;
    }

    public int getWidth() {
        return _objProps.w;
    }

    public int getHeight() {
        return _objProps.h;
    }

    public int getOriginX() {
        return (int) (_objProps.x + _objProps.w / 2);
    }

    public int getOriginY() {
        return (int) (_objProps.y + _objProps.h / 2);
    }

    public int getTileX() {
        return LevelMgr.get().pixelToTile(_objProps.x);
    }

    public int getTileY() {
        return LevelMgr.get().pixelToTile(_objProps.y);
    }

    public boolean isRemoved() {
        return _removed;
    }

    public void setObjProps(ObjProperty newProps) {
        _objProps = newProps;
    }

    public void setTextureID(String id) {
        _objProps.textureID = id;
    }

    public void setX(float x) {
        _objProps.x = x;
    }

    public void setY(float y) {
        _objProps.y = y;
    }

    public void setRemoved(boolean flag) {
        _removed = flag;
    }

    @Override
    public abstract void draw();

    @Override
    public abstract void update(float dt);

    @Override
    public abstract boolean collide(GameObject go);
}
