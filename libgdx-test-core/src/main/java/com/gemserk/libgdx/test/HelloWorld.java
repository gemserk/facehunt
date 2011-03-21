package com.gemserk.libgdx.test;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.TypeConverter;
import com.gemserk.animation4j.interpolator.Interpolators;
import com.gemserk.animation4j.time.UpdateableTimeProvider;
import com.gemserk.animation4j.transitions.AutoUpdateableTransition;
import com.gemserk.animation4j.transitions.Transition;

public class HelloWorld implements ApplicationListener {

	SpriteBatch spriteBatch;

	Texture texture;

	BitmapFont font;

	Transition<Vector2> position;

	Vector2 startPosition;

	Vector2 endPosition;

	private UpdateableTimeProvider timeProvider;

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(Color.RED);
		texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
		spriteBatch = new SpriteBatch();

		TypeConverter<Vector2> vector2Converter = new Vector2Converter();

		startPosition = new Vector2(0, 100);
		endPosition = new Vector2(0, 100);

		// position = new AutoUpdateableTransition<Vector2>(startPosition, endPosition, //
		// new GenericInterpolator<Vector2>(vector2Converter, new FloatArrayInterpolator(2)), 0.001f);

		timeProvider = new UpdateableTimeProvider();

		position = new AutoUpdateableTransition<Vector2>(new Vector2(startPosition), //
				Interpolators.interpolator(vector2Converter), 0.001f);
		// position.set(new Vector2(endPosition), 1000);

	}
	
	Random random = new Random();

	@Override
	public void render() {
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		Vector2 textPosition = position.get();
		Gdx.app.log("HelloWorld", "position " + textPosition + ", desiredPosition: " + endPosition);

		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(texture, centerX - texture.getWidth() / 2, centerY - texture.getHeight() / 2, 0, 0, texture.getWidth(), texture.getHeight());
		font.draw(spriteBatch, "Hello World!", (int) textPosition.x, (int) textPosition.y);
		spriteBatch.end();

		float deltaTime = Gdx.graphics.getDeltaTime();
		timeProvider.update((long) (deltaTime * 1000));
		
		if (textPosition.dst2(endPosition) < 1f) {
			endPosition.set(random.nextFloat() * Gdx.graphics.getWidth(), random.nextFloat() * Gdx.graphics.getHeight());
			position.set(new Vector2(endPosition), 1000);
		}

	}

	@Override
	public void resize(int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
