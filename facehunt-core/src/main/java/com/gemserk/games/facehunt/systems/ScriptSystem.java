package com.gemserk.games.facehunt.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.games.facehunt.components.ComponentWrapper;
import com.gemserk.games.facehunt.components.Script;
import com.gemserk.games.facehunt.components.ScriptComponent;

public class ScriptSystem extends EntityProcessingSystem {
	
	public ScriptSystem() {
		super(ScriptComponent.class);
	}
	
	@Override
	protected void added(Entity e) {
		super.added(e);
		Script script = ComponentWrapper.getScript(e);
		script.init(world, e);
	}
	
	@Override
	protected void removed(Entity e) {
		Script script = ComponentWrapper.getScript(e);
		script.dispose(world, e);
		super.removed(e);
	}

	@Override
	protected void process(Entity e) {
		Script script = ComponentWrapper.getScript(e);
		script.update(world, e);
	}

}
