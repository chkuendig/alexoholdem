package ao.bucket.index.detail;

import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.preflop.HoleDetails;
import ao.bucket.index.detail.river.CanonRiverDetail;
import ao.bucket.index.detail.river.RiverDetails;
import ao.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.bucket.index.hole.HoleLookup;
import ao.holdem.model.Round;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        return HoleDetails.lookup( canonHole );
    }
    public static CanonHoleDetail[] lookupHole(
            char canonHoleFrom,
            char canonHoleCount)
    {
        return HoleDetails.lookup(
                canonHoleFrom, canonHoleCount);
    }


    //--------------------------------------------------------------------
    public static CanonFlopDetail lookupFlop(int canonFlop)
    {
        return FlopDetails.lookup( canonFlop );
    }

    public static CanonFlopDetail[] lookupFlop(
            int canonFlopFrom,
            int canonFlopCount)
    {
        return FlopDetails.lookup(
                canonFlopFrom, canonFlopCount);
    }

    private static void lookupFlop(
            int canonFlopFrom, int canonFlopCount,
            CanonDetail[] into, int startingAt)
    {
        FlopDetails.lookup(
                canonFlopFrom, canonFlopCount,
                into, startingAt);
    }


    //--------------------------------------------------------------------
    public static CanonTurnDetail lookupTurn(int canonTurn)
    {
        return TurnDetails.lookup(canonTurn);
    }
    public static CanonTurnDetail[] lookupTurn(
            int canonTurnFrom, int canonTurnCount)
    {
        return TurnDetails.lookup(
                canonTurnFrom, canonTurnCount);
    }
    private static void lookupTurn(
            int canonTurnFrom, int canonTurnCount,
            CanonDetail[] into, int startingAt)
    {
        TurnDetails.lookup(
                canonTurnFrom, canonTurnCount,
                into, startingAt);
    }


    //--------------------------------------------------------------------
    public static Collection<CanonRiverDetail> lookupRiver(int canonTurn)
    {
        return RiverDetails.lookup( canonTurn );
    }


    //--------------------------------------------------------------------
    public static CanonDetail[] lookupSub(
            Round prevRound, int[] prevCanonIndexes)
    {
        assert prevRound != Round.RIVER;

        if (prevRound == null /*&& prevCanonIndexes.length == 0*/) {
            return lookupHole((char) 0,
                              (char) HoleLookup.CANONS);
        } else if (prevRound.ordinal() < Round.TURN.ordinal()) {
            return lookupPreTurnSub(prevRound, prevCanonIndexes);
        } else {
            return lookupTurnSub(prevCanonIndexes);
        }
    }

    //--------------------------------------------------------------------
    private static CanonRiverDetail[] lookupTurnSub(int[] turnCanons)
    {
        List<CanonRiverDetail> details =
                new ArrayList<CanonRiverDetail>();
        for (int canonTurn : turnCanons) {
            details.addAll( lookupRiver(canonTurn) );
        }
        return details.toArray(new CanonRiverDetail[details.size()]);
    }

    //--------------------------------------------------------------------
    private static CanonDetail[] lookupPreTurnSub(
            Round prevRound, int[] prevCanonIndexes)
    {
        int          size   = 0;
        CanonRange[] ranges = new CanonRange[ prevCanonIndexes.length ];
        for (int i = 0; i < prevCanonIndexes.length; i++)
        {
            ranges[i]  = lookupRange(prevRound, prevCanonIndexes[i]);
            size      += ranges[i].canonIndexCount();
        }

        int           offset = 0;
        CanonDetail[] subs   = new CanonDetail[ size ];
        for (CanonRange range : ranges)
        {
            lookupFlopTurnDetails(
                    prevRound.next(), range, subs, offset);
            offset += range.canonIndexCount();
        }
        return subs;
    }
    private static void lookupFlopTurnDetails(
            Round round, CanonRange range,
            CanonDetail[] into, int startingAt)
    {
        switch (round)
        {
            case FLOP:
                lookupFlop((int) range.fromCanonIndex(),
                           range.canonIndexCount(),
                           into, startingAt);
                return;

            case TURN:
                lookupTurn((int) range.fromCanonIndex(),
                           range.canonIndexCount(),
                           into, startingAt);
                return;

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
                                      (char) flopDetail.canonTurnCount());

//            case TURN:
//                CanonTurnDetail turnDetail =
//                        lookupTurn( (int) canonIndex );
//                return new CanonRange(turnDetail.firstCanonRiver(),
//                                      (char) turnDetail.canonRiverCount());

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
