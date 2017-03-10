package com.yujinwunz.spazpong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by yujinwunz on 10/03/2017.
 */

public class SpazPongGame extends ScreenAdapter {

	private static final float MAX_DELTA = 0.02f;
	private static final float PADDLE_MAX_SPEED = 800f;
	private static final float PADDLE_EDGE_NORMAL = (float)Math.PI/2;
	private static final float BALL_SIZE = 40f;
	private static final int PADDLE_INPUT_MARGIN = 300;
	private static final int PADDLE_SIZE = 80;
	private static final int PADDLE_THICKNESS = 20;

	private Ball ball;
	private Paddle[] paddles;
	private Field field;
	private SpazPong game;

	public SpazPongGame(SpazPong game, SpazPongOptions options) {
		this.ball = new Ball(400, 240, BALL_SIZE);
		this.paddles = new Paddle[2];
		this.paddles[0] = new Paddle(true, options.singlePlayer, 30, 240, this);
		this.paddles[1] = new Paddle(false, false, 30, 240, this);
		this.field = new Field(10, 10, 780, 460);
		this.game = game;
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
		} else if (ball.x + ball.size/2 > field.x + field.width) {
			// Player 1 (left) wins
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.7f, 0.4f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		progress(delta);

		field.render(game.shapeRenderer);
		for (Paddle p : paddles) {
			p.render(game.shapeRenderer);
		}
		ball.render(game.shapeRenderer);
	}

	static class SpazPongOptions {
		public SpazPongOptions(boolean singlePlayer) {
			this.singlePlayer = singlePlayer;
		}

		boolean singlePlayer;
	}

	static class Ball implements GameObject {
		Random random = new Random();
		float x, y, a, v = 600;
		int bounces = 0;
		float size;
		public Ball(float x, float y, float size) {
			this.x = x;
			this.y = y;
			this.size = size;
		}
		public void render(ShapeRenderer renderer) {
			renderer.begin(ShapeRenderer.ShapeType.Filled);
			renderer.setColor(new Color(0.4f, 0.1f, 0.1f, 1));
			renderer.circle(x, y, size);
			renderer.end();
		}
		public void tick(float delta) {
			x += Math.sin(a) * delta;
			y += Math.cos(a) * delta;
			// Spaziness go.
			a += Math.abs(random.nextFloat()) % Math.PI * bounces * delta;
			while (a > Math.PI * 2) {
				a -= Math.PI * 2;
			}
			while (a < 0) {
				a += Math.PI * 2;
			}
		}
		public void bounce(float normal) {
			if (Math.abs((normal - a + Math.PI * 100) % (Math.PI*2)) > Math.PI/2) {
				float normalDiff = (float)((normal - a + Math.PI * 100) % (Math.PI*2));
				a = (float)(normal + Math.PI + normalDiff);
				this.bounces += 1;

				this.v += 20;
			} // Ignore reflection when ball is moving away from normal already
		}
	}

	static class Paddle implements GameObject {
		boolean isAi;
		float x, y, size = PADDLE_SIZE, thickness = PADDLE_THICKNESS;
		SpazPongGame game;
		boolean faceRight;
		public void render(ShapeRenderer renderer) {
			renderer.begin(ShapeRenderer.ShapeType.Filled);
			renderer.setColor(new Color(0.2f, 0.05f, 0.05f, 1));
			renderer.rect(x - thickness/2, y - size/2, thickness, size);
			renderer.end();
		}
		public Paddle(boolean faceRight, boolean isAi, float x, float y, SpazPongGame game) {
			this.isAi = true;
			this.game = game;
			this.x = x;
			this.y = y;
			this.faceRight = faceRight;
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
		}
		public float getNormal(float y) {
			if (faceRight) {
				return (float)(Math.PI/2 - (y - this.y) * PADDLE_EDGE_NORMAL * 2 / size);
			} else {
				return (float)(-Math.PI/2 + (y - this.y) * PADDLE_EDGE_NORMAL * 2 / size);
			}
		}
		public boolean collidesWithBall(Ball b) {
			return new Rectangle(
					x - thickness/2 - b.size/2,
					y - size/2 - b.size/2,
					thickness + b.size,
					size + b.size
			).contains(b.x, b.y);
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
		public void render(ShapeRenderer shapeRenderer) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(new Color(0.4f, 0.1f, 0.15f, 1));
			shapeRenderer.rect(x, y, width, height);
			shapeRenderer.end();
		}
		public void tick(float delta) {
			// Do nothing
		}
		public boolean collidesWithBall(Ball b) {
			return b.y - b.size < y || b.y + b.size > y + height;
		}
		public float getNormal(float y) {
			if (y - this.y < this.y + height - y) {
				// Closer to top
				return (float)Math.PI;
			} else {
				return 0;
			}
		}
	}

	interface GameObject {
		void tick(float delta);
		void render(ShapeRenderer renderer);
	}
}
