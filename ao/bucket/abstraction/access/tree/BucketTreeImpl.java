package ao.bucket.abstraction.access.tree;

import ao.bucket.abstraction.access.tree.list.BucketListImpl;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.index.canon.flop.FlopLookup;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.DetailLookup;
import ao.holdem.model.Round;
import ao.util.data.AutovivifiedList;
import ao.util.data.primitive.IntList;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
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
    private final BucketList holes;
    private final BucketList flops;
    private final BucketList turns;
    private final BucketList rivers;
    private final File       flushFlag;


    //--------------------------------------------------------------------
    public BucketTreeImpl(File dir)
    {
        flushFlag  = new File(dir, "flushed");

        File  holeFile = new File(dir, "holes");
        File  flopFile = new File(dir, "flops");
        File  turnFile = new File(dir, "turns");
        File riverFile = new File(dir, "rivers");
        //File riverDir  =  Dir.get(dir, "rivers");

        LOG.debug("loading (or creating)");
        holes  = new BucketListImpl( holeFile,  HoleLookup.CANONS);
        flops  = new BucketListImpl( flopFile,  FlopLookup.CANONS);
        turns  = new HalfBucketList( turnFile,  TurnLookup.CANONS);
        rivers = new HalfBucketList(riverFile, RiverLookup.CANONS);
        //rivers = new ThirdBucketList(riverDir, RiverLookup.CANONS);
    }


    //--------------------------------------------------------------------
    public byte[] maxBucketBranch() {
        return new byte[]{
                holes.maxBuckets(),  flops.maxBuckets(),
                turns.maxBuckets(), rivers.maxBuckets()};
    }


    //--------------------------------------------------------------------
    public void setHole(char canonHole, byte holeBucket)
    {
        holes.set( canonHole, holeBucket );
    }

    public void setFlop(int  canonFlop, byte flopBucket)
    {
        flops.set( canonFlop, flopBucket );
    }

    public void setTurn(int  canonTurn, byte turnBucket)
    {
        turns.set( canonTurn, turnBucket );
    }

    public void setRiver(long canonRiver, byte riverBucket)
    {
        rivers.set(canonRiver, riverBucket);
    }

    public void set(Round round,
                    long  canonIndex,
                    byte  bucket)
    {
        switch (round)
        {
            case PREFLOP:  setHole((char) canonIndex, bucket); break;
            case FLOP:     setFlop((int)  canonIndex, bucket); break;
            case TURN:     setTurn((int)  canonIndex, bucket); break;
            case RIVER:   setRiver(       canonIndex, bucket); break;
        }
    }


    //--------------------------------------------------------------------
    public byte getHole(char canonHole)
    {
        return holes.get( canonHole );
    }

    public byte getFlop(int canonFlop)
    {
        return flops.get( canonFlop );
    }

    public byte getTurn(int canonTurn)
    {
        return turns.get( canonTurn );
    }

    public byte getRiver(long canonRiver)
    {
        return rivers.get( canonRiver );
    }

    public byte get(Round round, long canonIndex)
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
        holes.flush();
        flops.flush();
        turns.flush();
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
                byte bucket = get( detail.canonIndex() );

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
                                subBranchCanon.toArray()));
            }
            return subBranches;
        }
    }
}

