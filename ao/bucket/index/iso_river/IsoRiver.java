package ao.bucket.index.iso_river;

import ao.bucket.index.iso_cards.Ordering;
import ao.holdem.model.card.Card;

/**
 * Date: Sep 1, 2008
 * Time: 8:36:13 PM
 */
public class IsoRiver
{
    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    private RiverCase CASE;


    //--------------------------------------------------------------------
    public IsoRiver(Ordering turnOrder,
                    Card     hole[],
                    Card     flop[],
                    Card     turn,
                    Card     river)
    {
        Ordering byRiver = Ordering.suited(river.suit());
        Ordering refined = turnOrder.refine( byRiver );

        int count = 0;
        if (river.suit() == hole[0].suit()) count++;
        if (river.suit() == hole[1].suit()) count++;
        if (river.suit() == flop[0].suit()) count++;
        if (river.suit() == flop[1].suit()) count++;
        if (river.suit() == flop[2].suit()) count++;
        if (river.suit() ==    turn.suit()) count++;

        CASE = RiverCase.values()[ count ];

        refined.asWild( hole[0].suit() );
        
    }


    //--------------------------------------------------------------------
    public RiverCase riverCase()
    {
        return CASE;
    }
}
