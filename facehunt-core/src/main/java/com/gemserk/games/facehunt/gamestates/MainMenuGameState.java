package com.gemserk.games.facehunt.gamestates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	public MainMenuGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {

		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();

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

		titleFont = resourceManager.getResourceValue("TitleFont");
		titleFont.setColor(1f, 1f, 0f, 1f);
		titleFont.setScale(1f * viewportWidth / 800f);

		playButton = new TextButton(titleFont, "Tutorial", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.6f);
		survivalModeButton = new TextButton(titleFont, "Play", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.4f);
		exitButton = new TextButton(titleFont, "Exit", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.2f);

		Color notOverColor = new Color(1f, 1f, 1f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);

		playButton.setNotOverColor(notOverColor);
		playButton.setOverColor(overColor);
		playButton.setColor(notOverColor);

		survivalModeButton.setNotOverColor(notOverColor);
		survivalModeButton.setOverColor(overColor);
		survivalModeButton.setColor(notOverColor);

		exitButton.setNotOverColor(notOverColor);
		exitButton.setOverColor(overColor);
		exitButton.setColor(notOverColor);

		pressedSound = resourceManager.getResourceValue("ButtonPressedSound");

		particleEmitter1.setPosition(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.8f);
		particleEmitter2.setPosition(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f);
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);

		playButton.draw(spriteBatch);
		survivalModeButton.draw(spriteBatch);
		exitButton.draw(spriteBatch);

		titleFont.setColor(1f, 1f, 0f, 1f);
		SpriteBatchUtils.drawCentered(spriteBatch, titleFont, "Face Hunt", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.8f);

		particleEmitter1.draw(spriteBatch, ((float) delta) * 0.001f);
		particleEmitter2.draw(spriteBatch, ((float) delta) * 0.001f);

		happyFaceSprite.setColor(1f, 1f, 0f, 1f);
		SpriteBatchUtils.drawCentered(spriteBatch, happyFaceSprite, Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.8f, happyFaceAngle);
		SpriteBatchUtils.drawCentered(spriteBatch, happyFaceSprite, Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f, -happyFaceAngle);

		happyFaceAngle += 0.05f * delta;

		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize();

		playButton.update();
		survivalModeButton.update();
		exitButton.update();

		if (playButton.isReleased()) {
			game.transition(game.tutorialScreen, true);
			pressedSound.play();
		}

		if (survivalModeButton.isReleased()) {
			game.transition(game.gameScreen, true);
			pressedSound.play();
		}

		if (exitButton.isReleased())
			System.exit(0);

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
