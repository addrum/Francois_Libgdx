package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenuScreen extends ScreenManager implements Screen {
    // customs
    private Stage stage;
    private Table table;
    private Image francoisIcon;
    private ImageButton playButton, achievementsButton, leaderboardButton, gpgsLoggedInButton;
    private Texture backgroundImage;
    private OrthographicCamera camera;

    public MainMenuScreen(Francois game) {
        super(game);

        game().getActionResolver().setMainMenuScreen(this);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, getDeviceWidth(), getDeviceHeight());

        setStage();

        backgroundImage = new Texture((Gdx.files.internal("images/stripe.png")));

        francoisIcon = new Image(getSkin().newDrawable("francoisIcon"));
        playButton = new ImageButton(getSkin().newDrawable("playButton"));
        achievementsButton = new ImageButton(getSkin().newDrawable("achievementsButton"));
        leaderboardButton = new ImageButton(getSkin().newDrawable("leaderboardButton"));

        if (game().getActionResolver().getSignedInGPGS()) {
            gpgsLoggedInButton = new ImageButton(getGpgsLoggedInStyle());
        } else {
            gpgsLoggedInButton = new ImageButton(getGpgsLoggedOutStyle());
        }

        table.row().padTop(rowPadding * 2.5f);
        float francoisIconWidth = getDeviceWidth() / 1.8f;
        table.add(francoisIcon).expandX().size(francoisIconWidth, (361f / 600f) * francoisIconWidth);
        // call for each new row of the table
        table.row().padTop(rowPadding);
        table.add(achievementsButton).expandX();
        table.row().padTop(rowPadding);
        table.add(leaderboardButton).expandX();
        table.row().padTop(rowPadding);
        table.add(playButton).expandX();
        table.row().padTop(rowPadding);
        //table.add(settingsButton).expandX();
        table.add(gpgsLoggedInButton).expandX().size(getDeviceWidth() / 8, getDeviceHeight() / 8);

        gpgsLoggedInButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (game().getActionResolver().getSignedInGPGS()) {
                    game().getActionResolver().logoutGPGS();
                    gpgsLoggedInButton.setStyle(getGpgsLoggedOutStyle());
                } else if (!game().getActionResolver().getSignedInGPGS()) {
                    game().getActionResolver().loginGPGS();
                }
            }
        });

        playButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                game().setScreen(new GameScreen(game()));
                game().getAdsController().hideBannerAd();
                dispose();
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (game().getActionResolver().getSignedInGPGS())
                    game().getActionResolver().getLeaderboardGPGS();
                else
                    game().getActionResolver().loginGPGS();
            }
        });

        achievementsButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (game().getActionResolver().getSignedInGPGS())
                    game().getActionResolver().getAchievementsGPGS();
                else
                    game().getActionResolver().loginGPGS();
            }
        });

        /*settingsButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                game().setScreen(new SettingsScreen(game()));
                dispose();
            }
        });*/
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.97f, 0.97f, 0.97f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.getBatch().begin();
            stage.getBatch().draw(backgroundImage, 0, 0, getDeviceWidth(), getDeviceHeight());
        stage.getBatch().end();
        stage.draw();

        camera.update();
        game().batch.setProjectionMatrix(camera.combined);

        game().batch.begin();
        game().batch.end();
    }

    public void setGPGSButtonStyle(boolean loggedIn) {
        if (loggedIn) {
            gpgsLoggedInButton.setStyle(getGpgsLoggedInStyle());
        } else {
            gpgsLoggedInButton.setStyle(getGpgsLoggedOutStyle());
        }
    }

    private void setStage() {
        stage = new Stage(new StretchViewport(getDeviceWidth(), getDeviceHeight())) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    System.out.println("back button pressed");
                    Gdx.app.exit();
                }
                return super.keyDown(keyCode);
            }
        };
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.align(Align.center);
        table.setDebug(true);
        stage.addActor(table);
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
        if (stage != null) {
            stage.dispose();
        }
    }

    @Override
    public void dispose() {
    }


}