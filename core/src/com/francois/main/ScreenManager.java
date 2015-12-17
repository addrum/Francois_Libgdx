package com.francois.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public abstract class ScreenManager implements Screen {
	// finals
	final int deviceWidth;
	final int deviceHeight;

	BitmapFont font;
	Francois game;
	Preferences prefs = Gdx.app.getPreferences("prefs");

	public ScreenManager(Francois game) {
		this.game = game;
		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		
		createFont(deviceWidth / 10);
	}
	
	protected void createFont(int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/exo2medium.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        // 10p = 10%
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}
	
	protected BitmapFont getFont() {
		return font;
	}
	
	protected int deviceWidth() {
		return deviceWidth;
	}
	
	protected int deviceHeight() {
		return deviceHeight;
	}
	
	protected Preferences preferences() {
		return prefs;
	}

	public Francois getGame() {
		return game;
	}

}
