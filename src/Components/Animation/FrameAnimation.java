package Components.Animation;

import Commons.Rect;
import Obj.BaseObj.ObjProperty;
import Systems.Texture.TextureMgr;
import Systems.XMLParser.ParseMgr;

import java.util.*;

/**
 * SeqAnimation là animation được tạo từ các ảnh độc lập
 * được ghép với nhau 1 chuỗi hành động liên tiếp.
 * <p>
 * SeqAnimation được quản lý bới 2 file XML :
 * - 1 file XML quản lý texture, lưu ID và đường dẫn đến từng frame độc lập.
 * - 1 file XML quản lý các thông số của các kiểu hành động của
 */
public class FrameAnimation {

    public static class Sequence {

        public int widthFrame;
        public int heightFrame;
        public long nanoSpeed;
        public int frameCount;
        public Rect collideBuffer;
        public List<String> frameTextureIDs;

        public Sequence(int w, int h, long dt, int frCount, Rect colBuf, List<String> list) {
            this.widthFrame = w;
            this.heightFrame = h;
            this.nanoSpeed = dt;
            this.frameCount = frCount;
            this.collideBuffer = colBuf;
            this.frameTextureIDs = list;
        }
    }

    private boolean _repeated;              // Flag kiểm tra Animation của Obj có loop liên tục?
    private boolean _ended;                 // Flag kiểm tra đã loop đủ 1 vòng Animation chưa?
    private long _refresh;                   // Mốc thời gian để reset frame của animation về 0
    private int _currFrame;                 // Frame hiện tại của Animation
    private ObjProperty _objProps;          // ObjProperty của Obj
    private Sequence _currSeq;              // Sequence hiện tại đang sử dụng
    private final Map<String, Sequence> _seqMap;  // Components.XMLParser quản lý tất cả các Sequence mà Obj có thể
    // sử dụng

    public FrameAnimation(String animeFilePath, ObjProperty props) {
        _repeated = true;
        _ended = false;
        _currFrame = 0;
        _objProps = props;
        _refresh = 0;

        _seqMap = ParseMgr.get().parseAnimeData(animeFilePath);
        _currSeq = (Sequence)_seqMap.values().toArray()[0];  // Seq đầu tiên trong HashMap, thường là Idle

        _objProps.w = _currSeq.widthFrame;
        _objProps.h = _currSeq.heightFrame;
    }

    public void drawAnime() {
        TextureMgr.get().draw(_objProps);
    }

    public void updateAnime() {

        // Animation vẫn đang loop liên tục và Animation đó chưa kết thúc
        if (_repeated == true || _ended == false) {
            _ended = false;     // Vẫn cho animation đó chưa kết thúc

            _currFrame = (int)((System.nanoTime() - _refresh) / _currSeq.nanoSpeed) % _currSeq.frameCount;
        }

        // Nếu frame hiện tại của Obj = frame cuối cùng và Animation không còn loop liên tục
        if (_repeated == false && _currFrame == _currSeq.frameCount - 1) {
            _ended = true;      // Cho Animation đó kết thúc
            _currFrame = _currSeq.frameCount - 1;  // Cho frame hiện tại = frame cuối cùng của nó
        }

        _objProps.textureID = _currSeq.frameTextureIDs.get(_currFrame);
    }

    public void setCurrentSeq(String seqID) {
        if (!_seqMap.isEmpty()) {
            _currSeq = _seqMap.get(seqID);
            _objProps.w = _currSeq.widthFrame;
            _objProps.h = _currSeq.heightFrame;
            return;
        }
        System.err.println("The given sequence animation is not matching: " + seqID);
    }

    public void setEnded(boolean ended) { this._ended = ended; }
    public boolean isEnded() {
        return _ended;
    }

    public void setRepeated(boolean repeat) {
        this._repeated = repeat;
    }
    public boolean isRepeated() {
        return _repeated;
    }

    public void refreshFrame() { _refresh = System.nanoTime(); }
    public Rect getCurrSeqCollBuffer() { return _currSeq.collideBuffer; }
}
