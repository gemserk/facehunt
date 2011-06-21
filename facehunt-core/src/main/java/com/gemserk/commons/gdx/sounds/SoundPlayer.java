package com.gemserk.commons.gdx.sounds;

import com.badlogic.gdx.audio.Sound;

public class SoundPlayer {
	
	private float volume = 1f;
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public float getVolume() {
		return volume;
	}
	
	public void play(Sound sound) {
		this.play(sound, 1f);
	}
	
	public void play(Sound sound, float volume) {
		if (this.volume > 0f)
			sound.play(this.volume * volume);
	}

}
