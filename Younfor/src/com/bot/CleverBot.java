package com.bot;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import javax.swing.text.StyleContext.SmallAttributeSet;

import com.ai.Bys;
import com.ai.MCT;
import com.bot.Bot;
import com.game.Card;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class CleverBot implements Bot{
    public static int CORES = 4;
    State state;
    Card handcard[];
    long time;
    Player me=null;
    int activeplayer=0;
    public int getBestAction (State state, long time) {
    	
    	this.state=state;
    	this.time=time;
    	this.handcard=state.handcard;
    	int action=State.no;
    	int prePlayerActon=State.no;
    	activeplayer=0;
    	for (Player p : state.players) 
    	{
           if(p.getLastaction()!=State.fold&&prePlayerActon==State.no)
        	   prePlayerActon=p.getLastaction();
           if(p.getPid().equals(state.pid))
           		me=p;
           if((!p.getPid().equals(state.pid))&&p.isAlive())
        	   activeplayer++;
        }
    	debug("active player: "+activeplayer);
    	//preflop
    	if(state.currentState==State.baseState)
    	{
	    		//EP
	    		if((action=getEP())!=State.no)
	    			return action;
	    		//MP
	    		if((action=getMP())!=State.no)
	    			return action;
	    		//LP
	    		if((action=getLP())!=State.no)
	    			return action;
	    		//BL
	    		if((action=getBL())!=State.no)
	    			return action;
	    		//fold
	    		if(state.seatplayer>5)
	        		return State.fold;
	    		
	    		//junk
	        	if((action=delJunk())!=State.no) 
	        		return action;
	        	//good pair
	        	if((action=raiseGood())!=State.no)
	        		return action;
	        	//avoid unlimit raise
	        	if(state.raisenum<3&&state.getToCall()<10*state.bigblindbet)
	        	{
	        		debug("raisenum:"+state.raisenum+",tocall:"+state.getToCall());
	        		if(handcard[0].getRank()>13&&isSameSuit())
	        		{
	        			State.raisebet=state.getToCall();
	        			debug("3 people raise");
	        			return State.raise;
	        		}
	        		debug("3 people call");
	        		return State.call;
	        	}
        	debug("er fold");
        	return State.fold;
    	}
    	return getAction();
    }
    public int getBL()
    {
    	if(state.myloc==State.smallblind)
		{
    		debug("big or small blind");
    		if(level1())
    		{
    			State.raisebet=5*state.bigblindbet;
    			return State.raise;
    		}
    		if(level2())
    		{
    			State.raisebet=4*state.bigblindbet;
    			return State.raise;
    		}
    		if(level3())
    		{
    			State.raisebet=3*state.bigblindbet;
    			return State.raise;
    		}
    		if(state.seatplayer<7&&level4())
    		{
    			debug("small playerseat  level 4");
    			State.raisebet=2*state.bigblindbet;
    			return State.raise;
    		}
    		if(activeplayer<2&&level4()&&Math.random()>0.6)
    		{
    			debug("small active raise level 4");
    			State.raisebet=state.bigblindbet;
    			return State.raise;
    		}	
		}
    	if(state.myloc==State.bigblind)
    	{
    		if(state.raisenum==0)
    			return State.check;
    	}
    	return State.no;
    }
    public int getEP()
    {
    	if(state.myloc==State.EP)
		{
    		debug("EP");
    		if(level1())
    		{
    			State.raisebet=6*state.bigblindbet;
    			return State.raise;
    		}
    		if(level2())
    		{
    			State.raisebet=4*state.bigblindbet;
    			return State.raise;
    		}
    		if(level3())
    		{
    			State.raisebet=2*state.bigblindbet;
    			return State.raise;
    		}
		}
    	return State.no;
    }
    public int getMP()
    {
    	if(state.myloc==State.MP)
		{
    		debug("MP");
    		if(level1())
    		{
    			State.raisebet=6*state.bigblindbet;
    			return State.raise;
    		}
    		if(level2())
    		{
    			State.raisebet=5*state.bigblindbet;
    			return State.raise;
    		}
    		if(level3())
    		{
    			State.raisebet=3*state.bigblindbet;
    			return State.raise;
    		}
		}
    	return State.no;
    }
    public int getLP()
    {
    	if(state.myloc==State.LP)
		{
    		if(level1())
    		{
    			State.raisebet=6*state.bigblindbet;
    			return State.raise;
    		}
    		if(level2())
    		{
    			State.raisebet=4*state.bigblindbet;
    			return State.raise;
    		}
    		if(level3())
    		{
    			State.raisebet=3*state.bigblindbet;
    			return State.raise;
    		}
    		if(state.seatplayer<7&&level4())
    		{
    			debug("small playerseat  level 4");
    			State.raisebet=2*state.bigblindbet;
    			return State.raise;
    		}
    		if(activeplayer<2&&level4())
    		{
    			debug("small active raise level 4");
    			State.raisebet=state.bigblindbet;
    			return State.raise;
    		}	
		}
    	return State.no;
    }
    public boolean is2Straight()
    {
    	if(Math.abs(handcard[0].getRank()-handcard[1].getRank())==2&&isSameSuit())
    		return true;
    	return false;
    }
    public boolean isBigAsuit()
    {
    	if(isSameSuit()&&bothhigher(11)&&higher(13))
    	{
    		debug("big Asuit");
    		return true;
    	}
    	return false;
    }
    public boolean level1()
    {
    	if(isBigPair()||isBigAsuit())
    	{
    		debug("level 1");
    		return true;
    	}
    	return false;
    }
    public boolean level2()
    {
    	if(isMidPair()||isMidAsuit()||isBigAK())
    	{
    		debug("level 2");
    		return true;
    	}
    	return false;
    }
    public boolean level3()
    {
    	if(isMidSuit())
    	{
    		debug("level 3");
    		return true;
    	}
    	return false;
    }
    public boolean level4()
    {
    	if(isSameSuit())
    	{
    		if(is(14,8)||is(12,10)||is(14,9)||is(11,10))
    		{
    			debug("level 4");
    			return true;
    		}
    	}
    	if(is(13,12)||is(8,8)||is(14,10)||is(14,11))
    	{
    		debug("level 4");
    		return true;
    	}
    	return false;
    }
    public boolean level5()
    {
    	if(is(7,7)||is(13,11)||is(12,11)||is(13,10)||is(12,10))
    	{
    		debug("level 5");
    		return true;
    	}
    	if(isSameSuit())
    	{
    		if(higher(13)||is(12,9)||is(11,10)||is(11,9)||is(10,9)||is(13,9))
    		{
    			debug("level 5");
    			return true;
    		}
    	}
    	return false;
    }
    public boolean is(int a,int b)
    {
    	if(handcard[0].getRank()==a&&handcard[1].getRank()==b)
    		return true;
    	if(handcard[1].getRank()==a&&handcard[0].getRank()==b)
    		return true;
    	return false;
    }
    public boolean isMidSuit()
    {
    	if(isSameSuit())
    	{
    		//QJs KJs KTs
    		if(bothhigher(10))
    		{
    			debug("mid suit");
    			return true;
    		}
    		if(bothhigher(9)&&higher(12))
    		{
    			debug("mid suit");
    			return true;
    		}
    	}
    	if(isSameRank()&&higher(8))
    	{
    		debug("mid suit");
    		return true;
    	}
    	return false;
    }
    public boolean isBigAK()
    {
    	//AK AQ
    	if(bothhigher(11)&&higher(13))
    	{
    		debug("big AK");
    		return true;
    	}
    	//KQs
    	if(bothhigher(11)&&isSameSuit())
    	{
    		debug("big KQs");
    		return true;
    	}
    	return false;
    }
    public boolean isAsuit()
    {
    	if(isSameSuit()&&higher(13))
    	{
    		debug("A suit");
    		return true;
    	}
    	return false;
    }
    public boolean isBigsuit()
    {
    	if(isSameSuit()&&bothhigher(10))
    	{
    		debug("big suit");
    		return true;
    	}
    	return false;
    }
    public boolean higher(int i)
    {
    	if(handcard[0].getRank()>i ||handcard[1].getRank()>i)
    		return true;
    	return false;
    }
    public boolean lower(int i)
    {
    	if(handcard[0].getRank()<i ||handcard[1].getRank()<i)
    		return true;
    	return false;
    }
    public boolean bothlower(int i)
    {
    	if(handcard[0].getRank()<i &&handcard[1].getRank()<i)
    		return true;
    	return false;
    }
    public boolean bothhigher(int i)
    {
    	if(handcard[0].getRank()>i &&handcard[1].getRank()>i)
    		return true;
    	return false;
    }
    public boolean isBigPair()
    {
    	if( isSameRank()&&handcard[0].getRank()>=12)
    	{
    			debug("big pair");
    			return true;
    	}
    	return false;
    }
    public boolean isMidAsuit()
    {
    	if(isSameSuit()&&bothhigher(9)&&higher(13))
    	{
    		debug("mid Asuit");
    		return true;
    	}
    	return false;
    }
    public boolean isMidPair()
    {
    	if( isSameRank()&&handcard[0].getRank()>10)
    	{	
    		debug("mid pair");
			return true;
    	}
    	return false;
    }
    public boolean isSmallPair()
    {
    	if( isSameRank()&&handcard[0].getRank()<8)
    	{
    		debug("small pair");
			return true;
    	}
    	return false;
    }
    public int raiseGood()
    {
    	if(handcard[0].getRank()>9&&isSameRank()&&state.raisenum<3)
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
    			if(state.getToCall()==state.getPrebet())
    			{
    				debug("call==prebet,check");
    				return State.check;
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
	            add = 0.0011;
	        else if (comm.length >= 4)
	            add = 0.0013;
	        else if (comm.length >= 3)
	            add = 0.025;
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
	         if (tocall < hightocall) {
	            if (state.getInitjetton() < hightocall)
	                hightocall = state.getInitjetton() - 1;
	            double prob1=prob * Math.log(((double) state.getInitjetton() + ((double) activePlayers * hightocall)) / state.getInitjetton());
	            double prob2=(1 - prob) * Math.log((state.getInitjetton() - hightocall) / ((double) state.getInitjetton()));
	            double prob3= Math.log(((double) state.getInitjetton() - prebet)/state.getInitjetton());
	            debug("count :"+(prob1+prob2)+"   "+prob3);
	            debug("base:"+base+",averval/base:"+(aveval/base));
;	            if(base==0)
	            	return State.call;
	            if ( prob1+ prob2 >=prob3&&myval<aveval/base) {
	            	{
	            		debug("stupid call:"+(prob1+prob2)+"   "+prob3);
	            		return State.call;
	            	}
	            }
	            else {
	            	debug("shaby check");
	                return State.check;
	            }
	        }
	        else {
	        	State.raisebet=Math.min(tocall - prebet,5*state.totalpot);
	        	State.raisebet=Math.min(State.raisebet,30*state.bigblindbet);
	        	if(base==0)
	        		return State.raise;
	        	Log.getIns(state.pid).log("myval:ave  "+myval+":"+(aveval/base));
	        	debug("myraisenum: "+state.myraisenum);
	        	if(myval<aveval/base &&(state.myraisenum<3))
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
