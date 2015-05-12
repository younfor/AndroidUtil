package com.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import com.ai.simplebot.Bys;
import com.ai.simplebot.PokerLib;
import com.bot.Bot;
import com.bot.CallBot;
import com.bot.CleverBot;
import com.bot.RaiseCallBot;
import com.bot.RandomBot;
import com.bot.SimpleBot;
import com.game.Card;
import com.game.Player;
import com.game.State;
import com.util.Log;

public class Game {
	
	State state=new State();
	Bot bot=new SimpleBot();
	public Game(String id,String name)
	{
		state.pid=id;
		state.pname=name;
		if(name.equals("simplebot"))
			bot=new SimpleBot();
		else if(name.equals("randombot"))
			bot=new RandomBot();
		else if(name.equals("callbot"))
			bot=new CallBot();
		else if(name.equals("cleverbot"))
			bot=new CleverBot();
		else if(name.equals("raisecallbot"))
			bot=new RaiseCallBot();
		
	}
	public void reg(OutputStream out) throws IOException
	{
		String cmd="reg: "+state.pid+" "+state.pname+" \n";
		out.write(cmd.getBytes());
		out.flush();
		Log.getIns(state.pid).log("start to reg");
	}
	public void start(BufferedReader in,OutputStream out) 
	{
		try{
		
		String s=null;
		PokerLib.init();
		while((s=in.readLine())!=null)
		{
			if(s.startsWith("game-over"))
			{
				/*
				 game-over eol
				 */
				in.close();
				out.close();
				Log.getIns(state.pid).log("game over");
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
				state.clear();
				Log.getIns(state.pid).log("start to seat");
				s=in.readLine();
				while(!s.startsWith("/seat"))
				{
					Player player=null;
					if(s.startsWith("button"))
					{
						String []data=s.split(" ");
						player=new Player(data[1],Integer.parseInt(data[2]),Integer.parseInt(data[3]),State.button);
						state.players.add(player);
						Log.getIns(state.pid).log("button "+data[1]+","+data[2]+","+data[3]);
					}
					else if(s.startsWith("small"))
					{
						String []data=s.split(" ");
						player=new Player(data[2],Integer.parseInt(data[3]),Integer.parseInt(data[4]),State.smallblind);
						state.players.add(player);
						Log.getIns(state.pid).log("small "+data[2]+","+data[3]+","+data[4]);
					}
					else if(s.startsWith("big"))
					{
						String []data=s.split(" ");
						player=new Player(data[2],Integer.parseInt(data[3]),Integer.parseInt(data[4]),State.bigblind);
						state.players.add(player);
						Log.getIns(state.pid).log("big "+data[2]+","+data[3]+","+data[4]);
					}else
					{
						String []data=s.split(" ");
						player=new Player(data[0],Integer.parseInt(data[1]),Integer.parseInt(data[2]),State.normal);
						state.players.add(player);
						Log.getIns(state.pid).log("normal  "+data[0]+","+data[1]+","+data[2]);
					}
					if(player.getPid().equals(state.pid))
						state.setJetton(player.getJetton());
					s=in.readLine();
				}
			}
			else if(s.startsWith("blind"))
			{
				Log.getIns(state.pid).log("start to blind");
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
						Log.getIns(state.pid).log(temp.getPid()+" is small blind:"+data[1]);
						state.smallblindbet=Integer.parseInt(data[1]);
					}
					else if (temp.getType()==State.bigblind)
					{
						Log.getIns(state.pid).log(temp.getPid()+" is big blind:"+data[1]);
						state.bigblindbet=Integer.parseInt(data[1]);
					}
					s=in.readLine();
				}
			}else if(s.startsWith("hold"))
			{
				Log.getIns(state.pid).log("get hold");
				state.currentState=State.baseState;
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
					state.handcard[i]=new Card(data[0],data[1]);
					Log.getIns(state.pid).log("base card: "+state.handcard[i].getColor()+"-"+state.handcard[i].getPoint());
					i++;
					s=in.readLine();
				}
				Log.getIns(state.pid).log("value:"+state.handcard[0].getValue()+state.handcard[1].getValue());
				state.setHand(state.handcard[0].getValue(), state.handcard[1].getValue());
				Log.getIns(state.pid).log("base card: "+state.getHand()[0]+","+state.getHand()[1]);
			}else if(s.startsWith("inquire"))
			{
				Log.getIns(state.pid).log("get inquire");
				s=in.readLine();
				int lastbet=0;
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
						Log.getIns(state.pid).log("get total"+data[2]);
						state.totalpot=Integer.parseInt(data[2]);
					}else{
						Player p=findById(data[0]);
						p.setJetton(Integer.parseInt(data[1]));
						p.setGold(Integer.parseInt(data[2]));
						p.setBet(Integer.parseInt(data[3]));
						p.setLastaction(State.getAction(data[4]));
						//bys
						if(p.getLastaction()==State.fold)
							p.actions[state.currentState-State.baseState]=Bys.fold;//flod 0 call 1 raise 2
						else if(p.getLastaction()==State.raise)
							p.actions[state.currentState-State.baseState]=Bys.raise;
						else
							p.actions[state.currentState-State.baseState]=Bys.call;
						if(p.getLastaction()==State.raise)
							state.raisenum++;
						if(lastbet==0)
						{
							lastbet++;
							state.setBet(p.getBet());
						}
						if(p.getPid().equals(state.pid))
						{
							//self
							state.setJetton(p.getJetton());
						}
						Log.getIns(state.pid).log("get player"+p.getPid()+" "+p.getJetton()+" "+p.getGold()+" "+p.getBet()+" "+p.getLastaction());
					}
					s=in.readLine();
				}
				//reply action
				/*
				 check | call | raise num | all_in | fold eol
				 */
				//String action[]={"check","call","raise 0","all_in","fold"};
				//Random r=new Random();
				//int num=Math.abs(r.nextInt())%5;
				//Log.getIns(pid).log("action num: "+num);
				int len=0;
				if(state.currentState==State.baseState)
					len=0;
				else if(state.currentState==State.flopState)
					len=3;
				else if(state.currentState==State.turnState)
					len=4;
				else if(state.currentState==State.riverState)
					len=5;
				state.setComm(len);
				int ans=bot.getBestAction(state, 250);
				String action="fold";
				if(ans==State.fold)
					action="fold";
				else if(ans==State.call)
					action="check";
				else if(ans==State.raise)
					action="raise "+State.raisebet;
				Log.getIns(state.pid).log("action: "+action);
				out.write(action.getBytes());
				out.flush();
			}else if(s.startsWith("flop"))
			{
				Log.getIns(state.pid).log("get flop");
				state.currentState=State.flopState;
				state.raisenum=0;
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
					state.hostcard[i]=new Card(data[0],data[1]);
					Log.getIns(state.pid).log("flop card: "+state.hostcard[i].getColor()+"-"+state.hostcard[i].getPoint());
					s=in.readLine();
					i++;
				}
			}
			else if(s.startsWith("turn"))
			{
				Log.getIns(state.pid).log("get turn");
				state.currentState=State.turnState;
				state.raisenum=0;
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
					state.hostcard[i]=new Card(data[0],data[1]);
					Log.getIns(state.pid).log("turn card: "+state.hostcard[i].getColor()+"-"+state.hostcard[i].getPoint());
					s=in.readLine();
					i++;
				}
			}
			else if(s.startsWith("river"))
			{
				Log.getIns(state.pid).log("get river");
				state.currentState=State.riverState;
				state.raisenum=0;
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
					state.hostcard[i]=new Card(data[0],data[1]);
					Log.getIns(state.pid).log("river card: "+state.hostcard[i].getColor()+"-"+state.hostcard[i].getPoint());
					s=in.readLine();
					i++;
				}
			}
			else if(s.startsWith("showdown"))
			{
				Log.getIns(state.pid).log("get showdown");
				s=in.readLine();
				if(s.startsWith("common"))
				{
					/*
					showdown/ eol
					common/ eol
					(color point eol)5
					/common eol
					(rank: pid color point color point nut_hand eol)2-8
					/showdown eol
					 	showdown/ 
						common/ 
						SPADES 2 
						HEARTS 9 
						CLUBS J 
						DIAMONDS 10 
						SPADES K 
						/common 
						1: 8888 SPADES Q DIAMONDS J STRAIGHT 
						2: 6666 HEARTS 7 DIAMONDS 4 HIGH_CARD 
						1: 5555 HEARTS 8 DIAMONDS Q STRAIGHT 
						/showdown 
					 */
					//bys
					while(!s.startsWith("/common"))
						s=in.readLine();
					s=in.readLine();
					while(!s.startsWith("/showdown"))
					{
						Log.getIns(state.pid).log(s);
						String data[]=s.split(" ");
						String id=data[0].substring(2);
						if(!state.bys.containsKey(id))
						{
							Log.getIns(state.pid).log("create bys: "+id);
							state.bys.put(id, new Bys());
						}
						Log.getIns(state.pid).log("create bys: rank "+state.findRank(data[5]));
						state.bys.get(id).addBys(findById(id).actions, state.findRank(data[5]));
						int a[]=state.bys.get(id).getBys(new int[]{Bys.call});
						for(int i=0;i<a.length;i++)
							Log.getIns(state.pid).log("bys num: " +a[i]);
						s=in.readLine();
					}
					
				}
			}
			else if(s.startsWith("pot-win"))
			{
				Log.getIns(state.pid).log("get pot-win");
				s=in.readLine();
				while(!s.startsWith("/pot-win"))
				{
					/*
					pot-win/ eol
					(pid: num eol)0-8
					/pot-win eol
					 */
					Log.getIns(state.pid).log(s);
					s=in.readLine();
				}
			}
		}
		}catch(Exception e)
		{
			try {
				Log.getIns(state.pid).log(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public Player findById(String id)
	{
		for(int i=0;i<state.players.size();i++)
		{
			if(state.players.get(i).getPid().equals(id))
				return state.players.get(i);
		}
		return null;
	}
	
}
