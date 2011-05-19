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
import com.gemserk.commons.artemis.components.HitComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.Spatial;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpatialPhysicsImpl;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.triggers.AbstractTrigger;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.games.facehunt.Groups;
import com.gemserk.games.facehunt.components.BounceSmallVelocityFixComponent;
import com.gemserk.games.facehunt.components.FaceControllerComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;

public class EntityFactory {

	private final World world;

	private final BodyBuilder bodyBuilder;

	public EntityFactory(World world, BodyBuilder bodyBuilder) {
		this.world = world;
		this.bodyBuilder = bodyBuilder;
	}

	public void spawnerTemplate(Entity entity, int time, Trigger onSpawnTrigger) {
		entity.addComponent(new TimerComponent(time, onSpawnTrigger));
		entity.refresh();
	}
	
	public void touchableTemplate(Entity e, FaceHuntController controller, float touchTreshold, Trigger trigger) {
		e.addComponent(new FaceControllerComponent(controller, touchTreshold, trigger));
	}
	
	public void faceTemplate(Entity e, Spatial spatial, Sprite sprite, Vector2 linearVelocity, float angularVelocity, final int aliveTime, // 
			Color color, Trigger hitTrigger, Trigger timerTrigger) {
		e.setGroup(Groups.FaceGroup);

		Body body = bodyBuilder //
				.type(BodyType.DynamicBody) //
				.circleShape(spatial.getWidth() * 0.5f) //
				.mass(1f)//
				.friction(0f)//
				.restitution(1f)//
				.userData(e)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		body.applyLinearImpulse(linearVelocity, body.getTransform().getPosition());
		body.setAngularVelocity(angularVelocity * MathUtils.degreesToRadians);

		e.addComponent(new PhysicsComponent(body));
		e.addComponent(new BounceSmallVelocityFixComponent());
		e.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		e.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), color));
		e.addComponent(new PointsComponent(100));
		e.addComponent(new HitComponent(hitTrigger));
		e.addComponent(new TimerComponent(aliveTime, timerTrigger));
	}

	public void facePartTemplate(Entity e, Sprite sprite, Spatial spatial, int aliveTime, Color color) {
		e.setGroup(Groups.FaceGroup);

		Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color faceColor = new Color();

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(color).end(hideColor).time(aliveTime) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()));

		float radius = MathUtils.random(spatial.getWidth() * 0.1f, spatial.getWidth() * 0.2f);

		Body body = bodyBuilder //
				.type(BodyType.DynamicBody) //
				.circleShape(radius) //
				.mass(1f)//
				.bullet()//
				.friction(0f)//
				.restitution(1f)//
				.userData(e)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		Vector2 impulse = new Vector2(1f, 0f);
		impulse.rotate(MathUtils.random(0f, 360f));
		impulse.mul(MathUtils.random(1f, 3f));

		body.applyLinearImpulse(impulse, body.getTransform().getPosition());
		body.setAngularVelocity(MathUtils.random(-5f, 5f));

		e.addComponent(new PhysicsComponent(body));
		e.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial.getWidth() * 0.6f, spatial.getHeight() * 0.6f)));
		e.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), faceColor));
		e.addComponent(new TimerComponent(aliveTime, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				world.deleteEntity(e);
				return true;
			}
		}));
	}

}
