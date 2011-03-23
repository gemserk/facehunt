package com.gemserk.libgdx.test;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;

public class FadeAnimationTemplate extends EntityBuilder {
	
	@Override
	public void build() {

		tags("animation");
		
		Color startColor = parameters.get("startColor");
		Color endColor = parameters.get("endColor");
		
		Property<Color> colorProperty = new TransitionProperty<Color>(Transitions.transition(startColor, 0.002f, LibgdxConverters.color()));
		
		property("position", parameters.get("position"));
		property("angle", parameters.get("angle"));
		
		property("image", parameters.get("image"));
		property("color", colorProperty);
		
		property("endColor", endColor);
		
		colorProperty.set(endColor);
		
		// the entity to be added
		property("entity", parameters.get("entity"));

	}

}