package com.gemserk.games.facehunt;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Vector2Random {

	private static final Vector2 MIN = new Vector2(-Float.MAX_VALUE, -Float.MAX_VALUE);

	private static final Vector2 MAX = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);

	private static Random random = new Random();

	public static Vector2 vector2(float x1, float y1, float x2, float y2) {
		return new Vector2( //
				random.nextFloat() * (x2 - x1) + x1, //
				random.nextFloat() * (y2 - y1) + y1);
	}

	public static Vector2 vector2(Vector2 min, Vector2 max) {
		return vector2(min.x, min.y, max.y, max.y);
	}

	public static Vector2 vector2() {
		return vector2(MIN, MAX);
	}

}