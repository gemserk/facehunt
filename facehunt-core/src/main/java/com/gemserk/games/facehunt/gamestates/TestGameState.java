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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.HitComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.Spatial;
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
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.Groups;
import com.gemserk.games.facehunt.components.FaceControllerComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.components.RandomMovementBehaviorComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;
import com.gemserk.games.facehunt.controllers.FaceHuntControllerImpl;
import com.gemserk.games.facehunt.systems.FaceHuntControllerSystem;
import com.gemserk.games.facehunt.systems.RandomMovementBehaviorSystem;
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

	public TestGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		worldCamera.center(0f, 0f);

		cameraData = new CameraImpl(0f, 0f, 1f, 0f);

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

				sound("CritterKilledSound", "data/critter-killed.wav");
				sound("CritterSpawnedSound", "data/critter-spawned.wav");
				sound("CritterBounceSound", "data/bounce.wav");

				font("Font", "data/font.png", "data/font.fnt");
			}
		};
		
		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {{ 
			monitorMouseRightButton("insertFace");
		}};

		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();
		
		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		renderLayers.add(new RenderLayer(-100, 100, worldCamera));

		controller = new FaceHuntControllerImpl();

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
		worldWrapper.init();

		createBorder(viewportWidth * 0.5f, 0, viewportWidth, 10);
		createBorder(viewportWidth * 0.5f, viewportHeight, viewportWidth, 10);
		createBorder(0, viewportHeight * 0.5f, 10, viewportHeight);
		createBorder(viewportWidth, viewportHeight * 0.5f, 10, viewportHeight);

		Sprite backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");

		createStaticSprite(backgroundSprite, 0f, 0f, viewportWidth, viewportHeight, 0f, -101, 0f, 0f, Color.WHITE);
		
		world.loopStart();
	}

	BodyBuilder getBodyBuilder() {
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
		Body body = getBodyBuilder().type(BodyType.StaticBody).boxShape(w * 0.5f, h * 0.5f).mass(1f)//
				.friction(0f).userData(entity).position(x, y).build();
		entity.addComponent(new PhysicsComponent(body));
		entity.refresh();
	}

	Entity createFaceFirstType(float x, float y, Vector2 linearVelocity, float angularVelocity, final int aliveTime, Color color) {
		Entity entity = world.createEntity();
		entity.setGroup(Groups.FaceGroup);

		Sprite sprite = resourceManager.getResourceValue("SadFaceSprite");

		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);

		final Color faceColor = new Color(color);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500), new TransitionEventHandler<Color>() {
			@Override
			public void onTransitionFinished(Transition<Color> transition) {
				Synchronizers.transition(faceColor, Transitions.transitionBuilder(showColor).end(hideColor).time(aliveTime - 500));
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
		entity.addComponent(new PointsComponent(100));
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
		entity.addComponent(new FaceControllerComponent(controller, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				// world.add animation face...

				SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
				Spatial spatial = spatialComponent.getSpatial();

				SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
				Color currentColor = spriteComponent.getColor();

				createDeadFace(spatial, 10, 1500, currentColor);

				world.deleteEntity(e);

				return true;
			}
		}));

		entity.refresh();
		return entity;
	}

	void createFaceSecondType(float x, float y, Vector2 linearVelocity, float angularVelocity, final int aliveTime) {
		Entity e = createFaceFirstType(x, y, linearVelocity, angularVelocity, aliveTime, new Color(0f, 1f, 0f, 1f));
		e.addComponent(new RandomMovementBehaviorComponent(500));
		e.refresh();
	}

	private String[] partsIds = new String[] {"Part01", "Part02", "Part03", "Part04", "Part05"};

	private Sprite getRandomFacePart() {
		int partIndex = MathUtils.random(4);
		return resourceManager.getResourceValue(partsIds[partIndex]);
	}
	
	void createDeadFace(Spatial spatial, int count, final int aliveTime, Color color) {
		for (int i = 0; i < count; i++) {
			Entity entity = world.createEntity();
			createDeadFacePart(entity, getRandomFacePart(), spatial, aliveTime, color);
		}
	}

	void createDeadFacePart(Entity entity, Sprite sprite, Spatial spatial, final int aliveTime, Color color) {
		entity.setGroup(Groups.FaceGroup);

		Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color faceColor = new Color();

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(color).end(hideColor).time(aliveTime) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()));

		float radius = MathUtils.random(6f, 16f);

		Body body = getBodyBuilder() //
				.type(BodyType.DynamicBody) //
				.circleShape(radius) //
				.mass(1f)//
				.friction(0f)//
				.restitution(1f)//
				.userData(entity)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		Vector2 impulse = new Vector2(1f, 0f);
		impulse.rotate(MathUtils.random(0f, 360f));
		impulse.mul(MathUtils.random(200f, 500f));
		
		// body.applyLinearImpulse(impulse, body.getTransform().getPosition());
		body.setLinearVelocity(impulse);
		body.setAngularVelocity(MathUtils.random(-5f, 5f));

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, radius * 3, radius * 3)));
		entity.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), faceColor));
		entity.addComponent(new TimerComponent(aliveTime, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				world.deleteEntity(e);
				return true;
			}
		}));
		
		entity.refresh();
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
		
		if (inputDevicesMonitor.getButton("insertFace").isPressed()) {
			float x = Gdx.input.getX();
			float y = Gdx.graphics.getHeight() - Gdx.input.getY();
			createFaceFirstType(x, y, new Vector2(10f, 0f), 0f, 10000, Color.WHITE);
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