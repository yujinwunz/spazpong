package com.yujinwunz.spazpong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by yujinwunz on 13/03/2017.
 */

public class Assets {
	public static String FONT_SMALL = "fonts/prstartk.fnt";
	public static String FONT_LARGE = "fonts/prstartk-large.fnt";
	public static String BUTTON_ATLAS = "buttons/buttons.pack.atlas";
	public static String SPRITE_ATLAS = "sprites/sprites.pack.atlas";
	public static class Sprites {
		public static String PADDLE_LEFT = "paddle_left";
		public static String PADDLE_LEFT_BLINK = "paddle_blink_left";
		public static String PADDLE_LEFT_HIT = "paddle_hit_left";

		public static String PADDLE_RIGHT = "paddle_right";
		public static String PADDLE_RIGHT_BLINK = "paddle_blink_right";
		public static String PADDLE_RIGHT_HIT = "paddle_hit_right";

		public static String BALL = "ball";
	}

	public static class Button {
		public static String BUTTON_UP = "up-button";
		public static String BUTTON_DOWN = "down-button";
		public static String BUTTON_CHECKED = "checked-button";
	}


	public static void stage(AssetManager manager) {
		manager.load(BUTTON_ATLAS, TextureAtlas.class);
		manager.load(SPRITE_ATLAS, TextureAtlas.class);
		manager.load(FONT_SMALL, BitmapFont.class);
		manager.load(FONT_LARGE, BitmapFont.class);

	}
	public static void load(AssetManager manager) {
		Gdx.app.debug("Assets", "loading assets");
		manager.finishLoading();
		Gdx.app.debug("Assets", "loaded " + manager.getLoadedAssets() + " assets");
		for (String s : manager.getAssetNames()) {
			Gdx.app.debug("Assets", "..." + s);
		}
	}
}
