package ao.bucket.index.detail.preflop;

import ao.bucket.index.detail.enumeration.CanonTraverser;
import ao.bucket.index.detail.preflop.CanonHoleDetail.Buffer;
import ao.bucket.index.flop.Flop;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

/**
 * Date: Jan 9, 2009
 * Time: 12:30:51 PM
 */
public class CanonHoleLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(CanonHoleLookup.class);


    //--------------------------------------------------------------------
    private static final CanonHoleDetail[] DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonHoleDetail[] details = HoleLookupPersist.retrieveDetails();
        if (details == null)
        {
            details = computeDetails();
            HoleLookupPersist.persistDetails(details);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] computeDetails()
    {
        LOG.debug("computing details");
        final CanonHoleDetail.Buffer[] buffers =
                new CanonHoleDetail.Buffer[ Hole.CANONICAL_COUNT ];

        for (Card holeA : Card.VALUES)
        {
            for (Card holeB : Card.VALUES)
            {
                if (holeA.ordinal() >= holeB.ordinal()) continue;
                Hole hole = Hole.valueOf(holeA, holeB);

                Buffer buff = buffers[ hole.canonIndex() ];
                if (buff == null)
                {
                    buff = new Buffer( hole );
                    buffers[ hole.canonIndex() ] = buff;
                }
                buff.REPRESENTS++;
            }
        }

        CanonHoleDetail[] details =
                new CanonHoleDetail[ Hole.CANONICAL_COUNT ];
        for (int i = 0; i < buffers.length; i++)
        {
            System.out.print(".");
            computeFlopDetails( buffers[i] );
            details[ i ] = buffers[i].toDetail();
        }
        System.out.println();

        return details;
    }

    private static void computeFlopDetails(
            final CanonHoleDetail.Buffer buff)
    {
        new CanonTraverser().traverseFlops(
                new long[]{ buff.HOLE.canonIndex() },
                new Traverser<Flop>() {
            public void traverse(Flop flop) {
                if (buff.FIRST_CANON_FLOP == -1) {
                    buff.FIRST_CANON_FLOP = flop.canonIndex();
                }
                buff.FIRST_CANON_FLOP =
                        Math.min(buff.FIRST_CANON_FLOP,
                                 flop.canonIndex());
                buff.CANON_FLOP_COUNT++;
            }
        });
    }


    //--------------------------------------------------------------------
    private CanonHoleLookup() {}


    //--------------------------------------------------------------------
    public static CanonHoleDetail lookup(char canonHole)
    {
        return DETAILS[ canonHole ];
    }

    public static CanonHoleDetail[] lookup(
            char fromCanonHole,
            char canonHoleCount)
    {
        CanonHoleDetail[] details =
                new CanonHoleDetail[ canonHoleCount ];
        
        for (char i = 0; i < canonHoleCount; i++)
        {
            details[ i ] = lookup( (char)(fromCanonHole + i) );
        }

        return details;
    }
}
