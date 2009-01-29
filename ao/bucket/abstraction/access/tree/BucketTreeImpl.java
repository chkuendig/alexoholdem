package ao.bucket.abstraction.access.tree;

import ao.bucket.abstraction.access.BucketFlyweight;
import ao.bucket.abstraction.access.tree.list.BucketListImpl;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.access.tree.list.ThirdBucketList;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.DetailLookup;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.hole.HoleLookup;
import ao.bucket.index.river.RiverLookup;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.Round;
import ao.util.data.AutovivifiedList;
import ao.util.data.primitive.IntList;
import ao.util.io.Dir;
import ao.util.persist.PersistentBytes;

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
    private static final File DIR = Dir.get("lookup/bucket/");


    //--------------------------------------------------------------------
    private final BucketList holes;
    private final BucketList flops;
    private final BucketList turns;
    private final BucketList rivers;
    private final File       flushFlag;
    private final File       persistDir;


    //--------------------------------------------------------------------
    public BucketTreeImpl(String id)
    {
        persistDir = Dir.get(DIR, id);
        flushFlag  = new File(persistDir, "flushed");

        File holeFile  = new File(persistDir, "holes");
        File flopFile  = new File(persistDir, "flops");
        File turnFile  = new File(persistDir, "turns");
        File riverDir  =  Dir.get(persistDir, "rivers");

        holes  = new BucketListImpl (holeFile, HoleLookup.CANONS);
        flops  = new BucketListImpl (flopFile, FlopLookup.CANONS);
        turns  = new HalfBucketList (turnFile, TurnLookup.CANONS);
        rivers = new ThirdBucketList(riverDir, RiverLookup.CANONS);
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
//    public boolean isEmpty(Round round, long canonIndex)
//    {
//        switch (round)
//        {
//            case PREFLOP: return  holes.isEmpty( canonIndex );
//            case FLOP:    return  flops.isEmpty( canonIndex );
//            case TURN:    return  turns.isEmpty( canonIndex );
//            case RIVER:   return rivers.isEmpty( canonIndex );
//
//            default:
//                throw new UnsupportedOperationException();
//        }
//    }


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

//    public void flush(Round round, int fromCanon, char canonCount) {
//        switch (round)
//        {
//            case PREFLOP:  holes.flush(fromCanon, canonCount); break;
//            case FLOP:     flops.flush(fromCanon, canonCount); break;
//            case TURN:     turns.flush(fromCanon, canonCount); break;
//            case RIVER:   rivers.flush(fromCanon, canonCount); break;
//        }
//    }


    //--------------------------------------------------------------------
    public BucketFlyweight map()
    {
        return new BucketFlyweight(this, persistDir);
    }


    //--------------------------------------------------------------------
    public Branch holes()
    {
        return new BranchImpl();
    }


    //--------------------------------------------------------------------
    private class BranchImpl implements Branch
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

//        private boolean isEmpty(long canonIndex) {
//            return BucketTreeImpl.this.isEmpty(round, canonIndex);
//        }


        //----------------------------------------------------------------
        public CanonDetail[] details()
        {
            return DetailLookup.lookupSub(
                            round().previous(),
                            parentCanons());
//            if (subDetails != null) return subDetails;
//            subDetails = DetailLookup.lookupSub(
//                            round().previous(),
//                            parentCanons());
//            return subDetails;
        }

        public byte bucketCount()
        {
            int max = 0;
            for (CanonDetail detail : details()) {
                byte bucket = get(detail.canonIndex());
                max = Math.max(max, bucket);
            }
            return (byte)(max + 1);
        }


        //----------------------------------------------------------------
        public Iterable<Branch> subBranches()
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


        //----------------------------------------------------------------
//        public boolean isBucketized()
//        {
//            if (round == Round.PREFLOP) {
//                for (int i = 0; i < HoleLookup.CANONS; i++) {
//                    if (isEmpty(i)) return false;
//                }
//                return true;
//            }
//
//            for (int parent : parentCanons) {
//                CanonRange range =
//                            DetailLookup.lookupRange(
//                                    round.previous(), parent);
//                for (int i = 0; i < range.canonIndexCount(); i++) {
//                    if (isEmpty( range.fromCanonIndex() + i ))
//                        return false;
//                }
//            }
//            return true;
//        }


//        //----------------------------------------------------------------
//        public void flush()
//        {
//            if (round == Round.PREFLOP) {
//                BucketTreeImpl.this.flush(
//                        round, 0, (char) HoleLookup.CANONS);
//                return;
//            }
//
//            for (int parent : parentCanons) {
//                CanonRange range =
//                        DetailLookup.lookupRange(
//                                round.previous(), parent);
//
//                BucketTreeImpl.this.flush(
//                        round,
//                        (int) range.fromCanonIndex(),
//                        range.canonIndexCount());
//            }
//        }
    }
}

