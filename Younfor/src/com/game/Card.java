package com.game;

import java.io.IOException;

import com.ai.PokerLib;
import com.util.Log;

public class Card {
	public String color, point;

	public Card(String color, String point) {
		super();
		this.color = color;
		this.point = point;
	}
	public int getValue()
	{
		//Log.getIns("1111").log("value:"+point+color.charAt(0));
		if(point.equals("10"))
			point="T";
		return PokerLib.stringToCard(point+color.charAt(0));
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	/*
	public static void main(String []args)
	{
		System.out.println(PokerLib.stringToCard("3S"));
	}
	*/
}
