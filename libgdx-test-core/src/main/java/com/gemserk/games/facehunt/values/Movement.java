package com.gemserk.games.facehunt.values;

import com.badlogic.gdx.math.Vector2;

public class Movement {
	
	public Vector2 velocity;
	
	public Movement() {
		this(new Vector2());
	}
	
	public Movement(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public Movement set(Movement movement) {
		this.velocity.set(movement.velocity);
		return this;
	}

}
