package com.francois.main.core;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameScreen extends ScreenManager implements Screen {
	// finals
	final float defaultW, defaultH;
	final float francoisW, francoisH;

	// primitives
	private long weightLastDropTime, scoreLastDropTime;
	private int score, time;
	private float timer = 0f;
	private boolean drawCentrally, playerTouched, start;

	// customs
	private Stage stage;
	private Table table, innerTable;
	private Label scoreLabel, timeLabel, dragToStartLabel;
	private Texture weightImage, francoisImage, scoreImage;
	private Sound dropSound;
	private Music rainMusic;
    private	OrthographicCamera camera;
	private Rectangle player;
	private Array<Rectangle> weights, scores;
    private InputProcessor inputProcessor;
    private Container centreContainer;

	public GameScreen(Francois game) {
		super(game);

		// set finals
		defaultW = (int) (getDeviceWidth() / 12.5);
		defaultH = (int) (getDeviceWidth() / 12.5);

		// load the images
		weightImage = new Texture(Gdx.files.internal("images/weight_l.png"));
		scoreImage = new Texture(Gdx.files.internal("images/score_item.png"));
		francoisImage = new Texture(Gdx.files.internal("images/francois.png"));
		francoisW = francoisImage.getWidth();
		francoisH = francoisImage.getHeight();

		// load the sound effect and the background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rain.mp3"));
		rainMusic.setLooping(true);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, getDeviceWidth(), getDeviceHeight());

		setStage();
        inputProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Keys.BACK){
                    game().setScreen(new MainMenuScreen(game()));
                    dispose();
                }
                return false;
            }
        };


		scoreLabel = new Label("0", getLabelStyle());
		timeLabel = new Label("0", getLabelStyle());
        dragToStartLabel = new Label("Drag player to start", getLabelStyle());

        centreContainer = new Container(dragToStartLabel);
        stage.addActor(centreContainer);
        centreContainer.center();
        centreContainer.setFillParent(true);

		innerTable.add(scoreLabel).width(getDeviceWidth() / 2).height(getDeviceHeight() / 20);
		innerTable.add(timeLabel).width(getDeviceWidth() / 2).height(getDeviceHeight() / 20);
		scoreLabel.setAlignment(Align.center);
		timeLabel.setAlignment(Align.center);
        dragToStartLabel.setAlignment(Align.center);

		// create a Rectangle to logically represent the player
		player = new Rectangle();
		player.x = getDeviceWidth() / 2 - defaultW / 2; // center the player horizontally
		player.y = 100; 						  // bottom left corner of the player is 100 pixels above the bottom screen edge
		player.width = francoisW;
		player.height = francoisH;

		drawCentrally = true;
		playerTouched = false;
		start = false;

		// create the weights array and spawn the first raindrop
		weights = new Array<Rectangle>();
		scores = new Array<Rectangle>();
	}

	private void setStage() {
		stage = new Stage(new StretchViewport(getDeviceWidth(), getDeviceHeight()));
		Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

		table = new Table();
		innerTable = new Table();
		table.setFillParent(true);

		stage.addActor(table);
		stage.addActor(innerTable);

		table.add(innerTable);
		table.align(Align.top);

		Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB888);
		pm.setColor(0.97f, 0.97f, 0.97f, 1.0f);
		pm.fill();
		innerTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm))));
	}

	private void spawnWeight(float width, float height) {
		Rectangle weight = new Rectangle();
		weight.x = MathUtils.random(0, getDeviceWidth() - defaultW);
		weight.y = getDeviceHeight();
		weight.width = width;
		weight.height = height;
		weights.add(weight);
		weightLastDropTime = TimeUtils.millis();
	}

	private void spawnScore() {
		Rectangle scoreItem = new Rectangle();
		scoreItem.x = MathUtils.random(0, getDeviceHeight() - defaultW);
		scoreItem.y = getDeviceHeight();
		scoreItem.width = defaultW;
		scoreItem.height = defaultH;
		scores.add(scoreItem);
		scoreLastDropTime = TimeUtils.millis();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game().batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the player and
		// all weights
		game().batch.begin();
			if (drawCentrally) {
				game().batch.draw(francoisImage, player.x - defaultW / 2, player.y - defaultH / 2);
			} else {
				game().batch.draw(francoisImage, player.x, player.y - defaultH / 2);
			}
            for (Rectangle weight : weights) {
                game().batch.draw(weightImage, weight.x - weight.width / 2, weight.y - weight.width / 2, weight.width, weight.height);
            }
            for (Rectangle scoreItem : scores) {
                game().batch.draw(scoreImage, scoreItem.x, scoreItem.y, scoreItem.width, scoreItem.height);
            }
		game().batch.end();

		// process user input
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = getTouchPos();
			if (player.contains(touchPos.x, touchPos.y)) {
				playerTouched = true;
                start = true;
                centreContainer.setVisible(false);
			}
			// if player has been touched before and the user hasn't lifted their finger up, move the player
			if (playerTouched) {
				player.x = touchPos.x - defaultW / 2;
			}
		} else {
			playerTouched = false;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			player.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			player.x += 200 * Gdx.graphics.getDeltaTime();

		// make sure the player stays within the screen bounds
		if (player.x < 0)
			player.x = 0;
		if (player.x > getDeviceWidth() - defaultW)
			player.x = getDeviceWidth() - defaultW;

		double chance = Math.random();

        if (start) {
            // update time value
            timer+=delta;
            if (timer >= 1f) {
                time++;
                timer-=1f;
            }

            // check if we need to create a new raindrop
            if (TimeUtils.millis() - weightLastDropTime > 2000) {
                if (chance > 0 && chance <= 0.5) {
                    spawnWeight(defaultW, defaultH);
                } else if (chance > 0.5 && chance <= 0.85) {
                    spawnWeight(defaultW * 1.5f, defaultH * 1.5f);
                } else if (chance > 0.85) {
                    spawnWeight(defaultW * 2f, defaultH * 2f);
                }
            }

            if (TimeUtils.millis() - scoreLastDropTime > 10000) {
                spawnScore();
            }

            // move the weights, remove any that are beneath the bottom edge of
            // the screen or that hit the player. In the later case we increase the
            // value our drops counter and add a sound effect.
            Iterator<Rectangle> iter = weights.iterator();
            while (iter.hasNext()) {
                Rectangle weight = iter.next();
                weight.y -= 300 * Gdx.graphics.getDeltaTime();
                if (weight.y + weight.height < 0)
                    iter.remove();
			/*if (weight.overlaps(player)) {
				score++;
				dropSound.play();
				iter.remove();
			}*/
                if (weight.overlaps(player)) {
                    gameOver();
                }
            }

            Iterator<Rectangle> iter2 = scores.iterator();
            while (iter2.hasNext()) {
                Rectangle scoreItem = iter2.next();
                scoreItem.y -= 100 * Gdx.graphics.getDeltaTime();
                if (scoreItem.y + scoreItem.height < 0)
                    iter2.remove();
                if (scoreItem.overlaps(player)) {
                    iter2.remove();
                    score++;
                }
            }

            scoreLabel.setText(Integer.toString(score));
            timeLabel.setText(Integer.toString(time));
        }

		stage.act(delta);
		stage.draw();
	}

	public void gameOver() {
		// set local preferences for displaying score on main menu
		preferences().putInteger("lastscore", score);
		long highscore = Long.parseLong(preferences().getString("highscore"));
		if (highscore == 0 || score > highscore) {
			preferences().putLong("highscore", score);
		}

		preferences().putInteger("time", time);
		preferences().flush();

		// update gpgs leaderboard
		if (game().actionResolver.getSignedInGPGS()) {
			game().actionResolver.submitScoreGPGS(score, score_leaderboard);
			game().actionResolver.submitTimeGPGS(time, time_leaderboard);
        }

        ScreenManager.setScreen(new MainMenuScreen(game));
	}

	public Vector3 getTouchPos() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		return touchPos;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		// rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		weightImage.dispose();
		francoisImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}