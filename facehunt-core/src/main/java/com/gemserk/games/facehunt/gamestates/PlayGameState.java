package com.gemserk.games.facehunt.gamestates;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
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
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
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
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.input.LibgdxPointer;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.facehunt.EnemySpawnInfo;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.Groups;
import com.gemserk.games.facehunt.Spawner;
import com.gemserk.games.facehunt.components.HealthComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.components.RandomMovementBehaviorComponent;
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

public class PlayGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private ResourceManager<String> resourceManager;

	private SpriteBatch spriteBatch;

	private Libgdx2dCameraTransformImpl worldCamera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

	private Camera cameraData;

	private WorldWrapper worldWrapper;

	private World world;

	public boolean gameOver = true;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	private BodyBuilder bodyBuilder;

	private Box2DDebugRenderer box2dDebugRenderer;

	private FaceHuntController controller;

	private GameData gameData;

	private BitmapFont font;

	private Color waveIntroductionColor = new Color();

	static class Wave {

		public String[] texts;

		EnemySpawnInfo[] types;

	}

	private Wave[] waves = new Wave[] { new Wave() {
		{
			texts = new String[] { "Don't let the faces escape,\ntouch over them to kill'em.", "Let's practice, kill 15 faces..." };
			types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 15, 1f) };
		}
	}, new Wave() {
		{
			texts = new String[] { "Nicely done but don't celebrate yet,\nmore faces are coming!", "Some of them are too fast..." };
			types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 10, 0.5f), new EnemySpawnInfo(1, 5, 0.5f), };
		}
	}, new Wave() {
		{
			texts = new String[] { "And some of them just don't want to die." };
			types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 10, 0.4f), new EnemySpawnInfo(2, 5, 0.6f), };
		}
	}, new Wave() {
		{
			texts = new String[] { "Well well, it seems like someone\n is improving their skills.", "But, this war is just starting..." };
			types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 1000, 0.4f), new EnemySpawnInfo(1, 1000, 0.3f), new EnemySpawnInfo(2, 1000, 0.3f), };
		}
	}, };

	private int currentWaveIndex;

	private int currentTextIndex;

	private Wave currentWave;

	private Spawner spawner;

	private String currentText;

	enum InternalGameState {
		INTRO, PLAYING, PREPARE_INTRO
	}

	InternalGameState internalGameState;

	public PlayGameState(FaceHuntGame game) {
		this.game = game;
	}

	public void restartGame() {

		gameOver = false;

		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		worldCamera.center(0f, 0f);

		cameraData = new CameraImpl(0f, 0f, 64f, 0f);
		gameData = new GameData();

		gameData.points = 0;

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);

				texture("BackgroundTexture", "data/background01-1024x512.jpg", false);
				texture("HappyFaceTexture", "data/face-happy-64x64.png");
				texture("SadFaceTexture", "data/face-sad-64x64.png");
				texture("FaceSpriteSheet", "data/face-parts.png");
				texture("OverlayTexture", "data/white-rectangle.png");

				sprite("BackgroundSprite", "BackgroundTexture");
				sprite("HappyFaceSprite", "HappyFaceTexture");
				sprite("SadFaceSprite", "SadFaceTexture");
				sprite("OverlaySprite", "OverlayTexture");

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

		createBorder(worldWidth * 0.5f, 0, worldWidth, 0.1f);
		createBorder(worldWidth * 0.5f, worldHeight, worldWidth, 1f);
		createBorder(0, worldHeight * 0.5f, 0.1f, worldHeight);
		createBorder(worldWidth, worldHeight * 0.5f, 0.1f, worldHeight);

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		whiteRectangle = resourceManager.getResourceValue("OverlaySprite");

		createStaticSprite(backgroundSprite, 0f, 0f, viewportWidth, viewportHeight, 0f, -101, 0f, 0f, Color.WHITE);

		createFirstTypeFaceSpawner(new Rectangle(1, 1, worldWidth - 2, worldHeight - 2));
		
		player = world.createEntity();
		player.setTag("Player");
		player.addComponent(new HealthComponent(new Container(100f, 100f), 0f));
		player.refresh();

		world.loopStart();

		currentWaveIndex = 0;
		currentTextIndex = 0;
		currentText = "";

		internalGameState = InternalGameState.PREPARE_INTRO;
		
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

				Vector2 linearVelocity = new Vector2(0f, 0f);
				linearVelocity.x = MathUtils.random(1f, 4f);
				linearVelocity.rotate(MathUtils.random(0f, 360f));

				Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

				Spatial spatial = new SpatialImpl(x, y, 1f, 1f, 0f);

				if (type == 0)
					createFaceFirstType(spatial, sprite, linearVelocity, angularVelocity, new Color(1f, 1f, 0f, 1f));
				else if (type == 1)
					createFaceSecondType(spatial, sprite, linearVelocity, angularVelocity);
				else if (type == 2)
					createFaceInvulnerableType(spatial, sprite, linearVelocity, angularVelocity);

				Sound sound = resourceManager.getResourceValue("CritterSpawnedSound");
				sound.play();

				return false;
			}
		}));
		entity.refresh();
	}

	void createFaceFirstType(Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, Color color) {
		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);

		final Color faceColor = new Color(color);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500));

		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, faceColor);
		entity.refresh();
	}

	void createFaceSecondType(Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity) {
		Color color = new Color(0f, 1f, 0f, 1f);

		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);

		final Color faceColor = new Color(color);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500));

		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, faceColor);
		randomMovementFaceTemplate(entity, 500);
		entity.refresh();
	}

	void simpleFaceTemplate(Entity entity, Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, Color color) {

		Trigger hitTrigger = new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				Sound sound = resourceManager.getResourceValue("CritterBounceSound");
				sound.play();
				return false;
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

				healthComponent = player.getComponent(HealthComponent.class);
				health = healthComponent.getHealth();
				health.add(10f);

				Sound sound = resourceManager.getResourceValue("CritterKilledSound");
				sound.play();

				return true;
			}
		};

		templates.faceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, new Container(0.1f, 0.1f), 0f, color, hitTrigger);
		templates.touchableTemplate(entity, controller, spatial.getWidth() * 0.15f, touchTrigger);
	}

	void randomMovementFaceTemplate(Entity entity, int randomMovementTime) {
		entity.addComponent(new RandomMovementBehaviorComponent(randomMovementTime));
	}

	void createFaceInvulnerableType(Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity) {
		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, new Color(1f, 0f, 0f, 0f));
		templates.invulnerableFaceTemplate(entity, new Color(1f, 1f, 0f, 1f), new Color(1f, 0f, 0f, 1f), 2000);
		entity.refresh();
	}

	private String[] partsIds = new String[] { "Part01", "Part02", "Part03", "Part04", "Part05" };

	private Templates templates;

	private Entity player;

	private Sprite whiteRectangle;

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
		
		renderHealthBar(spriteBatch, whiteRectangle, Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() - 25, Gdx.graphics.getWidth() * 0.6f, 10f);

		font.setColor(Color.RED);
		font.draw(spriteBatch, "Points: " + gameData.points, 10, Gdx.graphics.getHeight());

		if (currentWave != null) {
			font.setColor(waveIntroductionColor);
			SpriteBatchUtils.drawMultilineTextCentered(spriteBatch, font, //
					currentText, (Gdx.graphics.getWidth() * 0.5f), (Gdx.graphics.getHeight() * 0.5f));

			font.setScale(0.7f);
			SpriteBatchUtils.drawMultilineTextCentered(spriteBatch, font, //
					"tap to continue", (Gdx.graphics.getWidth() * 0.8f), (Gdx.graphics.getHeight() * 0.1f));
			font.setScale(1f);
		}

		spriteBatch.end();
	}

	private void renderHealthBar(SpriteBatch spriteBatch, Sprite rectangle, float x, int y, float width, float height) {
		HealthComponent healthComponent = player.getComponent(HealthComponent.class);
		Container health = healthComponent.getHealth();
		renderBar(spriteBatch, rectangle, health, x, y, width, height);
	}

	private void renderBar(SpriteBatch spriteBatch, Sprite rectangle, Container health, float x, int y, float width, float height) {
		float border = 1f;
		
		rectangle.setColor(Color.BLACK);
		rectangle.setPosition(x - border, y - border);
		rectangle.setSize(width + border * 2f, height + border * 2f);
		rectangle.draw(spriteBatch);

		rectangle.setColor(Color.RED);
		rectangle.setPosition(x, y);
		rectangle.setSize(width, height);
		rectangle.draw(spriteBatch);

		rectangle.setColor(Color.GREEN);
		rectangle.setPosition(x, y);
		rectangle.setSize(width * health.getPercentage(), height);
		rectangle.draw(spriteBatch);
	}
	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		controller.update(delta);

		if (internalGameState == InternalGameState.PLAYING) {
			worldWrapper.update(delta);

			ImmutableBag<Entity> faces = world.getGroupManager().getEntities(Groups.FaceGroup);

			// for now, allow N to process next state...
			if (spawner.isEmpty() && faces.isEmpty()) {
				internalGameState = InternalGameState.PREPARE_INTRO;
				currentWaveIndex++;
			}

			if (Gdx.input.isKeyPressed(Keys.N)) {
				internalGameState = InternalGameState.PREPARE_INTRO;
				currentWaveIndex++;
			}

			HealthComponent healthComponent = player.getComponent(HealthComponent.class);
			Container health = healthComponent.getHealth();
			
			if (health.isEmpty()) {
				gameOver = true;
				game.scoreGameState.setGameData(gameData);
				game.transition(game.scoreScreen, true);
			}

		}

		if (internalGameState == InternalGameState.INTRO) {

			if (Gdx.input.justTouched()) {
				currentTextIndex++;
				if (currentTextIndex < currentWave.texts.length)
					currentText = currentWave.texts[currentTextIndex];
			}

			if (currentTextIndex >= currentWave.texts.length) {
				internalGameState = InternalGameState.PLAYING;

				Color endColor = new Color(Color.BLUE);
				endColor.a = 0f;

				Synchronizers.transition(waveIntroductionColor, Transitions.transitionBuilder().time(800).end(endColor));
			}

		}

		if (internalGameState == InternalGameState.PREPARE_INTRO) {
			Color endColor = new Color(Color.BLUE);
			endColor.a = 1f;
			waveIntroductionColor.a = 0f;

			currentTextIndex = 0;

			if (currentWaveIndex >= waves.length) {
				game.transition(game.scoreScreen, true);
				return;
			}

			currentWave = waves[currentWaveIndex];
			currentText = currentWave.texts[currentTextIndex];

			spawner = new Spawner(currentWave.types);

			Synchronizers.transition(waveIntroductionColor, Transitions.transitionBuilder().time(800).end(endColor), //
					new TransitionEventHandler() {
						@Override
						public void onTransitionFinished(Transition transition) {
							internalGameState = InternalGameState.INTRO;
						}
					});

		}

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE))
			game.transition(game.scoreScreen);

	}

	@Override
	public void init() {
		if (gameOver)
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
		gameOver = true;
	}

}