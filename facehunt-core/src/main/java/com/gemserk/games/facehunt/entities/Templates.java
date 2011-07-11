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
import com.gemserk.commons.artemis.Script;
import com.gemserk.commons.artemis.ScriptJavaImpl;
import com.gemserk.commons.artemis.components.HitComponent;
import com.gemserk.commons.artemis.components.LinearVelocityLimitComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.triggers.AbstractTrigger;
import com.gemserk.commons.artemis.triggers.NullTrigger;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.facehunt.Groups;
import com.gemserk.games.facehunt.components.BounceSmallVelocityFixComponent;
import com.gemserk.games.facehunt.components.ComponentWrapper;
import com.gemserk.games.facehunt.components.DamageComponent;
import com.gemserk.games.facehunt.components.HealthComponent;
import com.gemserk.games.facehunt.components.PointsComponent;
import com.gemserk.games.facehunt.components.RandomMovementBehaviorComponent;
import com.gemserk.games.facehunt.components.TouchableComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;
import com.gemserk.resources.ResourceManager;

public class Templates {

	private final World world;

	private final BodyBuilder bodyBuilder;

	private final ResourceManager<String> resourceManager;

	public Templates(World world, BodyBuilder bodyBuilder, ResourceManager<String> resourceManager) {
		this.world = world;
		this.bodyBuilder = bodyBuilder;
		this.resourceManager = resourceManager;
	}

