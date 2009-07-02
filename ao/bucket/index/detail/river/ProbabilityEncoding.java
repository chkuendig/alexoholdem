package ao.bucket.index.detail.river;

/**
 * User: alex
 * Date: 2-Jun-2009
 * Time: 4:14:23 PM
 */
public class ProbabilityEncoding
{
    //--------------------------------------------------------------------
    private ProbabilityEncoding() {}


    //--------------------------------------------------------------------
    public static int COUNT = Character.MAX_VALUE + 1;


    //--------------------------------------------------------------------
    public static char encodeWinProb(double prob) {
        return (char)(Character.MAX_VALUE * prob);
    }

    //--------------------------------------------------------------------
    public static double decodeWinProb(char prob) {
        return (double) prob / Character.MAX_VALUE;
    }
}
