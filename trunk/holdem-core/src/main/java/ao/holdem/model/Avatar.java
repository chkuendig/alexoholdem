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
    private final String DOMAIN;
    private final String NAME;


    //--------------------------------------------------------------------
    public Avatar(String domain, String name)
    {
        DOMAIN = domain;
        NAME   = name;
    }

    
    //--------------------------------------------------------------------
    public String domain()
    {
        return DOMAIN;
    }

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
}
