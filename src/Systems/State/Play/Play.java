package Systems.State.Play;

import Commons.Config;
import Obj.BaseObj.ObjProperty;
import Obj.Widgets.Button;
import Obj.Widgets.ChangeState;
import Systems.Input.InputMgr;
import Systems.Level.LevelMgr;
import Systems.State.GameState;
import Systems.State.Menu.Menu;
import Systems.State.StateMgr;
import Systems.XMLParser.ParseMgr;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Play extends GameState {

    private final List<Button> _btnList;

    public Play() {
        _btnList = new ArrayList<>();
    }

    @Override
    public void init() {

        // Parse textures in Play
        ParseMgr.get().parseTextures(Config.PLAY_TEXTURE_DIRECTORY);

        // Set data for LevelMgr, starting at level 1
        LevelMgr.get().initNewLevel();

        // Set the button
        Button menuBtn = new Button(
                new ObjProperty("CHANGE_LATER", 10, 10),
                new String[]{"HOME_NORMAL", "HOME_HOVER"},
                new ChangeState() {
                    @Override
                    public void changeState() {
                        openMenu();
                    }
                });

        Button pauseBtn = new Button(
                new ObjProperty("CHANGE_LATER", 70, 10),
                new String[]{"PAUSE_NORMAL", "PAUSE_HOVER"},
                new ChangeState() {
                    @Override
                    public void changeState() {
                        pauseGame();
                    }
                });

        Button optionBtn = new Button(
                new ObjProperty("CHANGE_LATER", 130, 10),
                new String[]{"OPTION_NORMAL", "OPTION_HOVER"},
                new ChangeState() {
                    @Override
                    public void changeState() {
                        options();
                    }
                });

        _btnList.add(menuBtn);
        _btnList.add(pauseBtn);
        _btnList.add(optionBtn);
        // Confirmation
        //System.out.println("Play init!");
    }

    @Override
    public void exit() {
        _btnList.clear();
        LevelMgr.get().clean();

        // Confirmation
        //System.out.println("Play exit!");
    }

    @Override
    public void update() {
        events();
        for (Button b : _btnList) {
            b.update(0);
            if (_btnList.isEmpty()) { break;}
        }

        LevelMgr.get().update();
    }

    @Override
    public void render() {
        for (Button b : _btnList) {
            b.draw();
            if (_btnList.isEmpty()) { break;}
        }

        LevelMgr.get().render();
    }

    @Override
    public void events() {
        InputMgr.get().getKL().update();

        if (InputMgr.get().getKL().isKeyPressed(KeyEvent.VK_P)) {
            StateMgr.get().pushState(new Pause());  // Push, not Change
        }

        if (InputMgr.get().getKL().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            StateMgr.get().changeState(new Menu());
        }
    }

    private void openMenu() {
        //
    }

    private void options() {
        //
    }

    private void pauseGame() {
        //
    }
}
