package com.gemserk.games.facehunt;

public class EnemySpawnInfo {

	public int type;

	public int count;

	public float probability;

	public EnemySpawnInfo(int type, int count, float probability) {
		super();
		this.type = type;
		this.count = count;
		this.probability = probability;
	}

}