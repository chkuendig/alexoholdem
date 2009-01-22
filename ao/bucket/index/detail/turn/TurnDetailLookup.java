package ao.bucket.index.detail.turn;

import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.enumeration.PermisiveFilter;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.util.io.Dir;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Date: Jan 9, 2009
 * Time: 12:39:28 PM
 */
public class TurnDetailLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnDetailLookup.class);

    private static final File   DIR = Dir.get("lookup/canon/detail/");

    private TurnDetailLookup() {}


    //--------------------------------------------------------------------
    private static final TurnDetailFlyweight DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static TurnDetailFlyweight retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        TurnDetailFlyweight details =
                TurnDetailFlyweight.retrieve( DIR );
        if (details == null)
        {
            details = computeDetails();
            TurnDetailFlyweight.persist(details, DIR);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static TurnDetailFlyweight computeDetails()
    {
        LOG.debug("computing details");

        final TurnDetailFlyweight fw = new TurnDetailFlyweight();
        CardEnum.traverseTurns(
                new PermisiveFilter<CanonHole>("%1$s"),
                new PermisiveFilter<Flop>(),
                new PermisiveFilter<Turn>(),
                new Traverser<Turn>() {
            public void traverse(Turn turn) {
                int index = turn.canonIndex();
                if (! fw.isInitiated( index )) {
                    fw.init(turn, TurnOdds.lookup(index));
                }
                fw.incrementRepresentation(index);
            }});
        unbufferAndComputeRiverInfo( fw );
        return fw;
    }


    //--------------------------------------------------------------------
    private static void unbufferAndComputeRiverInfo(
            TurnDetailFlyweight fw)
    {
        LOG.debug("computing river info");

        long   riverOffset = 0;
        byte[] riverCounts = riverCounts();

        for (int i = 0; i < TurnLookup.CANONICAL_COUNT; i++)
        {
            if ( i      %  1000 == 0) System.out.print(".");
            if ((i + 1) % 50000 == 0) System.out.println();

            fw.setRiverInfo(i, riverOffset, riverCounts[ i ]);
            riverOffset += riverCounts[ i ];
        }
    }

    public static byte[] riverCounts()
    {
        final byte[] riverCounts =
                new byte[ TurnLookup.CANONICAL_COUNT ];

        CardEnum.traverseUniqueRivers(new Traverser<River>() {
            public void traverse(River river) {
                riverCounts[ river.turn().canonIndex() ]++;
            }});

        return riverCounts;
    }


    //--------------------------------------------------------------------
    public static CanonTurnDetail lookup(int canonTurn)
    {
        return DETAILS.get(canonTurn);
    }

    public static CanonTurnDetail[] lookup(
            int fromCanonTurn, int canonTurnCount)
    {
        CanonTurnDetail[] details =
                new CanonTurnDetail[ canonTurnCount ];
        for (int i = 0; i < canonTurnCount; i++) {
            details[ fromCanonTurn + i ] = lookup(i);
        }
        return details;
    }
}
