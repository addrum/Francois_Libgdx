package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class SettingsScreen extends ScreenManager implements Screen {
    // customs
    private Stage stage;
    private Table table;
    private OrthographicCamera camera;
    private CheckBox centrePlayCheckBox, leftyCheckBox;

    public SettingsScreen(Francois game) {
        super(game);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, getDeviceWidth(), getDeviceHeight());

        setStage();

        centrePlayCheckBox = new CheckBox("Player under finger", getCheckBoxStyle());
        leftyCheckBox = new CheckBox("Lefty?", getCheckBoxStyle());

        table.add(centrePlayCheckBox).expandX();
        table.row().padTop(tablePadding);
        table.add(leftyCheckBox).expandX();

        centrePlayCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (centrePlayCheckBox.isChecked()) {
                    leftyCheckBox.setDisabled(false);
                } else {
                    leftyCheckBox.setDisabled(true);
                }
                System.out.println(Boolean.toString(leftyCheckBox.isDisabled()));
            }
        });
    }

    private void setStage() {
        stage = new Stage(new StretchViewport(getDeviceWidth(), getDeviceHeight())) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    System.out.println("back button pressed");
                    game().setScreen(new MainMenuScreen(game()));
                    dispose();
                }
                return super.keyDown(keyCode);
            }
        };
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.setDebug(true);
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
