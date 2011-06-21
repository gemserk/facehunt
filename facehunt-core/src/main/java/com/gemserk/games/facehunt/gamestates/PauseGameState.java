package com.gemserk.games.facehunt.gamestates;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.commons.gdx.sounds.SoundPlayer;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.gui.ToggleableImageButton;
import com.gemserk.games.facehunt.gui.ToggleableImageButton.ToggleHandler;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PauseGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private TextButton resumeButton;

	private TextButton mainMenuButton;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Sound pressedSound;

	private Screen previousScreen;

	private Screen menuScreen;

	private boolean mainMenu;

	private Sprite backgroundSprite;
	
	private SoundPlayer soundPlayer;

	private ToggleableImageButton speakersButton;
	
	private GamePreferences gamePreferences;
	
	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}
	
	public void setSoundPlayer(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	public void setPreviousScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	public PauseGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new GameResourceBuilder(resourceManager);

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);
		backgroundSprite.setSize(viewportWidth, viewportHeight);

		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont2");
		// buttonFont.setScale(1f * viewportWidth / 800f);

		resumeButton = new TextButton(buttonFont, "Resume", viewportWidth * 0.5f, viewportHeight * 0.65f);
		mainMenuButton = new TextButton(buttonFont, "Main Menu", viewportWidth * 0.5f, viewportHeight * 0.35f);

		Color notOverColor = new Color(1f, 1f, 0f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);

		resumeButton.setNotOverColor(notOverColor);
		resumeButton.setOverColor(overColor);
		resumeButton.setColor(notOverColor);

		mainMenuButton.setNotOverColor(notOverColor);
		mainMenuButton.setOverColor(overColor);
		mainMenuButton.setColor(notOverColor);

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

		mainMenu = false;
		
		Sprite speakersOnSprite = resourceManager.getResourceValue("SpeakersOnSprite");
		Sprite speakersOffSprite = resourceManager.getResourceValue("SpeakersOffSprite");

		speakersOnSprite.setColor(1f, 1f, 0f, 1f);
		speakersOnSprite.setScale(0.7f * viewportWidth / 800f);

		speakersOffSprite.setColor(1f, 0f, 0f, 1f);
		speakersOffSprite.setScale(0.7f * viewportWidth / 800f);

		speakersButton = new ToggleableImageButton().setEnabledSprite(speakersOnSprite) //
				.setDisabledSprite(speakersOffSprite) //
				.setEnabled(!soundPlayer.isMuted()) //
				.setPosition(viewportWidth * 0.92f, viewportHeight * 0.15f) //
				.setBounds(new Rectangle(-speakersOnSprite.getWidth() * 0.5f, -speakersOnSprite.getHeight() * 0.5f, speakersOnSprite.getWidth(), speakersOnSprite.getHeight())) //
				.setToggleHandler(new ToggleHandler() {
					@Override
					public void onToggle(boolean value) {
						if (value) {
							soundPlayer.unmute();
							gamePreferences.updateSoundVolume(soundPlayer.getVolume());
						}
						else {
							soundPlayer.mute();
							gamePreferences.updateSoundVolume(soundPlayer.getVolume());
						}
					}
				});
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
		resumeButton.draw(spriteBatch);
		mainMenuButton.draw(spriteBatch);
		speakersButton.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		speakersButton.udpate(delta);
		
		inputDevicesMonitor.update();

		resumeButton.update();
		mainMenuButton.update();

		if (resumeButton.isReleased()) {
			soundPlayer.play(pressedSound);
			game.transition(previousScreen, true);
		}

		if (mainMenuButton.isReleased() || inputDevicesMonitor.getButton("back").isReleased()) {
			soundPlayer.play(pressedSound);
			game.transition(menuScreen, true);
			mainMenu = true;
		}
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
		if (mainMenu)
			previousScreen.dispose();
	}

}
