package com.bot;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import com.ai.ProbMCT;
import com.ai.ProbValue;
import com.bot.Bot;
import com.game.Card;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class CleverBot implements Bot {
	public static int CORES = 1;
	State state;
	Card handcard[];
	long time;
	Player me = null;
	//除开我的存活数
	int activeplayer = 0;
	//最高需要call的筹码
	double hightocall=0;
	//初始化动作
	int action = State.no;
	//参数初始化
	public void initArgs()
	{
		activeplayer=0;
		for (Player p : state.players) {
			if (p.getPid().equals(state.pid))
				me = p;
			if ((!p.getPid().equals(state.pid)) && p.isAlive())
			{
				activeplayer++;
			}
			hightocall = Math.max(p.getBet(), hightocall);
		}
		debug("active player: " + activeplayer);
	}
	public int getBestAction(State state, long time) {

		//入口
		this.state = state;
		this.time = time;
		this.handcard = state.handcard;
		//参数
		initArgs();
		// 翻牌前
		if (state.currentState == State.baseState)
		{
			return preflop();
		}else
		{//翻牌后
			return postflop();
		}
	}
	public int preflop()
	{
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
	public int getBL() {
		if (state.myloc == State.bigblind) {
			debug("big  blind");
			if (level1()) {
					return State.call;
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
					return State.call;
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
						return State.call;
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
					return State.call;
					
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
					return State.call;

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
					return State.fold;
			}
			if(me.getGold()<5*state.bigblindbet&&state.getInitjetton()<25*state.bigblindbet)
				return State.no;
			if(isCall1()&&state.getInitjetton()>25*state.bigblindbet)
			{
				debug("xiahu");
				State.xiahu=true;
				State.raisebet=(int)(2.2*state.bigblindbet);
				return State.raise;
			}
			if(isFoldAll()||isCall2()&&state.getInitjetton()>80*state.bigblindbet)
			{
				debug("xiahu");
				State.xiahu=true;
				State.raisebet=(int)(2.2*state.bigblindbet);
				return State.raise;
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
			if(is(10,8)||is(10,9)||is(9,8)||is(8,7))
			{
				debug("level 6");
				return true;
			}
		}
		if ( is(13,9)||is(14, 9) || is(14, 8) || is(14, 7) || is(14, 6)
				|| is(14, 5)||is(14,4)||is(14,3)||is(14,2)) {
			debug("level 6");
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

	public int postflop() {
		try {
			int activenum = state.getNonFolded()-1;
			int[] hand = state.getHand();
			int[] comm = state.getComm();
			Log.getIns(state.pid).log("clever bot" + activenum);
			ProbValue  probvalue= new ProbValue(hand, activenum, comm);
			double prob=probvalue.getProb();
			debug("prob: "+prob);
			double add = 0.064;
			if (comm.length >= 5)
				add = 0.001;
			else if (comm.length >= 4)
				add = 0.012;
			else if (comm.length >= 3)
				add = 0.025;
			double deficit = 1.0 - prob;
			prob += deficit * add;
			int activeIncludingSelf = activeplayer + 1;
			int tocall = (int) ((state.getInitjetton() * (prob
					* activeIncludingSelf - 1))
					/ activeplayer * 0.8);
			int maxbet=1;
			maxbet= (int) (state.getInitjetton() / 1.6);
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
						* Math.log(((double) state.getInitjetton() + ((double) activeplayer * hightocall))
								/ state.getInitjetton());
				double prob2 = (1 - prob)
						* Math.log((state.getInitjetton() - hightocall)
								/ ((double) state.getInitjetton()));
				double prob3 = Math
						.log(((double) state.getInitjetton() - prebet)
								/ state.getInitjetton());
				debug("count :" + (prob1 + prob2) + "   " + prob3);
				debug("h-p:"+(highbet-prebet)+",15BB:"+(15*state.bigblindbet));
				//test
				debug("test a");
				double size=Math.sqrt(me.getJetton()/(50.0*state.bigblindbet));
				if(highbet-prebet<=3*size*state.bigblindbet&&(isRaise1Call0()||isRaise1Call1())&&state.raisenum<3&&me.getJetton()>25*state.bigblindbet)
				{
					if(!(me.getJetton()<20*state.bigblindbet&&me.getGold()<4*state.bigblindbet))
					{
						debug("nima");
						State.raisebet=(int)(highbet);
						return State.raise;
					}
				}
				if((isFoldAll()||isCall1()||isCall2() ) )
				{
					if(!(me.getJetton()<15*state.bigblindbet&&me.getGold()<4*state.bigblindbet))
					{
						debug("folmuch0 2.2");
						State.raisebet=(int)(2.2*state.bigblindbet);
						return State.raise;
					}
				}
				if (prob1 + prob2 >= prob3) 
				{
					return State.call;
				}
				else
				{
					return State.fold;
				}
			} else {
				debug("raisecall");
				if(state.currentState==State.flopState||state.currentState==State.turnState||state.currentState==State.riverState)
				{
					if(highbet-prebet==0)
					{
						State.raisebet=(int)(2.2*state.bigblindbet);
						debug("question:"+State.raisebet);
						return State.raise;
					}else if(highbet-prebet>0&&(isRaise1Call0()||isRaise1Call1()))
					{
							debug("question4:");
							State.raisebet=(int)(2.2*state.bigblindbet);
							return State.raise;
					}
				}
				if(highbet<state.getInitjetton()/8&&highbet-prebet<2*state.bigblindbet)
				{
					debug("question3:"+State.raisebet);
					State.raisebet=(int)(2.2*state.bigblindbet);
					return State.raise;
				}
				debug("calla");
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