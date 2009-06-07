package ao.bucket.index.detail.river;

/**
 * User: alex
 * Date: 2-Jun-2009
 * Time: 4:14:23 PM
 */
public class StrengthCode
{
    //--------------------------------------------------------------------
    public static char encodeWinProb(double prob) {
        return (char)(Character.MAX_VALUE * prob);
    }

    //--------------------------------------------------------------------
    public static double decodeWinProb(char prob) {
        return (double) prob / Character.MAX_VALUE;
    }
}
