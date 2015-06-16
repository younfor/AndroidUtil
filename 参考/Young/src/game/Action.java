package game;

import java.util.Arrays;
import java.util.Random;

public class Action {	
	
	Random rm=new Random();
	public static boolean isRaise=false;
	public static boolean huren=true;
	
	
	/*
	 * 翻牌前，每一次手牌也就是新一局开始的标志
	 */
	public String preFlopAction(Card[] holdCards,int needCallBet,int noFoldNum,int loopTime){
		Controller.setLimits();//每次重新计算限制值
		
		double raiseMaxCallBet=Opponent.maxAveRaiseBetOfPalyers("preFlop")>Controller.raiseBet*2?Opponent.maxAveRaiseBetOfPalyers("preFlop"):Controller.raiseBet*2;
		if(Controller.isAllFold){
			return "fold \n";
		}
		
		if(Round.getMyposition()<=4&&needCallBet<=Game.BB&&Round.getAliveNum()>=4){
			return "raise "+Controller.raiseBet+"\n";	
		}
//		String preAction;
//		if(!(preAction=useNotPreSeatRaise(needCallBet,(int)(Game.BB*1.26))).equals("notRaise")){
//			 return preAction;
//		}
		
		int billScoreOfHoldCards=Card.billScoreOfHoldCards(holdCards);
		Game.holdScoreCount[billScoreOfHoldCards]++;
		
		int hurenNum=0;
		if(Round.roundCount>50&&loopTime==1&&Round.preCallPlayers.size()==0&&billScoreOfHoldCards>4&&Round.getAliveNum()>3){
				for(String id:Round.preRaisePlayers){
					if(id.equals(Game.MYID)){
						continue;
					}
					if((double)Opponent.opponents.get(id).preRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount<0.05||needCallBet>=Opponent.opponents.get(id).zpreRaiseBet*1.2){
	//						&&(double)Opponent.opponents.get(id).preReRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount>0.01){
						huren=false;
					}
				}
			
				for(String id:Round.noActionedPlayers){
					if((double)Opponent.opponents.get(id).preRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount<0.05||((double)Opponent.opponents.get(id).preReRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Opponent.opponents.get(id).preRaiseOnPosition[Opponent.opponents.get(id).roundPosition])>0.2){
						hurenNum++;
					}
				}
				if(hurenNum>=2){
					huren=false;
				}
				
				if(huren){
					System.out.println(Round.preRaisePlayers);
					System.out.println(Round.noActionedPlayers);
					System.out.println("****************"+Round.roundCount);
					return  "raise "+Controller.raiseBet+" \n";
				}
		}	
		

		
		if(!Opponent.opponents.isEmpty()&&Round.roundCount>25){
			int highRaiseRatePlayers=0;
			for(String id:Round.noFoldPlayers){
				if((double)(Opponent.opponents.get(id).preRaiseCount/Round.roundCount)>0.25){
					highRaiseRatePlayers++;
				}
			}
			if(Round.noFoldPlayers.size()==highRaiseRatePlayers){
				if(billScoreOfHoldCards<8){
					billScoreOfHoldCards+=1;
					System.out.println("Round:"+Round.roundCount+" billScoreOfHoldCards+=1;");
					System.out.println("nofoldPlayers:"+Round.noFoldPlayers);
					
				}
			}
		}
		
		int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
	
		if(noFoldNum>1){
			if(billScoreOfHoldCards>=10){
				if(billScoreOfHoldCards>15){
					if(maxBetOfPlayers<Controller.raiseBet){
						return  "raise "+Controller.raiseBet/2+" \n";
					}else return "call \n";	
				}else if(maxBetOfPlayers<=Game.BB){
						return  "raise "+Controller.raiseBet/2+" \n";
				}else if(needCallBet<raiseMaxCallBet){
					return "call \n";
				}else return "fold \n";	
			}else if(billScoreOfHoldCards>=7){
				int maxRaiseBet=(int)Math.round(Game.BB*2.26+Round.myJetton*0.05);
				int avePlayersBet=(int)(Opponent.maxAveRaiseBetOfPalyers("preFlop")*1.26);
				int maxCallBet=(maxRaiseBet>avePlayersBet?maxRaiseBet:avePlayersBet);
				if(isRaise&&needCallBet<maxCallBet){
					return "call \n";	
				}else if(needCallBet<avePlayersBet){
					return "call \n";	
				}return "fold \n";	
			
//			}else if(billScoreOfHoldCards>=Controller.preFoldScore){
//				if(needCallBet<=Game.BB&&loopTime==1&&Math.random()>0.7){
//					if(Learn.preRandomRaise){
//						return "raise"+Controller.raiseBet +"\n";
//					}else return "call \n";
//				}else return "fold \n";		
			}else return "fold \n";
		}else {
			if(billScoreOfHoldCards>=9){
				if(billScoreOfHoldCards>15){
					if(maxBetOfPlayers<Controller.raiseBet){
						return  "raise "+Controller.raiseBet/2+" \n";
					}else return "call \n";	
				}else if(maxBetOfPlayers<=Game.BB){
						return  "raise "+Controller.raiseBet/2+" \n";
				}else if(needCallBet<raiseMaxCallBet){
					return "call \n";
				}else return "fold \n";	
			}else if(billScoreOfHoldCards<9&&billScoreOfHoldCards>=6){
				
				int maxRaiseBet=(int)Math.round(Game.BB*2.26+Round.myJetton*0.05);
				int avePlayersBet=(int)(Opponent.maxAveRaiseBetOfPalyers("preFlop")*1.26);
				int maxCallBet=(maxRaiseBet>avePlayersBet?maxRaiseBet:avePlayersBet);
				if(isRaise&&needCallBet<maxCallBet){
					return "call \n";	
				}else if(needCallBet<avePlayersBet){
					return "call \n";	
				}return "fold \n";		
//			}else if(billScoreOfHoldCards>=Controller.preFoldScore-1){
//				if(needCallBet<=Game.BB&&Math.random()>0.6&&loopTime==1){
//					if(Learn.preRandomRaise){
//						return "raise"+Controller.raiseBet +"\n";
//					}else return "call \n";
//				}else return "fold \n";	
			}else return "fold \n";
		}
	}
	
