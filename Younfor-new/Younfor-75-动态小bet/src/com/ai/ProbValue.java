package com.ai;

import java.io.IOException;
import java.util.concurrent.atomic.*;

import com.game.Card;
import com.game.State;
import com.util.Log;

public class ProbValue {
    private final int [] hand;
    private final int [] host;
    private final int [] deck;
    private final int hostlenght;
    private final int alivenum;
   
    public ProbValue (int [] hand, int numOther, int [] partialCommunity) throws IOException {
        this.hand = hand.clone();
        this.host = new int[7];
        this.hostlenght = partialCommunity.length;
        for (int i = 0; i < hostlenght; i ++)
            this.host[i] = partialCommunity[i];
        this.deck = new int[52];
        PokerLib.init_deck(this.deck);
        this.alivenum = numOther;
    }
    public double getProb()
    {
    	double small=0,equal=0,big=0;
    	long timestart=System.currentTimeMillis();
    	int count=0;
    	int mycards[]=new int[hostlenght+2];
    	int oppocards[]=new int[hostlenght+2];
    	//我的牌加上公牌
    	mycards[0]=hand[0];
    	mycards[1]=hand[1];
    	for(int i=0;i<hostlenght;i++)
    	{
    		mycards[i+2]=host[i];
    		oppocards[i+2]=host[i];
    	}
    	double myval=getValue(mycards);
    	for(int i=0;i<51;i++)
    	{
    		//除去公牌重复
			boolean repeat=false;
			for(int k=0;k<hostlenght;k++)
			{
				if(host[k]==deck[i])
				{
					repeat=true;
					break;
				}
			}
			//去除手牌重复
			if(hand[0]==deck[i])
				repeat=true;
			if(repeat)
				continue;
    		for(int j=i+1;j<52;j++)
    		{
    			//除去公牌重复
    			repeat=false;
    			for(int k=0;k<hostlenght;k++)
    			{
    				if(host[k]==deck[j])
    				{
    					repeat=true;
    					break;
    				}
    			}
    			//去除手牌重复
    			if(hand[1]==deck[j])
    				repeat=true;
    			if(repeat)
    				continue;
    			//对手手牌
    			oppocards[0]=deck[i];
    			oppocards[1]=deck[j];
    			//去除垃圾牌
    			if(getPower(oppocards)<6)
    			{
    				continue;
    			}
    			double oppoval=getValue(oppocards);
    			if(myval==oppoval)
    				equal++;
    			else if(myval>oppoval)
    				small++;
    			else
    				big++;
    			//超时保护
    			if(count++%20==0)
    			{
    				if(System.currentTimeMillis()-timestart>200)
    				{
    					debug("timeover");
    					return  (big+equal/2)/(big+small+equal);
    				}
    			}
    		}
    	}
    	return (big+equal/2)/(big+small+equal);
    }
    public double getValue(int card[])
    {
    	if(card.length==5)
    		return PokerLib.eval_5hand(card);
    	else if(card.length==6)
    		return PokerLib.eval_6hand(card);
    	else
    		return PokerLib.eval_7hand(card);
    }
    /**
	 * 根据编码值，解码获得牌值
	 * @param value	编码值
	 * @return		牌值
	 */
	public static int getNumByValue(int value){
		value = value>>16;
		for (int i = 0; i < 13; i++) {
			if ((value&0x01)!=0) {
				return (i+2);
			}
			else {
				value = value>>1; 
			}
		}
		return 2;
	}
	/**
	 * 根据编码值，解码获得牌的花色
	 * @param value	编码值
	 * @return		花色
	 */
	public static int getColorByValue(int value) {
		value = value>>12;
		for (int i = 0; i < 4; i++) {
			if ((value&0x01)!=0) {
				return i;
			}
			else {
				value = value>>1;
			}
		}
		return 0;
	}
	/**
	 * 对两张手牌进行评估
	 * @param OurTwoCards	两张底牌
	 * @return              两张牌的牌力评估值
	 */
	public static double getPower(int[] hand){
		float result = 0;
		float result_temp = 0;
		int n1 = getNumByValue(hand[0]);
		int n2 = getNumByValue(hand[1]);
		
		int c1 = getColorByValue(hand[0]);
		int c2 = getColorByValue(hand[1]);
		
		int big=n1>n2?n1:n2;
		switch (big) {
		case 14:{
			result_temp = 10;
		}break;
		case 13:{
			result_temp = 8;
		}break;			
		case 12:{
			result_temp = 7;
		}break;	
		case 11:{
			result_temp = 6;
		}break;	
		default:{
			result_temp = (float) (big/2.0);
		}break;	
		}
		if (n1==n2) {	//对子
			result = result_temp*2;
			if (result<5) {
				result = 5;
			}
			return result;
		}
		else {		//非对子
			if (c1==c2) {	//同种花色
				result = result_temp+2;
			}
			int off = Math.abs(n1-n2);
			switch (off) {
			case 1:{
				result = result_temp+0;
				if ((n1<12)&&(n2<12)) {
					result += 1;
				}
			}break;
			case 2:{
				result = result_temp - 1;
				if ((n1<12)&&(n2<12)) {
					result += 1;
				}
			}break;
			case 3:{
				result = result_temp - 2;
			}break;			
			case 4:{
				result = result_temp - 4;
			}break;	
			default:{
				result = result_temp - 5;
			}break;
				
			}
		}
		return result;
	}
    public static void main(String []args) throws IOException
    {
    	PokerLib.init();
    	ProbValue p=new ProbValue(new int[]{1,2},2, new int[]{1,2,3});
    	Card c1=new Card("SPADES","Q");
    	//Card c2=new Card("SPADES","8");
    	Card c2=new Card("HEARTS","Q");
    	System.out.println(p.getPower(new int[]{c1.getValue(),c2.getValue()}));
    	c1=new Card("SPADES","6");
    	c2=new Card("SPADES","7");
    	System.out.println(p.getPower(new int[]{c1.getValue(),c2.getValue()}));
    }
    public void debug(String s) {
		try {
			Log.getIns("8888").log(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
