package ao.bucket.abstraction.access;

import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.util.data.primitive.CharList;
import ao.util.io.Dir;
import ao.util.persist.PersistentInts;

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
        for (int i = 0; i < tree.length; i++)
        {
            flopTree[i] = new char[ tree[ i ].length ];
            turnTree[i] = new char[ tree[ i ].length ][];

            for (int j = 0; j < tree[ i ].length; j++, flop++)
            {
                flopTree[i][j] = flop;
                turnTree[i][j] = new char[ tree[ i ][ j ].length ];

                for (int k = 0; k < tree[ i ][ j ].length; k++, turn++)
                {
                    turnTree[i][j][k] = turn;
                }
            }
        }
    }


    //--------------------------------------------------------------------
    private char[][][][] retrieveOrCompute(Branch root, File dir)
    {
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

        char         river = 0;
        char[][][][] map   = new char[flopCounts.length][][][];
        for (int h = 0; h < flopCounts.length; h++)
        {
            map[h] = new char[ flopCounts[h] ][][];
        }
        return map;
    }

    private static void persist(char[][][][] map, File toDir)
    {

    }


    //--------------------------------------------------------------------
    private char[][][][] decodeHoleDown(Branch root)
    {
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
                    decodeTurnsDown(flopBucket));
        }
        return flopBuckets.toArray(
                new char[ flopBuckets.size() ][][]);
    }

    private char[][] decodeTurnsDown(Branch flopBucket)
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
