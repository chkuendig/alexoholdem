package ao.holdem.model.card;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
* Created by ao on 2014-04-19.
*/
public class CommunityBinding extends TupleBinding
{
    public static final CommunityBinding INSTANCE = new CommunityBinding();


    public Community entryToObject(TupleInput input)
    {
        Card flopA = CardBinding.INSTANCE.entryToObject(input);
        Card flopB = CardBinding.INSTANCE.entryToObject(input);
        Card flopC = CardBinding.INSTANCE.entryToObject(input);
        Card turn  = CardBinding.INSTANCE.entryToObject(input);
        Card river = CardBinding.INSTANCE.entryToObject(input);

        return new Community(flopA, flopB, flopC, turn, river);
    }

    public void objectToEntry(Object object, TupleOutput output)
    {
        Community community = (Community) object;

        CardBinding.INSTANCE.objectToEntry(community.flopA(), output);
        CardBinding.INSTANCE.objectToEntry(community.flopB(), output);
        CardBinding.INSTANCE.objectToEntry(community.flopC(), output);
        CardBinding.INSTANCE.objectToEntry(community.turn(), output);
        CardBinding.INSTANCE.objectToEntry(community.river(), output);
    }
}
