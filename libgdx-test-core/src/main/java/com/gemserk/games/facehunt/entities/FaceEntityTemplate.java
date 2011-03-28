package com.gemserk.games.facehunt.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class FaceEntityTemplate extends EntityBuilder {

	@Override
	public void build() {
		// some default parameters?
		
		parent("entities.Spatial");
		parent("entities.Moveable");
		parent("entities.Renderable");
	}

}