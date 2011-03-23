package com.gemserk.libgdx.test;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.templates.RegistrableTemplateProvider;
import com.gemserk.componentsengine.templates.TemplateProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class GameScreen extends ScreenAdapter {

	private final Game game;

	private Texture background;

	private SpriteBatch spriteBatch;

	private Texture island;

	private Sound sound;

	static class EntityManager {

		ArrayList<Entity> entities = new ArrayList<Entity>();

		ArrayList<Entity> entitiesToRemove = new ArrayList<Entity>();

		void addEntity(Entity entity) {
			entities.add(entity);
		}

		ArrayList<Entity> getEntities() {
			if (!entitiesToRemove.isEmpty()) {
				entities.removeAll(entitiesToRemove);
				entitiesToRemove.clear();
			}
			return entities;
		}

		public void remove(Entity entity) {
			entitiesToRemove.add(entity);
		}

	}

	EntityManager entityManager = new EntityManager();

	static class World {

		Vector2 min;

		Vector2 max;

		public World(Vector2 min, Vector2 max) {
			this.min = min;
			this.max = max;
		}

	}

	public GameScreen(Game game) {
		this.game = game;
		background = new Texture(Gdx.files.internal("data/background01-1024x512.jpg"));
		island = new Texture(Gdx.files.internal("data/island01-128x128.png"));
		spriteBatch = new SpriteBatch();
		sound = Gdx.audio.newSound(Gdx.files.internal("data/shot.ogg"));

		final RegistrableTemplateProvider templateProvider = new RegistrableTemplateProvider();

		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(TemplateProvider.class).toInstance(templateProvider);
			}
		});

		Provider<JavaEntityTemplate> javaEntityTemplateProvider = injector.getProvider(JavaEntityTemplate.class);

		templateProvider.add("Island", javaEntityTemplateProvider.get().with(new IslandTemplate()));

		JavaEntityTemplate javaEntityTemplate = new JavaEntityTemplate();
		javaEntityTemplate.setInjector(injector);

		final World world = new World(new Vector2(0, 0), new Vector2(800, 480));

		for (int i = 0; i < 20; i++) {

			entityManager.addEntity(templateProvider.getTemplate("Island").instantiate("island01", new HashMap<String, Object>() {
				{
					put("position", Vector2Random.vector2(world.min, world.max));
					put("image", island);
				}
			}));

		}

	}

	@Override
	public void render(float delta) {
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);

		spriteBatch.draw(background, centerX - 800 / 2, centerY - 480 / 2);

		ArrayList<Entity> entities = entityManager.getEntities();

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);

			// make some logic for the entity
			if (Gdx.input.justTouched()) {

				int x = Gdx.input.getX();
				int y = Gdx.graphics.getHeight() - Gdx.input.getY();

				Vector2 position = Properties.getValue(entity, "position");

				if (position.dst(x, y) < 20f) {
					sound.play(1f);
					entityManager.remove(entity);

					// add new entity with fade out animation
				}

			}

			{
				// render the entity
				Texture texture = Properties.getValue(entity, "image");
				Vector2 position = Properties.getValue(entity, "position");
				spriteBatch.draw(texture, position.x - texture.getWidth() / 2, position.y - texture.getHeight() / 2);
			}
		}

		spriteBatch.end();

		// if (Gdx.input.justTouched()) {
		// sound.play(1f);
		//
		// int x = Gdx.input.getX();
		// int y = Gdx.input.getY();
		//
		// }

	}

	@Override
	public void show() {
		Gdx.app.log(PlatformGame.applicationName, "entered game screen");
	}

	@Override
	public void dispose() {
		background.dispose();
		island.dispose();
		sound.dispose();
	}

}