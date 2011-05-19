package com.gemserk.games.facehunt.components;

import com.artemis.Component;

public class DamageComponent extends Component {

	private final float damagePerSecond;

	public float getDamagePerSecond() {
		return damagePerSecond;
	}
	
	public DamageComponent(float damagePerSecond) {
		this.damagePerSecond = damagePerSecond;
	}

}
