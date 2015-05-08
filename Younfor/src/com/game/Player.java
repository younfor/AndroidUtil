package com.game;

public class Player {
	public String pid;
	public int bet;
	public int gold;
	public int type;  //button smallblind bigblind
	boolean isAlive=true;
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public int getBet() {
		return bet;
	}
	public void setBet(int bet) {
		this.bet = bet;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public Player(String pid, int bet, int gold, int type) {
		super();
		this.pid = pid;
		this.bet = bet;
		this.gold = gold;
		this.type = type;
	}

	
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
}
