package com.bot;

import java.util.Random;

import com.game.State;

public class CallBot implements Bot{

	@Override
	public int getBestAction(State state, long timeMS) {
		return State.call;
	}
}
