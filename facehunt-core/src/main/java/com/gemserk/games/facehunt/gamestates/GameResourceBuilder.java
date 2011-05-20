package com.gemserk.games.facehunt.gamestates;

import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;

/**
 * Declares all resources needed for the game.
 */
public class GameResourceBuilder extends LibgdxResourceBuilder {
	public GameResourceBuilder(ResourceManager<String> resourceManager) {
		super(resourceManager);

		texture("BackgroundTexture", "data/background01-1024x512.jpg", false);
		texture("HappyFaceTexture", "data/face-happy-64x64.png");
		texture("SadFaceTexture", "data/face-sad-64x64.png");
		texture("FaceSpriteSheet", "data/face-parts.png");
		texture("OverlayTexture", "data/white-rectangle.png");

		sprite("BackgroundSprite", "BackgroundTexture");
		sprite("HappyFaceSprite", "HappyFaceTexture");
		sprite("SadFaceSprite", "SadFaceTexture");
		sprite("OverlaySprite", "OverlayTexture");

		sprite("Part01", "FaceSpriteSheet", 64 * 0, 64 * 0, 64, 64);
		sprite("Part02", "FaceSpriteSheet", 64 * 1, 64 * 0, 64, 64);
		sprite("Part03", "FaceSpriteSheet", 64 * 2, 64 * 0, 64, 64);
		sprite("Part04", "FaceSpriteSheet", 64 * 3, 64 * 0, 64, 64);
		sprite("Part05", "FaceSpriteSheet", 64 * 0, 64 * 1, 64, 64);

		sound("CritterKilledSound", "data/sounds/bounce.wav");
		sound("CritterSpawnedSound", "data/sounds/critter-spawned.wav");
		sound("CritterBounceSound", "data/sounds/bounce.wav");

		font("Font", "data/font.png", "data/font.fnt");
	}
}