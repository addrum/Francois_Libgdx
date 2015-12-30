package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenuScreen extends ScreenManager implements Screen {

    private Stage stage;
    private Table table;
    private LabelStyle francoisLabelStyle, labelStyle;
    private TextButtonStyle textButtonBlueStyle, textButtonStyle;
    private ImageTextButton.ImageTextButtonStyle imageTextButtonStyle;
    private Label francoisLabel, lastScoreLabel, lastScoreValueLabel, highscoreLabel, highscoreValueLabel;
    private TextButton achievementsButton, leaderboardsButton;
    private ImageButton playButton;
    //private ImageButton achievementsButton, leaderboardsButton;
    private Skin skin;
    private OrthographicCamera camera;
    private BitmapFont defaultFont, halfFont;
    private Color mainColor;

    public MainMenuScreen(Francois game) {
        super(game);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, deviceWidth(), deviceHeight());

        defaultFont = createFont(deviceWidth() / 10);
        halfFont = createFont(deviceWidth() / 20);
        mainColor = new Color();
        mainColor.add(0.97f, 0.97f, 0.97f, 1.0f);

        setMenu();

        francoisLabel = new Label("Francois", francoisLabelStyle);
        lastScoreLabel = new Label("Last Score:", labelStyle);
        lastScoreValueLabel = new Label("0", labelStyle);
        highscoreLabel = new Label("Highscore:", labelStyle);
        highscoreValueLabel = new Label("0", labelStyle);

        playButton = new ImageButton(skin.newDrawable("playButton"));
        //achievementsButton = new ImageButton(skin.newDrawable("achievement"));
        //leaderboardsButton = new ImageButton(skin.newDrawable("leaderboard"));
        achievementsButton = new TextButton("Achievements", skin, "plainButton");
        leaderboardsButton = new TextButton("Leaderboards", skin, "plainButton");

        int tablePadding = (int) deviceHeight() / 20;

        table.add(francoisLabel).expandX();
        // call for each new row of the table
        table.row().padTop(tablePadding);
        table.add(lastScoreLabel).expandX();
        table.row();
        table.add(lastScoreValueLabel).expandX();
        table.row().padTop(tablePadding);
        table.add(highscoreLabel).expandX();
        table.row();
        table.add(highscoreValueLabel).expandX();
        table.row().padTop(tablePadding);
        table.add(playButton).expandX();
        table.row();
        table.add(leaderboardsButton).expandX();
        table.row();
        table.add(achievementsButton).expandX().bottom();

        setScoreValues();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.97f, 0.97f, 0.97f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        camera.update();
        game().batch.setProjectionMatrix(camera.combined);

        game().batch.begin();
        game().batch.end();

        if (playButton.isPressed()) {
            game().setScreen(new GameScreen(game));
            dispose();
        }
        if (leaderboardsButton.isPressed()) {
            if (game().actionResolver().getSignedInGPGS())
                game().actionResolver().getLeaderboardGPGS();
            else
                game().actionResolver().loginGPGS();
        }
        if (achievementsButton.isPressed()) {
            if (game().actionResolver().getSignedInGPGS())
                game().actionResolver().getAchievementsGPGS();
            else
                game().actionResolver().loginGPGS();
        }
    }

    private void setStage() {
        stage = new Stage(new StretchViewport(deviceWidth(), deviceHeight()));
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        stage.addActor(table);

        table.setDebug(true);
    }

    private void setMenu() {
        setStage();
        createSkin();
        createLabelStyles();
        createTextButtonStyles();
    }

    private void createLabelStyles() {
        francoisLabelStyle = new LabelStyle();
        francoisLabelStyle.font = defaultFont;
        francoisLabelStyle.fontColor = Color.BLACK;

        labelStyle = new LabelStyle();
        labelStyle.font = halfFont;
        labelStyle.fontColor = Color.BLACK;
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default", defaultFont);
        skin.add("halfFont", halfFont);

        // Create a texture
        Pixmap pixmap = new Pixmap((int) deviceWidth() / 2, (int) deviceHeight() / 10, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("blue", new Texture(pixmap));
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        skin.add("playButton", new Texture(Gdx.files.internal("images/playButton.png")), Texture.class);
        skin.add("achievement", new Texture(Gdx.files.internal("images/games_achievements_green.png")), Texture.class);
        skin.add("leaderboard", new Texture(Gdx.files.internal("images/games_leaderboards_green.png")), Texture.class);
    }

    private void createTextButtonStyles() {
        // Create a button style
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", mainColor);
        textButtonStyle.down = skin.newDrawable("white", mainColor);
        textButtonStyle.checked = skin.newDrawable("white", mainColor);
        textButtonStyle.over = skin.newDrawable("white", mainColor);
        textButtonStyle.font = skin.getFont("halfFont");
        textButtonStyle.fontColor = Color.BLACK;
        skin.add("plainButton", textButtonStyle);

        textButtonBlueStyle = new TextButtonStyle();
        textButtonBlueStyle.up = skin.newDrawable("blue", Color.BLUE);
        textButtonBlueStyle.down = skin.newDrawable("blue", Color.NAVY);
        textButtonBlueStyle.checked = skin.newDrawable("blue", Color.NAVY);
        textButtonBlueStyle.over = skin.newDrawable("blue", Color.NAVY);
        textButtonBlueStyle.font = skin.getFont("default");
        skin.add("blueButton", textButtonBlueStyle);
    }

    private void setScoreValues() {
        if (getScorePreference() != 0) {
            setLastScoreValueLabel();
        }

        if (game().actionResolver().getSignedInGPGS() && score_leaderboard != null) {
            setHighscoreValueLabel();
        }
    }

    private void setHighscoreValueLabel() {
        highscoreValueLabel.setText(getHighscorePreferences());
    }

    private void setLastScoreValueLabel() {
        lastScoreValueLabel.setText(Integer.toString(getScorePreference()));
    }

    private String getHighscorePreferences() {
        game().actionResolver().getUserHighScoreGPGS(score_leaderboard);
        String hs = preferences().getString("highscore");
        return hs;
    }

    private int getScorePreference() {
        return preferences().getInteger("score");
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}