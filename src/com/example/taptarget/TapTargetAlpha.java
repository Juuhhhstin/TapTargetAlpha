package com.example.taptarget;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.entity.util.ScreenCapture;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;


public class TapTargetAlpha extends SimpleBaseGameActivity{
	// ===========================================================
	// Constants
	// ===========================================================

	// default camera values
	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 480;
	
	// values to generate random position coordinates
	private int MinRandX = 90;
	private int MaxRandX = 1190;
	private int MinRandY = 90;
	private int MaxRandY = 630;
	private int randX = 0;
	private int randY = 0;
	
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	
	// objects
    Text text = null;
    Text touchText1 = null;
    Text touchText2 = null;
    Rectangle rect1 = null;
    
    // text properties
    final int pCharactersMaximum = 11;
    
    
    
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		
		// Device properties & dimensions
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		CAMERA_WIDTH = width;
		CAMERA_HEIGHT = height;
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		
		// Text Properties
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mFont = new Font(this.getFontManager(), this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.getFontManager().loadFont(this.mFont);
	}

	int ctr = 0;
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		final Scene scene = new Scene();
		final ScreenCapture screenCapture = new ScreenCapture();
		scene.attachChild(screenCapture);
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
			        // TODO Auto-generated method stub
			        //Your code to run here!
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});	
		
		// set scene properties
		scene.setWidth(CAMERA_WIDTH);
		scene.setHeight(CAMERA_HEIGHT);
		scene.setBackground(new Background(0, 0, 0)); //black background
		
		//Generate random coordinates
		randX = randomX(MinRandX, MaxRandX);
		randY = randomY(MinRandY, MaxRandY);

		/* Create the rectangles. */
		rect1 = this.makeColoredRectangle(randX, randY, 1, 0, 0);
		
		Log.d("Rect1: ", randX+" "+randY);
		
		final Entity rectangleGroup = new Entity(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2){
			
			//enable onAreaTouched
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				ctr++;
				text.setText(ctr+"");
				Log.d("cord", rect1.getX()+" "+rect1.getY());
				return true;
			}
		};
		
		// create text
		text = new Text(CAMERA_WIDTH - 80, 50, mFont, "Score: "+ctr, pCharactersMaximum, this.getVertexBufferObjectManager());
		touchText1 = new Text(rect1.getX(), rect1.getY() + 20, mFont, "Touch", pCharactersMaximum, this.getVertexBufferObjectManager());
		touchText2 = new Text(rect1.getX(), rect1.getY() - 20, mFont, "me ;)", pCharactersMaximum, this.getVertexBufferObjectManager());
		
		rectangleGroup.attachChild(rect1);
		rectangleGroup.attachChild(text);
		rectangleGroup.attachChild(touchText1);
		rectangleGroup.attachChild(touchText2);
		scene.attachChild(rectangleGroup);
		//scene.attachChild(text);
		
		scene.registerTouchArea(rectangleGroup);
        scene.setTouchAreaBindingOnActionMoveEnabled(true);
       	scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		
       	
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			public boolean range(float rangex, float rangey, float inputx, float inputy, float giveTake){
				
				if(inputx <= (rangex + giveTake) && inputx >= (rangex - giveTake)){
					if(inputy <= (rangey + giveTake) && inputy >= (rangey - giveTake)){
						return true;
					}
				}
				
				return false;
			}

			@Override
			public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pt) {
				
				Log.d("cord", rect1.getX()+" "+rect1.getY());
				if(range(rect1.getX(), rect1.getY(), pt.getX(), pt.getY(), 100) && (pt.getAction() == TouchEvent.ACTION_UP)){
					// increase counter or score
					ctr++;
					text.setText("Score: " + ctr);
					
					
					// generate new random position
					randX = randomX(MinRandX, MaxRandX);
					randY = randomY(MinRandY, MaxRandY);
					
					// set new random pos.
					rect1.setX(randX);
					rect1.setY(randY);
					
					// change touchText position
					touchText1.setPosition(rect1.getX(), rect1.getY() + 20);
					touchText2.setPosition(rect1.getX(), rect1.getY() - 20);
					
					return true;
				}
				return false;
			}
		});
		
		return scene;
	}
	
	
	// generate random X value
	private int randomX(int minRandX, int maxRandX) {
		int randX = MinRandX + (int)(Math.random() * ((MaxRandX - MinRandX) + 1));
		return randX;
	}
	
	// generate random Y value
	private int randomY(int MinY, int MaxY){
		
		int randY = MinRandY + (int)(Math.random() * ((MaxRandY - MinRandY) + 1));
		
		return randY;
	}

	private Rectangle makeColoredRectangle(final float pX, final float pY, final float pRed, final float pGreen, final float pBlue) {
		final Rectangle coloredRect = new Rectangle(pX, pY, 180, 180, this.getVertexBufferObjectManager());
		coloredRect.setColor(pRed, pGreen, pBlue);
		
		
		
		return coloredRect;
	}
	
}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
