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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.commons.values.FloatValue;
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
import com.gemserk.games.facehunt.values.GameData;
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

	EntityManager entityManager;

	private RegistrableTemplateProvider templateProvider;

	public GameScreen(Game game) {
		this.game = game;
		background = new Texture(Gdx.files.internal("data/background01-1024x512.jpg"));

		happyFace = new Texture(Gdx.files.internal("data/face-happy-64x64.png"));
		sadFace = new Texture(Gdx.files.internal("data/face-sad-64x64.png"));

		spriteBatch = new SpriteBatch();
		bounceSound = Gdx.audio.newSound(Gdx.files.internal("data/bounce.wav"));

		Sound critterKilledSound = Gdx.audio.newSound(Gdx.files.internal("data/critter-killed.wav"));
		Sound critterSpawnedSound = Gdx.audio.newSound(Gdx.files.internal("data/critter-spawned.wav"));

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
				put("respawnTime", new FloatValue(3000f));
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

		gameData = new GameData();
		gameData.lives = 2;

		movementComponent = new MovementComponent("movement", world, bounceSound);
		renderComponent = new RenderComponent();
		rotateComponent = new RotateComponent("rotate");
		detectTouchAndKillComponent = new DetectTouchAndKillComponent(entityManager, templateProvider, critterKilledSound, sadFace, gameData);
		spawnerComponent = new SpawnerComponent("spawner", entityManager, world, critterSpawnedSound);

		identity = new Matrix4().idt();

		font = new BitmapFont();
		font.setColor(0f, 0f, 0f, 0.8f);
		// font.setScale(1f, 1.5f);

		fadeInColor = Transitions.transition(new Color(1f, 1f, 1f, 0f), LibgdxConverters.color());
		fadeInColor.set(new Color(1f, 1f, 1f, 1f), 2000);
	}

	Matrix4 identity = new Matrix4();

	enum GameState {
		Starting, Playing, GameOver
	}

	GameState gameState = GameState.Starting;

	@Override
	public void render(float delta) {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (gameState == GameState.Starting) {

			spriteBatch.setTransformMatrix(identity);
			spriteBatch.begin();
			spriteBatch.setColor(fadeInColor.get());
			spriteBatch.draw(background, 0, 0);
			spriteBatch.end();

			if (!fadeInColor.isTransitioning())
				gameState = GameState.Playing;

		} else if (gameState == GameState.Playing) {

			spriteBatch.setTransformMatrix(identity);
			spriteBatch.begin();
			spriteBatch.setColor(Color.WHITE);
			spriteBatch.draw(background, 0, 0);
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
							final FloatValue aliveTime = Properties.getValue(entity, "aliveTime");

							entityManager.addEntity(templateProvider.getTemplate("Touchable").instantiate("touchable." + entity.getId(), new HashMap<String, Object>() {
								{
									put("spatial", new Spatial().set(spatial));
									put("movement", new Movement().set(movement));
									put("image", happyFace);
									put("aliveTime", aliveTime);
								}
							}));
						}
						entityManager.remove(entity);
					}

				}

				if (entity.hasTag(Tags.TOUCHABLE)) {

					FloatValue aliveTime = Properties.getValue(entity, "aliveTime");

					// aliveTime.value -= 1f * delta;

					if (aliveTime.value <= 0f) {
						gameData.lives--;
						entityManager.remove(entity);

						if (gameData.lives <= 0) {

							fadeInColor.set(new Color(1f, 1f, 1f, 0f), 2000);
							gameState = GameState.GameOver;

						}
					}

				}

				renderComponent.render(entity, spriteBatch);
			}

			spriteBatch.setTransformMatrix(identity);
			spriteBatch.begin();

			font.setColor(0f, 0f, 0f, 1f);
			
			String str = "Killed: " + gameData.killedCritters;
			TextBounds textBounds = font.getBounds(str);
			font.draw(spriteBatch, str, 10, height - 20);

			str = "Lives: " + gameData.lives;
			textBounds = font.getBounds(str);
			font.draw(spriteBatch, str, width - textBounds.width - 10, height - 20);

			spriteBatch.end();

		} else if (gameState == GameState.GameOver) {

			spriteBatch.setTransformMatrix(identity);
			spriteBatch.begin();
			spriteBatch.setColor(fadeInColor.get());
			spriteBatch.draw(background, 0, 0);

			String str = "Game Over";
			TextBounds textBounds = font.getBounds(str);
			font.setColor(1f, 1f, 1f, 1f);
			font.draw(spriteBatch, str, centerX - textBounds.width / 2, centerY - textBounds.height / 2);

			spriteBatch.end();

			if (!fadeInColor.isTransitioning()) {
				// go to another scene
				// restart game! not this ->
				gameState = GameState.Starting;
				fadeInColor.set(new Color(1f, 1f, 1f, 1f), 2000);

				entityManager.removeAll(Tags.SPATIAL);

				gameData.killedCritters = 0;
				gameData.lives = 2;
			}

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
			Vector2 position = Vector2Random.vector2(world.min.x + 10, world.min.y + 10, world.max.x - 10, world.max.y - 10);
			Vector2 velocity = Vector2Random.vector2(-1f, -1f, 1f, 1f).mul(100f);
			float angle = random.nextFloat() * 360;

			// I want this to be dynamic based on the player's performance.
			float minAliveTime = 6000f;
			float maxAliveTime = 10000f;

			float aliveTime = minAliveTime + random.nextFloat() * (maxAliveTime - minAliveTime);

			parameters.put("spatial", new Spatial(position, angle));
			parameters.put("movement", new Movement(velocity));
			parameters.put("aliveTime", new FloatValue(aliveTime));

			return parameters;
		}
	}

	public static class DetectTouchAndKillComponent {

		TemplateProvider templateProvider;

		EntityManager entityManager;

		private final Sound sound;

		private final Texture image;

		Color endColor = new Color(1f, 1f, 1f, 0f);

		private final GameData gameData;

		public DetectTouchAndKillComponent(EntityManager entityManager, TemplateProvider templateProvider, Sound sound, Texture image, GameData gameData) {
			this.entityManager = entityManager;
			this.templateProvider = templateProvider;
			this.sound = sound;
			this.image = image;
			this.gameData = gameData;
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

			if (position.dst(x, y) > 32f)
				return;

			sound.play(1f);
			entityManager.remove(entity);
			gameData.killedCritters++;

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

	RenderComponent renderComponent;

	MovementComponent movementComponent;

	RotateComponent rotateComponent;

	private DetectTouchAndKillComponent detectTouchAndKillComponent;

	private SpawnerComponent spawnerComponent;

	private Sound bounceSound;

	private GameData gameData;

	private BitmapFont font;

	private Transition<Color> fadeInColor;

	@Override
	public void show() {
		Gdx.app.log(PlatformGame.applicationName, "entered game screen");
	}

	@Override
	public void dispose() {
		background.dispose();
		happyFace.dispose();
		sadFace.dispose();

		// TODO: dispose sounds
	}

}