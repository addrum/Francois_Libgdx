package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

public class UserPreferences {
    protected static String app_id, score_leaderboard, time_leaderboard, loser_achievement, give_up_achievement,
            warming_up_achievement, natural_achievement, beat_mike_achievement, my_hero_achievement,
            novice_evader_achievement, evader_achievement, score_means_nothing_achievement, how_did_you_do_that_achievement;

    public static String ad_id;

    public static void getStrings() {
        boolean exists = Gdx.files.internal("data/strings.properties").exists();
        if (exists) {
            FileHandle baseFileHandle = Gdx.files.internal("data/strings");
            I18NBundle strings = I18NBundle.createBundle(baseFileHandle);
            app_id = strings.get("app_id");
            score_leaderboard = strings.get("score_leaderboard");
            time_leaderboard = strings.get("time_leaderboard");
            loser_achievement = strings.get("loser_achievement");
            give_up_achievement = strings.get("give_up_achievement");
            warming_up_achievement = strings.get("warming_up_achievement");
            natural_achievement = strings.get("natural_achievement");
            beat_mike_achievement = strings.get("beat_mike_achievement");
            my_hero_achievement = strings.get("my_hero_achievement");
            novice_evader_achievement = strings.get("novice_evader_achievement");
            evader_achievement = strings.get("evader_achievement");
            score_means_nothing_achievement = strings.get("score_means_nothing_achievement");
            how_did_you_do_that_achievement = strings.get("how_did_you_do_that_achievement");
            ad_id = strings.get("ad_id");
        }
    }

    public static String getAd_ID() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("ad_id");
    }
}
