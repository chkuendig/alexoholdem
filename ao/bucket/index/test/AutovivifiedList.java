package ao.bucket.index.test;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Date: Sep 21, 2008
 * Time: 1:03:07 PM
 */
public class AutovivifiedList<T> extends AbstractList<T>
{
    //--------------------------------------------------------------------
    private final T            DEFAULT;
    private final ArrayList<T> DELEGET = new ArrayList<T>();


    //--------------------------------------------------------------------
    public AutovivifiedList(T defaultValue)
    {
        DEFAULT = defaultValue;
    }
    public AutovivifiedList()
    {
        this( null );
    }


    //--------------------------------------------------------------------
    public T get(int index)
    {
        return   index < DELEGET.size()
               ? DELEGET.get( index )
               : DEFAULT;
    }


    //--------------------------------------------------------------------
    @Override
    public T set(int index, T element)
    {
        T val = get(index);

        DELEGET.ensureCapacity(index + 1);
        while (index >= DELEGET.size())
        {
            DELEGET.add( DEFAULT );
        }
        DELEGET.set(index, element);

        return val;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return DELEGET.size();
    }
}
