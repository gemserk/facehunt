package com.gemserk.games.facehunt.components;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.facehunt.World;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Movement;
import com.gemserk.games.facehunt.values.Spatial;

public class MovementComponent {

	private Movement tmpMovement = new Movement();

	private Spatial tmpSpatial = new Spatial();

	World world;

	public MovementComponent(World world) {
		this.world = world;
	}

	public void update(Entity entity, float delta) {
		// if entity has tag "movable" or properties for movement (spatial) , then perform

		if (!entity.hasTag(Tags.MOVEABLE))
			return;

		// Vector2 position = Properties.getValue(entity, "position");
		// Vector2 velocity = Properties.getValue(entity, "velocity");

		Spatial spatial = Properties.getValue(entity, "spatial");
		Movement movement = Properties.getValue(entity, "movement");

		Vector2 position = spatial.position;
		Vector2 velocity = movement.velocity;

		Vector2 tmpPosition = tmpSpatial.position;
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

		Properties.setValue(entity, "spatial", spatial);
		Properties.setValue(entity, "movement", movement);
	}

}