package com.gemserk.games.facehunt.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.artemis.components.Spatial;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.commons.gdx.math.MathUtils2;
import com.gemserk.games.facehunt.components.OutsideAreaComponent;

public class OutsideAreaTriggerSystem extends EntityProcessingSystem {

	@SuppressWarnings("unchecked")
	public OutsideAreaTriggerSystem() {
		super(OutsideAreaComponent.class, SpatialComponent.class);
	}
	
	@Override
	protected void process(Entity e) {
		
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		OutsideAreaComponent outisdeAreaComponent = e.getComponent(OutsideAreaComponent.class);
		
		Spatial spatial = spatialComponent.getSpatial();
//		Vector2 position = spatialComponent.getPosition();
		Rectangle area = outisdeAreaComponent.getArea();
		
		if (MathUtils2.inside(area, spatial.getX(), spatial.getY()))
			return;
		
		Trigger trigger = outisdeAreaComponent.getTrigger();
		if (trigger.isAlreadyTriggered())
			return;
		
		trigger.trigger(e);
		
	}

}
