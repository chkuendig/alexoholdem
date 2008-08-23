package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_case.FlopCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.util.stats.Combiner;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Date: Aug 21, 2008
 * Time: 7:05:20 PM
 */
public class GenericIndexerTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        GenericIndexerTest indexerTest = new GenericIndexerTest();

        indexerTest.test( new IndexerImpl() );
    }


    //--------------------------------------------------------------------
    private Card cards[] = Card.values();


    //--------------------------------------------------------------------
    public void test(Indexer indexer)
    {
        Gapper flopGapper = new Gapper();

        Set<FlopCase> flopCases = new LinkedHashSet<FlopCase>();
        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
        {
            Hole hole = Hole.newInstance(
                            holeCards[0], holeCards[1]);
//            if (! hole.paired()) continue;
//            if (! hole.suited()) continue;
//            if (hole.suited() || hole.paired()) continue;

            swap(cards, holeCards[1].ordinal(), 51  );
            swap(cards, holeCards[0].ordinal(), 51-1);

            iterateFlops(hole, indexer, flopGapper,
                         flopCases);
//            int holeIndex =
//                    indexer.indexOf(
//                            new LiteralCardSequence(
//                                    hole, new Community()));

            swap(cards, holeCards[0].ordinal(), 51-1);
            swap(cards, holeCards[1].ordinal(), 51  );

//            break;
        }
        System.out.println("Flop gapper status:");
        flopGapper.displayStatus();

        System.out.println("Flop Cases:");
        for (FlopCase flopCase : flopCases)
        {
            System.out.println(flopCase);
        }
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            Hole    hole,
            Indexer indexer,
            Gapper  flopGapper,
            Set<FlopCase> flopCases)
    {
        for (Card flopCards[] : new Combiner<Card>(cards, 50, 3))
        {
            IsoFlop isoFlop = hole.isoFlop( flopCards );
//            if (! isoFlop.flopCase().equals(
//                    FlopCase.CASE_12_113)) continue;

            flopCases.add( isoFlop.flopCase() );

//            Arrays.sort(flopCards, Card.BY_RANK_DSC);
//
//            swap(cards, flopCards[2].ordinal(), 51-2);
//            swap(cards, flopCards[1].ordinal(), 51-3);
//            swap(cards, flopCards[0].ordinal(), 51-4);
//
//            CardSequence cardSeq =
//                    new LiteralCardSequence(
//                            hole, new Community(
//                            flopCards[0], flopCards[1], flopCards[2]));
//            int index = indexer.indexOf(cardSeq);
//            flopGapper.set(index);
//
////            if (index == 0) System.out.println(cardSeq);
//
//            swap(cards, flopCards[0].ordinal(), 51-4);
//            swap(cards, flopCards[1].ordinal(), 51-3);
//            swap(cards, flopCards[2].ordinal(), 51-2);
        }

        System.out.println(hole);
    }


    //--------------------------------------------------------------------
    private static void swap(Card cards[], int i, int j)
    {
        Card tmp = cards[i];
        cards[i] = cards[j];
        cards[j] = tmp;
    }
}
