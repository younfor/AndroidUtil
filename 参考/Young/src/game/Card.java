
package game;

public class Card implements Comparable<Card>{
	private Suit suit;
	private Rank rank;
		
	public Card(String cardMsg) {
		String [] msg=cardMsg.split(" ");
		this.suit = Suit.getSuit(msg[0]);
		this.rank = Rank.getRank(msg[1]);
	}
	public Card(Suit suit,Rank rank){
		this.suit=suit;
		this.rank=rank;
	}
	
	public String getSuitStringValue() {
		return this.suit.stringValue();
	}

	public String getRankStringValue() {
		return this.rank.stringValue();
	}
	public Rank getRank(){
		return this.rank;
	}
	public Suit getSuit(){
		return this.suit;
	}
	
	
	
	public static enum Rank {
		TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;

		public static final String[] RANK_STRINGS =
						{"2","3","4","5","6","7","8","9","10","J","Q","K","A"};

		
		public String stringValue() {
			return RANK_STRINGS[this.ordinal()];
		}

	
		public static Rank getRank (String s) {
			for(int index=0;index<RANK_STRINGS.length;index++){
				if(s.equals(RANK_STRINGS[index])){
					return Rank.values()[index];
				}
			}
			throw new IllegalArgumentException("'" + s+ "'");
		}

		public int pipValue() {
			return this.ordinal() + 2;
		}

	}

	public static enum Suit {
		CLUB, DIAMOND, HEART, SPADE;
		
		public static final String [] SUIT_STRINGS = {"CLUBS","DIAMONDS","HEARTS","SPADES"};

		public String stringValue() {
			return SUIT_STRINGS[this.ordinal()];
		}

		public static Suit getSuit (String s) {
			for(int index=0;index<SUIT_STRINGS.length;index++){
				if(s.equals(SUIT_STRINGS[index])){
					return Suit.values()[index];
				}
			}
			throw new IllegalArgumentException("'" + s+ "'");
		}

		
	}
	/*
	 * 先做好52张牌（一副牌）
	 */
	private final static Card[] Deck = new Card[52];
	static {
		int i = 0;
		for (Suit s : Suit.values())
			for (Rank r : Rank.values())
				Deck[i++] = new Card(s, r);
	}

	/*
	 * 从这副牌中拿到某一张牌
	 */
	public static Card getCardFromDeck(Rank rank, Suit suit) {
		return Deck[suit.ordinal() * 13 + rank.ordinal()];
	}

	
	/*
	 * Bill Chen 提出的手牌计算公式具体如下： 
	 * 1. 首先为手牌中牌值较大的牌打分，牌值 A 的评分为 10，牌值 K 的评分 8，牌值 Q 的评分为 7，
	 * 	      牌值 J 的评分为 6，牌值为 10 到 2 的牌的评分为牌值的一半，例如牌值 8 的评分为 4。 
	 * 2. 如果出现一对，则将 1 中的分值乘以 2，一对牌的最小分值是 5。例如一对 K 的分值是 16，一对 7 的分值是 7，而一对 2 的分值是 5。
	 * 3. 如果两张牌是同花色，总分加 2。 
	 * 4. 如果两张牌的牌值不相等，根据牌值的差减小总分。牌值差小于 2
	 * 	      不需要减分，牌值差为 2 总分减 1，牌值差为 3 总分减 2，牌值差为4 总分减 4，牌值差大于等于 5 总分减 5。
	 * 5. 如果两张牌的牌值都小于 Q，且牌值差小于 3，总分加 1。 
	 * 6. 对总分向上取整。
	 * 
	 */
	public static int  billScoreOfHoldCards(Card[] holdCards){
		double score=0;
		Card max=(holdCards[0].compareTo(holdCards[1])==1)?holdCards[0]:holdCards[1];
		int diff=Math.abs(holdCards[0].getRank().ordinal()-holdCards[1].getRank().ordinal());
		if(Controller.isHandRadical){
			if(max.equals("A")){
				score=13;
			}else if(max.equals("K")){
				score=11;
			}else if(max.equals("Q")){
				score=9;
			}else if(max.equals("J")){
				score=8;
			}else{
				score=max.getRank().pipValue()/2.0;
			}
		}else {
			if(max.equals("A")){
				score=11;
			}else if(max.equals("K")){
				score=9;
			}else if(max.equals("Q")){
				score=7;
			}else if(max.equals("J")){
				score=6;
			}else{
				score=max.getRank().pipValue()/2.0;
			}
		}
		
		if(holdCards[0].equals(holdCards[1])){
			score*=2;
			if(Controller.isHandRadical){
				if(score<8){
					score=8;
				}
			}else{ 
				if(score<6){
					score=6;
				}
			}
		}else if(holdCards[0].isSameColor(holdCards[1])){
			score+=2;
		}
		
		switch(diff)
		{
		case 0:
			break;
		case 1:
			break;
		case 2:
			score -= 1;
			break;
		case 3:
			score -= 2;
			break;
		case 4:
			score -= 4;
			break;
		default:
			score -= 5;
			break;
		}
		if(diff < 3 && 0< diff && (holdCards[0].compareTo("Q")==-1)&&(holdCards[1].compareTo("Q")==-1)){
			score+=1;
		}
		if(score < 0) score = 0;
		return (int)(score+0.5);
	}
	
