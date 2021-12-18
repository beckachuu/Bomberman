package Systems.State;

public abstract class GameState implements IStateMachine {

    public abstract void init();
    public abstract void exit();
    public abstract void update();
    public abstract void render();
    public abstract void events();
}
