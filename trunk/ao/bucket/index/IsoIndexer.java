package ao.bucket.index;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import ao.util.stats.Combo;

/**
 *
 */
public class IsoIndexer implements Indexer
{
    //--------------------------------------------------------------------
    public int indexOf(CardSequence cards)
    {
        IsoHole hole = new IsoHole( cards.hole() );

        System.out.println(hole);

        return 0;
    }


    //--------------------------------------------------------------------
    private static int colex(int... set)
    {
        int colex = 0;
        for (int i = 0; i < set.length; i++)
        {
            colex += Combo.choose(set[i], i + 1);
        }
        return colex;
    }


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        Indexer indexer = new IsoIndexer();

        for (Card a : Card.VALUES)
        {
            for (Card b : Card.VALUES)
            {
                if (a.compareTo(b) >= 0) continue;
                Hole hole = Hole.newInstance(a, b);

                indexer.indexOf(
                        new LiteralCardSequence(
                                hole, Community.PREFLOP));
            }
        }
    }
}
