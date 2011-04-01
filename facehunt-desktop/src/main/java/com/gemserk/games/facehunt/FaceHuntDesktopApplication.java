package com.gemserk.games.facehunt;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class FaceHuntDesktopApplication {
	public static void main (String[] argv) {
		new LwjglApplication(new FaceHuntGame(), "Face Hunt", 800, 480, false);
	}
}
