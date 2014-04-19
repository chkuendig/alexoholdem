package ao.holdem.abs.bucket.index.detail.river;


/**
 * 2-Jun-2009
 */
public enum ProbabilityEncoding
{;
    //--------------------------------------------------------------------
    public static int COUNT = Character.MAX_VALUE;
    public static int MAX   = COUNT - 1;


    //--------------------------------------------------------------------
    public static char encodeWinProb(double prob) {
        return (char)(MAX * prob);
    }

    //--------------------------------------------------------------------
    public static double decodeWinProb(char prob) {
        return (double) prob / MAX;
    }
}
