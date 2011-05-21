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
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.games.facehunt.values.GameData;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PauseGameState extends GameStateImpl {

	static class Text {

		private final String text;

		private final float x;

		private final float y;

		private boolean visible;

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		public boolean isVisible() {
			return visible;
		}

		public Text(String text, float x, float y) {
			this.text = text;
			this.x = x;
			this.y = y;
		}

		public void draw(SpriteBatch spriteBatch, BitmapFont font) {
			if (!isVisible())
				return;
			SpriteBatchUtils.drawMultilineTextCentered(spriteBatch, font, text, x, y);
		}

	}

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private TextButton tryAgainButton;

	private TextButton mainMenuButton;

	private BitmapFont font;

	private GameData gameData;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Sound pressedSound;

	private Screen previousScreen;

	private Screen menuScreen;

	private Text gameOverText;

	private Sprite overlaySprite;

	public void setGameData(GameData gameData) {
		this.gameData = gameData;
	}
	
	public void setPreviousScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	public PauseGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		new GameResourceBuilder(resourceManager);

		overlaySprite = resourceManager.getResourceValue("OverlaySprite");

		overlaySprite.setColor(0.5f, 0.5f, 0.5f, 0.7f);
		overlaySprite.setPosition(0, 0);
		overlaySprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);

		font = resourceManager.getResourceValue("Font");

		String buttonText = "Try again";

		gameOverText = new Text("Game Over\n" + "Score: " + gameData.points, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.8f);
		gameOverText.setVisible(gameData.gameOver);

		if (!gameData.gameOver) {
			buttonText = "Resume";
		}

		tryAgainButton = new TextButton(font, buttonText, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		mainMenuButton = new TextButton(font, "Main Menu", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.4f);

		Color notOverColor = new Color(1f, 1f, 1f, 1f);
		Color overColor = new Color(0.3f, 0.3f, 1f, 1f);

		tryAgainButton.setNotOverColor(notOverColor);
		tryAgainButton.setOverColor(overColor);
		tryAgainButton.setColor(notOverColor);

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

//		previousScreen = game.tutorialScreen;
		menuScreen = game.menuScreen;
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

		// Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		// backgroundSprite.draw(spriteBatch);
		overlaySprite.draw(spriteBatch);

		font.setColor(Color.RED);
		gameOverText.draw(spriteBatch, font);

		tryAgainButton.draw(spriteBatch);
		mainMenuButton.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		Synchronizers.synchronize(delta);

		inputDevicesMonitor.update();

		tryAgainButton.update();
		mainMenuButton.update();

		if (tryAgainButton.isReleased()) {
			pressedSound.play();
			game.transition(previousScreen, true);
		}

		if (mainMenuButton.isReleased() || inputDevicesMonitor.getButton("back").isReleased()) {
			pressedSound.play();
			game.transition(menuScreen, true);
			gameData.gameOver = true;
		}
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
		if (gameData.gameOver)
			previousScreen.dispose();
	}

}
