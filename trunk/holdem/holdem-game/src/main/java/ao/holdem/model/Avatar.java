package ao.holdem.model;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.io.Serializable;

/**
 *
 */
public class Avatar implements Serializable
{
    //--------------------------------------------------------------------
    public static Avatar local(String name)
    {
        return new Avatar("local", name);
    }


    //--------------------------------------------------------------------
    private final String DOMAIN;
    private final String NAME;


    //--------------------------------------------------------------------
    public Avatar(String domain, String name)
    {
        DOMAIN = domain;
        NAME   = name;
    }

    
    //--------------------------------------------------------------------
    public String name()
    {
        return NAME;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        //return DOMAIN + "." + NAME;
        return NAME;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        Avatar avatar = (Avatar) o;
        return DOMAIN.equals(avatar.DOMAIN) &&
               NAME.equals(avatar.NAME);
    }

    public int hashCode()
    {
        int result;
        result = DOMAIN.hashCode();
        result = 31 * result + NAME.hashCode();
        return result;
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleBinding<Avatar>
    {
        public Avatar entryToObject(TupleInput input)
        {
            String domain = input.readString();
            String name   = input.readString();
            return new Avatar(domain, name);
        }

        public void objectToEntry(
                Avatar      object,
                TupleOutput output)
        {
            Avatar avatar = (Avatar) object;
            output.writeString(avatar.DOMAIN);
            output.writeString(avatar.NAME);
        }
    }
}
