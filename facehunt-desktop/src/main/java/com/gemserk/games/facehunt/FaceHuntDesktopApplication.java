package com.gemserk.games.facehunt;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.DesktopAnalyticsAutoConfigurator;

public class FaceHuntDesktopApplication {
	public static void main(String[] argv) {
		AnalyticsConfigData config = new AnalyticsConfigData("UA-23542248-3");
		DesktopAnalyticsAutoConfigurator.populateFromSystem(config);
		
		JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(config,GoogleAnalyticsVersion.V_4_7_2);
		Analytics.traker = tracker;
		
		new LwjglApplication(new FaceHuntGame(), "Face Hunt", 800, 480, false);
		// new LwjglApplication(new FaceHuntGame(), "Face Hunt", 480, 320, false);
		// new LwjglApplication(new FaceHuntGame(), "Face Hunt", 1024, 768, false);
	}
}
