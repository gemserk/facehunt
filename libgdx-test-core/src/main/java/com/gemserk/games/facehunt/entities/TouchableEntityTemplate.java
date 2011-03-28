package com.gemserk.games.facehunt.entities;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.commons.values.FloatValue;
import com.gemserk.commons.values.FloatValueConverter;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.PropertiesHolder;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.facehunt.TransitionProperty;

public class TouchableEntityTemplate extends EntityBuilder {

	static class ColorFromAliveTimeProperty extends FixedProperty {
		Color tmpColor = new Color(1f,1f,1f,1f);

		ColorFromAliveTimeProperty(PropertiesHolder propertiesHolder) {
			super(propertiesHolder);
		}

		@Override
		public Object get() {
			FloatValue aliveTime = Properties.getValue(getHolder(), "aliveTime");
			tmpColor.a = aliveTime.value;
			return tmpColor;
		}
	}

	private static FloatValueConverter typeConverter = new FloatValueConverter();

	// should be a stateless class, reusable to build a lot of entities.
	// avoid using it directly, use it through JavaEntityTemplate.

	@Override
	public void build() {
		
		parent("entities.Spatial", parameters);

		tags(Tags.TOUCHABLE, Tags.MOVEABLE);
		
		property("movement", parameters.get("movement"));

		property("image", parameters.get("image"));

		// bind for rotate component
		propertyRef("rotate.spatial", "spatial");

		// bind for movement component
		propertyRef("movement.spatial", "spatial");
		propertyRef("movement.movement", "movement");
		
		// color now depends on aliveTime
		property("color", new ColorFromAliveTimeProperty(entity));

		FloatValue aliveTime = parameters.get("aliveTime");
		Transition<FloatValue> transition = Transitions.transition(new FloatValue(1f), typeConverter);
		property("aliveTime", new TransitionProperty<FloatValue>(transition));
		transition.set(new FloatValue(0f), (int) (aliveTime.value));

	}

}