package com.touchspin.td;

import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hero extends GameThing {
	public OrthographicCamera camera;
	public MoverInput heroMover = new MoverInput();

	private Map<String,TextureRegion> ballTypeMap = new HashMap<String, TextureRegion>();
	private Animation fireAnimation;
	private Animation smokeAnimation;
	private TextureRegion currentFireFrame;
	private TextureRegion currentSmokeFrame;
	private float scaleFactor;
	
	private int frameCount = 0;
	public Sprite heroSprite;
	public Sprite fireEffect;
	public Sprite smokeEffect;

	// private float distancePerFrameX;
	// private float distancePerFrameY;
	// private int gravity = -10;

	public Hero(OrthographicCamera camera, TiledMapWrapper tiledMapWrapper) {
		this.tiledMapWrapper = tiledMapWrapper;
		this.camera = camera;

		heroSprite = new Sprite();
		heroSprite.setBounds(0, 32, 32 * camera.zoom, 32 * camera.zoom);
		heroSprite.setOrigin(heroSprite.getWidth() / 2,
				heroSprite.getHeight() / 2);
		
		loadBallType();
		changeBall("Baseball");
		
		setHeight(heroSprite.getHeight() * camera.zoom);
		setWidth(heroSprite.getWidth() * camera.zoom);
		setX(10);
		setY(100);
		
		scaleFactor = 1f;

		// read in file animation
		loadFireAnimation();
		stateTime = 0f;
		g.i().fire = true;
		currentFireFrame = fireAnimation.getKeyFrame(stateTime, true);
		
		loadSmokeAnimation();
		currentSmokeFrame = smokeAnimation.getKeyFrame(stateTime, true);

		fireEffect = new Sprite(currentFireFrame);
		fireEffect.setBounds(0, 32, 32 * camera.zoom,
				32 * fireEffect.getHeight() / fireEffect.getWidth()
						* camera.zoom);
		fireEffect.setOrigin(heroSprite.getWidth() / 2,
				heroSprite.getHeight() / 2);
		
		smokeEffect = new Sprite(currentSmokeFrame);
		smokeEffect.setBounds(0, 32, 32 * camera.zoom,
				32 * smokeEffect.getHeight() / smokeEffect.getWidth()
						* camera.zoom);
		smokeEffect.setOrigin(heroSprite.getWidth() / 2,
				heroSprite.getHeight() / 2);

	}

	@Override
	public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(camera.combined);
		if (frameCount == 15) {
			heroSprite.setColor(Color.RED);
		} else if (frameCount == 1) {
			heroSprite.setColor(Color.WHITE);
		}
		heroSprite.draw(batch);
		if (g.i().fire) {
			drawEffect(batch);
		}
	}

	private void drawEffect(Batch batch) {
		// batch.draw(currentFrame,getX(),getY(),32f,currentFrame.getRegionHeight()*32/currentFrame.getRegionWidth());
		smokeEffect.draw(batch);
		fireEffect.draw(batch);
	}

	@Override
	public void act(float delta) {
		heroMover.move(this);

		// Attack
		if (frameCount > 1)
			frameCount--;
		if (g.i().leAnonymizer.attack) {
			attack();
			g.i().leAnonymizer.attack = false;
		}
		// position
		setSpritesPosition();

		// Rotation
		heroSprite.rotate(360 * (heroMover.previousX - getX())
				/ ((float) Math.PI * heroSprite.getRegionHeight()));
		setRotationAndScale();

		// animation
		stateTime += Gdx.graphics.getDeltaTime();
		
		currentFireFrame = fireAnimation.getKeyFrame(stateTime, true);
		fireEffect.setRegion(currentFireFrame);
		
		currentSmokeFrame = smokeAnimation.getKeyFrame(stateTime, true);
		smokeEffect.setRegion(currentSmokeFrame);

	}

	public void attack() {
		frameCount = 15;
	}

	public void changeBall(String type)
	{
		heroSprite.setRegion(ballTypeMap.get(type));
		g.i().currentBallType = type;
		g.i().sound.setBounce();
	}
	
	public void igniteBall(boolean fireOn)
	{
		g.i().fire = fireOn;
	}
	
    public void changeBalldx(float delta)
    {
    	heroMover.accelerationX = delta;
    }
    public void changeBalldy(float delta)
    {
    	heroMover.accelerationY = delta;
    }
    
    public float getYSpeed()
    {
    	return heroMover.speedYPerSecond;
    }
    
    public float getXSpeed()
    {
    	return heroMover.speedXPerSecond;
    }
	
	
	
	//--------------Private helper method------------------------------------------
	private void setSpritesPosition()
	{
		heroSprite.setX(getX());
		heroSprite.setY(getY());
		fireEffect.setX(getX());
		fireEffect.setY(getY());
		smokeEffect.setX(getX());
		smokeEffect.setY(getY());
	}
    
    private void loadBallType()
	{
		Texture appearance = new Texture("img/obsolete//Balls.png");
		TextureRegion [][] tmp = TextureRegion.split(appearance, appearance.getWidth() / 3,
				appearance.getHeight() / 4);
		
		ballTypeMap.put("Baseball", tmp[0][0]);
		ballTypeMap.put("Basketball", tmp[0][1]);
		ballTypeMap.put("Beachball", tmp[0][2]);
		ballTypeMap.put("BearingSteel", tmp[1][0]);
		ballTypeMap.put("Bowling", tmp[1][1]);
		ballTypeMap.put("Golfball", tmp[1][2]);
		ballTypeMap.put("Marble", tmp[2][0]);
		ballTypeMap.put("PingPong", tmp[2][1]);
		ballTypeMap.put("Poolball", tmp[2][2]);
		ballTypeMap.put("Soccerball", tmp[3][0]);
		ballTypeMap.put("Tennisball", tmp[3][1]);
		ballTypeMap.put("Blackball", tmp[3][2]);
	}
	
	private void loadFireAnimation()
	{
		Texture fire = new Texture(
				Gdx.files
						.internal("img/spritesheet/Fireball.png"));
		TextureRegion[][] tmp = TextureRegion.split(fire, fire.getWidth() / 12,
				fire.getHeight() / 4);
		TextureRegion[] fireFrames = new TextureRegion[12 * 4];
		int index = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 12; j++) {
				fireFrames[index++] = tmp[i][j];
			}
		}
		fireAnimation = new Animation(0.025f, fireFrames);
	}
	
	private void loadSmokeAnimation()
	{
		Texture fire = new Texture(
				Gdx.files
						.internal("img/spritesheet/Smoke.png"));
		TextureRegion[][] tmp = TextureRegion.split(fire, fire.getWidth() / 15,
				fire.getHeight() / 6);
		TextureRegion[] smokeFrames = new TextureRegion[15 * 6 - 4];
		int index = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 15; j++) {
				smokeFrames[index++] = tmp[i][j];
				if(index == smokeFrames.length)
					break;
			}
		}
		smokeAnimation = new Animation(0.025f, smokeFrames);
	}
	private void setRotationAndScale() {

		if (heroMover.speedXPerSecond == 0) {
			if (heroMover.speedYPerSecond > 0)
				fireEffect.setRotation((float) (-180));
			else if (heroMover.speedYPerSecond < 0)
				fireEffect.setRotation(0);
		}

		else if (heroMover.speedYPerSecond == 0) {

			if (g.i().gameMode == 'R') {
				if (heroMover.speedXPerSecond > 0)
					fireEffect.setRotation((float) (40 * Math
							.atan(heroMover.speedXPerSecond / 200)));
				else if (heroMover.speedXPerSecond < 0)
					fireEffect.setRotation((float) (40 * Math
							.atan(heroMover.speedXPerSecond / 200)));
			} else {
				if (heroMover.speedXPerSecond > 0)
					fireEffect.setRotation((float) (90));
				else if (heroMover.speedXPerSecond < 0)
					fireEffect.setRotation((float) (-90));
			}
		}

		else if (heroMover.speedXPerSecond * heroMover.speedYPerSecond > 0) {
			if (heroMover.speedXPerSecond > 0)
				fireEffect.setRotation((float) (90 + Math
						.atan(heroMover.speedYPerSecond
								/ heroMover.speedXPerSecond)
						/ Math.PI * 180));
			else
				fireEffect.setRotation((float) (-(90 - Math
						.atan(heroMover.speedYPerSecond
								/ heroMover.speedXPerSecond)
						/ Math.PI * 180)));

		} else if (heroMover.speedXPerSecond * heroMover.speedYPerSecond < 0) {

			if (heroMover.speedXPerSecond > 0)
				fireEffect.setRotation((float) (90 + Math
						.atan(heroMover.speedYPerSecond
								/ heroMover.speedXPerSecond)
						/ Math.PI * 180));
			else
				fireEffect.setRotation((float) (-90 + Math
						.atan(heroMover.speedYPerSecond
								/ heroMover.speedXPerSecond)
						/ Math.PI * 180));
		}
		
		smokeEffect.setRotation(fireEffect.getRotation());
		
		scaleFactor = Math.max((float) Math.hypot(heroMover.speedXPerSecond, heroMover.speedYPerSecond)/300,1f);
		fireEffect.setScale(1f, scaleFactor);
		smokeEffect.setScale(1f, scaleFactor);
	}

}
