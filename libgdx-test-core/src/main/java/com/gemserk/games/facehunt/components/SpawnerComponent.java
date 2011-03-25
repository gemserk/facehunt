package com.gemserk.games.facehunt.components;

import java.util.Map;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.games.facehunt.EntityManager;
import com.gemserk.games.facehunt.World;
import com.gemserk.games.facehunt.values.Spawner;

public class SpawnerComponent extends Component {

	EntityManager entityManager;

	World world;

	public SpawnerComponent(String id, EntityManager entityManager, World world) {
		super(id);
		this.entityManager = entityManager;
		this.world = world;
	}

	public void update(Entity entity, float delta) {
		if (!entity.hasTag("spawner"))
			return;

		Spawner spawner = Properties.getValue(entity, getId() + ".spawner");

		int spawnLimit = spawner.limit;

		if (entityManager.getEntities().size() >= spawnLimit)
			return;

		Timer timer = Properties.getValue(entity, "timer");

		if (!timer.update((int) (delta * 1000f)))
			return;

		Map<String, Object> parameters;

		if (spawner.defaultParametersBuilder != null)
			parameters = spawner.defaultParametersBuilder.buildParameters(spawner.defaultParameters);
		else
			parameters = spawner.defaultParameters;

		Entity newEntity = spawner.template.instantiate(null, parameters);

		entityManager.addEntity(newEntity);

		timer.reset();
	}

}