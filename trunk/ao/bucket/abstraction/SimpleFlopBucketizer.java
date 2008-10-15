package ao.bucket.abstraction;

import ao.bucket.index.CanonTraverser;
import ao.bucket.index.CanonTraverser.Traverser;
import ao.bucket.index.flop.Flop;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.odds.agglom.OddHist;
import ao.odds.agglom.impl.GeneralHistFinder;

import java.util.*;

/**
 * Date: Oct 14, 2008
 * Time: 6:52:10 PM
 */
public class SimpleFlopBucketizer
{
    //--------------------------------------------------------------------
    private final Map<Integer, OddHist> histograms;
    private final List<Integer>         inOrder;
    private final Map<Integer, Flop>    reverseIndex;


    //--------------------------------------------------------------------
    public SimpleFlopBucketizer(short canonHoles[])
    {
        reverseIndex = new HashMap<Integer, Flop>();
        histograms   = initHistogram( canonHoles, reverseIndex );
        inOrder      = new ArrayList<Integer>( histograms.keySet() );

        for (int i = 0; i < inOrder.size(); i++) inOrder.set(i, i);
        Collections.sort(inOrder, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return histograms.get( a ).compareTo(
                        histograms.get( b ));
            }
        });
    }


    //--------------------------------------------------------------------
    private Map<Integer, OddHist> initHistogram(
            final short              canonHoles[],
            final Map<Integer, Flop> rev)
    {
        final Map<Integer, OddHist> hist =
                new HashMap<Integer, OddHist>();

        new CanonTraverser().traverseFlops(canonHoles, new Traverser() {
            public void traverse(CardSequence cards) {
                Hole hole = cards.hole();
                Flop flop = hole.addFlop(
                        cards.community().flopA(),
                        cards.community().flopB(),
                        cards.community().flopC());

                int index = flop.canonIndex();
                hist.put(index, new GeneralHistFinder().compute(cards));
                rev .put(index, flop);
            }
        });

        return hist;
    }


    //--------------------------------------------------------------------
    public int[][] bucketize(int numBuckets)
    {
        int flops[][] = new int[ numBuckets ][];

        int index = 0;
        int chunk = (int) Math.ceil(
                      ((double) histograms.size()) / flops.length);
        for (int i = 0; i < flops.length; i++)
        {
            flops[ i ] = new int[
                    Math.min(chunk,
                             histograms.size() - index) ];
            for (int j = 0;
                     j < chunk && index < histograms.size();
                     j++)
            {
                flops[ i ][ j ] = inOrder.get( index++ );
            }
        }

        return flops;
    }


    //--------------------------------------------------------------------
    public void display( int buckets[][] )
    {
        for (int canons[] : buckets)
        {
            if (canons.length == 0) continue;

            for (int canon : canons)
            {
                System.out.print( reverseIndex.get( canon ) + "\t" );
            }
            System.out.println();
        }
    }
}
