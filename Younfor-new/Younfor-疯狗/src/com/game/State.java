package com.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.ai.Bys;

public class State {
	public final static int button=10,smallblind=11,bigblind=12,normal=13;
	public final static int blind=30,check=31,call=32,raise=33,all_in=34,fold=35,no=36;
	public final static int baseState=40,flopState=41,turnState=42,riverState=43;
	public final static int LP=50,MP=51,EP=52;
	public static boolean isRegistered=false;
	//new oppo
	public List<PlayerAction> oppotype=new ArrayList<PlayerAction>();
	public static int getAction(String s)
	{
		if(s.equals("blind"))
			return blind;
		else if(s.equals("check"))
			return check;
		else if (s.equals("call"))
			return call;
		else if (s.equals("raise"))
			return raise;
		else if (s.equals("all_in"))
			return all_in;
		else if (s.equals("fold"))
			return fold;
		else
			return -1;
	}
	
	//AI
	public static int handnum=0;
	public String pid,pname;//
	public boolean isFold=false;
	public int bigblindbet,smallblindbet,totalpot;//
	public Card handcard[]=new Card[2];//
	public Card hostcard[]=new Card[5];//
	public int currentState=-1;//
	private int[] hand;//
	private int[] comm;//
	private int jetton,bet,prebet,initjetton;//
	public int initGold=0;
	public static int raisebet=0; //show action raise
	public int myraisenum=0;
	//public Map<String,Integer[]> actions=new HashMap<String, Integer[]>();
	public int raisenum=0,callnum=0;
	public int seatplayer=0;
	public int myloc=0;
	public List<Player>  players=new ArrayList<Player>();//
	//bys- save to game over !!!!!!!!
	public Map<String,Bys> bys=new HashMap<String, Bys>();
	// bys prob
    public static AtomicInteger [] ranknum=new AtomicInteger[10];
    public double getMyVal()
    {
		double total=0,up=0;
		for(int i=1;i<10;i++)
		{
			total+=State.ranknum[i].doubleValue();
			up+=State.ranknum[i].doubleValue()*i;
		}
		return up/total;
    }
	
	public void clear()
	{
		State.ranknum=new AtomicInteger[10];
		for(int i=0;i<10;i++)
			State.ranknum[i]=new AtomicInteger();
		bigblindbet=0;
		seatplayer=0;
		smallblindbet=0;
		handcard=new Card[2];
		hostcard=new Card[5];
		totalpot=0;
		hand=null;
		//initGold=0;
		comm=null;
		isFold=false;
		raisenum=0;
		callnum=0;
		myraisenum=0;
		myloc=State.LP;
		jetton=0;
		bet=0;
		prebet=0;
		raisebet=0;
		players.clear();
	}
	
	public int getInitjetton() {
		return initjetton;
	}

	public void setInitjetton(int initjetton) {
		this.initjetton = initjetton;
	}

	public int getPrebet() {
		return prebet;
	}

	public void setPrebet(int prebet) {
		this.prebet = prebet;
	}

	public int getToCall() {
		return bet;
	}
	public void setToCall(int bet) {
		this.bet = bet;
	}
	public int getJetton()
	{
		return jetton;
	}
	public void setJetton(int n)
	{
		this.jetton=n;
	}
	public int getNonFolded()
	{
		int i=0;
		for(Player p:players)
		{
			if(p.isAlive())
				i++;
		}
		return i;
	}
	public int[] getHand()
	{
		return hand;
	}
	public void setHand(int h1,int h2)
	{
		hand=new int[2];
		hand[0]=h1;
		hand[1]=h2;
	}
	public int[] getComm()
	{
		return comm;
	}
	public void setComm(int n) throws IOException
	{
		comm=new int[n];
		for(int i=0;i<n;i++)
			comm[i]=hostcard[i].getValue();
	}
	public int findRank(String s)
	{
		if(s.equals("STRAIGHT_FLUSH"))
			return 1;
		else if(s.equals("FOUR_OF_A_KIND"))
			return 2;
		else if(s.equals("FULL_HOUSE"))
			return 3;
		else if(s.equals("FLUSH"))
			return 4;
		else if(s.equals("STRAIGHT"))
			return 5;
		else if(s.equals("THREE_OF_A_KIND"))
			return 6;
		else if(s.equals("TWO_PAIR"))
			return 7;
		else if(s.equals("ONE_PAIR"))
			return 8;
		else
			return 9;
	}
}
