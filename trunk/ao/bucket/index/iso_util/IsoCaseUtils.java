package ao.bucket.index.iso_util;

import ao.holdem.model.card.Card;
import static ao.util.stats.Combo.colex;

import java.util.Arrays;

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


    //--------------------------------------------------------------------
    public static Card[] sortByRank(Card... cards)
    {
        Card copy[] = cards.clone();
        Arrays.sort(copy, Card.BY_RANK_DSC);
        return copy;
    }

    public static <T> int distinct(T a, T b, T c)
    {
        return a == b && b == c
               ? 1
               : a != b && a != c && b != c
                 ? 3 : 2;
    }
}
