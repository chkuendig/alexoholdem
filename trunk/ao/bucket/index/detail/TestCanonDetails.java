package ao.bucket.index.detail;

import ao.bucket.index.detail.flop.CanonFlopDetail;
import ao.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.hole.HoleLookup;
import ao.bucket.index.turn.TurnLookup;

/**
 * Date: Jan 21, 2009
 * Time: 11:54:48 AM
 */
public class TestCanonDetails
{
    //--------------------------------------------------------------------
    private TestCanonDetails() {}


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        testHoleDetails();
//        testFlopDetails();
        testTurnDetails();
    }


    //--------------------------------------------------------------------
    public static void testHoleDetails()
    {
        for (char canonHole = 0;
                  canonHole < HoleLookup.CANONICAL_COUNT;
                  canonHole++)
        {
            System.out.println( DetailLookup.lookupHole(canonHole) );
        }
    }


    //--------------------------------------------------------------------
    public static void testFlopDetails()
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
            CanonFlopDetail details =
                    DetailLookup.lookupFlop( canonFlop );

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
    public static void testTurnDetails()
    {
        long representTotal = 0;
        long representMax   = Long.MIN_VALUE;
        long representMin   = Long.MAX_VALUE;

        long turnCountTotal = 0;
        long turnCountMax   = Long.MIN_VALUE;
        long turnCountMin   = Long.MAX_VALUE;

        for (int canonTurn = 0;
                 canonTurn < TurnLookup.CANONICAL_COUNT;
                 canonTurn++)
        {
            CanonTurnDetail details =
                    DetailLookup.lookupTurn( canonTurn );

            representTotal += details.represents();
            representMax    = Math.max(representMax,
                                       details.represents());
            representMin    = Math.min(representMin,
                                       details.represents());

            turnCountTotal += details.canonRiverCount();
            turnCountMax    = Math.max(turnCountMax,
                                       details.canonRiverCount());
            turnCountMin    = Math.min(turnCountMin,
                                       details.canonRiverCount());
        }

        System.out.println("representTotal = " + representTotal);
        System.out.println("representMax   = " + representMax);
        System.out.println("representMin   = " + representMin);

        System.out.println("turnCountTotal = " + turnCountTotal);
        System.out.println("turnCountMax   = " + turnCountMax);
        System.out.println("turnCountMin   = " + turnCountMin);
    }
}
