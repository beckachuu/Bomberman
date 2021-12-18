package Systems.Cores;

import Commons.Config;
import Systems.Input.InputMgr;

import javax.swing.*;

public class Window extends JFrame {

    private static Window instance;

    public static Window get() {
        return instance = (instance != null)? instance : new Window();
    }

    private Window() {
    }

    public void init() {
        this.addKeyListener(InputMgr.get().getKL());
        this.addMouseListener((InputMgr.get().getML()));
        this.addMouseMotionListener((InputMgr.get().getML()));

        this.setSize(Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);
        //this.pack();
        this.setTitle(Config.GAME_TITLE);
        this.setResizable(false);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
