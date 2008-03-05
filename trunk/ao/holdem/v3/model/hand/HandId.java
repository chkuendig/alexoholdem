package ao.holdem.v3.model.hand;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.io.Serializable;

/**
 *
 */
public class HandId
        implements Comparable<HandId>,
                   Serializable
{
    //--------------------------------------------------------------------
    private static final int OFFSET = 1 << 8;


    //--------------------------------------------------------------------
    public static HandId nextInstance()
    {
        return new HandId(
                    System.currentTimeMillis() * OFFSET +
                    (int) (Math.random() * OFFSET));
    }


    //--------------------------------------------------------------------
    private long ID;


    //--------------------------------------------------------------------
    private HandId(long id)
    {
        ID = id;
    }


    //--------------------------------------------------------------------
    public int compareTo(HandId o)
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

        HandId handId = (HandId) o;
        return ID == handId.ID;
    }

    public int hashCode()
    {
        return (int) (ID ^ (ID >>> 32));
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleBinding
    {
        public HandId entryToObject(TupleInput input)
        {
            // negated for newest-to-last sort order
            long id = -input.readLong();
            return new HandId( id );
        }

        public void objectToEntry(Object object, TupleOutput output)
        {
            // negated for newest-to-last sort order
            HandId handId = (HandId) object;
            output.writeLong( -handId.ID );
        }
    }
}
