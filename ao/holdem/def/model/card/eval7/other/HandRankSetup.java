package ao.holdem.def.model.card.eval7.other;

import ao.holdem.Main;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval_567.EvalSlow;
import ao.holdem.def.model.cards.Hand;

import java.io.*;
import java.util.Arrays;

public class HandRankSetup {

	static String HandRanks[] = {"BAD!!","High Card","Pair","Two Pair","Three of a Kind","Straight","Flush","Full House","Four of a Kind","Straight Flush"};

	static long IDs[] = new long[612978];
	static int HR[] = new int[32487834];

	static int numIDs = 1;
	static int numcards = 0;
	static int maxHR = 0;
	static long maxID = 0;

	// minimize heap allocations by making these class members rather than locals:
	static int suitcount[] = new int[4 + 1];
	static int rankcount[] = new int[13 + 1];
	static int workcards[] = new int[8];  // intentially keeping one as a 0 end

	public static void main(String[] args) {

		int count = 0;
		int card;

		long ID;
		int IDslot;

		long timer = System.nanoTime();   // remember when I started

		int handTypeSum[] = new int[10];

		Arrays.fill(handTypeSum, 0);  // init
		Arrays.fill(IDs, 0);
		Arrays.fill(HR, 0);

	  // step through the ID array - always shifting the current ID and adding 52 cards to the end of the array.
		// when I am at 7 cards put the Hand Rank in!!
		// stepping through the ID array is perfect!!

	    int IDnum;

		System.out.println("Getting Card IDs!");
		// as this loops through and find new combinations it adds them to the end
		// I need this list to be stable when I set the handranks (next set)  (I do the insertion sort on new IDs these)
		// so I had to get the IDs first and then set the handranks
		for (IDnum = 0; (IDs[IDnum] != 0) || (IDnum == 0); IDnum++) {  // start at 1 so I have a zero catching entry (just in case)
			for (card = 1; card < 53; card++) {	 // the ids above contain cards upto the current card.  Now add a new card
				ID = MakeID(IDs[IDnum], card);   // get the new ID for it
				if (numcards < 7) SaveID(ID);   // and save it in the list if I am not on the 7th card
			}
//			if (IDnum % 1000 == 0)
//				System.out.println(IDnum);	  // just to show the progress -- this will count up to  612976
		}
//		System.out.println(IDnum);

		System.out.println("\nSetting HandRanks!");
		// this is as above, but will not be adding anything to the ID list, so it is stable
		for (IDnum = 0; (IDs[IDnum] != 0) || (IDnum == 0); IDnum++) {  // start at 1 so I have a zero catching entry (just in case)
			for (card = 1; card < 53; card++) {
				ID = MakeID(IDs[IDnum], card);
				if (numcards < 7)  IDslot = SaveID(ID) * 53 + 53;  // when in the index mode (< 7 cards) get the id to save
				else IDslot = DoEval(ID);   // if I am at the 7th card, get the HandRank to save
				maxHR = IDnum * 53 + card + 53;	// find where to put it
				HR[maxHR] = IDslot;				// and save the pointer to the next card or the handrank
			}

			if (numcards == 6 || numcards == 7) {
				// an extra, If you want to know what the handrank when there is 5 or 6 cards
				// you can just do HR[u3] or HR[u4] from below code for Handrank of the 5 or 6 card hand
				HR[IDnum * 53 + 53] = DoEval(IDs[IDnum]);  // this puts the above handrank into the array
			}
//			if (IDnum % 1000 == 0)
//				System.out.println(IDnum);	  // just to show the progress -- this will count up to  612976
		}
//		System.out.println(IDnum);

		System.out.printf("\nNumber IDs = %d\nmaxHR = %d\n", numIDs, maxHR);  // for warm fuzzies

		timer = System.nanoTime() - timer;  // end the timer

		System.out.printf("Training seconds = %.2f\n", timer/1e9);  // display training time

		timer = System.nanoTime();   // now get current time for testing

		// another algorithm right off the thread

		int c0, c1, c2, c3, c4, c5, c6;
		int u0, u1, u2, u3, u4, u5;

        int frequency[] = new int[ Hand.HandRank.values().length ];
        int exactFrequency[] = new int[ 7462 ];

        for (c0 = 1; c0 < 53; c0++) {
			u0 = HR[53+c0];
			for (c1 = c0+1; c1 < 53; c1++) {
				u1 = HR[u0+c1];
				for (c2 = c1+1; c2 < 53; c2++) {
					u2 = HR[u1+c2];
					for (c3 = c2+1; c3 < 53; c3++) {
						u3 = HR[u2+c3];
						for (c4 = c3+1; c4 < 53; c4++) {
							u4 = HR[u3+c4];
							for (c5 = c4+1; c5 < 53; c5++) {
								u5 = HR[u4+c5];
								for (c6 = c5+1; c6 < 53; c6++) {
                                    short value = (short) HR[u5+c6];
                                    frequency[ Hand.HandRank.fromValue(value).ordinal() ]++;
                                    exactFrequency[ value ]++;

//                                    handTypeSum[(HR[u5+c6] >> 24)+1]++;
									count++;
								}
							}
						}
					}
				}
			}
		}

		timer = System.nanoTime() - timer;

        
        System.out.println(Arrays.toString(frequency));
        System.out.println(Main.join(exactFrequency, "\t"));


        for (int i = 0; i <= 9; i++)  // display the results
			System.out.printf("\n%16s = %d", HandRanks[i], handTypeSum[i]);

		System.out.printf("\nTotal Hands = %d\n", count);

		System.out.printf("\nValidation seconds = %.4f\n", timer/1e9);

		System.out.print("Writing HandRanks.dat...");
        File outFile = new File ("HandRanks.dat");

	    // write the HR array to the file.
	    try {
	      FileOutputStream file_output = new FileOutputStream (outFile);
	      DataOutputStream data_out = new DataOutputStream (new BufferedOutputStream(file_output));

	      for (int r : HR)
	          data_out.writeInt(r);
	      data_out.close();
	    }
	    catch (IOException e) {
	       System.out.println ("IO exception = " + e );
	    }
	    System.out.println("\nDone.");
	}

