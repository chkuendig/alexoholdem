package ao.holdem.abs.bucket.abstraction.access.tree.list.compress;

/**
 * Date: Jan 28, 2009
 * Time: 8:47:13 PM
 */
public class SlimBitTest
{
//    //--------------------------------------------------------------------
//    public static void main(String[] args) throws Exception
//    {
//        runGC();
//        long tmStart = System.currentTimeMillis();
//        long atStart = usedMemory();
//        System.out.println("atStart = " + atStart);
//
//        LongBitSet big =
//                LongBitSet.retrieve(new File(
//                        "compact/bucket/odds_homo.20;odds_homo.800;" +
//                            "odds_homo.4800;odds_homo.28800/rivers/c"));
//
//        long tmBig = System.currentTimeMillis();
//        runGC();
//        long atBig = usedMemory();
//        System.out.println(atBig - atStart);
//        System.out.println(tmBig - tmStart);
//
////        WAHBitSet bitsA = new WAHBitSet();
////        for (int i = 0; i < Integer.MAX_VALUE; i++)
////        {
////            if (big.get(i)) bitsA.set(i);
////        }
//
//        WAHBitSet bitsB = new WAHBitSet();
//        for (int i = 0; i < RiverLookup.CANONS - Integer.MAX_VALUE; i++)
//        {
//            if (big.get((long) Integer.MAX_VALUE + i)) bitsB.set(i);
//        }
//        System.out.println(RiverLookup.CANONS - Integer.MAX_VALUE);
//
//        long tmBits = System.currentTimeMillis();
//        runGC();
//        long atBits = usedMemory();
//        System.out.println(atBits - atBig);
//        System.out.println(tmBits - tmStart);
//
////        for (int i = 0; i < Integer.MAX_VALUE; i++)
////        {
////            assert big.get(i) == bitsA.get(i);
////        }
////        for (int i = 0; i < RiverLookup.CANONS - Integer.MAX_VALUE; i++)
////        {
////            assert big.get((long) Integer.MAX_VALUE + i) == bitsB.get(i);
////        }
//        for (int i = 0; i < 1000; i++)
//        {
//            assert big.get((long) Integer.MAX_VALUE + i) == bitsB.get(i);
//        }
//
//        long tmTest = System.currentTimeMillis();
//        System.out.println(tmTest - tmBits);
//    }
//
//
//
//    //--------------------------------------------------------------------
//    private static final Runtime s_runtime = Runtime.getRuntime ();
//
//    private static void runGC() throws Exception
//    {
//        // It helps to call Runtime.gc()
//        // using several method calls:
//        for (int r = 0; r < 4; ++ r) _runGC();
//    }
//    private static void _runGC() throws Exception
//    {
//        long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
//        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i)
//        {
//            s_runtime.runFinalization();
//            s_runtime.gc();
//            Thread.yield();
//
//            usedMem2 = usedMem1;
//            usedMem1 = usedMemory();
//        }
//    }
//    private static long usedMemory() throws Exception
//    {
//        return s_runtime.totalMemory() - s_runtime.freeMemory();
//    }
}
