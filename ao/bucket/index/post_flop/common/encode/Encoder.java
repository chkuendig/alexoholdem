package ao.bucket.index.post_flop.common.encode;

/**
 * Date: Sep 4, 2008
 * Time: 1:37:31 PM
 */
public class Encoder
{
//    //--------------------------------------------------------------------
//    private Encoder() {}
//
//
//    //--------------------------------------------------------------------
//    public static long encode(int       turnIndexAtStart,
//                              long      globalOffset,
//                              CaseCount caseCount)
//    {
//        long coding =
//                ((long) turnIndexAtStart) << 38 |
//                globalOffset << 6 |
//                caseCount.ordinal();
//
//        assert caseCount == caseCount(coding);
//        assert globalOffset == globalOffset(coding);
//        assert turnIndexAtStart == turnIndex(coding);
//
//        return coding;
//    }
//
//
//    //--------------------------------------------------------------------
//    public static int turnIndex(long encoding)
//    {
//        return (int)(encoding >>> 38);
//    }
//
//    public static long globalOffset(long encoding)
//    {
//        return (encoding >> 6) & 0x00000000FFFFFFFFL;
//    }
//
//    public static CaseCount caseCount(long encoding)
//    {
//        int caseCountOrdinal = (int)(encoding & 0x3F);
//        return CaseCount.VALUES[ caseCountOrdinal ];
//    }
}
