package com.bot;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.ai.Bys;
import com.ai.MCT;
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
        	if(state.raisenum<3&&state.getToCall()<3*state.bigblindbet)
        	{
        		debug("raisenum:"+state.raisenum+",tocall:"+state.getToCall());
        		if(handcard[0].getRank()>13&&isSameSuit())
        		{
        			State.raisebet=state.getToCall();
        			return State.raise;
        		}
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
    // <10 <10
    public int delJunk()
    {
    	if(state.currentState==State.baseState)
    	{
    		debug("raise num:"+state.raisenum);
    		if(state.raisenum>0&&isJunk()&&(!isSameRank()))
    		{
    			debug("fold: junk-"+state.raisenum+" prebet-"+state.getPrebet());
    			if((isStraight())&&Math.random()<0.2)
    			{
    				debug("same staightcall");
    				return State.call;
    			}
    			if((isSameSuit())&&Math.random()<0.4)
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
	        double add = 0.065;
	        if (comm.length >= 5)
	            add = 0.001;
	        else if (comm.length >= 4)
	            add = 0.0011;
	        else if (comm.length >= 3)
	            add = 0.024;
	        double prob = won.doubleValue() / total.doubleValue();
	        double deficit = 1.0 - prob;
	        prob += deficit * add;
	       // System.out.printf("Ran %d simulations\n", total.intValue());
	        Log.getIns(state.pid).log("time: "+won.doubleValue()+","+total.doubleValue());
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
	        int tocall = (int) ((state.getInitjetton() * (prob * activeIncludingSelf - 1)) / activePlayers * 0.8);
	        int maxbet = (int) (state.getInitjetton() / 1.8);
	        int prebet = 0;
	        try {
	            prebet = state.getPrebet();
	        } catch (Exception e) {
	            prebet = 0;
	        }
	        if (tocall > maxbet)
	            tocall = maxbet;
	         Log.getIns(state.pid).log("  activeIncludingSelf "+activeIncludingSelf+" tocall "+tocall+" maxbet "+maxbet+" prebet "+prebet+" prob "+prob+"\n");
	        if (tocall < hightocall) {
	            if (state.getInitjetton() < hightocall)
	                hightocall = state.getInitjetton() - 1;
	            double prob1=prob * Math.log(((double) state.getInitjetton() + ((double) activePlayers * hightocall)) / state.getInitjetton());
	            double prob2=(1 - prob) * Math.log((state.getInitjetton() - hightocall) / ((double) state.getInitjetton()));
	            double prob3= Math.log(((double) state.getInitjetton() - prebet)/state.getInitjetton());
	            debug("count :"+(prob1+prob2)+"   "+prob3);
	            if ( prob1+ prob2 >=prob3) {
	            	{
	            		debug("stupid call:"+(prob1+prob2)+"   "+prob3);
	            		return State.call;
	            	}
	            }
	            else {
	                return State.fold;
	            }
	        }
	        else {
	        	State.raisebet=Math.min(tocall - prebet,state.totalpot);
	        	State.raisebet=Math.min(State.raisebet,15*state.bigblindbet);
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
	        	debug("myraisenum: "+state.myraisenum);
	        	if(myval<aveval/base &&(state.myraisenum<2||State.raisebet<10*state.bigblindbet))
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
