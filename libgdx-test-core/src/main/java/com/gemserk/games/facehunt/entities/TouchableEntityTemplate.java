package com.gemserk.games.facehunt.entities;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class TouchableEntityTemplate extends EntityBuilder {

	// should be a stateless class, reusable to build a lot of entities.
	// avoid using it directly, use it through JavaEntityTemplate.

	@Override
	public void build() {
		
		tags("touchable", Tags.MOVEABLE);
		
		property("position", parameters.get("position"));
		property("velocity", parameters.get("velocity"));
		
		property("angle", parameters.get("angle"));
		
		property("image", parameters.get("image"));
		
		property("color", Color.WHITE);
	}

}