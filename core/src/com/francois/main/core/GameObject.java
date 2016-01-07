package com.francois.main.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject extends Rectangle {
    protected float speed;
    protected Texture image;

    public GameObject(float x, float y, float width, float height, float speed, Texture image) {
        super(x, y, width, height);
        this.speed = speed;
        this.image = image;
    }

    protected float getSpeed() { return speed; }
}
