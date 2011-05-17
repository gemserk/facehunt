package com.gemserk.games.facehunt.components;

import com.artemis.Component;

public class PointsComponent extends Component{

	private final int points;
	
	public int getPoints() {
		return points;
	}

	public PointsComponent(int points) {
		this.points = points;
	}
	
}
