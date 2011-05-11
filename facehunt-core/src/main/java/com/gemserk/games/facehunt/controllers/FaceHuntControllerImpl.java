package com.gemserk.games.facehunt.controllers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.input.LibgdxPointer;

public class FaceHuntControllerImpl implements FaceHuntController {
	
	private ArrayList<LibgdxPointer> libgdxPointers = new ArrayList<LibgdxPointer>();
	
	private ArrayList<Vector2> touchedPositions = new ArrayList<Vector2>();
	
	public FaceHuntControllerImpl() {
		libgdxPointers.add(new LibgdxPointer(0));
		libgdxPointers.add(new LibgdxPointer(1));
	}

	@Override
	public void update(int delta) {
		touchedPositions.clear();
		for (int i = 0; i < libgdxPointers.size(); i++) {
			LibgdxPointer libgdxPointer = libgdxPointers.get(i);
			libgdxPointer.update();
			if (libgdxPointer.wasPressed) {
				touchedPositions.add(libgdxPointer.getPressedPosition());
			}
		}
	}

	@Override
	public List<Vector2> getTouchedPositions() {
		return touchedPositions;
	}
	
}