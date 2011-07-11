package com.gemserk.games.facehunt.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.gdx.box2d.Contact;
import com.gemserk.commons.gdx.sounds.SoundPlayer;
import com.gemserk.games.facehunt.components.ComponentWrapper;
import com.gemserk.games.facehunt.components.HealthComponent;
import com.gemserk.games.facehunt.components.ScriptComponent;
import com.gemserk.games.facehunt.components.ScriptJavaImpl;
import com.gemserk.games.facehunt.components.TouchableComponent;
import com.gemserk.games.facehunt.entities.Templates;
import com.gemserk.resources.ResourceManager;

public class Scripts {

	public static class ExplosiveFaceScript extends ScriptJavaImpl {
		
		Templates templates;
		ResourceManager<String> resourceManager;
		SoundPlayer soundPlayer;
		
		public ExplosiveFaceScript(Templates templates, ResourceManager<String> resourceManager, SoundPlayer soundPlayer) {
			this.templates = templates;
			this.resourceManager = resourceManager;
			this.soundPlayer = soundPlayer;
		}
		
		@Override
		public void update(World world, Entity e) {
			TouchableComponent touchableComponent = ComponentWrapper.getTouchable(e);
			if (!touchableComponent.isTouched())
				return;

			SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
			SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);

			float angle = MathUtils.random(0f, 360f);
			float angleIncrement = 360f / 6;
			for (int i = 0; i < 6; i++) {
				Entity bullet = world.createEntity();
				templates.bulletFacePartTemplate(bullet, templates.getRandomFacePart(), spatialComponent.getSpatial(), 1500, spriteComponent.getColor(), angle);
				bullet.addComponent(new ScriptComponent(new ExplosiveFacePartScript()));

				bullet.refresh();
				angle += angleIncrement;
			}

			Sound sound = resourceManager.getResourceValue("CritterKilledSound");
			soundPlayer.play(sound);

			world.deleteEntity(e);
		}
	}

	public static class ExplosiveFacePartScript extends ScriptJavaImpl {
		int aliveTime = 1500;

		@Override
		public void update(World world, Entity e) {
			PhysicsComponent physics = ComponentWrapper.getPhysics(e);
			Contact contact = physics.getContact();

			aliveTime -= world.getDelta();

			if (aliveTime <= 0) {
				world.deleteEntity(e);
				return;
			}

			for (int i = 0; i < contact.getContactCount(); i++) {
				if (!contact.isInContact(i))
					continue;
				Entity contactEntity = (Entity) contact.getUserData(i);
				if (contactEntity == null)
					continue;
				HealthComponent health = ComponentWrapper.getHealth(contactEntity);
				if (health == null)
					continue;
				health.getHealth().remove(1000f);

				world.deleteEntity(e);
			}
		}
	}
}
