package ao.holdem.model.card;


import ao.holdem.model.Round;
import ao.util.serial.Prototype;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.util.Arrays;

/**
 *
 */
public class Community implements Prototype<Community>
{
    //--------------------------------------------------------------------
    public static final Community PREFLOP = new Community();


    //--------------------------------------------------------------------
    private final Card FLOP_A;
    private final Card FLOP_B;
    private final Card FLOP_C;
    private final Card TURN;
    private final Card RIVER;


    //--------------------------------------------------------------------
    public Community()
    {
        this(null, null, null, null, null);
    }
    public Community(Card flopA, Card flopB, Card flopC)
    {
        this(flopA, flopB, flopC, null, null);
    }

    public Community(Card flopA, Card flopB, Card flopC, Card turn)
    {
        this(flopA, flopB, flopC, turn, null);
    }

    public Community(
            Card flopA, Card flopB, Card flopC, Card turn, Card river)
    {
        FLOP_A = flopA;
        FLOP_B = flopB;
        FLOP_C = flopC;
        TURN   = turn;
        RIVER  = river;

        validate();
    }


    //--------------------------------------------------------------------
    private void validate()
    {
        assert FLOP_A == null && FLOP_B == null && FLOP_C == null ||
               FLOP_A != null && FLOP_B != null && FLOP_C != null :
                "all 3 flop cards must be delt atomically";

        assert TURN == null || FLOP_A != null :
                "turn requires the flop to be present";

        assert RIVER == null || TURN != null :
                "river requires the turn to be present";

        assert hasRiver() &&
                 areUnique(FLOP_A, FLOP_B, FLOP_C, TURN, RIVER) ||
               hasTurn() &&
                 areUnique(FLOP_A, FLOP_B, FLOP_C, TURN) ||
               hasFlop() &&
                 areUnique(FLOP_A, FLOP_B, FLOP_C) ||
               isPreflop() :
                 "community cards cannot repeat";
    }

    private static boolean areUnique(
            Card a, Card b, Card c, Card d, Card e)
    {
        return areUnique(a, b, c, d) &&
               e != a && e != b && e != c && e != d;
    }
    private static boolean areUnique(Card a, Card b, Card c, Card d)
    {
        return areUnique(a, b, c) &&
               d != a && d != b && d != c;
    }
    private static boolean areUnique(Card a, Card b, Card c)
    {
        return a != b && a != c && b != c;
    }


    //--------------------------------------------------------------------
    public Card flopA()
    {
        return FLOP_A;
    }
    public Card flopB()
    {
        return FLOP_B;
    }
    public Card flopC()
    {
        return FLOP_C;
    }
    public Card turn()
    {
        return TURN;
    }
    public Card river()
    {
        return RIVER;
    }

    public boolean contains(Card card)
    {
        return FLOP_A == card ||
               FLOP_B == card ||
               FLOP_C == card ||
               TURN   == card ||
               RIVER  == card;
    }
    

    //--------------------------------------------------------------------
    public Round round()
    {
        return hasRiver()
                ? Round.RIVER
                : hasTurn()
                   ? Round.TURN
                   : hasFlop()
                      ? Round.FLOP
                      : Round.PREFLOP;
    }

    public boolean isPreflop()
    {
        return !hasFlop();
    }
    public boolean hasFlop()
    {
        return FLOP_A != null;
    }
    public boolean hasTurn()
    {
        return TURN != null;
    }
    public boolean hasRiver()
    {
        return RIVER != null;
    }


    //--------------------------------------------------------------------
    public Community addTurn(Card turn)
    {
        assert TURN == null && turn != null;
        return new Community(FLOP_A, FLOP_B, FLOP_C, turn);
    }

    public Community addRiver(Card river)
    {
        assert RIVER == null && river != null;
        return new Community(FLOP_A, FLOP_B, FLOP_C, TURN, river);
    }


    //--------------------------------------------------------------------
    public Community asOf(Round round)
    {
//        return (round == null)
//                ? Round.RIVER.ofCommunity( this )
//                : round.ofCommunity( this );
        return round.ofCommunity( this );
    }

    public Community asPreflop()
    {
        return PREFLOP;
    }
    public Community asFlop()
    {
        assert hasFlop() : "no flop cards available";
        return hasTurn()
                ? new Community(FLOP_A, FLOP_B, FLOP_C)
                : this;
    }
    public Community asTurn()
    {
        assert hasTurn() : "no turn cards available";
        return hasRiver()
               ? new Community(FLOP_A, FLOP_B, FLOP_C, TURN)
               : this;
    }
    public Community asRiver()
    {
        assert hasRiver() : "no river cards available";
        return this;
    }


    //--------------------------------------------------------------------
    public int knownCount()
    {
        return hasRiver()
                ? 5
                : hasTurn()
                   ? 4
                   : hasFlop()
                      ? 3 : 0;
    }

    public Card[] known()
    {
        return Arrays.copyOf(asArray(), knownCount());
    }

    private Card[] asArray()
    {
        Card known[] = new Card[5];
        switch (knownCount())
        {
            case 5: known[4] = RIVER;
            case 4: known[3] = TURN;
            case 3: known[2] = FLOP_C;
                    known[1] = FLOP_B;
                    known[0] = FLOP_A;
        }
        return known;
    }


    //--------------------------------------------------------------------
    public Community prototype()
    {
        return this;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return Arrays.toString( known() );
    }

    public boolean equals(Object o)
    {
        //if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        Community community = (Community) o;
        return FLOP_A == community.FLOP_A &&
               FLOP_B == community.FLOP_B &&
               FLOP_C == community.FLOP_C &&
               RIVER  == community.RIVER  &&
               TURN   == community.TURN;
    }

    public int hashCode()
    {
        int result;
        result =               (FLOP_A != null ? FLOP_A.hashCode() : 0);
        result = 31 * result + (FLOP_B != null ? FLOP_B.hashCode() : 0);
        result = 31 * result + (FLOP_C != null ? FLOP_C.hashCode() : 0);
        result = 31 * result + (TURN   != null ? TURN.hashCode()   : 0);
        result = 31 * result + (RIVER  != null ? RIVER.hashCode()  : 0);
        return result;
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleBinding
    {
        public Community entryToObject(TupleInput input)
        {
            Card flopA = Card.BINDING.entryToObject(input);
            Card flopB = Card.BINDING.entryToObject(input);
            Card flopC = Card.BINDING.entryToObject(input);
            Card turn  = Card.BINDING.entryToObject(input);
            Card river = Card.BINDING.entryToObject(input);

            return new Community(flopA, flopB, flopC, turn, river);
        }

        public void objectToEntry(Object object, TupleOutput output)
        {
            Community community = (Community) object;

            Card.BINDING.objectToEntry(community.FLOP_A, output);
            Card.BINDING.objectToEntry(community.FLOP_B, output);
            Card.BINDING.objectToEntry(community.FLOP_C, output);
            Card.BINDING.objectToEntry(community.TURN,   output);
            Card.BINDING.objectToEntry(community.RIVER,  output);
        }
    }
}
