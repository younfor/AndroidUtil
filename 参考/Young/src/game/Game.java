package game;


import java.util.Arrays;
import java.util.HashMap;

public class Game {
	public final static  int JETTON=2000;
	public final static   int MONEY=2000;
	public final static   int ROUNDTIME=600;
	public final static   int PLAYERSNUM=8;
	
	public final static  int BB=40;
	public final static  int SB=20;
	
	public static String MYID;

	public static int handFoldCount;


	
	
	
	/*
	 * test
	 * winCounts,showDownCounts
	 */
	public static int []holdScoreCount=new int[29];
	
	public static int []winFlopRateCount=new int[11];
	public static int []winTurnRateCount=new int[11];
	public static int []winRiverRateCount=new int[11];
	public static int winRiverRateOver8Count[]= new int [21];
	public static int winTurnRateOver8Count[]= new int [21];
	
	public  static HashMap<String,Integer> winCounts=new HashMap<String,Integer>();
	public static HashMap<String,Integer> showDownCounts=new HashMap<String,Integer>();
	//统计为盖牌人数
//	public static int [] noFoldCount=new int[9];
	public static void addPotWinMsg(String msg){
		String msgs[]=msg.split(" ");
		if(winCounts.containsKey(msgs[0])){
			winCounts.put(msgs[0], winCounts.get(msgs[0])+1);
		}else winCounts.put(msgs[0],1 );
		
//		if(msgs[0].equals(MYID)){
//			
//		}
	}
	public static void showDownCount(String msg){
		String msgs[]=msg.split(" ");//2: 2222 SPADES 8 DIAMONDS A ONE_PAIR 
		if(showDownCounts.containsKey(msgs[1])){
			showDownCounts.put(msgs[1], showDownCounts.get(msgs[1])+1);
		}else showDownCounts.put(msgs[1],1 );
	}
	public static void gameOver(){
		
//		System.out.println("------------------flopNoFoldCount------------------------");
//		System.out.println(Arrays.toString(GameRecord.noFoldCount));
	
		System.out.println("-----------holdCardCount-----------");
		System.out.println(Arrays.toString(holdScoreCount));
		System.out.println("----------handFoldCount-----------");
		System.out.println(handFoldCount);
		System.out.println("--------------------ShowDownCount-------------------");
		System.out.println(showDownCounts);
		System.out.println("--------------------WinCount-------------------");
		System.out.println(winCounts);
		System.out.println("----------winRate--------------");
		int flopOver4Sum=0;
		int flopSum=0;
		int turnOver4Sum=0;
		int turnSum=0;
		int riverOver4Sum=0;
		int riverSum=0;
		
		
		System.out.println("-----------flop----------");
		System.out.println(Arrays.toString(winFlopRateCount));
		for(int i=0;i<winFlopRateCount.length;i++){
			flopSum+=winFlopRateCount[i];
			if(i>=3){
				flopOver4Sum+=winFlopRateCount[i];
			}
		}
		System.out.println("flopOver3Sum:"+flopOver4Sum);
		System.out.println("flopSum:"+flopSum);
		System.out.println("flopOver3Rate:"+(double)flopOver4Sum/flopSum);
		
		
		
		
		
		System.out.println("-----------turn----------");
		System.out.println(Arrays.toString(winTurnRateCount));
		for(int i=0;i<winTurnRateCount.length;i++){
			turnSum+=winTurnRateCount[i];
			if(i>=5){
				turnOver4Sum+=winTurnRateCount[i];
			}
		}
		System.out.println("turnOver5Sum:"+turnOver4Sum);
		System.out.println("turnSum:"+turnSum);
		System.out.println("turnOver5Rate:"+(double)turnOver4Sum/turnSum);
		
		
		
		
		
		System.out.println("-----------river----------");
		System.out.println(Arrays.toString(winRiverRateCount));
		for(int i=0;i<winRiverRateCount.length;i++){
			riverSum+=winRiverRateCount[i];
			if(i>=6){
				riverOver4Sum+=winRiverRateCount[i];
			}
		}
		System.out.println("riverOver6Sum:"+riverOver4Sum);
		System.out.println("riverSum:"+riverSum);
		System.out.println("riverOver6Rate:"+(double)riverOver4Sum/riverSum);
		
		System.out.println(Arrays.toString(winTurnRateOver8Count));
		System.out.println(Arrays.toString(winRiverRateOver8Count));
		
	}

}
