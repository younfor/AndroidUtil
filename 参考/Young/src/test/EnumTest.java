package test;

public class EnumTest {
	
	public static enum Suit {
		CLUB, DIAMOND, HEART, SPADE;
	}
	public static enum Rank {
		TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;
	}
	public static void main(String []args){
		System.out.println(Suit.SPADE.ordinal());
		System.out.println(Rank.TWO.ordinal());
	}
}
