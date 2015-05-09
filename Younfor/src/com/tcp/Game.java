package com.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.game.Card;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class Game {
	String pid,pname;
	int bigblindbet,smallblindbet,totalpot;
	List<Player>  players=new ArrayList<Player>();
	Card hand[]=new Card[2];
	Card host[]=new Card[5];
	int currentState=-1;
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
		out.flush();
		Log.getIns(pid).log("start to reg");
	}
	public void start(BufferedReader in,OutputStream out) throws IOException
	{
		
		String s=null;
		while((s=in.readLine())!=null)
		{
			if(s.startsWith("game-over"))
			{
				/*
				 game-over eol
				 */
				in.close();
				out.close();
				Log.getIns(pid).log("game over");
				break;
			}
			else if(s.startsWith("seat"))
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
				currentState=State.baseState;
				s=in.readLine();
				int i=0;
				while(!s.startsWith("/hold") && (i<2))
				{
					/*
					hold/ 
					HEARTS Q 
					SPADES 10 
					/hold 
					 */
					String data[]=s.split(" ");
					hand[i]=new Card(State.getColor(data[0]),State.getPoint(data[1]) );
					Log.getIns(pid).log("flop card: "+hand[i].getColor()+"-"+hand[i].getPoint());
					i++;
					
					s=in.readLine();
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
					String data[]=s.split(" ");
					if(data[0].startsWith("total"))
					{
						Log.getIns(pid).log("get total"+data[2]);
						totalpot=Integer.parseInt(data[2]);
					}else{
						Player p=findById(data[0]);
						p.setJetton(Integer.parseInt(data[1]));
						p.setGold(Integer.parseInt(data[2]));
						p.setBet(Integer.parseInt(data[3]));
						p.setLastaction(State.getAction(data[4]));
						Log.getIns(pid).log("get player"+p.getPid()+" "+p.getJetton()+" "+p.getGold()+" "+p.getBet()+" "+p.getLastaction());
					}
					s=in.readLine();
				}
				//reply action
				/*
				 check | call | raise num | all_in | fold eol
				 */
				
				String action[]={"check","call","raise 0","all_in","fold"};
				Random r=new Random();
				int num=Math.abs(r.nextInt())%5;
				Log.getIns(pid).log("action num: "+num);
				String ans=action[num]+" \n";
				Log.getIns(pid).log("action: "+ans);
				out.write(ans.getBytes());
				out.flush();
			}else if(s.startsWith("flop"))
			{
				Log.getIns(pid).log("get flop");
				currentState=State.flopState;
				s=in.readLine();
				int i=0;
				while(!s.startsWith("/flop"))
				{
					/*
					flop/ eol
					color point eol
					color point eol
					color point eol
					/flop eol
					 */
					String data[]=s.split(" ");
					host[i]=new Card(State.getColor(data[0]),State.getPoint(data[1]) );
					Log.getIns(pid).log("flop card: "+host[i].getColor()+"-"+host[i].getPoint());
					s=in.readLine();
					i++;
				}
			}
			else if(s.startsWith("turn"))
			{
				Log.getIns(pid).log("get turn");
				currentState=State.turnState;
				s=in.readLine();
				int i=3;
				while(!s.startsWith("/turn"))
				{
					/*
					turn/ eol
					color point eol
					/turn eol
					 */
					String data[]=s.split(" ");
					host[i]=new Card(State.getColor(data[0]),State.getPoint(data[1]) );
					Log.getIns(pid).log("turn card: "+host[i].getColor()+"-"+host[i].getPoint());
					s=in.readLine();
					i++;
				}
			}
			else if(s.startsWith("river"))
			{
				Log.getIns(pid).log("get river");
				currentState=State.riverState;
				s=in.readLine();
				int i=4;
				while(!s.startsWith("/river"))
				{
					/*
					river/ eol
					color point eol
					/river eol
					 */
					String data[]=s.split(" ");
					host[i]=new Card(State.getColor(data[0]),State.getPoint(data[1]) );
					Log.getIns(pid).log("river card: "+host[i].getColor()+"-"+host[i].getPoint());
					s=in.readLine();
					i++;
				}
			}
			else if(s.startsWith("showdown"))
			{
				Log.getIns(pid).log("get showdown");
				s=in.readLine();
				while(!s.startsWith("/showdown"))
				{
					/*
					showdown/ eol
					common/ eol
					(color point eol)5
					/common eol
					(rank: pid color point color point nut_hand eol)2-8
					/showdown eol
					 */
					Log.getIns(pid).log(s);
					s=in.readLine();
				}
			}
			else if(s.startsWith("pot-win"))
			{
				Log.getIns(pid).log("get pot-win");
				s=in.readLine();
				while(!s.startsWith("/pot-win"))
				{
					/*
					pot-win/ eol
					(pid: num eol)0-8
					/pot-win eol
					 */
					Log.getIns(pid).log(s);
					s=in.readLine();
				}
			}
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
}
