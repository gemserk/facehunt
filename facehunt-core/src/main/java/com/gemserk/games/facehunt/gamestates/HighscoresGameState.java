package com.gemserk.games.facehunt.gamestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.util.concurrent.FutureHandler;
import com.gemserk.util.concurrent.FutureProcessor;

public class HighscoresGameState extends GameStateImpl {

	private class RefreshScoresCallable implements Callable<Collection<Score>> {
		@Override
		public Collection<Score> call() throws Exception {
			Set<String> tags = new HashSet<String>();
			return scores.getOrderedByPoints(tags, 10, false);
		}
	}

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private BitmapFont font;

	private Scores scores;

	private ArrayList<Text> texts;

	private int viewportWidth;

	private int viewportHeight;

	private ExecutorService executorService;

	private FutureHandler<Collection<Score>> scoresRefreshHandler = new FutureHandler<Collection<Score>>() {
		@Override
		public void done(Collection<Score> scores) {
			refreshScores(scores);
		}

		@Override
		public void failed(Exception e) {
			texts.clear();
			texts.add(new Text("Refresh scores failed...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f).setColor(Color.RED));
			if (e != null)
				Gdx.app.log("FaceHunt", e.getMessage());
		}
	};

	private FutureProcessor<Collection<Score>> scoresRefreshProcessor;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private float newHeight;

	private static final Color yellowColor = new Color(1f, 1f, 0f, 1f);

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public HighscoresGameState(FaceHuntGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		resourceManager = new ResourceManagerImpl<String>();
		new GameResourceBuilder(resourceManager);

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);
		backgroundSprite.setSize(viewportWidth, viewportHeight);

		font = resourceManager.getResourceValue("ButtonFont");
		font.setScale(1f);
		newHeight = (viewportHeight * 0.9f / 12f) / font.getLineHeight();
		font.setScale(newHeight);

		spriteBatch = new SpriteBatch();
		texts = new ArrayList<Text>();
		scoresRefreshProcessor = new FutureProcessor<Collection<Score>>(scoresRefreshHandler);

		texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
		Future<Collection<Score>> future = executorService.submit(new RefreshScoresCallable());
		scoresRefreshProcessor.setFuture(future);
		
		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				if (Gdx.app.getType() == ApplicationType.Android)
					monitorKey("back", Keys.BACK);
				else
					monitorKey("back", Keys.ESCAPE);
			}
		};
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
		for (int i = 0; i < texts.size(); i++) {
			Text text = texts.get(i);
			text.draw(spriteBatch, font);
		}
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		inputDevicesMonitor.update();
		scoresRefreshProcessor.update();
		if (Gdx.input.justTouched())
			game.transition(game.menuScreen, true);
		if (inputDevicesMonitor.getButton("back").isReleased()) 
			game.transition(game.menuScreen, true);
	}

	private void refreshScores(Collection<Score> scoreList) {
		texts.clear();

		float x = viewportWidth * 0.5f;
		float y = viewportHeight * 0.95f;

		texts.add(new Text("HIGHSCORES", x, y, 0.5f, 0.5f).setColor(Color.GREEN));

		y -= font.getLineHeight() * font.getScaleY();

		texts.add(new Text("Name", viewportWidth * 0.3f, y, 0f, 0.5f).setColor(Color.GREEN));
		texts.add(new Text("Score", viewportWidth * 0.7f, y, 1f, 0.5f).setColor(Color.GREEN));

		y -= font.getLineHeight() * font.getScaleY();

		int index = 1;

		for (Score score : scoreList) {

			Text numberText = new Text("" + index + ". ", viewportWidth * 0.3f, y, 1f, 0.5f).setColor(yellowColor);
			Text nameText = new Text(score.getName(), viewportWidth * 0.3f, y, 0f, 0.5f).setColor(yellowColor);
			Text pointsText = new Text(Long.toString(score.getPoints()), viewportWidth * 0.7f, y, 1f, 0.5f).setColor(yellowColor);

			texts.add(numberText);
			texts.add(nameText);
			texts.add(pointsText);

			y -= font.getLineHeight() * font.getScaleY();
			index++;
		}
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		font.dispose();
		resourceManager.unloadAll();
	}
}
