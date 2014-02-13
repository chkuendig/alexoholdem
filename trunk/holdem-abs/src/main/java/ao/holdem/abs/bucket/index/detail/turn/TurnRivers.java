package ao.holdem.abs.bucket.index.detail.turn;

import ao.Infrastructure;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.canon.enumeration.HandEnum;
import ao.util.io.Dirs;
import ao.util.math.Calc;
import ao.util.pass.Traverser;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Feb 10, 2009
 * Time: 5:10:31 PM
 */
public enum TurnRivers
{;
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnRivers.class);

    private static final File store =
            new File(Dirs.get(Infrastructure.path(
                    "lookup/canon/detail/turn")),
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
        } else {
            LOG.debug("retrieved");
        }
        return offsets;
    }

    private static int[] computeFirstRivers()
    {
        LOG.debug("computeFirstRivers");

        final int offsets[] = new int[ Turn.CANONS ];
        Arrays.fill(offsets, (int)(River.CANONS + 1));

        final long[] count = {0};
        HandEnum.uniqueRivers(new Traverser<River>() {
            public void traverse(River river) {
                int turn = river.turn().canonIndex();
                offsets[ turn ] = (int) Math.min(
                        Calc.unsigned(offsets[ turn ]),
                        river.canonIndex());

                if (count[0]              == 0) System.out.println();
                if (count[0]++ % 40000000 == 0) System.out.print(".");
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

    public static long lastRiverOf(int canonTurn)
    {
        return (canonTurn == (Turn.CANONS - 1))
                    ? River.CANONS - 1
                    : Calc.unsigned(
                            FIRST_RIVER[ canonTurn + 1 ]) - 1;
    }

    public static byte canonRiverCount(int canonTurn)
    {
        return (byte)(lastRiverOf(canonTurn)
                        - firstRiverOf(canonTurn) + 1);
    }

    public static CanonRange rangeOf(int canonTurn)
    {
//        long fromIncluding = firstRiverOf(canonTurn);
//        long toExcluding   =
//                (canonTurn == (TurnLookup.CANONS - 1))
//                ? RiverLookup.CANONS
//                : firstRiverOf(canonTurn + 1);
//        return new CanonRange(
//                     fromIncluding,
//                     (char)(toExcluding - fromIncluding));
        return CanonRange.newFromCount(
                    firstRiverOf(    canonTurn ),
                    canonRiverCount( canonTurn ));
    }


    //--------------------------------------------------------------------
    public static int turnFor(long canonRiver)
    {
        int lo = 0;
        int hi = Turn.CANONS - 1;

        while (lo <= hi)
        {
            int        mid   = lo + (hi - lo) / 2;
            CanonRange range = rangeOf( mid );

            if (range.from() > canonRiver)
            {
                hi = mid - 1;
            }
            else if (range.toInclusive() < canonRiver)
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
}
