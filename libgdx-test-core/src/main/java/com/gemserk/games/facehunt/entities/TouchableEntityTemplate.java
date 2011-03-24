package com.gemserk.games.facehunt.entities;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class TouchableEntityTemplate extends EntityBuilder {

	// should be a stateless class, reusable to build a lot of entities.
	// avoid using it directly, use it through JavaEntityTemplate.

	@Override
	public void build() {
		
		tags("touchable", Tags.MOVEABLE, Tags.SPATIAL);

		property("position", parameters.get("position"));
		property("angle", parameters.get("angle"));
		
		property("velocity", parameters.get("velocity"));
		
		property("image", parameters.get("image"));
		
		property("color", Color.WHITE);
	}

}