package Systems.Input;

public class InputMgr {

    private static InputMgr _instance = null;

    private KL keyListener;
    private ML mouseListener;

    private InputMgr() {
        keyListener = new KL();
        mouseListener = new ML();
    }

    public static InputMgr get() {
        return _instance = (_instance != null) ? _instance : new InputMgr();
    }

    public KL getKL() {
        return keyListener;
    }
    public ML getML() {
        return mouseListener;
    }
}
