package com.gemserk.games.facehunt.gamestates;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.componentsengine.utils.Container;

public class FaceHuntRenderUtils {

	/**
	 * Renders a health bar using a white sprite rectangle.
	 */
	public static void renderBar(SpriteBatch spriteBatch, Sprite rectangle, Container health, float x, int y, float width, float height) {
		float border = 1f;

		rectangle.setColor(Color.BLACK);
		rectangle.setPosition(x - border, y - border);
		rectangle.setSize(width + border * 2f, height + border * 2f);
		rectangle.draw(spriteBatch);

		rectangle.setColor(Color.RED);
		rectangle.setPosition(x, y);
		rectangle.setSize(width, height);
		rectangle.draw(spriteBatch);

		rectangle.setColor(Color.GREEN);
		rectangle.setPosition(x, y);
		rectangle.setSize(width * health.getPercentage(), height);
		rectangle.draw(spriteBatch);
	}

}