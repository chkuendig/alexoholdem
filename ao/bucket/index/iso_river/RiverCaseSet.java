package ao.bucket.index.iso_river;

import static ao.bucket.index.iso_river.RiverCase.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 3, 2008
 * Time: 10:15:33 PM
 */
public enum RiverCaseSet
{
    //--------------------------------------------------------------------
    //[ZERO, ONE, TWO, THREE]
    ZOTR(ZERO, ONE, TWO, THREE),

    //[ZERO, ONE, FOUR]
    ZOF(ZERO, ONE, FOUR),

    //[ZERO, ONE, FIVE]
    ZOI(ZERO, ONE, FIVE),

    //[ZERO, TWO]
    ZT(ZERO, TWO),

    //[ZERO, TWO, FOUR]
    ZTF(ZERO, TWO, FOUR),

    //[ZERO, THREE]
    ZR(ZERO, THREE),

    //[ZERO, SIX]
    ZS(ZERO, SIX),

    //[ONE, TWO]
    OT(ONE, TWO),

    //[ONE, THREE]
    OR(ONE, THREE),
    ;

    public static RiverCaseSet VALUES[] = values();


    //--------------------------------------------------------------------
    public static RiverCaseSet valueOf(Set<RiverCase> cases)
    {
        for (RiverCaseSet riverCaseSet : VALUES)
        {
            if (riverCaseSet.RIVER_CASES.equals( cases ))
            {
                return riverCaseSet;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final Set<RiverCase> RIVER_CASES;
    private final int            SIZE;
    private final int            OFFSETS[];


    //--------------------------------------------------------------------
    private RiverCaseSet(RiverCase... riverCases)
    {
        RIVER_CASES = EnumSet.copyOf(Arrays.asList(riverCases));
        SIZE        = computeSize();
        OFFSETS     = computeOffset();
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SIZE;
    }
    private int computeSize()
    {
        int size = 0;
        for (RiverCase riverCase : RIVER_CASES)
        {
            size += riverCase.size();
        }
        return size;
    }


    //--------------------------------------------------------------------
    public int offset(RiverCase of)
    {
        return OFFSETS[ of.ordinal() ];
    }
    public int[] computeOffset()
    {
        int offset    = 0;
        int offsets[] = new int[RiverCase.VALUES.length];
        for (RiverCase riverCase : RIVER_CASES)
        {
            offsets[ riverCase.ordinal() ] = offset;
            offset += riverCase.size();
        }
        return offsets;
    }
}
