package ao.bucket.abstraction.tree;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonDetails;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.Round;
import ao.holdem.model.card.Hole;
import ao.util.data.AutovivifiedList;
import ao.util.data.primitive.IntList;
import ao.util.io.Dir;
import ao.util.persist.PersistentBytes;

import java.io.File;
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

    private final byte[] holes;
    private final byte[] flops;


    //--------------------------------------------------------------------
    public BucketTreeImpl(String id)
    {
        File persistDir = Dir.get(DIR, id);

        holeFile   = new File(persistDir, "holes");
        flopFile   = new File(persistDir, "flops");

        holes = retrieveOrCreate(holeFile,       Hole.CANONICAL_COUNT);
        flops = retrieveOrCreate(flopFile, FlopLookup.CANONICAL_COUNT);
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

//    public void addTurn(byte turnBucket,
//                        int  canonTurn)
//    {
//        throw new UnsupportedOperationException();
//    }
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

//    public byte getTurn(int canonTurn)
//    {
//        return 0;
//    }
//
//    public byte getRiver(long canonRiver)
//    {
//        return 0;
//    }

    public byte get(Round round, long canonIndex)
    {
        switch (round)
        {
            case PREFLOP: return getHole( (char) canonIndex );
            case FLOP:    return getFlop( (int)  canonIndex );

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        PersistentBytes.persist(holes, holeFile);
        PersistentBytes.persist(flops, flopFile);
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
        private CanonDetail[][] subDetails;
        private int[]           parentCanons;
        private Round           round;


        //----------------------------------------------------------------
        public BranchImpl()
        {
            round        = Round.PREFLOP;
            parentCanons = new int[] {-1};
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
            if (subDetails != null) return subDetails;
            subDetails = CanonDetails.lookupSub(
                            round().previous(),
                            parentCanons());
            return subDetails;
        }


        //----------------------------------------------------------------
        public Iterable<Branch> subBranches()
        {
            if (round == Round.RIVER) return new ArrayList<Branch>();

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
            for (CanonDetail[] details : details())
            {
                for (CanonDetail detail : details)
                {
                    if (get( detail.canonIndex() ) == -1)
                        return false;
                }
            }
            return true;
        }

//        public char subBranchCount()
//        {
//            return 0;
//        }
    }

}
