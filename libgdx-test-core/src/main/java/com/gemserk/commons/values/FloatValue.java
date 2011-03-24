package com.gemserk.commons.values;

public class FloatValue {

	public float value = 0;

	public FloatValue(float initialValue) {
		this.value = initialValue;
	}

	public FloatValue(FloatValue f) {
		this.value = f.value;
	}

}