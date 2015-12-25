package com.francois.main;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen extends ScreenManager implements Screen {
	// finals
	final float weightW;
	final float weightH;
	final float francoisW;
	final float francoisH;

	// primitives
	long lastDropTime;
	int score;

	// customs
	Texture weightImage;
	Texture francoisImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle player;
	Array<Rectangle> weights;
	BitmapFont font;

	public GameScreen(Francois game) {
		super(game);

		// set finals
		weightW = (int) (deviceWidth() / 12.5);
		weightH = (int) (deviceWidth() / 12.5);

		// load the images
		weightImage = new Texture(Gdx.files.internal("images/weight_l.png"));
		francoisImage = new Texture(Gdx.files.internal("images/francois.png"));
		francoisW = francoisImage.getWidth();
		francoisH = francoisImage.getHeight();

		// load the sound effect and the background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rain.mp3"));
		rainMusic.setLooping(true);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, deviceWidth(), deviceHeight());

		// create a Rectangle to logically represent the bucket
		player = new Rectangle();
		player.x = deviceWidth() / 2 - weightW / 2; // center the bucket horizontally
		player.y = 100; 						  // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		player.width = francoisW;
		player.height = francoisH;

		// create the weights array and spawn the first raindrop
		weights = new Array<Rectangle>();
		spawnWeight(weightW, weightH);

	}

	private void spawnWeight(float width, float height) {
		Rectangle weight = new Rectangle();
		weight.x = MathUtils.random(0, deviceWidth() - weightW);
		weight.y = deviceHeight();
		weight.width = width;
		weight.height = height;
		weights.add(weight);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render(float delta) {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0.97f, 0.97f, 0.97f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game().batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the player and
		// all weights
		game().batch.begin();
		// game().font.draw(game().batch, "Score: " + score, 0, deviceHeight);
		game().batch.draw(francoisImage, player.x, player.y);
		for (Rectangle weight : weights) {
			game().batch.draw(weightImage, weight.x, weight.y, weight.width, weight.height);
		}
		game().batch.end();

		// process user input
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			player.x = touchPos.x - weightW / 2;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			player.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			player.x += 200 * Gdx.graphics.getDeltaTime();

		// make sure the bucket stays within the screen bounds
		if (player.x < 0)
			player.x = 0;
		if (player.x > deviceWidth() - weightW)
			player.x = deviceWidth() - weightW;

		double chance = Math.random();

		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > 2000000000) {
            if (chance > 0 && chance <= 0.5) {
                spawnWeight(weightW, weightH);
            } else if (chance > 0.5 && chance <= 0.85) {
                spawnWeight(weightW * 1.5f, weightH * 1.5f);
            } else if (chance > 0.85) {
                spawnWeight(weightW * 2f, weightH * 2f);
            }
        }

		// move the weights, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we increase the
		// value our drops counter and add a sound effect.
		Iterator<Rectangle> iter = weights.iterator();
		while (iter.hasNext()) {
			Rectangle weight = iter.next();
			weight.y -= 100 * Gdx.graphics.getDeltaTime();
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
	}
	
	public void gameOver() {
		// set local preferences for displaying score on main menu
		preferences().putInteger("lastscore", score);
		int highscore = preferences().getInteger("highscore");
		if (highscore == 0 || score > highscore) {
			preferences().putInteger("highscore", score);
		}
		preferences().flush();

		// update gpgs leaderboard
		if (game().actionResolver.getSignedInGPGS()) {
			game().actionResolver.submitScoreGPGS(score);
		}

		ScreenManager.setScreen(new MainMenuScreen(game));
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