package game;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import util.RuntimeEval;


/*
 * 每一局 需要的某些信息记录
 */
public class Round {
	
	public static int roundCount;
//	public static Position position;
	public static int aliveNum;
	public static String smallBlindId;
	public static String buttonId;
	public static int myJetton;
	public static int myMoney;
	public static boolean showDown=false;
	public static boolean checkShowDown=false;
	
	
	
	//当前输赢状态表。每次开局时玩家的seat信息,直接保存了，可以取他的输赢情况
	private static HashMap<String,String[]> playerStates=new  HashMap<String,String[]>();//开局状态保存的座次带的信息
	


	/*
	 * 座次信息处理
	 */
	public static HashSet<String> noFoldPlayers=new HashSet<String>();
	public static HashSet<String> noActionedPlayers=new HashSet<String>();
	private static int myPosition=1;//二进制0001000  就表示在第5个
	public static int  getMyposition(){
		return myPosition;
	}
	static int winMoney;
	static int lostMoney;
	public static void addSeatMag(String msg){
		aliveNum++;
		String msgs[]=msg.split(" ");
		
		if(msgs[0].startsWith("button")){//button: 这个冒号注意啊 
			if(roundCount==1){
				Opponent.initOpponents(msgs[1]);
			}else {
				Opponent.updateJettonAndMoney(msgs[1], msgs[2], msgs[3]);
			}
			
			noFoldPlayers.add(msgs[1]);
			noActionedPlayers.add(msgs[1]);
			if(roundCount>5){
				Opponent.opponents.get(msgs[1]).isAlive=true;
			}
			buttonId=msgs[1];
			if(msgs[1].equals(Game.MYID)){
				noFoldPlayers.remove(Game.MYID);
				myPosition<<=aliveNum-1;
				if((winMoney=Integer.parseInt(msgs[2])+Integer.parseInt(msgs[3])-Round.myJetton-Round.myMoney)>500){
					RuntimeEval.largeWin.put(Round.roundCount-1, winMoney);
				}else if((lostMoney=Integer.parseInt(msgs[2])+Integer.parseInt(msgs[3])-Round.myJetton-Round.myMoney)<-500){
					RuntimeEval.largeLost.put(Round.roundCount-1, lostMoney);
				}
				Round.myJetton=Integer.parseInt(msgs[2]);
				Round.myMoney=Integer.parseInt(msgs[3]);
			}
			
			Opponent.opponents.get(msgs[1]).roundPosition=aliveNum-1;
			
			String tmp[]=new String[2];
			System.arraycopy(msgs, 2, tmp, 0, 2);
			playerStates.put(msgs[1],tmp);
			
			
		}else if(msgs[0].startsWith("small")){
			if(roundCount==1){
				Opponent.initOpponents(msgs[2]);
			}else {
				Opponent.updateJettonAndMoney(msgs[2], msgs[3], msgs[4]);
			}
			
			noFoldPlayers.add(msgs[2]);
			noActionedPlayers.add(msgs[2]);
			if(roundCount>5){
				Opponent.opponents.get(msgs[2]).isAlive=true;
			}
			smallBlindId=msgs[2];
			if(msgs[2].equals(Game.MYID)){
				noFoldPlayers.remove(Game.MYID);
				myPosition<<=aliveNum-1;
				Round.myJetton=Integer.parseInt(msgs[3]);
				Round.myMoney=Integer.parseInt(msgs[4]);
			}
			Opponent.opponents.get(msgs[2]).roundPosition=aliveNum-1;
			
			String tmp[]=new String[2];
			System.arraycopy(msgs, 3, tmp, 0, 2);
			playerStates.put(msgs[2],tmp);
			
			
		}else if(msgs[0].startsWith("big")){

			if(roundCount==1){
				Opponent.initOpponents(msgs[2]);
			}else {
				Opponent.updateJettonAndMoney(msgs[2], msgs[3], msgs[4]);
			}
			
			noFoldPlayers.add(msgs[2]);
			noActionedPlayers.add(msgs[2]);
			if(roundCount>5){
				Opponent.opponents.get(msgs[2]).isAlive=true;
			}
			if(msgs[2].equals(Game.MYID)){
				noFoldPlayers.remove(Game.MYID);
				myPosition<<=aliveNum-1;
				Round.myJetton=Integer.parseInt(msgs[3]);
				Round.myMoney=Integer.parseInt(msgs[4]);
			}
			Opponent.opponents.get(msgs[2]).roundPosition=aliveNum-1;
			
			String tmp[]=new String[2];
			System.arraycopy(msgs, 3, tmp, 0, 2);
			playerStates.put(msgs[2],tmp);
			
		}else{
			if(roundCount==1){
				Opponent.initOpponents(msgs[0]);
			}else {
				Opponent.updateJettonAndMoney(msgs[0], msgs[1], msgs[2]);
			}
			
			noFoldPlayers.add(msgs[0]);
			noActionedPlayers.add(msgs[0]);
			if(roundCount>5){
				Opponent.opponents.get(msgs[0]).isAlive=true;
			}
			String tmp[]=new String[2];
			System.arraycopy(msgs, 1, tmp, 0, 2);
			playerStates.put(msgs[0],tmp);
			if(msgs[0].equals(Game.MYID)){//0就没有设置过
				noFoldPlayers.remove(Game.MYID);//noFoldPlayers直接不包含自己了
				myPosition<<=aliveNum-1;	
				Round.myJetton=Integer.parseInt(msgs[1]);
				Round.myMoney=Integer.parseInt(msgs[2]);
			}
			Opponent.opponents.get(msgs[0]).roundPosition=aliveNum-1;
		}
	}

	

//	public static final int PRE=0;a
//	public static final int FLOP=1;
//	public static final int TURN=2;
//	public static final int RIVER=3;
//	public static final int RESULT=4;
	
	
	public static   HashSet<String> preRaisePlayers=new HashSet<String>();
	public static   HashSet<String> flopRaisePlayers=new HashSet<String>();
	public static   HashSet<String> turnRaisePlayers=new HashSet<String>();
	public static   HashSet<String> riverRaisePlayers=new HashSet<String>();
	
