package ao.bucket.abstraction.bucketize.linear;

import ao.bucket.index.detail.river.ProbabilityEncoding;
import ao.util.math.Calc;

/**
 * User: Alex
 * Date: 12-May-2009
 * Time: 9:21:14 PM
 */
public class IndexedStrength
        implements Comparable<IndexedStrength>
{
    //--------------------------------------------------------------------
    private final int  index;
    private final char strength;


    //--------------------------------------------------------------------
    public IndexedStrength(long canonIndex, double handStrength) {
        index    = (int) canonIndex;
        strength = ProbabilityEncoding.encodeWinProb( handStrength );
    }


    //--------------------------------------------------------------------
    public long index() {
        return Calc.unsigned(index);
    }

    public double realStrength()
    {
        return ProbabilityEncoding.decodeWinProb( strength );
    }
    public char strength()
    {
        return strength;
    }


    //--------------------------------------------------------------------
    public int compareTo(IndexedStrength o) {
        return strength - o.strength;
    }
}
