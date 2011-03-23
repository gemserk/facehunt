package com.gemserk.libgdx.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends ScreenAdapter {

	private final Game game;

	private Texture background;

	private SpriteBatch spriteBatch;

	private Texture island;

	private Sound sound;

	private AudioDevice audioDevice;

	public GameScreen(Game game) {
		this.game = game;
		background = new Texture(Gdx.files.internal("data/background01-1024x512.jpg"));
		island = new Texture(Gdx.files.internal("data/island01-128x128.png"));
		spriteBatch = new SpriteBatch();
		sound = Gdx.audio.newSound(Gdx.files.internal("data/shot.ogg"));
	}

	@Override
	public void render(float delta) {
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);

		spriteBatch.draw(background, centerX - 800 / 2, centerY - 480 / 2);

		spriteBatch.draw(island, 50 - island.getWidth() / 2, centerY - island.getHeight() / 2);
		spriteBatch.draw(island, Gdx.graphics.getWidth() - 50 - island.getWidth() / 2, centerY - island.getHeight() / 2);

		spriteBatch.end();

		if (Gdx.input.justTouched()) {
			sound.play(1f);
		}
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