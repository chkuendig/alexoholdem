package ao.holdem.canon.turn;

import ao.holdem.canon.CanonIndexed;
import ao.holdem.canon.card.CanonCard;
import ao.holdem.canon.card.CanonSuit;
import ao.holdem.canon.card.Order;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.hole.CanonHole;
import ao.holdem.canon.river.River;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;

/**
 * Date: Aug 27, 2008
 * Time: 10:53:23 AM
 *
 * suit isomorphic hand at turn
 */
public class Turn implements CanonIndexed
{
    //--------------------------------------------------------------------
    public  static final int    CANONS = 55190538;


    //--------------------------------------------------------------------
    private final CanonCard HOLE[],
                            TURN;
    private final Flop      FLOP_CARDS;
    private final Card      TURN_CARD;
    private final Order     ORDER;
    private       int       canonIndex = -1;


    //--------------------------------------------------------------------
    public Turn(Flop flop,
                Card turn)
    {
        ORDER       = flop.refineOrder(
                         Order.suited(turn.suit()));
        HOLE        = flop.refineHole(ORDER);
//        FLOP        = flop.refineFlop(ORDER);
        TURN        = ORDER.asCanon(turn);
        FLOP_CARDS  = flop;
        TURN_CARD   = turn;
    }


    //--------------------------------------------------------------------
    public River addRiver(Card river)
    {
        return new River(this, river);
    }

    public Order refineOrder(Order with)
    {
        return ORDER.refine(with);
    }

    public CanonCard[] refineHole(Order with)
    {
        return FLOP_CARDS.refineHole(HOLE, with);
    }

    public CanonCard[] refineFlop(Order with)
    {
        return FLOP_CARDS.refineFlop(with);
    }

    public CanonCard refineTurn(Order with)
    {
        return TURN.isWild()
               ? with.asCanon(TURN_CARD)
               : TURN;
    }


    //--------------------------------------------------------------------
    public int canonIndex()
    {
        if (canonIndex == -1)
        {
            canonIndex = computeCanonIndex(FLOP_CARDS.canonIndex());
        }
        return canonIndex;
    }
    public long packedCanonIndex()
    {
        return canonIndex();
    }

    private int computeCanonIndex(int flopIndex)
    {
        return TurnLookup.canonIndex(flopIndex, TURN);
    }

    public CanonCard turnCard()
    {
        return TURN;
    }
    public CanonSuit turnSuit()
    {
        return TURN.suit();
    }


    //--------------------------------------------------------------------
    public Flop flop()
    {
        return FLOP_CARDS;
    }

    public CanonHole hole()
    {
        return FLOP_CARDS.hole();
    }
    
    public Community community()
    {
        return FLOP_CARDS.community().addTurn( TURN_CARD );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
//        return Arrays.toString(HOLE) +
//               Arrays.toString(FLOP) +
//               "[" + TURN + "]";
        return FLOP_CARDS + "\t" + TURN_CARD;
    }
}
