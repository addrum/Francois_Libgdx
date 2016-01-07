package com.francois.main.core;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject extends Rectangle {
    protected float speed;

    public GameObject(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    protected float getSpeed() { return speed; }
}
