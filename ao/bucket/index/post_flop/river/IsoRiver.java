package ao.bucket.index.post_flop.river;

/**
 * Date: Sep 1, 2008
 * Time: 8:36:13 PM
 */
public class IsoRiver
{
//    //--------------------------------------------------------------------
//
//
//    //--------------------------------------------------------------------
//    private CanonCard HOLE_A, HOLE_B,
//                     FLOP_A, FLOP_B, FLOP_C,
//                     TURN,
//                     RIVER;
//
//    private PostFlopCase CASE;
//
//
//    //--------------------------------------------------------------------
//    public IsoRiver(Order turnOrder,
//                    Card     hole[],
//                    Card     flop[],
//                    Card     turn,
//                    Card     river)
//    {
//        Order byRiver = Order.suited(river.suit());
//        Order refined = turnOrder.refine( byRiver );
//
//        int count = 0;
//        if (river.suit() == hole[0].suit()) count++;
//        if (river.suit() == hole[1].suit()) count++;
//        if (river.suit() == flop[0].suit()) count++;
//        if (river.suit() == flop[1].suit()) count++;
//        if (river.suit() == flop[2].suit()) count++;
//        if (river.suit() ==    turn.suit()) count++;
//
//        CASE = PostFlopCase.values()[ count ];
//
//        refined.asWild( hole[0].suit() );
//
//        // todo: optimize, only sort if previosly WILD, eliminate array
//        CanonCard wildHole[] = new CanonCard[]{
//                CanonCard.newInstance(refined, hole[0]),
//                CanonCard.newInstance(refined, hole[1])};
//        sort(wildHole);
//        HOLE_A = wildHole[ 0 ];
//        HOLE_B = wildHole[ 1 ];
//
//        // todo: optimize, only sort if previosly WILD, eliminate array
//        CanonCard wildFlop[] = new CanonCard[]{
//                CanonCard.newInstance(refined, flop[ 0 ]),
//                CanonCard.newInstance(refined, flop[ 1 ]),
//                CanonCard.newInstance(refined, flop[ 2 ])};
//        sort(wildFlop);
//        FLOP_A = wildFlop[ 0 ];
//        FLOP_B = wildFlop[ 1 ];
//        FLOP_C = wildFlop[ 2 ];
//
//        // todo: optimize, only need new instance of it was WILD
//        TURN  = CanonCard.newInstance(refined, turn);
//        RIVER = CanonCard.newInstance(refined, river);
//    }
//
//
//    //--------------------------------------------------------------------
//    public PostFlopCase riverCase()
//    {
//        return CASE;
//    }
//
//
//    //--------------------------------------------------------------------
//    public int localSubIndex()
//    {
//        return CASE.subIndex(RIVER,
//                             HOLE_A, HOLE_B,
//                             FLOP_A, FLOP_B, FLOP_C,
//                             TURN);
//    }
}
