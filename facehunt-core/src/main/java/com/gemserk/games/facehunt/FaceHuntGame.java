package com.gemserk.games.facehunt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.InternalScreen;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.games.facehunt.screens.FadeTransitionScreen;
import com.gemserk.games.facehunt.screens.GameScreen;
import com.gemserk.games.facehunt.screens.MenuScreen;
import com.gemserk.games.facehunt.screens.ScoreScreen;

public class FaceHuntGame extends Game {

	public InternalScreen gameScreen;
	
	public FadeTransitionScreen fadeTransitionScreen;
	
	public InternalScreen menuScreen;

	public InternalScreen scoreScreen;

	public ScoreScreen scoreGameState;
	
	public GameScreen gameGameState;

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());

		final Texture gemserkLogo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		gemserkLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		gameGameState = new GameScreen(this);
		scoreGameState = new ScoreScreen(this);

		menuScreen = new InternalScreen(new MenuScreen(this));
		gameScreen = new InternalScreen(gameGameState);
		scoreScreen = new InternalScreen(scoreGameState);
		
		fadeTransitionScreen = new FadeTransitionScreen(this);

		transition(null, menuScreen);
		
//		setScreen(menuScreen);
//		setScreen(scoreScreen);
		// transition(gameScreen);

		// setScreen(new SplashScreen(gemserkLogo) {
		//
		// @Override
		// protected void onSplashScreenFinished() {
		// game.setScreen(new GameScreen(game));
		// }
		//
		// @Override
		// public void dispose() {
		// gemserkLogo.dispose();
		// }
		//
		// });

	}

	public void transition(ScreenAdapter nextScreen) {
		this.transition(nextScreen, false);
	}

	public void transition(ScreenAdapter nextScreen, boolean shouldDisposeCurrent) {
		fadeTransitionScreen.transition((ScreenAdapter) this.getScreen(), nextScreen, 1000, shouldDisposeCurrent);
		setScreen(fadeTransitionScreen);
	}

	public void transition(ScreenAdapter currentScreen, ScreenAdapter nextScreen) {
		fadeTransitionScreen.transition(currentScreen, nextScreen, 1000);
		setScreen(fadeTransitionScreen);
	}

}
