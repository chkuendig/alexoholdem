package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.river.compact.CompactRiverCounts;

/**
 * 13/02/14 6:34 PM
 */
public class CompactRiverCountsTest
{

    //--------------------------------------------------------------------
    public static void main(String[] args) {
        for (int i = 0;
             i < CompactRiverCounts.NUM_COUNTS;
             i++)
        {
            System.out.println(i + "\t" + CompactRiverCounts.count(i));
        }
    }


}
