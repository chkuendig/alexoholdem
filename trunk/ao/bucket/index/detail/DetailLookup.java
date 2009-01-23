package ao.bucket.index.detail;

import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetailLookup;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.preflop.HoleDetailLookup;
import ao.bucket.index.detail.river.CanonRiverDetail;
import ao.bucket.index.detail.river.RiverDetailLookup;
import ao.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.bucket.index.detail.turn.TurnDetailLookup;
import ao.bucket.index.hole.HoleLookup;
import ao.holdem.model.Round;

/**
 * Date: Jan 9, 2009
 * Time: 2:46:12 PM
 */
public class DetailLookup
{
    //--------------------------------------------------------------------
    private DetailLookup() {}


    //--------------------------------------------------------------------
    public static CanonHoleDetail lookupHole(char canonHole)
    {
        return HoleDetailLookup.lookup( canonHole );
    }
    public static CanonHoleDetail[] lookupHole(
            char canonHoleFrom,
            char canonHoleCount)
    {
        return HoleDetailLookup.lookup(
                canonHoleFrom, canonHoleCount);
    }


    //--------------------------------------------------------------------
    public static CanonFlopDetail lookupFlop(int canonFlop)
    {
        return FlopDetailLookup.lookup( canonFlop );
    }

    public static CanonFlopDetail[] lookupFlop(
            int canonFlopFrom,
            int canonFlopCount)
    {
        return FlopDetailLookup.lookup(
                canonFlopFrom, canonFlopCount);
    }


    //--------------------------------------------------------------------
    public static CanonTurnDetail lookupTurn(int canonTurn)
    {
        return TurnDetailLookup.lookup(canonTurn);
    }
    public static CanonTurnDetail[] lookupTurn(
            int canonTurnFrom,
            int canonTurnCount)
    {
        return TurnDetailLookup.lookup(
                canonTurnFrom, canonTurnCount);
    }


    //--------------------------------------------------------------------
    public static CanonRiverDetail[] lookupRiver(
            long canonTurnFrom,
            int  canonTurnCount)
    {
        return RiverDetailLookup.lookup(
                canonTurnFrom, canonTurnCount);
    }


    //--------------------------------------------------------------------
    public static CanonDetail[][] lookupSub(
            Round prevRound, int[] prevCanonIndexes)
    {
        if (prevRound == null /*&& prevCanonIndexes.length == 0*/) {
            return new CanonDetail[][]{
                    lookupHole((char) 0,
                               (char) HoleLookup.CANONS)};
        }

        CanonDetail[][] subs =
                new CanonDetail[ prevCanonIndexes.length ][];
        for (int i = 0; i < prevCanonIndexes.length; i++)
        {
            subs[ i ] = lookupSub(prevRound, prevCanonIndexes[i]);
        }
        return subs;
    }
    public static CanonDetail[] lookupSub(
            Round prevRound, int canonIndex)
    {
        switch (prevRound)
        {
            case PREFLOP:
                CanonHoleDetail holeDetail =
                        lookupHole( (char) canonIndex );
                return lookupFlop(holeDetail.firstCanonFlop(),
                                  holeDetail.canonFlopCount());

            case FLOP:
                CanonFlopDetail flopDetail =
                        lookupFlop( canonIndex );
                return lookupTurn(flopDetail.firstCanonTurn(),
                                  flopDetail.canonTurnCount());

            case TURN:
                return new CanonDetail[0];

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
    public static CanonRange lookupRange(
            Round forRound, long canonIndex)
    {
        switch (forRound)
        {
            case PREFLOP:
                CanonHoleDetail holeDetail =
                        lookupHole( (char) canonIndex );
                return new CanonRange(holeDetail.firstCanonFlop(),
                                      holeDetail.canonFlopCount());
            case FLOP:
                CanonFlopDetail flopDetail =
                        lookupFlop( (int) canonIndex );
                return new CanonRange(flopDetail.firstCanonTurn(),
                                      flopDetail.canonTurnCount());

            case TURN:
                CanonTurnDetail turnDetail =
                        lookupTurn( (int) canonIndex );
                return new CanonRange(turnDetail.firstCanonRiver(),
                                      turnDetail.canonRiverCount());

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
//    public static CanonDetail lookup(
//            Round forRound, long canonIndex)
//    {
//        switch (forRound)
//        {
//            case PREFLOP: return lookupHole( (char) canonIndex );
//            case FLOP:    return lookupFlop( (int)  canonIndex );
//            case TURN:    return lookupTurn( (int)  canonIndex );
//        }
//        return null;
//    }


    //--------------------------------------------------------------------
//    public static CanonDetail[] lookup(
//            Round forRound,
//            long  fromCanonIndex,
//            int   canonIndexCount)
//    {
//        switch (forRound)
//        {
//            case PREFLOP: return lookupHole(
//                    (char) fromCanonIndex, (char) canonIndexCount );
//
//            case FLOP:    return lookupFlop(
//                    (int)  fromCanonIndex,        canonIndexCount );
//        }
//        return null;
//    }
}
