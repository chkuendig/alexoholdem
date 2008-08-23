package ao.bucket.index.iso_case;

import ao.bucket.index.iso_cards.WildMarkedSuit;
import ao.bucket.index.iso_cards.WildSuit;

/**
 * Date: Aug 14, 2008
 * Time: 3:41:50 AM
 */
public class FlopCase
{
    //--------------------------------------------------------------------
    //[ONE_0, ONE_0][ONE, TWO, TWO]
    public static FlopCase OO_OTT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.ONE, WildSuit.TWO, WildSuit.TWO);

    //[ONE_0, ONE_0][TWO, TWO, THREE]
    public static FlopCase OO_TTR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.TWO, WildSuit.TWO, WildSuit.THREE);

    //[ONE_0, ONE_0][TWO, TWO, TWO]
    public static FlopCase OO_TTT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.TWO, WildSuit.TWO, WildSuit.TWO);

    //[ONE_0, ONE_0][ONE, ONE, TWO]
    public static FlopCase OO_OOT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.TWO);

    //[ONE_0, ONE_0][ONE, TWO, THREE]
    public static FlopCase OO_OTR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.ONE, WildSuit.TWO, WildSuit.THREE);

    //[ONE_0, ONE_0][ONE, WILD, WILD]
    public static FlopCase OO_OWW =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.ONE, WildSuit.WILD, WildSuit.WILD);

    //[ONE_0, ONE_0][THREE, WILD, WILD]
    public static FlopCase OO_RWW =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.THREE, WildSuit.WILD, WildSuit.WILD);

    //[ONE_0, ONE_0][TWO, THREE, FOUR]
    public static FlopCase OO_TRF =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.TWO, WildSuit.THREE, WildSuit.FOUR);

    //[ONE_0, ONE_0][WILD, WILD, WILD]
    public static FlopCase OO_WWW =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.WILD, WildSuit.WILD, WildSuit.WILD);

    //[ONE_0, ONE_0][ONE, ONE, ONE]
    public static FlopCase OO_OOO =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.ONE_0,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.ONE);

    //[ONE_0, TWO_1][ONE, ONE, THREE]
    public static FlopCase P_OT_OOR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.THREE);

    //[ONE_0, TWO_1][ONE, THREE, THREE]
    public static FlopCase P_OT_ORR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.THREE, WildSuit.THREE);

    //[WILD_0, WILD_1][TWO, WILD, WILD]
    public static FlopCase P_WW_TWW =
            new FlopCase(WildMarkedSuit.WILD_0, WildMarkedSuit.WILD_1,
                         WildSuit.TWO, WildSuit.WILD, WildSuit.WILD);

    //[ONE_0, TWO_1][ONE, TWO, THREE]
    public static FlopCase P_OT_OTR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.TWO, WildSuit.THREE);

    //[ONE_0, TWO_1][ONE, THREE, FOUR]
    public static FlopCase P_OT_ORF =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.THREE, WildSuit.FOUR);

    //[ONE_0, TWO_1][ONE, WILD, WILD]
    public static FlopCase P_OT_OWW =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.WILD, WildSuit.WILD);

    //[WILD_0, WILD_1][TWO, TWO, THREE]
    public static FlopCase P_WW_TTR =
            new FlopCase(WildMarkedSuit.WILD_0, WildMarkedSuit.WILD_1,
                         WildSuit.TWO, WildSuit.TWO, WildSuit.THREE);

    //[WILD_0, WILD_1][TWO, TWO, TWO]
    public static FlopCase P_WW_TTT =
            new FlopCase(WildMarkedSuit.WILD_0, WildMarkedSuit.WILD_1,
                         WildSuit.TWO, WildSuit.TWO, WildSuit.TWO);

    //[ONE_0, TWO_1][ONE, ONE, ONE]
    public static FlopCase P_OT_OOO =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.ONE);

    //[ONE_0, TWO_1][ONE, ONE, TWO]
    public static FlopCase P_OT_OOT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_1,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.TWO);

    //[ONE_0, TWO_0][TWO, TWO, THREE]
    public static FlopCase OT_TTR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.TWO, WildSuit.TWO, WildSuit.THREE);

    //[ONE_0, TWO_0][ONE, TWO, THREE]
    public static FlopCase OT_OTR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.TWO, WildSuit.THREE);

    //[ONE_0, TWO_0][TWO, THREE, THREE]
    public static FlopCase OT_TRR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.THREE, WildSuit.THREE);

    //[ONE_0, TWO_0][TWO, THREE, FOUR]
    public static FlopCase OT_TRF =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.TWO, WildSuit.THREE, WildSuit.FOUR);

    //[ONE_0, TWO_0][TWO, WILD, WILD]
    public static FlopCase OT_TWW =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.TWO, WildSuit.WILD, WildSuit.WILD);

    //[ONE_0, TWO_0][ONE, THREE, THREE]
    public static FlopCase OT_ORR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.THREE, WildSuit.THREE);

    //[ONE_0, TWO_0][ONE, ONE, THREE]
    public static FlopCase OT_OOR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.THREE);

    //[ONE_0, TWO_0][ONE, THREE, FOUR]
    public static FlopCase OT_ORF =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.THREE, WildSuit.FOUR);

    //[ONE_0, TWO_0][ONE, WILD, WILD]
    public static FlopCase OT_OWW =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.WILD, WildSuit.WILD);

    //[ONE_0, TWO_0][THREE, THREE, FOUR]
    public static FlopCase OT_RRF =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.THREE, WildSuit.THREE, WildSuit.FOUR);

    //[ONE_0, TWO_0][THREE, THREE, THREE]
    public static FlopCase OT_RRR =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.THREE, WildSuit.THREE, WildSuit.THREE);

    //[ONE_0, TWO_0][TWO, TWO, TWO]
    public static FlopCase OT_TTT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.TWO, WildSuit.TWO, WildSuit.TWO);

    //[ONE_0, TWO_0][ONE, TWO, TWO]
    public static FlopCase OT_OTT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.TWO, WildSuit.TWO);

    //[ONE_0, TWO_0][ONE, ONE, TWO]
    public static FlopCase OT_OOT =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.TWO);

    //[ONE_0, TWO_0][ONE, ONE, ONE]
    public static FlopCase OT_OOO =
            new FlopCase(WildMarkedSuit.ONE_0, WildMarkedSuit.TWO_0,
                         WildSuit.ONE, WildSuit.ONE, WildSuit.ONE);

    
    //--------------------------------------------------------------------
    public static FlopCase newInstance(
            WildMarkedSuit holeA, WildMarkedSuit holeB,
            WildSuit flopA, WildSuit flopB, WildSuit flopC)
    {
        if (holeA == WildMarkedSuit.ONE_0)
        {
            if (holeB == WildMarkedSuit.ONE_0)
            {
                switch (flopA)
                {
                    case ONE:
                        switch (flopB)
                        {
                            case ONE:
                                return (flopC == WildSuit.ONE)
                                       ? OO_OOO
                                       : OO_OOT;
                            case TWO:
                                return (flopC == WildSuit.TWO)
                                       ? OO_OTT
                                       : OO_OTR;
                            default:
                                return OO_OWW;
                        }
                    case TWO:
                        return (flopC == WildSuit.TWO)
                               ? OO_TTT
                               : (flopC == WildSuit.THREE)
                                 ? OO_TTR
                                 : OO_TRF;
                    default:
                        return (flopA == WildSuit.THREE)
                               ? OO_RWW : OO_WWW;
                }
            }
            else if (holeB == WildMarkedSuit.TWO_0)
            {
                switch (flopA)
                {
                    case ONE:
                        switch (flopB)
                        {
                            case ONE:
                                return (flopC == WildSuit.ONE)
                                       ? OT_OOO
                                       : (flopC == WildSuit.TWO)
                                         ? OT_OOT
                                         : OT_OOR;
                            case TWO:
                                return (flopC == WildSuit.TWO)
                                       ? OT_OTT
                                       : OT_OTR;
                            case THREE:
                                return (flopC == WildSuit.THREE)
                                       ? OT_ORR
                                       : OT_ORF;
                            default:
                                return OT_OWW;
                        }
                    case TWO:
                        switch (flopB)
                        {
                            case TWO:
                                return (flopC == WildSuit.TWO)
                                       ? OT_TTT
                                       : OT_TTR;
                            case THREE:
                                return (flopC == WildSuit.THREE)
                                       ? OT_TRR
                                       : OT_TRF;
                            default:
                                return OT_TWW;
                        }
                    default:
                        return (flopC == WildSuit.THREE)
                               ? OT_RRR
                               : OT_RRF;
                }
            }
            else //if (holeB == WildMarkedSuit.TWO_1)
            {
                switch (flopB)
                {
                    case ONE:
                        return (flopC == WildSuit.ONE)
                               ? P_OT_OOO
                               : (flopC == WildSuit.TWO)
                                 ? P_OT_OOT
                                 : P_OT_OOR;
                    case THREE:
                        return (flopC == WildSuit.FOUR)
                               ? P_OT_ORF
                               : P_OT_ORR;
                    default:
                        return (flopB == WildSuit.TWO)
                               ? P_OT_OTR
                               : P_OT_OWW;
                }
            }
        }
        else // if (holeA == WildMarkedSuit.WILD_0)
        {
            return (flopC == WildSuit.TWO)
                    ? P_WW_TTT
                    : (flopC == WildSuit.THREE)
                       ? P_WW_TTR
                       : P_WW_TWW;
        }
    }


    //--------------------------------------------------------------------
    private final WildMarkedSuit HOLE_A, HOLE_B;
    private final WildSuit       FLOP_A, FLOP_B, FLOP_C;


    //--------------------------------------------------------------------
    private FlopCase(WildMarkedSuit holeA, WildMarkedSuit holeB,
                     WildSuit flopA, WildSuit flopB, WildSuit flopC)
    {
        HOLE_A = holeA;
        HOLE_B = holeB;
        FLOP_A = flopA;
        FLOP_B = flopB;
        FLOP_C = flopC;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "[" + HOLE_A + ", " + HOLE_B + "]" +
               "[" + FLOP_A + ", " + FLOP_B + ", " + FLOP_C +"]";
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlopCase flopCase = (FlopCase) o;
        return HOLE_A == flopCase.HOLE_A &&
               HOLE_B == flopCase.HOLE_B &&
               FLOP_A == flopCase.FLOP_A &&
               FLOP_B == flopCase.FLOP_B &&
               FLOP_C == flopCase.FLOP_C;
    }

    public int hashCode()
    {
        int result = 0;
        result = 31 * result + HOLE_A.hashCode();
        result = 31 * result + HOLE_B.hashCode();
        result = 31 * result + FLOP_A.hashCode();
        result = 31 * result + FLOP_B.hashCode();
        result = 31 * result + FLOP_C.hashCode();
        return result;
    }
}
