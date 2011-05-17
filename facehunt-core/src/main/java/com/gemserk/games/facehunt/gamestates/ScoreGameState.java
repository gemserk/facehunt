package com.gemserk.games.facehunt.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.values.GameData;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class ScoreGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private TextButton tryAgainButton;

	private TextButton mainMenuButton;

	private BitmapFont font;

	private GameData gameData;

	public ScoreGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {

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

		font = resourceManager.getResourceValue("Font");

		String buttonText = "Try again";

		if (!game.playGameState.gameOver)
			buttonText = "Resume";

		tryAgainButton = new TextButton(font, buttonText, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.4f);
		mainMenuButton = new TextButton(font, "Main Menu", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.3f);
		
		Color notOverColor = new Color(1f, 1f, 1f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);
		
		tryAgainButton.setNotOverColor(notOverColor);
		tryAgainButton.setOverColor(overColor);
		tryAgainButton.setColor(notOverColor);
		
		mainMenuButton.setNotOverColor(notOverColor);
		mainMenuButton.setOverColor(overColor);
		mainMenuButton.setColor(notOverColor);
	}

	@Override
	public void render(int delta) {
		if (spriteBatch == null)
			return;
		
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);

		if (game.playGameState.gameOver && gameData != null) {
			font.setColor(Color.RED);
			SpriteBatchUtils.drawCentered(spriteBatch, font, "Game Over", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.7f);
			SpriteBatchUtils.drawCentered(spriteBatch, font, "Score: " + gameData.killedCritters * 100, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
		}

		tryAgainButton.draw(spriteBatch);
		mainMenuButton.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize();

		tryAgainButton.update();
		mainMenuButton.update();

		if (tryAgainButton.isReleased()) {
			game.transition(game.gameScreen, true);
		}

		if (mainMenuButton.isReleased()) {
			game.transition(game.menuScreen, true);
			game.gameScreen.dispose();
			setGameData(null);
		}
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

	public void setGameData(GameData gameData) {
		this.gameData = gameData;
	}

}
