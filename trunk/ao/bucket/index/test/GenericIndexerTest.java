package ao.bucket.index.test;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.river.CanonRiver;
import ao.bucket.index.river.RiverCaseSet;
import ao.bucket.index.river.RiverSparceLookup;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.Rank;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.GeneralOddFinder;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.Arrays;

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

        indexerTest.test(  );
    }


    //--------------------------------------------------------------------
    private Card   cards[]    = Card.values();
    private Gapper seenFlops  = new Gapper();
    private Gapper seenTurns  = new Gapper();
    private Gapper seenRivers = new Gapper();
    private AutovivifiedList<Rank[]> turnLog =
                new AutovivifiedList<Rank[]>();


    //--------------------------------------------------------------------
    public synchronized void test()
    {
        seenTurns.clear();
        seenRivers.clear();
        final Gapper seenHoles  = new Gapper();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            private long prevTime = System.currentTimeMillis();
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);
//                if (hole.suited()) return;

                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole);
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

        System.out.println("River Gapper Status:");
        seenRivers.displayStatus();
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            final Hole hole)
    {
//        System.out.println(hole);
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        cards[flopA], cards[flopB], cards[flopC]);
                int index = flop.canonIndex();

                if (index == 1870)
                {
                    Odds odds =
                            new GeneralOddFinder().compute(
                                    hole,
                                    new Community(
                                            cards[flopA],
                                            cards[flopB],
                                            cards[flopC]),
                                    1);
                    System.out.println(flop + "\t" + odds);
                }

//                if (seenFlops.get( index )) return;
//                seenFlops.set( index );
//
//                swap(cards, flopC, 51-2);
//                swap(cards, flopB, 51-3);
//                swap(cards, flopA, 51-4);
//
//                iterateTurns(flop);
//
//                swap(cards, flopA, 51-4);
//                swap(cards, flopB, 51-3);
//                swap(cards, flopC, 51-2);
            }});
    }

    public void iterateTurns(Flop flop)
    {
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard  = cards[ turnCardIndex ];
            Turn turn      = flop.addTurn(turnCard);
            int  turnIndex = turn.canonIndex();

//            if (turnIndex == 28820)
//            {
//                System.out.println(turn);
//                turn.canonIndex();
//            }

            Rank existing[] = turnLog.get( turnIndex );
            if (existing == null)
            {
                turnLog.set(turnIndex, flop.ranks());
            }
            else if (!Arrays.equals(existing, flop.ranks()))
            {
                System.out.println(
                        Arrays.toString(existing) + " :: " +
                        Arrays.toString(flop.ranks()));
            }

            if (seenTurns.get( turnIndex )) continue;
            seenTurns.set( turnIndex );

//            swap(cards, turnCardIndex, 51-5);
//            iterateRivers(turn);
//            swap(cards, turnCardIndex, 51-5);
        }
    }

    public void iterateRivers(Turn turn)
    {
        Gapper localGapper = new Gapper();
        for (int riverCardIndex = 0;
                 riverCardIndex < 52 - 2 - 3 - 1;
                 riverCardIndex++)
        {
            Card       riverCard  = cards[ riverCardIndex ];
            CanonRiver river      = turn.addRiver( riverCard );
            long       riverIndex = river.canonIndex();
            if (riverIndex < 0)
            {
                System.out.println(river);
                river.canonIndex();
            }

//            System.out.println(
//                    turn + "\t" + riverCard + "\t" + riverIndex);

//            seenRivers.set( riverIndex );
            localGapper.set( riverIndex );
        }
        
        RiverCaseSet rcs = RiverSparceLookup.caseSet(turn.canonIndex());
        int size = rcs.size();
        if (! localGapper.continuous() ||
              localGapper.length() != size)
        {
            System.out.println(turn + "\t" + rcs);
            System.out.println("size: " + size);
            localGapper.displayStatus();

            for (int riverCardIndex = 0;
                     riverCardIndex < 52 - 2 - 3 - 1;
                     riverCardIndex++)
            {
                Card       riverCard  = cards[ riverCardIndex ];
                CanonRiver river      = turn.addRiver( riverCard );
                long       riverIndex = river.canonIndex();

                System.out.println(
                        turn + "\t" + riverCard + "\t" +
                        riverIndex + "\t" + river.riverCase());

            }
        }
    }
}