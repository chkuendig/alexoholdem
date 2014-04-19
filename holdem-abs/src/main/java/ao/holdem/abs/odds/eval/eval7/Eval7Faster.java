package ao.holdem.abs.odds.eval.eval7;

import ao.Infrastructure;
import ao.holdem.engine.eval.EvalBy5;
import ao.holdem.model.card.Card;
import ao.holdem.engine.eval.HandRank;
import ao.holdem.engine.eval.Eval5;
import ao.util.io.Dirs;
import ao.util.persist.PersistentInts;
import ao.util.persist.PersistentLongs;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * http://forumserver.twoplustwo.com/showflat.php?Cat=0&Number=8513906&page=0&fpart=all&vc=1
 */
public class Eval7Faster
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            LoggerFactory.getLogger(Eval7Faster.class);


    //--------------------------------------------------------------------
    private static final File DIR    = Dirs.get(Infrastructure.path(
            "lookup/eval/eval7_faster"));
    private static final File F_RANK = new File(DIR, "ranks.cache");
    private static final File F_KEY  = new File(DIR, "keys.cache");


    //--------------------------------------------------------------------
	private final static int NUM_SUITS = 4;
	private final static int NUM_RANKS = 13;

    private static boolean retrievedOrComputed = false;

    private static int[]   handRanks   = null; // hand rank compact
	private static boolean verbose     = true; // toggles verbose mode

	private static int[]  hand; 			   // cards in a hand
	private static long[] keys         = null; // key compact table
	private static int    numKeys      = 1;	   // # of defined keys
	private static long   maxKey       = 0;	   // current max key value
	private static int    numCards     = 0;	   // # of cards in a hand
	private static int    cardIndex    = 0;	   // cards in a hands

    private static long   startTimer;
	private static long   stopTimer;


    //--------------------------------------------------------------------
    static void retrieveOrComputeIfRequired()
    {
        if (retrievedOrComputed) {
            return;
        }

        LOG.info("retrieveOrCompute");

        handRanks = PersistentInts.retrieve(F_RANK);
        keys      = PersistentLongs.retrieve(F_KEY);

        if (handRanks == null || keys == null)
        {
            LOG.info("computing");

            handRanks = new int[32487834];
            keys      = new long[612978];

            generateTables();

            PersistentInts .persist(handRanks, F_RANK);
            PersistentLongs.persist(keys     , F_KEY);

            LOG.info("done (computed)");
        }
        else
        {
            LOG.info("retrieved");
        }

        retrievedOrComputed = true;
    }


    //--------------------------------------------------------------------
    // Inserts a key into the key array and returns the insertion index.
	private static int insertKey(long key) {
		// check to see if key is valid
		if (key == 0) {
			return 0;
		}

		// short circuit insertion for most common cases
		if (key >= maxKey) {
			if (key > maxKey) {
                // appends the new key to the key array
				keys[numKeys++] = key;
				maxKey = key;
			}
			return numKeys - 1;
		}

		// use binary search to find insertion point for new key
		int low = -1;
		int high = numKeys;
		int pivot;
		long difference;

		while (high - low > 1) {
			pivot = (low + high) >>> 1;
			difference = keys[pivot] - key;
			if (difference > 0) {
				high = pivot;
			}
			else if (difference < 0) {
				low = pivot;
			}
			else {
				return pivot;	// key already exists
			}
		}

		// key does not exist so must be inserted
		System.arraycopy(keys, high, keys, high + 1, numKeys - high);
		keys[high] = key;

		numKeys++;
		return high;
	}


    //--------------------------------------------------------------------
	// Returns a key for the hand created by adding a new card to the
    //  hand represented by the given key.
    // Returns 0 if new card already appears in hand.
	private static long makeKey(long baseKey, int newCard) {

        // number of times a suit appears in a hand
		int[] suitCount = new int[NUM_SUITS + 1];
        // number of times a rank appears in a hand
		int[] rankCount = new int[NUM_RANKS + 1];
		hand = new int[8];

		// extract the hand represented by the key value
		for (cardIndex = 0; cardIndex < 6; cardIndex++) {

			// hand[0] is used to hold the new card
			hand[cardIndex + 1] =
                    (int)((baseKey >>> (8 * cardIndex)) & 0xFF);
		}

		hand[0] = formatCard8bit(newCard);

		// examine the hand to determine number
        //  of cards and rank/suit counts
		for (numCards = 0; hand[numCards] != 0; numCards++) {
			suitCount[hand[numCards] & 0xF]++;
			rankCount[(hand[numCards] >>> 4) & 0xF]++;

			// check to see if new card is already contained
            //  in hand (rank and suit considered)
			if (numCards != 0 && hand[0] == hand[numCards]) {
				return 0;
			}
		}

		// check to see if we already have four of a particular rank
		if (numCards > 4) {
			for (int rank = 1; rank < 14; rank++) {
				if (rankCount[rank] > 4) return 0;
			}
		}

		// determine the minimum number of suits required
        //  for a flush to be possible
		int minSuitCount = numCards - 2;

		// check to see if suit is significant
		if (minSuitCount > 1) {
			// examine each card in the hand
			for (cardIndex = 0; cardIndex < numCards; cardIndex++) {
				// if the suit is not significant
                //  then strip it from the card
				if (suitCount[hand[cardIndex] & 0xF] < minSuitCount) {
					hand[cardIndex] &= 0xF0;
				}
			}
		}

		sortHand();

		long key = 0;
		for (int i = 0; i < 7; i++) {
			key += (long)hand[i] << (i * 8);
		}

		return key;
	}


    //--------------------------------------------------------------------
	// Formats and returns a card in 8-bit packed representation.
	private static int formatCard8bit(int card) {

        // 8-Bit Packed Card Representation
        // +--------+
        // |rrrr--ss|
        // +--------+
        // r = rank of card
        //  (deuce = 1, trey = 2, four = 3, five = 4,..., ace = 13)
		// s = suit of card
        //  (suits are arbitrary, can take value from 0 to 3)

		card--;
		return (((card >>> 2) + 1) << 4) + (card & 3) + 1;
	}


    //--------------------------------------------------------------------
	// Sorts the hand using Bose-Nelson Sorting Algorithm (N = 7).
	private static void sortHand() {
		swapCard(0, 4);
		swapCard(1, 5);
		swapCard(2, 6);
		swapCard(0, 2);
		swapCard(1, 3);
		swapCard(4, 6);
		swapCard(2, 4);
		swapCard(3, 5);
		swapCard(0, 1);
		swapCard(2, 3);
		swapCard(4, 5);
		swapCard(1, 4);
		swapCard(3, 6);
		swapCard(1, 2);
		swapCard(3, 4);
		swapCard(5, 6);
	} // End sortHand method


	// Swaps card i with card j.
	private static void swapCard(int i, int j) {
		if (hand[i] < hand[j]) {
			hand[i] ^= hand[j];
			hand[j] ^= hand[i];
			hand[i] ^= hand[j];
		}
	}


    //--------------------------------------------------------------------
	// Determines the relative strength of a hand
    //  (the hand is given by its unique key value).
	private static int getHandRank(long key) {
        // The following method implements a modified version of
        //  "Cactus Kev's Five-Card Poker Hand Evaluator" to determine
        //  the relative strength of two five-card hands.
		// Reference: http://www.suffecool.net/poker/evaluator.html

		hand = new int[8];
		int currentCard;
		int rank;
        int handRank = -1;
		int suit;
		int numCards = 0;

        int mainsuit     = 420;
        int suititerator = 1;

        if (key != 0) {
//            Card handCards[] = new Card[7];

            for (cardIndex = 0; cardIndex < 7; cardIndex++) {
                currentCard = (int)((key >>> (8 * cardIndex)) & 0xFF);
                if ((suit = currentCard & 0xf) != 0) {
                    mainsuit = suit;
                }
            }

            for (cardIndex = 0; cardIndex < 7; cardIndex++) {

                currentCard = (int)((key >>> (8 * cardIndex)) & 0xFF);
                if (currentCard == 0) break;
                numCards++;

                // extract suit and rank from 8-bit packed representation
                rank = (currentCard >>> 4) - 1; // 0..12
                suit = currentCard & 0xF; //0..4, 0 = don't care.

                // covert suit to ordinal() 0..3
                // if suit wasn't significant though...
                if (suit == 0) {
                    // Cactus Kev needs a suit!
                    suit = suititerator++;

                    // loop through available suits
                    if (suititerator == 5)
                        suititerator = 1;

                    // if it was the sigificant suit... Don't want extras
                    if (suit == mainsuit) {
                        suit = suititerator++;    // skip it
                        if (suititerator == 5)	  // roll 1-4
                            suititerator = 1;
                    }
                }

                // change card representation to Cactus Kev Representation
                hand[cardIndex] = Eval5.asCactusKevsFormat(rank, suit-1);
            }

            switch (numCards) {
                case 5 :
                    handRank = EvalBy5.valueOf(
                            hand[0], hand[1], hand[2], hand[3], hand[4]);
                    break;

                case 6 :
                    handRank = EvalBy5.valueOf(
                            hand[0], hand[1],
                            hand[2], hand[3], hand[4],
                            hand[5]);
                    break;

                case 7 :
                    handRank = EvalBy5.valueOf(
                            hand[0], hand[1],
                            hand[2], hand[3], hand[4],
                            hand[5], hand[6]);
                    break;

                default :
                    LOG.error("ERROR: Invalid hand in GetRank method.");
                    break;
            }
        }
        return handRank;
	}

		
    //--------------------------------------------------------------------
	private static void generateTables() {

        int  handRank;
        int  keyIndex;
        long key;

        if (verbose) {
            LOG.info("Generating and sorting keys...");
            startTimer = System.currentTimeMillis();
        }

        for (keyIndex = 0;
             keys[keyIndex] != 0 || keyIndex == 0;
             keyIndex++) {

            // add a card to each previously calculated key
            for (int card = 1; card < 53; card++) {
                // create the new key
                key = makeKey(keys[keyIndex], card);


                // insert the new key into the key compact table
                if (numCards < 7) insertKey(key);
            }
        }

        if (verbose) {
            stopTimer = System.currentTimeMillis();
            System.out.printf(
                    "done.\n\n%35s %d\n",
                    "Number of Keys Generated:",
                    (keyIndex + 1));
            System.out.printf(
                    "%35s %f seconds\n\n",
                    "Time Required:",
                    ((stopTimer - startTimer) / 1000.0));
            LOG.info("Generating hand ranks...");
            startTimer = System.currentTimeMillis();
        }

        for (keyIndex = 0;
             keys[keyIndex] != 0 || keyIndex == 0;
             keyIndex++) {

            for (int card = 1; card < 53; card++) {
                key = makeKey(keys[keyIndex], card);

                if (numCards < 7) {
                    // if number of cards is < 7 insert key
                    handRank = insertKey(key) * 53 + 53;
                }
                else {
                    // if number of cards is 7 insert hand rank
                    handRank = getHandRank(key);
                }

                int maxHandRankIndex = keyIndex * 53 + card + 53;
                // populate hand rank compact table with appropriate value
                handRanks[maxHandRankIndex] = handRank;
            }

            if (numCards == 6 || numCards == 7) {
                // insert the hand rank into the hand rank compact table
                handRanks[keyIndex * 53 + 53] =
                        getHandRank(keys[keyIndex]);
                // handRanks[keyIndex * 53 + 53] = -1000000000;
            }
        }

        if (verbose) {
            stopTimer = System.currentTimeMillis();
            System.out.printf(
                    "done.\n\n%35s %f seconds\n\n",
                    "Time Required:",
                    ((stopTimer - startTimer) / 1000.0));
        }
    }


    //--------------------------------------------------------------------
    public static short valueOf(
            Card c1, Card c2, Card c3, Card c4, Card c5, Card c6)
    {
        return valueOf(c1.invertedIndex(), c2.invertedIndex(),
                       c3.invertedIndex(), c4.invertedIndex(),
                       c5.invertedIndex(), c6.invertedIndex());
    }
    public static short valueOf(
            int invIndex1, int invIndex2, int invIndex3,
            int invIndex4, int invIndex5, int invIndex6)
    {
        retrieveOrComputeIfRequired();

        return (short) (handRanks[handRanks[handRanks[handRanks[
                handRanks[handRanks[handRanks[
                    53 + invIndex1] + invIndex2] + invIndex3] + invIndex4]
                          + invIndex5] + invIndex6]]);
    }

    public static short valueOf(Card... sevenCards)
    {
        return valueOf(sevenCards[0], sevenCards[1], sevenCards[2],
                         sevenCards[3], sevenCards[4], sevenCards[5],
                         sevenCards[6]);
    }
    public static short valueOf(
            Card c1, Card c2, Card c3, Card c4,
            Card c5, Card c6, Card c7)
    {
        return valueOf(c1.invertedIndex(), c2.invertedIndex(),
                       c3.invertedIndex(), c4.invertedIndex(),
                       c5.invertedIndex(), c6.invertedIndex(),
                       c7.invertedIndex());
    }
    public static short valueOf(
            int invIndex1, int invIndex2, int invIndex3, int invIndex4,
            int invIndex5, int invIndex6, int invIndex7)
    {
        retrieveOrComputeIfRequired();

        // HR(HR(HR(HR(c1 * 140660 - c2 * 2704 + c3 * 52 - c4) + c5) + c6) + c7)
        return (short) handRanks[handRanks[handRanks[handRanks[
                handRanks[handRanks[handRanks[
                    53 + invIndex1] + invIndex2] + invIndex3] + invIndex4]
                         + invIndex5] + invIndex6] + invIndex7];
    }

    //--------------------------------------------------------------------
    public static int shortcutFor(
            Card a, Card b, Card c, Card d, Card e, Card f)
    {
        return shortcutFor(a.invertedIndex(), b.invertedIndex(),
                           c.invertedIndex(), d.invertedIndex(),
                           e.invertedIndex(), f.invertedIndex());
    }
    public static int shortcutFor(
            Card a, Card b, Card c, Card d, Card e)
    {
        return shortcutFor(a.invertedIndex(),
                           b.invertedIndex(), c.invertedIndex(),
                           d.invertedIndex(), e.invertedIndex());
    }
    public static int shortcutFor(
            Card c1, Card c2, Card c3, Card c4)
    {
        return shortcutFor(c1.invertedIndex(), c2.invertedIndex(),
                           c3.invertedIndex(), c4.invertedIndex());
    }
    public static int shortcutFor(
            Card c1, Card c2, Card c3)
    {
        return shortcutFor(c1.invertedIndex(),
                           c2.invertedIndex(), c3.invertedIndex());
    }
    public static int shortcutFor(
            Card c1, Card c2)
    {
        return shortcutFor(c1.invertedIndex(), c2.invertedIndex());
    }
    public static int nextShortcut(int from, Card card)
    {
        return nextShortcut(from, card.invertedIndex());
    }
    public static int shortcutFor(
            Card[] twoToFiveCards)
    {
        switch (twoToFiveCards.length)
        {
            case 2: return shortcutFor(
                        twoToFiveCards[0], twoToFiveCards[1]);
            case 3: return shortcutFor(
                        twoToFiveCards[0],
                        twoToFiveCards[1], twoToFiveCards[2]);
            case 4: return shortcutFor(
                        twoToFiveCards[0], twoToFiveCards[1],
                        twoToFiveCards[2], twoToFiveCards[3]);
            case 5: return shortcutFor(
                        twoToFiveCards[0],
                        twoToFiveCards[1], twoToFiveCards[2],
                        twoToFiveCards[3], twoToFiveCards[4]);
            case 6: return shortcutFor(
                        twoToFiveCards[0], twoToFiveCards[1],
                        twoToFiveCards[2], twoToFiveCards[3],
                        twoToFiveCards[4], twoToFiveCards[5]);
            default:
                throw new Error("must give 2..6 cards!!");
        }
    }

    public static int shortcutFor(
            int invIndex1, int invIndex2, int invIndex3,
            int invIndex4, int invIndex5, int invIndex6)
    {
        retrieveOrComputeIfRequired();
        return handRanks[shortcutFor(
                invIndex1, invIndex2, invIndex3, invIndex4, invIndex5)
                 + invIndex6];
    }
    public static int shortcutFor(
            int invIndex1, int invIndex2,
            int invIndex3, int invIndex4, int invIndex5)
    {
        retrieveOrComputeIfRequired();
        return handRanks[
                shortcutFor(invIndex1, invIndex2, invIndex3, invIndex4) +
                    invIndex5];
    }
    public static int shortcutFor(
            int invIndexA, int invIndexB, int invIndexC, int invIndexD)
    {
        retrieveOrComputeIfRequired();
        return handRanks[shortcutFor(invIndexA, invIndexB, invIndexC) +
                            invIndexD];
    }
    public static int shortcutFor(
            int invIndexA, int invIndexB, int invIndexC)
    {
        retrieveOrComputeIfRequired();
        return handRanks[shortcutFor(invIndexA, invIndexB) +
                         invIndexC];
    }
    public static int shortcutFor(
            int invIndexA, int invIndexB)
    {
        retrieveOrComputeIfRequired();
        return handRanks[handRanks[
                    53 + invIndexA] + invIndexB];
    }
    public static int nextShortcut(int from, int invIndex)
    {
        retrieveOrComputeIfRequired();
        return handRanks[from + invIndex];
    }


    //--------------------------------------------------------------------
    public static short fastValueOf(int shortcut, Card card)
    {
        return fastValueOf(shortcut, card.invertedIndex());
    }
    public static short fastValueOf(
            int shortcut, Card cardA, Card cardB)
    {
        return fastValueOf(shortcut,
                           cardA.invertedIndex(), cardB.invertedIndex());
    }

    public static short fastValueOf(
            int shortcut, int invIndexA, int invIndexB)
    {
        retrieveOrComputeIfRequired();
        return (short) handRanks[handRanks[
                        shortcut + invIndexA] + invIndexB];
    }

    public static short fastValueOf(
            int shortcut, int invIndex)
    {
        retrieveOrComputeIfRequired();
        return (short) handRanks[shortcut + invIndex];
    }



    //--------------------------------------------------------------------
    public static Multiset<HandRank> computeAll()
    {
        retrieveOrComputeIfRequired();

        int c0, c1, c2, c3, c4, c5, c6;
        int u0, u1, u2, u3, u4, u5;

        if (verbose) {
            startTimer = System.currentTimeMillis();
        }

        Multiset<HandRank> histogram = EnumMultiset.create(HandRank.class);

        for (c0 = 1; c0 < 53; c0++) {
            u0 = handRanks[53 + c0];
            for (c1 = c0 + 1; c1 < 53; c1++) {
                u1 = handRanks[u0 + c1];
                for (c2 = c1 + 1; c2 < 53; c2++) {
                    u2 = handRanks[u1 + c2];
                    for (c3 = c2 + 1; c3 < 53; c3++) {
                        u3 = handRanks[u2 + c3];
                        for (c4 = c3 + 1; c4 < 53; c4++) {
                            u4 = handRanks[u3 + c4];
                            for (c5 = c4 + 1; c5 < 53; c5++) {
                                u5 = handRanks[u4 + c5];
                                for (c6 = c5 + 1; c6 < 53; c6++)
                                {
                                    short value = (short) handRanks[u5 + c6];
                                    histogram.add(HandRank.fromValue(value));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (verbose) {
            stopTimer = System.currentTimeMillis();
            LOG.info("done");
        }

        return histogram;
    }
}
