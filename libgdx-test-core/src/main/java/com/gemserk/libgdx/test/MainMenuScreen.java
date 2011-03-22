package com.gemserk.libgdx.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class MainMenuScreen extends ScreenAdapter {

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

}
