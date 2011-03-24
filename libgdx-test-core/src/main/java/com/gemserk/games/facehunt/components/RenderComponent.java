package com.gemserk.games.facehunt.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.games.facehunt.values.Spatial;

public class RenderComponent {

	private Matrix4 rot = new Matrix4();

	private Vector3 rotationAxis = new Vector3(0f, 0f, 1f);

	private Matrix4 trx = new Matrix4();

	public void render(Entity entity, SpriteBatch spriteBatch) {
		Texture texture = Properties.getValue(entity, "image");
		Spatial spatial = Properties.getValue(entity, "spatial");
		Color color = Properties.getValue(entity, "color");
		
		Vector2 position = spatial.position;
		float angle = spatial.angle;

		rot.idt();
		trx.idt();

		rot.setToRotation(rotationAxis, angle);

		trx.trn(position.x, position.y, 0f);
		trx.mul(rot);

		spriteBatch.setTransformMatrix(trx);
		spriteBatch.begin();
		spriteBatch.setColor(color);
		spriteBatch.draw(texture, -texture.getWidth() / 2, -texture.getHeight() / 2);
		spriteBatch.end();
	}

}