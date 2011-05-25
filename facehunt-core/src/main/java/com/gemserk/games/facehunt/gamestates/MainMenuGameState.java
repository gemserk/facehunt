package com.gemserk.games.facehunt.gamestates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.dataloaders.DataLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;

public class MainMenuGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private TextButton playButton;

	private TextButton exitButton;

	private BitmapFont titleFont;

	private Sprite happyFaceSprite;

	private float happyFaceAngle = 0f;

	private ParticleEmitter particleEmitter1;

	private ParticleEmitter particleEmitter2;

	private Sound pressedSound;

	private TextButton survivalModeButton;

	private int viewportWidth;

	private int viewportHeight;

	private BitmapFont textFont;

	private TextButton changeUsernameButton;

	private String username;

	private Preferences preferences;

	private static final String KEY_USERNAME = "username";

	private TextButton highscoresButton;

	public MainMenuGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {

		preferences = Gdx.app.getPreferences("gemserk-facehunt");
		username = preferences.getString(KEY_USERNAME);

		if ("".equals(username)) {
			username = "undefined-" + MathUtils.random(10000, 99999);
			preferences.putString(KEY_USERNAME, username);
			preferences.flush();
		}

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new GameResourceBuilder(resourceManager) {
			{
				particleEmitter("FaceEmitter", "HappyFaceSprite", "data/emitters/FaceEmitter");
			}

			private void particleEmitter(String id, final String spriteId, final String file) {
				resourceManager.add(id, new ResourceLoaderImpl<ParticleEmitter>(new DataLoader<ParticleEmitter>() {
					@Override
					public ParticleEmitter load() {
						try {
							ParticleEmitter particleEmitter = new ParticleEmitter(new BufferedReader(new InputStreamReader(Gdx.files.internal(file).read())));
							Sprite sprite = resourceManager.getResourceValue(spriteId);
							particleEmitter.setSprite(sprite);
							return particleEmitter;
						} catch (IOException e) {
							throw new RuntimeException("Failed to load face particle emitter from file " + file, e);
						}
					}
				}));
			}

		};

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);
		backgroundSprite.setSize(viewportWidth, viewportHeight);

		happyFaceSprite = resourceManager.getResourceValue("HappyFaceSprite");

		particleEmitter1 = resourceManager.getResourceValue("FaceEmitter");
		particleEmitter2 = resourceManager.getResourceValue("FaceEmitter");

		textFont = resourceManager.getResourceValue("Font");
		textFont.setColor(1f, 1f, 0f, 1f);
		textFont.setScale(0.8f * viewportWidth / 800f);

		titleFont = resourceManager.getResourceValue("TitleFont");
		titleFont.setColor(1f, 1f, 0f, 1f);
		titleFont.setScale(1f * viewportWidth / 800f);
		
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");
		buttonFont.setScale(0.7f * viewportWidth / 800f);

		playButton = new TextButton(buttonFont, "Tutorial", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.66f);
		survivalModeButton = new TextButton(buttonFont, "Play", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.54f);
		highscoresButton = new TextButton(buttonFont, "Highscores", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.42f);
		exitButton = new TextButton(buttonFont, "Exit", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.3f);
		
		changeUsernameButton = new TextButton(textFont, "Username: " + username + "\n(tap to change it)", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.08f);

		Color notOverColor = new Color(1f, 1f, 1f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);

		playButton.setNotOverColor(notOverColor);
		playButton.setOverColor(overColor);
		playButton.setColor(notOverColor);

		survivalModeButton.setNotOverColor(notOverColor);
		survivalModeButton.setOverColor(overColor);
		survivalModeButton.setColor(notOverColor);
		
		highscoresButton.setNotOverColor(notOverColor);
		highscoresButton.setOverColor(overColor);
		highscoresButton.setColor(notOverColor);

		exitButton.setNotOverColor(notOverColor);
		exitButton.setOverColor(overColor);
		exitButton.setColor(notOverColor);

		changeUsernameButton.setColor(new Color(1f, 1f, 0f, 1f));
		changeUsernameButton.setOverColor(new Color(1f, 0f, 0f, 1f));
		changeUsernameButton.setNotOverColor(new Color(1f, 1f, 0f, 1f));

		pressedSound = resourceManager.getResourceValue("ButtonPressedSound");

		particleEmitter1.setPosition(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.85f);
		particleEmitter2.setPosition(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.85f);
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);

		playButton.draw(spriteBatch);
		survivalModeButton.draw(spriteBatch);
		exitButton.draw(spriteBatch);
		highscoresButton.draw(spriteBatch);
		changeUsernameButton.draw(spriteBatch);

		titleFont.setColor(1f, 1f, 0f, 1f);
		SpriteBatchUtils.drawCentered(spriteBatch, titleFont, "Face Hunt", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.85f);

		particleEmitter1.draw(spriteBatch, ((float) delta) * 0.001f);
		particleEmitter2.draw(spriteBatch, ((float) delta) * 0.001f);

		happyFaceSprite.setColor(1f, 1f, 0f, 1f);
		SpriteBatchUtils.drawCentered(spriteBatch, happyFaceSprite, Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.85f, happyFaceAngle);
		SpriteBatchUtils.drawCentered(spriteBatch, happyFaceSprite, Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.85f, -happyFaceAngle);

		happyFaceAngle += 0.05f * delta;

		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize();

		playButton.update();
		survivalModeButton.update();
		exitButton.update();
		highscoresButton.update();
		changeUsernameButton.update();

		if (playButton.isReleased()) {
			game.transition(game.tutorialScreen, true);
			pressedSound.play();
		}

		if (survivalModeButton.isReleased()) {
			game.transition(game.gameScreen, true);
			pressedSound.play();
		}
		
		if (highscoresButton.isReleased()) { 
			game.transition(game.highscoresScreen, true);
			pressedSound.play();
		}

		if (exitButton.isReleased())
			System.exit(0);

		if (changeUsernameButton.isReleased()) {

			game.getScreen().pause();

			Gdx.input.getTextInput(new TextInputListener() {

				@Override
				public void input(String username) {
					if (!"".equals(username)) {
						preferences.putString(KEY_USERNAME, username);
						preferences.flush();
						changeUsernameButton.setText("Username: " + username + "\n(tap to change it)");
					}
					game.getScreen().resume();
				}

				@Override
				public void cancled() {
					game.getScreen().resume();
				}

			}, "Username", username);

		}

		if (Gdx.input.isKeyPressed(Keys.T))
			game.transition(game.testScreen, true);

	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
