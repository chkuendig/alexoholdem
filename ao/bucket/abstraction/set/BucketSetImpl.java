package ao.bucket.abstraction.set;

import ao.bucket.index.test.MathUtil;
import ao.util.crypt.MD5;
import ao.util.crypt.SecureHash;
import ao.util.persist.PersistentChars;
import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;


/**
 * Date: Oct 29, 2008
 * Time: 7:50:17 PM
 */
public class BucketSetImpl implements BucketSet
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketSetImpl.class);

    private static final char   SENTINAL = Character.MAX_VALUE;


    //--------------------------------------------------------------------
    private final char[]     BUCKETS_A;
    private final char[]     BUCKETS_B;
    private final char       BUCKET_COUNT;
    private final String     FOLDER;
    private       String     id = null;


    //--------------------------------------------------------------------
    private BucketSetImpl(char[] bucketsA,
                          char[] bucketsB,
                          char   bucketCount,
                          String atFolder)
    {
        BUCKETS_A    = bucketsA;
        BUCKETS_B    = bucketsB;
        BUCKET_COUNT = bucketCount;
        FOLDER       = atFolder;
    }
    private BucketSetImpl(
            long canonIndexCount, char bucketCount, String atFolder)
    {
        assert canonIndexCount <= (long) (Integer.MAX_VALUE - 1) * 2;

        if (canonIndexCount > Integer.MAX_VALUE)
        {
            BUCKETS_A = new char[ Integer.MAX_VALUE              ];
            BUCKETS_B = new char[ (int)(canonIndexCount
                                            - Integer.MAX_VALUE) ];
            Arrays.fill(BUCKETS_B, Character.MAX_VALUE);
        }
        else
        {
            BUCKETS_A = new char[ (int) canonIndexCount ];
            BUCKETS_B = null;
        }
        Arrays.fill(BUCKETS_A, Character.MAX_VALUE);

        BUCKET_COUNT = bucketCount;
        FOLDER       = atFolder;
    }


    //--------------------------------------------------------------------
    public char bucketOf(long canonIndex)
    {
        return (canonIndex <= Integer.MAX_VALUE)
                ? BUCKETS_A[ (int) canonIndex                ]
                : BUCKETS_B[ MathUtil.signedPart(canonIndex) ];
    }


    //--------------------------------------------------------------------
    public long[] canonsOf(char bucket)
    {
        LongList canons = new LongArrayList();
        long canonCount = canonCount();
        for (long canonIndex = 0; canonIndex < canonCount; canonIndex++)
        {
            if (bucketOf(canonIndex) == bucket)
            {
                canons.add( canonIndex );
            }
        }
        return canons.toArray();
    }
    private long canonCount()
    {
        return BUCKETS_A.length + BUCKETS_B.length;
    }


    //--------------------------------------------------------------------
    public void add(long canonIndex, char bucket)
    {
        assert bucket != SENTINAL;

        char prevValue;
        if (canonIndex <= Integer.MAX_VALUE)
        {
            prevValue = BUCKETS_A[ (int) canonIndex ];
                        BUCKETS_A[ (int) canonIndex ] = bucket;
        }
        else
        {
            int index = BUCKETS_B[ MathUtil.signedPart(canonIndex) ];
            prevValue = BUCKETS_B[ index ];
                        BUCKETS_B[ index ] = bucket;
        }
        assert prevValue == SENTINAL;

        id = null;
    }


    //--------------------------------------------------------------------
    public void display()
    {
        if (BUCKETS_A.length > 1000000)
        {
            System.out.println( toString() );
            return;
        }

        for (char bucket = 0; bucket < BUCKET_COUNT; bucket++)
        {
            for (int canonIndex = 0; canonIndex < BUCKETS_A.length; canonIndex++)
            {
                if (BUCKETS_A[canonIndex] == bucket)
                {
                    System.out.print( canonIndex + "\t" );
                }
            }
        }
    }


    //--------------------------------------------------------------------
    public char bucketCount()
    {
        return BUCKET_COUNT;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        if (id == null)
        {
            id = computeId();
        }
        return id;
    }
    private String computeId()
    {
        SecureHash message = new MD5();

        message.feed( BUCKET_COUNT );
        message.feed( BUCKETS_A    );
        message.feed( BUCKETS_B == null
                      ? new char[0] : BUCKETS_B );

        return message.hexDigest();
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return id();
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketSetImpl bucketSet = (BucketSetImpl) o;
        return BUCKET_COUNT == bucketSet.BUCKET_COUNT        &&
               Arrays.equals(BUCKETS_A, bucketSet.BUCKETS_A) &&
               Arrays.equals(BUCKETS_B, bucketSet.BUCKETS_B);
    }

    @Override
    public int hashCode()
    {
        int result = Arrays.hashCode(BUCKETS_A);
        result = 31 * result +
                 (BUCKETS_B != null ? Arrays.hashCode(BUCKETS_B) : 0);
        result = 31 * result + (int) BUCKET_COUNT;
        return result;
    }


    //--------------------------------------------------------------------
    public static class BuilderImpl implements Builder<BucketSetImpl>
    {
        private final String HOME_FOLDER;

        public BuilderImpl(String holeFolder) {
            HOME_FOLDER = holeFolder;
        }

        public BucketSetImpl newInstance(
                long   canonIndexCount,
                char   bucketCount) {
            return new BucketSetImpl(
                    canonIndexCount, bucketCount, HOME_FOLDER);
        }

        public BucketSetImpl retrieve() {
            String folder = storeDir(HOME_FOLDER);
            LOG.info("attempting to retrieve " + folder);

            char bucketCounts[] = PersistentChars.retrieve(
                                    folder + "count");
            if (bucketCounts == null) return null;
            LOG.info("retrieving bucket set from " + folder);

            char bucketCount = bucketCounts[ 0 ];
            char bucketsA[]  = PersistentChars.retrieve(
                                    folder + "a");
            char bucketsB[]  = PersistentChars.retrieve(
                                    folder + "b");

            LOG.info("finished reading " +
                     (int)(bucketCount) + " buckets");
            BucketSetImpl instance = new BucketSetImpl(
                    bucketsA,
                    bucketsB.length > 0 ? bucketsB : null,
                    bucketCount,
                    HOME_FOLDER);
            
            String id = new String(PersistentChars.retrieve(
                                    folder + "id"));
            if (! id.equals( instance.id() ))
            {
                LOG.error("id mismatch");
                return null;
            }
            return instance;
        }
    }


    //--------------------------------------------------------------------
    public void persist()
    {
        String filename = storeDir(FOLDER);
        if (new File( filename ).mkdirs())
            LOG.info("created directories " + filename);

        LOG.info("persisting bucket set to " + filename);

        PersistentChars.persist(
                new char[]{ BUCKET_COUNT },
                filename + "count");
        PersistentChars.persist( BUCKETS_A, filename + "a" );

        char[] bOrEmpyty =
                (BUCKETS_B != null ? BUCKETS_B : new char[0]);
        PersistentChars.persist( bOrEmpyty, filename + "b" );

        PersistentChars.persist( id().toCharArray(), filename + "id" );
    }

    private static String storeDir(String inFolder)
    {
        return inFolder + "bucket_impl/";
    }
}
