package com.bot;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

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
	double aveval = 0, base = 0,minval=10,maxprob=0,maxaverbet=0;
	boolean raisemuch=false,raisefold=false;
	public int getBestAction(State state, long time) {

		this.state = state;
		this.time = time;
		this.handcard = state.handcard;
		int action = State.no;
		int prePlayerActon = State.no;
		activeplayer = 0;
		double hightocall=0,enterprob=0;
		for (Player p : state.players) {
			if (p.getLastaction() != State.fold && prePlayerActon == State.no)
				prePlayerActon = p.getLastaction();
			if (p.getPid().equals(state.pid))
				me = p;
			oppojetton=0;
			if ((!p.getPid().equals(state.pid)) && p.isAlive())
			{
				debug("jetton:"+p.getJetton()+":"+p.getLastaction());
				if(state.bys2.get(p.getPid())!=null&&state.bys2.get(p.getPid()).enterpotnum/State.seatnum>enterprob)
					enterprob=state.bys2.get(p.getPid()).enterpotnum/State.seatnum;
				//debug("enter prob:"+enterprob);
				if(oppojetton<p.getJetton())
					oppojetton=p.getJetton();
				debug("oppojetton:"+oppojetton);
				activeplayer++;
			}
			State.enterprob=enterprob;
			//debug(p.getPid()+":"+p.getBet());
			hightocall = Math.max(p.getBet(), hightocall);
		}
		debug("active player: " + activeplayer);
		aveval = 0;
		base = 0;
		minval=10;
		maxprob=0;
		maxaverbet=0;
		raisemuch=false;
		raisefold=false;
		try{
			for (Player p : state.players) {
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
						double averaisebet=0,maxraisebet=0;
						if(pro>maxprob)
							maxprob=pro;
						if(state.currentState==State.baseState)
						{
							averaisebet=state.bys2.get(p.getPid()).preflopbet/state.bys2.get(p.getPid()).preflopnum;
							maxraisebet=state.bys2.get(p.getPid()).maxpreflopbet;
							if((averaisebet+maxraisebet)/2.0<(p.getBet()-state.getPrebet()))
								raisefold=true;
							if(averaisebet*1.04<(p.getBet()-state.getPrebet()))
									raisemuch=true;
							
						}
						if(state.currentState==State.flopState)
						{
							maxraisebet=state.bys2.get(p.getPid()).maxflopbet;
							if((averaisebet+maxraisebet)/2.0<(p.getBet()-state.getPrebet()))
								raisefold=true;
							averaisebet=state.bys2.get(p.getPid()).flopbet/state.bys2.get(p.getPid()).flopnum;
							if(averaisebet*1.05<(p.getBet()-state.getPrebet()))
									raisemuch=true;
						}
						if(state.currentState==State.turnState)
						{
							maxraisebet=state.bys2.get(p.getPid()).maxturnbet;
							if((averaisebet+maxraisebet)/2.0<(p.getBet()-state.getPrebet()))
								raisefold=true;
							averaisebet=state.bys2.get(p.getPid()).turnbet/state.bys2.get(p.getPid()).turnnum;
							if(averaisebet*1.06<(p.getBet()-state.getPrebet()))
									raisemuch=true;
						}
						if(state.currentState==State.riverState)
						{
							maxraisebet=state.bys2.get(p.getPid()).maxriverbet;
							if((averaisebet+maxraisebet)/2.0<(p.getBet()-state.getPrebet()))
								raisefold=true;
							averaisebet=state.bys2.get(p.getPid()).riverbet/state.bys2.get(p.getPid()).rivernum;
							if(averaisebet*1.07<(p.getBet()-state.getPrebet()))
									raisemuch=true;
						}
						if(averaisebet>maxaverbet)
							maxaverbet=averaisebet;
						debug("raisefold: "+raisefold+" raisemuch:"+raisemuch+"averraise:"+averaisebet+",raise:"+(p.getBet()-state.getPrebet()));
						debug("maxraisebet:"+maxraisebet);
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
		// previous-flop
		if (state.currentState == State.baseState) {
			if((!level1())&&raisefold&&hightocall>5*state.bigblindbet)
				return State.fold;
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
				if(!raisemuch)
					State.raisebet = (int)(2.5*state.bigblindbet) ;
				else
					return State.call;
				
				return State.raise;
				
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					if(!raisemuch)
						State.raisebet =(int)(2.5*state.bigblindbet) ;
					else
						return State.call;
					return State.raise;
				}
				if(isRaise1Call1())
				{
					if(!raisemuch)
						State.raisebet = (int)(2.5*state.bigblindbet) ;
					else
						return State.call;
					return State.raise;
				}
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					if(!raisemuch)
						State.raisebet = state.bigblindbet ;
					else
						return State.call;
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
					State.raisebet=(int)(2.5*state.bigblindbet) ;
					return State.raise;
					//return State.check;
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
				if(!raisemuch)
				{
					return State.call;
				}
				else
					return State.fold;
				
			}
			if(isFoldAll()||isCall1()||isCall2())
			{
				return State.check;
			}
		}
		return State.no;
	}

	public int getEP() {
		if (state.myloc == State.EP) {
			debug("EP");
			if (level1()) {
					State.raisebet = 2*state.bigblindbet;
					return State.raise;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					debug("foldall call1 call2");
					State.raisebet=state.bigblindbet;
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
					{
						return State.fold;
					}
				if(isRaise1Call1()&&isSameSuit()&&is(13,12))
					return State.call;
			}
			if (level4()) {
				if(isFoldAll()||isCall1()||isRaise1Call0())
				{
					return State.fold;
				}
				if(isCall2()||isRaise1Call1())
					return State.call;
			}
			if (level5()) {
				if(isRaise1Call0()||isRaise1Call1())
				{
					return State.fold;
				}
				return State.call;
			}
		}
		return State.no;
	}

	public int getMP() {
		if (state.myloc == State.MP) {
			debug("MP");
			if (level1()) {
					State.raisebet = 2* state.bigblindbet;
					return State.raise;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
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
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					State.raisebet=state.bigblindbet;
					return State.raise;
				}
				if(isRaise1Call0())
				{
					return State.fold;
				}
				if(isRaise1Call1()&&isSameSuit()&&is(13,12))
					return State.call;
			}
			if (level4()) {
				if(isFoldAll()||isCall1()||isRaise1Call0())
				{
					return State.fold;
				}
				if(isCall2()||isRaise1Call1())
					return State.call;
			}
			if ( level5()) {
				if(isRaise1Call0()||isRaise1Call1())
				{
					return State.fold;
				}
				return State.call;
			}
		}
		return State.no;
	}

	public int getLP() {
		if (state.myloc == State.LP) {
			debug("LP");
			if (level1()) {
					return State.call;
			}
			if (level2()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					debug("foldall call1 call2");
					State.raisebet=(int)(2.5*state.bigblindbet);
					return State.raise;
				}
				if(isRaise1Call0()||isRaise1Call1())
				{
					debug("foldall call1 call2");
					if(!raisemuch)
						State.raisebet=4*state.bigblindbet;
					else
						return State.call;
					return State.raise;
				}
			}
			if (level3()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					debug("foldall call1 call2");
					State.raisebet=(int)(2.5*state.bigblindbet);
					return State.raise;
				}
				if(isRaise1Call0()||isRaise1Call1())
				{
					debug("foldall call1 call2");
					if(!raisemuch)
						State.raisebet=4*state.bigblindbet;
					else
						return State.call;
					return State.raise;
				}
			}
			if (level4()) {
				if(isFoldAll()||isCall1()||isCall2())
				{
					debug("foldall call1 call2");
					State.raisebet=(int)(2.5*state.bigblindbet);
					return State.raise;
				}
				if(isRaise1Call0()||isRaise1Call1())
				{
					debug("foldall call1 call2");
					if(!raisemuch)
						State.raisebet=4*state.bigblindbet;
					else
						return State.call;
					return State.raise;
				}
			}
			if (level5()) {
				if(isFoldAll())
				{
					debug("testnew");
					State.raisebet=3*state.bigblindbet;
					return State.raise;
				}
				if(isCall1()||isCall2())
				{
					return State.call;
				}
				if(!raisemuch)
					return State.call;
				else
					return State.fold;
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
		
		if(state.getInitjetton()<25*state.bigblindbet)
		{
			debug("level 5 but less than 1000");
			return false;
		}
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
			double add = 0.064;
			if (comm.length >= 5)
				add = 0.001;
			else if (comm.length >= 4)
				add = 0.012;
			else if (comm.length >= 3)
				add = 0.025;
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
					/ activePlayers * 0.8);
			int maxbet=1;
			maxbet= (int) (state.getInitjetton() / 1.8);
			int prebet = 0;
			try {
				prebet = state.getPrebet();
			} catch (Exception e) {
				prebet = 0;
			}
			if (tocall > maxbet)
				tocall = maxbet;
			Log.getIns(state.pid).log("hightocall: "+hightocall+" "+
					" tocall " + tocall + " maxbet " + maxbet + " prebet "
							+ prebet + " prob " + prob + "\n");
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
				debug("h-p:"+(highbet-prebet)+",15BB:"+(15*state.bigblindbet));
				if (prob1 + prob2 >= prob3) 
				{
					if(raisefold&&highbet>state.getInitjetton()/5&&prob<maxprob+0.1)
						return State.fold;
					return State.call;
				}else if((state.raisenum==0&&highbet-prebet==0))
				{
					debug("shaby call");
					return State.call;
				}else if(prob>maxprob-0.1&&(!raisemuch)&&(!raisefold))//if(prob>maxprob-0.1&&((!raisemuch)||(!raisefold)))
					return State.call;
				else if(prob>maxprob&&raisefold)
					return State.call;
				else if(prob>maxprob-0.05&&(!raisefold))
					return State.call;
				else
				{
					return State.fold;
				}
			} else {
				debug("raisecall");
				double size=1,scale=state.getInitjetton()/(50.0*state.bigblindbet);
				size=Math.sqrt(scale);
				debug("size:"+size);
				State.raisebet=(int)(size*tocall/(1+maxprob));
				//test
				if(state.currentState==State.flopState||state.currentState==State.turnState||state.currentState==State.riverState)
				{
					if(highbet-prebet==0)
					{
						State.raisebet=(int)(state.totalpot*prob);
						return State.raise;
					}else if(highbet-prebet>0&&(isRaise1Call0()||isRaise1Call1())&&(!raisemuch)&&(!raisefold))
							return State.raise;
						else if(raisefold)
							return State.call;
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
