package com.yujinwunz.spazpong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by yujinwunz on 10/03/2017.
 */

public class SpazPongGame extends ScreenAdapter {

	private static final float MAX_DELTA = 0.0002f;
	private static final float PADDLE_MAX_SPEED = 400f;
	private static final float PADDLE_EDGE_NORMAL = (float)Math.PI/4;
	private static final float BALL_SIZE = 30f;
	private static final int PADDLE_INPUT_MARGIN = 300;
	private static final int PADDLE_SIZE = 130;
	private static final int PADDLE_THICKNESS = 30;

	private static final int PLAYER_1_INDEX = 0;
	private static final int PLAYER_2_INDEX = 1;

	private Ball ball;
	private Paddle[] paddles;
	private Field field;
	private SpazPong game;
	private boolean servingRight;
	private boolean paused;

	private int score[];

	public SpazPongGame(SpazPong game, SpazPongOptions options) {
		this.game = game;
		this.ball = new Ball(this, 400, 240, 0, BALL_SIZE);
		this.paddles = new Paddle[2];
		this.paddles[0] = new Paddle(true, options.singlePlayer, 30, 240, this);
		this.paddles[1] = new Paddle(false, false, 770, 240, this);
		this.field = new Field(10, 10, 780, 460);
		this.score = new int[2];
		servingRight = true;
		reset_round(servingRight);
	}

	public void reset_round(boolean servingRight) {
		this.ball = new Ball(this, 400, 240, servingRight ? (float)Math.PI/2 : -(float)Math.PI/2, BALL_SIZE);
		this.paddles[0].y = 240;
		this.paddles[1].y = 240;
	}

	public void progress(float delta) {
		if (delta > MAX_DELTA) {
			for (float consumed = 0; consumed < delta; consumed += MAX_DELTA) {
				progress(Math.min(MAX_DELTA, delta - consumed));
			}
		}

		for (Paddle p : paddles) {
			p.tick(delta);
		}
		this.field.tick(delta);
		this.ball.tick(delta);

		// Collisions
		for (Paddle p : paddles) {
			// Collide paddles with the field
			p.y = Math.min(p.y, field.y + field.height - p.size / 2);
			p.y = Math.max(p.y, field.y + p.size / 2);
			// Collide ball with paddle
			if (p.collidesWithBall(ball)) {
				ball.bounce(p.getNormal(ball.y));
			}
		}
		if (field.collidesWithBall(ball)) {
			ball.bounce(field.getNormal(ball.y));
		}

		// Winning/losing conditions
		if (ball.x - ball.size/2 < field.x) {
			// Player 2 (right) wins
			score[PLAYER_2_INDEX]++;
			scoreMenu("Player 2 (right) wins");
		} else if (ball.x + ball.size/2 > field.x + field.width) {
			// Player 1 (left) wins
			score[PLAYER_1_INDEX]++;
			scoreMenu("Player 1 (left) wins");
		}
	}

	private void pauseGame() {
		this.paused = true;
	}

	private void resumeGame() {
		this.paused = false;
	}


	public void scoreMenu(String title) {
		pauseGame();
		servingRight = !servingRight;
		reset_round(servingRight);

		final SpazPongGame me = this;
		game.setScreen(new Menu(title + "\n" + "Score: " + score[0] + "    " + score[1], game,
				new Menu.MenuItem("Continue", new Callable() {
					@Override
					public Object call() throws Exception {
						game.setScreen(me);
						me.resumeGame();
						return null;
					}
				}, true),
				new Menu.MenuItem("Quit", new Callable() {
					@Override
					public Object call() throws Exception {
						game.quit();
						return null;
					}
				})
		));
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.4f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!this.paused) {
			progress(delta);
		}

		field.render(game.shapeRenderer, game.spriteBatch);

