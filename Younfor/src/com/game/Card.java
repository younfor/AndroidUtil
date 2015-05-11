package com.game;

import java.io.IOException;

import com.ai.simplebot.PokerLib;
import com.util.Log;

public class Card {
	public String color, point;
	private int rank,suit;
	public static int TEN=8,TWO=0,ACE=12;
	public Card(int rank,int suit)
	{
		this.rank=rank;
		this.suit=suit;
	}
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
	public int getRank() throws IOException
	{
		Log.getIns("7777").log("0");
		if(point.charAt(0)<='9'&&point.charAt(0)>='2')
			return point.charAt(0)-'0'-2;
		if(point.equals("10"))
			return 8;
		return point.charAt(0)-'J'+9;
	}
	public int getSuit()
	{
		if(color.equals("SPADES"))
			return 0;
		if(color.equals("HEARTS"))
			return 1;
		if(color.equals("DIAMONDS"))
			return 2;
		if(color.equals("CLUBS"))
			return 4;
		return -1;
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
