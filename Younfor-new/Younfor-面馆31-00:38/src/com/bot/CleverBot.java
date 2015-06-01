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

public class CleverBot implements Bot {
	public static int CORES = 4;
	State state;
	Card handcard[];
	long time;
	Player me = null;
	int activeplayer = 0;
	double oppojetton=0;

	public int getBestAction(State state, long time) {

		this.state = state;
		this.time = time;
		this.handcard = state.handcard;
		int action = State.no;
		int prePlayerActon = State.no;
		activeplayer = 0;
		double hightocall=0;
		for (Player p : state.players) {
			if (p.getLastaction() != State.fold && prePlayerActon == State.no)
				prePlayerActon = p.getLastaction();
			if (p.getPid().equals(state.pid))
				me = p;
			oppojetton=0;
			if ((!p.getPid().equals(state.pid)) && p.isAlive())
			{
				debug("jetton:"+p.getJetton()+":"+p.getLastaction());
				if(oppojetton<p.getJetton())
					oppojetton=p.getJetton();
				debug("oppojetton:"+oppojetton);
				activeplayer++;
			}
			//debug(p.getPid()+":"+p.getBet());
			hightocall = Math.max(p.getBet(), hightocall);
		}
		debug("active player: " + activeplayer);
		// previous-flop
		if (state.currentState == State.baseState) {
			if(hightocall-state.getPrebet()>8*state.bigblindbet&&state.getJetton()<80*state.bigblindbet)
			{
				debug("too big handcards");
				return State.fold;
			}
			// EP
			if ((action = getEP()) != State.no ) {
				if (action == State.raise && (state.myraisenum >= 1||hightocall>3*state.bigblindbet))
					return State.call;
				return action;
			}
			// MP
			if ((action = getMP()) != State.no) {
				if (action == State.raise && (state.myraisenum >= 1||hightocall>3*state.bigblindbet) )
					return State.call;
				return action;
			}
			// LP
			if ((action = getLP()) != State.no ) {
				if (action == State.raise && (state.myraisenum >= 1||hightocall>3*state.bigblindbet))
					return State.call;
				return action;
			}
			// BL
			if ((action = getBL()) != State.no) {
				if (action == State.raise && (state.myraisenum >= 1||hightocall>3*state.bigblindbet))
					return State.call;
				return action;
			}
			// fold
			return State.fold;
		}
		return getAction();
	}

