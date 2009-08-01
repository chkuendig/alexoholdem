package ao.bucket.index.detail;

import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.holdem.model.Round;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: aostrovsky
 * Date: 31-Jul-2009
 * Time: 5:28:09 PM
 *
 * Brings together a bunch of CanonRange look-up mechanisms
 */
public class RangeLookup
{
    //--------------------------------------------------------------------
    private RangeLookup() {}


    //--------------------------------------------------------------------
    public static CanonRange lookupRange(
            Round forRound, long canonIndex)
    {
        switch (forRound)
        {
            case PREFLOP:
                return DetailLookup.lookupHole(
                        (char) canonIndex).flops();

            case FLOP:
                return DetailLookup.lookupFlop(
                        (int) canonIndex).turns();

            case TURN:
                return TurnRivers.rangeOf(
                        (int) canonIndex);

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
    public static CanonRange lookupRange(
            long  fromCanonIndex,
            Round fromRound,
            Round toRound)
    {
        assert fromRound.ordinal() < toRound.ordinal();

        CanonRange range =
                lookupRange(fromRound, fromCanonIndex);
        if (fromRound.next() == toRound) return range;

        fromRound = fromRound.next();
        long from = lookupRange(fromRound, range.from()).from();
        long to   = lookupRange(fromRound,
                                range.toInclusive()).toInclusive();
        range = CanonRange.newFromTo(from, to);
        if (fromRound.next() == toRound) return range;

        fromRound = fromRound.next();
        from = lookupRange(fromRound, range.from()).from();
        to   = lookupRange(fromRound,
                           range.toInclusive()).toInclusive();
        return CanonRange.newFromTo(from, to);
    }


    //--------------------------------------------------------------------
    public static Iterable<CanonRange> lookup(
            Round parentRound,
            int   parentCanons[],
            Round toRound)
    {
        Collection<CanonRange> ranges = new ArrayList<CanonRange>();

        if (parentRound == null) {
            ranges.add( CanonRange.newFromCount(
                                     0, HoleLookup.CANONS) );
        } else {
            for (int parent : parentCanons) {
                ranges.add( lookupRange(
                        parent, parentRound, toRound));
            }
        }

        return ranges;
    }
}
