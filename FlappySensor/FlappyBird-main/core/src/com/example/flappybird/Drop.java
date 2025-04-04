package com.example.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class Drop {
    Texture texture;
    Rectangle bounds;
    float x, y;
    float speed = 600;

    public Drop(Texture texture) {
        this.texture = texture;
        this.x = MathUtils.random(0, Gdx.graphics.getWidth() - texture.getWidth());
        this.y = Gdx.graphics.getHeight();
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update() {
        y -= speed * Gdx.graphics.getDeltaTime();
        bounds.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean overlaps(Bucket bucket) {
        return bounds.overlaps(bucket.getBounds());
    }

    public boolean outOfScreen() {
        return y + texture.getHeight() < 0;
    }
}
