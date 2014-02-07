package ao.holdem.model.canon;

import ao.holdem.model.canon.flop.Flop;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.model.canon.river.River;
import ao.holdem.model.canon.turn.Turn;
import ao.holdem.model.Round;

/**
 * User: alex
 * Date: 19-Jul-2009
 * Time: 11:31:31 AM
 */
public class Canons
{
    //--------------------------------------------------------------------
    private Canons() {}


    //--------------------------------------------------------------------
    public static long count(Round round)
    {
        switch (round)
        {
            case PREFLOP: return CanonHole.CANONS;
            case FLOP:    return Flop.CANONS;
            case TURN:    return Turn.CANONS;
            case RIVER:   return River.CANONS;
        }
        return -1;
    }


//    //--------------------------------------------------------------------
//    public static CanonRange subCanons(
//            Round round, long forRoundCanon)
//    {
//        switch (round)
//        {
//            case PREFLOP: return HoleDetails.lookup(
//                                    (char) forRoundCanon).flops();
//            case FLOP:    return FlopDetails.lookup(
//                                    (int)  forRoundCanon).turns();
//            case TURN:    return TurnRivers.rangeOf(
//                                    (int)  forRoundCanon);
//        }
//        return null;
//    }
}
