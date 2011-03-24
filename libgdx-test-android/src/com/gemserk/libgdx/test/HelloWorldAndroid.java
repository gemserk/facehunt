package com.gemserk.libgdx.test;

import roboguice.application.GuiceApplication;
import roboguice.inject.InjectorProvider;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.inject.Injector;

public class HelloWorldAndroid extends AndroidApplication implements InjectorProvider {
	
	GuiceApplication guiceApplication;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new PlatformGame(), false);
	}
	
	@Override
	public Injector getInjector() {
		return guiceApplication.getInjector();
	}

	
}