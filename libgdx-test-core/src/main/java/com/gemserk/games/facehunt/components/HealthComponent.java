package com.gemserk.games.facehunt.components;

import com.gemserk.commons.values.FloatValue;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.facehunt.entities.Tags;

public class HealthComponent extends FieldsReflectionComponent {
	
	@EntityProperty
	FloatValue health;
	
	@EntityProperty
	Boolean touchable;

	public HealthComponent(String id) {
		super(id);
	}

	public void update(Entity entity, float delta) {
		// if entity has tag "movable" or properties for movement (spatial) , then perform

		if (!entity.hasTag(Tags.TOUCHABLE))
			return;
		

	}

}