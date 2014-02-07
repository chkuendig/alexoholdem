package ao.holdem.model.canon.flop;

import ao.holdem.model.canon.card.CanonSuit;

import static ao.holdem.model.canon.flop.FlopUtil.offset;
import static ao.holdem.model.canon.flop.FlopUtil.sortColex;
import static ao.util.math.stats.Combo.colex;

/**
 * Date: Aug 14, 2008
 * Time: 3:41:50 AM
 */
/*package-private*/ enum FlopCase
{
    //--------------------------------------------------------------------
    //[ONE, ONE][ONE, TWO, TWO]
    OO_OTT(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 11 +
                   (offset(holeA, holeB, flopA) + flopA);
    }},

    //[ONE, ONE][TWO, TWO, THREE]
    OO_TTR(1014) { public int subIndex(int holeA, int holeB,
                                       int flopA, int flopB, int flopC) {
            return sortColex(flopA, flopB) * 13 + flopC;
    }},


    //[ONE, ONE][TWO, TWO, TWO]
    OO_TTT(286) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA, flopB, flopC);
            //return colex(flopC, flopB, flopA);
        }
    },

    //[ONE, ONE][ONE, ONE, TWO]
    OO_OOT(715) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, holeB, flopA),
                         flopB + offset(holeA, holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //[ONE, ONE][ONE, TWO, THREE]
    OO_OTR(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 11 +
                   (flopA + offset(holeA, holeB, flopA));
        }
    },

    //[ONE, ONE][ONE, WILD, WILD]
    OO_OWW(143) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return (flopA + offset(holeA, holeB, flopA)) * 13 + flopB;
        }
    },

    //[ONE, ONE][THREE, WILD, WILD]
    OO_RWW(156) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return (flopB + offset(flopA, flopB)) * 13 +
                    flopA;
        }
    },

    //[ONE, ONE][TWO, THREE, FOUR]
    OO_TRF(286) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopC, flopB, flopA);
        }
    },

    //[ONE, ONE][WILD, WILD, WILD]
    OO_WWW(13) { public int subIndex(int holeA, int holeB,
                                     int flopA, int flopB, int flopC) {
            return flopA;
        }
    },

    //[ONE, ONE][ONE, ONE, ONE]
    OO_OOO(165) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, holeB, flopA),
                         flopB + offset(holeA, holeB, flopB),
                         flopC + offset(holeA, holeB, flopC));
        }
    },

    //pair [ONE, TWO][ONE, ONE, THREE]
    P_OT_OOR(858) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 13 + flopC;
        }
    },

    //pair [ONE, TWO][ONE, THREE, THREE]
    P_OT_ORR(936) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //pair [WILD, WILD][TWO, WILD, WILD]
    P_WW_TWW(156) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return (flopB + offset(holeA, flopB)) * 13 +
                    flopA;
        }
    },

    //pair [ONE, TWO][ONE, TWO, THREE]
    P_OT_OTR(858) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return sortColex(flopA + offset(holeA, flopA),
                             flopB + offset(holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //pair [ONE, TWO][ONE, THREE, FOUR]
    P_OT_ORF(936) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //pair [ONE, TWO][ONE, WILD, WILD]
    P_OT_OWW(156) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return (flopA + offset(holeA, flopA)) * 13 +
                   flopB;
        }
    },

    //pair [WILD, WILD][TWO, TWO, THREE]
    P_WW_TTR(1014) { public int subIndex(int holeA, int holeB,
                                         int flopA, int flopB, int flopC) {
            return sortColex(flopA, flopB) * 13 + flopC;
        }
    },

    //pair [WILD, WILD][TWO, TWO, TWO]
    P_WW_TTT(286) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA, flopB, flopC);
        }
    },

    //pair [ONE, TWO][ONE, ONE, ONE]
    P_OT_OOO(220) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB),
                         flopC + offset(holeA, flopC));
        }
    },

    //pair [ONE, TWO][ONE, ONE, TWO]
    P_OT_OOT(792) { public int subIndex(int holeA, int holeB,
                                        int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 12 +
                   (flopC + offset(holeB, flopC));
        }
    },

    //[ONE, TWO][TWO, TWO, THREE]
    OT_TTR(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopA + offset(holeB, flopA),
                             flopB + offset(holeB, flopB)) * 13 +
                   flopC;
        }
    },

    //[ONE, TWO][ONE, TWO, THREE]
    OT_OTR(1872) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return ((flopA + offset(holeA, flopA))  * 12 +
                    (flopB + offset(holeB, flopB))) * 13 + flopC;
