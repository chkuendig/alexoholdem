package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import static ao.util.data.Arr.swap;
import ao.util.stats.Combiner;

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

//        Set<FlopCase> flopCases = new LinkedHashSet<FlopCase>();
        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
        {
            Hole hole = Hole.newInstance(
                            holeCards[0], holeCards[1]);
//            if (! hole.paired()) continue;
//            if (! hole.suited()) continue;
//            if (hole.suited() || hole.paired()) continue; // unsuited

            swap(cards, holeCards[1].ordinal(), 51  );
            swap(cards, holeCards[0].ordinal(), 51-1);

//            for (FlopCase fc : FlopCase.VALUES)
//            {
//                System.out.println(fc);
//                Gapper flopGapper = new Gapper();
//                iterateFlops(hole, indexer, flopGapper, fc);
            iterateFlops(hole, indexer, flopGapper);
//                flopGapper.displayStatus();
//            }

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

//        System.out.println("Flop Cases:");
//        for (FlopCase flopCase : flopCases)
//        {
//            System.out.println(flopCase);
//        }
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            Hole    hole,
            Indexer indexer,
            Gapper  flopGapper
            //,FlopCase filter
            )
    {
        for (Card flopCards[] : new Combiner<Card>(cards, 50, 3))
        {
//            IsoFlop isoFlop = hole.isoFlop( flopCards );
//            if (! isoFlop.flopCase().equals(
//                    FlopCase.OT_TTR)) continue;
//            if (! isoFlop.flopCase().equals( filter )) continue;


//            flopCases.add( isoFlop.flopCase() );

//            Arrays.sort(flopCards, Card.BY_RANK_DSC);
//
//            swap(cards, flopCards[2].ordinal(), 51-2);
//            swap(cards, flopCards[1].ordinal(), 51-3);
//            swap(cards, flopCards[0].ordinal(), 51-4);
//
            CardSequence cardSeq =
                    new LiteralCardSequence(
                            hole, new Community(
                            flopCards[0], flopCards[1], flopCards[2]));
            int index = indexer.indexOf(cardSeq);
            flopGapper.set(index);
//
////            if (index == 0) System.out.println(cardSeq);
//
//            swap(cards, flopCards[0].ordinal(), 51-4);
//            swap(cards, flopCards[1].ordinal(), 51-3);
//            swap(cards, flopCards[2].ordinal(), 51-2);
        }

        System.out.println(hole);
    }
}
