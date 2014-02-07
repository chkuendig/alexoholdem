package ao.holdem.ai.abstraction.access;

import ao.holdem.ai.abstraction.access.tree.BucketTree.Branch;
import ao.util.data.primitive.CharList;
import ao.util.data.primitive.IntList;
import ao.util.io.Dirs;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: Jan 16, 2009
 * Time: 4:40:32 AM
 */
public class BucketDecoder
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketDecoder.class);

    private static final String SUB_DIR      =  "map";
    private static final String F_NUM_FLOPS  =  "nFlops.int";
    private static final String F_NUM_TURNS  =  "nTurns.int";
    private static final String F_NUM_RIVERS = "nRivers.int";


    //--------------------------------------------------------------------
    public static BucketDecoder retrieveInstance(File dir)
    {
        char[][][][] map = retrieve(dir);
        return map == null
               ? null : new BucketDecoder(map);
    }

    public static BucketDecoder computeAndStore(Branch holes, File dir)
    {
        char[][][][] map = decodeHoleDown(holes);
        persist(map, dir);
        return new BucketDecoder(map);
    }

    public static BucketDecoder retrieveOrCompute(Branch holes, File dir)
    {
        BucketDecoder ret = retrieveInstance(dir);
        return ret != null
               ? ret : computeAndStore(holes, dir);
    }


    //--------------------------------------------------------------------
    private final char[][]     flops;
    private final char[][][]   turns;
    private final char[][][][] rivers;


    //--------------------------------------------------------------------
    private BucketDecoder(char riverTree[][][][])
    {
        rivers = riverTree;
        flops  = new char[ rivers.length ][];
        turns  = new char[ rivers.length ][][];
        initFlopTurn();
    }
//    public BucketDecoder(BucketTree bucketTree, File dir)
//    {
//        File store = Dir.get(dir, "map");
//        rivers = retrieveOrCompute( bucketTree.holes(), store );
//
//        flops = new char[ rivers.length ][];
//        turns = new char[ rivers.length ][][];
//        initFlopTurn();
//    }


    //--------------------------------------------------------------------
    private void initFlopTurn()
    {
        char flop = 0, turn = 0;
        for (int h = 0; h < rivers.length; h++)
        {
            flops[h] = new char[ rivers[ h ].length ];
            turns[h] = new char[ rivers[ h ].length ][];

            for (int f = 0; f < rivers[ h ].length; f++, flop++)
            {
                flops[h][f] = flop;
                turns[h][f] = new char[ rivers[ h ][ f ].length ];

                for (int t = 0; t < rivers[ h ][ f ].length; t++, turn++)
                {
                    turns[h][f][t] = turn;
                }
            }
        }
    }

//    private void testTrees(final BucketTree buckets)
//    {
//        LOG.debug("testing tree");
//
//        final Gapper bucketGapper = new Gapper();
//        HandEnum.uniqueRivers(new Traverser<River>() {
//            public void traverse(River river) {
//                Turn      turn = river.turn();
//                Flop      flop = turn.flop();
//                CanonHole hole = flop.hole();
//
//                char absoluteRiverBucket = tree
//                        [ buckets.getHole (  hole.canonIndex() ) ]
//                        [ buckets.getFlop (  flop.canonIndex() ) ]
//                        [ buckets.getTurn (  turn.canonIndex() ) ]
//                        [ buckets.getRiver( river.canonIndex() ) ];
//                bucketGapper.set(absoluteRiverBucket);
//            }});
//        bucketGapper.displayStatus();
//    }


    //--------------------------------------------------------------------
