package com.gemserk.libgdx.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.interpolator.GenericInterpolator;
import com.gemserk.animation4j.timeline.TimelineAnimationBuilder;
import com.gemserk.animation4j.timeline.TimelineValueBuilder;
import com.gemserk.animation4j.timeline.sync.ObjectSynchronizer;
import com.gemserk.animation4j.timeline.sync.SynchrnonizedAnimation;
import com.gemserk.animation4j.timeline.sync.TimelineSynchronizer;

public class SplashScreen extends ScreenAdapter {

	private final Game game;

	private final Texture companyLogo;

	private SpriteBatch spriteBatch;

	private Color color = Color.BLACK;

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	private SynchrnonizedAnimation splashAnimation;

	public SplashScreen(Game game, Texture companyLogo) {
		this.game = game;
		this.companyLogo = companyLogo;
		this.spriteBatch = new SpriteBatch();

		// we can use one generic interpolator (with internal state) for each animation value, can't be reused between different animation values
		final GenericInterpolator<Color> genericColorInterpolator = new GenericInterpolator<Color>(Converters.color());

		// ObjectSynchronizer objectSynchronizer = new ReflectionObjectSynchronizer(this);
		ObjectSynchronizer objectSynchronizer = new ObjectSynchronizer() {

			@Override
			public void setValue(String name, Object value) {
				if (!"color".equals(name))
					return;
				color.set((Color) value);
			}
		};

		splashAnimation = new SynchrnonizedAnimation(new TimelineAnimationBuilder() {
			{
				delay(1000);
				value("color", new TimelineValueBuilder<Color>() {
					{
						keyFrame(0, new Color(1f, 1f, 1f, 0f), genericColorInterpolator);
						keyFrame(2000, new Color(1f, 1f, 1f, 1f), genericColorInterpolator);
						keyFrame(4000, new Color(1f, 1f, 1f, 1f), genericColorInterpolator);
						keyFrame(4250, new Color(1f, 1f, 0.3f, 0.7f), genericColorInterpolator);
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
		spriteBatch.draw(companyLogo, centerX - companyLogo.getWidth() / 2, centerY - companyLogo.getHeight() / 2, 0, 0, companyLogo.getWidth(), companyLogo.getHeight());
		spriteBatch.end();

		splashAnimation.update(delta * 1000);

		if (splashAnimation.isFinished())
			game.setScreen(new MainMenuScreen());

	}
	
}