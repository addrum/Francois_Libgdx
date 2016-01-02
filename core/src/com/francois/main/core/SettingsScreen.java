package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class SettingsScreen extends ScreenManager implements Screen {
    // customs
    private Stage stage;
    private Table table;
    private OrthographicCamera camera;
    private Slider drawPlayerUnderFinger;
    private InputProcessor inputProcessor;

    public SettingsScreen(Francois game) {
        super(game);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, getDeviceWidth(), getDeviceHeight());

        setStage();

        drawPlayerUnderFinger = new Slider(0, 1, 1, false, getSliderStyle());
        stage.addActor(drawPlayerUnderFinger);

        inputProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.BACK){
                    game().setScreen(new MainMenuScreen(game()));
                    dispose();
                }
                return false;
            }
        };
    }

    private void setStage() {
        stage = new Stage(new StretchViewport(getDeviceWidth(), getDeviceHeight()));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        table = new Table();
        table.setFillParent(true);
        table.align(Align.top);
        stage.addActor(table);
    }

    @Override
    public void show() {

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
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
