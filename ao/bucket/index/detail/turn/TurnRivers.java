package ao.bucket.index.detail.turn;

import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.enumeration.HandEnum;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.river.River;
import ao.bucket.index.river.RiverLookup;
import ao.bucket.index.turn.TurnLookup;
import ao.util.io.Dir;
import ao.util.math.Calc;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Feb 10, 2009
 * Time: 5:10:31 PM
 */
public class TurnRivers
{
    //--------------------------------------------------------------------
    private TurnRivers() {}

    private static final Logger LOG =
            Logger.getLogger(TurnRivers.class);

    private static final File store =
            new File(Dir.get("lookup/canon/detail/turn"),
                     "first_river.int");

    private static final int[] FIRST_RIVER =
            retrieveOrComputeFirstRivers();


    //--------------------------------------------------------------------
    private static int[] retrieveOrComputeFirstRivers()
    {
        LOG.debug("retrieveOrComputeFirstRivers");
        int[] offsets = PersistentInts.retrieve(store);
        if (offsets == null) {
            offsets = computeFirstRivers();
            LOG.debug("persisting");
            PersistentInts.persist(offsets, store);
        }
        return offsets;
    }

    private static int[] computeFirstRivers()
    {
        LOG.debug("computeFirstRivers");

        final int[] offsets = new int[ TurnLookup.CANONS ];
        Arrays.fill(offsets, (int)(RiverLookup.CANONS + 1));

        HandEnum.uniqueRivers(new Traverser<River>() {
            public void traverse(River river) {
                int turn = river.turn().canonIndex();
                offsets[ turn ] = (int) Math.min(
                        Calc.unsigned(offsets[ turn ]),
                        river.canonIndex());
            }
        });

        return offsets;
    }


    //--------------------------------------------------------------------
    public static long firstRiverOf(int canonTurn)
    {
        return Calc.unsigned(
                FIRST_RIVER[ canonTurn ]);
    }

    public static CanonRange rangeOf(int canonTurn)
    {
        long fromIncluding = firstRiverOf(canonTurn);
        long toExcluding   =
                (canonTurn == (TurnLookup.CANONS - 1))
                ? RiverLookup.CANONS
                : firstRiverOf(canonTurn + 1);
        return new CanonRange(
                     fromIncluding,
                     (char)(toExcluding - fromIncluding));
    }


    //--------------------------------------------------------------------
    public static int arbitraryTurnFor(long canonRiver)
    {
        int lo = 0;
        int hi = FlopLookup.CANONS - 1;

        while (lo <= hi)
        {
            int        mid   = lo + (hi - lo) / 2;
            CanonRange range = rangeOf( mid );

            if (range.fromCanonIndex() > canonRiver)
            {
                hi = mid - 1;
            }
            else if (range.upToAndIncluding() < canonRiver)
            {
                lo = mid + 1;
            }
            else
            {
                return mid;
            }
        }
        return -1;
    }



    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        System.out.println(
                TurnRivers.rangeOf(0));
    }
}
