package ao.holdem.def.model.cards;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval_567.EvalSlow;
import ao.holdem.def.model.cards.community.River;
import ao.util.stats.Combiner;

/**
 * Immutable.
 */
public class Hand implements Comparable<Hand>
{
    //--------------------------------------------------------------------
    private final Card CARDS[];
    private final short VALUE; //1 is the best, and 7462 is worst


    //--------------------------------------------------------------------
    public Hand(Card... sevenCards)
    {
        assert sevenCards.length == 7;

        CARDS = sevenCards;
        VALUE = bestFiveRank();
    }
    public Hand(Hole hole, River community)
    {
        this(hole.first(), hole.second(),
             community.first(), community.second(),
             community.third(), community.fourth(),
             community.fifth());
    }


    //--------------------------------------------------------------------
    private short bestFiveRank()
    {
        Combiner<Card> combiner = new Combiner<Card>(CARDS, 5);

//        System.out.println("!######");
//        long i = 0;
        short bestFiveValue = Short.MIN_VALUE;
        while (combiner.hasMoreElements())
        {
//            System.out.println("!!!" + i++);
            Card permutation[] = combiner.nextElement();

            short permutationRank = EvalSlow.valueOf(permutation);
            if (permutationRank > bestFiveValue)
            {
                bestFiveValue = permutationRank;
            }
        }

        return bestFiveValue;
    }


    //--------------------------------------------------------------------
    public short value()
    {
        return VALUE;
    }


    //--------------------------------------------------------------------
    public int compareTo(Hand o)
    {
        return Integer.signum(VALUE - o.VALUE);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return new Hole(CARDS[0], CARDS[1]) + ", " +
               new River(CARDS[2], CARDS[3], CARDS[4],
                         CARDS[5], CARDS[6]);
    }


    //--------------------------------------------------------------------
    public enum HandRank
    {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH;

        public static HandRank fromValue(int val)
        {
//            (7462 - bestFiveRank())
//            (7462 - 1277)
            if (val < 1277) return HIGH_CARD;        // 1277 high card
            if (val < 4137) return ONE_PAIR;         // 2860 one pair
            if (val < 4995) return TWO_PAIR;         //  858 two pair
            if (val < 5853) return THREE_OF_A_KIND;  //  858 three-kind
            if (val < 5863) return STRAIGHT;         //   10 straights
            if (val < 7140) return FLUSH;            // 1277 flushes
            if (val < 7296) return FULL_HOUSE;       //  156 full house
            if (val < 7452) return FOUR_OF_A_KIND;   //  156 four-kind
            return STRAIGHT_FLUSH;                   //   10 straight-flushes
            

//            if (val > 6185) return HIGH_CARD;        // 1277 high card
//            if (val > 3325) return ONE_PAIR;         // 2860 one pair
//            if (val > 2467) return TWO_PAIR;         //  858 two pair
//            if (val > 1609) return THREE_OF_A_KIND;  //  858 three-kind
//            if (val > 1599) return STRAIGHT;         //   10 straights
//            if (val > 322)  return FLUSH;            // 1277 flushes
//            if (val > 166)  return FULL_HOUSE;       //  156 full house
//            if (val > 10)   return FOUR_OF_A_KIND;   //  156 four-kind
//            return STRAIGHT_FLUSH;                   //   10 straight-flushes
        }
    }
}
