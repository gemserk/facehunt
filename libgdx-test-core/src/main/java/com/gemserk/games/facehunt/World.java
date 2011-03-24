package com.gemserk.games.facehunt;

import com.badlogic.gdx.math.Vector2;

public class World {

	public Vector2 min;

	public Vector2 max;

	public World(Vector2 min, Vector2 max) {
		this.min = min;
		this.max = max;
	}

}