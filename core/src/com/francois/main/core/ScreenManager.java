package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.I18NBundle;

public abstract class ScreenManager implements Screen {
	// shared
	protected static Francois game;
	protected static String app_id, score_leaderboard, time_leaderboard;

	// finals
	private final int deviceWidth;
	private final int deviceHeight;

	// customs
	private Preferences prefs = Gdx.app.getPreferences("prefs");
	private static Screen currentScreen;
	private BitmapFont defaultFont, halfFont;
	private Color mainColor;

	public ScreenManager(Francois game) {
		ScreenManager.game = game;

		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();

		defaultFont = createFont(getDeviceWidth() / 10);
		halfFont = createFont(getDeviceWidth() / 20);
		mainColor = new Color();
		mainColor.add(0.97f, 0.97f, 0.97f, 1.0f);

		getStrings();
	}

	protected BitmapFont createFont(int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/exo2medium.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		// 10p = 10%
		BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		return font;
	}

	public static void setScreen(Screen screen) {
		if (currentScreen != null) {
			currentScreen.dispose();
			System.out.println("Screen disposed");
		}
		currentScreen = screen;
		game().setScreen(currentScreen);
	}

	public void getStrings() {
		boolean exists = Gdx.files.internal("data/strings.properties").exists();
		if (exists) {
				FileHandle baseFileHandle = Gdx.files.internal("data/strings");
				I18NBundle strings = I18NBundle.createBundle(baseFileHandle);
				app_id = strings.get("app_id");
				score_leaderboard = strings.get("score_leaderboard");
				time_leaderboard = strings.get("time_leaderboard");
		}
	}

	protected int getDeviceWidth() { return deviceWidth; }

	protected int getDeviceHeight() { return deviceHeight; }

	protected Preferences preferences() {
		return prefs;
	}

	protected static Francois game() { return game; }

	protected BitmapFont getDefaultFont() { return defaultFont; }

	protected BitmapFont getHalfFont() { return halfFont; }

	protected Color getMainColor() { return mainColor; }

}
