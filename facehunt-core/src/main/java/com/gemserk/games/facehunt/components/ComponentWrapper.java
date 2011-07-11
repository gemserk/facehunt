package com.gemserk.games.facehunt.components;

import com.artemis.Entity;

public class ComponentWrapper {
	
	public static Script getScript(Entity e) {
		ScriptComponent component = e.getComponent(ScriptComponent.class);
		if (component == null)
			return null;
		return component.getScript();
	}

	public static TouchableComponent getTouchable(Entity e) {
		TouchableComponent component = e.getComponent(TouchableComponent.class);
		return component;
	}
}
