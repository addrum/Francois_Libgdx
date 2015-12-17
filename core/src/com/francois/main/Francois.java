package com.francois.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Francois extends Game {
    public SpriteBatch batch;

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

}