package ao.holdem.model.card;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
* Created by ao on 2014-04-19.
*/
public class HoleBinding extends TupleBinding
{
    public static final HoleBinding INSTANCE = new HoleBinding();


    public Hole entryToObject(TupleInput input)
    {
        byte cardA = input.readByte();
        byte cardB = input.readByte();
        return Hole.valueOf(Card.VALUES[cardA], Card.VALUES[cardB]);
    }

    public void objectToEntry(Object object, TupleOutput output)
    {
        Hole hole = (Hole) object;

        if (hole == null)
        {
            output.writeByte((byte) 0);
            output.writeByte((byte) 0);
        }
        else
        {
            output.writeByte((byte) hole.a().ordinal());
            output.writeByte((byte) hole.b().ordinal());
        }
    }
}
