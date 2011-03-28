package com.gemserk.games.facehunt.components;

import com.gemserk.commons.values.FloatValue;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Spatial;

public class RotateComponent extends FieldsReflectionComponent {
	
	@EntityProperty(required=true)
	Spatial spatial;

	@EntityProperty(required=true, readOnly=true)
	FloatValue speed;

	public RotateComponent(String id) {
		super(id);
	}

	public void update(Entity entity, float delta) {
		if (!entity.hasTag(Tags.ROTATION))
			return;
		super.setEntity(entity);
		preHandleMessage(null);
		spatial.angle += speed.value * delta;
		postHandleMessage(null);
	}

}