	/*
	 * 翻牌flopFoldRate=0.5;
	 */
	public String flopAction(Card[] holdCards,Card[] flopCards,int needCallBet,int noFoldNum,int loopTime){ 
		if(!Opponent.opponents.isEmpty()&&Round.roundCount>25){
//			int highRaiseRatePlayers=0;
//			for(String id:Round.noFoldPlayers){
//				if((double)(Opponent.opponents.get(id).flopRaiseCount/Round.roundCount)>0.3){
//					highRaiseRatePlayers++;
//				}
//			}
//			if(Round.noFoldPlayers.size()==highRaiseRatePlayers){
////				Controller.flopFoldRate=0.2;
////				Controller.turnFoldRate=0.3;
////				Controller.riverFoldRate=0.4;
//				
//				Controller.flopRaiseRate=0.75;
//				Controller.turnRaiseRate=0.8;
//				Controller.riverRaiseRate=0.85;
//				System.out.println(" down RiseRate ");
//			}
		}
		
		if(loopTime==1){
			Learn.flopCount++; 
		}
		if(Controller.isAllFold){
			return "fold \n";
		}
		int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
		double noFoldNumDouble=noFoldNum;
//		if(noFoldNum>3){
//			noFoldNumDouble=3;
//		}
		noFoldNumDouble=1;
		
		double winRate=0;
		//s胜率
		winRate=Card.flopWinRate(flopCards, holdCards);
	
		if(Round.roundCount>50&&loopTime==1&&Round.flopCallPlayers.size()==0&&winRate>0.3&&Round.getAliveNum()>3){
				for(String id:Round.flopRaisePlayers){
					if(id.equals(Game.MYID)){
						continue;
					}
					if((double)Opponent.opponents.get(id).preRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount<0.05||needCallBet>=Opponent.opponents.get(id).zflopRaiseBet*1.2){
	//						&&(double)Opponent.opponents.get(id).preReRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount>0.01){
						huren=false;
					}
				}
				
				if(huren){
					System.out.println(Round.preRaisePlayers);
					System.out.println(Round.noActionedPlayers);
					System.out.println("****************"+Round.roundCount);
					return  "raise "+Controller.raiseBet+" \n";
				}
		}	
		
		
		
		if(loopTime==1){
			Game.winFlopRateCount[(int)(winRate*10)]++;
		}
		
		if(Round.getMyposition()==1&&needCallBet==0&&Round.getAliveNum()>=4){
			return "raise "+Controller.raiseBet+"\n";	
		}else if(Learn.allCheckToRaiseStrategy&&needCallBet==0&&(rm.nextInt(8)+1<Round.getAliveNum())){
			boolean huRaise=true;
			for(String id :Round.noFoldPlayers){
				if(Learn.threatAllCheckToRaiseStrategyPlayers.contains(id)){
					if(Math.random()<(double)Opponent.opponents.get(id).reRaiseMeOnAllCheckToRaiseCount/Learn.allCheckToRaiseCount){
						huRaise=false;
					}
				}
			}
		
			
			if(huRaise){
				if(!Learn.allCheckToRaiseRound){
					Learn.allCheckToRaiseCount++;
				}
				Learn.allCheckToRaiseRound=true;
				System.out.println(("allCheckToRaiseStrategy is true and this round :"+Round.roundCount+" is true..."));
				Learn.allCheckToRaiseRoundBets+=Controller.raiseBet;
				isRaise=true;
				return "raise "+Controller.raiseBet+"\n";	
			}
		}else if(Learn.onCardallCheckToRaiseStrategy&&needCallBet==0&&winRate>=Controller.flopFoldRate*1.5){
			if(!Learn.allCheckToRaiseRound){
				Learn.allCheckToRaiseCount++;
			}
			Learn.allCheckToRaiseRound=true;
			System.out.println(("onCardallCheckToRaiseStrategy is true and this round :"+Round.roundCount+"onCard is true..."));
			Learn.allCheckToRaiseRoundBets+=Controller.raiseBet;
			isRaise=true;
			return "raise "+Controller.raiseBet+"\n";	
		}
		
		int betterRaiseBet;
		double maxSumBetOnOpp=Opponent.maxAveRaiseBetOfPalyers("flop")/(needCallBet+0.1);
		if(winRate==1){
			if((betterRaiseBet=Opponent.getBetterRaiseBet("flop"))*loopTime<Controller.raiseBet*loopTime){
				return "raise "+2*betterRaiseBet*loopTime+"\n";
			}else return "raise "+2*Controller.raiseBet*loopTime+"\n";
		}else if(winRate>Math.pow(Controller.flopRaiseRate+0.1, 1.0/(noFoldNumDouble))){
			 if(maxBetOfPlayers<Controller.maxSumBetRank3*maxSumBetOnOpp){
				if(loopTime<=4){
					if((betterRaiseBet=Opponent.getBetterRaiseBet("flop"))*loopTime<Controller.raiseBet*loopTime){
						return "raise "+betterRaiseBet*loopTime+"\n";
					}else return "raise "+Controller.raiseBet*loopTime+"\n";	
				}else return "call \n";
			}else return "call \n";
		}else if(winRate>Math.pow(Controller.flopRaiseRate+0.06, 1.0/(noFoldNumDouble))){
			 if(maxBetOfPlayers<Controller.maxSumBetRank2*maxSumBetOnOpp){
				if(loopTime<=3){
					if((betterRaiseBet=Opponent.getBetterRaiseBet("flop"))*loopTime<Controller.raiseBet*loopTime){
						return "raise "+betterRaiseBet*loopTime+"\n";
					}else return "raise "+Controller.raiseBet*loopTime+"\n";	
				}else return "call \n";
			}else return "call \n";
		}else if(winRate>Math.pow(Controller.flopRaiseRate, 1.0/(noFoldNumDouble))){
			 if(maxBetOfPlayers<Controller.maxSumBetRank1*maxSumBetOnOpp){
				 if(loopTime<=2){
					if((betterRaiseBet=Opponent.getBetterRaiseBet("flop"))<Controller.raiseBet){
						return "raise "+betterRaiseBet+"\n";
					}else return "raise "+Controller.raiseBet+"\n";	
				 }else return "call \n";
			}else return "call \n";
		}else if(winRate>Math.pow(Controller.flopFoldRate,1.0/(noFoldNumDouble))){
	
			
			if(Learn.notFailRaiseStrategy&&Round.roundCount>50&&huren&&loopTime<=2){
//				for(String id:Round.preRaisePlayers){
//					Opponent.opponents.get(id).MpreReRaiseOnPossition=true;
//					Opponent.opponents.get(id).MpreRaiseOnPossitionCount++;
//					System.out.println("reraise id:"+id);
//					System.out.println("possition of :"+id+" "+Arrays.toString(Opponent.opponents.get(id).preRaiseOnPosition));
//				}
//				System.out.println("reraise Round:"+Round.roundCount);
				isRaise=true;
				return "raise"+Controller.raiseBet +"\n";
			}
			
			int maxRaiseBet=(int)Math.round(Game.BB*2.26+Round.myJetton*0.05);
			int avePlayersBet=(int)(Opponent.maxAveRaiseBetOfPalyers("flop")*1.26);
			int maxCallBet=(maxRaiseBet>avePlayersBet?maxRaiseBet:avePlayersBet);
	
			if(needCallBet==0&&winRate>Controller.flopRaiseRate-0.2){
				isRaise=true;
				return "raise "+Controller.raiseBet+"\n";
			}else if(loopTime<=2&&isRaise&&needCallBet<maxCallBet){
				return "call \n";
			}else if(loopTime<=2&&!Opponent.failFlopRaise(winRate,needCallBet)){	
				return "call \n";
			}else return "fold \n";
	   }else return "fold \n";
	}
	/*
	 * 转牌
	 * turnFoldRate=0.55;
	 */
	public String turnAction(Card[] holdCards,Card[] flopCards,Card turnCard,int needCallBet,int noFoldNum,int loopTime){
		
		if(Controller.isAllFold){
			return "fold \n";
		}
		int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
		
		double noFoldNumDouble=noFoldNum;
//		if(noFoldNum>3){
//			noFoldNumDouble=3;
//		}
		noFoldNumDouble=1;
		double winRate=0;
	
		winRate=Card.turnWinRate(flopCards, holdCards,turnCard);
	
//		if(Round.roundCount>50&&loopTime==1&&Round.turnCallPlayers.size()==0&&winRate>0.5&&Round.getAliveNum()>3){
//			for(String id:Round.turnRaisePlayers){
//				if(id.equals(Game.MYID)){
//					continue;
//				}
//				if((double)Opponent.opponents.get(id).preRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount<0.05||needCallBet>=Opponent.opponents.get(id).zpreRaiseBet*1.2){
////						&&(double)Opponent.opponents.get(id).preReRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount>0.01){
//					huren=false;
//				}
//			}
//			
//			if(huren){
//				return  "raise "+Controller.raiseBet+" \n";
//			}
//		}
		
		//测试统计
		if(loopTime==1){
			Game.winTurnRateCount[(int)(winRate*10)]++;
			if(winRate>=0.8){
				Game.winTurnRateOver8Count[(int)(winRate*100)%80]++;
			}
		}
		
		if(Round.getMyposition()==1&&needCallBet==0&&Round.getAliveNum()>=4){
			return "raise "+Controller.raiseBet+"\n";	
		}else if(Learn.allCheckToRaiseStrategy&&needCallBet==0&&(rm.nextInt(8)+1<Round.getAliveNum())){
			boolean huRaise=true;
			for(String id :Round.noFoldPlayers){
				if(Learn.threatAllCheckToRaiseStrategyPlayers.contains(id)){
					if(Math.random()<(double)Opponent.opponents.get(id).reRaiseMeOnAllCheckToRaiseCount/Learn.allCheckToRaiseCount){
						huRaise=false;
					}
				}
			}
		
			
			if(huRaise){
				if(!Learn.allCheckToRaiseRound){
					Learn.allCheckToRaiseCount++;
				}
				Learn.allCheckToRaiseRound=true;
				System.out.println(("allCheckToRaiseStrategy is true and this round :"+Round.roundCount+" is true..."));
				Learn.allCheckToRaiseRoundBets+=Controller.raiseBet;
				isRaise=true;
				Learn.turnRaiseFanBeiTime++;
				return "raise "+Controller.raiseBet+"\n";	
			}
		}else if(Learn.onCardallCheckToRaiseStrategy&&needCallBet==0&&winRate>=Controller.turnFoldRate*1.5){
			if(!Learn.allCheckToRaiseRound){
				Learn.allCheckToRaiseCount++;
			}
			Learn.allCheckToRaiseRound=true;
			System.out.println(("onCardallCheckToRaiseStrategy is true and this round :"+Round.roundCount+"onCard is true..."));
			Learn.allCheckToRaiseRoundBets+=Controller.raiseBet;
			isRaise=true;
			
			return "raise "+Controller.raiseBet+"\n";	
		}
		
		//System.out.println("nofold :"+noFoldNumDouble);
		//System.out.println("turnRate : "+winRate);
		
		int betterRaiseBet;
		double maxSumBetOnOpp=Opponent.maxAveRaiseBetOfPalyers("turn")/(needCallBet+0.1);
		if(winRate==1){
				return "all_in \n";
		}else if(winRate>Math.pow(Controller.turnRaiseRate+0.08, 1.0/(noFoldNumDouble))){
					return "raise "+Controller.myVirstualJetton+"\n";
		}else if(winRate>Math.pow(Controller.turnRaiseRate+0.05, 1.0/(noFoldNumDouble))){
			 if(maxBetOfPlayers<Controller.maxSumBetRank2*maxSumBetOnOpp){
				if(loopTime<=4){
					Controller.isNoFold=true;
					if((betterRaiseBet=Opponent.getBetterRaiseBet("turn"))*loopTime<Controller.raiseBet*loopTime){
						return "raise "+betterRaiseBet*loopTime+"\n";
					}else return "raise "+Controller.raiseBet*loopTime+"\n";
				}else return "call \n";
			}else 	return "call \n";
		}else if(winRate>Math.pow(Controller.turnRaiseRate, 1.0/(noFoldNumDouble))){
			if(maxBetOfPlayers<Controller.maxSumBetRank1*maxSumBetOnOpp){
				if(loopTime<=4){
					if((betterRaiseBet=Opponent.getBetterRaiseBet("turn"))<Controller.raiseBet){
						return "raise "+betterRaiseBet+"\n";
					}else return "raise "+Controller.raiseBet+"\n";
				}else return "call \n";
			}else return "call \n";
		}else if(winRate>Math.pow(Controller.turnFoldRate, 1.0/(noFoldNumDouble))){
			int maxRaiseBet=(int)Math.round(Game.BB*2.26+Round.myJetton*0.05);
			int avePlayersBet=(int)(Opponent.maxAveRaiseBetOfPalyers("turn")*1.26);
			int maxCallBet=(maxRaiseBet>avePlayersBet?maxRaiseBet:avePlayersBet);
	
			
			if(needCallBet==0&&winRate>Controller.turnRaiseRate-0.2){
				isRaise=true;
				return "raise "+Controller.raiseBet+"\n";
			}else if(loopTime<=2&&isRaise&&needCallBet<maxCallBet){
				return "call \n";
			}else if(loopTime<=2&&!Opponent.failTurnRaise(winRate,needCallBet)){
				return "call \n";
			}else return "fold \n";
		}else return "fold \n";
	}
	
