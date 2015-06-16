package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import util.ReaderProxy;
import util.RuntimeEval;

/*
 * 初赛 预版本 
 * 不需要 notify
 * 
 * 
 */
public class Player {
	private  String serverIP;
	private  int serverPort;
	private  String playerIP;
	private  int playerPort;
	private  String playerID;
	private  String name="ShuQi";
	
	private Socket table=null; //牌桌socket
	private OutputStream out =null;
	private BufferedReader reader=null;
	private Reciever game =null;
	
	
	/*
	 * 方便测试的 消息接收日志 打印开关
	 */
	private boolean printToLog=false;//默认true为开启
	
    
	
	public Player(String serverIP, String serverPort, String playerIP,
			String playerPort, String playerID) {
		this.serverIP = serverIP;
		this.serverPort = Integer.parseInt(serverPort);
		this.playerIP = playerIP;
		this.playerPort =Integer.parseInt(playerPort);;
		this.playerID = playerID;
		Game.MYID=playerID;
		this.connect();//初始化套接字
	}
	
	/*
	 * 连接
	 */
	public void connect(){
		SocketAddress myAddress=new InetSocketAddress(this.playerIP,this.playerPort);
		SocketAddress serverAddress=new InetSocketAddress(this.serverIP,this.serverPort);
		this.table=new Socket();
		try {
			table.setReuseAddress(true);
			table.bind(myAddress);
			table.connect(serverAddress);
			this.out=table.getOutputStream();
			if(printToLog){//打印消息接收日志开关开启，则用代理类ReaderProxy ；否则用原生BufferedReader；开启后，readLine()将打印到消息日志文件
				this.reader=new ReaderProxy(new InputStreamReader(table.getInputStream()),this.playerID);
			}else {
				this.reader=new BufferedReader(new InputStreamReader(table.getInputStream()));
			}
		} catch (IOException e) {
			this.connect();
		}
	}
//		this.table=new Socket(this.serverIP,this.serverPort,myAddress,this.playerPort);  
	/*
	 * 注册
	 */
	public void regist() {
			
		try {
//			String msg="reg: "+this.playerID+" "+this.name+" \n";
			String msg="reg: "+this.playerID+" "+this.name+" need_notify "+" \n";
			byte [] msgAsc=msg.getBytes("ASCII");	
			out.write(msgAsc);
		} catch (IOException e) {
			regist();
		}
	}
		
		
	public static void  main(String []args) {
		//创建牌手
		Player player=new Player(args[0],args[1],args[2],args[3],args[4]);
		
		//注册进场
		player.regist();
		//坐下
		player.game=new Reciever();
		//开始
		player.game.startGame(player.reader,player.out,player.playerID);	
		
		//game over就关闭套接字，不然有可能影响下一场比赛
		if(player.game.gameOver()){
			player.gameOver();
		}
		
		//copy Log 到win共享
//		CopyServerLog.copyLog();
		
	}
	

	

	public void gameOver(){
		try {
			this.table.close();
		} catch (IOException e) {
			gameOver();
		}
		
		
		
		
		/*
		 * 测试统计
		 */

		Game.gameOver();
		RuntimeEval.gameOver();
		System.out.println("--------------Learn--------------");
		Learn.gameOver();
		System.out.println("--------------OpponentEval--------------");
		Opponent.gameOver();
	}
	
}
