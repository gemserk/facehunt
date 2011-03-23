package com.gemserk.libgdx.test;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class IslandTemplate extends EntityBuilder {

	// should be a stateless class, reusable to build a lot of entities.
	// avoid using it directly, use it through JavaEntityTemplate.

	@Override
	public void build() {

		tags("island");
		
		property("position", parameters.get("position"));
		property("image", parameters.get("image"));
		
		property("color", Color.WHITE);

	}

}