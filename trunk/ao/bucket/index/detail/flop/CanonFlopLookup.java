package ao.bucket.index.detail.flop;

import ao.bucket.index.detail.enumeration.CanonTraverser;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.Odds;
import static ao.util.data.Arr.swap;
import ao.util.math.stats.FastIntCombiner;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor3;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

/**
 * Date: Jan 9, 2009
 * Time: 12:34:30 PM
 */
public class CanonFlopLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(CanonFlopLookup.class);


    //--------------------------------------------------------------------
//    static {  fixComputedDetails();  }
//    private static void fixComputedDetails()
//    {
//        LOG.info("fixing computed details");
//
////        Odds[] odds = new Odds[ FlopLookup.CANONICAL_COUNT ];
////        for (CanonFlopDetail detail :
////                FlopLookupPersist.retrieveDetails())
////            odds[ (int) detail.canonIndex() ] = detail.headsUpOdds();
////
////        FlopLookupPersist.persistDetails(
////                computeDetails( odds ));
//
//        FlopLookupPersist.persistDetails(
//                FlopLookupPersist.retrieveDetails() );
//    }


    //--------------------------------------------------------------------
    private static final CanonFlopDetail[] DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonFlopDetail[] details =
                FlopLookupPersist.retrieveDetails();
        if (details == null)
        {
            details = computeDetails( null );
            FlopLookupPersist.persistDetails(details);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] computeDetails(
            final Odds[] odds)
    {
        LOG.debug("computing details");
        final CanonFlopDetailBuffer[] buffers =
                new CanonFlopDetailBuffer[
                        FlopLookup.CANONICAL_COUNT ];

        for (int i = 0; i < buffers.length; i++)
            buffers[ i ] = CanonFlopDetailBuffer.SENTINAL;

        final Card[] cards     = Card.values();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB) {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, cards, buffers, odds);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        return unbufferAndComputeTurnInfo( buffers );
    }

    public static void iterateFlops(
            final Hole                    hole,
            final Card[]                  cards,
            final CanonFlopDetailBuffer[] buffers,
            final Odds[]                  odds)
    {
        long before = System.currentTimeMillis();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        cards[flopA], cards[flopB], cards[flopC]);
                int index = flop.canonIndex();

                CanonFlopDetailBuffer buff = buffers[ index ];
                if (buff == CanonFlopDetailBuffer.SENTINAL)
                {
                    buff = new CanonFlopDetailBuffer(
                                  flop,
                                  odds == null ? null : odds[ index ] );
                    buffers[ index ] = buff;
                }
                buff.incrementFlopRepresentation();
            }});
        System.out.println(
                hole + " took " + (System.currentTimeMillis() - before));
    }


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] unbufferAndComputeTurnInfo(
            CanonFlopDetailBuffer[] buffers)
    {
        LOG.debug("computing turn info");

        int    turnOffset = 0;
        byte[] turnCounts = turnCounts();

        CanonFlopDetail[] details =
                new CanonFlopDetail[ FlopLookup.CANONICAL_COUNT ];
        for (int i = 0; i < buffers.length; i++)
        {
            if ( i      % 20   == 0) System.out.print(".");
            if ((i + 1) % 1000 == 0) System.out.println();

            buffers[ i ].setTurnInfo(turnOffset, turnCounts[ i ]);
            details[ i ] = buffers[ i ].toDetail();

            turnOffset += turnCounts[ i ];
        }
        System.out.println();
        return details;
    }

    public static byte[] turnCounts()
    {
        final byte[] turnCounts =
                new byte[ FlopLookup.CANONICAL_COUNT ];

        new CanonTraverser().traverseTurns(
                null, new Traverser<Turn>() {
            public void traverse(Turn turn) {
                turnCounts[ turn.flop().canonIndex() ]++;
            }
        });

        return turnCounts;
    }


    //--------------------------------------------------------------------
    private CanonFlopLookup() {}


    //--------------------------------------------------------------------
    public static CanonFlopDetail lookup(int canonFlop)
    {
        return DETAILS[ canonFlop ];
    }

    public static CanonFlopDetail[] lookup(
            int canonFlopFrom, int canonFlopCount)
    {
        CanonFlopDetail[] details =
                new CanonFlopDetail[ canonFlopCount ];
        for (int i = 0; i < canonFlopCount; i++)
        {
            details[ i ] = lookup( canonFlopFrom + i );
        }
        return details;
    }
}
