package Systems.Cores;

import Systems.Audio.AudioMgr;
import Systems.Texture.TextureMgr;
import Systems.State.Menu.Menu;
import Systems.State.StateMgr;

import java.awt.image.BufferStrategy;

public class Engine {

    private static Engine _instance = null;
    private boolean _running = false;

    private Engine() {
    }

    public static Engine get() {
        return _instance = (_instance != null)? _instance : new Engine();
    }

    public void init() {
        Window.get().init();
        AudioMgr.get().init();
        StateMgr.get().pushState(new Menu());
        _running = true;
    }

    public void update() {
        StateMgr.get().update();
    }

    public void render() {

        BufferStrategy bs = Window.get().getBufferStrategy();
        if (bs == null) {
            Window.get().createBufferStrategy(3);
            return;
        }
        TextureMgr.get().resetGraphics(bs.getDrawGraphics());
        StateMgr.get().render();
        bs.getDrawGraphics().dispose();
        bs.show();
/*
        Image dbImage = Window.get().createImage(Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);
        Graphics dbg = dbImage.getGraphics();
        TextureMgr.get().resetGraphics(dbg);
        StateMgr.get().render();
        Window.get().getGraphics().drawImage(dbImage, 0, 0, Window.get());
 */
    }

    public void events() {
        StateMgr.get().events();
    }

    public void clean() {
        TextureMgr.get().clean();
        AudioMgr.get().clean();
        System.exit(0);
    }

    public boolean isRunning() {
        return _running;
    }
    public void quit() {
        _running = false;
    }
}
