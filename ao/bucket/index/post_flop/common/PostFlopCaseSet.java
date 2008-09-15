package ao.bucket.index.post_flop.common;

import static ao.bucket.index.post_flop.common.PostFlopCase.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 3, 2008
 * Time: 10:15:33 PM
 */
public enum PostFlopCaseSet
{
    //--------------------------------------------------------------------
    //[ZERO, ONE, TWO]
    ZOT(ZERO, ONE, TWO),

    //[ZERO, ONE, THREE]
    ZOR(ZERO, ONE, THREE),

    //[ZERO, ONE, TWO, THREE]
    ZOTR(ZERO, ONE, TWO, THREE),

    //[ZERO, ONE, FOUR]
    ZOF(ZERO, ONE, FOUR),

    //[ZERO, ONE, FIVE]
    ZOV(ZERO, ONE, FIVE),

    //[ZERO, TWO]
    ZT(ZERO, TWO),

    //[ZERO, TWO, THREE]
    ZTR(ZERO, TWO, THREE),

    //[ZERO, TWO, FOUR]
    ZTF(ZERO, TWO, FOUR),

    //[ZERO, THREE]
    ZR(ZERO, THREE),

    //[ZERO, FIVE]
    ZV(ZERO, FIVE),

    //[ZERO, SIX]
    ZS(ZERO, SIX),

    //[ONE, TWO]
    OT(ONE, TWO),

    //[ONE, THREE]
    OR(ONE, THREE),
    ;

    public static PostFlopCaseSet VALUES[] = values();


    //--------------------------------------------------------------------
    public static PostFlopCaseSet valueOf(Set<PostFlopCase> cases)
    {
        for (PostFlopCaseSet riverCaseSet : VALUES)
        {
            if (riverCaseSet.RIVER_CASES.equals( cases ))
            {
                return riverCaseSet;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final Set<PostFlopCase> RIVER_CASES;
    private final int               SIZE;
    private final int               OFFSETS[];


    //--------------------------------------------------------------------
    private PostFlopCaseSet(PostFlopCase... riverCases)
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
        for (PostFlopCase riverCase : RIVER_CASES)
        {
            size += riverCase.size();
        }
        return size;
    }


    //--------------------------------------------------------------------
    public int offset(PostFlopCase of)
    {
        return OFFSETS[ of.ordinal() ];
    }
    private int[] computeOffset()
    {
        int offset    = 0;
        int offsets[] = new int[PostFlopCase.VALUES.length];
        for (PostFlopCase riverCase : RIVER_CASES)
        {
            offsets[ riverCase.ordinal() ] = offset;
            offset += riverCase.size();
        }
        return offsets;
    }
}
