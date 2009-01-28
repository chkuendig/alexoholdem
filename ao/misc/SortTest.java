package ao.misc;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.preflop.HoleDetails;
import ao.bucket.index.hole.HoleLookup;

/**
 * Date: Jan 23, 2009
 * Time: 12:51:42 PM
 */
public class SortTest {
    public static void main(String[] args) {

        CanonHoleDetail[] proto =
                HoleDetails.lookup(
                        (char) 0,
                        (char) HoleLookup.CANONS);

        CanonDetail[] byStrength = proto.clone();
        CanonDetail[] byRadix    = proto.clone();


    }

    private static void testSort()
    {

    }

}
