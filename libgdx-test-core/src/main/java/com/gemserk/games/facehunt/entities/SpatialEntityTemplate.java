package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class SpatialEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		tags(Tags.SPATIAL);
		property("spatial", parameters.get("spatial"));
		
		// add components?
	}

}