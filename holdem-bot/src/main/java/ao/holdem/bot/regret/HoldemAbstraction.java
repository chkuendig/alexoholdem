package ao.holdem.bot.regret;

import ao.Infrastructure;
import ao.holdem.abs.bucket.abstraction.access.BucketDecoder;
import ao.holdem.abs.bucket.abstraction.access.BucketSequencer;
import ao.holdem.abs.bucket.abstraction.access.odds.BucketOdds;
import ao.holdem.abs.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.access.tree.BucketTreeImpl;
import ao.holdem.abs.bucket.abstraction.bucketize.build.BucketTreeBuilder;
import ao.holdem.abs.bucket.abstraction.bucketize.build.FastBucketTreeBuilder;
import ao.holdem.abs.bucket.abstraction.bucketize.def.Bucketizer;
import ao.util.io.Dirs;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Feb 27, 2009
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
    private final File DIR;

    private final BucketTreeBuilder TREE_BUILDER;

    private final int  N_HOLES;
    private final char N_FLOPS;
    private final char N_TURNS;
    private final char N_RIVERS;

    private BucketTree            tree;
    private BucketDecoder         decoder;
    private BucketOdds            odds;
    private BucketSequencer       sequence;
    private Map<String, InfoPart> infoParts;


    //--------------------------------------------------------------------
    public HoldemAbstraction(
            Bucketizer bucketizer,
            int  nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets)
    {
        this(new FastBucketTreeBuilder(bucketizer),
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);
    }

    public HoldemAbstraction(
            BucketTreeBuilder treeBuilder,
            int  nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets)
    {
        DIR = Dirs.get(Infrastructure.path(
                "lookup/bucket/" + id(treeBuilder, nHoleBuckets,
                                      nFlopBuckets, nTurnBuckets, nRiverBuckets)));

        TREE_BUILDER = treeBuilder;

        N_HOLES  = nHoleBuckets;
        N_FLOPS  = nFlopBuckets;
        N_TURNS  = nTurnBuckets;
        N_RIVERS = nRiverBuckets;

        infoParts = new HashMap<>();
    }


    //--------------------------------------------------------------------
    private String id(
            BucketTreeBuilder bucketizer,
            int numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets)
    {
        StringBuilder str = new StringBuilder();

        str.append( bucketizer.id() )
           .append( ';' )
           .append( numHoleBuckets )
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
            tree = TREE_BUILDER.bucketize(
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


    //--------------------------------------------------------------------
//    public int holeBucketCount() {
//        return N_HOLES;
//    }
//
//    public int flopBucketCount() {
//        return N_FLOPS;
//    }
//
//    public int turnBucketCount() {
//        return N_TURNS;
//    }
//
//    public int riverBucketCount() {
//        return N_RIVERS;
//    }
}
