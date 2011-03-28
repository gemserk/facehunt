package com.gemserk.games.facehunt.entities;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.facehunt.TransitionProperty;

public class FadeAnimationTemplate extends EntityBuilder {
	
	private int sequence = 1;
	
	@Override
	public String getId() {
		return "face." + sequence;
	}
	
	@Override
	public void build() {
		
		sequence++;

		parent("entities.Spatial", parameters);
		
		tags("animation", Tags.MOVEABLE);
		
		Color startColor = parameters.get("startColor");
		Color endColor = parameters.get("endColor");
		
		Property<Color> colorProperty = new TransitionProperty<Color>(Transitions.transition(startColor, 0.002f, LibgdxConverters.color()));
		
		property("spatial", parameters.get("spatial"));
		property("movement", parameters.get("movement"));
		
		property("image", parameters.get("image"));
		property("color", colorProperty);
		
		property("endColor", endColor);
		
		colorProperty.set(endColor);
		
		// if a touchable should be spawned
		property("shouldSpawn", parameters.get("shouldSpawn", false));
		
		// bind for rotate component
		propertyRef("rotate.spatial", "spatial");
		
		// bind for movement component
		propertyRef("movement.spatial", "spatial");
		propertyRef("movement.movement", "movement");
		
		// alive time
		
		property("aliveTime", parameters.get("aliveTime"));

	}

}