package Systems.Input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KL extends KeyAdapter implements KeyListener {

    private boolean[] keyPressed = new boolean[128];

    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;
    public boolean space;

    public void update() {
        up = keyPressed[KeyEvent.VK_UP] || keyPressed[KeyEvent.VK_W];
        down = keyPressed[KeyEvent.VK_DOWN] || keyPressed[KeyEvent.VK_S];
        left = keyPressed[KeyEvent.VK_LEFT] || keyPressed[KeyEvent.VK_A];
        right = keyPressed[KeyEvent.VK_RIGHT] || keyPressed[KeyEvent.VK_D];
        space = keyPressed[KeyEvent.VK_SPACE];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyPressed[e.getKeyCode()] = false;
    }

    public boolean isKeyPressed(int keyCode) {
        return keyPressed[keyCode];
    }
}