//            return sortColex(flopA + offset(holeA, flopA),
//                             flopB + offset(holeB, flopB)) * 13 +
//                   flopC;
        }                    
    },

    //[ONE, TWO][TWO, THREE, THREE]
    OT_TRR(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopB, flopC) * 12 +
                   (flopA + offset(holeB, flopA));
        }
    },

    //[ONE, TWO][TWO, THREE, FOUR]
    OT_TRF(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 12 +
                   (flopA + offset(holeB, flopA));
        }
    },

    //[ONE, TWO][TWO, WILD, WILD]
    OT_TWW(156) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return flopB * 12 +
                   (flopA + offset(holeB, flopA));
        }
    },

    //[ONE, TWO][ONE, THREE, THREE]
    OT_ORR(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //[ONE, TWO][ONE, ONE, THREE]
    OT_OOR(858) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 13 + flopC;
        }
    },

    //[ONE, TWO][ONE, THREE, FOUR]
    OT_ORF(936) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return sortColex(flopB, flopC) * 12 +
                   (flopA + offset(holeA, flopA));
        }
    },

    //[ONE, TWO][ONE, WILD, WILD]
    OT_OWW(156) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return (flopA + offset(holeA, flopA)) * 13 + flopB;
        }
    },

    //[ONE, TWO][THREE, THREE, FOUR]
    OT_RRF(1014) { public int subIndex(int holeA, int holeB,
                                       int flopA, int flopB, int flopC) {
            return colex(flopA, flopB) * 13 + flopC;
        }
    },

    //[ONE, TWO][THREE, THREE, THREE]
    OT_RRR(286) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA, flopB, flopC);
        }
    },

    //[ONE, TWO][TWO, TWO, TWO]
    OT_TTT(220) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeB, flopA),
                         flopB + offset(holeB, flopB),
                         flopC + offset(holeB, flopC));
        }
    },

    //[ONE, TWO][ONE, TWO, TWO]
    OT_OTT(792) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopB + offset(holeB, flopB),
                         flopC + offset(holeB, flopC)) * 12 +
                    (flopA + offset(holeA, flopA));
        }
    },

    //[ONE, TWO][ONE, ONE, TWO]
    OT_OOT(792) { public int subIndex(int holeA, int holeB,
                                      int flopA, int flopB, int flopC) {
            return colex(flopA + offset(holeA, flopA),
                         flopB + offset(holeA, flopB)) * 12 +
                    (flopC + offset(holeB, flopC));
        }
    },

    //[ONE, TWO][ONE, ONE, ONE]
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
            boolean  isHolePair,
            CanonSuit holeA, CanonSuit holeB,
            CanonSuit flopA, CanonSuit flopB, CanonSuit flopC)
    {
        if (holeA == CanonSuit.FIRST)
        {
            if (holeB == CanonSuit.FIRST)
            {
                switch (flopA)
                {
                    case FIRST:
                        switch (flopB)
                        {
                            case FIRST:
                                return (flopC == CanonSuit.FIRST)
                                       ? OO_OOO
                                       : OO_OOT;
                            case SECOND:
                                return (flopC == CanonSuit.SECOND)
                                       ? OO_OTT
                                       : OO_OTR;
                            default:
                                return OO_OWW;
                        }
                    case SECOND:
                        return (flopC == CanonSuit.SECOND)
                               ? OO_TTT
                               : (flopC == CanonSuit.THIRD)
                                 ? OO_TTR
                                 : OO_TRF;
                    default:
                        return (flopA == CanonSuit.THIRD)
                               ? OO_RWW : OO_WWW;
                }
            }
            else if (! isHolePair) // && holeB == CanonSuit.SECOND
            {
                switch (flopA)
                {
                    case FIRST:
                        switch (flopB)
                        {
                            case FIRST:
                                return (flopC == CanonSuit.FIRST)
                                       ? OT_OOO
                                       : (flopC == CanonSuit.SECOND)
                                         ? OT_OOT
                                         : OT_OOR;
                            case SECOND:
                                return (flopC == CanonSuit.SECOND)
                                       ? OT_OTT
                                       : OT_OTR;
                            case THIRD:
                                return (flopC == CanonSuit.THIRD)
                                       ? OT_ORR
                                       : OT_ORF;
                            default:
                                return OT_OWW;
                        }
                    case SECOND:
                        switch (flopB)
                        {
                            case SECOND:
                                return (flopC == CanonSuit.SECOND)
                                       ? OT_TTT
                                       : OT_TTR;
                            case THIRD:
                                return (flopC == CanonSuit.THIRD)
                                       ? OT_TRR
                                       : OT_TRF;
                            default:
                                return OT_TWW;
                        }
                    default:
                        return (flopC == CanonSuit.THIRD)
                               ? OT_RRR
                               : OT_RRF;
                }
            }
            else //if (holeB == CanonSuit.SECOND && isHolePair)
            {
                switch (flopB)
                {
                    case FIRST:
                        return (flopC == CanonSuit.FIRST)
                               ? P_OT_OOO
                               : (flopC == CanonSuit.SECOND)
                                 ? P_OT_OOT
                                 : P_OT_OOR;
                    case THIRD:
                        return (flopC == CanonSuit.FOURTH)
                               ? P_OT_ORF
                               : P_OT_ORR;
                    default:
                        return (flopB == CanonSuit.SECOND)
                               ? P_OT_OTR
                               : P_OT_OWW;
                }
            }
        }
        else // if (holeA == CanonSuit.WILD)
        {
            return (flopC == CanonSuit.SECOND)
                    ? P_WW_TTT
                    : (flopC == CanonSuit.THIRD)
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
