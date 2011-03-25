package com.gemserk.games.facehunt.entities;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class TouchableEntityTemplate extends EntityBuilder {

	// should be a stateless class, reusable to build a lot of entities.
	// avoid using it directly, use it through JavaEntityTemplate.

	@Override
	public void build() {
		tags(Tags.TOUCHABLE, Tags.MOVEABLE, Tags.SPATIAL);

		property("spatial", parameters.get("spatial"));
		property("movement", parameters.get("movement"));
		
		property("image", parameters.get("image"));
		
		property("color", Color.WHITE);
		
		// bind for rotate component
		propertyRef("rotate.spatial", "spatial");
		
		// bind for movement component
		propertyRef("movement.spatial", "spatial");
		propertyRef("movement.movement", "movement");
		
	}

}