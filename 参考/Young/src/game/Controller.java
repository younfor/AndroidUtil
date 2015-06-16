package game;

public class Controller {
	public static int myVirstualJetton;
	public static boolean isAllFold=false;
	public static boolean win1=false;
	public static boolean isNoFold=false;
//	private int myMoney;
	
	/*preFlop限制*/
	public static int maxBetForPreCall;
	public static boolean isHandRadical=false;
	public static boolean reduceRate=false;
	public static int preFoldScore;
	public static int preRaiseScore;
	
	/* 胜率限制,赌注限制*/
	
	public static double flopFoldRate;
	public static double turnFoldRate;
	public static double riverFoldRate;
	
	public static double flopRaiseRate;
	public static double turnRaiseRate;
	public static double riverRaiseRate;
	
	public static int maxBetRandomFoldOrCall;

	public static int maxBetCallRank1;
	public static int maxBetCallRank2;

	public static int maxSumBetRank1;
	public static int maxSumBetRank2;
	public static int maxSumBetRank3;
	public static int raiseBet;

	public static boolean limitMoney100bb=false;
	
	public static void setLimits(){
		
		limitMoney();
	
		//对手分析看他玩得大不大，重点修改这个值
//		maxBetForPreCall=(int)Opponent.maxAveRaiseBetOfPalyers("preFlop");//跟注限制，人加注100以内
		
		preFoldScore=6;
		//玩牌最低胜率
		flopFoldRate=0.3;
		turnFoldRate=0.5;
		riverFoldRate=0.6;
		
		flopRaiseRate=0.85;
		turnRaiseRate=0.9;
		riverRaiseRate=0.95;
		
		//CallBet限制
		maxBetRandomFoldOrCall=(int)Math.round(Game.BB*1.26+myVirstualJetton*0.05);//50以内的赌注可以看一下下一张
	
		maxBetCallRank1=(int)Math.round(Game.BB*2.28+myVirstualJetton*0.05)*2;//0.4
	
		maxBetCallRank2=(int)Math.round(Game.BB*2.28+myVirstualJetton*0.05)*3;//0.4
		

		maxSumBetRank1=(int)Math.round(myVirstualJetton*0.75);//加注时所投赌注总额限制
	
		maxSumBetRank2=(int)Math.round(myVirstualJetton);
		
		maxSumBetRank3=(int)Math.round(myVirstualJetton*2);
		if(Math.random()>0.5){
			raiseBet=(int)Math.round(Game.BB*2+Round.myJetton*0.05);//初始预设加注
		}else if(Math.random()>0.2){
			raiseBet=(int)Math.round(Game.BB*2+Round.myJetton*0.08);//初始预设加注
		}else raiseBet=(int)Math.round(Game.BB*2+Round.myJetton*0.1);//初始预设加注
	
	
	}
	
	public static void limitMoney(){
		if(Round.myMoney+Round.myJetton>(4.1*(Game.JETTON+Game.MONEY)+(Game.ROUNDTIME-Round.roundCount)*(Game.BB+Game.SB)/3.0)){
			isAllFold=true;
		}
		isNoFold=false;
		if(limitMoney100bb){
			if(Round.myJetton>20000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.3);//8000+
			}else	if(Round.myJetton>15000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.4);//8000+
			}else if(Round.myJetton>10000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.5);//4000~8000
			}else if(Round.myJetton>5000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.4);//1500~3000
			}else if(Round.myJetton>3000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.4);//900~1500
			}else if(Round.myJetton>2000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.35);//300~600
			}else if(Round.myJetton>1000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.3);//300~600
			}else if(Round.myJetton>500){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.5);//250~500
			}else myVirstualJetton=Round.myJetton;
		}else{ 
			if(Round.myJetton>15000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.3);
			}else if(Round.myJetton>10000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.35);
			}else if(Round.myJetton>5000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.35);
			}else if(Round.myJetton>3000){
				myVirstualJetton=Round.myJetton-2000;//600~900
			}else if(Round.myJetton>1000){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.3);//300~600
			}else if(Round.myJetton>500){
				myVirstualJetton=(int)Math.round(Round.myJetton*0.5);//250~500
			}else myVirstualJetton=Round.myJetton;
			
//			if(Round.myMoney==2000){
////				myVirstualJetton=Round.myJetton;
//				if(Round.myJetton>3000){
//					myVirstualJetton=3000;
//				}else myVirstualJetton=Round.myJetton;
//			}
//			if(Round.myMoney+Round.myJetton>=5000){
//				 Learn.allCheckToRaiseStrategy=true;
//			}
		}
	}
	
}
