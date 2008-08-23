package ao.bucket.index.flop;

import ao.bucket.index.iso_case.FlopCase;


/**
 * Date: Aug 21, 2008
 * Time: 8:17:28 PM
 */
public enum PostSuitedFlop //implements FlopSubIndexer
{
    //--------------------------------------------------------------------
//    // [ONE, ONE][ONE, TWO, TWO] :: 858 x 78 = 66924
//    OO_OTT(858, FlopCase.CASE_11_122){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(b, c) * 11 +
//                   (abOffsetA(flop) + a);
//        }
//    },
//
//    // todo: merge with FlopCase.CASE_WW_223
//    // [ONE, ONE][TWO, TWO, THREE] :: 1014 x 78 = 79092
//    OO_TTR(1014, FlopCase.CASE_11_223){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(a, b) * 13 + c;
//        }
//    },
//
//    // todo: merge with FlopCase.CASE_WW_222
//    //[ONE, ONE][TWO, TWO, TWO]	:: 286 x 78 = 22308
//    OO_TTT(286, FlopCase.CASE_11_222){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a, b, c);
//        }
//    },
//
//    //[ONE, ONE][ONE, ONE, TWO]	:: 715 x 78 = 55770
//    OO_OOT(715, FlopCase.CASE_11_112){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a + abOffsetA(flop),
//                         b + abOffsetB(flop)) * 13 + c;
//        }
//    },
//
//    //[ONE, ONE][ONE, TWO, THREE] :: 858 x 78 = 66924
//    OO_OTR(858, FlopCase.CASE_11_123){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return sortColex(b, c) * 11 +
//                   (a + abOffsetA(flop));
//        }
//    },
//
//    // todo: merge with FlopCase.CASE_12_1WW
//    //[ONE, ONE][ONE, WILD, WILD] :: 143 x 78 = 11154
//    OO_OWW(143, FlopCase.CASE_11_1WW){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return (a + abOffsetA(flop)) * 13 + b;
//        }
//    },
//
//    //[ONE, ONE][THREE, WILD, WILD] :: 156 x 78 = 12168
//    OO_RWW(156, FlopCase.CASE_11_3WW){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return (b + (a < b ? -1 : 0)) * 13 +
//                    a;
//        }
//    },
//
//    //[ONE, ONE][TWO, THREE, FOUR] :: 286 x 78 = 22308
//    OO_TRF(286, FlopCase.CASE_11_234){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            //int index = colex(c, b, a);
//            //System.out.println(index + " :: " + flop);
//            return colex(c, b, a);
//        }
//    },
//
//    //[ONE, ONE][WILD, WILD, WILD] :: 13 * 78 = 1014
//    OO_WWW(13, FlopCase.CASE_11_WWW){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return a;
//        }
//    },
//
//    // todo: different # members from FlopCase.CASE_12_111
//    //[ONE, ONE][ONE, ONE, ONE] :: 165 x 78 = 12870
//    OO_OOO(165, FlopCase.CASE_11_111){
//        public int subIndex(IsoFlop flop, int a, int b, int c) {
//            return colex(a + abOffsetA(flop),
//                         b + abOffsetB(flop),
//                         c + abOffsetC(flop));
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

    private PostSuitedFlop(int      size,
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
