package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
/*
 * 每一局牌
 */

public class Party {
	private int count=0;//局数统计
	
	private Card [] holdCards=new Card[2];
	private Card [] flopCards=new Card[3];
	private Card turnCard;
	private Card riverCard;
	
	public 	BufferedReader in=null;
	public PrintStream out =null;

	public Party(PrintStream out){
		this.out=out;
	}
	
	public int getCount() {
		return count;
	}

	public void startOneParty(BufferedReader in) {
		this.in = in;
		this.count++;
	}
	/*
	 * playID 用于消息日志命名
	 */
	public void playOneParty(String playerID,String s) throws IOException{
		PrintUtil.printToLog(System.out,playerID);
		String msgHead=s;
		while(!(msgHead=in.readLine()).equals("/pot-win ")){
			
			if(msgHead.equals("hold/ ")){
				holdCards[0]=new Card(in.readLine());
				holdCards[1]=new Card(in.readLine());	
			}
			if(msgHead.equals("/hold ")&&(msgHead=in.readLine()).equals("inquire/ ")){
				/*
				 * 匹配 翻牌前下注 询问
				 */
				double a = Math.random();
				if(a > 0.50) { this.out.write("fold \n".getBytes());}
				else {this.out.write("call \n".getBytes());}
			}
			if(msgHead.equals("flop/ ")){
				flopCards[0]=new Card(in.readLine());
				flopCards[1]=new Card(in.readLine());	
				flopCards[2]=new Card(in.readLine());	
			}
			if(msgHead.equals("/flop ")&&(msgHead=in.readLine()).equals("inquire/ ")){
				/*
				 * 匹配 翻牌圈下注 询问
				 */
				System.out.println("------------------------");
				double a = Math.random();
				if(a > 0.50) { this.out.write("fold \n".getBytes());}
				else {this.out.write("call \n".getBytes());}
				
			}
			if(msgHead.equals("turn/ ")){
				turnCard=new Card(in.readLine());
			}
			if(msgHead.equals("/turn ")&&(msgHead=in.readLine()).equals("inquire/ ")){
				/*
				 * 匹配 转牌圈下注 询问
				 */
				double a = Math.random();
				if(a > 0.50) { this.out.write("fold \n".getBytes());}
				else {this.out.write("call \n".getBytes());}
				
			}
			if(msgHead.equals("river/ ")){
				riverCard=new Card(in.readLine());
			}
			if(msgHead.equals("/river ")&&(msgHead=in.readLine()).equals("inquire/ ")){
				/*
				 * 匹配 河牌圈下注 询问
				 */
				double a = Math.random();
				if(a > 0.50) { this.out.write("fold \n".getBytes());}
				else {this.out.write("call \n".getBytes());}
				
			}
			
		}
		System.out.println("party :"+this.getCount());
		System.out.println("holdCards:"+Arrays.toString(holdCards));
		System.out.println("flopCards:"+Arrays.toString(flopCards));
		System.out.println("turnCard:"+this.turnCard);
		System.out.println("riverCard:"+this.riverCard);
		
	}
	
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
