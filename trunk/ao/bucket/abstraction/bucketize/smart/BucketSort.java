package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.LongByteList;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.preflop.HoleOdds;
import ao.bucket.index.detail.river.compact.CompactRiverProbabilities;
import ao.bucket.index.detail.river.compact.MemProbCounts;

import java.util.Arrays;

/**
 * User: aostrovsky
 * Date: 26-Jul-2009
 * Time: 2:59:49 PM
 */
public class BucketSort
{
    //--------------------------------------------------------------------
    private BucketSort() {}


    //--------------------------------------------------------------------
    public static void sortRiver(
            LongByteList riverBuckets, int nBuckets)
    {
        IndexedValue sortable[] = new IndexedValue[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortable[i] = new IndexedValue(i);
        }

        for (long i = 0; i < RiverLookup.CANONS; i++)
        {
            sortable[ riverBuckets.get(i) ].add(
                    (double) MemProbCounts.compactProb(i) /
                            CompactRiverProbabilities.COUNT);
        }

        Arrays.sort(sortable);

        byte sortedBuckets[] = new byte[ nBuckets ];
        for (byte i = 0; i < nBuckets; i++)
        {
            sortedBuckets[ sortable[i].index ] = i;
        }

        for (long i = 0; i < RiverLookup.CANONS; i++)
        {
            riverBuckets.set(i,
                    sortedBuckets[
                            riverBuckets.get(i)]);
        }
    }


    //--------------------------------------------------------------------
    public static void sortPreFlop(
            LongByteList holeBuckets, int nBuckets)
    {
        IndexedValue sortable[] = new IndexedValue[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortable[i] = new IndexedValue(i);
        }

        for (int i = 0; i < HoleLookup.CANONS; i++)
        {
            sortable[ holeBuckets.get(i) ].add(
                    HoleOdds.lookup(i).strengthVsRandom());
        }

        Arrays.sort(sortable);

        byte sortedBuckets[] = new byte[ nBuckets ];
        for (byte i = 0; i < nBuckets; i++)
        {
            sortedBuckets[ sortable[i].index ] = i;
        }

        for (int i = 0; i < HoleLookup.CANONS; i++)
        {
            holeBuckets.set(i,
                    sortedBuckets[
                            holeBuckets.get(i)]);
        }
    }


    //--------------------------------------------------------------------
    private static class IndexedValue
            implements Comparable<IndexedValue>
    {
        public final int    index;
        public       long   count;
        public       double value;

        public IndexedValue(int canonIndex)
        {
            index = canonIndex;
        }

        public void add(double val)
        {
            value += val;
            count++;
        }

        public double average()
        {
            return value / count;
        }

        public int compareTo(IndexedValue o)
        {
            return Double.compare(average(), o.average());
        }
    }
}