package ao.bucket.index.flop;

import static ao.bucket.index.flop.FlopIsoUtils.*;
import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_case.FlopCase;
import static ao.util.stats.Combo.colex;

/**
 * Date: Aug 16, 2008
 * Time: 3:36:51 PM
 */
public enum PostPairFlop implements FlopSubIndexer
{
    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, ONE, TWO] :: 0 .. 791
    OT_OOT(792, FlopCase.CASE_12_112){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return colex(a + offsetA(flop),
                         b + offsetB(flop)) * 12 +
                   (c + offsetC(flop));
        }
    },

    // [ONE, TWO][ONE, ONE, ONE] :: 0 .. 219
    OT_OOO(220, FlopCase.CASE_12_111){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return colex(a + offsetA(flop),
                         b + offsetB(flop),
                         c + offsetC(flop));
        }
    },

    // [WILD, WILD][TWO, TWO, TWO] :: 0 .. 285
    WW_TTT(286, FlopCase.CASE_WW_222){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return colex(a, b, c);
        }
    },

    //[WILD, WILD][TWO, TWO, THREE] :: 0 .. 1013
    WW_TTR(1014, FlopCase.CASE_WW_223){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return sortColex(a, b) * 13 + c;
        }
    },

    // [ONE, TWO][ONE, WILD, WILD] :: 0 .. 155
    OT_OWW(156, FlopCase.CASE_12_1WW){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return (a + offsetA(flop)) * 13 + b;
        }
    },

    // [ONE, TWO][ONE, THREE, FOUR] :: 0 .. 935
    OT_ORF(936, FlopCase.CASE_12_134){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return sortColex(b, c) * 12 +
                   (a + offsetA(flop));
        }
    },

    // [ONE, TWO][ONE, TWO, THREE] :: 0 .. 857
    OT_OTR(858, FlopCase.CASE_12_123){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return sortColex(a + offsetA(flop),
                             b + offsetB(flop)) * 13 + c;
        }
    },

    // [WILD, WILD][TWO, WILD, WILD] :: 0 .. 155
    WW_TWW(156, FlopCase.CASE_WW_2WW){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return (b + offsetA(flop)) * 13 + a;
        }
    },

    // [ONE, TWO][ONE, THREE, THREE] :: 0 .. 935
    OT_ORR(936, FlopCase.CASE_12_133){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return colex(b, c) * 12 + (a + offsetA(flop));
        }
    },

    // [ONE, TWO][ONE, ONE, THREE] :: 0 .. 857
    OT_OOR(858, FlopCase.CASE_12_113){
        public int subIndex(IsoFlop flop, int a, int b, int c) {
            return colex(a + offsetA(flop),
                         b + offsetB(flop)) * 13 + c;
        }
    };


    //--------------------------------------------------------------------
//    public static final Map<FlopCase, PostPairFlop> INDEX =
//            new LinkedHashMap<FlopCase, PostPairFlop>(){{
//                //for (PostPairFlop ppf : values())
//                for (PostPairFlop ppf : Arrays.asList(
//                        OT_OOT, OT_OOO, WW_TTT, WW_TTR, OT_OWW,
//                        OT_ORF, OT_OTR, WW_TWW, OT_ORR, OT_OOR))
//                {
//                    put(ppf.CASE, ppf);
//                }
//            }};


    //--------------------------------------------------------------------
    private final int      SIZE;
    private final FlopCase CASE;

    private PostPairFlop(int      size,
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
    public int subIndex(IsoFlop flop)
    {
        return subIndex(flop,
                        flop.flopA().ordinal(),
                        flop.flopB().ordinal(),
                        flop.flopC().ordinal());
    }
    protected abstract int subIndex(IsoFlop flop, int a, int b, int c);
}
