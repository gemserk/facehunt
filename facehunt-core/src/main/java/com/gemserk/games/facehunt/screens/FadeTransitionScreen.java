package com.gemserk.games.facehunt.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizer;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.facehunt.FaceHuntGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class FadeTransitionScreen extends ScreenImpl {

	private final FaceHuntGame game;

	private final Color fadeColor = new Color();

	private final Color startColor = new Color(0f, 0f, 0f, 1f);

	private final Color endColor = new Color(0f, 0f, 0f, 0f);

	private ResourceManager<String> resourceManager;

	private SpriteBatch spriteBatch;

	private Sprite overlay;

	private Screen currentScreen;

	private Screen nextScreen;

	private int time;

	private boolean shouldDisposeCurrent;
	
	private Synchronizer synchronizer;
	
	private Screen getCurrentScreen() {
		return currentScreen;
	}
	
	private Screen getNextScreen() {
		return nextScreen;
	}
	
	private void setCurrentScreen(Screen currentScreen) {
		this.currentScreen = currentScreen;
	}

	public void transition(Screen currentScreen, Screen nextScreen, int time) {
		this.transition(currentScreen, nextScreen, time, false);
	}
	
	public void transition(Screen currentScreen, Screen nextScreen, int time, boolean shouldDisposeCurrent) {
		this.currentScreen = currentScreen;
		this.nextScreen = nextScreen;
		this.time = time;
		this.shouldDisposeCurrent = shouldDisposeCurrent;
	}

	public FadeTransitionScreen(FaceHuntGame game) {
		super(new GameStateImpl());
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
	public void resume() {
		synchronizer = new Synchronizer();
		
		overlay = resourceManager.getResourceValue("OverlaySprite");
		overlay.setPosition(0, 0);
		overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		if (getCurrentScreen() == null) {
			setCurrentScreen(getNextScreen());
			getCurrentScreen().init();
			getCurrentScreen().show();
			synchronizer.transition(fadeColor, Transitions.transitionBuilder(startColor).end(endColor).time(time / 2), new TransitionEventHandler() {
				@Override
				public void onTransitionFinished(Transition transition) {
					getCurrentScreen().resume();
					game.setScreen(getCurrentScreen());
				}
			});
		} else {
			getCurrentScreen().pause();
			synchronizer.transition(fadeColor, Transitions.transitionBuilder(endColor).end(startColor).time(time / 2), new TransitionEventHandler() {
				@Override
				public void onTransitionFinished(Transition transition) {
					getCurrentScreen().hide();
					if (shouldDisposeCurrent)
						getCurrentScreen().dispose();
					setCurrentScreen(getNextScreen());
					getCurrentScreen().init();
					getCurrentScreen().show();
					synchronizer.transition(fadeColor, Transitions.transitionBuilder(startColor).end(endColor).time(time / 2), new TransitionEventHandler() {
						@Override
						public void onTransitionFinished(Transition transition) {
							getCurrentScreen().resume();
							game.setScreen(getCurrentScreen());
						}
					});
				}
			});
		}
	}
	
	@Override
	public void render(int delta) {
		if (currentScreen != null)
			currentScreen.render(delta);

		if (spriteBatch == null)
			return;

		spriteBatch.begin();
		overlay.setColor(fadeColor);
		overlay.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update(int delta) {
		synchronizer.synchronize(delta);
	}
	
	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
