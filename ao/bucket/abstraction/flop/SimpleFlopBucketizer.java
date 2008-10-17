package ao.bucket.abstraction.flop;

import ao.bucket.index.CanonTraverser;
import ao.bucket.index.CanonTraverser.Traverser;
import ao.bucket.index.flop.Flop;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.odds.agglom.impl.GeneralHistFinder;
import ao.odds.eval.eval5.Eval5;
import ao.util.data.IntList;
import org.apache.log4j.Logger;

/**
 * Date: Oct 14, 2008
 * Time: 6:52:10 PM
 */
public class SimpleFlopBucketizer implements FlopBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(SimpleFlopBucketizer.class);


    //--------------------------------------------------------------------
    private final IntList byMean[];
    private       int     meanCount;


    //--------------------------------------------------------------------
    public SimpleFlopBucketizer(short canonHoles[])
    {
        byMean = initByMean( canonHoles );
    }


    //--------------------------------------------------------------------
    private IntList[] initByMean(
            final short canonHoles[])
    {
        LOG.info("initializing means");
        final IntList hist[] = new IntList[ Eval5.VALUE_COUNT ];

        new CanonTraverser().traverseFlops(canonHoles, new Traverser() {
            public void traverse(CardSequence cards) {
                Hole hole = cards.hole();
                Flop flop = hole.addFlop(
                        cards.community().flopA(),
                        cards.community().flopB(),
                        cards.community().flopC());

                int mean  = (int) Math.round(
                        new GeneralHistFinder().compute(cards).mean());
                int index = flop.canonIndex();

                hist[ mean ] = IntList.addTo(hist[ mean ], index);
                meanCount++;
            }
        });

        return hist;
    }


    //--------------------------------------------------------------------
    public int[][] bucketize(int numBuckets)
    {
        int count     = 0;
        int partStart = 0;
        int flopIndex = 0;
        int flops[][] = new int[ numBuckets ][];
        int partition = (int) Math.ceil(
                           ((double) meanCount) / numBuckets);
        for (int mean = 0; mean < byMean.length; mean++)
        {
            if (count >= partition ||
                    mean == (byMean.length - 1))
            {
                int bucket[]         = new int[ count ];
                int bucketIndex      = 0;
                flops[ flopIndex++ ] = bucket;
                for (int bucketMean = partStart;
                         bucketMean < mean;
                         bucketMean++)
                {
                    if (byMean[ bucketMean ] == null) continue; 
                    for (int flop : byMean[ bucketMean ].toArray())
                    {
                        bucket[ bucketIndex++ ] = flop;
                    }
                }

                partStart = mean;
                count     = 0;
            }

            count += IntList.sizeOf( byMean[ mean ] );
        }
        return flops;
    }


    //--------------------------------------------------------------------
    public void display( int buckets[][] )
    {
        for (int canons[] : buckets)
        {
            if (canons.length == 0) continue;
            System.out.println( canons.length );

//            for (int i = 0; i < canons.length; i++)
//            {
//                int canon = canons[i];
//
//                if (i % 500 == 499) System.out.print("\n\t");
////                System.out.print( reverseIndex.get( canon ) + "\t" );
//                System.out.print(canon + "\t");
//            }
//            System.out.println();
        }
    }
}
