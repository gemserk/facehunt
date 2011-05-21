package com.gemserk.games.facehunt.gamestates;

import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;

// TODO: use the same unique game resources declaration for all game states

/**
 * Declares all resources needed for the game.
 */
public class GameResourceBuilder extends LibgdxResourceBuilder {
	public GameResourceBuilder(ResourceManager<String> resourceManager) {
		super(resourceManager);

		texture("BackgroundTexture", "data/images/background01-1024x512.jpg", false);
		texture("HappyFaceTexture", "data/images/face-happy-64x64.png");
		texture("SadFaceTexture", "data/images/face-sad-64x64.png");
		texture("FaceSpriteSheet", "data/images/face-parts.png");
		texture("OverlayTexture", "data/images/white-rectangle.png");

		sprite("BackgroundSprite", "BackgroundTexture");
		sprite("HappyFaceSprite", "HappyFaceTexture");
		sprite("SadFaceSprite", "SadFaceTexture");
		sprite("OverlaySprite", "OverlayTexture");

		sprite("Part01", "FaceSpriteSheet", 64 * 0, 64 * 0, 64, 64);
		sprite("Part02", "FaceSpriteSheet", 64 * 1, 64 * 0, 64, 64);
		sprite("Part03", "FaceSpriteSheet", 64 * 2, 64 * 0, 64, 64);
		sprite("Part04", "FaceSpriteSheet", 64 * 3, 64 * 0, 64, 64);
		sprite("Part05", "FaceSpriteSheet", 64 * 0, 64 * 1, 64, 64);

		sound("CritterKilledSound", "data/sounds/critter_killed.ogg");
		sound("CritterSpawnedSound", "data/sounds/critter_spawned.ogg");
		sound("CritterBounceSound", "data/sounds/bounce.wav");
		sound("ButtonPressedSound", "data/sounds/button_pressed.ogg");

		font("Font", "data/fonts/font.png", "data/fonts/font.fnt");
		font("TitleFont", "data/fonts/titlefont.png", "data/fonts/titlefont.fnt", true);
	}
}