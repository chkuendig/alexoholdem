package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.bucket.index.incremental.RiverIndexer;
import ao.bucket.index.iso_cards.Ordering;
import ao.bucket.index.iso_flop.IsoFlop;
import ao.bucket.index.iso_river.RiverCaseSet;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
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
    private Gapper seenTurns = new Gapper();
    private Gapper riverGaps = new Gapper();

    private Set<Ordering> ordersHole  = new LinkedHashSet<Ordering>();
    private Set<Ordering> ordersFlop  = new LinkedHashSet<Ordering>();
    private Set<Ordering> ordersTurn  = new LinkedHashSet<Ordering>();
    private Set<Ordering> ordersRiver = new LinkedHashSet<Ordering>();


    //--------------------------------------------------------------------
    public synchronized void test(final Indexer indexer)
    {
        seenTurns.clear();
        riverGaps.clear();
        final BitSet seenHoles  = new BitSet();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            private long prevTime = System.currentTimeMillis();
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

//                if (seenHoles.get( hole.suitIsomorphicIndex() )) return;
//                seenHoles.set( hole.suitIsomorphicIndex() );

                if (ordersHole.add( hole.ordering() ))
                {
                    System.out.println("hole\t" + hole.ordering());
                }
                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, indexer);
//                System.out.println(System.currentTimeMillis() - prevTime);
                prevTime = System.currentTimeMillis();

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

//        for (Ordering order : orders)
//        {
//            System.out.println(order);
//        }
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            final Hole    hole,
            final Indexer indexer)
    {
        final BitSet seenFlops = new BitSet();

//        System.out.println(hole);
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCards[] =
                        {cards[flopA], cards[flopB], cards[flopC]};

                IsoFlop isoFlop = hole.isoFlop(flopCards);
                if (ordersFlop.add( isoFlop.order() ) &&
                        !ordersHole.contains( isoFlop.order() ))
                {
                    System.out.println("flop\t" + isoFlop.order());
                }

                CardSequence cardSeq =
                    new LiteralCardSequence(
                            hole, new Community(
                            flopCards[0], flopCards[1], flopCards[2]));
                int index = (int) indexer.indexOf(cardSeq);
                if (seenFlops.get( index )) return;
                seenFlops.set( index );

//                swap(cards, flopC, 51-2);
//                swap(cards, flopB, 51-3);
//                swap(cards, flopA, 51-4);
//
//                iterateTurns(
//                        hole, flopCards, indexer);
//
//                swap(cards, flopA, 51-4);
//                swap(cards, flopB, 51-3);
//                swap(cards, flopC, 51-2);
            }});
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
            int turnIndex = (int) indexer.indexOf(seq);

            if (seenTurns.get( turnIndex )) continue;
                seenTurns.set( turnIndex );

            swap(cards, turnCardIndex, 51-5);
            iterateRivers(hole, flop, turnCard, turnIndex, indexer);
            swap(cards, turnCardIndex, 51-5);
        }
    }

    public void iterateRivers(
            Hole    hole,
            Card    flop[],
            Card    turn,
            int     turnIndex,
            Indexer indexer)
    {
//        System.out.println(
//                Arrays.toString(flop) + "\t" + turn);

        Gapper localRiver = new Gapper();
        for (int riverCardIndex = 0;
                 riverCardIndex < 52 - 2 - 3 - 1;
                 riverCardIndex++)
        {
            Card riverCard = cards[ riverCardIndex ];

            CardSequence seq = new LiteralCardSequence(hole,
                   new Community(flop[0], flop[1], flop[2],
                                 turn, riverCard));
            int riverIndex = (int) indexer.indexOf(seq);

            localRiver.set( riverIndex );
//            System.out.println(seq + "\t" + riverIndex);
        }

        RiverCaseSet rcs = RiverIndexer.riverCaseSet(turnIndex);
        //RiverCaseSet rcs = RiverIndexer.readRiverCaseSet(turnIndex);

        int size = rcs.size();
        if (! localRiver.continuous() ||
              localRiver.length() != size)
        {
            System.out.println("size: " + size);
            localRiver.displayStatus();
        }
    }
}
