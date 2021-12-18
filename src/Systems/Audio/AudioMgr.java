package Systems.Audio;

import java.util.HashMap;
import java.util.Map;

import Commons.Config;
import Systems.XMLParser.ParseMgr;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.MediaPlayer;

public class AudioMgr {
    private static AudioMgr _instance;
    private final JFXPanel fxPanel = new JFXPanel();
    private Map<String, MediaPlayer> _audioMap;

    private AudioMgr() {
        _audioMap = new HashMap<>();
    }

    public static AudioMgr get() {
        return _instance = (_instance != null) ? _instance : new AudioMgr();
    }

    public void init() {
        ParseMgr.get().parseAudio(Config.AUDIO_DIRECTORY);
    }

    public void play(String id) {
        _audioMap.get(id).play();
    }

    public void pause(String id) {
        _audioMap.get(id).pause();
    }

    public void stop(String id) {
        _audioMap.get(id).stop();
    }

    public void setVolume(String id, double vol) {
        _audioMap.get(id).setVolume(vol);
    }

    public void clean() {
        _audioMap.clear();
    }

    public Map<String, MediaPlayer> getAudioMap() {
        return _audioMap;
    }
}
