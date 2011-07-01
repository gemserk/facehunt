package com.gemserk.games.facehunt;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.dmurph.tracking.VisitorData;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.android.AnalyticsStoredConfig;
import com.gemserk.analytics.googleanalytics.android.BasicConfig;
import com.gemserk.commons.adwhirl.AdWhirlAndroidHandler;
import com.gemserk.commons.adwhirl.CustomAdViewHandler;
import com.gemserk.commons.adwhirl.PausableAdWhirlLayout;

public class FaceHuntAndroidApplication extends AndroidApplication  implements AdWhirlInterface  {

	private PausableAdWhirlLayout adView;
	private AnalyticsStoredConfig storedConfig;
	private VisitorData visitorData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RelativeLayout layout = new RelativeLayout(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useGL20 = false;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;

		AdWhirlManager.setConfigExpireTimeout(1000 * 15);
		AdWhirlTargeting.setAge(23);
		AdWhirlTargeting.setGender(AdWhirlTargeting.Gender.MALE);
		AdWhirlTargeting.setKeywords("online games gaming");
		AdWhirlTargeting.setPostalCode("94123");
		AdWhirlTargeting.setTestMode(false);

		adView = new PausableAdWhirlLayout(this, "7b8dd38c0a3e48f3bdf995791d6d186e");
		
		Handler handler = new AdWhirlAndroidHandler(adView);
		CustomAdViewHandler adWhirlViewHandler = new CustomAdViewHandler(handler);
		FaceHuntGame faceHuntGame = new FaceHuntGame(adWhirlViewHandler){
			@Override
			public void pause() {
				super.pause();
				saveAnalyticsData();
			}
		};
		
		View gameView = initializeForView(faceHuntGame, config);

		int diWidth = 800;
		int diHeight = 50;
		
		float density = getResources().getDisplayMetrics().density;

		adView.setAdWhirlInterface(this);
		adView.setMaxWidth((int) (diWidth * density));
		adView.setMaxHeight((int) (diHeight * density));

		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		layout.addView(gameView);
		layout.addView(adView, adParams);

		setContentView(layout);
		
		storedConfig = new AnalyticsStoredConfig(getApplicationContext());
		visitorData = storedConfig.loadVisitor();
		
		AnalyticsConfigData analyticsconfig = new AnalyticsConfigData("UA-23542248-3",visitorData);
		BasicConfig.configure(analyticsconfig, getApplicationContext());
		
		JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(analyticsconfig,GoogleAnalyticsVersion.V_4_7_2);
		Analytics.traker = tracker;
	}
	
	protected void saveAnalyticsData() {
		Analytics.traker.completeBackgroundTasks(500);
		storedConfig.saveVisitor(visitorData);
	}
	
	@Override
	public void adWhirlGeneric() {

	}

}