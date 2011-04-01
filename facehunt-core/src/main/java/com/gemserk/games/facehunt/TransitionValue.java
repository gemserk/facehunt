package com.gemserk.games.facehunt;

public class TransitionValue<T> {
	
	private final T value;

	private final int time;
	
	public int getTime() {
		return time;
	}
	
	public T getValue() {
		return value;
	}

	public TransitionValue(T value, int time) {
		this.value = value;
		this.time = time;
	}

}
