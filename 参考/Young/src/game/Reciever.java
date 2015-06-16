package game;



import java.io.BufferedReader;
import java.io.OutputStream;

import util.PrintUtil;


/*
 * 程序主流程
 */

public class Reciever {
	/*
	 *Cards
	 */
	private Card [] holdCards=new Card[2];
	private Card [] flopCards=new Card[3];
	private Card turnCard;
	private Card riverCard;
	private  String playerID;
	
	public 	BufferedReader reader=null;//读消息
	public OutputStream out =null;//回应消息

	
	private Action action=new Action();

	/*
	 * 一些状态
	 */
	private boolean isFoldOrAllIn=false;
	private boolean isGameOver=false;
	
	
	boolean isPrintLog =false;//测试日志打印开关，默认true开
	

	/*
	 * 重置游戏;也重置Roundecord
	 */
	public void resetGame(){
		
		//重设牌，防止影响
		this.holdCards=new Card[2];
		this.flopCards=new Card[3];
		this.turnCard=null;
		this.riverCard=null;
		this.isFoldOrAllIn=false;
		this.isGameOver=false;
		Round.resetRound();//重置RoundRecord
		//System.out.println("---------- party end-------------");
		//System.out.println();
		
	
		
	}
	/*
	 * 开始游戏，传单套接字读写流进来
	 */
	public void startGame(BufferedReader reader,OutputStream out,String playerID) {
		this.reader = reader;
		this.out=out;
		this.playerID=playerID;
		this.playGame();
	}
	
	
  /*
   * play game，接收消息框架
   */
	public void playGame(){
		
		if(isPrintLog){//打印测试日志
			PrintUtil.printToLog(this.playerID+"log",true);
		}
		
		try{
			String msgHead;//此用来打印其它想要的东西到日志（完整消息日志已在开启打印状态下，自动将readLine()打印到日志）
			
			//不断读取消息
			while(!(msgHead=reader.readLine()).startsWith("game-over")){
				/*
				 * 新一局开始，座次信息
				 */
				//System.out.println(Arrays.toString(RoundRecord.idsOrderByPositions));
				if(msgHead.startsWith("seat/")){
					Round.roundCount++;//局计数
					this.resetGame();//重置每一局游戏，初始化 牌 fold all_in 标记等，以防影响
					
					//System.out.println("Game Num:"+count);
					//System.out.println("-----座次：-----");
					//记录座次信息到 局状态
					while(!(msgHead=reader.readLine()).startsWith("/seat")){
						
						Round.addSeatMag(msgHead);
					}
					//System.out.println("position:"+RoundRecord.getMyposition());
					//System.out.println(RoundRecord.myJetton);
	
				}
				/*
				 * 盲注信息
				 */
				if(msgHead.startsWith("blind/")){
					//System.out.println("-----盲注：-----");
					while(!(msgHead=reader.readLine()).startsWith("/blind")){
						 //System.out.println(msgHead);
						
					}
				}
			
		
				/*
				 * 手牌信息
				 */
		
				if(msgHead.startsWith("hold/")){
					//System.out.println("-----手牌-----");
					holdCards[0]=new Card(msgHead=reader.readLine());
					holdCards[1]=new Card(msgHead=reader.readLine());	
					//System.out.println("holdCards:"+Arrays.toString(holdCards));
						 
					/*
					 * 翻牌前下注(手牌下注)
					 */
					if((msgHead=reader.readLine()).startsWith("/hold")){
						int loopTime=0;
						Action.isRaise=false;
						Round.firstRaiseBet=0;Round.firstRaise=false;Round.msgStack.clear();
//						Round.maxBet=-1;
//						private static String maxActionId;
						Round.lastActionId=null;
						Round.currentPreId=null;
						while((msgHead=reader.readLine()).startsWith("inquire/")||msgHead.startsWith("notify/")){
							loopTime++;
							while(!((msgHead=reader.readLine()).startsWith("/inquire"))&&!msgHead.startsWith("/notify")){//继续读取inquire
								if(msgHead.startsWith("total pot:")){
									//System.out.println("彩池注额："+msgHead.split(" ")[2]);
								}else{
									//Round.msgStack.add(msgHead);
									Round.addInquireOrNotifyMsg(msgHead);
								}
							}
							Round.handleInquireMsg("preFlop",loopTime,isFoldOrAllIn);
							/*
							 * 动作
							 */
							if(!isFoldOrAllIn&&msgHead.startsWith("/inquire")){//双保险
								int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
								String resMsg;
							
								/*取得动作*/
								resMsg=action.preFlopAction(holdCards,maxBetOfPlayers-Round.getPlayerBet(this.playerID),Round.noFoldPlayers.size(),loopTime);
								
								if(resMsg.startsWith("fold")&&maxBetOfPlayers==Round.getPlayerBet(this.playerID)){//能让则让
									resMsg="check \n";
								}else if(resMsg.startsWith("fold")||resMsg.startsWith("all_in")){
									isFoldOrAllIn=true;
								}
								if(resMsg.startsWith("fold")){
									Game.handFoldCount++;
								}
								//System.out.println("preflopAction : "+resMsg);
								this.out.write(resMsg.getBytes());//给出应答	 
							}
						}
	
					}
				}
			
				
				/*
				 * 翻牌消息
				 */
				if(msgHead.startsWith("flop/")){
						//System.out.println("-----翻牌-----");
					flopCards[0]=new Card(reader.readLine());
					flopCards[1]=new Card(reader.readLine());	
					flopCards[2]=new Card(reader.readLine());	
					//System.out.println("flopCards:"+Arrays.toString(flopCards));
					
					/*
					 * 翻牌圈下注
					 */
					if((msgHead=reader.readLine()).startsWith("/flop")){
						int loopTime=0;
						Action.isRaise=false;
						Round.firstRaiseBet=0;Round.firstRaise=false;Round.msgStack.clear();
//						Round.maxBet=-1;
//						private static String maxActionId;
						Round.lastActionId=null;
						Round.currentPreId=null;
						
						while((msgHead=reader.readLine()).startsWith("inquire/")||msgHead.startsWith("notify/")){
							loopTime++;
							while(!((msgHead=reader.readLine()).startsWith("/inquire"))&&!msgHead.startsWith("/notify")){//继续读取inquire
								if(msgHead.startsWith("total pot:")){
									//System.out.println("彩池注额："+msgHead.split(" ")[2]);
								}else{
									//Round.msgStack.add(msgHead);
									Round.addInquireOrNotifyMsg(msgHead);
								}	
							}
							Round.handleInquireMsg("flop",loopTime,isFoldOrAllIn);
							/*
							 *当没有盖牌 才需要动作
							 */
							if(!isFoldOrAllIn&&msgHead.startsWith("/inquire")){//双保险
								int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
								String resMsg;
								
								/*取得动作*/
								resMsg=action.flopAction(holdCards, flopCards,maxBetOfPlayers -Round.getPlayerBet(this.playerID),Round.noFoldPlayers.size(),loopTime);	
								
		
								if(resMsg.startsWith("fold")&&maxBetOfPlayers==Round.getPlayerBet(this.playerID)){//能让则让
									resMsg="check \n";
								}else
								if(resMsg.startsWith("fold")||resMsg.startsWith("all_in")){
									isFoldOrAllIn=true;
									if(resMsg.startsWith("fold")){
										Learn.addFold("flop");
									}
								}
								this.out.write(resMsg.getBytes());
								          //System.out.println("flopAction : "+resMsg);
							}	
						}
	
					}
				}
				/*
				 * 转牌消息
				 */
				if((msgHead).startsWith("turn/")){
					//System.out.println("-----转牌-----");
					turnCard=new Card(reader.readLine());
					//System.out.println("turnCard:"+this.turnCard);
					
					/*
					 *  转牌圈下注 
					 */
					if((msgHead=reader.readLine()).startsWith("/turn")){
						int loopTime=0;
						Action.isRaise=false;
						Round.firstRaiseBet=0;Round.firstRaise=false;Round.msgStack.clear();
//						Round.maxBet=-1;
//						private static String maxActionId;
						Round.lastActionId=null;
						Round.currentPreId=null;
						
						while((msgHead=reader.readLine()).startsWith("inquire/")||msgHead.startsWith("notify/")){
							loopTime++;
							while(!((msgHead=reader.readLine()).startsWith("/inquire"))&&!msgHead.startsWith("/notify")){//继续读取inquire
								if(msgHead.startsWith("total pot:")){
									//System.out.println("彩池注额："+msgHead.split(" ")[2]);
								}else{
									//Round.msgStack.add(msgHead);
									Round.addInquireOrNotifyMsg(msgHead);
								}
							}
							Round.handleInquireMsg("turn",loopTime,isFoldOrAllIn);
							if(!isFoldOrAllIn&&msgHead.startsWith("/inquire")){//双保险
								int maxBetOfPlayers=Round.getMaxBetsOfPlayers();;
								String resMsg;
								
								/*取得动作*/
								resMsg=action.turnAction(holdCards, flopCards, turnCard,maxBetOfPlayers -Round.getPlayerBet(this.playerID),Round.noFoldPlayers.size(),loopTime);		
			
								if(resMsg.startsWith("fold")&&maxBetOfPlayers==Round.getPlayerBet(this.playerID)){//能让则让
									resMsg="check \n";
								}else
								if(resMsg.startsWith("fold")||resMsg.startsWith("all_in")){
									isFoldOrAllIn=true;
									Learn.addFold("turn");
								}
								
								this.out.write(resMsg.getBytes());
							          //System.out.println("turnAction : "+resMsg);
							}
						}
	
					}
				}//end转牌消息
				
				/*
				 * 河牌
				 */
				if((msgHead).startsWith("river/")){
					//System.out.println("-----河牌-----");
					riverCard=new Card(reader.readLine());
					//System.out.println("riverCard:"+this.riverCard);
					
					/*
					 *  河牌圈下注 
					 */
					if((msgHead=reader.readLine()).startsWith("/river")){
						int loopTime=0;
						Action.isRaise=false;
						Round.firstRaiseBet=0;Round.firstRaise=false;Round.msgStack.clear();
//						Round.maxBet=-1;
//						private static String maxActionId;
						Round.lastActionId=null;
						Round.currentPreId=null;
						while((msgHead=reader.readLine()).startsWith("inquire/")||msgHead.startsWith("notify/")){
							loopTime++;
							while(!((msgHead=reader.readLine()).startsWith("/inquire"))&&!msgHead.startsWith("/notify")){//继续读取inquire
								if(msgHead.startsWith("total pot:")){
									//System.out.println("彩池注额："+msgHead.split(" ")[2]);
								}else{
									//Round.msgStack.add(msgHead);
									Round.addInquireOrNotifyMsg(msgHead);
								}
							}
							Round.handleInquireMsg("river",loopTime,isFoldOrAllIn);
							if(!isFoldOrAllIn&&msgHead.startsWith("/inquire")){//双保险
								int maxBetOfPlayers=Round.getMaxBetsOfPlayers();
								String resMsg;
								/*取得动作*/
								resMsg=action.riverAction(holdCards, flopCards, turnCard,riverCard,maxBetOfPlayers -Round.getPlayerBet(this.playerID),Round.noFoldPlayers.size(),loopTime);		
	
								if(resMsg.startsWith("fold")&&maxBetOfPlayers==Round.getPlayerBet(this.playerID)){//能让则让
									resMsg="check \n";
								}else
								if(resMsg.startsWith("fold")||resMsg.startsWith("all_in")){
									isFoldOrAllIn=true;
									Learn.addFold("river");
								}
								this.out.write(resMsg.getBytes());
							          //System.out.println("riverAction : "+resMsg);
							}
						}
	
					}
				}
				
				/*
				 * 有摊牌则有摊牌消息，无摊牌则没有
				 */
				if((msgHead).startsWith("showdown/")){
					//System.out.println("-----摊牌消息-----");
					while(!(msgHead=reader.readLine()).startsWith("/common")){
						//读公牌
					}
						
						//然后摊手牌
					if(msgHead.startsWith("/common")){
						while(!(msgHead=reader.readLine()).startsWith("/showdown")){
							//读摊牌
							Game.showDownCount(msgHead);
							Round.addShowDownMsg(msgHead,flopCards,turnCard,riverCard);
						}
					}
				}
				/*
				 * 彩池分配消息
				 */
				if(msgHead.startsWith("pot-win/")){
					while(!(msgHead=reader.readLine()).startsWith("/pot-win")){
						Game.addPotWinMsg(msgHead);
						Round.addPotWinMsg(msgHead);
					}
				}
			
			}//end while
			
			this.isGameOver=true;
		}catch(Exception e){
			e.printStackTrace(System.out);
			this.playGame();
		}
	
		
	}//end function	
	
	
	/*
	 * 游戏结束
	 */
	public boolean gameOver(){
		return this.isGameOver;
	}
	

}
