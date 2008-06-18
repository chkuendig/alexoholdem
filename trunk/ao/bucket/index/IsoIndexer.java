package ao.bucket.index;

import ao.bucket.index.iso_cards.IsoHole;
import ao.bucket.index.iso_case.CommunityCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.util.stats.Combiner;
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
        Card cards[] = Card.values();

        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
        {
            swap(cards, holeCards[1].ordinal(), 51  );
            swap(cards, holeCards[0].ordinal(), 51-1);

            Hole    hole    = Hole.newInstance(
                                    holeCards[0], holeCards[1]);
            IsoHole isoHole = new IsoHole( hole );
            handleFlop(cards, holeCards, hole, isoHole);

            swap(cards, holeCards[0].ordinal(), 50);
            swap(cards, holeCards[1].ordinal(), 51);
        }
    }

    private static void handleFlop(
            Card    cards[],
            Card    holeCards[],
            Hole    hole,
            IsoHole isoHole)
    {
        if (isoHole.holeCase().index() != 0) return;

//        Map<IsoHole, int[]> abs =
//                        new LinkedHashMap<IsoHole, int[]>();

        for (Card flopCards[] : new Combiner<Card>(cards, 50, 3))
        {
            swap(cards, flopCards[2].ordinal(), 51-2);
            swap(cards, flopCards[1].ordinal(), 51-3);
            swap(cards, flopCards[0].ordinal(), 51-4);

            CommunityCase flopCase =
                    new CommunityCase(flopCards, holeCards);

            swap(cards, flopCards[0].ordinal(), 51-4);
            swap(cards, flopCards[1].ordinal(), 51-3);
            swap(cards, flopCards[2].ordinal(), 51-2);
        }
    }


    private static void swap(Card cards[], int i, int j)
    {
        Card tmp = cards[i];
        cards[i] = cards[j];
        cards[j] = tmp;
    }
}
