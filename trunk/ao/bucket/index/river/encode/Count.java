package ao.bucket.index.river.encode;

/**
 * Date: Sep 4, 2008
 * Time: 11:51:04 AM
 */
public enum Count
{
    //--------------------------------------------------------------------
    C_8(8),
    C_9(9),
    C_10(10),
    C_11(11),
    C_12(12),
    C_13(13),

    C_22(22),
    C_23(23),
    C_24(24),
    C_25(25),

    C_33(33),

    C_35(35),
    C_36(36),

    C_48(48),
    ;

    public static final Count VALUES[] = values();


    //--------------------------------------------------------------------
    public static Count valueOf(int count)
    {
        for (Count repeatCount : VALUES)
        {
            if (repeatCount.COUNT == count)
            {
                return repeatCount;
            }
        }
        return null;
    }
    

    //--------------------------------------------------------------------
    private final int COUNT;


    //--------------------------------------------------------------------
    private Count(int count)
    {
        COUNT = count;
    }


    //--------------------------------------------------------------------
    public int count()
    {
        return COUNT;
    }
}
