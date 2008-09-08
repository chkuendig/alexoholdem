package ao.bucket.index.iso_river;

import ao.bucket.index.iso_cards.Ordering;
import ao.bucket.index.iso_cards.wild.card.WildCard;
import ao.holdem.model.card.Card;

import static java.util.Arrays.sort;

/**
 * Date: Sep 1, 2008
 * Time: 8:36:13 PM
 */
public class IsoRiver
{
    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    private WildCard HOLE_A, HOLE_B,
                     FLOP_A, FLOP_B, FLOP_C,
                     TURN,
                     RIVER;

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

        // todo: optimize, only sort if previosly WILD, eliminate array
        WildCard wildHole[] = new WildCard[]{
                WildCard.newInstance(refined, hole[0]),
                WildCard.newInstance(refined, hole[1])};
        sort(wildHole);
        HOLE_A = wildHole[ 0 ];
        HOLE_B = wildHole[ 1 ];

        // todo: optimize, only sort if previosly WILD, eliminate array
        WildCard wildFlop[] = new WildCard[]{
                WildCard.newInstance(refined, flop[ 0 ]),
                WildCard.newInstance(refined, flop[ 1 ]),
                WildCard.newInstance(refined, flop[ 2 ])};
        sort(wildFlop);
        FLOP_A = wildFlop[ 0 ];
        FLOP_B = wildFlop[ 1 ];
        FLOP_C = wildFlop[ 2 ];

        // todo: optimize, only need new instance of it was WILD
        TURN  = WildCard.newInstance(refined, turn);
        RIVER = WildCard.newInstance(refined, river);
    }


    //--------------------------------------------------------------------
    public RiverCase riverCase()
    {
        return CASE;
    }


    //--------------------------------------------------------------------
    public int localSubIndex()
    {
        return CASE.subIndex(RIVER,
                             HOLE_A, HOLE_B,
                             FLOP_A, FLOP_B, FLOP_C,
                             TURN);
    }
}
