package com.gemserk.games.facehunt;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.Game;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.games.facehunt.gamestates.MenuGameState;
import com.gemserk.games.facehunt.gamestates.PlayGameState;
import com.gemserk.games.facehunt.gamestates.ScoreGameState;
import com.gemserk.games.facehunt.gamestates.SplashGameState;
import com.gemserk.games.facehunt.gamestates.TestGameState;
import com.gemserk.games.facehunt.screens.FadeTransitionScreen;

public class FaceHuntGame extends Game {

	public FadeTransitionScreen fadeTransitionScreen;

	public Screen splashScreen;
	
	public Screen gameScreen;

	public Screen menuScreen;

	public Screen scoreScreen;
	
	public Screen testScreen;

	public ScoreGameState scoreGameState;

	public PlayGameState playGameState;

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());

		playGameState = new PlayGameState(this);
		scoreGameState = new ScoreGameState(this);

		menuScreen = new ScreenImpl(new MenuGameState(this));
		gameScreen = new ScreenImpl(playGameState);
		scoreScreen = new ScreenImpl(scoreGameState);
		splashScreen = new ScreenImpl(new SplashGameState(this));
		testScreen = new ScreenImpl(new TestGameState(this));

		fadeTransitionScreen = new FadeTransitionScreen(this);

		transition(null, splashScreen);
	}
	
	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public void transition(Screen nextScreen) {
		this.transition(nextScreen, false);
	}

	public void transition(Screen nextScreen, boolean shouldDisposeCurrent) {
		fadeTransitionScreen.transition(getScreen(), nextScreen, 1000, shouldDisposeCurrent);
		fadeTransitionScreen.resume();
		fadeTransitionScreen.show();
		setScreen(fadeTransitionScreen);
	}

	public void transition(Screen currentScreen, Screen nextScreen) {
		fadeTransitionScreen.transition(currentScreen, nextScreen, 1000);
		fadeTransitionScreen.resume();
		fadeTransitionScreen.show();
		setScreen(fadeTransitionScreen);
	}

}
