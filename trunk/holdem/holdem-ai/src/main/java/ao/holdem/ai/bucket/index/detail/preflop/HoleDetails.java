package ao.holdem.ai.bucket.index.detail.preflop;

import ao.holdem.model.canon.flop.Flop;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.ai.bucket.index.detail.preflop.CanonHoleDetail.Buffer;
import ao.holdem.model.enumeration.HandEnum;
import ao.holdem.model.enumeration.PermisiveFilter;
import ao.holdem.model.enumeration.UniqueFilter;
import ao.util.pass.Filter;
import ao.util.pass.Filters;
import ao.util.pass.Traverser;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Date: Jan 9, 2009
 * Time: 12:30:51 PM
 */
public class HoleDetails
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(HoleDetails.class);


    //--------------------------------------------------------------------
    private static final CanonHoleDetail[] DETAILS =
            retrieveOrComputeDetails();

    //--------------------------------------------------------------------
    public static void load()
    {
        LOG.debug("Loaded (" + DETAILS.length + ") details.");
    }


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonHoleDetail[] details = HoleDetailDao.retrieveDetails();
        if (details == null)
        {
            details = computeDetails();
            HoleDetailDao.persistDetails(details);
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] computeDetails()
    {
        LOG.debug("computing details");
        final CanonHoleDetail.Buffer[] buffers =
                new CanonHoleDetail.Buffer[CanonHole.CANONS];

        HandEnum.holes(
                new PermisiveFilter<CanonHole>(),
                new Traverser<CanonHole>() {
            public void traverse(CanonHole canonHole) {
                Buffer buff = buffers[ canonHole.canonIndex() ];
                if (buff == null)
                {
                    LOG.info( canonHole.reify() );
                    buff = new Buffer( canonHole.reify() );
                    buffers[ canonHole.canonIndex() ] = buff;
                }
                buff.REPRESENTS++;
            }});

        CanonHoleDetail[] details =
                new CanonHoleDetail[CanonHole.CANONS];
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
        final int filterIndex = buff.HOLE.asCanon().canonIndex();
        HandEnum.flops(Filters.and(
                new Filter<CanonHole>() {
                    public boolean accept(CanonHole canonHole) {
                        return canonHole.canonIndex() == filterIndex;
                    }},
                new UniqueFilter<CanonHole>()),
            new UniqueFilter<Flop>(),
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
            }
        );
    }


    //--------------------------------------------------------------------
    private HoleDetails() {}


    //--------------------------------------------------------------------
    public static CanonHoleDetail lookup(char canonHole)
    {
        return DETAILS[ canonHole ];
    }

    public static CanonHoleDetail[] lookup(
            char fromCanonHole,
            char canonHoleCount)
    {
        return Arrays.copyOfRange(DETAILS,
                                  fromCanonHole,
                                  fromCanonHole + canonHoleCount);
    }
}
