package com.gemserk.games.facehunt.components;

import com.artemis.Component;

public class DamageComponent extends Component {

	private float damagePerSecond;

	public float getDamagePerSecond() {
		return damagePerSecond;
	}
	
	public void setDamagePerSecond(float damagePerSecond) {
		this.damagePerSecond = damagePerSecond;
	}
	
	public DamageComponent(float damagePerSecond) {
		this.damagePerSecond = damagePerSecond;
	}

}
