package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenuScreen extends ScreenManager implements Screen {

    Stage stage;
    Table table, innerTable;
    LabelStyle labelStyle;
    TextButtonStyle textButtonStyle;
    Label francoisLabel;
    TextButton playButton;
    ImageButton achievementsButton, leaderboardsButton;
    Skin skin;

    OrthographicCamera camera;

    public MainMenuScreen(Francois game) {
        super(game);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, deviceWidth(), deviceHeight());

        setMenu();

        francoisLabel = new Label("Francois", labelStyle);

        playButton = new TextButton("Play", skin, "blueButton");
        achievementsButton = new ImageButton(skin.newDrawable("achievement"));
        leaderboardsButton = new ImageButton(skin.newDrawable("leaderboard"));

        table.add(francoisLabel).expandX();
        // call for each new row of the table
        table.row().padTop((int) deviceHeight() / 10);
        table.add(playButton).expandX();
        table.row().padTop((int) deviceHeight() / 10);
        table.add(innerTable);
        innerTable.add(achievementsButton).expandX();
        innerTable.add(leaderboardsButton).expandX();
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
        innerTable = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        stage.addActor(table);
        stage.addActor(innerTable);

        table.setDebug(true);
        innerTable.setDebug(true);
    }

    private void setMenu() {
        setStage();
        createSkin();
        createLabelStyle();
        createTextButtonStyle();
    }

    private void createLabelStyle() {
        labelStyle = new LabelStyle();
        labelStyle.font = font();
        labelStyle.fontColor = Color.BLACK;
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default", font());

        // Create a texture
        Pixmap pixmap = new Pixmap((int) deviceWidth() / 2, (int) deviceHeight() / 10, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("background", new Texture(pixmap));
        skin.add("achievement", new Texture(Gdx.files.internal("images/games_achievements_green.png")), Texture.class);
        skin.add("leaderboard", new Texture(Gdx.files.internal("images/games_leaderboards_green.png")), Texture.class);
    }

    private void createTextButtonStyle() {
        // Create a button style
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", Color.BLUE);
        textButtonStyle.down = skin.newDrawable("background", Color.NAVY);
        textButtonStyle.checked = skin.newDrawable("background", Color.NAVY);
        textButtonStyle.over = skin.newDrawable("background", Color.NAVY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("blueButton", textButtonStyle);
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