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
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.commons.gdx.input.LibgdxPointer;
import com.gemserk.commons.gdx.math.MathUtils2;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.ProfileJsonSerializer;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.scores.Scores.Range;
import com.gemserk.util.concurrent.FutureHandler;
import com.gemserk.util.concurrent.FutureProcessor;

public class HighscoresGameState extends GameStateImpl {

	static class ToggleableTextButton {

		private float x, y;

		private BitmapFont font;

		private String text;

		private Rectangle bounds = new Rectangle();

		private boolean pressed;

		private boolean released;

		private LibgdxPointer libgdxPointer = new LibgdxPointer(0);

		private Color color = new Color(1f, 1f, 1f, 1f);

		private Color selectedColor = new Color(1f, 1f, 1f, 1f);

		private boolean selected = false;

		public ToggleableTextButton setColor(Color color) {
			this.color.set(color);
			return this;
		}

		/**
		 * Increment size of the bounds by the specified w,h
		 */
		public ToggleableTextButton setBoundsOffset(float w, float h) {
			this.bounds = SpriteBatchUtils.getBounds(font, text, x, y, w, h);
			return this;
		}

		public ToggleableTextButton setSelectedColor(Color selectedColor) {
			this.selectedColor.set(selectedColor);
			return this;
		}

		public ToggleableTextButton setText(String text) {
			this.text = text;
			this.bounds = SpriteBatchUtils.getBounds(font, text, x, y, 0f, 0f);
			return this;
		}

		public ToggleableTextButton setSelected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public ToggleableTextButton(BitmapFont font, String text, float x, float y) {
			this.font = font;
			this.text = text;
			this.x = x;
			this.y = y;
			this.bounds = SpriteBatchUtils.getBounds(font, text, x, y, 0f, 0f);
		}

		public void draw(SpriteBatch spriteBatch) {
			if (selected)
				font.setColor(selectedColor);
			else
				font.setColor(color);
			SpriteBatchUtils.drawMultilineTextCentered(spriteBatch, font, text, x, y);
			// ImmediateModeRendererUtils.drawRectangle(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, Color.GREEN);
		}

		public boolean isPressed() {
			return pressed;
		}

		public boolean isReleased() {
			return released;
		}

		public void update() {

			libgdxPointer.update();

			pressed = false;
			released = false;

			if (libgdxPointer.wasPressed)
				pressed = MathUtils2.inside(bounds, libgdxPointer.getPressedPosition());

			if (libgdxPointer.wasReleased)
				released = MathUtils2.inside(bounds, libgdxPointer.getReleasedPosition());

			// NOTE: for now the button could be released while it was never pressed before

		}

	}

	private class RefreshScoresCallable implements Callable<Collection<Score>> {

		private Range range;

		public RefreshScoresCallable(Range range) {
			this.range = range;
		}

		@Override
		public Collection<Score> call() throws Exception {
			Set<String> tags = new HashSet<String>();
			return scores.getOrderedByPoints(tags, 10, false, range);
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
				Gdx.app.log("FaceHunt", e.getMessage(), e);
		}
	};

	private FutureProcessor<Collection<Score>> scoresRefreshProcessor;

	private ResourceManager<String> resourceManager;

	private Sprite backgroundSprite;

	private float newHeight;

	private static final Color yellowColor = new Color(1f, 1f, 0f, 1f);

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Preferences preferences;

	private Profile profile;

	private ToggleableTextButton allButton;

	private ToggleableTextButton monthlyButton;

	private ToggleableTextButton weeklyButton;

	private ToggleableTextButton dailyButton;

	// private Text tapScreenText;

	private TextButton tapScreenButton;

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

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

		String profileJson = preferences.getString("profile");
		profile = new ProfileJsonSerializer().parse(profileJson);

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		resourceManager = new ResourceManagerImpl<String>();
		new GameResourceBuilder(resourceManager);

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		backgroundSprite.setPosition(0, 0);
		backgroundSprite.setSize(viewportWidth, viewportHeight);

		// font = resourceManager.getResourceValue("ButtonFont");
		font = resourceManager.getResourceValue("ScoresFont");

//		font.setScale(1f * viewportWidth / 480f);
		font.setScale(1f);

		// font.setScale(1f);
		// newHeight = (viewportHeight * 0.9f / 12f) / font.getLineHeight();
		// font.setScale(newHeight);

		spriteBatch = new SpriteBatch();
		texts = new ArrayList<Text>();
		scoresRefreshProcessor = new FutureProcessor<Collection<Score>>(scoresRefreshHandler);

		reloadScores(Range.Day);

		// texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
		// Future<Collection<Score>> future = executorService.submit(new RefreshScoresCallable(Range.Day));
		// scoresRefreshProcessor.setFuture(future);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				if (Gdx.app.getType() == ApplicationType.Android)
					monitorKey("back", Keys.BACK);
				else
					monitorKey("back", Keys.ESCAPE);
			}
		};

		allButton = new ToggleableTextButton(font, "All", viewportWidth * 0.9f, viewportHeight * 0.85f) //
				.setColor(yellowColor).setSelectedColor(Color.BLUE).setBoundsOffset(40f, 20f);
		monthlyButton = new ToggleableTextButton(font, "Monthly", viewportWidth * 0.9f, viewportHeight * 0.65f) //
				.setColor(yellowColor).setSelectedColor(Color.BLUE).setBoundsOffset(40f, 20f);
		weeklyButton = new ToggleableTextButton(font, "Weekly", viewportWidth * 0.9f, viewportHeight * 0.45f) //
				.setColor(yellowColor).setSelectedColor(Color.BLUE).setBoundsOffset(40f, 20f);
		dailyButton = new ToggleableTextButton(font, "Daily", viewportWidth * 0.9f, viewportHeight * 0.25f) //
				.setColor(yellowColor).setSelectedColor(Color.BLUE).setSelected(true).setBoundsOffset(40f, 20f);

		tapScreenButton = new TextButton(font, "Tap here to return", viewportWidth * 0.5f, viewportHeight * 0.1f).setBoundsOffset(0f, 20f);
		tapScreenButton.setColor(yellowColor);
		tapScreenButton.setOverColor(yellowColor);
		tapScreenButton.setNotOverColor(yellowColor);

		// tapScreenText = new Text("Tap the screen to return", viewportWidth * 0.5f, viewportHeight * 0.1f).setColor(yellowColor);

	}

	@Override
	public void resume() {
		game.getAdWhirlViewHandler().hide();
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void pause() {
		game.getAdWhirlViewHandler().show();
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

		tapScreenButton.draw(spriteBatch);
		// tapScreenText.draw(spriteBatch, font);

		allButton.draw(spriteBatch);
		monthlyButton.draw(spriteBatch);
		weeklyButton.draw(spriteBatch);
		dailyButton.draw(spriteBatch);

		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		inputDevicesMonitor.update();
		scoresRefreshProcessor.update();

		allButton.update();
		monthlyButton.update();
		weeklyButton.update();
		dailyButton.update();

		tapScreenButton.update();

		if (allButton.isPressed()) {
			reloadScores(Range.All);

			allButton.setSelected(true);
			monthlyButton.setSelected(false);
			weeklyButton.setSelected(false);
			dailyButton.setSelected(false);

			return;
		}

		if (monthlyButton.isPressed()) {
			reloadScores(Range.Month);

			allButton.setSelected(false);
			monthlyButton.setSelected(true);
			weeklyButton.setSelected(false);
			dailyButton.setSelected(false);

			return;
		}

		if (weeklyButton.isPressed()) {
			reloadScores(Range.Week);

			allButton.setSelected(false);
			monthlyButton.setSelected(false);
			weeklyButton.setSelected(true);
			dailyButton.setSelected(false);

			return;
		}

		if (dailyButton.isPressed()) {
			reloadScores(Range.Day);

			allButton.setSelected(false);
			monthlyButton.setSelected(false);
			weeklyButton.setSelected(false);
			dailyButton.setSelected(true);

			return;
		}

		if (tapScreenButton.isPressed())
			game.transition(game.menuScreen, true);

		if (inputDevicesMonitor.getButton("back").isReleased())
			game.transition(game.menuScreen, true);
	}

	private void reloadScores(Range range) {
		texts.clear();
		texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
		Future<Collection<Score>> future = executorService.submit(new RefreshScoresCallable(range));
		scoresRefreshProcessor.setFuture(future);
	}

	private void refreshScores(Collection<Score> scoreList) {
		texts.clear();

		float x = viewportWidth * 0.5f;
		float y = viewportHeight * 0.98f;

		texts.add(new Text("HIGHSCORES", x, y, 0.5f, 0.5f).setColor(Color.GREEN));

		y -= font.getLineHeight() * font.getScaleY();

		texts.add(new Text("Name", viewportWidth * 0.3f, y, 0f, 0.5f).setColor(Color.GREEN));
		texts.add(new Text("Score", viewportWidth * 0.7f, y, 1f, 0.5f).setColor(Color.GREEN));

		y -= font.getLineHeight() * font.getScaleY();

		int index = 1;

		for (Score score : scoreList) {

			Color scoreColor = yellowColor;

			if (profile.getPublicKey() != null && profile.getPublicKey().equals(score.getProfilePublicKey()))
				scoreColor = Color.RED;

			Text numberText = new Text("" + index + ". ", viewportWidth * 0.3f, y, 1f, 0.5f).setColor(scoreColor);
			Text nameText = new Text(score.getName(), viewportWidth * 0.3f, y, 0f, 0.5f).setColor(scoreColor);
			Text pointsText = new Text(Long.toString(score.getPoints()), viewportWidth * 0.7f, y, 1f, 0.5f).setColor(scoreColor);

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
