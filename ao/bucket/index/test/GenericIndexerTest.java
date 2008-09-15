package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

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
    private Gapper seenFlops = new Gapper();
    private Gapper seenTurns = new Gapper();
    private Gapper riverGaps = new Gapper();

//    private Set<Ordering> ordersHole  = new LinkedHashSet<Ordering>();
//    private Set<Ordering> ordersFlop  = new LinkedHashSet<Ordering>();
//    private Set<Ordering> ordersTurn  = new LinkedHashSet<Ordering>();
//    private Set<Ordering> ordersRiver = new LinkedHashSet<Ordering>();


    //--------------------------------------------------------------------
    public synchronized void test(final Indexer indexer)
    {
        seenTurns.clear();
        riverGaps.clear();
        final Gapper seenHoles  = new Gapper();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            private long prevTime = System.currentTimeMillis();
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

//                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, indexer);
//                System.out.println(System.currentTimeMillis() - prevTime);
                prevTime = System.currentTimeMillis();

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        System.out.println("Hole Gapper Status:");
        seenHoles.displayStatus();

        System.out.println("Flop Gapper Status:");
        seenFlops.displayStatus();

        System.out.println("Turn Gapper Status:");
        seenTurns.displayStatus();
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            final Hole    hole,
            final Indexer indexer)
    {
//        System.out.println(hole);
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                CardSequence cardSeq =
                    new LiteralCardSequence(
                            hole, new Community(
                            cards[flopA], cards[flopB], cards[flopC]));
                int index = (int) indexer.indexOf(cardSeq);
//                if (seenFlops.get( index )) return;
                seenFlops.set( index );

                // must come before swap
                Card flopCards[] =
                        {cards[flopA], cards[flopB], cards[flopC]};

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                iterateTurns(
                        hole, flopCards, index, indexer);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});
    }

    public void iterateTurns(
            Hole    hole,
            Card    flop[],
            int     flopIndex,
            Indexer indexer)
    {
//        Gapper localTurns = new Gapper();
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];

            CardSequence seq = new LiteralCardSequence(hole,
                   new Community(flop[0], flop[1], flop[2], turnCard));
            int turnIndex = (int) indexer.indexOf(seq);

            if (turnIndex == 420420)
            {
                System.out.println(seq);
            }

//            localTurns.set( turnIndex );
//            if (seenTurns.get( turnIndex )) continue;
            seenTurns.set( turnIndex );

//            swap(cards, turnCardIndex, 51-5);
//            iterateRivers(hole, flop, turnCard, turnIndex, indexer);
//            swap(cards, turnCardIndex, 51-5);
        }

//        if (! localTurns.continuous() ||
//            localTurns.length() !=
//                TurnLookup.caseSet(flopIndex).size() ||
//            localTurns.fillRatio() > 24)
//        {
//            System.out.println(
//                    hole + "\t" +
//                    Arrays.toString(flop) + "\t" +
//                    TurnLookup.caseSet(flopIndex).size() + "\t" +
//                    localTurns.fillRatio());
//            localTurns.displayStatus();
//        }
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

//        PostFlopCaseSet rcs = RiverIndexer.riverCaseSet(turnIndex);
        //PostFlopCaseSet rcs = RiverIndexer.readRiverCaseSet(turnIndex);

//        int size = rcs.size();
//        if (! localRiver.continuous() ||
//              localRiver.length() != size)
//        {
//            System.out.println("size: " + size);
//            localRiver.displayStatus();
//        }
    }
}
