package com.gemserk.games.facehunt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.InternalScreen;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.games.facehunt.screens.FadeTransitionScreen;
import com.gemserk.games.facehunt.screens.PlayGameState;
import com.gemserk.games.facehunt.screens.MenuGameState;
import com.gemserk.games.facehunt.screens.ScoreGameState;
import com.gemserk.games.facehunt.screens.SplashGameState;

public class FaceHuntGame extends Game {

	public FadeTransitionScreen fadeTransitionScreen;

	public InternalScreen splashScreen;
	
	public InternalScreen gameScreen;

	public InternalScreen menuScreen;

	public InternalScreen scoreScreen;

	public ScoreGameState scoreGameState;

	public PlayGameState gameGameState;

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());

		gameGameState = new PlayGameState(this);
		scoreGameState = new ScoreGameState(this);

		menuScreen = new InternalScreen(new MenuGameState(this));
		gameScreen = new InternalScreen(gameGameState);
		scoreScreen = new InternalScreen(scoreGameState);
		splashScreen = new InternalScreen(new SplashGameState(this));

		fadeTransitionScreen = new FadeTransitionScreen(this);

		transition(null, splashScreen);
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
