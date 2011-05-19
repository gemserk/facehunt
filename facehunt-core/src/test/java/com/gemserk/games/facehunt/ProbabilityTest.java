package com.gemserk.games.facehunt;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

public class ProbabilityTest {

	@Test
	public void test() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 1, 0.8f), new EnemySpawnInfo(1, 1, 0.2f) };

		Spawner spawner = new Spawner(types);

		assertThat(spawner.getType(0.1f), IsEqual.equalTo(0));
		assertThat(spawner.getType(0.8f), IsEqual.equalTo(1));
	}

	@Test
	public void test2() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 1, 0.8f) };

		Spawner spawner = new Spawner(types);

		assertThat(spawner.getType(0.1f), IsEqual.equalTo(0));
		assertThat(spawner.getType(0.9f), IsEqual.equalTo(0));
	}

	@Test
	public void test3() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 1, 0.3f), new EnemySpawnInfo(1, 1, 0.2f), new EnemySpawnInfo(2, 1, 0.5f) };

		Spawner spawner = new Spawner(types);

		assertThat(spawner.getType(0f), IsEqual.equalTo(0));
		assertThat(spawner.getType(0.3f), IsEqual.equalTo(1));
		assertThat(spawner.getType(0.5f), IsEqual.equalTo(1));
		assertThat(spawner.getType(0.7f), IsEqual.equalTo(2));
	}

	@Test
	public void testDependOnTotalCount() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 10, 0.5f), new EnemySpawnInfo(1, 1, 0.2f), new EnemySpawnInfo(2, 1, 0.3f) };

		Spawner spawner = new Spawner(types);

		assertThat(spawner.getType(0f), IsEqual.equalTo(0));
		assertThat(spawner.getType(0.3f), IsEqual.equalTo(0));
		assertThat(spawner.getType(0.9f), IsEqual.equalTo(0));
	}

	@Test
	public void testDependOnTotalCount2() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 10, 0.5f), new EnemySpawnInfo(1, 1, 0.2f), new EnemySpawnInfo(2, 1, 0.3f) };
		Spawner spawner = new Spawner(types);
		assertThat(spawner.getType(0.9f), IsEqual.equalTo(0));
		spawner.remove(0, 9);
		assertThat(spawner.getType(0.5f), IsEqual.equalTo(1));
	}
	
	@Test
	public void testIsEmpty() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 5, 0.5f), new EnemySpawnInfo(1, 5, 0.2f) };
		Spawner spawner = new Spawner(types);
		boolean empty = spawner.isEmpty();
		assertThat(empty, IsEqual.equalTo(false));
	}
	
	@Test
	public void testIsEmpty2() {
		EnemySpawnInfo[] types = new EnemySpawnInfo[] { new EnemySpawnInfo(0, 0, 0.5f), new EnemySpawnInfo(1, 0, 0.2f) };
		Spawner spawner = new Spawner(types);
		boolean empty = spawner.isEmpty();
		assertThat(empty, IsEqual.equalTo(true));
	}

}
