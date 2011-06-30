package com.gemserk.games.facehunt;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;

public class FaceHuntDesktopApplication {
	public static void main (String[] argv) {
		new LwjglApplication(new FaceHuntGame(new AdWhirlViewHandler()), "Face Hunt", 800, 480, false);
//		 new LwjglApplication(new FaceHuntGame(), "Face Hunt", 480, 320, false);
//		 new LwjglApplication(new FaceHuntGame(), "Face Hunt", 1024, 768, false);
	}
}
