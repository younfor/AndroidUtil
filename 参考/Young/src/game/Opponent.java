package game;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

public class Opponent implements Comparable<Opponent>{
	
	private String id;
	private String Rank;
	public  boolean isAlive=false;//位次信息已經將活着置爲true;
	
	   
	DecimalFormat  df = new DecimalFormat("#.00");
   
  
	private int jetton=Game.JETTON;
	private int money=Game.MONEY;
	
	public  int zpreRaiseCount=0;
	public  double zpreRaiseBet=0;
	public  int preRaiseCount=0;
	public int preCallCount=0;
	public double preCallBet=0;
	public int preCall;
	public int preFoldCount=0;
	public int preAllIn;
	public int preRaiseOnPosition[]=new int[8];
	public int preReRaiseOnPosition[]=new int[8];
	public int preReRaiseToFoldCount=0;//这个是raise过,后面fold的统计
	
	public int MpreRaiseOnPossitionCount=0;
	public boolean MpreReRaiseOnPossition=false;
	public int MpreReRaiseSuccessCount=0;
	
	public  int zflopRaiseCount=0;
	public  double zflopRaiseBet=0;
	public  int flopRaiseCount=0;
	public int flopCallCount=0;
	public double flopCallBet=0;
	public int flopCall;
	public int flopFoldCount=0;
	public int flopAllIn;
	public int flopReRaiseToFoldCount=0;
	public int flopRaiseOnPosition[]=new int[8];
	public int flopReRaiseOnPosition[]=new int[8];
	
	public  int zturnRaiseCount=0;
	public  double turnRaiseBet=0;
	public  int turnRaiseCount=0;
	public int turnCallCount=0;
	public double turnCallBet=0;
	public int turnCall;
	public int turnFoldCount=0;
	public int turnAllIn;
	public int turnReRaiseToFoldCount=0;
	
	public  int zriverRaiseCount=0;
	public  double riverRaiseBet=0;
	public  int riverRaiseCount=0;
	public int riverCallCount=0;
	public double riverCallBet=0;
	public int riverCall;
	public int riverFoldCount=0;
	public int riverAllIn;
	public int riverReRaiseToFoldCount=0;
	
	public int reRaiseMeOnAllCheckToRaiseCount=0;
	public int callMeOnAllCheckToRaiseCount=0;
	public int foldMeOnAllCheckToRaiseCount=0;
	
	public int flopCount=0;
	public int turnCount=0;
	public int riverCount=0;
	
	public int showDownWinCount=0;
	public int showDownCount=0;
	public int noShowDownWinCount=0;

	public int roundPosition=0;
	public boolean raiseRound=false;
	public boolean actioned=false;
	
	private boolean preraiseBetNum3Reset=false;
	private boolean preraiseBetNum2Reset=false;
	private boolean flopraiseBetNum3Reset=false;
	private boolean flopraiseBetNum2Reset=false;
	private boolean turnraiseBetNum3Reset=false;
	private boolean turnraiseBetNum2Reset=false;
	private boolean riverraiseBetNum3Reset=false;
	private boolean riverraiseBetNum2Reset=false;
	private boolean precallBetNum3Reset=false;
	private boolean precallBetNum2Reset=false;
	private boolean flopcallBetNum3Reset=false;
	private boolean flopcallBetNum2Reset=false;
	private boolean turncallBetNum3Reset=false;
	private boolean turncallBetNum2Reset=false;
	private boolean rivercallBetNum3Reset=false;
	private boolean rivercallBetNum2Reset=false;
	
