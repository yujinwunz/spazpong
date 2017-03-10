package com.yujinwunz.spazpong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.concurrent.Callable;

public class SpazPong extends Game {
	public static int height = 0, width = 0;
	public static final int CAMERA_HEIGHT = 480;
	public static final int CAMERA_WIDTH = 800;

	OrthographicCamera camera = new OrthographicCamera();

	SpriteBatch batch;
	Texture img;
	ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(makeMainMenu());
		camera.setToOrtho(false, 800, 480);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		SpazPong.height = height;
		SpazPong.width = width;
	}

	@Override
	public void render () {
		super.render();
	}


	public Menu makeMainMenu() {
		final SpazPong me = this;
		return new Menu(this,
				Menu.createMenuItem("Single Player", new Callable() {
					@Override
					public Object call() throws Exception {
						setScreen(new SpazPongGame(me));
						return null;
					}
				}, true),
				Menu.createMenuItem("2 Player", new Callable() {
					@Override
					public Object call() throws Exception {
						return null;
					}
				}),
				Menu.createMenuItem("Quit", new Callable() {
					@Override
					public Object call() throws Exception {
						quit();
						return null;
					}
				})
		);
	}

	public void quit() {
		Gdx.app.exit();
	}
}
