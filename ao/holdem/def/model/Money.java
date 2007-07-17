package ao.holdem.def.model;

import java.io.Serializable;

/**
 *
 */
public class Money implements Serializable
{
    //--------------------------------------------------------------------
    public static final Money ZERO        = new Money(0);
    public static final Money SMALL_BLIND = new Money(1);
    public static final Money BIG_BLIND   = new Money(2);
    public static final Money SMALL_BET   = BIG_BLIND;
    public static final Money BIG_BET     = new Money(4);


    //--------------------------------------------------------------------
    private final int smallBlinds;

    public Money(int smallBlinds)
    {
        this.smallBlinds = smallBlinds;
    }


    //--------------------------------------------------------------------
    public int smallBlinds()
    {
        return smallBlinds;
    }

    public int bigBlinds()
    {
        return (smallBlinds + 1) >> 1;
    }

    public int smallBets()
    {
        return bigBets();
    }

    public int bigBets()
    {
        return (smallBets() + 1) >> 1;
    }

    public int chips(int smallBlind)
    {
        return smallBlinds() * smallBlind;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return smallBlinds + " small blinds";
    }


    //--------------------------------------------------------------------
    public Money plus(Money addend)
    {
        return new Money(smallBlinds + addend.smallBlinds);
    }

    public Money minus(Money subtractor)
    {
        return new Money(smallBlinds - subtractor.smallBlinds);
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;
        return smallBlinds == money.smallBlinds;
    }

    public int hashCode()
    {
        return smallBlinds;
    }
}