	/*
	 * 	riverFoldRate=0.6
	 */
	public String riverAction(Card[] holdCards,Card[] flopCards,Card turnCard,Card riverCard,int needCallBet,int noFoldNum,int loopTime){	
	
		
		double raiseMaxCallBet=Opponent.maxAveRaiseBetOfPalyers("river")>Controller.raiseBet?Opponent.maxAveRaiseBetOfPalyers("river"):Controller.raiseBet;
		
		if(Controller.isAllFold){
			return "fold \n";
		}
		int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
		
		double noFoldNumDouble=noFoldNum;

		if(noFoldNum>2){
			noFoldNumDouble=2;
		}else if(noFoldNumDouble<1){
			noFoldNumDouble=1;
		}
	
		double winRate=0;
		winRate=Card.riverWinRate(flopCards, holdCards,turnCard,riverCard);
	
//		if(Round.roundCount>50&&loopTime==1&&Round.riverCallPlayers.size()==0&&winRate>0.5&&Round.getAliveNum()>3){
//			for(String id:Round.riverRaisePlayers){
//				if(id.equals(Game.MYID)){
//					continue;
//				}
//				if((double)Opponent.opponents.get(id).preRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount<0.05||needCallBet>=Opponent.opponents.get(id).zpreRaiseBet*1.2){
////						&&(double)Opponent.opponents.get(id).preReRaiseOnPosition[Opponent.opponents.get(id).roundPosition]/Round.roundCount>0.01){
//					huren=false;
//				}
//			}
//			
//			if(huren){
//				return  "raise "+Controller.raiseBet+" \n";
//			}
//		}
		
		//测试统计
		if(loopTime==1){
			
			Game.winRiverRateCount[(int)(winRate*10)]++;
			if(winRate>=0.8){
				Game.winRiverRateOver8Count[(int)(winRate*100)%80]++;
			}
		}
		
		if(Round.getMyposition()==1&&needCallBet==0&&Round.getAliveNum()>=4){
			return "raise "+Controller.raiseBet+"\n";	
		}else if(Learn.allCheckToRaiseStrategy&&needCallBet==0&&(rm.nextInt(8)+1<Round.getAliveNum())){
			boolean huRaise=true;
			for(String id :Round.noFoldPlayers){
				if(Learn.threatAllCheckToRaiseStrategyPlayers.contains(id)){
					if(Math.random()<(double)Opponent.opponents.get(id).reRaiseMeOnAllCheckToRaiseCount/Learn.allCheckToRaiseCount){
						huRaise=false;
					}
				}
			}
		
			
			if(huRaise){
				if(!Learn.allCheckToRaiseRound){
					Learn.allCheckToRaiseCount++;
				}
				Learn.allCheckToRaiseRound=true;
				System.out.println(("allCheckToRaiseStrategy is true and this round :"+Round.roundCount+" is true..."));
				Learn.allCheckToRaiseRoundBets+=Controller.raiseBet;
				isRaise=true;
				Learn.riverRaiseFanBeiTime++;
				return "raise "+Controller.raiseBet+"\n";	
			}
		}else if(Learn.onCardallCheckToRaiseStrategy&&needCallBet==0&&winRate>=Controller.riverFoldRate*1.5){
			if(!Learn.allCheckToRaiseRound){
				Learn.allCheckToRaiseCount++;
			}
			Learn.allCheckToRaiseRound=true;
			System.out.println(("onCardallCheckToRaiseStrategy is true and this round :"+Round.roundCount+"onCard is true..."));
			Learn.allCheckToRaiseRoundBets+=Controller.raiseBet;
			isRaise=true;
			
			return "raise "+Controller.raiseBet+"\n";	
		}
		
		int betterRaiseBet;
		double maxSumBetOnOpp=Opponent.maxAveRaiseBetOfPalyers("river")/(needCallBet+0.1);
		if(winRate==1){
				return "all_in \n";
		}else if(winRate>Math.pow(Controller.riverRaiseRate+0.041, 1.0/(noFoldNumDouble))){
					return "raise "+Controller.myVirstualJetton+"\n";
		}else if(winRate>Math.pow(Controller.riverRaiseRate+0.02, 1.0/(noFoldNumDouble))){
			if(loopTime<=2){
				return "raise "+Controller.maxSumBetRank3+"\n";
			}else return "call \n";
		}else if(winRate>Math.pow(Controller.riverRaiseRate, 1.0/(noFoldNumDouble))){
			if(needCallBet<raiseMaxCallBet*10){
				if(maxBetOfPlayers<Controller.maxSumBetRank2*maxSumBetOnOpp){
					if(loopTime<=4){
						if((betterRaiseBet=Opponent.getBetterRaiseBet("river"))<Controller.raiseBet){
							return "raise "+betterRaiseBet*loopTime+"\n";
						}else return "raise "+Controller.raiseBet*loopTime+"\n";
					}return "call \n";
				}else return "call \n";
			}else return "fold \n";
		}else if(winRate>Math.pow(Controller.riverRaiseRate-0.03, 1.0/(noFoldNumDouble))){
			if(loopTime==1&&needCallBet==0){
				if((betterRaiseBet=Opponent.getBetterRaiseBet("river"))<Controller.raiseBet){
					return "raise "+betterRaiseBet*loopTime+"\n";
				}else return "raise "+Controller.raiseBet*loopTime+"\n";
			}else if(needCallBet<raiseMaxCallBet*3){
				return "call \n";
			}return "fold \n";
		}else if(winRate>Math.pow(Controller.riverFoldRate, 1.0/(noFoldNumDouble))){
			int maxRaiseBet=(int)Math.round(Game.BB*2.26+Round.myJetton*0.05);
			int avePlayersBet=(int)(Opponent.maxAveRaiseBetOfPalyers("river")*1.26);
			int maxCallBet=(maxRaiseBet>avePlayersBet?maxRaiseBet:avePlayersBet);
			
			if(needCallBet==0&&winRate>Controller.riverRaiseRate-0.2){
				isRaise=true;
				return "raise "+Controller.raiseBet+"\n";
			}else if(loopTime<=2&&isRaise&&needCallBet<maxCallBet){
				return "call \n";
			}else if(loopTime<=2&&!Opponent.failRiverRaise(winRate,needCallBet)){
				return "call \n";
			}else return "fold \n";
		}else return "fold \n";	
	}
	
	
	
	
	
	
	