	public static void addNoShowDownWin(String id){
		opponents.get(id).noShowDownWinCount++;
	}
	public static void addShowDownWin(String id){
		opponents.get(id).showDownWinCount++;
	}
//	public static void addNoShowDown(String id){
//		opponents.get(id).noShowDownWinCount++;
//	}
	public static void addShowDown(String id,Card [] holdCards,Card [] flopCards,Card turnCard,Card riverCard){
		opponents.get(id).showDownCount++;
	}
	
	
	public  void addPreRaiseBet(int raiseBet){
		if(Round.getAliveNum()<=3&&!preraiseBetNum3Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			preraiseBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!preraiseBetNum2Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			preraiseBetNum2Reset=true;
		}
		this.zpreRaiseBet=Double.parseDouble(  df.format((double)(this.zpreRaiseBet*(this.zpreRaiseCount-1)+raiseBet)/this.zpreRaiseCount));
	}
	public  void addFlopRaiseBet(int raiseBet){
		if(Round.getAliveNum()<=3&&!flopraiseBetNum3Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			flopraiseBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!flopraiseBetNum2Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			flopraiseBetNum2Reset=true;
		}
		this.zflopRaiseBet=Double.parseDouble( df.format((double)(this.zflopRaiseBet*(this.zflopRaiseCount-1)+raiseBet)/this.zflopRaiseCount));
	}
	public  void addTurnRaiseBet(int raiseBet){
		if(Round.getAliveNum()<=3&&!turnraiseBetNum3Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			turnraiseBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!turnraiseBetNum2Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			turnraiseBetNum2Reset=true;
		}
		this.turnRaiseBet=Double.parseDouble(df.format((double)(this.turnRaiseBet*(this.zturnRaiseCount-1)+raiseBet)/this.zturnRaiseCount));
	}
	public  void addRiverRaiseBet(int raiseBet){
		if(Round.getAliveNum()<=3&&!riverraiseBetNum3Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			riverraiseBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!riverraiseBetNum2Reset){
			this.zpreRaiseBet=this.zpreRaiseBet*0.5+raiseBet*0.5;
			riverraiseBetNum2Reset=true;
		}
		this.riverRaiseBet=Double.parseDouble(df.format((double)(this.riverRaiseBet*(this.zriverRaiseCount-1)+raiseBet)/this.zriverRaiseCount));
	}
	

	public  void addPreCallBet(int CallBet,String id){
		this.preCallBet=Double.parseDouble(df.format((double)(this.preCallBet*(this.preCallCount-1)+CallBet)/this.preCallCount));
//		if(id.equals("8888")){
//			System.out.println(this.preCallCount);
//			System.out.println("888ave"+preCallBet);
//		}
	}
	public  void addFlopCallBet(int CallBet){
		if(Round.getAliveNum()<=3&&!flopcallBetNum3Reset){
			this.flopCallBet=this.flopCallBet*0.5+CallBet*0.5;
			flopcallBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!flopcallBetNum2Reset){
			this.flopCallBet=this.flopCallBet*0.5+CallBet*0.5;
			flopcallBetNum2Reset=true;
		}
		this.flopCallBet=Double.parseDouble( df.format((double)(this.flopCallBet*(this.flopCallCount-1)+CallBet)/this.flopCallCount));
	}
	public  void addTurnCallBet(int CallBet){
		if(Round.getAliveNum()<=3&&!turncallBetNum3Reset){
			this.turnCallBet=this.turnCallBet*0.5+CallBet*0.5;
			turncallBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!turncallBetNum2Reset){
			this.turnCallBet=this.turnCallBet*0.5+CallBet*0.5;
			turncallBetNum2Reset=true;
		}
		this.turnCallBet= Double.parseDouble(df.format((double)(this.turnCallBet*(this.turnCallCount-1)+CallBet)/this.turnCallCount));
	}
	public  void addRiverCallBet(int CallBet){
		if(Round.getAliveNum()<=3&&!rivercallBetNum3Reset){
			this.riverCallBet=this.riverCallBet*0.5+CallBet*0.5;
			rivercallBetNum3Reset=true;
		}else if(Round.getAliveNum()<=2&&!rivercallBetNum2Reset){
			this.riverCallBet=this.riverCallBet*0.5+CallBet*0.5;
			rivercallBetNum2Reset=true;
		}
		this.riverCallBet=Double.parseDouble(df.format((double)(this.riverCallBet*(this.riverCallCount-1)+CallBet)/this.riverCallCount));
	}
	public static int getBetterRaiseBet(String ofWaht){
		int sum=0;
		if(ofWaht.equals("preFlop")){
			for(String id:Round.noFoldPlayers){
				if(id.equals(Game.MYID)){
					continue;
				}else if(opponents.get(id)!=null){
					sum+=opponents.get(id).preCallBet;
				}
	//			System.out.println("id"+id);
			}
		}else 	if(ofWaht.equals("flop")){
			for(String id:Round.noFoldPlayers){
				if(id.equals(Game.MYID)){
					continue;
				}else if(opponents.get(id)!=null){
					sum+=opponents.get(id).flopCallBet;
				}
	//			System.out.println("id"+id);
			}
		}else 	if(ofWaht.equals("turn")){
			for(String id:Round.noFoldPlayers){
				if(id.equals(Game.MYID)){
					continue;
				}else if(opponents.get(id)!=null){
					sum+=opponents.get(id).turnCallBet;
				}
	//			System.out.println("id"+id);
			}
		}else 	if(ofWaht.equals("river")){
			for(String id:Round.noFoldPlayers){
				if(id.equals(Game.MYID)){
					continue;
				}else if(opponents.get(id)!=null){
					sum+=opponents.get(id).riverCallBet;
				}
	//			System.out.println("id"+id);
			}
		}
//		System.out.println(maxCallBetOfPlayers.toString());
//		System.out.println("ave:"+sum/RoundRecord.noFoldPlayers.size());
		if(!Round.noFoldPlayers.isEmpty()){
			return sum/Round.noFoldPlayers.size();	
		}else return sum;
	}
	
	
	public static HashMap<String,Opponent>opponents=new HashMap<String,Opponent>();
	public static void initOpponents(String id){
		opponents.put(id, new Opponent(id));
	}
	
	
	public static void updateJettonAndMoney(String id,String jetton,String money){
		if(opponents.containsKey(id)){
			opponents.get(id).setJetton(Integer.parseInt(jetton));
			opponents.get(id).setMoney(Integer.parseInt(money));
		}
	}
	
