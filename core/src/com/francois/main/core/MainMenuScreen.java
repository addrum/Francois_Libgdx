package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenuScreen extends ScreenManager implements Screen {
    // customs
    private Stage stage;
    private Table table;
    private Label francoisLabel, lastScoreLabel, lastScoreValueLabel, highscoreLabel, highscoreValueLabel;
    private TextButton achievementsButton, leaderboardsButton;
    private ImageButton playButton;
    private OrthographicCamera camera;

    public MainMenuScreen(Francois game) {
        super(game);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, getDeviceWidth(), getDeviceHeight());

        setStage();

        francoisLabel = new Label("Francois", getFrancoisLabelStyle());
        lastScoreLabel = new Label("Last Score:", getLabelStyle());
        lastScoreValueLabel = new Label("0", getLabelStyle());
        highscoreLabel = new Label("Highscore:", getLabelStyle());
        highscoreValueLabel = new Label("0", getLabelStyle());

        playButton = new ImageButton(getSkin().newDrawable("playButton"));
        achievementsButton = new TextButton("Achievements", getSkin(), "plainButton");
        leaderboardsButton = new TextButton("Leaderboards", getSkin(), "plainButton");

        int tablePadding = (int) getDeviceHeight() / 20;

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
        Gdx.gl.glClearColor(0.97f, 0.97f, 0.97f, 1.0f);
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
        stage = new Stage(new StretchViewport(getDeviceWidth(), getDeviceHeight()));
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
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
        if (game().actionResolver().getSignedInGPGS()) {
            game().actionResolver().getUserHighScoreGPGS(score_leaderboard);
        }
        return preferences().getString("highscore");
    }

    private int getScorePreference() {
        return preferences().getInteger("lastscore");
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
        if (stage != null) {
            stage.dispose();
        }
    }

}