package ao.holdem.model;

import java.io.Serializable;

/**
 * Represents a player's identity.
 * Could be factored out of core logic.
 */
public class Avatar implements Serializable
{
    //--------------------------------------------------------------------
    public static Avatar local(String name)
    {
        return new Avatar("local", name);
    }


    //--------------------------------------------------------------------
    private final String domain;
    private final String name;


    //--------------------------------------------------------------------
    public Avatar(String domain, String name)
    {
        this.domain = domain;
        this.name   = name;
    }

    
    //--------------------------------------------------------------------
    public String domain()
    {
        return domain;
    }

    public String name()
    {
        return name;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        //return domain + "." + name;
        return name;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        Avatar avatar = (Avatar) o;
        return domain.equals(avatar.domain) &&
               name.equals(avatar.name);
    }

    public int hashCode()
    {
        int result;
        result = domain.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
