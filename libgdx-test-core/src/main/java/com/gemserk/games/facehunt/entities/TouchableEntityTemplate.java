package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class TouchableEntityTemplate extends EntityBuilder {

	@Override
	public void build() {

		tags(Tags.TOUCHABLE);
		
		property("radius", parameters.get("radius"));

		propertyRef("touchable.radius", "radius");
		propertyRef("touchable.spatial", "spatial");

	}

}