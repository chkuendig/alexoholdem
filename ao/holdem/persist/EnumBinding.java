package ao.holdem.persist;

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

        assert VALUES.length < Byte.MAX_VALUE;
    }


    //--------------------------------------------------------------------
    public E entryToObject(TupleInput input)
    {
        byte index = input.readByte();
        return index == Byte.MAX_VALUE
               ? null
               : VALUES[ index ];
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void objectToEntry(Object object, TupleOutput output)
    {
        E action = (E) object;
        if (action == null)
        {
            output.writeByte( Byte.MAX_VALUE );
        }
        else
        {
            output.writeByte( (byte) action.ordinal() );
        }
    }
}
