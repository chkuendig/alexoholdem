package ao.holdem.ai.bucket.abstraction;

import ao.Infrastructure;
import ao.holdem.ai.bucket.abstraction.access.BucketDecoder;
import ao.holdem.ai.bucket.abstraction.access.BucketSequencer;
import ao.holdem.ai.bucket.abstraction.access.odds.BucketOdds;
import ao.holdem.ai.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.ai.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.ai.bucket.abstraction.access.tree.BucketTreeImpl;
import ao.holdem.ai.bucket.abstraction.bucketize.build.BucketTreeBuilder;
import ao.holdem.ai.bucket.abstraction.bucketize.build.SmartBucketTreeBuilder;
import ao.holdem.ai.bucket.abstraction.bucketize.def.Bucketizer;
import ao.holdem.ai.ai.regret.holdem.InfoPart;
import ao.util.io.Dirs;
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
    private final int        N_HOLES;
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
            int        nHoleBuckets,
            char       nFlopBuckets,
            char       nTurnBuckets,
            char       nRiverBuckets)
    {
        DIR = Dirs.get(Infrastructure.path(
                "lookup/bucket/" + id(bucketizer, nHoleBuckets,
                                      nFlopBuckets, nTurnBuckets, nRiverBuckets)));

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
            int        numHoleBuckets,
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
    public BucketTree tree(boolean storedReadOnly)
    {
        if (tree != null) return tree;

        if (storedReadOnly)
        {
            tree = new BucketTreeImpl(DIR, true);
        }
        else
        {
            BucketTreeBuilder treeBuilder =
//                    new FastBucketTreeBuilder(BUCKETIZER);
                    new SmartBucketTreeBuilder(BUCKETIZER);

            tree = treeBuilder.bucketize(
                    DIR, N_HOLES, N_FLOPS, N_TURNS, N_RIVERS);
        }
        return tree;
    }


    //--------------------------------------------------------------------
    public BucketDecoder decoder()
    {
        if (decoder != null) return decoder;
        decoder = BucketDecoder.retrieveInstance(DIR);
        if (decoder == null) {
            decoder = BucketDecoder.computeAndStore(
                          tree(false).holes(), DIR);
        }
        return decoder;
    }


    //--------------------------------------------------------------------
    public BucketOdds odds()
    {
        if (odds != null) return odds;
        odds = BucketOdds.retrieve(DIR, decoder());
        if (odds == null) {
            odds = BucketOdds.retrieveOrCompute(
                          DIR, tree(false), decoder());
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


    //--------------------------------------------------------------------
    public boolean deleteSequence()
    {
        return BucketSequencer.delete(DIR);
    }

    public BucketSequencer sequence()
    {
        if (sequence != null) return sequence;
        sequence = BucketSequencer.retrieve(DIR, decoder());
        if (sequence == null) {
            sequence = BucketSequencer.retrieveOrCompute(
                    DIR, tree(false), decoder());
        }
        return sequence;
    }


    //--------------------------------------------------------------------
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
                     infoPartDir(name, doublePrecision),
                     N_HOLES, N_FLOPS, N_TURNS, N_RIVERS,
                     readOnly, doublePrecision);
        infoParts.put(name, infoPart);

        return infoPart;
    }

    private File infoPartDir(String name, boolean doublePrecision)
    {
        return Dirs.get(DIR, "info/" + name +
                            (doublePrecision ? "_d" : "_f"));
    }

    public boolean hasInfoPart(boolean doublePrecision)
    {
        return hasInfoPart(null, doublePrecision);
    }
    public boolean hasInfoPart(String name, boolean doublePrecision)
    {
        if (name == null) {
            return hasInfoPart("main", doublePrecision);
        }

        return infoParts.get(name) != null ||
               InfoPart.exists(infoPartDir(name, doublePrecision));

    }
}
