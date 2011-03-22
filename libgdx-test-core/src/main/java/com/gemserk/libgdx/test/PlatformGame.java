package com.gemserk.libgdx.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PlatformGame extends Game {

	@Override
	public void create() {
		
		Texture gemserkLogo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		
		setScreen(new SplashScreen(this, gemserkLogo));
		
	}


}
