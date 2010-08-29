package im.crate.zbridge.clutter;

import com.stickycoding.rokon.DrawPriority;
import com.stickycoding.rokon.RokonActivity;

public class ClutterActivity extends RokonActivity {
	public static final float GAME_WIDTH = 480f;
    public static final float GAME_HEIGHT = 320f;

    private GameScene scene;

    public void onCreate() {
        debugMode();
        forceFullscreen();
        forceLandscape();
        setGameSize(GAME_WIDTH, GAME_HEIGHT);
        setDrawPriority(DrawPriority.PRIORITY_VBO);
        setGraphicsPath("textures/");
        createEngine();
    }

    public void onLoadComplete() {
        setScene(scene = new GameScene());
    }
}