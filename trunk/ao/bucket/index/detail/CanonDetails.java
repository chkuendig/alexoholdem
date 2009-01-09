package ao.bucket.index.detail;

import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.preflop.CanonHoleLookup;
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
        textHoleDetails();
    }


    //--------------------------------------------------------------------
    private static void textHoleDetails()
    {
        for (char canonHole = 0;
                  canonHole < Hole.CANONICAL_COUNT;
                  canonHole++)
        {
            System.out.println( lookupHole(canonHole) );
        }
    }


    //--------------------------------------------------------------------
    public static CanonHoleDetail lookupHole(char canonHole)
    {
        return CanonHoleLookup.lookup( canonHole );
    }
}
