package ao.bucket.abstraction.access;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.index.canon.flop.Flop;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.river.River;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.Turn;
import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.util.time.Progress;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;

/**
 * Date: Feb 16, 2009
 * Time: 11:39:52 AM
 */
public class AbsBucketStore implements Iterable<Character>
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(AbsBucketStore.class);
    
    private static final String fName = "abs_bucket.char";


    //--------------------------------------------------------------------
    private final File          ioFile;
    //private final BucketDecoder decoder;


    //--------------------------------------------------------------------
    public AbsBucketStore(
            File          dir,
            BucketTree    bucketTree,
            BucketDecoder decoder)
    {
        ioFile = new File(dir, fName);

        if (! (ioFile.canRead() &&
               ioFile.length() == (RiverLookup.CANONS * 2)))
        {
            computeAndPersist(bucketTree, decoder);
        }
    }


    //--------------------------------------------------------------------
    private void computeAndPersist(
            BucketTree    tree,
            BucketDecoder decoder)
    {
        try {
            doComputeAndPersist(tree, decoder);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private void doComputeAndPersist(
            BucketTree    tree,
            BucketDecoder decoder) throws IOException
    {
        LOG.debug("computeAndPersist");

        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(ioFile)));

        Progress p = new Progress(RiverLookup.CANONS);
        for (long r = 0; r < RiverLookup.CANONS; r++) {
            char absoluteRiverBucket = bucketOf(tree, decoder, r);
            out.writeChar( absoluteRiverBucket );

            p.checkpoint();
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
            BucketTree    tree,
            BucketDecoder decoder,
            long          river)
    {
        int  turn = TurnRivers.turnFor( river );
        CanonFlopDetail flopDetail = FlopDetails.containing(turn);
        int  flop = (int)  flopDetail.canonIndex();
        char hole = (char) flopDetail.holeDetail().canonIndex();
        return bucketOf(tree, decoder,
                        hole, flop, turn, river);
    }

     public static char bucketOf(
            BucketTree    tree,
            BucketDecoder decoder,
            River         river)
    {
        Turn turn = river.turn();
        Flop flop = turn.flop();
        CanonHole hole = flop.hole();
        return bucketOf(
                tree, decoder,
                 hole.canonIndex(),
                 flop.canonIndex(),
                 turn.canonIndex(),
                river.canonIndex());
    }

    public static char bucketOf(
            BucketTree tree, BucketDecoder decoder,
            char hole, int flop, int turn, long river)
    {
        return decoder.decode(
                tree.getHole ( hole  ),
                tree.getFlop ( flop  ),
                tree.getTurn ( turn  ),
                tree.getRiver( river ));
    }
}
