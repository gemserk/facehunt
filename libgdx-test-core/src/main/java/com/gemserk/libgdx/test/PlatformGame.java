package com.gemserk.libgdx.test;

import com.badlogic.gdx.Game;

public class PlatformGame extends Game {
	
	public static final String applicationName = "PlatformGame";

	@Override
	public void create() {
		setScreen(new SplashScreen(this));
//		setScreen(new MainMenuScreen(this));
	}


}
