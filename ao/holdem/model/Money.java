package ao.holdem.model;

//import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 */
//@Embeddable
public class Money implements Comparable<Money>, Serializable
{
    //--------------------------------------------------------------------
    public static final Money ZERO        = new Money(0);
    public static final Money SMALL_BLIND = new Money(1);
    public static final Money BIG_BLIND   = new Money(2);
    public static final Money SMALL_BET   = BIG_BLIND;
    public static final Money BIG_BET     = new Money(4);

    public static Money blind(boolean isSmall)
    {
        return isSmall ? SMALL_BLIND : BIG_BLIND;
    }


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
        return bigBlinds();
    }

    public int bigBets()
    {
        return (smallBets() + 1) >> 1;
    }

    // true for small bets, false for big bets
    public int bets(boolean smallOrBig)
    {
        return (smallOrBig) ? smallBets()
                            : bigBets();
    }
    public int bets(Money betSize)
    {
        return bets(betSize.equals(SMALL_BET));
    }


    public int chips(int smallBlind)
    {
        return smallBlinds() * smallBlind;
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
    @Override
    public String toString()
    {
        return smallBlinds + " sb";
    }
    
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
