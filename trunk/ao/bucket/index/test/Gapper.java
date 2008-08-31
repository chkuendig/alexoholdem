package ao.bucket.index.test;

import java.util.BitSet;

/**
 * Date: Aug 21, 2008
 * Time: 7:43:32 PM
 */
public class Gapper
{
    //--------------------------------------------------------------------
    private BitSet indexes = new BitSet();
    private int    count   = 0;


    //--------------------------------------------------------------------
    public void set(int index)
    {
//        if (indexes.get(index))
//        {
//            throw new Error("duplicate at " + index);
//        }

        count++;
        indexes.set( index );
    }


    //--------------------------------------------------------------------
    public boolean continuous()
    {
        return indexes.nextClearBit(0) == indexes.length();
    }


    //--------------------------------------------------------------------
    public boolean displayStatus()
    {
        boolean isContinuous = continuous();
        if (isContinuous)
        {
            System.out.println(
                "Compressed " + count + " into " +
                    indexes.length() + " isomorphisms.");

        }
        else
        {
            System.out.println(
                    "ERROR: gap at " + indexes.nextClearBit(0) +
                        " of " + indexes.length() + " indexes.");
        }
        return isContinuous;
    }
}
