package ao.bucket.index.iso_river.encode;

import ao.bucket.index.iso_river.RiverCaseSet;
import static ao.bucket.index.iso_river.RiverCaseSet.*;
import static ao.bucket.index.iso_river.encode.Count.*;

/**
 * Date: Sep 4, 2008
 * Time: 12:11:00 PM
 */
public enum CaseCount
{
    OR_10(OR, C_10),
    OR_12(OR, C_12),
    OR_13(OR, C_13),
    OR_24(OR, C_24),
    OR_25(OR, C_25),
    OR_36(OR, C_36),
    OR_48(OR, C_48),

    OT_11(OT, C_11),
    OT_12(OT, C_12),
    OT_13(OT, C_13),
    OT_22(OT, C_22),
    OT_24(OT, C_24),

    ZOF_9(ZOF, C_9),
    ZOF_11(ZOF, C_11),
    ZOF_12(ZOF, C_12),
    ZOF_13(ZOF, C_13),
    ZOF_24(ZOF, C_24),
    ZOF_25(ZOF, C_25),

    ZOI_8(ZOI, C_8),
    ZOI_11(ZOI, C_11),
    ZOI_12(ZOI, C_12),
    ZOI_13(ZOI, C_13),

    ZOTR_10(ZOTR, C_10),
    ZOTR_11(ZOTR, C_11),
    ZOTR_12(ZOTR, C_12),
    ZOTR_13(ZOTR, C_13),
    ZOTR_23(ZOTR, C_23),

    ZR_10(ZR, C_10),
    ZR_13(ZR, C_13),

    ZS_11(ZS, C_11),
    ZS_13(ZS, C_13),

    ZT_11(ZT, C_11),
    ZT_13(ZT, C_13),
    ZT_22(ZT, C_22),
    ZT_33(ZT, C_33),
    ZT_35(ZT, C_35),

    ZTF_9(ZTF, C_9),
    ZTF_11(ZTF, C_11),
    ZTF_12(ZTF, C_12),
    ZTF_13(ZTF, C_13),
    ZTF_22(ZTF, C_22),
    ;

    public static final CaseCount VALUES[] = values();


    //--------------------------------------------------------------------
    public static CaseCount valueOf(
            RiverCaseSet riverCaseSet,
            Count count)
    {
        for (CaseCount caseCount : VALUES)
        {
            if (caseCount.CASE == riverCaseSet &&
                    caseCount.COUNT == count)
            {
                return caseCount;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private Count        COUNT;
    private RiverCaseSet CASE;
    
    
    //--------------------------------------------------------------------
    private CaseCount(RiverCaseSet riverCaseSet,
                      Count        count)
    {
        COUNT = count;
        CASE  = riverCaseSet;
    }


    //--------------------------------------------------------------------
    public int count()
    {
        return COUNT.count();
    }

    public RiverCaseSet caseSet()
    {
        return CASE;
    }

    public int totalSize()
    {
        return CASE.size() * count();
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return CASE + "\t" + COUNT;
    }


    //--------------------------------------------------------------------
//    @Override
//    public boolean equals(Object o)
//    {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        CaseCount caseCount = (CaseCount) o;
//
//        if (CASE != caseCount.CASE) return false;
//        if (COUNT != caseCount.COUNT) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode()
//    {
//        int result = COUNT.hashCode();
//        result = 31 * result + CASE.hashCode();
//        return result;
//    }
}
