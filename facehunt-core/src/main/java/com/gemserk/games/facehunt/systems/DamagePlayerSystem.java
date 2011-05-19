package com.gemserk.games.facehunt.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.facehunt.components.DamageComponent;
import com.gemserk.games.facehunt.components.HealthComponent;

public class DamagePlayerSystem extends EntityProcessingSystem {

	public DamagePlayerSystem() {
		super(DamageComponent.class);
	}

	@Override
	protected void process(Entity e) {
		DamageComponent damageComponent = e.getComponent(DamageComponent.class);
		Entity player = world.getTagManager().getEntity("Player");

		HealthComponent healthComponent = player.getComponent(HealthComponent.class);
		Container health = healthComponent.getHealth();
		
		float damage = damageComponent.getDamagePerSecond() * world.getDelta() * 0.001f;
		health.remove(damage * (1 - healthComponent.getResistance()));
	}

}
