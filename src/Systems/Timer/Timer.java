package Systems.Timer;

import Commons.Config;

/**
 * LƯU Ý MỘT SỐ LOẠI THỜI GIAN TRONG GAME:
 * - DELTA_TIME cho Components.Physics: (double), tính bằng giây.
 *  CHỈ ĐƯỢC DÙNG ĐỂ TÍNH TOÁN CHO CÁC PHÉP BIẾN ĐỔI VẬT LÝ, KHÔNG LIÊN QUAN TỚI ANIMATION
 *
 * - DELTA_TIME cho Animation: (int), tính theo mili giây,
 *  Khoảng thời gian giữa 2 frame trong 1 animation nhiều frame.
 *
 * - FRAME_TIME : (int), tính bằng frame,
 * Thời gian đếm ngược về 0 để thực hiện một hành động nào đó.
 * Quy đổi ra thời gian, giây theo công thức:
 *          Tổng số frame * (1/60)  = t
 */
public class Timer {

    private static Timer _instance = null;
    private float _deltaTime = 0.0f;
    private float _lastTime = 0.0f;

    // Update
    public void ticking() {
        _deltaTime = (System.nanoTime() - _lastTime)*Config.FRAME_PER_MILLISECOND;
        if (_deltaTime > Config.MAX_DELTA_TIME) {
            _deltaTime = Config.MAX_DELTA_TIME;
        }
        _lastTime = System.nanoTime();

        try {
            Thread.sleep((long) Config.MILLISECOND_PER_FRAME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Timer get() {
        return _instance = (_instance != null) ? _instance : new Timer();
    }
    public float getDeltaTime() { return _deltaTime; }
}

