package Systems.State;

import java.util.ArrayList;
import java.util.List;

public class StateMgr {

    private static StateMgr instance;
    private final List<GameState> gameStates;

    private StateMgr() {
        gameStates = new ArrayList<>();
    }

    public static StateMgr get() {
        return instance = (instance != null)? instance : new StateMgr();
    }

    public void pushState(GameState gs) {
        gameStates.add(gs);
        gameStates.get(gameStates.size() - 1).init();
    }

    public void popState() {
        if (!gameStates.isEmpty()) {
            gameStates.get(gameStates.size() - 1).exit();
            gameStates.remove(gameStates.size() - 1);
        }
    }

    public void changeState(GameState gs) {

        if (!gameStates.isEmpty()) {

            GameState currState = gameStates.get(gameStates.size() - 1);

            if (gs.getClass().equals(currState.getClass())) {
                return;
            }

            gameStates.get(gameStates.size() - 1).exit();
            gameStates.remove(gameStates.size() - 1);
        }

        gameStates.add(gs);
        gameStates.get(gameStates.size() - 1).init();
    }

    public void update() {
        if (!gameStates.isEmpty()) {
            gameStates.get(gameStates.size() - 1).update();
        }
    }

    public void render() {
        if (!gameStates.isEmpty()) {
            gameStates.get(gameStates.size() - 1).render();
        }
    }

    public void events() {
        if (!gameStates.isEmpty()) {
            gameStates.get(gameStates.size() - 1).events();
        }
    }

    public List<GameState> getGameStates() {
        return gameStates;
    }
}
