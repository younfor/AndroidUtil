package com.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.game.Player;
import com.game.State;
import com.util.Log;

public class Game {
	String pid,pname;
	int pidIndex;
	int bigblindbet,smallblindbet;
	List<Player>  players=new ArrayList<Player>();
	State state=new State();
	public Game(String id,String name)
	{
		pid=id;
		pname=name;
	}
	public void reg(OutputStream out) throws IOException
	{
		String cmd="reg: "+pid+" "+pname+" \n";
		out.write(cmd.getBytes());
		Log.getIns(pid).log("start to reg");
	}
	public void start(BufferedReader in,OutputStream out) throws IOException
	{
		
		String s=null;
		while((s=in.readLine())!=null)
		{
			if(s.startsWith("seat"))
			{
				/*
				seat/ 
				button: 1111 2000 8000 
				small blind: 8888 2000 8000 
				big blind: 7777 2000 8000 
				3333 2000 8000 
				2222 2000 8000 
				6666 2000 8000 
				5555 2000 8000 
				4444 2000 8000 
				/seat
				 **/
				players.clear();
				Log.getIns(pid).log("start to seat");
				s=in.readLine();
				while(!s.startsWith("/seat"))
				{
					if(s.startsWith("button"))
					{
						String []data=s.split(" ");
						players.add(new Player(data[1],Integer.parseInt(data[2]),Integer.parseInt(data[3]),State.button));
						Log.getIns(pid).log("button "+data[1]+","+data[2]+","+data[3]);
					}
					else if(s.startsWith("small"))
					{
						String []data=s.split(" ");
						players.add(new Player(data[2],Integer.parseInt(data[3]),Integer.parseInt(data[4]),State.smallblind));
						Log.getIns(pid).log("small "+data[2]+","+data[3]+","+data[4]);
					}
					else if(s.startsWith("big"))
					{
						String []data=s.split(" ");
						players.add(new Player(data[2],Integer.parseInt(data[3]),Integer.parseInt(data[4]),State.bigblind));
						Log.getIns(pid).log("big "+data[2]+","+data[3]+","+data[4]);
					}else
					{
						String []data=s.split(" ");
						players.add(new Player(data[0],Integer.parseInt(data[1]),Integer.parseInt(data[2]),State.normal));
						Log.getIns(pid).log("normal  "+data[0]+","+data[1]+","+data[2]);
					}
					s=in.readLine();
				}
			}
			else if(s.startsWith("blind"))
			{
				Log.getIns(pid).log("start to blind");
				s=in.readLine();
				while(!s.startsWith("/blind"))
				{
					/*
					 blind/ 
					8888: 50 
					7777: 100 
					/blind
					 * */
					String data[]=s.split(" ");
					data[0]=data[0].substring(0, data[0].length()-1);
					Player temp=findById(data[0]);
					if(temp.getType()==State.smallblind)
					{
						Log.getIns(pid).log(temp.getPid()+"is small blind:"+data[1]);
						smallblindbet=Integer.parseInt(data[1]);
					}
					else if (temp.getType()==State.bigblind)
					{
						Log.getIns(pid).log(temp.getPid()+"big blind:"+data[1]);
						bigblindbet=Integer.parseInt(data[1]);
					}
					s=in.readLine();
				}
			}else if(s.startsWith("hold"))
			{
				Log.getIns(pid).log("get hold");
				s=in.readLine();
				while(!s.startsWith("/hold"))
				{
					/*
					hold/ 
					HEARTS Q 
					SPADES 10 
					/hold 
					 */
				}
			}else if(s.startsWith("inquire"))
			{
				Log.getIns(pid).log("get inquire");
				s=in.readLine();
				while(!s.startsWith("/inquire"))
				{
					/*
					inquire/ 
					4444 2000 8000 0 fold 
					5555 2000 8000 0 fold 
					6666 2000 8000 0 fold 
					2222 2000 8000 0 fold 
					3333 2000 8000 0 fold 
					7777 1900 8000 100 blind 
					8888 1950 8000 50 blind 
					total pot: 150 
					/inquire
					 */
				}
			}else if(s.startsWith("pot-win"))
			{
				Log.getIns(pid).log("get pot-win");
				s=in.readLine();
				while(!s.startsWith("/pot-win"))
				{
					/*
					inquire/ 
					4444 2000 8000 0 fold 
					5555 2000 8000 0 fold 
					6666 2000 8000 0 fold 
					2222 2000 8000 0 fold 
					3333 2000 8000 0 fold 
					7777 1900 8000 100 blind 
					8888 1950 8000 50 blind 
					total pot: 150 
					/inquire
					 */
				}
			}
			
			//Log.getIns(pid).log(s);
		}
		
	}
	public Player findById(String id)
	{
		for(int i=0;i<players.size();i++)
		{
			if(players.get(i).getPid().equals(id))
				return players.get(i);
		}
		return null;
	}
	/*
	public static void main(String []args) throws IOException
	{
		Log.getIns("2").log("哈哈");
	}*/
}
