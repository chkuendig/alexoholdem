package ao.bucket.index.test.detail;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.test.Gapper;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.OddHist;
import ao.odds.agglom.impl.GeneralHistFinder;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor1;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

/**
 * User: iscott
 * Date: Sep 30, 2008
 */
public class CanonTurnTest
{
    //--------------------------------------------------------------------
    private static final int HOLE_A = 51 - 1,
                             HOLE_B = 51,

                             FLOP_A = 51 - 4,
                             FLOP_B = 51 - 3,
                             FLOP_C = 51 - 2,

                             TURN   = 51 - 5;


    //--------------------------------------------------------------------
    private final Card   CARDS[]    = Card.values();
    private final Gapper seenHoles  = new Gapper();
    private final Gapper seenFlops  = new Gapper();
    private       long   TURNS[]    =
            new long[ TurnLookup.CANONICAL_COUNT];


    //-----------------------------------------------------------------------
    public synchronized void testTurns()
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        Card.VALUES[holeA], Card.VALUES[holeB]);
                System.out.println(hole);

//                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(CARDS, holeB, HOLE_B );
                swap(CARDS, holeA, HOLE_A );

                iterateFlops(hole);

                swap(CARDS, holeA, HOLE_A );
                swap(CARDS, holeB, HOLE_B );
            }
        });
    }

    private void iterateFlops(
            final Hole hole)
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCardA = CARDS[flopA];
                Card flopCardB = CARDS[flopB];
                Card flopCardC = CARDS[flopC];

                Flop flop = hole.addFlop(
                        flopCardA, flopCardB, flopCardC);
                int index = flop.canonIndex();
//                if (seenFlops.get( index )) return;
                seenFlops.set( index );

                swap(CARDS, flopC, FLOP_C);
                swap(CARDS, flopB, FLOP_B);
                swap(CARDS, flopA, FLOP_A);

                iterateTurns(
                        hole, flop);

                swap(CARDS, flopA, FLOP_A);
                swap(CARDS, flopB, FLOP_B);
                swap(CARDS, flopC, FLOP_C);
            }});
    }

    private void iterateTurns(
            final Hole hole,
            final Flop flop)
    {
        new FastIntCombiner(Card.INDEXES, TURN + 1)
                .combine(new CombinationVisitor1() {
            public void visit(int turnIndex)
            {
                Card turnCard  = CARDS[ turnIndex ];
                Turn turn      = flop.addTurn( turnCard );
                int  canonTurn = turn.canonIndex();

                OddHist odds     =
                        new GeneralHistFinder().compute(
                                hole,
                                new Community(
                                        CARDS[ FLOP_A ],
                                        CARDS[ FLOP_B ],
                                        CARDS[ FLOP_C ],
                                        turnCard));
                long existing = TURNS[ canonTurn ];
                long incoming = odds.secureHashCode();
                if (existing == 0)
                {
                    TURNS[ canonTurn ] = incoming;
                }
                else if (existing != incoming)
                {
                    System.out.println("ERROR AT: " + turn);
                }
            }
        });
    }
}
