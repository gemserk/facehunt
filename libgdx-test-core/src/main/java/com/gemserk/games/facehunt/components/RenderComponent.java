package com.gemserk.games.facehunt.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.facehunt.entities.Tags;
import com.gemserk.games.facehunt.values.Spatial;

public class RenderComponent {

	public void render(Entity entity, SpriteBatch spriteBatch) {

		if (!entity.hasTag(Tags.SPATIAL))
			return;

		Sprite sprite = Properties.getValue(entity, "image");
		Spatial spatial = Properties.getValue(entity, "spatial");
		Color color = Properties.getValue(entity, "color");

		Vector2 position = spatial.position;
		float angle = spatial.angle;

		sprite.setColor(color);
		sprite.rotate(-sprite.getRotation());
		sprite.translate(-sprite.getX(), -sprite.getY());
		
		sprite.rotate(angle);
		sprite.translate(-sprite.getTexture().getWidth() / 2, -sprite.getTexture().getHeight() / 2);
		sprite.translate(position.x, position.y);
		sprite.draw(spriteBatch);
	}

}