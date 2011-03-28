package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class MoveableEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		property("movement", parameters.get("movement"));
		propertyRef("movement.spatial", "spatial");
		propertyRef("movement.movement", "movement");
	}

}