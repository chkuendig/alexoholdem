package ao.bucket.abstraction;

import ao.bucket.abstraction.access.BucketDecoder;
import ao.bucket.abstraction.access.BucketSequencer;
import ao.bucket.abstraction.access.odds.BucketOdds;
import ao.bucket.abstraction.access.odds.IBucketOdds;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.abstraction.bucketize.build.SmartBucketTreeBuilder;
import ao.regret.holdem.InfoPart;
import ao.util.io.Dir;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: shalom
 * Date: Feb 27, 2009
 * Time: 3:42:25 PM
 *
 * Note: NOT threadsafe
 */
public class HoldemAbstraction
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HoldemAbstraction.class);


    //--------------------------------------------------------------------
//    private final String ID;
    private final File       DIR;

    private final Bucketizer BUCKETIZER;
    private final byte       N_HOLES;
    private final char       N_FLOPS;
    private final char       N_TURNS;
    private final char       N_RIVERS;

    private BucketTree            tree;
    private BucketDecoder         decoder;
    private BucketOdds            odds;
    private BucketSequencer       sequence;
    private Map<String, InfoPart> infoParts;


    //--------------------------------------------------------------------
    public HoldemAbstraction(
            Bucketizer bucketizer,
            byte       nHoleBuckets,
            char       nFlopBuckets,
            char       nTurnBuckets,
            char       nRiverBuckets)
    {
        DIR = Dir.get("lookup/bucket/" + id(bucketizer,
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets));

        BUCKETIZER = bucketizer;
        N_HOLES    = nHoleBuckets;
        N_FLOPS    = nFlopBuckets;
        N_TURNS    = nTurnBuckets;
        N_RIVERS   = nRiverBuckets;

        infoParts  = new HashMap<String, InfoPart>();
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
//        return (tree = new FastBucketTreeBuilder(BUCKETIZER).bucketize(
//                            DIR, N_HOLES, N_FLOPS, N_TURNS, N_RIVERS));
        return (tree = new SmartBucketTreeBuilder(BUCKETIZER).bucketize(
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
    public IBucketOdds oddsCache()
    {
        BucketOdds bucketOdds;
        if (odds != null) {
            bucketOdds = odds;
        } else {
            bucketOdds = BucketOdds.retrieve(DIR, decoder());
            if (bucketOdds == null) {
                bucketOdds = odds();
            }
        }
        if (bucketOdds == null) return null;
        return bucketOdds.cache();
    }


    public BucketSequencer sequence()
    {
        if (sequence != null) return sequence;
        sequence = BucketSequencer.retrieve(DIR, decoder());
        if (sequence == null) {
            sequence = BucketSequencer.retrieveOrCompute(
                    DIR, tree(), decoder());
        }
        return sequence;
    }

    public InfoPart infoPart(boolean readOnly, boolean doublePrecision)
    {
        return infoPart(null, readOnly, doublePrecision);
    }
    public InfoPart infoPart(
            String name, boolean readOnly, boolean doublePrecision)
    {
        if (name == null) {
            return infoPart("main", readOnly, doublePrecision);
        }

        InfoPart infoPart = infoParts.get(name);
        if (infoPart != null) {
            assert infoPart.isReadOnly() == readOnly;
            return infoPart;
        }

        infoPart = InfoPart.retrieveOrCreate(
                     Dir.get(DIR, "info/" + name +
                             (doublePrecision ? "_d" : "_f")),
                     N_HOLES, N_FLOPS, N_TURNS, N_RIVERS,
                     readOnly, doublePrecision);
        infoParts.put(name, infoPart);

        return infoPart;
    }
}
