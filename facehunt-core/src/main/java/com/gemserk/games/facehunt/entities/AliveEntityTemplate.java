package com.gemserk.games.facehunt.entities;

import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.commons.values.FloatValue;
import com.gemserk.commons.values.FloatValueConverter;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.games.facehunt.TransitionProperty;

public class AliveEntityTemplate extends EntityBuilder {

	private static FloatValueConverter typeConverter = new FloatValueConverter();

	@Override
	public void build() {

		tags(Tags.ALIVE);

		property("touchable", false);

		// FloatValue aliveTime = parameters.get("aliveTime");

		Transition<FloatValue> aliveTransition = Transitions.transition(new FloatValue(0f), typeConverter);
		
		// used from outside
		property("aliveTime", new TransitionProperty<FloatValue>(aliveTransition));
		
		property("aliveTransition", aliveTransition);
		property("totalAliveTime", parameters.get("aliveTime"));
		
		propertyRef("alive.touchable", "touchable");
		propertyRef("alive.aliveTransition", "aliveTransition");
		propertyRef("alive.totalAliveTime", "totalAliveTime");

		// Transition<FloatValue> transition = Transitions.transition(new FloatValue(1f), typeConverter);
		// property("aliveTime", new TransitionProperty<FloatValue>(transition));
		// transition.set(new FloatValue(0f), (int) (aliveTime.value));

	}

}