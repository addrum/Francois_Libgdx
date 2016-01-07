package com.francois.main.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

public class UserPreferences {

    public static String getApp_id() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("ad_id");
    }

    public static String getScore_leaderboard() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("score_leaderboard");
    }

    public static String getTime_leaderboard() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("time_leaderboard");
    }

    public static String getLoser_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("loser_achievement");
    }

    public static String getGive_up_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("give_up_achievement");
    }

    public static String getWarming_up_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("warming_up_achievement");
    }

    public static String getNatural_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("natural_achievement");
    }

    public static String getBeat_mike_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("beat_mike_achievement");
    }

    public static String getMy_hero_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("my_hero_achievement");
    }

    public static String getEvader_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("evader_achievement");
    }

    public static String getNovice_evader_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("novice_evader_achievement");
    }

    public static String getScore_means_nothing_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("score_means_nothing_achievement");
    }

    public static String getHow_did_you_do_that_achievement() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("how_did_you_do_that_achievement");
    }

    public static String getAd_ID() {
        FileHandle file = Gdx.files.internal("data/strings");
        I18NBundle strings = I18NBundle.createBundle(file);
        return strings.get("ad_id");
    }
}
