package ao.bucket.abstraction.bucketize.linear;

import ao.util.math.Calc;

/**
 * User: alex
 * Date: 15-May-2009
 * Time: 5:47:00 PM
 */
public class IndexedStrengthList
{
    //--------------------------------------------------------------------
    private final int  index   [];
    private final char strength[];


    //--------------------------------------------------------------------
    public IndexedStrengthList(int length) {
        index    = new int [ length ];
        strength = new char[ length ];
    }


    //--------------------------------------------------------------------
    public int length() {
        return index.length;
    }


    //--------------------------------------------------------------------
    public void set(int i, long canonIndex, double handStrength) {
        index   [i] = (int) canonIndex;
        strength[i] = (char)(Character.MAX_VALUE * handStrength);
    }


    //--------------------------------------------------------------------
    public long index(int i) {
        return Calc.unsigned(index[i]);
    }

    public char strength(int i)
    {
        return strength[ i ];
    }
}
