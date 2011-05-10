package com.gemserk.games.facehunt.components;

import java.util.Map;

import com.badlogic.gdx.audio.Sound;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.games.facehunt.EntityManager;
import com.gemserk.games.facehunt.World;
import com.gemserk.games.facehunt.values.Spawner;
import com.gemserk.resources.ResourceManager;

public class SpawnerComponent extends Component {

	private final ResourceManager<String> resourceManager;
	
	private final EntityManager entityManager;

	World world;

	public SpawnerComponent(String id, EntityManager entityManager, World world, ResourceManager<String> resourceManager) {
		super(id);
		this.entityManager = entityManager;
		this.world = world;
		this.resourceManager = resourceManager;
	}

	public void update(Entity entity, float delta) {
		if (!entity.hasTag("spawner"))
			return;

		Spawner spawner = Properties.getValue(entity, getId() + ".spawner");

		int spawnLimit = spawner.limit;

		if (entityManager.getEntities().size() >= spawnLimit)
			return;

		Timer timer = Properties.getValue(entity, "timer");

		float spawnSpeed = spawner.spawnSpeed;
		float spawnSpeedFactor = spawner.spawnSpeedFactor; 
		
		if (!timer.update((int) (delta * 1000f * spawnSpeed)))
			return;

		Map<String, Object> parameters;

		if (spawner.defaultParametersBuilder != null)
			parameters = spawner.defaultParametersBuilder.buildParameters(spawner.defaultParameters);
		else
			parameters = spawner.defaultParameters;

		Entity newEntity = spawner.template.instantiate(null, parameters);
		
		Sound sound = resourceManager.getResourceValue("CritterSpawnedSound");
		
		if (sound != null)
			sound.play();

		entityManager.addEntity(newEntity);

		timer.reset();
		
		if (spawner.spawnSpeed >= spawner.spawnSpeedLimit) {
			spawner.spawnSpeed = spawner.spawnSpeedLimit;
			return;
		}
		
		// Gdx.app.log("FaceHunt", "incrementing spawn speed from " + spawnSpeed + " to " + spawnSpeed * spawnSpeedFactor);
		
		spawner.spawnSpeed *= spawnSpeedFactor;
		
	}

}