package com.gemserk.games.facehunt;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.gemserk.commons.values.FloatValue;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.templates.RegistrableTemplateProvider;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.gemserk.games.facehunt.components.MovementComponent;
import com.gemserk.games.facehunt.components.RenderComponent;
import com.gemserk.games.facehunt.entities.FadeAnimationTemplate;
import com.gemserk.games.facehunt.entities.TouchableEntityTemplate;
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

		JavaEntityTemplate javaEntityTemplate = new JavaEntityTemplate();
		javaEntityTemplate.setInjector(injector);

		final World world = new World(new Vector2(0, 0), new Vector2(800, 480));

		final Color startColor = new Color(1f, 1f, 1f, 0f);
		final Color endColor = new Color(1f, 1f, 1f, 1f);

		Random random = new Random();

		for (int i = 0; i < 20; i++) {

			final int entityIndex = i;

			final Vector2 position = Vector2Random.vector2(world.min, world.max);
			final Vector2 velocity = Vector2Random.vector2(-1f, -1f, 1f, 1f).mul(100f);
			final float randomAngle = random.nextFloat() * 360;

			entityManager.addEntity(templateProvider.getTemplate("FadeAnimation").instantiate("animation." + entityIndex, new HashMap<String, Object>() {
				{
					put("position", position);
					put("velocity", velocity);
					put("angle", new FloatValue(randomAngle));
					put("image", happyFace);
					put("startColor", startColor);
					put("endColor", endColor);
					put("shouldSpawn", true);
				}
			}));

		}

		movementComponent = new MovementComponent(world);
		renderComponent = new RenderComponent();

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

			if (entity.hasTag("touchable")) {

				if (Gdx.input.justTouched()) {

					int x = Gdx.input.getX();
					int y = Gdx.graphics.getHeight() - Gdx.input.getY();

					final Vector2 position = Properties.getValue(entity, "position");

					if (position.dst(x, y) < 32f) {
						sound.play(1f);

						Properties.setValue(entity, "dead", true);

						entityManager.remove(entity);

						final FloatValue angle = Properties.getValue(entity, "angle");
						final Vector2 velocity = Properties.getValue(entity, "velocity");

						entityManager.addEntity(templateProvider.getTemplate("FadeAnimation").instantiate("animation." + entity.getId(), new HashMap<String, Object>() {
							{
								put("position", position);
								put("velocity", velocity);
								put("angle", angle);
								put("image", sadFace);
								put("startColor", Color.WHITE);
								put("endColor", new Color(1f, 1f, 1f, 0f));
							}
						}));

					}

				}

			}

			movementComponent.update(entity, delta);

			if (entity.hasTag("animation")) {

				Color color = Properties.getValue(entity, "color");
				Color endColor = Properties.getValue(entity, "endColor");

				if (color.equals(endColor)) {
					Boolean shouldSpawn = Properties.getValue(entity, "shouldSpawn");
					if (shouldSpawn) {
						// passing same instances as parameters, this could be a problem!
						entityManager.addEntity(templateProvider.getTemplate("Touchable").instantiate("touchable." + entity.getId(), new HashMap<String, Object>() {
							{
								put("position", Properties.getValue(entity, "position"));
								put("velocity", Properties.getValue(entity, "velocity"));
								put("angle", Properties.getValue(entity, "angle"));
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

	RenderComponent renderComponent;

	private MovementComponent movementComponent;

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