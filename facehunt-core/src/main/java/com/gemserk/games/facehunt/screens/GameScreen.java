package com.gemserk.games.facehunt.screens;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.artemis.systems.SpriteRendererSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TimerSystem;
import com.gemserk.commons.artemis.triggers.AbstractTrigger;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class GameScreen extends ScreenAdapter {

	private final FaceHuntGame game;

	private ResourceManager<String> resourceManager;

	private SpriteBatch spriteBatch;

	private Libgdx2dCameraTransformImpl worldCamera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

	private Camera cameraData;

	private WorldWrapper worldWrapper;

	private World world;

	private boolean gameOver = false;

	private static final Color hideColor = new Color(1f, 1f, 1f, 0f);
	private static final Color showColor = new Color(1f, 1f, 1f, 1f);

	public GameScreen(FaceHuntGame game) {
		this.game = game;
	}

	public void restartGame() {

		gameOver = false;

		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		// worldCamera.center(viewportWidth / 2, viewportHeight / 2);
		worldCamera.center(0f, 0f);

		float zoom = 1f;
		float invZoom = 1 / zoom;

		cameraData = new Camera(0f, 0f, zoom, 0f);

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BackgroundTexture", "data/background01-1024x512.jpg", false);
				texture("HappyFaceTexture", "data/face-sad-64x64.png");
				texture("SadFaceTexture", "data/face-happy-64x64.png");
				texture("HeartTexture", "data/heart-32x32.png");

				sprite("BackgroundSprite", "BackgroundTexture");
				sprite("HappyFaceSprite", "HappyFaceTexture");
				sprite("SadFaceSprite", "SadFaceTexture");

				sound("CritterKilledSound", "data/critter-killed.wav");
				sound("CritterSpawnedSound", "data/critter-spawned.wav");
				sound("CritterBounceSound", "data/bounce.wav");

				font("Font", "data/font.png", "data/font.fnt");
			}
		};

		spriteBatch = new SpriteBatch();

		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();
		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		renderLayers.add(new RenderLayer(-100, 100, worldCamera));

		world = new World();

		worldWrapper = new WorldWrapper(world);
		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new SpriteRendererSystem(renderLayers));
		worldWrapper.addUpdateSystem(new TimerSystem());
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.init();

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		createStaticSprite(backgroundSprite, viewportWidth * 0.5f, viewportHeight * 0.5f, 1024, 512, 0f, -101, 0.5f, 0.5f, Color.WHITE);
		createFaceSpawner(new Rectangle(50, 50, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 100));

		world.loopStart();
	}

	void createStaticSprite(Sprite sprite, float x, float y, float width, float height, float angle, int layer, float centerx, float centery, Color color) {
		Entity entity = world.createEntity();
		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(width, height), angle));
		entity.addComponent(new SpriteComponent(sprite, layer, new Vector2(centerx, centery), new Color(color)));
		entity.refresh();
	}

	void createFaceSpawner(final Rectangle spawnArea) {
		Entity entity = world.createEntity();

		final int minTime = 2000;
		final int maxTime = 5000;

		entity.addComponent(new TimerComponent(MathUtils.random(minTime, maxTime), new AbstractTrigger() {
			@Override
			public boolean handle(Entity e) {
				TimerComponent timerComponent = e.getComponent(TimerComponent.class);
				timerComponent.reset();
				timerComponent.setCurrentTime(MathUtils.random(minTime, maxTime));

				float x = MathUtils.random(spawnArea.x, spawnArea.width);
				float y = MathUtils.random(spawnArea.y, spawnArea.height);

				float angularVelocity = MathUtils.random(30f, 90f);

				if (MathUtils.randomBoolean())
					angularVelocity = -angularVelocity;

				Vector2 linearVelocity = new Vector2(0f, 0f);
				linearVelocity.x = MathUtils.random(30f, 100f);
				linearVelocity.rotate(MathUtils.random(0f, 360f));
				
				createFace(x, y, linearVelocity, angularVelocity);

				Gdx.app.log("FaceHunt", "Face spawned at (" + x + ", " + y + ")");

				return false;
			}
		}));
		entity.refresh();
	}

	void createFace(float x, float y, Vector2 linearVelocity, float angularVelocity) {
		Entity entity = world.createEntity();

		Sprite sprite = resourceManager.getResourceValue("SadFaceSprite");

		Color faceColor = new Color();

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500).build());

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(64f, 64f), 0f));
		entity.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), faceColor));
		entity.addComponent(new MovementComponent(linearVelocity, angularVelocity));

		entity.refresh();
	}

	public void internalRender(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldCamera.zoom(cameraData.getZoom());
		worldCamera.move(cameraData.getX(), cameraData.getY());
		worldCamera.rotate(cameraData.getAngle());
		worldWrapper.render();
	}

	@Override
	public void internalUpdate(float delta) {
		Synchronizers.synchronize();
		int deltaInMs = (int) (delta * 1000f);
		worldWrapper.update(deltaInMs);

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE))
			game.transition(game.menuScreen);
	}

	@Override
	public void show() {
		if (gameOver)
			restartGame();
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}