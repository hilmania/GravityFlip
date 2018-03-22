package com.neurondigital.gravityflip;

import android.graphics.Canvas;

import com.neurondigital.nudge.Instance;
import com.neurondigital.nudge.Screen;
import com.neurondigital.nudge.Sprite;

public class FloorGenerator {
	Sprite sprites[];
	int startX, startY;
	Screen screen;
	int[] tyleList;
	int space_size;
	int floorWidth = 0;
	int offset = 0;

	public FloorGenerator(Screen screen, Sprite sprites[], int startX, int startY, int[] tyleList) {
		this.sprites = sprites;
		this.startX = startX;
		this.startY = startY;
		this.screen = screen;
		this.tyleList = tyleList;
		this.space_size = (int) (sprites[0].getWidth());
	}

	public void update() {

	}

	public void restart() {
		floorWidth = 0;
		offset = 0;
	}

	public void restart_if_larger_than_last_tile(int x) {
		if (floorWidth != 0) {
			if (x + screen.ScreenWidth() >= offset + floorWidth) {
				offset += floorWidth;
			}
		}
	}

	public boolean collision(Instance instance) {
		//test for collisions with current floor
		int currentX = startX + offset;
		for (int i = 0; i < tyleList.length; i++) {
			if (tyleList[i] == 0) {
				//draw blank space
				currentX += space_size;
			} else {
				//test if tile is in screen to avoid computing extra stuff
				if (screen.inScreen(currentX + sprites[tyleList[i] - 1].getWidth(), startY) || screen.inScreen(currentX, startY)) {
					if (tyleList[i] == 7) {
						if (instance.CollidedWith(currentX, startY, sprites[tyleList[i] - 1].getWidth(), (int) (sprites[tyleList[i] - 1].getHeight() + (sprites[tyleList[i] - 1].getHeight() * 0.5f)), true))
							return true;
					} else {
						if (instance.CollidedWith(currentX, startY, sprites[tyleList[i] - 1].getWidth(), sprites[tyleList[i] - 1].getHeight(), true))
							return true;
					}
				}
				currentX += sprites[tyleList[i] - 1].getWidth();
			}
		}

		//compute previous floor section. This is done to avoid empty screens. This is done to loop floors to give the illusion of an infinite game.
		currentX = startX + offset - floorWidth;
		for (int i = 0; i < tyleList.length; i++) {
			if (tyleList[i] == 0) {
				//draw blank space
				currentX += space_size;
			} else {
				//test if tile is in screen to avoid computing extra stuff
				if (screen.inScreen(currentX + sprites[tyleList[i] - 1].getWidth(), startY) || screen.inScreen(currentX, startY)) {
					if (tyleList[i] == 7) {
						if (instance.CollidedWith(currentX, startY, sprites[tyleList[i] - 1].getWidth(), (int) (sprites[tyleList[i] - 1].getHeight() + (sprites[tyleList[i] - 1].getHeight() * 0.5f)), true))
							return true;
					} else {
						if (instance.CollidedWith(currentX, startY, sprites[tyleList[i] - 1].getWidth(), sprites[tyleList[i] - 1].getHeight(), true))
							return true;
					}
				}
				currentX += sprites[tyleList[i] - 1].getWidth();
			}
		}

		//instace collided with no tile.
		return false;
	}

	public void drawFloor(Canvas canvas) {
		//draw all onscreen tiles
		int currentX = startX + offset;
		for (int i = 0; i < tyleList.length; i++) {

			if (tyleList[i] == 0) {
				//draw blank space
				currentX += space_size;
			} else {
				//test if tile is in screen to avoid drawing extra stuff
				if (screen.inScreen(currentX + sprites[tyleList[i] - 1].getWidth(), startY) || screen.inScreen(currentX, startY)) {
					sprites[tyleList[i] - 1].draw(canvas, screen.ScreenX(currentX), screen.ScreenY(startY));
				}
				currentX += sprites[tyleList[i] - 1].getWidth();
			}

		}
		if (floorWidth == 0)
			floorWidth = currentX;

		//draw previous floor section. This is done to avoid empty screens. This is done to loop floors to give the illusion of an infinite game.
		currentX = startX + offset - floorWidth;
		for (int i = 0; i < tyleList.length; i++) {

			if (tyleList[i] == 0) {
				//draw blank space
				currentX += space_size;
			} else {
				//test if tile is in screen to avoid drawing extra stuff
				if (screen.inScreen(currentX + sprites[tyleList[i] - 1].getWidth(), startY) || screen.inScreen(currentX, startY)) {
					sprites[tyleList[i] - 1].draw(canvas, screen.ScreenX(currentX), screen.ScreenY(startY));
				}
				currentX += sprites[tyleList[i] - 1].getWidth();
			}

		}

	}
}