	public static   HashSet<String> preFirstRaisePlayers=new HashSet<String>();
	public static   HashSet<String> flopFirstRaisePlayers=new HashSet<String>();
	public static   HashSet<String> turnFirstRaisePlayers=new HashSet<String>();
	public static   HashSet<String> riverFirstRaisePlayers=new HashSet<String>();
	
	public static   HashSet<String> preCallPlayers=new HashSet<String>();
	public static   HashSet<String> flopCallPlayers=new HashSet<String>();
	public static   HashSet<String> turnCallPlayers=new HashSet<String>();
	public static   HashSet<String> riverCallPlayers=new HashSet<String>();
	
	public static HashSet<String> reRaiseMePlayers=new HashSet<String>();
	public static int callRiseBet;
	public static int lastCallBet;
	public static boolean firstRaise=false;
	public static int firstRaiseBet=0;
	//倒着处理消息
	public static LinkedList<String>msgStack=new LinkedList<String>();
	public static void  addInquireOrNotifyMsg(String msg){	
		msgStack.add(msg);
	}
	//所有人本局已投注额
	public  static HashMap<String, Integer> playerBets=new HashMap<String, Integer>();
	public static int getMaxBetsOfPlayers(){
		if(!playerBets.isEmpty()){
			return Collections.max(playerBets.values()).intValue();
		}else return Game.BB;
	}
	public static int getPlayerBet(String myId){
		if(playerBets.containsKey(myId)){
			return playerBets.get(myId);
		}else return 0;
	}
	/*
	 * 询问消息处理(包括 通知消息)
	 * 本次消息接收完毕,倒着开始处理
	 */
	public static int maxBet=-1;
//	private static String maxActionId;
	public static String lastActionId;
	public static String currentPreId;
	private static HashSet<String> isfolds=new HashSet<String>();
	public static int actionedNum=0;
	public static void  handleInquireMsg(String ofWhat,int loopTime,boolean isFoldOrAllIn){	
		String msg;
		String msgs[];
		boolean addPre=true;
		boolean reraise=false;
		if(loopTime==1){
			for(String id:Opponent.opponents.keySet()){
				Opponent.opponents.get(id).actioned=false;
			}
		}
		
		
		while((msg=msgStack.pollLast())!=null){
			msgs=msg.split(" ");
			Opponent.opponents.get(msgs[0]).actioned=true;
			actionedNum++;
			noActionedPlayers.remove(msgs[0]);
			if(ofWhat.equals("flop")){
				
				if(msgs[4].equals("fold")&&loopTime==1){
					Opponent.opponents.get(msgs[0]).flopCount++;
				}else if(!msgs[4].equals("fold")){
					Opponent.opponents.get(msgs[0]).flopCount++;
				}
			}else if(ofWhat.equals("turn")){
				if(msgs[4].equals("fold")&&loopTime==1){
					Opponent.opponents.get(msgs[0]).turnCount++;
				}else if(!msgs[4].equals("fold")){
					Opponent.opponents.get(msgs[0]).turnCount++;
				}
			}else if(ofWhat.equals("river")){
				if(msgs[4].equals("fold")&&loopTime==1){
					Opponent.opponents.get(msgs[0]).riverCount++;
				}else if(!msgs[4].equals("fold")){
					Opponent.opponents.get(msgs[0]).riverCount++;
				}
			}
			
			
			if(Integer.parseInt(msgs[3])>maxBet){
				maxBet=Integer.parseInt(msgs[3]);
//				maxActionId=msgs[0];
				lastActionId=currentPreId;
			}
			if(!msgs[4].equals("fold")){
				currentPreId=msgs[0];
			}
		
//			if(msgs[0].equals(smallBlindId)&&msgs[0].equals(Game.MYID)){
//				
//			}
			if(msgs[0].equals(smallBlindId)&&!msgs[0].equals(Game.MYID)){
				addPre=false;
				if(loopTime==1){
					reraise=false;
				}	
			}
			/*
			 * fold
			 */
			if(msgs[4].equals("fold")&&!isfolds.contains((msgs[0]))){
					if(Learn.allCheckToRaiseRound&&!msgs[0].equals(Game.MYID)){
						Opponent.opponents.get(msgs[0]).foldMeOnAllCheckToRaiseCount++;
					}
					noFoldPlayers.remove(msgs[0]);
					
					if(ofWhat.equals("preFlop")){
					
						Opponent.opponents.get(msgs[0]).preFoldCount++;
						
						if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
							Opponent.opponents.get(msgs[0]).preReRaiseToFoldCount++;
						}
						if(Opponent.opponents.get(msgs[0]).MpreReRaiseOnPossition){
							Opponent.opponents.get(msgs[0]).MpreReRaiseSuccessCount++;
						}
						
						if(preRaisePlayers.contains(msgs[0])){
							preRaisePlayers.remove(msgs[0]);
						}
						if(preCallPlayers.contains(msgs[0])){
							preCallPlayers.remove(msgs[0]);
						}
						if(preFirstRaisePlayers.contains(msgs[0])){
							preFirstRaisePlayers.remove(msgs[0]);
						}
						if(preCallPlayers.contains(msgs[0])){
							preCallPlayers.remove(msgs[0]);
						}
					}else if(ofWhat.equals("flop")){
						if(addPre&&loopTime==1){
							Opponent.opponents.get(msgs[0]).preFoldCount++;
							if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
								Opponent.opponents.get(msgs[0]).preReRaiseToFoldCount++;
							}
							if(Opponent.opponents.get(msgs[0]).MpreReRaiseOnPossition){
								Opponent.opponents.get(msgs[0]).MpreReRaiseSuccessCount++;
							}
						}else{
							Opponent.opponents.get(msgs[0]).flopFoldCount++;
							if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
								Opponent.opponents.get(msgs[0]).flopReRaiseToFoldCount++;
							}
						}
						
						if(flopRaisePlayers.contains(msgs[0])){
							flopRaisePlayers.remove(msgs[0]);
						}
						if(flopCallPlayers.contains(msgs[0])){
							flopCallPlayers.remove(msgs[0]);
						}
						if(flopFirstRaisePlayers.contains(msgs[0])){
							flopFirstRaisePlayers.remove(msgs[0]);
						}
						if(flopCallPlayers.contains(msgs[0])){
							flopCallPlayers.remove(msgs[0]);
						}
					}else if(ofWhat.equals("turn")){
						if(addPre&&loopTime==1){
							Opponent.opponents.get(msgs[0]).flopFoldCount++;
							if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
								Opponent.opponents.get(msgs[0]).flopReRaiseToFoldCount++;
							}
						}else{
							Opponent.opponents.get(msgs[0]).turnFoldCount++;
							if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
								Opponent.opponents.get(msgs[0]).turnReRaiseToFoldCount++;
							}
						}
						if(turnRaisePlayers.contains(msgs[0])){
							turnRaisePlayers.remove(msgs[0]);
						}
						if(turnCallPlayers.contains(msgs[0])){
							turnCallPlayers.remove(msgs[0]);
						}
						if(turnFirstRaisePlayers.contains(msgs[0])){
							turnFirstRaisePlayers.remove(msgs[0]);
						}
						if(turnCallPlayers.contains(msgs[0])){
							turnCallPlayers.remove(msgs[0]);
						}
					}else if(ofWhat.equals("river")){
						if(addPre&&loopTime==1){
							Opponent.opponents.get(msgs[0]).turnFoldCount++;
							if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
								Opponent.opponents.get(msgs[0]).turnReRaiseToFoldCount++;
							}
						}else{
							Opponent.opponents.get(msgs[0]).riverFoldCount++;
							if(Opponent.opponents.get(msgs[0]).raiseRound&&reraise){
								Opponent.opponents.get(msgs[0]).riverReRaiseToFoldCount++;
							}
						}
						if(riverRaisePlayers.contains(msgs[0])){
							riverRaisePlayers.remove(msgs[0]);
						}
						if(riverCallPlayers.contains(msgs[0])){
							riverCallPlayers.remove(msgs[0]);
						}
						if(riverFirstRaisePlayers.contains(msgs[0])){
							riverFirstRaisePlayers.remove(msgs[0]);
						}
						if(riverCallPlayers.contains(msgs[0])){
							riverCallPlayers.remove(msgs[0]);
						}
					}
					if(!isfolds.contains((msgs[0]))){
						isfolds.add(msgs[0]);
					}
			/*
			 * call
			 */
			}else if(msgs[4].equals("call")){
				if(Learn.allCheckToRaiseRound&&!msgs[0].equals(Game.MYID)){
					Opponent.opponents.get(msgs[0]).callMeOnAllCheckToRaiseCount++;
				}
				//对手分析之 callbet，以获得别人跟注最大利益
				if(playerBets.get(msgs[0])==null){
					lastCallBet=0;
				}else lastCallBet=playerBets.get(msgs[0]);
				callRiseBet=Integer.parseInt(msgs[3])-lastCallBet;
		
				if(ofWhat.equals("preFlop")){
					Opponent.opponents.get(msgs[0]).preCallCount++;
					Opponent.opponents.get(msgs[0]).addPreCallBet(callRiseBet,msgs[0]);
					if(preRaisePlayers.contains(msgs[0])){
						preRaisePlayers.remove(msgs[0]);
					}
					preCallPlayers.add(msgs[0]);
					if(preRaisePlayers.contains(msgs[0])){
						preRaisePlayers.remove(msgs[0]);
					}
					if(preFirstRaisePlayers.contains(msgs[0])){
						preFirstRaisePlayers.remove(msgs[0]);
					}
				}else if(ofWhat.equals("flop")){
					if(addPre&&loopTime==1){
						Opponent.opponents.get(msgs[0]).preCallCount++;
						Opponent.opponents.get(msgs[0]).addPreCallBet(callRiseBet,msgs[0]);
						preCallPlayers.add(msgs[0]);
					}else{
						Opponent.opponents.get(msgs[0]).flopCallCount++;
						Opponent.opponents.get(msgs[0]).addFlopCallBet(callRiseBet);
						flopCallPlayers.add(msgs[0]);
					}
					if(flopRaisePlayers.contains(msgs[0])){
						flopRaisePlayers.remove(msgs[0]);
					}
					if(flopFirstRaisePlayers.contains(msgs[0])){
						flopFirstRaisePlayers.remove(msgs[0]);
					}
					
				}else if(ofWhat.equals("turn")){
					if(addPre&&loopTime==1){
						Opponent.opponents.get(msgs[0]).flopCallCount++;
						Opponent.opponents.get(msgs[0]).addFlopCallBet(callRiseBet);
						flopCallPlayers.add(msgs[0]);
					}else{
						Opponent.opponents.get(msgs[0]).turnCallCount++;
						Opponent.opponents.get(msgs[0]).addTurnCallBet(callRiseBet);
						turnCallPlayers.add(msgs[0]);
					}
					if(turnRaisePlayers.contains(msgs[0])){
						turnRaisePlayers.remove(msgs[0]);
					}
					if(turnFirstRaisePlayers.contains(msgs[0])){
						turnFirstRaisePlayers.remove(msgs[0]);
					}
					
				}else if(ofWhat.equals("river")){
					checkShowDown=false;//河牌有过加注最终摊牌则先不算checkShowDown
					if(addPre&&loopTime==1){
						Opponent.opponents.get(msgs[0]).turnCallCount++;
						Opponent.opponents.get(msgs[0]).addTurnCallBet(callRiseBet);
						turnCallPlayers.add(msgs[0]);
					}else{
						Opponent.opponents.get(msgs[0]).riverCallCount++;
						Opponent.opponents.get(msgs[0]).addRiverCallBet(callRiseBet);
						riverCallPlayers.add(msgs[0]);
					}	
					if(riverRaisePlayers.contains(msgs[0])){
						riverRaisePlayers.remove(msgs[0]);
					}
					if(riverFirstRaisePlayers.contains(msgs[0])){
						riverFirstRaisePlayers.remove(msgs[0]);
					}
				}
				
			}else if(msgs[4].equals("raise")||msgs[4].equals("all_in")){
			/*
			 * raise
			 */
				
				Opponent.opponents.get(msgs[0]).raiseRound=true;
				
				if(Learn.allCheckToRaiseRound&&!msgs[0].equals(Game.MYID)){
					Opponent.opponents.get(msgs[0]).reRaiseMeOnAllCheckToRaiseCount++;
				}
				
				if(ofWhat.equals("preFlop")){
					Opponent.opponents.get(msgs[0]).preRaiseCount++;
					preRaisePlayers.add(msgs[0]);
					if(Integer.parseInt(msgs[3])-getMaxBetsOfPlayers()>firstRaiseBet){
						firstRaise=true;
					}
					if(firstRaise){
						Opponent.opponents.get(msgs[0]).zpreRaiseCount++;
						firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
						firstRaise=false;
						Opponent.opponents.get(msgs[0]).addPreRaiseBet(firstRaiseBet);
						preFirstRaisePlayers.add(msgs[0]);
					}
					Opponent.opponents.get(msgs[0]).preRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
					if(reraise){
						Opponent.opponents.get(msgs[0]).preReRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
					}
				
				}else if(ofWhat.equals("flop")){
					if(addPre&&loopTime==1){
						Opponent.opponents.get(msgs[0]).preRaiseCount++;
						preRaisePlayers.add(msgs[0]);
						Opponent.opponents.get(msgs[0]).preRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
						if(reraise){
							Opponent.opponents.get(msgs[0]).preReRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
						}
					
					}else {
						Opponent.opponents.get(msgs[0]).flopRaiseCount++;
						Opponent.opponents.get(msgs[0]).flopRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
						if(reraise){
							Opponent.opponents.get(msgs[0]).flopReRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
						}
						flopRaisePlayers.add(msgs[0]);
					}
					if(Integer.parseInt(msgs[3])-getMaxBetsOfPlayers()>firstRaiseBet){
						firstRaise=true;
					}
					if(firstRaise){
						if(addPre&&loopTime==1){
							Opponent.opponents.get(msgs[0]).zpreRaiseCount++;
							firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
							firstRaise=false;
							Opponent.opponents.get(msgs[0]).addPreRaiseBet(firstRaiseBet);
							preFirstRaisePlayers.add(msgs[0]);
						}else{
							Opponent.opponents.get(msgs[0]).zflopRaiseCount++;
							firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
//							if(msgs[0].equals("8888"))
//								System.out.println("8888turnRaise"+Round.roundCount+":"+firstRaiseBet);
							firstRaise=false;
							Opponent.opponents.get(msgs[0]).addFlopRaiseBet(firstRaiseBet);
							flopFirstRaisePlayers.add(msgs[0]);
						}
					}
					
				}else if(ofWhat.equals("turn")){
					if(addPre&&loopTime==1){
						Opponent.opponents.get(msgs[0]).flopRaiseCount++;
						Opponent.opponents.get(msgs[0]).flopRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
						if(reraise){
							Opponent.opponents.get(msgs[0]).flopReRaiseOnPosition[Opponent.opponents.get(msgs[0]).roundPosition]++;
						}
						flopRaisePlayers.add(msgs[0]);
					}else {
						Opponent.opponents.get(msgs[0]).turnRaiseCount++;
						turnRaisePlayers.add(msgs[0]);
					}
					//下面爲主動加註
					if(Integer.parseInt(msgs[3])-getMaxBetsOfPlayers()>firstRaiseBet){
						firstRaise=true;
					}
					if(firstRaise){
						if(addPre&&loopTime==1){
							Opponent.opponents.get(msgs[0]).zflopRaiseCount++;
							firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
//							if(msgs[0].equals("8888"))
//								System.out.println("8888turnRaise"+Round.roundCount+":"+firstRaiseBet);
							firstRaise=false;
							Opponent.opponents.get(msgs[0]).addFlopRaiseBet(firstRaiseBet);
							flopFirstRaisePlayers.add(msgs[0]);
						}else{
							Opponent.opponents.get(msgs[0]).zturnRaiseCount++;
							firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
							firstRaise=false;
							Opponent.opponents.get(msgs[0]).addTurnRaiseBet(firstRaiseBet);
							turnFirstRaisePlayers.add(msgs[0]);
						}
					}
					
				}else if(ofWhat.equals("river")){
					checkShowDown=false;//河牌有过加注最终摊牌则先不算checkShowDown
					if(addPre&&loopTime==1){
						Opponent.opponents.get(msgs[0]).turnRaiseCount++;
						turnRaisePlayers.add(msgs[0]);
					}else{
						Opponent.opponents.get(msgs[0]).riverRaiseCount++;
						riverRaisePlayers.add(msgs[0]);
					}
					if(Integer.parseInt(msgs[3])-getMaxBetsOfPlayers()>firstRaiseBet){
						firstRaise=true;
					}
					if(firstRaise){
						if(addPre&&loopTime==1){
							Opponent.opponents.get(msgs[0]).zturnRaiseCount++;
							firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
							firstRaise=false;
							Opponent.opponents.get(msgs[0]).addTurnRaiseBet(firstRaiseBet);
							turnFirstRaisePlayers.add(msgs[0]);
						}else{
							Opponent.opponents.get(msgs[0]).zriverRaiseCount++;
							firstRaiseBet=Integer.parseInt(msgs[3])-getMaxBetsOfPlayers();
							firstRaise=false;
							Opponent.opponents.get(msgs[0]).addRiverRaiseBet(firstRaiseBet);
							riverFirstRaisePlayers.add(msgs[0]);
						}
					}	
				}
				reraise=true;//出现raise
			}else if(msgs[4].equals("check")){
//				if(!ofWhat.equals("preFlop")){
//					checkShowDown=true;
//				}
			}
			//添加投注额
			playerBets.put(msgs[0], Integer.parseInt(msgs[3]));
			
			
