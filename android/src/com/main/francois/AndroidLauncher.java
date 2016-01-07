package com.main.francois;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.francois.main.core.ActionResolver;
import com.francois.main.core.Francois;
import com.francois.main.core.MainMenuScreen;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements GameHelperListener, ActionResolver {
    protected AdView adView;
    protected View gameView;

    private String AD_UNIT_ID;

    // customs
    private GameHelper gameHelper;
    private MainMenuScreen mainMenuScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        initialize(new Francois(this), config);

        if (gameHelper == null) {
            gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
            gameHelper.enableDebugLog(true);
        }
        gameHelper.setup(this);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        AdView admobView = createAdView();
        layout.addView(admobView);
        View gameView = createGameView(cfg);
        layout.addView(gameView);

        setContentView(layout);
        startAdvertising(admobView);
    }

    private AdView createAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        adView.setId(R.id.adViewId); // this is an arbitrary id, allows for relative positioning in createGameView()
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        adView.setLayoutParams(params);
        adView.setBackgroundColor(Color.BLACK);
        return adView;
    }

    private View createGameView(AndroidApplicationConfiguration cfg) {
        gameView = initializeForView(new Francois(this), cfg);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, adView.getId());
        gameView.setLayoutParams(params);
        return gameView;
    }

    private void startAdvertising(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        gameHelper.onActivityResult(request, response, data);
        if (response == RESULT_CANCELED) {
            Preferences prefs = Gdx.app.getPreferences("prefs");
            prefs.putBoolean("explicitSignOut", true);
            gameHelper.setConnectOnStart(false);
            gameHelper.setMaxAutoSignInAttempts(0);
        }
    }

    @Override
    public boolean getSignedInGPGS() {
        return gameHelper.isSignedIn();
    }

    @Override
    public void getUserHighScoreGPGS(String score_leaderboard) {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), score_leaderboard, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                try {
                    if (scoreResult != null) {
                        Preferences prefs = Gdx.app.getPreferences("prefs");
                        prefs.putString("highscore", Long.toString(scoreResult.getScore().getRawScore()));
                        prefs.flush();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void loginGPGS() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        } catch (final Exception ex) {
        }
    }

    @Override
    public void logoutGPGS() {
        gameHelper.signOut();
        Preferences prefs = Gdx.app.getPreferences("prefs");
        prefs.putBoolean("explicitSignOut", true);
        gameHelper.setConnectOnStart(false);
        gameHelper.setMaxAutoSignInAttempts(0);
    }

    @Override
    public void submitScoreGPGS(int score, String score_leaderboard) {
        Games.Leaderboards.submitScore(gameHelper.getApiClient(), score_leaderboard, score);
    }

    @Override
    public void submitTimeGPGS(int time, String time_leaderboard) {
        Games.Leaderboards.submitScore(gameHelper.getApiClient(), time_leaderboard, time);
    }

    @Override
    public void unlockAchievementGPGS(String achievementId) {
        Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
    }

    @Override
    public void incrementAchievementGPGS(String achievementId, int value) {
        Games.Achievements.increment(gameHelper.getApiClient(), achievementId, value);
    }

    @Override
    public void getLeaderboardGPGS() {
        if (gameHelper.isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), 100);
        } else if (!gameHelper.isConnecting()) {
            loginGPGS();
        }
    }

    @Override
    public void getAchievementsGPGS() {
        if (gameHelper.isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), 101);
        } else if (!gameHelper.isConnecting()) {
            loginGPGS();
        }
    }

    @Override
    public void onSignInFailed() {
        if (mainMenuScreen != null)
            mainMenuScreen.setGPGSButtonStyle(false);
    }

    @Override
    public void onSignInSucceeded() {
        if (mainMenuScreen != null) {
            mainMenuScreen.setGPGSButtonStyle(true);
            mainMenuScreen.setHighscoreValueLabel(mainMenuScreen.getHighscorePreferences());
        }
        Preferences prefs = Gdx.app.getPreferences("prefs");
        prefs.putBoolean("explicitSignOut", false);
        gameHelper.setConnectOnStart(true);
    }

    public void setMainMenuScreen(MainMenuScreen mainMenuScreen) {
        this.mainMenuScreen = mainMenuScreen;
    }
}
