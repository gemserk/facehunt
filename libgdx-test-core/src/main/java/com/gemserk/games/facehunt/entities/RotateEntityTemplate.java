package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class RotateEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		tags(Tags.ROTATION);
		propertyRef("rotate.spatial", "spatial");
	}

}