package ao.bucket.index.test.exhaustive;

import ao.holdem.persist.GenericBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Oct 1, 2008
 * Time: 2:45:20 PM
 */
public class SeenCount<T>
{
    //--------------------------------------------------------------------
    private final T                 PAYLOAD;
//    private final GenericBinding<T> PAYLOAD_BINDING;
    private       int               count;


    //--------------------------------------------------------------------
    public SeenCount(T                 payload)//,
//                     GenericBinding<T> binding)
    {
        this( payload, /*binding,*/ 0 );
    }
    public SeenCount(
            T                 payload,
//            GenericBinding<T> binding,
            int               initialCount)
    {
        PAYLOAD         = payload;
//        PAYLOAD_BINDING = binding;
        count           = initialCount;
    }


    //--------------------------------------------------------------------
    public void increment()
    {
        count++;
    }


    //--------------------------------------------------------------------
    public int count()
    {
        return count;
    }


    //--------------------------------------------------------------------
    public boolean payloadEquals(T other)
    {
        return PAYLOAD.equals( other );
    }

    public T payload()
    {
        return PAYLOAD;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return PAYLOAD + "\t" + count;
    }


    //--------------------------------------------------------------------
    public static <T> Binding<T> binding(
            GenericBinding<T> payloadBinding)
    {
        return new Binding<T>( payloadBinding );
    }
    public static class Binding<T>
            extends GenericBinding<SeenCount<T>>
    {
        private final GenericBinding<T> PAYLOAD_BINDING;
        private Binding(GenericBinding<T> payloadBinding) {
            PAYLOAD_BINDING = payloadBinding;
        }

        public SeenCount<T> read(TupleInput input)
        {
            T payload = PAYLOAD_BINDING.read( input );
            return new SeenCount<T>(
                payload, input.readInt());
        }

        public void write(SeenCount<T> o, TupleOutput to)
        {
            PAYLOAD_BINDING.write(o.PAYLOAD, to);
            to.writeLong( o.count );
        }
    }
}