	public static double failPreRaise(){
		if(Round.roundCount<20){
			return 0;
		}
		double minRate=1;
		double playerRaiseRate=0;
		for(String id:Round.preRaisePlayers){
			if(id.equals(Game.MYID)){
				continue;
			}
			if((playerRaiseRate=(double)opponents.get(id).preRaiseCount/Round.roundCount)<minRate){
				minRate=playerRaiseRate;
			}
		}
		return minRate;
	}
	public static double failFlopRaise(){
		if(Round.roundCount<20){
			return 0;
		}
		double minRate=1;
		double playerRaiseRate=0;
		for(String id:Round.flopRaisePlayers){
			if(id.equals(Game.MYID)){
				continue;
			}
			if((playerRaiseRate=(double)opponents.get(id).flopRaiseCount/Round.roundCount)<minRate){
				minRate=playerRaiseRate;
			}
		}
		return minRate;
	}
	public static double failTurnRaise(){
		if(Round.roundCount<20){
			return 0;
		}
		double minRate=1;
		double playerRaiseRate=0;
		for(String id:Round.turnRaisePlayers){
			if(id.equals(Game.MYID)){
				continue;
			}
			if((playerRaiseRate=(double)opponents.get(id).turnRaiseCount/Round.roundCount)<minRate){
				minRate=playerRaiseRate;
			}
		}
		return minRate;
	}

