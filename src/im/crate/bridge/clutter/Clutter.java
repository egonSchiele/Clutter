package im.crate.bridge.clutter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Clutter extends BaseGameActivity implements IAccelerometerListener {
  private static int CAMERA_WIDTH;
  private static int CAMERA_HEIGHT;
  
  // base resolutions for android devices (add more as needed)
  private static final int WIDTH16BY9 = 854;
  private static final int HEIGHT16BY9 = 480;
  private static final int WIDTH3BY2 = 720;
  private static final int HEIGHT3BY2 = 480;

  private Camera mCamera;
  private Font mFont;
  private Font mEnglishFont;
  private PhysicsWorld mPhysicsWorld;
  private ArrayList<String[]> wordlist = new ArrayList<String[]>();
  private Map<String,String> inscene = new HashMap<String,String>();
  private String currentWord;
  final Random random = new Random();

  
  final FixtureDef wordFixtureDef = PhysicsFactory.createFixtureDef(1, 0.1f,
      0.5f);
  
  private void setCameraDimensions() {
	DisplayMetrics metrics = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(metrics);
	
	if((metrics.widthPixels / metrics.heightPixels) >= (16/9)) {
		CAMERA_WIDTH = WIDTH16BY9;
		CAMERA_HEIGHT = HEIGHT16BY9;
	} else {
		CAMERA_WIDTH = WIDTH3BY2;
		CAMERA_HEIGHT = HEIGHT3BY2;
	}	
  }

  public Engine onLoadEngine() {
    setCameraDimensions();
    this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
        new FillResolutionPolicy(),
        this.mCamera));
  }

  public void onLoadResources() {
	AssetManager assetManager = getAssets();
    Texture mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR);
    Texture mEnglishFontTexture = new Texture(256, 256, TextureOptions.BILINEAR);
    this.mFont = new Font(mFontTexture, Typeface.create(
        Typeface.SERIF, Typeface.BOLD), 38, true, Color.BLACK);
    this.mEnglishFont = new Font(mEnglishFontTexture, Typeface.create(
            Typeface.SERIF, Typeface.BOLD), 38, true, Color.GREEN);
    this.mEngine.getTextureManager().loadTexture(mFontTexture);
    this.mEngine.getTextureManager().loadTexture(mEnglishFontTexture);
    this.mEngine.getFontManager().loadFont(this.mFont); 
    this.mEngine.getFontManager().loadFont(this.mEnglishFont);
    this.enableAccelerometerSensor(this);
    try {
		BufferedReader wordrdr = new BufferedReader(new InputStreamReader(assetManager.open("dicts/english-french.txt")));
		String line;
		while((line = wordrdr.readLine()) != null)
		{
			wordlist.add(new String[] {line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":")+1)});
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  class Word {
    Text txtShape;
    Body txtBody;

    public Word(Font font, String text) {
      txtShape = new Text(100, 100, font, text);
      txtBody = PhysicsFactory.createBoxBody(mPhysicsWorld, txtShape,
          BodyType.DynamicBody, wordFixtureDef);
      mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
          txtShape, txtBody, true, true, true, true));
    }
    
    public Word(Font font, String text, BodyType bodytype) {
        txtShape = new Text(0, 0, font, text);
        txtBody = PhysicsFactory.createBoxBody(mPhysicsWorld, txtShape,
            bodytype, wordFixtureDef);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
            txtShape, txtBody, true, true, true, true));
      }
  }

  public Scene onLoadScene() {
	Random rand = new Random();
    this.mEngine.registerUpdateHandler(new FPSLogger());

    final Scene scene = new Scene(1);

    final Vector2 gravity = new Vector2(0, 0);

    this.mPhysicsWorld = new PhysicsWorld(gravity, false);
    scene.setBackground(new ColorBackground(1.0f, 1.0f, 1.0f));

    final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH,
        2);
    final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
    final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
    final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

    final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,
        0.5f, 0.5f);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
        BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
        BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
        BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
        BodyType.StaticBody, wallFixtureDef);

    // scene.getTopLayer().addEntity(textCenter);
    for (int i = 0; i < 12; i++)
    {
    	String[] pair = wordlist.get(rand.nextInt(wordlist.size()));
    	scene.getTopLayer().addEntity(new Word(mFont, pair[1]).txtShape); //Add the French part of 12 random word pairs
    	inscene.put(pair[0], pair[1]);
    }
    
    currentWord = inscene.keySet().iterator().next();
    scene.getTopLayer().addEntity(new Word(mEnglishFont, currentWord, BodyType.StaticBody).txtShape);
    
    
    scene.registerUpdateHandler(this.mPhysicsWorld);
    scene.registerUpdateHandler(new IUpdateHandler() {

      public void onUpdate(float pSecondsElapsed) {
        for (Body x : mPhysicsWorld.getBodies()) {
          for(Body i: mPhysicsWorld.getBodies())
          {
            if(x != i)
            {
              float dist = i.getWorldCenter().dst2(x.getWorldCenter());

              if(dist != 0)
                i.applyLinearImpulse(x.getWorldCenter().sub(i.getWorldCenter()).nor().mul(((10/dist) - (.001f * dist)) * -pSecondsElapsed), i.getWorldCenter());
            }
          }
        }
      }

      public void reset() {
        // TODO Auto-generated method stub

      }
    });

    return scene;
  }

  public void onAccelerometerChanged(
      final AccelerometerData pAccelerometerData) {

    //this.mPhysicsWorld.setGravity(new Vector2(pAccelerometerData.getY(),
    //    pAccelerometerData.getX()));
  }

  public void onLoadComplete() {
    // TODO Auto-generated method stub

  }
}
