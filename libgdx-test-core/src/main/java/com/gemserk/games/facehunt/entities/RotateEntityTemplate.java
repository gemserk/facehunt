package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class RotateEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		tags(Tags.ROTATION);
		
		property("rotate.speed", parameters.get("rotationSpeed"));
		propertyRef("rotate.spatial", "spatial");
	}

}