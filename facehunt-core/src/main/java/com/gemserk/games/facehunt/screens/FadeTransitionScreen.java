package com.gemserk.games.facehunt.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class FadeTransitionScreen extends ScreenAdapter {

	private final FaceHuntGame game;

	private final Color fadeColor = new Color();

	private final Color startColor = new Color(0f, 0f, 0f, 1f);

	private final Color endColor = new Color(0f, 0f, 0f, 0f);

	private ResourceManager<String> resourceManager;

	private SpriteBatch spriteBatch;

	private Sprite overlay;

	private ScreenAdapter currentScreen;

	private ScreenAdapter nextScreen;

	private int time;

	public void transition(ScreenAdapter currentScreen, ScreenAdapter nextScreen, int time) {
		this.currentScreen = currentScreen;
		this.nextScreen = nextScreen;
		this.time = time;
	}

	public FadeTransitionScreen(FaceHuntGame game) {
		this.game = game;
		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();
		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("OverlayTexture", "data/white-rectangle.png");
				sprite("OverlaySprite", "OverlayTexture");
			}
		};
	}

	@Override
	public void show() {
		overlay = resourceManager.getResourceValue("OverlaySprite");
		overlay.setPosition(0, 0);
		overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Synchronizers.transition(fadeColor, Transitions.transitionBuilder(endColor).end(startColor).time(time / 2).build(), new TransitionEventHandler() {
			@Override
			public void onTransitionFinished(Transition transition) {
				currentScreen = nextScreen;
				Synchronizers.transition(fadeColor, Transitions.transitionBuilder(startColor).end(endColor).time(time / 2).build(), new TransitionEventHandler() {
					@Override
					public void onTransitionFinished(Transition transition) {
						game.setScreen(nextScreen);
					}
				});
			}
		});
	}

	@Override
	public void internalRender(float delta) {
		currentScreen.internalRender(delta);

		if (spriteBatch == null)
			return;

		spriteBatch.begin();
		overlay.setColor(fadeColor);
		overlay.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void internalUpdate(float delta) {
		Synchronizers.synchronize();
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
