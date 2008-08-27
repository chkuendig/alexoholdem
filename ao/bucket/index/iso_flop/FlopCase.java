package ao.bucket.index.iso_flop;

import ao.bucket.index.iso_cards.wild.suit.WildMarkedSuit;
import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import static ao.bucket.index.iso_case.IsoCaseUtils.offset;
import static ao.bucket.index.iso_case.IsoCaseUtils.sortColex;
import static ao.util.stats.Combo.colex;

/**
 * Date: Aug 14, 2008
 * Time: 3:41:50 AM
 */
public enum FlopCase
{
    //--------------------------------------------------------------------
    //[ONE_0, ONE_0][ONE, TWO, TWO]
    OO_OTT(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 11 +
                   (offset(holeA, holeB, flopA) + flopA);
    }},

    //[ONE_0, ONE_0][TWO, TWO, THREE]
    OO_TTR(1014) { public int subIndex(int holeA, int holeB,
                                       int flopA, int flopB, int flopC) {
            return sortColex(flopA, flopB) * 13 + flopC;
    }},


    //[ONE_0, ONE_0][TWO, TWO, TWO]
    OO_TTT(286) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA, flopB, flopC);
            //return colex(flopC, flopB, flopA);
        }
    },

    //[ONE_0, ONE_0][ONE, ONE, TWO]
    OO_OOT(715) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, holeB, flopA),
                         flopB + offset(holeA, holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //[ONE_0, ONE_0][ONE, TWO, THREE]
    OO_OTR(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 11 +
                   (flopA + offset(holeA, holeB, flopA));
        }
    },

    //[ONE_0, ONE_0][ONE, WILD, WILD]
    OO_OWW(143) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return (flopA + offset(holeA, holeB, flopA)) * 13 + flopB;
        }
    },

    //[ONE_0, ONE_0][THREE, WILD, WILD]
    OO_RWW(156) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return (flopB + offset(flopA, flopB)) * 13 +
                    flopA;
        }
    },

    //[ONE_0, ONE_0][TWO, THREE, FOUR]
    OO_TRF(286) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopC, flopB, flopA);
        }
    },

    //[ONE_0, ONE_0][WILD, WILD, WILD]
    OO_WWW(13) { public int subIndex(int holeA, int holeB,
                                     int flopA, int flopB, int flopC) {
            return flopA;
        }
    },

    //[ONE_0, ONE_0][ONE, ONE, ONE]
    OO_OOO(165) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, holeB, flopA),
                         flopB + offset(holeA, holeB, flopB),
                         flopC + offset(holeA, holeB, flopC));
        }
    },

    //[ONE_0, TWO_1][ONE, ONE, THREE]
    P_OT_OOR(858) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 13 + flopC;
        }
    },

    //[ONE_0, TWO_1][ONE, THREE, THREE]
    P_OT_ORR(936) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //[WILD_0, WILD_1][TWO, WILD, WILD]
    P_WW_TWW(156) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return (flopB + offset(holeA, flopB)) * 13 +
                    flopA;
        }
    },

    //[ONE_0, TWO_1][ONE, TWO, THREE]
    P_OT_OTR(858) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return sortColex(flopA + offset(holeA, flopA),
                             flopB + offset(holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //[ONE_0, TWO_1][ONE, THREE, FOUR]
    P_OT_ORF(936) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //[ONE_0, TWO_1][ONE, WILD, WILD]
    P_OT_OWW(156) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return (flopA + offset(holeA, flopA)) * 13 +
                   flopB;
        }
    },

    //[WILD_0, WILD_1][TWO, TWO, THREE]
    P_WW_TTR(1014) { public int subIndex(int holeA, int holeB,
                                         int flopA, int flopB, int flopC) {
            return sortColex(flopA, flopB) * 13 + flopC;
        }
    },

    //[WILD_0, WILD_1][TWO, TWO, TWO]
    P_WW_TTT(286) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA, flopB, flopC);
        }
    },

    //[ONE_0, TWO_1][ONE, ONE, ONE]
    P_OT_OOO(220) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB),
                         flopC + offset(holeA, flopC));
        }
    },

    //[ONE_0, TWO_1][ONE, ONE, TWO]
    P_OT_OOT(792) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 12 +
                   (flopC + offset(holeB, flopC));
        }
    },

    //[ONE_0, TWO_0][TWO, TWO, THREE]
    OT_TTR(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopA + offset(holeB, flopA),
                             flopB + offset(holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //[ONE_0, TWO_0][ONE, TWO, THREE]
    OT_OTR(871) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopA + offset(holeA, flopA),
                             flopB + offset(holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //[ONE_0, TWO_0][TWO, THREE, THREE]
    OT_TRR(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopB, flopC) * 12 +
                   (flopA + offset(holeB, flopA));
        }
    },

    //[ONE_0, TWO_0][TWO, THREE, FOUR]
    OT_TRF(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 12 +
                   (flopA + offset(holeB, flopA));
        }
    },

    //[ONE_0, TWO_0][TWO, WILD, WILD]
    OT_TWW(156) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return flopB * 12 +
                   (flopA + offset(holeB, flopA));
        }
    },

    //[ONE_0, TWO_0][ONE, THREE, THREE]
    OT_ORR(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //[ONE_0, TWO_0][ONE, ONE, THREE]
    OT_OOR(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 13 + flopC;
        }
    },

    //[ONE_0, TWO_0][ONE, THREE, FOUR]
    OT_ORF(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //[ONE_0, TWO_0][ONE, WILD, WILD]
    OT_OWW(156) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return (flopA + offset(holeA, flopA)) * 13 + flopB;
        }
    },

    //[ONE_0, TWO_0][THREE, THREE, FOUR]
    OT_RRF(1014) { public int subIndex(int holeA, int holeB,
                                       int flopA, int flopB, int flopC) {
            return colex(flopA, flopB) * 13 + flopC;
        }
    },

    //[ONE_0, TWO_0][THREE, THREE, THREE]
    OT_RRR(286) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA, flopB, flopC);
        }
    },

    //[ONE_0, TWO_0][TWO, TWO, TWO]
    OT_TTT(220) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeB, flopA),
                         flopB + offset(holeB, flopB),
                         flopC + offset(holeB, flopC));
        }
    },

    //[ONE_0, TWO_0][ONE, TWO, TWO]
    OT_OTT(792) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopB + offset(holeB, flopB),
                         flopC + offset(holeB, flopC)) * 12 +
                    (flopA + offset(holeA, flopA));
        }
    },

    //[ONE_0, TWO_0][ONE, ONE, TWO]
    OT_OOT(792) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 12 +
                    (flopC + offset(holeB, flopC));
        }
    },

    //[ONE_0, TWO_0][ONE, ONE, ONE]
    OT_OOO(220) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB),
                         flopC + offset(holeA, flopC));
        }
    };

    public static final FlopCase VALUES[] = values();


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
    private final int SIZE;

    private FlopCase(int size)
    {
        SIZE = size;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SIZE;
    }


    //--------------------------------------------------------------------
    public abstract int subIndex(
            int holeA, int holeB,
            int flopA, int flopB, int flopC);
}
