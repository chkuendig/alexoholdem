package ao.holdem.ai.bucket.abstraction.access.tree;

import ao.holdem.ai.bucket.abstraction.access.tree.list.FullLongByteList;
import ao.holdem.ai.bucket.abstraction.access.tree.list.HalfLongByteList;
import ao.holdem.ai.bucket.abstraction.access.tree.list.StoredHalfLongByteList;
import ao.holdem.ai.bucket.abstraction.access.tree.list.StoredLongByteList;
import ao.holdem.model.canon.flop.Flop;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.model.canon.river.River;
import ao.holdem.model.canon.turn.Turn;
import ao.holdem.ai.bucket.index.detail.CanonDetail;
import ao.holdem.ai.bucket.index.detail.DetailLookup;
import ao.holdem.model.Round;
import ao.util.data.AutovivifiedList;
import ao.util.data.primitive.IntList;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

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
    private static final Logger LOG =
            Logger.getLogger(BucketTreeImpl.class);


    //--------------------------------------------------------------------
    private final PersistentLongByteList holes;
    private final PersistentLongByteList flops;
    private final PersistentLongByteList turns;
    private final PersistentLongByteList rivers;
    private final File                   flushFlag;


    //--------------------------------------------------------------------
    public BucketTreeImpl(File dir, boolean storedReadOnly)
    {
        flushFlag  = new File(dir, "flushed");

        File  holeFile = new File(dir, "holes");
        File  flopFile = new File(dir, "flops");
        File  turnFile = new File(dir, "turns");
        File riverFile = new File(dir, "rivers");
        //File riverDir  =  Dir.get(dir, "rivers");

        if (storedReadOnly)
        {
            LOG.debug("retrieving stored read-only");
            holes  = new StoredLongByteList(holeFile);
            flops  = new StoredLongByteList(flopFile);
            turns  = new StoredHalfLongByteList( turnFile);
            rivers = new StoredHalfLongByteList(riverFile);
        }
        else
        {
            LOG.debug("loading (or creating)");
            holes  = new FullLongByteList( holeFile,  CanonHole.CANONS);
            flops  = new FullLongByteList( flopFile,  Flop.CANONS);
            turns  = new HalfLongByteList( turnFile,  Turn.CANONS);
            rivers = new HalfLongByteList(riverFile, River.CANONS);
        }
    }


    //--------------------------------------------------------------------
    public int[] maxBucketBranch() {
        return new int[]{
                holes.maxBuckets(),  flops.maxBuckets(),
                turns.maxBuckets(), rivers.maxBuckets()};
    }


    //--------------------------------------------------------------------
    public void setHole(char canonHole, int holeBucket)
    {
        holes.set( canonHole, holeBucket );
    }

    public void setFlop(int  canonFlop, int flopBucket)
    {
        flops.set( canonFlop, flopBucket );
    }

    public void setTurn(int  canonTurn, int turnBucket)
    {
        turns.set( canonTurn, turnBucket );
    }

    public void setRiver(long canonRiver, int riverBucket)
    {
        rivers.set(canonRiver, riverBucket);
    }

    public void set(Round round,
                    long  canonIndex,
                    int   bucket)
    {
        switch (round)
        {
            case PREFLOP:  setHole((char) canonIndex, bucket); break;
            case FLOP:     setFlop((int)  canonIndex, bucket); break;
            case TURN:     setTurn((int)  canonIndex, bucket); break;
            case RIVER:   setRiver(       canonIndex, bucket); break;
        }
    }

