package com.bot;

import java.io.IOException;
import java.util.*;
import com.ai.cleverbot.PartialStageFastEval;
import com.bot.Bot;
import com.game.Card;
import com.game.Hand;
import com.game.State;
import com.util.Log;

public class CleverBot implements Bot{
    private State state;
    private Card c1,c2;
    private Hand comm;
    /*
    public static void main(String args[]) throws IOException
    {
    	CleverBot b=new CleverBot();
    	State state=new State();
    	com.game.Card c1=new com.game.Card("SPADES", "9"),c2=new com.game.Card("CLUBS","4");
    	System.out.print("a");
    	Card b1=new Card(0,1);
    	Card b2=new Card(c2.getRank(),c2.getSuit());
    	b.getBestAction(state, 0);
    }*/
    public int getBestAction (State state, long timeMS) {
    	try{
    		this.state=state;
    		c1=state.handcard[0];
    		c2=state.handcard[1];
    		comm=new Hand();
    		if(state.currentState!=State.baseState)
    		{
	    		for(int i=0;i<state.currentState-38;i++)
	    		{
	    			Card c=new Card(state.hostcard[i].getRank(),state.hostcard[i].getSuit());
	    			comm.addCard(c);
	    		}
    		}
    		if(state.currentState==State.baseState)
    			return preFlopAction();
    		else
    			return postFlopAction();
    	}catch(Exception e)
    	{
    		try {
				Log.getIns(state.pid).log("fold error ");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		return State.fold;
    	}
    }
    private int preFlopAction() throws IOException {
        
		double toCall = state.getBet();
		// play all pocket-pairs      
		if (c1.getRank() == c2.getRank()) {
			if ((c1.getRank() >= Card.TEN || c1.getRank() == Card.TWO) 
					&& state.raisenum<2)
			{
				Log.getIns(state.pid).log(">=10 || 2  && size<2");

				return State.raise;
			}
			if (state.raisenum < 3) {
				Log.getIns(state.pid).log("size>2");
				return State.call;
			}
		}
		// play all cards where both cards are bigger than Tens
		// and raise if they are suited
		if (c1.getRank() >= Card.TEN && c2.getRank() >= Card.TEN) {
			if (c1.getSuit() == c2.getSuit() &&state.raisenum < 2) {
				Log.getIns(state.pid).log("same point and > 10 >10 size <2");
				return State.raise;
			}
			if (state.raisenum < 3) {
				Log.getIns(state.pid).log("same point and > 10 >10 size >3");
				return State.call;
			}
		}
		Log.getIns(state.pid).log("j4");
		// play all suited connectors
		if ((c1.getSuit() == c2.getSuit())) {
			if (Math.abs(c1.getRank() - c2.getRank()) == 1) {
				Log.getIns(state.pid).log("same suit a ");
					return State.call;
			}
			// raise A2 suited
			if ((c1.getRank() == Card.ACE && c2.getRank() == Card.TWO) || (c2.getRank() == Card.ACE && c1.getRank() == Card.TWO)) {
				if (state.raisenum == 0) {
					Log.getIns(state.pid).log("rank a 2 ");
					return State.raise;
				}
				if (state.raisenum>=1) {
					Log.getIns(state.pid).log("rank a 2 ");
					return State.call;
				}
			}
			// call any suited ace
			if ((c1.getRank() == Card.ACE || c2.getRank() == Card.ACE)) {
				Log.getIns(state.pid).log("rank a 2 ");
				return State.call;
			}
		}
		// toCall is current bet
		// play anything 5% of the time
		if (toCall<= state.bigblindbet) {
			if (Math.random() < 0.05) {
				Log.getIns(state.pid).log("call ");
				return State.call;
			}
		}

		// check or fold
		if (Math.random() < 0.2)
		{
			Log.getIns(state.pid).log("fold a 2 ");
			return State.fold;
		}
			
		return State.call;
	}
    private int postFlopAction() throws IOException {
		// number of players left in the hand (including us)
    	
		int np = state.getNonFolded();

		// amount to call
		double toCall = state.getBet();

		// bet/pot odds
		double PO = toCall / (double) (state.totalpot + toCall);

		EnumerateResult result = enumerateHands(c1, c2, comm);
		// compute our current hand rank
		double HRN = Math.pow(result.HR, np - 1);

		// compute a fast approximation of our hand potential
		double PPOT = 0.0;
		if (state.currentState < State.riverState) {
			PPOT = result.PPot;
		}

		Log.getIns(state.pid).log(comm+ " | HRn = " + Math.round(HRN * 10) / 10.0 + " PPot = " + Math.round(PPOT * 10) / 10.0 + " PotOdds = " + Math.round(PO * 10)
				/ 10.0);

		if (HRN == 1.0) {
			// dah nuts -- raise the roof!
			return betOrRaisePot();
		}

		// consider checking or betting:
		if (toCall == 0) {
			if (Math.random() < HRN * HRN) {
				return betOrRaisePot(); // bet a hand in proportion to it's strength

			}
			if (Math.random() < PPOT) {
				return betOrRaisePot(); // semi-bluff
			}
			// just check
			return State.check;
		} else {
			// consider folding, calling or raising:        
			if (Math.random() < Math.pow(HRN, 1 + state.raisenum)) {
				// raise in proportion to the strength of our hand
				return betOrRaisePot();
			}

			if (HRN * HRN * state.totalpot > toCall || PPOT > PO) {
				// if we have draw odds or a strong enough hand to call
				return State.call;
			}
			return State.fold;
		}
	}
    /**
	 * if fixed-limit: just bets or raises<br>
	 * in no-limit:<br>
	 * - bets 2/3 pot<br>
	 * - or raises a 2/3*(pot + toCall) if someone bet before<br>
	 * thus always giving 1:2.5 pot odds to the villain.<br>
	 * <br>
	 * if stack is lower than to call, just bets the stack
	 * if stack remaining after the raise is lower than the bet/raise goes
	 * all-in
	 * @return
	 */
	private int betOrRaisePot() {
		double toCall = state.getBet();
		if (toCall> 0) {
			if (state.getJetton() > toCall) {
				int  wantedRaiseAmount = (int)((state.totalpot + toCall) / 3.0 * 2);
				int  maxPossibleRaise = (int)(state.getJetton()- toCall);
				if (maxPossibleRaise < wantedRaiseAmount) {
					wantedRaiseAmount = maxPossibleRaise;
				}
				State.raisebet=wantedRaiseAmount;
				return State.raise;
			} else {
				return State.call;
			}
		} else {
			int betAmount = (int)(state.totalpot/ 3 * 2);
			//TODO check: is this even correct?
			if (state.getJetton()- betAmount < betAmount) {
				betAmount = state.getJetton();
			}
			State.raisebet=betAmount;
			return State.raise;
		}
	}
    /**
	 * Calculate the raw (unweighted) PPot1 and NPot1 of a hand. (Papp 1998, 5.3)
	 * Does a one-card look ahead.
	 * 
	 * @param c1 the first hole card
	 * @param c2 the second hole card
	 * @param bd the board cards
	 * @return 
     * @throws IOException 
	 */
	public EnumerateResult enumerateHands(Card c1, Card c2, Hand bd) throws IOException {
		double[][] HP = new double[3][3];
		double[] HPTotal = new double[3];
		int ourrank7, opprank;
		int index;
		int[] boardIndexes = new int[bd.size()];
		int[] boardIndexes2 = new int[bd.size() + 1];

		int c1Index;
		int c2Index;

		ArrayList<Integer> deck = new ArrayList<Integer>();
		for (int i = 0; i < 52; i++) {
			deck.add(Integer.valueOf(i));
		}
		for (int i = 0; i < bd.size(); i++) {
			Card card = bd.getCard(i + 1);
			boardIndexes[i] = PartialStageFastEval.encode(card.getRank(), card.getSuit());
			boardIndexes2[i] = PartialStageFastEval.encode(card.getRank(), card.getSuit());
			deck.remove(Integer.valueOf(boardIndexes[i]));
		}
		c1Index = PartialStageFastEval.encode(c1.getRank(), c1.getSuit());
		c2Index = PartialStageFastEval.encode(c2.getRank(), c2.getSuit());
		deck.remove(Integer.valueOf(c1Index));
		deck.remove(Integer.valueOf(c2Index));

		int ourrank5 = eval(boardIndexes, c1Index, c2Index);

		// pick first opponent card
		for (int i = 0; i < deck.size(); i++) {
			int o1Card = deck.get(i);
			// pick second opponent card
			for (int j = i + 1; j < deck.size(); j++) {
				int o2Card = deck.get(j);
				opprank = eval(boardIndexes, o1Card, o2Card);
				if (ourrank5 > opprank)
					index = AHEAD;
				else if (ourrank5 == opprank)
					index = TIED;
				else
					index = BEHIND;
				HPTotal[index]++;
				if (bd.size() < 5) {

					// tally all possiblities for next board card
					for (int k = 0; k < deck.size(); k++) {
						if (i == k || j == k)
							continue;
						boardIndexes2[boardIndexes2.length - 1] = deck.get(k);
						ourrank7 = eval(boardIndexes2, c1Index, c2Index);
						opprank = eval(boardIndexes2, o1Card, o2Card);
						if (ourrank7 > opprank)
							HP[index][AHEAD]++;
						else if (ourrank7 == opprank)
							HP[index][TIED]++;
						else
							HP[index][BEHIND]++;
					}
				}
			}
		} /* end of possible opponent hands */

		double den1 = (45 * (HPTotal[BEHIND] + (HPTotal[TIED] / 2.0)));
		double den2 = (45 * (HPTotal[AHEAD] + (HPTotal[TIED] / 2.0)));
		EnumerateResult result = new EnumerateResult();
		if (den1 > 0) {
			result.PPot = (HP[BEHIND][AHEAD] + (HP[BEHIND][TIED] / 2.0) + (HP[TIED][AHEAD] / 2.0)) / (double) den1;
		}
		if (den2 > 0) {
			result.NPot = (HP[AHEAD][BEHIND] + (HP[AHEAD][TIED] / 2.0) + (HP[TIED][BEHIND] / 2.0)) / (double) den2;
		}
		result.HR = (HPTotal[AHEAD] + (HPTotal[TIED] / 2)) / (HPTotal[AHEAD] + HPTotal[TIED] + HPTotal[BEHIND]);

		return result;
	}

	private int eval(int[] boardIndexes, int c1Index, int c2Index) {
		if (boardIndexes.length == 5) {
			return PartialStageFastEval.eval7(boardIndexes[0], boardIndexes[1], boardIndexes[2], boardIndexes[3], boardIndexes[4], c1Index, c2Index);
		} else if (boardIndexes.length == 4) {
			return PartialStageFastEval.eval6(boardIndexes[0], boardIndexes[1], boardIndexes[2], boardIndexes[3], c1Index, c2Index);
		} else {
			return PartialStageFastEval.eval5(boardIndexes[0], boardIndexes[1], boardIndexes[2], c1Index, c2Index);
		}
	}
	private final static int AHEAD = 0;
	private final static int TIED = 1;
	private final static int BEHIND = 2;
	class EnumerateResult {
		double HR;
		double PPot;
		double NPot;
	}
}
