package com.bot;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.ai.simplebot.Bys;
import com.ai.simplebot.MCT;
import com.bot.Bot;
import com.game.Card;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class CleverBot implements Bot{
    public static int CORES = 2;
    State state;
    Card handcard[];
    long time;
    Player me=null;
    public int getBestAction (State state, long time) {
    	this.state=state;
    	this.time=time;
    	this.handcard=state.handcard;
    	int action=State.no;
    	int prePlayerActon=State.no;
    	for (Player p : state.players) 
    	{
           if(p.getLastaction()!=State.fold&&prePlayerActon==State.no)
        	   prePlayerActon=p.getLastaction();
           if(p.getPid().equals(state.pid))
           		me=p;
        }
    	//prelop
    	if(state.currentState==State.baseState)
    	{
    		//junk
        	if((action=delJunk())!=State.no) 
        		return action;
        	//good pair
        	if((action=raiseGood())!=State.no)
        		return action;
        	//avoid unlimit raise
        	int size=0;
        	for(Player p:state.players)
        	{
        		if(p.isAlive()&&me.getJetton()>p.getJetton())
        			size++;
        	}
        	debug("size:"+size);
        	if(state.raisenum<3&&state.getToCall()<1.5*size*state.bigblindbet)
        	{
        		debug("raisenum:"+state.raisenum+",tocall:"+state.getToCall());
        		return State.call;
        	}
        	debug("raisenum:"+state.raisenum+",tocall:"+state.getToCall());
        	return State.fold;
    	}
    	
    	return getAction();
    }
    public int raiseGood()
    {
    	if(handcard[0].getRank()>9&&isSameRank())
    	{
    		debug("raise good pair");
    			State.raisebet=0;
    			return State.raise;
    	}else if(isSameRank())
    	{
    		debug("call good pair");
    		return State.call;
    	}
    	return State.no;
    }
    public int delJunk()
    {
    	if(state.currentState==State.baseState)
    	{
    		debug("raise num:"+state.raisenum);
    		if(state.raisenum>0&&isJunk()&&(!isSameRank()))
    		{
    			debug("fold: junk-"+state.raisenum+" prebet-"+state.getPrebet());
    			if((isStraight())&&Math.random()<0.3)
    			{
    				debug("same staightcall");
    				return State.call;
    			}
    			if((isSameSuit())&&Math.random()<0.6)
    			{
    				debug("samesuit call");
    				return State.call;
    			}
    			return State.fold;
    		}
    	}
    	return State.no;
    }
    public boolean isStraight()
    {
    	if(Math.abs(handcard[0].getRank()-handcard[1].getRank())==1)
    		return true;
    	return false;
    }
    public boolean isJunk()
    {
    	if(handcard[0].getRank()<10&&handcard[1].getRank()<10)
    		return true;
    	return false;
    }
    public boolean isSameSuit()
    {
    	if(handcard[0].getSuit()==handcard[1].getSuit())
    		return true;
    	return false;
    }
    public boolean isSameRank()
    {
    	if(handcard[0].getRank()==handcard[1].getRank())
    		return true;
    	return false;
    }
    public int getAction()
    {
    	try{
	    	int activenum = state.getNonFolded();
	        int [] hand = state.getHand();
	        int [] comm = state.getComm();
	        long endMS = System.currentTimeMillis() + time;
	        Log.getIns(state.pid).log("clever bot"+activenum);
	        // pair
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
	        int hightocall = 0;
	        for (Player p : players) {
	            if (p.isAlive())
	                activePlayers ++;
	           
	            hightocall = Math.max(p.getBet(), hightocall);
	        }
	        activePlayers--;
	        int activeIncludingSelf = activePlayers + 1;
	        int tocall = (int) ((state.getJetton() * (prob * activeIncludingSelf - 1)) / activePlayers * 0.8);
	        int maxbet = (int) (state.getJetton() / 1.8);
	        int prebet = 0;
	        try {
	            prebet = state.getPrebet();
	        } catch (Exception e) {
	            prebet = 0;
	        }
	        if (tocall > maxbet)
	            tocall = maxbet;
	         Log.getIns(state.pid).log("  activeIncludingSelf "+activeIncludingSelf+" wager "+tocall+" maxWager "+maxbet+" prevWager "+prebet+" prob "+prob+"\n");
	        if (tocall < hightocall) {
	            if (state.getJetton() < hightocall)
	                hightocall = state.getJetton() - 1;
	            if (prob * Math.log(((double) state.getJetton() + ((double) activePlayers * hightocall)) / state.getJetton()) + (1 - prob) * Math.log((state.getJetton() - hightocall) / ((double) state.getJetton())) >= Math.log(((double) state.getJetton() - prebet)/state.getJetton())) {
	            	return State.call;
	            }
	            else {
	                return State.fold;
	            }
	        }
	        else {
	        	State.raisebet=(tocall - prebet);
	        	//bys
	        	Log.getIns(state.pid).log("myprobval:"+state.getMyVal());
	        	double myval=state.getMyVal();
	        	double aveval=0,base=0;
	        	for (Player p : players) {
	                if (p.isAlive()&& (!p.getPid().equals(state.pid)))
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
	                		if(oppoval>0&&oppoval<10)
	                		{
	                			aveval+=oppoval;
	                			base++;
	                		}
	                	}
	                }
	            }
	        	Log.getIns(state.pid).log("myval:ave  "+myval+":"+(aveval/base));
	        	if(myval>aveval/base)
	        		return State.raise;
	        	else
	        		return State.call;
	        }
	    	}catch(Exception e)
	    	{
	    		return State.fold;
	    	}
	    }
    
    public void debug(String s)
    {
    	try {
			Log.getIns(state.pid).log(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
