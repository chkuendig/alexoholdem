package ao.bucket.index.test;

/**
 * Date: Nov 16, 2008
 * Time: 1:47:59 PM
 */
public class MathUtil
{
    //--------------------------------------------------------------------
    private MathUtil() {}


    //--------------------------------------------------------------------
    public static int signedPart(long unsignedInteger)
    {
        return (int) (unsignedInteger - Integer.MAX_VALUE - 1);
    }
}
