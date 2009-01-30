package ao.bucket.abstraction.access;

import ao.bucket.abstraction.access.odds.BucketOdds;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.regret.holdem.HoldemBucket;
import ao.util.data.primitive.CharList;
import ao.util.data.primitive.IntList;
import ao.util.io.Dir;
import ao.util.persist.PersistentInts;
import ao.util.text.Txt;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: Jan 16, 2009
 * Time: 4:40:32 AM
 */
public class BucketFlyweight
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketFlyweight.class);

    private static final String F_NUM_FLOPS  =  "nFlops.int";
    private static final String F_NUM_TURNS  =  "nTurns.int";
    private static final String F_NUM_RIVERS = "nRivers.int";


    //--------------------------------------------------------------------
//    private final char[][]     flopTree;
//    private final char[][][]   turnTree;

    private final BucketOdds   odds;
    private final char[][][][] tree;
    private       char         nextRiverBucket;


    //--------------------------------------------------------------------
    public BucketFlyweight(BucketTree bucketTree, File dir)
    {
        File store = Dir.get(dir, "map");
        tree = retrieveOrCompute( bucketTree.holes(), store );
        odds = BucketOdds.retrieveOrCompute(dir, bucketTree, tree);

//        flopTree = new char[ tree.length ][];
//        turnTree = new char[ tree.length ][][];
//        initFlopTurn();
    }


//    //--------------------------------------------------------------------
//    private void initFlopTurn()
//    {
//        char flop = 0, turn = 0;
//        for (int h = 0; h < tree.length; h++)
//        {
//            flopTree[h] = new char[ tree[ h ].length ];
//            turnTree[h] = new char[ tree[ h ].length ][];
//
//            for (int f = 0; f < tree[ h ].length; f++, flop++)
//            {
//                flopTree[h][f] = flop;
//                turnTree[h][f] = new char[ tree[ h ][ f ].length ];
//
//                for (int t = 0; t < tree[ h ][ f ].length; t++, turn++)
//                {
//                    turnTree[h][f][t] = turn;
//                }
//            }
//        }
//    }


    //--------------------------------------------------------------------
    private char[][][][] retrieveOrCompute(Branch holes, File dir)
    {
        LOG.debug("retrieveOrCompute");

        char[][][][] map = retrieve(dir);
        if (map == null) {
            map = decodeHoleDown(holes);
            persist(map, dir);
        }
        return map;
    }

    private static char[][][][] retrieve(File fromDir)
    {
        int[] flopCounts = PersistentInts.retrieve(
                new File(fromDir, F_NUM_FLOPS));
        if (flopCounts == null) return null;

        int[] turnCounts = PersistentInts.retrieve(
                new File(fromDir, F_NUM_TURNS));
        int[] riverCounts = PersistentInts.retrieve(
                new File(fromDir, F_NUM_RIVERS));

        char         flop  = 0;
        char         turn  = 0;
        char         river = 0;
        char[][][][] map   = new char[flopCounts.length][][][];
        for (int h = 0; h < flopCounts.length; h++)
        {
            map[h] = new char[ flopCounts[h] ][][];

            for (int f = 0; f < map[h].length; f++, flop++)
            {
                map[h][f] = new char[ turnCounts[flop] ][];

                for (int t = 0; t < map[h][f].length; t++, turn++)
                {
                    map[h][f][t] = new char[ riverCounts[turn] ];

                    for (int r = 0; r < map[h][f][t].length; r++, river++)
                    {
                        map[h][f][t][r] = river;
                    }
                }
            }
        }
        return map;
    }

    private static void persist(char[][][][] map, File toDir)
    {
        LOG.debug("persisting");

        IntList flops  = new IntList();
        IntList turns  = new IntList();
        IntList rivers = new IntList();

        for (char[][][] flop : map)
        {
            flops.add( flop.length );

            for (char[][] turn : flop)
            {
                turns.add( turn.length );

                for (char[] river : turn)
                {
                    rivers.add( river.length );
                }
            }
        }

        PersistentInts.persist(flops.toArray(),
                               new File(toDir, F_NUM_FLOPS));
        PersistentInts.persist(turns.toArray(),
                               new File(toDir, F_NUM_TURNS));
        PersistentInts.persist(rivers.toArray(),
                               new File(toDir, F_NUM_RIVERS));
    }


    //--------------------------------------------------------------------
    private char[][][][] decodeHoleDown(Branch holes)
    {
        LOG.debug("computing");
        nextRiverBucket = 0;

        List<char[][][]> holeBuckets =
                new ArrayList<char[][][]>();
        for (Branch flop : holes.subBranches())
        {
            holeBuckets.add(
                    decodeFlopDown(flop));
        }
        return holeBuckets.toArray(
                new char[ holeBuckets.size() ][][][]);
    }

    private char[][][] decodeFlopDown(Branch flop)
    {
        List<char[][]> flopBuckets =
                new ArrayList<char[][]>();
        for (Branch turn : flop.subBranches())
        {
            flopBuckets.add(
                    decodeTurnDown(turn));
        }
        return flopBuckets.toArray(
                new char[ flopBuckets.size() ][][]);
    }

    private char[][] decodeTurnDown(Branch turn)
    {
        List<char[]> turnBuckets =
                new ArrayList<char[]>();
        for (Branch river : turn.subBranches()) {
            turnBuckets.add(
                    decodeRivers(river));
        }
        return turnBuckets.toArray(
                new char[ turnBuckets.size() ][]);
    }

    private char[] decodeRivers(Branch river)
    {
        CharList riverBuckets = new CharList();
        for (byte i = river.bucketCount(); i > 0; i--) {
            riverBuckets.add( nextRiverBucket++ );
        }
        return riverBuckets.toArray();
    }


    //--------------------------------------------------------------------
    public char serialize(
            byte  holeBucket,
            byte  flopBucket,
            byte  turnBucket,
            byte riverBucket)
    {
        return tree[  holeBucket ]
                   [  flopBucket ]
                   [  turnBucket ]
                   [ riverBucket ];
    }

