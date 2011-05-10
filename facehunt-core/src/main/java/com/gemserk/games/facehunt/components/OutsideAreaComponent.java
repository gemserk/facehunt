package com.gemserk.games.facehunt.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.artemis.triggers.Trigger;

public class OutsideAreaComponent extends Component{
	
	private final Rectangle area;

	private final Trigger trigger;
	
	public Rectangle getArea() {
		return area;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}

	public OutsideAreaComponent(Rectangle area, Trigger trigger) {
		this.area = area;
		this.trigger = trigger;
	}

}