//			if(lastActionId!=null&&lastActionId.equals(msgs[0])&&maxBet==Integer.parseInt(msgs[3])){
//				while(msgStack.peekLast()!=null){
//					
//					System.out.println("remove"+msgStack.pollLast());
//				}
//			}
			if(lastActionId!=null&&lastActionId.equals(msgs[0])&&(maxBet==Integer.parseInt(msgs[3])||msgs[4].equals("fold"))){
				break;
			}
			
		}
	}
	public static int getKnownPreRaiseNum(){
		return preRaisePlayers.size();
	}
	public static int getKnownFlopRaiseNum(){
		return flopRaisePlayers.size();
	}
	public static int getKnownTurnRaiseNum(){
		return turnRaisePlayers.size();
	}
	public static int getKnownRiverRaiseNum(){
		return riverRaisePlayers.size();
	}
	
	//处理notify
//	public static void  addNotifyMsg(String msg,String ofWhat){
//		addInquireMsg( msg, ofWhat);
//	}
	/*
	 * 处理showDown
	 */
	public static void addShowDownMsg(String msg,Card [] flopCards,Card turnCard,Card riverCard){//2: 2222 SPADES 8 DIAMONDS A ONE_PAIR 
		String msgs[]=msg.split(" ");
		showDown=true;
		Card []holdCards=new Card[2];
		holdCards[0]=new Card(msgs[2]+" "+msgs[3]);
		holdCards[1]=new Card(msgs[4]+" "+msgs[5]);
		if(!checkShowDown){
			Opponent.addShowDown(msgs[1],holdCards,flopCards,turnCard,riverCard);
		}
		
	}
	/*
	 * pot win
	 */
	public static void addPotWinMsg(String msg){
		String []msgs=msg.split(" ");
		
		if(showDown&&!checkShowDown){//摊牌赢
			Opponent.addShowDownWin(msgs[0].substring(0,msgs[0].indexOf(":")));
		}else{//非摊牌赢
			Opponent.addNoShowDownWin(msgs[0].substring(0,msgs[0].indexOf(":")));
		}
		
		if(msgs[0].equals(Game.MYID+":")){//自己赢
			if(showDown&&!checkShowDown){//摊牌赢
				Learn.addShowDownWin(msgs[1]);
			}else{//非摊牌赢
				Learn.addNoShowDownWin(msgs[1]);
			}
		}
	}
	
	
	
	
	
	public static int getAliveNum(){
		return aliveNum;
	}
	
	public static void resetRound(){
		//打印每个人的输赢情况
//		if(roundCount>1){
//			System.out.println("-----------印每个人的输赢情况-----------");
//			System.out.println(Opponent.opponents);
			
//			Object[]opponentArray=Opponent.opponents.values().toArray();
//			Arrays.sort(opponentArray);
//			System.out.println("Rank:"+Arrays.toString(opponentArray));
//			System.out.println("Rank first:"+opponentArray[7]);
//		}
//		System.out.println(getMyposition());
		//重置
		smallBlindId=null;
		myPosition=1;
		aliveNum=0;
		callRiseBet=0;
		lastCallBet=0;
		playerStates.clear();
		playerBets.clear();
		noFoldPlayers.clear();
		preRaisePlayers.clear();
		flopRaisePlayers.clear();
	    turnRaisePlayers.clear();
		riverRaisePlayers.clear();
		isfolds.clear();
		actionedNum=0;
		preCallPlayers.clear();
		flopCallPlayers.clear();
	    turnCallPlayers.clear();
		riverCallPlayers.clear();
		showDown=false;
		checkShowDown=true;
		
		Opponent.roundReset();
		Learn.roundReset();
		Action.roundReset();
		
	}
	
	
	
	
	/*
	 * 动作链，循化动作；无人加注，则没有链，只有一个动作（不会多次杠起来）
	 */
	
	public static class ActionChain{
		private int sumBets;
		private String actionName;
		public  ActionChain loopNext=null;
		private int loopCount;
		
		public ActionChain(int sumBets,String actionName){
			this.sumBets=sumBets;
			this.actionName=actionName;
		}
		public final int getSumBets(){
			return this.sumBets;
		}
		public final String getActionName(){
			return this.actionName;
		}
		//this为头结点，添加至动作链尾部
		public void addNextAction(ActionChain nextAction){
			ActionChain current=this;
			while(current.loopNext!=null){
				current=current.loopNext;
			}
			current.loopNext= nextAction;
		}
		public void setLoopCount(int count){
			this.loopCount=count;
		}
		public int getLoopCount(){
			return this.loopCount;
		}
		public String toString(){
			StringBuilder toString=new StringBuilder(this.loopCount+":"+this.actionName+" "+this.sumBets);
			ActionChain current=this;
			while(current.loopNext!=null){
				toString.append("->"+current.loopNext.getActionName()+" "+current.loopNext.sumBets);
				current=current.loopNext;
			}
			return toString.toString();
		}
	}
	
	
	public static void main(String args[]){
		msgStack.add("a");
		msgStack.add("b");
		msgStack.add("c");
		msgStack.add("d");
		String s;
	while((s=msgStack.pollLast())!=null){
		  System.out.println(s);
		}
		
		Opponent.opponents.put("ss", new Opponent("ss"));
		Opponent.opponents.get("ss").zriverRaiseCount++;
		Opponent.opponents.get("ss").addRiverRaiseBet(Integer.parseInt("22")-getMaxBetsOfPlayers());
		playerBets.put("ss", 1111221);
		  System.out.println(getMaxBetsOfPlayers());
		  HashSet<String> preRaisePlayers=new HashSet<String>();
		  preRaisePlayers.add("111");
		  System.out.println(preRaisePlayers.size());
		  preRaisePlayers.remove("111");
		  preRaisePlayers.remove("111");
		  preRaisePlayers.remove("111");
		  preRaisePlayers.remove("111");
		  preRaisePlayers.remove("111111");
		  System.out.println(preRaisePlayers.size());
		  String d="111:";
		  System.out.println(d.substring(0, d.indexOf(":")));
		  
		  System.out.println(1<<8);
	}
}
