package com.ai;

public class Opponent {
//对手模型
	
	//任何位置
	public double raisenum[]=new double[4]; //翻牌前,翻牌后,转牌,河牌 加注数
	public double foldnum[]=new double[4]; //翻牌前,翻牌后,转牌,河牌 弃牌数
	public double callnum[]=new double[4]; //翻牌前,翻牌后,转牌,河牌 跟注数
	//加注量
	public double raisebet[]=new double[4];//翻牌前,后,转牌,河牌,加注量
	public double getAveBet(int i)
	{
		return raisebet[i]/(raisenum[i]+0.1);
	}
	/*
	 * Attempt to steal blinds 
	 * 我们说一位牌手在偷盲注指的是牌手在CO位或是Button位时，他前面的牌手都盖牌，而这时他开始加注。以Poker Tracker的说法看，他是在尝试着利用他的位置优势来偷走或是赢得盲注。这个有助于我们分辨出那些在后位悄悄加注的牌手（他们的ASB%会比其他的牌手要高），所以我们也可以偷偷的对抗这些牌手。
	 */
	public double ASB=0;
	/*
	 * Preflop raise % (PFR):【15%-19%】 
　　指某位牌手在翻牌前加注的频率。如果频率越高，那么对手的牌风也就越凶。将PFR同VPIP组合起来分析就是你捕获fish的主要数据。
 		计算方法: 统计翻牌前加注的次数   (raise+all_in)/(raise+fold+call+check)
	 */
	public double PFR=0;
	public double getPFR(int i)
	{
		return  (raisenum[i])/(raisenum[i]+callnum[i]+foldnum[i]+1);
	}

}
