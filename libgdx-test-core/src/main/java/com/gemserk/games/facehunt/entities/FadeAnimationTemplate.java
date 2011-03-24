package com.gemserk.games.facehunt.entities;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.facehunt.TransitionProperty;

public class FadeAnimationTemplate extends EntityBuilder {
	
	@Override
	public void build() {

		tags("animation", "moveable");
		
		Color startColor = parameters.get("startColor");
		Color endColor = parameters.get("endColor");
		
		Property<Color> colorProperty = new TransitionProperty<Color>(Transitions.transition(startColor, 0.002f, LibgdxConverters.color()));
		
		property("position", parameters.get("position"));
		property("velocity", parameters.get("velocity"));
		property("angle", parameters.get("angle"));
		
		property("image", parameters.get("image"));
		property("color", colorProperty);
		
		property("endColor", endColor);
		
		colorProperty.set(endColor);
		
		// if a touchable should be spawned
		property("shouldSpawn", parameters.get("shouldSpawn", false));

	}

}