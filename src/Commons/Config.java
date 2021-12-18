package Commons;

public class Config {

    public static final int[] addX = { -1, 0, 1, 0 };
    public static final int[] addY = { 0, -1, 0, 1 };

    // Timer
    public static final int FPS = 60;
    public static final float FRAME_PER_MILLISECOND = FPS / 1000.0f;
    public static final float MILLISECOND_PER_FRAME = 1000.0f / FPS;
    public static final float MAX_DELTA_TIME = 1.5f;

    // Components.Physics
    public static final float RIGID_BODY_DEFAULT_UNIT_MASS = 1.0f;
    public static final float RIGID_BODY_DEFAULT_GRAVITY_ACCEL = 9.8f;

    public static final float TRANSFORM_DEFAULT_SCALE_X = 1.0f;
    public static final float TRANSFORM_DEFAULT_SCALE_Y = 1.0f;
    public static final float TRANSFORM_DEFAULT_SYNC_RATIO = 1.0f;
    public static final float TRANSFORM_DEFAULT_ROTATION = 0.0f;

    // Direction
    public static final String DIRECTION_NONE = "_NONE";
    public static final String DIRECTION_UP = "_UP";
    public static final String DIRECTION_DOWN = "_DOWN";
    public static final String DIRECTION_LEFT = "_LEFT";
    public static final String DIRECTION_RIGHT = "_RIGHT";
    public static final String DIRECTION_HORIZONTAL = "_HORIZONTAL";
    public static final String DIRECTION_VERTICAL = "_VERTICAL";

    // Window
    public static final String GAME_TITLE = "Bomberman-ECS";
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 862; // 13 * 64 + 30

    // CHANGE LATER
    public static final int TILE_SIZE = 64;

    // Directory XML File
    public static final int START_LEVEL_ID = 1;

    // Audio
    public static final String AUDIO_DIRECTORY = "res/xml/audio/audio.xml";

    // Menu
    public static final String MENU_TEXTURE_DIRECTORY = "res/xml/common/menu_textures.xml";

    // Play
    public static final String PLAY_TEXTURE_DIRECTORY = "res/xml/common/play_textures.xml";

    // Level
    public static final String LEVEL_MAP_DIRECTORY = "res/xml/level/"; // + "level{1,2,3}.xml"

    // Animation
    public static final String BOMBER_ANIME_DIRECTORY = "res/xml/anime/bomber_anime.xml";
    public static final String DUMMY_ANIME_DIRECTORY = "res/xml/anime/dummy_anime.xml";
    public static final String SKELETON_ANIME_DIRECTORY = "res/xml/anime/skeleton_anime.xml";
    public static final String SHAMAN_ANIME_DIRECTORY = "res/xml/anime/shaman_anime.xml";
    public static final String BOSS_ANIME_DIRECTORY = "res/xml/anime/boss_anime.xml";
    public static final String BOMB_ANIME_DIRECTORY = "res/xml/anime/bomb_anime.xml";
    public static final String TILE_ANIME_DIRECTORY = "res/xml/anime/tile_anime.xml";

    // Offsets
    public static final int CHARACTER_START_OFFSET_X = 1;
    public static final int CHARACTER_START_OFFSET_Y = 1;

    public static final int AUTO_REPOSITION_X = 1;
    public static final int AUTO_REPOSITION_Y = 1;

    // Bomber
    public static final float RIGID_BODY_GRAVITY_ACCEL = 0.0f;
    public static final float BOMBER_DEFAULT_RUN_FORCE = 2.0f;
    public static final float BOMBER_DELTA_TIME = 0.35f;

    public static final int BOMBER_DEFAULT_COOL_DOWN_TIME = 180; // 180 FRAMES ~ 3 seconds
    public static final int BOMBER_DEFAULT_CURRENT_BOMB_RATE = 1;
    public static final int BOMBER_DEFAULT_BOMB_RADIUS = 1;

    // Skeleton
    public static final float SKELETON_RUN_FORCE = 1.5f;
    public static final int SKELETON_RESET_DIRECTION_TIME = 60;

    // Shaman
    public static final float SHAMAN_RUN_FORCE = 1.5f;

    // Bomb
    public static final int BOMB_COUNT_DOWN_TIME = 60; // 60 FRAMES, 1 SECONDS
    public static final int BOMB_AFTER_EXP_TIME = 10; // 10 FRAMES, 1/6 SECONDS

    // Brick
    public static final int BRICK_TIME_AFTER_DESTROY = 30; // 60 FRAMES ~ 1 SECOND

    // Item
    public static final int ITEM_BOMB_RATE = BOMBER_DEFAULT_CURRENT_BOMB_RATE + 1;
    public static final int ITEM_BOMB_COOL_DOWN = BOMBER_DEFAULT_COOL_DOWN_TIME - 60;
    public static final int ITEM_FLAME_RADIUS = BOMBER_DEFAULT_BOMB_RADIUS + 1;
    public static final float ITEM_RUN_FORCE = BOMBER_DEFAULT_RUN_FORCE * 2;

    public static final int ITEM_DURATION_USAGE = 5000;
    public static final int ITEM_DURATION_ON_MAP = 5000;

    // Collider
    public static final int COLLIDER_OFFSET = 1;
}