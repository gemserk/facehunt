package com.gemserk.games.facehunt;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.games.facehunt.gamestates.HighscoresGameState;
import com.gemserk.games.facehunt.gamestates.MainMenuGameState;
import com.gemserk.games.facehunt.gamestates.PauseGameState;
import com.gemserk.games.facehunt.gamestates.SplashGameState;
import com.gemserk.games.facehunt.gamestates.SurvivalModeGameState;
import com.gemserk.games.facehunt.gamestates.TestGameState;
import com.gemserk.games.facehunt.gamestates.TutorialModeGameState;
import com.gemserk.games.facehunt.screens.FadeTransitionScreen;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresHttpImpl;

public class FaceHuntGame extends com.gemserk.commons.gdx.Game {

	public FadeTransitionScreen fadeTransitionScreen;

	public Screen splashScreen;

	public Screen tutorialScreen;

	public Screen gameScreen;

	public Screen menuScreen;

	public Screen scoreScreen;

	public Screen testScreen;

	public Screen highscoresScreen;

	public PauseGameState pauseGameState;

	public TutorialModeGameState tutorialModeGameState;

	public Scores scores;

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());

		scores = new ScoresHttpImpl("db3bbc454ad707213fe02874e526e5f7", "http://gemserkscores.appspot.com", new ScoreSerializerJSONImpl());

		tutorialModeGameState = new TutorialModeGameState(this);
		pauseGameState = new PauseGameState(this);

		menuScreen = new ScreenImpl(new MainMenuGameState(this));
		tutorialScreen = new ScreenImpl(tutorialModeGameState);
		gameScreen = new ScreenImpl(new SurvivalModeGameState(this));
		scoreScreen = new ScreenImpl(pauseGameState);
		splashScreen = new ScreenImpl(new SplashGameState(this));
		testScreen = new ScreenImpl(new TestGameState(this));
		highscoresScreen = new ScreenImpl(new HighscoresGameState(this));

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
