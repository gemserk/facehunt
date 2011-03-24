package com.gemserk.games.facehunt.components;

import com.gemserk.commons.values.FloatValue;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.facehunt.entities.Tags;

public class RotateComponent {

	public void update(Entity entity, float delta) {
		// if entity has tag "movable" or properties for movement (spatial) , then perform
		
		if (!entity.hasTag(Tags.MOVEABLE))
			return;
		
		FloatValue angle = Properties.getValue(entity, "angle");
		angle.value += 90f * delta;
		Properties.setValue(entity, "angle", angle);
		
	}

}