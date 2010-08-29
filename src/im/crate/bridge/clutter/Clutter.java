package im.crate.bridge.clutter;

import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
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

import android.graphics.Color;
import android.graphics.Typeface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Clutter extends BaseGameActivity implements IAccelerometerListener {
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private Camera mCamera;
	private Font mFont;
	private Texture mFontTexture;
	private PhysicsWorld mPhysicsWorld;
	final Random random = new Random();

	final FixtureDef wordFixtureDef = PhysicsFactory.createFixtureDef(1, 0.1f,
			0.5f);

	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				this.mCamera));
	}

	public void onLoadResources() {
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR);
		this.mFont = new Font(this.mFontTexture, Typeface.create(
				Typeface.SERIF, Typeface.BOLD), 38, true, Color.BLACK);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		this.enableAccelerometerSensor(this);
	}

	class Word {
		Text txtShape;
		Body txtBody;

		public Word(Font font, String text) {
			txtShape = new Text(font.getStringWidth(text),
					font.getLineHeight(), font, text);
			txtBody = PhysicsFactory.createBoxBody(mPhysicsWorld, txtShape,
					BodyType.DynamicBody, wordFixtureDef);
			mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
					txtShape, txtBody, true, true, true, true));
		}
	}

	public Scene onLoadScene() {
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
			scene.getTopLayer().addEntity(new Word(mFont, "bonjour").txtShape);
		for (int i = 0; i < 12; i++)
			scene.getTopLayer().addEntity(new Word(mFont, "fuck").txtShape);

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
		//		pAccelerometerData.getX()));
	}

	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}
}