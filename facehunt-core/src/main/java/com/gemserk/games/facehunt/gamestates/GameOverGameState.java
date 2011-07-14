package com.gemserk.games.facehunt.gamestates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.commons.gdx.gui.TextButton.ButtonHandler;
import com.gemserk.commons.gdx.sounds.SoundPlayer;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.util.concurrent.FutureHandler;
import com.gemserk.util.concurrent.FutureProcessor;

public class GameOverGameState extends GameStateImpl {

	class SubmitScoreCallable implements Callable<String> {

		private final Score score;

		private final Profile profile;

		private SubmitScoreCallable(Score score, Profile profile) {
			this.score = score;
			this.profile = profile;
		}

		@Override
		public String call() throws Exception {
			return scores.submit(profile.getPrivateKey(), score);
		}

	}

	class SubmitScoreHandler implements FutureHandler<String> {

		public void done(String scoreId) {
			scoreSubmitText.setText("Score submitted!").setColor(Color.GREEN);
		}

		public void failed(Exception e) {
			scoreSubmitText.setText("Submit score failed :(").setColor(Color.RED);
			if (e != null)
				Gdx.app.log("FaceHunt", e.getMessage(), e);
		}

	}

	private final FaceHuntGame game;

	private ResourceManager<String> resourceManager;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private SoundPlayer soundPlayer;
	private SpriteBatch spriteBatch;
	private Sound pressedSound;
	private Screen previousScreen;
	private Screen menuScreen;
	private Scores scores;
	private Profiles profiles;
	private Score score;
	private BitmapFont buttonFont;
	private ExecutorService executorService;
	private Text scoreSubmitText;
	private FutureProcessor<String> submitScoreProcessor;
	private Profile profile;
	private FutureProcessor<Profile> registerProfileProcessor;
	private GamePreferences gamePreferences;
	private Sprite backgroundSprite;

	private Container container;

	public void setSoundPlayer(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public void setProfiles(Profiles profiles) {
		this.profiles = profiles;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public void setGameProfiles(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}

	public GameOverGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new GameResourceBuilder(resourceManager);

		container = new Container();

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);
		backgroundSprite.setSize(viewportWidth, viewportHeight);

		Color notOverColor = new Color(1f, 1f, 0f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);

		buttonFont = resourceManager.getResourceValue("ButtonFont2");
		// buttonFont.setScale(1f * viewportWidth / 800f);

		container.add(GuiControls //
				.label("Game Over\n" + "Score: " + score.getPoints()) //
				.font(buttonFont) //
				.position(viewportWidth * 0.5f, viewportHeight * 0.70f) //
				.color(Color.RED) //
				.build());

		container.add(GuiControls.textButton() //
				.text("Try again") //
				.font(buttonFont) //
				.position(viewportWidth * 0.5f, viewportHeight * 0.35f) //
				.notOverColor(notOverColor) //
				.overColor(overColor) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased() {
						soundPlayer.play(pressedSound);
						game.transition(previousScreen, true);
					}
				})//
				.build());

		container.add(GuiControls.textButton() //
				.text("Main Menu") //
				.font(buttonFont) //
				.position(viewportWidth * 0.5f, viewportHeight * 0.20f) //
				.notOverColor(notOverColor) //
				.overColor(overColor) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased() {
						soundPlayer.play(pressedSound);
						game.transition(menuScreen, true);
					}
				})//
				.build());

		Gdx.input.setCatchBackKey(true);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				if (Gdx.app.getType() == ApplicationType.Android)
					monitorKey("back", Keys.BACK);
				else
					monitorKey("back", Keys.ESCAPE);
			}
		};

		pressedSound = resourceManager.getResourceValue("ButtonPressedSound");
		menuScreen = game.menuScreen;
		previousScreen = game.gameScreen;

		scoreSubmitText = GuiControls //
				.label("Submitting score...") //
				.font(buttonFont) //
				.position(viewportWidth * 0.5f, viewportHeight * 0.50f) //
				.color(new Color(1f, 1f, 0f, 1f)) //
				.build();
		container.add(scoreSubmitText);

		profile = gamePreferences.getCurrentProfile();

		submitScoreProcessor = new FutureProcessor<String>(new SubmitScoreHandler());
		registerProfileProcessor = new FutureProcessor<Profile>(new FutureHandler<Profile>() {

			@Override
			public void done(Profile profile) {
				gamePreferences.updateProfile(profile);

				submitScoreProcessor.setFuture(executorService.submit(new SubmitScoreCallable(score, profile)));
			}

			@Override
			public void failed(Exception e) {
				scoreSubmitText.setText("Submit score failed :(").setColor(Color.RED);
				if (e != null)
					Gdx.app.log("FaceHunt", e.getMessage(), e);
			}

		});
		registerProfileProcessor.setFuture(executorService.submit(new Callable<Profile>() {
			@Override
			public Profile call() throws Exception {
				if (profile.getPublicKey() != null)
					return profile;
				return profiles.register(profile.getName(), profile.isGuest());
			}
		}));
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void pause() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		container.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);
		inputDevicesMonitor.update();
		registerProfileProcessor.update();
		submitScoreProcessor.update();
		container.update();
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
		previousScreen.dispose();
	}

}
