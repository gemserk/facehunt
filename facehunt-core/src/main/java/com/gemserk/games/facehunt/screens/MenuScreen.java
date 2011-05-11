package com.gemserk.games.facehunt.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class MenuScreen extends ScreenAdapter {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private TextButton playButton;

	private TextButton exitButton;

	public MenuScreen(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void show() {

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);
				texture("BackgroundTexture", "data/background01-1024x512.jpg", false);
				sprite("BackgroundSprite", "BackgroundTexture");
				font("Font", "data/font.png", "data/font.fnt");
			}
		};

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);

		BitmapFont font = resourceManager.getResourceValue("Font");
		font.setScale(2f);

		String playButtonText = "Play";

		// if (!game.gameScreen.gameOver)
		// playButtonText = "Resume";

		playButton = new TextButton(font, playButtonText, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.7f);
		exitButton = new TextButton(font, "Exit", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.4f);

	}

	@Override
	public void internalRender(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		playButton.draw(spriteBatch);
		exitButton.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void internalUpdate(float delta) {
		Synchronizers.synchronize();
		playButton.update();
		exitButton.update();

		if (playButton.isReleased()) {
			game.transition(game.gameScreen);
		}

		if (exitButton.isReleased()) {
			System.exit(0);
		}
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
