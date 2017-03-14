package com.yujinwunz.spazpong;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class SpazPong extends Game {
	public static int height = 0, width = 0;
	public static final int CAMERA_HEIGHT = 480;
	public static final int CAMERA_WIDTH = 800;

	OrthographicCamera camera = new OrthographicCamera();

	SpriteBatch spriteBatch;
	Texture img;
	ShapeRenderer shapeRenderer;
	AssetManager assetManager;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.debug("Spazpong", "Loading assets");
		assetManager = new AssetManager();
		maintainAssets();

		camera.setToOrtho(false, 800, 480);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		spriteBatch = new SpriteBatch();
		spriteBatch.setProjectionMatrix(camera.combined);
		this.setScreen(makeMainMenu());
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
		return new Menu("Spaz Pong!", this,
				Menu.createMenuItem("Single Player", new Callable() {
					@Override
					public Object call() throws Exception {
						setScreen(new SpazPongGame(me, new SpazPongGame.SpazPongOptions(true)));
						return null;
					}
				}, true),
				Menu.createMenuItem("2 Player", new Callable() {
					@Override
					public Object call() throws Exception {
						setScreen(new SpazPongGame(me, new SpazPongGame.SpazPongOptions(false)));
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

	public void maintainAssets() {
		Assets.stage(assetManager);
		Gdx.app.debug("SpazPong", "Middle of maintaining assets");
		Assets.load(assetManager);
	}

	@Override
	public void resume() {
		maintainAssets();
	}

	public void quit() {
		Gdx.app.exit();
	}
}