//    public char serialize(
//            byte  holeBucket,
//            byte  flopBucket,
//            byte  turnBucket)
//    {
//        return turnTree[ holeBucket ][ flopBucket ][ turnBucket ];
//    }
//
//    public char serialize(
//            byte  holeBucket,
//            byte  flopBucket)
//    {
//        return flopTree[ holeBucket ][ flopBucket ];
//    }


    //--------------------------------------------------------------------
    public BucketOdds odds()
    {
        return odds;
    }


    //--------------------------------------------------------------------
    public HoldemBucket root()
    {
        return new RootBucket();
    }

    private class RootBucket extends NodeBucket {
        public RootBucket() {  super((byte) -1, tree.length);  }
        public HoleBucket nextBucket(byte index) {
            return new HoleBucket(index, tree[index]);
        }
    }
    private class HoleBucket extends NodeBucket {
        private char[][][] flops;
        public HoleBucket(byte i, char[][][] f) {
            super(i, f.length);
            flops = f;
        }
        public HoldemBucket nextBucket(byte index) {
            return new FlopBucket(index, flops[index]);
        }
    }
    private class FlopBucket extends NodeBucket {
        private char[][] turns;
        public FlopBucket(byte i, char[][] t) {
            super(i, t.length);
            turns = t;
        }
        public HoldemBucket nextBucket(byte index) {
            return new TurnBucket(index, turns[index]);
        }
    }
    private class TurnBucket extends NodeBucket {
        private char[] rivers;
        public TurnBucket(byte i, char[] r) {
            super(i, r.length);
            rivers = r;
        }
        public HoldemBucket nextBucket(byte index) {
            return new RiverBucket(index, rivers[index]);
        }
    }
    private class RiverBucket extends IndexedBucket {
        private final char absoluteIndex;
        public RiverBucket(byte i, char abs) {
            super(i);
            absoluteIndex = abs;
        }
        public double against(HoldemBucket otherTerminal) {
            return odds.nonLossProb(
                    absoluteIndex,
                    ((RiverBucket) otherTerminal).absoluteIndex);
        }
        public HoldemBucket[] nextBuckets() {
            throw new UnsupportedOperationException();
        }
        public HoldemBucket nextBucket(byte index) {
            throw new UnsupportedOperationException();
        }

        @Override public String toString() {
            return index() + "\t[" + odds.status(absoluteIndex) + "]";
        }
    }


    //--------------------------------------------------------------------
    private abstract class IndexedBucket implements HoldemBucket {
        private final byte index;
        public IndexedBucket(byte i) {  index = i;  }
        public byte index() {
            return index;
        }
    }
    private abstract class NodeBucket extends IndexedBucket {
        private final byte nBuckets;
        public NodeBucket(byte i, int n) {
            super(i);
            nBuckets = (byte) n;
        }
        public HoldemBucket[] nextBuckets() {
            HoldemBucket[] buckets = new HoldemBucket[nBuckets];
            for (byte i = 0; i < buckets.length; i++) {
                buckets[ i ] = nextBucket(i);
            }
            return buckets;
        }
        public double against(HoldemBucket otherTerminal) {
            throw new UnsupportedOperationException();
        }

        @Override public String toString() {  return toString(0);  }
        public String toString(int depth) {
            StringBuilder str = new StringBuilder();
            str.append(Txt.nTimes("\t", depth)).append(index());
            for (byte i = 0; i < nBuckets; i++) {
                HoldemBucket bucket = nextBucket(i);
                str.append("\n")
                   .append((bucket instanceof NodeBucket)
                           ? ((NodeBucket) bucket).toString(depth + 1)
                           : Txt.nTimes("\t", depth + 1) +
                               bucket.toString());
            }
            return str.toString();
        }
    }
}
