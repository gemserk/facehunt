package com.gemserk.games.facehunt.gamestates;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.Spatial;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpatialImpl;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.HitDetectionSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.artemis.systems.SpriteRendererSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TimerSystem;
import com.gemserk.commons.artemis.triggers.AbstractTrigger;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.input.LibgdxPointer;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.components.HealthComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.components.RandomMovementBehaviorComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;
import com.gemserk.games.facehunt.controllers.FaceHuntControllerImpl;
import com.gemserk.games.facehunt.entities.Templates;
import com.gemserk.games.facehunt.systems.BounceSmallVelocityFixSystem;
import com.gemserk.games.facehunt.systems.FaceHuntControllerSystem;
import com.gemserk.games.facehunt.systems.IntermittentInvulnerabilitySystem;
import com.gemserk.games.facehunt.systems.RandomMovementBehaviorSystem;
import com.gemserk.games.facehunt.values.GameData;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class TestGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private ResourceManager<String> resourceManager;

	private Libgdx2dCameraTransformImpl worldCamera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

	private Camera cameraData;

	private WorldWrapper worldWrapper;

	private World world;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	private BodyBuilder bodyBuilder;

	private Box2DDebugRenderer box2dDebugRenderer;

	private FaceHuntController controller;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private final Vector2 mousePosition = new Vector2();

	private GameData gameData;

	public TestGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		worldCamera.center(0f, 0f);

		cameraData = new CameraImpl(0f, 0f, 64f, 0f);

		gameData = new GameData();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);

				texture("BackgroundTexture", "data/background01-1024x512.jpg", false);
				texture("HappyFaceTexture", "data/face-happy-64x64.png");
				texture("SadFaceTexture", "data/face-sad-64x64.png");
				texture("HeartTexture", "data/heart-32x32.png");
				texture("FaceSpriteSheet", "data/face-parts.png");

				sprite("BackgroundSprite", "BackgroundTexture");
				sprite("HappyFaceSprite", "HappyFaceTexture");
				sprite("SadFaceSprite", "SadFaceTexture");
				sprite("HeartSprite", "HeartTexture");

				sprite("Part01", "FaceSpriteSheet", 64 * 0, 64 * 0, 64, 64);
				sprite("Part02", "FaceSpriteSheet", 64 * 1, 64 * 0, 64, 64);
				sprite("Part03", "FaceSpriteSheet", 64 * 2, 64 * 0, 64, 64);
				sprite("Part04", "FaceSpriteSheet", 64 * 3, 64 * 0, 64, 64);
				sprite("Part05", "FaceSpriteSheet", 64 * 0, 64 * 1, 64, 64);

				sound("CritterKilledSound", "data/sounds/bounce.wav");
				sound("CritterSpawnedSound", "data/sounds/critter-spawned.wav");
				sound("CritterBounceSound", "data/sounds/bounce.wav");

				font("Font", "data/font.png", "data/font.fnt");
			}
		};

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("insertFace1", Keys.NUM_1);
				monitorKey("insertFace2", Keys.NUM_2);
				monitorKey("insertFace3", Keys.NUM_3);
			}
		};

		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();

		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		renderLayers.add(new RenderLayer(-100, 100, worldCamera));

		controller = new FaceHuntControllerImpl(new LibgdxPointer(0, worldCamera), new LibgdxPointer(1, worldCamera));

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);
		bodyBuilder = new BodyBuilder(physicsWorld);
		box2dDebugRenderer = new Box2DDebugRenderer();

		world = new World();

		worldWrapper = new WorldWrapper(world);
		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new SpriteRendererSystem(renderLayers));
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(new BounceSmallVelocityFixSystem());
		worldWrapper.addUpdateSystem(new HitDetectionSystem());
		worldWrapper.addUpdateSystem(new TimerSystem());
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.addUpdateSystem(new IntermittentInvulnerabilitySystem());
		worldWrapper.addUpdateSystem(new FaceHuntControllerSystem());
		worldWrapper.addUpdateSystem(new RandomMovementBehaviorSystem());
		worldWrapper.init();

		templates = new Templates(world, bodyBuilder);

		float worldWidth = viewportWidth * 1 / cameraData.getZoom();
		float worldHeight = viewportHeight * 1 / cameraData.getZoom();

		createBorder(worldWidth * 0.5f, 0, worldWidth, 0.1f);
		createBorder(worldWidth * 0.5f, worldHeight, worldWidth, 0.1f);
		createBorder(0, worldHeight * 0.5f, 0.1f, worldHeight);
		createBorder(worldWidth, worldHeight * 0.5f, 0.1f, worldHeight);

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");

		createStaticSprite(backgroundSprite, 0f, 0f, viewportWidth, viewportHeight, 0f, -101, 0f, 0f, Color.WHITE);

		world.loopStart();
	}

	BodyBuilder getBodyBuilder() {
		return bodyBuilder;
	}

	void createStaticSprite(Sprite sprite, float x, float y, float width, float height, float angle, int layer, float centerx, float centery, Color color) {
		Entity entity = world.createEntity();
		templates.staticSpriteTemplate(entity, sprite, x, y, width, height, angle, layer, centerx, centery, color);
		entity.refresh();
	}

	void createBorder(float x, float y, float w, float h) {
		Entity entity = world.createEntity();
		templates.staticBoxTemplate(entity, x, y, w, h);
		entity.refresh();
	}

	void createFaceFirstType(Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, final int aliveTime, Color color) {
		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);

		final Color faceColor = new Color(color);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500), new TransitionEventHandler<Color>() {
			@Override
			public void onTransitionFinished(Transition<Color> transition) {
				Synchronizers.transition(faceColor, Transitions.transitionBuilder(showColor).end(hideColor).time(aliveTime - 500));
			}
		});

		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, aliveTime, faceColor);
		entity.refresh();
	}

	void createFaceSecondType(Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, final int aliveTime) {
		Color color = new Color(0f, 1f, 0f, 1f);

		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);

		final Color faceColor = new Color(color);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500), new TransitionEventHandler<Color>() {
			@Override
			public void onTransitionFinished(Transition<Color> transition) {
				Synchronizers.transition(faceColor, Transitions.transitionBuilder(showColor).end(hideColor).time(aliveTime - 500));
			}
		});

		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, aliveTime, faceColor);
		randomMovementFaceTemplate(entity, 500);
		entity.refresh();
	}

	void simpleFaceTemplate(Entity entity, Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, final int aliveTime, Color color) {

		Trigger hitTrigger = new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				Sound sound = resourceManager.getResourceValue("CritterBounceSound");
				sound.play();
				return false;
			}
		};

		Trigger timerTrigger = new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				world.deleteEntity(e);
				gameData.lives--;
				return true;
			}
		};

		Trigger touchTrigger = new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				// world.add animation face...
				HealthComponent healthComponent = e.getComponent(HealthComponent.class);
				Container health = healthComponent.getHealth();
				float damagePerMs = 10f / 1000f; // 10 damage per second

				float damage = damagePerMs * (float) world.getDelta() * (1f - healthComponent.getResistance());
				health.remove(damage);

				if (!health.isEmpty())
					return false;

				SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
				Spatial spatial = spatialComponent.getSpatial();

				SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
				Color currentColor = spriteComponent.getColor();

				createDeadFace(spatial, 6, 1500, currentColor);

				world.deleteEntity(e);

				PointsComponent pointsComponent = e.getComponent(PointsComponent.class);
				if (pointsComponent != null) {
					gameData.points += pointsComponent.getPoints();
				}

				Sound sound = resourceManager.getResourceValue("CritterKilledSound");
				sound.play();

				return true;
			}
		};

		templates.faceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, aliveTime, new Container(0.1f, 0.1f), 0f, color, hitTrigger, timerTrigger);
		templates.touchableTemplate(entity, controller, spatial.getWidth() * 0.15f, touchTrigger);
	}

	void randomMovementFaceTemplate(Entity entity, int randomMovementTime) {
		entity.addComponent(new RandomMovementBehaviorComponent(randomMovementTime));
	}

	void createFaceInvulnerableType(Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, final int aliveTime) {
		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, aliveTime, new Color(1f, 0f, 0f, 0f));
		templates.invulnerableFaceTemplate(entity, new Color(1f, 1f, 0f, 1f), new Color(1f, 0f, 0f, 1f), 2000);
		entity.refresh();
	}

	private String[] partsIds = new String[] { "Part01", "Part02", "Part03", "Part04", "Part05" };

	private Templates templates;

	private Sprite getRandomFacePart() {
		int partIndex = MathUtils.random(partsIds.length - 1);
		return resourceManager.getResourceValue(partsIds[partIndex]);
	}

	void createDeadFace(Spatial spatial, int count, final int aliveTime, Color color) {
		float angle = MathUtils.random(0f, 360f);
		float angleIncrement = 360f / count;
		for (int i = 0; i < count; i++) {
			Entity e = world.createEntity();
			templates.facePartTemplate(e, getRandomFacePart(), spatial, aliveTime, color, angle);
			e.refresh();
			angle += angleIncrement;
		}
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldCamera.zoom(cameraData.getZoom());
		worldCamera.move(cameraData.getX(), cameraData.getY());
		worldCamera.rotate(cameraData.getAngle());
		worldWrapper.render();

		if (Gdx.input.isKeyPressed(Keys.D))
			box2dDebugRenderer.render(physicsWorld);
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		controller.update(delta);
		inputDevicesMonitor.update();
		worldWrapper.update(delta);

		if (inputDevicesMonitor.getButton("insertFace1").isPressed()) {
			mousePosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			worldCamera.unproject(mousePosition);
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			createFaceFirstType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, linearImpulse, 0f, 10000, new Color(1f, 1f, 0f, 1f));
		}

		if (inputDevicesMonitor.getButton("insertFace2").isPressed()) {
			mousePosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			worldCamera.unproject(mousePosition);
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			createFaceSecondType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, linearImpulse, 0f, 10000);
		}

		if (inputDevicesMonitor.getButton("insertFace3").isPressed()) {
			mousePosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			worldCamera.unproject(mousePosition);
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			createFaceInvulnerableType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, linearImpulse, 0f, 10000);
		}

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE))
			game.transition(game.menuScreen);

	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void pause() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		physicsWorld.dispose();
	}

}