package ao.bucket.abstraction;

import ao.bucket.abstraction.access.BucketDecoder;
import ao.bucket.abstraction.access.BucketSequencer;
import ao.bucket.abstraction.access.odds.BucketOdds;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.BucketTreeBuilder;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.util.io.Dir;

import java.io.File;

/**
 * User: shalom
 * Date: Feb 27, 2009
 * Time: 3:42:25 PM
 *
 * Note: NOT threadsafe
 */
public class CardAbstraction
{
    //--------------------------------------------------------------------
    private final String ID;
    private final File   DIR;

    private final Bucketizer BUCKETIZER;
    private final byte       N_HOLES;
    private final char       N_FLOPS;
    private final char       N_TURNS;
    private final char       N_RIVERS;

    private BucketTree      tree;
    private BucketDecoder   decoder;
    private BucketOdds      odds;
    private BucketSequencer sequence;


    //--------------------------------------------------------------------
    public CardAbstraction(
            Bucketizer bucketizer,
            byte       nHoleBuckets,
            char       nFlopBuckets,
            char       nTurnBuckets,
            char       nRiverBuckets)
    {
        ID  = id(bucketizer,
                 nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);
        DIR = Dir.get("lookup/bucket/" + ID);

        BUCKETIZER = bucketizer;
        N_HOLES    = nHoleBuckets;
        N_FLOPS    = nFlopBuckets;
        N_TURNS    = nTurnBuckets;
        N_RIVERS   = nRiverBuckets;
    }


    //--------------------------------------------------------------------
    private String id(
            Bucketizer bucketizer,
            byte       numHoleBuckets,
            char       numFlopBuckets,
            char       numTurnBuckets,
            char       numRiverBuckets)
    {
        StringBuilder str = new StringBuilder();

        str.append( bucketizer.id() )
           .append( ';' )
           .append( (int) numHoleBuckets )
           .append( '.' )
           .append( (int) numFlopBuckets )
           .append( '.' )
           .append( (int) numTurnBuckets )
           .append( '.' )
           .append( (int) numRiverBuckets );

        return str.toString();
    }


    //--------------------------------------------------------------------
    public BucketTree tree()
    {
        if (tree != null) return tree;
        return (tree = new BucketTreeBuilder(BUCKETIZER).bucketize(
                            DIR, N_HOLES, N_FLOPS, N_TURNS, N_RIVERS));
    }

    public BucketDecoder decoder()
    {
        if (decoder != null) return decoder;
        decoder = BucketDecoder.retrieveInstance(DIR);
        if (decoder == null) {
            decoder = BucketDecoder.computeAndStore(tree().holes(), DIR);
        }
        return decoder;
    }

    public BucketOdds odds()
    {
        if (odds != null) return odds;
        odds = BucketOdds.retrieve(DIR, decoder());
        if (odds == null) {
            odds = BucketOdds.retrieveOrCompute(DIR, tree(), decoder());
        }
        return odds;
    }

    public BucketSequencer sequence()
    {
        return null;
    }
}
