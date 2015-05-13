package com.bot;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.ai.simplebot.Bys;
import com.ai.simplebot.MCT;
import com.bot.Bot;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class CleverBot implements Bot{
    public static int CORES = 2;
    public int getBestAction (State state, long timeMS) {
    	try{
        int activenum = state.getNonFolded();
        int [] hand = state.getHand();
        int [] comm = state.getComm();
        long endMS = System.currentTimeMillis() + timeMS;
        Log.getIns(state.pid).log("clever bot");
        // Run monte carlo & calc prob 
        ExecutorService threads = Executors.newFixedThreadPool(CORES);
        AtomicInteger won = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);
        for (int i = 0; i < CORES; i ++) {
            MCT thread = new MCT(hand, activenum, comm, endMS, won, total);
            threads.submit(thread);
        }
        threads.shutdown();
        try {
            threads.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {}
        //bys ranknum
        for(int i=1;i<10;i++)
        	Log.getIns(state.pid).log("myrank ["+i+"] "+State.ranknum[i].intValue());
        double add = 0.063;
        if (comm.length >= 5)
            add = 0.001;
        else if (comm.length >= 4)
            add = 0.009;
        else if (comm.length >= 3)
            add = 0.022;
        double prob = won.doubleValue() / total.doubleValue();
        double deficit = 1.0 - prob;
        prob += deficit * add;
       // System.out.printf("Ran %d simulations\n", total.intValue());
        //Log.getIns(state.pid).log("time: "+won.doubleValue()+","+total.doubleValue());
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
        	//bys
        	Log.getIns(state.pid).log("myprobval:"+state.getMyVal());
        	double myval=state.getMyVal();
        	boolean israise=true;
        	for (Player p : players) {
                if (p.isAlive())
                {
                	int ac[]=new int[state.currentState-State.baseState+1];
                	for(int j=0;j<ac.length;j++)
            		{	
            			ac[j]=p.actions[j];
            		}
                	if(ac[ac.length-1]!=Bys.fold)
                	{
                		double oppoval=state.bys.get(p.getPid()).getVal(ac);
                		Log.getIns(state.pid).log("val"+p.getPid()+":"+oppoval);
                		if(myval<oppoval)
                			israise=false;
                	}
                }
            }
        	if((new Random()).nextDouble()>1.0/activePlayers && israise)
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
