package com.gemserk.games.facehunt.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Spatial;

public class RenderComponent extends FieldsReflectionComponent {

	@EntityProperty(readOnly = true)
	Spatial spatial;

	@EntityProperty(key = "image", readOnly = true)
	Sprite sprite;

	@EntityProperty(readOnly = true)
	Color color;

	public RenderComponent(String id) {
		super(id);
	}

	public void render(Entity entity, SpriteBatch spriteBatch) {

		if (!entity.hasTag(Tags.SPATIAL))
			return;

		super.setEntity(entity);
		super.preHandleMessage(null);

		Vector2 position = spatial.position;
		float angle = spatial.angle;

		sprite.setColor(color);

		sprite.setScale(spatial.size.x, spatial.size.y);
		sprite.setRotation(angle);
		sprite.setPosition(
				position.x - sprite.getTexture().getWidth() / 2, 
				position.y - sprite.getTexture().getHeight() / 2
			);

		sprite.draw(spriteBatch);

		super.postHandleMessage(null);
	}

}