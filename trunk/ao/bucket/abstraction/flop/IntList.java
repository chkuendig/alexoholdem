package ao.bucket.abstraction.flop;

import java.util.Arrays;

/**
 * Date: Oct 15, 2008
 * Time: 2:40:42 PM
 */
public class IntList
{
    //--------------------------------------------------------------------
    public static IntList addTo(IntList intList, int value)
    {
        IntList list =
                (intList == null)
                ? new IntList()
                : intList;

        list.add( value );

        return list;
    }
    public static int sizeOf(IntList intList)
    {
        return (intList == null)
               ? 0
               : intList.size();
    }


    //--------------------------------------------------------------------
    private int values[] = new int[ 16 ];
    private int size     = 0;


    //--------------------------------------------------------------------
    public void add(int value)
    {
        ensureCapacity( size + 1 );
        values[ size++ ] = value;
    }


    //--------------------------------------------------------------------
    private void ensureCapacity(int cap)
    {
        if (values.length < cap)
        {
            int newLength =
                    Math.max(cap, values.length * 2);
            values = Arrays.copyOf(values, newLength);
        }
    }


    //--------------------------------------------------------------------
    public int get(int index)
    {
        return values[ index ];
    }

    public int size()
    {
        return size;
    }


    //--------------------------------------------------------------------
    public int[] toArray()
    {
        return Arrays.copyOf(values, size);
    }
}
