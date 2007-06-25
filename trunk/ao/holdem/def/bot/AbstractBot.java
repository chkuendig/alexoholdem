package ao.holdem.def.bot;

/**
 *
 */
public abstract class AbstractBot implements Bot
{
    //--------------------------------------------------------------------
    @Override public String toString()
    {
//        throw new Error("You must define your own toString method!!");
        return getClass().getSimpleName();
    }


    //--------------------------------------------------------------------
    @Override public int hashCode()
    {
        return toString().hashCode();
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o)
    {
        return getClass() == o.getClass() &&
               toString().equals( o.toString() );
    }
}
