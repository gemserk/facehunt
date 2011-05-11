package com.gemserk.games.facehunt.screens;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.HitComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpatialImpl;
import com.gemserk.commons.artemis.components.SpatialPhysicsImpl;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.systems.HitDetectionSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.artemis.systems.SpriteRendererSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TimerSystem;
import com.gemserk.commons.artemis.triggers.AbstractTrigger;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.systems.OutsideAreaTriggerSystem;
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

	public boolean gameOver = false;

	private static final Color hideColor = new Color(1f, 1f, 1f, 0f);
	private static final Color showColor = new Color(1f, 1f, 1f, 1f);

	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	private BodyBuilder bodyBuilder;

	private Box2DDebugRenderer box2dDebugRenderer;

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

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);
		bodyBuilder = new BodyBuilder(physicsWorld);
		box2dDebugRenderer = new Box2DDebugRenderer();

		world = new World();

		worldWrapper = new WorldWrapper(world);
		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new SpriteRendererSystem(renderLayers));
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(new HitDetectionSystem());
		worldWrapper.addUpdateSystem(new TimerSystem());
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.addUpdateSystem(new OutsideAreaTriggerSystem());
		worldWrapper.init();

		createBorder(viewportWidth * 0.5f, 0, viewportWidth, 10);
		createBorder(viewportWidth * 0.5f, viewportHeight, viewportWidth, 10);

		createBorder(0, viewportHeight * 0.5f, 10, viewportHeight);
		createBorder(viewportWidth, viewportHeight * 0.5f, 10, viewportHeight);

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		createStaticSprite(backgroundSprite, viewportWidth * 0.5f, viewportHeight * 0.5f, 1024, 512, 0f, -101, 0.5f, 0.5f, Color.WHITE);
		createFaceSpawner(new Rectangle(64, 64, viewportWidth - 128, viewportHeight - 128));

		world.loopStart();
	}

	BodyBuilder getBodyBuilder() {
		bodyBuilder.reset();
		return bodyBuilder;
	}

	void createStaticSprite(Sprite sprite, float x, float y, float width, float height, float angle, int layer, float centerx, float centery, Color color) {
		Entity entity = world.createEntity();
		entity.addComponent(new SpatialComponent(new SpatialImpl(x, y, width, height, angle)));
		entity.addComponent(new SpriteComponent(sprite, layer, new Vector2(centerx, centery), new Color(color)));
		entity.refresh();
	}

	void createBorder(float x, float y, float w, float h) {
		Entity entity = world.createEntity();
		Body body = getBodyBuilder() //
				.type(BodyType.StaticBody) //
				.boxShape(w * 0.5f, h * 0.5f).mass(1f)//
				.friction(0f)//
				.userData(entity)//
				.position(x, y)//
				.build();
		entity.addComponent(new PhysicsComponent(body));
		entity.refresh();
	}

	void createFaceSpawner(final Rectangle spawnArea) {
		Entity entity = world.createEntity();

		final int minTime = 1000;
		final int maxTime = 2000;

		entity.addComponent(new TimerComponent(MathUtils.random(minTime, maxTime), new AbstractTrigger() {
			@Override
			public boolean handle(Entity e) {
				TimerComponent timerComponent = e.getComponent(TimerComponent.class);
				timerComponent.reset();
				timerComponent.setCurrentTime(MathUtils.random(minTime, maxTime));

				float x = MathUtils.random(spawnArea.x, spawnArea.width);
				float y = MathUtils.random(spawnArea.y, spawnArea.height);

				float angularVelocity = MathUtils.random(30f, 180f);

				if (MathUtils.randomBoolean())
					angularVelocity = -angularVelocity;

				Vector2 linearVelocity = new Vector2(0f, 0f);
				linearVelocity.x = MathUtils.random(50f, 150f);
				linearVelocity.rotate(MathUtils.random(0f, 360f));
				
				int aliveTime = MathUtils.random(3000, 7000);

				createFace(x, y, linearVelocity, angularVelocity, aliveTime);

				Sound sound = resourceManager.getResourceValue("CritterSpawnedSound");
				sound.play();

				Gdx.app.log("FaceHunt", "Face spawned at (" + x + ", " + y + ")");

				return false;
			}
		}));
		entity.refresh();
	}

	void createFace(float x, float y, Vector2 linearVelocity, float angularVelocity, final int aliveTime) {
		Entity entity = world.createEntity();

		Sprite sprite = resourceManager.getResourceValue("SadFaceSprite");

		final Color faceColor = new Color();

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500).build(), new TransitionEventHandler() {
			@Override
			public void onTransitionFinished(Transition transition) {
				Synchronizers.transition(faceColor, Transitions.transitionBuilder(showColor).end(hideColor).time(aliveTime - 500).build());
			}
		});

		Body body = getBodyBuilder() //
				.type(BodyType.DynamicBody) //
				.circleShape(32f) //
				.mass(1f)//
				.friction(0f)//
				.restitution(1f)//
				.userData(entity)//
				.position(x, y)//
				.build();

		body.setLinearVelocity(linearVelocity);
		body.setAngularVelocity(angularVelocity * MathUtils.degreesToRadians);

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, 64f, 64f)));
		entity.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), faceColor));
		entity.addComponent(new HitComponent(new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				Sound sound = resourceManager.getResourceValue("CritterBounceSound");
				sound.play();
				return false;
			}
		}));
		entity.addComponent(new TimerComponent(aliveTime, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				world.deleteEntity(e);
				return true;
			}
		}));

		entity.refresh();
	}

	public void internalRender(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldCamera.zoom(cameraData.getZoom());
		worldCamera.move(cameraData.getX(), cameraData.getY());
		worldCamera.rotate(cameraData.getAngle());
		worldWrapper.render();

		if (Gdx.input.isKeyPressed(Keys.D))
			box2dDebugRenderer.render(physicsWorld);
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
		physicsWorld.dispose();
	}

}