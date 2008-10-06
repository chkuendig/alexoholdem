package ao.bucket.index.river;

import ao.bucket.index.turn.TurnLookup;

/**
 * Date: Sep 16, 2008
 * Time: 3:53:13 PM
 */
public class RiverLookup
{
    //--------------------------------------------------------------------
    private static final int  SHRINK    = 3;
    private static final int  CHUNK     = (1 << (SHRINK));
    private static final int  OFFSETS[] = computeOffsets();


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        offset(55190537);
    }


    //--------------------------------------------------------------------
    public static long offset(int canonTurn)
    {
        int  index = canonTurn / CHUNK;
        long base  = RiverUtil.unsigned(OFFSETS[ index ]);

        int  addend = 0;
        int  delta  = canonTurn % CHUNK;
        for (int i = 0; i < delta; i++)
        {
            addend += RiverRawLookup.caseSet(index * CHUNK + i).size();
        }

        return base + addend;
    }


    //--------------------------------------------------------------------
    private static int[] computeOffsets()
    {
        System.out.println("RiverSparceLookup.computeOffsets");
        int offset    = 0;
        int offsets[] =
                new int[ TurnLookup.CANON_TURN_COUNT / CHUNK + 1 ];

        int prevIndex = -1;
        for (int i = 0; i < TurnLookup.CANON_TURN_COUNT; i++)
        {
            if (i % 1000000 == 1)
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
            RiverCaseSet caseSet = RiverRawLookup.caseSet(i);
            offset += caseSet.size();
        }

        System.out.println("\nRiverSparceLookup.computeOffsets end");
        return offsets;
    }
}
