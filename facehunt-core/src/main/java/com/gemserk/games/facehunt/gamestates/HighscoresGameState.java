package com.gemserk.games.facehunt.gamestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.games.facehunt.FaceHuntGame;
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
			texts.add(new Text("Refresh scores failed...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
			if (e != null)
				Gdx.app.log("FaceHunt", e.getMessage());
		}
	};

	private FutureProcessor<Collection<Score>> scoresRefreshProcessor;
	
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

		font = new BitmapFont();
		spriteBatch = new SpriteBatch();
		texts = new ArrayList<Text>();
		scoresRefreshProcessor = new FutureProcessor<Collection<Score>>(scoresRefreshHandler);

		texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
		Future<Collection<Score>> future = executorService.submit(new RefreshScoresCallable());
		scoresRefreshProcessor.setFuture(future);
	}

	@Override
	public void render(int delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		for (int i = 0; i < texts.size(); i++) {
			Text text = texts.get(i);
			text.draw(spriteBatch, font);
		}
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		scoresRefreshProcessor.update();
		if (Gdx.input.justTouched())
			game.transition(game.menuScreen, true);
	}

	private void refreshScores(Collection<Score> scoreList) {
		texts.clear();

		float x = viewportWidth * 0.5f;
		float y = viewportHeight * 0.8f;

		texts.add(new Text("HIGHSCORES", x, y, 0.5f, 0.5f));

		y -= font.getLineHeight() + 5f;

		texts.add(new Text("Name", viewportWidth * 0.3f, y, 0f, 0.5f));
		texts.add(new Text("Score", viewportWidth * 0.7f, y, 1f, 0.5f));

		y -= font.getLineHeight() + 5f;

		int index = 1;

		for (Score score : scoreList) {

			Text numberText = new Text("" + index + ". ", viewportWidth * 0.3f, y, 1f, 0.5f);
			Text nameText = new Text(score.getName(), viewportWidth * 0.3f, y, 0f, 0.5f);
			Text pointsText = new Text(Long.toString(score.getPoints()), viewportWidth * 0.7f, y, 1f, 0.5f);

			texts.add(numberText);
			texts.add(nameText);
			texts.add(pointsText);

			y -= font.getLineHeight() + 5f;
			index++;
		}
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		font.dispose();
	}
}
