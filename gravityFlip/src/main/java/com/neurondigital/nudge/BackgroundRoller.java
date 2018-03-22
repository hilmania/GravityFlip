package com.neurondigital.nudge;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BackgroundRoller {
	public Instance image1, image2;
	int speed;
	Screen screen;

	public BackgroundRoller(Bitmap bitmap, Screen screen, int y, int speed) {
		image1 = new Instance(new Sprite(bitmap, screen.ScreenWidth()), 0, y, screen, false);
		image2 = new Instance(new Sprite(bitmap, screen.ScreenWidth()), image1.getWidth(), y, screen, false);

		this.speed = speed;
		this.screen = screen;
		reset();
	}

	public void reset() {
		image1.speedx = -screen.dpToPx(speed);
		image2.speedx = -screen.dpToPx(speed);
		image1.x = 0;
		image2.x = image1.getWidth();
	}

	public void step() {
		image1.Update();
		image2.Update();

		//move background
		if (image1.x < -screen.ScreenWidth())
			image1.x = screen.ScreenWidth();
		if (image2.x < -screen.ScreenWidth())
			image2.x = screen.ScreenWidth();
	}

	public void draw(Canvas canvas) {
		image1.draw(canvas);
		image2.draw(canvas);
	}

	public void setY(float y) {
		image1.y = y;
		image2.y = y;
	}

	public float getY() {
		return image1.y;
	}
}
