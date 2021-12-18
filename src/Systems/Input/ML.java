package Systems.Input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ML extends MouseAdapter implements MouseListener {

    public boolean isPressed = false;
    public int x = 0;
    public int y = 0;

    @Override
    public void mousePressed(MouseEvent e) {
        isPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isPressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.x = e.getX();
        this.y = e.getY();
    }
}

