package com.ai.simplebot;

import java.io.IOException;
import java.util.concurrent.atomic.*;

import com.game.State;
import com.util.Log;

public class MCT implements Runnable {
    private final int [] hand;
    private final int [] host;
    private final int [] deck;
    private final int hostlenght;
    private final int alivenum;
    private final long stop;
    private final AtomicInteger won;
    private final AtomicInteger total;
    private final int [] curDeck;
    public MCT (int [] hand, int numOther, int [] partialCommunity,
                             long stop, AtomicInteger won, AtomicInteger total) throws IOException {
        this.hand = hand.clone();
        this.host = new int[7];
        this.hostlenght = partialCommunity.length;
        for (int i = 0; i < hostlenght; i ++)
            this.host[i] = partialCommunity[i];
        this.deck = new int[52];
        PokerLib.init_deck(this.deck);
        this.alivenum = numOther;
        this.stop = stop;
        this.won = won;
        this.total = total;
        this.curDeck = new int[52];
    }
    public boolean tryReplace () throws IOException {

        if (Math.random() < 0.04)
            return true;

        // Instantiate curDeck
        for (int i = 0; i < 52; i ++)
            curDeck[i] = deck[i];
        PokerLib.shuffle_deck(curDeck);
        //System.out.printf("%d %d\n", hand[0], hand[1]);
        for (int i = 0; i < 52; i ++) {
            //System.out.printf("%d %s\n", curDeck[i], PokerLib.cardToString(curDeck[i]));
            if (curDeck[i] == hand[0] || curDeck[i] == hand[1]) {
                curDeck[i] = -1;
                continue;
            }
            for (int j = 0; j < hostlenght; j ++) {
                if (curDeck[i] == host[j]) {
                    curDeck[i] = -1;
                    break;
                }
            }
        }

        int curPos = 0, bestVal = 9999;

        // Generate missing community cards
        for (int i = hostlenght; i < 5; i ++) {
            while (curDeck[curPos] == -1)
                curPos ++;
            host[i] = curDeck[curPos];
            curDeck[curPos] = -1;
        }

        // Evaluate opponent hands
        for (int i = 0; i < alivenum; i ++) {
            for (int j = 5; j < 7; j ++) {
                while (curDeck[curPos] == -1)
                    curPos ++;
                host[j] = curDeck[curPos];
                curDeck[curPos] = -1;
            }
            //System.out.print(i);
            //for (int j = 0; j < 7; j ++)
                //System.out.print(" " + PokerLib.cardToString(community[j]));
            //System.out.println();
            int val = PokerLib.eval_7hand(host);
            //System.out.println(val);
            if (val < bestVal) {
                bestVal = val;
                //bestPlayer = i;
            }
        }

        // Evaluate my hand
        host[5] = hand[0];
        host[6] = hand[1];
        //System.out.print("me");
        //for (int j = 0; j < 7; j ++)
            //System.out.print(" " + PokerLib.cardToString(community[j]));
        //System.out.println();
        int myVal = PokerLib.eval_7hand(host);
        //Log.getIns("7777").log("clever bot:"+State.ranknum[0].intValue());
        State.ranknum[PokerLib.hand_rank(myVal)].getAndIncrement();
        
       // System.out.println(myVal);

        // Return true if win
        if (myVal < bestVal) {
            return true;
        } else if (myVal == bestVal) {
            return Math.random() * (1 + alivenum) < 1;
        }
        return false;
    }
    public void run () {
        for (int i = 0;; i ++) {
            if (i % 10 == 0) {
                if (System.currentTimeMillis() >= stop)
                    return;
            }
            total.getAndIncrement();
            try {
				if (tryReplace())
				    won.getAndIncrement();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
