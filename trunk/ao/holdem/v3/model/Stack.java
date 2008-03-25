package ao.holdem.v3.model;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * One or more chips.
 */
public class Stack implements Comparable<Stack>
{
    //--------------------------------------------------------------------
    private static final Stack CACHE_NEGATIVE[] = new Stack[ 64  ];
    private static final Stack CACHE_POSITIVE[] = new Stack[ 256 ];
    static
    {
        for (int i = 0; i < CACHE_NEGATIVE.length; i++)
            CACHE_NEGATIVE[ i ] = new Stack(-i);

        for (int i = 0; i < CACHE_POSITIVE.length; i++)
            CACHE_POSITIVE[ i ] = new Stack(i);
    }


    //--------------------------------------------------------------------
    public static final Stack ZERO        = newInstance(0);
    public static final Stack SMALL_BLIND = newInstance(1);
    public static final Stack BIG_BLIND   = newInstance(2);
    public static final Stack SMALL_BET   = BIG_BLIND;
    public static final Stack BIG_BET     = newInstance(4);
    public static final Stack MAX_VALUE   = newInstance(Short.MAX_VALUE);

    public static Stack blind(boolean isSmall)
    {
        return isSmall ? SMALL_BLIND : BIG_BLIND;
    }

    public static Stack newInstance(int smallBlinds)
    {
        return (0 <= smallBlinds &&
                     smallBlinds < CACHE_POSITIVE.length)
                ? CACHE_POSITIVE[ smallBlinds ]
                : (0 > smallBlinds &&
                       smallBlinds >= -(CACHE_NEGATIVE.length + 1))
                   ? CACHE_NEGATIVE[-(smallBlinds + 1)]
                   : new Stack( smallBlinds );
    }


    //--------------------------------------------------------------------
    private final int smallBlinds;

    private Stack(int smallBlinds)
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
    public Stack plus(Stack addend)
    {
        return Stack.newInstance(
                smallBlinds + addend.smallBlinds);
    }

    public Stack minus(Stack subtractor)
    {
        return Stack.newInstance(
                smallBlinds - subtractor.smallBlinds);
    }

    public Stack negate()
    {
        return Stack.newInstance(-smallBlinds);
    }

    public Stack split(int into)
    {
        return Stack.newInstance(
                smallBlinds / into);
    }
    public Stack remainder(int afterSplittingInto)
    {
        return minus( split(afterSplittingInto)
                        .times(afterSplittingInto) );
    }
    private Stack times(int factor)
    {
        return Stack.newInstance(
                smallBlinds * factor);
    }


    //--------------------------------------------------------------------
    public int compareTo(Stack o)
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

        Stack money = (Stack) o;
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
        public Stack entryToObject(TupleInput input)
        {
            short smallBlinds = input.readShort();
            return Stack.newInstance( smallBlinds );
        }

        public void objectToEntry(Object      object,
                                  TupleOutput output)
        {
            Stack stack = (Stack) object;
            output.writeShort((short) stack.smallBlinds);
        }
    }
}
