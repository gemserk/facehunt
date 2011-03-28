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

		property("touchable", true);

		FloatValue aliveTime = parameters.get("aliveTime");
		Transition<FloatValue> transition = Transitions.transition(new FloatValue(1f), typeConverter);
		property("aliveTime", new TransitionProperty<FloatValue>(transition));
		transition.set(new FloatValue(0f), (int) (aliveTime.value));
		

	}

}