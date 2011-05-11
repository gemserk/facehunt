package com.gemserk.games.facehunt.systems;

import java.util.List;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.Spatial;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.facehunt.components.FaceControllerComponent;
import com.gemserk.games.facehunt.controllers.FaceHuntController;

public class FaceHuntControllerSystem extends EntityProcessingSystem {

	private static final Vector2 position = new Vector2();

	@SuppressWarnings("unchecked")
	public FaceHuntControllerSystem() {
		super(FaceControllerComponent.class, SpatialComponent.class);
	}

	@Override
	protected void process(Entity e) {
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		FaceControllerComponent faceControllerComponent = e.getComponent(FaceControllerComponent.class);

		FaceHuntController faceHuntController = faceControllerComponent.getFaceHuntController();
		List<Vector2> touchedPositions = faceHuntController.getTouchedPositions();

		if (touchedPositions.isEmpty())
			return;

		Spatial spatial = spatialComponent.getSpatial();
		float radius = spatial.getWidth() * 0.5f;
		position.set(spatial.getX(), spatial.getY());

		Trigger trigger = faceControllerComponent.getTrigger();

		for (int i = 0; i < touchedPositions.size(); i++) {
			Vector2 touchedPosition = touchedPositions.get(i);
			if (touchedPosition.dst(position) > radius)
				continue;

			if (trigger.isAlreadyTriggered())
				return;

			trigger.trigger(e);
		}
	}

}
