package com.francois.main.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Francois extends Game {
    public SpriteBatch batch;
    private ActionResolver actionResolver;
    private AdsController adsController;

    public Francois(ActionResolver actionResolver, AdsController adsController) {
        this.actionResolver = actionResolver;
        this.adsController = adsController;
    }

    public void create() {
    	
        batch = new SpriteBatch();
        adsController.showBannerAd();
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
    }

    public ActionResolver actionResolver() {
        return actionResolver;
    }

}