package ao.bucket.index.flop;

import ao.bucket.index.iso_case.FlopCase;

/**
 * Date: Aug 21, 2008
 * Time: 10:44:50 PM
 */
public enum PostUnsuitedFlop //implements FlopSubIndexer
{
    //--------------------------------------------------------------------
//    // [ONE, TWO][TWO, TWO, THREE] :: 858 x 78 = 66924
//    OO_OTT(858, FlopCase.CASE_12_223) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(a + bOffsetA(flop),
//                             b + bOffsetB(flop)) * 13 + c;
//        }
//    },
//
//    // todo: reconcile with PostPair FlopCase
//    // [ONE, TWO][ONE, TWO, THREE] :: 1872 x 78 = 146016
//    //                             :: 871  x 78 = 67938
//    OT_OTR(871, FlopCase.CASE_12_123) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(a + aOffsetA(flop),
//                             b + bOffsetB(flop)) * 13 + c;
//        }
//    },
//
//    // [ONE, TWO][TWO, THREE, THREE] :: 936 x 78 = 73008
//    OT_TRR(936, FlopCase.CASE_12_233) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(b, c) * 12 +
//                   (a + bOffsetA(flop));
//        }
//    },
//
//    // todo: merge with case above
//    // [ONE, TWO][TWO, THREE, FOUR] :: 936 x 78 = 73008
//    OT_TRF(936, FlopCase.CASE_12_234) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(b, c) * 12 +
//                   (a + bOffsetA(flop));
//        }
//    },
//
//    // [ONE, TWO][TWO, WILD, WILD] :: 156 x 78 = 12168
//    OT_TWW(156, FlopCase.CASE_12_2WW) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return b * 12 +
//                   (a + bOffsetA(flop));
//        }
//    },
//
//    // todo: merge with CASE_12_133 from PostPairFlop
//    // [ONE, TWO][ONE, THREE, THREE] :: 936 x 78 = 73008
//    OT_ORR(936, FlopCase.CASE_12_133) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(b, c) * 12 + (a + aOffsetA(flop));
//        }
//    },
//
//    // todo: merge with CASE_12_113 from PostPairFlop
//    // [ONE, TWO][ONE, ONE, THREE] :: 858 x 78 = 66924
//    OT_OOR(858, FlopCase.CASE_12_113) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a + aOffsetA(flop),
//                         b + aOffsetB(flop)) * 13 + c;
//        }
//    },
//
//    // todo: merge with CASE_12_134 from PostPairFlop
//    // [ONE, TWO][ONE, THREE, FOUR] :: 936 x 78 = 73008
//    OT_ORF(936, FlopCase.CASE_12_134) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(b, c) * 12 +
//                   (a + aOffsetA(flop));
//        }
//    },
//
//    // todo: merge with CASE_12_1WW from PostPairFlop
//    // [ONE, TWO][ONE, WILD, WILD] :: 156 x 78 = 12168
//    OT_OWW(156, FlopCase.CASE_12_1WW) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return (a + aOffsetA(flop)) * 13 + b;
//        }
//    },
//
//    // [ONE, TWO][THREE, THREE, FOUR] :: 1014 x 78 = 79092
//    OT_RRF(1014, FlopCase.CASE_12_334) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a, b) * 13 + c;
//        }
//    },
//
//    // [ONE, TWO][THREE, THREE, THREE] :: 286 x 78 = 22308
//    OT_RRR(286, FlopCase.CASE_12_333) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a, b, c);
//        }
//    },
//
//    // [ONE, TWO][TWO, TWO, TWO] :: 220 x 78 = 17160
//    OT_TTT(220, FlopCase.CASE_12_222) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a + bOffsetA(flop),
//                         b + bOffsetB(flop),
//                         c + bOffsetC(flop));
//        }
//    },
//
//    // [ONE, TWO][ONE, TWO, TWO] :: 792 x 78 = 61776
//    OT_OTT(792, FlopCase.CASE_12_122) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(b + bOffsetB(flop),
//                         c + bOffsetC(flop)) * 12 +
//                    (a + aOffsetA(flop));
//        }
//    },
//
//    // todo: merge with CASE_12_112 from PostPairFlop
//    // [ONE, TWO][ONE, ONE, TWO] :: 792 x 78 = 61776
//    OT_OOT(792, FlopCase.CASE_12_112) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a + aOffsetA(flop),
//                         b + aOffsetB(flop)) * 12 +
//                    (c + bOffsetC(flop));
//        }
//    },
//
//    // todo: merge with CASE_12_111 from PostPairFlop
//    // [ONE, TWO][ONE, ONE, ONE] :: 220 x 78 = 17160
//    OT_OOO(220, FlopCase.CASE_12_111) {
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a + aOffsetA(flop),
//                         b + aOffsetB(flop),
//                         c + aOffsetC(flop));
//        }
//    },
     ;


    //--------------------------------------------------------------------
//    public static final Map<FlopCase, PostSuitedFlop> INDEX =
//            new LinkedHashMap<FlopCase, PostSuitedFlop>(){{
//                //for (PostPairFlop ppf : values())
//                for (PostSuitedFlop ppf : Arrays.asList(
//                        OO_OTT, OO_TTR, OO_TTT, OO_OOT, OO_OTR,
//                        OO_OWW, OO_RWW, OO_TRF, OO_WWW, OO_OOO))
//                {
//                    put(ppf.CASE, ppf);
//                }
//            }};


    //--------------------------------------------------------------------
    private final int      SIZE;
    private final FlopCase CASE;

    private PostUnsuitedFlop(int      size,
                             FlopCase flopCase)
    {
        SIZE = size;
        CASE = flopCase;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SIZE;
    }


    //--------------------------------------------------------------------
    public boolean caseEquals(FlopCase flopCase)
    {
        return CASE.equals( flopCase );
    }


    //--------------------------------------------------------------------
//    public int subIndex(IsoFlop flop)
//    {
//        return subIndex(flop,
//                        flop.flopA().ordinal(),
//                        flop.flopB().ordinal(),
//                        flop.flopC().ordinal());
//    }
//    protected abstract int subIndex(IsoFlop flop, int a, int b, int c);
}