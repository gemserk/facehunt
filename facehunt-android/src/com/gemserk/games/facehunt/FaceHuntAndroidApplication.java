package com.gemserk.games.facehunt;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class FaceHuntAndroidApplication extends AndroidApplication  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = false;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		initialize(new FaceHuntGame(), config);
		
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

		adView = new PausableAdWhirlLayout(this, "5d99c9fc499b41e5be30b22e3b52d799");
		
		handler = new AdWhirlAndroidHandler(adView);
		CustomAdViewHandler adWhirlViewHandler = new CustomAdViewHandler(handler);
		View gameView = initializeForView(new FaceHuntGame(adWhirlViewHandler), config);

		int diWidth = 400;
		int diHeight = 32;
		
		float density = getResources().getDisplayMetrics().density;

		adView.setAdWhirlInterface(this);
		adView.setMaxWidth((int) (diWidth * density));
		adView.setMaxHeight((int) (diHeight * density));

		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		layout.addView(gameView);
		layout.addView(adView, adParams);

		setContentView(layout);
	}
	
}