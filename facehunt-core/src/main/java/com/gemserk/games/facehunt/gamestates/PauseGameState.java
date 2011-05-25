package com.gemserk.games.facehunt.gamestates;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
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

	private Sprite overlaySprite;
	
	private boolean mainMenu;

	public void setPreviousScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	public PauseGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		int viewportWidth = Gdx.graphics.getWidth();

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new GameResourceBuilder(resourceManager);

		overlaySprite = resourceManager.getResourceValue("OverlaySprite");

		overlaySprite.setColor(0.5f, 0.5f, 0.5f, 0.7f);
		overlaySprite.setPosition(0, 0);
		overlaySprite.setSize(viewportWidth, Gdx.graphics.getHeight());

		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");
		buttonFont.setScale(0.7f * viewportWidth / 800f);

		resumeButton = new TextButton(buttonFont, "Resume", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		mainMenuButton = new TextButton(buttonFont, "Main Menu", viewportWidth * 0.5f, Gdx.graphics.getHeight() * 0.35f);

		Color notOverColor = new Color(1f, 1f, 1f, 1f);
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
	}

	@Override
	public void show() {
		previousScreen.show();
	}

	@Override
	public void hide() {
		previousScreen.hide();
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
		if (spriteBatch == null)
			return;

		previousScreen.render(delta);

		spriteBatch.begin();
		overlaySprite.draw(spriteBatch);
		resumeButton.draw(spriteBatch);
		mainMenuButton.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		inputDevicesMonitor.update();

		resumeButton.update();
		mainMenuButton.update();

		if (resumeButton.isReleased()) {
			pressedSound.play();
			game.transition(previousScreen, true);
		}

		if (mainMenuButton.isReleased() || inputDevicesMonitor.getButton("back").isReleased()) {
			pressedSound.play();
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
