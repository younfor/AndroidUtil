package com.game;

public class Card {
	public int color, point;

	public Card(int color, int point) {
		super();
		this.color = color;
		this.point = point;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}
}
