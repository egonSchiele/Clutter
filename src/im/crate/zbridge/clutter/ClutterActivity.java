package im.crate.zbridge.clutter;

import android.graphics.Typeface;
import android.util.Log;

import com.stickycoding.rokon.DrawPriority;
import com.stickycoding.rokon.RokonActivity;

public class ClutterActivity extends RokonActivity {
	public static final float GAME_WIDTH = 480f;
	public static final float GAME_HEIGHT = 320f;

	private GameScene scene;
	private Typeface mType;

	public void onCreate() {
		mType = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		debugMode();
		forceFullscreen();
		forceLandscape();
		setGameSize(GAME_WIDTH, GAME_HEIGHT);
		setDrawPriority(DrawPriority.PRIORITY_VBO);
		setGraphicsPath("textures/");
		createEngine();
		Log.d("Clutter", "Engine started");
	}

	public void onLoadComplete() {
		Log.d("Clutter", "onLoadComplete called");
		Textures.load();
		setScene(scene = new GameScene(mType));
	}
}