	public static double failRiverRaiseRate(){
//		if(Round.roundCount<20){
//			return 0;
//		}
		double minRate=1;
		double playerRaiseRate=0;
		for(String id:Round.riverRaisePlayers){
			if(id.equals(Game.MYID)){
				continue;
			}
			if((playerRaiseRate=(double)opponents.get(id).zriverRaiseCount/Round.roundCount)<minRate){
				minRate=playerRaiseRate;
			}
		}
		return minRate;
	}
	public static double maxAveRaiseBetOfPalyers(String ofWhat){
		double maxBet=Game.BB;
		if(ofWhat.equals("preFlop")){
			for(String id:Round.preFirstRaisePlayers){
				if(opponents.get(id).zpreRaiseBet>maxBet){
					maxBet=opponents.get(id).zpreRaiseBet;
					if(maxBet<Game.BB){
						maxBet=Game.BB;
					}
				}
			}
		}else if(ofWhat.equals("flop")){
			for(String id:Round.flopFirstRaisePlayers){
				if(opponents.get(id).zflopRaiseBet>maxBet){
					maxBet=opponents.get(id).zflopRaiseBet;
					if(maxBet==0){
						maxBet=50;
					}
				}
			}
		}else if(ofWhat.equals("turn")){
			for(String id:Round.turnFirstRaisePlayers){
				if(opponents.get(id).turnRaiseBet>maxBet){
					maxBet=opponents.get(id).turnRaiseBet;
					if(maxBet==0){
						maxBet=150;
					}
				}
			}
		}else if(ofWhat.equals("river")){
			for(String id:Round.riverFirstRaisePlayers){
				if(opponents.get(id).riverRaiseBet>maxBet){
					maxBet=opponents.get(id).riverRaiseBet;
					if(maxBet==0){
						maxBet=200;
					}
				}
			}
		}
		if(Round.roundCount>50){
			return maxBet;
		}else return 2*Game.BB;
			
		
		
	}
	public static double limitCallBet(String ofWhat){
		double minRate=1;
		double tmpRate;
		if(Round.roundCount>50){
			if(ofWhat.equals("flop")){
				for(String id:Round.flopRaisePlayers){
					if(id.equals(Game.MYID)){
						continue;
					}
					if((tmpRate=(double)(opponents.get(id).flopRaiseCount/Round.roundCount))<minRate){
						minRate=tmpRate;
					}
				}
//				if(minRate<0.12){
//					return 2;
//				}else if(minRate<0.25){
//					return 3;
//				}else return 4;
			
			}else if(ofWhat.equals("turn")){
				for(String id:Round.turnRaisePlayers){
					if(id.equals(Game.MYID)){
						continue;
					}
					if((tmpRate=(double)(opponents.get(id).turnRaiseCount/Round.roundCount))<minRate){
						minRate=tmpRate;
					}
				}
//				if(minRate<0.12){
//					return 1.5;
//				}else if(minRate<0.25){
//					return 2.5;
//				}else return 3.5;
			
			}else if(ofWhat.equals("river")){
				for(String id:Round.riverRaisePlayers){
					if(id.equals(Game.MYID)){
						continue;
					}
					if((tmpRate=(double)(opponents.get(id).riverRaiseCount/Round.roundCount))<minRate){
						minRate=tmpRate;
					}
				}
//				if(minRate<(double)1/Round.getAliveNum()){
//					return 1;
//				}else if(minRate<(double)2/Round.getAliveNum()){
//					return 2;
//				}else return 3;
			}
			
		}
		if(minRate<(double)1/Round.getAliveNum()){
			return 1;
		}else if(minRate<(double)2/Round.getAliveNum()){
			return 2;
		}else return 3;
	
	}
	

	
	
