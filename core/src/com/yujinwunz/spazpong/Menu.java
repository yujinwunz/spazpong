package com.yujinwunz.spazpong;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by yujinwunz on 8/03/2017.
 */

public class Menu extends ScreenAdapter {

	Stage stage;

	public static float BACKGROUND_COLOUR_R = 0.43f;
	public static float BACKGROUND_COLOUR_G = 0.65f;
	public static float BACKGROUND_COLOUR_B = 0.70f;

	public static final int MENUITEM_PADDING = 50;

	public static final int BUTTON_PADDING = 30;

	private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
	private ApplicationListener game;


	// Style objects reused across menus
	static BitmapFont font_small;
	static BitmapFont font_large;
	static Skin skin;
	static {
		skin = new Skin();
		TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.pack.atlas"));
		skin.addRegions(buttonAtlas);
		font_small = new BitmapFont(Gdx.files.internal("fonts/prstartk.fnt"));
		font_large = new BitmapFont(Gdx.files.internal("fonts/prstartk-large.fnt"));
		for (String drawableName: new String[]{"up-button", "down-button", "checked-button"}) {
			Drawable d = skin.getDrawable(drawableName);
			d.setBottomHeight(BUTTON_PADDING);
			d.setLeftWidth(BUTTON_PADDING);
			d.setTopHeight(BUTTON_PADDING);
			d.setRightWidth(BUTTON_PADDING);
		}
	}

	public Menu(ApplicationListener game, MenuItem... menuItems) {
		for (MenuItem m : menuItems) {
			this.menuItems.add(m);
		}
		this.game = game;

		stage = new Stage();
		Table table = new Table();
		table.setFillParent(true);
		table.center();


		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("up-button");
		textButtonStyle.down = skin.getDrawable("down-button");
		textButtonStyle.checked = skin.getDrawable("checked-button");
		textButtonStyle.checkedFontColor =
				new Color(BACKGROUND_COLOUR_R, BACKGROUND_COLOUR_G, BACKGROUND_COLOUR_B, 1);
		textButtonStyle.downFontColor =
				new Color(BACKGROUND_COLOUR_R/3, BACKGROUND_COLOUR_G/3, BACKGROUND_COLOUR_B/3, 1);
		textButtonStyle.fontColor =
				new Color(BACKGROUND_COLOUR_R, BACKGROUND_COLOUR_G, BACKGROUND_COLOUR_B, 1);

		for (MenuItem m : menuItems) {
			final MenuItem final_m = m;
			Button menuButton = new Button();
			textButtonStyle.font = m.isMain ? font_large : font_small;
			Button button = new TextButton(m.text, textButtonStyle);
			button.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					try {
						final_m.callable.call();
					} catch (Exception e) {
						Gdx.app.error("Menu", "Menu item exception", e);
					}
				}
			});
			table.add(button).pad(MENUITEM_PADDING);
			table.row();
		}

		stage.addActor(table);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(BACKGROUND_COLOUR_R, BACKGROUND_COLOUR_G, BACKGROUND_COLOUR_B, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	public static class MenuItem {
		public String text;
		public Callable callable;
		public boolean isMain = false;

		public MenuItem(String text, Callable callable) {
			this(text, callable, false);
		}
		public MenuItem(String text, Callable callable, boolean isMain) {
			this.text = text;
			this.callable = callable;
			this.isMain = isMain;
		}
	}

	public static MenuItem createMenuItem(String text, Callable callable) {
		return new MenuItem(text, callable);
	}
	public static MenuItem createMenuItem(String text, Callable callable, boolean isMain) {
		return new MenuItem(text, callable, isMain);
	}
}