	public static double  flopWinRate(Card [] flopCards,Card [] holdCards){
		int myHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],holdCards[0],holdCards[1]);
		int lowCount=0;
		int highCount=0;
		int equalCount=0;
		int possibleHand;
		for(int i=0;i<52;i++){
			if(Deck[i].equals(flopCards[0],true)||Deck[i].equals(flopCards[1],true)||Deck[i].equals(flopCards[2],true)
					||Deck[i].equals(holdCards[0],true)||Deck[i].equals(holdCards[1],true)){
				continue;
			}
			for(int j=i+1;j<52;j++){
				if(Deck[j].equals(flopCards[0],true)||Deck[j].equals(flopCards[1],true)||Deck[j].equals(flopCards[2],true)
						||Deck[j].equals(holdCards[0],true)||Deck[j].equals(holdCards[1],true)){
					continue;
				}
				for(int k=j+1;k<52;k++){
					if(Deck[k].equals(flopCards[0],true)||Deck[k].equals(flopCards[1],true)||Deck[k].equals(flopCards[2],true)
							||Deck[k].equals(holdCards[0],true)||Deck[k].equals(holdCards[1],true)){
						continue;
					}
					for(int l=k+1;l<52;l++){
						if(Deck[l].equals(flopCards[0],true)||Deck[l].equals(flopCards[1],true)||Deck[l].equals(flopCards[2],true)
								||Deck[l].equals(holdCards[0],true)||Deck[l].equals(holdCards[1],true)){
							continue;
						}
						if((possibleHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],Deck[i],Deck[j],Deck[k],Deck[l]))>myHand){
							highCount++;
						}else if(possibleHand==myHand){
							equalCount++;
						}else{
							lowCount++;
						}
					}
//					if((possibleHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],Deck[i],Deck[j],Deck[k]))>myHand){
//						highCount++;
//					}else if(possibleHand==myHand){
//						equalCount++;
//					}else{
//						lowCount++;
//					}
				}
			}
		}
		return (double)(lowCount+equalCount)/(highCount+lowCount+equalCount);
	}
	public static double  turnWinRate(Card [] flopCards,Card [] holdCards,Card turnCard){
		int myHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],turnCard,holdCards[0],holdCards[1]);
		int lowCount=0;
		int highCount=0;
		int equalCount=0;
		int possibleHand;
		for(int i=0;i<52;i++){
			if(Deck[i].equals(flopCards[0],true)||Deck[i].equals(flopCards[1],true)||Deck[i].equals(flopCards[2],true)
					||Deck[i].equals(turnCard,true)||Deck[i].equals(holdCards[0],true)||Deck[i].equals(holdCards[1],true)){
				continue;
			}
			for(int j=i+1;j<52;j++){
				if(Deck[j].equals(flopCards[0],true)||Deck[j].equals(flopCards[1],true)||Deck[j].equals(flopCards[2],true)
						||Deck[j].equals(turnCard,true)||Deck[j].equals(holdCards[0],true)||Deck[j].equals(holdCards[1],true)){
						continue;
					}
				for(int k=j+1;k<52;k++){
					if(Deck[k].equals(flopCards[0],true)||Deck[k].equals(flopCards[1],true)||Deck[k].equals(flopCards[2],true)
							||Deck[k].equals(holdCards[0],true)||Deck[k].equals(holdCards[1],true)){
						continue;
					}
				
					
					if((possibleHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],turnCard,Deck[i],Deck[j],Deck[k]))>myHand){
						highCount++;
					}else if(possibleHand==myHand){
						equalCount++;
					}else{
						lowCount++;
					}
				}
			}
		}
		return (double)(lowCount+equalCount)/(highCount+lowCount+equalCount);
	}
	public static double  riverWinRate(Card [] flopCards,Card [] holdCards,Card turnCard,Card riverCard){
		int myHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],holdCards[0],holdCards[1],turnCard,riverCard);
		int lowCount=0;
		int highCount=0;
		int equalCount=0;
		int possibleHand;
		for(int i=0;i<52;i++){
			if(Deck[i].equals(flopCards[0],true)||Deck[i].equals(flopCards[1],true)||Deck[i].equals(flopCards[2],true)
					||Deck[i].equals(holdCards[0],true)||Deck[i].equals(holdCards[1],true)
					||Deck[i].equals(turnCard,true)||Deck[i].equals(riverCard,true)){
				continue;
			}
			for(int j=i+1;j<52;j++){
				if(Deck[j].equals(flopCards[0],true)||Deck[j].equals(flopCards[1],true)||Deck[j].equals(flopCards[2],true)
						||Deck[j].equals(holdCards[0],true)||Deck[j].equals(holdCards[1],true)
						||Deck[j].equals(turnCard,true)||Deck[j].equals(riverCard,true)){
					continue;
				}
				if((possibleHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],turnCard,
						riverCard,Deck[i],Deck[j]))>myHand){
					highCount++;
				}else if(possibleHand==myHand){
					equalCount++;
				}else{
					lowCount++;
				}
			}
		}
		return (double)(lowCount+equalCount)/(highCount+lowCount+equalCount);
	}
	
	
	
	
	public static double  turnWinRaiseRate(Card [] flopCards,Card [] holdCards,Card turnCard){
		double myTurn=turnWinRate(flopCards,holdCards,turnCard);
		int lowCount=0;
		int highCount=0;
		int equalCount=0;
		double possibleRiver;
		for(int i=0;i<52;i++){
			if(Deck[i].equals(flopCards[0],true)||Deck[i].equals(flopCards[1],true)||Deck[i].equals(flopCards[2],true)
					||Deck[i].equals(holdCards[0],true)||Deck[i].equals(holdCards[1],true)){
				continue;
			}
			if((possibleRiver=riverWinRate(flopCards,holdCards,turnCard,Deck[i]))>myTurn){
				highCount++;
				//System.out.println(Deck[i]);
			}else if(possibleRiver==myTurn){
				equalCount++;
			}else{
				lowCount++;
			}
		}
		return (double)(highCount+equalCount)/(highCount+lowCount+equalCount);
	}
	
	
	public static double  flopWinRaiseRate(Card [] flopCards,Card [] holdCards){
		double myFlop=flopWinRate(flopCards,holdCards);
		int lowCount=0;
		int highCount=0;
		int equalCount=0;
		double possibleTurn;
		for(int i=0;i<52;i++){
			if(Deck[i].equals(flopCards[0],true)||Deck[i].equals(flopCards[1],true)||Deck[i].equals(flopCards[2],true)
					||Deck[i].equals(holdCards[0],true)||Deck[i].equals(holdCards[1],true)){
				continue;
			}
			for(int j=i+1;j<52;j++){
				if(Deck[j].equals(flopCards[0],true)||Deck[j].equals(flopCards[1],true)||Deck[j].equals(flopCards[2],true)
						||Deck[j].equals(holdCards[0],true)||Deck[j].equals(holdCards[1],true)){
					continue;
				}
				if((possibleTurn=riverWinRate(flopCards,holdCards,Deck[i],Deck[j]))>myFlop){
					highCount++;
				}else if(possibleTurn==myFlop){
					equalCount++;
				}else{
					lowCount++;
				}
			}
		}
		return (double)(highCount+equalCount)/(highCount+lowCount+equalCount);
	}
