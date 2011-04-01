package com.gemserk.games.facehunt.values;

import com.badlogic.gdx.math.Vector2;

public class Spatial {
	
	public Vector2 position;
	
	public Vector2 size;
	
	public float angle;
	
	public void setSize(Vector2 size) {
		this.size = size;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public Spatial() {
		this(new Vector2(0f, 0f), 0f);
	}
	
	public Spatial(Vector2 position, float angle) {
		this(position, new Vector2(1f, 1f), angle);
	}

	public Spatial(Vector2 position, Vector2 size, float angle) {
		this.position = position;
		this.size = size;
		this.angle = angle;
	}

	public Spatial set(Spatial spatial) {
		this.position.set(spatial.position);
		this.size.set(spatial.size);
		this.angle = spatial.angle;
		return this;
	}

}
