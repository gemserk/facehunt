package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class MoveableEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		tags(Tags.MOVEABLE);
		
		property("movement", parameters.get("movement"));
		
		// binding for component/system
		
		propertyRef("movement.spatial", "spatial");
		propertyRef("movement.movement", "movement");
	}

}