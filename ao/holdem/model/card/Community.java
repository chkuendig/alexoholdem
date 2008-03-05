package ao.holdem.model.card;

import ao.holdem.model.BettingRound;

//import javax.persistence.Embeddable;
//import javax.persistence.Enumerated;
import java.util.Arrays;

/**
 * Immutable.
 */
//@Embeddable
public class Community
{
    //--------------------------------------------------------------------
    private Card FLOP_A;
    private Card FLOP_B;
    private Card FLOP_C;
    private Card TURN;
    private Card RIVER;


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
            Card flopA,
            Card flopB,
            Card flopC,
            Card turn,
            Card river)
    {
        FLOP_A = flopA;
        FLOP_B = flopB;
        FLOP_C = flopC;
        TURN = turn;
        RIVER = river;
    }


    //--------------------------------------------------------------------
    // used for persistance purposes.
//    @Enumerated
    public Card getFlopA()
    {
        return FLOP_A;
    }
    public void setFlopA(Card flopA)
    {
        FLOP_A = flopA;
    }

//    @Enumerated
    public Card getFlopB()
    {
        return FLOP_B;
    }
    public void setFlopB(Card flopB)
    {
        FLOP_B = flopB;
    }

//    @Enumerated
    public Card getFlopC()
    {
        return FLOP_C;
    }
    public void setFlopC(Card flopC)
    {
        FLOP_C = flopC;
    }

//    @Enumerated
    public Card getTurn()
    {
        return TURN;
    }
    public void setTurn(Card turn)
    {
        TURN = turn;
    }
    
//    @Enumerated
    public Card getRiver()
    {
        return RIVER;
    }
    public void setRiver(Card river)
    {
        RIVER = river;
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

    public BettingRound round()
    {
        return hasRiver()
                ? BettingRound.RIVER
                : hasTurn()
                   ? BettingRound.TURN
                   : hasFlop()
                      ? BettingRound.FLOP
                      : BettingRound.PREFLOP;
    }


    //--------------------------------------------------------------------
    public Community addTurn(Card turn)
    {
        assert TURN == null;
        return new Community(FLOP_A, FLOP_B, FLOP_C, turn);
    }

    public Community addRiver(Card river)
    {
        assert RIVER == null;
        return new Community(FLOP_A, FLOP_B, FLOP_C, TURN, river);
    }

    public Community asOf(BettingRound round)
    {
        switch (round)
        {
            case PREFLOP:
                return new Community();
            case FLOP:
                return new Community(FLOP_A, FLOP_B, FLOP_C);
            case TURN:
                return new Community(FLOP_A, FLOP_B, FLOP_C, TURN);
            case RIVER:
                return new Community(FLOP_A, FLOP_B, FLOP_C, TURN, RIVER);
        }
        return null;
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

    public int unknownCount()
    {
        return 5 - knownCount();
    }


    //--------------------------------------------------------------------
    public boolean contains(Card card)
    {
        return FLOP_A == card ||
               FLOP_B == card ||
               FLOP_C == card ||
               TURN   == card ||
               RIVER  == card;
    }

    public boolean contains(Card.Rank rank)
    {
        return contains(Card.valueOf(rank, Card.Suit.CLUBS)) ||
               contains(Card.valueOf(rank, Card.Suit.DIAMONDS)) ||
               contains(Card.valueOf(rank, Card.Suit.HEARTS)) ||
               contains(Card.valueOf(rank, Card.Suit.SPADES));
    }

    public boolean flushPossible()
    {
        int suits[] = new int[4];

        for (Card c : asArray())
        {
            if (c == null)
            {
                suits[0]++; suits[1]++; suits[2]++; suits[3]++;
            }
            else
            {
                suits[ c.suit().ordinal() ]++;
            }
        }

        return suits[0] >= 3 || suits[1] >= 3 ||
               suits[2] >= 3 || suits[3] >= 3;
    }


    //--------------------------------------------------------------------
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
    @Override
    public String toString()
    {
        return Arrays.toString( known() );
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Community community = (Community) o;
        return FLOP_A == community.FLOP_A &&
               FLOP_B == community.FLOP_B &&
               FLOP_C == community.FLOP_C &&
               RIVER == community.RIVER &&
               TURN == community.TURN;
    }

    public int hashCode()
    {
        int result;
        result = (FLOP_A != null ? FLOP_A.hashCode() : 0);
        result = 31 * result + (FLOP_B != null ? FLOP_B.hashCode() : 0);
        result = 31 * result + (FLOP_C != null ? FLOP_C.hashCode() : 0);
        result = 31 * result + (TURN != null ? TURN.hashCode() : 0);
        result = 31 * result + (RIVER != null ? RIVER.hashCode() : 0);
        return result;
    }
}