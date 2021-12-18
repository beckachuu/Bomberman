package Systems.XMLParser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Commons.Rect;
import Components.Animation.FrameAnimation;
import Systems.AISystem.MapDetail;
import Systems.Audio.AudioMgr;
import Systems.Level.LevelMgr;
import Systems.Texture.TextureMgr;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class ParseMgr {

    private char[][] _originalMap;

    private static ParseMgr instance;

    private ParseMgr() {
    }

    public static ParseMgr get() {
        return instance = (instance != null) ? instance : new ParseMgr();
    }

    public void parseAudio(String filePath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            doc.getDocumentElement().normalize();

            // Kiểm tra element gốc
            // System.out.println("Root Element :" +
            // doc.getDocumentElement().getNodeName());

            NodeList groupList = doc.getElementsByTagName("group");
            for (int g = 0; g < groupList.getLength(); ++g) {

                Node groupNode = groupList.item(g);

                if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element groupElem = (Element) groupNode;

                    String type = groupElem.getAttribute("type");
                    String commonSrc = groupElem.getAttribute("commonSource");

                    NodeList auList = groupElem.getElementsByTagName("audio");
                    for (int au = 0; au < auList.getLength(); ++au) {
                        Node auNode = auList.item(au);

                        if (auNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element auElem = (Element) auNode;

                            String id = auElem.getAttribute("id");
                            String src = commonSrc + auElem.getAttribute("fileName");

                            MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File(src).toURI().toString()));
                            if (type.equals("BKG")) {
                                mediaPlayer.setOnEndOfMedia(new Runnable() {
                                    @Override
                                    public void run() {
                                        mediaPlayer.seek(Duration.ZERO);
                                    }
                                });
                                mediaPlayer.setVolume(0.5f);
                            } else if (type.equals("SFX")) {
                                mediaPlayer.setOnEndOfMedia(new Runnable() {
                                    @Override
                                    public void run() {
                                        mediaPlayer.stop();
                                    }
                                });
                            }
                            AudioMgr.get().getAudioMap().put(id, mediaPlayer);
                        }
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void parseTextures(String filePath) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            doc.getDocumentElement().normalize();

            // Kiểm tra element gốc
            // System.out.println("Root Element :" +
            // doc.getDocumentElement().getNodeName());

            NodeList groupList = doc.getElementsByTagName("group");
            for (int g = 0; g < groupList.getLength(); ++g) {

                Node groupNode = groupList.item(g);

                if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element groupElem = (Element) groupNode;

                    String commonSrc = groupElem.getAttribute("commonSource");

                    NodeList textrList = groupElem.getElementsByTagName("texture");
                    for (int tt = 0; tt < textrList.getLength(); ++tt) {
                        Node textrNode = textrList.item(tt);

                        if (textrNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element textrElem = (Element) textrNode;

                            String id = textrElem.getAttribute("id");
                            String src = commonSrc + textrElem.getAttribute("fileName");

                            try {
                                BufferedImage bfImg = ImageIO.read(new File(src));
                                TextureMgr.get().getTextureMap().put(id, bfImg);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            // Checking id and source
                            // System.out.println("ID: " + id + "Source: " + source);

                        }
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void parseLevelMap(String mapInfoPath) {

        // Khởi tạo Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(mapInfoPath));

            doc.getDocumentElement().normalize();

            // Texture
            this.parseTextures(mapInfoPath);

            // Map
            Node mapNode = doc.getElementsByTagName("map").item(0); // There's only 1 map
            if (mapNode.getNodeType() == Node.ELEMENT_NODE) {

                Element mapElement = (Element) mapNode;

                // Lấy thuộc tính bên trong các element
                int size = Integer.parseInt(mapElement.getAttribute("tileSize"));
                LevelMgr.get().setLvlTileSize(size);

                int cols = Integer.parseInt(mapElement.getAttribute("tileWidth"));
                LevelMgr.get().setLvlTileWidth(cols);

                int rows = Integer.parseInt(mapElement.getAttribute("tileHeight"));
                LevelMgr.get().setLvlTileHeight(rows);

                NodeList dataList = mapElement.getElementsByTagName("data");
                String data = dataList.item(0).getTextContent().replace("\n", "");

                char[][] originalMap = new char[rows][cols];
                for (int i = 0; i < data.length(); ++i) {
                    originalMap[i / cols][i % cols] = data.charAt(i);
                }
                LevelMgr.get().setOriginalMap(originalMap);
                MapDetail.setMapDetail(originalMap);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String, FrameAnimation.Sequence> parseAnimeData(String filePath) {

        Map<String, FrameAnimation.Sequence> seqMap = new HashMap<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            doc.getDocumentElement().normalize();
            // System.out.println("Root Element :" +
            // doc.getDocumentElement().getNodeName());

            NodeList seqList = doc.getElementsByTagName("sequence");

            for (int sq = 0; sq < seqList.getLength(); ++sq) {

                Node seqNode = seqList.item(sq);
                if (seqNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element seqElement = (Element) seqNode;

                    String seqId = seqElement.getAttribute("id");
                    int frCount = Integer.parseInt(seqElement.getAttribute("frameCount"));
                    long dt = (long) (Integer.parseInt(seqElement.getAttribute("speed")) * 1E6);
                    int w = Integer.parseInt(seqElement.getAttribute("width"));
                    int h = Integer.parseInt(seqElement.getAttribute("height"));
                    int bufX = Integer.parseInt(seqElement.getAttribute("bufferX"));
                    int bufY = Integer.parseInt(seqElement.getAttribute("bufferY"));
                    int bufW = Integer.parseInt(seqElement.getAttribute("bufferW"));
                    int bufH = Integer.parseInt(seqElement.getAttribute("bufferH"));
                    Rect buf = new Rect(bufX, bufY, bufW, bufH);

                    List<String> list = new ArrayList<>();
                    NodeList frameList = seqElement.getElementsByTagName("frame");
                    for (int fr = 0; fr < frameList.getLength(); ++fr) {
                        Node frNode = frameList.item(fr);
                        if (frNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element frElement = (Element) frNode;
                            String textureID = frElement.getAttribute("textureID");
                            list.add(textureID);
                        }
                    }

                    FrameAnimation.Sequence seq = new FrameAnimation.Sequence(w, h, dt, frCount, buf, list);
                    seqMap.put(seqId, seq);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return seqMap;
    }
}
