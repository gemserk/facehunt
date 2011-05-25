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
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;

public class GameOverGameState extends GameStateImpl {

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private TextButton tryAgainButton;

	private TextButton mainMenuButton;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Sound pressedSound;

	private Screen previousScreen;

	private Screen menuScreen;

	private Text gameOverText;

	private Sprite overlaySprite;
	
	private Scores scores;

	private Score score;

	private BitmapFont buttonFont;
	
	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public GameOverGameState(FaceHuntGame game) {
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
		overlaySprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);

		buttonFont = resourceManager.getResourceValue("ButtonFont");
		buttonFont.setScale(0.7f * viewportWidth / 800f);

		gameOverText = new Text("Game Over\n" + "Score: " + score.getPoints(), Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.8f);
		gameOverText.setColor(Color.RED);
		
		tryAgainButton = new TextButton(buttonFont, "Try again", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		mainMenuButton = new TextButton(buttonFont, "Main Menu", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.35f);

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
		menuScreen = game.menuScreen;
		previousScreen = game.gameScreen;
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

		gameOverText.draw(spriteBatch, buttonFont);

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
		}
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
		previousScreen.dispose();
	}

}
