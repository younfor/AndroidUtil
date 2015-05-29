package com.game;

public class PlayerAction {
	public PlayerAction(String id)
	{
		this.id=id;
		for(int i=0;i<4;i++)
		{
			action[i]=new Action();
		}
	}
	public String id;
	public Action action[]=new Action[4];
}
