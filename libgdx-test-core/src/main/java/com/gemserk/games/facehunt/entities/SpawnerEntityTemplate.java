package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.timers.CountDownTimer;
import com.gemserk.games.facehunt.EntityManager;
import com.google.inject.Inject;

public class SpawnerEntityTemplate extends EntityBuilder {

	EntityManager entityManager;

	@Inject
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void build() {

		tags("spawner");

		property("spawner", parameters.get("spawner"));

		//

		property("timer", new CountDownTimer(1000, true));

	}

}