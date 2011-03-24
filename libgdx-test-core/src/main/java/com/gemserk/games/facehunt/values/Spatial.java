package com.gemserk.games.facehunt.values;

import com.badlogic.gdx.math.Vector2;

public class Spatial {
	
	public Vector2 position;
	
	public float angle;
	
	public Spatial() {
		this(new Vector2(), 0f);
	}
	
	public Spatial(Vector2 position, float angle) {
		this.position = position;
		this.angle = angle;
	}

}
