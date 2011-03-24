package com.gemserk.games.facehunt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.animation4j.interpolator.GenericInterpolator;
import com.gemserk.animation4j.timeline.TimelineAnimationBuilder;
import com.gemserk.animation4j.timeline.TimelineValueBuilder;
import com.gemserk.animation4j.timeline.sync.ObjectSynchronizer;
import com.gemserk.animation4j.timeline.sync.ReflectionObjectSynchronizer;
import com.gemserk.animation4j.timeline.sync.SynchrnonizedAnimation;
import com.gemserk.animation4j.timeline.sync.TimelineSynchronizer;

public class SplashScreen extends ScreenAdapter {

	private final Game game;

	private final Texture logo;

	private SpriteBatch spriteBatch;

	private Color color = Color.BLACK;

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	private SynchrnonizedAnimation splashAnimation;

	public SplashScreen(Game game) {
		this.game = game;
		this.logo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		this.spriteBatch = new SpriteBatch();

		// we can use one generic interpolator (with internal state) for each animation value, can't be reused between different animation values
		final GenericInterpolator<Color> genericColorInterpolator = new GenericInterpolator<Color>(LibgdxConverters.color());

		ObjectSynchronizer objectSynchronizer = new ReflectionObjectSynchronizer(this);

		splashAnimation = new SynchrnonizedAnimation(new TimelineAnimationBuilder() {
			{
				delay(1000);
				value("color", new TimelineValueBuilder<Color>() {
					{
						keyFrame(0, new Color(1f, 1f, 1f, 0f), genericColorInterpolator);
						keyFrame(2000, new Color(1f, 1f, 1f, 1f), genericColorInterpolator);
						keyFrame(4000, new Color(1f, 1f, 1f, 1f), genericColorInterpolator);
						keyFrame(4250, new Color(1f, 1f, 1f, 0.7f), genericColorInterpolator);
						keyFrame(4500, new Color(0f, 0f, 0f, 0f), genericColorInterpolator);
					}
				});
				speed(1.3f);
			}
		}.build(), new TimelineSynchronizer(objectSynchronizer));

		splashAnimation.start(1);

	}

	@Override
	public void render(float delta) {
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		// spriteBatch.setColor(color.get());
		spriteBatch.setColor(color);
		spriteBatch.draw(logo, centerX - logo.getWidth() / 2, centerY - logo.getHeight() / 2, 0, 0, logo.getWidth(), logo.getHeight());
		spriteBatch.end();

		splashAnimation.update(delta * 1000);

		if (splashAnimation.isFinished())
			game.setScreen(new GameScreen(game));

	}

	@Override
	public void show() {
		Gdx.app.log(PlatformGame.applicationName, "entered splash screen");
	}

	@Override
	public void dispose() {
		this.logo.dispose();
	}

}