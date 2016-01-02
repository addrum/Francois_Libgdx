package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.awt.event.InputEvent;

public class MainMenuScreen extends ScreenManager implements Screen, InputProcessor {
    // customs
    private Stage stage;
    private Table table;
    private Label francoisLabel, lastScoreLabel, lastScoreValueLabel, highscoreLabel, highscoreValueLabel;
    private TextButton achievementsButton, leaderboardsButton;
    private ImageButton playButton, gpgsLoggedInButton;
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

        if (game().actionResolver().getSignedInGPGS()) {
            gpgsLoggedInButton = new ImageButton(getSkin().newDrawable("gpgsLoggedIn"));
        } else {
            gpgsLoggedInButton = new ImageButton(getSkin().newDrawable("gpgsLoggedOut"));
        }

        gpgsLoggedInButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return false;
            }
        });

        achievementsButton = new TextButton("Achievements", getSkin(), "plainButton");
        leaderboardsButton = new TextButton("Leaderboards", getSkin(), "plainButton");

        int tablePadding = getDeviceHeight() / 20;

        table.add(gpgsLoggedInButton).expandX().align(Align.topLeft).size(getDeviceWidth() / 8, getDeviceHeight() / 8);
        table.row().padTop(tablePadding);
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
            if (game().actionResolver().getSignedInGPGS()) {
                gpgsLoggedInButton.setBackground(getSkin().newDrawable("gpgsLoggedIn"));
            } else {
                gpgsLoggedInButton.setBackground(getSkin().newDrawable("gpgsLoggedOut"));
            }
        game().batch.end();

        if (gpgsLoggedInButton.isPressed()) {
            if (game().actionResolver().getSignedInGPGS()) {
                game().actionResolver().logoutGPGS();
                setHighscoreValueLabel("loggedout flow");
            }
        }

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
        table.align(Align.top);
        stage.addActor(table);
    }

    private void setScoreValues() {
        if (getScorePreference() != 0) {
            setLastScoreValueLabel(Integer.toString(getScorePreference()));
        }

        if (game().actionResolver().getSignedInGPGS() && score_leaderboard != null) {
            setHighscoreValueLabel(getHighscorePreferences());
        }
    }

    private void setHighscoreValueLabel(String text) {
        highscoreValueLabel.setText(text);
    }

    private void setLastScoreValueLabel(String text) {
        lastScoreValueLabel.setText(text);
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