package com.gemserk.games.facehunt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen extends ScreenAdapter {

	private final Game game;

	private Texture logo;

	private SpriteBatch spriteBatch;

	public MainMenuScreen(Game game) {
		this.game = game;
		this.logo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		this.spriteBatch = new SpriteBatch();
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		int x = centerX;
		int y = 0;

		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);

		y = centerY - logo.getHeight() ;
		spriteBatch.draw(logo, x - logo.getWidth() / 2, y - logo.getHeight() / 2, 0, 0, logo.getWidth(), logo.getHeight());

		y = centerY;
		spriteBatch.draw(logo, x - logo.getWidth() / 2, y - logo.getHeight() / 2, 0, 0, logo.getWidth(), logo.getHeight());

		y = centerY + logo.getHeight();
		spriteBatch.draw(logo, x - logo.getWidth() / 2, y - logo.getHeight() / 2, 0, 0, logo.getWidth(), logo.getHeight());

		spriteBatch.end();
	}

}
