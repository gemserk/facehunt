package com.gemserk.games.facehunt;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.DesktopAnalyticsAutoConfigurator;

public class FaceHuntApplet extends Applet {

	private static final long serialVersionUID = 6396112708370503447L;

	private Canvas canvas;

	private LwjglApplication application;

	public void start() {

	}

	public void stop() {

	}

	public void destroy() {
		remove(canvas);
		super.destroy();
	}

	public void init() {
		GdxNativesLoader.disableNativesLoading = true;

		System.loadLibrary("gdx");

		try {
			setLayout(new BorderLayout());
			// ApplicationListener game = (ApplicationListener) Class.forName(getParameter("game")).newInstance();

			canvas = new Canvas() {
				public final void addNotify() {
					super.addNotify();

					AnalyticsConfigData config = new AnalyticsConfigData("UA-23542248-3");
					DesktopAnalyticsAutoConfigurator.populateFromSystem(config);

					JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(config, GoogleAnalyticsVersion.V_4_7_2);
					Analytics.traker = tracker;

					application = new LwjglApplication(new FaceHuntGame() {
						@Override
						public void create() {
							Gdx.graphics.setVSync(true);
							super.create();
						}
					}, false, this) {
						public com.badlogic.gdx.Application.ApplicationType getType() {
							return ApplicationType.Applet;
						};
					};
				}

				public final void removeNotify() {
					application.stop();
					super.removeNotify();
				}
			};
			canvas.setSize(getWidth(), getHeight());
			add(canvas);
			canvas.setFocusable(true);
			canvas.requestFocus();
			canvas.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display", e);
		}
	}
}