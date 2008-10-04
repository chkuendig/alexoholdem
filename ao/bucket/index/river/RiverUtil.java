package ao.bucket.index.river;

/**
 * Date: 3-Oct-2008
 * Time: 6:17:48 PM
 */
public class RiverUtil
{
    //--------------------------------------------------------------------
    private RiverUtil() {}


    //--------------------------------------------------------------------
    public static long unsigned(int value)
    {
        return value >= 0
               ? value
               : (long)(Integer.MAX_VALUE + value) +
                 (long)(Integer.MAX_VALUE        ) + 2;
    }


//    public static void main(String[] args) {
//        System.out.println(
//                unsigned(-1866679876)
//        );
//    }
}
