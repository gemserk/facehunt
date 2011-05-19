package com.gemserk.games.facehunt.controllers;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.controllers.Controller;

public interface FaceHuntController extends Controller {
	
	List<Vector2> getTouchedPositions();
	
	// should have damage?
	
}