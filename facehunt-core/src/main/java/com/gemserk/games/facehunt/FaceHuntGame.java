package com.gemserk.games.facehunt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.analytics.Analytics;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.sounds.SoundPlayer;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.datastore.profiles.ProfilesHttpImpl;
import com.gemserk.games.facehunt.gamestates.GameOverGameState;
import com.gemserk.games.facehunt.gamestates.GamePreferences;
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
	public Screen pauseScreen;
	public Screen testScreen;
	public Screen highscoresScreen;
	public Screen gameOverScreen;

	public PauseGameState pauseGameState;
	public TutorialModeGameState tutorialModeGameState;
	public GameOverGameState gameOverGameState;

	public Scores scores;
	public Profiles profiles;
	public Preferences preferences;
	private ExecutorService executorService;

	private final AdWhirlViewHandler adWhirlViewHandler;

	public AdWhirlViewHandler getAdWhirlViewHandler() {
		return adWhirlViewHandler;
	}

	public FaceHuntGame(AdWhirlViewHandler adWhirlViewHandler) {
		this.adWhirlViewHandler = adWhirlViewHandler;
		scores = new ScoresHttpImpl("db3bbc454ad707213fe02874e526e5f7", "http://gemserkscores.appspot.com", new ScoreSerializerJSONImpl());
		profiles = new ProfilesHttpImpl("http://gemserkscores.appspot.com");
	}

	public FaceHuntGame() {
		this(new AdWhirlViewHandler());
	}
	
	public void setScores(Scores scores) {
		this.scores = scores;
	}
	
	public void setProfiles(Profiles profiles) {
		this.profiles = profiles;
	}

	@Override
	public void create() {
		
		Analytics.traker.trackPageView("/start", "/start", null);
		
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());
		
		com.badlogic.gdx.physics.box2d.World.setVelocityThreshold(0f);

		executorService = Executors.newCachedThreadPool();
		preferences = Gdx.app.getPreferences("gemserk-facehunt");

		GamePreferences gamePreferences = new GamePreferences(preferences);
		// GamePreferences gamePreferences = new GamePreferences(preferences);
		SoundPlayer soundPlayer = new SoundPlayer();

		soundPlayer.setVolume(gamePreferences.getSoundVolume());

		tutorialModeGameState = new TutorialModeGameState(this);
		tutorialModeGameState.setSoundPlayer(soundPlayer);

		pauseGameState = new PauseGameState(this);
		pauseGameState.setSoundPlayer(soundPlayer);
		pauseGameState.setGamePreferences(gamePreferences);

		gameOverGameState = new GameOverGameState(this);
		gameOverGameState.setScores(scores);
		gameOverGameState.setExecutorService(executorService);
		gameOverGameState.setProfiles(profiles);
		gameOverGameState.setGameProfiles(gamePreferences);
		gameOverGameState.setSoundPlayer(soundPlayer);

		MainMenuGameState mainMenuGameState = new MainMenuGameState(this);
		mainMenuGameState.setProfiles(profiles);
		mainMenuGameState.setGameProfiles(gamePreferences);
		mainMenuGameState.setSoundPlayer(soundPlayer);

		HighscoresGameState highscoresGameState = new HighscoresGameState(this);
		highscoresGameState.setScores(scores);
		highscoresGameState.setExecutorService(executorService);
		highscoresGameState.setPreferences(preferences);

		SurvivalModeGameState survivalModeGameState = new SurvivalModeGameState(this);
		survivalModeGameState.setGameProfiles(gamePreferences);
		survivalModeGameState.setSoundPlayer(soundPlayer);

		TestGameState testGameState = new TestGameState(this);
		testGameState.setSoundPlayer(soundPlayer);
		
		menuScreen = new ScreenImpl(mainMenuGameState);
		tutorialScreen = new ScreenImpl(tutorialModeGameState);
		gameScreen = new ScreenImpl(survivalModeGameState);
		pauseScreen = new ScreenImpl(pauseGameState);
		gameOverScreen = new ScreenImpl(gameOverGameState);
		splashScreen = new ScreenImpl(new SplashGameState(this));
		testScreen = new ScreenImpl(testGameState);
		highscoresScreen = new ScreenImpl(highscoresGameState);

		fadeTransitionScreen = new FadeTransitionScreen(this);

		transition(null, splashScreen, 1000);
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public void transition(Screen nextScreen, int time) {
		this.transition(nextScreen, false, time);
	}

	public void transition(Screen nextScreen, boolean shouldDisposeCurrent, int time) {
		fadeTransitionScreen.transition(getScreen(), nextScreen, time, shouldDisposeCurrent);
		fadeTransitionScreen.resume();
		fadeTransitionScreen.show();
		setScreen(fadeTransitionScreen);
	}

	public void transition(Screen currentScreen, Screen nextScreen, int time) {
		fadeTransitionScreen.transition(currentScreen, nextScreen, time);
		fadeTransitionScreen.resume();
		fadeTransitionScreen.show();
		setScreen(fadeTransitionScreen);
	}

	@Override
	public void dispose() {
		super.dispose();
		try {
			executorService.shutdown();
			executorService.awaitTermination(1000l, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		super.pause();
		Gdx.app.log("FaceHunt", "game paused via ApplicationListner.pause()");
		adWhirlViewHandler.hide();
	}
	
	@Override
	public void resume() {
		super.resume();
		Gdx.app.log("FaceHunt", "game resumed via ApplicationListner.resume()");
		adWhirlViewHandler.show();		
	}
	
	@Override
	public void render() {
		super.render();
		if (Gdx.input.isKeyPressed(Keys.T))
			super.setScreen(testScreen);	
	}

}
