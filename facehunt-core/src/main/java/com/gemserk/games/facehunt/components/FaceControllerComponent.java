package com.gemserk.games.facehunt.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.facehunt.controllers.FaceHuntController;

public class FaceControllerComponent extends Component {

	private final FaceHuntController faceHuntController;
	
	private final Trigger trigger;
	
	public FaceHuntController getFaceHuntController() {
		return faceHuntController;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}

	public FaceControllerComponent(FaceHuntController faceHuntController, Trigger trigger) {
		this.faceHuntController = faceHuntController;
		this.trigger = trigger;
	}
	
}
