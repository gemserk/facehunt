package com.gemserk.games.facehunt.components;

import com.artemis.Entity;
import com.gemserk.commons.artemis.Script;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.gdx.games.Spatial;

public class ComponentWrapper {
	
	public static Script getScript(Entity e) {
		ScriptComponent component = e.getComponent(ScriptComponent.class);
		if (component == null)
			return null;
		return component.getScript();
	}
	
	public static Spatial getSpatial(Entity e) {
		SpatialComponent component = e.getComponent(SpatialComponent.class);
		if (component == null)
			return null;
		return component.getSpatial();
	}

	public static TouchableComponent getTouchable(Entity e) {
		return e.getComponent(TouchableComponent.class);
	}
	
	public static PhysicsComponent getPhysics(Entity e) {
		return e.getComponent(PhysicsComponent.class);
	}
	
	public static HealthComponent getHealth(Entity e) {
		return e.getComponent(HealthComponent.class);
	}
	
	public static SpriteComponent getSprite(Entity e) {
		return e.getComponent(SpriteComponent.class);
	}
}
