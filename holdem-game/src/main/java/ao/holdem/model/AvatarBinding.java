package ao.holdem.model;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
* Created by ao on 2014-04-19.
*/
public class AvatarBinding extends TupleBinding<Avatar>
{
    public static final AvatarBinding INSTANCE = new AvatarBinding();


    public Avatar entryToObject(TupleInput input)
    {
        String domain = input.readString();
        String name   = input.readString();
        return new Avatar(domain, name);
    }

    public void objectToEntry(
            Avatar      avatar,
            TupleOutput output)
    {
        output.writeString(avatar.domain());
        output.writeString(avatar.name());
    }
}
