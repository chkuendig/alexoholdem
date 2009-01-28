package ao.bucket.index.detail.flop;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.turn.Turn;
import ao.util.io.Dir;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Date: Jan 9, 2009
 * Time: 12:34:30 PM
 */
public class FlopDetails
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(FlopDetails.class);

    private static final File DIR =
            Dir.get("lookup/canon/detail/flop/");

    private FlopDetails() {}


    //--------------------------------------------------------------------
    private static final FlopDetailFlyweight DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static FlopDetailFlyweight retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        FlopDetailFlyweight details =
                FlopDetailFlyweight.retrieve(DIR);
        if (details == null)
        {
            details = computeDetails();
            FlopDetailFlyweight.persist(details, DIR);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static FlopDetailFlyweight computeDetails()
    {
        LOG.debug("computing details");

        final FlopDetailFlyweight fw = new FlopDetailFlyweight();
        CardEnum.traverseUniqueFlops(
                new Traverser<Flop>() {
            public void traverse(Flop flop) {
                int index = flop.canonIndex();
                fw.init(flop, FlopOdds.lookup(index));
            }});

        computeTurnInfo( fw );
        return fw;
    }


    //--------------------------------------------------------------------
    private static void computeTurnInfo(
            FlopDetailFlyweight fw)
    {
        LOG.debug("computing turn info");

        int    turnOffset = 0;
        byte[] turnCounts = turnCounts();

        for (int i = 0; i < FlopLookup.CANONS; i++)
        {
            fw.setTurnInfo(i, turnOffset, turnCounts[ i ]);
            turnOffset += turnCounts[ i ];
        }
    }

    public static byte[] turnCounts()
    {
        final byte[] turnCounts =
                new byte[ FlopLookup.CANONS];

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
        return DETAILS.get( canonFlop );
    }

    public static CanonFlopDetail[] lookup(
            int canonFlopFrom, int canonFlopCount)
    {
        CanonFlopDetail[] details =
                new CanonFlopDetail[ canonFlopCount ];
        for (int i = 0; i < canonFlopCount; i++) {
            details[ i ] = lookup(canonFlopFrom + i);
        }
        return details;
    }

    public static void lookup(
            int canonFlopFrom, int canonFlopCount,
            CanonDetail[] into, int startingAt)
    {
        for (int i = 0; i < canonFlopCount; i++) {
            into[ startingAt + i ] = lookup(canonFlopFrom + i);
        }
    }


    //--------------------------------------------------------------------
    //   http://en.wikipedia.org/wiki/Binary_search
    //                  #Single_comparison_per_iteration
    public static CanonFlopDetail containing(int turnCanon)
    {
        int lo = 0;
        int hi = FlopLookup.CANONS - 1;

        while (lo <= hi)
        {
            int        mid       = lo + (hi - lo) / 2;
            CanonRange turnRange = DETAILS.getTurnRange( mid );

            if (turnRange.fromCanonIndex() > turnCanon)
            {
                hi = mid - 1;
            }
            else if (turnRange.upToAndIncluding() < turnCanon)
            {
                lo = mid + 1;
            }
            else
            {
                return lookup(mid);
            }
        }

        return null;
    }
}
