package com.gemserk.games.facehunt.entities;

import com.gemserk.commons.values.FloatValue;
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

		tags(Tags.SPAWNER);

		property("spawner", parameters.get("spawner"));

		//

		// should be a dynamic based on player performance? or on a function of time...
		FloatValue respawnTime = parameters.get("respawnTime");
		CountDownTimer timer = new CountDownTimer((int)respawnTime.value, true);
		property("timer", timer);
		
		// 
		
		propertyRef("spawner.spawner", "spawner");
	}

}