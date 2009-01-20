package ao.bucket.abstraction.access.tree;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonDetails;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.Round;
import ao.holdem.model.card.Hole;
import ao.util.data.AutovivifiedList;
import ao.util.data.primitive.IntList;
import ao.util.io.Dir;
import ao.util.persist.PersistentBytes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: Jan 8, 2009
 * Time: 10:51:29 AM
 */
public class BucketTreeImpl implements BucketTree
{
    //--------------------------------------------------------------------
    private static final File DIR = Dir.get("lookup/bucket/tree/");


    //--------------------------------------------------------------------
    private final File holeFile;
    private final File flopFile;
    private final File turnFile;

    private final byte[] holes;
    private final byte[] flops;
    private final byte[] turns;


    //--------------------------------------------------------------------
    public BucketTreeImpl(String id)
    {
        File persistDir = Dir.get(DIR, id);

        holeFile = new File(persistDir, "holes");
        flopFile = new File(persistDir, "flops");
        turnFile = new File(persistDir, "turns");

        holes = retrieveOrCreate(holeFile,       Hole.CANONICAL_COUNT);
        flops = retrieveOrCreate(flopFile, FlopLookup.CANONICAL_COUNT);
        turns = retrieveOrCreate(turnFile, TurnLookup.CANONICAL_COUNT);
    }

    private byte[] retrieveOrCreate(File fullName, int canonCount)
    {
        byte[] buckets  = PersistentBytes.retrieve( fullName );
        if (buckets == null) {
            buckets = new byte[ canonCount ];
            Arrays.fill(buckets, (byte) -1);
        }
        return buckets;
    }


    //--------------------------------------------------------------------
    public void setHole(char canonHole,
                        byte holeBucket)
    {
        holes[ canonHole ] = holeBucket;
    }

    public void setFlop(int  canonFlop,
                        byte flopBucket)
    {
        flops[ canonFlop ] = flopBucket;
    }

    public void setTurn(int  canonTurn,
                        byte turnBucket)
    {
        turns[ canonTurn ] = turnBucket;
    }
//
//    public void addRiver(byte riverBucket,
//                         long canonRiver)
//    {
//        throw new UnsupportedOperationException();
//    }

    public void set(Round round,
                    long  canonIndex,
                    byte  bucket)
    {
        switch (round)
        {
            case PREFLOP: setHole((char) canonIndex, bucket); break;
            case FLOP:    setFlop((int)  canonIndex, bucket); break;
            case TURN:    setTurn((int)  canonIndex, bucket); break;
        }
    }


    //--------------------------------------------------------------------
    public byte getHole(char canonHole)
    {
        return holes[ canonHole ];
    }

    public byte getFlop(int canonFlop)
    {
        return flops[ canonFlop ];
    }

    public byte getTurn(int canonTurn)
    {
        return turns[ canonTurn ];
    }

    public byte getRiver(long canonRiver)
    {
        return -1;
    }

