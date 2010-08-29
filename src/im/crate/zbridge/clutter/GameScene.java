package im.crate.zbridge.clutter;

import com.stickycoding.rokon.Scene;
import com.stickycoding.rokon.background.FixedBackground;

public class GameScene extends Scene {

    private FixedBackground background;

    public GameScene() {
        super();

        setBackground(background = new FixedBackground(Textures.background));
    }

    @Override
    public void onGameLoop() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onReady() {
    }
}