	public  String useNotPreSeatRaise(int needCallBet,int limitBet){
		if(Round.getAliveNum()>=6&&Round.getMyposition()==1||Round.getMyposition()>=(1<<(Round.getAliveNum()-2))&&needCallBet<=limitBet){
			return "raise "+Controller.raiseBet+"\n";	
		}else if(Round.getAliveNum()>=4&&Round.getMyposition()==1||Round.getMyposition()>=(1<<(Round.getAliveNum()-1))&&needCallBet<=limitBet){
				return "raise "+Controller.raiseBet+"\n";	
		}else if(Round.getAliveNum()>=2&&Round.getMyposition()==1&&needCallBet<=limitBet){
			return "raise "+Controller.raiseBet+"\n";
		}return "notRaise";
	}
	
	public  String useSeatCall(int needCallBet,int limitBet){
		if(Round.getAliveNum()>=6&&Round.getMyposition()==1||Round.getMyposition()>=(1<<(Round.getAliveNum()-2))&&needCallBet<=limitBet){
			return "call \n";	
		}else if(Round.getAliveNum()>=4&&Round.getMyposition()==1||Round.getMyposition()>=(1<<(Round.getAliveNum()-1))&&needCallBet<=limitBet){
			return "call \n";		
		}else if(Round.getAliveNum()>=2&&Round.getMyposition()==1&&needCallBet<=limitBet){
			return "call \n";	
		}return "notCall";
	}

	
	
