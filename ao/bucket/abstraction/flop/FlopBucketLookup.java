package ao.bucket.abstraction.flop;

import ao.bucket.abstraction.community.CommunityBucketLookup;
import ao.bucket.abstraction.community.CommunityBucketizer;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.abstraction.set.BucketSetImpl;
import org.apache.log4j.Logger;

import java.io.File;


/**
 * Date: Oct 17, 2008
 * Time: 1:39:40 PM
 */
public class FlopBucketLookup implements CommunityBucketLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(FlopBucketLookup.class);


    //--------------------------------------------------------------------
    private final static String DIR = "lookup/bucket/flop/";


    //--------------------------------------------------------------------
    private final CommunityBucketizer BUCKETIZER;
    private final String              BUCKET_DIR;


    //--------------------------------------------------------------------
    public FlopBucketLookup(CommunityBucketizer bucketizer)
    {
        BUCKETIZER   = bucketizer;
        BUCKET_DIR   = bucketFolder(bucketizer.id());
    }


    //--------------------------------------------------------------------
    public BucketSet buckets(
            BucketSet onTopOf,
            char      flopBucketCount)
    {
        BucketSet.Builder<BucketSetImpl> builder =
                new BucketSetImpl.BuilderImpl(
                        bucketDir( onTopOf.id(), flopBucketCount ) );
        BucketSet cache = builder.retrieve();
        if (cache == null)
        {
            cache = BUCKETIZER.bucketize(
                      onTopOf, flopBucketCount, builder);
            cache.persist();
        }
        return cache;
    }

//    private short doRead(File from, int flopBuckets, int canonIndex)
//            throws IOException
//    {
//        RandomAccessFile target =
//                FILES.get( flopBuckets );
//        if (target == null)
//        {
//            target = new RandomAccessFile(from, "rw");
//            FILES.set(flopBuckets, target);
//        }
//
//        int offset = canonIndex * Short.SIZE / 8;
//        target.seek( offset );
//        return target.readShort();
//    }


    //--------------------------------------------------------------------
    private static String bucketFolder(String bucketizerId)
    {
        String bucketFolder = DIR + bucketizerId + "/";

        if ( new File(bucketFolder).mkdirs() )
            LOG.info("created " + bucketFolder);

        return bucketFolder;
    }

    private String bucketDir(
            String holeBucketingId,
            int    flopBucketCount)
    {
        return BUCKET_DIR +
               holeBucketingId + "." +
               flopBucketCount + "/";
    }
}
