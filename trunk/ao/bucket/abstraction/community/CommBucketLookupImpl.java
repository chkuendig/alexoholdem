package ao.bucket.abstraction.community;

import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.abstraction.set.BucketSetImpl;
import ao.holdem.model.Round;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Date: Jan 7, 2009
 * Time: 4:29:59 PM
 */
public class CommBucketLookupImpl implements CommunityBucketLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CommBucketLookupImpl.class);


    //--------------------------------------------------------------------
    private final static String BASE_DIR = "lookup/bucket/";


    //--------------------------------------------------------------------
    private final CommunityBucketizer BUCKETIZER;
    private final String              BUCKET_DIR;


    //--------------------------------------------------------------------
    public CommBucketLookupImpl(Round forRound,
                                CommunityBucketizer bucketizer)
    {
        BUCKETIZER   = bucketizer;
        BUCKET_DIR   = bucketFolder(forRound, bucketizer.id());
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
    private String bucketFolder(Round forRound,
                                String bucketizerId)
    {
        String bucketFolder =
                BASE_DIR + forRound.toString() + "/"
                         + bucketizerId + "/";

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
