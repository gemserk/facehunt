package com.gemserk.games.facehunt.entities;

import java.util.Random;

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

public class FaceEntityTemplate extends EntityBuilder {

	static class ColorFromAliveTimeProperty extends FixedProperty {
		Color tmpColor = new Color(1f, 1f, 1f, 1f);

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

	private static Random random = new Random();

	@Override
	public void build() {
		
		tags(Tags.ALIVE);

		parameters.put("radius", new FloatValue(36f));
		parameters.put("rotationSpeed", new FloatValue(random.nextFloat() * 180f - 90f));

		parent("entities.Spatial", parameters);
		parent("entities.Moveable", parameters);
		parent("entities.Touchable", parameters);
		parent("entities.Rotationable", parameters);

		property("image", parameters.get("image"));
		// color now depends on aliveTime
		property("color", new ColorFromAliveTimeProperty(entity));
		
//		parent("entities.Alive", parameters);

		FloatValue aliveTime = parameters.get("aliveTime");
		Transition<FloatValue> transition = Transitions.transition(new FloatValue(1f), typeConverter);
		property("aliveTime", new TransitionProperty<FloatValue>(transition));
		transition.set(new FloatValue(0f), (int) (aliveTime.value));

	}

}