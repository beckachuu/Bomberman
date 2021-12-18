package Systems.Camera;

import Obj.BaseObj.ObjProperty;
import Systems.Cores.Window;
import Obj.BaseObj.GameObject;
import Commons.Rect;
import Components.Physics.Vector2D;

/**
 * System thiết lập góc nhìn của Player để môi trường chạy theo Player
 */
public class Camera {

    private static Camera instance = null;

    private GameObject _player;
    private Vector2D _currentPos;
    private Rect _viewBox;

    private int _levelWidth;
    private int _levelHeight;

    private Camera() {
        _player = null;
        _currentPos = new Vector2D();
        _viewBox = new Rect(0.0f, 0.0f, Window.get().getWidth(), Window.get().getHeight());
        _levelWidth = 0;
        _levelHeight = 0;
    }

    public static Camera get() {
        return instance = (instance != null)? instance : new Camera();
    }

    public void draw() {
        // ...
    }

    public void trackPlayer() {
        if (_player != null) {
            // Chỉ bắt đầu chạy Systems.Camera <=> đã đi hết 1/2 màn hình đầu tiên
            _viewBox.x = _player.getOriginX() - _viewBox.w/2;
            _viewBox.y = _player.getOriginY() - _viewBox.h/2;

            // Obj vẫn đang nằm trong 1/2 màn hình đầu tiên
            if (_viewBox.x < 0) { _viewBox.x = 0; }
            if (_viewBox.y < 0) { _viewBox.y = 0; }

            // Obj đã đi tới góc màn hình cuối cùng, Systems.Camera ngừng chạy
            if (_viewBox.x > _levelWidth - _viewBox.w) { _viewBox.x = _levelWidth - _viewBox.w; }
            if (_viewBox.y > _levelHeight - _viewBox.h) { _viewBox.y = _levelHeight - _viewBox.h; }

            // Lưu lại vị trí của Systems.Camera dưới dạng Vector2D
            _currentPos.x = _viewBox.x;
            _currentPos.y = _viewBox.y;
        }
    }

    public Vector2D syncObject(ObjProperty props) {
        float targetX = props.x - _currentPos.x;
        float targetY = props.y - _currentPos.y;
        return new Vector2D(targetX, targetY);
    }

    public Vector2D syncRect(Rect r) {
        float targetX = r.x - _currentPos.x;
        float targetY = r.y - _currentPos.y;
        return new Vector2D(targetX, targetY);
    }

    public GameObject getPlayer() { return _player; }
    public Vector2D getCurrentPos() { return _currentPos; }
    public void setTarget(GameObject target) { _player = target; }
    public void setLevelSize(int lw, int lh) { _levelWidth = lw; _levelHeight = lh; }
}
