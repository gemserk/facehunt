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
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class MenuGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private TextButton playButton;

	private TextButton exitButton;

	private BitmapFont font;

	private BitmapFont titleFont;

	private Sprite happyFaceSprite;

	public MenuGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {

		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);
				texture("BackgroundTexture", "data/background01-1024x512.jpg", false);
				sprite("BackgroundSprite", "BackgroundTexture");
				font("Font", "data/titlefont.png", "data/titlefont.fnt", true);
				font("TitleFont", "data/titlefont.png", "data/titlefont.fnt", true);
				
				texture("HappyFaceTexture", "data/face-happy-64x64.png");
				sprite("HappyFaceSprite", "HappyFaceTexture");
				
			}
		};

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);
		
		happyFaceSprite = resourceManager.getResourceValue("HappyFaceSprite");

		titleFont = resourceManager.getResourceValue("TitleFont");
		titleFont.setColor(1f, 1f, 0f, 1f);
		titleFont.setScale(1f * viewportWidth / 800f);
		
		font = resourceManager.getResourceValue("Font");
		font.setScale(1f * viewportWidth / 800f);

		playButton = new TextButton(font, "Play", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		exitButton = new TextButton(font, "Exit", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.3f);
		
		Color notOverColor = new Color(1f, 1f, 1f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);
		
		playButton.setNotOverColor(notOverColor);
		playButton.setOverColor(overColor);
		playButton.setColor(notOverColor);
		
		exitButton.setNotOverColor(notOverColor);
		exitButton.setOverColor(overColor);
		exitButton.setColor(notOverColor);
		
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		playButton.draw(spriteBatch);
		exitButton.draw(spriteBatch);
		
		SpriteBatchUtils.drawCentered(spriteBatch, titleFont, "Face Hunt", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.8f);
		
		SpriteBatchUtils.drawCentered(spriteBatch, happyFaceSprite, Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.8f, 0f);
		SpriteBatchUtils.drawCentered(spriteBatch, happyFaceSprite, Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f, 0f);

		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize();
		playButton.update();
		exitButton.update();

		if (playButton.isReleased()) 
			game.transition(game.gameScreen, true);

		if (exitButton.isReleased()) 
			System.exit(0);
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
