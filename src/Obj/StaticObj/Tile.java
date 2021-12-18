package Obj.StaticObj;

import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Systems.Texture.TextureMgr;

public abstract class Tile extends GameObject {

    public Tile(ObjProperty props) {
        super(props);
    }

    @Override
    public void draw() {
        TextureMgr.get().draw(_objProps);
    }

    @Override
    public void update(float dt) {
        //
    }


    @Override
    public boolean collide(GameObject go) {
        // Mặc định CÓ XẢY RA VA CHẠM với bất cứ tile
        return true;
    }
}
