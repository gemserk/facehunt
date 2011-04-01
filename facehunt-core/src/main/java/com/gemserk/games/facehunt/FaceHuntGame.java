package com.gemserk.games.facehunt;

import com.badlogic.gdx.Game;

public class FaceHuntGame extends Game {
	
	@Override
	public void create() {
		setScreen(new SplashScreen(this));
	}

}
