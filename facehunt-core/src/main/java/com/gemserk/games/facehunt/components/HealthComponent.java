package com.gemserk.games.facehunt.components;

import com.artemis.Component;
import com.gemserk.componentsengine.utils.Container;

public class HealthComponent extends Component {

	private final Container health;
	
	private float resistance;
	
	public Container getHealth() {
		return health;
	}
	
	public float getResistance() {
		return resistance;
	}
	
	public void setResistance(float resistance) {
		this.resistance = resistance;
	}

	public HealthComponent(Container health, float resistance) {
		this.health = health;
		this.resistance = resistance;
	}

}
