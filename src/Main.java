import Systems.Cores.Engine;
import Systems.Timer.Timer;

public class Main {

    public static void main(String[] args) {

        Engine.get().init();

        while(Engine.get().isRunning())
        {
            Engine.get().events();
            Engine.get().update();
            Engine.get().render();
            Timer.get().ticking();
        }

        Engine.get().clean();
    }

}
