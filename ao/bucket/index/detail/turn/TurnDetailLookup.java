package ao.bucket.index.detail.turn;

import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.enumeration.PermisiveFilter;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

import java.util.Arrays;

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
    private static final CanonTurnDetail[] DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static CanonTurnDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonTurnDetail[] details =
                TurnDetailPersist.retrieveDetails();
        if (details == null)
        {
            details = computeDetails();
            TurnDetailPersist.persistDetails(details);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static CanonTurnDetail[] computeDetails()
    {
        LOG.debug("computing details");

        final TurnDetailBuffer[] buffers =
                new TurnDetailBuffer[
                        TurnLookup.CANONICAL_COUNT ];
        Arrays.fill(buffers, TurnDetailBuffer.SENTINAL);

        CardEnum.traverseTurns(
                new PermisiveFilter<CanonHole>("%1$s"),
                new PermisiveFilter<Flop>(),
                new PermisiveFilter<Turn>(),
                new Traverser<Turn>() {
            public void traverse(Turn turn) {
                int              index = turn.canonIndex();
                TurnDetailBuffer buff  = buffers[ index ];
                if (buff == TurnDetailBuffer.SENTINAL)
                {
                    buff = new TurnDetailBuffer(
                                  turn, TurnOdds.lookup(index));
                    buffers[ index ] = buff;
                }
                buff.incrementTurnRepresentation();
            }});

        return unbufferAndComputeRiverInfo( buffers );
    }


    //--------------------------------------------------------------------
    private static CanonTurnDetail[] unbufferAndComputeRiverInfo(
            TurnDetailBuffer[] buffers)
    {
        LOG.debug("computing river info");

        long   riverOffset = 0;
        byte[] riverCounts = riverCounts();

        CanonTurnDetail[] details =
                new CanonTurnDetail[ TurnLookup.CANONICAL_COUNT ];
        for (int i = 0; i < buffers.length; i++)
        {
            if ( i      %  1000 == 0) System.out.print(".");
            if ((i + 1) % 50000 == 0) System.out.println();

            buffers[ i ].setRiverInfo(riverOffset, riverCounts[ i ]);
            details[ i ] = buffers[ i ].toDetail();

            riverOffset += riverCounts[ i ];
        }
        System.out.println();
        return details;
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
        return DETAILS[canonTurn];
    }

    public static CanonTurnDetail[] lookup(
            int fromCanonTurn, int canonTurnCount)
    {
        return Arrays.copyOfRange(DETAILS,
                                  fromCanonTurn,
                                  fromCanonTurn + canonTurnCount);
    }
}
