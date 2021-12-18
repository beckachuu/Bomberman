package Systems.Collision;

import Obj.BaseObj.GameObject;
import Obj.BaseObj.ObjProperty;
import Systems.Input.InputMgr;
import Obj.DynamicObj.Person;

public class CollisionMgr {

    private static CollisionMgr instance = null;

    private CollisionMgr() {
    }

    public static CollisionMgr get() {
        return instance = (instance != null) ? instance : new CollisionMgr();
    }

    // Check va chạm giữa 2 person
    public boolean checkPersonToPersonCollision(Person p1, Person p2) {
        boolean x_overlays =
                (p1.getCollider().getX() < p2.getCollider().getX() + p2.getCollider().getW())
                        && (p1.getCollider().getX() + p1.getCollider().getW() > p2.getCollider().getX());

        boolean y_overlays =
                (p1.getCollider().getY() < p2.getCollider().getY() + p2.getCollider().getH())
                        && (p1.getCollider().getY() + p1.getCollider().getH() > p2.getCollider().getY());

        return x_overlays && y_overlays ;
    }
    
    // Check va chạm giữa 1 obj với 1 person
    public boolean checkObjectToPersonCollision(GameObject go, Person p) {
        boolean x_overlays =
                (go.getX() < p.getCollider().getX() + p.getCollider().getW())
                        && (go.getX() + go.getWidth() > p.getCollider().getX());

        boolean y_overlays =
                (go.getY() < p.getCollider().getY() + p.getCollider().getH())
                        && (go.getY() + go.getHeight() > p.getCollider().getY());

        return x_overlays && y_overlays ;
    }

    // Check va chạm giữa 2 obj nói chung
    public boolean checkObjectToObjectCollision(GameObject go1, GameObject go2) {
        boolean x_overlays =
                (go1.getX() < go2.getX() + go2.getWidth())
                        && (go1.getX() + go1.getWidth() > go2.getX());

        boolean y_overlays =
                (go1.getY() < go2.getY() + go2.getHeight())
                        && (go1.getY() + go1.getHeight() > go2.getY());

        return x_overlays && y_overlays ;
    }

    public boolean checkMouseCollision(ObjProperty props) {
        int x_mouse = InputMgr.get().getML().x;
        int y_mouse = InputMgr.get().getML().y;

        boolean x_surround = x_mouse > props.x && x_mouse < props.x + props.w;
        boolean y_surround = y_mouse > props.y && y_mouse < props.y + props.h;
        return x_surround && y_surround;
    }

}
