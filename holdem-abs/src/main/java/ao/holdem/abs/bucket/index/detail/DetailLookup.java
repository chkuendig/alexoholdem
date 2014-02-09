package ao.holdem.abs.bucket.index.detail;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.canon.hole.CanonHole;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.preflop.CanonHoleDetail;
import ao.holdem.abs.bucket.index.detail.preflop.HoleDetails;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.range.RangeLookup;
import ao.holdem.abs.bucket.index.detail.river.compact.MemProbCounts;
import ao.holdem.abs.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.holdem.abs.bucket.index.detail.turn.TurnDetails;
import ao.holdem.abs.bucket.index.detail.turn.TurnRivers;
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
    public static byte riverBucketCount(
            BucketTree bucketTree,
            int[]      turnCanonIndexes)
    {
        byte maxBucket = -1;
        for (int turnIndex : turnCanonIndexes)
        {
            CanonRange rivers = TurnRivers.rangeOf( turnIndex );
            for (long r  = rivers.toInclusive();
                      r >= rivers.from();
                      r--)
            {
                maxBucket = (byte) Math.max(
                        maxBucket, bucketTree.getRiver(r));
            }
        }
        return (byte)(maxBucket + 1);
    }


    //--------------------------------------------------------------------
    public static CanonDetail[] lookupSub(
            Round prevRound, int[] prevCanonIndexes)
    {
        assert prevRound != Round.RIVER;

        if (prevRound == null /*&& prevCanonIndexes.length == 0*/) {
            return lookupHole((char) 0,
                              (char) CanonHole.CANONS);
        } else if (prevRound.ordinal() < Round.TURN.ordinal()) {
            return lookupPreTurnSub(prevRound, prevCanonIndexes);
        } else {
//            return lookupTurnSub(prevCanonIndexes);
            return null;
        }
    }

    public static CanonDetail[] lookupPreRiver(
            Round round, int[] canonIndexes)
    {
        CanonDetail details[] = new CanonDetail[ canonIndexes.length ];
        for (int i = 0; i < canonIndexes.length; i++) {
            details[i] = lookupPreRiver(round, canonIndexes[i]);
        }
        return details;
    }
    public static CanonDetail lookupPreRiver(Round round, int canonIndex)
    {
        switch (round)
        {
            case TURN:    return lookupTurn(canonIndex);
            case FLOP:    return lookupFlop(canonIndex);
            case PREFLOP: return lookupHole((char) canonIndex);
        }
        throw new IllegalArgumentException("can't look-up " + round);
    }

    //--------------------------------------------------------------------
//    private static CanonRiverDetail[] lookupTurnSub(int[] turnCanons)
//    {
//        List<CanonRiverDetail> details =
//                new ArrayList<CanonRiverDetail>();
//        for (int canonTurn : turnCanons) {
//            details.addAll(
//                    RiverExamples.compact( canonTurn ));
//        }
//        return details.toArray(new CanonRiverDetail[details.size()]);
//    }
//
//    public static void lookupSub(
//            Round prevRound, int[] prevCanonIndexes,
//            Traverser<CanonDetail> visit)
//    {
//        assert prevRound == Round.TURN
//                : "support implemented only for river details";
//
////        Progress p = new Progress(prevCanonIndexes.length * 50);
//        for (int canonTurn : prevCanonIndexes) {
//            RiverExamples.compact( canonTurn, visit );
////            p.checkpoint();
//        }
//    }


    //--------------------------------------------------------------------
    private static CanonDetail[] lookupPreTurnSub(
            Round prevRound, int[] prevCanonIndexes)
    {
        int          size   = 0;
        CanonRange[] ranges = new CanonRange[ prevCanonIndexes.length ];
        for (int i = 0; i < prevCanonIndexes.length; i++)
        {
            ranges[i]  = RangeLookup.lookupRange(
                    prevRound, prevCanonIndexes[i]);
            size      += ranges[i].count();
        }

        int           offset = 0;
        CanonDetail[] subs   = new CanonDetail[ size ];
        for (CanonRange range : ranges)
        {
            lookupFlopTurnDetails(
                    prevRound.next(), range, subs, offset);
            offset += range.count();
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
                lookupFlop((int) range.from(),
                           (int) range.count(),
                           into, startingAt);
                return;

            case TURN:
                lookupTurn((int) range.from(),
                           (int) range.count(),
                           into, startingAt);
                return;

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
    // @return the number of real hands that the given
    //          canonical hand index represents
    public static int lookupRepresentation(
            Round forRound, long canonIndex)
    {
        switch (forRound)
        {
            case PREFLOP:
                return HoleDetails.lookup((char) canonIndex).represents();

            case FLOP:
                return FlopDetails.lookup((int)  canonIndex).represents();

            case TURN:
                return TurnDetails.lookup((int)  canonIndex).represents();

            case RIVER:
                return MemProbCounts.riverCount( canonIndex );
        }
        return -1;
    }
}
