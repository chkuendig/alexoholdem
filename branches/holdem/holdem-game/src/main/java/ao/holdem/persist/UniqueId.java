package ao.holdem.persist;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.io.Serializable;

/**
 *
 */
public class UniqueId
        implements Comparable<UniqueId>,
                   Serializable
{
    //--------------------------------------------------------------------
    private static final int OFFSET = 1 << 10;


    //--------------------------------------------------------------------
    public static UniqueId nextInstance()
    {
        return new UniqueId(
                    System.currentTimeMillis() * OFFSET +
                    (int) (Math.random() * OFFSET));
    }


    //--------------------------------------------------------------------
    private long ID;


    //--------------------------------------------------------------------
    private UniqueId(long id)
    {
        ID = id;
    }


    //--------------------------------------------------------------------
    public int compareTo(UniqueId o)
    {
        return ID < o.ID
               ? -1
               : ID > o.ID
                 ? 1 : 0;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf( ID );
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        UniqueId handId = (UniqueId) o;
        return ID == handId.ID;
    }

    public int hashCode()
    {
        return (int) (ID ^ (ID >>> 32));
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleBinding<UniqueId>
    {
        public UniqueId entryToObject(TupleInput input)
        {
            // negated for newest-to-last sort order
            long id = -input.readLong();
            return new UniqueId( id );
        }

        public void objectToEntry(UniqueId object, TupleOutput output)
        {
            // negated for newest-to-last sort order
            UniqueId handId = (UniqueId) object;
            output.writeLong( -handId.ID );
        }
    }
}
