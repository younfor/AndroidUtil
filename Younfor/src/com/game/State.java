package com.game;

public class State {
	public final static int button=10,smallblind=11,bigblind=12,normal=13;
	public final static int HEARTS=20,SPADES=21,CLUBS=22,DIAMONDS=23;
	public final static int blind=30,check=31,call=32,raise=33,all_in=34,fold=35;
	public final static int baseState=40,flopState=41,turnState=41,riverState=42;
	public static int getPoint(String s)
	{
		if(s.charAt(0)<='9'&&s.charAt(0)>='0')
			return Integer.parseInt(s);
		if(s.equals("J"))
			return 11;
		if(s.equals("Q"))
			return 12;
		if(s.equals("K"))
			return 13;
		if(s.equals("A"))
			return 14;
		return -1;
	}
	public static int getColor(String s)
	{
		if(s.equals("HEARTS"))
			return HEARTS;
		else if(s.equals("SPADES"))
			return SPADES;
		else if (s.equals("CLUBS"))
			return CLUBS;
		else if (s.equals("DIAMONDS"))
			return DIAMONDS;
		else
			return -1;
	}
	public static int getAction(String s)
	{
		if(s.equals("blind"))
			return blind;
		else if(s.equals("check"))
			return check;
		else if (s.equals("call"))
			return call;
		else if (s.equals("raise"))
			return raise;
		else if (s.equals("all_in"))
			return all_in;
		else if (s.equals("fold"))
			return fold;
		else
			return -1;
	}
}