	static int DoEval(long IDin) {

    	if (IDin == 0)
    		return 0;

        Card checkup[]   = new Card[7];
        int lastDontCare = 0;

        long hand = 0;
    	int cardCount = 0;
    	int sigSuit = -1;
    	for (long id = IDin; (id & 0xFF) != 0; id >>>= 8, cardCount++) {
    		int suit = ((int)id & 0xF) - 1; // -1, 0..3
            int rank = (((int)id >>> 4) & 0xF) - 1; // 0..12

            if (suit >= 0) {
    			sigSuit = suit;
    			hand |= (1L << (suit*13 + (((int)id >>> 4) & 0xF) - 1));
    		}

            if (suit == -1)
            {
                do
                {
                    checkup[cardCount] =
                            Card.values()[ lastDontCare*13 + rank ];

                    lastDontCare = (lastDontCare + 1) % 4;
                }
                while (contains(checkup, cardCount - 1, checkup[cardCount]));
            }
            else
            {
                checkup[cardCount] =
                        Card.values()[ suit*13 + rank ];
            }
        }
    	int assignSuit = sigSuit;
    	for (long id = IDin; (id & 0xFF) != 0; id >>= 8) {
    		int suit = ((int)id & 0xF) - 1;
    		while (suit < 0) {
    			if ((suit = ++assignSuit) > 3)
    				if (sigSuit == 0)
    					suit = assignSuit = 1;
    				else
    					suit = assignSuit = 0;
    			long mask = (1L << (suit*13 + (((int)id >>> 4) & 0xF) - 1));
    			if ((hand & mask) != 0)
    				suit = -1;
    		}
    		hand |= (1L << (suit*13 + (((int)id >>> 4) & 0xF) - 1));
    	}
    	switch (cardCount) {
            case 5:	return EvalSlow.valueOf(checkup[0],checkup[1],checkup[2],checkup[3],checkup[4]);//HandEval.hand5Eval(hand);
            case 6: return EvalSlow.valueOf(checkup[0],checkup[1],checkup[2],checkup[3],checkup[4],checkup[5]);//HandEval.hand6Eval(hand);
            case 7: return EvalSlow.valueOf(checkup[0],checkup[1],checkup[2],checkup[3],checkup[4],checkup[5],checkup[6]);//HandEval.hand7Eval(hand);
            default: System.out.println("Error in DoEval()");
                return 0;
    	}
	}

    private static boolean contains(Card cards[], int upToIndex, Card card)
    {
        for (int i = 0; i <= upToIndex; i++)
        {
            if (cards[i] == card)
            {
                return true;
            }
        }
        return false;
    }



