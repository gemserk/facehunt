package com.gemserk.games.facehunt;

import com.gemserk.componentsengine.templates.EntityBuilder;
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

		property("template", parameters.get("template"));
		property("defaultParameters", parameters.get("defaultParameters"));
		
		property("spawnedElements", parameters.get("template"));

	}

}