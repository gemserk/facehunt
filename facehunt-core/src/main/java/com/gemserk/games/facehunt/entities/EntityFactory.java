package com.gemserk.games.facehunt.entities;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.Spatial;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpatialPhysicsImpl;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.triggers.AbstractTrigger;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.games.facehunt.Groups;

public class EntityFactory {

	private final World world;
	
	private final BodyBuilder bodyBuilder;

	public EntityFactory(World world, BodyBuilder bodyBuilder) {
		this.world = world;
		this.bodyBuilder = bodyBuilder;
	}

	public void deadFacePartTemplate(Entity entity, Sprite sprite, Spatial spatial, int aliveTime, Color color) {
		entity.setGroup(Groups.FaceGroup);

		Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color faceColor = new Color();

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(color).end(hideColor).time(aliveTime) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()));

		float radius = MathUtils.random(6f, 16f);

		Body body = bodyBuilder //
				.type(BodyType.DynamicBody) //
				.circleShape(radius) //
				.mass(1f)//
				.friction(0f)//
				.restitution(1f)//
				.userData(entity)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		Vector2 impulse = new Vector2(1f, 0f);
		impulse.rotate(MathUtils.random(0f, 360f));
		impulse.mul(MathUtils.random(200f, 500f));

		// body.applyLinearImpulse(impulse, body.getTransform().getPosition());
		body.setLinearVelocity(impulse);
		body.setAngularVelocity(MathUtils.random(-5f, 5f));

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, radius * 3, radius * 3)));
		entity.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), faceColor));
		entity.addComponent(new TimerComponent(aliveTime, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				world.deleteEntity(e);
				return true;
			}
		}));

		entity.refresh();
	}

}
