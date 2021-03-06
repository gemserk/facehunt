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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.systems.HitDetectionSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.artemis.systems.ScriptSystem;
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
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.input.LibgdxPointer;
import com.gemserk.commons.gdx.sounds.SoundPlayer;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.components.ComponentWrapper;
import com.gemserk.games.facehunt.components.HealthComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;
import com.gemserk.games.facehunt.controllers.FaceHuntControllerImpl;
import com.gemserk.games.facehunt.entities.Templates;
import com.gemserk.games.facehunt.scripts.Scripts.ExplosiveFaceScript;
import com.gemserk.games.facehunt.systems.DamagePlayerSystem;
import com.gemserk.games.facehunt.systems.FaceHuntControllerSystem;
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

	private Templates templates;

	private Entity player;

	private Sprite whiteRectangle;

	private SpriteBatch spriteBatch;
	private SoundPlayer soundPlayer;

	public void setSoundPlayer(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

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

		new GameResourceBuilder(resourceManager);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("insertFace1", Keys.NUM_1);
				monitorKey("insertFace2", Keys.NUM_2);
				monitorKey("insertFace3", Keys.NUM_3);
				monitorKey("insertFace4", Keys.NUM_4);
				monitorKey("insertFace5", Keys.NUM_5);
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
		worldWrapper.addUpdateSystem(new HitDetectionSystem());
		worldWrapper.addUpdateSystem(new TimerSystem());
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.addUpdateSystem(new FaceHuntControllerSystem());
		worldWrapper.addUpdateSystem(new RandomMovementBehaviorSystem());
		worldWrapper.addUpdateSystem(new DamagePlayerSystem());
		worldWrapper.addUpdateSystem(new ScriptSystem());

		worldWrapper.init();

		templates = new Templates(world, bodyBuilder, resourceManager);

		float worldWidth = viewportWidth * 1 / cameraData.getZoom();
		float worldHeight = viewportHeight * 1 / cameraData.getZoom();

		templates.createBorder((worldWidth * 0.5f), 0, worldWidth, 0.1f);
		templates.createBorder((worldWidth * 0.5f), worldHeight, worldWidth, 1f);
		templates.createBorder(0, (worldHeight * 0.5f), 0.1f, worldHeight);
		templates.createBorder(worldWidth, (worldHeight * 0.5f), 0.1f, worldHeight);

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		whiteRectangle = resourceManager.getResourceValue("OverlaySprite");

		templates.createStaticSprite(backgroundSprite, 0f, 0f, viewportWidth, viewportHeight, 0f, (-101), 0f, 0f, Color.WHITE);

		player = world.createEntity();
		player.setTag("Player");
		player.addComponent(new HealthComponent(new Container(100f, 100f), 0f));
		player.refresh();

		world.loopStart();

		spriteBatch = new SpriteBatch();
	}

	BodyBuilder getBodyBuilder() {
		return bodyBuilder;
	}

	private Trigger getFaceDeadHandler() {
		return new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				PointsComponent pointsComponent = e.getComponent(PointsComponent.class);

				if (pointsComponent != null) {
					gameData.points += pointsComponent.getPoints();
					Gdx.app.log("FaceHunt", "points = " + gameData.points);
				}

				HealthComponent healthComponent = player.getComponent(HealthComponent.class);
				Container health = healthComponent.getHealth();
				health.add(5f);

				Sound sound = resourceManager.getResourceValue("CritterKilledSound");
				soundPlayer.play(sound);

				return true;
			}
		};
	}

	private Trigger getMedicFaceTouchTrigger() {
		return new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				HealthComponent healthComponent = player.getComponent(HealthComponent.class);
				Container health = healthComponent.getHealth();
				health.remove(20f);
				Sound sound = resourceManager.getResourceValue("CritterKilledSound");
				soundPlayer.play(sound);
				return true;
			}
		};
	}

	private Trigger getFaceHitTrigger() {
		return new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				Sound sound = resourceManager.getResourceValue("CritterBounceSound");
				soundPlayer.play(sound);
				return false;
			}
		};
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldCamera.zoom(cameraData.getZoom());
		worldCamera.move(cameraData.getX(), cameraData.getY());
		worldCamera.rotate(cameraData.getAngle());
		worldWrapper.render();

		spriteBatch.begin();
		HealthComponent healthComponent = player.getComponent(HealthComponent.class);
		Container health = healthComponent.getHealth();
		FaceHuntRenderUtils.renderBar(spriteBatch, whiteRectangle, health, (Gdx.graphics.getWidth() * 0.3f), (Gdx.graphics.getHeight() - 25), (Gdx.graphics.getWidth() * 0.6f), 10f);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		controller.update(delta);
		inputDevicesMonitor.update();
		worldWrapper.update(delta);

		mousePosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		worldCamera.unproject(mousePosition);

		if (inputDevicesMonitor.getButton("insertFace1").isReleased()) {
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			templates.createFaceFirstType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, controller, linearImpulse, 0f, new Color(1f, 1f, 0f, 1f), getFaceHitTrigger(), getFaceDeadHandler());
		}

		if (inputDevicesMonitor.getButton("insertFace2").isReleased()) {
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			templates.createFaceSecondType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, controller, linearImpulse, 0f, getFaceHitTrigger(), getFaceDeadHandler());
		}

		if (inputDevicesMonitor.getButton("insertFace3").isReleased()) {
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			templates.createFaceInvulnerableType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, controller, linearImpulse, 0f, getFaceHitTrigger(), getFaceDeadHandler());
		}

		if (inputDevicesMonitor.getButton("insertFace4").isReleased()) {
			Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));

			templates.createMedicFaceType(new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f), sprite, controller, linearImpulse, 0f, getFaceHitTrigger(), getMedicFaceTouchTrigger());
		}

		if (inputDevicesMonitor.getButton("insertFace5").isReleased()) {
			Entity e = world.createEntity();
			Spatial spatial = new SpatialImpl(mousePosition.x, mousePosition.y, 1f, 1f, 0f);

			templates.explosiveFaceTemplate(e, spatial, new ExplosiveFaceScript(templates, resourceManager, soundPlayer), controller);

			Vector2 linearImpulse = new Vector2(1f, 0f);
			linearImpulse.rotate(MathUtils.random(360f));
			linearImpulse.mul(MathUtils.random(1f, 5f));
			
			PhysicsComponent physics = ComponentWrapper.getPhysics(e);
			Body body = physics.getBody();

			body.applyLinearImpulse(linearImpulse, body.getTransform().getPosition());
			body.setAngularVelocity(0f * MathUtils.degreesToRadians);
		}

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE))
			game.transition(game.menuScreen, true, 1000);

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
		spriteBatch.dispose();
	}

}