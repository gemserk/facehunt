package com.gemserk.games.facehunt.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.facehunt.controllers.FaceHuntController;

public class FaceControllerComponent extends Component {

	private final FaceHuntController faceHuntController;
	
	private final Trigger trigger;
	
	/**
	 * used to increment the range when detecting a touch.
	 */
	private final float treshold;
	
	public FaceHuntController getFaceHuntController() {
		return faceHuntController;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public float getTreshold() {
		return treshold;
	}

	public FaceControllerComponent(FaceHuntController faceHuntController, float treshold, Trigger trigger) {
		this.faceHuntController = faceHuntController;
		this.trigger = trigger;
		this.treshold = treshold;
	}
	
}
