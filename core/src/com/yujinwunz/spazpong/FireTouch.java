package com.yujinwunz.spazpong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by yujinwunz on 10/03/2017.
 */

public class FireTouch extends ScreenAdapter {

	LinkedList<Box> boxes = new LinkedList<Box>();
	SpazPong parent;
	Random random = new Random();

	public FireTouch(SpazPong parent) {
		this.parent = parent;
	}

	BitmapFont font = new BitmapFont();

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		while (!boxes.isEmpty() && boxes.getFirst().age > 90) {
			boxes.removeFirst();
		}

		for (int i = 0; i < 20; i++) {
			if (Gdx.input.isTouched(i)) {
				Vector3 unprojectedInput =
						parent.camera.unproject(new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0));
				boxes.addLast(new Box((int)unprojectedInput.x, (int)unprojectedInput.y));
			}
		}

		parent.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		LinkedList<Box> newboxes = new LinkedList<Box>();
		Collections.sort(boxes, new Comparator<Box>() {
			@Override
			public int compare(Box o1, Box o2) {
				return Integer.valueOf(o2.age).compareTo(o1.age);
			}
		});
		for (Box b: boxes) {
			int size = 30 + b.age;
			parent.shapeRenderer.setColor(new Color(1 - b.age / 90.f, 1 - b.age / 30.f, 0 ,1));
			parent.shapeRenderer.rect(b.x - size/2, b.y - size/2, size, size);

			if (Math.abs(random.nextInt()) % 900 < 30&& random.nextInt() % 90 > b.age) {
				newboxes.addLast(
						new Box(b.x - size/2 + Math.abs(random.nextInt()) % (size/2*2+1),
							b.y - size/2 + Math.abs(random.nextInt()) % (size/2*2+1),
							Math.max(0, b.age - 30)
						)
				);
			}

			b.age++;
		}
		boxes.addAll(newboxes);
		parent.shapeRenderer.end();

		parent.batch.begin();
		parent.batch.setProjectionMatrix(parent.camera.combined);
		font.draw(parent.batch, Integer.toString(boxes.size()), 3, 20);
		parent.batch.end();
	}

	class Box {
		int x;
		int y;
		int age;

		public Box(int x, int y) {
			this.x = x;
			this.y = y;
			this.age = 0;
		}
		public Box(int x, int y, int age) {
			this.x = x;
			this.y = y;
			this.age = age;
		}
	}
}
