package com.gemserk.commons.gdx.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;

public class Text {

	private String text;

	private final float x;

	private final float y;

	private boolean visible;

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
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