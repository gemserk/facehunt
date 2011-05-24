package com.gemserk.games.facehunt;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class FaceHuntAndroidApplication extends AndroidApplication  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new FaceHuntGame(), false);
	}	
}