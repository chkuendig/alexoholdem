package ao.holdem.abs.bucket.abstraction.access;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.turn.TurnRivers;
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
               ioFile.length() == (River.CANONS * 2)))
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

        Progress p = new Progress(River.CANONS);
        for (long r = 0; r < River.CANONS; r++) {
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
                return position < River.CANONS;
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
            int hole, int flop, int turn, long river)
    {
        return decoder.decode(
                tree.getHole ( hole  ),
                tree.getFlop ( flop  ),
                tree.getTurn ( turn  ),
                tree.getRiver( river ));
    }
}
