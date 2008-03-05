package ao.holdem.v3.persist;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 *
 */
public class EnumBinding<E extends Enum<E>>
        extends TupleBinding
{
    //--------------------------------------------------------------------
    private final E[] VALUES;


    //--------------------------------------------------------------------
    public EnumBinding(Class<E> enumType)
    {
        VALUES = enumType.getEnumConstants();
    }


    //--------------------------------------------------------------------
    public E entryToObject(TupleInput input)
    {
        short index = input.readShort();
        return VALUES[ index ];
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void objectToEntry(Object object, TupleOutput output)
    {
        E action = (E) object;
        output.writeShort( action.ordinal() );
    }
}
