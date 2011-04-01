package com.gemserk.games.facehunt.components;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.facehunt.World;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Movement;
import com.gemserk.games.facehunt.values.Spatial;

public class MovementComponent extends FieldsReflectionComponent {

	private Movement tmpMovement = new Movement();

	private Spatial tmpSpatial = new Spatial();

	@EntityProperty
	Spatial spatial;

	@EntityProperty
	Movement movement;

	// inject?

	private final World world;

	private final Sound bounceSound;

	public MovementComponent(String id, World world, Sound bounceSound) {
		super(id);
		this.world = world;
		this.bounceSound = bounceSound;
	}

	public void update(Entity entity, float delta) {
		// if entity has tag "movable" or properties for movement (spatial) , then perform

		if (!entity.hasTag(Tags.MOVEABLE))
			return;

		super.setEntity(entity);
		super.preHandleMessage(null);
		
//		tmpSpatial.set(spatial);
//		tmpMovement.set(movement);

		Vector2 position = spatial.position;
		Vector2 velocity = movement.velocity;
		
		Vector2 tmpPosition = tmpSpatial.position;
		Vector2 tmpVelocity = tmpMovement.velocity;

		tmpPosition.set(position);
		tmpVelocity.set(velocity);
		
		tmpPosition.add(tmpVelocity.mul(delta));

		tmpVelocity.set(velocity);
		
		boolean bounce = false;

		// world size!!
		if (tmpPosition.x > world.max.x - 10) {
			tmpVelocity.x = -tmpVelocity.x;
			tmpPosition.x = position.x;
			
			bounce = true;
		}

		if (tmpPosition.x < world.min.x + 10) {
			tmpVelocity.x = -tmpVelocity.x;
			tmpPosition.x = position.x;
			
			bounce = true;
		}

		if (tmpPosition.y > world.max.y - 10) {
			tmpVelocity.y = -tmpVelocity.y;
			tmpPosition.y = position.y;
			
			bounce = true;
		}

		if (tmpPosition.y < world.min.y + 10) {
			tmpVelocity.y = -tmpVelocity.y;
			tmpPosition.y = position.y;
			
			bounce = true;
		}
		
		if (bounce) 
			bounceSound.play();

		position.set(tmpPosition);
		velocity.set(tmpVelocity);
		
		// rotate
		float speed = movement.angularVelocity;
		spatial.angle += speed * delta;
		
		// resize
//		tmpSpatial.size.add(tmpMovement.sizeVelocity.mul(delta));
//		spatial.size.set(tmpSpatial.size);
		
		super.postHandleMessage(null);
	}

}