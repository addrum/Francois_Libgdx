package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;

public abstract class ScreenManager implements Screen {
	// shared
	protected static Francois game;
	protected static String app_id, score_leaderboard, time_leaderboard;

	public int tablePadding;

	// finals
	private final int deviceWidth;
	private final int deviceHeight;
	private boolean userSignedIn;

	// customs
	private static Screen currentScreen;
	private Preferences prefs = Gdx.app.getPreferences("prefs");
	private BitmapFont defaultFont, halfFont;
	private Color mainColor;
	private Skin skin;
	private Label.LabelStyle francoisLabelStyle, labelStyle;
	private TextButton.TextButtonStyle textButtonStyle;
    private ImageButton.ImageButtonStyle gpgsLoggedInStyle, gpgsLoggedOutStyle;
	private CheckBox.CheckBoxStyle checkBoxStyle;

	public ScreenManager(Francois game) {
		ScreenManager.game = game;

		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();

		defaultFont = createFont(getDeviceWidth() / 10);
		halfFont = createFont(getDeviceWidth() / 20);
		mainColor = new Color();
		mainColor.add(0.97f, 0.97f, 0.97f, 1.0f);

		tablePadding = getDeviceHeight() / 20;

		getStrings();
		createSkin();
		createLabelStyles();
		createPlainTextButtonStyle();
        createImageButtonStyles();
        createCheckBoxStyle();
	}

	protected void createLabelStyles() {
		francoisLabelStyle = new Label.LabelStyle();
		francoisLabelStyle.font = getDefaultFont();
		francoisLabelStyle.fontColor = Color.BLACK;

		labelStyle = new Label.LabelStyle();
		labelStyle.font = getHalfFont();
		labelStyle.fontColor = Color.BLACK;
	}

	protected void createSkin() {
		skin = new Skin();
		skin.add("default", getDefaultFont());
		skin.add("halfFont", getHalfFont());

		// Create a texture
		Pixmap pixmap = new Pixmap((int) getDeviceWidth() / 2, (int) getDeviceHeight() / 10, Pixmap.Format.RGB888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		skin.add("playButton", new Texture(Gdx.files.internal("images/playButton.png")), Texture.class);
		skin.add("gpgsLoggedOutUp", new Texture(Gdx.files.internal("images/games_controller_grey.png")), Texture.class);
		skin.add("gpgsLoggedInUp", new Texture(Gdx.files.internal("images/games_controller.png")), Texture.class);

		// checkbox edited from https://dribbble.com/shots/2008339-Animated-toggle?list=searches&tag=ui_animation&offset=4
		skin.add("checkBoxTicked", new Texture(Gdx.files.internal("images/checkBox_ticked.png")), Texture.class);
		skin.add("checkBoxUnticked", new Texture(Gdx.files.internal("images/checkBox_unticked.png")), Texture.class);
	}

	protected void createPlainTextButtonStyle() {
		// Create a button style
		textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", getMainColor());
		textButtonStyle.down = skin.newDrawable("white", getMainColor());
		textButtonStyle.checked = skin.newDrawable("white", getMainColor());
		textButtonStyle.over = skin.newDrawable("white", getMainColor());
		textButtonStyle.font = skin.getFont("halfFont");
		textButtonStyle.fontColor = Color.BLACK;
		skin.add("plainButton", textButtonStyle);
	}

    protected void createImageButtonStyles() {
        gpgsLoggedInStyle = new ImageButton.ImageButtonStyle();
        gpgsLoggedInStyle.imageUp = skin.newDrawable("gpgsLoggedInUp");
        skin.add("gpgsLoggedIn", gpgsLoggedInStyle);

        gpgsLoggedOutStyle = new ImageButton.ImageButtonStyle();
        gpgsLoggedOutStyle.imageUp = skin.newDrawable("gpgsLoggedOutUp");
        skin.add("gpgsLoggedOut", gpgsLoggedOutStyle);
    }

	protected void createCheckBoxStyle() {
		checkBoxStyle = new CheckBox.CheckBoxStyle();
		checkBoxStyle.checkboxOff = skin.newDrawable("checkBoxUnticked");
		checkBoxStyle.checkboxOn = skin.newDrawable("checkBoxTicked");
		checkBoxStyle.font = skin.getFont("halfFont");
		checkBoxStyle.fontColor = Color.BLACK;
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

	protected static void setScreen(Screen screen) {
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

	protected static Francois game() { return game; }

	protected Preferences preferences() { return prefs; }

	protected BitmapFont getDefaultFont() { return defaultFont; }

	protected BitmapFont getHalfFont() { return halfFont; }

	protected Color getMainColor() { return mainColor; }

	protected Skin getSkin() { return skin; }

	protected Label.LabelStyle getFrancoisLabelStyle() { return francoisLabelStyle; }

	protected Label.LabelStyle getLabelStyle() { return labelStyle; }

	protected TextButton.TextButtonStyle getTextButtonStyle() { return textButtonStyle; }

    protected ImageButton.ImageButtonStyle getGpgsLoggedInStyle() { return gpgsLoggedInStyle; }

    protected ImageButton.ImageButtonStyle getGpgsLoggedOutStyle() { return gpgsLoggedOutStyle;}

	protected CheckBox.CheckBoxStyle getCheckBoxStyle() { return checkBoxStyle; }

}
