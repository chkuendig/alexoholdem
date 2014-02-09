package ao.holdem.canon.river;

import static ao.holdem.canon.river.RiverCase.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 16, 2008
 * Time: 8:43:27 AM
 *
 * there are 37 RiverCaseSets
 */
/*package-private*/ enum RiverCaseSet
{
    //--------------------------------------------------------------------
    F1_S1_T2_R2(F1, S1, T2, R2),
    F1_S1_T3_R1(F1, S1, T3, R1),
    F1_S1_T4_R0(F1, S1, T4, R0),
    F1_S2_T1_R2(F1, S2, T1, R2),
    F1_S2_T2   (F1, S2, T2    ),
    F1_S2_T2_R1(F1, S2, T2, R1),
    F1_S2_T3_R0(F1, S2, T3, R0),
    F1_S3_T1   (F1, S3, T1    ),
    F1_S3_T1_R1(F1, S3, T1, R1),
    F1_S3_T2_R0(F1, S3, T2, R0),
    F1_S4_T0   (F1, S4, T0    ),
    F1_S4_T0_R0(F1, S4, T0, R0),
    F1_S4_T1_R0(F1, S4, T1, R0),
    F1_S5_T0   (F1, S5, T0    ),

    F2_S1_T1   (F2, S1, T1    ),
    F2_S1_T1_R2(F2, S1, T1, R2),
    F2_S1_T2   (F2, S1, T2    ),
    F2_S1_T2_R1(F2, S1, T2, R1),
    F2_S1_T3_R0(F2, S1, T3, R0),
    F2_S2_T0   (F2, S2, T0    ),
    F2_S2_T1   (F2, S2, T1    ),
    F2_S2_T1_R1(F2, S2, T1, R1),
    F2_S2_T2_R0(F2, S2, T2, R0),
    F2_S3_T1_R0(F2, S3, T1, R0),
    F2_S4_T0   (F2, S4, T0    ),
    F2_S4_T0_R0(F2, S4, T0, R0),

    F3_S1      (F3, S1        ),
    F3_S1_T1   (F3, S1, T1    ),
    F3_S1_T2_R0(F3, S1, T2, R0),
    F3_S1_T1_R1(F3, S1, T1, R1),
    F3_S2_T1_R0(F3, S2, T1, R0),
    F3_S3_T0   (F3, S3, T0    ),

    F4_S1_T1_R0(F4, S1, T1, R0),
    F4_S1_T0   (F4, S1, T0    ),
    F4_S2_T0   (F4, S2, T0    ),

    F5_S1_T0   (F5, S1, T0    ),

    F6_S0      (F6, S0        ),
    ;

    public static final RiverCaseSet VALUES[] = values();


    //--------------------------------------------------------------------
    public static RiverCaseSet valueOf(Set<RiverCase> cases)
    {
        for (RiverCaseSet caseSet : VALUES)
        {
            if (caseSet.CASES.equals(cases))
            {
                return caseSet;
            }
        }
        System.err.println("unable to locate: " + cases);
        return null;
    }



    //--------------------------------------------------------------------
    private final EnumSet<RiverCase> CASES;
    private final int                SIZE;
    private final int                OFFSETS[];


    //--------------------------------------------------------------------
    private RiverCaseSet(RiverCase first, RiverCase... rest)
    {
        CASES   = EnumSet.of(first, rest);
        SIZE    = computeSize();
        OFFSETS = computeOffsets();
    }


    //--------------------------------------------------------------------
    public int offsetOf(RiverCase riverCase)
    {
        return OFFSETS[ riverCase.ordinal() ];
    }
    private int[] computeOffsets()
    {
        int offset    = 0;
        int offsets[] = new int[ RiverCase.VALUES.length ];
        Arrays.fill(offsets, -1);

        for (RiverCase riverCase : CASES)
        {
            offsets[ riverCase.ordinal() ] = offset;
            offset += riverCase.size();
        }
        return offsets;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SIZE;
    }
    private int computeSize()
    {
        int size = 0;
        for (RiverCase riverCase : CASES)
            size += riverCase.size();
        return size;
    }
}
