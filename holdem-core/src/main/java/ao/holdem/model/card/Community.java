package ao.holdem.model.card;


import ao.holdem.model.Round;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 */
public class Community
{
    //--------------------------------------------------------------------
    public static final Community PREFLOP = new Community();


    //--------------------------------------------------------------------
    private final Card flopA;
    private final Card flopB;
    private final Card flopC;
    private final Card turn;
    private final Card river;


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
        this.flopA = flopA;
        this.flopB = flopB;
        this.flopC = flopC;
        this.turn = turn;
        this.river = river;

        validate();
    }


    //--------------------------------------------------------------------
    private void validate()
    {
        if (! (flopA == null && flopB == null && flopC == null ||
               flopA != null && flopB != null && flopC != null)) {
            throw new IllegalArgumentException("All 3 flop cards must be dealt atomically");
        }

        if (! (turn == null || flopA != null)) {
            throw new IllegalArgumentException("Turn requires the flop to be present");
        }

        if (! (river == null || turn != null)) {
            throw new IllegalArgumentException("River requires the turn to be present");
        }

        if (! (hasRiver() && areUnique(flopA, flopB, flopC, turn, river) ||
                hasTurn() && areUnique(flopA, flopB, flopC, turn) ||
                hasFlop() && areUnique(flopA, flopB, flopC) ||
                isPreflop())) {
            throw new IllegalArgumentException("Community cards must be unique");
        }
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
        return flopA;
    }
    public Card flopB()
    {
        return flopB;
    }
    public Card flopC()
    {
        return flopC;
    }
    public Card turn()
    {
        return turn;
    }
    public Card river()
    {
        return river;
    }

    public boolean contains(Card card)
    {
        return flopA == card ||
               flopB == card ||
               flopC == card ||
               turn == card ||
               river == card;
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
        return flopA != null;
    }
    public boolean hasTurn()
    {
        return turn != null;
    }
    public boolean hasRiver()
    {
        return river != null;
    }


    //--------------------------------------------------------------------
    public Community addFlop(Card flopA, Card flopB, Card flopC) {
        if (flopA == null || flopB == null || flopC == null) {
            throw new NullPointerException();
        }
        if (this.flopA != null || this.flopB != null || this.flopC != null) {
            throw new IllegalArgumentException("Flop already dealt");
        }
        return new Community(flopA, flopB, flopC);
    }

    public Community addTurn(Card turn)
    {
        if (turn == null) {
            throw new NullPointerException();
        }
        if (this.turn != null) {
            throw new IllegalArgumentException("Turn already dealt");
        }

        return new Community(flopA, flopB, flopC, turn);
    }

    public Community addRiver(Card river)
    {
        if (river == null) {
            throw new NullPointerException();
        }
        if (this.river != null) {
            throw new IllegalArgumentException("River already dealt");
        }

        return new Community(flopA, flopB, flopC, turn, river);
    }


    //--------------------------------------------------------------------
    public Community asOf(Round round)
    {
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
                ? new Community(flopA, flopB, flopC)
                : this;
    }
    public Community asTurn()
    {
        assert hasTurn() : "no turn cards available";
        return hasRiver()
               ? new Community(flopA, flopB, flopC, turn)
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
        return hasRiver() ? 5 :
                hasTurn() ? 4 :
                hasFlop() ? 3 : 0;
    }

    public Card[] toArray()
    {
        return Arrays.copyOf(toShowdownArray(), knownCount());
    }

    private Card[] toShowdownArray()
    {
        Card known[] = new Card[5];
        switch (knownCount())
        {
            case 5: known[4] = river;
            case 4: known[3] = turn;
            case 3: known[2] = flopC;
                    known[1] = flopB;
                    known[0] = flopA;
        }
        return known;
    }

    public Set<Card> toSet() {
        EnumSet<Card> copy = EnumSet.noneOf(Card.class);
        copy.addAll(Arrays.asList(toArray()));
        return copy;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return Arrays.toString( toArray() );
    }

    public boolean equals(Object o)
    {
        if (! (o instanceof Community)) {
            return false;
        }

        Community that = (Community) o;
        return flopA == that.flopA &&
                flopB == that.flopB &&
                flopC == that.flopC &&
                turn == that.turn &&
                river == that.river;
    }

    public int hashCode() {
        return Arrays.hashCode(new Card[]{
                flopA, flopB, flopC, turn, river});
    }
}
