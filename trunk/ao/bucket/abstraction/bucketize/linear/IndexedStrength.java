package ao.bucket.abstraction.bucketize.linear;

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
        strength = (char)(Character.MAX_VALUE * handStrength);
    }


    //--------------------------------------------------------------------
    public long index() {
        return Calc.unsigned(index);
    }

//    public double strength()
//    {
//        return (double) strength / Character.MAX_VALUE;
//    }
    public char strength()
    {
        return strength;
    }


    //--------------------------------------------------------------------
    public int compareTo(IndexedStrength o) {
        return strength - o.strength;
    }
}