	public static boolean failFlopRaise(double myrate,int needCallBet){
//		if(needCallBet<=(Game.BB+((maxRaiseBetOfPalyers("flop")-Game.BB)/((Controller.flopRaiseRate-Controller.flopFoldRate)*0.7))*(myrate-Controller.flopFoldRate))*minRaiseRateOfPalyers("flop")*10){
//			return false;
//		}else return true;
//		if(limitCallBet("flop")>0.25&&needCallBet<=maxAveRaiseBetOfPalyers("flop")*1.2){
//			if(Controller.flopFoldRate==0.05){
//				System.out.println("flopmyrate:"+myrate);
//				System.out.println("needCallBet:"+needCallBet);
//				System.out.println("maxRaiseBetOfPalyers"+maxAveRaiseBetOfPalyers("flop"));
//			}
//			return false;
//		}else
		double a=myrate-Controller.flopFoldRate;
		double b=limitCallBet("flop");
		double c=maxAveRaiseBetOfPalyers("flop")-Game.BB;
		double d=Controller.flopRaiseRate-Controller.flopFoldRate;
		if(a*b/d>3.5){
			a=3.5;
		}
		if(needCallBet<=a*b*c/d+Game.BB){
			System.out.println("--------------needCallBet:"+needCallBet);
			System.out.println("myrate:"+myrate);
			System.out.println("myrate-Controller.flopFoldRate:"+a);
			System.out.println("limitCallBet:"+b);
			System.out.println("myrate-maxAveRaiseBetOfPalyers()-Game.BB...:"+c);
			System.out.println("Controller.flopRaiseRate-Controller.flopFoldRate:"+d);
			System.out.println("maxAve:"+maxAveRaiseBetOfPalyers("flop"));
			System.out.println("flop false:"+(a*b*c/d+Game.BB));
			return false;
		}else {
			System.out.println("------------------------needCallBet:"+needCallBet);
			System.out.println("myrate:"+myrate);
			System.out.println("maxAve:"+maxAveRaiseBetOfPalyers("flop"));
			System.out.println("flop true:"+(Game.BB+(maxAveRaiseBetOfPalyers("flop")-Game.BB)/(Controller.flopRaiseRate-Controller.flopFoldRate)*(myrate-Controller.flopFoldRate)*limitCallBet("flop")));
			return true;	
		}
	}
	public static boolean failTurnRaise(double myrate,int needCallBet){
		
		double a=myrate-Controller.turnFoldRate;
		double b=limitCallBet("flop");
		double c=maxAveRaiseBetOfPalyers("turn")-Game.BB;
		double d=Controller.turnRaiseRate-Controller.turnFoldRate;
		if(a*b/d>3){
			a=3;
		}
		if(needCallBet<=a*b*c/d+Game.BB){
			System.out.println("turn false:"+Round.roundCount);
			return false;
		}else return true;
	}
	public static boolean failRiverRaise(double myrate,int needCallBet){

		double a=myrate-Controller.riverFoldRate;
		double b=limitCallBet("flop");
		double c=maxAveRaiseBetOfPalyers("river")-Game.BB;
		double d=Controller.riverRaiseRate-Controller.riverFoldRate;
		if(a*b/d>2.5){
			a=2.5;
		}
		if(needCallBet<=a*b*c/d+Game.BB){
				System.out.println("river false:"+Round.roundCount);
				return false;
		}else return true;
	}

	
	public Opponent(String id){
		this.id=id;
	}
	
