package com.gemserk.games.facehunt.values;

import java.util.Map;

import com.gemserk.componentsengine.templates.EntityTemplate;
import com.gemserk.games.facehunt.components.DefaultParametersBuilder;

public class Spawner {
	
	public final int limit;
	
	public EntityTemplate template;

	public Map<String, Object> defaultParameters;
	
	public DefaultParametersBuilder defaultParametersBuilder;
	
	public Spawner(EntityTemplate template, Map<String, Object> defaultParameters, DefaultParametersBuilder defaultParametersBuilder, int limit) {
		this.template = template;
		this.defaultParameters = defaultParameters;
		this.defaultParametersBuilder = defaultParametersBuilder;
		this.limit = limit;
	}
	
}
