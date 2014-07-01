package ao.holdem.canon.river;

import ao.Infrastructure;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.canon.turn.Turn;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.canon.enumeration.PermisiveFilter;
import ao.holdem.canon.enumeration.UniqueFilter;
import ao.util.pass.Traverser;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 4, 2008
 * Time: 12:40:23 PM
 */
/*package-private*/ class RiverRawLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(RiverRawLookup.class);

    private static final String DIR =
            Infrastructure.path("lookup/canon/");
    private static final String F_RAW_CASES =
                                    DIR + "river.cases.raw.cache";

    private static final byte   CASES[] = rawCases();


    //--------------------------------------------------------------------
    private static byte[] rawCases()
    {
        LOG.info("rawCases");

        byte[] rawCases = PersistentBytes.retrieve(F_RAW_CASES);
        if (rawCases == null)
        {
            rawCases = computeRawCases();
            PersistentBytes.persist(rawCases, F_RAW_CASES);
        }
        return rawCases;
    }
    private static byte[] computeRawCases()
    {
        final int[]          prevTurn   = {-1};
        final Set<RiverCase> caseBuffer =
                EnumSet.noneOf( RiverCase.class );
        final byte[]         riverCases =
                new byte[ Turn.CANONS];

        HandEnum.rivers(
                new UniqueFilter<CanonHole>("%1$s"),
                new UniqueFilter<Flop>(),
                new UniqueFilter<Turn>(),
                new PermisiveFilter<River>(),
                new Traverser<River>() {
            public void traverse(River river) {
                if (prevTurn[0] != river.turn().canonIndex())
                {
                    if (prevTurn[0] != -1) {
                        riverCases[ prevTurn[0] ] = (byte) RiverCaseSet
                                .valueOf( caseBuffer ).ordinal();
                    }

                    caseBuffer.clear();
                    prevTurn[0] = river.turn().canonIndex();
                }
                caseBuffer.add( river.riverCase() );
            }});

        riverCases[ prevTurn[0] ] =
                (byte) RiverCaseSet.valueOf( caseBuffer ).ordinal();

        return riverCases;
    }

    
    //--------------------------------------------------------------------
    public static RiverCaseSet caseSet(int canonTurn)
    {
        return RiverCaseSet.VALUES[ CASES[canonTurn] ];
    }
}
