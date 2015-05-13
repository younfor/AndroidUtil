package com.bot;

import java.util.Random;

import com.game.State;

public class RandomBot implements Bot{

	@Override
	public int getBestAction(State state, long timeMS) {
		int action[]={State.call,State.check,State.fold};
		State.raisebet=0;
		Random r=new Random();
		return action[r.nextInt(3)];
	}
	/*
	public static void main(String args[])
	{
		for(int i=0;i<10;i++)
		{
			Random r=new Random();
			System.out.println(r.nextInt(5));
		}
	}
	*/
}
