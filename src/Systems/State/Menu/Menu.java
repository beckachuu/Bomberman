package Systems.State.Menu;

import Obj.BaseObj.ObjProperty;
import Obj.Widgets.Button;
import Obj.Widgets.ChangeState;
import Commons.Config;
import Systems.Audio.AudioMgr;
import Systems.Cores.Engine;
import Systems.Cores.Window;
import Systems.Texture.TextureMgr;
import Systems.Input.InputMgr;
import Systems.Level.LevelMgr;
import Systems.State.GameState;
import Systems.State.Play.Play;
import Systems.State.StateMgr;
import Systems.XMLParser.ParseMgr;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Menu extends GameState {

    private final List<ObjProperty> _imgList;
    private final List<Button> _uiList;

    public Menu() {
        _imgList = new ArrayList<>();
        _uiList = new ArrayList<>();
    }

    @Override
    public void init() {

        // Parse menu textures
        ParseMgr.get().parseTextures(Config.MENU_TEXTURE_DIRECTORY);

        // Adding menu textures
        _imgList.add(new ObjProperty("BKG_CLOUD", 0, 0));
        _imgList.add(new ObjProperty("BKG_FOREST", 0, 0));
        _imgList.add(new ObjProperty("GAME_TITLE", TextureMgr.CENTER_X, 0));
        _imgList.add(new ObjProperty("OPTION_BAR", TextureMgr.CENTER_X, TextureMgr.BOTTOM_Y));

        Button playBtn = new Button(
                new ObjProperty(
                        "PLAY_NORMAL",
                        TextureMgr.CENTER_X,
                        Window.get().getHeight() - 450),
                new String[]{"PLAY_NORMAL", "PLAY_HOVER"},
                new ChangeState() {
                    @Override
                    public void changeState() {
                        StateMgr.get().changeState(new Play());
                    }
                });

        Button insBtn = new Button(
                new ObjProperty(
                        "AI_MODE",
                        TextureMgr.CENTER_X,
                        Window.get().getHeight() - 300),
                new String[]{"AI_NORMAL", "AI_HOVER"},
                new ChangeState() {
                    @Override
                    public void changeState() {
                        LevelMgr.get().setAutoPlayer(true);
                        StateMgr.get().changeState(new Play());
                    }
                });

        Button quitBtn = new Button(
                new ObjProperty(
                        "QUIT_NORMAL",
                        TextureMgr.CENTER_X,
                        Window.get().getHeight() - 150),
                new String[]{"QUIT_NORMAL", "QUIT_HOVER"},
                new ChangeState() {
                    @Override
                    public void changeState() {
                        Engine.get().quit();
                    }
                });

        _uiList.add(playBtn);
        _uiList.add(insBtn);
        _uiList.add(quitBtn);

        AudioMgr.get().play("INTRO");

        // Confirmation
        //System.out.println("Menu initialize!");
    }

    @Override
    public void exit() {
        AudioMgr.get().stop("INTRO");
        _uiList.clear();

        // Confirmation
        //System.out.println("Menu exit!");
    }

    @Override
    public void update() {

        events();

        for (Button btn : _uiList) {
            btn.update(0);
            if (_uiList.isEmpty()) {
                break;
            }
        }

        // Confirmation
        //System.out.println("Menu update!");
    }

    @Override
    public void render() {

        for (ObjProperty objImg : _imgList) {
            TextureMgr.get().drawNoCam(objImg);
        }

        for (Button btn : _uiList) {
            btn.draw();
            if (_uiList.isEmpty()) { break; }
        }

        // Confirmation
        //System.out.println("Menu render!");
    }

    @Override
    public void events() {

        if (InputMgr.get().getKL().isKeyPressed(KeyEvent.VK_ENTER)) {
            StateMgr.get().changeState(new Play());
        }

        // Confirmation
        //System.out.println("Menu events!");
    }
}
