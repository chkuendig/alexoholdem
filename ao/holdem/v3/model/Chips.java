package ao.holdem.v3.model;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * One or more chips.
 */
public class Chips implements Comparable<Chips>
{
    //--------------------------------------------------------------------
    private static final Chips CACHE_NEGATIVE[] = new Chips[ 64  ];
    private static final Chips CACHE_POSITIVE[] = new Chips[ 256 ];
    static
    {
        for (int i = 0; i < CACHE_NEGATIVE.length; i++)
            CACHE_NEGATIVE[ i ] = new Chips(-i);

        for (int i = 0; i < CACHE_POSITIVE.length; i++)
            CACHE_POSITIVE[ i ] = new Chips(i);
    }


    //--------------------------------------------------------------------
    public static final Chips ZERO        = newInstance(0);
    public static final Chips SMALL_BLIND = newInstance(1);
    public static final Chips BIG_BLIND   = newInstance(2);
    public static final Chips SMALL_BET   = BIG_BLIND;
    public static final Chips BIG_BET     = newInstance(4);
    public static final Chips MAX_VALUE   = newInstance(Short.MAX_VALUE);

    public static Chips blind(boolean isSmall)
    {
        return isSmall ? SMALL_BLIND : BIG_BLIND;
    }

    public static Chips newInstance(int smallBlinds)
    {
        return (0 <= smallBlinds &&
                     smallBlinds < CACHE_POSITIVE.length)
                ? CACHE_POSITIVE[ smallBlinds ]
                : (0 > smallBlinds &&
                       smallBlinds >= -(CACHE_NEGATIVE.length + 1))
                   ? CACHE_NEGATIVE[-(smallBlinds + 1)]
                   : new Chips( smallBlinds );
    }


    //--------------------------------------------------------------------
    private final int smallBlinds;

    private Chips(int smallBlinds)
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


    //--------------------------------------------------------------------
    public Chips plus(Chips addend)
    {
        return Chips.newInstance(
                smallBlinds + addend.smallBlinds);
    }

    public Chips minus(Chips subtractor)
    {
        return Chips.newInstance(
                smallBlinds - subtractor.smallBlinds);
    }

    public Chips negate()
    {
        return Chips.newInstance(-smallBlinds);
    }

    public Chips split(int into)
    {
        return Chips.newInstance(
                smallBlinds / into);
    }
    public Chips remainder(int afterSplittingInto)
    {
        return minus( split(afterSplittingInto)
                        .times(afterSplittingInto) );
    }
    private Chips times(int factor)
    {
        return Chips.newInstance(
                smallBlinds * factor);
    }


    //--------------------------------------------------------------------
    public int compareTo(Chips o)
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
        //if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        Chips money = (Chips) o;
        return smallBlinds == money.smallBlinds;
    }

    public int hashCode()
    {
        return smallBlinds;
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleBinding
    {
        public Chips entryToObject(TupleInput input)
        {
            short smallBlinds = input.readShort();
            return Chips.newInstance( smallBlinds );
        }

        public void objectToEntry(Object      object,
                                  TupleOutput output)
        {
            Chips chips = (Chips) object;
            output.writeShort((short) chips.smallBlinds);
        }
    }
}
