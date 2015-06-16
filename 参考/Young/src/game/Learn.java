package game;

import java.util.HashSet;

public class Learn {
	
	public static int flopCount=0;
	
	
	public static int noShowDownWinCount=0;
	public static int showDownWinCount=0;
	
	public static boolean allCheckToRaiseStrategy=true;
	public static boolean onCardallCheckToRaiseStrategy=true;
	public static boolean allCheckToRaiseRound=true;
	public static  boolean notFailRaiseStrategy=true;
	public static int allCheckToRaiseRoundBets=0;
	public static int allCheckToRaiseTotalWin=0;
	public static int allCheckToRaiseWinCount=0;
	public static int allCheckToRaiseFoldCount=0;
	public static int allCheckToRaiseCount=0;
	public static int riverRaiseFanBeiTime=0;
	public static int turnRaiseFanBeiTime=0;
	
	public static boolean preRandomRaise=false;
	
	public static HashSet<String>threatAllCheckToRaiseStrategyPlayers=new HashSet<String>();
	
	public static void putAllCheckToRaiseWin(){
		
	}
	public static void addNoShowDownWin(String winMoney){
		noShowDownWinCount++;
		if(allCheckToRaiseRound){
			allCheckToRaiseWinCount++;
			System.out.println("allCheckToRaiseWinRound:"+Round.roundCount);
			System.out.println("allCheckToRaiseWinPosition:"+Round.getMyposition());
		}
	}
	public static void addShowDownWin(String winMoney){
		showDownWinCount++;
	}
	public static void addFold(String ofWaht){
		if(allCheckToRaiseRound){
			allCheckToRaiseFoldCount++;
			System.out.println("allCheckToRaiseFoldRound:"+Round.roundCount);
			System.out.println("allCheckToRaiseFoldPosition:"+Round.getMyposition());
		}
	}
	
	
	public  static void roundReset(){
		
		allCheckToRaiseRound=false;
		allCheckToRaiseRoundBets=0;
		if((Round.roundCount&15)==0){
			for(String id :Opponent.opponents.keySet()){
				if((double)(Opponent.opponents.get(id).reRaiseMeOnAllCheckToRaiseCount/allCheckToRaiseCount)>=0.25
						||Opponent.opponents.get(id).callMeOnAllCheckToRaiseCount/allCheckToRaiseCount>=0.4){
					threatAllCheckToRaiseStrategyPlayers.add(id);
				}else if(threatAllCheckToRaiseStrategyPlayers.contains(id)){
					threatAllCheckToRaiseStrategyPlayers.remove(id);
				}
			}
			
		}
//		if((Round.roundCount&63)==1){
//			allCheckToRaiseStrategy=true;
//		}
		
		/*
		 * 动态开启,注释关闭哦
		 */
//		if((Round.roundCount&31)==0){
////			System.out.println("******************************************");
//			allCheckToRaiseStrategy=true;
//			onCardallCheckToRaiseStrategy=true;
//			if(!threatAllCheckToRaiseStrategyPlayers.isEmpty()){
//				System.out.println("threatAllCheckToRaiseStrategyPlayers:"+threatAllCheckToRaiseStrategyPlayers);
//				for(String id:threatAllCheckToRaiseStrategyPlayers){
//					if(Opponent.opponents.get(id).isAlive&&(double)Opponent.opponents.get(id).flopReRaiseToFoldCount/Opponent.opponents.get(id).flopRaiseCount<0.2){
//						allCheckToRaiseStrategy=false;
//						System.out.println("allCheckToRaiseStrategy set false...");
//					}else {
//						threatAllCheckToRaiseStrategyPlayers.remove(id);
//					}
//				}
//			
//			}
//			if(Round.getAliveNum()<=3){
//				allCheckToRaiseStrategy=false;
//			}
//		}
		if(Round.roundCount%100==0){
			System.out.println("allCheckToRaiseWinCount:"+allCheckToRaiseWinCount);
			System.out.println("allCheckToRaiseFoldCount:"+allCheckToRaiseFoldCount);
			System.out.println("allCheckToRaiseSuccessRate:"+(double)allCheckToRaiseWinCount/allCheckToRaiseFoldCount);
		}
	}	
	
	public  static void gameOver(){
		System.out.println("flopCount:"+flopCount);
		System.out.println("showDownWinCount:"+showDownWinCount);
		System.out.println("noShowDownWinCount:"+noShowDownWinCount);
		System.out.println("allCheckToRaiseWinCount:"+allCheckToRaiseWinCount);
		System.out.println("allCheckToRaiseFoldCount:"+allCheckToRaiseFoldCount);
		System.out.println("allCheckToRaiseCount:"+allCheckToRaiseCount);
		System.out.println("flopfanbei:"+Learn.turnRaiseFanBeiTime);
		System.out.println("riverfanbei:"+Learn.riverRaiseFanBeiTime);
	}	
	
}
