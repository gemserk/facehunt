package com.gemserk.games.facehunt.components;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Spatial;

public class RotateComponent {

	public void update(Entity entity, float delta) {
		if (!entity.hasTag(Tags.SPATIAL))
			return;
		Spatial spatial = Properties.getValue(entity, "spatial");
		spatial.angle += 90f * delta;
		Properties.setValue(entity, "spatial", spatial);
	}

}