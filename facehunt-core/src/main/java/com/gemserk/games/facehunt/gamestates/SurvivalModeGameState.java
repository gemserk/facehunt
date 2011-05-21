package com.gemserk.games.facehunt.gamestates;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.Spatial;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpatialImpl;
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
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.input.LibgdxPointer;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.facehunt.EnemySpawnInfo;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.Spawner;
import com.gemserk.games.facehunt.components.HealthComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;
import com.gemserk.games.facehunt.controllers.FaceHuntControllerImpl;
import com.gemserk.games.facehunt.entities.Templates;
import com.gemserk.games.facehunt.systems.BounceSmallVelocityFixSystem;
import com.gemserk.games.facehunt.systems.DamagePlayerSystem;
import com.gemserk.games.facehunt.systems.FaceHuntControllerSystem;
import com.gemserk.games.facehunt.systems.IntermittentInvulnerabilitySystem;
import com.gemserk.games.facehunt.systems.RandomMovementBehaviorSystem;
import com.gemserk.games.facehunt.values.GameData;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class SurvivalModeGameState extends GameStateImpl {

	static class Wave {

		EnemySpawnInfo[] types;

	}

	enum InternalGameState {
		INTRO, PLAYING, PREPARE_INTRO
	}

	public static interface Function {

		float f(float x);

	}

	private final FaceHuntGame game;

	private ResourceManager<String> resourceManager;

	private SpriteBatch spriteBatch;

	private Libgdx2dCameraTransformImpl worldCamera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

	private Camera cameraData;

	private WorldWrapper worldWrapper;

	private World world;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	private BodyBuilder bodyBuilder;

	private Box2DDebugRenderer box2dDebugRenderer;

	private FaceHuntController controller;

	private GameData gameData;

	private BitmapFont font;

	private Wave[] waves;

	private int currentWaveIndex;

	private Wave currentWave;

	private Spawner spawner;

	private Templates templates;

	private Entity player;

	private Sprite whiteRectangle;

	private Function velocityIncrementFunction = new Function() {
		public float f(float x) {
			if (x > 4f * 60f)
				return 16f;
			float nx = x / 60f;
			float fx = (float) Math.pow(2, nx);
			return fx;
		}
	};

	public SurvivalModeGameState(FaceHuntGame game) {
		this.game = game;
		this.gameData = new GameData();
	}

	public void restartGame() {

		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		worldCamera.center(0f, 0f);

		cameraData = new CameraImpl(0f, 0f, 64f, 0f);

		gameData.gameOver = false;
		gameData.points = 0;
		gameData.gameTime = 0;

		resourceManager = new ResourceManagerImpl<String>();

		new GameResourceBuilder(resourceManager);

		spriteBatch = new SpriteBatch();

		font = resourceManager.getResourceValue("Font");

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
		worldWrapper.addUpdateSystem(new DamagePlayerSystem());
		worldWrapper.init();

		templates = new Templates(world, bodyBuilder);

		float worldWidth = viewportWidth * 1 / cameraData.getZoom();
		float worldHeight = viewportHeight * 1 / cameraData.getZoom();

		templates.createBorder((worldWidth * 0.5f), 0, worldWidth, 0.1f);
		templates.createBorder((worldWidth * 0.5f), worldHeight, worldWidth, 1f);
		templates.createBorder(0, (worldHeight * 0.5f), 0.1f, worldHeight);
		templates.createBorder(worldWidth, (worldHeight * 0.5f), 0.1f, worldHeight);

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		whiteRectangle = resourceManager.getResourceValue("OverlaySprite");

		templates.createStaticSprite(backgroundSprite, 0f, 0f, viewportWidth, viewportHeight, 0f, (-101), 0f, 0f, Color.WHITE);

		createFirstTypeFaceSpawner(new Rectangle(1, 1, worldWidth - 2, worldHeight - 2));

		player = world.createEntity();
		player.setTag("Player");
		player.addComponent(new HealthComponent(new Container(100f, 100f), 0f));
		player.refresh();

		world.loopStart();

		waves = new Wave[] { new Wave() {
			{
				types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 10000, 0.4f), new EnemySpawnInfo(1, 10000, 0.3f), new EnemySpawnInfo(2, 10000, 0.3f), };
			}
		}, };

		currentWaveIndex = 0;

		currentWave = waves[currentWaveIndex];
		spawner = new Spawner(currentWave.types);

	}

	BodyBuilder getBodyBuilder() {
		return bodyBuilder;
	}

	void createFirstTypeFaceSpawner(final Rectangle spawnArea) {
		Entity entity = world.createEntity();
		final int minTime = 1000;
		final int maxTime = 2000;
		entity.addComponent(new TimerComponent(MathUtils.random(minTime, maxTime), new AbstractTrigger() {
			@Override
			public boolean handle(Entity e) {

				if (spawner.isEmpty())
					return false;

				float probability = MathUtils.random(1f);
				int type = spawner.getType(probability);

				spawner.remove(type, 1);

				TimerComponent timerComponent = e.getComponent(TimerComponent.class);
				timerComponent.reset();
				timerComponent.setCurrentTime(MathUtils.random(minTime, maxTime));

				float x = MathUtils.random(spawnArea.x, spawnArea.width);
				float y = MathUtils.random(spawnArea.y, spawnArea.height);

				float angularVelocity = MathUtils.random(30f, 180f);

				if (MathUtils.randomBoolean())
					angularVelocity = -angularVelocity;

				float increment = velocityIncrementFunction.f(gameData.gameTime * 0.001f);

				Vector2 linearVelocity = new Vector2(0f, 0f);
				linearVelocity.x = MathUtils.random(1f, 3f);
				linearVelocity.mul(increment);

				Gdx.app.log("FaceHunt", "spawn.vel: " + linearVelocity.len());

				linearVelocity.rotate(MathUtils.random(0f, 360f));

				Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

				Spatial spatial = new SpatialImpl(x, y, 1f, 1f, 0f);

				if (type == 0)
					templates.createFaceFirstType(spatial, sprite, controller, linearVelocity, angularVelocity, new Color(1f, 1f, 0f, 1f), getFaceHitTrigger(), getFaceTouchTrigger());
				else if (type == 1)
					templates.createFaceSecondType(spatial, sprite, controller, linearVelocity, angularVelocity, getFaceHitTrigger(), getFaceTouchTrigger());
				else if (type == 2)
					templates.createFaceInvulnerableType(spatial, sprite, controller, linearVelocity, angularVelocity, getFaceHitTrigger(), getFaceTouchTrigger());

				Sound sound = resourceManager.getResourceValue("CritterSpawnedSound");
				sound.play();

				return false;
			}
		}));
		entity.refresh();
	}

	private Trigger getFaceTouchTrigger() {
		return new AbstractTrigger() {
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

				healthComponent = player.getComponent(HealthComponent.class);
				health = healthComponent.getHealth();
				health.add(5f);

				Sound sound = resourceManager.getResourceValue("CritterKilledSound");
				sound.play();

				return true;
			}
		};
	}

	private Trigger getFaceHitTrigger() {
		return new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				Sound sound = resourceManager.getResourceValue("CritterBounceSound");
				sound.play();
				return false;
			}
		};
	}

	private String[] partsIds = new String[] { "Part01", "Part02", "Part03", "Part04", "Part05" };

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

		spriteBatch.begin();

		HealthComponent healthComponent = player.getComponent(HealthComponent.class);
		Container health = healthComponent.getHealth();
		FaceHuntRenderUtils.renderBar(spriteBatch, whiteRectangle, health, (Gdx.graphics.getWidth() * 0.3f), (Gdx.graphics.getHeight() - 25), (Gdx.graphics.getWidth() * 0.6f), 10f);

		font.setColor(Color.RED);
		font.draw(spriteBatch, "Points: " + gameData.points, 10, Gdx.graphics.getHeight());

		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		controller.update(delta);

		worldWrapper.update(delta);
		gameData.gameTime += delta;

		if (spawner.isEmpty()) {
			currentWaveIndex++;
			currentWave = waves[currentWaveIndex];
			spawner = new Spawner(currentWave.types);
		}

		HealthComponent healthComponent = player.getComponent(HealthComponent.class);
		Container health = healthComponent.getHealth();

		if (health.isEmpty()) {
			gameData.gameOver = true;
			game.pauseGameState.setGameData(gameData);
			game.pauseGameState.setPreviousScreen(game.gameScreen);
			game.transition(game.scoreScreen);
		}

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.pauseGameState.setGameData(gameData);
			game.pauseGameState.setPreviousScreen(game.gameScreen);
			game.transition(game.scoreScreen);
		}

	}

	@Override
	public void init() {
		if (gameData.gameOver)
			restartGame();
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
		spriteBatch.dispose();
		spriteBatch = null;
		physicsWorld.dispose();
		gameData.gameOver = true;
	}

}