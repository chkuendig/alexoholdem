package ao.bucket.index.detail.turn;

import org.apache.log4j.Logger;

/**
 * Date: Jan 9, 2009
 * Time: 12:39:28 PM
 */
public class TurnDetailLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(TurnDetailLookup.class);

    private TurnDetailLookup() {}


    //--------------------------------------------------------------------
//    private static final CanonTurnDetail[] DETAILS =
//            retrieveOrComputeDetails();
//
//
//    //--------------------------------------------------------------------
//    private static CanonTurnDetail[] retrieveOrComputeDetails()
//    {
//        LOG.debug("retrieveOrComputeDetails");
//
//        CanonTurnDetail[] details =
//                TurnDetailPersist.retrieveDetails();
//        if (details == null)
//        {
//            details = computeDetails( null );
//            TurnDetailPersist.persistDetails(details);
//        }
//        return details;
//    }
//
//
//    //--------------------------------------------------------------------
//    private static CanonTurnDetail[] computeDetails()
//    {
//        LOG.debug("computing details");
//        final TurnDetailBuffer[] buffers =
//                new TurnDetailBuffer[
//                        TurnLookup.CANONICAL_COUNT ];
//
//        for (int i = 0; i < buffers.length; i++)
//            buffers[ i ] = TurnDetailBuffer.SENTINAL;
//
//        final Card[] cards = Card.values();
//        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
//                new CombinationVisitor2() {
//            public void visit(int holeA, int holeB) {
//                Hole hole = Hole.valueOf(
//                        cards[holeA], cards[holeB]);
//
//                swap(cards, holeB, 51  );
//                swap(cards, holeA, 51-1);
//
//                iterateFlops(hole, cards, buffers);
//
//                swap(cards, holeA, 51-1);
//                swap(cards, holeB, 51  );
//            }
//        });
//
//        return unbufferAndComputeTurnInfo( buffers );
//    }
//
//    private static void iterateFlops(
//            final Hole                    hole,
//            final Card[]                  cards,
//            final TurnDetailBuffer[] buffers)
//    {
//        long before = System.currentTimeMillis();
//        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
//                .combine(new CombinationVisitor3() {
//            public void visit(int flopA, int flopB, int flopC)
//            {
//                Flop flop = hole.addFlop(
//                        cards[flopA], cards[flopB], cards[flopC]);
//
//                iterateTurns(flop, cards, buffers);
//            }});
//        System.out.println(
//                hole + " took " + (System.currentTimeMillis() - before));
//    }
//
//    private static void iterateTurns(
//            final Flop                    flop,
//            final Card[]                  cards,
//            final TurnDetailBuffer[] buffers)
//    {
//
////        FlopDetailBuffer buff = buffers[ index ];
////        if (buff == FlopDetailBuffer.SENTINAL)
////        {
////            buff = new FlopDetailBuffer(
////                          flop,
////                          odds == null ? null : odds[ index ] );
////            buffers[ index ] = buff;
////        }
////        buff.incrementFlopRepresentation();
//    }
//
//
//    //--------------------------------------------------------------------
//    private static CanonFlopDetail[] unbufferAndComputeTurnInfo(
//            FlopDetailBuffer[] buffers)
//    {
//        LOG.debug("computing turn info");
//
//        int    turnOffset = 0;
//        byte[] turnCounts = turnCounts();
//
//        CanonFlopDetail[] details =
//                new CanonFlopDetail[ FlopLookup.CANONICAL_COUNT ];
//        for (int i = 0; i < buffers.length; i++)
//        {
//            if ( i      % 20   == 0) System.out.print(".");
//            if ((i + 1) % 1000 == 0) System.out.println();
//
//            buffers[ i ].setTurnInfo(turnOffset, turnCounts[ i ]);
//            details[ i ] = buffers[ i ].toDetail();
//
//            turnOffset += turnCounts[ i ];
//        }
//        System.out.println();
//        return details;
//    }
//
//    public static byte[] turnCounts()
//    {
//        final byte[] turnCounts =
//                new byte[ FlopLookup.CANONICAL_COUNT ];
//
////        new CanonTraverser().traverseTurns(
////                null, new Traverser<Turn>() {
////            public void traverse(Turn turn) {
////                turnCounts[ turn.flop().canonIndex() ]++;
////            }
////        });
//
//        return turnCounts;
//    }


    //--------------------------------------------------------------------
    public static CanonTurnDetail lookup(int canonTurn)
    {
        return null;
    }

    public static CanonTurnDetail[] lookup(
            int fromCanonTurn, int canonTurnCount)
    {
        return null;
    }
}
