package ao.bucket.abstraction.access;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.river.RiverLookup;
import ao.bucket.index.turn.Turn;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;

/**
 * Date: Feb 16, 2009
 * Time: 11:39:52 AM
 */
public class AbsoluteBuckets implements Iterable<Character>
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(AbsoluteBuckets.class);
    
    private static final String fName = "abs_bucket.char";


    //--------------------------------------------------------------------
    private final File ioFile;


    //--------------------------------------------------------------------
    public AbsoluteBuckets(
            File         dir,
            BucketTree   bucketTree,
            char[][][][] holes)
    {
        ioFile = new File(dir, fName);

        if (! (ioFile.canRead() &&
               ioFile.length() == (RiverLookup.CANONS * 2)))
        {
            computeAndPersist(bucketTree, holes);
        }
    }


    //--------------------------------------------------------------------
    private void computeAndPersist(
            BucketTree   tree,
            char[][][][] holes)
    {
        try {
            doComputeAndPersist(tree, holes);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private void doComputeAndPersist(
            BucketTree   tree,
            char[][][][] holes) throws IOException
    {
        LOG.debug("computeAndPersist");

        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(ioFile)));

        for (long r = 0; r < RiverLookup.CANONS; r++) {
            char absoluteRiverBucket = bucketOf(tree, holes, r);
            out.writeChar( absoluteRiverBucket );
        }
        
        out.close();
    }


    //--------------------------------------------------------------------
    public Iterator<Character> iterator()
    {
        final DataInputStream in;
        try {
            in = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(ioFile),
                            1024 * 1024));
        } catch (FileNotFoundException e) {
            throw new Error( e );
        }

        return new Iterator<Character>() {
            private long position = 0;
            public boolean hasNext() {
                return position < RiverLookup.CANONS;
            }

            public Character next() {
                try {
                    char next = in.readChar();

                    position++;
                    if (! hasNext()) {
                        in.close();
                    }

                    return next;
                } catch (IOException e) {
                    throw new Error( e );
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    //--------------------------------------------------------------------
    public static char bucketOf(
            BucketTree   tree,
            char[][][][] holes,
            long         river)
    {
        int  turn = TurnRivers.turnFor( river );
        CanonFlopDetail flopDetail = FlopDetails.containing(turn);
        int  flop = (int)  flopDetail.canonIndex();
        char hole = (char) flopDetail.holeDetail().canonIndex();
        return bucketOf(tree, holes,
                        hole, flop, turn, river);
    }

     public static char bucketOf(
            BucketTree   tree,
            char[][][][] holes,
            River        river)
    {
        Turn turn = river.turn();
        Flop flop = turn.flop();
        CanonHole hole = flop.hole();
        return bucketOf(
                tree, holes,
                 hole.canonIndex(),
                 flop.canonIndex(),
                 turn.canonIndex(),
                river.canonIndex());
    }

    public static char bucketOf(
            BucketTree tree, char[][][][] holes,
            char hole, int flop, int turn, long river)
    {
        return holes[ tree.getHole ( hole  ) ]
                    [ tree.getFlop ( flop  ) ]
                    [ tree.getTurn ( turn  ) ]
                    [ tree.getRiver( river ) ];
    }
}
