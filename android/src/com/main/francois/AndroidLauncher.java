package com.main.francois;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.francois.main.core.ActionResolver;
import com.francois.main.core.AdsController;
import com.francois.main.core.Francois;
import com.francois.main.core.MainMenuScreen;
import com.francois.main.core.UserPreferences;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements GameHelperListener, ActionResolver, AdsController {
    protected AdView bannerAd;
    protected View gameView;

    private String AD_UNIT_ID;

    // customs
    private GameHelper gameHelper;
    private MainMenuScreen mainMenuScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        initialize(new Francois(this, this), config);

        if (gameHelper == null) {
            gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
            gameHelper.enableDebugLog(true);
        }
        gameHelper.setup(this);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;

        /*RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);*/

        AD_UNIT_ID = UserPreferences.getAd_ID();

        /*AdView admobView = createAdView();
        layout.addView(admobView);
        View gameView = createGameView(cfg);
        layout.addView(gameView);

        setContentView(layout);
        startAdvertising(admobView);*/
        View gameView = initializeForView(new Francois(this, this), config);
        setupAds();

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(bannerAd, params);

        setContentView(layout);
    }

    private AdView createAdView() {
        bannerAd = new AdView(this);
        bannerAd.setAdSize(AdSize.SMART_BANNER);
        bannerAd.setAdUnitId(AD_UNIT_ID);
        bannerAd.setId(R.id.adViewId); // this is an arbitrary id, allows for relative positioning in createGameView()
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        bannerAd.setLayoutParams(params);
        bannerAd.setBackgroundColor(Color.BLACK);
        return bannerAd;
    }

    public void setupAds() {
        bannerAd = new AdView(this);
        bannerAd.setVisibility(View.INVISIBLE);
        bannerAd.setBackgroundColor(0xff000000); // black
        bannerAd.setAdUnitId(AD_UNIT_ID);
        bannerAd.setAdSize(AdSize.SMART_BANNER);
    }

    private View createGameView(AndroidApplicationConfiguration cfg) {
        gameView = initializeForView(new Francois(this, this), cfg);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, bannerAd.getId());
        gameView.setLayoutParams(params);
        return gameView;
    }

    private void startAdvertising(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void showBannerAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(bannerAd.getAdSize());
                System.out.println(bannerAd.getAdUnitId());

                bannerAd.setVisibility(View.VISIBLE);
                AdRequest.Builder builder = new AdRequest.Builder();
                AdRequest ad = builder.build();
                bannerAd.loadAd(ad);
            }
        });
    }

    @Override
    public void hideBannerAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerAd.setVisibility(View.INVISIBLE);
            }
        });
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