	public void createFaceFirstType(Spatial spatial, Sprite sprite, FaceHuntController controller, Vector2 linearImpulse, float angularVelocity, Color color, Trigger hitTrigger, final Trigger deadFaceTrigger) {
		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);
		final Color faceColor = new Color(color);
		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500));
		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, faceColor, 6f, 15f, 100);
		collidableTemplate(entity, hitTrigger);
		touchableTemplate(entity, controller, spatial.getWidth() * 0.15f, new NullTrigger());

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
			@Override
			public void update(World world, Entity e) {
				HealthComponent healthComponent = ComponentWrapper.getHealth(e);
				Spatial spatial = ComponentWrapper.getSpatial(e);
				SpriteComponent spriteComponent = ComponentWrapper.getSprite(e);
				TouchableComponent touchableComponent = ComponentWrapper.getTouchable(e);

				if (touchableComponent.isTouched()) {
					Container health = healthComponent.getHealth();
					float damagePerMs = 10f / 1000f; // 10 damage per second
					float damage = damagePerMs * (float) world.getDelta() * (1f - healthComponent.getResistance());
					health.remove(damage);
				}

				if (healthComponent.getHealth().isEmpty()) {
					deadFaceTrigger.trigger(e);

					createDeadFace(spatial, 6, 1500, spriteComponent.getColor());
					world.deleteEntity(e);
				}

			}
		}));

		entity.refresh();
	}

	public void createFaceSecondType(Spatial spatial, Sprite sprite, FaceHuntController controller, Vector2 linearImpulse, float angularVelocity, Trigger hitTrigger, final Trigger deadFaceTrigger) {
		Color color = new Color(0f, 1f, 0f, 1f);
		final Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color showColor = new Color(color.r, color.g, color.b, 1f);
		final Color faceColor = new Color(color);
		Synchronizers.transition(faceColor, Transitions.transitionBuilder(hideColor).end(showColor).time(500));
		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, faceColor, 3f, 7f, 250);
		collidableTemplate(entity, hitTrigger);
		touchableTemplate(entity, controller, spatial.getWidth() * 0.3f, new NullTrigger());
		entity.addComponent(new RandomMovementBehaviorComponent(750, 10f));

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
			@Override
			public void update(World world, Entity e) {
				HealthComponent healthComponent = ComponentWrapper.getHealth(e);
				Spatial spatial = ComponentWrapper.getSpatial(e);
				SpriteComponent spriteComponent = ComponentWrapper.getSprite(e);
				TouchableComponent touchableComponent = ComponentWrapper.getTouchable(e);

				if (touchableComponent.isTouched()) {
					Container health = healthComponent.getHealth();
					float damagePerMs = 10f / 1000f; // 10 damage per second
					float damage = damagePerMs * (float) world.getDelta() * (1f - healthComponent.getResistance());
					health.remove(damage);
				}

				if (healthComponent.getHealth().isEmpty()) {
					deadFaceTrigger.trigger(e);

					createDeadFace(spatial, 6, 1500, spriteComponent.getColor());
					world.deleteEntity(e);
				}

			}
		}));

		entity.refresh();
	}

	public void createFaceInvulnerableType(Spatial spatial, Sprite sprite, FaceHuntController controller, Vector2 linearImpulse, float angularVelocity, Trigger hitTrigger, final Trigger deadFaceTrigger) {
		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, new Color(1f, 0f, 0f, 0f), 2.5f, 13f, 150);
		collidableTemplate(entity, hitTrigger);
		touchableTemplate(entity, controller, spatial.getWidth() * 0.15f, new NullTrigger());
		invulnerableFaceTemplate(entity, new Color(1f, 1f, 0f, 1f), new Color(1f, 0f, 0f, 1f), 1000, 3000);

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
			@Override
			public void update(World world, Entity e) {
				HealthComponent healthComponent = ComponentWrapper.getHealth(e);
				Spatial spatial = ComponentWrapper.getSpatial(e);
				SpriteComponent spriteComponent = ComponentWrapper.getSprite(e);
				TouchableComponent touchableComponent = ComponentWrapper.getTouchable(e);

				if (touchableComponent.isTouched()) {
					Container health = healthComponent.getHealth();
					float damagePerMs = 10f / 1000f; // 10 damage per second
					float damage = damagePerMs * (float) world.getDelta() * (1f - healthComponent.getResistance());
					health.remove(damage);
				}

				if (healthComponent.getHealth().isEmpty()) {
					deadFaceTrigger.trigger(e);

					createDeadFace(spatial, 6, 1500, spriteComponent.getColor());
					world.deleteEntity(e);
				}

			}
		}));

		entity.refresh();
	}

	public void createMedicFaceType(Spatial spatial, Sprite sprite, FaceHuntController controller, Vector2 linearImpulse, float angularVelocity, Trigger hitTrigger, final Trigger deadFaceTrigger) {
		Entity entity = world.createEntity();
		simpleFaceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, new Color(1f, 1f, 1f, 1f), -6f, 13f, 0);
		collidableTemplate(entity, hitTrigger);
		touchableTemplate(entity, controller, spatial.getWidth() * 0f, new NullTrigger());

		final int aliveTime = 4000;

		SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
		Color color = spriteComponent.getColor();
		color.a = 1f;
		Color vulnerableColor = new Color(color.r, color.g, color.b, 0f);
		Synchronizers.transition(color, Transitions.transitionBuilder(color).end(vulnerableColor).time(4000) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()));

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {

			int time = aliveTime;

			@Override
			public void update(World world, Entity e) {
				HealthComponent healthComponent = ComponentWrapper.getHealth(e);
				Spatial spatial = ComponentWrapper.getSpatial(e);
				SpriteComponent spriteComponent = ComponentWrapper.getSprite(e);
				TouchableComponent touchableComponent = ComponentWrapper.getTouchable(e);

				time -= world.getDelta();

				if (time <= 0) {
					world.deleteEntity(e);
					return;
				}

				if (touchableComponent.isTouched()) {
					Container health = healthComponent.getHealth();
					float damagePerMs = 10f / 1000f; // 10 damage per second
					float damage = damagePerMs * (float) world.getDelta() * (1f - healthComponent.getResistance());
					health.remove(damage);
				}

				if (healthComponent.getHealth().isEmpty()) {
					deadFaceTrigger.trigger(e);

					createDeadFace(spatial, 6, 1500, spriteComponent.getColor());
					world.deleteEntity(e);
				}

			}
		}));

		entity.refresh();
	}

	void simpleFaceTemplate(Entity entity, Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, Color color, float damagePerSecond, float maxSpeedLimit, int points) {
		faceTemplate(entity, spatial, sprite, linearImpulse, angularVelocity, new Container(0.1f, 0.1f), 0f, color, damagePerSecond, maxSpeedLimit, points);
	}

	public void createStaticSprite(Sprite sprite, float x, float y, float width, float height, float angle, int layer, float centerx, float centery, Color color) {
		Entity entity = world.createEntity();
		staticSpriteTemplate(entity, sprite, x, y, width, height, angle, layer, centerx, centery, color);
		entity.refresh();
	}

	void staticSpriteTemplate(Entity entity, Sprite sprite, float x, float y, float width, float height, float angle, int layer, float centerx, float centery, Color color) {
		entity.addComponent(new SpatialComponent(new SpatialImpl(x, y, width, height, angle)));
		entity.addComponent(new SpriteComponent(sprite, layer, new Vector2(centerx, centery), new Color(color)));
	}

	public void createBorder(float x, float y, float w, float h) {
		Entity entity = world.createEntity();
		staticBoxTemplate(entity, x, y, w, h);
		entity.refresh();
	}

	void staticBoxTemplate(Entity entity, float x, float y, float w, float h) {
		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(w * 0.5f, h * 0.5f) //
						.restitution(1f) //
						.friction(0f) //
						.categoryBits(Collisions.Border) //
						.maskBits(Collisions.All)) //
				.type(BodyType.StaticBody) //
				.mass(1f)//
				.userData(entity) //
				.position(x, y) //
				.build();
		entity.addComponent(new PhysicsComponent(body));
	}

	public void spawnerTemplate(Entity entity, int time, Trigger onSpawnTrigger) {
		entity.addComponent(new TimerComponent(time, onSpawnTrigger));
		entity.refresh();
	}

	public void touchableTemplate(Entity e, FaceHuntController controller, float touchTreshold, Trigger trigger) {
		e.addComponent(new TouchableComponent(controller, touchTreshold, trigger));
	}

	public void faceTemplate(Entity e, Spatial spatial, Sprite sprite, Vector2 linearImpulse, float angularVelocity, Container health, //
			float resistance, Color color, float damagePerSecond, float maxSpeedLimit, int points) {
		e.setGroup(Groups.FaceGroup);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(spatial.getWidth() * 0.5f) //
						.friction(0.5f)//
						.restitution(1f)//
						.categoryBits(Collisions.Face) //
						.maskBits(Collisions.All)) //
				.type(BodyType.DynamicBody) //
				.mass(1f)//
				.userData(e)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		body.applyLinearImpulse(linearImpulse, body.getTransform().getPosition());
		body.setAngularVelocity(angularVelocity * MathUtils.degreesToRadians);

		e.addComponent(new PhysicsComponent(body));
		e.addComponent(new LinearVelocityLimitComponent(maxSpeedLimit));
		e.addComponent(new BounceSmallVelocityFixComponent());
		e.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		e.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), color));
		e.addComponent(new PointsComponent(points));
		e.addComponent(new HealthComponent(health, resistance));
		e.addComponent(new DamageComponent(damagePerSecond));
	}

	public void collidableTemplate(Entity e, Trigger hitTrigger) {
		e.addComponent(new HitComponent(hitTrigger));
	}

	public void invulnerableFaceTemplate(Entity entity, final Color vulnerableColor, final Color invulnerableColor, final int minTime, final int maxTime) {
		entity.addComponent(new TimerComponent(0, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				TimerComponent timerComponent = e.getComponent(TimerComponent.class);
				timerComponent.setCurrentTime(MathUtils.random(minTime, maxTime));

				HealthComponent healthComponent = e.getComponent(HealthComponent.class);
				DamageComponent damageComponent = e.getComponent(DamageComponent.class);
				SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);

				if (healthComponent.getResistance() > 0f) {
					healthComponent.setResistance(0f);
					damageComponent.setDamagePerSecond(5f);
					Color color = spriteComponent.getColor();
					Synchronizers.transition(color, Transitions.transitionBuilder(color).end(vulnerableColor).time(250));
				} else {
					healthComponent.setResistance(1f);
					damageComponent.setDamagePerSecond(0);
					Color color = spriteComponent.getColor();
					Synchronizers.transition(color, Transitions.transitionBuilder(color).end(invulnerableColor).time(250));
				}

				return false;
			}
		}));
	}

	public void facePartTemplate(Entity e, Sprite sprite, Spatial spatial, int aliveTime, Color color, float angle) {
		e.setGroup(Groups.FaceGroup);

		Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color faceColor = new Color(color.r, color.g, color.b, 1f);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(faceColor).end(hideColor).time(aliveTime) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()));

		float radius = MathUtils.random(spatial.getWidth() * 0.1f, spatial.getWidth() * 0.2f);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder().circleShape(radius) //
						.friction(0.5f)//
						.restitution(0f)//
						.categoryBits(Collisions.FacePart) //
						.maskBits((short) (Collisions.All & ~Collisions.Face & ~Collisions.FacePart))) //
				.type(BodyType.DynamicBody) //
				.mass(0.2f)//
				.userData(e)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		Vector2 impulse = new Vector2(1f, 0f);
		impulse.rotate(angle);
		impulse.mul(MathUtils.random(1f, 1.5f));

		body.applyLinearImpulse(impulse, body.getTransform().getPosition());
		body.setAngularVelocity(MathUtils.random(-5f, 5f));

		e.addComponent(new PhysicsComponent(body));
		e.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial.getWidth() * 0.6f, spatial.getHeight() * 0.6f)));
		e.addComponent(new SpriteComponent(sprite, 0, new Vector2(0.5f, 0.5f), faceColor));
		e.addComponent(new TimerComponent(aliveTime, new AbstractTrigger() {
			@Override
			protected boolean handle(Entity e) {
				world.deleteEntity(e);
				return true;
			}
		}));
	}

	// /

	private String[] partsIds = new String[] { "Part01", "Part02", "Part03", "Part04", "Part05" };

	public Sprite getRandomFacePart() {
		int partIndex = MathUtils.random(partsIds.length - 1);
		return resourceManager.getResourceValue(partsIds[partIndex]);
	}

	public void createDeadFace(Spatial spatial, int count, final int aliveTime, Color color) {
		float angle = MathUtils.random(0f, 360f);
		float angleIncrement = 360f / count;
		for (int i = 0; i < count; i++) {
			Entity e = world.createEntity();
			facePartTemplate(e, getRandomFacePart(), spatial, aliveTime, color, angle);
			e.refresh();
			angle += angleIncrement;
		}
	}

	public void bulletFacePartTemplate(Entity e, Sprite sprite, Spatial spatial, int aliveTime, Color color, float angle) {
		e.setGroup(Groups.FaceGroup);

		Color hideColor = new Color(color.r, color.g, color.b, 0f);
		final Color faceColor = new Color(color.r, color.g, color.b, 1f);

		Synchronizers.transition(faceColor, Transitions.transitionBuilder(faceColor).end(hideColor).time(aliveTime) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()));

		float radius = MathUtils.random(spatial.getWidth() * 0.1f, spatial.getWidth() * 0.2f);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder().circleShape(radius) //
						.friction(0.5f)//
						.restitution(0f)//
						.categoryBits(Collisions.FacePart) //
						.maskBits((short) (Collisions.All & ~Collisions.FacePart))) //
				.type(BodyType.DynamicBody) //
				.mass(0.2f)//
				.userData(e)//
				.bullet() //
				.position(spatial.getX(), spatial.getY())//
				.build();

		Vector2 impulse = new Vector2(1f, 0f);
		impulse.rotate(angle);
		impulse.mul(MathUtils.random(1f, 1.5f));

		body.applyLinearImpulse(impulse, body.getTransform().getPosition());
		body.setAngularVelocity(MathUtils.random(-5f, 5f));

		e.addComponent(new PhysicsComponent(body));
		e.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial.getWidth() * 0.6f, spatial.getHeight() * 0.6f)));
		e.addComponent(new SpriteComponent(sprite, 0, new Vector2(0.5f, 0.5f), faceColor));
	}

	public void createBulletFaceParts(Spatial spatial, int count, final int aliveTime, Color color, Script script) {
		float angle = MathUtils.random(0f, 360f);
		float angleIncrement = 360f / count;
		for (int i = 0; i < count; i++) {
			Entity e = world.createEntity();
			bulletFacePartTemplate(e, getRandomFacePart(), spatial, aliveTime, color, angle);
			e.addComponent(new ScriptComponent(script));
			e.refresh();
			angle += angleIncrement;
		}
	}

	public void explosiveFaceTemplate(Entity e, Spatial spatial, Script script, FaceHuntController controller) {
		Sprite sprite = resourceManager.getResourceValue("HappyFaceSprite");

		e.setGroup(Groups.FaceGroup);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder().circleShape(spatial.getWidth() * 0.5f) //
						.friction(0.5f)//
						.restitution(1f)//
						.categoryBits(Collisions.Face) //
						.maskBits(Collisions.All)) //
				.type(BodyType.DynamicBody) //
				.mass(1f)//
				.userData(e)//
				.position(spatial.getX(), spatial.getY())//
				.build();

		e.addComponent(new PhysicsComponent(body));
		e.addComponent(new LinearVelocityLimitComponent(10f));
		e.addComponent(new BounceSmallVelocityFixComponent());
		e.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		e.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), Color.BLUE));
		e.addComponent(new PointsComponent(0));
		e.addComponent(new HealthComponent(new Container(0.1f, 0.1f), 0f));
		e.addComponent(new DamageComponent(0f));
		e.addComponent(new TouchableComponent(controller, spatial.getWidth() * 0f));
		e.addComponent(new ScriptComponent(script));

		e.refresh();
	}

}
