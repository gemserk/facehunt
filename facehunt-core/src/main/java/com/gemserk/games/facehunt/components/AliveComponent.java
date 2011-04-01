package com.gemserk.games.facehunt.components;

import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.commons.values.FloatValue;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.facehunt.entities.Tags;

public class AliveComponent extends FieldsReflectionComponent {
	
	@EntityProperty
	Transition<FloatValue> aliveTransition;
	
	@EntityProperty
	FloatValue totalAliveTime;
	
	@EntityProperty
	Boolean touchable;
	
	public AliveComponent(String id) {
		super(id);
	}

	public void update(Entity entity, float delta) {
		// if entity has tag "movable" or properties for movement (spatial) , then perform

		if (!entity.hasTag(Tags.ALIVE))
			return;
		
		super.setEntity(entity);
		super.preHandleMessage(null);
		
		if (aliveTransition.isTransitioning())
			return;
		
		if (!touchable) {
			aliveTransition.set(new FloatValue(1f), (int) totalAliveTime.value);
			touchable = true;
		}
		
		// reduce alive time over time
		
		super.postHandleMessage(null);
	}

}