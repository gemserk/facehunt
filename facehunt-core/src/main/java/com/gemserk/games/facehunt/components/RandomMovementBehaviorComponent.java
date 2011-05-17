package com.gemserk.games.facehunt.components;

import com.artemis.Component;

public class RandomMovementBehaviorComponent extends Component {
	
	private final int time;
	
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
	
	public RandomMovementBehaviorComponent(int time) {
		this.time = time;
		this.currentTime = time;
	}

}
