package ao.bucket.index.canon;

import ao.bucket.index.canon.flop.FlopLookup;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
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
            case PREFLOP: return HoleLookup.CANONS;
            case FLOP:    return FlopLookup.CANONS;
            case TURN:    return TurnLookup.CANONS;
            case RIVER:   return RiverLookup.CANONS;
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