	public int getBL() {
		if (state.myloc == State.bigblind) {
			debug("big  blind");
			if (level1()) {
				State.raisebet = (2+state.callnum)*state.bigblindbet ;
				return State.raise;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=(2+state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					State.raisebet=2*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call1())
				{
					return State.call;
				}
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=(1+state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					return State.call;
				}
				if(isRaise1Call1()&&isSameSuit())
				{	
					return State.call;
				}
			}
			if (level4()) {
				if(isFoldAll())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					return State.call;
				}
				if(isRaise1Call1())
				{
					return State.call;
				}
				if(isCall1()||isCall2())
				{
					return State.check;
				}
			}
			if (level5()) {
				if(isFoldAll())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isCall1()||isCall2())
				{
					return State.check;
				}
			}
			if(isFoldAll()||isCall1()||isCall2())
				return State.check;
		}
		return State.no;
	}

	public int getEP() {
		if (state.myloc == State.EP) {
			debug("EP");
			if (level1()) {
				State.raisebet = (1+state.callnum)* state.bigblindbet;
				return State.raise;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					debug("foldall call1 call2");
					State.raisebet=(state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					return State.fold;
				}
				if(isRaise1Call1())
				{
					return State.call;
				}
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2()||isRaise1Call0())
					return State.fold;
				if(isRaise1Call1()&&isSameSuit()&&is(13,12))
					return State.call;
			}
			if (level4()) {
				if(isFoldAll()||isCall1()||isRaise1Call0())
					return State.fold;
				if(isCall2()||isRaise1Call1())
					return State.call;
			}
			if (level5()) {
				return State.call;
			}
		}
		return State.no;
	}

	public int getMP() {
		if (state.myloc == State.MP) {
			debug("MP");
			if (level1()) {
				State.raisebet = (2+state.callnum) * state.bigblindbet;
				return State.raise;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=(1+state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call1())
				{
					return State.call;
				}
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=(1+state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
					return State.fold;
				if(isRaise1Call1()&&isSameSuit()&&is(13,12))
					return State.call;
			}
			if (level4()) {
				if(isFoldAll()||isCall1()||isRaise1Call0())
					return State.fold;
				if(isCall2()||isRaise1Call1())
					return State.call;
			}
			if ( level5()) {
				return State.call;
			}
		}
		return State.no;
	}

	public int getLP() {
		if (state.myloc == State.LP) {
			debug("LP");
			if (level1()) {
				State.raisebet = (3+state.callnum) * state.bigblindbet;
				return State.raise;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					debug("foldall call1 call2");
					State.raisebet=(2+state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					debug("foldall call1 call2");
					State.raisebet=2*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call1())
				{
					return State.call;
				}
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=(state.callnum)*state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					return State.fold;
				}
				if(isRaise1Call1()&&isSameSuit()&&is(13,12))
				{
					return State.call;
				}
			}
			if (level4()) {
				if(isFoldAll())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					return State.fold;
				}
				if(isCall1()||isCall2()||isRaise1Call1())
				{
					return State.call;
				}
			}
			if (level5()) {
				if(isFoldAll())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isCall1()||isCall2())
				{
					return State.call;
				}
			}
		}
		return State.no;
	}



	public boolean level1() {
		//AAKKQQ  AKs AKo
		if (is(14,14)||is(13,13)||is(12,12) || is(14,13)) {
			debug("level 1");
			return true;
		}
		return false;
	}
	public boolean level2() {
		//JJ 1010  99 AQs AJs AQo
		if (is(11,11)||is(10,10)||is(9,9) || is(14,12) || (is(14,11)&&isSameSuit())) {
			debug("level 2");
			return true;
		}
		return false;
	}
	public boolean level3() {
		if(is(14,11)||is(14,10)||is(13,12))
		{
			debug("level 3");
			return true;
		}
		return false;
	}
	public boolean level4() {
		boolean ac=false;
		if(is(8,8)||is(7,7)||is(6,6)||is(5,5)||is(4,4)||is(3,3)||is(2,2))
			ac=true;
		if (isSameSuit()) {
			if (is(13, 10) || is(12, 11) || is(12, 10) || is(11, 10)||is(10,9)) {
				ac=true;
			}
		}
		if (ac) {
			debug("level 4");
			return true;
		}
		return false;
	}
	public boolean level5() {
		
		if (isSameSuit()) {
			if ( is(13,9)||is(9,8)||is(8,7)||is(14, 9) || is(14, 8) || is(14, 7) || is(14, 6)
					|| is(14, 5)||is(14,4)||is(14,3)||is(14,2)) {
				debug("level 5");
				return true;
			}
		}
		if (is(13, 11) || is(13, 10) || is(12, 11) || is(12, 10) || is(11, 10)) {
			debug("level 5");
			return true;
		}
	
		return false;
	}
	public boolean level6()
	{
		if(isSameSuit())
		{
			if(is(10,8))
			{
				debug("level 6");
				return true;
			}
		}
		return false;
	}
	public boolean isFoldAll()
	{
		if(state.callnum==0&&state.raisenum==0)
		{
			debug("is fold all");
			return true;
		}
		return false;
	}
	public boolean isCall1()
	{
		if(state.callnum==1&&state.raisenum==0)
		{
			debug("is call 1");
			return true;
		}
		return false;
	}
	public boolean isCall2()
	{
		if(state.raisenum==0&&state.callnum>1)
		{
			debug("is call 2");
			return true;
		}
		return false;
	}
	public boolean isRaise1Call0()
	{
		if(state.raisenum>=1&&state.callnum==0)
		{
			debug("is raise 1, call 0");
			return true;
		}
		return false;
	}
	public boolean isRaise1Call1()
	{
		if(state.raisenum>=1&&state.callnum>0)
		{
			debug("is raise 1,call 1");
			return true;
		}
		return false;
	}
	public boolean is(int a, int b) {
		if (handcard[0].getRank() == a && handcard[1].getRank() == b)
			return true;
		if (handcard[1].getRank() == a && handcard[0].getRank() == b)
			return true;
		return false;
	}

	public boolean isSameSuit() {
		if (handcard[0].getSuit() == handcard[1].getSuit())
			return true;
		return false;
	}

	public boolean isSameRank() {
		if (handcard[0].getRank() == handcard[1].getRank())
			return true;
		return false;
	}

	public int getAction() {
		try {
			int activenum = state.getNonFolded();
			int[] hand = state.getHand();
			int[] comm = state.getComm();
			long endMS = System.currentTimeMillis() + time;
			Log.getIns(state.pid).log("clever bot" + activenum);
			ExecutorService threads = Executors.newFixedThreadPool(CORES);
			AtomicInteger won = new AtomicInteger(0);
			AtomicInteger total = new AtomicInteger(0);
			for (int i = 0; i < CORES; i++) {
				MCT thread = new MCT(hand, activenum, comm, endMS, won, total);
				threads.submit(thread);
			}
			threads.shutdown();
			try {
				threads.awaitTermination(1, TimeUnit.MINUTES);
			} catch (Exception e) {
			}
			double add = 0.057;
			if (comm.length >= 5)
				add = 0.001;
			else if (comm.length >= 4)
				add = 0.011;
			else if (comm.length >= 3)
				add = 0.023;
			double prob = won.doubleValue() / total.doubleValue();
			double deficit = 1.0 - prob;
			prob += deficit * add;
			Log.getIns(state.pid).log(
					"time: " + won.doubleValue() + "," + total.doubleValue());
			List<Player> players = state.players;
			int activePlayers = 0;
			int hightocall = 0;
			for (Player p : players) {
				if (p.isAlive())
					activePlayers++;
				hightocall = Math.max(p.getBet(), hightocall);
				debug("high:"+hightocall);
			}
			activePlayers--;
			int activeIncludingSelf = activePlayers + 1;
			int tocall = (int) ((state.getInitjetton() * (prob
					* activeIncludingSelf - 1))
					/ activePlayers * 0.73);
			int maxbet=1;
			int level=0;// 0 low  1 high
			if(state.getInitjetton()>100*state.bigblindbet && me.getGold()>40*state.bigblindbet)
			{
				//  4000  2000
				 level=2; 
				 maxbet= (int) (state.getInitjetton() / 2.2);
			}
			else if(state.getInitjetton()>100*state.bigblindbet)
			{
				// 4000  0
				 maxbet= (int) (state.getInitjetton() / 2.2);
				 level=2;
				 debug("more than 8000");
			}else if(me.getGold()>40*state.bigblindbet)
			{
				//  2000 2000
				level=1;
				maxbet = (int) (state.getInitjetton() / 1.7);
			}else
			{
				// 2000 0
				level=1;
				maxbet = (int) (state.getInitjetton() / 1.8);
			}
			int prebet = 0;
			try {
				prebet = state.getPrebet();
			} catch (Exception e) {
				prebet = 0;
			}
			
			if(((hightocall-prebet)>5*state.bigblindbet)&&(prob<0.85)&&state.getInitjetton()<25*state.bigblindbet&&me.getGold()<5*state.bigblindbet)
			{
				debug("big oppoent less than 1000");
				return State.fold;
			}
			if (tocall > maxbet)
				tocall = maxbet;
			Log.getIns(state.pid).log("hightocall: "+hightocall+" "+
					" tocall " + tocall + " maxbet " + maxbet + " prebet "
							+ prebet + " prob " + prob + "\n");
			Log.getIns(state.pid).log("myprobval:" + state.getMyVal());
			double myval = state.getMyVal();
			double aveval = 0, base = 0,minval=10,maxprob=0;
			try{
				for (Player p : players) {
					if (p.isAlive() && (!p.getPid().equals(state.pid))) {
						int ac[] = new int[state.currentState - State.baseState + 1];
						int ac2[] = new int[state.currentState - State.baseState + 1];
						for (int j = 0; j < ac.length; j++) {
							ac[j] = p.actions[j];
							ac2[j] = p.actions2[j];
						}
						if(state.bys2.get(p.getPid())!=null)
						{
							double pro=(state.bys2.get(p.getPid()).getVal(ac2));
							debug(p.getPid()+": pro b "+(state.bys2.get(p.getPid()).getVal(ac2)));
							if(pro>maxprob)
								maxprob=pro;
						}
						if (ac[ac.length - 1] != Bys.fold) {
							double oppoval = 0;
							if(state.bys.get(p.getPid())!=null)
								oppoval=state.bys.get(p.getPid()).getVal(ac);
							Log.getIns(state.pid).log(
									"val" + p.getPid() + ":" + oppoval);
							if (oppoval > 0 && oppoval < 10) {
								if(oppoval<minval)
									minval=oppoval;
								aveval += oppoval;
								base++;
							}
						}
					}
				}
			}catch(Exception e)
			{
				debug("exception");
				e.printStackTrace();
				base=0;
			}
			double highbet=hightocall;
			
			if (tocall < hightocall) {
				debug("callfold");
				
				if (state.getInitjetton() < hightocall)
					hightocall = state.getInitjetton() - 1;
				double prob1 = prob
						* Math.log(((double) state.getInitjetton() + ((double) activePlayers * hightocall))
								/ state.getInitjetton());
				double prob2 = (1 - prob)
						* Math.log((state.getInitjetton() - hightocall)
								/ ((double) state.getInitjetton()));
				double prob3 = Math
						.log(((double) state.getInitjetton() - prebet)
								/ state.getInitjetton());
				debug("count :" + (prob1 + prob2) + "   " + prob3);
				if(base!=0)
					debug("level:"+level+",base="+base+", ave/base:"+(aveval/base));
				else
					debug("base=0");
				debug("h-p:"+(highbet-prebet)+",15BB:"+(15*state.bigblindbet));
				//must fold
				if((isRaise1Call0()||isRaise1Call1())&&maxprob>0.9)
				{
					debug("must fold");
					return State.fold;
				}
				//scare
				if(highbet-prebet<=state.bigblindbet&&state.raisenum<=1&&maxprob<0.6&&prob>0.15&&prob<0.61)
				{
						debug("just a test");
						State.raisebet=state.bigblindbet;
						return State.raise;
				}
				//protect
				if((isRaise1Call0()||isRaise1Call1())&&prob<0.3)
				{
					debug("protect fold");
					return State.fold;
				}
				if((isCall1()||isCall2())&&maxprob<0.6)
				{
					debug("protect raise");
					State.raisebet=2*state.bigblindbet;
					return State.raise;
				}
				if (prob1 + prob2 >= prob3 &&(level==2||base==0 ||myval<aveval/base+1||prob>maxprob)) {
					{
						
						if((highbet-prebet>20*state.bigblindbet)&&level!=2&&(prob<maxprob||maxprob==0)&&prob<0.58)
						{
							debug("d fold");
							return State.fold;
						}
						else if((prob<0.4)&&highbet-prebet>5*state.bigblindbet&&(maxprob-0.5>prob))
						{
							debug("doubi fold");
							return State.fold;
						}
						else if((highbet-prebet>15*state.bigblindbet)&&(prob<0.8)&&(maxprob==0||maxprob>prob))
						{
							if(maxprob<0.6&&level>=1)
							{
								debug("big call");
								return State.call;
							}
							debug("big oppoent");
							return State.fold;
						}
		
						
						debug("stupid call:" + (prob1 + prob2) + "   " + prob3);
						return State.call;
					}
				} else {
					debug("shaby fold");
					return State.fold;
				}
			} else {
				debug("raisecall");
				//test
				if(state.currentState==State.flopState||state.currentState==State.turnState||state.currentState==State.riverState)
				{
					if(prob>0.93)
					{
						debug("test skill");
						if(highbet-prebet>16*state.bigblindbet&&prebet>=10*state.bigblindbet)
						{
							debug("0.93 call");
							return State.call;
						}
						else if(state.raisenum==1&&maxprob<prob)
						{
							debug("0.93 total");
							State.raisebet=state.totalpot;
							return State.raise;
						}
						else if(state.raisenum==0)
						{
							debug("0.93 raise");
							State.raisebet=6*state.bigblindbet;
							return State.raise;
						}
						
					}
					if(prob>0.8)
					{
						debug("test skill");
						if(highbet-prebet>20*state.bigblindbet&&maxprob>prob)
						{
							debug("0.8 fold");
							return State.fold;
						}
						else if(highbet-prebet>16*state.bigblindbet)
						{
							debug("0.8 call");
							return State.call;
						}
						else if(state.raisenum==1&&maxprob<prob)
						{
							debug("0.8 total");
							State.raisebet=state.totalpot;
							return State.raise;
						}
						else if(state.raisenum==0)
							State.raisebet=4*state.bigblindbet;
						return State.raise;
					}
					if(prob>0.7)
					{
						debug("test skill");
						if(highbet-prebet>16*state.bigblindbet&&maxprob>prob)
						{
							debug("0.7 fold");
							return State.fold;
						}
						else if(highbet-prebet>12*state.bigblindbet)
						{
							debug("0.7 call");
							return State.call;
						}
						else if(state.raisenum==1&&maxprob<prob)
						{
							debug("0.7 toall");
							State.raisebet=(int)(state.totalpot*prob);
							return State.raise;
						}
						else if(state.raisenum==0)
						{
							debug("0.7 raise");
							State.raisebet=3*state.bigblindbet;
							return State.raise;
						}
					}
					if(prob>0.6)
					{
						debug("big bet");
						if(highbet-prebet>10*state.bigblindbet&&maxprob>prob)
						{
							debug("0.6 fold");
							return State.fold;
						}
						else if(highbet-prebet>6*state.bigblindbet)
						{
							debug("0.6 call");
							return State.call;
						}
						else if(state.raisenum==1&&maxprob<prob)
						{
							debug("0.6 raise");
							State.raisebet=(int)(state.totalpot*prob);
							return State.raise;
						}
						else if(state.raisenum==0)
						{
							debug("0.6 raise");
							State.raisebet=2*state.bigblindbet;
							return State.raise;
						}
					}
					if(prob>0.5&&state.raisenum==0&&maxprob<0.8)
					{
						debug("0.5 raise");
						State.raisebet=state.bigblindbet;
						return State.raise;
					}
				}
		
				return State.call;
			}
		} catch (Exception e) {
			debug("exception fold");
			e.printStackTrace();
			return State.call;
		}
	}

	public void debug(String s) {
		try {
			Log.getIns(state.pid).log(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
