package com.example.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameover;
	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	int score = 0;
	int maxScore = 0;
	int scoringTube = 0;
	BitmapFont font;
	BitmapFont difficultyFont;
	BitmapFont maxScoreFont;
	Preferences preferences;

	int gameState = 0;
	float gravity;
	float tubeVelocity;
	float gap;

	final float GRAVITY_EASY = 2;
	final float GRAVITY_MEDIUM = 3;
	final float GRAVITY_HARD = 4;

	final float TUBE_VELOCITY_EASY = 4;
	final float TUBE_VELOCITY_MEDIUM = 6;
	final float TUBE_VELOCITY_HARD = 8;

	final float GAP_EASY = 600;
	final float GAP_MEDIUM = 500;
	final float GAP_HARD = 400;

	Texture topTube;
	Texture bottomTube;
	Random randomGenerator;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	@Override
	public void create() {
		batch = new SpriteBatch();
		background = new Texture("background.png");
		gameover = new Texture("gameover.png");
		birdCircle = new Circle();

		// Fonte para pontuação
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		// Fonte para a seleção de dificuldade
		difficultyFont = new BitmapFont();
		difficultyFont.setColor(Color.YELLOW);
		difficultyFont.getData().setScale(5);

		// Fonte para pontuação máxima
		maxScoreFont = new BitmapFont();
		maxScoreFont.setColor(Color.GREEN);
		maxScoreFont.getData().setScale(5);

		// Inicializa o sistema de preferências para salvar a pontuação
		preferences = Gdx.app.getPreferences("FlappyBirdPrefs");
		maxScore = preferences.getInteger("maxScore", 0);  // Carrega a pontuação máxima

		birds = new Texture[2];
		birds[0] = new Texture("flappybirdup.png");
		birds[1] = new Texture("flappybirddown.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();
	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 0) {  // Tela de Seleção de Dificuldade
			difficultyFont.draw(batch, "Escolha a Dificuldade:", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() - 200);
			difficultyFont.draw(batch, "Facil", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 50);
			difficultyFont.draw(batch, "Medio", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 50);
			difficultyFont.draw(batch, "Dificil", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 150);

			// Exibe a pontuação máxima
			maxScoreFont.draw(batch, "Recorde: " + maxScore, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() - 400);

			if (Gdx.input.justTouched()) {
				float touchX = Gdx.input.getX();
				float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();  // Inverte coordenada Y

				if (touchX > Gdx.graphics.getWidth() / 2 - 100 && touchX < Gdx.graphics.getWidth() / 2 + 100 &&
						touchY > Gdx.graphics.getHeight() / 2 && touchY < Gdx.graphics.getHeight() / 2 + 100) {
					gravity = GRAVITY_EASY;
					tubeVelocity = TUBE_VELOCITY_EASY;
					gap = GAP_EASY;
					gameState = 1;
				} else if (touchX > Gdx.graphics.getWidth() / 2 - 100 && touchX < Gdx.graphics.getWidth() / 2 + 100 &&
						touchY > Gdx.graphics.getHeight() / 2 - 100 && touchY < Gdx.graphics.getHeight() / 2) {
					gravity = GRAVITY_MEDIUM;
					tubeVelocity = TUBE_VELOCITY_MEDIUM;
					gap = GAP_MEDIUM;
					gameState = 1;
				} else if (touchX > Gdx.graphics.getWidth() / 2 - 100 && touchX < Gdx.graphics.getWidth() / 2 + 100 &&
						touchY > Gdx.graphics.getHeight() / 2 - 200 && touchY < Gdx.graphics.getHeight() / 2 - 100) {
					gravity = GRAVITY_HARD;
					tubeVelocity = TUBE_VELOCITY_HARD;
					gap = GAP_HARD;
					gameState = 1;
				}
			}

		} else if (gameState == 1) {
			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;
				scoringTube = (scoringTube + 1) % numberOfTubes;
			}

			if (Gdx.input.justTouched()) {
				velocity = -30;
			}

			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
				} else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight());
				topTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2, topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight(), bottomTube.getWidth(), bottomTube.getHeight());
			}

			if (birdY > 0 && birdY < Gdx.graphics.getHeight() - birds[flapState].getHeight()) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}

			if (flapState == 0) {
				flapState = 1;
			} else {
				flapState = 0;
			}
			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

			birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

			for (int i = 0; i < numberOfTubes; i++) {
				if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
					gameState = 2;
				}
			}
		} else if (gameState == 2) {
			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

			// Atualiza a pontuação máxima, se necessário
			if (score > maxScore) {
				maxScore = score;
				preferences.putInteger("maxScore", maxScore);
				preferences.flush();
			}

			if (Gdx.input.justTouched()) {
				gameState = 0;
				score = 0;
				scoringTube = 0;
				velocity = 0;
				startGame();
			}
		}

		// Desenha a pontuação atual durante o jogo
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();
	}
}
