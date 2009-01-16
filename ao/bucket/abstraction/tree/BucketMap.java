package ao.bucket.abstraction.tree;

import ao.bucket.abstraction.tree.BucketTree.Branch;
import ao.util.data.primitive.CharList;

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
    private final char[][][][] tree;
    private       char         nextRiverBucket;


    //--------------------------------------------------------------------
    public BucketMap(BucketTree bucketTree)
    {
        tree = decodeHoleDown( bucketTree.root() );
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
}
