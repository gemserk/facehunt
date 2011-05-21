package com.gemserk.games.facehunt.components;

import com.artemis.Component;

public class RandomMovementBehaviorComponent extends Component {
	
	private final int time;
	
	private final float impulse;

	private int currentTime;
	
	public int getTime() {
		return time;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
	
	public float getImpulse() {
		return impulse;
	}
	
	public RandomMovementBehaviorComponent(int time, float impulse) {
		this.time = time;
		this.currentTime = time;
		this.impulse = impulse;
	}

}
