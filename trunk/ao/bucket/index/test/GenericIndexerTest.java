package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import ao.util.data.Arr;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;

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
    private Card cards[]   = Card.values();
    private int  indexes[] = Arr.sequence( Card.VALUES.length );


    //--------------------------------------------------------------------
    public void test(final Indexer indexer)
    {
        final BitSet seenHoles  = new BitSet();
        final Gapper turnGaps   = new Gapper();
        new FastIntCombiner(indexes, indexes.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

//                if (seenHoles.get( hole.suitIsomorphicIndex() )) return;
                seenHoles.set( hole.suitIsomorphicIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, indexer, turnGaps);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        turnGaps.displayStatus();
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            final Hole    hole,
            final Indexer indexer,
            final Gapper  turnGaps)
    {
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(indexes, indexes.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCards[] =
                        {cards[flopA], cards[flopB], cards[flopC]};

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                CardSequence cardSeq =
                    new LiteralCardSequence(
                            hole, new Community(
                            flopCards[0], flopCards[1], flopCards[2]));
                int index = indexer.indexOf(cardSeq);
//                flopGapper.set(index);

//                if (! seenFlops.get( index ))
//                {
                    seenFlops.set( index );
                    iterateTurns(
                            hole, flopCards, indexer, turnGaps);
//                }

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});

        System.out.println(hole);
    }

    public void iterateTurns(
            Hole    hole,
            Card    flop[],
//            int     flopIndex,
            Indexer indexer,
            Gapper  turnGaps)
    {
//        if (TurnIndexer.CASE_SETS[flopIndex] !=
//            TurnCaseSet.OTOTR_P) return;

//        Gapper localGapper = new Gapper();
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];

            CardSequence seq = new LiteralCardSequence(hole,
                   new Community(flop[0], flop[1], flop[2], turnCard));
            int turnIndex = indexer.indexOf(seq);
            turnGaps.set( turnIndex );
            
//            localGapper.set( turnIndex );

//            System.out.println( hole.isoFlop(flop).isoTurn(
//                    hole.asArray(), flop, turnCard) + "\t" + turnIndex);
        }

//        if (! localGapper.continuous())
//        {
//            System.out.println(hole + "\t" + Arrays.toString(flop));
//            localGapper.displayStatus();
//        }
//        else if (TurnIndexer.CASE_SETS[flopIndex].size() !=
//                    localGapper.length())
//        {
//            System.out.println(hole + "\t" + Arrays.toString(flop));
//            System.out.println(
//                    TurnIndexer.CASE_SETS[flopIndex].size() + "\t" +
//                    localGapper.length());
//        }
    }
}
