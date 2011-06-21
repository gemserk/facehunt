package com.gemserk.games.facehunt.gui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;

public class ToggleableImageButton {
	
	float x,y;
	
	boolean enabled;
	
	Sprite enabledSprite;
	
	Sprite disabledSprite;
	
	Rectangle bounds;
	
	public ToggleableImageButton setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public ToggleableImageButton setEnabledSprite(Sprite enabledSprite) {
		this.enabledSprite = enabledSprite;
		return this;
	}
	
	public ToggleableImageButton setDisabledSprite(Sprite disabledSprite) {
		this.disabledSprite = disabledSprite;
		return this;
	}
	
	public ToggleableImageButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public ToggleableImageButton setBounds(Rectangle bounds) {
		this.bounds = bounds;
		return this;
	}
	
	public ToggleableImageButton() {

	}
	
	public void toggle() {
		enabled = !enabled;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		if (enabled) {
			SpriteBatchUtils.drawCentered(spriteBatch, enabledSprite, x, y, 0f);
		} else {
			SpriteBatchUtils.drawCentered(spriteBatch, disabledSprite, x, y, 0f);
		}
	}
	
	public void udpate(int delta) {
		
	}

}
