package com.gemserk.games.facehunt;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

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
		try {
			setLayout(new BorderLayout());
			// ApplicationListener game = (ApplicationListener) Class.forName(getParameter("game")).newInstance();

			canvas = new Canvas() {
				public final void addNotify() {
					super.addNotify();
					application = new LwjglApplication(new FaceHuntGame(), false, this);
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