    static long MakeID(long IDin, int newcard)  // adding a new card to this ID
	{
		long ID = 0;
		int cardnum;
		boolean getout = false;

		Arrays.fill(workcards, 0);
		Arrays.fill(rankcount, 0);
		Arrays.fill(suitcount, 0);

		for (cardnum = 0; cardnum < 6; cardnum++) {  // can't have more than 6 cards!
			workcards[cardnum + 1] =  ((int)(IDin >>> (8 * cardnum)) & 0xff);  // leave the 0 hole for new card
		}

		// my cards are 2c = 1, 2d = 2  ... As = 52
		newcard--;  // make 0 based!

		workcards[0] = (((newcard >> 2) + 1) << 4) + (newcard & 3) + 1;  // add next card formats card to rrrr00ss

		for (numcards = 0; workcards[numcards] != 0; numcards++) {
			suitcount[workcards[numcards] & 0xf]++;           // need to see if suit is significant
			rankcount[(workcards[numcards] >> 4) & 0xf]++;	  // and rank to be sure we don't have 4!
			if (numcards != 0) {
				if (workcards[0] == workcards[numcards]) {	  // can't have the same card twice
					getout = true;								  // if so need to get out after counting numcards
				}
			}
		}

		if (getout) {
			return 0;     // duplicated another card (ignore this one)
		}


		int needsuited = numcards - 2;	   // for suit to be significant - need to have n-2 of same suit

		if (numcards > 4) {
			for (int rank = 1; rank < 14; rank++) {
				if (rankcount[rank] > 4) {  // if I have more than 4 of a rank then I shouldn't do this one!!
					return 0;   // can't have more than 4 of a rank so return an ID that can't be!
				}
			}
		}

		// However in the ID process I prefered that
		// 2s = 0x21, 3s = 0x31,.... Kc = 0xD4, Ac = 0xE4
		// This allows me to sort in Rank then Suit order

		// if we don't have at least 2 cards of the same suit for 4, we make this card suit 0.

		if (needsuited > 1) {
			for (cardnum = 0; cardnum < numcards; cardnum++) {  // for each card
				if (suitcount[workcards[cardnum] & 0xf] < needsuited) {	// check suitcount to the number I need to have suits significant
					workcards[cardnum] &= 0xf0;   // if not enough - 0 out the suit - now this suit would be a 0 vs 1-4
				}
			}
		}

		// Sort Using XOR.  Network for N=7, using Bose-Nelson Algorithm: Thanks to the thread!

		SWAP(0, 4);
		SWAP(1, 5);
		SWAP(2, 6);
		SWAP(0, 2);
		SWAP(1, 3);
		SWAP(4, 6);
		SWAP(2, 4);
		SWAP(3, 5);
		SWAP(0, 1);
		SWAP(2, 3);
		SWAP(4, 5);
		SWAP(1, 4);
		SWAP(3, 6);
		SWAP(1, 2);
		SWAP(3, 4);
		SWAP(5, 6);

		// long winded way to put the pieces into a long
		// cards in bytes --66554433221100
		// the resulting ID is a 64 bit value with each card represented by 8 bits.
		ID =  (long) workcards[0] +
			 ((long) workcards[1] << 8) +
			 ((long) workcards[2] << 16) +
			 ((long) workcards[3] << 24) +
			 ((long) workcards[4] << 32) +
			 ((long) workcards[5] << 40) +
			 ((long) workcards[6] << 48);

		return ID;
	}

	static void SWAP(int I, int J) {
		if (workcards[I] < workcards[J]) {
			workcards[I]^=workcards[J]; workcards[J]^=workcards[I]; workcards[I]^=workcards[J];
			}
		}

	static int SaveID(long ID)
	{
		if (ID == 0) return 0;   // don't use up a record for a 0!

		if (ID >= maxID) {           // take care of the most likely first goes on the end...
			if (ID > maxID) {        // greater than create new else it was the last one!
				IDs[numIDs++] = ID;  // add the new ID
				maxID = ID;
			}
			return numIDs - 1;
		}

		// find the slot I will find it (by a pseudo bsearch algorithm)
		int low = 0;
		int high = numIDs - 1;
		long testval;
		int holdtest;

		while (high - low > 1) {
			holdtest = (high + low + 1) / 2;
			testval = IDs[holdtest] - ID;
			if (testval > 0) high = holdtest;
			else if (testval < 0) low = holdtest;
			else return holdtest;   // got it!!
		}
		// I guess it couldn't be found so must be added to the current location (high)
		// make space...  // don't expect this much!
		System.arraycopy(IDs, high, IDs, high+1, numIDs - high);

		IDs[high] = ID;   // do the insert into the hole created
		numIDs++;
		return high;
	}
}
/*
High Card = 23294460
Pair = 58627800
Two Pair = 31433400
Three of a Kind = 6461620
Straight = 6180020
Flush = 4047644
Full House = 3473184
Four of a Kind = 224848
Straight Flush = 41584
Total Hands = 133784560
*/