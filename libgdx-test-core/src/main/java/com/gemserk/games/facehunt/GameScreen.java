package com.gemserk.games.facehunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.templates.RegistrableTemplateProvider;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.games.facehunt.components.DefaultParametersBuilder;
import com.gemserk.games.facehunt.components.MovementComponent;
import com.gemserk.games.facehunt.components.RenderComponent;
import com.gemserk.games.facehunt.components.RotateComponent;
import com.gemserk.games.facehunt.components.SpawnerComponent;
import com.gemserk.games.facehunt.entities.FadeAnimationTemplate;
import com.gemserk.games.facehunt.entities.SpawnerEntityTemplate;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.entities.TouchableEntityTemplate;
import com.gemserk.games.facehunt.values.Movement;
import com.gemserk.games.facehunt.values.Spatial;
import com.gemserk.games.facehunt.values.Spawner;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class GameScreen extends ScreenAdapter {

	private final Game game;

	private Texture background;

	private SpriteBatch spriteBatch;

	private Texture happyFace;

	private Texture sadFace;

	private Sound sound;

	EntityManager entityManager;

	private RegistrableTemplateProvider templateProvider;

	public GameScreen(Game game) {
		this.game = game;
		background = new Texture(Gdx.files.internal("data/background01-1024x512.jpg"));

		happyFace = new Texture(Gdx.files.internal("data/face-happy-64x64.png"));
		sadFace = new Texture(Gdx.files.internal("data/face-sad-64x64.png"));

		spriteBatch = new SpriteBatch();
		sound = Gdx.audio.newSound(Gdx.files.internal("data/shot.ogg"));
		bounceSound = Gdx.audio.newSound(Gdx.files.internal("data/bounce.wav"));

		templateProvider = new RegistrableTemplateProvider();

		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(TemplateProvider.class).toInstance(templateProvider);
			}
		});

		entityManager = injector.getInstance(EntityManager.class);

		Provider<JavaEntityTemplate> javaEntityTemplateProvider = injector.getProvider(JavaEntityTemplate.class);

		templateProvider.add("Touchable", javaEntityTemplateProvider.get().with(new TouchableEntityTemplate()));
		templateProvider.add("FadeAnimation", javaEntityTemplateProvider.get().with(new FadeAnimationTemplate()));
		templateProvider.add("Spawner", javaEntityTemplateProvider.get().with(new SpawnerEntityTemplate()));

		JavaEntityTemplate javaEntityTemplate = new JavaEntityTemplate();
		javaEntityTemplate.setInjector(injector);

		final World world = new World(new Vector2(0, 0), new Vector2(800, 480));

		final Color startColor = new Color(1f, 1f, 1f, 0f);
		final Color endColor = new Color(1f, 1f, 1f, 1f);

		entityManager.addEntity(templateProvider.getTemplate("Spawner").instantiate("global.spawner", new HashMap<String, Object>() {
			{
				put("spawner", new Spawner(templateProvider.getTemplate("FadeAnimation"), new HashMap<String, Object>() {
					{
						put("image", happyFace);
						put("startColor", startColor);
						put("endColor", endColor);
						put("shouldSpawn", true);
					}
				}, new FaceDefaultParametersBuilder(world), 10));
			}
		}));

		movementComponent = new MovementComponent("movement", world, bounceSound);
		renderComponent = new RenderComponent();
		rotateComponent = new RotateComponent("rotate");
		detectTouchAndKillComponent = new DetectTouchAndKillComponent(entityManager, templateProvider, sound, sadFace);
		spawnerComponent = new SpawnerComponent("spawner", entityManager, world);

		identity = new Matrix4().idt();
	}

	Matrix4 identity = new Matrix4();

	@Override
	public void render(float delta) {
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setTransformMatrix(identity);
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(background, centerX - 800 / 2, centerY - 480 / 2);
		spriteBatch.end();

		ArrayList<Entity> entities = entityManager.getEntities();

		for (int i = 0; i < entities.size(); i++) {
			final Entity entity = entities.get(i);

			// make some logic for the entity

			detectTouchAndKillComponent.detectTouchAndKill(entity, delta);
			movementComponent.update(entity, delta);
			rotateComponent.update(entity, delta);
			spawnerComponent.update(entity, delta);

			if (entity.hasTag("animation")) {

				Color color = Properties.getValue(entity, "color");
				Color endColor = Properties.getValue(entity, "endColor");

				if (color.equals(endColor)) {
					Boolean shouldSpawn = Properties.getValue(entity, "shouldSpawn");
					if (shouldSpawn) {
						final Spatial spatial = Properties.getValue(entity, "spatial");
						final Movement movement = Properties.getValue(entity, "movement");

						entityManager.addEntity(templateProvider.getTemplate("Touchable").instantiate("touchable." + entity.getId(), new HashMap<String, Object>() {
							{
								put("spatial", new Spatial().set(spatial));
								put("movement", new Movement().set(movement));
								put("image", happyFace);
							}
						}));
					}
					entityManager.remove(entity);
				}

			}

			renderComponent.render(entity, spriteBatch);
		}

	}

	static class FaceDefaultParametersBuilder implements DefaultParametersBuilder {

		private final World world;

		private static Random random = new Random();

		FaceDefaultParametersBuilder(World world) {
			this.world = world;
		}

		@Override
		public Map<String, Object> buildParameters(Map<String, Object> parameters) {
			// should be outside...
			final Vector2 position = Vector2Random.vector2(world.min.x + 10, world.min.y + 10, world.max.x - 10, world.max.y - 10);
			final Vector2 velocity = Vector2Random.vector2(-1f, -1f, 1f, 1f).mul(100f);
			final float angle = random.nextFloat() * 360;

			parameters.put("spatial", new Spatial(position, angle));
			parameters.put("movement", new Movement(velocity));

			return parameters;
		}
	}

	public static class DetectTouchAndKillComponent {

		TemplateProvider templateProvider;

		EntityManager entityManager;

		private final Sound sound;

		private final Texture image;

		Color endColor = new Color(1f, 1f, 1f, 0f);

		public DetectTouchAndKillComponent(EntityManager entityManager, TemplateProvider templateProvider, Sound sound, Texture image) {
			this.entityManager = entityManager;
			this.templateProvider = templateProvider;
			this.sound = sound;
			this.image = image;
		}

		protected void detectTouchAndKill(final Entity entity, float delta) {

			if (!entity.hasTag(Tags.TOUCHABLE))
				return;
			
			if (!Gdx.input.justTouched())
				return;

			int x = Gdx.input.getX();
			int y = Gdx.graphics.getHeight() - Gdx.input.getY();

			final Spatial spatial = Properties.getValue(entity, "spatial");
			Vector2 position = spatial.position;

			if (position.dst(x, y) < 32f) {
				sound.play(1f);
				
				entityManager.remove(entity);

				final Movement movement = Properties.getValue(entity, "movement");
				final Color color = Properties.getValue(entity, "color");

				entityManager.addEntity(templateProvider.getTemplate("FadeAnimation").instantiate("animation." + entity.getId(), new HashMap<String, Object>() {
					{
						put("spatial", new Spatial().set(spatial));
						put("movement", new Movement().set(movement));
						put("image", image);
						put("startColor", color);
						put("endColor", endColor);
					}
				}));

			}
		}

	}

	RenderComponent renderComponent;

	MovementComponent movementComponent;

	RotateComponent rotateComponent;

	private DetectTouchAndKillComponent detectTouchAndKillComponent;

	private SpawnerComponent spawnerComponent;

	private Sound bounceSound;

	@Override
	public void show() {
		Gdx.app.log(PlatformGame.applicationName, "entered game screen");
	}

	@Override
	public void dispose() {
		background.dispose();
		happyFace.dispose();
		sadFace.dispose();
		sound.dispose();
	}

}