		for (Paddle p : paddles) {
			p.render(game.shapeRenderer, game.spriteBatch);
		}
		ball.render(game.shapeRenderer, game.spriteBatch);
	}

	static class SpazPongOptions {
		public SpazPongOptions(boolean singlePlayer) {
			this.singlePlayer = singlePlayer;
		}

		boolean singlePlayer;
	}

	static class Ball implements GameObject {
		Random random = new Random();
		float x, y, a, v = 400;
		int bounces = 0;
		float size;
		TextureRegion ball;

		public Ball(SpazPongGame game, float x, float y, float a, float size) {
			this.x = x;
			this.y = y;
			this.a = a;
			Skin skin = new Skin();
			skin.addRegions(game.game.assetManager.get(Assets.SPRITE_ATLAS, TextureAtlas.class));
			ball = skin.getRegion(Assets.Sprites.BALL);
			this.size = ball.getRegionHeight();
		}
		public void render(ShapeRenderer renderer, SpriteBatch batch) {
			batch.begin();
			batch.draw(ball, x - ball.getRegionWidth()/2, y - ball.getRegionHeight()/2);
			batch.end();
		}
		public void tick(float delta) {
			x += Math.sin(a) * delta * v;
			y += Math.cos(a) * delta * v;
			// Spaziness go.
			//a += (random.nextFloat() * Math.PI - Math.PI / 2) * bounces * delta / 10;
			while (a > Math.PI * 2) {
				a -= Math.PI * 2;
			}
			while (a < 0) {
				a += Math.PI * 2;
			}
		}
		public void bounce(float normal) {
			float normalDiff = (float)((normal - a + Math.PI * 100) % (Math.PI*2));
			if (normalDiff > Math.PI/2 && normalDiff < Math.PI/2*3) {

				a = (float)(normal + Math.PI + normalDiff);
				this.bounces += 1;

				this.v += 8;
			} // Ignore reflection when ball is moving away from normal already
		}
	}

	static class Paddle implements GameObject {
		boolean isAi;
		float x, y, size = PADDLE_SIZE, thickness = PADDLE_THICKNESS;
		SpazPongGame game;
		boolean faceRight;
		float hittingTimeRemaining = 0;
		float blinkingTimeRemaining = 0;
		Skin skin;
		TextureRegion paddle, paddle_blink, paddle_hit;

		public void render(ShapeRenderer renderer, SpriteBatch batch) {
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
			batch.begin();
			float xpos = x - paddle.getRegionWidth() / 2;
			float ypos = y - paddle.getRegionHeight() / 2;
			if (this.hittingTimeRemaining > 0) {
				batch.draw(paddle_hit, xpos, ypos);
			} else if (this.blinkingTimeRemaining > 0) {
				batch.draw(paddle_blink, xpos, ypos);
			} else {
				batch.draw(paddle, xpos, ypos);
			}
			batch.end();
		}
		public Paddle(boolean faceRight, boolean isAi, float x, float y, SpazPongGame game) {
			this.isAi = isAi;
			this.game = game;
			this.x = x;
			this.y = y;
			this.faceRight = faceRight;
			this.skin = new Skin();
			skin.addRegions(game.game.assetManager.get(Assets.SPRITE_ATLAS, TextureAtlas.class));
			if (!faceRight) {
				paddle = skin.getRegion(Assets.Sprites.PADDLE_RIGHT);
				paddle_blink = skin.getRegion(Assets.Sprites.PADDLE_RIGHT_BLINK);
				paddle_hit = skin.getRegion(Assets.Sprites.PADDLE_RIGHT_HIT);
			} else {
				paddle = skin.getRegion(Assets.Sprites.PADDLE_LEFT);
				paddle_blink = skin.getRegion(Assets.Sprites.PADDLE_LEFT_BLINK);
				paddle_hit = skin.getRegion(Assets.Sprites.PADDLE_LEFT_HIT);
			}
		}
		public void tick(float delta) {
			if (this.isAi) {
				// Extremely rudimentary AI
				this.y += Math.min(
						PADDLE_MAX_SPEED * delta,
						Math.max(
								-PADDLE_MAX_SPEED * delta,
								game.ball.y - this.y
						)
				);
			} else {
				// Accept the first tap within a 300px margin.
				// We'll try just infinite speed for players for now.
				for (int i = 0; i < 20; i++) {
					if (Gdx.input.isTouched(i)) {
						Vector3 input = game.game.camera.unproject(new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0));
						if (Math.abs(input.x - this.x) < PADDLE_INPUT_MARGIN) {
							this.y = input.y;
						}
					}
				}
			}

			blinkingTimeRemaining = Math.max(0, blinkingTimeRemaining - delta);
			hittingTimeRemaining = Math.max(0, hittingTimeRemaining - delta);
			if (random.nextFloat() * 2.0 < delta) {
				blinkingTimeRemaining = 0.2f;
			}
		}
		public float getNormal(float y) {
			if (faceRight) {
				return (float)(Math.PI/2 - (y - this.y) * PADDLE_EDGE_NORMAL * 2 / size);
			} else {
				return (float)(-Math.PI/2 + (y - this.y) * PADDLE_EDGE_NORMAL * 2 / size);
			}
		}
		public boolean collidesWithBall(Ball b) {
			boolean result = new Rectangle(
					x - thickness/2 - b.size/2,
					y - size/2 - b.size/2,
					thickness + b.size,
					size + b.size
			).contains(b.x, b.y);
			if (result) {
				hittingTimeRemaining = 0.2f;
			}
			return result;
		}
	}

	static class Field implements GameObject {
		float width, height, x, y;
		public Field(float x, float y, float width, float height) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
		}
		public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(new Color(0.4f, 0.1f, 0.15f, 1));
			shapeRenderer.rect(x, y, width, height);
			shapeRenderer.end();
		}
		public void tick(float delta) {
			// Do nothing
		}
		public boolean collidesWithBall(Ball b) {
			return b.y - b.size/2 < y || b.y + b.size/2 > y + height;
		}
		public float getNormal(float y) {
			if (y - this.y < this.y + height - y) {
				// Closer to top
				return 0;
			} else {
				return (float)Math.PI;
			}
		}
	}

	interface GameObject {
		void tick(float delta);
		void render(ShapeRenderer renderer, SpriteBatch batch);
	}
}
