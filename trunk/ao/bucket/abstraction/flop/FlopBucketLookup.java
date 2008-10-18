package ao.bucket.abstraction.flop;

import ao.bucket.abstraction.flop.FlopBucketizer.Factory;
import ao.bucket.index.flop.FlopLookup;
import ao.util.data.AutovivifiedList;
import ao.util.persist.PersistentShorts;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

/**
 * Date: Oct 17, 2008
 * Time: 1:39:40 PM
 */
public class FlopBucketLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(FlopBucketLookup.class);


    //--------------------------------------------------------------------
    private final static String DIR = "lookup/bucket/flop/";


    //--------------------------------------------------------------------
    private final List<RandomAccessFile> FILES;
    private final File                   BUCKET_DIR;
    private final Factory                BUCKETIZER;
    private final short[][]              HOLE_BUCKETS;


    //--------------------------------------------------------------------
    public FlopBucketLookup(
            String    holeBucketizerId,
            short[][] holeBuckets,
            Factory   bucketizer)
    {
        FILES        = new AutovivifiedList<RandomAccessFile>();
        BUCKETIZER   = bucketizer;
        BUCKET_DIR   = bucketFolder(
                holeBucketizerId, holeBuckets.length);
        HOLE_BUCKETS = holeBuckets;
    }


    //--------------------------------------------------------------------
    public short bucket(
            short flopBucketsPerHoleBucket,
            int   forCanonFlop)
    {
        File bucketFile = bucketFile(flopBucketsPerHoleBucket);
        if (! bucketFile.exists())
        {
            calculateBuckets(
                    flopBucketsPerHoleBucket,
                    bucketFile);
        }
        return read(bucketFile, flopBucketsPerHoleBucket, forCanonFlop);
    }

    private short read(File from, int flopBuckets, int canonIndex)
    {
        try
        {
            return doRead(from, flopBuckets, canonIndex);
        }
        catch (IOException e)
        {
            throw new Error( e );
        }
    }
    private short doRead(File from, int flopBuckets, int canonIndex)
            throws IOException
    {
        RandomAccessFile target =
                FILES.get( flopBuckets );
        if (target == null)
        {
            target = new RandomAccessFile(from, "rw");
            FILES.set(flopBuckets, target);
        }

        int offset = canonIndex * Short.SIZE / 8;
        target.seek( offset );
        return target.readShort();
    }


    //--------------------------------------------------------------------
    private void calculateBuckets(
            int   numBuckets,
            File  bucketFile)
    {
        LOG.info("calculateBuckets " + numBuckets);

        short byCanon[] = new short[ FlopLookup.CANON_FLOP_COUNT ];
        Arrays.fill(byCanon, (short) -1);

        int bucketOffset = 0;
        for (short[] holeHucket : HOLE_BUCKETS)
        {
            int buckets[][] =
                    BUCKETIZER.newInstance( holeHucket )
                              .bucketize  ( numBuckets );

            for (short bucket = 0; bucket < buckets.length; bucket++)
            {
                for (int canon : buckets[ bucket ])
                {
                    byCanon[ canon ] = (short)(bucketOffset + bucket);
                }
            }

            bucketOffset += buckets.length;
        }

        PersistentShorts.writeBinary(
                byCanon, bucketFile.toString());
    }


    //--------------------------------------------------------------------
    private static File bucketFolder(
            String holeBucketizerId,
            int    numHoleBuckets)
    {
        File folder = new File(
                DIR + holeBucketizerId + "." + numHoleBuckets + "/");
        if ( folder.mkdirs() )
        {
            LOG.info("created " + folder);
        }
        return folder;
    }

    private File bucketFile(int totalFlopBuckets)
    {
        return new File(BUCKET_DIR, totalFlopBuckets + ".cache");
    }
}
