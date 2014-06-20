package ao.holdem.model;

/**
 * One or more chips.
 */
public class ChipStack implements Comparable<ChipStack>
{
    //------------------------------------------------------------------------
    private static final ChipStack[] CACHE_NEGATIVE = new ChipStack[ 64  ];
    private static final ChipStack[] CACHE_POSITIVE = new ChipStack[ 256 ];
    static
    {
        for (int i = 0; i < CACHE_NEGATIVE.length; i++) {
            CACHE_NEGATIVE[ i ] = new ChipStack(-(i + 1));
        }

        for (int i = 0; i < CACHE_POSITIVE.length; i++) {
            CACHE_POSITIVE[ i ] = new ChipStack(i);
        }
    }


    //-------------------------------------------------------------------------
    public static final ChipStack ZERO        = newInstance(0);
    public static final ChipStack SMALL_BLIND = newInstance(1);
    public static final ChipStack BIG_BLIND   = newInstance(2);
    public static final ChipStack SMALL_BET   = BIG_BLIND;
    public static final ChipStack BIG_BET     = newInstance(4);
    public static final ChipStack MAX_VALUE   = newInstance(Short.MAX_VALUE);

    public static ChipStack blind(boolean isSmall)
    {
        return isSmall ? SMALL_BLIND : BIG_BLIND;
    }

    public static ChipStack newInstance(int smallBlinds)
    {
        return (0 <= smallBlinds &&
                     smallBlinds < CACHE_POSITIVE.length)
                ? CACHE_POSITIVE[ smallBlinds ]
                : (0 > smallBlinds &&
                       smallBlinds > -(CACHE_NEGATIVE.length + 1))
                   ? CACHE_NEGATIVE[-(smallBlinds + 1)]
                   : new ChipStack( smallBlinds );
    }

    public static ChipStack orZero(ChipStack chips)
    {
        return chips == null
               ? ZERO : chips;
    }


    //--------------------------------------------------------------------
    private final int smallBlinds;

    private ChipStack(int smallBlinds)
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
    public ChipStack plus(ChipStack addend)
    {
        return ChipStack.newInstance(
                smallBlinds + addend.smallBlinds);
    }

    public ChipStack minus(ChipStack subtractor)
    {
        return ChipStack.newInstance(
                smallBlinds - subtractor.smallBlinds);
    }

    public ChipStack negate()
    {
        return ChipStack.newInstance(-smallBlinds);
    }

    public ChipStack split(int into)
    {
        return ChipStack.newInstance(
                smallBlinds / into);
    }
    public ChipStack remainder(int afterSplittingInto)
    {
        return minus( split(afterSplittingInto)
                        .times(afterSplittingInto) );
    }
    private ChipStack times(int factor)
    {
        return ChipStack.newInstance(
                smallBlinds * factor);
    }


    //--------------------------------------------------------------------
    public int compareTo(ChipStack o)
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

        ChipStack money = (ChipStack) o;
        return smallBlinds == money.smallBlinds;
    }

    public int hashCode()
    {
        return smallBlinds;
    }
}
