package com.gemserk.games.facehunt.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.timers.Timer;
import com.gemserk.games.facehunt.EntityManager;
import com.gemserk.games.facehunt.Vector2Random;
import com.gemserk.games.facehunt.World;
import com.gemserk.games.facehunt.values.Movement;
import com.gemserk.games.facehunt.values.Spatial;
import com.gemserk.games.facehunt.values.Spawner;

public class SpawnerComponent {

	EntityManager entityManager;
	
	World world;

	public SpawnerComponent(EntityManager entityManager, World world) {
		this.entityManager = entityManager;
		this.world = world;
	}

	public void update(Entity entity, float delta) {
		if (!entity.hasTag("spawner"))
			return;

		Timer timer = Properties.getValue(entity, "timer");

		if (!timer.update((int) (delta * 1000f)))
			return;

		Spawner spawner = Properties.getValue(entity, "spawner");
		
		// should be outside...
		final Vector2 position = Vector2Random.vector2(world.min, world.max);
		final Vector2 velocity = Vector2Random.vector2(-1f, -1f, 1f, 1f).mul(100f);
		final float angle = random.nextFloat() * 360;
		
		Map<String, Object> parameters = new HashMap<String, Object>(spawner.defaultParameters) {{
			put("spatial", new Spatial(position, angle));
			put("movement", new Movement(velocity));
		}};
		
		Entity newEntity = spawner.template.instantiate("entities." + getRandomInt(), parameters);

		entityManager.addEntity(newEntity);

		timer.reset();
	}

	Random random = new Random();

	protected int getRandomInt() {
		return random.nextInt();
	}

}