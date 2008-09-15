package ao.bucket.index.post_flop;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.Order;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.post_flop.common.PostFlopCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

import java.util.Arrays;

/**
 * Date: Sep 12, 2008
 * Time: 5:53:54 PM
 */
public class FastIsoTurn
{
    //--------------------------------------------------------------------
    private final Order        ORDER;
    private final CanonCard    HOLE[];
    private final CanonCard    FLOP[];
    private final CanonCard    TURN;
    private final PostFlopCase CASE;


    //--------------------------------------------------------------------
    public FastIsoTurn(Hole    hole,
                       Card    flopA,
                       Card    flopB,
                       Card    flopC,
                       Flop isoFlop,
                       Card    turn)
    {
        ORDER = isoFlop.order().refine(
                    Order.suited(turn.suit()));

        // compute hole
        if (isoFlop.hasWildHole())
        {
            HOLE = new CanonCard[]{
                    ORDER.asWild( hole.a() ),
                    ORDER.asWild( hole.b() )};
            Arrays.sort(HOLE);
        }
        else
        {
            HOLE = isoFlop.hole();
        }

        // compute flop
        if (isoFlop.hasWildFlop())
        {
            FLOP = new CanonCard[]{
                    ORDER.asWild(flopA),
                    ORDER.asWild(flopB),
                    ORDER.asWild(flopC)};
            Arrays.sort(FLOP);
        }
        else
        {
            FLOP = isoFlop.flop();
        }

        TURN = ORDER.asWild(turn);
        CASE = computePostFlopCase();
    }


    //--------------------------------------------------------------------
    private PostFlopCase computePostFlopCase()
    {
        int count = 0;
        if (TURN.suit() == HOLE[0].suit()) count++;
        if (TURN.suit() == HOLE[1].suit()) count++;
        if (TURN.suit() == HOLE[0].suit()) count++;
        if (TURN.suit() == HOLE[1].suit()) count++;
        if (TURN.suit() == HOLE[2].suit()) count++;
        return PostFlopCase.VALUES[ count ];
    }


    //--------------------------------------------------------------------
    public int localSubIndex()
    {
        return CASE.subIndex(HOLE,
                             FLOP,
                             TURN);
    }
}
