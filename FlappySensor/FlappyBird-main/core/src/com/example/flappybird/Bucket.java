package com.example.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bucket {
    Texture texture;
    Rectangle bounds;
    float x, y;
    float speed = 300;

    public Bucket(Texture texture) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.x = Gdx.graphics.getWidth() / 2 - texture.getWidth() / 2;
        this.y = 20;  // Posição inicial do balde
    }

    public void update() {
        // Usa o valor do sensor para atualizar a posição do balde
        float acelX = Gdx.input.getAccelerometerX();
        x -= acelX * speed * Gdx.graphics.getDeltaTime();

        // Limites para o balde não sair da tela
        if (x < 0) x = 0;
        if (x > Gdx.graphics.getWidth() - texture.getWidth()) x = Gdx.graphics.getWidth() - texture.getWidth();

        bounds.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
