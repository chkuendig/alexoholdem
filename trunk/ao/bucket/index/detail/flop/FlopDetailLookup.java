package ao.bucket.index.detail.flop;

import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.enumeration.CardEnum.PermisiveFilter;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.turn.Turn;
import ao.odds.agglom.Odds;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

/**
 * Date: Jan 9, 2009
 * Time: 12:34:30 PM
 */
public class FlopDetailLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(FlopDetailLookup.class);

    private FlopDetailLookup() {}


    //--------------------------------------------------------------------
//    static {  fixComputedDetails();  }
//    private static void fixComputedDetails()
//    {
//        LOG.info("fixing computed details");
//
////        Odds[] odds = new Odds[ FlopLookup.CANONICAL_COUNT ];
////        for (CanonFlopDetail detail :
////                FlopDetailPersist.retrieveDetails())
////            odds[ (int) detail.canonIndex() ] = detail.headsUpOdds();
////
////        FlopDetailPersist.persistDetails(
////                computeDetails( odds ));
//
//        FlopDetailPersist.persistDetails(
//                FlopDetailPersist.retrieveDetails() );
//    }


    //--------------------------------------------------------------------
    private static final CanonFlopDetail[] DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonFlopDetail[] details =
                FlopDetailPersist.retrieveDetails();
        if (details == null)
        {
            details = computeDetails( null );
            FlopDetailPersist.persistDetails(details);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] computeDetails(
            final Odds[] odds)
    {
        LOG.debug("computing details");
        final FlopDetailBuffer[] buffers =
                new FlopDetailBuffer[
                        FlopLookup.CANONICAL_COUNT ];

        for (int i = 0; i < buffers.length; i++)
            buffers[ i ] = FlopDetailBuffer.SENTINAL;

        CardEnum.traverseFlops(
                new PermisiveFilter<CanonHole>(),
                new PermisiveFilter<Flop>(),
                new Traverser<Flop>() {
            public void traverse(Flop flop) {
                int index = flop.canonIndex();

                FlopDetailBuffer buff = buffers[ index ];
                if (buff == FlopDetailBuffer.SENTINAL)
                {
                    buff = new FlopDetailBuffer(
                                  flop,
                                  odds == null ? null : odds[ index ] );
                    buffers[ index ] = buff;
                }
                buff.incrementFlopRepresentation();
            }});

        return unbufferAndComputeTurnInfo( buffers );
    }


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] unbufferAndComputeTurnInfo(
            FlopDetailBuffer[] buffers)
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

        CardEnum.traverseUniqueTurns(
                new Traverser<Turn>() {
            public void traverse(Turn turn) {
                turnCounts[ turn.flop().canonIndex() ]++;
            }});

        return turnCounts;
    }


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
