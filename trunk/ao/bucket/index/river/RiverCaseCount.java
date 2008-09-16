package ao.bucket.index.river;

/**
 * Date: Sep 16, 2008
 * Time: 10:30:06 AM
 */
public enum RiverCaseCount
{
    //--------------------------------------------------------------------
    ;

    public static final RiverCaseCount VALUES[] = values();


    //--------------------------------------------------------------------
    public static RiverCaseCount valueOf(
            RiverCaseSet caseSet,
            int          count)
    {
        for (RiverCaseCount caseCount :  VALUES)
        {
            if (caseCount.CASE_SET == caseSet &&
                    caseCount.COUNT == count)
            {
                return caseCount;
            }
        }
        System.err.println("cannot locate: " + caseSet + ", " + count);
        return null;
    }


    //--------------------------------------------------------------------
    private final RiverCaseSet CASE_SET;
    private final int          COUNT;

    
    //--------------------------------------------------------------------
    private RiverCaseCount(RiverCaseSet caseSet,
                           int          count)
    {
        CASE_SET = caseSet;
        COUNT    = count;
    }
}
