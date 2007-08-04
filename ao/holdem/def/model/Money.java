package ao.holdem.def.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 */
@Embeddable
public class Money implements Comparable<Money>, Serializable
{
    //--------------------------------------------------------------------
    public static final Money ZERO        = new Money(0);
    public static final Money SMALL_BLIND = new Money(1);
    public static final Money BIG_BLIND   = new Money(2);
    public static final Money SMALL_BET   = BIG_BLIND;
    public static final Money BIG_BET     = new Money(4);


    //--------------------------------------------------------------------
    private int smallBlinds;

    public Money()
    {
        this(0);
    }
    public Money(int smallBlinds)
    {
        this.smallBlinds = smallBlinds;
    }


    //--------------------------------------------------------------------
    // for persitance purposes
    public int getSmallBlinds()
    {
        return smallBlinds;
    }

    public void setSmallBlinds(int smallBlinds)
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

    public Money split(int into)
    {
        return new Money(smallBlinds / into);
    }
    public Money remainder(int afterSplittingInto)
    {
        return minus( split(afterSplittingInto)
                        .times(afterSplittingInto) );
    }
    private Money times(int factor)
    {
        return new Money(smallBlinds * factor);
    }


    //--------------------------------------------------------------------
    public int compareTo(Money o)
    {
        return (smallBlinds < o.smallBlinds) ? -1 :
               (smallBlinds > o.smallBlinds) ?  1 : 0;
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
