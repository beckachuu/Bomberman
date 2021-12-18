package Obj.StaticObj.SpecificTile;

import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.StaticObj.Tile;

public class Grass extends Tile {
    public Grass(ObjProperty props) {
        super(props);
    }

    @Override
    public boolean collide(GameObject go) {
        // Mặc định KHÔNG XẢY RA VA CHẠM VỚI GRASS
        return false;
    }
}
