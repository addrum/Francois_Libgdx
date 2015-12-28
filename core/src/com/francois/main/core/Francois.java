package com.francois.main.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Francois extends Game {
    public SpriteBatch batch;
    public ActionResolver actionResolver;

    public Francois(ActionResolver actionResolver) {
        this.actionResolver = actionResolver;
    }

    public void create() {
    	
        batch = new SpriteBatch();
        
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