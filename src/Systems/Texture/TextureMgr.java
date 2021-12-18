package Systems.Texture;

import Obj.BaseObj.ObjProperty;
import Systems.Camera.Camera;
import Components.Physics.Vector2D;
import Systems.Cores.Window;
import Systems.Level.LevelMgr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý việc load và in hình ảnh
 */
public class TextureMgr {

    public static final int CENTER_X = -10;
    public static final int CENTER_Y = -11;
    public static final int BOTTOM_X = -12;
    public static final int BOTTOM_Y = -13;
    public static final int TOP_X = -14;
    public static final int TOP_Y = -15;

    private void normalize(ObjProperty props) {
        if (props.x == CENTER_X) {
           props.x = (Window.get().getWidth() - props.w) / 2;
        }

        if (props.y == BOTTOM_Y) {
            props.y = Window.get().getHeight() - props.h;
        }
    }

    private static TextureMgr instance = null;
    private Graphics g = null;
    private final Map<String, BufferedImage> textureMap = new HashMap<>();

    private TextureMgr() {
    }

    public static TextureMgr get() {
        return instance = (instance != null) ? instance : new TextureMgr();
    }

    public void draw(ObjProperty props) {
        Vector2D dst = Camera.get().syncObject(props);
        g.drawImage(textureMap.get(props.textureID),
                (int)dst.x, (int)dst.y,
                props.w, props.h, null);
    }

/*
    public void drawBorder(Rect box) {
        Vector2D dst = Camera.get().syncRect(box);
        g.drawRect((int)dst.x, (int)dst.y, (int)box.w, (int)box.h);
    }
*/

    public void drawNoCam(ObjProperty props) {
        normalize(props);
        g.drawImage(textureMap.get(props.textureID),
                (int)props.x, (int)props.y, props.w, props.h,
                null);
    }

    public void resetGraphics(Graphics g) {
        this.g = g;
    }
    public Graphics getGraphics() { return g; }

    public void erase(String id) {
        textureMap.remove(id);
    }
    public void clean() {
        textureMap.clear();
    }

    public Map<String, BufferedImage> getTextureMap() { return textureMap; }

    public int getTextureWidth(String id) {
        return textureMap.containsKey(id)? textureMap.get(id).getWidth() : LevelMgr.get().getLvlTileSize();
    }
    public int getTextureHeight(String id) {
        return textureMap.containsKey(id)? textureMap.get(id).getHeight() : LevelMgr.get().getLvlTileSize();
    }
}