//	public static double  WinRateProAtTurn(Card [] flopCards,Card [] holdCards){
//		int myHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],holdCards[0],holdCards[1]);
//		int lowCount=0;
//		int highCount=0;
//		int equalCount=0;
//		int possibleHand;
//		for(int i=0;i<52;i++){
//			if(Deck[i].equals(flopCards[0],true)||Deck[i].equals(flopCards[1],true)||Deck[i].equals(flopCards[2],true)
//					||Deck[i].equals(holdCards[0],true)||Deck[i].equals(holdCards[1],true)){
//				continue;
//			}
//		
//			if((possibleHand=Hand.HandEvaluate(flopCards[0],flopCards[1],flopCards[2],Deck[i],holdCards[0],holdCards[1]))>myHand){
//				highCount++;
//			}else if(possibleHand==myHand){
//				equalCount++;
//			}else{
//				lowCount++;
//			}
//			
//		}
//		return (double)(highCount)/(highCount+lowCount+equalCount);
//	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "color:"+this.suit.stringValue()+" "+"point:"+this.rank.stringValue();
	}
	/*
	 * 不区分花色,比较本牌与其它牌是否相同大小
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (obj instanceof Card) {
        	Card anotherCard= (Card)obj;
            if (this.rank.stringValue().equals(anotherCard.rank.stringValue())) {
                return true;
            }
        }
        if (obj instanceof String) {
        	String anotherPoint= (String)obj;
            if (this.rank.stringValue().equals(anotherPoint)) {
                return true;
            }
        }
        return false;
	}
	
	public boolean equals(Object obj,boolean thinkColor) {
		if(thinkColor){//区分花色
			if(this.equals(obj)&&this.isSameColor((Card)obj)){
				return true;
			}else return false;
	    }else return this.equals(obj);
	}

	@Override
	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 不区分花色,比较本牌与其它牌的大小
	 */
	public int compareTo(Card o) {
		 return compare(this.rank.ordinal(), o.rank.ordinal());
	}
	public int compareTo(String stringValue) {
		 return compare(this.rank.ordinal(), Rank.getRank(stringValue).ordinal());
	}
	/*
	 * 两张牌花色是否相同
	 */
	public boolean isSameColor(Card card){
		if(this.suit.stringValue().equals(card.suit.stringValue())){
			return true;
		}else return false;
	}

	private static int  compare(int valueOfpoint1, int valueOfpoint2) {
		
		return (valueOfpoint1 < valueOfpoint2) ? -1 : ((valueOfpoint1 == valueOfpoint2) ? 0 : 1);
		
	}

	
	/*
	 * 测试
	 */
	public static void  main(String []args) {
//		Card [] cards=new Card[6];
//		cards[0]=new Card("CLUBS A");
//		cards[1]=new Card("DIAMONDS K");
//		cards[2]=new Card("HEARTS 5");
//		cards[3]=new Card("SPADES 10");
//		cards[4]=new Card("SPADES J");
//		cards[5]=new Card("DIAMONDS 2");
//		
//		System.out.println(Arrays.toString(cards));
//		Arrays.sort(cards);
//		System.out.println(Arrays.toString(cards));
		long currentTime=System.currentTimeMillis();
		Card[] flopCards=new Card[3];
		flopCards[0]=new Card("SPADES 8");
		flopCards[1]=new Card("HEARTS 2");
		flopCards[2]=new Card("SPADES 3");
//		flopCards[3]=new Card("CLUBS 10");
		Card[] holdCards=new Card[2];
		holdCards[0]=new Card("HEARTS A");
		holdCards[1]=new Card("DIAMONDS 7");
		System.out.println(billScoreOfHoldCards(holdCards));
		System.out.println(flopWinRate(flopCards, holdCards));
//		getAll4(flopCards,holdCards);
		System.out.println(turnWinRate(flopCards,holdCards,new Card("DIAMONDS 6")));
//		System.out.println(winRate3(flopCards,holdCards));
//		System.out.println(WinRateProAtTurn(flopCards,holdCards));
//		System.out.println("raise:"+flopWinRaiseRate(flopCards,holdCards));
//		System.out.println(turnWinRate(flopCards, holdCards,new Card("CLUBS 7")));
		System.out.println(riverWinRate(flopCards, holdCards,new Card("CLUBS 6"),new Card("DIAMONDS 6")));
		System.out.println("takes : "+(System.currentTimeMillis()-currentTime)+"ms");
//		Integer s=null;
//		System.out.println(s/5);
		
	}
	

}
