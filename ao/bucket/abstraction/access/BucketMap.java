package ao.bucket.abstraction.access;

import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.util.data.primitive.CharList;
import ao.util.data.primitive.IntList;
import ao.util.io.Dir;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Date: Jan 16, 2009
 * Time: 4:40:32 AM
 */
public class BucketMap
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketMap.class);

    private static final String F_NUM_FLOPS  =  "nFlops.int";
    private static final String F_NUM_TURNS  =  "nTurns.int";
    private static final String F_NUM_RIVERS = "nRivers.int";


    //--------------------------------------------------------------------
    private final char[][]     flopTree;
    private final char[][][]   turnTree;

    private final char[][][][] tree;
    private       char         nextRiverBucket;


    //--------------------------------------------------------------------
    public BucketMap(Branch root, File dir)
    {
        File store = Dir.get(dir, "map");
        tree = retrieveOrCompute( root, store );

        flopTree = new char[ tree.length ][];
        turnTree = new char[ tree.length ][][];
        initFlopTurn();
    }

    
    //--------------------------------------------------------------------
    private void initFlopTurn()
    {
        char flop = 0, turn = 0;
        for (int h = 0; h < tree.length; h++)
        {
            flopTree[h] = new char[ tree[ h ].length ];
            turnTree[h] = new char[ tree[ h ].length ][];

            for (int f = 0; f < tree[ h ].length; f++, flop++)
            {
                flopTree[h][f] = flop;
                turnTree[h][f] = new char[ tree[ h ][ f ].length ];

                for (int t = 0; t < tree[ h ][ f ].length; t++, turn++)
                {
                    turnTree[h][f][t] = turn;
                }
            }
        }
    }


    //--------------------------------------------------------------------
    private char[][][][] retrieveOrCompute(Branch root, File dir)
    {
        LOG.debug("retrieveOrCompute");

        char[][][][] map = retrieve(dir);
        if (map == null) {
            map = decodeHoleDown(root);
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
    private char[][][][] decodeHoleDown(Branch root)
    {
        LOG.debug("computing");
        nextRiverBucket = 0;

        List<char[][][]> holeBuckets =
                new ArrayList<char[][][]>();
        for (Branch holeBucket : root.subBranches())
        {
            holeBuckets.add(
                    decodeFlopDown(holeBucket));
        }
        return holeBuckets.toArray(
                new char[ holeBuckets.size() ][][][]);
    }

    private char[][][] decodeFlopDown(Branch holeBucket)
    {
        List<char[][]> flopBuckets =
                new ArrayList<char[][]>();
        for (Branch flopBucket : holeBucket.subBranches())
        {
            flopBuckets.add(
                    decodeTurnDown(flopBucket));
        }
        return flopBuckets.toArray(
                new char[ flopBuckets.size() ][][]);
    }

    private char[][] decodeTurnDown(Branch flopBucket)
    {
        List<char[]> turnBuckets =
                new ArrayList<char[]>();
        for (Branch turnBucket : flopBucket.subBranches())
        {
            turnBuckets.add(
                    decodeRivers(turnBucket));
        }
        return turnBuckets.toArray(
                new char[ turnBuckets.size() ][]);
    }

    private char[] decodeRivers(Branch turnBucket)
    {
        CharList riverBuckets = new CharList();
        for (Iterator itr = turnBucket.subBranches().iterator();
                      itr.hasNext();)
        {
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

    
    //--------------------------------------------------------------------
    @SuppressWarnings({"ConstantConditions"})
    public char serialize(
            byte  holeBucket,
            byte  flopBucket,
            byte  turnBucket)
    {
        return turnTree[ holeBucket ][ flopBucket ][ turnBucket ];
//        char index = 0;
//        for (int i = 0; i < tree.length; i++)
//        {
//            for (int j = 0; j < tree[ i ].length; j++)
//            {
//                for (int k = 0; k < tree[ i ][ j ].length; k++, index++)
//                {
//                    if (i == holeBucket &&
//                        j == flopBucket &&
//                        k == turnBucket) return index;
//                }
//            }
//        }
//        return index;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings({"ConstantConditions"})
    public char serialize(
            byte  holeBucket,
            byte  flopBucket)
    {
        return flopTree[ holeBucket ][ flopBucket ];
//        char index = 0;
//        for (int i = 0; i < tree.length; i++)
//        {
//            for (int j = 0; j < tree[ i ].length; j++, index++)
//            {
//                if (i == holeBucket && j == flopBucket)
//                    return index;
//            }
//        }
//        return index;
    }
}
