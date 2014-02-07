package ao.holdem.ai.abstraction.bucketize.smart;

import ao.holdem.ai.abstraction.access.tree.BucketTree;
import ao.holdem.ai.abstraction.access.tree.LongByteList;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.model.canon.river.River;
import ao.holdem.engine.detail.preflop.HoleOdds;
import ao.holdem.engine.detail.range.CanonRange;
import ao.holdem.engine.detail.range.RangeLookup;
import ao.holdem.engine.detail.river.compact.CompactRiverProbabilities;
import ao.holdem.engine.detail.river.compact.MemProbCounts;
import ao.holdem.model.Round;

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
    public static void sortRiverBranch(
            BucketTree.Branch riverBuckets, byte nBuckets)
    {
        sortRiverBranch(
                riverBuckets,
                riverBuckets.round(),
                riverBuckets.parentCanons(),
                nBuckets);
    }

    public static void sortRiverBranch(
            LongByteList branch,
            Round        parentRound,
            int          parentCanons[],
            byte         nBuckets)
    {
        IndexedValue sortable[] = new IndexedValue[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortable[i] = new IndexedValue(i);
        }

        for (CanonRange rivers : RangeLookup.lookup(
                parentRound, parentCanons, Round.RIVER)) {
            accumulateSortingData(
                    rivers, branch, sortable);
        }

        Arrays.sort(sortable);

        int sortedBuckets[] = new int[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortedBuckets[ sortable[i].index ] = i;
        }

        for (CanonRange rivers : RangeLookup.lookup(
                parentRound, parentCanons, Round.RIVER))
        {
            for (long river  = rivers.from();
                      river <= rivers.toInclusive();
                      river++) {
                branch.set(river,
                    sortedBuckets[
                            branch.get(river)]);
            }
        }
    }

    private static void accumulateSortingData(
            CanonRange   range,
            LongByteList riverBuckets,
            IndexedValue into[])
    {
        for (long river  = range.from();
                  river <= range.toInclusive();
                  river++) {
            into[ riverBuckets.get(river) ].add(
                    (double) MemProbCounts.compactProb(river) /
                            CompactRiverProbabilities.COUNT);
        }
    }


    //--------------------------------------------------------------------
    public static void sortRiver(
            LongByteList riverBuckets, int nBuckets)
    {
        IndexedValue sortable[] = new IndexedValue[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortable[i] = new IndexedValue(i);
        }

        for (long i = 0; i < River.CANONS; i++)
        {
            sortable[ riverBuckets.get(i) ].add(
                    (double) MemProbCounts.compactProb(i) /
                            CompactRiverProbabilities.COUNT);
        }

        Arrays.sort(sortable);

        int sortedBuckets[] = new int[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortedBuckets[ sortable[i].index ] = i;
        }

        for (long i = 0; i < River.CANONS; i++)
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

        for (int i = 0; i < CanonHole.CANONS; i++)
        {
            sortable[ holeBuckets.get(i) ].add(
                    HoleOdds.lookup(i).strengthVsRandom());
        }

        Arrays.sort(sortable);

        int sortedBuckets[] = new int[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            sortedBuckets[ sortable[i].index ] = i;
        }

        for (int i = 0; i < CanonHole.CANONS; i++)
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
