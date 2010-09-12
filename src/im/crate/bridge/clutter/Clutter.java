package im.crate.bridge.clutter;
import android.view.MotionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.runnable.RunnableHandler;
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
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Clutter extends BaseGameActivity implements IAccelerometerListener
{
  private static int CAMERA_WIDTH;
  private static int CAMERA_HEIGHT;
  
  // base resolutions for android devices (add more as needed)
  private static final int WIDTH16BY9 = 854;
  private static final int HEIGHT16BY9 = 480;
  private static final int WIDTH3BY2 = 720;
  private static final int HEIGHT3BY2 = 480;
  private static final int PENALTY = 3;
  private static final int TOTAL_WORDS = 5;
  
  private Camera mCamera;
  private Font mFont;
  private Font mEnglishFont;
  private PhysicsWorld mPhysicsWorld;
  private ArrayList<String[]> wordlist = new ArrayList<String[]>();
  private HashMap<String, ArrayList<String>> inscene = new HashMap<String, ArrayList<String>>();
  private String currentWord;
  private Word currentWordObj;
  private RunnableHandler addremove = new RunnableHandler();
  
  final Random random = new Random();
  private Scene scene;
  final FixtureDef wordFixtureDef = PhysicsFactory.createFixtureDef(1, 0.1f,
      0.5f);
  
  private void setCameraDimensions()
  {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    
    if ((metrics.widthPixels / metrics.heightPixels) >= (16 / 9))
    {
      CAMERA_WIDTH = WIDTH16BY9;
      CAMERA_HEIGHT = HEIGHT16BY9;
    } else
    {
      CAMERA_WIDTH = WIDTH3BY2;
      CAMERA_HEIGHT = HEIGHT3BY2;
    }
  }
  
  public Engine onLoadEngine()
  {
    setCameraDimensions();
    this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
        new FillResolutionPolicy(), this.mCamera));
  }
  
  public void onLoadResources()
  {
    AssetManager assetManager = getAssets();
    Texture mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR);
    Texture mEnglishFontTexture = new Texture(256, 256, TextureOptions.BILINEAR);
    this.mFont = new Font(mFontTexture, Typeface.create(Typeface.SANS_SERIF,
        Typeface.BOLD), 38, true, Color.BLACK);
    this.mEnglishFont = new Font(mEnglishFontTexture, Typeface.create(
        Typeface.SANS_SERIF, Typeface.NORMAL), 38, true, Color.GREEN);
    this.mEngine.getTextureManager().loadTexture(mFontTexture);
    this.mEngine.getTextureManager().loadTexture(mEnglishFontTexture);
    this.mEngine.getFontManager().loadFont(this.mFont);
    this.mEngine.getFontManager().loadFont(this.mEnglishFont);
    
    this.enableAccelerometerSensor(this);
    
    try
    {
      BufferedReader wordrdr = new BufferedReader(new InputStreamReader(
          assetManager.open("dicts/english-french.txt")));
      String line;
      while ((line = wordrdr.readLine()) != null)
      {
        wordlist.add(new String[]{line.substring(0, line.indexOf(":")),
            line.substring(line.indexOf(":") + 1)});
      }
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  class Word
  {
    Text txtShape;
    Body txtBody;
    String mText;
    Scene mScene;
    boolean isEnglishWord;
    
    public Word(Font font, String text, Vector2 position, Scene scene)
    {
      this(font, text, BodyType.DynamicBody, position, false, scene);
    }
    
    public Word(Font font, String text, BodyType bodytype, Vector2 position,
        boolean englishword, final Scene scene)
    {
      mText = text;
      isEnglishWord = englishword;
      mScene = scene;
      txtShape = new Text(position.x, position.y, font, text)
      {
        @Override
        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
            final float pTouchAreaLocalX, final float pTouchAreaLocalY)
        {
          if (!isEnglishWord && inscene.get(currentWord) != null
              && pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP)
          {
            String correctTranslation = inscene.get(currentWord).get(0);
            if (correctTranslation != mText)
            {
              // wrong guess, add duplicates
              addremove.postRunnable(new Runnable()
              {
                public void run()
                {
                  String correctTranslation = inscene.get(currentWord).get(0);
                  Vector2 posVector = new Vector2(100, 100);
                  
                  for (int i = 0; i < PENALTY; i++)
                  {
                    Word newWord = new Word(mFont, correctTranslation,
                        posVector, mScene);
                    scene.getTopLayer().addEntity(newWord.txtShape);
                    scene.registerTouchArea(newWord.txtShape);
                    inscene.get(currentWord).add(correctTranslation);
                  }
                }
              });
            } else
            {
              // correct guess
              Log.d("Clutter",
                  "REMOVING THE WORD DUDE. (OR POSTING TO THE MF'ING HANDLER ANYWAY)"
                      + mText);
              
              /*
               * If we have removed all translations from the screen, Remove
               * english word from word list
               */
              if (inscene.get(currentWord).size() == 1)
              {
                inscene.remove(currentWord);
              } else
              {
                // remove one copy of the translation in the
                // background
                inscene.get(currentWord).remove(
                    inscene.get(currentWord).size() - 1);
              }
              // Add new english word.. Runnable handlers run in
              // reverse order, this runs AFTER the next one.
              addremove.postRunnable(new Runnable()
              {
                public void run()
                {
                  if (inscene.keySet().iterator().hasNext())
                  {
                    currentWord = inscene.keySet().iterator().next();
                    Log.d("Clutter", "Eng word add " + currentWord);
                    Vector2 currentWordPos = new Vector2(0, 0);
                    currentWordObj = new Word(mEnglishFont, currentWord,
                        BodyType.DynamicBody, currentWordPos, true, mScene);
                    mScene.getTopLayer().addEntity(currentWordObj.txtShape);
                  } else
                  {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), Victory.class);
                    startActivity(intent);
                  }
                  
                }
              });
              
              // Delete correctly guessed english word.
              addremove.postRunnable(new Runnable()
              {
                public void run()
                {
                  Log.d("Clutter", "Eng word remove.");
                  mScene.getTopLayer().removeEntity(currentWordObj.txtShape); // Remove
                  // from
                  // scene
                  mPhysicsWorld.destroyBody(currentWordObj.txtBody); // Remove
                  // from
                  // physics.
                }
              });
              
              // Remove dead french word
              addremove.postRunnable(new Runnable()
              {
                public void run()
                {
                  Log.d("Clutter", "French word remove.");
                  mScene.getTopLayer().removeEntity(Word.this.txtShape);
                }
              });
            }
          }
          return true;
        };
      };
      txtBody = PhysicsFactory.createBoxBody(mPhysicsWorld, txtShape, bodytype,
          wordFixtureDef);
      mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(txtShape,
          txtBody, true, true, true, true));
    }
  }
  
  public void buildWorld()
  {
    final Vector2 gravity = new Vector2(0, 0);
    this.mPhysicsWorld = new PhysicsWorld(gravity, false);
    
    final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
    final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
    final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
    final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
    
    final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f,
        0.5f);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
        BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody,
        wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody,
        wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
        BodyType.StaticBody, wallFixtureDef);
    
  }
  
  public void addWords()
  {
    Word newWord;
    Random rand = new Random();
    for (int i = 0; i < TOTAL_WORDS; i++)
    {
      String[] pair = wordlist.get(rand.nextInt(wordlist.size()));
      // float x = rand.nextInt(CAMERA_WIDTH - 20)+10;
      // float y = rand.nextInt(CAMERA_HEIGHT - 20)+10;
      float x = 100;
      float y = 100;
      Vector2 posVector = new Vector2(x, y);
      newWord = new Word(mFont, pair[1], posVector, scene);
      scene.getTopLayer().addEntity(newWord.txtShape); // Add the French
      // part of 12
      // random word
      // pairs
      scene.registerTouchArea(newWord.txtShape);
      ArrayList<String> tempArray = new ArrayList<String>();
      tempArray.add(pair[1]);
      inscene.put(pair[0], tempArray);
      
      // by convention, the last word we add is the first correct word.
      if (i == TOTAL_WORDS - 1)
      {
        currentWord = pair[0];
      }
    }
    
    Vector2 currentWordPos = new Vector2(0, 0);
    currentWordObj = new Word(mEnglishFont, currentWord, BodyType.StaticBody,
        currentWordPos, true, scene);
    scene.getTopLayer().addEntity(currentWordObj.txtShape);
  }
  
  public void registerHandlers()
  {
    scene.registerUpdateHandler(addremove);
    scene.registerUpdateHandler(this.mPhysicsWorld);
    scene.registerUpdateHandler(new IUpdateHandler()
    {
      public void onUpdate(float pSecondsElapsed)
      {
        for (Body x : mPhysicsWorld.getBodies())
        {
          for (Body i : mPhysicsWorld.getBodies())
          {
            if (x != i)
            {
              float dist = i.getWorldCenter().dst2(x.getWorldCenter());
              
              if (dist != 0)
                i.applyLinearImpulse(x.getWorldCenter().sub(i.getWorldCenter())
                    .nor().mul(
                        ((10 / dist) - (.001f * dist)) * -pSecondsElapsed), i
                    .getWorldCenter());
            }
          }
        }
      }
      
      public void reset()
      {
        // TODO Auto-generated method stub
      }
    });
  }
  
  public Scene onLoadScene()
  {
    this.mEngine.registerUpdateHandler(new FPSLogger());
    
    scene = new Scene(1);
    scene.setBackground(new ColorBackground(1.0f, 1.0f, 1.0f));
    buildWorld();
    
    // scene.getTopLayer().addEntity(textCenter);
    addWords();
    registerHandlers();
    return scene;
  }
  
  public void onAccelerometerChanged(final AccelerometerData pAccelerometerData)
  {
    // this.mPhysicsWorld.setGravity(new Vector2(pAccelerometerData.getY(),
    // pAccelerometerData.getX()));
  }
  
  public void onLoadComplete()
  {
    // TODO Auto-generated method stub
  }
}
