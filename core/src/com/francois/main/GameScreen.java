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
	final int weightW;
	final int weightH;
	final int francoisW;
	final int francoisH;

	// primitives
	long lastDropTime;
	int score;

	// customs
	Texture dropImage;
	Texture francoisImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle player;
	Array<Rectangle> raindrops;
	BitmapFont font;

	public GameScreen(Francois game) {
		super(game);

		// set finals
		weightW = (int) (deviceWidth() / 12.5);
		weightH = (int) (deviceWidth() / 12.5);

		// load the images
		dropImage = new Texture(Gdx.files.internal("images/weight_s.png"));
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

		// create the raindrops array and spawn the first raindrop
		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, deviceWidth() - weightW);
		raindrop.y = deviceHeight();
		raindrop.width = weightW;
		raindrop.height = weightH;
		raindrops.add(raindrop);
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

		// begin a new batch and draw the bucket and
		// all drops
		game().batch.begin();
		// game().font.draw(game().batch, "Score: " + score, 0, deviceHeight);
		game().batch.draw(francoisImage, player.x, player.y);
		for (Rectangle raindrop : raindrops) {
			game().batch.draw(dropImage, raindrop.x, raindrop.y, weightW, weightH);
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

		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > 2000000000)
			spawnRaindrop();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we increase the
		// value our drops counter and add a sound effect.
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 100 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + weightH < 0)
				iter.remove();
			/*if (raindrop.overlaps(player)) {
				score++;
				dropSound.play();
				iter.remove();
			}*/
			if (raindrop.overlaps(player)) {
				gameOver();
			}
		}
	}
	
	public void gameOver() {
		preferences().putInteger("lastscore", score);
		int highscore = preferences().getInteger("highscore");
		if (highscore == 0 || score > highscore) {
			preferences().putInteger("highscore", score);
		}
		preferences().flush();
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
		dropImage.dispose();
		francoisImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}