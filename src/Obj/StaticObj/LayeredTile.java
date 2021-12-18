package Obj.StaticObj;

import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Obj.StaticObj.Item.Item;
import Obj.StaticObj.SpecificTile.Brick;

import java.util.ArrayList;
import java.util.List;

public class LayeredTile extends Tile {

    protected final List<Tile> _tileList = new ArrayList<>();

    public LayeredTile(ObjProperty props, Tile[] tlList) {
        super(props);
        if (tlList.length == 1) {
            _tileList.add(tlList[0]);
        } else {
            for (Tile tl : tlList) {

                if (tl == null) { continue; }

                if (tl instanceof Brick) {
                    _tileList.add(0, tl);

                } else if (tl instanceof Item) {
                    _tileList.add(1, tl);

                } else {
                    _tileList.add(tl);
                }
            }
        }
    }


    @Override
    public void draw() {

        if (_tileList.get(0) instanceof Item) {
            _tileList.get(1).draw();
        }

        // Lấy tile trên cùng để vẽ
        _tileList.get(0).draw();
    }


    @Override
    public void update(float dt) {

        // Flag _removed tile trên cùng = true
        if (_tileList.get(0).isRemoved()) {
            _tileList.remove(0);
        }

        // Lấy tile trên cùng để update
        _tileList.get(0).update(dt);
    }


    @Override
    public boolean collide(GameObject go) {

        // Lấy tile trên cùng để xử lý va chạm
        return _tileList.get(0).collide(go);
    }

    public Tile getTopTile() {
        return _tileList.get(0);
    }
}
