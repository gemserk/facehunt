package com.gemserk.games.facehunt.components;

import com.artemis.Entity;
import com.artemis.World;

public interface Script {
	
	void init(World world, Entity e);
	
	void update(World world, Entity e);
	
	void dispose(World world, Entity e);
	
}
