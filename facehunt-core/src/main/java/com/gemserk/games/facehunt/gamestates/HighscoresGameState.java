package com.gemserk.games.facehunt.gamestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;

public class HighscoresGameState extends GameStateImpl {

	private class SubmitScoreCallable implements Callable<String> {

		private final Score score;

		private SubmitScoreCallable(Score score) {
			this.score = score;
		}

		@Override
		public String call() throws Exception {
			return scores.submit(score);
		}

	}

	private class RefreshScoresCallable implements Callable<Collection<Score>> {
		@Override
		public Collection<Score> call() throws Exception {
			Set<String> tags = new HashSet<String>();
			return scores.getOrderedByPoints(tags, 10, false);
		}
	}

	interface FutureHandler<T> {

		void done(T t);

		void failed(Exception e);

	}

	interface ScoreSubmitHandler extends FutureHandler<String> {

		void done(String scoreId);

		void failed(Exception e);

	}

	class ScoreSubmitHanlderImpl implements ScoreSubmitHandler {

		public void done(String scoreId) {
			score.setId(scoreId);
			texts.clear();
			texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
			refreshScoresFuture = executorService.submit(new RefreshScoresCallable());
			futureProcessor.setFuture(refreshScoresFuture);
		}

		public void failed(Exception e) {
			texts.clear();
			texts.add(new Text("Submit score failed...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
			if (e != null)
				Gdx.app.log("FaceHunt", e.getMessage());
		}

	}

	interface ScoresRefreshHandler extends FutureHandler<Collection<Score>> {

		void done(Collection<Score> scores);

		void failed(Exception e);

	}

	class ScoresRefreshHandlerImpl implements ScoresRefreshHandler {

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

	}

	static class FutureProcessor<T> {

		private final FutureHandler<T> futureHandler;

		private Future<T> future;

		public void setFuture(Future<T> future) {
			this.future = future;
		}

		public FutureProcessor(FutureHandler<T> scoresRefreshHandler) {
			this.futureHandler = scoresRefreshHandler;
		}

		public void update() {
			if (future == null)
				return;

			if (!future.isDone())
				return;

			try {

				if (future.isCancelled()) {
					futureHandler.failed(null);
				} else {
					futureHandler.done(future.get());
				}

			} catch (InterruptedException e) {
				futureHandler.failed(e);
			} catch (ExecutionException e) {
				futureHandler.failed(e);
			}

			future = null;
		}

	}

	private final FaceHuntGame game;

	private SpriteBatch spriteBatch;

	private BitmapFont font;

	private Scores scores;

	private Score score;

	private ArrayList<Text> texts;

	private int viewportWidth;

	private int viewportHeight;

	private Future<Collection<Score>> refreshScoresFuture;

	private Future<String> submitScoreFuture;

	private ExecutorService executorService;

	private ScoreSubmitHandler scoreSubmitHanlderImpl = new ScoreSubmitHanlderImpl();

	private ScoresRefreshHandler scoresRefreshHandler = new ScoresRefreshHandlerImpl();

	private FutureProcessor futureProcessor;

	public HighscoresGameState(FaceHuntGame game) {
		this.game = game;
		this.scores = game.scores;
	}

	@Override
	public void init() {
		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		font = new BitmapFont();
		spriteBatch = new SpriteBatch();
		texts = new ArrayList<Text>();
		executorService = Executors.newCachedThreadPool();

		if (score != null) {
			texts.add(new Text("Submitting score...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
			submitScoreFuture = executorService.submit(new SubmitScoreCallable(score));
		} else {
			texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
			refreshScoresFuture = executorService.submit(new RefreshScoresCallable());
		}

		futureProcessor = new FutureProcessor(scoresRefreshHandler);
		futureProcessor.setFuture(refreshScoresFuture);

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
		futureProcessor.update();
		// processRefreshScores();
		processSubmitScore();
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

			if (this.score != null && this.score.getId().equals(score.getId())) {
				nameText.setColor(Color.RED);
				pointsText.setColor(Color.RED);
			}

			texts.add(numberText);
			texts.add(nameText);
			texts.add(pointsText);

			y -= font.getLineHeight() + 5f;
			index++;
		}
	}

	private void processSubmitScore() {
		if (submitScoreFuture == null)
			return;

		if (!submitScoreFuture.isDone())
			return;

		try {

			if (submitScoreFuture.isCancelled()) {
				scoreSubmitHanlderImpl.failed(null);
			} else {
				String scoreId = submitScoreFuture.get();
				scoreSubmitHanlderImpl.done(scoreId);
			}

		} catch (InterruptedException e) {
			scoreSubmitHanlderImpl.failed(e);
		} catch (ExecutionException e) {
			scoreSubmitHanlderImpl.failed(e);
		}

		submitScoreFuture = null;
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		score = null;
		font.dispose();
		try {
			executorService.shutdown();
			executorService.awaitTermination(1000l, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
