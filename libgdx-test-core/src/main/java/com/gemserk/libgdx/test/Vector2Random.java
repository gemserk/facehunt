package com.gemserk.libgdx.test;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Vector2Random {
	
	private static final Vector2 MIN = new Vector2(-Float.MAX_VALUE, -Float.MAX_VALUE);
	
	private static final Vector2 MAX = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
	
	private static Random random = new Random();
	
	public static Vector2 vector2(Vector2 min, Vector2 max) {
		return new Vector2(random.nextFloat() * (max.x - min.x) + min.x, // 
				random.nextFloat() * (max.y - min.y) + min.y);
	}

	public static Vector2 vector2() {
		return vector2(MIN, MAX);
	}

}