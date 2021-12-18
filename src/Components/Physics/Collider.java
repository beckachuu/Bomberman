package Components.Physics;

import Commons.Config;
import Obj.BaseObj.ObjProperty;
import Commons.Rect;
import Systems.Level.LevelMgr;

public class Collider {

    private ObjProperty _objProps;  // ObjProps của đối tượng sử dụng Collider
    private Rect _buffer;   // Buffer là phần margins bao xung quanh Box,
                            // ngăn cách phần hình ảnh của Obj với Box

    public Collider(ObjProperty props, Rect buffer) {
        _objProps = props;
        _buffer = buffer;
    }

    public float getX() { return _objProps.x - _buffer.x; }
    public float getY() { return _objProps.y - _buffer.y; }
    public float getW() { return _objProps.w - _buffer.w; }
    public float getH() { return _objProps.h - _buffer.h; }

    public int getCollLeftTile()  { return LevelMgr.get().pixelToTile(this.getX() + Config.COLLIDER_OFFSET); }
    public int getCollRightTile() { return LevelMgr.get().pixelToTile(this.getX() + this.getW() - Config.COLLIDER_OFFSET); }
    public int getCollTopTile()   { return LevelMgr.get().pixelToTile(this.getY() + Config.COLLIDER_OFFSET); }
    public int getCollBottomTile() { return LevelMgr.get().pixelToTile(this.getY() + this.getH() - Config.COLLIDER_OFFSET);}

    public Rect getBuffer() { return _buffer; }
    public void setBuffer(Rect newBuffer) { _buffer = newBuffer; }
}