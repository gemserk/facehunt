package com.gemserk.games.facehunt.values;

import com.badlogic.gdx.math.Vector2;

public class Movement {
	
	public Vector2 velocity;
	
	public float angularVelocity;
	
	public Movement() {
		this(new Vector2());
	}
	
	public Movement(Vector2 velocity) {
		this(velocity, 0f);
	}

	public Movement(Vector2 velocity, float angularVelocity) {
		this.velocity = velocity;
		this.angularVelocity = angularVelocity;
	}

	public Movement set(Movement movement) {
		this.velocity.set(movement.velocity);
		this.angularVelocity = movement.angularVelocity;
		return this;
	}

}
