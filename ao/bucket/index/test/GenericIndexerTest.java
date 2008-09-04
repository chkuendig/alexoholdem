package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.bucket.index.iso_flop.IsoFlop;
import ao.bucket.index.iso_river.IsoRiver;
import ao.bucket.index.iso_river.RiverCase;
import ao.bucket.index.iso_turn.IsoTurn;
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
    private Card   cards[]   = Card.values();
    private int    indexes[] = Arr.sequence( Card.VALUES.length );
    private Gapper seenTurns = new Gapper();
    private Gapper riverGaps = new Gapper();
    private Set<RiverCase> riverCases = new LinkedHashSet<RiverCase>();


    //--------------------------------------------------------------------
    public synchronized void test(final Indexer indexer)
    {
        seenTurns.clear();
        riverGaps.clear();
        riverCases.clear();

        final BitSet seenHoles  = new BitSet();
        new FastIntCombiner(indexes, indexes.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                if (seenHoles.get( hole.suitIsomorphicIndex() )) return;
                seenHoles.set( hole.suitIsomorphicIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, indexer);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        riverGaps.displayStatus();
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            final Hole    hole,
            final Indexer indexer)
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

                if (! seenFlops.get( index ))
                {
                    seenFlops.set( index );
                    iterateTurns(
                            hole, flopCards, indexer);
                }

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});

        System.out.println(hole);
    }

    public void iterateTurns(
            Hole    hole,
            Card    flop[],
            Indexer indexer)
    {
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];

            CardSequence seq = new LiteralCardSequence(hole,
                   new Community(flop[0], flop[1], flop[2], turnCard));
            int turnIndex = indexer.indexOf(seq);

            if (seenTurns.get( turnIndex )) continue;
                seenTurns.set( turnIndex );

            swap(cards, turnCardIndex, 51-5);
            iterateRivers(hole, flop, turnCard, indexer);
            swap(cards, turnCardIndex, 51-5);
        }
    }

    public void iterateRivers(
            Hole    hole,
            Card    flop[],
            Card    turn,
            Indexer indexer)
    {
//        Gapper localRiver = new Gapper();
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card riverCard = cards[ turnCardIndex ];

            IsoFlop  isoFlop  = hole.isoFlop(flop);
            IsoTurn  isoTurn  = isoFlop.isoTurn(
                    hole.asArray(), flop, turn);
            IsoRiver isoRiver = isoTurn.isoRiver(
                    hole.asArray(), flop, turn, riverCard);


            if (riverCases.add( isoRiver.riverCase() ))
            {
                System.out.println(isoRiver.riverCase());
            }


//            CardSequence seq = new LiteralCardSequence(hole,
//                   new Community(flop[0], flop[1], flop[2],
//                                 turn, riverCard));
//            int riverIndex = indexer.indexOf(seq);

//            localRiver.set( riverIndex );


        }
    }
}
