package im.crate.zbridge.clutter;

import java.util.ArrayList;

import android.graphics.Typeface;

import com.stickycoding.rokon.Font;
import com.stickycoding.rokon.Scene;
import com.stickycoding.rokon.background.FixedBackground;


public class GameScene extends Scene {

    private FixedBackground background;
    private ArrayList<Word> words = new ArrayList<Word>();
    private Font uifont;
    
    private void addWordPair(String w1, String w2)
    {
    	Word first = new Word(w1, uifont);
    	first.match = new Word(w2, uifont);
    	words.add(first);
    	words.add(first.match);
    	add(first.sprite);
    	add(first.match.sprite);
    }
    
    public GameScene(Typeface mType) {
        super();
        uifont = new Font(mType);
        background = new FixedBackground(Textures.background);
        addWordPair("test", "foo");
        
        setBackground(background);
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