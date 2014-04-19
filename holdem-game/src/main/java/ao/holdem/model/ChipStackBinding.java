package ao.holdem.model;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
* Created by ao on 2014-04-19.
*/
public class ChipStackBinding extends TupleBinding
{
    public static final ChipStackBinding INSTANCE = new ChipStackBinding();


    public ChipStack entryToObject(TupleInput input)
    {
        short smallBlinds = input.readShort();
        return ChipStack.newInstance(smallBlinds);
    }

    public void objectToEntry(Object      object,
                              TupleOutput output)
    {
        ChipStack chips = (ChipStack) object;
        output.writeShort((short) chips.smallBlinds());
    }
}
