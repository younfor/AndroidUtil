package com.game;

public class Hand {
	Card card[]=new Card[5];
	int len=0;
	public int size()
	{
		return len;
	}
	public Card getCard(int loc)
	{
		return card[loc-1];
	}
	public void addCard(Card c)
	{
		card[len]=c;
		len++;
	}
}