//    private char[][][][] retrieveOrCompute(Branch holes, File dir)
//    {
//        LOG.debug("retrieveOrCompute");
//
//        char[][][][] map = retrieve(dir);
//        if (map == null) {
//            map = decodeHoleDown(holes);
//            persist(map, dir);
//        }
//        return map;
//    }

    private static char[][][][] retrieve(File fromDir)
    {
        File fromSubDir = Dirs.get(fromDir, SUB_DIR);

        int[] flopCounts = PersistentInts.retrieve(
                new File(fromSubDir, F_NUM_FLOPS));
        if (flopCounts == null) return null;

        int[] turnCounts = PersistentInts.retrieve(
                new File(fromSubDir, F_NUM_TURNS));
        int[] riverCounts = PersistentInts.retrieve(
                new File(fromSubDir, F_NUM_RIVERS));

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

        File toSubDir = Dirs.get(toDir, SUB_DIR);
        PersistentInts.persist(flops.toIntArray(),
                               new File(toSubDir, F_NUM_FLOPS));
        PersistentInts.persist(turns.toIntArray(),
                               new File(toSubDir, F_NUM_TURNS));
        PersistentInts.persist(rivers.toIntArray(),
                               new File(toSubDir, F_NUM_RIVERS));
    }


    //--------------------------------------------------------------------
    private static char[][][][] decodeHoleDown(Branch holes)
    {
        LOG.debug("computing");
        char nextRiverBucket[] = {0};

        List<char[][][]> holeBuckets =
                new ArrayList<char[][][]>();
        for (Branch flop : holes.subBranches())
        {
            holeBuckets.add(
                    decodeFlopDown(flop, nextRiverBucket));
        }
        return holeBuckets.toArray(
                new char[ holeBuckets.size() ][][][]);
    }

    private static char[][][] decodeFlopDown(
            Branch flop, char nextRiverBucket[])
    {
        List<char[][]> flopBuckets =
                new ArrayList<char[][]>();
        for (Branch turn : flop.subBranches())
        {
            flopBuckets.add(
                    decodeTurnDown(turn, nextRiverBucket));
        }
        return flopBuckets.toArray(
                new char[ flopBuckets.size() ][][]);
    }

    private static char[][] decodeTurnDown(
            Branch turn, char nextRiverBucket[])
    {
        List<char[]> turnBuckets =
                new ArrayList<char[]>();
        for (Branch river : turn.subBranches()) {
            turnBuckets.add(
                    decodeRivers(river, nextRiverBucket));
        }
        return turnBuckets.toArray(
                new char[ turnBuckets.size() ][]);
    }

    private static char[] decodeRivers(
            Branch river, char nextRiverBucket[])
    {
        CharList riverBuckets = new CharList();
        for (byte i = river.bucketCount(); i > 0; i--) {
            riverBuckets.add( nextRiverBucket[0]++ );
        }
        return riverBuckets.toCharArray();
    }


    //--------------------------------------------------------------------
    public char decode(
            int  holeBucket,
            int  flopBucket,
            int  turnBucket,
            int riverBucket)
    {
        return rivers[  holeBucket ]
                     [  flopBucket ]
                     [  turnBucket ]
                     [ riverBucket ];
    }

    public char decode(
            int  holeBucket,
            int  flopBucket,
            int  turnBucket)
    {
        return turns[ holeBucket ]
                    [ flopBucket ]
                    [ turnBucket ];
    }

    public char decode(
            int  holeBucket,
            int  flopBucket)
    {
        return flops[ holeBucket ]
                    [ flopBucket ];
    }


    //--------------------------------------------------------------------
    public char riverBucketCount()
    {
        char lastFlop [][][] = rivers  [   rivers.length - 1 ];
        char lastTurn [][]   = lastFlop[ lastFlop.length - 1 ];
        char lastRiver[]     = lastTurn[ lastTurn.length - 1 ];
        return (char)(lastRiver[ lastRiver.length - 1 ] + 1);
    }

    public char turnBucketCount()
    {
        char lastFlop[][] = turns[ turns.length - 1 ];
        char lastTurn[]   = lastFlop[ lastFlop.length - 1 ];
        return (char)(lastTurn[ lastTurn.length - 1 ] + 1);
    }

    public char flopBucketCount()
    {
        char lastFlop[] = flops[ flops.length - 1 ];
        return (char)(lastFlop[ lastFlop.length - 1 ] + 1);
    }

    public char holeBucketCount()
    {
        return (char) rivers.length;
    }
}
