package com.gemserk.commons.values;

import com.gemserk.animation4j.converters.TypeConverter;

public class FloatValueConverter implements TypeConverter<FloatValue> {

	@Override
	public int variables() {
		return 1;
	}

	@Override
	public float[] copyFromObject(FloatValue f, float[] x) {
		if (x == null)
			x = new float[1];
		x[0] = f.value;
		return x;
	}

	@Override
	public FloatValue copyToObject(FloatValue f, float[] x) {
		if (f == null)
			f = new FloatValue(0f);
		f.value = x[0];
		return f;
	}

}