//    public long size(Round round)
//    {
//
//    }


    //--------------------------------------------------------------------
    public int getHole(int canonHole)
    {
        return holes.get( canonHole );
    }

    public int getFlop(int canonFlop)
    {
        return flops.get( canonFlop );
    }

    public int getTurn(int canonTurn)
    {
        return turns.get( canonTurn );
    }

    public int getRiver(long canonRiver)
    {
        return rivers.get( canonRiver );
    }

    public int get(Round round, long canonIndex)
    {
        switch (round)
        {
            case PREFLOP: return  getHole( (char) canonIndex );
            case FLOP:    return  getFlop( (int)  canonIndex );
            case TURN:    return  getTurn( (int)  canonIndex );
            case RIVER:   return getRiver(        canonIndex );

            default:
                throw new IllegalArgumentException();
        }
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        holes .flush();
        flops .flush();
        turns .flush();
        rivers.flush();

        PersistentBytes.persist(new byte[]{1}, flushFlag);
    }

    public boolean isFlushed()
    {
        return flushFlag.canRead();
    }


//    //--------------------------------------------------------------------
//    public BucketDecoder map()
//    {
//        return BucketDecoder.retrieveOrCompute(holes(), persistDir);
//    }


    //--------------------------------------------------------------------
    public Branch holes()
    {
        return new BranchImpl();
    }


    //--------------------------------------------------------------------
    private class BranchImpl implements Branch
    {
        //----------------------------------------------------------------
//        private CanonDetail[] subDetails;
        private int[]           parentCanons;
        private Round           round;


        //----------------------------------------------------------------
        public BranchImpl()
        {
            round        = Round.PREFLOP;
            parentCanons = new int[] {};
        }

        public BranchImpl(Round r, int parents[])
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

        public int parentBucket()
        {
            return BucketTreeImpl.this.get(
                    round.previous(), parentCanons[0]);
        }


        //----------------------------------------------------------------
        public void set(long canonIndex, int bucket)
        {
            BucketTreeImpl.this.set(round, canonIndex, bucket);
        }

        public int get(long canonIndex)
        {
            return BucketTreeImpl.this.get(round, canonIndex);
        }

//        public void reset(byte fromBucket, byte toBucket) {
//            BucketTreeImpl.this.get();
//
//            BucketTreeImpl.this.set(round, canonIndex, bucket);
//        }


        //----------------------------------------------------------------
        public CanonDetail[] details()
        {
            return DetailLookup.lookupSub(
                            round().previous(),
                            parentCanons());
        }
//        public void details(Traverser<CanonDetail> visit)
//        {
//            DetailLookup.lookupSub(
//                    round().previous(),
//                    parentCanons(),
//                    visit);
//        }

        public byte bucketCount()
        {
            assert round == Round.RIVER;
            return DetailLookup.riverBucketCount(
                     BucketTreeImpl.this,
                     parentCanons());
        }


        //----------------------------------------------------------------
        public List<Branch> subBranches()
        {
            if (round == Round.RIVER) return new ArrayList<Branch>();

            AutovivifiedList<IntList> subBranchCanons =
                    new AutovivifiedList<IntList>();

            for (CanonDetail detail : details())
            {
                int bucket = get( detail.canonIndex() );

                IntList bucketCanons = subBranchCanons.get(bucket);
                if (bucketCanons == null) {
                    bucketCanons = new IntList();
                    subBranchCanons.set(bucket, bucketCanons);
                }
                bucketCanons.add( (int) detail.canonIndex() );
            }

            List<Branch> subBranches = new ArrayList<Branch>();
            for (IntList subBranchCanon : subBranchCanons) {
                subBranches.add(
                        new BranchImpl(
                                round.next(),
                                subBranchCanon.toIntArray()));
            }
            return subBranches;
        }


        //----------------------------------------------------------------
        @Override
        public String toString()
        {
            return round + " (" +
                   parentCanons.length + " parents)";
        }


        //----------------------------------------------------------------
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BranchImpl)) return false;

            BranchImpl branch = (BranchImpl) o;

            return Arrays.equals(parentCanons, branch.parentCanons) &&
                   round == branch.round;

        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(parentCanons);
            result = 31 * result + round.hashCode();
            return result;
        }
    }
}

