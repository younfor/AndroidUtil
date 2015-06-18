package com.bot;

import java.io.IOException;
import java.util.*;
import com.ai.ProbValue;
import com.bot.Bot;
import com.game.Card;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class CleverBot implements Bot {

	State state;
	//手牌
	Card handcard[];
	long time;
	//我
	Player me = null;
	//胜率
	double prob=0;
	//除开我的存活人
	int activeplayer = 0;
	//最高跟住
	double hightobet=0,prebet=0;
	//手牌
	double level=0;
	public int getBestAction(State state, long time) {

		//初始化
		this.state = state;
		this.time = time;
		this.handcard = state.handcard;
		int hightocall = 0;
		for (Player p : state.players) {
			if (p.getPid().equals(state.pid))
				me = p;
			hightocall = Math.max(p.getBet(), hightocall);
		}
		hightobet=hightocall;
		debug("hightobet:"+hightobet);
		activeplayer=state.getNonFolded()-1;
		debug("activeplayer:"+activeplayer);
		//求手牌范围
		level=ProbValue.getPower(new int[]{handcard[0].getValue(),handcard[1].getValue()});
		debug("level:"+level);
		//翻牌前策略
		if(state.currentState==State.baseState)
			return getPreAction();
		else{
		//翻牌后策略
			return getAction();
		}
	}

	//翻牌前策略
	public int getPreAction()
	{
		//关煞位置
		if(state.seatplayer==me.position)
		{
			debug("关煞位");
			if((isCall1()||isCall2()||isFoldAll()))
			{
				State.raisebet=5*state.bigblindbet;
				return State.raise;
			}
			if(level>6&&(isRaise1Call1()))
				return State.call;
		}
		//按钮位置
		if(state.myloc==State.button)
		{
			debug("按钮位置");
			if((isCall1()||isCall2()||isFoldAll()))
			{
				State.raisebet=3*state.bigblindbet;
				return State.raise;
			}
			if((isRaise1Call0()||isRaise1Call1())&&level>6&&hightobet-prebet<=3*state.bigblindbet)
			{
				State.raisebet=2*state.bigblindbet;
				return State.raise;
			}
			if(level>8)
				return State.call;
		}
		//大盲位置
		if(state.myloc==State.bigblind)
		{
			debug("大盲");
			if((isCall1()||isCall2()||isFoldAll()))
			{
				return State.check;
			}
			if(isRaise1Call1()&&hightobet-prebet<3*state.bigblindbet&&level>6)
			{
				State.raisebet=3*state.bigblindbet;
				return State.raise;
			}
		}
		//小盲位置
		if(state.myloc==State.smallblind)
		{
			debug("小盲");
			if((isCall1()||isCall2()||isFoldAll()||isRaise1Call1())&&level>=5)
			{
				State.raisebet=state.bigblindbet;
				return State.raise;
			}
		}
		//枪口位置
		if(state.myloc==State.EP)
		{
			debug("枪口");
			if((isCall1()||isCall2()||isFoldAll())&&level>=8)
			{
				State.raisebet=state.bigblindbet;
				return State.raise;
			}
		}
		//中间位置
		if(state.myloc==State.MP)
		{
			debug("中间");
			if((isCall1()||isCall2()||isFoldAll()||isRaise1Call1())&&level>=6)
			{
				State.raisebet=state.bigblindbet;
				return State.raise;
			}
		}
		
		return State.fold;
	}

	//翻牌后策略
	public int getAction() {
		try {
				if(raise())
				{
					debug("raise");
					if(hightobet-prebet<3*state.bigblindbet)
					{
						debug("raise:"+State.raisebet);
						State.raisebet=(int)(2.2*state.bigblindbet);
						return State.raise;
					}
					else
						return State.call;
				}else
				{
					//吓唬
					debug("吓唬");
					double size=Math.sqrt(me.getJetton()/(50.0*state.bigblindbet));
					if(hightobet-prebet<=3*size*state.bigblindbet&&(isRaise1Call0()||isRaise1Call1())&&state.raisenum<3&&me.getJetton()>25*state.bigblindbet)
					{
							if(!(me.getJetton()<20*state.bigblindbet&&me.getGold()<4*state.bigblindbet))
							{
								debug("nima");
								State.raisebet=(int)(hightobet);
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
					if(ev())
					{
						debug("call");
						return State.call;
						
					}else
					{
						debug("fold");
						return State.fold;
						
					}
				}
		} catch (Exception e) {
			debug("exception fold");
			e.printStackTrace();
			return State.call;
		}
	}
    //判断期望
	public boolean ev()
	{
		double hightocall=hightobet;
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
		debug("EV :" + (prob1 + prob2) + "   " + prob3);
		if(prob1+prob2>prob3)
			return true;
		else
			return false;
	}
    //判断是否够加注
	public boolean raise() throws IOException
    {
		int[] hand = state.getHand();
		int[] comm = state.getComm();
		ProbValue  probvalue= new ProbValue(hand, activeplayer, comm);
		//得到我的胜率
		prob=probvalue.getProb();
		double add = 0.064;
		if (comm.length >= 5)
			add = 0.001;
		else if (comm.length >= 4)
			add = 0.012;
		else if (comm.length >= 3)
			add = 0.025;
		double deficit = 1.0 - prob;
		prob += deficit * add;
		double hightocall = hightobet;
		int activeIncludingSelf = activeplayer + 1;
		int tocall = (int) ((state.getInitjetton() * (prob
				* activeIncludingSelf - 1))
				/ activeplayer * 0.8);
		int maxbet=1;
		maxbet= (int) (state.getInitjetton() / 1.6);
		prebet = 0;
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
		if (tocall < hightocall) 
			return false;
		else 
			return true;
    }
    //一些常用函数
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
	public void debug(String s) {
		try {
			Log.getIns(state.pid).log(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}