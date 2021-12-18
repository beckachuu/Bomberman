package Systems.State;

public interface IStateMachine {
    void init();
    void exit();
    void update();
    void render();
    void events();
}
