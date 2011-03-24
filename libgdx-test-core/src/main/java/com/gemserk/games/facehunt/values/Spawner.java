package com.gemserk.games.facehunt.values;

import java.util.Map;

import com.gemserk.componentsengine.templates.EntityTemplate;

public class Spawner {
	
	public EntityTemplate template;
	
	public Map<String, Object> defaultParameters;

	public Spawner(EntityTemplate template, Map<String, Object> defaultParameters) {
		super();
		this.template = template;
		this.defaultParameters = defaultParameters;
	}
	
}
