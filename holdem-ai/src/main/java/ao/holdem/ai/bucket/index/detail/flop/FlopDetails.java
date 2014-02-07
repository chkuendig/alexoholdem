package ao.holdem.ai.bucket.index.detail.flop;

import ao.holdem.model.canon.flop.Flop;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.model.canon.turn.Turn;
import ao.holdem.ai.bucket.index.detail.CanonDetail;
import ao.holdem.ai.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.holdem.ai.bucket.index.detail.range.CanonRange;
import ao.holdem.model.enumeration.HandEnum;
import ao.holdem.model.enumeration.PermisiveFilter;
import ao.util.data.LongBitSet;
import ao.util.io.Dirs;
import ao.util.pass.Traverser;
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
            Dirs.get("lookup/canon/detail/flop/");

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

        LOG.debug("done");
        return details;
    }


    //--------------------------------------------------------------------
    private static FlopDetailFlyweight computeDetails()
    {
        LOG.debug("computing details");

        final LongBitSet          initiated =
                new LongBitSet(Flop.CANONS);
        final FlopDetailFlyweight flyweight = new FlopDetailFlyweight();
        HandEnum.flops(
                new PermisiveFilter<CanonHole>(),
                new PermisiveFilter<Flop>(),
                new Traverser<Flop>() {
            public void traverse(Flop flop) {
                int index = flop.canonIndex();
                if (initiated.get(index)) {
                    flyweight.incrementRepresentation(index);
                } else {
                    initiated.set(index);
                    flyweight.initiate(flop, FlopOdds.lookup(index));
                }
            }});

        computeTurnInfo( flyweight );
        return flyweight;
    }


    //--------------------------------------------------------------------
    private static void computeTurnInfo(
            FlopDetailFlyweight fw)
    {
        LOG.debug("computing turn info");

        int    turnOffset = 0;
        byte[] turnCounts = turnCounts();

        for (int i = 0; i < Flop.CANONS; i++)
        {
            fw.setTurnInfo(i, turnOffset, turnCounts[ i ]);
            turnOffset += turnCounts[ i ];
        }
    }

    public static byte[] turnCounts()
    {
        final byte[] turnCounts =
                new byte[ Flop.CANONS];

        HandEnum.uniqueTurns(
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
        int hi = Flop.CANONS - 1;

        while (lo <= hi)
        {
            int        mid       = lo + (hi - lo) / 2;
            CanonRange turnRange = DETAILS.getTurnRange( mid );

            if (turnRange.from() > turnCanon)
            {
                hi = mid - 1;
            }
            else if (turnRange.toInclusive() < turnCanon)
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
