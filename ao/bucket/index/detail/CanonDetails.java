package ao.bucket.index.detail;

import ao.bucket.index.detail.flop.CanonFlopDetail;
import ao.bucket.index.detail.flop.CanonFlopLookup;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.preflop.CanonHoleLookup;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.Round;
import ao.holdem.model.card.Hole;

/**
 * Date: Jan 9, 2009
 * Time: 2:46:12 PM
 */
public class CanonDetails
{
    //--------------------------------------------------------------------
    private CanonDetails() {}


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        testHoleDetails();
        testFlopDetails();
    }


    //--------------------------------------------------------------------
    protected static void testHoleDetails()
    {
        for (char canonHole = 0;
                  canonHole < Hole.CANONICAL_COUNT;
                  canonHole++)
        {
            System.out.println( lookupHole(canonHole) );
        }
    }


    //--------------------------------------------------------------------
    protected static void testFlopDetails()
    {
        long sentinalCount  = 0;

        long representTotal = 0;
        long representMax   = Long.MIN_VALUE;
        long representMin   = Long.MAX_VALUE;

        long turnCountTotal = 0;
        long turnCountMax   = Long.MIN_VALUE;
        long turnCountMin   = Long.MAX_VALUE;

        for (int canonFlop = 0;
                 canonFlop < FlopLookup.CANONICAL_COUNT;
                 canonFlop++)
        {
            CanonFlopDetail details = lookupFlop( canonFlop );

            representTotal += details.represents();
            representMax    = Math.max(representMax,
                                       details.represents());
            representMin    = Math.min(representMin,
                                       details.represents());

            turnCountTotal += details.canonTurnCount();
            turnCountMax    = Math.max(turnCountMax,
                                       details.canonTurnCount());
            turnCountMin    = Math.min(turnCountMin,
                                       details.canonTurnCount());

            if (details.equals( CanonFlopDetail.SENTINAL ))
            {
                sentinalCount++;
            }
        }

        System.out.println("sentinalCount  = " + sentinalCount);

        System.out.println("representTotal = " + representTotal);
        System.out.println("representMax   = " + representMax);
        System.out.println("representMin   = " + representMin);

        System.out.println("turnCountTotal = " + turnCountTotal);
        System.out.println("turnCountMax   = " + turnCountMax);
        System.out.println("turnCountMin   = " + turnCountMin);
    }


    //--------------------------------------------------------------------
    public static CanonHoleDetail lookupHole(char canonHole)
    {
        return CanonHoleLookup.lookup( canonHole );
    }
    public static CanonHoleDetail[] lookupHole(
            char canonHoleFrom,
            char canonHoleCount)
    {
        return CanonHoleLookup.lookup(
                canonHoleFrom, canonHoleCount);
    }


    //--------------------------------------------------------------------
    public static CanonFlopDetail lookupFlop(int canonFlop)
    {
        return CanonFlopLookup.lookup( canonFlop );
    }

    public static CanonFlopDetail[] lookupFlop(
            int canonFlopFrom,
            int canonFlopCount)
    {
        return CanonFlopLookup.lookup(
                canonFlopFrom, canonFlopCount);
    }


    //--------------------------------------------------------------------
    public static CanonDetail[][] lookupSub(
            Round prevRound, int[] canonIndexes)
    {
        CanonDetail[][] subs = new CanonDetail[ canonIndexes.length ][];
        for (int i = 0; i < canonIndexes.length; i++)
        {
            subs[ i ] = lookupSub(prevRound, canonIndexes[i]);
        }
        return subs;
    }
    public static CanonDetail[] lookupSub(
            Round prevRound, int canonIndex)
    {
        if (prevRound == null)
        {
            return lookupHole((char) 0,
                              (char) Hole.CANONICAL_COUNT );
        }

        switch (prevRound)
        {
            case PREFLOP:
                CanonHoleDetail holeDetail =
                        lookupHole( (char) canonIndex );
                return lookupFlop(holeDetail.firstCanonFlop(),
                                  holeDetail.canonFlopCount());

            case FLOP:
                return new CanonDetail[0];

            case TURN:
                return new CanonDetail[0];

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    public static CanonDetail lookup(
            Round forRound, long canonIndex)
    {
        switch (forRound)
        {
            case PREFLOP: return lookupHole( (char) canonIndex );
            case FLOP:    return lookupFlop( (int)  canonIndex );
        }
        return null;
    }


    //--------------------------------------------------------------------
    public static CanonDetail[] lookup(
            Round forRound,
            long  fromCanonIndex,
            int   canonIndexCount)
    {
        switch (forRound)
        {
            case PREFLOP: return lookupHole(
                    (char) fromCanonIndex, (char) canonIndexCount );

            case FLOP:    return lookupFlop(
                    (int)  fromCanonIndex,        canonIndexCount );
        }
        return null;
    }
}
