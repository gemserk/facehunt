package com.gemserk.games.facehunt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.games.facehunt.screens.FadeTransitionScreen;
import com.gemserk.games.facehunt.screens.GameScreen;
import com.gemserk.games.facehunt.screens.MenuScreen;

public class FaceHuntGame extends Game {

	public GameScreen gameScreen;
	public FadeTransitionScreen fadeTransitionScreen;
	private MenuScreen menuScreen;

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());

		final Texture gemserkLogo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		gemserkLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);
		gameScreen.restartGame();

		fadeTransitionScreen = new FadeTransitionScreen(this);

		setScreen(menuScreen);
		transition(gameScreen);

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
		fadeTransitionScreen.setCurrentScreen((ScreenAdapter) this.getScreen());
		fadeTransitionScreen.setNextScreen(nextScreen);
		setScreen(fadeTransitionScreen);
	}

}
