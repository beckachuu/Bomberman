package Obj.Widgets;

import Obj.BaseObj.ObjProperty;
import Systems.Audio.AudioMgr;
import Systems.Collision.CollisionMgr;
import Systems.Texture.TextureMgr;
import Systems.Input.InputMgr;

public class Button {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_HOVER = 1;
    private static final int STATE_PRESSED = 2;

    private boolean _inside;
    private ObjProperty _objProp;
    private boolean _released;
    private String[] _textureIDList;
    private ChangeState _csFunc;

    public Button(ObjProperty props, String[] txtIDs, ChangeState func) {
        _objProp = props;
        _textureIDList = txtIDs;
        _csFunc = func;

        props.textureID = _textureIDList[STATE_NORMAL];
        props.w = TextureMgr.get().getTextureWidth(props.textureID);
        props.h = TextureMgr.get().getTextureHeight(props.textureID);
    }

    public void draw() {
        TextureMgr.get().drawNoCam(_objProp);
    }

    public void update(float dt) {
        if (CollisionMgr.get().checkMouseCollision(_objProp)) {
            if (!_inside) {
                AudioMgr.get().play("BUTTON_HOVER");
            }
            _inside = true;

            if (InputMgr.get().getML().isPressed && _released) {
                _csFunc.changeState();
                _released = false;
                //_objProp.textureID = _textureIDList.get(STATE_PRESSED);
            } else if (!InputMgr.get().getML().isPressed) {
                _released = true;
                _objProp.textureID = _textureIDList[STATE_HOVER];
            }
        } else {
            _inside = false;
            _objProp.textureID = _textureIDList[STATE_NORMAL];
        }
    }
}
