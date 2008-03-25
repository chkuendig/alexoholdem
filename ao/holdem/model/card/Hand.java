package ao.holdem.model.card;

import ao.odds.eval.eval_567.EvalSlow;
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
    public Hand(Hole hole, Community community)
    {
        this(hole.first(), hole.second(),
             community.flopA(), community.flopB(),
             community.flopC(), community.turn(),
             community.river());
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
               new Community(CARDS[2], CARDS[3], CARDS[4],
                             CARDS[5], CARDS[6]);
    }


}
