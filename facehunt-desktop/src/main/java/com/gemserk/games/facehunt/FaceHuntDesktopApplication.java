package com.gemserk.games.facehunt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.DesktopAnalyticsAutoConfigurator;
import com.gemserk.scores.ScoresMemoryImpl;

public class FaceHuntDesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(FaceHuntDesktopApplication.class);

	public static void main(String[] argv) {
		AnalyticsConfigData analyticsConfig = new AnalyticsConfigData("UA-23542248-3");
		DesktopAnalyticsAutoConfigurator.populateFromSystem(analyticsConfig);

		JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(analyticsConfig, GoogleAnalyticsVersion.V_4_7_2);
		Analytics.traker = tracker;

		String runningInDebug = System.getProperty("runningInDebug");
		boolean debugMode = runningInDebug != null;

		if (debugMode) {
			logger.info("Running in debug mode, Analytics disabled");
			Analytics.traker.setEnabled(false);
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		config.title = "Face Hunt";
		config.forceExit = true;

		FaceHuntGame faceHuntGame = new FaceHuntGame() {
			@Override
			public void create() {
				Gdx.graphics.setVSync(true);
				super.create();
			}
		};

		if (debugMode) {
			// faceHuntGame.setProfiles(profiles);
			faceHuntGame.setScores(new ScoresMemoryImpl());
		}

		new LwjglApplication(faceHuntGame, config);
		// new LwjglApplication(new FaceHuntGame(), "Face Hunt", 480, 320, false);
		// new LwjglApplication(new FaceHuntGame(), "Face Hunt", 1024, 768, false);
	}
}
