package com.gemserk.games.facehunt.components;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.facehunt.World;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Movement;

public class MovementComponent {

	Movement tmpMovement = new Movement();

	World world;

	public MovementComponent(World world) {
		this.world = world;
	}

	public void update(Entity entity, float delta) {
		// if entity has tag "movable" or properties for movement (spatial) , then perform
		
		if (!entity.hasTag(Tags.MOVEABLE))
			return;
		
		Vector2 position = Properties.getValue(entity, "position");
		Vector2 velocity = Properties.getValue(entity, "velocity");

		Vector2 tmpPosition = tmpMovement.position;
		Vector2 tmpVelocity = tmpMovement.velocity;
		
		tmpPosition.set(position);
		tmpVelocity.set(velocity);

		tmpPosition.add(tmpVelocity.mul(delta));

		tmpVelocity.set(velocity);

		// world size!!
		if (tmpPosition.x > world.max.x - 10) {
			tmpVelocity.x = -tmpVelocity.x;
			tmpPosition.x = position.x;
		}

		if (tmpPosition.x < world.min.x + 10) {
			tmpVelocity.x = -tmpVelocity.x;
			tmpPosition.x = position.x;
		}

		if (tmpPosition.y > world.max.y - 10) {
			tmpVelocity.y = -tmpVelocity.y;
			tmpPosition.y = position.y;
		}

		if (tmpPosition.y < world.min.y + 10) {
			tmpVelocity.y = -tmpVelocity.y;
			tmpPosition.y = position.y;
		}

		position.set(tmpPosition);
		velocity.set(tmpVelocity);

		Properties.setValue(entity, "position", position);
		Properties.setValue(entity, "velocity", velocity);
	}

}