package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class RenderableEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		// requires spatial property.
		property("image", parameters.get("image"));
		
		propertyRef("color", "color");
		propertyRef("spatial", "spatial");
	}

}