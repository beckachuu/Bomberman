package Systems.State.Play.Inter;

import Obj.BaseObj.ObjProperty;
import Systems.Audio.AudioMgr;
import Systems.Texture.TextureMgr;
import Systems.Input.InputMgr;
import Systems.Level.LevelMgr;
import Systems.State.GameState;
import Systems.State.Menu.Menu;
import Systems.State.StateMgr;

import java.awt.event.KeyEvent;

public class GameOver extends GameState {

    private ObjProperty _imgObj;

    @Override
    public void init() {
        AudioMgr.get().stop(String.valueOf(LevelMgr.get().getCurrLvlID()));
        AudioMgr.get().play("DEFEAT");
        _imgObj = new ObjProperty("YOU_LOST", 0, 0);
    }

    @Override
    public void exit() {
        AudioMgr.get().stop("DEFEAT");
        // Nothing to clear
    }

    @Override
    public void update() {
        // Nothing to update
    }

    @Override
    public void render() {
        TextureMgr.get().drawNoCam(_imgObj);
    }

    @Override
    public void events() {

        // Play again
        if (InputMgr.get().getKL().isKeyPressed(KeyEvent.VK_R)) {
            StateMgr.get().popState();  // Pop Game Over state
            LevelMgr.get().clean();     // Clean old level data
            LevelMgr.get().initNewLevel();  // Reset level
        }

        // Quit to menu
        if (InputMgr.get().getKL().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            StateMgr.get().popState();  // Pop Game Over state
            StateMgr.get().popState();  // Pop Play state
            StateMgr.get().pushState(new Menu());       // Push Menu state
        }
    }
}
