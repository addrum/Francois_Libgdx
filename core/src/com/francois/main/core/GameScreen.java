package com.francois.main.core;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
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
	private Texture weightImage, francoisImage, scoreImage100, scoreImage500, scoreImage1000, backgroundImage;
	private Sound dropSound;
	private Music rainMusic;
    private	OrthographicCamera camera;
	private Rectangle player;
    private Array<GameObject> entities;
    private Container centreContainer;

	public GameScreen(Francois game) {
		super(game);

		// set finals
		defaultW = (int) (getDeviceWidth() / 12.5);
		defaultH = (int) (getDeviceWidth() / 12.5);

		// load the images
        backgroundImage = new Texture((Gdx.files.internal("images/stripe.png")));
		weightImage = new Texture(Gdx.files.internal("images/weight_l.png"));
		scoreImage100 = new Texture(Gdx.files.internal("images/score_100.png"));
        scoreImage500 = new Texture(Gdx.files.internal("images/score_500.png"));
        scoreImage1000 = new Texture(Gdx.files.internal("images/score_1000.png"));
		francoisImage = new Texture(Gdx.files.internal("images/francois.png"));

		francoisW = getDeviceWidth() / 7.74f;
		// old height / old width * new width = new height
		francoisH = (510f / 330f) * francoisW;

		// load the sound effect and the background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rain.mp3"));
		rainMusic.setLooping(true);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, getDeviceWidth(), getDeviceHeight());

		setStage();

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

        entities = new Array<GameObject>();
	}

	private void setStage() {
		stage = new Stage(new StretchViewport(getDeviceWidth(), getDeviceHeight())) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    System.out.println("back button pressed");
                    game().setScreen(new MainMenuScreen(game()));
                    if (game().getAdsController().isNetworkAvailable())
                        game().getAdsController().showBannerAd();
                    dispose();
                }
                return super.keyDown(keyCode);
            }
        };
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
            // draw background
            game().batch.draw(backgroundImage, 0, 0, getDeviceWidth(), getDeviceHeight());

			if (drawCentrally) {
				game().batch.draw(francoisImage, player.x - defaultW / 2, player.y - defaultH / 2, francoisW, francoisH);
			} else {
				game().batch.draw(francoisImage, player.x, player.y - defaultH / 2);
			}
            for (GameObject entity : entities) {
                if (entity instanceof Score) {
                    if (((Score) entity).getScoreValue() == 100) {
                        game().batch.draw(scoreImage100, entity.x - entity.width / 2, entity.y - entity.width / 2, entity.width, entity.height);
                    } else if (((Score) entity).getScoreValue() == 500) {
                        game().batch.draw(scoreImage500, entity.x - entity.width / 2, entity.y - entity.width / 2, entity.width, entity.height);
                    } else if (((Score) entity).getScoreValue() == 1000) {
                        game().batch.draw(scoreImage1000, entity.x - entity.width / 2, entity.y - entity.width / 2, entity.width, entity.height);
                    }
                } else if (entity instanceof Weight) {
                    game().batch.draw(weightImage, entity.x - entity.width / 2, entity.y - entity.width / 2, entity.width, entity.height);
                }
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

        if (start) {
            // update time value
            timer+=delta;
            if (timer >= 1f) {
                time++;
                timer-=1f;
            }

            // check if we need to create a new weight
            if (TimeUtils.millis() - weightLastDropTime > 1000) {
                double chance = Math.random();
                if (chance > 0 && chance <= 0.5) {
                    entities.add(new Weight(MathUtils.random(0, getDeviceWidth() - defaultW), getDeviceHeight(), defaultW, defaultH, 300, weightImage));
                } else if (chance > 0.5 && chance <= 0.85) {
                    entities.add(new Weight(MathUtils.random(0, getDeviceWidth() - defaultW * 1.5f), getDeviceHeight(), defaultW * 1.5f, defaultH * 1.5f, 400, weightImage));
                } else if (chance > 0.85) {
                    entities.add(new Weight(MathUtils.random(0, getDeviceWidth() - defaultW * 2f), getDeviceHeight(), defaultW * 2f, defaultH * 2f, 700, weightImage));
                }
                weightLastDropTime = TimeUtils.millis();
            }

            if (TimeUtils.millis() - scoreLastDropTime > 10000) {
                double chance = Math.random();
                if (chance > 0 && chance <= 0.7) {
                    entities.add(new Score(MathUtils.random(0, getDeviceWidth() - defaultW), getDeviceHeight(), defaultW, defaultH, 100, scoreImage100, 100));
                } else if (chance > 0.7 && chance <= 0.9) {
                    entities.add(new Score(MathUtils.random(0, getDeviceWidth() - defaultW), getDeviceHeight(), defaultW, defaultH, 300, scoreImage500, 500));
                } else if (chance > 0.9) {
                    entities.add(new Score(MathUtils.random(0, getDeviceWidth() - defaultW), getDeviceHeight(), defaultW, defaultH, 800, scoreImage1000, 1000));
                }
                scoreLastDropTime = TimeUtils.millis();
            }

            // move the weights, remove any that are beneath the bottom edge of
            // the screen or that hit the player. In the later case we increase the
            // value our drops counter and add a sound effect.
            Iterator<GameObject> iter = entities.iterator();
            while (iter.hasNext()) {
                GameObject entity = iter.next();
                entity.y -= entity.speed * Gdx.graphics.getDeltaTime();
                if (entity.y + entity.height < 0)
                    iter.remove();
                if (entity instanceof Weight) {
                    if (entity.overlaps(player)) {
                        gameOver();
                    }
                } else if (entity instanceof Score) {
                    if (entity.overlaps(player)) {
                        iter.remove();

                        if (((Score) entity).getScoreValue() == 100) {
                            score += 100;
                        } else if (((Score) entity).getScoreValue() == 500) {
                            score += 500;
                        } else if (((Score) entity).getScoreValue() == 1000) {
                            score += 1000;
                        }
                        //dropSound.play();
                    }
                }
            }

            scoreLabel.setText(Integer.toString(score));
            timeLabel.setText(Integer.toString(time));
        }

		stage.act(delta);
		stage.draw();
	}

	public void gameOver() {
		// set local getPreferences for displaying score on main menu
		getPreferences().putInteger("lastscore", score);

        // update preferences with latest high score from GPG if needed
        if (game().getActionResolver().getSignedInGPGS()) {
            game().getActionResolver().getUserHighScoreGPGS(PropertiesRetriever.getScore_leaderboard());
        }

		long highscore = Long.parseLong(getPreferences().getString("highscore"));
		if (score > highscore) {
			getPreferences().putString("highscore", Long.toString(score));
		}

		getPreferences().flush();

		// update gpgs leaderboard
		if (game().getActionResolver().getSignedInGPGS()) {
			game().getActionResolver().submitScoreGPGS(score, PropertiesRetriever.getScore_leaderboard());
            game().getActionResolver().incrementAchievementGPGS(PropertiesRetriever.getLoser_achievement(), 1);
            if (score == 0)
                game().getActionResolver().incrementAchievementGPGS(PropertiesRetriever.getGive_up_achievement(), 1);
            if (score >= 5)
                game().getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getWarming_up_achievement());
            if (score >= 10)
                game.getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getNatural_achievement());
            if (score >= 27)
                game().getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getBeat_mike_achievement());
            if (score >= 100)
                game().getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getMy_hero_achievement());
            if (time >= 30)
                game.getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getNovice_evader_achievement());
            if (time >= 60) {
                game().getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getEvader_achievement());
                if (score == 0)
                    game().getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getScore_means_nothing_achievement());
            }
            if (time >= 120)
                game().getActionResolver().unlockAchievementGPGS(PropertiesRetriever.getHow_did_you_do_that_achievement());
        }

        ScreenManager.setScreen(new MainMenuScreen(game));
        if (game().getAdsController().isNetworkAvailable())
            game().getAdsController().showBannerAd();
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