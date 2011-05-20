package com.gemserk.games.facehunt.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.utils.timers.CountDownTimer;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class SplashGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite gemserkLogoSprite;
	
	private CountDownTimer timer;

	public SplashGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);
				texture("GemserkLogoTexture", "data/images/logo-gemserk-512x128-white.png");
				sprite("GemserkLogoSprite", "GemserkLogoTexture");
			}
		};

		gemserkLogoSprite = resourceManager.getResourceValue("GemserkLogoSprite");
		
		SpriteUtils.resize(gemserkLogoSprite, width * 0.8f);
		SpriteUtils.centerOn(gemserkLogoSprite, width * 0.5f, height * 0.5f);
		
		timer = new CountDownTimer(2000, true);
	}
	
	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		gemserkLogoSprite.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		timer.update(delta);
		if (!timer.isRunning())
			game.transition(game.menuScreen, true);
		if (Gdx.input.justTouched())
			timer.update(10000);
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
