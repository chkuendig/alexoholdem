package ao.holdem.persist;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Sep 30, 2008
 * Time: 10:47:27 AM
 */
public abstract class GenericBinding<T> extends TupleBinding
{
    //--------------------------------------------------------------------
    public T entryToObject(TupleInput tupleInput)
    {
        return read(tupleInput);
    }
    public abstract T read(TupleInput input);


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void objectToEntry(Object o, TupleOutput tupleOutput)
    {
        write((T) o, tupleOutput);
    }
    public abstract void write(T obj, TupleOutput out);
}
