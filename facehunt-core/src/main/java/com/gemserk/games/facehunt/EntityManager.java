package com.gemserk.games.facehunt;

import java.util.ArrayList;

import com.gemserk.componentsengine.entities.Entity;

public class EntityManager {

	ArrayList<Entity> entities = new ArrayList<Entity>();

	ArrayList<Entity> entitiesToAdd = new ArrayList<Entity>();

	ArrayList<Entity> entitiesToRemove = new ArrayList<Entity>();

	public void addEntity(Entity entity) {
		entitiesToAdd.add(entity);
	}

	public ArrayList<Entity> getEntities() {

		if (!entitiesToAdd.isEmpty()) {
			entities.addAll(entitiesToAdd);
			entitiesToAdd.clear();
		}

		if (!entitiesToRemove.isEmpty()) {
			entities.removeAll(entitiesToRemove);
			entitiesToRemove.clear();
		}

		return entities;
	}

	public void remove(Entity entity) {
		entitiesToRemove.add(entity);
	}

	public void removeAll() {

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entitiesToRemove.add(entity);
		}

	}

	public void removeAll(String... tags) {

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);

			for (int j = 0; j < tags.length; j++) {
				String tag = tags[j];
				if (entity.hasTag(tag)) {
					entitiesToRemove.add(entity);
					break;
				}
			}

		}

	}

}