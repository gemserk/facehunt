package com.gemserk.games.facehunt.components;

import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Movement;
import com.gemserk.games.facehunt.values.Spatial;

public class RotateComponent extends FieldsReflectionComponent {
	
	@EntityProperty(required=true)
	Spatial spatial;

	@EntityProperty(required=true)
	Movement movement;

	public RotateComponent(String id) {
		super(id);
	}

	public void update(Entity entity, float delta) {
		if (!entity.hasTag(Tags.MOVEABLE))
			return;
		super.setEntity(entity);
		preHandleMessage(null);
		float speed = movement.angularVelocity;
		spatial.angle += speed * delta;
		postHandleMessage(null);
	}

}