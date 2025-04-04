package com.example.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MainGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bucketTexture;
	Texture dropTexture;
	Texture backgroundTexture;

	// Balde e gotas
	Bucket bucket;
	Array<Drop> drops;
	long lastDropTime;
	boolean isGameOver;

	// Pontuação
	int score;
	int highScore;
	BitmapFont font;

	// Preferências para salvar a pontuação máxima
	Preferences preferences;

	@Override
	public void create() {
		batch = new SpriteBatch();
		bucketTexture = new Texture("bucket.png");
		dropTexture = new Texture("drop.png");
		backgroundTexture = new Texture("background.png");
		bucket = new Bucket(bucketTexture);
		drops = new Array<>();
		lastDropTime = TimeUtils.nanoTime();
		isGameOver = false;

		// Configuração de pontuação
		score = 0;
		font = new BitmapFont();
		font.getData().setScale(2.0f);  // Aumenta a fonte para o dobro do tamanho

		// Carrega a pontuação máxima salva
		preferences = Gdx.app.getPreferences("MyPreferences");
		highScore = preferences.getInteger("highScore", 0); // Padrão: 0

		// Adiciona uma gota inicial
		spawnDrop();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Verifica se o jogo está no estado de Game Over
		if (isGameOver) {
			batch.begin();
			batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			font.draw(batch, "Game Over! Score: " + score, 50, Gdx.graphics.getHeight() - 50);
			font.draw(batch, "High Score: " + highScore, 50, Gdx.graphics.getHeight() - 100);
			batch.end();

			// Detecta clique para reiniciar o jogo
			if (Gdx.input.isTouched()) {
				restartGame();  // Reinicia o jogo ao clicar na tela
			}
			return;
		}

		// Atualiza a posição do balde
		bucket.update();

		// Cria novas gotas periodicamente
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			spawnDrop();
		}

		// Renderiza o fundo e o jogo
		batch.begin();
		batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bucket.draw(batch);

		// Atualiza e desenha cada gota
		for (Iterator<Drop> iter = drops.iterator(); iter.hasNext();) {
			Drop drop = iter.next();
			drop.update();

			if (drop.overlaps(bucket)) {
				iter.remove();  // Remove a gota se capturada
				score++;  // Incrementa a pontuação
			} else if (drop.outOfScreen()) {
				// Se a gota sair da tela e não for capturada, fim de jogo
				isGameOver = true;
				checkHighScore();  // Verifica e salva a pontuação máxima
				break;
			}
			drop.draw(batch);
		}

		// Desenha a pontuação na tela
		font.draw(batch, "Score: " + score, 50, Gdx.graphics.getHeight() - 20);
		font.draw(batch, "High Score: " + highScore, 50, Gdx.graphics.getHeight() - 70); // Ajusta posição

		batch.end();
	}

	private void spawnDrop() {
		Drop drop = new Drop(dropTexture);
		drops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void checkHighScore() {
		// Atualiza e salva a pontuação máxima, se a pontuação atual for maior
		if (score > highScore) {
			highScore = score;
			preferences.putInteger("highScore", highScore);
			preferences.flush();  // Salva as preferências
		}
	}

	private void restartGame() {
		score = 0;
		drops.clear();  // Limpa as gotas existentes
		isGameOver = false;  // Reseta o estado do jogo
		spawnDrop();  // Adiciona uma gota inicial novamente
	}

	@Override
	public void dispose() {
		batch.dispose();
		bucketTexture.dispose();
		dropTexture.dispose();
		backgroundTexture.dispose();
		font.dispose();
	}
}

