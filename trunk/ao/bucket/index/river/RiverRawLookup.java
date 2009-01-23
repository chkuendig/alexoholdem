package ao.bucket.index.river;

import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.enumeration.PermisiveFilter;
import ao.bucket.index.enumeration.UniqueFilter;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 4, 2008
 * Time: 12:40:23 PM
 */
public class RiverRawLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(RiverRawLookup.class);

    private static final String DIR = "lookup/canon/";
    private static final String F_RAW_CASES =
                                    DIR + "river.cases.raw.cache";

    private static final byte   CASES[] = rawCases();


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        rawCases();
//        compressRawCases();
        //computeEncoding();

        long size = 0;
        for (byte caseOrdinal : CASES)
        {
            size += RiverCaseSet.VALUES[ caseOrdinal ].size();
        }
        System.out.println(size);
    }


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
                new byte[ TurnLookup.CANONS];

        CardEnum.traverseRivers(
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
