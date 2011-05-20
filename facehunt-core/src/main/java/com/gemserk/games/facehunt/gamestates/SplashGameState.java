package com.gemserk.games.facehunt.gamestates;

import com.badlogic.gdx.Application.ApplicationType;
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

	private CountDownTimer timer;

	private Sprite gemserkLogo;

	private Sprite lwjglLogo;

	private Sprite libgdxLogo;

	public SplashGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);
				texture("GemserkLogoTexture", "data/images/logo-gemserk-512x128-white.png");
				texture("LwjglLogoTexture", "data/images/logo-lwjgl-512x256-inverted.png");
				texture("LibgdxLogoTexture", "data/images/logo-libgdx-clockwork-512x256.png");
				sprite("GemserkLogo", "GemserkLogoTexture");
				sprite("LwjglLogo", "LwjglLogoTexture", 0, 0, 512, 185);
				sprite("LibgdxLogo", "LibgdxLogoTexture", 0, 25, 512, 256 - 50);
			}
		};

		gemserkLogo = resourceManager.getResourceValue("GemserkLogo");
		lwjglLogo = resourceManager.getResourceValue("LwjglLogo");
		libgdxLogo = resourceManager.getResourceValue("LibgdxLogo");

		SpriteUtils.resize(gemserkLogo, width * 0.8f);
		SpriteUtils.resize(lwjglLogo, width * 0.2f);
		SpriteUtils.resize(libgdxLogo, width * 0.2f);

		SpriteUtils.centerOn(gemserkLogo, centerX, centerY);
		SpriteUtils.centerOn(lwjglLogo, width * 0.85f, lwjglLogo.getHeight() * 0.5f);
		SpriteUtils.centerOn(libgdxLogo, width * 0.15f, libgdxLogo.getHeight() * 0.5f);

		timer = new CountDownTimer(2000, true);
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		gemserkLogo.draw(spriteBatch);
		if (Gdx.app.getType() != ApplicationType.Android)
			lwjglLogo.draw(spriteBatch);
		libgdxLogo.draw(spriteBatch);
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