	public  String usePreSeatRaise(int needCallBet,int limitBet){
		if(Round.getAliveNum()>=6){
			if(Round.getMyposition()<=4&&needCallBet<=limitBet){
				return "raise "+Controller.raiseBet+"\n";	
			}
		}else if(Round.getAliveNum()>=4){
			if(Round.getMyposition()<=4&&Round.getMyposition()>=2&&needCallBet<=limitBet){
				return "raise "+Controller.raiseBet+"\n";	
			}
		}else {
			if(Round.getMyposition()==4&&needCallBet<=limitBet){
				return "raise "+Controller.raiseBet+"\n";	
			}
		} return "notRaise";
	}
	
	
	public static void roundReset(){
		isRaise=false;
		huren=true;
	}
	/*
	 * test
	 */
	public static void main(String []args){
		Card [] holdCards=new Card[2];
		holdCards[0]=new Card("HEARTS K");
		holdCards[1]=new Card("HEARTS 9");
		int bill=Card.billScoreOfHoldCards(holdCards);
		Game.holdScoreCount[bill]++;
		System.out.println(Arrays.toString(Game.holdScoreCount));
		System.out.println("bill:"+bill);
		//System.out.println(new ConservativeAction().billScoreOfHoldCards(cards));
//		long currentTime=System.currentTimeMillis();
//		Card[] flopCards=new Card[3];
//		flopCards[0]=new Card("DIAMONDS 9");
//		flopCards[1]=new Card("CLUBS J");
//		flopCards[2]=new Card("DIAMONDS Q");
//		flopCards[3]=new Card("CLUBS 10");
//		Card[] holdCards=new Card[2];
//		holdCards[0]=new Card("HEARTS K");
//		holdCards[1]=new Card("DIAMONDS K");
//		getAll4(flopCards,holdCards);

//		//System.out.println("takes : "+(System.currentTimeMillis()-currentTime)+"ms");	
		long currentTime=System.currentTimeMillis();
		//System.out.println(Card.riverWinRate(flopCards,holdCards,new Card("CLUBS 10"),new Card("HEARTS 7")));
		//System.out.println(Math.pow(0.5,1/3.0 ));
		//System.out.println(Math.pow(0.6,1/3.0 ));
		//System.out.println(Math.pow(0.7,1/3.0 ));
		//System.out.println(Math.pow(0.8,1/3.0 ));
		//System.out.println(Math.pow(0.9,1/3.0 ));
		//System.out.println(Math.pow(0.95,1/3.0 ));
		//System.out.println("takes : "+(System.currentTimeMillis()-currentTime)+"ms");	
		System.out.println(Math.pow(0.7,1.0/(1)));
//		Random rm=new Random();
//		while(true){
//		System.out.println(rm.nextInt(8)+1);}
		
	
	}
}
