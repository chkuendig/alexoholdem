package ao.bucket.index.river;

import ao.bucket.index.turn.TurnLookup;
import ao.util.persist.PersistentBytes;

/**
 * Date: Sep 16, 2008
 * Time: 3:53:13 PM
 */
public class RiverSparceLookup
{
    //--------------------------------------------------------------------
    private static final String DIR = "lookup/canon/";
    private static final String F_RAW_CASES =
                                    DIR + "river.cases.raw.cache";

    private static final int  SHRINK    = 4;
    private static final int  CHUNK     = (1 << (SHRINK));
    private static final byte CASES[]   = rawCases();
    private static final int  OFFSETS[] = computeOffsets();


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        rawCases();
//        compressRawCases();
        //computeEncoding();

        long size = 0;
        for (byte caseOrdinal : CASES)
        {
            size += RiverCaseSet.VALUES[ CASES[ caseOrdinal ] ].size();
        }
        System.out.println(size);
    }


    //--------------------------------------------------------------------
    public static RiverCaseSet caseSet(int canonTurn)
    {
        return RiverCaseSet.VALUES[ CASES[canonTurn] ];
    }


    //--------------------------------------------------------------------
    public static long offset(int canonTurn)
    {
        int  index = canonTurn / CHUNK;
        long base  = unsigned(OFFSETS[ index ]);

        int  addend = 0;
        int  delta  = canonTurn % CHUNK;
        for (int i = 1; i <= delta; i++)
        {
            addend += caseSet(index + i).size();
        }

        return base + addend;
    }
    private static int[] computeOffsets()
    {
        System.out.println("RiverSparceLookup.computeOffsets");
        int offset    = 0;
        int offsets[] =
                new int[ TurnLookup.CANON_TURN_COUNT / CHUNK + 1 ];

        int prevIndex = -1;
        for (int i = 0; i < TurnLookup.CANON_TURN_COUNT; i++)
        {
            if (i % 1000000 == 0)
            {
                System.out.print(".");
                System.out.flush();
            }

            int index = i / CHUNK;
            if (prevIndex != index)
            {
                offsets[ index ] = offset;
                prevIndex = index;
            }
            RiverCaseSet caseSet = caseSet(i);
            offset += caseSet.size();
        }

        System.out.println("\nRiverSparceLookup.computeOffsets end");
        return offsets;
    }

    private static long unsigned(int value)
    {
        return value >= 0
               ? value
               : (long)(Integer.MAX_VALUE + value) +
                 (long)(Integer.MAX_VALUE        ) + 2;
    }


    //--------------------------------------------------------------------
    private static byte[] rawCases()
    {
        System.out.println("RiverSparceLookup.rawCases");

        byte[] rawCases = PersistentBytes.retrieve(F_RAW_CASES);
        if (rawCases == null)
        {
            throw new Error("lookup missing");
        }

        System.out.println("RiverSparceLookup.rawCases end");
        return rawCases;
    }
}
