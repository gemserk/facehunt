package com.gemserk.games.facehunt;

public class Spawner {

	private final EnemySpawnInfo[] types;

	private float[] prob;

	public Spawner(EnemySpawnInfo[] types) {
		this.types = types;
		this.prob = new float[types.length];
	}

	public int getType(float p) {
		float x = 0f;

		for (int i = 0; i < types.length; i++) {
			EnemySpawnInfo enemySpawnInfo = types[i];
			x += enemySpawnInfo.probability * enemySpawnInfo.count;
		}

		for (int i = 0; i < types.length; i++) {
			EnemySpawnInfo enemySpawnInfo = types[i];
			prob[i] = enemySpawnInfo.probability * enemySpawnInfo.count / x;
		}

		for (int i = 0; i < types.length; i++) {
			if (p < prob[i])
				return types[i].type;
			p -= prob[i];
		}

		return types[types.length - 1].type;
	}

	public void remove(int type, int count) {
		for (int i = 0; i < types.length; i++) {
			EnemySpawnInfo enemySpawnInfo = types[i];
			if (enemySpawnInfo.type != type)
				continue;
			enemySpawnInfo.count -= count;
			if (enemySpawnInfo.count < 0)
				enemySpawnInfo.count = 0;
			return;
		}
	}

	public boolean isEmpty() {
		int totalCount = 0;
		for (int i = 0; i < types.length; i++) {
			EnemySpawnInfo enemySpawnInfo = types[i];
			totalCount += enemySpawnInfo.count;
		}
		return totalCount <= 0;
	}

}