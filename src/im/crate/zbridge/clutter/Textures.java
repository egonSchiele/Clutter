package im.crate.zbridge.clutter;

import android.util.Log;

import com.stickycoding.rokon.Texture;
import com.stickycoding.rokon.TextureAtlas;

public class Textures {

    public static TextureAtlas atlas;
    public static Texture background;

    public static void load() {
    	Log.d("Clutter", "Textures being loaded");
        atlas = new TextureAtlas();
        atlas.insert(background = new Texture("redToGrey.png"));
        atlas.complete();
    }
}