package im.crate.zbridge.clutter;

import com.stickycoding.rokon.Texture;
import com.stickycoding.rokon.TextureAtlas;

public class Textures {

    public static TextureAtlas atlas;
    public static Texture background, bob;

    public static void load() {
        atlas = new TextureAtlas();
        atlas.insert(background = new Texture("background.png"));
        atlas.insert(bob = new Texture("bob.png"));
        atlas.complete();
    }
}