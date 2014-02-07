package ao.holdem.ai.bucket.abstraction.access;

import ao.holdem.ai.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.model.canon.flop.Flop;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.model.canon.river.River;
import ao.holdem.model.canon.turn.Turn;
import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.card.chance.DeckCards;
import ao.util.math.Calc;
import ao.util.math.rand.Rand;
import ao.util.time.Progress;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;

/**
 * Date: Feb 19, 2009
 * Time: 4:40:41 PM
 */
public class BucketSequencer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketSequencer.class);

    private static final String FILE_NAME = "bucket_seq.byte";
    public  static final long   COUNT     = 1000 * 1000 * 1000;


    //--------------------------------------------------------------------
    public static boolean delete(File dir)
    {
        File store = new File(dir, FILE_NAME);
        return ! store.exists() || store.delete();
    }

    public static BucketSequencer retrieve(
            File dir, BucketDecoder decoder)
    {
        File store = new File(dir, FILE_NAME);
        LOG.debug("attempting to retrieve " + store);

        if (! store.canRead()) {
            LOG.debug("can't read");
            return null;
        } else if (store.length() < (COUNT * 8)) {
            LOG.debug("too small " +
                      store.length() + " vs " + (COUNT * 8));
            return null;
        }

        LOG.debug("retrieved");
        return new BucketSequencer(store, decoder);
    }

    public static BucketSequencer retrieveOrCompute(
            File dir, BucketTree tree, BucketDecoder decoder)
    {
        BucketSequencer seq = retrieve(dir, decoder);
        if (seq != null) return seq;

        File store = new File(dir, FILE_NAME);
        try {
            computeSequences(store, tree);
        } catch (IOException e) {
            throw new Error( e );
        }
        return new BucketSequencer(store, decoder);
    }


    //--------------------------------------------------------------------
    private static void computeSequences(
            File store, BucketTree tree) throws IOException
    {
        LOG.debug("computeSequences");
        Progress p = new Progress(COUNT);

        Rand.randomize();

        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(store)));
        for (long i = 0; i < COUNT; i++) {
            byte[][] pair = randomBucketSequencePair(tree);
            writeBuckets(out, pair);
            p.checkpoint();
        }
        out.close();
    }

    private static byte[][] randomBucketSequencePair(
            BucketTree tree)
    {
        ChanceCards cards     = new DeckCards();
        Hole        holeA     = cards.hole(Avatar.local("dealer"));
        Hole        holeB     = cards.hole(Avatar.local("dealee"));
        Community   community = cards.community(Round.RIVER);

        return new byte[][]{
                computeBuckets(tree, holeA, community),
                computeBuckets(tree, holeB, community)};
    }

    private static byte[] computeBuckets(
            BucketTree tree,
            Hole       hole,
            Community  community)
    {
        CanonHole canonHole  = hole.asCanon();
        int       holeBucket = tree.getHole(canonHole.canonIndex());

        Flop flop        = canonHole.addFlop(community);
        int  flopBucket  = tree.getFlop( flop.canonIndex() );

        Turn turn        = flop.addTurn(community.turn());
        int  turnBucket  = tree.getTurn( turn.canonIndex() );

        River river       = turn.addRiver(community.river());
        int  riverBucket = tree.getRiver( river.canonIndex() );

        return new byte[]{
                (byte) holeBucket, (byte) flopBucket,
                (byte) turnBucket, (byte) riverBucket};
    }


    //--------------------------------------------------------------------
    private static void writeBuckets(
            DataOutput out, byte[][] buckets) throws IOException {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                out.writeByte(buckets[i][j]);
            }
        }
    }

    private static int[][] readBuckets(DataInput in) {
        try {
            return doReadBuckets(in);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private static int[][] doReadBuckets(DataInput in) throws IOException {
        int[][] buckets = new int[2][4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                buckets[i][j] = Calc.unsigned( in.readByte() );
            }
        }
        return buckets;
    }


    //--------------------------------------------------------------------
    private final File          STORE;
    private final BucketDecoder DECODER;


    //--------------------------------------------------------------------
    private BucketSequencer(File store, BucketDecoder decoder)
    {
        STORE   = store;
        DECODER = decoder;
    }


    //--------------------------------------------------------------------
    public Iterator<char[][]> iterator(final long sequences) {
        return new Iterator<char[][]>() {
            private long            location;
            private long            count = 0;
            private DataInputStream in;
            private DataInputStream open(long at) {
                DataInputStream in;
                try {
                    in = new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(STORE),
                                    1024 * 1024));
                    long toSkip = ((at % COUNT) * 8);
                    if (in.skip( toSkip ) != toSkip) {
                        throw new Error("skip failed");
                    }
                    location = at;
                } catch (IOException e) {
                    throw new Error( e );
                }
                return in;
            }

            public boolean hasNext() {
                boolean hasNext = (count < sequences);
                if ((! hasNext) && in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        in = null;
                    }
                }
                return hasNext;
            }

            public char[][] next() {
                try {
                    if ((location++ % COUNT) == 0) {
                        if (in != null) {
                            in.close();
                            in = open(0);
                        } else {
                            in = open(0);
                            location++;
                        }
                    }
                    count++;

                    char decoded[][] = decode(readBuckets(in));
                    hasNext();
                    return decoded;
                } catch (IOException e) {
                    throw new Error( e );
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    //--------------------------------------------------------------------
    private char[] decode(int buckets[])
    {
        return new char[]{
                (char) buckets[0],
                DECODER.decode(buckets[0], buckets[1]),
                DECODER.decode(buckets[0], buckets[1], buckets[2]),
                DECODER.decode(buckets[0], buckets[1],
                               buckets[2], buckets[3])};
    }

    private char[][] decode(int buckets[][])
    {
        return new char[][]{
                decode(buckets[0]),
                decode(buckets[1])};
    }
}
