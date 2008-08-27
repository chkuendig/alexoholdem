package ao.bucket.index.iso_case;

import static ao.util.stats.Combo.colex;

/**
 * Date: Aug 21, 2008
 * Time: 8:20:22 PM
 */
public class IsoCaseUtils
{
    //--------------------------------------------------------------------
    private IsoCaseUtils() {}


    //--------------------------------------------------------------------
    @SuppressWarnings({"SuspiciousNameCombination"})
    public static int sortColex(int x, int y)
    {
        return x < y ? colex(x, y) : colex(y, x);
    }


    //--------------------------------------------------------------------
    public static int offset(
            int precededByA,
            int precededByB,
            int of)
    {
        return offset(precededByA, of) +
               offset(precededByB, of);
    }

    public static int offset(int precededBy, int of)
    {
        return Integer.signum(
                 Integer.signum(precededBy - of) // -1 or +1
                    - 1                          // -2 or 0
               );                                // -1 or 0

//        return (precededBy < of)
//                ? -1 : 0;
    }
}