    public byte get(Round round, long canonIndex)
    {
        switch (round)
        {
            case PREFLOP: return getHole( (char) canonIndex );
            case FLOP:    return getFlop( (int)  canonIndex );
            case TURN:    return getTurn( (int)  canonIndex );

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        PersistentBytes.persist(holes, holeFile);
        PersistentBytes.persist(flops, flopFile);
        PersistentBytes.persist(turns, turnFile);
    }

    public void flush(Round round, int fromCanon, int canonCount) {
        try {
            doFlush(round, fromCanon, canonCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void doFlush(Round round, int fromCanon, int canonCount)
            throws IOException
    {
        File   roundFile;
        byte[] roundBuckets;

        if (round == Round.PREFLOP) {
            roundFile    = holeFile;
            roundBuckets = holes;
        } else if (round == Round.FLOP) {
            roundFile    = flopFile;
            roundBuckets = flops;
        } else if (round == Round.TURN) {
            roundFile    = turnFile;
            roundBuckets = turns;
        } else /*if (round == Round.RIVER)*/ {
            throw new UnsupportedOperationException();
        }

        if (! roundFile.canRead()) {
            PersistentBytes.persist(roundBuckets, roundFile);
            return;
        }

        RandomAccessFile f = new RandomAccessFile(roundFile, "rw");
        f.seek(fromCanon);
        f.write(roundBuckets, fromCanon, canonCount);
        f.close();
    }


    //--------------------------------------------------------------------
    public Branch root()
    {
        return new BranchImpl();
    }


    //--------------------------------------------------------------------
    public class BranchImpl implements Branch
    {
        //----------------------------------------------------------------
//        private CanonDetail[][] subDetails;
        private int[]           parentCanons;
        private Round           round;


        //----------------------------------------------------------------
        public BranchImpl()
        {
            round        = Round.PREFLOP;
            parentCanons = new int[] {};
        }

        public BranchImpl(Round r, int[] parents)
        {
            round        = r;
            parentCanons = parents;
        }


        //----------------------------------------------------------------
        public Round round()
        {
            return round;
        }


        //----------------------------------------------------------------
        public int[] parentCanons()
        {
            return parentCanons;
        }

        public byte parentBucket()
        {
            return BucketTreeImpl.this.get(
                    round.previous(), parentCanons[0]);
        }


        //----------------------------------------------------------------
        public void set(long canonIndex, byte bucket)
        {
            BucketTreeImpl.this.set(round, canonIndex, bucket);
        }

        public byte get(long canonIndex)
        {
            return BucketTreeImpl.this.get(round, canonIndex);
        }


        //----------------------------------------------------------------
        public CanonDetail[][] details()
        {
            return CanonDetails.lookupSub(
                            round().previous(),
                            parentCanons());
//            if (subDetails != null) return subDetails;
//            subDetails = CanonDetails.lookupSub(
//                            round().previous(),
//                            parentCanons());
//            return subDetails;
        }


        //----------------------------------------------------------------
        public Iterable<Branch> subBranches()
        {
            if (round == Round.RIVER ||
                round == Round.TURN) return new ArrayList<Branch>();

            AutovivifiedList<IntList> subBranchCanons =
                    new AutovivifiedList<IntList>();

            for (CanonDetail[] detailList : details())
            {
                for (CanonDetail detail : detailList)
                {
                    byte bucket = get( detail.canonIndex() );

                    IntList bucketCanons = subBranchCanons.get(bucket);
                    if (bucketCanons == null) {
                        bucketCanons = new IntList();
                        subBranchCanons.set(bucket, bucketCanons);
                    }
                    bucketCanons.add( (int) detail.canonIndex() );
                }
            }

            List<Branch> subBranches = new ArrayList<Branch>();
            for (IntList subBranchCanon : subBranchCanons) {
                subBranches.add(
                        new BranchImpl(
                                round.next(),
                                subBranchCanon.toArray()));
            }
            return subBranches;
        }


        //----------------------------------------------------------------
        public boolean isBucketized()
        {
            if (round == Round.PREFLOP) {
                for (int i = 0; i < Hole.CANONICAL_COUNT; i++) {
                    if (get(i) == -1) return false;
                }
                return true;
            }

            for (int parent : parentCanons) {
                CanonRange range =
                            CanonDetails.lookupRange(
                                    round.previous(), parent);
                for (int i = 0; i < range.canonIndexCount(); i++) {
                    if (get( range.fromCanonIndex() + i ) == -1)
                        return false;
                }
            }
            return true;
        }


        //----------------------------------------------------------------
        public void flush()
        {
            if (round == Round.PREFLOP) {
                BucketTreeImpl.this.flush(
                        round, 0, Hole.CANONICAL_COUNT);
            } else if (round == Round.FLOP) {
                for (int parent : parentCanons) {
                    CanonRange range =
                            CanonDetails.lookupRange(
                                    round.previous(), parent);

                    BucketTreeImpl.this.flush(
                            round,
                            (int) range.fromCanonIndex(),
                            range.canonIndexCount());
                }
            }
        }

//        public char subBranchCount()
//        {
//            return 0;
//        }
    }

}
