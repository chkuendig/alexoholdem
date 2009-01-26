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
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

import java.util.Arrays;

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

    private static void lookupFlop(
            int canonFlopFrom, int canonFlopCount,
            CanonDetail[] into, int startingAt)
    {
        FlopDetailLookup.lookup(
                canonFlopFrom, canonFlopCount,
                into, startingAt);
    }


    //--------------------------------------------------------------------
    public static CanonTurnDetail lookupTurn(int canonTurn)
    {
        return TurnDetailLookup.lookup(canonTurn);
    }
    public static CanonTurnDetail[] lookupTurn(
            int canonTurnFrom, int canonTurnCount)
    {
        return TurnDetailLookup.lookup(
                canonTurnFrom, canonTurnCount);
    }
    private static void lookupTurn(
            int canonTurnFrom, int canonTurnCount,
            CanonDetail[] into, int startingAt)
    {
        TurnDetailLookup.lookup(
                canonTurnFrom, canonTurnCount,
                into, startingAt);
    }


    //--------------------------------------------------------------------
    public static CanonRiverDetail[] lookupRiver(
            CanonHoleDetail hole,
            CanonFlopDetail flop,
            CanonTurnDetail turn)
    {
        CanonDetail[] details = new CanonDetail[ 46 ];
        lookupRiver(hole.example(),
                    flop.exampleA(), flop.exampleB(), flop.exampleC(),
                    turn.example(),
                    details, 0);

        int length = 0;
        for (CanonDetail detail : details) {
            if (detail == null) break;
            length++;
        }
        return (CanonRiverDetail[])
                Arrays.copyOfRange(details, 0, length);
    }

    public static void lookupRiver(
            Hole hole, Card flopA, Card flopB, Card flopC, Card turn,
            CanonDetail[] into, int startingAt)
    {
        RiverDetailLookup.lookup(
                hole, flopA, flopB, flopC, turn,
                into, startingAt);
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
            return (CanonDetail[]) lookupTurnSub(prevCanonIndexes);
        }
    }

    //--------------------------------------------------------------------
    private static CanonRiverDetail[] lookupTurnSub(int[] turnCanons)
    {
        return new CanonRiverDetail[0];
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

            case TURN:
                CanonTurnDetail turnDetail =
                        lookupTurn( (int) canonIndex );
                return new CanonRange(turnDetail.firstCanonRiver(),
                                      (char) turnDetail.canonRiverCount());

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
