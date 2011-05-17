package com.gemserk.games.facehunt.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.games.facehunt.components.RandomMovementBehaviorComponent;

public class RandomMovementBehaviorSystem extends EntityProcessingSystem {

	private static final Vector2 randomDirectionImpulse = new Vector2();

	public RandomMovementBehaviorSystem() {
		super(RandomMovementBehaviorComponent.class);
	}

	@Override
	protected void process(Entity e) {
		RandomMovementBehaviorComponent randomMovementComponent = e.getComponent(RandomMovementBehaviorComponent.class);
		
		randomMovementComponent.setCurrentTime(randomMovementComponent.getCurrentTime() - world.getDelta());
		
		if (randomMovementComponent.getCurrentTime() > 0)
			return;
		
		randomDirectionImpulse.set(300f, 0f);
		randomDirectionImpulse.rotate(MathUtils.random(360f));
		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();
		body.applyLinearImpulse(randomDirectionImpulse, body.getTransform().getPosition());
		
		randomMovementComponent.setCurrentTime(randomMovementComponent.getTime());
	}

}