	public int getJetton() {
		return jetton;
	}
	public void setJetton(int jetton) {
		this.jetton = jetton;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public int getPreRaiseCount() {
		return zpreRaiseCount;
	}
	public void setPreRaiseCount(int preRaiseCount) {
		this.zpreRaiseCount = preRaiseCount;
	}
	public int getPreCall() {
		return preCall;
	}
	public void setPreCall(int preCall) {
		this.preCall = preCall;
	}

	public int getPreAllIn() {
		return preAllIn;
	}
	public void setPreAllIn(int preAllIn) {
		this.preAllIn = preAllIn;
	}
	public String toString(){
//		return this.id+" :"+"money:"+this.money+" jetton:"+this.jetton+ " raiseTime:"+" "+this.preRaise+" "+this.flopRaise+" "+this.turnRaise+" "+this.riverRaise;
//		return this.id+" :"+"raiseTime:"+" "+this.preRaiseCount+" "+this.flopRaiseCount+" "+this.turnRaiseCount+" "+this.riverRaiseCount;
		return "\nshowDownWinCount:"+showDownWinCount+"\nshowDownCount:"+showDownCount
					+"\nshowWinRate:"+(double)showDownWinCount/showDownCount+"\nnoShowDownWinCount:"+noShowDownWinCount
					+"\nreRaiseMeOnAllCheckToRaiseCount:"+reRaiseMeOnAllCheckToRaiseCount
					+"\nreRaiseOnAllCheckToRaiseRate"+(double)reRaiseMeOnAllCheckToRaiseCount/Learn.allCheckToRaiseCount
					+"\ncallMeOnAllCheckToRaiseCount:"+callMeOnAllCheckToRaiseCount
					+"\ncallOnAllCheckToRaiseRate"+(double)callMeOnAllCheckToRaiseCount/Learn.allCheckToRaiseCount
					+"\nfoldMeOnAllCheckToRaiseCount"+foldMeOnAllCheckToRaiseCount
					+"\nfoldOnAllCheckToRaiseRate"+(double)foldMeOnAllCheckToRaiseCount/Learn.allCheckToRaiseCount
					+"\nzpreRaiseCount:"+zpreRaiseCount+"\nzpreRaiseBet:"+zpreRaiseBet
						+"\npreRaiseCount:"+preRaiseCount
						+"\npreReRaiseToFoldCount:"+preReRaiseToFoldCount
						+"\nMpreRaiseOnPossitionCount:"+MpreRaiseOnPossitionCount
						+"\nMpreReRaiseSuccessCount:"+MpreReRaiseSuccessCount
						+"\npreRaiseOnPosstionCount:"+Arrays.toString(preRaiseOnPosition)
						+"\npreReRaiseOnPosstionCount:"+Arrays.toString(preReRaiseOnPosition)
						+"\n------------------flopCount:"+flopCount
							+"\nzflopRaiseCount:"+zflopRaiseCount+"\nzflopRaiseBet:"+zflopRaiseBet
							+"\nflopRaiseCount:"+flopRaiseCount
							+"\nflopRaiseRate:"+(double)flopRaiseCount/flopCount
							+"\nflopFoldCount:"+flopFoldCount
							+"\nflopReRaiseToFoldCount:"+flopReRaiseToFoldCount
							+"\nflopRaiseOnPosstionCount:"+Arrays.toString(flopRaiseOnPosition)
							+"\nflopReRaiseOnPosstionCount:"+Arrays.toString(flopReRaiseOnPosition)
							+"\n------------------turnCount:"+turnCount
								+"\nzturnRaiseCount:"+zturnRaiseCount+"\nzturnRaiseBet:"+turnRaiseBet
								+"\nturnRaiseCount:"+turnRaiseCount
								+"\nturnRaiseRate:"+(double)turnRaiseCount/turnCount
								+"\nturnFoldCount:"+turnFoldCount
								+"\nturnReRaiseToFoldCount:"+turnReRaiseToFoldCount
								+"\n------------------riverCount:"+riverCount
									+"\nzriverRaiseCount:"+zriverRaiseCount+"\nzriverRaiseBet:"+riverRaiseBet
									+"\nriverRaiseCount:"+riverRaiseCount
									+"\nriverRaiseRate:"+(double)riverRaiseCount/riverCount
									+"\nriverFoldCount:"+riverFoldCount
									+"\nriverReRaiseToFoldCount:"+riverReRaiseToFoldCount
										+"\npreCallCount:"+preCallCount+"\npreCallBet:"+preCallBet
											+"\nflopCallCount:"+flopCallCount+"\nflopCallBet:"+flopCallBet
												+"\nturnCallCount:"+turnCallCount+"\nturnCallBet:"+turnCallBet
												+"\nriverCallCount:"+riverCallCount+"\nriverCallBet:"+riverCallBet	
									
													+"\n\n";	
	}
	@Override
	public int compareTo(Opponent other) {
		if(this.jetton+this.money<other.jetton+other.money){
			return -1;
		}else if(this.jetton+this.money==other.jetton+other.money){
			return 0;
		}else return 1;
	}
	public static void gameOver(){
		System.out.println(opponents);
	}
	
	//每个人必要的每局重新初始化
	public static void roundReset(){
		for(String id:opponents.keySet()){
			opponents.get(id).isAlive=false;
			opponents.get(id).roundPosition=0;
			opponents.get(id).raiseRound=false;
			opponents.get(id).MpreReRaiseOnPossition=false;
			opponents.get(id).actioned=false;
		}
		if(Round.roundCount%100==0){
			System.out.println("-----------------------------------------------------Round "+Round.roundCount+"----------------------------------");
				System.out.println(Opponent.opponents);
		}
	}
	public static void main(String []a){
		System.out.println( new Opponent("22").df.format(29.973498723974));
	}
}
