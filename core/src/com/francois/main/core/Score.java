package com.francois.main.core;

import com.badlogic.gdx.graphics.Texture;

public class Score extends GameObject {

    private float scoreValue;

    public Score(float x, float y, float width, float height, float speed, Texture image, float scoreValue) {
        super(x, y, width, height, speed, image);
        this.scoreValue = scoreValue;
    }

    public float getScoreValue() { return scoreValue; }
}
