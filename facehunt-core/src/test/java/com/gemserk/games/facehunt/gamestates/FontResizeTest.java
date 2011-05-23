package com.gemserk.games.facehunt.gamestates;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.junit.Test;


public class FontResizeTest {

	public float calculateScaleForText(float viewportWidth, float textWidth, float limit) {
		if (textWidth < viewportWidth * limit)
			return 1f;
		return viewportWidth / textWidth * limit;
	}
	
	@Test
	public void shouldNotResizeIfLessThanLimit() {
		float viewportWidth = 100;
		float textWidth = 40;
		float limit = 0.8f;
		float scale = calculateScaleForText(viewportWidth, textWidth, limit);
		assertThat(scale, IsEqual.equalTo(1f));
	}
	
	@Test
	public void shouldResizeIfGreaterThanLimit() {
		float viewportWidth = 100;
		float textWidth = 100;
		float limit = 0.8f;
		float scale = calculateScaleForText(viewportWidth, textWidth, limit);
		assertThat(scale, IsEqual.equalTo(0.8f));
	}
	
	@Test
	public void shouldResizeIfGreaterThanLimit2() {
		float viewportWidth = 100;
		float textWidth = 200;
		float limit = 0.8f;
		float scale = calculateScaleForText(viewportWidth, textWidth, limit);
		assertThat(scale, IsEqual.equalTo(0.4f));
	}
	
}
