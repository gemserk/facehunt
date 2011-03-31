package com.gemserk.games.facehunt;

import roboguice.application.GuiceApplication;
import roboguice.inject.InjectorProvider;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.gemserk.componentsengine.reflection.internalfields.PropertiesInternalFields;
import com.gemserk.games.facehunt.PlatformGame;
import com.google.inject.Injector;

public class FaceHuntAndroidApplication extends AndroidApplication implements InjectorProvider {

	GuiceApplication guiceApplication;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new PlatformGame(), false);
		PropertiesInternalFields.useFastClassIfPossible = false;
	}

	@Override
	public Injector getInjector() {
		return guiceApplication.getInjector();
	}

}