package ao.bucket.abstraction.flop;

import ao.bucket.index.CanonTraverser;
import ao.bucket.index.CanonTraverser.Traverser;
import ao.bucket.index.flop.Flop;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.odds.agglom.impl.GeneralHistFinder;
import ao.odds.eval.eval5.Eval5;
import org.apache.log4j.Logger;

/**
 * Date: Oct 14, 2008
 * Time: 6:52:10 PM
 */
public class SimpleFlopBucketizer
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
        int flops[][] = new int[ numBuckets ][];

        int index  = 0;
        int mean   = 0;
        int inMean = 0;
        int chunk  = (int) Math.ceil(
                      ((double) meanCount) / flops.length);
        for (int i = 0; i < flops.length; i++)
        {
            flops[ i ] = new int[
                    Math.min(chunk,
                             meanCount - index) ];
            for (int j = 0;
                     j < flops[ i ].length;
                     j++)
            {
                if (inMean >= IntList.sizeOf( byMean[ mean ] ))
                {
                    if    (byMean[ mean ] != null) mean++;
                    while (byMean[ mean ] == null)
                    {
                        mean++;
                    }
                    inMean = 0;
                }

                flops[ i ][ j ] = byMean[ mean ].get( inMean++ );
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
