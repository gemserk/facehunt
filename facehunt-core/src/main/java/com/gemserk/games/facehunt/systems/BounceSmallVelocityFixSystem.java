package com.gemserk.games.facehunt.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.Contact;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.games.facehunt.components.BounceSmallVelocityFixComponent;

public class BounceSmallVelocityFixSystem extends EntityProcessingSystem {

	public BounceSmallVelocityFixSystem() {
		super(BounceSmallVelocityFixComponent.class);
	}

	@Override
	protected void process(Entity e) {
		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();

		Contact contact = physicsComponent.getContact();
		if (!contact.isInContact())
			return;

		for (int i = 0; i < contact.getContactCount(); i++) {

			if (!contact.isInContact(i))
				continue;

			// Body contactBody = contact.getBody(i);
			// if (contactBody.getType() == BodyType.DynamicBody)
			// continue;

			body.applyForce(contact.getNormal(i), body.getTransform().getPosition());
		}
	}

}
