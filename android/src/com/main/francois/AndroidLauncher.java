package com.main.francois;


import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.francois.main.core.ActionResolver;
import com.francois.main.core.Francois;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import com.badlogic.gdx.Preferences;

public class AndroidLauncher extends AndroidApplication implements GameHelperListener, ActionResolver {

    private GameHelper gameHelper;

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
    }

    @Override
    public boolean getSignedInGPGS() {
        return gameHelper.isSignedIn();
    }

    @Override
    public void getUserHighScoreGPGS(String score_leaderboard) {
        if (getSignedInGPGS()) {
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), score_leaderboard, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                    String highscore = Long.toString(scoreResult.getScore().getRawScore());
                    Preferences prefs = Gdx.app.getPreferences("prefs");
                    prefs.putString("highscore", highscore);
                    prefs.flush();
                }
            });
        } else {
            loginGPGS();
            getUserHighScoreGPGS(score_leaderboard);
        }
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
    }

    @Override
    public void onSignInSucceeded() {
    }
}
