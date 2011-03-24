package com.gemserk.games.facehunt.values;

import com.badlogic.gdx.math.Vector2;

public class Movement {
	
	public Vector2 position;
	
	public Vector2 velocity;
	
	public Movement() {
		this(new Vector2(), new Vector2());
	}
	
	public Movement(Vector2 position, Vector2 velocity) {
		this.position = position;
		this.velocity = velocity;
	}

}
