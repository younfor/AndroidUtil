package com.bot;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.ai.MCT;
import com.bot.Bot;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class SimpleBot implements Bot{
    public static int CORES = 2;
    public int getBestAction (State state, long timeMS) {
    	try{
        // -1 for fold, 0 for call, other are raises
        // We will probabilistically decide odds of winning and whether to call, raise, or fold
        int numOther = state.getNonFolded();
        int [] hand = state.getHand();
        int [] community = state.getComm();
        long endMS = System.currentTimeMillis() + timeMS;

        // Run monte carlo & calc prob 
        ExecutorService threads = Executors.newFixedThreadPool(CORES);
        AtomicInteger won = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);
        for (int i = 0; i < CORES; i ++) {
            MCT thread = new MCT(hand, numOther, community, endMS, won, total);
            threads.submit(thread);
        }
        threads.shutdown();
        try {
            threads.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {}
        double add = 0.065;
        if (community.length >= 5)
            add = 0.001;
        else if (community.length >= 4)
            add = 0.010;
        else if (community.length >= 3)
            add = 0.025;
        double prob = won.doubleValue() / total.doubleValue();
        double deficit = 1.0 - prob;
        prob += deficit * add;
       // System.out.printf("Ran %d simulations\n", total.intValue());
        Log.getIns(state.pid).log("time: "+won.doubleValue()+","+total.doubleValue());
        // Evaluate other players & calculate move
        List<Player> players = state.players;
        int activePlayers = 0;
        int highWager = 0;
        for (Player p : players) {
            if (p.isAlive())
                activePlayers ++;
            highWager = Math.max(p.getBet(), highWager);
        }
        activePlayers--;
        int activeIncludingSelf = activePlayers + 1;
        int wager = (int) ((state.getJetton() * (prob * activeIncludingSelf - 1)) / activePlayers * 0.8);
        int maxW = (int) (state.getJetton() / 1.8);
        int prevWager = 0;
        try {
            prevWager = state.getPrebet();
        } catch (Exception e) {
            prevWager = 0;
        }
        if (wager > maxW)
            wager = maxW;
        //System.out.printf("  activeIncludingSelf %d wager %d maxWager %d prevWager %d prob %.4f\n", activeIncludingSelf, wager, maxW, prevWager, prob);
        Log.getIns(state.pid).log("  activeIncludingSelf "+activeIncludingSelf+" wager "+wager+" maxWager "+maxW+" prevWager "+prevWager+" prob "+prob+"\n");
        if (wager < highWager) {
            if (state.getJetton() < highWager)
                highWager = state.getJetton() - 1;
            if (prob * Math.log(((double) state.getJetton() + ((double) activePlayers * highWager)) / state.getJetton()) + (1 - prob) * Math.log((state.getJetton() - highWager) / ((double) state.getJetton())) >= Math.log(((double) state.getJetton() - prevWager)/state.getJetton())) {
                //System.out.printf("  Calling\n");
                return State.call;
            }
            else {
                //System.out.printf("  Fold\n");
                return State.fold;
            }
        }
        else {
            //System.out.printf("  Raise %d\n", wager - prevWager);
        	State.raisebet=(wager - prevWager);
        	if((new Random()).nextDouble()>1.0/activePlayers)
        		return State.raise;
        	else
        		return State.call;
        }
    	}catch(Exception e)
    	{
    		return State.fold;
    	}
    }
}
