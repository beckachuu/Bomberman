package Obj.BaseObj;

import Systems.Texture.TextureMgr;

public class ObjProperty {

    public String textureID;
    public float x;
    public float y;
    public int w;
    public int h;

    public ObjProperty(String textureID, float x, float y) {
        this.textureID = textureID;
        this.x = x;
        this.y = y;
        this.w = TextureMgr.get().getTextureWidth(textureID);
        this.h = TextureMgr.get().getTextureHeight(textureID);
    }

}
